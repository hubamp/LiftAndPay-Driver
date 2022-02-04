package com.example.liftandpay_driver.API.paystack;

import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class paystack {

    public static String authUrl;
    public static String paymentRef;
    public static String access_code;
    public static postDataResults dataresult;

    public paystack() {
    }


    public postDataResults postData(String email, double amount, String sendersId, String rideId) {
        // on below line we are creating a retrofit
        // builder and passing our base urll
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.liftandpay.com/public/api/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();
        // below line is to create an instance for our retrofit api class.
        postInterface retrofitAPI = retrofit.create(postInterface.class);

        postInterface.postingData postingData = new postInterface.postingData(email, amount,sendersId,rideId);

        retrofitAPI.createPost(postingData).enqueue(new Callback<ArrayList<postPaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<postPaymentModel>> call, Response<ArrayList<postPaymentModel>> response) {

                if (response.isSuccessful()){

                    Log.e("Payment response", response.body().toString());
                    Log.e("Payment execution", "Successful");
                    Log.e("authorization_url", response.body().get(0).getData().get("authorization_url")+"h");

                    authUrl = response.body().get(0).getData().get("authorization_url");
                    paymentRef = response.body().get(0).getData().get("reference");
                    access_code = response.body().get(0).getData().get("access_code");
                    dataresult =  new postDataResults(authUrl,paymentRef,access_code);


                }



            }

            @Override
            public void onFailure(Call<ArrayList<postPaymentModel>> call, Throwable t) {
                Log.e("Payment Fails", t.getLocalizedMessage());
            }



        });


        Log.e("Payment auth", "dataresult "+dataresult);

        return dataresult;

    }

    public class postDataResults extends paystack {

        private String authUrl;
        private String paymentRef;
        private String access_code;



        public postDataResults(String authUrl, String paymentRef, String access_code) {
            this.authUrl = authUrl;
            this.paymentRef = paymentRef;
            this.access_code = access_code;
            Log.e("Payment auth001", "auth "+authUrl);

        }


        public String getAuthUrl() {
            return authUrl;
        }



        public String getPaymentRef() {
            return paymentRef;
        }

        public String getAccess_code() {
            return access_code;
        }
    }

}
