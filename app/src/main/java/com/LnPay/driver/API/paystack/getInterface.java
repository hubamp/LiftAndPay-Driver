package com.LnPay.driver.API.paystack;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface getInterface {

    @GET("public/api/transaction-ride/")
    Call<paymentResponseData> getPaymentResponseFromRide(@Query("ride_id") String ride_id);

/*    @GET("public/api/transaction-ride/")
    Call<paymentResponseData> getPaymentResponseFromDriver(@Query("senders_id") String senders_id);    */


    @GET("/public/api/all-payments")
    Call<paymentResponseData> getAllPaymentsFromDriver(@Query("senders_id") String senders_id);

    @GET("public/api/transaction-ride/")
    Call<paymentResponseData> getPaymentResponse(@Query("senders_id") String senders_id, @Query("ride_id") String ride_id);

//    https://apis.liftandpay.com/public/api/pending-charge/x2nowhwope

    @GET("public/api/pending-charge/{reference}")
    Call<ArrayList<pendingCharge>> checkPendingCharge(@Path("reference") String referenceId);


}
