package com.LnPay.driver.API.paystack;

public class pendingCharge {

    private String status;
    private String message;
    private PendingData data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public PendingData getData() {
        return data;
    }



    class PendingData {
        private String reference;
        private String status;
        private String message;
        private String channel;

        public String getReference() {
            return reference;
        }

        public String getChannel() {
            return channel;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    /*
    * [
    {
        "status": true,
        "message": "Reference check successful",
        "data": {
            "reference": "x2nowhwope",
            "status": "failed",
            "message": "The transaction was not completed"
        }
    }
]
    * */
}
