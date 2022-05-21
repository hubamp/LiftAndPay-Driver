package com.LnPay.driver.API.paystack;

import java.util.ArrayList;

public class paymentResponseData {

    private String message;
    private int status;
    private ArrayList<fetchedData> All;

    public paymentResponseData(String message, int status, ArrayList<fetchedData> all) {
        this.message = message;
        this.status = status;
        All = all;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<fetchedData> getAll() {
        return All;
    }

     public class fetchedData{
        int id;
        String email;
        double amount;
        String senders_id;
        String deleted_at;
        String created_at;
        String updated_at;

        String currency;
        String reference_id;
        String payment_channel;
        String payment_status;

        /* "id": 10,
            "payment_channel": null,
            "payment_status": null,
            "reference_id": "r8gkb5o1xo",
            "senders_id": "tp2cpThBQ5eqe6tj4ZLqxv1bFP02",
            "email": "noemail@liftandpay.com",
            "amount": "2",
            "currency": "GHS",
            "created_at": "2022-05-12T15:16:26.000000Z",
            "updated_at": "2022-05-12T15:16:26.000000Z"*/

         public fetchedData(int id, String email, double amount, String senders_id, String deleted_at, String created_at, String updated_at, String currency, String reference_id, String payment_channel, String payment_status) {
             this.id = id;
             this.email = email;
             this.amount = amount;
             this.senders_id = senders_id;
             this.deleted_at = deleted_at;
             this.created_at = created_at;
             this.updated_at = updated_at;
             this.currency = currency;
             this.reference_id = reference_id;
             this.payment_channel = payment_channel;
             this.payment_status = payment_status;
         }

         public int getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public double getAmount() {
            return amount;
        }

         public String getCurrency() {
             return currency;
         }

         public String getPayment_channel() {
             return payment_channel;
         }

         public String getPayment_status() {
             return payment_status;
         }

         public String getReference_id() {
             return reference_id;
         }

         public String getSenders_id() {
            return senders_id;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }


}
