package com.coco.livestreaming.app.server.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PlayingListResponse {
	boolean success;
	Integer	key;
	@SerializedName("list")
	List<PlayingItemResponse> list;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public List<PlayingItemResponse> getList() {
		return list;
	}
	public void setList(List<PlayingItemResponse> list) {this.list = list;}
}
