package com.coco.livestreaming.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.nostra13.universalimageloader.utils.BitmapLoader;
import com.squareup.picasso.Picasso;
import com.wowza.gocoder.sdk.api.android.opengl.WOWZGLES;
import com.wowza.gocoder.sdk.api.geometry.WOWZSize;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileViewInfoEditPhotoActivity extends Activity implements View.OnTouchListener {

    private String imgPath = "";

    private int mSlop;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutMask;
    private WindowManager.LayoutParams mLayoutParamPhoto;
    private ImageView mPhotoMask;
    private ImageView mPhoto;
    private int mIndex = 0;
    private int mOrientation = 0;
    private int mStartX = 0;
    private int mStartY = 0;
    final static int DRAGBARTHICK = 6;
    private Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_info_edit_photo);

        Intent data = getIntent();
        if (data != null){
            imgPath = data.getStringExtra("path");
            mIndex = data.getIntExtra("index", 0);
        }
        mSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        float fMaskSize = (float)(Constants.SCREEN_WIDTH * 0.8);
        int nWidth = 0;
        try {
            mBitmap = BitmapFactory.decodeFile(imgPath);
            ExifInterface imgInfo = new ExifInterface(imgPath);
            Matrix xformMatrix = new Matrix();
            float fScale = fMaskSize / Math.min(mBitmap.getWidth(), mBitmap.getHeight());
            xformMatrix.setScale(fScale, fScale);
            switch (imgInfo.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)){
                case ExifInterface.ORIENTATION_UNDEFINED:
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                case ExifInterface.ORIENTATION_NORMAL:
                    xformMatrix.preRotate(0);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    xformMatrix.preRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    xformMatrix.preRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    xformMatrix.preRotate(270);
                    break;
            }
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), xformMatrix, false);


        }catch (Exception e){
            Toast.makeText(this, "이미지 파일이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        };
        mLayoutParamPhoto = new WindowManager.LayoutParams();
        mLayoutParamPhoto.x = mStartX = (int)(Constants.SCREEN_WIDTH - mBitmap.getWidth()) /2;
        mLayoutParamPhoto.y = mStartY = (int)(Constants.SCREEN_HEIGHT - mBitmap.getHeight()) /2;
        mLayoutParamPhoto.width = mBitmap.getWidth();
        mLayoutParamPhoto.height = mBitmap.getHeight();

        if(mBitmap.getWidth() > mBitmap.getHeight()){
            mLayoutParamPhoto.gravity = Gravity.TOP|Gravity.START;
            mOrientation = 0;
        }
        else{
            mLayoutParamPhoto.gravity = Gravity.LEFT|Gravity.TOP;
            mOrientation = 1;
        }

        mLayoutParamPhoto.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParamPhoto.format = PixelFormat.RGBA_8888;
        mPhoto = new ImageView(this);
        //mBitmap = Bitmap.createScaledBitmap(mBitmap, mLayoutParamPhoto.width, mLayoutParamPhoto.height, false);
        mPhoto.setImageBitmap(mBitmap);
        mPhoto.setVisibility(View.VISIBLE);
        mWindowManager.addView(mPhoto, mLayoutParamPhoto);

        WindowManager.LayoutParams leftParam = new WindowManager.LayoutParams();
        leftParam.gravity = Gravity.LEFT|Gravity.TOP;
        leftParam.x = 0;
        leftParam.y = (int)(Constants.SCREEN_HEIGHT - fMaskSize) / 2;
        leftParam.width = (int)(Constants.SCREEN_WIDTH - fMaskSize) / 2;
        leftParam.height = (int)fMaskSize;
        leftParam.flags =  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  | WindowManager.LayoutParams.FLAG_FULLSCREEN;;
        leftParam.alpha = 0.6f;
        ImageView leftView = new ImageView(this);
        leftView.setBackgroundColor(Color.BLACK);
        leftView.setVisibility(View.VISIBLE);
        mWindowManager.addView(leftView, leftParam);

       WindowManager.LayoutParams rightParam = new WindowManager.LayoutParams();
        rightParam.copyFrom(leftParam);
        rightParam.x = Constants.SCREEN_WIDTH - leftParam.width;
        rightParam.alpha = 0.6f;
        ImageView rightView = new ImageView(this);
        rightView.setBackgroundColor(Color.BLACK);
        rightView.setVisibility(View.VISIBLE);
        mWindowManager.addView(rightView, rightParam);

        WindowManager.LayoutParams topParam = new WindowManager.LayoutParams();
        topParam.gravity = Gravity.TOP|Gravity.START;
        topParam.x = 0;
        topParam.y = 0;
        topParam.width = Constants.SCREEN_WIDTH;
        topParam.height = leftParam.y;
        topParam.flags =  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        topParam.alpha = 0.6f;
        ImageView topView = new ImageView(this);
        topView.setBackgroundColor(Color.BLACK);
        topView.setVisibility(View.VISIBLE);
        mWindowManager.addView(topView, topParam);


        WindowManager.LayoutParams bottomParam = new WindowManager.LayoutParams();
        bottomParam.copyFrom(topParam);
        bottomParam.y = topParam.height + (int)fMaskSize;
        bottomParam.alpha = 0.6f;
        ImageView bottomView = new ImageView(this);
        bottomView.setBackgroundColor(Color.BLACK);
        bottomView.setVisibility(View.VISIBLE);
        mWindowManager.addView(bottomView, bottomParam);

        mLayoutMask = new WindowManager.LayoutParams();
        mLayoutMask.gravity = Gravity.TOP|Gravity.START;
        mLayoutMask.x = (int)(Constants.SCREEN_WIDTH - fMaskSize) / 2;
        mLayoutMask.y = (int)(Constants.SCREEN_HEIGHT - fMaskSize) / 2;
        mLayoutMask.height = (int)fMaskSize;
        mLayoutMask.width = (int)fMaskSize;
        mLayoutMask.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                /*
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                ;*/
        mLayoutMask.format = PixelFormat.TRANSLUCENT;
        //mLayoutMask.windowAnimations = 0;

        mPhotoMask = new ImageView(this);
        mPhotoMask.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(R.drawable.photo_sel_rect1)
                .placeholder(R.drawable.album_empty_default_img)
                .into(mPhotoMask);
        mWindowManager.addView(mPhotoMask, mLayoutMask);

        findViewById(R.id.activity_profile_view_info_edit_photo).setOnTouchListener(this);

    }
    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.btn_check_id:
                onSave();
                break;
            case R.id.btn_cancel_id:
                finish();
                break;
            case R.id.btn_rotate_id:
                int temp = mLayoutParamPhoto.width;
                mLayoutParamPhoto.width = mLayoutParamPhoto.height;
                mLayoutParamPhoto.height = temp;
                mOrientation = mOrientation == 1 ? 0 : 1;
                if (mOrientation == 0){
                    mStartX = (int)((Constants.SCREEN_WIDTH - mLayoutParamPhoto.width) /2);
                    mStartY = (int)(Constants.SCREEN_HEIGHT  - Constants.SCREEN_WIDTH * 0.8) / 2;
                }else{
                    mStartX = (int)(Constants.SCREEN_WIDTH * 0.1);
                    mStartY = (int)((Constants.SCREEN_HEIGHT - mLayoutParamPhoto.height) /2);
                }
                mLayoutParamPhoto.x = mStartX;
                mLayoutParamPhoto.y = mStartY;

                Matrix xformMatrix = new Matrix();
                //xformMatrix.setScale(mScale, mScale);  // flip horizxfor
                xformMatrix.preRotate(90);  // flip vert
                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mLayoutParamPhoto.height, mLayoutParamPhoto.width, xformMatrix, false);
                mPhoto.setImageBitmap(mBitmap);
                mWindowManager.updateViewLayout(mPhoto, mLayoutParamPhoto);
                break;
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    int mStartDragY = 0;
    int mStartDragX = 0;
    boolean isDragging = false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                mStartDragY = y;
                isDragging = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isDragging)
                    return true;
                if (mOrientation == 0 && Math.abs(x - mStartDragX) > mSlop) {
                    if (mLayoutParamPhoto.x > mLayoutMask.x ) {
                        mLayoutParamPhoto.x = mLayoutMask.x;
                        isDragging = false;
                    }
                    else if (mLayoutParamPhoto.x + mLayoutParamPhoto.width < mLayoutMask.x + mLayoutMask.width) {
                        mLayoutParamPhoto.x = mLayoutMask.x + mLayoutMask.width - mLayoutParamPhoto.width;
                        isDragging = false;
                    }
                    else {
                        mLayoutParamPhoto.x = mStartX + (x - mStartDragX);
                        isDragging = true;
                    }

                }else if (mOrientation == 1 && Math.abs(y - mStartDragY) > mSlop) {
                    if (mLayoutParamPhoto.y > mLayoutMask.y ) {
                        mLayoutParamPhoto.y = mLayoutMask.y;
                        isDragging = false;
                    }
                    else if (mLayoutParamPhoto.y + mLayoutParamPhoto.height < mLayoutMask.y + mLayoutMask.height) {
                        mLayoutParamPhoto.y = mLayoutMask.y + mLayoutMask.height - mLayoutParamPhoto.height;
                        isDragging = false;
                    }
                    else {
                        mLayoutParamPhoto.y = mStartY + (y - mStartDragY);
                        isDragging = true;
                    }
                }else
                    return true;

                mWindowManager.updateViewLayout(mPhoto, mLayoutParamPhoto);

                return false;

            case MotionEvent.ACTION_UP:
                isDragging = false;
                mStartX = mLayoutParamPhoto.x;
                mStartY = mLayoutParamPhoto.y;
                return true;
        }
        return true;
    }

    private void onSave(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedOutputStream bitmapStream = null;
                StringBuffer statusMessage = new StringBuffer();
                try {
                    imgPath = Constants.IMG_FULL_PATH + File.separator + new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date()) + ".jpg";
                    File jpgFile = new File(imgPath);
                    if (jpgFile != null) {
                        bitmapStream = new BufferedOutputStream(new FileOutputStream(jpgFile));
                        int x = mLayoutMask.x - mLayoutParamPhoto.x;
                        int y = mLayoutMask.y - mLayoutParamPhoto.y;
                        int width = mLayoutMask.width;
                        int height = mLayoutMask.height;
                        mBitmap = Bitmap.createBitmap(mBitmap, x, y, width, height);
                        //bmp = Bitmap.createScaledBitmap(bmp, Constants.SNAP_IMAGE_WIDTH, Constants.SNAP_IMAGE_HEIGHT, false);
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapStream);
                        mBitmap.recycle();
                        statusMessage.append("Screenshot saved to ").append(jpgFile.getAbsolutePath());
                    } else {
                        statusMessage.append("The directory for the screenshot could not be created");
                    }

                } catch (Exception e) {
                    statusMessage.append(e.getLocalizedMessage());
                } finally {
                    if (bitmapStream != null) {
                        try {
                            bitmapStream.close();
                        } catch (IOException closeException) {
                            // ignore exception on close
                        }
                    }
                    final String toastStr = statusMessage.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("path", imgPath);
                            intent.putExtra("index", mIndex);
                            setResult(Utils.REQUEST_PHOTO_EDIT, intent);
                            finish();

                        }
                    });
                }
            }
        }).start();

    }
}
