package com.LnPay.driver.chats.model_ChatActivity;

public class messageModel {

    private String message;
    private int vType;
    private int time;
    private int image;

    public messageModel(String message, int vType){
        this.message = message;
        this.vType =vType;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getvType() {
        return vType;
    }

    public void setvType(int vType) {
        this.vType = vType;
    }
}
