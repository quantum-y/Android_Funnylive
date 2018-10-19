package com.coco.livestreaming.app.server.response;

public class FriendItemResponse {

	String freind_id;
	String freind_location;
	String freind_name;
	String first;

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public boolean isSeleted = false;
	
	public String getFreind_id() {
		return freind_id;
	}
	public void setFreind_id(String freind_id) {
		this.freind_id = freind_id;
	}
	public String getFreind_location() {
		return freind_location;
	}
	public void setFreind_location(String freind_location) {
		this.freind_location = freind_location;
	}
	public String getFreind_name() {
		return freind_name;
	}
	public void setFreind_name(String freind_name) {
		this.freind_name = freind_name;
	}

	
}
