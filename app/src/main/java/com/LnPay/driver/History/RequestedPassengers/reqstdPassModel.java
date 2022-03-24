package com.LnPay.driver.History.RequestedPassengers;

public class reqstdPassModel {

    private String name, pickupLocation, status;

    public reqstdPassModel(String name, String pickupLocation, String status) {
        this.name = name;
        this.pickupLocation = pickupLocation;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
