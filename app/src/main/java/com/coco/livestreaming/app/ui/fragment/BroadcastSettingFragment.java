package com.coco.livestreaming.app.ui.fragment;


import com.coco.livestreaming.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.ui.activity.BroadcastActivity;
import com.coco.livestreaming.app.ui.activity.ProfileViewActivity;
import com.coco.livestreaming.app.ui.fragment.data.BroadcastSettingData;
import com.coco.livestreaming.app.util.Constants;
import com.squareup.picasso.Picasso;

public class BroadcastSettingFragment extends Fragment {

	public final String TAG = BroadcastSettingFragment.class.getName();
    
	ImageButton btnBack, btnArrow;
	ImageView img_bg;
	Button btnConfrim;
    View rootView;
    EditText etPw, etPwRepeat, etChocoCnt, etMemberNum;
  	ToggleButton btnMusic, btnGame, btnLife;
  	ToggleButton btnHigh, btnMid, btnLow;
  	ToggleButton btnAdult;
  	
    boolean blnAdult;
    int selectedTheme;
    int selectedQuality;

    BroadcastSettingData sendData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_broadcastsetting, container, false);
        
    	btnConfrim = (Button)rootView.findViewById(R.id.btnSettingConfrim);
    	btnBack = (ImageButton)rootView.findViewById(R.id.btnSettingBack);
    	
    	btnConfrim.setOnClickListener(mButtonClickListener);
    	btnBack.setOnClickListener(mButtonClickListener);
    	
    	//cgh_edit
    	btnMusic = (ToggleButton)rootView.findViewById(R.id.btnMusic);
    	btnGame = (ToggleButton)rootView.findViewById(R.id.btnGame);
    	btnLife = (ToggleButton)rootView.findViewById(R.id.btnLife);
    	
    	btnHigh = (ToggleButton)rootView.findViewById(R.id.btnHigh);
    	btnMid = (ToggleButton)rootView.findViewById(R.id.btnMid);
    	btnLow = (ToggleButton)rootView.findViewById(R.id.btnLow);
    	
    	btnAdult = (ToggleButton)rootView.findViewById(R.id.btnAdult);
    	btnArrow = (ImageButton)rootView.findViewById(R.id.btnArrow);
    	
    	btnMusic.setOnClickListener(mButtonClickListener);
    	btnGame.setOnClickListener(mButtonClickListener);
    	btnLife.setOnClickListener(mButtonClickListener);
    	btnHigh.setOnClickListener(mButtonClickListener);
    	btnMid.setOnClickListener(mButtonClickListener);
    	btnLow.setOnClickListener(mButtonClickListener);
    	btnAdult.setOnClickListener(mButtonClickListener);
    	btnArrow.setOnClickListener(mButtonClickListener);
    	
		etPw = (EditText)rootView.findViewById(R.id.edit_password);
		etPwRepeat = (EditText)rootView.findViewById(R.id.edit_password_repeat);
		etChocoCnt = (EditText)rootView.findViewById(R.id.et_setting_choco_cnt);
		etMemberNum = (EditText)rootView.findViewById(R.id.et_setting_member_num);
		
		if (sendData == null)
			sendData = new BroadcastSettingData();
		
		Bundle data = getArguments();
		if (data != null) {
			sendData.setTitle(data.getString("title"));
			sendData.setTheme(data.getInt("theme"));
			sendData.setPw(data.getString("pw"));
			sendData.setEnterChoco(data.getInt("enter_choco"));
			sendData.setLimitNum(data.getInt("limit_num"));
			sendData.setVideoQuality(data.getInt("video_quality"));
			sendData.setAdult(data.getBoolean("blnAdult"));
		}
		
		setThemeValue(sendData.getTheme());
		setQualityValue(sendData.getVideoQuality());
		
		if (sendData.getPw() != null) {
			etPw.setText(sendData.getPw());
			etPwRepeat.setText(sendData.getPw());
		}
		
		etChocoCnt.setText(String.valueOf(sendData.getEnterChoco()));
		etMemberNum.setText(String.valueOf(sendData.getLimitNum()));
		
		btnAdult.setChecked(sendData.getAdult());
		blnAdult = sendData.getAdult();

		img_bg = (ImageView) rootView.findViewById(R.id.img_bg);
		String userId = SessionInstance.getInstance().getLoginData().getBjData().getUserid();
		String thumb = SessionInstance.getInstance().getLoginData().getBjData().getThumbnail();
		Picasso.with(getActivity())
				.load(Constants.IMG_MODEL_URL + userId + "/" + thumb)
				.placeholder(R.drawable.no_image)
				.into(img_bg);
		return rootView;
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
	@Override
	public void onResume()
	{
		super.onResume();
		//txtTitle.setText(getResources().getString(R.string.setting_title) + ":" + mTitle);
	}
	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	switch(v.getId()){
        	case R.id.btnMusic:
        		setThemeValue(Constants.VIDEO_THEME_MUSIC);
        		break;
        	case R.id.btnGame:
        		setThemeValue(Constants.VIDEO_THEME_GAME);
        		break;
        	case R.id.btnLife:
        		setThemeValue(Constants.VIDEO_THEME_LIFE);
        		break;
        	case R.id.btnHigh:
        		setQualityValue(Constants.VIDEO_QUALITY_HIGH);
        		break;
        	case R.id.btnMid:
        		setQualityValue(Constants.VIDEO_QUALITY_STANDARD);
        		break;
        	case R.id.btnLow:
        		setQualityValue(Constants.VIDEO_QUALITY_LOW);
        		break;
        	case R.id.btnAdult:
        		if (blnAdult){
        			btnAdult.setChecked(false);
        			blnAdult = false;
        		} else {
        			btnAdult.setChecked(true);
        			blnAdult = true;
        		}
        		break;
        	case R.id.btnSettingConfrim:
        		if(!etPw.getText().toString().equals("") && !etPwRepeat.getText().toString().equals(etPw.getText().toString())){
        			Toast.makeText(getActivity(), getString(R.string.warning_pw_confirm), Toast.LENGTH_LONG).show();
        			return;
        		}
        		
        		if(etChocoCnt.getText().toString().isEmpty() || Integer.valueOf(etChocoCnt.getText().toString()) == 0){
					etChocoCnt.setText("0");
        		}
        		
        		if(etMemberNum.getText().toString().isEmpty() || Integer.valueOf(etMemberNum.getText().toString()) == 0){
					etMemberNum.setText("500");
        		}
        		
				sendData.setTheme(selectedTheme);
				sendData.setVideoQuality(selectedQuality);
				sendData.setPw(etPw.getText().toString());
				sendData.setEnterChoco(Integer.valueOf(etChocoCnt.getText().toString()));
				sendData.setLimitNum(Integer.valueOf(etMemberNum.getText().toString()));
				sendData.setAdult(blnAdult);
				
				toBroadMainFragment(sendData);
        		break;
        	case R.id.btnSettingBack:
        		toBroadMainFragment(sendData);
        		break;
        	case R.id.btnArrow:
        		toBroadSelectFanFragment(sendData);
        		break;
        	}
        }
    };
    
    //public void toBroadCastViewFragment(BroadcastSettingData data){
	//	((BroadcastActivity)getActivity()).onToBroadFragment(Constants.BROADCAST_LISTENER, data);
    //}
    
    public void toBroadMainFragment(BroadcastSettingData data){
    	((BroadcastActivity)getActivity()).onToBroadFragment(Constants.BROADCAST_HOME, data);
    }

    public void toBroadSelectFanFragment(BroadcastSettingData data){
    	((BroadcastActivity)getActivity()).onToBroadFragment(Constants.BROADCAST_SELECT_FAN, data);
    }
    
    public void setThemeValue(int nThemeValue){
    	
    	btnMusic.setChecked(false);
		btnGame.setChecked(false);
		btnLife.setChecked(false);
		
    	if(nThemeValue == Constants.VIDEO_THEME_MUSIC){
    		btnMusic.setChecked(true);
    		selectedTheme = Constants.VIDEO_THEME_MUSIC;
    	} else if (nThemeValue == Constants.VIDEO_THEME_GAME) {
    		btnGame.setChecked(true);
    		selectedTheme = Constants.VIDEO_THEME_GAME;
		} else if (nThemeValue == Constants.VIDEO_THEME_LIFE) {
    		btnLife.setChecked(true);
    		selectedTheme = Constants.VIDEO_THEME_LIFE;
		}
    }
    
    public void setQualityValue(int nQualityValue){
    	
    	btnHigh.setChecked(false);
		btnMid.setChecked(false);
		btnLow.setChecked(false);
		
    	if(nQualityValue == Constants.VIDEO_QUALITY_HIGH){
    		btnHigh.setChecked(true);
    		selectedQuality = Constants.VIDEO_QUALITY_HIGH;
    	} else if (nQualityValue == Constants.VIDEO_QUALITY_STANDARD) {
    		btnMid.setChecked(true);
    		selectedQuality = Constants.VIDEO_QUALITY_STANDARD;
		} else if (nQualityValue == Constants.VIDEO_QUALITY_LOW) {
			btnLow.setChecked(true);
			selectedQuality = Constants.VIDEO_QUALITY_LOW;
		}
    }
}
