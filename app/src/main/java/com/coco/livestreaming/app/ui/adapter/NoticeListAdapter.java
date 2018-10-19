package com.coco.livestreaming.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coco.livestreaming.app.server.response.NoticeItemResponse;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoticeListAdapter extends BaseAdapter {

    public final String TAG = NoticeListAdapter.class.getName();

    List<NoticeItemResponse> listBoxs;
    Context mContext;
    
    public NoticeListAdapter(Context context,List<NoticeItemResponse> list) {
    	listBoxs = list;
    	mContext = context;
    }

    @Override
    public int getCount() {
        return listBoxs.size();
    }

    @Override
    public NoticeItemResponse getItem(int position) {
        return listBoxs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        }
        final NoticeItemResponse data = (NoticeItemResponse) listBoxs.get(position);
        ((TextView) convertView.findViewById(R.id.lbNoticeAuthor)).setText(data.getAuthor());
        ((TextView) convertView.findViewById(R.id.lbNoticeTitle)).setText(data.getTitle());
        ((TextView) convertView.findViewById(R.id.lbNoticeContent)).setText(data.getContent());
        ((ImageView) convertView.findViewById(R.id.imgPhoto)).setImageResource(R.drawable.no_image);
        
        Picasso.with(parent.getContext())
        		.load(Constants.IMG_NOTICE_URL + data.getFilename())
        		.placeholder(R.drawable.no_image)
        		.into((ImageView) convertView.findViewById(R.id.imgPhoto));
       
        return convertView;
    }

}
