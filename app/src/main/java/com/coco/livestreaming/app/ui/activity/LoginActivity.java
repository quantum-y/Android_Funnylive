package com.coco.livestreaming.app.ui.activity;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.util.GPSTracker;
import com.coco.livestreaming.app.ui.fragment.LoginMainFragment;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.util.Constants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity {

	public final String TAG = LoginActivity.class.getName();
    public static Fragment currentFragment;  //OnActivityResult()함수를 넘겨받기위한 fragment.

    Fragment fragmentLoginMain;
    
    static final int BACK_TAP_INTERVAL = 2000;
    long backPressedTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		if (savedInstanceState == null) {
            fragmentLoginMain = new LoginMainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frameLoginMain, fragmentLoginMain).commit();
            currentFragment = fragmentLoginMain;
        } 
		
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //GPS설정이 끝나면
        if (requestCode == Constants.GPS_SETTING) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.in_from_down, R.anim.out_to_up);
            ((CocotvingApplication)getApplication()).getGPSTracker().getLocation();//설정이 ok되였을 가능성때문에
            finish();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

	@Override
    public void onBackPressed() {
        if (currentFragment != fragmentLoginMain) {
            getSupportFragmentManager().popBackStack();
            currentFragment = fragmentLoginMain;
            return;
        } else if (backPressedTime + BACK_TAP_INTERVAL > System.currentTimeMillis()) {
            finish();
            return;
        } else {
            Toast.makeText(this, getResources().getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }
    public void onButtonClick(View v){

    }
	public void toMainActivity(){

        //위치보호가 되여있지 않으면 GPS 알람을 기동한다.
        if (SessionInstance.getInstance().getLoginData().getBjData().isProtect_location() == 0) {
            ((CocotvingApplication)getApplication()).getGPSTracker().getLocation();
            if (!((CocotvingApplication)getApplication()).getGPSTracker().canGetLocation()) {
                showGPSSettingsAlert();
                return;
            }
        }
		startActivity(new Intent(this, MainActivity.class));
		overridePendingTransition(R.anim.in_from_down, R.anim.out_to_up);
		finish();
	}

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showGPSSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS 세팅.");
        // Setting Dialog Message
        alertDialog.setMessage("GPS가 세팅되어 있지않아 앱의 일부기능을 리용하실수 없습니다. 설정하시겠습니까?");
        // On pressing Settings button
        alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, Constants.GPS_SETTING);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

}
