package com.coco.livestreaming.app.ui.activity;

import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.network.NetworkEngine;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExchargeViewActivity extends Activity {

	public final String TAG = ExchargeViewActivity.class.getName();
    
    ImageButton btnBack;
    Button btnConfrim;
    ImageView imgSinbun, imgTongjang;
    EditText etBankNm, etAccountNum, etBananaNum;
    TextView lbModelName;
    SyncInfo info;
    boolean isIdentify = false, isBank = false;
    Uri identifyImgPath = null;
	Uri	bankImgPath = null;
	private int mMaxBananaCount = 0;
	private int mNowBananaCount = 0;

	private int mKey = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchargeview);
        
        btnBack = (ImageButton)findViewById(R.id.btn_cancel);
        btnConfrim = (Button)findViewById(R.id.btnExchargeOk);
        btnBack.setOnClickListener(mButtonClickListener);
        btnConfrim.setOnClickListener(mButtonClickListener);
        
        lbModelName = (TextView)findViewById(R.id.lb_model_nm);
		etBankNm = (EditText)findViewById(R.id.et_bankname);
		etAccountNum = (EditText)findViewById(R.id.et_accountnum);
		imgSinbun = (ImageView)findViewById(R.id.img_sinbun);
		imgTongjang = (ImageView)findViewById(R.id.img_tongjang);
		imgSinbun.setOnClickListener(mButtonClickListener);
		imgTongjang.setOnClickListener(mButtonClickListener);
		etBananaNum = (EditText) findViewById(R.id.lb_model_money);
		etBananaNum.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (etBananaNum.getText()==null || etBananaNum.getText().toString() == "" || etBananaNum.getText().toString().length() == 0) {
					etBananaNum.setHint(getString(R.string.warning_max_banana_count, mMaxBananaCount));
					return false;
				}
				if (Integer.valueOf(etBananaNum.getText().toString()) > mMaxBananaCount)
					etBananaNum.setText(String.valueOf(mMaxBananaCount));
				return false;
			}
		});
		etBananaNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//환산금액을 설정한다.
				int nowcnt = Integer.valueOf(etBananaNum.getText().toString());
				int srcPrice = nowcnt * SessionInstance.getInstance().getLoginData().getBjData().getPrice_per_banana();
				int decPrice = (int)(nowcnt * SessionInstance.getInstance().getLoginData().getBjData().getPrice_per_banana() * Constants.TAX);
				((TextView)findViewById(R.id.tv_model_money)).setText(getString(R.string.str_choco_excharge_price ,srcPrice - decPrice));
			}
		});
        
        info = new SyncInfo(this);
		Intent intent = getIntent();
		if(intent != null){
			lbModelName.setText(intent.getStringExtra("username"));
			etBananaNum.setText(String.valueOf(intent.getIntExtra("value", 0)));
			etBananaNum.setHint(getString(R.string.warning_max_banana_count, mMaxBananaCount));
			mMaxBananaCount = intent.getIntExtra("value", 0);
		}
	}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case Utils.REQUEST_PHOTO_ALBUM:
				identifyImgPath = data.getData();
            	Picasso.with(ExchargeViewActivity.this).load(identifyImgPath).into(imgSinbun);
                isIdentify = true;
            	break;
            case Utils.REQUEST_PHOTO_ALBUM1:
            	bankImgPath = data.getData();
            	Picasso.with(ExchargeViewActivity.this).load(bankImgPath).into(imgTongjang);
                isBank = true;
            	break;
        }
    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent0 = new Intent(ExchargeViewActivity.this, ProfileViewActivity.class);
		startActivity(intent0);
	}

	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	switch(v.getId()){
        	case R.id.btn_cancel:
				onBackPressed();
        		break;
        	case R.id.btnExchargeOk:
        		if(etBankNm.getText().toString().equals("")){
        			Toast.makeText(getBaseContext(), "은행명을 입력하십시오.", Toast.LENGTH_LONG).show();
        			return;
        		}
				if (etBananaNum.getText().toString().equals("")||Integer.valueOf(etBananaNum.getText().toString()) == 0){
					Toast.makeText(getBaseContext(), "환전할 바나나개수를 입력하십시오.", Toast.LENGTH_LONG).show();
					return;
				}else if(Integer.valueOf(etBananaNum.getText().toString()) > mMaxBananaCount){
					Toast.makeText(getBaseContext(), "환전할 바나나개수가 초과되였습니다.", Toast.LENGTH_LONG).show();
					return;
				}
        		if(etAccountNum.getText().toString().equals("")){
        			Toast.makeText(getBaseContext(), "구좌번호을 입력하십시오.", Toast.LENGTH_LONG).show();
        			return;
        		}
        		if(!isIdentify){
        			Toast.makeText(getBaseContext(), "신분이미지를 입력하십시오.", Toast.LENGTH_LONG).show();
        			return;
        		}

        		if(!isBank){
        			Toast.makeText(getBaseContext(), "통장이미지를 입력하십시오.", Toast.LENGTH_LONG).show();
        			return;
        		}
				mNowBananaCount = mMaxBananaCount - Integer.valueOf(etBananaNum.getText().toString());
				mKey = 0;
				new ExchargeRequestAsync().execute(etBankNm.getText().toString().trim(), etAccountNum.getText().toString(), etBananaNum.getText().toString());
        		break;
        	case R.id.img_sinbun:
        		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     			startActivityForResult(intent, Utils.REQUEST_PHOTO_ALBUM);
        		break;
        	case R.id.img_tongjang:	
        		Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     			startActivityForResult(intent1, Utils.REQUEST_PHOTO_ALBUM1);
        		break;
        	}
        }
    };

	class ExchargeRequestAsync extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.displayProgressDialog(ExchargeViewActivity.this);
		}
		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			String str1 = args[0];
			String str2 = args[1];
			String str3 = args[2];
			SuccessFailureResponse result = info.syncExcharge(str1, str2, str3);//먼저 정보들을 올린다.
			return result;
		}
		@Override
		protected void onPostExecute(final SuccessFailureResponse result) {
			super.onPostExecute(result);
			if (result != null) {
				if (result.isSuccess())
				{
					SessionInstance.getInstance().getLoginData().getBjData().setChocoCnt(mNowBananaCount);
					if (result.getResult() != null)//통장이메지가 있으면
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								mKey = 1;
								new ExchargeRequestAsync1().execute(result.getResult(), String.valueOf(mKey));
							}
						},10);
				}else
					mNowBananaCount = mMaxBananaCount;
			}
			Utils.disappearProgressDialog();
			//finish();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}

    class ExchargeRequestAsync1 extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayProgressDialog(ExchargeViewActivity.this);
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result = info.syncExcharge1(args[0], args[1], identifyImgPath);//다음 신분증이메지만 올린다.
            return result;
        }
        @Override
        protected void onPostExecute(final SuccessFailureResponse result) {
        	super.onPostExecute(result);
            if (result != null) {
				if (result.isSuccess())
				{
					SessionInstance.getInstance().getLoginData().getBjData().setChocoCnt(mNowBananaCount);
					if (result.getResult() != null)//통장이메지가 있으면
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								mKey = 2;
								new ExchargeRequestAsync2().execute(result.getResult(), String.valueOf(mKey));
							}
						},10);
				}else
					mNowBananaCount = mMaxBananaCount;
            }
            Utils.disappearProgressDialog();
            //finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

	class ExchargeRequestAsync2 extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.displayProgressDialog(ExchargeViewActivity.this);
		}
		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result = info.syncExcharge1(args[0], args[1], bankImgPath);//통장이메지올리기
			return result;
		}
		@Override
		protected void onPostExecute(SuccessFailureResponse result) {
			super.onPostExecute(result);
			Utils.disappearProgressDialog();

			if (result != null) {
				if (result.isSuccess())
					Toast.makeText(ExchargeViewActivity.this, result.getResult(),Toast.LENGTH_LONG).show();
			}
			Intent intent0 = new Intent(ExchargeViewActivity.this, ProfileViewActivity.class);
			startActivity(intent0);
			finish();
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}

}
