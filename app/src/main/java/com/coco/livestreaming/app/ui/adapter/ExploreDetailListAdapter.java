package com.coco.livestreaming.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExploreDetailListAdapter extends BaseAdapter {

    public final String TAG = ExploreDetailListAdapter.class.getName();

    List<PlayingItemResponse> list;
    Context     mContext;
    String      mKey = "";
    public interface OnImgClickListener {
        public void onImgClick(int pos);
    }

    OnImgClickListener listener;

    public void setOnImgClickListener(OnImgClickListener listener) {
        this.listener = listener;
    }
    
    public ExploreDetailListAdapter(Context context,List<PlayingItemResponse> list, String key) {
    	this.list = list;
    	this.mContext = context;
        this.mKey = key;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PlayingItemResponse getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_detail, parent, false);
        }
        final PlayingItemResponse data = list.get(position);
        ImageView img = (ImageView) convertView.findViewById(R.id.imgOnLine);
//        if(data.getIsLastUpdate() == 1){
//            img.setImageResource(R.drawable.online_bg);
//        } else {
//            img.setImageResource(R.drawable.offline_bg);
//        }
        if(data.getIsLastUpdate() == 1){
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
        }
        //String snapImg = data.getSnapImg();
        //String pass = data.getRoomPass();
        //String category = data.getCategory();
        //스냅이 없거나 비번방이거나 19금이면
        //if (snapImg == null || (pass != null && !pass.isEmpty()) || (category != null && category.equals("Adult")))
        //    snapImg = "thumbnail.jpg";
        Picasso.with(parent.getContext())
		.load(Constants.IMG_MODEL_URL + data.getUserid() + "/" + data.getFirst())
		.placeholder(R.drawable.no_image)
		.into((ImageView) convertView.findViewById(R.id.imgModel));
        ((ImageView) convertView.findViewById(R.id.imgModel)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(listener != null)
					listener.onImgClick(position);
			}
		});
        String strInfo = "";
        if (mKey.equals("NEW")) {
            if (Integer.valueOf(data.getHour_diff()) != 0)
                strInfo = data.getHour_diff() + "hours";
            else if (Integer.valueOf(data.getMin_diff()) != 0)
                strInfo = data.getMin_diff() + "mins";
        }else if (mKey.equals("RECOM")||mKey.equals("BEST")) {
            if (data.getOnlinemembers() != 0)
                strInfo = String.valueOf(data.getOnlinemembers()) + "명";
        }else if (mKey.equals("NEAR")){
            if (data.getDistance() != 0)
                strInfo = String.valueOf(data.getDistance()) + "km";
        }
        ((TextView)convertView.findViewById(R.id.txt_info)).setText(strInfo);
        return convertView;
    }

}
