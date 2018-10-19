package com.coco.livestreaming.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coco.livestreaming.R;

public class CancelAuctionDialog extends Dialog {
	private Context mContext;
	private String strContent;
	private TextView txtTitle;

	private int typeFlag = 0; // 1 : member, 2 : bj

	public interface OnOkClickListener {
        void onOkClick(int flag);
    }

    private OnOkClickListener listener;

    public void setOnOkClickListener(OnOkClickListener listener) {
        this.listener = listener;
    }

	public CancelAuctionDialog(Context context, String str, int flag) {
		super(context);
		mContext = context;
		this.strContent = str;
		this.typeFlag = flag;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_cancel_auction, (ViewGroup) findViewById(R.id.dialog_select));
		setContentView(dlg);
		
		((TextView)findViewById(R.id.lb_select_content)).setText(this.strContent);
		txtTitle = (TextView)findViewById(R.id.txt_title);
		
		setCancelable(false);


		(findViewById(R.id.btn_select_cancel)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CancelAuctionDialog.this.cancel();
			}
		});
		
		(findViewById(R.id.btn_select_ok)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listener != null)
                    listener.onOkClick(CancelAuctionDialog.this.typeFlag);
			}
		});
	}

	public void setTitle(String title) {
		txtTitle.setText(title);
	}
}