package com.amol.realapp.chatty.model;

import java.util.ArrayList;

public class userStatus {
  private String name, profileImage;
  private String uid;
  private long lastUpdated;
  private ArrayList<Status> statuses;

  public userStatus() {}

  public userStatus(
      String uid, String name, String profileImage, long lastUpdated, ArrayList<Status> statuses) {
    this.uid = uid;
    this.name = name;
    this.profileImage = profileImage;
    this.lastUpdated = lastUpdated;
    this.statuses = statuses;
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

  public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

  public String getProfileImage() {
    return profileImage;
  }

  public void setLastUpdated(long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public long getLastUpdated() {
    return lastUpdated;
  }

  public void setStatuses(ArrayList<Status> statuses) {
    this.statuses = statuses;
  }

  public ArrayList<Status> getStatuses() {
    return statuses;
  }
}
