package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

public class InvitePlayingItemResponse {

    @SerializedName("room")
	String room;

    public String getRoom() {return room;}
    public void setRoom(String value) {this.room = value;}

}
