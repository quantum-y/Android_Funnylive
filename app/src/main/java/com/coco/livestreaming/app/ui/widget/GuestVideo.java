package com.coco.livestreaming.app.ui.widget;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 3/8/2017.
 */

public class GuestVideo {
    public LiveVideoShowActivity   mActivity;
    public GuestPlayerView mGuestPlayerView = null;
    private ImageView               mCoverImage;
    private TextView                mCoverText;
    public String                  mPath;
    public String                   mUserID;
    public View                    mContainer;
    public boolean                  mIsInit = false;
    private TranslateAnimation      mAniTran;
    public GuestVideo(LiveVideoShowActivity activity, View container, GuestPlayerView player, ImageView coverImg, TextView coverText){
        mActivity = activity;
        mGuestPlayerView = player;
        mCoverImage = coverImg;
        mCoverText = coverText;
        mContainer = container;
    }

    public void init(String userid, String path) {
        mUserID = userid;
        mPath = path;
        if (mActivity.mIsBJ || !mUserID.equals(SessionInstance.getInstance().getLoginData().getBjData().getUserid())) {//초청카메라를 나를 제외한 모든사람들에게 보여준다.
            mAniTran = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            mAniTran.setDuration(2000);
            mAniTran.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (mCoverImage != null) {
                        Picasso.with(mActivity)
                                .load(Constants.IMG_MODEL_URL + mUserID + File.separator + mActivity.getFirstImage(mUserID))
                                .placeholder(R.drawable.no_image)
                                .into(mCoverImage);
                        mCoverImage.setVisibility(View.VISIBLE);
                    }
                    if (mCoverText != null) {
                        mCoverText.setVisibility(View.VISIBLE);
                        mCoverText.setText(mUserID);
                    }
                    mContainer.setAlpha(0.0f);
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mContainer.setAlpha(1.0f);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {//이경우는 방송자가 초청화면 close하는 경우
                    onLongClickInviteClose();
                    return true;
                }
            });
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mActivity.mIsBJ)
                        Toast.makeText(mActivity, mActivity.getString(R.string.str_invite_off), Toast.LENGTH_SHORT).show();
                }
            });
            if (mGuestPlayerView != null)
                mGuestPlayerView.setZOrderMediaOverlay(true);
            mContainer.startAnimation(mAniTran);
        }

        if (mGuestPlayerView != null) {
            mGuestPlayerView.init(GuestVideo.this, mPath);
            mGuestPlayerView.setVisibility(View.VISIBLE);
        }

        mIsInit = true;
    }

    public void onLongClickInviteClose(){//방송자가 롱클릭으로 초청화면을 닫는경우
        if(mActivity.mIsBJ) {
            mActivity.attemptSend(mActivity.getString(R.string.str_invite_reject), Constants.ALERT_MSG_INVITE_REJECT, mUserID);
            mActivity.onGuestVideoClose(mUserID);
        }
    }
    public void onDisappear(){
        TranslateAnimation mAniEndTran = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mAniEndTran.setDuration(2000);
        mContainer.startAnimation(mAniEndTran);
        mAniEndTran.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mContainer.setVisibility(View.INVISIBLE);}
            @Override
            public void onAnimationEnd(Animation animation) {Deinit();}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    public void Deinit(){
        if (mGuestPlayerView != null) {
            mGuestPlayerView.setVisibility(View.GONE);
            mGuestPlayerView.surfaceDestroyed(mGuestPlayerView.mHolder);
        }

        if (mCoverImage != null) {
            mCoverImage.setVisibility(View.GONE);
        }
        if (mCoverText != null) {
            mCoverText.setVisibility(View.GONE);
        }
        mUserID = "";
        mIsInit = false;
    }

    public void showProgressView() {
//        mActivity.mMediaPlayerLoadingProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressView() {
        mActivity.mMediaPlayerLoadingProgress.setVisibility(View.INVISIBLE);
    }
}
