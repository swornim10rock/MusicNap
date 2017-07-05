package com.example.swornim.musicnap.customAdapterPackage;

import java.util.List;

/**
 * Created by Swornim on 6/3/2017.
 */

public class SocialDetails {

    private String musicStatus;
    private String musicStatusSongLink;
    private List<String> listOfSongs;

    //headset and mediaplayer related
    private String headSetPlugged;
    private String mediaplayerPlaying;


    public String getMediaplayerPlaying() {
        return mediaplayerPlaying;
    }

    public void setMediaplayerPlaying(String mediaplayerPlaying) {
        this.mediaplayerPlaying = mediaplayerPlaying;
    }

    public String getHeadSetPlugged() {
        return headSetPlugged;
    }

    public void setHeadSetPlugged(String headSetPlugged) {
        this.headSetPlugged = headSetPlugged;
    }

    public String getMusicStatus() {
        return musicStatus;
    }

    public void setMusicStatus(String musicStatus) {
        this.musicStatus = musicStatus;
    }

    public String getMusicStatusSongLink() {
        return musicStatusSongLink;
    }

    public void setMusicStatusSongLink(String musicStatusSongLink) {
        this.musicStatusSongLink = musicStatusSongLink;
    }

    public List<String> getListOfSongs() {
        return listOfSongs;
    }

    public void setListOfSongs(List<String> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }
}
