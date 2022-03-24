package com.LnPay.liftandpay_api;

import java.util.Observable;

public class myObserver implements java.util.Observer {

    private String gotten;

    @Override
    public void update(Observable o, Object arg) {


        boolean isReceived = (boolean) arg;
        if (isReceived) {
            gotten = "false";
        }
        else
            gotten = "true";


    }


    public String getGotten() {
        return gotten;
    }
}
