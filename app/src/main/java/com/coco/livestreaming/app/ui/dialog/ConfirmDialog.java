package com.coco.livestreaming.app.ui.dialog;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.ui.activity.ProfileViewInfoEditActivity;
import com.coco.livestreaming.app.util.Constants;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ConfirmDialog extends Dialog {
	Context mContext;
	String 	mUserid;
	String mFirstImage;
	OnConfirmClickListener mListener;

	public ConfirmDialog(Context context, String str, String img) {
		super(context);
		mContext = context;
		mUserid = str;
		mFirstImage = img;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_confirm, (ViewGroup) findViewById(R.id.dialog_confirm));
		setContentView(dlg);
		setCancelable(false);
		((TextView)findViewById(R.id.txt_confrim_content_id)).setText(mUserid + "님이 게스트로 초대하였습니다.\n수락하시겠습니까?");

		findViewById(R.id.btn_confirm_yes_id).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onConfirmClick(true);
			}
		});
		findViewById(R.id.btn_confirm_no_id).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mListener.onConfirmClick(false);
			}
		});
	}
	public interface  OnConfirmClickListener {
		public void onConfirmClick(boolean isYes);
	}
	public void setOnConfirmClickListener(OnConfirmClickListener listener) {
		mListener = listener;
	}
}