package com.coco.livestreaming.app.ui.dialog;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.FanItemResponse;
import com.coco.livestreaming.app.server.response.ProfileInfoResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.ui.view.CircleImageView;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.squareup.picasso.Picasso;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.sync.SyncInfo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

import io.socket.client.Socket;

public class ModelProfileViewDialog extends Dialog {
	LiveVideoShowActivity mActivity;
	Context mContext;
	String mUserID = "";
	OnInviteClickListener mInviteListener;
	OnChatClickListener mChatListener;
	public boolean mIsInviteView = true;
	public boolean mIsBottomToolBar = true;
	public boolean mIsForceOut = false;
	private SyncInfo 				info;
	private ProfileInfoResponse 	mMyProfileInfo;
	private List<FanItemResponse> 	mMyTopFanList;
	private List<CircleImageView> 	mTopFanList = new ArrayList<CircleImageView>();
	private int[]                 	mDefaultNoImageArr = new int[3];
	private Socket					mSocket;
	public ModelProfileViewDialog(Context context, LiveVideoShowActivity activity, String userid, Socket socket) {
		super(context);
		mContext = context;
		mActivity = activity;
		mUserID = userid;
		mSocket = socket;;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inf = LayoutInflater.from(mContext);
		View dlg = inf.inflate(R.layout.dialog_modelprofileview, (ViewGroup) findViewById(R.id.rl_modelprofileview));
		setContentView(dlg);
		setCancelable(true);
		findViewById(R.id.btn_profile_close_id).setOnClickListener(MainListener);
		if (!mIsBottomToolBar || SessionInstance.getInstance().getLoginData().getBjData().getIsAdmin() == 1)
			findViewById(R.id.lay_bottom_tool_bar).setVisibility(View.GONE);
		else  if (!mIsInviteView)
			findViewById(R.id.btn_invite_id).setVisibility(View.GONE);
		if (mIsForceOut)
			findViewById(R.id.btn_force_out_id).setVisibility(View.VISIBLE);

		findViewById(R.id.btn_chat_id).setOnClickListener(MainListener);
		findViewById(R.id.btn_invite_id).setOnClickListener(MainListener);
		findViewById(R.id.btn_follow_id).setOnClickListener(MainListener);
		findViewById(R.id.btn_unfollow_id).setOnClickListener(MainListener);
		findViewById(R.id.btn_force_out_id).setOnClickListener(MainListener);

        info = new SyncInfo(mContext);
        new ProfileViewAsync().execute(mUserID);
		mTopFanList.add((CircleImageView)findViewById(R.id.img_fan_first));
		mTopFanList.add((CircleImageView)findViewById(R.id.img_fan_second));
		mTopFanList.add((CircleImageView)findViewById(R.id.img_fan_third));
		mDefaultNoImageArr[0] = R.drawable.no_image;
		mDefaultNoImageArr[1] = R.drawable.no_image;
		mDefaultNoImageArr[2] = R.drawable.no_image;

	}
	private View.OnClickListener MainListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btn_profile_close_id:
					ModelProfileViewDialog.this.cancel();
					break;
				case R.id.btn_invite_id:
					mInviteListener.onInviteClick(mUserID, mMyProfileInfo.getNickname());
					break;
				case R.id.btn_follow_id:
					new FollowSetAsync().execute("follow");
					break;
				case R.id.btn_unfollow_id:
					new FollowSetAsync().execute("unfollow");
					break;
				case R.id.btn_chat_id:
					mChatListener.onChatClick(mUserID);
					break;
				case R.id.btn_force_out_id:
					new FollowSetAsync().execute("force_out");
					break;
			}
		}
	};
	public interface OnInviteClickListener {
		public void onInviteClick(String userid, String nickname);
	}
	public void setOnInviteClickListener(OnInviteClickListener listener) {
		this.mInviteListener = listener;
	}

	public interface OnChatClickListener {
		public void onChatClick(String userid);
	}
	public void setOnChatClickListener(OnChatClickListener listener) {
		this.mChatListener = listener;
	}

	class ProfileViewAsync extends AsyncTask<String, String, ProfileViewResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (((CocotvingApplication)mActivity.getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(mContext);
				((CocotvingApplication)mActivity.getApplication()).mIsVisibleFlag = false;
			}
		}
		@Override
		protected ProfileViewResponse doInBackground(String... args) {
			ProfileViewResponse result = info.syncProfileView(args[0]);
			return result;
		}
		@Override
		protected void onPostExecute(ProfileViewResponse result) {
			super.onPostExecute(result);
			if (result != null) {
				mActivity.mIsConnectedWeb = true;
				mMyProfileInfo = result.getProfileInfo();
				mMyTopFanList = result.getTopFanList();
				Picasso.with(mContext)
						.load(Constants.IMG_MODEL_URL + mMyProfileInfo.getUserid() + File.separator + mMyProfileInfo.getFirst())
						.placeholder(R.drawable.no_image)
						.into((ImageView)findViewById(R.id.imgModelProfilePhoto));

				((TextView)findViewById(R.id.txt_profile_username_id)).setText(String.valueOf(mMyProfileInfo.getUsername()));
				((TextView)findViewById(R.id.txt_profile_userid_id)).setText(String.valueOf(mMyProfileInfo.getNickname()));

				//레벨에 따른 색갈정보를 얻어 라운드이펙트하기
				int[] nLevelColors = mActivity.getResources().getIntArray(R.array.level_colors);
				Drawable levelDrawable = mActivity.getBaseContext().getResources().getDrawable(R.drawable.round_bg_level_icon);
				levelDrawable.setColorFilter(nLevelColors[mMyProfileInfo.getLevel_color()], PorterDuff.Mode.SRC_IN);
//				((TextView)findViewById(R.id.txt_level_id)).setBackground(levelDrawable);
				((TextView)findViewById(R.id.txt_level_id)).setText(mActivity.getString(R.string.str_level_prifix, mMyProfileInfo.getLevel_num()));
				if (mMyProfileInfo.getProtect_location() == 0)//위치보호기능이 안되여있으면 위치를 표시한다.
					((TextView)findViewById(R.id.txt_location_id)).setText(mMyProfileInfo.getLocation());
				//((TextView)findViewById(R.id.txt_relation_num_id)).setText(String.valueOf(mMyProfileInfo.getFanCnt() + mMyProfileInfo.getFollowCnt()));
				//((TextView)findViewById(R.id.txt_constellation_id)).setText(String.valueOf(mMyProfileInfo.getConstellation()));
				((TextView)findViewById(R.id.txt_send_bana_id)).setText(String.valueOf(mMyProfileInfo.getSend_choco_cnt()));
				if (mMyProfileInfo.getIntroduce() != null)
					((TextView)findViewById(R.id.txt_oneself_introduce_id)).setText(mMyProfileInfo.getIntroduce());
				if (mMyProfileInfo.getFollow_flag()){
					findViewById(R.id.btn_unfollow_id).setVisibility(View.VISIBLE);
					findViewById(R.id.btn_follow_id).setVisibility(View.GONE);
				}else{
					findViewById(R.id.btn_unfollow_id).setVisibility(View.GONE);
					findViewById(R.id.btn_follow_id).setVisibility(View.VISIBLE);
				}
				int i = 0;
				for (FanItemResponse item : mMyTopFanList){
					Picasso.with(mContext)
							.load(Constants.IMG_MODEL_URL + item.getFanID() + File.separator + item.getFirst())
							.placeholder(mDefaultNoImageArr[i])
							.into(mTopFanList.get(i));
					i++;
				}
			}else {
				Toast.makeText(mContext, mActivity.getString(R.string.operation_failure), Toast.LENGTH_SHORT).show();
				mActivity.mIsConnectedWeb = false;
			}
			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication)mActivity.getApplication()).mIsVisibleFlag = true;
				}
			},10);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}

	class FollowSetAsync extends AsyncTask<String, String, SuccessFailureResponse> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (((CocotvingApplication)mActivity.getApplication()).mIsVisibleFlag) {
				Utils.displayProgressDialog(mContext);
				((CocotvingApplication)mActivity.getApplication()).mIsVisibleFlag = false;
			}
		}
		@Override
		protected SuccessFailureResponse doInBackground(String... args) {
			SuccessFailureResponse result = info.syncFollowSet(mUserID, args[0]);
			return result;
		}
		@Override
		protected void onPostExecute(SuccessFailureResponse result) {
			super.onPostExecute(result);
			if (result != null) {
				mActivity.mIsConnectedWeb = true;
				if (result.isSuccess())
				{
					if (result.getResult().equals("follow")){
						findViewById(R.id.btn_unfollow_id).setVisibility(View.VISIBLE);
						findViewById(R.id.btn_follow_id).setVisibility(View.GONE);
					}else if (result.getResult().equals("unfollow")){
						findViewById(R.id.btn_follow_id).setVisibility(View.VISIBLE);
						findViewById(R.id.btn_unfollow_id).setVisibility(View.GONE);
					}else if(result.getResult().equals("force_out")){
//						mSocket.emit("new message", "Force Out", Constants.ALERT_MSG_SINGLE_FORCE_OUT, mUserID);
						mSocket.emit("new message", "Force Out" + "/" + Constants.ALERT_MSG_SINGLE_FORCE_OUT + "/" + mUserID);
						ModelProfileViewDialog.this.cancel();
					}
				}
			} else {
				Toast.makeText(mContext, mActivity.getString(R.string.operation_failure), Toast.LENGTH_SHORT).show();
				mActivity.mIsConnectedWeb = false;
			}
			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication)mActivity.getApplication()).mIsVisibleFlag = true;
				}
			},10);

		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Utils.disappearProgressDialog();
		}
	}
}