package com.example.liftandpay_driver.accounts;

public class accountModel {
    private String start;
    private String end;
    private double totalAmount;


    public accountModel(String start, String end, double totalAmount) {
        this.start = start;
        this.end = end;
        this.totalAmount = totalAmount;
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
}
