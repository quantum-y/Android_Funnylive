package com.coco.livestreaming.app.util;

import android.os.Environment;

public class Constants {
    public static final String WZ_LIVE_HOST_ADDRESS = "211.57.203.41";// "200.173.91.61";// "211.57.201.133";
    public static final String WEB_HOST_ADDRESS = "211.57.203.41";// "200.173.91.61";
    public static final String CHAT_HOST_ADDRESS = "211.57.203.41";// "200.173.91.61";//될수록 와우자서버와 소켓서버의 주소가 같아야 함.

//    public static final String WZ_LIVE_HOST_ADDRESS = "200.173.91.61";// "211.57.201.133";
//    public static final String WEB_HOST_ADDRESS = "200.173.91.61";
//    public static final String CHAT_HOST_ADDRESS = "200.173.91.61";//될수록 와우자서버와 소켓서버의 주소가 같아야 함.

//    public static final String WZ_LIVE_HOST_ADDRESS = "192.168.228.2";
//    public static final String WEB_HOST_ADDRESS = "192.168.228.2";
//    public static final String CHAT_HOST_ADDRESS = "192.168.228.2";

	public static final String IMG_SUB_PATH = "funnylive/image/";
    public static final String IMG_FULL_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + IMG_SUB_PATH;
    
    public static final String HOME_URL = "http://"+ WEB_HOST_ADDRESS + "/cocotving/";
    public static final String MOBILE_URL = "http://"+ WEB_HOST_ADDRESS + "/cocotving/mobile/mb/index.php";

//    public static final String CHAT_SERVER_URL = "http://" + CHAT_HOST_ADDRESS + ":3000/cocotving";//cocotving은 namespace임.
    public static final String CHAT_SERVER_URL = "http://" + CHAT_HOST_ADDRESS + ":3000";
    public static final String WZ_LIVE_APP_NAME = "funnylive";
    public static final String WZ_LIVE_PORT = "1935";

    public static final String IMG_MODEL_URL = "http://" + WEB_HOST_ADDRESS + "/cocotving/mobile/files/member/";
    public static final String IMG_NOTICE_URL = "http://" + WEB_HOST_ADDRESS + "/cocotving/mobile/files/notice/";
    public static final String IMG_SLIDE_URL = "http://" + WEB_HOST_ADDRESS + "/cocotving/mobile/files/slide";
    public static final String IMG_AUCTION_URL = "http://" + WEB_HOST_ADDRESS + "/cocotving/mobile/files/auction/";

    public static final String WOWZA_USERNM = "root";
    public static final String WOWZA_PW = "root";
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    public static final String LOGIN = "LOGIN";
    public static final String BROADCASTLIST = "BROADCAST_LIST";
    public static final String PROFILEINFO  = "PROFILE_INFO";
    public static final String PROFILESET   = "PROFILE_SET";
    public static final String CATEGORY     = "CATEGORY";
    public static final String RECOMMEND    = "RECOMMEND";
    public static final String SENDCHOCO    = "SENDCHOCO";
    public static final String BUYBANANA     = "BUYBANANA";
    public static final String EXPLORE      = "EXPLORE";
    public static final String NOTICE       = "NOTICE";
    public static final String PAN          = "PAN";
    public static final String UPLOAD_SCREENSHOT = "UPLOAD_SCREENSHOT";
    public static final String SETMODELINFO = "SETMODELINFO";
    public static final String FOLLOWSET = "FOLLOWSET";
    public static final String FRIENDLIST = "FRIEND_LIST";
    public static final String EXCHARGE = "EXCHARGE";
    public static final String UPDATE_LOCATION = "UPDATE_LOCATION";
    public static final String CHECK_ROOM_IN = "CHECK_ROOM_IN";
    public static final String USER_REGISTER = "USER_REGISTER";
    // added by HiStar
    public static final String AUCTION_SAVE      = "AUCTION_SAVE";
    public static final String AUCTION_LIST      = "AUCTION_LIST";
    public static final String AUCTION_INFO      = "AUCTION_INFO";

    public static final String ROOM_ADMIN      = "ROOM_ADMIN";

    public static final String APFLAG = "apflag";
    public static final int AGREEMENT_FLAG = 1;
    public static final int PRIVACY_FLAG = 2;
    
    public static final int AGREEMENT = 1;
    public static final int PRIVACY = 2;
    
    public static final String CATEGORY_DETAIL_TYPE = "category_detail_type";
    public static final int CATEGORY_NEW = 1;
    public static final int CATEGORY_RECOM = 2;
    public static final int CATEGORY_BEST = 3;
    public static final int CATEGORY_NEAR = 4;
    
    public static final String MAIN_TAB_INDEX = "main_tab_index";
    
    public static final int BROADCAST_HOME = 1;
    public static final int BROADCAST_SETTING = 2;
    public static final int BROADCAST_LIVE = 3;
    public static final int BROADCAST_SELECT_FAN = 4;

    public static final String ALERT_MSG_RECOMMAND="alert_msg_recommand";
    public static final String ALERT_MSG_CHOCO="alert_msg_choco";
    public static final String ALERT_MSG_COMMON="alert_msg_common";
    public static final String ALERT_MSG_CHAT_LOCK = "alert_msg_chat_lock";
    public static final String ALERT_MSG_CHAT_UNLOCK = "alert_msg_chat_unlock";
    public static final String ALERT_MSG_INVITE_YES = "alert_msg_invite_yes";
    public static final String ALERT_MSG_INVITE_NO = "alert_msg_invite_no";
    public static final String ALERT_MSG_INVITE_REJECT = "alert_msg_invite_reject";
    public static final String ALERT_MSG_INVITE_RELOADING = "alert_msg_invite_reloading";
    public static final String ALERT_MSG_SINGLE_CHAT = "alert_msg_single_chat";
    public static final String ALERT_MSG_SINGLE_INVITE = "alert_msg_single_invite";
    public static final String ALERT_MSG_SINGLE_FORCE_OUT = "alert_msg_single_force_out";
    public static final String ALERT_MSG_ADMIN_FORCE_OFFLINE = "alert_msg_admin_force_offline";
    public static final String ALERT_MSG_NETWORK_CHECK = "alert_msg_network_check";
    public static final String ALERT_MSG_SET_ROOMADMIN = "alert_msg_setroomadmin";

    public static final int PLAYING_LIST_NOW = 0;
    public static final int PLAYING_LIST_ALARM = 1;

    public static final int VIDEO_QUALITY_HIGH      = 5000;
    public static final int VIDEO_QUALITY_STANDARD  = 3750;
    public static final int VIDEO_QUALITY_LOW       = 1500;

    public static final int    BACK_TAP_INTERVAL = 2000;
    public static long         BACK_PRESSED_TIME;


    final public static int SNAP_IMAGE_WIDTH = 300;
    final public static int SNAP_IMAGE_HEIGHT = 300;

    final public static int TOP_LEVEL_COUNT = 30;//mashang

    final public static float TAX = 0.033f;

	public static final int VIDEO_THEME_MUSIC	= 0;
    public static final int VIDEO_THEME_GAME	= 1;
    public static final int VIDEO_THEME_LIFE	= 2;

    public static final int GPS_SETTING = 1;
    //최소거리변화
    public static final long GPS_MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 10 meters
    // 최소시간변화
    public static final long GPS_MIN_TIME_BW_UPDATES = 1000 * 60 * 2; // 2 minute

    public static final int SEX_MALE     = 1;
    public static final int SEX_FEMALE   = 2;
    public static final int SEX_NO       = 0;

    public static final String ID_SEPARATOR = "_~!@_";
    public static final long UPLOAD_FILE_SIZE = 100 * 1024; // 100KB

    public static final String STATE_INTERVAL_UPDATE    = "state_interval_update";
    public static final String STATE_CHAT_LOCK          = "state_chat_lock";
    public static final String STATE_CHAT_UNLOCK        = "state_chat_unlock";
    public static final String STATE_USER_BLOCK         = "state_user_block";
    public static final String STATE_USER_FORCE_OUT     = "state_user_force_out";

    final public static long DEFAULT_SCREENSHOT_INTERVAL = 6000;
    public static final int LIBSTREAM_BUFFERING_TIME = 15000;
    public static final int GOCODER_BUFFERING_TIME = 3000;// 3000;
    public static final int INVALID_EXIT_TIME = 20000;
    public static final long PING_TIME_OUT = 13000;

}
