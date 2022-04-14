package com.example.chatty;

import android.net.Uri;

import java.net.URI;

public class VoiceMessages {

    String audioName;
    Uri voiceUID;
    String senderId;
    long timestamp;
    String currentTime;


    public VoiceMessages() {
    }

    public VoiceMessages(String audioName, Uri voiceUID, String senderId, long timestamp, String currentTime) {
        this.audioName = audioName;
        this.voiceUID = voiceUID;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.currentTime = currentTime;
    }

    public Uri getVoiceUID() {
        return voiceUID;
    }

    public void setVoiceUID(Uri voiceUID) {
        this.voiceUID = voiceUID;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }
}//end of class
