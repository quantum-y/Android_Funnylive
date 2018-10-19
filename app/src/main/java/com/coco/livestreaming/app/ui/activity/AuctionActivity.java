package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.ProfileInfoResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.adapter.ImageAdapter;
import com.coco.livestreaming.app.ui.view.RectangleImageView;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.ShareUtil;
import com.coco.livestreaming.app.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AuctionActivity extends Activity implements View.OnTouchListener {

    private EditText mTxtName;
    private EditText mTxtContent;
    private Spinner spAuctionHour;
    private SyncInfo info;

    WindowManager mWindowManager;
    WindowManager.LayoutParams mParamsBar;
    WindowManager.LayoutParams mParamImage;

    int mStartDragY;
    int mStartDragX;
    boolean mDragging;
    ImageView mDragBar;
    ImageView mDragImage;
    int mSlop;
    final static int DRAGBARTHICK = 6;

    boolean blnLongTouchState = false;
    boolean blnChangState = false;
    GridView gv_choose;

    int nCellX;
    int nCellY;
    int nCellHeight;
    int nCellWidth;

    RectangleImageView img_large;
    ImageView 			img_large_rect;
    boolean             flagimg = false;

    Rect rect;
    int nPosition;
    ImageAdapter adapter;
    List<Object> listImageUrl;
    List<String> listRealImagePath;
    private int		mUploadImageIndex = 0;
    private int		mUploadImageCount = 0;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);
        info = new SyncInfo(this);

        mTxtName = (EditText)findViewById(R.id.edit_sell_thing_name);
        mTxtContent = (EditText)findViewById(R.id.edit_content);

        userId = SessionInstance.getInstance().getLoginData().getBjData().getUserid();
        gv_choose = (GridView)findViewById(R.id.gv_choose);
        listImageUrl = new ArrayList<Object>();
        listImageUrl.add(Constants.IMG_AUCTION_URL + userId +  "/1.jpg");
        listImageUrl.add(Constants.IMG_AUCTION_URL + userId +  "/2.jpg");
        listImageUrl.add(Constants.IMG_AUCTION_URL + userId +  "/3.jpg");
        listImageUrl.add(Constants.IMG_AUCTION_URL + userId +  "/4.jpg");
        listImageUrl.add(Constants.IMG_AUCTION_URL + userId +  "/5.jpg");
        adapter = new ImageAdapter(listImageUrl);
        adapter.setOnImgClickListener(imgListener);
        adapter.setOnImgLongClickListener(imgLongListener);
        gv_choose.setAdapter(adapter);

        spAuctionHour = (Spinner)findViewById(R.id.spinner_acution_hour);
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.arr_auction_hour,  android.R.layout.simple_spinner_dropdown_item);
        spAuctionHour.setAdapter(mSpinnerAdapter);

        img_large = (RectangleImageView)findViewById(R.id.img_large);
        img_large_rect = (ImageView)findViewById(R.id.img_large_rect);
        gv_choose.setOnTouchListener(this);
        prepareDragReorder();

        listRealImagePath = new ArrayList<String>();
        listRealImagePath.add("");
        listRealImagePath.add("");
        listRealImagePath.add("");
        listRealImagePath.add("");
        listRealImagePath.add("");
    }
    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Utils.REQUEST_PHOTO_EDIT) {
            if (resultCode != Activity.RESULT_OK || data == null)
                return;
            Uri uri = data.getData();
            String path = ShareUtil.getRealPathFromURI(this, uri);
            if (path == null) {
                Toast.makeText(this, getString(R.string.profile_image_select_warning), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, ProfileViewInfoEditPhotoActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("index", requestCode);
            startActivityForResult(intent, Utils.REQUEST_PHOTO_EDIT);
        }else if (requestCode == Utils.REQUEST_PHOTO_EDIT){
            if (data == null)
                return;
            String filename = data.getStringExtra("path");
            int index = data.getIntExtra("index", 0);
            if (filename == null){
                Toast.makeText(this, getString(R.string.profile_image_select_warning), Toast.LENGTH_LONG).show();
                return;
            }
            listRealImagePath.set(index, filename);
            Uri uri = Uri.fromFile(new File(filename));
            switch (index) {
                case 0:
                    Picasso.with(this).load(uri).into(img_large);
                    listImageUrl.set(0, uri);
                    break;
                default:
                    adapter.setItem(index - 1, uri);
                    adapter.notifyDataSetChanged();
                    break;
            }
            flagimg = true;
        }

    }

    private void prepareDragReorder() {
        mSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        mParamsBar = new WindowManager.LayoutParams();
        mParamsBar.gravity = Gravity.TOP|Gravity.START;

        mParamsBar.x = 0;
        mParamsBar.y = 0;
        mParamsBar.height = DRAGBARTHICK;
        mParamsBar.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParamsBar.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParamsBar.format = PixelFormat.TRANSLUCENT;
        mParamsBar.windowAnimations = 0;

        mDragBar = new ImageView(this);
        int backGroundColor = Color.parseColor("red");
        mDragBar.setBackgroundColor(backGroundColor);
        mDragBar.setVisibility(View.GONE);

        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mDragBar, mParamsBar);

        mParamImage = new WindowManager.LayoutParams();
        mParamImage.copyFrom(mParamsBar);
        mParamImage.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParamImage.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParamImage.alpha = 0.5f;

        mDragImage = new ImageView(this);
        mDragImage.setVisibility(View.GONE);
        mWindowManager.addView(mDragImage, mParamImage);
    }

    public void onButtonClick(View v) {
        switch (v.getId()){
            case R.id.img_large:
                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                mediaChooser.setType("image/*");
                mediaChooser.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(mediaChooser, 0);
                break;
            case R.id.img_back_id:
                finish();
                break;
            case R.id.btn_ok_id:
                onSave();
                break;
        }
    }
    private void onSave(){
        String strName = mTxtName.getText().toString().trim();
        String strContent = mTxtContent.getText().toString().trim();
        String strHour = spAuctionHour.getSelectedItem().toString().trim();
        int imageCount = 0;
        for (int i=0; i < listRealImagePath.size(); i ++) {
            if (listRealImagePath.get(i) != "")
                imageCount++;
        }

        if (strName.equals("")) {
            Toast.makeText(this, getString(R.string.str_sell_thing_hint), Toast.LENGTH_LONG).show();
            return;
        }  else if (strContent.equals("")) {
            Toast.makeText(this, getString(R.string.str_content_hint), Toast.LENGTH_LONG).show();
            return;
        } else  if (imageCount == 0) {
            Toast.makeText(this, getString(R.string.str_select_one_image_at_lease), Toast.LENGTH_LONG).show();
            return;
        }
        new AuctionSaveAsync().execute(strName, strContent, strHour, String.valueOf(imageCount));
    }

    private ImageAdapter.OnImgClickListener imgListener = new ImageAdapter.OnImgClickListener() {
        @Override
        public void onImgClick(int pos) {
            if (blnLongTouchState) {
                mDragImage.setVisibility(View.GONE);
                return;
            }
            Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
            mediaChooser.setType("image/*");
            mediaChooser.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(mediaChooser, pos + 1);
        }
    };

    private ImageAdapter.OnImgLongClickListener imgLongListener = new ImageAdapter.OnImgLongClickListener() {
        @Override
        public void onImgLongClick(View view, int position) {
            if (mDragging) {
                blnLongTouchState = false;
            } else {
                blnLongTouchState = true;
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                Bitmap bit = Bitmap.createBitmap(view.getDrawingCache(true));
                mDragImage.setImageBitmap(bit);
                mDragImage.setVisibility(View.VISIBLE);
                view.setDrawingCacheEnabled(false);

                nCellHeight = view.getHeight();
                nCellWidth = view.getWidth();

                nPosition = position + 1;
                nCellX = 0;
                nCellY = 0;
                if (position > 1)
                    nCellX = nCellWidth;
                if (position == 1 || position == 3)
                    nCellY = nCellHeight;

                mParamImage.y = nCellX + gv_choose.getTop() + nCellHeight / 2;
                mParamImage.x = nCellY + gv_choose.getLeft();
                mWindowManager.updateViewLayout(mDragImage, mParamImage);
                //large_image rect
                rect = new Rect(img_large.getLeft() - nCellWidth / 3
                        ,img_large.getTop()
                        ,img_large.getRight() - nCellWidth / 2
                        ,img_large.getBottom() + nCellHeight / 2);
                //Log.d(TAG, "left="+ rect.left + " right="+ rect.right + " top=" + rect.top + " bottom=" + rect.bottom);
            }
        }
    };
    class AuctionSaveAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(AuctionActivity.this);
                ((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
            }
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            SuccessFailureResponse result = info.syncAuctionSave(args[0], args[1], args[2], args[3]);
            return result;
        }

        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
                //이메지변화가 있으면
                if (flagimg && listImageUrl != null && !listImageUrl.isEmpty()) {
                    while (mUploadImageIndex < listImageUrl.size()) {
                        Object item = listImageUrl.get(mUploadImageIndex);
                        if (item.getClass().getSimpleName().contains(Uri.class.getSimpleName())) {
                            new ImageUploadAsync().execute(String.valueOf(mUploadImageCount+1), listRealImagePath.get(mUploadImageIndex));
                            mUploadImageIndex++;
                            mUploadImageCount++;
                            break;
                        }
                        mUploadImageIndex++;
                    }
                }else{
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },20);
                }
            }else{
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AuctionActivity.this, getString(R.string.error_network), Toast.LENGTH_LONG);
                    }
                },20);
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
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        if (blnLongTouchState == false) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragY = y;
                mStartDragX = x;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (mDragging == false && blnLongTouchState == true) {
                    if (Math.abs(y - mStartDragY) > mSlop) {
                        mDragging = true;
                    }
                }

                if (mDragging) {
                    mDragImage.setVisibility(View.VISIBLE);

                    mParamImage.y = gv_choose.getTop() + nCellHeight + y - nCellHeight;
                    mParamImage.x = gv_choose.getLeft() + x - nCellWidth / 2;
                    mWindowManager.updateViewLayout(mDragImage, mParamImage);

                    if (rect.contains(mParamImage.x, mParamImage.y)) {
                        img_large_rect.setVisibility(View.VISIBLE);
                        blnChangState = true;

                    } else {
                        img_large_rect.setVisibility(View.GONE);
                        blnChangState = false;
                    }
                }
                return false;

            case MotionEvent.ACTION_UP:
                if (blnChangState) {
                    Object temp = listImageUrl.get(nPosition);
                    if (temp.getClass().getSimpleName().contains(Uri.class.getSimpleName())) {
                        Picasso.with(AuctionActivity.this)
                                .load((Uri) temp)
                                .placeholder(R.drawable.album_empty_default_img)
                                .into((ImageView) findViewById(R.id.img_large));
                    }
                    else {
                        Picasso.with(AuctionActivity.this)
                                .load((String) temp)
                                .placeholder(R.drawable.album_empty_default_img)
                                .into((ImageView) findViewById(R.id.img_large));
                    }
                    img_large_rect.setVisibility(View.GONE);
                    adapter.setItem(nPosition - 1, listImageUrl.get(0));
                    adapter.notifyDataSetChanged();
                    listImageUrl.set(0, temp);
                    blnChangState = false;

                    temp = listRealImagePath.get(nPosition);
                    listRealImagePath.set(nPosition, listRealImagePath.get(0));
                    listRealImagePath.set(0, (String)temp);
                }
                mDragImage.setVisibility(View.GONE);
                mDragging = false;
                blnLongTouchState = false;
                return true;
        }
        return true;
    }

    class ImageUploadAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
                Utils.displayProgressDialog(AuctionActivity.this);
                ((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
            }
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            SuccessFailureResponse result  = info.syncAuctionImageUpload(String.valueOf(args[0]), args[1]);
            return result;
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
                if (!result.getResult().isEmpty())
                    SessionInstance.getInstance().getLoginData().getBjData().setThumbnail(result.getResult());
                //이메지변화가 있으면
                if (flagimg && listImageUrl != null && !listImageUrl.isEmpty()) {
                    while (mUploadImageIndex < listImageUrl.size()) {
                        Object item = listImageUrl.get(mUploadImageIndex);
                        if (item.getClass().getSimpleName().contains(Uri.class.getSimpleName())) {
                            new ImageUploadAsync().execute(String.valueOf(mUploadImageCount+1), listRealImagePath.get(mUploadImageIndex));
                            mUploadImageIndex++;
                            mUploadImageCount++;
                            break;
                        }
                        mUploadImageIndex++;
                    }
                }else{
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },20);
                }
            }
            else{
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                },20);
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
                    if (mUploadImageIndex == listImageUrl.size())
                        onBackPressed();
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

