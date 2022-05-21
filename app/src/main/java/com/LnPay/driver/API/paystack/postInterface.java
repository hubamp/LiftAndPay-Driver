package com.LnPay.driver.API.paystack;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface postInterface {

    @POST("initialize-payment")
    Call<ArrayList<postPaymentModel>> createPost(@Body postingData postingData);

    @POST("/public/api/success-payments")
    Call<afterInitializationPaymentModel> createPost(@Body afterInitializationPayment afterInitializationPayment);

    /*
     * https://apis.liftandpay.com/public/api/success-payments
     * */

    class afterInitializationPayment {

        private String reference_id;
        private String senders_id;
        private String email;
        private String currency;
        private double amount;

        public afterInitializationPayment(String reference_id, String senders_id, String email, String currency, double amount) {
            this.reference_id = reference_id;
            this.senders_id = senders_id;
            this.email = email;
            this.currency = currency;
            this.amount = amount;
        }

        /*
        * {
"reference_id":"w57vvpkm0w7gmt1",
"senders_id":"23494303",
"email":"kwesi@gmail.com",
"amount":"1",
"currency":"GHS"
}
        * */
    }

    class postingData {
        private String email;
        private double amount;
        private String senders_id;
        private String ride_id;

        postingData(String email, double amount, String senders_id, String ride_id) {

            this.email = email;
            this.amount = amount;
            this.senders_id = senders_id;
            this.ride_id = ride_id;
        }

    }
}
