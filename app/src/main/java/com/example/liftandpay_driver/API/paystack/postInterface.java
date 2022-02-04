package com.example.liftandpay_driver.API.paystack;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface postInterface {

    @POST("initialize-payment")
    Call<ArrayList<postPaymentModel>> createPost(@Body postingData postingData);


     class postingData {
        private String email;
        private double amount;
        private String senders_id;
        private String ride_id;

        postingData(String email, double amount, String senders_id, String ride_id){

            this.email = email;
            this.amount = amount;
            this.senders_id =senders_id;
            this.ride_id = ride_id;
        }

    }
}
