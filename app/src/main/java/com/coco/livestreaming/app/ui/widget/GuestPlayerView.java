package com.coco.livestreaming.app.ui.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.TimedMetaData;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.debug.Logger;
import com.coco.livestreaming.app.util.Constants;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * TODO: document your custom view class.
 */
public class GuestPlayerView extends SurfaceView implements
        SurfaceHolder.Callback,
        IVLCVout.Callback
{

    private Context mContext;
    public SurfaceHolder mHolder;
    private String mStreamPath;
    private GuestVideo parent;
    private int mLimit;
    private int mTime;

    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // yyyyyy  vlc customize 18.11.09
        setSize(mVideoWidth, mVideoHeight);
    }

    public GuestPlayerView(Context context) {
        super(context);
        mContext = context;
    }

    public GuestPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GuestPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void init(GuestVideo parent, String path) {
        this.parent = parent;
        mStreamPath = path;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.RGBA_8888);
    }

    /**
     * Creates MediaPlayer and plays video
     *
     * @param media
     */
    private void createPlayer(String media) {
        releasePlayer();
        try {
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
//            options.add("--rtsp-tcp");
//            options.add("--network-caching=3000");

            libvlc = new LibVLC(options);
            mHolder.setKeepScreenOn(true);

            // Creating media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mPlayerListener = new MyPlayerListener(this);
            mMediaPlayer.setEventListener(mPlayerListener);

            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(this);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();

            Media m = new Media(libvlc, Uri.parse(media));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();

//            parent.showProgressView();
        } catch (Exception e) {

        }
    }

    private void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();

        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();

        mHolder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    @Override
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onSurfacesCreated(IVLCVout ivlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout ivlcVout) {
    }

    /**
     * Used to set size for SurfaceView
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
        if (getWidth() < getHeight()) {
            lp.width = getWidth();
            lp.height = lp.width * height / width;
        }else{
            lp.height = getHeight();
            lp.width = lp.height*width/height;
        }
        mHolder.setFixedSize(width, height);
        setLayoutParams(lp);
        invalidate();
    }

    /**
     * Registering callbacks
     */
    private MediaPlayer.EventListener mPlayerListener = null;

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        createPlayer(mStreamPath);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //Toast.makeText(mContext, "surfaceDestroyed", Toast.LENGTH_SHORT).show();
        if (mHolder != null) {
            mHolder.removeCallback(this);
        }
        if (mMediaPlayer != null) {
            releasePlayer();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (parent.mActivity.mMediaPlayerLoadingProgress != null && parent.mActivity.mMediaPlayerLoadingProgress.getVisibility() == VISIBLE)
            parent.mActivity.mMediaPlayerLoadingProgress.setVisibility(INVISIBLE);

    }


    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<GuestPlayerView> mOwner;
        public boolean mbPlay = false;

        public MyPlayerListener(GuestPlayerView owner) {
            mOwner = new WeakReference<GuestPlayerView>(owner);
            mbPlay = false;
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            final GuestPlayerView playerView = mOwner.get();
            switch (event.type) {
                case MediaPlayer.Event.Opening:
                    playerView.parent.showProgressView();
                    break;
                case MediaPlayer.Event.Playing:
                    mbPlay = true;
                    break;
                case MediaPlayer.Event.Vout:
                    if (mbPlay)
                    {
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                playerView.parent.mActivity.createSocketIO();
////                                playerView.parent.mActivity.startTimer(Constants.DEFAULT_SCREENSHOT_INTERVAL);
//                            }
//                        },10000);

                        playerView.parent.mActivity.onGuestVideoView("watch_on", playerView.parent.mUserID);
                        playerView.parent.hideProgressView();
                    }
                    break;
                case MediaPlayer.Event.EndReached:
                    playerView.releasePlayer();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    if (!playerView.parent.mActivity.mIsBJ && playerView.parent.mActivity.mRoomName.equals(playerView.parent.mUserID))//시청자이면서 현재 스트림이 방송자의 것이라면 오프라인된 방에 들어온 경우로 됨
                    {
                        Toast.makeText(playerView.mContext, playerView.mContext.getString(R.string.str_offline_info1), Toast.LENGTH_SHORT).show();
                        playerView.parent.mActivity.mMediaPlayerLoadingProgress.setVisibility(INVISIBLE);
                        playerView.parent.mActivity.onGuestVideoView("watch_off", playerView.parent.mUserID);
                    }else if (!playerView.parent.mActivity.mIsBJ && !playerView.parent.mActivity.mRoomName.equals(playerView.parent.mUserID)){//시청자이면서 현재 스트림이 초청자의 것이라면 초청자카메라를 중지한다.
                        Toast.makeText(playerView.mContext, "초청자의 화면을 현시할수 없습니다.", Toast.LENGTH_SHORT).show();
                        playerView.parent.mActivity.onGuestVideoClose(playerView.parent.mUserID);
                    }
                    else //방송자이면 초청화면이 제대로 play되지 않는 경우이거나 //시청자이면서 현재 스트림이 초청자의것인경우
                    {
                        Toast.makeText(playerView.mContext, playerView.mContext.getString(R.string.str_invite_error_info), Toast.LENGTH_SHORT).show();
                        playerView.parent.mActivity.mMediaPlayerLoadingProgress.setVisibility(INVISIBLE);
                        playerView.parent.onLongClickInviteClose();
                    }
                    break;
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                case MediaPlayer.Event.TimeChanged:
                case MediaPlayer.Event.PositionChanged:
                case MediaPlayer.Event.SeekableChanged:
                case MediaPlayer.Event.PausableChanged:
                case MediaPlayer.Event.ESAdded:
                case MediaPlayer.Event.ESDeleted:
                default:
                    break;
            }

            Logger.e("GuestPlayerView:", "EventTypee:" + event.type);
        }
    }
}

