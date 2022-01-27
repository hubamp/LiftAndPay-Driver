package com.example.liftandpay_driver.API.paystack;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface postInterface {

    @POST("initialize-payment")
    Call<ArrayList<postPaymentModel>> createPost(@Body postPaymentModel postPaymentModel);
}
