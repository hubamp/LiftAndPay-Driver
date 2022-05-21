package com.LnPay.driver.API.paystack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface updateInterface {

//    https://apis.liftandpay.com/public/api/payment-update/yv1nqknhx9
    @PUT("/public/api/payment-update/{ref}")
    Call<afterInitializationPaymentModel> updatePost(@Body updateBody updateBody,@Path("ref") String ref);

    class updateBody{

        private String payment_status;
        private String payment_channel;

        public updateBody(String payment_status, String payment_channel) {
            this.payment_status = payment_status;
            this.payment_channel = payment_channel;
        }
    }

}
