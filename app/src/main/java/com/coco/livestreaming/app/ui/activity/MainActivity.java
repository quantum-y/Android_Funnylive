package com.coco.livestreaming.app.ui.activity;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.PlayingListResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.adapter.NowPlayingListAdapter;
import com.coco.livestreaming.app.ui.dialog.FanLiveListDialog;
import com.coco.livestreaming.app.ui.dialog.PasswordDialog;
import com.coco.livestreaming.app.ui.fragment.AuctionListFragment;
import com.coco.livestreaming.app.ui.fragment.ExplorerListFragment;
import com.coco.livestreaming.app.ui.fragment.PlayingListMainFragment;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.util.Utils;
import com.wowza.gocoder.sdk.api.encoder.WOWZEncoderAPI;
import com.wowza.gocoder.sdk.api.h264.WOWZProfileLevel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

	public final String TAG = MainActivity.class.getName();
    public static Fragment currentFragment;  //OnActivityResult()함수를 넘겨받기위한 fragment.

    private Fragment fragmentPlayingList;
    private Fragment fragmentExplore;
    private Fragment fragmentAuction;
    private Fragment fragmentCurrent;
    private Button mBtnTabLive;
    private ToggleButton mBtnTabHome;
    private ToggleButton mBtnTabExplorer;
    private ToggleButton mBtnTabAuction;
    private ImageButton btnToProfile;
    private ImageButton btnAlarm;
    private ImageButton btnAlarmOn;
    private ImageButton btnAuctionAdd;
    private FanLiveListDialog fanLiveDlg;
    private List<PlayingItemResponse> mPlayingItemList;
    private NowPlayingListAdapter mPlayingItemListAdapter;
    private SyncInfo    info;
    PasswordDialog      passwordDlg;
    private String      mSelectedUserid;
    private int      mTheme;
    static final int    BACK_TAP_INTERVAL = 2000;
    private long        backPressedTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
        mBtnTabLive = (Button) findViewById(R.id.btnTabLive);
        mBtnTabHome = (ToggleButton) findViewById(R.id.btnTabHome);
        mBtnTabExplorer = (ToggleButton) findViewById(R.id.btnTabExplorer);
        mBtnTabAuction = (ToggleButton) findViewById(R.id.btnAuction);
        btnToProfile = (ImageButton)findViewById(R.id.imgBtn_to_profile);
        btnAlarm = (ImageButton)findViewById(R.id.btn_alarm_id);
        btnAlarmOn = (ImageButton)findViewById(R.id.btn_alarm_on_id);
        btnAuctionAdd = (ImageButton)findViewById(R.id.btn_add_auction_id);

        if (SessionInstance.getInstance().getLoginData().getBjData().isAlarm() != 0){
            btnAlarm.setVisibility(View.VISIBLE);
        }else{
            btnAlarmOn.setVisibility(View.GONE);
            btnAlarm.setVisibility(View.GONE);
        }

        mPlayingItemList = new ArrayList<PlayingItemResponse>();
        info = new SyncInfo(this);
        Intent intent = getIntent();
		if (intent == null) {
            fragmentPlayingList = new PlayingListMainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frameMain, fragmentPlayingList).commit();
            fragmentCurrent = fragmentPlayingList;
            mBtnTabHome.setChecked(true);
        } else {
        	if(intent.getIntExtra(Constants.MAIN_TAB_INDEX, 1) == 1){
        		fragmentPlayingList = new PlayingListMainFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.frameMain, fragmentPlayingList).commit();
                fragmentCurrent = fragmentPlayingList;
                mBtnTabHome.setChecked(true);
        	} else {
        		fragmentExplore = new ExplorerListFragment();
        		getSupportFragmentManager().beginTransaction().add(R.id.frameMain, fragmentExplore).commit();
        		fragmentCurrent = fragmentExplore;
                mBtnTabExplorer.setChecked(false);
        	}
        }

	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        if (Constants.BACK_PRESSED_TIME + Constants.BACK_TAP_INTERVAL > System.currentTimeMillis()) {
            finish();
            return;
        } else {
            Toast.makeText(this, getResources().getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
        }
        Constants.BACK_PRESSED_TIME = System.currentTimeMillis();
    }
	public void onButtonClick(View v) {
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.remove(fragmentCurrent);

        switch (v.getId()) {
            case R.id.btnTabHome:
                mBtnTabHome.setChecked(true);
                mBtnTabExplorer.setChecked(false);
                mBtnTabAuction.setChecked(false);
                btnAuctionAdd.setVisibility(View.GONE);

                if (fragmentCurrent != fragmentPlayingList) {
                    if (fragmentPlayingList == null) {
                        fragmentPlayingList = new PlayingListMainFragment();
                    }
                    fragmentCurrent = fragmentPlayingList;
                } else {
                    return;
                }
                break;
            case R.id.btnTabLive:
                if(SessionInstance.getInstance() != null){
                    if(SessionInstance.getInstance().getLoginData().getBjData() == null){
                        //Toast.makeText(getBaseContext(), "BJ로 로그인을 해야 합니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                startActivity(new Intent(MainActivity.this, BroadcastActivity.class));
                //mBtnTabLive.setChecked(true);
                return;
            case R.id.btnTabExplorer:
                mBtnTabHome.setChecked(false);
                mBtnTabExplorer.setChecked(true);
                mBtnTabAuction.setChecked(false);
                btnAuctionAdd.setVisibility(View.GONE);

                if (fragmentCurrent != fragmentExplore) {
                    if (fragmentExplore == null) {
                        fragmentExplore = new ExplorerListFragment();
                    }
                    fragmentCurrent = fragmentExplore;
                }else {
                    return;
                }
                break;
            case R.id.btnAuction:
                mBtnTabHome.setChecked(false);
                mBtnTabExplorer.setChecked(false);
                mBtnTabAuction.setChecked(true);
                btnAuctionAdd.setVisibility(View.VISIBLE);

                if (fragmentCurrent != fragmentAuction) {
                    if (fragmentAuction == null) {
                        fragmentAuction = new AuctionListFragment();
                    }
                    fragmentCurrent = fragmentAuction;
                } else {
                    return;
                }
                break;
            case R.id.imgBtn_to_profile:
                toProfileActivity();
                return;
            case R.id.btn_alarm_on_id:
                new PlayingListAsync().execute(Constants.PLAYING_LIST_ALARM);
                break;
            case R.id.btn_alarm_id:
                new PlayingListAsync().execute(Constants.PLAYING_LIST_ALARM);
                break;
            case R.id.btn_add_auction_id:
                toAuctionActivity();
                return;
            case R.id.btn_close_id:
                if (Constants.BACK_PRESSED_TIME + Constants.BACK_TAP_INTERVAL > System.currentTimeMillis()) {
                    finish();
                    return;
                } else {
                    Toast.makeText(this, getResources().getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
                }
                Constants.BACK_PRESSED_TIME = System.currentTimeMillis();
            default:
                return;
        }
        transaction.replace(R.id.frameMain, fragmentCurrent);
        transaction.commit();

    } // end of onButtonClick

    public void toAuctionActivity(){
        Intent intent = new Intent(this, AuctionActivity.class);
        startActivity(intent);
    }

    public void toProfileActivity(){
        Intent intent = new Intent(this, ProfileViewActivity.class);
        startActivity(intent);
        finish();
    }
    public void toBroadCastListenerFragment(String roomid, int theme){
        Intent data = new Intent(this, LiveVideoShowActivity.class);
        data.putExtra("roomid", roomid);
        data.putExtra("theme", theme);
        startActivity(data);
    }
    public void onShowFanList(){
        fanLiveDlg = new FanLiveListDialog(this, mPlayingItemListAdapter);
        fanLiveDlg.setCancelable(false);
        fanLiveDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fanLiveDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            fanLiveDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private NowPlayingListAdapter.OnImgClickListener imgListener = new NowPlayingListAdapter.OnImgClickListener() {

        @Override
        public void onImgClick(int pos) {
            // TODO Auto-generated method stub
            mSelectedUserid = mPlayingItemList.get(pos).getUserid();
            String strCategory = mPlayingItemList.get(pos).getCategory() == "" ? "0" : mPlayingItemList.get(pos).getCategory();
            mTheme = Integer.valueOf(strCategory);

            String password = "";
            if (mPlayingItemList.get(pos).getRoomPass() != 0 && SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 0)
            {
                passwordDlg = new PasswordDialog(MainActivity.this);
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
                new CheckRoomInAsync().execute(mSelectedUserid, password);
            else {
                Toast.makeText(MainActivity.this, getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
                getSupportFragmentManager().popBackStack();
            }
            passwordDlg.dismiss();
        }
    };

   public class PlayingListAsync extends AsyncTask<Integer, Integer, PlayingListResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(MainActivity.this);
                ((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
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
            if (result != null && result.isSuccess()) {
                mPlayingItemList = result.getList();
                mPlayingItemListAdapter = new NowPlayingListAdapter(MainActivity.this, mPlayingItemList, result.getKey());
                mPlayingItemListAdapter.setOnImgClickListener(imgListener);
                if (result.getKey() == Constants.PLAYING_LIST_ALARM)
                    if (SessionInstance.getInstance().getLoginData().getBjData().isAlarm() != 0){
                        if (!mPlayingItemList.isEmpty())
                            btnAlarmOn.setVisibility(View.VISIBLE);
                        else
                            btnAlarmOn.setVisibility(View.GONE);
                    }else{
                        btnAlarmOn.setVisibility(View.GONE);
                        btnAlarm.setVisibility(View.GONE);
                    }
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onShowFanList();
                    }
                }, 20);
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
            Utils.disappearProgressDialog();
        }
    }

    public class CheckRoomInAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(MainActivity.this);
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
                    Toast.makeText(MainActivity.this, getString(R.string.str_offline_info1),Toast.LENGTH_LONG).show();
                else if (Integer.valueOf(result.getResult()) == 2)
                    Toast.makeText(MainActivity.this, getString(R.string.str_adult_info),Toast.LENGTH_SHORT).show();
                else if (Integer.valueOf(result.getResult()) == 3)
                    Toast.makeText(MainActivity.this, getString(R.string.str_onlinenumber_over_info),Toast.LENGTH_SHORT).show();
                else if (Integer.valueOf(result.getResult()) == 4)
                    Toast.makeText(MainActivity.this, getString(R.string.str_enter_little_money_info),Toast.LENGTH_SHORT).show();
                else if (Integer.valueOf(result.getResult()) == 5)
                    Toast.makeText(MainActivity.this, getString(R.string.str_friend_info),Toast.LENGTH_SHORT).show();
                else if (Integer.valueOf(result.getResult()) == 6)
                    Toast.makeText(MainActivity.this, getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
                else if (Integer.valueOf(result.getResult()) == 7)
                    Toast.makeText(MainActivity.this, getString(R.string.str_force_out),Toast.LENGTH_LONG).show();
                else if (Integer.valueOf(result.getResult()) == 8)
                    Toast.makeText(MainActivity.this, getString(R.string.login_block_failure),Toast.LENGTH_LONG).show();
                else
                    toBroadCastListenerFragment(mSelectedUserid, mTheme);
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
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
