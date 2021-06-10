package com.example.liftandpay_driver.chats.model_ChaltList;

public class chatListModel {

    private String nameOfPassenger, status, message, passengerId;

    public chatListModel(String nameOfPassenger, String status, String message, String passengerId) {
        this.nameOfPassenger = nameOfPassenger;
        this.status = status;
        this.message = message;
        this.passengerId = passengerId;
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
