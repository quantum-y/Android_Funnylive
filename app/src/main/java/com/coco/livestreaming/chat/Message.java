package com.coco.livestreaming.chat;

public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;
    public static final int TYPE_PRESENT = 3;

    private int     mType;
    private String  mMessage;
    private String  mUsername;
    private String  mSuffixStyle;
    private int     mLevelNum;
    private int     mColorIndex;

    private Message() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() {
        return mUsername;
    };

    public String  getSuffixStyle(){return  mSuffixStyle;};

    public int getLevelNum(){ return  mLevelNum;};

    public int getColorIndex(){ return  mColorIndex;};

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mSuffixStyle;
        private int     mLevelNum;
        private int     mColorIndex;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }
        public Builder suffix(String style){
            mSuffixStyle = style;
            return  this;
        }
        public Builder level(int levelNum, int colorIndex){
            mLevelNum = levelNum;
            mColorIndex = colorIndex;
            return this;
        }
        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mSuffixStyle = mSuffixStyle;
            message.mLevelNum = mLevelNum;
            message.mColorIndex = mColorIndex;
            return message;
        }
    }
}
