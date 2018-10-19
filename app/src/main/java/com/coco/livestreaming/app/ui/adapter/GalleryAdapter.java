package com.coco.livestreaming.app.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.FriendItemResponse;
import com.coco.livestreaming.app.util.Constants;

public class GalleryAdapter extends BaseAdapter {

	private List<FriendItemResponse> friendList;
	private LayoutInflater infalter;
	//private ArrayList<FriendGallery> data = new ArrayList<FriendGallery>();
	ImageLoader imageLoader;

	private boolean isActionMultiplePick;
	private List<FriendItemResponse> dataT;

	public GalleryAdapter(Context c, List<FriendItemResponse> friendList, ImageLoader imageLoader) {
		infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = imageLoader;
		this.friendList = friendList;
		clearCache();
	}

	@Override
	public int getCount() {
		return friendList == null ? 0 : friendList.size();
	}

	@Override
	public FriendItemResponse getItem(int position) {
		return friendList == null ? null : friendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		if(friendList == null)
			return;
		
		for (int i = 0; i < friendList.size(); i++) {
			friendList.get(i).isSeleted = selection;
		}
		
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < friendList.size(); i++) {
			if (!friendList.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < friendList.size(); i++) {
			if (friendList.get(i).isSeleted) {
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public List<FriendItemResponse> getSelected() {
		List<FriendItemResponse> dataT = new ArrayList<FriendItemResponse>();

		for (int i = 0; i < friendList.size(); i++) {
			if (friendList.get(i).isSeleted) {
				dataT.add(friendList.get(i));
			}
		}

		return dataT;
	}

	public void addAll(List<FriendItemResponse> files) {

		try {
			this.friendList.clear();
			this.friendList.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(ViewHolder holder, FriendItemResponse item) {
		if(item == null)
			return;
		
		item.isSeleted = ! item.isSeleted;
		holderSelected(holder, item.isSeleted);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		final ViewHolder holder;		
		if (view == null) {

			view = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) view.findViewById(R.id.imgQueue);
			holder.imgQueueMultiSelected = (ImageView) view.findViewById(R.id.imgQueueMultiSelected);
			holder.imgQueueMask = (ImageView) view.findViewById(R.id.imgQueueMask);
			
			if (isActionMultiplePick) {
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			} else {
				holder.imgQueueMultiSelected.setVisibility(View.GONE);				
			}

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		final FriendItemResponse item = friendList.get(position);
		if(item == null || holder == null)
			return view;		
		
		holder.imgQueue.setTag(position);
		if(isActionMultiplePick)
			holder.imgQueue.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					changeSelection(holder , item);
				}
			});
		
		holder.imgQueueMultiSelected.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				changeSelection(holder , item);
			}
		});
		((TextView)view.findViewById(R.id.txt_info)).setText(item.getFreind_id());

		try {
//			if(!act.isUsingLoader){
//				SettingUtil.setImage(null, holder.imgQueue, item.sdcardPath);
//			}else{
				
//			}
			//tempcode
			Picasso.with(parent.getContext())
    		.load(Constants.IMG_MODEL_URL + item.getFreind_id() + "/" + item.getFirst())
    		.placeholder(R.drawable.no_image)
    		.into(holder.imgQueue);

//			imageLoader.displayImage(Constants.IMG_MODEL_URL + item.getFreind_id() + "/thumbnail.jpg", holder.imgQueue, 
//				new SimpleImageLoadingListener(){
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						super.onLoadingStarted(imageUri, view);
//					}
//					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//						holder.imgQueue.setImageResource(R.drawable.no_image);
//						super.onLoadingFailed(imageUri, view, failReason);
//					}
//				});
//			
			if (isActionMultiplePick) {
				holderSelected(holder, item.isSeleted);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		ImageView imgQueueMask;
	}

	public void clearCache() {
		if(imageLoader !=null){
			imageLoader.clearDiskCache();
			imageLoader.clearMemoryCache();
		}
	}

	public void clear() {
		friendList.clear();
		notifyDataSetChanged();
	}
	
	public void holderSelected(ViewHolder holder, boolean selected){
		if(holder == null)
			return;
		
		holder.imgQueueMultiSelected.setSelected(selected);
		holder.imgQueueMask.setVisibility(selected ? View.VISIBLE : View.GONE);	
		int selectedCount = getSelected().size();		
	}	
}
