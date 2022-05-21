package com.LnPay.driver.accounts;

public class accountModel {
    private String start;
    private String end;
    private String rideId;
    private double totalAmount;

    private String startLat, startLon, endLat, endLon;


    public accountModel(String start, String end, double totalAmount, String rideId, String startLat, String startLon, String endLat, String endLon) {
        this.start = start;
        this.end = end;
        this.totalAmount = totalAmount;
        this.rideId = rideId;
        this.startLat =startLat;
        this.startLon = startLon;
        this.endLat = endLon;
    }

    public String getStartLon() {
        return startLon;
    }

    public String getStartLat() {
        return startLat;
    }

    public String getEndLat() {
        return endLat;
    }

    public String getEndLon() {
        return endLon;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
