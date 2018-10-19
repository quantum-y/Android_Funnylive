package com.coco.livestreaming.app.ui.dialog;

import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginDialog extends Dialog {
	Context mContext;
	
	//int formatFlag = 1; // 1: member, 2 : model
	
	Button btnLogin, btnRegister, btnCancel, btnFindPw;
	
	public interface OnOkClickListener {
        public void onOkClick(View v);
    }

    OnOkClickListener listener;

    public void setOnOkClickListener(OnOkClickListener listener) {
        this.listener = listener;
    }
	
	public LoginDialog(Context context) {
		super(context);
		mContext = context;
		//this.formatFlag = flag;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_login, (ViewGroup) findViewById(R.id.dialog_login));
		setContentView(dlg);
		
		setCancelable(false);
		((Button)findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LoginDialog.this.cancel();
			}
		});
		
		((Button)findViewById(R.id.btnRegister)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*Uri uri = Uri.parse(Constants.USER_REGISTER);
				mContext.startActivity(new Intent(Intent.ACTION_VIEW,uri));*/
				if (listener != null)
					listener.onOkClick(v);
			}
		});
		
		((Button)findViewById(R.id.btnFindPwId)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*Uri uri = Uri.parse(Constants.FIND_ID_PW);
				mContext.startActivity(new Intent(Intent.ACTION_VIEW,uri));*/
				if (listener != null)
					listener.onOkClick(v);
			}
		});
		
		((Button)findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listener != null)
                    listener.onOkClick(v);
			}
		});
		/*
		if(this.formatFlag == Constants.MEMBER_BJ){
			((Button)findViewById(R.id.btnLogin)).setText(mContext.getString(R.string.dialog_bj_login_title));
		} else if(this.formatFlag == Constants.MEMBER_MEM){
			((Button)findViewById(R.id.btnLogin)).setText(mContext.getString(R.string.dialog_member_login_title));
		}*/
	}
}