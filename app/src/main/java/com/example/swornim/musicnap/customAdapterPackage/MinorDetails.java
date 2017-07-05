package com.example.swornim.musicnap.customAdapterPackage;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Swornim on 5/19/2017.
 */

public class MinorDetails implements Serializable{
    //this class holds information regarding stories and other features

    private String userStoryMessage;
    private String userStoryTitle;

    public String getUserStoryMessage() {
        return userStoryMessage;
    }

    public void setUserStoryMessage(String userStoryMessage) {
        this.userStoryMessage = userStoryMessage;
    }

    public String getUserStoryTitle() {
        return userStoryTitle;
    }

    public void setUserStoryTitle(String userStoryTitle) {
        this.userStoryTitle = userStoryTitle;
    }
}
