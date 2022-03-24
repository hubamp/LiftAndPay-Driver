package com.LnPay.liftandpay_api;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;


public interface LnPayAPI_Interface {

    String BASE_URL = "https://apilif.liftandpay.com/api/";

    @GET("all-drivers")
    Call<driverModel> getDrivers();

    @GET("all-passengers")
    Call<driverModel> getPassengers();
}
