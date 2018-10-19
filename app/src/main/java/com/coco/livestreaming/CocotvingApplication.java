package com.coco.livestreaming;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

import com.coco.livestreaming.app.network.NetworkEngine;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.GPSTracker;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class CocotvingApplication extends Application {
    private static Context mContext;
    private static Socket mSocket;
    private static GPSTracker mGPSTracker;
    public static boolean mIsVisibleFlag;
    public static boolean mIsEmulator;
    public static boolean mIsInvalidExitFlag;
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        //keep session alive on server side.
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mGPSTracker = new GPSTracker(mContext);
        mIsVisibleFlag = true;
        mIsEmulator = false;
        mIsInvalidExitFlag = false;
        NetworkEngine.mContentResolver = getContentResolver();
        super.onCreate();
    }
    @Override
    public void onTerminate() {
        ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setMode(AudioManager.MODE_NORMAL);
        Toast.makeText(mContext, "Application Terminate", Toast.LENGTH_LONG).show();
    }
    public static Context getContext() {
        return mContext;
    }
    public Socket getSocket() {
        return mSocket;
    }
    public GPSTracker getGPSTracker(){return  mGPSTracker;}
}
