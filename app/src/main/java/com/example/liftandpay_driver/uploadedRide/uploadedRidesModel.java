package com.example.liftandpay_driver.uploadedRide;

public class uploadedRidesModel {

    private String JOURNEY;
    private String RIDEDATE;
    private String RIDETIME;
    private int NUMBEROFREQUESTS;


    public uploadedRidesModel(String JOURNEY, String RIDEDATE, String RIDETIME, int NUMBEROFREQUESTS) {
        this.JOURNEY = JOURNEY;
        this.RIDEDATE = RIDEDATE;
        this.RIDETIME = RIDETIME;
        this.NUMBEROFREQUESTS = NUMBEROFREQUESTS;
    }


    public String getJOURNEY() {
        return JOURNEY;
    }

    public void setJOURNEY(String JOURNEY) {
        this.JOURNEY = JOURNEY;
    }

    public String getRIDEDATE() {
        return RIDEDATE;
    }

    public void setRIDEDATE(String RIDEDATE) {
        this.RIDEDATE = RIDEDATE;
    }

    public String getRIDETIME() {
        return RIDETIME;
    }

    public void setRIDETIME(String RIDETIME) {
        this.RIDETIME = RIDETIME;
    }

    public int getNUMBEROFREQUESTS() {
        return NUMBEROFREQUESTS;
    }

    public void setNUMBEROFREQUESTS(int NUMBEROFREQUESTS) {
        this.NUMBEROFREQUESTS = NUMBEROFREQUESTS;
    }

}
