package com.coco.livestreaming.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.coco.livestreaming.R;

public class PasswordDialog extends Dialog {
	private Context mContext;
	private int mType = 0;
	public interface OnOkClickListener {
        void onOkClick(String  pass);
    }

    private OnOkClickListener listener;

    public void setOnOkClickListener(OnOkClickListener listener) {
        this.listener = listener;
    }

	public PasswordDialog(Context context) {
		super(context);
		mContext = context;
		mType = 0;
	}

	public PasswordDialog(Context context, int type) {
		super(context);
		mContext = context;
		mType = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_password, (ViewGroup) findViewById(R.id.dialog_password));
		setContentView(dlg);
		setCancelable(false);
		if (mType == 1) {
			((TextView) findViewById(R.id.txt_password_title_id)).setText(R.string.dialog_request_code_title);
			((EditText)findViewById(R.id.editPassword)).setHint(R.string.request_code_input_hint);
			((EditText)findViewById(R.id.editPassword)).setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
		(findViewById(R.id.btn_select_cancel)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PasswordDialog.this.cancel();
			}
		});
		
		(findViewById(R.id.btn_select_ok)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String sPassword = ((EditText)findViewById(R.id.editPassword)).getText().toString();
				if (listener != null)
                    listener.onOkClick(sPassword);
			}
		});
	}
}