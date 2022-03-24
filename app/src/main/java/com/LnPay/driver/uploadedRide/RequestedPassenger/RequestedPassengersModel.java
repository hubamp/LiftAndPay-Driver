package com.LnPay.driver.uploadedRide.RequestedPassenger;

public class RequestedPassengersModel {
    private String  PASSENGERNAME;
    private String  PICKUPLOCATION;
    private String  PASSENGERID;
    private double PICKUPLONG, PICKUPLAT;
    private String STATUS;


    public RequestedPassengersModel(String PASSENGERNAME, String PICKUPLOCATION, String PASSENGERID, double PICKUPLONG, double PICKUPLAT, String STATUS) {
        this.PASSENGERNAME = PASSENGERNAME;
        this.PICKUPLOCATION = PICKUPLOCATION;
        this.PASSENGERID = PASSENGERID;
        this.PICKUPLONG = PICKUPLONG;
        this.PICKUPLAT = PICKUPLAT;
        this.STATUS = STATUS;
    }

    public String getPASSENGERNAME() {
        return PASSENGERNAME;
    }

    public void setPASSENGERNAME(String PASSENGERNAME) {
        this.PASSENGERNAME = PASSENGERNAME;
    }

    public String getPICKUPLOCATION() {
        return PICKUPLOCATION;
    }

    public void setPICKUPLOCATION(String PICKUPLOCATION) {
        this.PICKUPLOCATION = PICKUPLOCATION;
    }

    public String getPASSENGERID() {
        return PASSENGERID;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void setPASSENGERID(String PASSENGERID) {
        this.PASSENGERID = PASSENGERID;
    }

    public double getPICKUPLONG() {
        return PICKUPLONG;
    }

    public void setPICKUPLONG(double PICKUPLONG) {
        this.PICKUPLONG = PICKUPLONG;
    }

    public double getPICKUPLAT() {
        return PICKUPLAT;
    }

    public void setPICKUPLAT(double PICKUPLAT) {
        this.PICKUPLAT = PICKUPLAT;
    }
}
