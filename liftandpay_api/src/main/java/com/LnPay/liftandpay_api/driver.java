package com.LnPay.liftandpay_api;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class driver {

    @SerializedName("id")
    private String id;

    private String name;
    private String email;
    private String car_image;
    private String phone_number;
    private String about;
    private String car_model;
    private String car_color;
    private String number_plate;
    private int number_of_seats;
    private Timestamp created_at;
    private Timestamp updated_at;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCar_image() {
        return car_image;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getAbout() {
        return about;
    }

    public String getCar_model() {
        return car_model;
    }

    public String getCar_color() {
        return car_color;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    public int getNumber_of_seats() {
        return number_of_seats;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

}
