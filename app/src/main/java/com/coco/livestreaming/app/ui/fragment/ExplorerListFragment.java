package com.coco.livestreaming.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.ExploreListResponse;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.ExploreDetailActivity;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.ui.activity.MainActivity;
import com.coco.livestreaming.app.ui.activity.ProfileViewActivity;
import com.coco.livestreaming.app.ui.adapter.ExploreListAdapter;
import com.coco.livestreaming.app.ui.adapter.data.ExploreItemData;
import com.coco.livestreaming.app.ui.dialog.PasswordDialog;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.R;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import static com.coco.livestreaming.CocotvingApplication.getContext;

public class ExplorerListFragment extends Fragment {

	public final String TAG = ExplorerListFragment.class.getName();
    
	public final int BUTTON_FLAG1 = 1; // to BroadcastSettingFragment
	public final int BUTTON_FLAG2 = 2; // to BroadcastListFragment
	public final int BUTTON_FLAG3 = 3; // click login
	
	int type_flag = 0;
	String selectedModelid="";
	int mTheme = 0;
	ImageButton imgBtnProgile, imgBtnNotice;
    View rootView;
    
    ListView exploreList;
    List<ExploreItemData> arrList;
    ExploreListAdapter listAdapter;
	SyncInfo info;
	PasswordDialog passwordDlg;

	private boolean isSafeFlag = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrList = new ArrayList<ExploreItemData>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_explorerlist, container, false);
        
    	exploreList =  (ListView)rootView.findViewById(R.id.lv_ExploreList);
                
    	//imgBtnProgile = (ImageButton)rootView.findViewById(R.id.imgBtn_to_profile);
    	//imgBtnNotice = (ImageButton)rootView.findViewById(R.id.imgBtn_to_notice);
    	//imgBtnAddFriend = (ImageButton)rootView.findViewById(R.id.imgBtn_Add_Friend);
        
    	//imgBtnProgile.setOnClickListener(mButtonClickListener);
    	//imgBtnNotice.setOnClickListener(mButtonClickListener);
    	//imgBtnAddFriend.setOnClickListener(mButtonClickListener);
    	
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		info = new SyncInfo(getActivity());
		new ExploreListAsync().execute();
	}

	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	switch(v.getId()){
        	//case R.id.imgBtn_to_profile:
        	//	startActivity(new Intent(getActivity(), ProfileViewActivity.class));
        	//	getActivity().finish();
        	//	break;
        	//case R.id.imgBtn_to_notice:
        	//	startActivity(new Intent(getActivity(), NoticeListActivity.class));
        	//	break;
        	//case R.id.imgBtn_Add_Friend:
        		
        	//	break;
        	}
        }
    };

    private ExploreListAdapter.OnDetailClickListener detailListener = new ExploreListAdapter.OnDetailClickListener() {
		
		@Override
		public void onDetailClick(int pos) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getActivity(), ExploreDetailActivity.class);
			intent.putExtra(Constants.CATEGORY_DETAIL_TYPE, pos);
			startActivity(intent);
		}
	};
	
	private ExploreListAdapter.OnImgClickListener imgListener = new ExploreListAdapter.OnImgClickListener() {

		@Override
		public void onImgClick(int rowIndex, int colIndex) {
			// TODO Auto-generated method stub
			selectedModelid = arrList.get(rowIndex).getBroadcastList().get(colIndex).getUserid();
			mTheme = Integer.valueOf(arrList.get(rowIndex).getBroadcastList().get(colIndex).getCategory());

			PlayingItemResponse selectedItem = arrList.get(rowIndex).getBroadcastList().get(colIndex);

			String password = "";
			if (selectedItem.getRoomPass() != 0 && SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 0)
			{
				passwordDlg = new PasswordDialog(getContext());
				passwordDlg.setOnOkClickListener(select_okListener);
				passwordDlg.setCancelable(true);
				passwordDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
				passwordDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				try{
					passwordDlg.show();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				return;
			}
			new CheckRoomInAsync().execute(selectedModelid, password);
		}
	};
	//비번방인 경우 처리
	private PasswordDialog.OnOkClickListener select_okListener = new PasswordDialog.OnOkClickListener() {
		@Override
		public void onOkClick(String  password) {
			if (password != null)
				new CheckRoomInAsync().execute(selectedModelid, password);
			else {
				Toast.makeText(getContext(), getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
				getActivity().getSupportFragmentManager().popBackStack();
			}
			passwordDlg.dismiss();
		}
	};
	public void toBroadCastListenerFragment(String roomid, int theme){
		Intent data = new Intent(getActivity(), LiveVideoShowActivity.class);
		data.putExtra("roomid", roomid);
		data.putExtra("theme", theme);
		startActivity(data);
	}

    class ExploreListAsync extends AsyncTask<String, String, ExploreListResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			if (((CocotvingApplication)getActivity().getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(getContext());
				((CocotvingApplication)getActivity().getApplication()).mIsVisibleFlag = false;
			}
        }

        @Override
        protected ExploreListResponse doInBackground(String... args) {
        	ExploreListResponse result = info.syncExploreData();
            return result;
        }

        @Override
        protected void onPostExecute(ExploreListResponse result) {
        	super.onPostExecute(result);

            if (result != null && result.isSuccess()) {

        		ExploreItemData temp = new ExploreItemData();
        		temp.setBroadcastList(result.getNewList());
        		arrList.add(temp);

        		temp = new ExploreItemData();
        		temp.setBroadcastList(result.getRecomList());
        		arrList.add(temp);

        		temp = new ExploreItemData();
        		temp.setBroadcastList(result.getBestList());
        		arrList.add(temp);

        		temp = new ExploreItemData();
        		temp.setBroadcastList(result.getNearList());
        		arrList.add(temp);

            	listAdapter = new ExploreListAdapter(getActivity(), arrList);
            	listAdapter.setOnImgClickListener(imgListener);
            	listAdapter.setOnDetailClickListener(detailListener);
            	exploreList.setAdapter(listAdapter);
				exploreList.setOnScrollListener(new AbsListView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {}
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//						if (!view.canScrollList(1))
//							rootView.findViewById(R.id.temp_bottom_view).setVisibility(View.VISIBLE);
//						else
//							rootView.findViewById(R.id.temp_bottom_view).setVisibility(View.GONE);
					}
				});

            }
			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication)getActivity().getApplication()).mIsVisibleFlag = true;
				}
			},10);
        }
        
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

	public class CheckRoomInAsync extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (((CocotvingApplication)(getActivity().getApplication())).mIsVisibleFlag) {
				Utils.displayProgressDialog(getContext());
				((CocotvingApplication)(getActivity().getApplication())).mIsVisibleFlag = false;
			}
		}
		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result = info.syncCheckRoomIn(args[0], args[1]);
			return result;
		}
		@Override
		protected void onPostExecute(SuccessFailureResponse result) {
			super.onPostExecute(result);
			if (result != null && result.isSuccess()) {
				if (Integer.valueOf(result.getResult()) == 1)
					Toast.makeText(getContext(), getString(R.string.str_offline_info1),Toast.LENGTH_LONG).show();
				else if (Integer.valueOf(result.getResult()) == 2)
					Toast.makeText(getContext(), getString(R.string.str_adult_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 3)
					Toast.makeText(getContext(), getString(R.string.str_onlinenumber_over_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 4)
					Toast.makeText(getContext(), getString(R.string.str_enter_little_money_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 5)
					Toast.makeText(getContext(), getString(R.string.str_friend_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 6)
					Toast.makeText(getContext(), getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
				else if (Integer.valueOf(result.getResult()) == 7)
					Toast.makeText(getContext(), getString(R.string.str_force_out),Toast.LENGTH_LONG).show();
				else if (Integer.valueOf(result.getResult()) == 8)
					Toast.makeText(getContext(), getString(R.string.login_block_failure),Toast.LENGTH_LONG).show();
				else
					toBroadCastListenerFragment(selectedModelid, mTheme);
			}
			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication) (getActivity().getApplication())).mIsVisibleFlag = true;
				}
			}, 10);
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}
}
