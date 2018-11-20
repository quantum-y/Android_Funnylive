/**
 *  This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be test2 reference for the
 *  purpose of educating developers, and is not intended to be used in any production environment.
 *
 *  IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 *  OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 *  EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 *  WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *  Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 */

package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.FanItemResponse;
import com.coco.livestreaming.app.server.response.FriendItemResponse;
import com.coco.livestreaming.app.server.response.PanListResponse;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.PlayingListResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.dialog.BuyChocoDialog;
import com.coco.livestreaming.app.ui.dialog.ConfirmDialog;
import com.coco.livestreaming.app.ui.dialog.ModelProfileViewDialog;
import com.coco.livestreaming.app.ui.dialog.PasswordDialog;
import com.coco.livestreaming.app.ui.dialog.SelectDialog;
import com.coco.livestreaming.app.ui.dialog.SendChocoDialog;
import com.coco.livestreaming.app.ui.fragment.data.BroadcastSettingData;
import com.coco.livestreaming.app.ui.view.CircleImageView;
import com.coco.livestreaming.app.ui.widget.GuestPlayerView;
import com.coco.livestreaming.app.ui.widget.GuestVideo;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.ShareUtil;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.chat.Message;
import com.coco.livestreaming.chat.MessageAdapter;
import com.coco.livestreaming.gocoder.GoCoderCamera;
import com.coco.livestreaming.gocoder.ui.StatusView;
import com.hipmob.gifanimationdrawable.GifAnimationDrawable;
import com.squareup.picasso.Picasso;

import com.wowza.gocoder.sdk.api.android.opengl.WOWZGLES;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.geometry.WOWZSize;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.render.WOWZRenderAPI;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;

//import net.majorkernelpanic.streaming.gl.SurfaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.coco.livestreaming.CocotvingApplication.getContext;

public class LiveVideoShowActivity extends Activity {

    private final static String TAG = LiveVideoShowActivity.class.getSimpleName();
    public Context mContext;
    //소켓관련 변수
    private Socket mSocket;
    public String mRoomName = null;
    public String mNickName = "";
    public int mTheme;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView mMessagesView;
    private RecyclerView.Adapter mAdapter;
    private EditText mInputMessageView;
    private String mChatMode = Constants.ALERT_MSG_COMMON;
    private String mToUser = "everybody";
    // UI controls
    private ImageButton mBtnChat = null;
    private ToggleButton mBtnChatLock = null;
    private ImageButton mBtnShare = null;
    private ImageButton mBtnSwitchCamera = null;
    public  ImageButton mBtnScreenShot = null;
    private ImageButton mBtnBanana = null;
    private ImageButton mBtnRecomm = null;
    private ImageButton btnMic;
    //private ImageButton mBtnBroadcast = null;
    private CircleImageView mImgBjPhoto;
    private ModelProfileViewDialog mpDlg;
    private TextView mTxtRoomName;
    private TextView mTxtOnlineNum;
    private TextView mTxtHeartRank;
    private LinearLayout layShare;
    private LinearLayout layGuestList;
    private RelativeLayout layTopBar;
    private SelectDialog selectDlg;
    private BroadcastSettingData settingData = new BroadcastSettingData();
    private List<FanItemResponse> mGuestList = new ArrayList<FanItemResponse>();
    private SendChocoDialog bananaDlg;
    private BuyChocoDialog buyDlg;
    private int nSendBananaCnt;
    public StatusView   mMainStatusView;
    public ProgressBar  mMediaPlayerLoadingProgress;
    //GoCoder관련변수
    public GoCoderCamera mGoCoder = null;
    //Guest관련변수
    private GuestVideo  mGuestVideo = null;
    private String      mStreamURL = null;
    //초청자관련변수
    private ArrayList<GuestVideo> mInviteVideoViewList = new ArrayList<GuestVideo>();
    public boolean              mIsBJ = false;//true 방송자, false 시청자
    public boolean              mIsChatLock = false;//true 현재 방의 채팅이 잠금이면, false 오픈이면
    private int                 mNowInviteCount = 0;
//    public int                GUEST_INVITE_WIDTH = 0;
//    public int                GUEST_INVITE_HEIGHT = 0;

    //선물애니메이션관련변수
    private TranslateAnimation  mAniTransCome;
    private TranslateAnimation  mAniTransGo;
    private AlphaAnimation      mAniAlpha;
    private View                mPresentView;
    private ArrayList<PresentInfo>  mPresentList = new ArrayList<PresentInfo>();
    private Handler             mAniHandler = null;
    //웹봉사기관련변수
    private SyncInfo            mWebServer;

    private String              mJoinedUser = "";
    private String              mJoinedUserNickName = "";
    private String              mLeftUser = "";

    //스샷관련변수
    private AtomicBoolean   mGrabFrame      = new AtomicBoolean(false);
    private AtomicBoolean   mSavingFrame    = new AtomicBoolean(false);
    private ScheduledExecutorService    mTimerThread = null;
    private boolean         mIsUpload;
    private View.OnLayoutChangeListener onKeyBoardLayoutChangeListener;
    //LibStreaming Section
//    private LibStreamingCamera mLibCamera;
    public boolean mIsSelfCloseCamera = true;

    //망상태기발
    public boolean mIsConnectedWeb = true;
    public boolean mIsConnectedSocket =false;
    private long mPreviousTime = 0L;

    private boolean mIsPreFinish = false;
    public AudioManager mAudioManager;
    private android.media.MediaPlayer mHiddenBackgroundPlayer = null;

    //이전/다음 방송으로 가기 상태기발
    private  boolean isNextFlag, isPrevFlag;
    private List<PlayingItemResponse> mPlayingItemList;
    private PlayingItemResponse mNextStreamInfo;
    PasswordDialog passwordDlg;
    private String mSelectedUserid;

    // 에코방지를 위한 단추보이기 설정기발
    private static boolean FLAG_SHOW_ECHO_BUTTON = false;

    // 애님관련
    GifAnimationDrawable mGifDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_live_video_show);
        }catch (Exception e){//애니메션이 제대로 적재안되는 경우를 가상하여
            Toast.makeText(getApplicationContext(), "미안하지만 방을 다시 창조해주세요", Toast.LENGTH_LONG);
            finish();
            return;
        }
        mContext = this;

        //UI 콘트롤 초기화
//        mMainStatusView = (StatusView) findViewById(R.id.statusview_id);
        mBtnChat = (ImageButton) findViewById(R.id.btn_open_chat_id);
        mBtnChat.setOnClickListener(MainListener);
        mBtnChatLock = (ToggleButton) findViewById(R.id.btn_chat_lock_id);
        mBtnChatLock.setOnClickListener(MainListener);
        mBtnShare = (ImageButton) findViewById(R.id.btn_open_share_id);
        mBtnShare.setOnClickListener(MainListener);
        mBtnSwitchCamera = (ImageButton) findViewById(R.id.btn_switch_camera_id);
        mBtnSwitchCamera.setOnClickListener(MainListener);
        mBtnScreenShot = (ImageButton) findViewById(R.id.btn_screenshot_id);
        mBtnScreenShot.setOnClickListener(MainListener);
        mBtnBanana = (ImageButton) findViewById(R.id.btn_send_banana_id);
        mBtnBanana.setOnClickListener(MainListener);
        //mBtnBroadcast = (MultiStateButton) findViewById(R.id.ic_broadcast);
        //mBtnBroadcast.setOnClickListener(MainListener);
        mBtnRecomm = (ImageButton) findViewById(R.id.btn_send_recomm_id);
        mBtnRecomm.setOnClickListener(MainListener);
        btnMic = (ImageButton) findViewById(R.id.btn_mic);
        btnMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        btnMic.setBackground(getResources().getDrawable(R.drawable.btn_mic_on));
                        enableMicAndDisableSpeaker();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        btnMic.setBackground(getResources().getDrawable(R.drawable.btn_mic));
                        disableMicAndEnableSpeaker();
                        return true;
                    }
                }
                return false;
            }
        });

        mImgBjPhoto = (CircleImageView) findViewById(R.id.img_bj_photo_id);
        mImgBjPhoto.setOnClickListener(MainListener);
        mMessagesView = (RecyclerView) findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(mContext));
        layGuestList = (LinearLayout) findViewById(R.id.lay_guest_id);
        mInputMessageView = (EditText) findViewById(R.id.message_input);
        mTxtRoomName = (TextView) findViewById(R.id.txt_roomname_id);
        mTxtOnlineNum = (TextView) findViewById(R.id.txt_online_num_id);
        mTxtHeartRank = (TextView) findViewById(R.id.txt_heart_rank_id);
        layShare = (LinearLayout) findViewById(R.id.lay_share_id);
        layTopBar = (RelativeLayout) findViewById(R.id.lay_top_bar);
        mPresentView = findViewById(R.id.lay_present_view_id);
        mMediaPlayerLoadingProgress = (ProgressBar)findViewById(R.id.progress_mediaplayer_loading_id);
        mMediaPlayerLoadingProgress.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_close_room_id).setOnClickListener(MainListener);
        findViewById(R.id.btn_chat_send_id).setOnClickListener(MainListener);
        findViewById(R.id.btn_chat_off_id).setOnClickListener(MainListener);
        findViewById(R.id.btn_share_close_id).setOnClickListener(MainListener);

        Intent data = getIntent();
        if (data != null) {
            settingData.setRoomid(data.getStringExtra("roomid"));
            settingData.setTitle(data.getStringExtra("title"));
            settingData.setTheme(data.getIntExtra("theme",0));
            settingData.setPw(data.getStringExtra("pw"));
            settingData.setLimitNum(data.getIntExtra("limit_num", 500));
            settingData.setVideoQuality(data.getIntExtra("video_quality", Constants.VIDEO_QUALITY_LOW));
            settingData.setEnterChoco(data.getIntExtra("enter_choco", 0));
            settingData.setAdult(data.getBooleanExtra("blnAdult", false));
        }
        mRoomName = data.getStringExtra("roomid");//방이름 설정
        mNickName = data.getStringExtra("nickname");
        mTheme = data.getIntExtra("theme", 0);//방테마설정   4이면 방송자가 녹스로 판정

//        mStreamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + mRoomName;
        mStreamURL = "rtmp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + mRoomName;
        mTxtRoomName.setText(mNickName);
        //방에 들어온 가입자와 방이름이 같으면 방송자, 다르면 시청자로 판정하고 해당한 처리진행.
        mIsBJ = mRoomName.equals(SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        if (mIsBJ)//방송자이면 방송자고코도카메라를 초기화하고 카메라를 켠다.
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mBtnBanana.setVisibility(View.INVISIBLE);
            mBtnRecomm.setVisibility(View.INVISIBLE);
            /*mHiddenBackgroundPlayer = new android.media.MediaPlayer();
            try{
                mHiddenBackgroundPlayer.setDataSource(mContext, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hidden));
                mHiddenBackgroundPlayer.setLooping(true);
                mHiddenBackgroundPlayer.prepare();
                mHiddenBackgroundPlayer.start();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "미안하지만 방을 다시 창조해주세요", Toast.LENGTH_LONG);
                finish();
                return;
            }*/

        } else {//시청자이면 스트림보기화면을 초기화한다.
            //mBtnBroadcast.setVisibility(View.GONE);
            mBtnSwitchCamera.setVisibility(View.GONE);
            mBtnChatLock.setVisibility(View.GONE);
            if(!SessionInstance.getInstance().getLoginData().getBjData().getIsPossibleRecommend())
                mBtnRecomm.setVisibility(View.INVISIBLE);
        }
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // gesture
        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
        findViewById(R.id.layout_chat_container).setOnTouchListener(activitySwipeDetector);

        // set next/prev flag to false
        isNextFlag = false;
        isPrevFlag = false;
        mPlayingItemList = new ArrayList<PlayingItemResponse>();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                onPreResume();
            }
        },10);

        mIsPreFinish = false;

        this.registerReceiver(this.mEarPhoneReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        return;
    }

    private void enableMicAndDisableSpeaker() {
        // disable speaker
        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        //enable mic
        mAudioManager.setMicrophoneMute(false);
    }

    private void enableMicAndSpeaker() {
        // enable speaker
        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        //enable mic
        mAudioManager.setMicrophoneMute(false);
    }

    private void disableMicAndEnableSpeaker() {
        // enable speaker
        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        //diaable mic
        mAudioManager.setMicrophoneMute(true);
    }

    private BroadcastReceiver mEarPhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int state = intent.getIntExtra("state", 0);
            if (state == 0) { // ear phone is unplugged
                if (FLAG_SHOW_ECHO_BUTTON)
                    btnMic.setVisibility(View.VISIBLE);
//                disableMicAndEnableSpeaker();
            } else if (state == 1) {// ear phone is plugged
                if (FLAG_SHOW_ECHO_BUTTON)
                    btnMic.setVisibility(View.INVISIBLE);
//                enableMicAndSpeaker();
            }
        }
    };

    public class ActivitySwipeDetector implements View.OnTouchListener {
        static final String logTag = "ActivitySwipeDetector";
        private Activity activity;
        static final int MIN_DISTANCE = 100;
        private float downX, downY, upX, upY;

        public ActivitySwipeDetector(Activity activity){
            this.activity = activity;
        }

        public void onRightSwipe(){
            /*
            *  Swipe Right to Left
            *  hide message view if it is shown
            */
            if (mMessagesView.getVisibility() == View.VISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(LiveVideoShowActivity.this, R.anim.out_to_left);
                animation.setAnimationListener(new TranslateAnimation.AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                        mMessagesView.setVisibility(View.GONE);
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }
                });
                mMessagesView.startAnimation(animation);
            }
        }

        public void onLeftSwipe(){
            /**
             * Swipe Left to Right
             * show message view if it is hidden
            */
            if (mMessagesView.getVisibility() == View.GONE) {
                Animation animation = AnimationUtils.loadAnimation(LiveVideoShowActivity.this, R.anim.in_from_left);
                mMessagesView.setVisibility(View.VISIBLE);
                mMessagesView.startAnimation(animation);
            }
        }

        public void onDownSwipe(){
            /**
              *  Swipe Top to Bottom
             *  go to next video if not BJ
             */
            if (!mIsBJ) {
                isNextFlag = true;
                isPrevFlag = false;
                new PlayingListAsync().execute(Constants.PLAYING_LIST_NOW);//온라인 방송항목을 다시 얻어온다
            }
        }

        public void onUpSwipe(){
            /**
             * Swipe Bottom to Top
             *  go to prev video if not BJ
             */
            if (!mIsBJ) {
                isNextFlag = false;
                isPrevFlag = true;
                new PlayingListAsync().execute(Constants.PLAYING_LIST_NOW);//온라인 방송항목을 다시 얻어온다
            }
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if(Math.abs(deltaX) > Math.abs(deltaY))
                    {
                        if(Math.abs(deltaX) > MIN_DISTANCE){
                            // left or right
                            if(deltaX > 0) { this.onRightSwipe(); return true; }
                            if(deltaX < 0) { this.onLeftSwipe(); return true; }
                        }
                        else {
                            Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }
                    // swipe vertical?
                    else
                    {
                        if(Math.abs(deltaY) > MIN_DISTANCE){
                            // top or down
                            if(deltaY < 0) { this.onDownSwipe(); return true; }
                            if(deltaY > 0) { this.onUpSwipe(); return true; }
                        }
                        else {
                            Log.i(logTag, "Vertical Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP :
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return false;
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP :
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_BACK:
                onCloseRoom(null);
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
//        if(!isFinishing())
//            onPreFinish();
        if (mSocket != null) {
            stopTimer();
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("new message", onNewMessage);
            mSocket.off("user joined", onUserJoined);
            mSocket.off("user left", onUserLeft);
            mSocket = null;
            mIsConnectedSocket = false;
        }

        this.unregisterReceiver(this.mEarPhoneReceiver);
       super.onPause();
    }
    @Override
    public void onBackPressed() {

    }
    private void onPreResume(){

        mIsPreFinish = false;
       /* GUEST_INVITE_WIDTH = Constants.SCREEN_WIDTH / 3;//시청자가 초청될경우 자기화면에 표시될 카메라의 크기
        GUEST_INVITE_HEIGHT = GUEST_INVITE_WIDTH * 4 / 3;
        int tempHeight = findViewById(R.id.lay_chat_log).getHeight() - 3 * ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        if (3 * GUEST_INVITE_HEIGHT > tempHeight)
            GUEST_INVITE_HEIGHT = tempHeight / 3;*/
        mWebServer = new SyncInfo(mContext);
        mAdapter = new MessageAdapter(mContext, mMessages);
        mMessagesView.setAdapter(mAdapter);
        mAniHandler = new Handler(Looper.getMainLooper());

        mInviteVideoViewList.add(new GuestVideo(LiveVideoShowActivity.this, findViewById(R.id.lay_invite_video1_id), (GuestPlayerView)findViewById(R.id.invite_view1_id), (ImageView)findViewById(R.id.invite_thumbnail_view1_id), (TextView)findViewById(R.id.txt_invite_cover1_id)));
        mInviteVideoViewList.add(new GuestVideo(LiveVideoShowActivity.this, findViewById(R.id.lay_invite_video2_id), (GuestPlayerView)findViewById(R.id.invite_view2_id), (ImageView)findViewById(R.id.invite_thumbnail_view2_id), (TextView)findViewById(R.id.txt_invite_cover2_id)));
        mInviteVideoViewList.add(new GuestVideo(LiveVideoShowActivity.this, findViewById(R.id.lay_invite_video3_id), (GuestPlayerView)findViewById(R.id.invite_view3_id), (ImageView)findViewById(R.id.invite_thumbnail_view3_id), (TextView)findViewById(R.id.txt_invite_cover3_id)));

        WOWZCameraView WOWZCameraView = mIsBJ ? (WOWZCameraView) findViewById(R.id.WOWZCameraView_id):(WOWZCameraView) findViewById(R.id.guest_WOWZCameraView_id);
//        SurfaceView libCameraView = mIsBJ ? (SurfaceView)findViewById(R.id.libcamera_view_id):(SurfaceView)findViewById(R.id.guest_libcameraview_id);

        if (mIsBJ)//방송자이면 방송자고코도카메라를 초기화하고 카메라를 켠다.
        {
            if (FLAG_SHOW_ECHO_BUTTON)
                btnMic.setVisibility(View.VISIBLE);

            if (!CocotvingApplication.mIsEmulator) {//ifphone
                mGoCoder = new GoCoderCamera(LiveVideoShowActivity.this, WOWZCameraView, settingData.getVideoQuality(), mRoomName);
                mGoCoder.onInit();
            }
            else
            {
                //mLibCamera = new LibStreamingCamera(LiveVideoShowActivity.this, libCameraView, settingData.getVideoQuality(), "Nox_" + mRoomName);
//                settingData.setTheme(4);//방송자가 녹스이면 테마를 4로 설정함.
//                mLibCamera = new LibStreamingCamera(LiveVideoShowActivity.this, libCameraView, settingData.getVideoQuality(),mRoomName);
//                mLibCamera.onInit();
            }

        } else {//시청자이면 스트림보기화면을 초기화한다.
            if (!CocotvingApplication.mIsEmulator) //ifphone
                mGoCoder = new GoCoderCamera(LiveVideoShowActivity.this, WOWZCameraView, Constants.VIDEO_QUALITY_LOW, SessionInstance.getInstance().getLoginData().getBjData().getUserid());
            else {
                //mLibCamera = new LibStreamingCamera(LiveVideoShowActivity.this, libCameraView, Constants.VIDEO_QUALITY_LOW, "Nox_" + SessionInstance.getInstance().getLoginData().getBjData().getUserid());
//                mLibCamera = new LibStreamingCamera(LiveVideoShowActivity.this, libCameraView, Constants.VIDEO_QUALITY_LOW, SessionInstance.getInstance().getLoginData().getBjData().getUserid());
            }
            if (mTheme != 4 ) {//방송자가 녹스가 아니면
                findViewById(R.id.nox_videoview_id).setVisibility(View.GONE);
                mGuestVideo = new GuestVideo(LiveVideoShowActivity.this, findViewById(R.id.videoview_id), (GuestPlayerView) findViewById(R.id.videoview_id), null, null);
            }
            else {
                findViewById(R.id.videoview_id).setVisibility(View.GONE);
                mGuestVideo = new GuestVideo(LiveVideoShowActivity.this, findViewById(R.id.nox_videoview_id), (GuestPlayerView) findViewById(R.id.nox_videoview_id), null, null);
            }
            //mashang
//            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
            //mStreamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/vod/member1.mp4";
            //mGuestVideo.init(SessionInstance.getInstance().getLoginData().getBjData().getUserid(), mStreamURL);
//            mStreamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/vod/sample.mp4";
//            mStreamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/member2";
            mGuestVideo.init(mRoomName, mStreamURL);
        }

//        initPresentAnimation();
        //initCameraShot();2분에 한번씩 카메라캡쳐해서 봉사기에 올리는 기능
        //화면을 켜진상태로 유지한다.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //키보드로 하여 위치가 변할때 호출되는 리스너
        onKeyBoardLayoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(final View v, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layTopBar.setVisibility(View.GONE);
                        layTopBar.setVisibility(View.VISIBLE);
                    }
                }, 100);
            }
        };
        mInputMessageView.addOnLayoutChangeListener(onKeyBoardLayoutChangeListener);

//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
                createSocketIO();
//            }
//        },17000);

                            startTimer(Constants.DEFAULT_SCREENSHOT_INTERVAL);
        new SetRoomInfoAsync().execute("watch_on", "", "", "", "", "", "");

        //mashang
        //new SetRoomInfoAsync().execute("live_on", settingData.getTitle(), String.valueOf(settingData.getTheme()), String.valueOf(settingData.getLimitNum()), settingData.getPw(), String.valueOf(settingData.getEnterChoco()), String.valueOf(settingData.getAdult()));

    }
    public synchronized void onPreFinish(){
        if (mIsPreFinish) return;//이함수가 한번만 실행되게 하기 위하여

        CocotvingApplication.mIsInvalidExitFlag = false;
        //5S에 한번씩 update 하는 타이머중지
        stopTimer();
        //화면 켜진상태유지를 해소한다.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //음성체계를 본래대로 회복한다.
        if (!FLAG_SHOW_ECHO_BUTTON)
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        /*if (mHiddenBackgroundPlayer != null)
        {
            mHiddenBackgroundPlayer.release();
            mHiddenBackgroundPlayer = null;
        }*/
        mIsSelfCloseCamera = true;
        if (mGoCoder != null) {
            mGoCoder.onStop(true);
        }
//        if (mLibCamera != null) {
//            //mLibCamera.onStop(true);
//            if (mLibCamera.mClient != null) {
//                mLibCamera.mClient.release();
//                mLibCamera.mClient = null;
//            }
//            if (mLibCamera.mSession != null) {
//                //mLibCamera.mSession.release();
//                mLibCamera.mSession = null;
//            }
//        }
        if (mGuestVideo != null && mGuestVideo.mIsInit)
            mGuestVideo.Deinit();
        for (GuestVideo item : mInviteVideoViewList) {
            if (item != null && item.mIsInit) item.Deinit();
        }
        destroySocketIO();
        //선물애니메션처리중에있는 메세지 free
        if (mAniHandler != null)
            mAniHandler.removeCallbacksAndMessages(null);


        if (mMediaPlayerLoadingProgress.getVisibility() == View.VISIBLE)
            mMediaPlayerLoadingProgress.setVisibility(View.INVISIBLE);

        if (isNextFlag || isPrevFlag) {
            isPrevFlag = false;
            isNextFlag = false;
            mIsPreFinish = true;

            mRoomName = mNextStreamInfo.getUserid();//방이름 설정
            mNickName = mNextStreamInfo.getNickname();
            mTxtRoomName.setText(mNickName);
            String strCategory =mNextStreamInfo.getCategory() == "" ? "0" :mNextStreamInfo.getCategory();
            mTheme = Integer.valueOf(strCategory);
//            mStreamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + mRoomName;
            mStreamURL = "rtmp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + mRoomName;
            onPreResume();
        } else {
            mIsPreFinish = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGoCoder!= null && mGoCoder.mAutoFocusDetector != null)
            mGoCoder.mAutoFocusDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //화면안의 단추사건처리부
    private View.OnClickListener MainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_close_room_id:
                    onCloseRoom(v);
                    break;
                case R.id.btn_open_chat_id:
                    onOpenChat(v);
                    break;
                case R.id.btn_open_share_id:
                    onOpenShare(v);
                    break;
                case R.id.btn_switch_camera_id:
                    if (!CocotvingApplication.mIsEmulator) //ifphone
                        mGoCoder.switchCamera();
//                    else
////                        mLibCamera.switchCamera();
//                        mGoCoder.switchCamera();
                    break;
                case R.id.btn_send_banana_id:
                    onSendBanana(v);
                    break;
                case R.id.btn_send_recomm_id:
                    new RecommendAsync().execute(mRoomName);
                    break;
                case R.id.btn_chat_send_id:
//                    attemptSend(null, mChatMode, mToUser);
                    attemptSend(mInputMessageView.getText().toString(), mChatMode, mToUser);
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    break;
                case R.id.btn_chat_off_id:
                    onDisappearChat(v);
                    break;
                case R.id.btn_chat_lock_id:
                    onChatLock(v);
                    break;
                case R.id.btn_share_close_id:
                    layShare.setVisibility(View.GONE);
                    break;
                case R.id.img_bj_photo_id:
                    onOpenUserProfile(mRoomName);
                    break;
                case R.id.btn_screenshot_id:
                    onTakeScreenShot();
                    break;

            }
        }
    };

    public void onGoCoderWOWZStatus(final WOWZStatus goCoderStatus) {
        if (goCoderStatus.isRunning() ) {
            if (FLAG_SHOW_ECHO_BUTTON)
                disableMicAndEnableSpeaker();
            if (mIsBJ) {//와우자에 정확히 접속하여 스트림방송을 진행하는 경우 웹서버에 설정정보를 저장한다.
                CocotvingApplication.mIsInvalidExitFlag = false;//만일 이전에 비정상종료되엿엇다면 그 상태를 해제함.
                new SetRoomInfoAsync().execute("live_on", settingData.getTitle(), String.valueOf(settingData.getTheme()), String.valueOf(settingData.getLimitNum()), settingData.getPw(), String.valueOf(settingData.getEnterChoco()), String.valueOf(settingData.getAdult()));
            } else {//초청되여 스트림이 정상적으로 라이브되는 경우 3초지연하여 모두에게 알림.
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attemptSend(getString(R.string.str_invite_accept), Constants.ALERT_MSG_INVITE_YES, mRoomName);
                    }
                }, Constants.GOCODER_BUFFERING_TIME);
            }
        }
        else if (goCoderStatus.isIdle())
            if (mIsBJ){

                if (CocotvingApplication.mIsInvalidExitFlag)//만일 그전에 비정상종료된경우 와우자에 스트림정보가 남아서 방송이 안되는 경우 다시 재방송을 시도한다.
                {
                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {//약간 지연을 주지 않으면 련속 세네번정도 재시도를 진행한다.
                        @Override
                        public void run() {
                            mGoCoder.onInit();
                        }
                    }, Constants.INVALID_EXIT_TIME);
                    return;
                }
                for (GuestVideo item : mInviteVideoViewList) {
                    if (item.mIsInit) {
                        item.Deinit();
                        mNowInviteCount--;
                    }
                }
                //스트림봉사를 진행하지 않으면 탈퇴한다.탈퇴시 웹접속이 이미 실패한 상황이라면 서버에 부득불 저장하느라 수고하지 않고 저레 탈퇴로 ~~
                if (mIsConnectedWeb)
                    new SetRoomInfoAsync().execute("toHome", "", "", "", "", "","");
                else
                    onPreFinish();

            }else {//시청자쪽의 원인으로 하여 카메라가 꺼지는 경우 모두에게 알림.
                if (mIsSelfCloseCamera)
                    attemptSend("초청카메라중지.", Constants.ALERT_MSG_INVITE_NO, mRoomName);
                else
                    mIsSelfCloseCamera = true;
            }
        if (!mIsBJ)
            return;
        if (mMainStatusView != null) mMainStatusView.setStatus(goCoderStatus);

    }

    public void onGoCoderWZError(final WOWZStatus goCoderStatus) {
        if (mMainStatusView != null) mMainStatusView.setStatus(goCoderStatus);
    }

    private  void selectNextStream() {
        int currentIndex = -1;
        for (int i=0; i<mPlayingItemList.size(); i++) {
            PlayingItemResponse item = mPlayingItemList.get(i);
            if (item.getUserid().equals(mRoomName)) {
                currentIndex = i;
                break;
            }
        }
        mNextStreamInfo = null;
        if (currentIndex != -1) {
            if (isNextFlag) {
                if (currentIndex < mPlayingItemList.size() - 1) {
                    mNextStreamInfo = mPlayingItemList.get(currentIndex + 1);
                } else {
                    Toast.makeText(LiveVideoShowActivity.this, getString(R.string.str_no_next_stream), Toast.LENGTH_LONG).show();
                }
            } else if (isPrevFlag) {
                if (currentIndex > 0) {
                   mNextStreamInfo = mPlayingItemList.get(currentIndex - 1);
                } else {
                    Toast.makeText(LiveVideoShowActivity.this, getString(R.string.str_no_prev_stream), Toast.LENGTH_LONG).show();
                }
            }
        }
        if (mNextStreamInfo != null) {
            String password = "";
            mSelectedUserid = mNextStreamInfo.getUserid();
            String strCategory = mNextStreamInfo.getCategory() == "" ? "0" : mNextStreamInfo.getCategory();
            mTheme = Integer.valueOf(strCategory);

            if (mNextStreamInfo.getRoomPass() != 0 && SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 0)
            {
                passwordDlg = new PasswordDialog(LiveVideoShowActivity.this);
                passwordDlg.setOnOkClickListener(select_okListener);
                passwordDlg.setCancelable(false);
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
    }

    private PasswordDialog.OnOkClickListener select_okListener = new PasswordDialog.OnOkClickListener() {
        @Override
        public void onOkClick(String  password) {
            if (password != null)
                new CheckRoomInAsync().execute(mSelectedUserid, password);
            else {
                Toast.makeText(getContext(), getString(R.string.dialog_password_incorrect),Toast.LENGTH_LONG).show();
            }
            passwordDlg.dismiss();
        }
    };

    public class PlayingListAsync extends AsyncTask<Integer, Integer, PlayingListResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)(getApplication())).mIsVisibleFlag) {
                Utils.displayProgressDialog(getContext());
                ((CocotvingApplication)(getApplication())).mIsVisibleFlag = false;
            }
        }
        @Override
        protected PlayingListResponse doInBackground(Integer... args) {
            PlayingListResponse result = mWebServer.syncPlayingList(String .valueOf(args[0]));
            return result;
        }
        @Override
        protected void onPostExecute(PlayingListResponse result) {
            super.onPostExecute(result);
            if (result != null) {
                mPlayingItemList = result.getList();
                selectNextStream();
            } else {
                Toast.makeText(LiveVideoShowActivity.this, getString(R.string.str_no_live_stream_list), Toast.LENGTH_LONG).show();
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)(getApplication())).mIsVisibleFlag = true;
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
            if (((CocotvingApplication)(getApplication())).mIsVisibleFlag) {
                Utils.displayProgressDialog(getContext());
                ((CocotvingApplication)(getApplication())).mIsVisibleFlag = false;
            }
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            SuccessFailureResponse result = mWebServer.syncCheckRoomIn(args[0], args[1]);
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
                    new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");
//                new SetRoomInfoAsync().execute("watch_on", "", "", "", "", "","");
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication) (getApplication())).mIsVisibleFlag = true;
                }
            }, 10);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    //방에 들어온 유저목록얻기(쵸코보낸수량순위로), 현 비제이랭킹정보같이 얻어옴.
    class FanListAsync extends AsyncTask<String, String, PanListResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (CocotvingApplication.mIsVisibleFlag) {
                Utils.displayProgressDialog(LiveVideoShowActivity.this);
                CocotvingApplication.mIsVisibleFlag = false;
            }
        }
        @Override
        protected PanListResponse doInBackground(String... args) {
            String roomid = args[0];
            PanListResponse result = mWebServer.syncPanList(roomid);
            return result;
        }
        @Override
        protected void onPostExecute(PanListResponse result) {
            super.onPostExecute(result);

            Utils.disappearProgressDialog();
            CocotvingApplication.mIsVisibleFlag = true;

            if (mIsPreFinish)// 웹에서 자료를 얻어올 동안 이미 액티비티가 꺼진상태라면
                return;
            layGuestList.removeAllViews();
            if (result != null && result.isSuccess()) {
                mIsConnectedWeb = true;
                mGuestList = result.getPanList();
                mTxtOnlineNum.setText(String.valueOf(mGuestList.size()));
                //왼쪽웃구석의 방송자사진현시
                Picasso.with(mContext)
                        .load(Constants.IMG_MODEL_URL + mRoomName + File.separator + getFirstImage(mRoomName))
                        .placeholder(R.drawable.no_image)
                        .into(mImgBjPhoto);
                for (int i = 0; i < mGuestList.size(); i++) {
                    if (mGuestList.get(i).getFanID().equals(mRoomName) || mGuestList.get(i).getFanID().equals(mLeftUser))//방송자이거나 탈퇴한 시청자라면(시청자가 비정상적으로 종료된경우 웹서버에는 자료가 남아있으므로)
                        continue;
                    if (mGuestList.get(i).getFanID().equals(SessionInstance.getInstance().getLoginData().getBjData().getUserid())) {
                        SessionInstance.getInstance().getLoginData().getBjData().setIsAdmin(mGuestList.get(i).getIsAdmin());
                    }
                    RelativeLayout item = (RelativeLayout) View.inflate(LiveVideoShowActivity.this, R.layout.item_pan_member, null);
                    CircleImageView itemImg = (CircleImageView) item.findViewById(R.id.imgPhoto);
                    itemImg.setTag(i);
                    Picasso.with(LiveVideoShowActivity.this)
                            .load(Constants.IMG_MODEL_URL + mGuestList.get(i).getFanID() + File.separator + getFirstImage(mGuestList.get(i).getFanID()))
                            .placeholder(R.drawable.no_image)
                            .into(itemImg);
                    layGuestList.addView(item);
                    item.findViewById(R.id.imgPhoto).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onOpenUserProfile(mGuestList.get(Integer.valueOf(v.getTag().toString())).getFanID());
                        }
                    });
                }
                //가입자목록을 얻어올때 현BJ랭킹정보도 같이 얻어옴.
//                mTxtHeartRank.setVisibility(View.VISIBLE);
                mTxtHeartRank.setText(getResources().getString(R.string.str_rank_prifix, result.getRank_recommend()));
                //가입자목록을 얻어올때 지금방의 채팅잠금상태를 같이 얻어옴
                if (result.getChat_status().equals("chat_lock") && !mIsBJ && !isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid())) {
                    mBtnChat.setVisibility(View.GONE);
                    mIsChatLock = true;
                }
                //새로 가입한 시청자의 로그정보현시
                if (!mJoinedUser.isEmpty()) {
                    addLog(mJoinedUser, getResources().getString(R.string.message_user_joined), Constants.ALERT_MSG_COMMON, mJoinedUserNickName);
//                    if (!mIsBJ && CocotvingApplication.mIsEmulator && mLibCamera != null && mLibCamera.mClient != null && mLibCamera.mClient.isStreaming()){//초청된 화면이 존재하는 방에 입장한경우
//                        //attemptSend("초청화면현시", Constants.ALERT_MSG_INVITE_YES, "Nox_");
//                        attemptSend("초청화면현시", Constants.ALERT_MSG_INVITE_YES, mRoomName);
//                    }else
                        if (!mIsBJ && !CocotvingApplication.mIsEmulator && mGoCoder != null && mGoCoder.mWOWZBroadcast != null && mGoCoder.mWOWZBroadcast.getStatus().isRunning())
                        //attemptSend("초청화면현시", Constants.ALERT_MSG_INVITE_YES, "Phone_");
                        attemptSend("초청화면현시", Constants.ALERT_MSG_INVITE_YES, mRoomName);
                }
                mJoinedUser = "";
                mJoinedUserNickName = "";
                mLeftUser = "";
            }else if(result == null){
                mIsConnectedWeb = false;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }

    }

    //방송설정을 웹서버에 보관
    class SetRoomInfoAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (CocotvingApplication.mIsVisibleFlag) {
                Utils.displayProgressDialog(LiveVideoShowActivity.this);
                CocotvingApplication.mIsVisibleFlag = false;
            }
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            SuccessFailureResponse result = mWebServer.syncSetModelInfo(mRoomName, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            return result;
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);

            Utils.disappearProgressDialog();
            CocotvingApplication.mIsVisibleFlag = true;
            if (mIsPreFinish)// 웹에서 자료를 얻어올 동안 이미 액티비티가 꺼진상태라면
                return;

            if (result != null) {
                mIsConnectedWeb = true;
                if (result.isSuccess()) {
                    if (result.getResult().equals("watch_off") || result.getResult().equals("toHome") || result.getResult().equals("forcedOffline")) {
                        onPreFinish();
                    }else {
                        if (result.getResult().equals("live_on") || result.getResult().equals("watch_on")) {
//                            createSocketIO();
//                            startTimer(Constants.DEFAULT_SCREENSHOT_INTERVAL);
                        }
                        if (result.getResult().equals("live_on")) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SessionInstance.getInstance().setFriendList(new ArrayList<FriendItemResponse>());//방송시작시 세션에 등록된 친구목록을 초기화한다.
                                }
                            },1000);
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new FanListAsync().execute(mRoomName);
                            }
                        },6000);
                    }
                } else if (result.getResult().equals("live_on")){
                    Toast.makeText(LiveVideoShowActivity.this, getString(R.string.str_force_out), Toast.LENGTH_LONG).show();
                    onPreFinish();
                }
            } else {
                mIsConnectedWeb =false;
                Toast.makeText(LiveVideoShowActivity.this, getString(R.string.operation_failure), Toast.LENGTH_SHORT).show();
                isPrevFlag = false;
                isNextFlag = false;
                onPreFinish();
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    //바나나를 보낸 정보를 웹서버에 보관
    class SendBananaAsync extends AsyncTask<String, String, SuccessFailureResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (CocotvingApplication.mIsVisibleFlag) {
                Utils.displayProgressDialog(LiveVideoShowActivity.this);
                CocotvingApplication.mIsVisibleFlag = false;
            }
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            String str1 = args[0];
            String str2 = args[1];
            SuccessFailureResponse result = mWebServer.syncSendChoco(str1, str2);
            return result;
        }

        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            Utils.disappearProgressDialog();
            CocotvingApplication.mIsVisibleFlag = true;
            if (mIsPreFinish)// 웹에서 자료를 얻어올 동안 이미 액티비티가 꺼진상태라면
                return;

            if (result != null) {
                mIsConnectedWeb = true;
                if (result.isSuccess()) {
                    //현재 세션에 등록된 일부정보를 갱신한다.
                    int nowChocoCnt = SessionInstance.getInstance().getLoginData().getBjData().getChocoCnt();
                    int newChocoCnt = nowChocoCnt - nSendBananaCnt;
                    SessionInstance.getInstance().getLoginData().getBjData().setChocoCnt(newChocoCnt);
                    ResortTopLevel(SessionInstance.getInstance().getLoginData().getBjData().getUserid(), nSendBananaCnt);

                    if(mIsChatLock && !isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))//시청자이면서 현재가 채팅잠금상태인데 톱레벨이 아니면
                        mBtnChat.setVisibility(View.GONE);
                    else if(mIsChatLock && isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))//시청자이면서 현재가 채팅잠금상태인데 톱레벨이 되었으면
                        mBtnChat.setVisibility(View.VISIBLE);

                    //현재 멤버가 BJ에게 쵸코를 보낸 소식을 모두에게 알림.
                    attemptSend(result.getResult(), Constants.ALERT_MSG_CHOCO, String.valueOf(nSendBananaCnt));
                }
            }else if(result == null){
                Toast.makeText(LiveVideoShowActivity.this, getString(R.string.operation_banana_failure), Toast.LENGTH_LONG).show();
                mIsConnectedWeb = false;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    class RecommendAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (CocotvingApplication.mIsVisibleFlag) {
                Utils.displayProgressDialog(LiveVideoShowActivity.this);
                CocotvingApplication.mIsVisibleFlag = false;
            }
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            String str1 = args[0];
            SuccessFailureResponse result = mWebServer.syncRecommend(str1);
            return result;
        }

        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            Utils.disappearProgressDialog();
            CocotvingApplication.mIsVisibleFlag = true;
            if (mIsPreFinish)// 웹에서 자료를 얻어올 동안 이미 액티비티가 꺼진상태라면
                return;

            if (result != null) {
                mIsConnectedWeb = true;
                if (result.isSuccess()) {
                    //현재 멤버가 BJ를 추천한 소식을 모두에게 알림.
                    attemptSend(result.getResult(), Constants.ALERT_MSG_RECOMMAND, String.valueOf(0));
                    SessionInstance.getInstance().getLoginData().getBjData().setIsPossibleRecommend(false);
                } else {
                    //Toast.makeText(LiveVideoShowActivity.this, result.getResult(), Toast.LENGTH_LONG).show();
                }
                //하트단추는 더이상 보일필요가 없으므로
                mBtnRecomm.setVisibility(View.INVISIBLE);
            } else if(result == null){
                Toast.makeText(LiveVideoShowActivity.this, getString(R.string.operation_recommend_failure), Toast.LENGTH_SHORT).show();
                mIsConnectedWeb = false;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    //방에서 나갈때 (방송자가 나간 경우와 시청자가 나가는 경우를 따로 처리해야함)
    public void onCloseRoom(View v) {
        selectDlg = new SelectDialog(LiveVideoShowActivity.this, getString(R.string.dialog_broadcast_live_stop_text), 0);
        selectDlg.setCancelable(false);
        selectDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectDlg.setOnOkClickListener(new SelectDialog.OnOkClickListener() {
            @Override
            public void onOkClick(int flag) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!mIsConnectedWeb) {//만일 웹접속이 안된다며는 봉사기에 저장할 필요가 없이 즉시 탈퇴할것이다.
                            isNextFlag = false;
                            isPrevFlag = false;
                            onPreFinish();
                            return;
                        }
                        if (mIsBJ) // 방송자가 탈퇴한 경우
                        {
                            new SetRoomInfoAsync().execute("toHome", "", "", "", "", "","");//방송자가 탈퇴한 정보를 웹서버에 보관.

                        } else //시청자가 탈퇴한 경우
                        {
                            new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");
                        }
                    }
                },10);
                selectDlg.dismiss();
            }
        });

        try {
            selectDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void onOpenChat(View v) {
        LinearLayout layChatBar = (LinearLayout) findViewById(R.id.lay_bottom_chat_bar);
        layChatBar.setVisibility(LinearLayout.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layToolBar = (RelativeLayout) findViewById(R.id.lay_bottom_tool_bar);
                layToolBar.setVisibility(LinearLayout.GONE);
            }
        }, 100);

        if (mChatMode.equals(Constants.ALERT_MSG_SINGLE_CHAT))
            mInputMessageView.setHint(R.string.prompt_single_message);
        mInputMessageView.setText("");
    }
    public void onChatLock(View v){
        if (((ToggleButton)v).isChecked()){
            ((ToggleButton)v).setChecked(true);
            new UpdateRoomAsync().execute(Constants.STATE_CHAT_LOCK);
        }else {
            ((ToggleButton)v).setChecked(false);
            new UpdateRoomAsync().execute(Constants.STATE_CHAT_UNLOCK);
        }
    }
    public void onOpenShare(View v) {
        layShare.setVisibility(LinearLayout.VISIBLE);
    }

    public void onDisappearChat(View v) {
        LinearLayout layChatBar = (LinearLayout) findViewById(R.id.lay_bottom_chat_bar);
        layChatBar.setVisibility(LinearLayout.INVISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layToolBar = (RelativeLayout) findViewById(R.id.lay_bottom_tool_bar);
                layToolBar.setVisibility(LinearLayout.VISIBLE);
            }
        }, 100);


        mChatMode = Constants.ALERT_MSG_COMMON;//항상 챠트창이 없어질때는 공개대화창으로 초기화를 진행.
        mToUser = "everybody";
        mInputMessageView.setHint(R.string.prompt_everybody_message);
        mInputMessageView.setText("");

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    public void onSendBanana(View v) {
        bananaDlg = new SendChocoDialog(LiveVideoShowActivity.this, mNickName);
        bananaDlg.setCancelable(false);
        bananaDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bananaDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bananaDlg.setOnSendClickListener(new SendChocoDialog.OnSendClickListener() {
            @Override
            public void onSendClick(int nChoco) {
                nSendBananaCnt = nChoco;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new SendBananaAsync().execute(String.valueOf(nSendBananaCnt), mRoomName);
                    }
                }, 10);
                bananaDlg.dismiss();
            }
        });

        bananaDlg.setOnChargeClickListener(new SendChocoDialog.OnChargeClickListener() {
            @Override
            public void onChargeClick() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBuyBanana();
                    }
                }, 10);
                bananaDlg.dismiss();

            }
        });

        try {
            bananaDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void onBuyBanana() {
        buyDlg = new BuyChocoDialog(LiveVideoShowActivity.this, mRoomName);
        buyDlg.setCancelable(false);
        buyDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        buyDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            buyDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onOpenUserProfile(String userid) {
        mpDlg = new ModelProfileViewDialog(LiveVideoShowActivity.this, this, userid, mSocket);
        mpDlg.setCancelable(false);
        mpDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mpDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mpDlg.setOnInviteClickListener(new ModelProfileViewDialog.OnInviteClickListener() {
            @Override
            public void onInviteClick(String userid, String nickname) {
                if (mNowInviteCount < 3)
                    attemptSend(getString(R.string.str_invite_request, nickname), Constants.ALERT_MSG_SINGLE_INVITE, userid);
                mpDlg.dismiss();
            }
        });
        mpDlg.setOnChatClickListener(new ModelProfileViewDialog.OnChatClickListener() {
            @Override
            public void onChatClick(String userid) {
                if(!mIsBJ && mIsChatLock && !isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid())){
                    Toast.makeText(LiveVideoShowActivity.this, getString(R.string.str_chat_lock_on_state), Toast.LENGTH_SHORT).show();
                    return;
                }
                mChatMode = Constants.ALERT_MSG_SINGLE_CHAT;
                if (mRoomName.equals(userid))//시청자가 방송자에게 1:1메세지를 보내려는 경우
                    mToUser = userid + "_" + userid;
                else
                    mToUser = userid;
                mpDlg.dismiss();
                onOpenChat(null);
            }
        });
        if (!mIsBJ && !mRoomName.equals(userid))//시청자가 시청자선택
            mpDlg.mIsBottomToolBar = false;
        else if (mIsBJ && mRoomName.equals(userid))//방송자가 방송자선택
            mpDlg.mIsBottomToolBar = false;
        else if (!mIsBJ && mRoomName.equals(userid))//시청자가 방송자선택
            mpDlg.mIsInviteView = false;
        else if (mIsBJ && !mRoomName.equals(userid))//방송자가 시청자선택
        {
            if ((!CocotvingApplication.mIsEmulator && mNowInviteCount >= 3) || (CocotvingApplication.mIsEmulator && mNowInviteCount >= 1))//폰이면서 초대인원이 3명이상이거나 녹스이면서 1명이상이면
                mpDlg.mIsInviteView = false;
            else
                for (GuestVideo item : mInviteVideoViewList) {//이미 초대된 사람이면 초대단추가 사라짐
                    if (item.mIsInit && item.mUserID.equals(userid)) {
                        mpDlg.mIsInviteView = false;
                        break;
                    }
                }
            mpDlg.mIsForceOut = true;
        }
        try {
            mpDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onGuestVideoView(String key, String userid)//시청자의 화면에 방송이 나오는 시점에서 봉사기에 사용자상태를 보관한다.
    {
        if (mMediaPlayerLoadingProgress != null && mMediaPlayerLoadingProgress.getVisibility() == View.VISIBLE)
            mMediaPlayerLoadingProgress.setVisibility(View.INVISIBLE);

        if (!mIsBJ) { //시청자인경우
            if (mIsPreFinish)
                return;
            if (mIsConnectedWeb)
                new SetRoomInfoAsync().execute(key, "", "", "", "", "", "");
            else
                onPreFinish();

        }else{//AEC를 위하여 재방송
            if (mAudioManager.getMode() != AudioManager.MODE_IN_COMMUNICATION) {
//                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                mAudioManager.setSpeakerphoneOn(true);
            }
            //mashang
            //mGoCoder.endBroadcast(false);
        }
    }
    private void onGuestVideoReload(){//시청자의 화면이 굳어져있을경우 리로딩한다.
        if (mGuestVideo != null && mGuestVideo.mIsInit)
            mGuestVideo.Deinit();
        mGuestVideo.init(mRoomName, mStreamURL);
    }
    public void onSelectInviteRequestDlg() {//시청자가 방송자로부터 초청요청을 받앗을경우 선택여부처리

        final ConfirmDialog confirmDlg = new ConfirmDialog(LiveVideoShowActivity.this, mNickName, getFirstImage(mRoomName));
        confirmDlg.setCancelable(false);
        confirmDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDlg.setOnConfirmClickListener(new ConfirmDialog.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(boolean isYes) {
                if (isYes) {
                    //초청을 수락한경우에는 먼저 고코도를 초기화하고 화면을 라이브한 다음 그 성공여부에 따라 방송자에게 소식을 알림
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!CocotvingApplication.mIsEmulator)//ifphone
                                mGoCoder.mWOWZCameraView.setZOrderMediaOverlay(true);
                            else
//                                mLibCamera.mLibView.setZOrderMediaOverlay(true);
                                mGoCoder.mWOWZCameraView.setZOrderMediaOverlay(true);
                            if (FLAG_SHOW_ECHO_BUTTON)
                                btnMic.setVisibility(View.VISIBLE);

                            TranslateAnimation transBeginAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                            transBeginAni.setDuration(2000);
                            findViewById(R.id.lay_guest_camera_id).setVisibility(View.VISIBLE);
                            findViewById(R.id.txt_guest_camera_cover_id).setVisibility(View.VISIBLE);
                            Picasso.with(mContext)
                                    .load(Constants.IMG_MODEL_URL + SessionInstance.getInstance().getLoginData().getBjData().getUserid() + File.separator + getFirstImage(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))
                                    .placeholder(R.drawable.no_image)
                                    .into((ImageView)findViewById(R.id.guest_thumbnail_view_id));
                            ((TextView)findViewById(R.id.txt_guest_camera_cover_id)).setText(SessionInstance.getInstance().getLoginData().getBjData().getNickname());
                            findViewById(R.id.lay_guest_camera_id).startAnimation(transBeginAni);

                           new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (mIsPreFinish)// 그동안 이미 액티비티가 꺼진상태라면
                                        return;
                                    if (!CocotvingApplication.mIsEmulator) //ifphone
                                        mGoCoder.onInit();
                                    else
                                        mGoCoder.onInit();
//                                        mLibCamera.onInit();
                                }
                            }, 2000);
                        }
                    }, 10);

                } else//시청자가 초청을 거부한경우 방송자에게 초청거부결과를 방송자에게 알린다.
                    attemptSend(getString(R.string.str_invite_denied),
                            Constants.ALERT_MSG_SINGLE_CHAT,
                            mRoomName + "_" + mRoomName);//방송자인 경우에는 model1_model1로 보내야지 그렇지 않으면 모두에게 보내진다.

                confirmDlg.dismiss();
            }
        });

        try {
            confirmDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SocketIO 부분
    public void createSocketIO() {

        if (mSocket != null || mIsPreFinish)
            return;
        mSocket = ((CocotvingApplication) getApplication()).getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.connect();
    }

    public void destroySocketIO() {

        if (mSocket == null)
            return;
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket = null;
        mIsConnectedSocket = false;
        mBtnChat.setEnabled(mIsConnectedSocket);

    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mIsPreFinish)// 이미 액티비티가 꺼진상태라면
                        return;
                    if (!mIsConnectedSocket) {
                        if (mRoomName != null && mSocket != null) {
//                            mSocket.emit("add user", mRoomName + "/" + SessionInstance.getInstance().getLoginData().getBjData().getUserid() + "/" + SessionInstance.getInstance().getLoginData().getBjData().getNickname(), SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin());
                            int isAdmin = SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin();
//                            mSocket.emit("add user", "live_"+mRoomName + "/" + SessionInstance.getInstance().getLoginData().getBjData().getUserid() + "/"
//                                    + SessionInstance.getInstance().getLoginData().getBjData().getNickname() + "/" + String.valueOf(isAdmin));
                            mSocket.emit("add user", mRoomName + "/" + SessionInstance.getInstance().getLoginData().getBjData().getUserid() + "/"
                                    + SessionInstance.getInstance().getLoginData().getBjData().getNickname() + "/" + String.valueOf(isAdmin) + "/"
                                    + SessionInstance.getInstance().getLoginData().getBjData().getClasses());
                        }
                        //Toast.makeText(LiveVideoShowActivity.this, R.string.connect, Toast.LENGTH_LONG).show();
                        mIsConnectedSocket = true;
                        mBtnChat.setVisibility(View.VISIBLE);
                        if (SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin()  > 1) {
                            new SetRoomAdminAsync().execute(mRoomName,  "0");
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    if (mIsPreFinish)// 이미 액티비티가 꺼진상태라면
                        return;

                    mIsConnectedSocket = false;
                    mBtnChat.setVisibility(View.GONE);

                    //비정상적으로 챠트서버와 차단된경우
                    /*destroySocketIO();
                    if (mIsBJ) // 방송자가 탈퇴한 경우
                        new SetRoomInfoAsync().execute("toHome", "", "", "", "", "","");//방송자가 탈퇴한 정보를 웹서버에 보관.
                    else //시청자가 탈퇴한 경우
                        new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");*/

                    if(!mIsBJ && !mIsConnectedWeb)//만일 시청자가 웹과 소켓접속에 실패한경우 망이 불안하므로 방송화면이 중지되여잇을수 있으므로 다시 플레이를 시도해본다.
                    {
                        Toast.makeText(mContext, getString(R.string.operation_reloading_failure), Toast.LENGTH_LONG);
                        onGuestVideoReload();
                    }else if (!mIsBJ && mIsConnectedWeb){//만일 시청자가 소켓은 끊어졋으나 웹은 접속된다며는 다시한번 웹접속이 사실인지를 확인하기 위하여 재접속시도해본다.
                        new UpdateRoomAsync().execute(Constants.STATE_INTERVAL_UPDATE);
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIsConnectedSocket = false;
                    mBtnChat.setVisibility(View.GONE);
                    Toast.makeText(mContext, "onConnectError", Toast.LENGTH_LONG);
                    //비정상적으로 챠트서버와 차단된경우
                    /*destroySocketIO();
                    if (mIsBJ) // 방송자가 탈퇴한 경우
                    {
                        new SetRoomInfoAsync().execute("toHome", "", "", "", "", "","");//방송자가 탈퇴한 정보를 웹서버에 보관.

                    } else //시청자가 탈퇴한 경우
                    {
                        new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");
                    }*/
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mIsPreFinish)// 이미 액티비티가 꺼진상태라면
                        return;
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String nickname;
                    String message;
                    String action;
                    String sVal;
                    try {
                        username = data.getString("username");
                        nickname = data.getString("nickname");
                        message = data.getString("message");
                        action = data.getString("action");
                        sVal = data.getString("value");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    if (action.equals(Constants.ALERT_MSG_RECOMMAND)) {
                        if (mIsBJ) {//방송자인경우 일부세션정보를 변경
                           //int nNow = SessionInstance.getInstance().getLoginData().getBjData().getRecommendation();
                           //SessionInstance.getInstance().getLoginData().getBjData().setRecommendation(nNow + 1);
                        }
                        //addLog(username, message, Constants.ALERT_MSG_RECOMMAND);
                        addLog(username, "", Constants.ALERT_MSG_RECOMMAND, nickname);//send하고 하트만 표시하기 위하여
                    } else if (action.equals(Constants.ALERT_MSG_CHOCO)) {
                        if (mIsBJ) {//방송자경우 세션의 쵸코개수를 변경 ,자료기지는 이미 변경된상태임.
                            int nNow = SessionInstance.getInstance().getLoginData().getBjData().getChocoCnt();
                            int nNew = nNow + Integer.valueOf(sVal);
                            SessionInstance.getInstance().getLoginData().getBjData().setChocoCnt(nNew);
                        }
                        //addLog(username, message, Constants.ALERT_MSG_COMMON);
                        addLog(username, getString(R.string.str_present_egg_count, nickname, sVal), Constants.ALERT_MSG_COMMON, nickname);
                        PresentInfo addInfo = new PresentInfo();
                        addInfo.user_name = username;
                        addInfo.nick_name = nickname;
                        addInfo.banana_count = sVal;
                        mPresentList.add(addInfo);
                        if (mPresentList.size() >= 1 /*&& mIsBJ*/)
                        {
                            startGifAnim();
//                            mPresentView.setVisibility(View.VISIBLE);
//                            mPresentView.startAnimation(mAniTransCome);
                        }
                        //왕관아이콘을 위하여 팬리스트의 toplevel을  재정렬한다
                        ResortTopLevel(username, Integer.valueOf(sVal));
                        //mashang 요구사양이 어떻게 달라질지 모른다.현재는 왕관쓴경우만 채팅가능하게 함
                        if(!mIsBJ && mIsChatLock && !isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))//시청자이면서 현재가 채팅잠금상태인데 톱레벨이 아니면
                            mBtnChat.setVisibility(View.GONE);
                        else if(!mIsBJ && mIsChatLock && isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))//시청자이면서 현재가 채팅잠금상태인데 톱레벨이 되었으면
                            mBtnChat.setVisibility(View.VISIBLE);
                    } else if (action.equals(Constants.ALERT_MSG_SINGLE_INVITE)) {//시청자가 방송자로부터 초대요청을 받은경우
                        onSelectInviteRequestDlg();
                    } else if (action.equals(Constants.ALERT_MSG_INVITE_YES)) {//시청자로부터 초대접수통보를 받은경우
                        if (mNowInviteCount >= 3)
                            return;
                        if (!mIsBJ && CocotvingApplication.mIsEmulator)//녹스시청자인경우에는 Vitamio 오유로 하여 초청화면을 보여줄수 없음.
                            return;
                        if (mIsBJ && CocotvingApplication.mIsEmulator && mNowInviteCount >= 1)//녹스방송자인경우에는 1명이상초청할수 없다.
                            return;
                        for (GuestVideo item : mInviteVideoViewList) {
                            if (item != null && item.mUserID != null && item.mUserID.equals(username)) {//이미 초청된 스트림이 플레이 되고있는중이라면
                                return;
                            }
                        }
                        mNowInviteCount++;
                        String streamURL = "";
                        /*if (sVal != null && sVal.equals("Nox_") )
                            streamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/Nox_" + username;
                        else if (sVal != null && sVal.equals("Phone_"))
                            streamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + username;*/

                        streamURL = "rtmp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + username;
                        //mashang
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        /*for (FanItemResponse item : mGuestList){
                            if (item.getFanID().equals(username)) {
                                streamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/" + Constants.WZ_LIVE_APP_NAME + "/" + username;
                                break;
                            }
                        }
                        if (streamURL.isEmpty())
                            //streamURL = "android.resource://" + getPackageName() + "/" + R.raw.a;
                            streamURL = "rtsp://" + Constants.WZ_LIVE_HOST_ADDRESS + ":" + Constants.WZ_LIVE_PORT + "/vod/" + username + ".mp4";*/
                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        for (GuestVideo item : mInviteVideoViewList) {
                            if (!item.mIsInit) {
                                item.init(username, streamURL);
                                break;
                            }
                        }
                        addLog(username, message, Constants.ALERT_MSG_COMMON, nickname);
                    }else if (action.equals(Constants.ALERT_MSG_INVITE_NO)){//시청자로부터 자기의 와우자접속에 문제 생겻음을 알려올때
                        onGuestVideoClose(username);
                        addLog(username, message, Constants.ALERT_MSG_COMMON, nickname);
                    }else if (action.equals(Constants.ALERT_MSG_INVITE_REJECT)){//시청자에게 방송자로부터 초청카메라가 close 되였다는 통보를 알려올 경우
                        if (sVal.equals(SessionInstance.getInstance().getLoginData().getBjData().getUserid())) {//초청자라면 고코다나 리브를 스톱 , 일반시청자라면 해당초청화면을 스톱
                            mIsSelfCloseCamera = false;
                            if (!CocotvingApplication.mIsEmulator) //ifphone
                                mGoCoder.onStop(false);
                            else
                                mGoCoder.onStop(false);
//                                mLibCamera.onStop(false);
                        }else{
                            onGuestVideoClose(sVal);
                        }
                        addLog(username, message,Constants.ALERT_MSG_COMMON, nickname);
                    }else if(action.equals(Constants.ALERT_MSG_CHAT_LOCK)){//방송자가 채팅을 잠근경우
                        mIsChatLock = true;
                        if(!mIsBJ && !isPossibleToChat(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))//시청자이면서 톱레벨이 아니면
                        {
                            mBtnChat.setVisibility(View.GONE);
                            onDisappearChat(findViewById(R.id.btn_chat_off_id));
                        }
                        addLog(username, message,Constants.ALERT_MSG_COMMON, nickname);
                    }else if(action.equals(Constants.ALERT_MSG_CHAT_UNLOCK)){//방송자가 채팅을 오픈한 경우
                        mIsChatLock = false;
                        mBtnChat.setVisibility(View.VISIBLE);
                        addLog(username, message,Constants.ALERT_MSG_COMMON, nickname);
                    }else if (action.equals(Constants.ALERT_MSG_SINGLE_FORCE_OUT))//방송자가 시청자를 강제탈퇴시킨경우
                    {
                        Toast.makeText(mContext, getString(R.string.str_force_out), Toast.LENGTH_LONG).show();
                        new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");
                    }else if (action.equals(Constants.ALERT_MSG_ADMIN_FORCE_OFFLINE))//관리자가 방송자를 강제탈퇴시킨경우
                    {
                        if (mIsBJ && sVal.equals(SessionInstance.getInstance().getLoginData().getBjData().getUserid()))
                        {
                            new SetRoomInfoAsync().execute("forcedOffline", "", "", "", "", "","");
                            Toast.makeText(mContext, getString(R.string.str_force_out), Toast.LENGTH_LONG).show();
                        }
                    }else if (action.equals(Constants.ALERT_MSG_INVITE_RELOADING)){
                        Toast.makeText(mContext, getString(R.string.str_invite_reloading), Toast.LENGTH_LONG).show();
                        //if (!mIsBJ && !CocotvingApplication.mIsEmulator && mGoCoder.mWOWZBroadcast.getStatus().isRunning())//만일 초청자라면 리로딩을 하지 않는다.
                        //{
                            mMediaPlayerLoadingProgress.setVisibility(View.VISIBLE);
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mMediaPlayerLoadingProgress != null)
                                        mMediaPlayerLoadingProgress.setVisibility(View.INVISIBLE);
                                }
                            }, 5000);

                        //}else
                        //    onGuestVideoReload();
                    }
                    else if (action.equals(Constants.ALERT_MSG_NETWORK_CHECK)){//망상태검사를 위하여 방송자가 보내는 신호
                        mPreviousTime = System.currentTimeMillis();
                    }
                    else if (action.equals(Constants.ALERT_MSG_SET_ROOMADMIN)){//매니저설정/해제, 벙어리설정/해제 시 팬목록갱신
                        new FanListAsync().execute(mRoomName);
                    }
                    else {
                        addLog(username, message, Constants.ALERT_MSG_COMMON, nickname);
                    }

                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mIsPreFinish)// 이미 액티비티가 꺼진상태라면
                        return;

                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String nickname;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        nickname = data.getString("nickname");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    //addLog(username, getResources().getString(R.string.message_user_joined), Constants.ALERT_MSG_COMMON);
                    //로그정보는 레벨아이콘표시를 위해서 FanListAsync에서 진행.
                    mJoinedUser = username;
                    mJoinedUserNickName = nickname;
                    //멤버입장시 가입자목록갱신
                    new FanListAsync().execute(mRoomName);

                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mIsPreFinish)// 이미 액티비티가 꺼진상태라면
                        return;

                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String nickname;
                    int numUsers;
                    boolean isExit;
                    try {
                        username = data.getString("username");
                        nickname = data.getString("nickname");
                        numUsers = data.getInt("numUsers");
                        isExit = data.getBoolean("flag");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    //멤버퇴장시 가입자목록갱신
                    addLog(username, getResources().getString(R.string.message_user_left), Constants.ALERT_MSG_COMMON, nickname);
                    if (isExit && !mIsBJ)//방송자가 탈퇴한경우
                    {
                        Toast.makeText(LiveVideoShowActivity.this, getString(R.string.room_out_alert), Toast.LENGTH_LONG).show();
                        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //onPreFinish();
                                new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");
                            }
                        }, 2000);
                    } else//어떤 유저가 탈퇴하는 경우 가입자목록갱신
                    {
                        //탈퇴한 유저가 초대되였던 유저라면
                        for (GuestVideo item : mInviteVideoViewList) {
                            if (item.mIsInit && item.mUserID.equals(username)) {
                                item.onDisappear();
                                //item.Deinit();
                                mNowInviteCount--;
                            }
                        }
                        mLeftUser = username;
                        new FanListAsync().execute(mRoomName);
                    }
                }
            });
        }
    };

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void addLog(String username, String message, String style, String nickname) {
        FanItemResponse item = getFanItem(username);
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .username(nickname).message(message).suffix(style).level(item != null ? item.getLevel_num() : 0, item != null ? (item.getTopLevel() ? -1:item.getLevel_color()) : 0).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

//    private void addMessage(String username, String message) {
//        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
//                .username(username).suffix(Constants.ALERT_MSG_COMMON).message(message).build());
//        mAdapter.notifyItemInserted(mMessages.size() - 1);
//        scrollToBottom();
//    }

    private void removeLog() {
        int i = mMessages.size() - 1;
        while (i >= 0) {
            mMessages.remove(mMessages.get(i--));
        }
        mAdapter.notifyDataSetChanged();
    }

    public void attemptSend(String message, String action, String value) {
        if (null == mRoomName) return;
        if (mIsConnectedSocket == false || !mSocket.connected())  {
            Toast.makeText(getApplicationContext(), R.string.operation_chat_failure, Toast.LENGTH_LONG).show();
            return;
        }

        if (SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 3) {
            Toast.makeText(getApplicationContext(), R.string.operation_chat_dumb, Toast.LENGTH_LONG).show();
            return;
        }
        if (action.equals(Constants.ALERT_MSG_COMMON) || action.equals(Constants.ALERT_MSG_SINGLE_CHAT)) {
            message = mInputMessageView.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                mInputMessageView.requestFocus();
                return;
            }
            mInputMessageView.setText("");
            addLog(SessionInstance.getInstance().getLoginData().getBjData().getUserid(), message, action, SessionInstance.getInstance().getLoginData().getBjData().getNickname());
        } else if (action.equals(Constants.ALERT_MSG_RECOMMAND)) {
            addLog(SessionInstance.getInstance().getLoginData().getBjData().getUserid(), "", Constants.ALERT_MSG_RECOMMAND, SessionInstance.getInstance().getLoginData().getBjData().getNickname());
        }else if(action.equals(Constants.ALERT_MSG_NETWORK_CHECK)){
            //망상태체크로써 로그를 남길필요가 없음.
        }
        else {
            addLog(SessionInstance.getInstance().getLoginData().getBjData().getUserid(), message, Constants.ALERT_MSG_COMMON, SessionInstance.getInstance().getLoginData().getBjData().getNickname());
        }
        // perform the sending message attempt.

        String realMessage = message + "/" + action + "/" + value;
        mSocket.emit("new message", realMessage);
//        mSocket.emit("new message", message, action, value);

    }

    public FanItemResponse getFanItem(String username) {
        if (mGuestList.isEmpty())
            return null;
        for (FanItemResponse item : mGuestList) {
            if (item.getFanID().equals(username))
                return item;
        }
        return null;
    }

    private void startGifAnim() {
        if (mGifDrawable != null && mGifDrawable.isRunning())
            return;

        PresentInfo now = mPresentList.get(0);

        int bananaCnt = Integer.valueOf(now.banana_count);
        int drawable = R.raw.eggs;
        if (bananaCnt == 1)
            drawable = R.raw.eggs_1;
        else if (bananaCnt == 10)
            drawable = R.raw.eggs_10;
        else if (bananaCnt == 100)
            drawable = R.raw.eggs_100;
        else if (bananaCnt == 1000)
            drawable = R.raw.eggs_1k;
        else if (bananaCnt == 10000)
            drawable = R.raw.eggs_10k;

        ((TextView)findViewById(R.id.txt_user_id)).setText("by  " + now.nick_name);
        final ImageView img_gif = (ImageView) findViewById(R.id.img_anim) ;
        mPresentView.setVisibility(View.VISIBLE);

        try {
            mGifDrawable = new GifAnimationDrawable(getResources().openRawResource(drawable)) {
                public void onGotTotalTime(final int totalTime) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mPresentView.setVisibility(View.GONE);
                                    mGifDrawable.stop();

                                    mPresentList.remove(0);
                                    if (!mPresentList.isEmpty())
                                        startGifAnim();
                                }
                            });
                        }
                    };
                    thread.run();
                }
            };
            mGifDrawable.setOneShot(false);

            img_gif.setImageDrawable(mGifDrawable);
            mGifDrawable.setVisible(true, true);

            img_gif.post(new Runnable() {

                @Override
                public void run() {
                    mGifDrawable.start();
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initPresentAnimation(){
        mAniTransCome = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1,Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
        mAniTransCome.setDuration(1000);

        mAniAlpha = new AlphaAnimation(0, 1);
        mAniAlpha.setDuration(500);
        mAniAlpha.setStartOffset(0);
        mAniAlpha.setRepeatCount(3);
        mAniAlpha.setRepeatMode(Animation.REVERSE);
        mAniTransGo = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
        mAniTransGo.setDuration(1000);

        mAniTransCome.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                PresentInfo now = mPresentList.get(0);
                if (now != null){
//                    Picasso.with(mContext)
//                            .load(Constants.IMG_MODEL_URL + now.user_name + File.separator + getFirstImage(now.user_name))
//                            .placeholder(R.drawable.no_image)
//                            .into(mImgPresentPhoto);
//                    ((TextView)findViewById(R.id.txt_present_send_id)).setText(now.user_name);
//                    ((TextView)findViewById(R.id.txt_present_banana_cnt)).setText(now.banana_count);
                    findViewById(R.id.lay_present_view_id).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mAniHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPresentView.startAnimation(mAniAlpha);
                    }
                }, 3000);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAniAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mPresentView.startAnimation(mAniTransGo);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAniTransGo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mPresentView.setVisibility(View.GONE);
                mPresentList.remove(0);
                if (!mPresentList.isEmpty())
                    mPresentView.startAnimation(mAniTransCome);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    private class PresentInfo{
        public String user_name;
        public String nick_name;
        public String banana_count;
        public PresentInfo(){
            user_name = "";
            nick_name = "";
            banana_count = "";
        }
    }
    private void onTakeScreenShot(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.lv_container);
        ShareUtil.savePicture(layout);
    }
    public void onTakeCameraShot() {
        // Setting mGrabFrame to true will trigger the video frame listener to become active
        if (!mGrabFrame.get() && !mSavingFrame.get()) {
            /*mBtnScreenShot.setEnabled(false);
            mBtnScreenShot.setClickable(false);*/
            mGrabFrame.set(true);
        }
    }
    private void initCameraShot() {
        // Create an register test2 video frame listener with WZCameraPreview

        WOWZRenderAPI.VideoFrameListener videoFrameListener = new WOWZRenderAPI.VideoFrameListener() {
            @Override
            public boolean isWZVideoFrameListenerActive() {
                return (mGrabFrame.get() && !mSavingFrame.get());
            }

            @Override
            public void onWZVideoFrameListenerInit(WOWZGLES.EglEnv eglEnv) {

            }

            @Override
            public void onWZVideoFrameListenerFrameAvailable(WOWZGLES.EglEnv eglEnv, WOWZSize WOWZSize, int i, long l) {
                mSavingFrame.set(true);
                mGrabFrame.set(false);

                // create test2 pixel buffer and read the pixels from the camera preview display surface using GLES
                final WOWZSize bitmapSize = new WOWZSize(WOWZSize.width, WOWZSize.width);
                final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(bitmapSize.getWidth() * bitmapSize.getHeight() * 4);
                pixelBuffer.order(ByteOrder.LITTLE_ENDIAN);
                GLES20.glReadPixels(0, 0, bitmapSize.getWidth(), bitmapSize.getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
                final int eglError = WOWZGLES.checkForEglError(TAG + "(glReadPixels)");
                if (eglError != EGL14.EGL_SUCCESS) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*mBtnScreenShot.setEnabled(true);
                            mBtnScreenShot.setClickable(true);
                            Toast.makeText(getApplicationContext(), WOWZGLES.getEglErrorString(eglError), Toast.LENGTH_LONG).show();*/
                        }
                    });

                    mSavingFrame.set(false);
                    return;
                }
                pixelBuffer.rewind();

                // now that we have the pixels, create test2 new thread for transforming and saving the bitmap
                // so that we don't block the camera preview display renderer
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedOutputStream bitmapStream = null;
                        StringBuffer statusMessage = new StringBuffer();

                        try {
                            File jpgFile = new File(Constants.IMG_FULL_PATH + "snap.jpg");
                            if (jpgFile != null) {
                                bitmapStream = new BufferedOutputStream(new FileOutputStream(jpgFile));
                                Bitmap bmp = Bitmap.createBitmap(bitmapSize.getWidth(), bitmapSize.getHeight(), Bitmap.Config.ARGB_8888);
                                bmp.copyPixelsFromBuffer(pixelBuffer);

                                Matrix xformMatrix = new Matrix();
                                xformMatrix.setScale(-1, 1);  // flip horiz
                                xformMatrix.preRotate(180);  // flip vert
                                bmp = Bitmap.createBitmap(bmp, 0, 0, bitmapSize.getWidth(), bitmapSize.getHeight(), xformMatrix, false);
                                bmp = Bitmap.createScaledBitmap(bmp, Constants.SNAP_IMAGE_WIDTH, Constants.SNAP_IMAGE_HEIGHT, false);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bitmapStream);
                                bmp.recycle();

                                statusMessage.append("Screenshot saved to ").append(jpgFile.getAbsolutePath());
                            } else {
                                statusMessage.append("The directory for the screenshot could not be created");
                            }

                        } catch (Exception e) {
                            WOWZLog.error(TAG, "An exception occurred trying to create the screenshot", e);
                            statusMessage.append(e.getLocalizedMessage());
                        } finally {
                            if (bitmapStream != null) {
                                try {
                                    bitmapStream.close();
                                } catch (IOException closeException) {
                                    // ignore exception on close
                                }
                            }
                            final String toastStr = statusMessage.toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    /*Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
                                    mBtnScreenShot.setEnabled(true);
                                    mBtnScreenShot.setClickable(true);*/
                                    new UploadCameraShotAsync().execute(Constants.IMG_FULL_PATH + "snap.jpg");
                                }
                            });
                            mSavingFrame.set(false);
                        }
                    }
                }).start();
            }
            @Override
            public void onWZVideoFrameListenerRelease(WOWZGLES.EglEnv eglEnv) {

            }
        };
        // register our newly created video frame listener wth the camera preview display view
        mGoCoder.mWOWZCameraView.registerFrameListener(videoFrameListener);
    }

//    public synchronized void startTimer(long refreshInterval) {
//        if (mTimerThread != null) return;
//        onTakeCameraShot();//일단은 먼저 한장서버에 올린후 2분간격으로 타이머가 동작함
//        mTimerThread = Executors.newSingleThreadScheduledExecutor();
//        mTimerThread.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onTakeCameraShot();
//                    }
//                });
//            }
//        }, refreshInterval, refreshInterval, TimeUnit.MILLISECONDS);
//    }

    public synchronized void startTimer(long refreshInterval) {
        if (mTimerThread != null || mIsPreFinish) return;
        if (mIsBJ)
            new UpdateRoomAsync().execute(Constants.STATE_INTERVAL_UPDATE);//일단은 먼저 update한후 2분간격으로 타이머가 동작함
        mTimerThread = Executors.newSingleThreadScheduledExecutor();
        mTimerThread.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (mIsPreFinish)// 이미 액티비티가 꺼진상태라면
                {
                    stopTimer();
                    return;
                }
                if (mIsBJ && mIsConnectedSocket)
                    attemptSend("ping", Constants.ALERT_MSG_NETWORK_CHECK, "everybody");
                else if (!mIsBJ ){
                    mPreviousTime = mPreviousTime == 0 ? System.currentTimeMillis() : mPreviousTime;
                    long elapsedTime = System.currentTimeMillis() - mPreviousTime;
                    if (elapsedTime > Constants.PING_TIME_OUT){//만일 13초동안 방송자에게서 핑이 안오면 방송자가 비정상적으로 탈퇴된 경우로 보고 해당처리를 진행
                        stopTimer();
                        if (mIsConnectedWeb)
                            new SetRoomInfoAsync().execute("watch_off", "", "", "", "", "","");
                        else
                            onPreFinish();
                        return;
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsBJ)
                            new UpdateRoomAsync().execute(Constants.STATE_INTERVAL_UPDATE);
                    }
                });
            }
        }, refreshInterval, refreshInterval, TimeUnit.MILLISECONDS);
    }

    public synchronized void stopTimer() {
        if (mTimerThread == null) return;
        mTimerThread.shutdown();
        mTimerThread = null;
    }

    // upload image to server
    class UploadCameraShotAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... strs) {
            String filename = strs[0];
            return mWebServer.syncUploadScreenShot(filename);
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (mIsPreFinish)
                return;
            if (result != null) {
                mIsConnectedWeb = true;
                if(result.isSuccess()){
                    //Toast.makeText(mContext, result.getResult(), Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(mContext, "img failure!", Toast.LENGTH_LONG).show();
                }
            } else {
                mIsConnectedWeb = false;
            }
            mIsUpload = true;
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            mIsUpload = true;
        }
    }

    private void ResortTopLevel(String username, int nSendBananaCnt){
        int nTopLevelCount = 0;
        FanItemResponse lastItem = null;
        FanItemResponse currentItem = null;
        for (FanItemResponse item: mGuestList) {
            if (item.getFanID().equals(mRoomName))
                continue;
            if (item.getFanID().equals(username)){
                item.setNow_banana_cnt(item.getNow_banana_cnt() + nSendBananaCnt);
                if (item.getTopLevel())//쵸코보낸사람이 이미 toplevel이면 변함이 없다.
                    return;
                currentItem = item;
            }
            if (item.getTopLevel()){
                nTopLevelCount++;
                if (lastItem == null)
                    lastItem = item;
                else if (lastItem.getNow_banana_cnt() >= item.getNow_banana_cnt())
                    lastItem = item;
            }
        }

        if (nTopLevelCount < Constants.TOP_LEVEL_COUNT) {
            if (currentItem != null)
                currentItem.setTopLevel(true);
        }else if (currentItem.getNow_banana_cnt() > lastItem.getNow_banana_cnt()){
            if (currentItem != null)
                currentItem.setTopLevel(true);
            if (nTopLevelCount >= Constants.TOP_LEVEL_COUNT) {
                if (lastItem != null)
                    lastItem.setTopLevel(false);
            }
        }
    }
    public String getFirstImage(String username){
        for ( FanItemResponse item : mGuestList){
            if (item.getFanID().equals(username))
                return item.getFirst();
        }
        return null;
    }
    private Boolean isPossibleToChat(String username){
        for (FanItemResponse item : mGuestList){
            if (item.getFanID().equals(username))
                return item.getTopLevel();
        }
        return false;
    }

    public void onGuestVideoClose(String username){
        //해당한 초청자의 화면을 애니메션을 동반하여 제거한다.
        for (GuestVideo item : mInviteVideoViewList) {
            if (item.mIsInit && item.mUserID.equals(username)) {
                item.onDisappear();
                mNowInviteCount--;
            }
        }
        /*if (mNowInviteCount == 0 && mIsBJ) {
            if (mAudioManager.getMode() != AudioManager.MODE_NORMAL)
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }*/
    }

    // update room state
    class UpdateRoomAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... strs) {
            return mWebServer.syncUpdateRoom(strs[0]);
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (mIsPreFinish)
                return;
            //채팅서버의 상태에 따라 채팅단추가 생겻다 사라졋다 한다.
            if (!mIsConnectedSocket && mBtnChat != null && mBtnChat.getVisibility() == View.VISIBLE)
                mBtnChat.setVisibility(View.GONE);
            else if (mIsConnectedSocket && mBtnChat != null && mBtnChat.getVisibility() == View.GONE){
                mBtnChat.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsConnectedWeb) {
                            new FanListAsync().execute(mRoomName);    
                        }
                        
                    }
                },10);
            }

            if (result != null){
                mIsConnectedWeb = true;
                if(result.isSuccess()) {//방송자인경우에만 여기에 옴
                    if (result.getResult().equals(Constants.STATE_CHAT_LOCK)) {
                        attemptSend(getString(R.string.str_chat_lock_on), Constants.ALERT_MSG_CHAT_LOCK, "everybody");
                        mIsChatLock = true;
                    } else if (result.getResult().equals(Constants.STATE_CHAT_UNLOCK)) {
                        attemptSend(getString(R.string.str_chat_lock_off), Constants.ALERT_MSG_CHAT_UNLOCK, "everybody");
                        mIsChatLock = false;
                    }
                }
            }else{
                //Toast.makeText(LiveVideoShowActivity.this, getString(R.string.operation_failure), Toast.LENGTH_LONG).show();
                mIsConnectedWeb = false;
                if(!mIsBJ && !mIsConnectedSocket)//만일 시청자가 웹과 소켓접속에 실패한경우 망이 불안하므로 방송화면이 중지되여잇을수 있으므로 다시 플레이를 시도해본다.
                {
                    Toast.makeText(mContext, getString(R.string.operation_reloading_failure), Toast.LENGTH_LONG);
                    onGuestVideoReload();
                }
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    // init isAdmin
    class SetRoomAdminAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... strs) {
            return mWebServer.syncSetRoomAdmin(strs[0], strs[1]);
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (mIsPreFinish)
                return;

            if (result != null){
                mIsConnectedWeb = true;
                if(result.isSuccess()) {
                    if (mIsConnectedSocket) {
//                        attemptSend(result.getResult(), Constants.ALERT_MSG_SET_ROOMADMIN, "everybody");
                        SessionInstance.getInstance().getLoginData().getBjData().setIsAdmin(0);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mIsConnectedWeb) {
                                    new FanListAsync().execute(mRoomName);
                                }

                            }
                        },10);
                    }
                }
            }else{
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}