package com.coco.livestreaming.app.ui.dialog;

import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SendChocoDialog extends Dialog {
	Context mContext;
	String mBj;
	EditText etChocoCnt;
//	Spinner spinnerChocoCnt;
    private int[]  arrChargeCounts;

	
	public interface OnSendClickListener {
        public void onSendClick(int Choco);
    }

	public interface OnChargeClickListener {
		public void onChargeClick();
	}
	OnSendClickListener listener;
	OnChargeClickListener chargeListener;

    public void setOnSendClickListener(OnSendClickListener listener) {
        this.listener = listener;
    }

	public void setOnChargeClickListener(OnChargeClickListener chargeListener) {
		this.chargeListener = chargeListener;
	}

	public SendChocoDialog(Context context, String strBj) {
		super(context);
		mContext = context;
		mBj = strBj;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_sendchoco, (ViewGroup) findViewById(R.id.dialog_sendchoco));
		setContentView(dlg);

		((TextView)findViewById(R.id.lb_choco_cnt)).setText(String.valueOf(SessionInstance.getInstance().getLoginData().getBjData().getChocoCnt()));
		((TextView)findViewById(R.id.lb_choco_to_who)).setText(mBj);

//		spinnerChocoCnt = (Spinner)findViewById(R.id.spinner_choco_send_cnt);
//        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.arr_str_egg_charge_count,  android.R.layout.simple_spinner_dropdown_item);
//        spinnerChocoCnt.setAdapter(mSpinnerAdapter);
		etChocoCnt = (EditText)findViewById(R.id.lb_choco_send_cnt);

		setCancelable(false);
		((Button)findViewById(R.id.btn_choco_cancel)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SendChocoDialog.this.cancel();
			}
		});

		((Button)findViewById(R.id.btn_charge)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Uri uri = Uri.parse(Constants.CHOCO_BUY_URL);
				//mContext.startActivity(new Intent(Intent.ACTION_VIEW,uri));
				if (chargeListener != null)
					chargeListener.onChargeClick();
			}
		});
		
		((Button)findViewById(R.id.btn_choco_ok)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String chocoCntStr = etChocoCnt.getText().toString();
				if (chocoCntStr.isEmpty())
					return;
                int chocoCnt = Integer.valueOf(etChocoCnt.getText().toString()); //Integer.valueOf(spinnerChocoCnt.getSelectedItem().toString());
				if(chocoCnt < 1){
					Toast.makeText(mContext, mContext.getString(R.string.dialog_choco_cnt_zero), Toast.LENGTH_LONG).show();
					return;
				}else if(chocoCnt> SessionInstance.getInstance().getLoginData().getBjData().getChocoCnt()){
					Toast.makeText(mContext, mContext.getString(R.string.dialog_choco_cnt_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (listener != null)
                    listener.onSendClick(chocoCnt);
			}
		});
	}
}