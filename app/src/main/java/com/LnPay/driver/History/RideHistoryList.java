package com.LnPay.driver.History;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.LnPay.driver.R;
import com.LnPay.driver.fastClass.StringFunction;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RideHistoryList extends AppCompatActivity {

    private ArrayList<historyModel> historyModels = new ArrayList<>();
    private historyModel historyModel;
    private RideHistoryAdapter historyAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        recyclerView = findViewById(R.id.recyclerview);

        CollectionReference rideCollection = db.collection("Rides");

        rideCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                          String theID = new StringFunction(documentSnapshot.getId()).removeLastNumberOfCharacter(2);
                    String theID = new StringFunction(documentSnapshot.getId()).splitStringWithAndGet(" ", 0);

                    if (theID.equals(mAuth.getUid())) {

                        CollectionReference rCollection = rideCollection.document(documentSnapshot.getId()).collection("Booked By");

                        rCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots001) {

                                if (queryDocumentSnapshots001 != null) {

                                    historyModel = new historyModel(
                                            Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.getData()).get("startLocation")).toString() +
                                                    " - " + Objects.requireNonNull(documentSnapshot.getData().get("endLocation")).toString(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("rideDate")).toString(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("rideTime")).toString(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("driversStatus")).toString(),
                                            documentSnapshot.getId(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("startLat")).toString(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("startLon")).toString(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("endLat")).toString(),
                                            Objects.requireNonNull(documentSnapshot.getData().get("endLon")).toString()

                                    );

                                } else {
                                    Toast.makeText(RideHistoryList.this, "Value is still null", Toast.LENGTH_SHORT).show();
                                }

                                historyModels.add(historyModel);

                                historyAdapter = new RideHistoryAdapter(RideHistoryList.this, historyModels);
                                recyclerView.setLayoutManager(new LinearLayoutManager(RideHistoryList.this, LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(historyAdapter);


                            }
                        });


                    }

                }

            }
        });


    }
}