package com.coco.livestreaming.app.ui.fragment;

import com.coco.livestreaming.app.ui.activity.LoginActivity;
import com.coco.livestreaming.app.ui.activity.RegisterActivity;
import com.coco.livestreaming.app.ui.activity.SearchIDandPwdActivity;
import com.coco.livestreaming.app.ui.dialog.LoginDialog;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class LoginMainFragment extends Fragment {

	public final String TAG = LoginMainFragment.class.getName();
    
    private Button btnFaceBook, btnGoogle, btnTwiter, btnPhone, btnMore;
    private View 		rootView;
    private LoginDialog loginDlg;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView 	= inflater.inflate(R.layout.fragment_login_main, container, false);
    	btnFaceBook = (Button)rootView.findViewById(R.id.imgBtnFb);
    	btnGoogle 	= (Button)rootView.findViewById(R.id.imgBtnGg);
    	btnTwiter 	= (Button)rootView.findViewById(R.id.imgBtnTw);
		btnPhone 	= (Button)rootView.findViewById(R.id.imgBtnPhone);
    	btnMore 	= (Button)rootView.findViewById(R.id.btnLoginMore);

		btnFaceBook.setOnClickListener(mButtonClickListener);
		btnGoogle.setOnClickListener(mButtonClickListener);
		btnTwiter.setOnClickListener(mButtonClickListener);
		btnPhone.setOnClickListener(mButtonClickListener);
		btnMore.setOnClickListener(mButtonClickListener);

		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(getActivity());
		rootView.findViewById(R.id.lv_container).setOnTouchListener(activitySwipeDetector);

        return rootView;
    }

	public class ActivitySwipeDetector implements View.OnTouchListener {
		static final String logTag = "ActivitySwipeDetector";
		private Activity activity;
		static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;

		public ActivitySwipeDetector(Activity activity){
			this.activity = activity;
		}

		public void onRightSwipe(){
			Log.i(logTag, "RightToLeftSwipe!");
		}

		public void onLeftSwipe(){
			Log.i(logTag, "LeftToRightSwipe!");
		}

		public void onDownSwipe(){
			Log.i(logTag, "onTopToBottomSwipe!");
		}

		public void onUpSwipe(){
			Log.i(logTag, "onBottomToTopSwipe!");
		}

		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN: {
					downX = event.getX();
					downY = event.getY();
					return true;
				}
				case MotionEvent.ACTION_UP: {
					upX = event.getX();
					upY = event.getY();

					float deltaX = downX - upX;
					float deltaY = downY - upY;

					// swipe horizontal?
					if(Math.abs(deltaX) > Math.abs(deltaY))
					{
						if(Math.abs(deltaX) > MIN_DISTANCE){
							// left or right
							if(deltaX > 0) { this.onRightSwipe(); return true; }
							if(deltaX < 0) { this.onLeftSwipe(); return true; }
						}
						else {
							Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
							return false; // We don't consume the event
						}
					}
					// swipe vertical?
					else
					{
						if(Math.abs(deltaY) > MIN_DISTANCE){
							// top or down
							if(deltaY < 0) { this.onDownSwipe(); return true; }
							if(deltaY > 0) { this.onUpSwipe(); return true; }
						}
						else {
							Log.i(logTag, "Vertical Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
							return false; // We don't consume the event
						}
					}
					return true;
				}
			}
			return false;
		}
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
		public void onClick(View v)  {
			switch(v.getId()) {
				case R.id.imgBtnFb:
					break;
				case R.id.imgBtnGg:
					break;
				case R.id.imgBtnTw:
					break;
				case R.id.btnLoginMore:
					loginDlg = new LoginDialog(getActivity());
					loginDlg.setOnOkClickListener(okListener);
					loginDlg.setCancelable(false);
					loginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
					loginDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					try {
						loginDlg.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			}
		}
	};
    private LoginDialog.OnOkClickListener okListener = new LoginDialog.OnOkClickListener () {
        @Override
        public void onOkClick(View v) {
			switch (v.getId()){
				case R.id.btnLogin:
					toLoginFragment();
					break;
				case R.id.btnFindPwId:
					Intent data1 = new Intent(getActivity(), SearchIDandPwdActivity.class);
					startActivity(data1);
					break;
				case R.id.btnRegister:
					Intent data = new Intent(getActivity(), RegisterActivity.class);
					startActivity(data);
					break;
			}
            loginDlg.dismiss();
        }
    };
        
    public void toLoginFragment(){
    	LoginFragment loginFragment = new LoginFragment();
		LoginActivity activity = (LoginActivity)getActivity();
		activity.currentFragment = loginFragment;
		getActivity().getSupportFragmentManager()
			.beginTransaction()
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
			.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
			.addToBackStack(TAG)
			.replace(R.id.frameLoginMain, loginFragment)
			.commit();
    }
}
