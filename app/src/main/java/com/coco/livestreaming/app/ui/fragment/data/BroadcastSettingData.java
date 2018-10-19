package com.coco.livestreaming.app.ui.fragment.data;

import com.coco.livestreaming.app.util.Constants;

public class BroadcastSettingData {

	String roomid;
	String title 		= "";
	int theme 			= Constants.VIDEO_THEME_MUSIC;
	int video_quality 	= Constants.VIDEO_QUALITY_STANDARD;
	String pw 			= "";
	int enter_choco 	= 0;
	int limit_num 		= 500;
	boolean blnAdult	= false;
	
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTheme() {
		return theme;
	}
	public void setTheme(int theme) {
		this.theme = theme;
	}
	public int getVideoQuality() {
		return video_quality;
	}
	public void setVideoQuality(int videoQuality) {
		this.video_quality = videoQuality;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public int getEnterChoco() {
		return enter_choco;
	}
	public void setEnterChoco(int enter_choco) {
		this.enter_choco = enter_choco;
	}
	public int getLimitNum() {
		return limit_num;
	}
	public void setLimitNum(int limit_num) {
		this.limit_num = limit_num;
	}
	public boolean getAdult() {
		return blnAdult;
	}
	public void setAdult(boolean blnAdult) {
		this.blnAdult = blnAdult;
	}
}
