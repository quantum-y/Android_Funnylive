package com.coco.livestreaming.app.server.response;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ExploreListResponse {

	boolean success;
	@SerializedName("new_list")
	List<PlayingItemResponse> newList;
	@SerializedName("recom_list")
	List<PlayingItemResponse> recomList;
	@SerializedName("best_list")
	List<PlayingItemResponse> bestList;
	@SerializedName("near_list")
	List<PlayingItemResponse> nearList;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<PlayingItemResponse> getNewList() {
		return newList;
	}
	public void setNewList(List<PlayingItemResponse> newList) {
		this.newList = newList;
	}
	public List<PlayingItemResponse> getRecomList() {
		return recomList;
	}
	public void setRecomList(List<PlayingItemResponse> recomList) {
		this.recomList = recomList;
	}
	public List<PlayingItemResponse> getBestList() {
		return bestList;
	}
	public void setBestList(List<PlayingItemResponse> bestList) {
		this.bestList = bestList;
	}
	public List<PlayingItemResponse> getNearList() {
		return nearList;
	}
	public void setNearList(List<PlayingItemResponse> nearList) {
		this.nearList = nearList;
	}
	
	
}
