package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileViewResponse {
	boolean success;
	@SerializedName("profile_info")
	ProfileInfoResponse profileInfo;
	@SerializedName("top_fan_list")
	List<FanItemResponse> top_fan_list;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public ProfileInfoResponse getProfileInfo() {return profileInfo;}
	public void setProfileInfo(ProfileInfoResponse profileInfo) {
		this.profileInfo = profileInfo;
	}

	public List<FanItemResponse> getTopFanList() {return top_fan_list;	}
	public void setTopFanList(List<FanItemResponse> fanList) {
		this.top_fan_list = top_fan_list;
	}
}