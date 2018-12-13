package com.coco.livestreaming.app.server.response;

import java.util.List;

public class InvitePlayingListResponse {
	boolean success;
	List<InvitePlayingItemResponse> result;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<InvitePlayingItemResponse> getList() {
		return result;
	}
	public void setList(List<InvitePlayingItemResponse> list) {this.result = list;}
}
