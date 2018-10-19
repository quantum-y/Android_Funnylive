package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
	
	boolean success;
	//@SerializedName("user_id")
	String userid;
	String nickname;
	String password;
	String token;
	String error;
	//@SerializedName("choco_cnt")
	int chococnt;
	boolean isPossibleRecommend;
	int isAdmin;
	int alarm;
	int protect_location;
	int price_per_banana;
	int adult;
	String thumbnail;
	String platform;

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String value) {
		this.nickname = value;
	}
	public int getAdult() {
		return adult;
	}
	public void setAdult(int adult) {
		this.adult = adult;
	}
	public int getChocoCnt() {
		return chococnt;
	}
	public void setChocoCnt(int chococnt) {
		this.chococnt = chococnt;
	}
	public int getPrice_per_banana() {
		return price_per_banana;
	}
	public void setPrice_per_banana(int price_per_banana) {this.price_per_banana = price_per_banana;}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean getIsPossibleRecommend() {
		return isPossibleRecommend;
	}
	public void setIsPossibleRecommend(boolean isPossibleRecommend) {
		this.isPossibleRecommend = isPossibleRecommend;
	}

	public int isAlarm() {
		return alarm;
	}
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	public int isProtect_location() {
		return protect_location;
	}
	public void setProtect_location(int protect_location) {	this.protect_location = protect_location;}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
