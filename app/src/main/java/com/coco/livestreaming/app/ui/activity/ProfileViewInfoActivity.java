package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
//import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.FanItemResponse;
import com.coco.livestreaming.app.server.response.ProfileInfoResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.view.CircleImageView;
import com.coco.livestreaming.app.ui.view.CustomButton;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ProfileViewInfoActivity extends Activity {

    private SyncInfo info;
    private ProfileInfoResponse mMyProfileInfo;
    private List<FanItemResponse> mMyTopFanList;
    private List<CircleImageView> mTopFanList = new ArrayList<CircleImageView>();
    private int[]                 mDefaultNoImageArr = new int[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_info);

        info = new SyncInfo(this);
        Intent data = getIntent();
        if (data != null){
            new ProfileViewAsync().execute(data.getStringExtra("userid"));
        }
        findViewById(R.id.img_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_profile_edit_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileViewInfoActivity.this, ProfileViewInfoEditActivity.class);
                String userid = mMyProfileInfo == null ? "" : mMyProfileInfo.getUserid();
                intent.putExtra("userid", userid);
                startActivity(intent);
                finish();

            }
        });
        mTopFanList.add((CircleImageView)findViewById(R.id.img_fan_first));
        mTopFanList.add((CircleImageView)findViewById(R.id.img_fan_second));
        mTopFanList.add((CircleImageView)findViewById(R.id.img_fan_third));

        mDefaultNoImageArr[0] = R.drawable.no_image;
        mDefaultNoImageArr[1] = R.drawable.no_image;
        mDefaultNoImageArr[2] = R.drawable.no_image;
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(ProfileViewInfoActivity.this, ProfileViewActivity.class);
        String userid = mMyProfileInfo == null ? "" : mMyProfileInfo.getUserid();
        intent.putExtra("userid", userid);
        startActivity(intent);
        finish();
    }
    class ProfileViewAsync extends AsyncTask<String, String, ProfileViewResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(ProfileViewInfoActivity.this);
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
                mMyTopFanList = result.getTopFanList();

                Picasso.with(ProfileViewInfoActivity.this)
                        .load(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getFirst())
                        .placeholder(R.drawable.no_image)
                        .into((ImageView)findViewById(R.id.img_profile));

                ((TextView)findViewById(R.id.txt_profile_username_id)).setText(String.valueOf(mMyProfileInfo.getUsername()));
                //((TextView)findViewById(R.id.txt_profile_userid_id)).setText(String.valueOf(mMyProfileInfo.getUserid()));
                ((TextView)findViewById(R.id.txt_level_id)).setText(getString(R.string.str_level_prifix, mMyProfileInfo.getLevel_num()));
                ((TextView)findViewById(R.id.txt_rank_id)).setText(String.valueOf(mMyProfileInfo.getRank_recommend()));

                ((TextView)findViewById(R.id.txt_friend_num_id)).setText(String.valueOf(mMyProfileInfo.getFriend_cnt()));
                ((TextView)findViewById(R.id.txt_follow_num_id)).setText(String.valueOf(mMyProfileInfo.getFollow_cnt()));
                ((TextView)findViewById(R.id.txt_fan_num_id)).setText(String.valueOf(mMyProfileInfo.getFan_cnt()));
                //((TextView)findViewById(R.id.txt_relation_num_id)).setText(String.valueOf(mMyProfileInfo.getFanCnt() + mMyProfileInfo.getFollowCnt()));
                //((TextView)findViewById(R.id.txt_constellation_id)).setText(String.valueOf(mMyProfileInfo.getConstellation()));
                ((TextView)findViewById(R.id.txt_location_id)).setText(String.valueOf(mMyProfileInfo.getLocation()));
                ((TextView)findViewById(R.id.txt_send_bana_id)).setText(String.valueOf(mMyProfileInfo.getSend_choco_cnt()));
                if (mMyProfileInfo.getIntroduce() != null)
                    ((TextView)findViewById(R.id.txt_oneself_introduce_id)).setText(mMyProfileInfo.getIntroduce());

                int i = 0;
                for (FanItemResponse item : mMyTopFanList){
                    Picasso.with(ProfileViewInfoActivity.this)
                    .load(Constants.IMG_MODEL_URL + item.getFanID() + "/" + item.getFirst())
                    .placeholder(mDefaultNoImageArr[i])
                    .into(mTopFanList.get(i));
                    i++;
                }
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
