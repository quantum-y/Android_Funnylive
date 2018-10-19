package com.coco.livestreaming.app.server.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ModelDetailListResponse {

	
	boolean success;
	@SerializedName("model_list")
	List<PlayingItemResponse> modelList;
	String category;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<PlayingItemResponse> getModelList() {
		return modelList;
	}
	public void setModelList(List<PlayingItemResponse> modelList) {
		this.modelList = modelList;
	}
	public String getCategory() {return category;}
	public void setCategory(String category) {this.category = category;}
}
