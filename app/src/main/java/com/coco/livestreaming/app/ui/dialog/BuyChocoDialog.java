package com.coco.livestreaming.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.adapter.BananaChargeListAdapter;
import com.coco.livestreaming.app.util.Utils;

import java.util.ArrayList;

public class BuyChocoDialog extends Dialog {
	Context mContext;
	String mBj;
	private  ArrayList<BananaItem> m_listBananaItem;
	private ListView listBanana;
	private SyncInfo mWebServer;

	public BuyChocoDialog(Context context, String strBj) {
		super(context);
		mContext = context;
		mBj = strBj;
		mWebServer = new SyncInfo(mContext);
	}

	private void initBananaCharge(){

		m_listBananaItem = new ArrayList<BananaItem>();
		listBanana = (ListView) findViewById(R.id.list_banana);

		int[]  arrChargeDrawables = {R.drawable.ic_buy_egg_1, R.drawable.ic_buy_egg_10, R.drawable.ic_buy_egg_100, R.drawable.ic_buy_egg_1k, R.drawable.ic_buy_egg_10k};
		int[]  arrChargeCounts = mContext.getResources().getIntArray(R.array.arr_egg_charge_count);
		int[]  arrChargePrices = mContext.getResources().getIntArray(R.array.arr_egg_charge_price);

		for (int i = 0; i < 5; i++) {
			String strChargeCount = mContext.getResources().getString(R.string.str_choco_charge_count, arrChargeCounts[i]);
			String arrChargePrice = mContext.getResources().getString(R.string.str_choco_charge_price, arrChargePrices[i]);
			m_listBananaItem.add(new BananaItem(arrChargeDrawables[i], strChargeCount, arrChargePrice));
		}

		BananaChargeListAdapter listAdapter = new BananaChargeListAdapter(mContext, R.layout.item_choco_charge, m_listBananaItem);
		listBanana.setAdapter(listAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_buychoco, (ViewGroup) findViewById(R.id.dialog_buychoco));
		setContentView(dlg);

		setCancelable(false);
		((Button)findViewById(R.id.btn_choco_cancel)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BuyChocoDialog.this.cancel();
			}
		});

		((Button)findViewById(R.id.btn_charge)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						int[]  arrChargeCounts = mContext.getResources().getIntArray(R.array.arr_egg_charge_count);
						int[]  arrChargePrices = mContext.getResources().getIntArray(R.array.arr_egg_charge_price);
						int position = 0;
						for (int i = 0; i < m_listBananaItem.size(); i++){
							if (m_listBananaItem.get(i).m_isSelected) {
								position = i;
								break;
							}
						}
						new BuyBananaAsync().execute(String.valueOf(arrChargeCounts[position]), String.valueOf(arrChargePrices[position]));
					}
				}, 10);
				BuyChocoDialog.this.dismiss();
			}
		});
		initBananaCharge();
	}
	public class BananaItem {
		public int m_nImage;
		public String m_strBananaCntName;
		public String m_strBananaPriceName;
		public boolean m_isSelected;
		BananaItem(int nImage, String strBananaCntName, String strBananaPriceName) {
			m_nImage = nImage;
			m_strBananaCntName = strBananaCntName;
			m_strBananaPriceName = strBananaPriceName;
			m_isSelected = false;
		}
	}

	//바나나충진
	class BuyBananaAsync extends AsyncTask<String, String, SuccessFailureResponse> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.displayProgressDialog(mContext);
		}
		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result = mWebServer.syncBuyBanana(args[0], args[1]);
			return result;
		}
		@Override
		protected void onPostExecute(SuccessFailureResponse result) {
			super.onPostExecute(result);
			if (result != null) {
				if (result.isSuccess()) {
					Toast.makeText(mContext, result.getResult(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext, result.getResult(), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(mContext, mContext.getString(R.string.operation_buy_failure), Toast.LENGTH_LONG).show();
			}
			Utils.disappearProgressDialog();
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}
}