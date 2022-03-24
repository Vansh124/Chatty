package com.amol.realapp.chatty.model;
import java.io.Serializable;

public class Status implements Serializable{
   private String imageUrl;
    private long timeStamp;

    public Status(){
        
    }
    
    public Status(String imageUrl, long timeStamp) {
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
    }
    
    


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    public static class myUserStatus implements Serializable{
     public myUserStatus(){
         
     }  
    }
 }
