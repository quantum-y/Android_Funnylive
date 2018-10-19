package com.coco.livestreaming.app.server.response;

import org.w3c.dom.Comment;

import java.util.List;

public class AuctionDetailResponse {
    boolean success;

    AuctionItemResponse item;
    List<CommentResponse> comment;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public AuctionItemResponse getItem() {return item;}
    public void setItem(AuctionItemResponse value) {this.item = value;}
    public List<CommentResponse> getComment() {return comment;}
    public void setComment(List<CommentResponse> value) {this.comment = value;}
}
