package com.coco.livestreaming.gocoder.ui;

/*
 * WOWZA MEDIA SYSTEMS, LLC ("Wowza") CONFIDENTIAL
 * Copyright (c) 2005-2016 Wowza Media Systems, LLC, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Wowza Media Systems, LLC.
 * The intellectual and technical concepts contained herein are proprietary to Wowza Media Systems, LLC
 * and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret
 * or copyright law. Dissemination of this information or reproduction of this material is strictly forbidden
 * unless prior written permission is obtained from Wowza Media Systems, LLC. Access to the source code
 * contained herein is hereby forbidden to anyone except current Wowza Media Systems, LLC employees, managers
 * or contractors who have executed Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this
 * source code, which includes information that is confidential and/or proprietary, and is test2 trade secret, of
 * Wowza Media Systems, LLC. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE, OR PUBLIC DISPLAY
 * OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF WOWZA MEDIA SYSTEMS, LLC IS
 * STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION
 * OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR
 * DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 *
 */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.coco.livestreaming.R;

public class StatusView extends RelativeLayout {
    final private static String TAG = StatusView.class.getSimpleName();

    private TextView mTxtStatus;
    private Button mBtnDismiss;
    private String mStatusMessage;
    //private ImageView mImgLoadingAni;
    private ViewAnimator mImgLoadingAni;
    private AlphaAnimation showAnimation, hideAnimation;
    private AnimationDrawable loadingAni;
    private volatile boolean isPaused;
    private volatile boolean isShowing;
    private volatile boolean isHiding;

    public StatusView(Context context) {
        super(context);
        init(context);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        isPaused = false;
        isShowing = false;
        isHiding = false;
        mStatusMessage = null;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.status_view, this, true);

        mTxtStatus = (TextView) findViewById(R.id.txtStatus);
        mBtnDismiss = (Button) findViewById(R.id.btnDismiss);

        mBtnDismiss.setVisibility(GONE);
        mBtnDismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(INVISIBLE);
                setClickable(false);
                mBtnDismiss.setVisibility(GONE);
                isPaused = false;
            }
        });

        showAnimation = new AlphaAnimation(0.0f, 1.0f);
        showAnimation.setDuration(500);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                preAnimation(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                postAnimation(true);
           }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        hideAnimation = new AlphaAnimation(1.0f, 0.0f);
        hideAnimation.setDuration(1500);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                preAnimation(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                postAnimation(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mImgLoadingAni = (ViewAnimator)findViewById(R.id.img_loading_ani_id);
        loadingAni = (AnimationDrawable)mImgLoadingAni.getBackground();

        setClickable(false);
    }

    private synchronized void preAnimation(boolean beforeShow) {
        if (beforeShow) {
            isShowing = true;

            bringToFront();
            setClickable(true);
        } else {
            isHiding = true;
        }
    }

    private synchronized void postAnimation(boolean afterShow) {
        if (afterShow) {
            isShowing = false;
            setVisibility(VISIBLE);

            if (mStatusMessage == null) { // if message has been cleared, hide
                updateView();
            }

        } else {
            isHiding = false;

            setClickable(false);
            setVisibility(View.INVISIBLE);
            setAlpha(1f);
        }
    }

    public synchronized void setStatus(WOWZStatus status) {
        if (status.getLastError() != null) {
            isPaused = true;
            mStatusMessage = status.getLastError().getErrorDescription();
            updateView();
        } else if (!isPaused) {
            switch (status.getState()) {
                case WOWZState.IDLE:
                case WOWZState.RUNNING:
                    mStatusMessage = null;
                    break;

                case WOWZState.STARTING:
                    mStatusMessage = getResources().getString(R.string.status_connecting);
                    break;

                case WOWZState.READY:
                    //mStatusMessage = getResources().getString(R.string.status_connected);
                    mStatusMessage = getResources().getString(R.string.status_connecting);
                    break;

                case WOWZState.STOPPING:
                    mStatusMessage = getResources().getString(R.string.status_disconnecting);
                    break;
            }
            updateView();
        }
    }

    public synchronized void setErrorMessage(String message) {
        setStatus(new WOWZStatus(WOWZState.IDLE, new WOWZError(message)));
    }

    public synchronized void showMessage(String message) {
        setErrorMessage(message);
    }

    private synchronized void updateView() {
        if (isPaused) { // show error

            loadingAni.stop();//망키애니메이션중지
            mImgLoadingAni.setVisibility(INVISIBLE);

            clearAnimation();
            mTxtStatus.setText(mStatusMessage);
            bringToFront();
            setClickable(true);
            setVisibility(VISIBLE);
            setAlpha(1f);
            mBtnDismiss.setVisibility(VISIBLE);
        } else if (mStatusMessage != null) { // show message
            mTxtStatus.setText(mStatusMessage);
            if (isHiding) {
                loadingAni.stop();
                mImgLoadingAni.setVisibility(INVISIBLE);
                clearAnimation();
            }
            if (!isShowing){
                mImgLoadingAni.setVisibility(VISIBLE);
                loadingAni.start();
                startAnimation(showAnimation);
            }
        } else { // hide
            if (!isShowing && !isHiding) {
                mImgLoadingAni.setVisibility(VISIBLE);
                loadingAni.start();
                startAnimation(hideAnimation);
            }
        }
    }
}
