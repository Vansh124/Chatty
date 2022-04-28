package com.amol.realapp.chatty.model;

public class groupProfile {

  private String groupUid, groupName, groupProfile;

  public groupProfile(String groupUid, String groupName, String groupProfile) {
    this.groupUid = groupUid;
    this.groupName = groupName;
    this.groupProfile = groupProfile;
  }

  public groupProfile() {}

  public void setGroupUid(String groupUid) {
    this.groupUid = groupUid;
  }

  public String getGroupUid() {
    return groupUid;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupProfile(String groupProfile) {
    this.groupProfile = groupProfile;
  }

  public String getGroupProfile() {
    return groupProfile;
  }
}
