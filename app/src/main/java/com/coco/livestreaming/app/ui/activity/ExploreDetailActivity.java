package com.coco.livestreaming.app.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.ModelDetailListResponse;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.adapter.ExploreDetailListAdapter;
import com.coco.livestreaming.app.ui.dialog.PasswordDialog;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExploreDetailActivity extends FragmentActivity {

	public final String TAG = ExploreDetailActivity.class.getName();
	GridView gvModelList;
	SyncInfo info;
	List<PlayingItemResponse> arrList;
	ExploreDetailListAdapter adapter;
	GridView list;
	LinearLayout tvNoDatas;
	Button btn_refresh;
	String selectedModelid;
	int mTheme;
	int syncType;
	PasswordDialog passwordDlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore_detail);	
		
		arrList = new ArrayList<PlayingItemResponse>();
		Intent intent = getIntent();
		syncType = intent.getIntExtra(Constants.CATEGORY_DETAIL_TYPE, Constants.CATEGORY_NEW);
		tvNoDatas = (LinearLayout)findViewById(R.id.layoutNoDatas);
		btn_refresh = (Button)findViewById(R.id.btn_refresh) ;
		btn_refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				syncPlayingListData();
			}
		});
		list = (GridView)findViewById(R.id.gv_model_list);
		info = new SyncInfo(this);
		syncPlayingListData();
	}

	void  syncPlayingListData() {
		switch(syncType){
			case Constants.CATEGORY_NEW:
				new PlayingListAsync().execute(getString(R.string.category_detail_new),"");
				((TextView)findViewById(R.id.tvExploreDetailTitle)).setText(getString(R.string.category_new));
				break;
			case Constants.CATEGORY_RECOM:
				new PlayingListAsync().execute(getString(R.string.category_detail_recom), "");
				((TextView)findViewById(R.id.tvExploreDetailTitle)).setText(getString(R.string.category_recom));
				break;
			case Constants.CATEGORY_BEST:
				new PlayingListAsync().execute(getString(R.string.category_detail_best), "");
				((TextView)findViewById(R.id.tvExploreDetailTitle)).setText(getString(R.string.category_best));
				break;
			case Constants.CATEGORY_NEAR:
				new PlayingListAsync().execute(getString(R.string.category_detail_near), "");
				((TextView)findViewById(R.id.tvExploreDetailTitle)).setText(getString(R.string.category_near));
				break;
		}
	}

	class PlayingListAsync extends AsyncTask<String, String, ModelDetailListResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(ExploreDetailActivity.this);
				((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
			}
        }

        @Override
        protected ModelDetailListResponse doInBackground(String... args) {
        	String str = args[0];
        	String str1 = args[1];
			ModelDetailListResponse result = info.syncPlayingList(str, str1);
            return result;
        }

        @Override
        protected void onPostExecute(ModelDetailListResponse result) {
        	super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
            	arrList = result.getModelList();
				//ModelItemResponse temp = arrList.get(0);

            	adapter = new ExploreDetailListAdapter(ExploreDetailActivity.this, arrList, result.getCategory());
            	list.setAdapter(adapter);
				adapter.setOnImgClickListener(imgListener);
                if (arrList.size() > 0) {
                    tvNoDatas.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                } else {
                    tvNoDatas.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                }
            } else {
            	tvNoDatas.setVisibility(View.VISIBLE);
            	list.setVisibility(View.GONE);
            }

			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
				}
			},10);
        }
        
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();;
        }
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
	
	public void onButtonClick(View v) {
		
        switch (v.getId()) {
            case R.id.imgBtn_Back:
            	finish();
            	break;
            default:
                return;
        }

    } // end of onButtonClick
	
	@Override
    public void onBackPressed() {
        finish();
    }

	private ExploreDetailListAdapter.OnImgClickListener imgListener = new ExploreDetailListAdapter.OnImgClickListener() {

		@Override
		public void onImgClick(int pos) {
			// TODO Auto-generated method stub
			selectedModelid = arrList.get(pos).getUserid();
			mTheme = Integer.valueOf(arrList.get(pos).getCategory());
			String password = "";
			if (arrList.get(pos).getRoomPass()!= 0 && SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 0)
			{
				passwordDlg = new PasswordDialog(ExploreDetailActivity.this);
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
				Toast.makeText(ExploreDetailActivity.this, getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
				getSupportFragmentManager().popBackStack();
			}
			passwordDlg.dismiss();
		}
	};
	public class CheckRoomInAsync extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(ExploreDetailActivity.this);
				((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
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
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.str_offline_info1),Toast.LENGTH_LONG).show();
				else if (Integer.valueOf(result.getResult()) == 2)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.str_adult_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 3)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.str_onlinenumber_over_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 4)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.str_enter_little_money_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 5)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.str_friend_info),Toast.LENGTH_SHORT).show();
				else if (Integer.valueOf(result.getResult()) == 6)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
				else if (Integer.valueOf(result.getResult()) == 7)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.str_force_out),Toast.LENGTH_LONG).show();
				else if (Integer.valueOf(result.getResult()) == 8)
					Toast.makeText(ExploreDetailActivity.this, getString(R.string.login_block_failure),Toast.LENGTH_LONG).show();
				else
					toBroadCastListenerFragment(selectedModelid, mTheme);
			}
			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication) getApplication()).mIsVisibleFlag = true;
				}
			}, 10);
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}
	public void toBroadCastListenerFragment(String roomid, int theme){
		Intent data = new Intent(this, LiveVideoShowActivity.class);
		data.putExtra("roomid", roomid);
		data.putExtra("theme", theme);
		startActivity(data);
	}


}
