package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.LoginDataResponse;
import com.coco.livestreaming.app.server.response.LoginResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hipmob.gifanimationdrawable.GifAnimationDrawable;
import com.wowza.gocoder.sdk.api.encoder.WOWZEncoderAPI;
import com.wowza.gocoder.sdk.api.h264.WOWZProfileLevel;

public class SplashActivity extends Activity {

    SyncInfo sync;
    ImageView img_gif;
    GifAnimationDrawable gifDrawable;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WOWZProfileLevel avcProfileLevels[] = WOWZEncoderAPI.getProfileLevels();
        if (avcProfileLevels.length == 0)//if is running on Emulator
        {
//            LibsChecker.checkVitamioLibs(this);
            CocotvingApplication.mIsEmulator = true;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Constants.SCREEN_HEIGHT = metrics.heightPixels;
        Constants.SCREEN_WIDTH = metrics.widthPixels;

        setContentView(R.layout.activity_splash);
        img_gif = (ImageView) findViewById(R.id.img_gif);
        try {
            gifDrawable = new GifAnimationDrawable(getResources().openRawResource(R.raw.splash)) {
                public void onGotTotalTime ( final int totalTime) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(totalTime * 3);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toMainActivity();
                                    }
                                });
                            } catch (InterruptedException e) {
                            }
                        }
                    }).start();
                }
            } ;
            gifDrawable.setOneShot(false);
            img_gif.setImageDrawable(gifDrawable);
            gifDrawable.setVisible(true, true);
            img_gif.post(new Runnable() {
                @Override
                public void run() {
                    gifDrawable.start();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        File directory = new File(Constants.IMG_FULL_PATH);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Toast.makeText(this, R.string.no_external_storage, Toast.LENGTH_LONG).show();
            }
        }
        boolean flag = true;
        if (SessionInstance.getInstance() != null) {
            LoginResponse loginData = SessionInstance.getInstance().getLoginData().getBjData();
            if (loginData != null) {
                new LoginAsync().execute(loginData.getUserid(), loginData.getPassword());
                flag = false;
            }
        }
        if (flag) new LoginAsync().execute("xx", "xx");
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void toMainActivity() {
        Intent intent;
        SessionInstance instance = SessionInstance.getInstance();
        if (instance != null && instance.getLoginData().getBjData() != null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Splash Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class LoginAsync extends AsyncTask<String, String, LoginDataResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected LoginDataResponse doInBackground(String... strs) {
            String id = strs[0];
            String pw = strs[1];
            sync = new SyncInfo(SplashActivity.this.getBaseContext());
            return sync.syncLogin(id, pw, "");
        }

        @Override
        protected void onPostExecute(LoginDataResponse result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.getBjData() != null) {
                    //Toast.makeText(getBaseContext(), getString(R.string.login_bj_success), Toast.LENGTH_LONG).show();
                }
            }
            SessionInstance.initialize(SplashActivity.this, result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
