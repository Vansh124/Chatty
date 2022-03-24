package com.amol.realapp.chatty.model;

public class userProfile {
    String uid,name,phoneNumber,userProfileImage;
    public userProfile(){

    
    }
    public userProfile(String uid, String name, String phoneNumber, String userProfileImage)
    {
    this.uid = uid;
    this.name = name;
        this.phoneNumber = phoneNumber;
    this.userProfileImage = userProfileImage;
    }
    
    


    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }}
