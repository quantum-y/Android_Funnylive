package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuctionItemListResponse {
	boolean success;
	List<AuctionItemResponse> list;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<AuctionItemResponse> getList() {
		return list;
	}
	public void setList(List<AuctionItemResponse> list) {this.list = list;}
}
