package com.LnPay.driver.API.paystack;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;

import com.LnPay.driver.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class paystack {

    private Context context;

    /*Data For Posting*/
    public String authUrl;
    static public String paymentRef;
    public String access_code;
    public postDataResults dataresult;

    /*Data For Fetching*/
    private String senders_id;
    private String ride_id;


    /*Conditional*/
    public String isSuccessful;
    public ArrayList<paymentResponseData.fetchedData> fetchedDataResult;

    public paystack(Context context) {
        this.context = context;
    }


    public postDataResults postData(String email, double amount, String sendersId, String rideId) {

        Looper.prepare();
        ///Create a popup.
        AlertDialog.Builder waiting_progressAlert = new AlertDialog.Builder(context);
        waiting_progressAlert.setView(LayoutInflater.from(context).inflate(R.layout.alert_payment_progress, null));
        waiting_progressAlert.setCancelable(false);
        AlertDialog waitingDialog = waiting_progressAlert.create();

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

        postInterface.postingData postingData = new postInterface.postingData(email, amount, sendersId, rideId);

        retrofitAPI.createPost(postingData).enqueue(new Callback<ArrayList<postPaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<postPaymentModel>> call, Response<ArrayList<postPaymentModel>> response) {

                if (response.isSuccessful()) {

                  /*  Log.e("Payment response", response.body().toString());
                    Log.e("Payment execution", "Successful");
                    Log.e("authorization_url", response.body().get(0).getData().get("authorization_url") + "h");
*/
                    authUrl = response.body().get(0).getData().get("authorization_url");
                    paymentRef = response.body().get(0).getData().get("reference");
                    access_code = response.body().get(0).getData().get("access_code");
                    dataresult = new postDataResults(authUrl, paymentRef, access_code);


                } else {
//                    Log.e("Payment response", "Not successful");
                    authUrl = "NoURL";
                    paymentRef = "NoRef";
                    access_code = "NoCode";
                    dataresult = new postDataResults(authUrl, paymentRef, access_code);


                }


            }

            @Override
            public void onFailure(Call<ArrayList<postPaymentModel>> call, Throwable t) {
//                Log.e("Payment Fails", t.getLocalizedMessage());
                authUrl = "NoURL";
                paymentRef = "NoRef";
                access_code = "NoCode";
                dataresult = new postDataResults(authUrl, paymentRef, access_code);


            }


        });


        do {
            Log.e("Payment auth", "dataresult " + dataresult);
        }
        while (dataresult == null);

        waitingDialog.dismiss();
        Log.e("Payment auth returned", "dataresult " + dataresult.authUrl);
        return dataresult;
    }

    public class postDataResults {

        private String authUrl;
        private String paymentRef;
        private String access_code;


        public postDataResults(String authUrl, String paymentRef, String access_code) {
            this.authUrl = authUrl;
            this.paymentRef = paymentRef;
            this.access_code = access_code;
//            Log.e("Payment auth001", "auth " + authUrl);

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


    public ArrayList<paymentResponseData.fetchedData> fetchData(String senders_id) {

        /*Create waiting progress*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.liftandpay.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getInterface getApiInterface = retrofit.create(getInterface.class);

        getApiInterface.getAllPaymentsFromDriver(senders_id).enqueue(new Callback<paymentResponseData>() {
            @Override
            public void onResponse(Call<paymentResponseData> call, Response<paymentResponseData> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    isSuccessful = "Successful";
                    fetchedDataResult = response.body().getAll();

                } else {
                    isSuccessful = "NotSuccessful";
                }
                Log.i("PaymentCallbackResponse", isSuccessful);

            }

            @Override
            public void onFailure(Call<paymentResponseData> call, Throwable t) {

                isSuccessful = "Failed";
                Log.i("PaymentCallbackResponse", isSuccessful);


            }
        });


        do {
//            System.out.println(" "+isSuccessful);
            Log.i("PaymentCallbackResponse", "responseStatus :" + isSuccessful);

        }
        while (isSuccessful == null);


        Log.i("PaystackResponse", "pay " + isSuccessful);
        return fetchedDataResult;
    }


    public void postDataAfterInitialization(String reference_id, double amount, String currency, String senders_id, String email) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.liftandpay.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        postInterface getInterface = retrofit.create(postInterface.class);

        Call<afterInitializationPaymentModel> myModel = getInterface.createPost(new postInterface.afterInitializationPayment(reference_id, senders_id, email, currency, amount));

        myModel.enqueue(new Callback<afterInitializationPaymentModel>() {
            @Override
            public void onResponse(Call<afterInitializationPaymentModel> call, Response<afterInitializationPaymentModel> response) {

                if (response.isSuccessful()) {
                    Log.i("LooperPrepare", "Hello enqued success");
                } else {
                    Log.i("LooperPrepare", "Hello " + response.code());
                }
            }

            @Override
            public void onFailure(Call<afterInitializationPaymentModel> call, Throwable t) {
                Log.i("LooperPrepare", "Hello enqueued Failed");

            }
        });


    }


    public void checkPendingCharge(String referenceId, @Nullable AlertDialog alertDialog) {

        Log.i("Charge response", "Ref = " + referenceId + ", AlertDialog = " +
                alertDialog);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.liftandpay.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(getInterface.class).checkPendingCharge(referenceId).enqueue(new Callback<ArrayList<pendingCharge>>() {
            @Override
            public void onResponse(Call<ArrayList<pendingCharge>> call, Response<ArrayList<pendingCharge>> response) {
                Log.i("Pending Charge 000", "Success");

                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        Log.i("Pending Charge 000", "get :"+ response.body().get(0));
                        Log.i("Pending Charge 000", "data :"+ response.body().get(0).getData());
                        Log.i("Pending Charge 000", "channel :"+ response.body().get(0).getData().getChannel());


                            String channel = response.body().get(0).getData().getChannel();
                            String message = response.body().get(0).getData().getMessage();
                            String status = response.body().get(0).getData().getStatus();

                        Log.i("Pending Charge 001", "Success");
                        Log.i("Charge response", "Channel = " + channel + ", Message = " +
                                response.body().get(0).getMessage() +
                                ", status = " + status);


                        if (!channel.equalsIgnoreCase("null") && !status.equals("null"))
                        //Call the put request
                        {
                            Log.i("Pending Charge 000", "Updated Message: " + message);
                            updatePaymentWithPaymentSuccess(status, channel, referenceId, alertDialog);
                        }
                    } else {
                        Log.i("Pending Charge 000", "Failed");

                        //Toast a fail
                    }
                    }
                    else {
                        Log.i("Pending Charge 000", "Response Body is null");

                    }


            }

            @Override
            public void onFailure(Call<ArrayList<pendingCharge>> call, Throwable t) {
                Log.i("Pending Charge 000", "Failed");

            }
        });


    }


    void updatePaymentWithPaymentSuccess(String status, String channel, String reference_id, AlertDialog alertDialog) {
        Log.i("Update 000", "Started");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.liftandpay.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(updateInterface.class)
                .updatePost(new updateInterface.updateBody(status,channel),reference_id).enqueue(new Callback<afterInitializationPaymentModel>() {
            @Override
            public void onResponse(Call<afterInitializationPaymentModel> call, Response<afterInitializationPaymentModel> response) {
                Log.i("Update 000", "Successful: "+response.message());

                if (response.isSuccessful()){
                    Log.i("Update 000", "SuccessFul: responded");
                }
                else  {
                    Log.i("Update 000", "SuccessFul: Failed to respond");

                }

                if (alertDialog !=null)
                {
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<afterInitializationPaymentModel> call, Throwable t) {
                Log.i("Update 000", "Failed: "+t.getMessage());

                if (alertDialog !=null)
                {
                    alertDialog.dismiss();
                }

            }
        });

    }

}
