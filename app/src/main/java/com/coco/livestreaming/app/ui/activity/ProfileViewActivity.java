package com.coco.livestreaming.app.ui.activity;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.LoginDataResponse;
import com.coco.livestreaming.app.server.response.LoginResponse;
import com.coco.livestreaming.app.server.response.ProfileInfoResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.view.CircleImageView;
import com.coco.livestreaming.app.ui.view.CustomButton;
import com.coco.livestreaming.app.ui.view.CustomProgressBar;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileViewActivity extends Activity {

	public final String TAG = ProfileViewActivity.class.getName();
    
    ImageButton        btnBack;
	CircleImageView     imgProfile;
	CustomProgressBar   m_spinner;
    SyncInfo            info;
    ProfileInfoResponse mMyProfileInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        btnBack = (ImageButton)findViewById(R.id.imgBtn_Back);
        btnBack.setOnClickListener(mButtonClickListener);
		m_spinner = (CustomProgressBar)findViewById(R.id.image_loading_progress);
        findViewById(R.id.img_profile).setOnClickListener(mButtonClickListener);
        findViewById(R.id.btn_profile_info_id).setOnClickListener(mButtonClickListener);
        findViewById(R.id.img_excharge_id).setOnClickListener(mButtonClickListener);
        findViewById(R.id.img_profile_user_setting_id).setOnClickListener(mButtonClickListener);
        info = new SyncInfo(this);
        String id = SessionInstance.getInstance().getLoginData().getBjData().getUserid();

		new ProfileViewAsync().execute(id);
    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(ProfileViewActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	switch(v.getId()){
        	case R.id.imgBtn_Back:
        		onBackPressed();
        		break;

            case R.id.img_profile:
            case R.id.btn_profile_info_id:
                Intent intent = new Intent(ProfileViewActivity.this, ProfileViewInfoActivity.class);
                String userid = mMyProfileInfo == null ? "" : mMyProfileInfo.getUserid();
                intent.putExtra("userid", userid);
                startActivity(intent);
                finish();
                break;
            case R.id.img_excharge_id:
        		if(SessionInstance.getInstance().getLoginData().getBjData().getChocoCnt() < 1000){
        			Toast.makeText(getBaseContext(), getString(R.string.warning_excharge), Toast.LENGTH_LONG).show();
        			return;
        		}
        		
        		Intent intent3 = new Intent(ProfileViewActivity.this, ExchargeViewActivity.class);
                intent3.putExtra("username", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
                intent3.putExtra("value", SessionInstance.getInstance().getLoginData().getBjData().getChocoCnt());
        		startActivity(intent3);
        		finish();
        		break;
             case R.id.img_profile_user_setting_id:
                 Intent intent4 = new Intent(ProfileViewActivity.this, ProfileSettingActivity.class);
                 String userid4 = mMyProfileInfo == null ? "" : mMyProfileInfo.getUserid();
                 intent4.putExtra("userid", userid4);
                 startActivity(intent4);
                 finish();
                 break;

        	}
        }
    };
    
    class ProfileViewAsync extends AsyncTask<String, String, ProfileViewResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(ProfileViewActivity.this);
                ((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
            }
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
				Picasso.with(ProfileViewActivity.this)
							.load(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getFirst())
							.placeholder(R.drawable.no_image)
							.into((ImageView)findViewById(R.id.img_profile));

                ((TextView)findViewById(R.id.txt_profile_username_id)).setText(String.valueOf(mMyProfileInfo.getUsername()));
                ((TextView)findViewById(R.id.txt_profile_userid_id)).setText(String.valueOf(mMyProfileInfo.getNickname()));
                ((TextView)findViewById(R.id.txt_level_id)).setText(getString(R.string.str_level_prifix, mMyProfileInfo.getLevel_num()));

                ((TextView)findViewById(R.id.txt_friend_num_id)).setText(String.valueOf(mMyProfileInfo.getFriend_cnt()));
                ((TextView)findViewById(R.id.txt_follow_num_id)).setText(String.valueOf(mMyProfileInfo.getFollow_cnt()));
                ((TextView)findViewById(R.id.txt_fan_num_id)).setText(String.valueOf(mMyProfileInfo.getFan_cnt()));

                ((TextView)findViewById(R.id.profile_user_import_id)).setText(String.valueOf(mMyProfileInfo.getPriceperchoco() * (mMyProfileInfo.getReceive_choco_cnt() - mMyProfileInfo.getSend_choco_cnt())));
                ((TextView)findViewById(R.id.profile_user_banana_id)).setText(String.valueOf(mMyProfileInfo.getChococnt()));
                SessionInstance.getInstance().getLoginData().getBjData().setChocoCnt(mMyProfileInfo.getChococnt());
                ((TextView)findViewById(R.id.profile_user_level_id)).setText(getString(R.string.str_level_prifix, mMyProfileInfo.getLevel_num()));
                if (mMyProfileInfo.getRank_recommend() > 100)
                    ((TextView)findViewById(R.id.profile_user_rank_id)).setText(getString(R.string.str_ranking_top_100));
                else
                    ((TextView)findViewById(R.id.profile_user_rank_id)).setText(getString(R.string.str_ranking_prifix, mMyProfileInfo.getRank_recommend()));
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

  
}
