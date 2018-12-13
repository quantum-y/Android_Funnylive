package com.coco.livestreaming.app.server.sync;

import android.content.Context;
import android.net.Uri;

import com.coco.livestreaming.CocotvingApplication;
import com.coco.livestreaming.SessionInstance;
import com.coco.livestreaming.app.server.response.AuctionDetailResponse;
import com.coco.livestreaming.app.server.response.AuctionItemListResponse;
import com.coco.livestreaming.app.server.response.FriendItemResponse;
import com.coco.livestreaming.app.server.response.InvitePlayingListResponse;
import com.coco.livestreaming.app.ui.activity.AuctionActivity;
import com.google.gson.Gson;
import com.coco.livestreaming.app.debug.Logger;
import com.coco.livestreaming.app.network.NetworkEngine;
import com.coco.livestreaming.app.server.response.ExploreListResponse;
import com.coco.livestreaming.app.server.response.LoginDataResponse;
import com.coco.livestreaming.app.server.response.LoginResponse;
import com.coco.livestreaming.app.server.response.ModelDetailListResponse;
import com.coco.livestreaming.app.server.response.NoticeItemResponse;
import com.coco.livestreaming.app.server.response.NoticeListResponse;
import com.coco.livestreaming.app.server.response.FriendListResponse;
import com.coco.livestreaming.app.server.response.PanListResponse;
import com.coco.livestreaming.app.server.response.PlayingItemResponse;
import com.coco.livestreaming.app.server.response.PlayingListResponse;
import com.coco.livestreaming.app.server.response.ProfileViewResponse;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ssk
 *         Created on 2/2/2017.
 */
public class SyncInfo {

    public static final String TAG = SyncInfo.class.getName();
    Context mContext;

    public SyncInfo(Context mContext) {
        this.mContext = mContext;
    }

    public LoginDataResponse syncLogin(String userid, String pw, String request_code){
        LoginDataResponse result = new LoginDataResponse();

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.LOGIN);
        params.put("userid", userid);
        params.put("pw", pw);
        params.put("request_code", request_code);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);

        if (responseString != null) {

            Gson gson = new Gson();
            try {
                LoginResponse response = gson.fromJson(responseString, LoginResponse.class);
//                if (response.isSuccess() && (response.getError() == "" || response.getError() == null)) {
                if (response.isSuccess()) {
                    result.setBjData(response);
                    Logger.i(TAG, "login status:" + response.getUserid());
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }

    public PlayingListResponse syncPlayingList(String key) {

        PlayingListResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        
        String url;
        
        url = Constants.MOBILE_URL;

        params.put("mode", Constants.BROADCASTLIST);
        params.put("key", key);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("userid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
               
        String responseString = NetworkEngine.post(url, params);

         if (responseString != null) {

            Gson gson = new Gson();
            try {
                PlayingListResponse response = gson.fromJson(responseString, PlayingListResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    
    public SuccessFailureResponse syncRecommend(String receiver) {

        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        
        String url;
        
        url = Constants.MOBILE_URL;

        params.put("mode", Constants.RECOMMEND);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("sender", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("receiver", receiver);
               
        String responseString = NetworkEngine.post(url, params);

        if (responseString != null) {

            Gson gson = new Gson();
            try {
            	result = gson.fromJson(responseString, SuccessFailureResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    
    public SuccessFailureResponse syncSendChoco(String cnt, String modelid) {

        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        
        String url;
        
        url = Constants.MOBILE_URL;

        params.put("mode", Constants.SENDCHOCO);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("sender", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("receiver", modelid);
        params.put("choco_cnt", cnt);
               
        String responseString = NetworkEngine.post(url, params);

        if (responseString != null) {

            Gson gson = new Gson();
            try {
            	result = gson.fromJson(responseString, SuccessFailureResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    public SuccessFailureResponse syncBuyBanana(String cnt, String price) {

        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();

        String url;

        url = Constants.MOBILE_URL;

        params.put("mode", Constants.BUYBANANA);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("userid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("chococnt", cnt);
        params.put("chocoprice", price);

        String responseString = NetworkEngine.post(url, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    public ModelDetailListResponse syncPlayingList(String search, String word) {

        ModelDetailListResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        String url;
        
        url = Constants.MOBILE_URL;
        params.put("mode", Constants.CATEGORY);
        params.put("token",SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("userid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("category", search);
        params.put("word", word);
        String responseString = NetworkEngine.post(url, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                ModelDetailListResponse response = gson.fromJson(responseString, ModelDetailListResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    
    public List<NoticeItemResponse> syncNoticeList() {

        List<NoticeItemResponse> result = null;

        Map<String, String> params = new HashMap<String, String>();
        
        String url;
        
        url = Constants.MOBILE_URL;

        params.put("mode", Constants.NOTICE);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
               
        String responseString = NetworkEngine.post(url, params);

        if (responseString != null) {
            Gson gson = new Gson();
            try {
                NoticeListResponse response = gson.fromJson(responseString, NoticeListResponse.class);
                if (response.isSuccess()) {
                    
                    result = response.getNoticeList();
                    
                    Logger.i(TAG, "sync list from server count:" + result.size());
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    
    public PanListResponse syncPanList(String roomid) {

        PanListResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        
        String url;
        
        url = Constants.MOBILE_URL;

        params.put("mode", Constants.PAN);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("roomid", roomid);

        String responseString = NetworkEngine.post(url, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                PanListResponse response = gson.fromJson(responseString, PanListResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }

    public InvitePlayingListResponse syncInvite(String key, String userID, String receiver) {

        InvitePlayingListResponse result = null;

        Map<String, String> params = new HashMap<String, String>();

        String url;

        url = Constants.MOBILE_URL;

        params.put("mode", Constants.INVITE);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("key", key);
        params.put("userid", userID);
        params.put("roomid", receiver);

//        params.put("mode", Constants.PAN);
//        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
//        params.put("roomid", userID);


        String responseString = NetworkEngine.post(url, params);
        if (responseString != null && key.equals("get")) {
            Gson gson = new Gson();
            try {
                InvitePlayingListResponse response = gson.fromJson(responseString, InvitePlayingListResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }


    public ProfileViewResponse syncProfileView(String userid){
        ProfileViewResponse result = null;
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("mode", Constants.PROFILEINFO);
    	params.put("member_id", userid);
        params.put("sender",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
    	params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());

    	String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
    	
    	if (responseString != null) {
            Gson gson = new Gson();
            try {
                ProfileViewResponse response = gson.fromJson(responseString, ProfileViewResponse.class);
                if (response.isSuccess()) {
                	result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
    	
    	return result;
    }

    public SuccessFailureResponse syncProfileSet(String strName, String strSexIndex, String strBirthday, String strNative, String strIntroduce, String strKnowledge, String strImageList){
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.PROFILESET);
        params.put("key", "profile_info_edit");
        params.put("member_id",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("strName", strName);
        params.put("strSexIndex", strSexIndex);
        params.put("strBirthday", strBirthday);
        params.put("strNative", strNative);
        params.put("strIntroduce", strIntroduce);
        params.put("strKnowledge", strKnowledge);
        params.put("strImageList", strImageList);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    public SuccessFailureResponse syncProfileAlarmSet(Integer nAlarm, int nProtectLocation){
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.PROFILESET);
        params.put("key", "profile_alarm_setting");
        params.put("member_id", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("alarm", String.valueOf(nAlarm));
        params.put("protect_location", String.valueOf(nProtectLocation));
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

    public SuccessFailureResponse syncFollowSet(String receiver, String key){
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.FOLLOWSET);
        params.put("key", key);
        params.put("sender", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("receiver", receiver);
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

    public ExploreListResponse syncExploreData(){
    	ExploreListResponse result = null;
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("mode", Constants.EXPLORE);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("userid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());

    	String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
    	if (responseString != null) {
            Gson gson = new Gson();
            try {
            	result = gson.fromJson(responseString, ExploreListResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
    	
    	
    	return result;
    }
    public SuccessFailureResponse syncSetModelInfo(String roomid, String key, String title, String category, String limit, String roompass, String enterchoco, String adult) {

        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        String url;
        url = Constants.MOBILE_URL;
        params.put("mode", Constants.SETMODELINFO);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("roomid", roomid);
        params.put("guestid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("title", title);
        params.put("category", category);
        params.put("limit", limit);
        params.put("roompass", roompass);
        params.put("enterchoco", enterchoco);
        params.put("adult", adult);
        params.put("key", key);
        if (CocotvingApplication.mIsEmulator)
            params.put("live_device", "0");
        else
            params.put("live_device", "1");

        if (key.equals("live_on")){
            StringBuffer friend_id_list = new StringBuffer();
            for (FriendItemResponse item: SessionInstance.getInstance().getFriendList()){
                if (item != null && item.isSeleted){
                    friend_id_list.append(item.getFreind_id());
                    friend_id_list.append(Constants.ID_SEPARATOR);
                }
            }
            params.put("friend_id_list", friend_id_list.toString());
        }
        String responseString = NetworkEngine.post(url, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }

    // upload image into server
    public SuccessFailureResponse syncUploadScreenShot(String filename){
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.UPLOAD_SCREENSHOT);
        params.put("userid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        String responseString = NetworkEngine.postMultipart(Constants.MOBILE_URL, params, "file", filename);
        if (responseString != null) {

            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
                Logger.i(TAG, "profile status:" + result.getResult());
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

    public SuccessFailureResponse syncExcharge(String bankname, String accountnum, String banananum){
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.EXCHARGE);
        params.put("userid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("bankname", bankname);
        params.put("accountnum", accountnum);
        params.put("banananum", banananum);
        params.put("key", "0");

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {

            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
                Logger.i(TAG, "profile status:" + result.getResult());
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncExcharge1(String id, String key, Uri imgPath){
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.EXCHARGE);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("id", id);
        params.put("key", key);

        String responseString = NetworkEngine.postMultipart(Constants.MOBILE_URL, params, "file", imgPath);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
                Logger.i(TAG, "profile status:" + result.getResult());
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

	public FriendListResponse syncFriendList() {

    	FriendListResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        
        String url;
        
        url = Constants.MOBILE_URL;

        params.put("mode", Constants.FRIENDLIST);
        params.put("userid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        
        String responseString = NetworkEngine.post(url, params);

        if (responseString != null) {

            Gson gson = new Gson();
            try {
            	FriendListResponse response = gson.fromJson(responseString, FriendListResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncUpdateLocation(String cityname, String latitude, String longitude){
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.UPDATE_LOCATION);
        params.put("userid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        if(cityname != null) params.put("cityname", cityname);
        if(latitude != null) params.put("latitude", latitude);
        if(longitude != null) params.put("longitude", longitude);
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncCheckRoomIn(String roomid, String password){
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.CHECK_ROOM_IN);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("userid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("roomid", roomid);
        params.put("password", password);
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncUpdateRoom(String key) {
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.SETMODELINFO);
        params.put("key", key);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("roomid", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                //if (response.isSuccess()) {
                    result = response;
                //}
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

    public SuccessFailureResponse syncProfileImageUpload(String id, String imgPath){
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.PROFILESET);
        params.put("key", "profile_image_upload");
        params.put("member_id", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("image_index", id);

        String responseString = NetworkEngine.postMultipart(Constants.MOBILE_URL, params, "file", imgPath);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
                Logger.i(TAG, "profile status:" + result.getResult());
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncUserRegister(String username, String userid, String people_no, String password, String nickname, String phonenumber, String emailaddress) {
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.USER_REGISTER);
        params.put("username", username);
        params.put("userid", userid);
        params.put("people_no", people_no);
        params.put("password", password);
        params.put("nickname", nickname);
        params.put("phonenumber", phonenumber);
        params.put("emailaddress", emailaddress);
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncGetBjPlatform(String roomid){
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.CHECK_ROOM_IN);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("roomid", roomid);
        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public AuctionItemListResponse syncAuctionListData(){
        AuctionItemListResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_LIST);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, AuctionItemListResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

    public AuctionDetailResponse syncAuctionDetailData(String auctionid, String key){
        AuctionDetailResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_INFO);
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("member_id", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("auctionid", auctionid);
        params.put("key", key);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, AuctionDetailResponse.class);
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }

    public SuccessFailureResponse syncAuctionSave(String strName, String strContent, String strHour, String strImageCount) {
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_SAVE);
        params.put("member_id",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("key", "text");
        params.put("strName", strName);
        params.put("strContent", strContent);
        params.put("strHour", strHour);
        params.put("strImageCount", strImageCount);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    public SuccessFailureResponse syncAuctionImageUpload(String id, String imgPath){
        SuccessFailureResponse result = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_SAVE);
        params.put("member_id", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("key", "image");
        params.put("image_index", id);

        String responseString = NetworkEngine.postMultipart(Constants.MOBILE_URL, params, "file", imgPath);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(responseString, SuccessFailureResponse.class);
                Logger.i(TAG, "profile status:" + result.getResult());
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }
        return result;
    }
    public SuccessFailureResponse syncAuctionCancel(String auction_id) {
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_INFO);
        params.put("userid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("key", "cancel");
        params.put("auctionid", auction_id);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    public AuctionDetailResponse syncAuctionComment(String auction_id, String comment) {
        AuctionDetailResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_INFO);
        params.put("userid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        params.put("key", "comment");
        params.put("auctionid", auction_id);
        params.put("comment", comment);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                AuctionDetailResponse response = gson.fromJson(responseString, AuctionDetailResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
    public SuccessFailureResponse syncAuctionLike(String auction_id, String islike) {
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.AUCTION_INFO);
        params.put("userid",SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("token", SessionInstance.getInstance().getLoginData().getBjData().getToken());
        String key = (Integer.valueOf(islike) == 0) ? "dislike" : "like";
        params.put("key", key);
        params.put("auctionid", auction_id);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }

    public SuccessFailureResponse syncSetRoomAdmin(String sender, String val) {
        SuccessFailureResponse result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("mode", Constants.ROOM_ADMIN);
        params.put("sender",sender);
        params.put("receiver", SessionInstance.getInstance().getLoginData().getBjData().getUserid());
        params.put("avalue", val);

        String responseString = NetworkEngine.post(Constants.MOBILE_URL, params);
        if (responseString != null) {
            Gson gson = new Gson();
            try {
                SuccessFailureResponse response = gson.fromJson(responseString, SuccessFailureResponse.class);
                if (response.isSuccess()) {
                    result = response;
                }
            } catch (Exception ex) {
                Logger.ex(TAG, ex);
            }
        }

        return result;
    }
}
