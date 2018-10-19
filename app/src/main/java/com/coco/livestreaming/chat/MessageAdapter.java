package com.coco.livestreaming.chat;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.util.Constants;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public List<Message> mMessages;
    private int[] mUsernameColors;
    private Context mContext;
    public MessageAdapter(Context context, List<Message> messages) {
        mContext = context;
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
        case Message.TYPE_MESSAGE:
            //layout = R.layout.item_chat_message;
            layout = R.layout.item_chat_log;
            break;
        case Message.TYPE_LOG:
            layout = R.layout.item_chat_log;
            break;
        case Message.TYPE_ACTION:
            //layout = R.layout.item_chat_action;
            break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());
        viewHolder.setSuffix(message.getSuffixStyle());
        viewHolder.setLevel(message.getLevelNum(), message.getColorIndex());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mContainerView;
        private TextView mLevelView;
        private TextView mUsernameView;
        private TextView mMessageView;
        private TextView mRecommandSuffix;
        private ImageView mTopLevelImage;
        private TextView mTopLevelText;
        public ViewHolder(View itemView) {
            super(itemView);
            mContainerView = (LinearLayout) itemView.findViewById(R.id.layout_container);
            mLevelView       = (TextView) itemView.findViewById(R.id.txt_level_id);
            mUsernameView    = (TextView) itemView.findViewById(R.id.txt_username_id);
            mMessageView     = (TextView) itemView.findViewById(R.id.txt_message_id);
            mRecommandSuffix = (TextView) itemView.findViewById(R.id.txt_recommend_suffix_id);
            mTopLevelImage   = (ImageView) itemView.findViewById(R.id.img_level_top_id);
            mTopLevelText    = (TextView) itemView.findViewById(R.id.txt_level_top_id);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(mUsernameColors[4]);

        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
            mMessageView.setTextColor(mUsernameColors[4]);
        }
        public  void setSuffix(String style){
            if (style.equals(Constants.ALERT_MSG_COMMON)) {
                mRecommandSuffix.setVisibility(View.GONE);
            }else if (style.equals(Constants.ALERT_MSG_SINGLE_CHAT)){
                mRecommandSuffix.setVisibility(View.GONE);
            }
            else if(style.equals(Constants.ALERT_MSG_RECOMMAND)) {
                mRecommandSuffix.setVisibility(View.VISIBLE);
            }
            if (style.equals(Constants.ALERT_MSG_CHOCO)) {
                mRecommandSuffix.setVisibility(View.GONE);
//                mContainerView.setBackground(mContext.getResources().getDrawable(R.drawable.round_bg_blue_trans));
            }
        }
        public void setLevel(int levelNum, int colorIndex){
            //레벨에 따른 색갈정보를 얻어 라운드이펙트하기
            if (levelNum == 0 )
                mLevelView.setVisibility(View.GONE);
            else {
                if (colorIndex != -1) {
                    mTopLevelText.setVisibility(View.GONE);
                    mTopLevelImage.setVisibility(View.GONE);
                    int[] nLevelColors = mContext.getResources().getIntArray(R.array.level_colors);
                    Drawable levelDrawable = mContext.getResources().getDrawable(R.drawable.round_bg_level_icon);
                    levelDrawable.setColorFilter(nLevelColors[colorIndex], PorterDuff.Mode.SRC_IN);
                    mLevelView.setVisibility(View.VISIBLE);
                    mLevelView.setBackground(levelDrawable);
                    mLevelView.setText(String.valueOf(levelNum));
                }else{
                    mLevelView.setVisibility(View.GONE);
                    mTopLevelImage.setVisibility(View.VISIBLE);
                    mTopLevelText.setVisibility(View.VISIBLE);
                    mTopLevelText.setText(String.valueOf(levelNum));
                }
            }
        }
    }
}
