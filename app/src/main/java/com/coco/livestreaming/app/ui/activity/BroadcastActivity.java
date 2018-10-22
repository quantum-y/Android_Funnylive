package com.coco.livestreaming.app.ui.activity;

import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.ui.fragment.BroadcastHomeFragment;
import com.coco.livestreaming.app.ui.fragment.BroadcastSelectFanFragment;
import com.coco.livestreaming.app.ui.fragment.BroadcastSettingFragment;
import com.coco.livestreaming.app.ui.fragment.data.BroadcastSettingData;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.util.PermissionUtils;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class BroadcastActivity extends FragmentActivity {

	public final String TAG = BroadcastActivity.class.getName();
    
    Fragment fragmentBroadHome;
    Fragment fragmentBroadSetting;
    Fragment fragmentBroadListener;
    Fragment fragmentBroadSelectFan;
    
    Fragment fragmentCurrent;
    
    static final int BACK_TAP_INTERVAL = 2000;
    long backPressedTime;

    BroadcastSettingData tempData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast);
		
		if (savedInstanceState == null) {
            fragmentBroadHome = new BroadcastHomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragmentBroadHome).commit();
            fragmentCurrent = fragmentBroadHome;
        }
		
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
    }
	
	public void onToBroadFragment(int id, BroadcastSettingData data) {
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.remove(fragmentCurrent);
        
        Bundle sendData = null;
        if(data != null){
        	sendData = new Bundle();
            sendData.putString("roomid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        	sendData.putString("title", data.getTitle());
            sendData.putInt("theme", data.getTheme());
            sendData.putInt("video_quality",data.getVideoQuality());
            sendData.putString("pw", data.getPw());
            sendData.putInt("enter_choco", data.getEnterChoco());
            sendData.putInt("limit_num", data.getLimitNum());
            sendData.putBoolean("blnAdult", data.getAdult());
        }
        
        switch (id) {
            case Constants.BROADCAST_HOME:
            	if (fragmentBroadHome == null) {
            		fragmentBroadHome = new BroadcastHomeFragment();
                }
                fragmentBroadHome.setArguments(sendData);
                fragmentCurrent = fragmentBroadHome;
            	break;
            case Constants.BROADCAST_SETTING:
            	if (fragmentBroadSetting == null) {
            		fragmentBroadSetting = new BroadcastSettingFragment();
                }
            	fragmentBroadSetting.setArguments(sendData);
                fragmentCurrent = fragmentBroadSetting;
                break;
            case Constants.BROADCAST_LIVE:

                if ( !checkPerm() ) {
                    tempData = data;
                    requestPerm();
                    return;
                } else {
                    gotoBroadcast(data);
                    return;
                }
//                Intent intent = new Intent(this, LiveVideoShowActivity.class);
//                intent.putExtra("roomid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
//                intent.putExtra("nickname", SessionInstance.getInstance().getLoginData().getBjData().getNickname());
//                intent.putExtra("title", data.getTitle());
//                intent.putExtra("theme", data.getTheme());
//                intent.putExtra("pw", data.getPw());
//                intent.putExtra("limit_num", data.getLimitNum());
//                intent.putExtra("video_quality", data.getVideoQuality());
//                intent.putExtra("enter_choco", data.getEnterChoco());
//                intent.putExtra("blnAdult", data.getAdult());
//                startActivity(intent);
            case Constants.BROADCAST_SELECT_FAN:
            	if (fragmentBroadSelectFan == null) {
            		fragmentBroadSelectFan = new BroadcastSelectFanFragment();
                }
            	fragmentBroadSelectFan.setArguments(sendData);
                fragmentCurrent = fragmentBroadSelectFan;
            	break;
            default:
                return;
        }
        transaction.replace(R.id.frameLayout, fragmentCurrent);
        transaction.commit();

    } // end of onButtonClick

    private void gotoBroadcast(BroadcastSettingData data) {
        Intent intent = new Intent(this, LiveVideoShowActivity.class);
        intent.putExtra("roomid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        intent.putExtra("nickname", SessionInstance.getInstance().getLoginData().getBjData().getNickname());
        intent.putExtra("title", data.getTitle());
        intent.putExtra("theme", data.getTheme());
        intent.putExtra("pw", data.getPw());
        intent.putExtra("limit_num", data.getLimitNum());
        intent.putExtra("video_quality", data.getVideoQuality());
        intent.putExtra("enter_choco", data.getEnterChoco());
        intent.putExtra("blnAdult", data.getAdult());
        startActivity(intent);
    }

    private boolean checkPerm() {
	    return (PermissionUtils.checkPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO));
    }

    private void requestPerm() {
        PermissionUtils.requestPermissions(this, 11, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ( requestCode == 11 ) {
            if ( checkPerm() ) {
                gotoBroadcast(tempData);
            } else {
                Toast.makeText(this, "Allowing camera and record audio permission is needed for broadcasting.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        /*if (backPressedTime + BACK_TAP_INTERVAL > System.currentTimeMillis()) {
            finish();
            return;
        } else {
            Toast.makeText(this, getResources().getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();*/
    }
    
}
