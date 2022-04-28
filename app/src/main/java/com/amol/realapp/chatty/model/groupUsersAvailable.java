package com.amol.realapp.chatty.model;

public class groupUsersAvailable {

  private String uid, userAvailImage, userAvailName;

  public groupUsersAvailable() {}

  public groupUsersAvailable(String uid, String userAvailImage, String userAvailName) {
    this.uid = uid;
    this.userAvailImage = userAvailImage;
    this.userAvailName = userAvailName;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUid() {
    return uid;
  }

  public void setUserAvailImage(String userAvailImage) {
    this.userAvailImage = userAvailImage;
  }

  public String getUserAvailImage() {
    return userAvailImage;
  }

  public void setUserAvailName(String userAvailName) {
    this.userAvailName = userAvailName;
  }

  public String getUserAvailName() {
    return userAvailName;
  }
}
