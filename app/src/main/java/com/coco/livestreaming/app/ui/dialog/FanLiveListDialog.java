package com.coco.livestreaming.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.ui.adapter.NowPlayingListAdapter;

public class FanLiveListDialog extends Dialog {
	private Context 	mContext;
	private ListView	mFanLiveList;
	private NowPlayingListAdapter	mAdapter;
	public FanLiveListDialog(Context context, NowPlayingListAdapter adapter) {
		super(context);
		mContext = context;
		mAdapter = adapter;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_favourite, (ViewGroup) findViewById(R.id.dialog_favourite));
		setContentView(dlg);
		setCancelable(false);
		findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				FanLiveListDialog.this.cancel();
			}
		});

		mFanLiveList = (ListView) findViewById(R.id.list_fan);
		mFanLiveList.setDividerHeight(0);
		mFanLiveList.setAdapter(mAdapter);

	}

}