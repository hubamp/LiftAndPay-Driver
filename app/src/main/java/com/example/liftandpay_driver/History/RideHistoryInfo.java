package com.example.liftandpay_driver.History;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.liftandpay_driver.History.RequestedPassengers.ReqstdPassAdapter;
import com.example.liftandpay_driver.History.RequestedPassengers.reqstdPassModel;
import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.uploadedRide.UploadedRideMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;

public class RideHistoryInfo extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private TextView startingLocation, endingLocation;
    private TextView driversStatus;
    private TextView backButton;

    //Adapter recycler
    private RecyclerView passengersRequestView;
    private ArrayList<reqstdPassModel> reqstdPassModels = new ArrayList<>();
    private ReqstdPassAdapter reqstdPassAdapter;


    private final String mapBoxStyleUrl = "mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";


    //Database
    private String mUid = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    //Intent
    private String theRideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(RideHistoryInfo.this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_ride_history_info);

        //
        startingLocation = findViewById(R.id.startingLocationId);
        endingLocation = findViewById(R.id.endingLocationId);
        driversStatus = findViewById(R.id.rideStatusId);
        passengersRequestView = findViewById(R.id.requestedPassengersList);
        backButton = findViewById(R.id.backButton);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //getIntent bundle
         theRideId =getIntent().getStringExtra("TheRideId");

        //get the drivers ride information from firestore
        db.collection("Rides").document(theRideId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //fetch all the parameters from the firebase
                String startLoc = documentSnapshot.getString("startLocation");
                String endLoc = documentSnapshot.getString("endLocation");
                String status = documentSnapshot.getString("driversStatus");
                double elat = documentSnapshot.getDouble("endLat");
                double lon = documentSnapshot.getDouble("endLon");
                double slat = documentSnapshot.getDouble("startLat");
                double slon = documentSnapshot.getDouble("startLon");


                //populate the values into the corresponding views
                startingLocation.setText(startLoc);
                endingLocation.setText(endLoc);
                driversStatus.setText(status);


                //fetch details of all who booked the ride
                documentSnapshot.getReference().collection("Booked By").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot reqstdPassDocument : queryDocumentSnapshots.getDocuments()) {
                            String pickupLoc = reqstdPassDocument.getString("Location Desc");
                            String passName = reqstdPassDocument.getString("Name");
                            String passStatus = reqstdPassDocument.getString("Status");

                            reqstdPassModels.add(new reqstdPassModel(passName, pickupLoc, passStatus));


                        }

                        //Display the day on the RecyclerView
                        reqstdPassAdapter = new ReqstdPassAdapter(RideHistoryInfo.this, reqstdPassModels);
                        passengersRequestView.setNestedScrollingEnabled(false);
                        passengersRequestView.setLayoutManager(new LinearLayoutManager(RideHistoryInfo.this, LinearLayoutManager.VERTICAL, false));
                        passengersRequestView.setAdapter(reqstdPassAdapter);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        //Mapbox instantiation
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(RideHistoryInfo.this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

            }
        });
    }
}