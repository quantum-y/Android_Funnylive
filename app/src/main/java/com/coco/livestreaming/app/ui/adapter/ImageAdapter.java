package com.coco.livestreaming.app.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.R;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
	private List<Object> 	imageUrlList;
	private List<ImageView> imageViewList;
	private OnImgClickListener listener;
	private OnImgLongClickListener longListener;
	public interface OnImgClickListener {
		public void onImgClick(int pos);
	}
	public void setOnImgClickListener(OnImgClickListener listener) {
		this.listener = listener;
	}
	public interface OnImgLongClickListener {
		public void onImgLongClick(View v, int pos);
	}
	public void setOnImgLongClickListener(OnImgLongClickListener listener) {
		this.longListener = listener;
	}

	public ImageAdapter(List<Object> imageUrlList){
		this.imageUrlList = imageUrlList.subList(1, imageUrlList.size());
		this.imageViewList = new ArrayList<ImageView>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageUrlList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imageUrlList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void setItem(int position, Object obj){
		imageUrlList.set(position, obj);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_image, parent, false);
        }
		Object item = imageUrlList.get(position);
		if (item.getClass().getSimpleName().contains(Uri.class.getSimpleName())) {
			Picasso.with(parent.getContext())
					.load((Uri) item)
					.placeholder(R.drawable.album_empty_default_img)
					.into((ImageView) convertView.findViewById(R.id.img_cell));
		}
		else {
			Picasso.with(parent.getContext())
					.load((String) item)
					.placeholder(R.drawable.album_empty_default_img)
					.into((ImageView) convertView.findViewById(R.id.img_cell));

		}

		((ImageView) convertView.findViewById(R.id.img_cell)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.onImgClick(position);
			}
		});
		((ImageView) convertView.findViewById(R.id.img_cell)).setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				v.buildDrawingCache();
				if (longListener != null)
					longListener.onImgLongClick(v, position);
				return false;
			}
		});
		return convertView;
	}
	
}
