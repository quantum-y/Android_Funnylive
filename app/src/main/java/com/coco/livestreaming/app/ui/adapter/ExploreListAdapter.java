package com.coco.livestreaming.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.livestreaming.app.ui.adapter.data.ExploreItemData;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ExploreListAdapter extends BaseAdapter {

    public final String TAG = ExploreListAdapter.class.getName();

    List<ExploreItemData> listBoxs;
    Context mContext;
    public interface OnImgClickListener {
        public void onImgClick(int pos1, int pos2);
    }

    OnImgClickListener listener;

    public void setOnImgClickListener(OnImgClickListener listener) {
        this.listener = listener;
    }
    
    public interface OnDetailClickListener {
        public void onDetailClick(int pos);
    }

    OnDetailClickListener detailListener;

    public void setOnDetailClickListener(OnDetailClickListener listener) {
        this.detailListener = listener;
    }
    
    public ExploreListAdapter(Context context,List<ExploreItemData> list) {
    	listBoxs = list;
    	mContext = context;
    }

    @Override
    public int getCount() {
        return listBoxs.size();
    }

    @Override
    public ExploreItemData getItem(int position) {
        return listBoxs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explorer, parent, false);
        }
        final ExploreItemData data = (ExploreItemData) listBoxs.get(position);
        
        int count = 0;
        
        switch(position + 1){
        case Constants.CATEGORY_NEW:
        	((TextView) convertView.findViewById(R.id.tvCategoryTitle)).setText(this.mContext.getString(R.string.category_new));
        	((Button)convertView.findViewById(R.id.btnDetail)).setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				if(detailListener != null)
    					detailListener.onDetailClick(Constants.CATEGORY_NEW);
    			}
    		});
        	break;
        case Constants.CATEGORY_RECOM:
        	((TextView) convertView.findViewById(R.id.tvCategoryTitle)).setText(this.mContext.getString(R.string.category_recom));
        	((Button)convertView.findViewById(R.id.btnDetail)).setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				if(detailListener != null)
    					detailListener.onDetailClick(Constants.CATEGORY_RECOM);
    			}
    		});
        	break;
        case Constants.CATEGORY_BEST:
        	((TextView) convertView.findViewById(R.id.tvCategoryTitle)).setText(this.mContext.getString(R.string.category_best));
        	((Button)convertView.findViewById(R.id.btnDetail)).setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				if(detailListener != null)
    					detailListener.onDetailClick(Constants.CATEGORY_BEST);
    			}
    		});
        	break;
        case Constants.CATEGORY_NEAR:
        	((TextView) convertView.findViewById(R.id.tvCategoryTitle)).setText(this.mContext.getString(R.string.category_near));
        	((Button)convertView.findViewById(R.id.btnDetail)).setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				if(detailListener != null)
    					detailListener.onDetailClick(Constants.CATEGORY_NEAR);
    			}
    		});
        	break;
        }
		count = data.getBroadcastList() == null ? 0 : data.getBroadcastList().size();
		int[] layerArr = {R.id.rlBjMemeber1, R.id.rlBjMemeber2, R.id.rlBjMemeber3};
		int[] imgArr = {R.id.imgBjMemeber1, R.id.imgBjMemeber2, R.id.imgBjMemeber3};
		for (int index = 0; index < count; index++){
			((RelativeLayout)convertView.findViewById(layerArr[index])).setVisibility(View.VISIBLE);
			convertView.findViewById(imgArr[index]).setTag(index);

			//String snapImg = data.getBroadcastList().get(index).getFirst();
			//String pass = data.getBroadcastList().get(index).getRoomPass();
			//String category = data.getBroadcastList().get(index).getCategory();
			//스냅이 없거나 비번방이거나 19금이면
			//if (snapImg == null || (pass != null && !pass.isEmpty()) || (category != null && category.equals("Adult")))
			//	snapImg = "thumbnail.jpg";

			Picasso.with(parent.getContext())
					.load(Constants.IMG_MODEL_URL + data.getBroadcastList().get(index).getUserid() + File.separator + data.getBroadcastList().get(index).getFirst())
					.placeholder(R.drawable.no_image)
					.into((ImageView) convertView.findViewById(imgArr[index]));
			((ImageView) convertView.findViewById(imgArr[index])).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (listener != null)
						imgClick(position, Integer.valueOf(v.getTag().toString()));
				}
			});
		}
        return convertView;
    }
    
    public void imgClick(int pos1, int pos2){
    		listener.onImgClick(pos1, pos2);
    }
    
}
