package com.example.liftandpay_driver.API.paystack;

public class postPaymentModel {

    private String status;
    private double message;
    public class theData{
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

    public String getStatus() {
        return status;
    }

    public double getMessage() {
        return message;
    }

    public theData getData(){
        return new theData();
    }
    /*
    *
        "status": true,
        "message": "Authorization URL created",
        "data": {
            "authorization_url": "https://checkout.paystack.com/oyty6b35f6yy8y8",
            "access_code": "oyty6b35f6yy8y8",
            "reference": "g3ffp0igez"
    * */

}
