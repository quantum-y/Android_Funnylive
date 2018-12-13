package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.FanItemResponse;
import com.coco.livestreaming.app.server.response.LoginDataResponse;
import com.coco.livestreaming.app.server.response.LoginResponse;
import com.coco.livestreaming.app.server.response.ProfileInfoResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.fragment.AgreementAndPrivacyFragment;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.squareup.picasso.Picasso;

public class ProfileSettingActivity extends FragmentActivity {

    public final String TAG = ProfileSettingActivity.class.getName();
    private ProfileInfoResponse mMyProfileInfo;
    private SyncInfo info;
    private ToggleButton mSwitchAlarm;
    private ToggleButton mSwitchProtectLocation;
    public View          mAgreementFrame;
    public String mUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_setting);
        mSwitchAlarm            = (ToggleButton)findViewById(R.id.switch_alarm_id);
        mSwitchProtectLocation  = (ToggleButton)findViewById(R.id.switch_protect_location_id);
        mAgreementFrame         = (View)findViewById(R.id.lay_login_agreement_id);
        info = new SyncInfo(this);

        mUserid = "";
        Intent data = getIntent();
        if (data != null) {
            mUserid = data.getStringExtra("userid");
            new ProfileViewAsync().execute(data.getStringExtra("userid"));
        }
        findViewById(R.id.img_logout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionInstance.clearInstance();
                ((CocotvingApplication)getApplication()).getGPSTracker().stopUsingGPS();
                Toast.makeText(ProfileSettingActivity.this, getString(R.string.logout_bj_success), Toast.LENGTH_LONG).show();

                new LogoutAsync().execute(mUserid);
            }
        });
        findViewById(R.id.img_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        mSwitchAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSwitchAlarm.isChecked())
//                    mSwitchAlarm.setChecked(true);
//                else
//                    mSwitchAlarm.setChecked(false);
//            }
//        });
//        mSwitchProtectLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSwitchProtectLocation.isChecked())
//                    mSwitchProtectLocation.setChecked(true);
//                else
//                    mSwitchProtectLocation.setChecked(false);
//            }
//        });
        findViewById(R.id.img_login_agreement_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgreementAndPrivacyFragment apFragment = new AgreementAndPrivacyFragment();
                mAgreementFrame.setVisibility(View.VISIBLE);
                apFragment.mProfileView = mAgreementFrame;
                Bundle data = new Bundle();
                data.putInt(Constants.APFLAG, Constants.AGREEMENT);
                apFragment.setArguments(data);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .addToBackStack(TAG)
                        .replace(R.id.lay_login_agreement_id, apFragment)
                        .commit();

            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        if (SessionInstance.getInstance() != null)//만일 로그아웃이라면
            new ProfileSetAsync().execute(mSwitchAlarm.isChecked() ? 1 : 0, mSwitchProtectLocation.isChecked() ? 1 : 0);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(ProfileSettingActivity.this, ProfileViewActivity.class);
        String userid = mMyProfileInfo == null ? "" : mMyProfileInfo.getUserid();
        intent.putExtra("userid", userid);
        startActivity(intent);
        finish();
    }
    class ProfileViewAsync extends AsyncTask<String, String, ProfileViewResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayProgressDialog(ProfileSettingActivity.this);
        }

        @Override
        protected ProfileViewResponse doInBackground(String... args) {
            ProfileViewResponse result = info.syncProfileView(args[0]);
            return result;
        }

        @Override
        protected void onPostExecute(ProfileViewResponse result) {
            super.onPostExecute(result);
            if (result != null) {

                mMyProfileInfo = result.getProfileInfo();

                if (mMyProfileInfo.getAlarm() == 0 )
                    mSwitchAlarm.setChecked(false);
                else
                    mSwitchAlarm.setChecked(true);

                if (mMyProfileInfo.getProtect_location() == 0 ) {
                    mSwitchProtectLocation.setChecked(false);
                    ((CocotvingApplication)getApplication()).getGPSTracker().getLocation();
                }
                else {
                    mSwitchProtectLocation.setChecked(true);
                    ((CocotvingApplication)getApplication()).getGPSTracker().stopUsingGPS();
                }
            }
            Utils.disappearProgressDialog();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    class ProfileSetAsync extends AsyncTask<Integer, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Utils.displayProgressDialog(ProfileSettingActivity.this);
        }
        @Override
        protected SuccessFailureResponse doInBackground(Integer... args) {
            SuccessFailureResponse result = info.syncProfileAlarmSet(args[0],args[1]);
            return result;
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null)
            {
                SessionInstance.getInstance().getLoginData().getBjData().setAlarm(mSwitchAlarm.isChecked() ? 1 : 0);
                SessionInstance.getInstance().getLoginData().getBjData().setProtect_location(mSwitchProtectLocation.isChecked() ? 1 : 0);
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
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

            startActivity(new Intent(ProfileSettingActivity.this, LoginActivity.class));
            finish();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Utils.disappearProgressDialog();
        }
    }

}
