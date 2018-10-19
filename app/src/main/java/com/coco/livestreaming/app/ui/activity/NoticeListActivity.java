package com.coco.livestreaming.app.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.app.server.response.NoticeItemResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.adapter.NoticeListAdapter;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoticeListActivity extends Activity {

	public final String TAG = NoticeListActivity.class.getName();
    
	ImageButton btnBack;
    TextView lbNoDatas;
    View rootView;
    RelativeLayout rl_search;
    ListView noticeList;
    List<NoticeItemResponse> arrList;
    NoticeListAdapter listAdapter;
    SyncInfo info;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticelist);
        
        arrList = new ArrayList<NoticeItemResponse>();
       // noticeList =  (ListView)findViewById(R.id.contentList);
        btnBack = (ImageButton)findViewById(R.id.imgBtn_Back);
        //lbNoDatas = (TextView)findViewById(R.id.lbNoticeListNoDatas);
        
        btnBack.setOnClickListener(mButtonClickListener);
        info = new SyncInfo(this);
		
        new NoticeListAsync().execute("", "");
    }

	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	switch(v.getId()){
        	case R.id.imgBtn_Back:
        		finish();
        		break;
        	}
        }
    };
    
    class NoticeListAsync extends AsyncTask<String, String, List<NoticeItemResponse>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayProgressDialog(NoticeListActivity.this);
        }

        @Override
        protected List<NoticeItemResponse> doInBackground(String... args) {
        	List<NoticeItemResponse> result = info.syncNoticeList();
            return result;
        }

        @Override
        protected void onPostExecute(List<NoticeItemResponse> result) {
        	super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
            	arrList = result;
            	
            	listAdapter = new NoticeListAdapter(getBaseContext(), arrList);
            	noticeList.setAdapter(listAdapter);
            	
            	lbNoDatas.setVisibility(View.GONE);
            	noticeList.setVisibility(View.VISIBLE);
            } else {
            	lbNoDatas.setVisibility(View.VISIBLE);
            	noticeList.setVisibility(View.GONE);
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
