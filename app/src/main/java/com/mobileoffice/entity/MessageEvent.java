package com.mobileoffice.entity;

/**
 * Created by 99213 on 2017/8/30.
 */

public class MessageEvent {
    private String message;
    public MessageEvent(String message){
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
