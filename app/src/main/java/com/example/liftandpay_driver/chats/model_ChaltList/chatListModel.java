package com.example.liftandpay_driver.chats.model_ChaltList;


import com.google.firebase.Timestamp;

public class chatListModel {

    private String nameOfPassenger, status, message, passengerId;
    private Timestamp Time;

    public chatListModel(String nameOfPassenger, String status, String message, String passengerId, Timestamp Time) {
        this.nameOfPassenger = nameOfPassenger;
        this.status = status;
        this.message = message;
        this.passengerId = passengerId;
        this.Time = Time;
    }


    public Timestamp getTime() {
        return Time;
    }

    public void setTime(Timestamp time) {
        Time = time;
    }

    public String getNameOfPassenger() {
        return nameOfPassenger;
    }

    public void setNameOfPassenger(String nameOfPassenger) {
        this.nameOfPassenger = nameOfPassenger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }
}
