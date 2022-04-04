package com.LnPay.liftandpay_api;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;

public class driverModel {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;
    
    @SerializedName("All")
    private ArrayList<driver> All;


    driverModel() {

    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<driver> getDrivers() {
        return All;
    }


    public driver findDriver(String index){

        if (All==null){
            return null;
        }
        else {


            }
            return null;
        }

    public driver findDriver(int name){

        if (All==null){
            return null;
        }
        else {


        }
        return null;
    }





}
