package com.coco.livestreaming.app.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.activity.LiveVideoShowActivity;
import com.coco.livestreaming.app.ui.dialog.BuyChocoDialog;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 3/11/2017.
 */

public class BananaChargeListAdapter extends BaseAdapter {
    private Context m_Context;
    private LayoutInflater m_Inflater;
    private ArrayList<BuyChocoDialog.BananaItem> m_listBananaItem;
    private int m_layout;
    public BananaChargeListAdapter(Context context, int nlayout, ArrayList<BuyChocoDialog.BananaItem> arSrc) {
        m_Context = context;
        m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_listBananaItem = arSrc;
        m_layout = nlayout;
    }

    public int getCount() {
        return m_listBananaItem.size();
    }

    public String getItem(int position) {
        return m_listBananaItem.get(position).m_strBananaCntName;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = m_Inflater.inflate(m_layout, parent, false);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.iv_banana);
        img.setImageResource(m_listBananaItem.get(position).m_nImage);
        TextView bananaCntName = (TextView) convertView.findViewById(R.id.txt_banana_cnt);
        bananaCntName.setText(m_listBananaItem.get(position).m_strBananaCntName);
        TextView bananaPriceName = (TextView) convertView.findViewById(R.id.txt_banana_price);
        bananaPriceName.setText(m_listBananaItem.get(position).m_strBananaPriceName);
        final ToggleButton btnToggle = (ToggleButton)convertView.findViewById(R.id.btn_toggle_id);
        if (m_listBananaItem.get(position).m_isSelected)
            btnToggle.setChecked(true);
        else
            btnToggle.setChecked(false);
        btnToggle.setTag(position);
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nSelIndex = Integer.valueOf(v.getTag().toString());
                for (int i = 0; i < m_listBananaItem.size(); i++){
                    if (i == nSelIndex)
                        m_listBananaItem.get(i).m_isSelected = true;
                    else
                        m_listBananaItem.get(i).m_isSelected = false;
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

}

