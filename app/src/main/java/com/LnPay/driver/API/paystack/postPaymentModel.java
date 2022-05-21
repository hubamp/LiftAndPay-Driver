package com.LnPay.driver.API.paystack;

import androidx.annotation.Keep;

import java.util.HashMap;

@Keep
public class postPaymentModel {


    private boolean status;
    private String message;
    private HashMap<String,String> data = new HashMap<>();

    protected class dataModel {
        private String authorization_url;
        private String access_code;
        private String reference;


        public String getAuthorization_url() {
            return authorization_url;
        }

        public String getAccess_code() {
            return access_code;
        }

        public String getReference() {
            return reference;
        }
    }

    postPaymentModel(){

    }

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String,String> getData(){
        return data;
    }

   /* public dataModel getData() {
        return new dataModel();
    }*/

    /*
    *
 [
    {
        "status": true,
        "message": "Authorization URL created",
        "data": {
            "authorization_url": "https://checkout.paystack.com/om8olhzyvia3f9v",
            "access_code": "om8olhzyvia3f9v",
            "reference": "q4vpfmn00k"
        }
    }
 ]
    * */

}
