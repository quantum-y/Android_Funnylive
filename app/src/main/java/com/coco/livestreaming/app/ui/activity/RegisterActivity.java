package com.coco.livestreaming.app.ui.activity;

import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.text.style.CharacterStyle;
import android.text.style.LocaleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;

import java.util.Locale;

import javax.net.ssl.KeyManager;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText mUserName;
    private EditText mUserID;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mNickName;
    private EditText mPhoneNumber;
    private EditText mEmailAddress;
    private EditText mPeopleNo;
    private SyncInfo mWebServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserName = (EditText)findViewById(R.id.edit_user_name_id);
        mUserID = (EditText)findViewById(R.id.edit_user_id_id);
        mPassword = (EditText)findViewById(R.id.edit_password);
        mPasswordConfirm = (EditText)findViewById(R.id.edit_password_repeat);
        mNickName = (EditText)findViewById(R.id.edit_user_nickname_id);
        mPhoneNumber = (EditText)findViewById(R.id.edit_user_phone_id);
        mEmailAddress = (EditText)findViewById(R.id.edit_user_email_id);
        mPeopleNo = (EditText)findViewById(R.id.edit_user_people_no_id);
        mWebServer = new SyncInfo(RegisterActivity.this);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_register_ok_id).setOnClickListener(this);

        mUserID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String strUserID = ((EditText)v).getText().toString();
                if (strUserID == null || strUserID == "")
                    return;
                CharSequence chUserID = strUserID.subSequence(0, strUserID.length());
                for (int i = 0; i < strUserID.length(); i++)
                    if (chUserID.charAt(i) > 10000){//유저아이디에 유니코드가 섞여있으면
                        Toast.makeText(RegisterActivity.this, R.string.str_profile_edit_id_hint, Toast.LENGTH_LONG).show();
                        ((EditText)v).setText("");
                        break;
                    }
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String strPasswd = ((EditText)v).getText().toString();
                if (strPasswd == null || strPasswd == "")
                    return;
                if (strPasswd.length() < 8){
                    Toast.makeText(RegisterActivity.this, R.string.str_profile_edit_password_hint, Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_register_ok_id:
                onSave();
                break;
            default:
                return;
        }
    }

    private void onSave(){
        if (mUserName.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_username), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mUserID.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_user_id), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPassword.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_pw), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mPassword.getText().toString().isEmpty() && !mPasswordConfirm.getText().toString().equals(mPassword.getText().toString())){
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_pw_confirm), Toast.LENGTH_LONG).show();
            return;
        }
        if (mPassword.getText().toString().length() < 8){
            Toast.makeText(RegisterActivity.this, R.string.str_profile_edit_password_hint, Toast.LENGTH_LONG).show();
            return;
        }

        if (mNickName.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_nickname), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_phonenumber), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEmailAddress.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.warning_email), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("yyyyyyyyyyyyy", "sdfafadsfasdf");

        new UserRegisterAsync().execute(mUserName.getText().toString().trim(), mUserID.getText().toString().trim(), mPeopleNo.getText().toString().trim(), mPassword.getText().toString().trim(), mNickName.getText().toString().trim(), mPhoneNumber.getText().toString().trim(), mEmailAddress.getText().toString().trim());
    }

    class UserRegisterAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication) getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(RegisterActivity.this);
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
                if (result.getResult().toString().equals("1")){
                    Toast.makeText(RegisterActivity.this, R.string.warning_same_user_id, Toast.LENGTH_SHORT).show();
                }else if (result.getResult().toString().equals("2"))
                    Toast.makeText(RegisterActivity.this, R.string.warning_same_nickname, Toast.LENGTH_SHORT).show();
                else if (!result.getResult().toString().equals("0")) {
                    Toast.makeText(RegisterActivity.this, R.string.register_sucess, Toast.LENGTH_LONG).show();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 50);
                }
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
        }
    }
}
