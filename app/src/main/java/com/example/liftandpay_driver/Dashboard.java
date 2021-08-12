package com.example.liftandpay_driver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liftandpay_driver.menu.MenuListActivity;
import com.example.liftandpay_driver.uploadRide.UploadRideActivity;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.maps.model.Dash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

public class Dashboard extends AppCompatActivity {

    private ImageView sendToUploadedRide;
    private RelativeLayout noRideLayout;
    private RelativeLayout rideAvailableLayout;
    private RelativeLayout parentLayout;
    private LinearLayout rideUploadLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();
    private TextView pendingRideText;

    private TextView journeyName, dateTime, distanceCost, no_Seats,no_Requests;

    private ListenerRegistration lastRideListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sendToUploadedRide = findViewById(R.id.allUploadedRideBtn);

        parentLayout = findViewById(R.id.currentRideLayoutId);
        noRideLayout = findViewById(R.id.noRideLayoutId);
        rideAvailableLayout = findViewById(R.id.rideavailablelayout);
        rideUploadLayout = findViewById(R.id.mainRideUpload);
        pendingRideText = findViewById(R.id.pendingRideTextId);

        journeyName =findViewById(R.id.locationNameId);
        dateTime = findViewById(R.id.dateTimeId);
        distanceCost = findViewById(R.id.distanceCostId);
        no_Seats = findViewById(R.id.numberOfOccupantsId);
        no_Requests = findViewById(R.id.numberOfRequestedPassengersId);

        rideUploadLayout.setOnClickListener(View->{
            Intent intent = new Intent(Dashboard.this, UploadRideActivity.class);
            startActivity(intent);
        });


        sendToUploadedRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, UploadedRidesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        db.collection("Driver").document(mUid).collection("Rides").document("Pending").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                assert value != null;
                if (!value.exists()) {
                    parentLayout.setVisibility(View.VISIBLE);
                    noRideLayout.setVisibility(View.VISIBLE);
                    rideAvailableLayout.setVisibility(View.INVISIBLE);
                    pendingRideText.setText("Pending Ride");
                    Log.e("available000", "Does not exist");
                } else {

                    ArrayList<String> availableRideIds = new ArrayList<>((Collection<? extends String>) value.get("AvailableRideIds"));
                    if (availableRideIds.isEmpty()) {
                        parentLayout.setVisibility(View.VISIBLE);
                        noRideLayout.setVisibility(View.VISIBLE);
                        rideAvailableLayout.setVisibility(View.INVISIBLE);
                        pendingRideText.setText("Pending Ride");
                        Log.e("available001", "Exists but empty");

                    } else {
                        parentLayout.setVisibility(View.VISIBLE);
                        noRideLayout.setVisibility(View.INVISIBLE);
                        rideAvailableLayout.setVisibility(View.VISIBLE);
                        pendingRideText.setText("Pending Ride");
                        Log.e("available002", "Exists with item");

                        String lastAvailableRideId = availableRideIds.get(availableRideIds.size() - 1).trim();

                        //Listen to last ride from ride
                        lastRideListener = db.collection("Rides").document(lastAvailableRideId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value002, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                                Log.e("Last Ride",value002.getReference().getPath());


                                if (value002.exists()) {
                                    Log.e("lastRide","Ride exists");
                                    String cost = value002.getString("Ride Cost");
                                    String dateTimes = value002.getString("Ride Date") + " "+ value002.getString("Ride Time");
                                    String journey = value002.getString("startLocation")+" to "+value002.getString("endLocation");
                                    double endLon = value002.getDouble("endLon");
                                    double endLat = value002.getDouble("endLat");
                                    double stLat = value002.getDouble("startLat");
                                    double stLon = value002.getDouble("startLon");

                                    distanceCost.setText(cost);
                                    dateTime.setText(dateTimes);
                                    journeyName.setText(journey);

                                    //Listen to requested passengers
                                    value002.getReference().collection("Booked By").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value003, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                            assert value003 != null;
                                            if (value003.isEmpty())
                                            {
                                                Log.e("Request","Empty");
                                                no_Requests.setText("0");
                                            }
                                            else
                                            {
                                                Log.e("Request","Not Empty");
                                                no_Requests.setText(""+value003.size());

                                                for (DocumentChange changes : value003.getDocumentChanges()){
                                                    if (changes.getType() == DocumentChange.Type.ADDED){
                                                        no_Requests.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.success)));
                                                    }
                                                }
                                            }
                                        }
                                    });




                                }

                                else
                                {
                                    Log.e("lastRide","does not exists");
                                }

                            }
                        });

                    }


                }
            }
        });


    }
}