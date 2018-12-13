package com.coco.livestreaming;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.AsyncTask;
import android.util.Log;

import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;

public class CocotvingLifecycleListener implements LifecycleObserver {

    private SyncInfo info;

    public CocotvingLifecycleListener(SyncInfo info) {
        this.info = info;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public final void onMoveToForeground() {
        Log.d("SampleLifecycle", "Returning to foreground…");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public final void onMoveToBackground() {
        new LogoutAsync().execute("");
    }

    class LogoutAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... strs) {
            return info.syncLogout(strs[0]);
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
