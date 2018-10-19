package com.coco.livestreaming;

import android.content.Context;
import android.util.Log;

import com.coco.livestreaming.app.debug.Logger;
import com.coco.livestreaming.app.server.response.FanItemResponse;
import com.coco.livestreaming.app.server.response.FriendItemResponse;
import com.coco.livestreaming.app.server.response.LoginDataResponse;
import com.google.gson.annotations.SerializedName;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Store Session data
 *
 * @author ssk
 */
public class SessionInstance implements Serializable {

    private static final long serialVersionUID = -1509339033761436903L;
    public static final String TAG = "SessionInstance";
    private static SessionInstance INSTANCE;
    
    LoginDataResponse           loginData;

    @SerializedName("friendList")
    List<FriendItemResponse>    friendList;
    /**
     * Constructor
     *
     * @author ssk
     */
    public SessionInstance() {

    }

    /**
     * return SessionInstance
     *
     * @author ssk
     */
    public static synchronized SessionInstance getInstance() {
        if (INSTANCE == null) {

            try {
                FileInputStream fis = CocotvingApplication.getContext().openFileInput(TAG);
                ObjectInputStream ois = new ObjectInputStream(fis);
                INSTANCE = (SessionInstance) ois.readObject();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }

        return INSTANCE;
    }

    /**
     * clear Session Instance for sign out.
     *
     * @author ssk
     */
    public static void clearInstance() {
        CocotvingApplication.getContext().deleteFile(TAG);
        INSTANCE = null;
    }

    /**
     * initialize Session with login data
     *
     * param data DataLogin
     * @author ssk
     */
    public static void initialize(Context context, LoginDataResponse response) {
        INSTANCE = new SessionInstance();
        INSTANCE.setLoginData(response);
        INSTANCE.setFriendList(new ArrayList<FriendItemResponse>());
        INSTANCE.serialize(context);
    }

    /**
     * Serialize Session Data
     */
    public void serialize(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(TAG, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
        Logger.i(TAG, "Session has been serialized");

    }

	public LoginDataResponse getLoginData() {
		return loginData;
	}
	public void setLoginData(LoginDataResponse loginData) {
		this.loginData = loginData;
	}
    public List<FriendItemResponse> getFriendList() {
        return friendList;
    }
    public void setFriendList(List<FriendItemResponse> friendList) {
        this.friendList = friendList;
    }
}
