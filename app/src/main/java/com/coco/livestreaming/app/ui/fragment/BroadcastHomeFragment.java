package com.coco.livestreaming.app.ui.fragment;

import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.BroadcastActivity;
import com.coco.livestreaming.app.ui.fragment.data.BroadcastSettingData;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.squareup.picasso.Picasso;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.coco.livestreaming.CocotvingApplication.getContext;

public class BroadcastHomeFragment extends Fragment {

	public final String TAG = BroadcastHomeFragment.class.getName();
    
    ImageButton imgBtnClose;
    ImageButton btnStart;
    ImageView imgCover;
    Button imgBtnBroadCastSetting;
    EditText etBroadcastTitle;

    View rootView;

    BroadcastSettingData sendData;
	SyncInfo info;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_live_main, container, false);

    	imgBtnClose = (ImageButton)rootView.findViewById(R.id.imgBtnClose);
    	btnStart = (ImageButton)rootView.findViewById(R.id.btn_start);
    	imgCover = (ImageView)rootView.findViewById(R.id.imgCover);
    	imgBtnBroadCastSetting = (Button)rootView.findViewById(R.id.imgBtnBroadCastSetting);
    	etBroadcastTitle = (EditText)rootView.findViewById(R.id.etBroadCastTitle);
    	imgBtnClose.setOnClickListener(mButtonClickListener);
    	btnStart.setOnClickListener(mButtonClickListener);
    	imgBtnBroadCastSetting.setOnClickListener(mButtonClickListener);
		Picasso.with(getActivity())
				.load(Constants.IMG_MODEL_URL + SessionInstance.getInstance().getLoginData().getBjData().getUserid() + "/" + SessionInstance.getInstance().getLoginData().getBjData().getThumbnail())
				.placeholder(R.drawable.no_image)
				.into(imgCover);

		if (info == null)
			info = new SyncInfo(getContext());
		if(sendData == null)
			sendData = new BroadcastSettingData();

		Bundle data = getArguments();
		if(data != null){//태그설정화면으로 부터 넘어온경우
			sendData.setTitle(data.getString("title"));
			sendData.setTheme(data.getInt("theme"));
			sendData.setPw(data.getString("pw"));
			sendData.setEnterChoco(data.getInt("enter_choco"));
			sendData.setLimitNum(data.getInt("limit_num"));
			sendData.setVideoQuality(data.getInt("video_quality"));
			sendData.setAdult(data.getBoolean("blnAdult"));
		}

		ImageView img_bg = (ImageView) rootView.findViewById(R.id.img_bg);
		String userId = SessionInstance.getInstance().getLoginData().getBjData().getUserid();
		String thumb = SessionInstance.getInstance().getLoginData().getBjData().getThumbnail();
		Picasso.with(getActivity())
				.load(Constants.IMG_MODEL_URL + userId + "/" + thumb)
				.placeholder(R.drawable.no_image)
				.into(img_bg);

        return rootView;
    }
	@Override
	public void onResume()
	{
		super.onResume();
		etBroadcastTitle.setText(sendData.getTitle().trim());

	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	switch(v.getId()){
        	case R.id.imgBtnClose:
        		getActivity().finish();
        		break;
        	case R.id.btn_start:

        		if(etBroadcastTitle.getText().toString().equals("")){
        			Toast.makeText(getActivity(), getString(R.string.warning_title), Toast.LENGTH_LONG).show();
        			return;
        		}
        		sendData.setTitle(etBroadcastTitle.getText().toString().trim());
				toBroadCastLiveFragment(sendData);

        		break;
			case R.id.imgBtnBroadCastSetting:
				if(sendData == null){
					sendData = new BroadcastSettingData();
				}
				sendData.setTitle(etBroadcastTitle.getText().toString().trim());
				toBroadCastSettingFragment(sendData);
				break;
        	}
        }
    };
    
    public void toBroadCastSettingFragment(BroadcastSettingData data){
    	((BroadcastActivity)getActivity()).onToBroadFragment(Constants.BROADCAST_SETTING, data);
    }
    
    public void toBroadCastLiveFragment(BroadcastSettingData data){
    	((BroadcastActivity)getActivity()).onToBroadFragment(Constants.BROADCAST_LIVE, data);
    }
}
