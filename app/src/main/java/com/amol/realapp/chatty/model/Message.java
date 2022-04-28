package com.amol.realapp.chatty.model;

public class Message {

  private String messageId;
  private String message, senderId, imageUrl;
  private long timeStamp;
  private String pdfName, pdfUrl;

  public Message() {}

  public Message(String message, String senderId, long timeStamp) {
    this.message = message;
    this.senderId = senderId;
    this.timeStamp = timeStamp;
  }

  public void setPdfName(String pdfName) {
    this.pdfName = pdfName;
  }

  public String getPdfName() {
    return pdfName;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setPdfUrl(String pdfUrl) {
    this.pdfUrl = pdfUrl;
  }

  public String getPdfUrl() {
    return pdfUrl;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public long getTimeStamp() {
    return timeStamp;
  }
}
