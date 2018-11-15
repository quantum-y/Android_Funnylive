package com.coco.livestreaming.app.server.response;

import com.google.gson.annotations.SerializedName;

public class AuctionItemResponse {

    @SerializedName("auction_id")
	String auctionId;
    @SerializedName("user_id")
    String userId;
	String username;
    String nickname;
    int sex;
    @SerializedName("user_avatar")
    String userAvatar;
    String distance;
    @SerializedName("left_second")
    int leftSecond;
    int age;
    float longitude;
    float latitude;

    String title;
    String descript;
    int	first;
    int	second;
    int	third;
    int	fourth;
    int	fifth;

    @SerializedName("show_count")
    int showCount;
    @SerializedName("like_count")
    int likeCount;
    @SerializedName("comment_count")
    int commentCount;
    int isliked;

    @SerializedName("auction_hour")
    int auctionHour;
    @SerializedName("is_canceled")
    int isCanceled;
    int price;
    int type;
    @SerializedName("max_price")
    int maxPrice;
    @SerializedName("max_user_id")
    int maxUserId;
    @SerializedName("max_username")
    int maxUsername;
    @SerializedName("max_nickname")
    int maxNickname;

    public int getAuctionHour() {return auctionHour;}
    public void setAuctionHour(int value) {this.auctionHour = value;}

    public int getIsCanceled() {return isCanceled;}
    public void setIsCanceled(int value) {this.isCanceled = value;}

    public int getPrice() {return price;}
    public void setPrice(int value) {this.price = value;}

    public int getType() {return type;}
    public void setType(int value) {this.type = value;}

    public int getMaxPrice() {return maxPrice;}
    public void setMaxPrice(int value) {this.maxPrice = value;}

    public int getMaxUserId() {return maxUserId;}
    public void setMaxUserId(int value) {this.maxUserId = value;}

    public int getMaxUsername() {return maxUsername;}
    public void setMaxUsername(int value) {this.maxUsername = value;}

    public int getMaxNickname() {return maxNickname;}
    public void setMaxNickname(int value) {this.maxNickname = value;}

    public String getAuctionId() {return auctionId;}
    public void setAuctionId(String value) {this.auctionId = value;}

    public String getUserId() {return userId;}
    public void setUserId(String value) {this.userId = value;}
    public String getUsername() {return username;}
    public void setUsername(String value) {this.username = value;}

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String value) {
        this.nickname = value;
    }

    public int getSex() {return sex;}
    public void setSex(int value) {this.sex = value;}
    public String getUserAvatar() {return userAvatar;}
    public void setUserAvatar(String value) {this.userAvatar = value;}
    public String getDistance() {return distance;}
    public void setDistance(String value) {this.distance = value;}
    public int getLeftSecond() {return leftSecond;}
    public void setLeftSecond(int value) {this.leftSecond = value;}
    public int getAge() {return age;}
    public void setAge(int value) {this.age= value;}
    public float getLongitude() {return longitude;}
    public void setLongitude(float value) {this.longitude = value;}
    public float getLatitude() {return latitude;}
    public void setLatitude(float value) {this.latitude = value;}

    public String getTitle() {return title;}
    public void setTitle(String value) {this.title = value;}
    public String getDescript(){return descript;}
    public void setDescript(String value) {this.descript = value;}
    public int getFirst() {
        return first;
    }
    public void setFirst(int value) {
        this.first = value;
    }
    public int getSecond() {
        return second;
    }
    public void setSecond(int value) {
        this.second = value;
    }
    public int getThird() {
        return third;
    }
    public void setThird(int value) {
        this.third = value;
    }
    public int getFourth() {
        return fourth;
    }
    public void setFourth(int value) {
        this.fourth = value;
    }
    public int getFifth() {
        return fifth;
    }
    public void setFifth(int value) {
        this.fifth = value;
    }

    public int getShowCount() {
        return showCount;
    }
    public void setShowCount(int value) {this.showCount = value;}
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int value) {this.likeCount = value;}
    public int getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(int value) {
        this.commentCount = value;
    }
    public int getIsliked() {
        return isliked;
    }
    public void setIsliked(int value) {this.isliked = value;}
}
