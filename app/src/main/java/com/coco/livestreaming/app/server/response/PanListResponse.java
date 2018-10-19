package com.coco.livestreaming.app.server.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.google.tagmanager.protobuf.Internal;

public class PanListResponse {

	boolean success;
	@SerializedName("pan_list")
	List<FanItemResponse> panList;
	Integer rank_recommend;
	String  chat_status;

	public String getChat_status() {
		return chat_status;
	}
	public void setChat_status(String chat_status) {
		this.chat_status = chat_status;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<FanItemResponse> getPanList() {
		return panList;
	}
	public void setPanList(List<FanItemResponse> panList) {
		this.panList = panList;
	}
	public Integer getRank_recommend(){return rank_recommend;}
	public void setRank_recommend(Integer rank_recommend){ this.rank_recommend = rank_recommend;}
}
