package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;

public class CommentResponse {

    String userid;
	String username;
    String nickname;
    @SerializedName("user_avatar")
	String userAvatar;
    String comment;

    public String getUserid() {return userid;}
    public  void setUserid(String value) {this.userid = value;}
    public String getUsername() {return username;}
    public void setUsername(String value) {this.username = value;}
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String value) {
        this.nickname = value;
    }
    public String getUserAvatar() {return userAvatar;}
    public void setUserAvatar(String value) {this.userAvatar = value;}
    public String getComment() {return comment;}
    public void setComment(String value) {this.comment = value;}
}
