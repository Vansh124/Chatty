package com.amol.realapp.chatty.model;

public class groupUsersAdded {
  private String userImage, userName, uid;

  public groupUsersAdded() {}

  public groupUsersAdded(String uid,String userImage, String userName) {
	this.uid=uid;
    this.userImage = userImage;
    this.userName = userName;
  }

  public void setUserImage(String userImage) {
    this.userImage = userImage;
  }

  public String getUserImage() {
    return userImage;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }

  public String getUid() {
    return this.uid;
  }

  public void setUid(java.lang.String uid) {
    this.uid = uid;
  }
}
