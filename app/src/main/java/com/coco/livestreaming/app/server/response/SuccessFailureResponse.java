package com.coco.livestreaming.app.server.response;

public class SuccessFailureResponse {
	boolean success = false;
	String result = null;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
