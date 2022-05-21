package com.LnPay.driver.accounts;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.API.paystack.Waiting_ProgressAlert;
import com.LnPay.driver.API.paystack.paymentResponseData;
import com.LnPay.driver.API.paystack.paystack;
import com.LnPay.driver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private paymentAccountAdapter paymentAccountAdapter;
    public static ArrayList<accountModel> accountModels = new ArrayList<>();
    private Double totalAmount;
    private String mUid = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Double totalRideAmount = 0.0;
    public Double totalAmountPaid = 0.0;
    public Double totalAmountLeft = 0.0;
    private int counter;
    private TextView overallAmountView;
    private TextView makeOverallPaymentBtn;
    private ImageView backBtn;
    private Thread paymentThread;
    private Thread paymentThreadCheck;
    public ArrayList<paymentResponseData.fetchedData> fetchedData;
    //    private Waiting_ProgressAlert waiting_progressAlert;
    private AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        recyclerView = findViewById(R.id.paymentRecycler);
        overallAmountView = findViewById(R.id.overallAmount);
        makeOverallPaymentBtn = findViewById(R.id.makepaymentBtn);
        backBtn = findViewById(R.id.back_arrow);

        AlertDialog.Builder waiting_progressAlert = new AlertDialog.Builder(this);
        waiting_progressAlert.setView(LayoutInflater.from(this).inflate(R.layout.alert_payment_progress, null));
        waiting_progressAlert.setCancelable(false);
        waitingDialog = waiting_progressAlert.create();
        waitingDialog.show();

        /* waiting_progressAlert = new Waiting_ProgressAlert(this);

         waiting_progressAlert.show();
*/

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        paymentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                paystack payStackResponse = new paystack(AccountActivity.this);
                fetchedData = payStackResponse.fetchData("tp2cpThBQ5eqe6tj4ZLqxv1bFP02");


                Log.i("PaystackResponse", " Responded");


            }
        });


        paymentThreadCheck = new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();

                try {
                    paymentThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int loop = 0; loop < fetchedData.size(); loop++) {
                    if (fetchedData.get(loop).getPayment_status() != null) {
                        if (fetchedData.get(loop).getPayment_status().equalsIgnoreCase("success")) {
                            totalAmountPaid = totalAmountPaid + fetchedData.get(loop).getAmount();
                        }
                    }
                }


                Log.i("Amount", "Total Amount Paid: " + totalAmountPaid);


                db.collection("Rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        accountModels.clear();
                        counter = 0;
                        int documentSize = queryDocumentSnapshots.size();

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {


                            if (doc.getReference().getId().contains(mUid)) {

                                Log.i("totalAmount before multiplication", totalAmount + "");
                                doc.getReference().collection("Booked By").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot qDocSnapshot) {
                                        counter++;
                                        Log.i("Counter book", "c// " + counter);

                                        if (doc.get("rideCost") instanceof String) {

                                            totalAmount = Double.parseDouble(doc.getString("rideCost")) * 0.12;

                                        } else {
                                            totalAmount = doc.getDouble("rideCost") * 0.12;
                                        }
                                        totalAmount = totalAmount * qDocSnapshot.getDocuments().size();
                                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                        totalAmount = Double.valueOf(decimalFormat.format(totalAmount));

                                        Log.i("totalAmount during multiplication", totalAmount + "");
                                        Log.i("totalSize during multiplication", qDocSnapshot.getDocuments().size() + "");


                                        Log.i("totalAmount after multiplication", totalAmount + "");


                                        totalRideAmount = Double.valueOf(decimalFormat.format(totalRideAmount + totalAmount));

                                        accountModels.add(new accountModel(
                                                doc.getString("startLocation"),
                                                doc.getString("endLocation"),
                                                totalAmount,
                                                doc.getReference().getId(),
                                                Objects.requireNonNull(doc.getData().get("startLat")).toString(),
                                                Objects.requireNonNull(doc.getData().get("startLon")).toString(),
                                                Objects.requireNonNull(doc.getData().get("endLat")).toString(),
                                                Objects.requireNonNull(doc.getData().get("endLon")).toString()

                                        ));


                                        //Activate payment parameters
                                        makeOverallPaymentBtn.setEnabled(true);
                                        paymentAccountAdapter = new paymentAccountAdapter(AccountActivity.this, accountModels);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(AccountActivity.this, LinearLayoutManager.VERTICAL, false));
                                        recyclerView.setAdapter(paymentAccountAdapter);
                                        overallAmountView.setText("GHC" + totalRideAmount.toString());

                                        makeOverallPaymentBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if (totalAmountLeft == 0) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(AccountActivity.this, "Amount is 0.0", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                } else {
                                                    preparePayment();
                                                }
                                            }
                                        });

                                        if (counter == documentSize) {

                                            Log.i("Counter count", "c++ " + counter);
                                            Log.i("Counter docs", "c-- " + documentSize);

                                            totalAmountLeft = totalRideAmount - totalAmountPaid;

                                            totalAmountLeft = Double.valueOf(decimalFormat.format(totalAmountLeft));

                                            if (totalAmountLeft < 0) {
                                                totalAmountLeft = totalAmountLeft * -1;
                                                overallAmountView.setText("+ GHC" + totalAmountLeft);
                                            } else {
                                                overallAmountView.setText("- GHC" + totalAmountLeft);
                                            }

                                            waitingDialog.dismiss();
                                        } else {
                                            Log.i("Counter count", "c-- " + counter);
                                            Log.i("Counter docs", "c-- " + documentSize);

                                        }


                                        Log.i("overall", "00 " + totalRideAmount);

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                counter++;
                                            }
                                        });

                                Log.i("overall", "01 " + totalRideAmount);


                            } else {
                                counter++;
                            }


                            Log.i("overall", "02 " + totalRideAmount);

                        }


                        Log.i("overall", "03 " + totalRideAmount);


                    }
                });

            }
        });


        paymentThread.start();
        paymentThreadCheck.start();


    }

    void preparePayment() {

        db.collection("Driver").document(mUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String email = documentSnapshot.getString("email");
                if (email != null) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paystack.postDataResults myPost = new paystack(AccountActivity.this).
                                    postData(
                                            "noemail@liftandpay.com",
                                            totalAmountLeft,
                                            mUid,
                                            "rideId"
                                    );

                            Log.i("Payment auth Dashboard", myPost.getAuthUrl());
                            Log.e("payment URL RETRIEVED", myPost.getAuthUrl());
                            Intent i = new Intent(AccountActivity.this, Payment.class);
                            i.putExtra(
                                    "authUrl",
                                    myPost.getAuthUrl());
                            new paystack(AccountActivity.this).postDataAfterInitialization(myPost.getPaymentRef(), totalAmountLeft, "GHS", mUid, email);

                            startActivity(i);
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paystack.postDataResults myPost = new paystack(AccountActivity.this).
                                    postData(
                                            "noemail@liftandpay.com",
                                            totalAmountLeft,
                                            mUid,
                                            "rideId"
                                    );

                            Log.i("Payment auth Dashboard", myPost.getAuthUrl());
                            Log.e("payment URL RETRIEVED", myPost.getAuthUrl());
                            Intent i = new Intent(AccountActivity.this, Payment.class);
                            i.putExtra(
                                    "authUrl",
                                    myPost.getAuthUrl());

                            Log.i("PaystackResponse", myPost.getAuthUrl());
                            new paystack(AccountActivity.this).postDataAfterInitialization(myPost.getPaymentRef(), totalAmountLeft, "GHS", mUid, "noemail@liftandpay.com");
                            Log.i("PaystackResponse", " RespondedAfter");
                            startActivity(i);
                        }
                    }).start();

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}