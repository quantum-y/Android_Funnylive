package com.coco.livestreaming.app.ui.fragment;


import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.coco.livestreaming.app.server.response.FanItemResponse;
import com.coco.livestreaming.app.server.response.FriendItemResponse;
import com.coco.livestreaming.app.server.response.FriendListResponse;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.BroadcastActivity;
import com.coco.livestreaming.app.ui.activity.ProfileViewInfoActivity;
import com.coco.livestreaming.app.ui.adapter.GalleryAdapter;
import com.coco.livestreaming.app.ui.adapter.NowPlayingListAdapter;
import com.coco.livestreaming.app.ui.fragment.PlayingListMainFragment.PlayingListAsync;
import com.coco.livestreaming.app.ui.fragment.data.BroadcastSettingData;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.squareup.picasso.Picasso;

public class BroadcastSelectFanFragment extends Fragment {

	public final String TAG = BroadcastSelectFanFragment.class.getName();
    
	View rootView;
	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	TextView tvNoDatas;
	public TextView btnAddPhoto;

	private ImageLoader imageLoader;

    BroadcastSettingData sendData;

    SyncInfo info;
    List<FriendItemResponse> friendList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	friendList = new ArrayList<FriendItemResponse>();
    	
    	rootView = inflater.inflate(R.layout.gallery, container, false);
        
    	gridGallery = (GridView) rootView.findViewById(R.id.gridGallery);
		//gridGallery.setFastScrollEnabled(true);
    	//PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, true, true);
        tvNoDatas = (TextView)rootView.findViewById(R.id.lbNoDatas);
        ((ImageButton)rootView.findViewById(R.id.btnSettingBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionInstance.getInstance().setFriendList(friendList);
                ((BroadcastActivity)getActivity()).onToBroadFragment(Constants.BROADCAST_SETTING, sendData);
            }
        });
		//btnAddPhoto = (TextView) rootView.findViewById(R.id.btn_add_photo);
		//btnAddPhoto.setOnClickListener(addPhotoListener);
		
		//btnAddPhoto.setVisibility(View.VISIBLE);
		info = new SyncInfo(getActivity());
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
		new BroadcastSelectFanAsync().execute();
	}
	
	class BroadcastSelectFanAsync extends AsyncTask<String, String, FriendListResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayProgressDialog(getActivity());
        }
        @Override
        protected FriendListResponse doInBackground(String... args) {
        	FriendListResponse result = info.syncFriendList();
            return result;
        }
        @Override
        protected void onPostExecute(FriendListResponse result) {
            super.onPostExecute(result);
            if (result != null) {
                friendList = result.getFriend_list();
                List<FriendItemResponse> sessionFriendList = SessionInstance.getInstance().getFriendList();
                if (sessionFriendList != null && !sessionFriendList.isEmpty()){
                    for (FriendItemResponse itemSession: sessionFriendList){
                        for ( FriendItemResponse itemNew : friendList){
                            if (itemNew.getFreind_id().equals(itemSession.getFreind_id())){
                                if (itemSession.isSeleted) {
                                    itemNew.isSeleted = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            	adapter = new GalleryAdapter(getActivity().getBaseContext(), friendList, imageLoader);
        		//gridGallery.setOnScrollListener(listener);
        		adapter.setMultiplePick(true);			
        		gridGallery.setAdapter(adapter);
        		tvNoDatas.setVisibility(View.GONE);
        		gridGallery.setVisibility(View.VISIBLE);

            } else {
                tvNoDatas.setVisibility(View.VISIBLE);
            	gridGallery.setVisibility(View.GONE);
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
