package com.amol.realapp.chatty.model;

public class groupUsersAdded {
  private String userImage, userName;

  public groupUsersAdded() {}

  public groupUsersAdded(String userImage, String userName) {
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
}
