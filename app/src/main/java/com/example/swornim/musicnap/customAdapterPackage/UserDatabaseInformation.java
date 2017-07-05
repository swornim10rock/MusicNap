package com.example.swornim.musicnap.customAdapterPackage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Swornim on 1/21/2017.
 */
public class UserDatabaseInformation implements Serializable {

    //just for the UI displaying of messages
    private String currentMessageTobeSent;
    private String isSongPlaying;
    private String streamNow;




    //songs related variables
    private String songsFullPathFirebase;
    private String musicnapRequest;//flag whether the messag is a request for first time
    private String didUserAcceptRequest;//flag if the user has accepted your reques// t
    private String firebaseSongName;

    private String uploadingSongName;
    private String uploadingFilePath;
    private String uploaderUserName;

    private String valueSongPath;
    private List<songList> uploadingSongsList;
    private String userName;//user name for distinguishing who called whom

    public String getUploaderUserName() {
        return uploaderUserName;
    }

    public void setUploaderUserName(String uploaderUserName) {
        this.uploaderUserName = uploaderUserName;
    }

    public String getStreamNow() {
        return streamNow;
    }

    public void setStreamNow(String streamNow) {
        this.streamNow = streamNow;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<songList> getUploadingSongsList() {
        return uploadingSongsList;
    }

    public void setUploadingSongsList(List<songList> uploadingSongsList) {
        this.uploadingSongsList = uploadingSongsList;
    }

    public String getValueSongPath() {
        return valueSongPath;
    }

    public void setValueSongPath(String valueSongPath) {
        this.valueSongPath = valueSongPath;
    }

    public String getUploadingSongName() {
        return uploadingSongName;
    }

    public void setUploadingSongName(String uploadingSongName) {
        this.uploadingSongName = uploadingSongName;
    }


    public String getUploadingFilePath() {
        return uploadingFilePath;
    }

    public void setUploadingFilePath(String uploadingFilePath) {
        this.uploadingFilePath = uploadingFilePath;
    }

    public String getFirebaseSongName() {
        return firebaseSongName;
    }

    public void setFirebaseSongName(String firebaseSongName) {
        this.firebaseSongName = firebaseSongName;
    }

    public String getDidUserAcceptRequest() {
        return didUserAcceptRequest;
    }

    public void setDidUserAcceptRequest(String didUserAcceptRequest) {
        this.didUserAcceptRequest = didUserAcceptRequest;
    }

    public String getSongsFullPathFirebase() {
        return songsFullPathFirebase;
    }

    public void setSongsFullPathFirebase(String songsFullPathFirebase) {
        this.songsFullPathFirebase = songsFullPathFirebase;
    }

    public String getMusicnapRequest() {
        return musicnapRequest;
    }

    public void setMusicnapRequest(String musicnapRequest) {
        this.musicnapRequest = musicnapRequest;
    }

    public String getIsSongPlaying() {
        return isSongPlaying;
    }

    public void setIsSongPlaying(String isSongPlaying) {
        this.isSongPlaying = isSongPlaying;
    }

    private String currentAppUserName;
    private String phoneNumber;

    //friendslist
    private String friendName;
    private String favourite;

    //songslist
    private String songName;


    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getCurrentMessageTobeSent() {
        return currentMessageTobeSent;
    }

    public void setCurrentMessageTobeSent(String currentMessageTobeSent) {
        this.currentMessageTobeSent = currentMessageTobeSent;
    }



    public String getCurrentAppUserName() {
        return currentAppUserName;
    }

    public void setCurrentAppUserName(String currentAppUserName) {
        this.currentAppUserName = currentAppUserName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }


}
