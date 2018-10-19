package com.coco.livestreaming.app.server.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NoticeListResponse {

	boolean success;
	@SerializedName("notice_list")
	List<NoticeItemResponse> noticeList;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<NoticeItemResponse> getNoticeList() {
		return noticeList;
	}
	public void setNoticeList(List<NoticeItemResponse> noticeList) {
		this.noticeList = noticeList;
	}

}
