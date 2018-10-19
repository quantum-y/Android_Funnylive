package com.coco.livestreaming.app.ui.adapter.data;

import java.util.ArrayList;
import java.util.List;

import com.coco.livestreaming.app.server.response.PlayingItemResponse;

public class ExploreItemData {
	
	List<PlayingItemResponse> generalList;
	public List<PlayingItemResponse> getBroadcastList() {
		return generalList;
	}
	public void setBroadcastList(List<PlayingItemResponse> generalList) {
		this.generalList = generalList;
	}
	
}
