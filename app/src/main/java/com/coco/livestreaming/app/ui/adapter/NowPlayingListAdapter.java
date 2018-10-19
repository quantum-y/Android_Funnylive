package com.coco.livestreaming.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class NowPlayingListAdapter extends BaseAdapter {

    public final String TAG = NowPlayingListAdapter.class.getName();

    private List<PlayingItemResponse> listBoxs;
    private Context mContext;
    private int listType;
    private View mRootView;
    public interface OnImgClickListener {
        public void onImgClick(int pos);
    }

    OnImgClickListener listener;

    public void setOnImgClickListener(OnImgClickListener listener) {
        this.listener = listener;
    }

    public NowPlayingListAdapter(Context context,List<PlayingItemResponse> list, int listType) {
        listBoxs = list;
        mContext = context;
        this.listType = listType;
    }
    @Override
    public int getCount() {
        return listBoxs.size();
    }

    @Override
    public PlayingItemResponse getItem(int position) {
        return listBoxs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final PlayingItemResponse data = (PlayingItemResponse) listBoxs.get(position);
        if (convertView == null) {
            if (listType == Constants.PLAYING_LIST_NOW)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_now_playing, parent, false);
            else
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        }
        //온라인수설정
        ((TextView) convertView.findViewById(R.id.tvOnLine)).setText(String.valueOf(data.getOnlinemembers()));
        //방송자이름 설정
        ((TextView) convertView.findViewById(R.id.txt_username_id)).setText(data.getNickname());
        //방송자위치 설정
        ((TextView) convertView.findViewById(R.id.txt_location_id)).setText(data.getLocation());
        //방송형태설정
        if (data.getAdult() == 0)
            ((ImageView)convertView.findViewById(R.id.img_video_category)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_live));
        else if (data.getAdult() == 1)
            ((ImageView)convertView.findViewById(R.id.img_video_category)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_age_19));

        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);
        //imgPhoto.setMinimumHeight(parent.getHeight() / 2);
        if (listType == Constants.PLAYING_LIST_NOW) {
            ((TextView) convertView.findViewById(R.id.tvNowBroadTitle)).setText(data.getTitle());
            //((ImageView) convertView.findViewById(R.id.imgPhoto_over)).setMinimumHeight(parent.getWidth() / 2);
            /*Picasso.with(parent.getContext())
                    .load(Constants.IMG_MODEL_URL + data.getUserid() + File.separator + data.getFirst())
                    .placeholder(R.drawable.no_image)
                    .into((ImageView) convertView.findViewById(R.id.img_profile));*/
        }

        //String snapImg = data.getSnapImg();
        //String pass = data.getRoomPass();
        //String category = data.getCategory();
        //스냅이 없거나 비번방이거나 19금이면
        //if (snapImg == null || (pass != null && !pass.isEmpty()) || (category != null && category.equals("Adult")))
        //    snapImg = "thumbnail.jpg";

        Picasso.with(parent.getContext())
            .load(Constants.IMG_MODEL_URL + data.getUserid() + File.separator + data.getFirst())
            .placeholder(R.drawable.no_image)
            .into((ImageView) convertView.findViewById(R.id.imgPhoto));
        
        
        ((ImageView) convertView.findViewById(R.id.imgPhoto)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listener != null)
                    listener.onImgClick(position);
			}
		});

        return convertView;
    }

}
