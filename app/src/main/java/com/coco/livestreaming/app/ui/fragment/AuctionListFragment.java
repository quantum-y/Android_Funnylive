package com.coco.livestreaming.app.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.AuctionItemListResponse;
import com.coco.livestreaming.app.server.response.AuctionItemResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.AuctionDetailActivity;
import com.coco.livestreaming.app.ui.activity.ExploreDetailActivity;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.ui.view.CircleImageView;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AuctionListFragment extends Fragment {

	public final String TAG = AuctionListFragment.class.getName();

    String selectedModelid="";
	int mTheme = 0;
    View rootView;
    
    ListView auctionList;
    List<AuctionItemResponse> arrList;
    AuctionItemListAdapter listAdapter;
	SyncInfo info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrList = new ArrayList<AuctionItemResponse>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_auctionlist, container, false);
        auctionList =  (ListView)rootView.findViewById(R.id.lv_auctionList);

        info = new SyncInfo(getActivity());

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new AuctionListAsync().execute();
    }

    class AuctionListAsync extends AsyncTask<String, String, AuctionItemListResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected AuctionItemListResponse doInBackground(String... args) {
            AuctionItemListResponse result = info.syncAuctionListData();
            return result;
        }

        @Override
        protected void onPostExecute(AuctionItemListResponse result) {
        	super.onPostExecute(result);

            if (result != null && result.isSuccess()) {
                arrList.clear();
                arrList.addAll(result.getList());

            	listAdapter = new AuctionItemListAdapter(getActivity(), arrList);
            	listAdapter.setOnDetailClickListener(detailListener);
            	auctionList.setAdapter(listAdapter);
            }

			Utils.disappearProgressDialog();
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					((CocotvingApplication)getActivity().getApplication()).mIsVisibleFlag = true;
				}
			},10);
        }
        
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    public interface OnDetailClickListener {
        public void onDetailClick(int pos);
    }

    private OnDetailClickListener detailListener = new OnDetailClickListener() {
        @Override
        public void onDetailClick(int pos) {
            AuctionItemResponse item = (AuctionItemResponse)arrList.get(pos);
            Intent intent = new Intent(getActivity(), AuctionDetailActivity.class);
            intent.putExtra("auctionId", item.getAuctionId());
            startActivity(intent);
        }
    };

    public class AuctionItemListAdapter extends BaseAdapter {
        public final String TAG = AuctionItemListAdapter.class.getName();

        Context mContext;
        List<AuctionItemResponse> listBoxs;
        OnDetailClickListener detailListener;

        public void setOnDetailClickListener(OnDetailClickListener listener) {
            this.detailListener = listener;
        }

        public AuctionItemListAdapter(Context context,List<AuctionItemResponse> list) {
            listBoxs = list;
            mContext = context;
        }

        @Override
        public int getCount() {
            return listBoxs.size();
        }

        @Override
        public AuctionItemResponse getItem(int position) {
            return listBoxs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auction, parent, false);
            }
            final AuctionItemResponse data = (AuctionItemResponse) listBoxs.get(position);
            ImageView userimgView = (ImageView) convertView.findViewById(R.id.img_user_photo);
            Picasso.with(getActivity())
                    .load(Constants.IMG_MODEL_URL + data.getUserId() + "/" + data.getUserAvatar())
                    .placeholder(R.drawable.no_image)
                    .into(userimgView);

            ((TextView)convertView.findViewById(R.id.txt_user_id)).setText(data.getNickname());
            ((TextView)convertView.findViewById(R.id.txt_time_left)).setText(getTimeString(data.getLeftSecond()));
            String userInfo = "";
            int sex = data.getSex();
            if (sex == 1)
                userInfo += "남 ";
            else if (sex == 2)
                userInfo += "여 ";
            userInfo += String.valueOf(data.getAge()) + "세";
            ((TextView)convertView.findViewById(R.id.txt_seller_info)).setText(userInfo);
            ((TextView)convertView.findViewById(R.id.tx_descript)).setText(data.getDescript());
            ((TextView)convertView.findViewById(R.id.txt_view_count)).setText(String.valueOf(data.getShowCount()));
            ((TextView)convertView.findViewById(R.id.txt_like_count)).setText(String.valueOf(data.getLikeCount()));
            ((TextView)convertView.findViewById(R.id.txt_comment_count)).setText(String.valueOf(data.getCommentCount()));

            int imgCount = 1;
            if (data.getSecond() == 2)
                imgCount = 2;
            if (data.getThird() == 3)
                imgCount = 3;
            if (data.getFourth() == 4)
                imgCount = 4;
            if (data.getFifth() == 5)
                imgCount = 5;

            ((LinearLayout)convertView.findViewById(R.id.layout_img)).removeAllViews();

            for (int i = 0; i < imgCount; i++) {
                RelativeLayout item = (RelativeLayout) View.inflate(getActivity(), R.layout.item_auction_photo, null);
                ImageView imgView = (ImageView) item.findViewById(R.id.imgPhoto);
                Picasso.with(getActivity())
                        .load(Constants.IMG_AUCTION_URL + data.getAuctionId()+"/"+String.valueOf(i+1)+".jpg")
                        .placeholder(R.drawable.no_image)
                        .into(imgView);
                ((LinearLayout)convertView.findViewById(R.id.layout_img)).addView(item);
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailListener.onDetailClick(position);
                    }
                });
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailListener.onDetailClick(position);
                }
            });
            return convertView;
        }
    }

    private String getTimeString(int second) {
        int hour = second / 3600;
        int min = (second - hour * 3600) / 60;
        int sec = second - hour * 3600 - min * 60;
        String strTime = String.format("%02d:%02d:%02d", hour, min, sec);
        return strTime;
    }
}
