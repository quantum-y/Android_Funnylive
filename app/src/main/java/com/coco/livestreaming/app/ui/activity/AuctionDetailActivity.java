package com.coco.livestreaming.app.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.R;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.AuctionDetailResponse;
import com.coco.livestreaming.app.server.response.AuctionItemListResponse;
import com.coco.livestreaming.app.server.response.AuctionItemResponse;
import com.coco.livestreaming.app.server.response.CommentResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;
import com.coco.livestreaming.app.ui.dialog.CancelAuctionDialog;
import com.coco.livestreaming.app.ui.dialog.SelectDialog;
import com.coco.livestreaming.app.ui.fragment.AuctionListFragment;
import com.coco.livestreaming.app.ui.view.CircleImageView;
import com.coco.livestreaming.app.util.Constants;
import com.coco.livestreaming.app.util.Utils;
import com.coco.livestreaming.chat.Message;
import com.coco.livestreaming.chat.MessageAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AuctionDetailActivity extends Activity {

    private Button btn_cancel_auction;
    private EditText message_input;
    private CircleImageView userImage;
    private TextView txt_like_count;
    private TextView txt_comment_count;
    private LinearLayout layout_photo;
    private LinearLayout layoutChatBar;
    private RecyclerView rv_messages;
    private RecyclerView.Adapter mAdapter;
    private SyncInfo info;
    private CancelAuctionDialog selectDialog;

    private AuctionItemResponse auctionItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_detail);

        btn_cancel_auction = (Button)findViewById(R.id.btn_cancel_auction);
        message_input = (EditText)findViewById(R.id.message_input);
        userImage = (CircleImageView)findViewById(R.id.img_user_photo);
        txt_like_count = (TextView)findViewById(R.id.txt_like_count);
        txt_comment_count = (TextView)findViewById(R.id.txt_comment_count);
        layout_photo = (LinearLayout)findViewById(R.id.layout_photo);
        layoutChatBar = (LinearLayout)findViewById(R.id.lay_bottom_chat_bar);
        rv_messages = (RecyclerView)findViewById(R.id.rv_messages);
        rv_messages.setLayoutManager(new LinearLayoutManager(this));

        info = new SyncInfo(this);
        Intent intent = getIntent();
        String auctionId = intent.getStringExtra("auctionId");
        new AuctionDetailAsync().execute(auctionId);
    }
    @Override
    public void onBackPressed(){
        finish();
    }

    public void onButtonClick(View v) {
        switch (v.getId()){
            case R.id.btn_Back:
                finish();
                break;
            case R.id.btn_like:
                int isliked = auctionItem.getIsliked();
                isliked = (isliked + 1) % 2; // 0->1 , 1->0
                new AuctionLikeAsync().execute(auctionItem.getAuctionId(), String.valueOf(isliked));
                break;
            case R.id.btn_comment:
                if (layoutChatBar.getVisibility() == View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.out_to_down);
                    animation.setAnimationListener(new TranslateAnimation.AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                            layoutChatBar.setVisibility(View.GONE);
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }
                    });
                    layoutChatBar.startAnimation(animation);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_down);
                    layoutChatBar.setVisibility(View.VISIBLE);
                    layoutChatBar.startAnimation(animation);
                }
                break;
            case R.id.btn_cancel_auction:
                selectDialog = new CancelAuctionDialog(AuctionDetailActivity.this, getString(R.string.dialog_auction_cancel), 0);
                selectDialog.setCancelable(false);
                selectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectDialog.setOnOkClickListener(new CancelAuctionDialog.OnOkClickListener() {
                    @Override
                    public void onOkClick(int flag) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new AuctionCancelAsync().execute(auctionItem.getAuctionId());
                            }
                        },10);
                        selectDialog.dismiss();
                    }
                });
                selectDialog.show();

                break;
            case R.id.btn_chat_send_id:
                String commentStr = message_input.getText().toString();
                if (!commentStr.equals("")) {
                    new AuctionCommentAsync().execute(auctionItem.getAuctionId(), commentStr);
                }
                break;
        }
    }

    class AuctionDetailAsync extends AsyncTask<String, String, AuctionDetailResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected AuctionDetailResponse doInBackground(String... args) {
            AuctionDetailResponse result = info.syncAuctionDetailData(args[0], "detail");
            return result;
        }

        @Override
        protected void onPostExecute(AuctionDetailResponse result) {
            super.onPostExecute(result);

            if (result != null && result.isSuccess()) {
                auctionItem = result.getItem();
                String userid = SessionInstance.getInstance().getLoginData().getBjData().getUserid();
                if (userid.equals(auctionItem.getUserId()))
                    btn_cancel_auction.setVisibility(View.VISIBLE);
                else
                    btn_cancel_auction.setVisibility(View.INVISIBLE);

                Picasso.with(AuctionDetailActivity.this)
                        .load(Constants.IMG_MODEL_URL + auctionItem.getUserId() + "/" + auctionItem.getUserAvatar())
                        .placeholder(R.drawable.no_image)
                        .into(userImage);
                txt_like_count.setText(String.valueOf(auctionItem.getLikeCount()));
                txt_comment_count.setText(String.valueOf(auctionItem.getCommentCount()));

                mAdapter = new MessageAdapter(AuctionDetailActivity.this, result.getComment());
                rv_messages.setAdapter(mAdapter);
                rv_messages.scrollToPosition( result.getComment().size() - 1);

                int imgCount = 1;
                if (auctionItem.getSecond() == 2)
                    imgCount = 2;
                if (auctionItem.getThird() == 3)
                    imgCount = 3;
                if (auctionItem.getFourth() == 4)
                    imgCount = 4;
                if (auctionItem.getFifth() == 5)
                    imgCount = 5;

                for (int i = 0; i < imgCount; i++) {
                    RelativeLayout item = (RelativeLayout) View.inflate(AuctionDetailActivity.this, R.layout.item_auction_photo_detail, null);
                    ImageView imgView = (ImageView) item.findViewById(R.id.imgPhoto);
                    Picasso.with(AuctionDetailActivity.this)
                            .load(Constants.IMG_AUCTION_URL + auctionItem.getAuctionId()+"/"+String.valueOf(i+1)+".jpg")
                            .placeholder(R.drawable.no_image)
                            .into(imgView);
                    layout_photo.addView(item);
                }
            }

            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
                }
            },10);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    class AuctionCancelAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            SuccessFailureResponse result = info.syncAuctionCancel(args[0]);
            return result;
        }

        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                },10);
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
                }
            },10);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    class AuctionCommentAsync extends AsyncTask<String, String, AuctionDetailResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected AuctionDetailResponse doInBackground(String... args) {
            AuctionDetailResponse result = info.syncAuctionComment(args[0], args[1]);
            return result;
        }

        @Override
        protected void onPostExecute(AuctionDetailResponse result) {
            super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
                ((MessageAdapter)mAdapter).mMessages = result.getComment();
                mAdapter.notifyDataSetChanged();
                message_input.setText("");
                // 코멘트의 값을 변경
                int comment_count = result.getComment().size();
                txt_comment_count.setText(String.valueOf(comment_count));
                rv_messages.scrollToPosition(comment_count-1);
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
                }
            },10);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }

    class AuctionLikeAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SuccessFailureResponse doInBackground(String... args) {
            SuccessFailureResponse result = info.syncAuctionLike(args[0], args[1]);
            return result;
        }

        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null && result.isSuccess()) {
                int isliked = auctionItem.getIsliked();
                isliked = (isliked + 1) % 2; // 0->1 , 1->0
                auctionItem.setIsliked(isliked);
                String like_count = (result.getResult() == null) ? "0" : result.getResult();
                txt_like_count.setText(like_count);
            }
            Utils.disappearProgressDialog();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CocotvingApplication)getApplication()).mIsVisibleFlag = true;
                }
            },10);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.disappearProgressDialog();
        }
    }


    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        public List<CommentResponse> mMessages;
        private Context mContext;

        public MessageAdapter(Context context, List<CommentResponse> messages) {
            mContext = context;
            mMessages = messages;
        }

        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_comment, parent, false);
            return new MessageAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MessageAdapter.ViewHolder viewHolder, int position) {
            CommentResponse message = mMessages.get(position);
            Picasso.with(AuctionDetailActivity.this)
                    .load(Constants.IMG_MODEL_URL + message.getUserid() + "/" + message.getUserAvatar())
                    .placeholder(R.drawable.no_image)
                    .into(viewHolder.mUserImage);
            viewHolder.mUserName.setText(message.getNickname());
            viewHolder.mComment.setText(message.getComment());
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CircleImageView mUserImage;
            public TextView mUserName;
            public TextView mComment;

            public ViewHolder(View itemView) {
                super(itemView);
                mUserImage   = (CircleImageView) itemView.findViewById(R.id.img_user_photo);
                mUserName       = (TextView) itemView.findViewById(R.id.txt_user_name);
                mComment    = (TextView) itemView.findViewById(R.id.txt_comment);
            }
        }
    }
}

