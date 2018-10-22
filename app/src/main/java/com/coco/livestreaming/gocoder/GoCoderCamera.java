package com.coco.livestreaming.gocoder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.NoiseSuppressor;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GestureDetectorCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.gocoder.ui.AutoFocusListener;
import com.coco.livestreaming.gocoder.ui.StatusView;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.configuration.WowzaConfig;
import com.wowza.gocoder.sdk.api.data.WOWZDataMap;
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WOWZCamera;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.encoder.WOWZEncoderAPI;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.geometry.WOWZSize;
import com.wowza.gocoder.sdk.api.h264.WOWZProfileLevel;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.util.Arrays;

/**
 * Created by redstar on 3/11/2017.
 */

public class GoCoderCamera implements
        //WOWZCameraView.PreviewStatusListener,
        WOWZStatusCallback ,
        View.OnClickListener,
        View.OnLongClickListener{

    private final static String TAG = GoCoderCamera.class.getSimpleName();

    private static final String SDK_GOCODER_APP_LICENSE_KEY = "GOSK-5442-0101-750D-4A14-FB5C";
    public WOWZBroadcast          mWOWZBroadcast = null;
    public WOWZAudioDevice        mWOWZAudioDevice = null;
    public WowzaGoCoder         mGoCoderSDK = null;
    public WOWZBroadcastConfig    mGoCoderConfig = null;
    public WOWZCameraView        mWOWZCameraView = null;
    private LiveVideoShowActivity mActivity;
    public GestureDetectorCompat mAutoFocusDetector = null;
    private int mPrevStatusCode = -1;

    public GoCoderCamera(LiveVideoShowActivity broadcast, WOWZCameraView cameraView, int nQuality, String streamname){
        mActivity = broadcast;
        mWOWZCameraView = cameraView;
        if (mGoCoderSDK == null) {
            // Enable detailed logging from the GoCoder SDK
            WOWZLog.LOGGING_ENABLED = true;
            // Initialize the GoCoder SDK
            mGoCoderSDK = WowzaGoCoder.init(mActivity.mContext, SDK_GOCODER_APP_LICENSE_KEY);
            if (mGoCoderSDK == null) {
                WOWZLog.error(TAG, WowzaGoCoder.getLastError());
            }
        }
        if (mGoCoderSDK != null) {
            // Create test2 GoCoder broadcaster and an associated broadcast configuration
            mWOWZBroadcast = new WOWZBroadcast();
            mGoCoderConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_640x480);
            mGoCoderConfig.setLogLevel(WOWZLog.LOG_LEVEL_DEBUG);
            setWowzaConfig(nQuality, streamname);
        }
        if (mWOWZCameraView != null){
            //mWOWZCameraView.setPreviewReadyListener(this);
            mWOWZCameraView.setOnClickListener(this);
            mWOWZCameraView.setOnLongClickListener(this);
            mWOWZCameraView.setScaleMode(WOWZMediaConfig.FILL_VIEW);
            mGoCoderConfig.setVideoBroadcaster(mWOWZCameraView);
            mWOWZCameraView.setCameraConfig(mGoCoderConfig);
        }
        if (mAutoFocusDetector == null)
            mAutoFocusDetector = new GestureDetectorCompat(mActivity.mContext, new AutoFocusListener(mActivity.mContext, mWOWZCameraView));
        if (mWOWZAudioDevice == null){
            //if (!mActivity.mIsBJ)
            //    mActivity.mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            //else
            //    mActivity.mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mActivity.mAudioManager.setSpeakerphoneOn(true);

            mWOWZAudioDevice = new WOWZAudioDevice();
            //mWOWZAudioDevice.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            mGoCoderConfig.setAudioBroadcaster(mWOWZAudioDevice);
            //mWOWZAudioDevice.setSamplingConfig(mGoCoderConfig);
        }
    }
    public synchronized WOWZStreamingError startBroadcast() {
        WOWZStreamingError configValidationError = null;
        if (mWOWZBroadcast.getStatus().isIdle()) {
            WOWZLog.info(TAG, mGoCoderConfig.toString());
            configValidationError = mGoCoderConfig.validateForBroadcast();
            if (configValidationError == null)
                mWOWZBroadcast.startBroadcast(mGoCoderConfig, this);
        } else {
            Toast.makeText(mActivity.mContext, "startBroadcast() called while another broadcast is active", Toast.LENGTH_LONG).show();
        }
        return configValidationError;
    }
    //bFlag true: 완전히 끄려할때
    //false: AEC때문에 림시 끄려할때
    public  synchronized void endBroadcast(){
        if (mWOWZBroadcast != null && mWOWZBroadcast.getStatus().isRunning()) {
            mWOWZBroadcast.endBroadcast();
        }
    }
    @Override
    public void onWZStatus(final WOWZStatus WOWZStatus) {

        if (mPrevStatusCode == WOWZStatus.getState())//같은상태가 반복적으로 들어오는 경우배제.
            return;
        mPrevStatusCode = WOWZStatus.getState();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //if (WOWZStatus.isIdle() )
                //    startBroadcast();
                mActivity.onGoCoderWOWZStatus(WOWZStatus);
            }
        });
    }
    @Override
    public void onWZError(final WOWZStatus WOWZStatus) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
            if (WOWZStatus.isStopping() && WOWZStatus.getLastError().getErrorCode() == 55) {//비정상종료되여 와우자에 현스트림에 관한 정보가 아직도 남아있는 경우
                CocotvingApplication.mIsInvalidExitFlag = true;
                return;
            }
            mActivity.onGoCoderWZError(WOWZStatus);
            }
        });
    }
    /*@Override
    public void onWZCameraPreviewStarted(WZCamera wzCamera, WOWZSize WOWZSize, int i) {
        *//*if (mWOWZBroadcast == null) return;
        if (mWOWZBroadcast.getStatus().isIdle() && !mIsSwitchCamera) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    startBroadcast();
                }
            });
        }
        mIsSwitchCamera = false;*//*
  }
    @Override
    public void onWZCameraPreviewStopped(int cameraId) {
        *//*if (mWOWZBroadcast == null) return;
        if (!mWOWZBroadcast.getStatus().isIdle() && !mIsSwitchCamera) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWOWZBroadcast.endBroadcast();//endBroadcast();
                }
            }, 10);
        }*//*
    }
    @Override
    public void onWZCameraPreviewError(WZCamera wzCamera, WZError wzError) {
        *//*if (mWOWZBroadcast == null) return;
        if (!mWOWZBroadcast.getStatus().isIdle())
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWOWZBroadcast.endBroadcast();//endBroadcast();
                }
            }, 10);*//*
    }*/
    @Override
    public void onClick(View v) {
        if (!mActivity.mIsBJ)//시청자이면 초청카메라를 터치한경우로 됨
            Toast.makeText(mActivity.mContext, mActivity.getString(R.string.str_guest_camera_off), Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onLongClick(View v) {//시청자가 초청카메라를 끄는경우
        if (!mActivity.mIsBJ) {
            mActivity.mIsSelfCloseCamera = true;
            onStop(false);
        }
        return true;
    }
    public void onInit() {
        //변수초기화
        mPrevStatusCode = -1;
        mActivity.mIsSelfCloseCamera = true;

        if (mGoCoderSDK == null || mWOWZCameraView == null || mWOWZCameraView.getCameras().length == 0 ) {
            Toast.makeText(mActivity.mContext, mActivity.getString(R.string.str_camera_error), Toast.LENGTH_LONG).show();
            return;
        }
        mWOWZCameraView.setVisibility(View.VISIBLE);
        WOWZCamera activeCamera = mWOWZCameraView.getCamera();
        if (activeCamera != null && activeCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
            activeCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);

        if (!mWOWZCameraView.isPreviewing())
            mWOWZCameraView.startPreview();

        if (mWOWZAudioDevice != null)
            mWOWZAudioDevice.startAudioSampler();
        /*if (mWOWZAudioDevice == null){
            ((AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE)).setMode(AudioManager.MODE_IN_COMMUNICATION);
            ((AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE)).setSpeakerphoneOn(true);
            mWOWZAudioDevice = new WOWZAudioDevice();
            mWOWZAudioDevice.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            mGoCoderConfig.setAudioBroadcaster(mWOWZAudioDevice);
            mWOWZAudioDevice.setSamplingConfig(mGoCoderConfig);
        }*/

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if( mWOWZBroadcast != null && mWOWZBroadcast.getStatus().isIdle())
                    startBroadcast();
                else
                    endBroadcast();
            }
        },10);
    }
    //isFinish : 완전히 끄려면 true, 잠시 끄려면 false
    //이 앱에서는 false일때가 없으므로 항상 true로 동작하게 하였음
    public void onStop(boolean isFinish){

        if (mWOWZCameraView != null) {
            mWOWZCameraView.onPause();
            mWOWZCameraView.setVisibility(View.GONE);
        }
        if (mWOWZAudioDevice != null)
            mWOWZAudioDevice.stopAudioSampler();
        /*if (mWOWZAudioDevice != null && !mWOWZAudioDevice.isAudioPaused()) {
            mWOWZAudioDevice.setAudioPaused(true);
            mWOWZAudioDevice = null;
        }*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                endBroadcast();
            }
        }).start();

        if (!mActivity.mIsBJ) {//초청카메라가 꺼지는 경우
            TranslateAnimation transEndAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            mActivity.findViewById(R.id.lay_guest_camera_id).startAnimation(transEndAni);
            mActivity.findViewById(R.id.lay_guest_camera_id).setVisibility(View.GONE);
            transEndAni.setDuration(2000);
        }
    }
    public void switchCamera() {
        if (mWOWZCameraView == null) return;
        //mBtnTorch.setState(false);
        //mBtnTorch.setEnabled(false);
        WOWZCamera newCamera = mWOWZCameraView.switchCamera();
        if (newCamera != null) {
            if (newCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);
            /*boolean hasTorch = newCamera.hasCapability(WZCamera.TORCH);
            if (hasTorch) {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }*/
        }
    }
    private void setWowzaConfig(int nQuality, String streamname){
        WOWZMediaConfig selectedConfig = null;
        if (mWOWZCameraView != null && mWOWZCameraView.getCamera() != null) {
            WOWZMediaConfig cameraConfigs[] = mWOWZCameraView.getCamera().getSupportedConfigs();
            if (cameraConfigs == null)
                return;
            Arrays.sort(cameraConfigs);

            // change camera config to lowest ones
            //            if (nQuality == Constants.VIDEO_QUALITY_HIGH)
//                selectedConfig = cameraConfigs[cameraConfigs.length - 1];
//            else if (nQuality == Constants.VIDEO_QUALITY_LOW)
//                selectedConfig = cameraConfigs[0];
//            else if (nQuality == Constants.VIDEO_QUALITY_STANDARD)
//                selectedConfig = cameraConfigs[cameraConfigs.length / 2];
            if (nQuality == Constants.VIDEO_QUALITY_HIGH)
                selectedConfig = cameraConfigs[cameraConfigs.length>1?2:cameraConfigs.length>0?1:0];
            else if (nQuality == Constants.VIDEO_QUALITY_LOW)
                selectedConfig = cameraConfigs[0];
            else if (nQuality == Constants.VIDEO_QUALITY_STANDARD)
                selectedConfig = cameraConfigs[cameraConfigs.length>0?1:0];


            selectedConfig.setAudioChannels(WOWZMediaConfig.AUDIO_CHANNELS_MONO);
            selectedConfig.setAudioBitRate(64000);
            selectedConfig.setAudioSampleRate(16000);
            mGoCoderConfig.setHostAddress(Constants.WZ_LIVE_HOST_ADDRESS);
            mGoCoderConfig.setPortNumber(WowzaConfig.DEFAULT_PORT);
//            mGoCoderConfig.setPortNumber(554);
            mGoCoderConfig.setApplicationName(Constants.WZ_LIVE_APP_NAME);
            mGoCoderConfig.setStreamName(streamname);
            mGoCoderConfig.setUsername(null);
            mGoCoderConfig.setPassword(null);
            mGoCoderConfig.setABREnabled(true);
            mGoCoderConfig.set(selectedConfig);

            WOWZDataMap map = mGoCoderConfig.getConnectionParameters();
//            map.keys()


        }
    }
}
