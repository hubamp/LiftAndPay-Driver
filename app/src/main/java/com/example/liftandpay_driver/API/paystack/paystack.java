package com.example.liftandpay_driver.API.paystack;

import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class paystack {

    private String email;
    private double amount;

    public paystack() {
    }

    public void postData(String email, double amount) {
        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.liftandpay.com/public/api/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();
        // below line is to create an instance for our retrofit api class.
        postInterface retrofitAPI = retrofit.create(postInterface.class);

        // passing data from our text fields to our modal class.
        postPaymentModel postPaymentModel = new postPaymentModel();
        
        retrofitAPI.createPost(postPaymentModel).enqueue(new Callback<ArrayList<postPaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<postPaymentModel>> call, Response<ArrayList<postPaymentModel>> response) {

                Log.i("Payment", "Successful");
                Log.i("authorization code", response.body().get(0).getData().getAuthorization_url());

            }

            @Override
            public void onFailure(Call<ArrayList<postPaymentModel>> call, Throwable t) {
                Log.i("Payment", t.getLocalizedMessage());

            }
        });
        
        

        // calling a method to create a post and passing our modal class.
        
    }
}
