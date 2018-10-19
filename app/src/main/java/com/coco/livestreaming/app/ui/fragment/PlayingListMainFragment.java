package com.coco.livestreaming.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.PlayingListResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.ExploreDetailActivity;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.ui.activity.MainActivity;
import com.coco.livestreaming.app.ui.activity.ProfileViewActivity;
import com.coco.livestreaming.app.ui.adapter.NowPlayingListAdapter;
import com.coco.livestreaming.app.ui.dialog.FanLiveListDialog;
import com.coco.livestreaming.app.ui.dialog.PasswordDialog;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.app.ui.adapter.UrlImagePagerAdapter;
import com.coco.livestreaming.app.ui.widget.AutoScrollViewPager;
import com.coco.livestreaming.app.ui.widget.PageIndicator;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import static com.coco.livestreaming.CocotvingApplication.getContext;
import static com.coco.livestreaming.R.id.btn_refresh;

public class PlayingListMainFragment extends Fragment implements View.OnClickListener {

	public final String TAG = PlayingListMainFragment.class.getName();

    private GridView                mNowPlayingGrid;
    private NowPlayingListAdapter   mPlayingItemListAdapter;
    private List<PlayingItemResponse> mPlayingItemList;
    private PlayingItemResponse mSelectedItem;
//    private String mSelectedUserid;
//    private int mTheme;
    private View rootView;
    AutoScrollViewPager recommendViewPager;
    PageIndicator pageIndicator;
    UrlImagePagerAdapter recommendImageAdapter;
    List<String> listRecommendImageUrl;
    PasswordDialog passwordDlg;
    private LinearLayout lbNoDatas;
    private SyncInfo    info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPlayingItemList = new ArrayList<PlayingItemResponse>();
        listRecommendImageUrl = new ArrayList<String>();
        info = new SyncInfo(getContext());

    	rootView = inflater.inflate(R.layout.fragment_playinglist, container, false);
        mNowPlayingGrid = (GridView)rootView.findViewById(R.id.lv_playingList);
        lbNoDatas = (LinearLayout)rootView.findViewById(R.id.layoutNoDatas);

        listRecommendImageUrl.add(Constants.IMG_SLIDE_URL + "/now_playing_top_bg.png");
        listRecommendImageUrl.add(Constants.IMG_SLIDE_URL + "/now_playing_top_bg1.png");
        listRecommendImageUrl.add(Constants.IMG_SLIDE_URL + "/now_playing_top_bg2.png");
        listRecommendImageUrl.add(Constants.IMG_SLIDE_URL + "/now_playing_top_bg3.png");
        listRecommendImageUrl.add(Constants.IMG_SLIDE_URL + "/now_playing_top_bg4.png");
        listRecommendImageUrl.add(Constants.IMG_SLIDE_URL + "/now_playing_top_bg5.png");
        recommendImageAdapter = new UrlImagePagerAdapter(listRecommendImageUrl).setInfiniteLoop(true);
        recommendViewPager = (AutoScrollViewPager) rootView.findViewById(R.id.autoScrollViewPager);
        recommendViewPager.setAdapter(recommendImageAdapter);
        recommendViewPager.setInterval(5000);
        pageIndicator = (PageIndicator) rootView.findViewById(R.id.pageIndicator);
        pageIndicator.setViewPager(recommendViewPager);

        rootView.findViewById(btn_refresh).setOnClickListener(this);
        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
        recommendViewPager.stopAutoScroll();
    }
    @Override
    public void onResume() {
        super.onResume();
        recommendViewPager.startAutoScroll();
        new PlayingListAsync().execute(Constants.PLAYING_LIST_NOW);//온라인 방송항목을 다시 얻어 재그리기 진행.
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_refresh) {
            new PlayingListAsync().execute(Constants.PLAYING_LIST_NOW);//온라인 방송항목을 다시 얻어 재그리기 진행.
        }
    }

    private NowPlayingListAdapter.OnImgClickListener imgListener = new NowPlayingListAdapter.OnImgClickListener() {

        @Override
        public void onImgClick(int pos) {
            // TODO Auto-generated method stub
            mSelectedItem = mPlayingItemList.get(pos);
            String password = "";
            String mSelectedUserid = mSelectedItem.getUserid();
            String strCategory = mSelectedItem.getCategory() == "" ? "0" : mSelectedItem.getCategory();
            int mTheme = Integer.valueOf(strCategory);

            if (mPlayingItemList.get(pos).getRoomPass() != 0 && SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 0)
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
            new CheckRoomInAsync().execute(mSelectedUserid, password);

        }
    };
    //비번방인 경우 처리
    private PasswordDialog.OnOkClickListener select_okListener = new PasswordDialog.OnOkClickListener() {
        @Override
        public void onOkClick(String  password) {
            if (password != null)
                new CheckRoomInAsync().execute(mSelectedItem.getUserid(), password);
            else {
                Toast.makeText(getContext(), getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
            passwordDlg.dismiss();
        }
    };

    public class PlayingListAsync extends AsyncTask<Integer, Integer, PlayingListResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)(getActivity().getApplication())).mIsVisibleFlag) {
                Utils.displayProgressDialog(getContext());
                ((CocotvingApplication)(getActivity().getApplication())).mIsVisibleFlag = false;
            }
        }
        @Override
        protected PlayingListResponse doInBackground(Integer... args) {
            PlayingListResponse result = info.syncPlayingList(String .valueOf(args[0]));
            return result;
        }
        @Override
        protected void onPostExecute(PlayingListResponse result) {
            super.onPostExecute(result);
            if (result != null) {
                mPlayingItemList = result.getList();
                mPlayingItemListAdapter = new NowPlayingListAdapter(getContext(), mPlayingItemList, result.getKey());
                mPlayingItemListAdapter.setOnImgClickListener(imgListener);
                mNowPlayingGrid.setAdapter(mPlayingItemListAdapter);

                mNowPlayingGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {}
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                        if (!view.canScrollList(1))
//                            rootView.findViewById(R.id.temp_bottom_view).setVisibility(View.VISIBLE);
//                        else
//                            rootView.findViewById(R.id.temp_bottom_view).setVisibility(View.GONE);
                    }
                });
                lbNoDatas.setVisibility(View.GONE);
                mNowPlayingGrid.setVisibility(View.VISIBLE);

                if (mPlayingItemList.isEmpty()) {
                    lbNoDatas.setVisibility(View.VISIBLE);
                    mNowPlayingGrid.setVisibility(View.GONE);
                }
            } else {
                lbNoDatas.setVisibility(View.VISIBLE);
                mNowPlayingGrid.setVisibility(View.GONE);
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)(getActivity().getApplication())).mIsVisibleFlag = true;
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
                    toBroadCastListenerFragment();
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

    public void toBroadCastListenerFragment(){
        Intent data = new Intent(getActivity(), LiveVideoShowActivity.class);
//        mSelectedItem
        String roomid = mSelectedItem.getUserid();
        String nickname = mSelectedItem.getNickname();
        String strCategory = mSelectedItem.getCategory() == "" ? "0" : mSelectedItem.getCategory();
        int theme = Integer.valueOf(strCategory);

        data.putExtra("roomid", roomid);
        data.putExtra("nickname", nickname);
        data.putExtra("theme", theme);
        startActivity(data);
    }
}
