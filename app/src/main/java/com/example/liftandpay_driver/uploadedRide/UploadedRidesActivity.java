package com.example.liftandpay_driver.uploadedRide;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.liftandpay_driver.SignUp.PhoneAuthenticationActivity;
import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.fastClass.StringFunction;
import com.example.liftandpay_driver.menu.MenuListActivity;
import com.example.liftandpay_driver.threads.chatNotification;
import com.example.liftandpay_driver.uploadRide.UploadRideActivity;
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
    private TextView uploadBtn;
    private TextView noUploadTxt;
    private RelativeLayout mainLayout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String theDriverId = mAuth.getUid();
    private CoordinatorLayout coordinatorLayout;
    private ImageButton menuBtn;
    private uploadedRidesModel uploadedRidesModel;
    private UploadedRidesAdapter uploadedRidesAdapter;
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
       noUploadTxt = findViewById(R.id.noUplaodTxt);


    OneTimeWorkRequest periodicWorkRequest  = new OneTimeWorkRequest.Builder(chatNotification.class).build();
    WorkManager.getInstance(getApplicationContext()).enqueue(periodicWorkRequest);


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
               overridePendingTransition( 0, 0);

           }
       });


      CollectionReference rideCollection = db.collection("Rides");

      rideCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

              if (value != null) {
                if (!value.isEmpty()) {
                uploadedRidesModels.clear();
                      for(DocumentSnapshot documentSnapshot : value.getDocuments()){
//                          String theID = new StringFunction(documentSnapshot.getId()).removeLastNumberOfCharacter(2);
                         String theID = new StringFunction(documentSnapshot.getId()).splitStringWithAndGet(" ",0);

                          if(theID.equals(theDriverId))
                          {
                              CollectionReference rCollection = rideCollection.document(documentSnapshot.getId()).collection("Booked By");

                              rCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                  @Override
                                  public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error)
                                  {
                                      if (value != null)
                                      {
                                          numberOfBookedPassengers = value.size();

                                              uploadedRidesModel = new uploadedRidesModel(
                                              Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.getData()).get("startLocation")).toString() +
                                                      " - " + Objects.requireNonNull(documentSnapshot.getData().get("endLocation")).toString(),
                                                      Objects.requireNonNull(documentSnapshot.getData().get("Ride Date")).toString(),
                                                      Objects.requireNonNull(documentSnapshot.getData().get("Ride Time")).toString(),
                                                      numberOfBookedPassengers,
                                                      documentSnapshot.getId(),
                                                      Objects.requireNonNull(documentSnapshot.getData().get("startLat")).toString(),
                                                      Objects.requireNonNull(documentSnapshot.getData().get("startLon")).toString(),
                                                      Objects.requireNonNull(documentSnapshot.getData().get("endLat")).toString(),
                                                      Objects.requireNonNull(documentSnapshot.getData().get("endLon")).toString()

                                              );

                                      } else {
                                          Toast.makeText(UploadedRidesActivity.this, "Value is still null", Toast.LENGTH_SHORT).show();
                                      }

                                      uploadedRidesModels.add(uploadedRidesModel);
                                      if(uploadedRidesModels.size()<=0){
                                          noUploadTxt.setVisibility(View.GONE);
                                      }
                                      uploadedRidesAdapter = new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels);
                                      recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this, LinearLayoutManager.VERTICAL, false));
                                      recyclerView.setAdapter(uploadedRidesAdapter);


                                  }
                              });

                          }

                      }
                uploadedRidesModels.clear();

                }
                  else
                  {
                      Toast.makeText(UploadedRidesActivity.this, "task not successful", Toast.LENGTH_SHORT).show();
                  }
              }

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