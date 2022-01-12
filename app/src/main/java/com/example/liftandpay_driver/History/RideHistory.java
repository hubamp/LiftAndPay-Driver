package com.example.liftandpay_driver.History;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.liftandpay_driver.Dashboard;
import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.fastClass.StringFunction;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesAdapter;
import com.example.liftandpay_driver.uploadedRide.uploadedRidesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class RideHistory extends AppCompatActivity {

   private ArrayList<historyModel> historyModels;
   private historyModel historyModel;
   private RideHistoryAdapter historyAdapter;
   private FirebaseFirestore db = FirebaseFirestore.getInstance();
   private FirebaseAuth mAuth = FirebaseAuth.getInstance();
   private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        /*recyclerView = findViewById(R.id.recyclerview);

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
                                   int  numberOfBookedPassengers = queryDocumentSnapshots.size();

                                    historyModel = new historyModel(
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
                                    Toast.makeText(RideHistory.this, "Value is still null", Toast.LENGTH_SHORT).show();
                                }

                                historyModels.add(historyModel);

                                historyAdapter = new RideHistoryAdapter(RideHistory.this, historyModels);
                                recyclerView.setLayoutManager(new LinearLayoutManager(RideHistory.this, LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(historyAdapter);



                            }
                        });


                    }

                }

            }
        });

*/

    }
}