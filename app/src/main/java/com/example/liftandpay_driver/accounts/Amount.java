package com.example.liftandpay_driver.accounts;

public class Amount {
    private double money;
    private String name;
    private String date;

    public Amount(double money, String name, String date){

        this.money = money;
        this.name = name;
        this.date = date;
    }

    public Double getMoney(){
        return money;
    }

    public void setMoney(Double money){
        this.money = money;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

}
