package com.coco.livestreaming.app.server.response;

public class PlayingItemResponse {

	int onlinemembers;
	String userid;
	String username;
	String nickname;
	String title;
	int roompass;
	int adult;
	String category;
	String online_date;
	String hour_diff;
	String min_diff;
	String location;
	int isLastUpdate;
	int nlimit;
	int enterchoco;
	double distance;
	String first;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public int getRoomPass() {		return roompass;	}
	public void setRoomPass(int roompass) {
		this.roompass = roompass;
	}
    public int getAdult() {		return adult;	}
    public void setAdult(int adult) {
        this.adult = adult;
    }

    public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getOnlinemembers() { return onlinemembers;}
	public void setOnlinemembers(int onlinemembers) {
		this.onlinemembers = onlinemembers;
	}
	public String getOnlinedate() {
		return online_date;
	}
	public void setOnlinedate(String online_date) {
		this.online_date = online_date;
	}
	public String getHour_diff() {
		return hour_diff;
	}
	public void setHour_diff(String hour_diff) {
		this.hour_diff = hour_diff;
	}
	public String getMin_diff() {
		return min_diff;
	}
	public void setMin_diff(String min_diff) {
		this.min_diff = min_diff;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String place) {
		this.location = location;
	}
//	public String getSnapImg() {
//		return snapImg;
//	}
//	public void setSnapImg(String snapImg) {
//		this.snapImg = snapImg;
//	}
	public int getIsLastUpdate(){return isLastUpdate;}
	public void setLastUpdate(int isLastUpdate){this.isLastUpdate = isLastUpdate;}
	public int getLimit(){return nlimit;}
	public void setLimit(int nlimit){this.nlimit = nlimit;}
	public int getEnterchoco(){return enterchoco;}
	public void setEnterchoco(int enterchoco){this.enterchoco = enterchoco;}
	public double getDistance(){return distance;}
	public void setDistance(int distance){this.distance = distance;}
}
