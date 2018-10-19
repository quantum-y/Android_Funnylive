package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.FileDescriptor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.nostra13.universalimageloader.utils.BitmapLoader;
import com.squareup.picasso.Picasso;

public class ProfileViewInfoEditActivity extends Activity implements OnTouchListener{

    private EditText mTxtName;
    private TextView mTxtBirthday;
    private EditText mTxtNative;
    private EditText mTxtIntroduce;
    private EditText mTxtKnowledge;
    private SyncInfo info;
    private ProfileInfoResponse mMyProfileInfo;
    
    //cgh_edit
    final static String TAG = "DragReorder";
	//ReorderScrollView mScroll;
	//LinearLayout mList;
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
	ImageButton btn;
	GridView gv_choose;
	
	int nCellX;
	int nCellY;
	int nCellHeight;
	int nCellWidth;
	
	RectangleImageView 	img_large;
	ImageView 			img_large_rect;
	boolean             flagimg = false;

	Rect rect;
	int nPosition;
	ImageAdapter adapter;
	List<Object> listImageUrl;
	List<String> listRealImagePath;
	private int		mUploadImageIndex = 0;
	private List<ToggleButton> mBtnSexList = new ArrayList<>();
	private int mSexIndex;
	DateFormat fmtDateAndTime=DateFormat.getDateInstance();
	Calendar dateAndTime=Calendar.getInstance();
	DatePickerDialog.OnDateSetListener datePickerDialog=new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			//mTxtBirthday.setText(fmtDateAndTime.format(dateAndTime.getTime()).trim());
			mTxtBirthday.setText(String.format("%d-%d-%d",year, monthOfYear+1, dayOfMonth));

		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_info_edit);

        info = new SyncInfo(this);
        Intent data = getIntent();
        if (data != null){
            new ProfileViewAsync().execute(data.getStringExtra("userid"));
        }

        mTxtName = (EditText)findViewById(R.id.edit_user_name_id);
        mTxtBirthday = (TextView)findViewById(R.id.edit_user_birthday_id);
        mTxtNative = (EditText)findViewById(R.id.edit_user_native_id);
        mTxtIntroduce = (EditText)findViewById(R.id.edit_user_introduce_id);
        mTxtKnowledge = (EditText)findViewById(R.id.edit_user_knowlege_id);

		mBtnSexList.add((ToggleButton) findViewById(R.id.btn_sex_male));
		mBtnSexList.add((ToggleButton) findViewById(R.id.btn_sex_female));
		mBtnSexList.add((ToggleButton) findViewById(R.id.btn_sex_no));
        
        //cgh_edit
        mTxtBirthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(ProfileViewInfoEditActivity.this,
						datePickerDialog,
						dateAndTime.get(Calendar.YEAR),
						dateAndTime.get(Calendar.MONTH),
						dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
        gv_choose = (GridView)findViewById(R.id.gv_choose);
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
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != Utils.REQUEST_PHOTO_EDIT) {
			if (resultCode != Activity.RESULT_OK || data == null)
				return;
			Uri uri = data.getData();
			String path = ShareUtil.getRealPathFromURI(ProfileViewInfoEditActivity.this, uri);
			if (path == null) {
				Toast.makeText(ProfileViewInfoEditActivity.this, getString(R.string.profile_image_select_warning), Toast.LENGTH_LONG).show();
				return;
			}
			Intent intent = new Intent(this, ProfileViewInfoEditPhotoActivity.class);
			intent.putExtra("path", path);
			//intent.putExtra("uri", uri.toString());
			intent.putExtra("index", requestCode);
			startActivityForResult(intent, Utils.REQUEST_PHOTO_EDIT);
		}else if (requestCode == Utils.REQUEST_PHOTO_EDIT){

			/*
			String filename = BitmapLoader.getCompressSaveFileName(path);
			*/
			if (data == null)
				return;
			String filename = data.getStringExtra("path");
			int index = data.getIntExtra("index", 0);
			if (filename == null){
				Toast.makeText(ProfileViewInfoEditActivity.this, getString(R.string.profile_image_select_warning), Toast.LENGTH_LONG).show();
				return;
			}
			listRealImagePath.set(index, filename);
			Uri uri = Uri.fromFile(new File(filename));
			switch (index) {
				case 0:
					Picasso.with(ProfileViewInfoEditActivity.this).load(uri).into(img_large);
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
		// TODO Auto-generated method stub
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
				/*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);*/
				break;
			case R.id.img_back_id:
                onSave();
				break;
			case R.id.btn_sex_male:
			case R.id.btn_sex_female:
			case R.id.btn_sex_no:
				for(ToggleButton item: mBtnSexList){
					if (item.getTag().equals(v.getTag())) {
						mSexIndex = Integer.valueOf(item.getTag().toString());
						item.setChecked(true);
					}
					else
						item.setChecked(false);
				}
				break;
		}

	}
	private void onSave(){
		String strName = mTxtName.getText().toString().trim();
		String strBirthday = mTxtBirthday.getText().toString();
		String strNative = mTxtNative.getText().toString().trim();
		String strIntroduce = mTxtIntroduce.getText().toString().trim();
		String strKnowledge = mTxtKnowledge.getText().toString().trim();
		String strImageList = "";
		for (int i = 0; i < listImageUrl.size(); i++){
			String path = listImageUrl.get(i).toString();
			String[] q = path.split("/");
			int idx = q.length - 1;
			strImageList += q[idx];
			strImageList += Constants.ID_SEPARATOR;
		}
		for(ToggleButton item: mBtnSexList){
			if (item.isChecked()) {
				mSexIndex = Integer.valueOf(item.getTag().toString());
				break;
			}
		}
		new ProfileSetAsync().execute(strName, String.valueOf(mSexIndex), strBirthday, strNative, strIntroduce, strKnowledge, strImageList);
	}
	class ProfileViewAsync extends AsyncTask<String, String, ProfileViewResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(ProfileViewInfoEditActivity.this);
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
				listImageUrl = new ArrayList<Object>();
				listImageUrl.add(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getFirst());
                listImageUrl.add(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getSecond());
                listImageUrl.add(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getThird());
                listImageUrl.add(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getFourth());
                listImageUrl.add(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + "/" + mMyProfileInfo.getFifth());
				Picasso.with(ProfileViewInfoEditActivity.this)
						.load((String)listImageUrl.get(0))
						.placeholder(R.drawable.album_empty_default_img)
						.into((ImageView)findViewById(R.id.img_large));

                adapter = new ImageAdapter(listImageUrl);
				adapter.setOnImgClickListener(imgListener);
				adapter.setOnImgLongClickListener(imgLongListener);
        		gv_choose.setAdapter(adapter);
        		
        		mTxtName.setText(String.valueOf(mMyProfileInfo.getUsername().trim()));
				for (ToggleButton item : mBtnSexList){
					if (item.getTag().equals(String.valueOf(mMyProfileInfo.getSex())))
						item.setChecked(true);
					else
						item.setChecked(false);
				}
                mTxtBirthday.setText(mMyProfileInfo.getBirthday());
                mTxtNative.setText(mMyProfileInfo.getNative_type().trim());
                mTxtIntroduce.setText(mMyProfileInfo.getIntroduce().trim());
                mTxtKnowledge.setText(mMyProfileInfo.getKnowledge().trim());
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

			/*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, pos + 1);*/
		}
	};
	private ImageAdapter.OnImgLongClickListener imgLongListener = new ImageAdapter.OnImgLongClickListener() {
		@Override
		public void onImgLongClick(View view, int position) {

			Log.d(TAG, "position = "+ position);
			if (mDragging) {
				//Log.d(TAG, "long_Dragging_true: blnLongTouchState="+ String.valueOf(blnLongTouchState));
				blnLongTouchState = false;
			} else {
				//Log.d(TAG, "long_touch");
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
	class ProfileSetAsync extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(ProfileViewInfoEditActivity.this);
				((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
			}
		}

		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result = info.syncProfileSet(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
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
                            new ProfileImageUploadAsync().execute(String.valueOf(mUploadImageIndex), listRealImagePath.get(mUploadImageIndex));
                            mUploadImageIndex++;
                            break;
                        }
                        mUploadImageIndex++;
                    }
                }else{
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBack();
                        }
                    },20);
                }

			}else{
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                   onBack();
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
		// TODO Auto-generated method stub
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
			Log.d(TAG, "up x = "+ event.getX() + "y="+event.getY() + "blnLongTouchState="+ String.valueOf(blnLongTouchState));
			if (blnChangState) {
				Object temp = listImageUrl.get(nPosition);
				if (temp.getClass().getSimpleName().contains(Uri.class.getSimpleName())) {
					Picasso.with(ProfileViewInfoEditActivity.this)
							.load((Uri) temp)
							.placeholder(R.drawable.album_empty_default_img)
							.into((ImageView) findViewById(R.id.img_large));
				}
				else {
					Picasso.with(ProfileViewInfoEditActivity.this)
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

	class ProfileImageUploadAsync extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (((CocotvingApplication)getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(ProfileViewInfoEditActivity.this);
				((CocotvingApplication)getApplication()).mIsVisibleFlag = false;
			}
		}

		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result  = info.syncProfileImageUpload(String.valueOf(args[0]), args[1]);
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
                            new ProfileImageUploadAsync().execute(String.valueOf(mUploadImageIndex), listRealImagePath.get(mUploadImageIndex));
                            mUploadImageIndex++;
                            break;
                        }
                        mUploadImageIndex++;
                    }
                }else{
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBack();
                        }
                    },20);
                }
            }
			else{
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBack();
                    }
                },20);
            }
			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
                    if (mUploadImageIndex == listImageUrl.size())
                        onBack();
				}
			},10);
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}
	private void onBack(){
		Intent intent = new Intent(ProfileViewInfoEditActivity.this, ProfileViewInfoActivity.class);
		String userid = mMyProfileInfo == null ? "" : mMyProfileInfo.getUserid();
		intent.putExtra("userid", userid);
		startActivity(intent);
		finish();
	}
}


