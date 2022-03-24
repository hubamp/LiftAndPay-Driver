package com.LnPay.driver.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private paymentAccountAdapter paymentAccountAdapter;
    private ArrayList<accountModel> accountModels = new ArrayList<>();
    private Double totalAmount;
    private String mUid = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Double overallAmount = 0.0;
    private TextView overallAmountView;
    private TextView makeOverallPaymentBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        recyclerView = findViewById(R.id.paymentRecycler);
        overallAmountView = findViewById(R.id.overallAmount);
        makeOverallPaymentBtn = findViewById(R.id.makepaymentBtn);
        backBtn = findViewById(R.id.back_arrow);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        makeOverallPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountActivity.this, Payment.class);

                startActivity(i);
            }
        });


        db.collection("Rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                accountModels.clear();

                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {


                    if (doc.getReference().getId().contains(mUid)) {


                        Log.i("totalAmount before multiplication", totalAmount + "");
                        doc.getReference().collection("Booked By").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot qDocSnapshot) {

                                if (doc.get("rideCost") instanceof String) {

                                    totalAmount = Double.parseDouble(doc.getString("rideCost")) * 0.12;

                                } else {
                                    totalAmount = doc.getDouble("rideCost") * 0.12;
                                }
                                totalAmount = totalAmount * qDocSnapshot.getDocuments().size();
                                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                totalAmount = Double.valueOf(decimalFormat.format(totalAmount));

                                Log.i("totalAmount during multiplication", totalAmount + "");
                                Log.i("totalSize during multiplication", qDocSnapshot.getDocuments().size() + "");


                                Log.i("totalAmount after multiplication", totalAmount + "");


                                overallAmount = overallAmount + totalAmount;

                                accountModels.add(new accountModel(
                                        doc.getString("startLocation"),
                                        doc.getString("endLocation"),
                                        totalAmount,
                                        doc.getReference().getId()));


                                //Activate payment parameters
                                makeOverallPaymentBtn.setEnabled(true);
                                overallAmountView.setText("GHC" + overallAmount.toString());

                                paymentAccountAdapter = new paymentAccountAdapter(AccountActivity.this, accountModels);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AccountActivity.this, LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(paymentAccountAdapter);

                            }
                        });


                    }


                }


            }
        });


    }
}