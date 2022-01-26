package com.example.liftandpay_driver.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.liftandpay_driver.History.RideHistoryInfo;
import com.example.liftandpay_driver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private paymentAccountAdapter paymentAccountAdapter;
    private ArrayList<accountModel> accountModels = new ArrayList<>();
    private static Double totalAmount;
    private String mUid = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Double overallAmount=0.0;
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
        backBtn =findViewById(R.id.back_arrow);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        makeOverallPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountActivity.this,Payment.class);

                startActivity(i);
            }
        });



        db.collection("Rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {


                    if (doc.getReference().getId().contains(mUid)) {


                        Log.i("totalAmount before multiplication",totalAmount+"");
                      doc.getReference().collection("Booked By").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                           @Override
                           public void onSuccess(QuerySnapshot qDocSnapshot) {

                               if (doc.get("rideCost") instanceof String) {

                                   totalAmount = Double.parseDouble(doc.getString("rideCost"));

                               } else {
                                   totalAmount = doc.getDouble("rideCost");
                               }
                               totalAmount = totalAmount * qDocSnapshot.getDocuments().size();
                               Log.i("totalAmount during multiplication",totalAmount+"");
                               Log.i("totalSize during multiplication",qDocSnapshot.getDocuments().size()+"");


                               Log.i("totalAmount after multiplication",totalAmount+"");


                               overallAmount = overallAmount+totalAmount;

                               accountModels.add(new accountModel(
                                       doc.getString("startLocation"),
                                       doc.getString("endLocation"),
                                       totalAmount));


                               //Activate payment parameters
                               makeOverallPaymentBtn.setEnabled(true);
                               overallAmountView.setText("GHC"+overallAmount.toString());


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