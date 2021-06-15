package com.example.liftandpay_driver.uploadedRide;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.liftandpay_driver.PhoneAuthenticationActivity;
import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.fastClass.StringFunction;
import com.example.liftandpay_driver.menu.MenuListActivity;
import com.example.liftandpay_driver.threads.chatNotification;
import com.example.liftandpay_driver.uploadRide.UploadRideActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UploadedRidesActivity extends AppCompatActivity {

    private ArrayList<uploadedRidesModel> uploadedRidesModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView uploadBtn;
    private RelativeLayout mainLayout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String theDriverId = mAuth.getUid();
    private CoordinatorLayout coordinatorLayout;
    private ImageButton menuBtn;
    private uploadedRidesModel uploadedRidesModel;
    int numberOfBookedPassengers = 0;
    int iterator = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_rides);

       recyclerView = findViewById(R.id.recyclerViewId);
       uploadBtn = findViewById(R.id.addRidebtnId);
       mainLayout = findViewById(R.id.mainLayout);
       menuBtn = findViewById(R.id.menu_spinner);


//    PeriodicWorkRequest periodicWorkRequest  = new PeriodicWorkRequest.Builder(chatNotification.class,1000, TimeUnit.MILLISECONDS).build();

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
               Intent intent = new Intent(UploadedRidesActivity.this, MenuListActivity.class);
               startActivity(intent);
           }
       });


      CollectionReference pendingRides = db.collection("Driver").document(theDriverId).collection("Pending Rides");
      CollectionReference rideCollection = db.collection("Rides");

      rideCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

              if (task.isSuccessful()) {
                  for(DocumentSnapshot documentSnapshot : task.getResult()){
                      String theID = new StringFunction(documentSnapshot.getId()).removeLastNumberOfCharacter(2);

                      if(theID.equals(theDriverId)) {

                          CollectionReference rCollection = rideCollection.document(documentSnapshot.getId()).collection("Booked By");

                          rCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                              @Override
                              public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                                  if (value != null) {

                                      numberOfBookedPassengers = value.size();

                                          uploadedRidesModel = new uploadedRidesModel(
                                                  Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.getData()).get("startLocation")).toString() +
                                                          " - " + Objects.requireNonNull(documentSnapshot.getData().get("endLocation")).toString(),
                                                  Objects.requireNonNull(documentSnapshot.getData().get("Ride Date")).toString(),
                                                  Objects.requireNonNull(documentSnapshot.getData().get("Ride Time")).toString(),
                                                  numberOfBookedPassengers
                                          );




                                          uploadedRidesModels.add(uploadedRidesModel);
                                          recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this, LinearLayoutManager.VERTICAL, false));
                                          recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));

                                  } else {
                                      Toast.makeText(UploadedRidesActivity.this, "Value is still null", Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });
                      }
                      else
                      {
                          Toast.makeText(UploadedRidesActivity.this, "The ids are not equal", Toast.LENGTH_SHORT).show();
                      }
                  }
              }
              else
              {
                  Toast.makeText(UploadedRidesActivity.this, "task not successful", Toast.LENGTH_SHORT).show();
              }
          }
      });


      /*
        pendingRides
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Snackbar.make(recyclerView, e.toString(), 5000).show();
                            return;
                        }

                        uploadedRidesModels.clear();

                        for (DocumentSnapshot document : value.getDocuments()) {
                            CollectionReference bookedByCollection = pendingRides.document(document.getId()).collection("Booked By");
                            bookedByCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    numberOfBookedPassengers = queryDocumentSnapshots.size();

                                    if (numberOfBookedPassengers > 0) {

                                        uploadedRidesModel = new uploadedRidesModel(
                                                Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("startLocation")).toString() +
                                                        " - " + Objects.requireNonNull(document.getData().get("endLocation")).toString(),
                                                Objects.requireNonNull(document.getData().get("Ride Date")).toString(),
                                                Objects.requireNonNull(document.getData().get("Ride Time")).toString(),
                                                numberOfBookedPassengers
                                        );

                                    } else {
                                        numberOfBookedPassengers = 0;

                                        uploadedRidesModel = new uploadedRidesModel(
                                                Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("startLocation")).toString() +
                                                        " - " + Objects.requireNonNull(document.getData().get("endLocation")).toString(),
                                                Objects.requireNonNull(document.getData().get("Ride Date")).toString(),
                                                Objects.requireNonNull(document.getData().get("Ride Time")).toString(),
                                                numberOfBookedPassengers
                                        );

                                    }
                                    uploadedRidesModels.add(uploadedRidesModel);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this, LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    numberOfBookedPassengers = 0;
                                    uploadedRidesModels.add(uploadedRidesModel);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this, LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));

                                }
                            });
                        }
                    }
                });
                */

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