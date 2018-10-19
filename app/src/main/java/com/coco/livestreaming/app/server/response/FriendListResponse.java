package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendListResponse {
	boolean success;
	@SerializedName("friend_list")
	List<FriendItemResponse> friend_list;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<FriendItemResponse> getFriend_list() {
		return friend_list;
	}
	public void setFriend_list(List<FriendItemResponse> friend_list) {
		this.friend_list = friend_list;
	}
	
	
}