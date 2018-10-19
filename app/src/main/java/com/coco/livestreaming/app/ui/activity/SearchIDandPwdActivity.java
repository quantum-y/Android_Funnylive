package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.util.Utils;

public class SearchIDandPwdActivity extends Activity implements View.OnClickListener {

    private EditText mUserName;
    private EditText mUserID;
    private EditText mPeopleNo;
    private SyncInfo mWebServer;
    private Button btnOK, btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_idpwd);

        mUserName = (EditText)findViewById(R.id.edit_user_name_id);
        mUserID = (EditText)findViewById(R.id.edit_user_id_id);
        mPeopleNo = (EditText)findViewById(R.id.edit_user_people_no_id);
        mWebServer = new SyncInfo(SearchIDandPwdActivity.this);

        btnBack = (Button)findViewById(R.id.btn_back);
        btnOK = (Button)findViewById(R.id.btn_ok);
        btnBack.setOnClickListener(this);
        btnOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                onSave();
                break;
            default:
                return;
        }
    }

    private void onSave(){
        if (mUserName.getText().toString().isEmpty() && mUserID.getText().toString().isEmpty()) {
            Toast.makeText(SearchIDandPwdActivity.this, getString(R.string.warning_name_id), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPeopleNo.getText().toString().isEmpty()) {
            Toast.makeText(SearchIDandPwdActivity.this, getString(R.string.warning_people_no), Toast.LENGTH_SHORT).show();
            return;
        }
        new UserRegisterAsync().execute(mUserName.getText().toString().trim(), mUserID.getText().toString().trim(), mPeopleNo.getText().toString().trim(), "", "", "", "");
    }

    class UserRegisterAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication) getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(SearchIDandPwdActivity.this);
                ((CocotvingApplication) getApplication()).mIsVisibleFlag = false;
            }
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... strs) {
            return mWebServer.syncUserRegister(strs[0], strs[1], strs[2], strs[3], strs[4], strs[5], strs[6]);
        }

        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
                if (result.getResult().toString().equals("4")){
                    Toast.makeText(SearchIDandPwdActivity.this, R.string.str_register_idpwd_success_result, Toast.LENGTH_LONG).show();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 50);
                }else
                    Toast.makeText(SearchIDandPwdActivity.this, R.string.idpwd_search_error, Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(SearchIDandPwdActivity.this, R.string.operation_failure, Toast.LENGTH_LONG).show();
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
        }
    }
}
