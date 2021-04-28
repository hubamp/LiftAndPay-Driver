package com.example.liftandpay_driver.uploadedRide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.PhoneAuthenticationActivity;
import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.driverProfile.ProfileActivity;
import com.example.liftandpay_driver.uploadRide.UploadRideActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Objects;

public class UploadedRidesActivity extends AppCompatActivity {

    private ArrayList<uploadedRidesModel> uploadedRidesModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageButton uploadBtn;
    private RelativeLayout mainLayout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String theDriverId = mAuth.getUid();
    private CoordinatorLayout coordinatorLayout;
    private ImageButton menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_rides);

       recyclerView = findViewById(R.id.recyclerViewId);
       uploadBtn = findViewById(R.id.addRidebtnId);
       mainLayout = findViewById(R.id.mainLayout);
       menuBtn = findViewById(R.id.menu_spinner);


       uploadBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(UploadedRidesActivity.this, UploadRideActivity.class);
               startActivity(intent);
           }
       });

       menuBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(UploadedRidesActivity.this, ProfileActivity.class);
               startActivity(intent);
           }
       });




 /*       uploadedRidesModel uploadedRidesModel = new uploadedRidesModel(
                "documentng()" ,
                "document.getData().toString()",
                "toString()",
                4);
        uploadedRidesModels.add(uploadedRidesModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));
*/


      CollectionReference pendingRides = db.collection("Driver").document(theDriverId).collection("Pending Rides");

      pendingRides
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Snackbar.make(recyclerView, e.toString(), Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        uploadedRidesModels.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
//                                Toast.makeText(UploadedRidesActivity.this, ""+ document.getData().get("Ride Time")+" "+document.getData().get("Ride Date"), Toast.LENGTH_LONG).show();
                            uploadedRidesModel uploadedRidesModel = new uploadedRidesModel(
                                    Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("startLocation")).toString()+
                                            " - "+ Objects.requireNonNull(document.getData().get("endLocation")).toString(),
                                    Objects.requireNonNull(document.getData().get("Ride Date")).toString(),
                                    Objects.requireNonNull(document.getData().get("Ride Time")).toString(),
                                    4
                            );
                            uploadedRidesModels.add(uploadedRidesModel);
                        }

                        recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this,LinearLayoutManager.VERTICAL,false));
                        recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent intent = new Intent(UploadedRidesActivity.this, PhoneAuthenticationActivity.class);
            startActivity(intent);
            finish();
        }
    }


}