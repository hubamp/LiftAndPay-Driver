

package com.LnPay.driver.uploadedRide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.SignUp.PhoneAuthenticationActivity;
import com.LnPay.driver.R;

import com.LnPay.driver.fastClass.StringFunction;
import com.LnPay.driver.uploadRide.UploadRideActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadedRidesActivity extends AppCompatActivity {

    private ArrayList<uploadedRidesModel> uploadedRidesModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView uploadBtn;
    private TextView noUploadTxt;
    private RelativeLayout mainLayout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String theDriverId = mAuth.getUid();
    private CoordinatorLayout coordinatorLayout;

    private uploadedRidesModel uploadedRidesModel;
    private UploadedRidesAdapter uploadedRidesAdapter;
    int numberOfBookedPassengers = 0;
    int totalNumberOfBookedPassengers = 0;

    private SharedPreferences backgroundSharedPrefs;

    private TextView backBtn;

    private final static List<DocumentSnapshot> documentSnapshotList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_rides);

        recyclerView = findViewById(R.id.recyclerViewId);
        uploadBtn = findViewById(R.id.addRidebtnId);
        mainLayout = findViewById(R.id.mainLayout);

        noUploadTxt = findViewById(R.id.noUplaodTxt);
        backBtn = findViewById(R.id.backButton);


        backgroundSharedPrefs = getApplicationContext().getSharedPreferences("BACKGROUNDFILE", MODE_PRIVATE);

        Log.e("starting Passengers: ", "" + backgroundSharedPrefs.getInt("TheTotalNumberOfPassengers", -1) + "");



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadedRidesActivity.this, UploadRideActivity.class);
                startActivity(intent);
            }
        });



        CollectionReference rideCollection = db.collection("Rides");

        rideCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                if (value != null) {
                    if (!value.isEmpty()) {
                        uploadedRidesModels.clear();
                        documentSnapshotList.clear();

                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
//                          String theID = new StringFunction(documentSnapshot.getId()).removeLastNumberOfCharacter(2);
                            String theID = new StringFunction(documentSnapshot.getId()).splitStringWithAndGet(" ", 0);

                            if (theID.equals(theDriverId)) {
                                documentSnapshotList.add(documentSnapshot);
                                CollectionReference rCollection = rideCollection.document(documentSnapshot.getId()).collection("Booked By");

                                rCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        if (value != null) {
                                            numberOfBookedPassengers = queryDocumentSnapshots.size();
                                            totalNumberOfBookedPassengers = totalNumberOfBookedPassengers + numberOfBookedPassengers;

                                            uploadedRidesModel = new uploadedRidesModel(
                                                    Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.getData()).get("startLocation")).toString() +
                                                            " - " + Objects.requireNonNull(documentSnapshot.getData().get("endLocation")).toString(),
                                                    Objects.requireNonNull(documentSnapshot.getData().get("rideDate")).toString(),
                                                    Objects.requireNonNull(documentSnapshot.getData().get("rideTime")).toString(),
                                                    numberOfBookedPassengers,
                                                    documentSnapshot.getId(),
                                                    Objects.requireNonNull(documentSnapshot.getData().get("startLat")).toString(),
                                                    Objects.requireNonNull(documentSnapshot.getData().get("startLon")).toString(),
                                                    Objects.requireNonNull(documentSnapshot.getData().get("endLat")).toString(),
                                                    Objects.requireNonNull(documentSnapshot.getData().get("endLon")).toString()

                                            );

                                        } else {
                                            Log.e("Reference","Value is null");

                                        }



                                        if(!Objects.equals(documentSnapshot.getString("driversStatus"), "Cancelled")){
                                            Log.i("driverStatus001",documentSnapshot.getString("driversStatus"));

                                            uploadedRidesModels.add(uploadedRidesModel);

                                        }
                                        if (uploadedRidesModels.size() <= 0) {
                                            noUploadTxt.setVisibility(View.GONE);
                                        }
                                        uploadedRidesAdapter = new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this, LinearLayoutManager.VERTICAL, false));
                                        recyclerView.setAdapter(uploadedRidesAdapter);

                                        backgroundSharedPrefs.edit().putInt("TheTotalNumberOfPassengers", totalNumberOfBookedPassengers).apply();
                                        Log.e("Total Booked Passengers", totalNumberOfBookedPassengers + "");

                                    }
                                });


                            }

                        }

                    } else {
                        Toast.makeText(UploadedRidesActivity.this, "task not successful", Toast.LENGTH_SHORT).show();
                    }

                    Log.e("001", documentSnapshotList.size() + "");

                }

            }
        });

        Log.e("002", documentSnapshotList.size() + "");


        for (DocumentSnapshot ds : documentSnapshotList) {

            uploadedRidesModels.clear();
            Log.e("The Ride Id", ds.getId());

            CollectionReference rCollection = rideCollection.document(ds.getId()).collection("Booked By");

            rCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                    if (value != null) {

                        numberOfBookedPassengers = value.size();

                        uploadedRidesModel = new uploadedRidesModel(
                                Objects.requireNonNull(Objects.requireNonNull(ds.getData()).get("startLocation")).toString() +
                                        " - " + Objects.requireNonNull(ds.getData().get("endLocation")).toString(),
                                Objects.requireNonNull(ds.getData().get("rideDate")).toString(),
                                Objects.requireNonNull(ds.getData().get("rideTime")).toString(),
                                numberOfBookedPassengers,
                                ds.getId(),
                                Objects.requireNonNull(ds.getData().get("startLat")).toString(),
                                Objects.requireNonNull(ds.getData().get("startLon")).toString(),
                                Objects.requireNonNull(ds.getData().get("endLat")).toString(),
                                Objects.requireNonNull(ds.getData().get("endLon")).toString()

                        );

                    } else {
                        Log.e("Reference","Value is null");

                    }

                    if(!Objects.equals(ds.getString("driversStatus"), "Cancelled")){
                        Log.i("driverStatus",ds.getString("driversStatus"));
                        uploadedRidesModels.add(uploadedRidesModel);

                    }
                    if (uploadedRidesModels.size() > 0) {
                        noUploadTxt.setVisibility(View.GONE);
                    }
                    uploadedRidesAdapter = new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels);
                    recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(uploadedRidesAdapter);

                    uploadedRidesModels.clear();


                }
            });
        }


    }


    @Override
    protected void onStart() {
        super.onStart();

    }



}