package com.coco.livestreaming.app.util;

import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.ui.widget.ProgressHUD;
import com.coco.livestreaming.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Utils {
	
	Context mContext;
	SharedPreferences cocoPreference;
	SharedPreferences.Editor editor;
	
	public static final int REQUEST_PHOTO_ALBUM = 1;
	public static final int REQUEST_CROP_IMAGE = 2;
	
	public static final int REQUEST_PHOTO_ALBUM1 = 3;
	public static final int REQUEST_CROP_IMAGE2 = 4;
	public static final int REQUEST_PHOTO_EDIT = 5;

	public static ProgressHUD mProgressHUD;
	
	public Utils(Context context) {
		this.mContext = context;
		
		cocoPreference = this.mContext.getSharedPreferences(this.mContext.getString(R.string.preference_cocotving), 0);
		editor = cocoPreference.edit();
	}

	public void savePrefInfo(String userid, String pw){
		this.editor.putString(this.mContext.getString(R.string.pref_userid), userid);
		this.editor.putString(this.mContext.getString(R.string.pref_password), pw);
		this.editor.commit();
	} // end of savePrefInfo()
	
	public String getUserid(){
		return this.cocoPreference.getString(this.mContext.getString(R.string.pref_userid), "xx");
	}
	
	public String getPassword(){
		return this.cocoPreference.getString(this.mContext.getString(R.string.pref_password), "xx");
	}
	public static void displayProgressDialog(Context context){
		try {
			mProgressHUD = ProgressHUD.show(context, context.getResources().getString(R.string.loading_text), true, true);
		}catch (Exception e){
			//Toast.makeText(context, "Progress Diaglog Error", Toast.LENGTH_SHORT).show();
		}
    }
    public static void disappearProgressDialog(){
		try {
			mProgressHUD.dismiss();
		}catch (Exception e){
			//Toast.makeText(mProgressHUD.getContext(), "Progress Diaglog Error", Toast.LENGTH_SHORT).show();
		}
    }
	
}
