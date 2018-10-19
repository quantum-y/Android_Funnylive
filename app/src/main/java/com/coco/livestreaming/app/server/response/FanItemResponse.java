package com.coco.livestreaming.app.server.response;

public class FanItemResponse {

	Integer banana_cnt;
	Integer fan_banana_cnt;
	Integer now_banana_cnt;
	String fan_id;
	Integer level_num;
	Integer level_color;
	Boolean isTopLevel;
	String first;
	Integer isAdmin;

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getFanID() {
		return fan_id;
	}
	public void setFanID(String fan_id) {
		this.fan_id = fan_id;
	}

	public Integer getCnt() {
		return banana_cnt;
	}
	public void setCnt(Integer cnt) {
		this.banana_cnt = banana_cnt;
	}

	public Integer getFan_banana_cnt() {
		return fan_banana_cnt;
	}
	public void setFan_banana_cnt(Integer fan_banana_cnt) {
		this.fan_banana_cnt = fan_banana_cnt;
	}

	public Integer getNow_banana_cnt() {
		return now_banana_cnt;
	}
	public void setNow_banana_cnt(Integer now_banana_cnt) {
		this.now_banana_cnt = now_banana_cnt;
	}

	public Integer getLevel_num() {
	return level_num;
}
	public void setLevel_num(Integer level_num) {
		this.level_num = level_num;
	}

	public Integer getLevel_color() {
		return level_color;
	}
	public void setLevel_color(Integer level_color) {
		this.level_color = level_color;
	}

	public Boolean getTopLevel() {
		return isTopLevel;
	}
	public void setTopLevel(Boolean isTopLevel) {
		this.isTopLevel = isTopLevel;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}
}
