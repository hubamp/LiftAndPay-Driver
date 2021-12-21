package com.example.liftandpay_driver.uploadedRide.RequestedPassenger;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class RequestedPassengersSheet extends BottomSheetDialogFragment {

    //For Recycler View
    private RecyclerView recyclerView;
    private ArrayList<RequestedPassengersModel> requestedPassengersModels = new ArrayList<>();
    private RequestedPassengersModel requestedPassengersModel;
    private String name;
    private String number;
    private String theRequestedId;

    //Variables
    private int numberOfRequests;

    //For firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.i("The retrieved Id",theRequestedId);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requested_passengers_sheet, container, false);

        recyclerView = v.findViewById(R.id.requestedRecyclerView);


        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            mPermissionResult.launch(Manifest.permission.READ_PHONE_STATE);
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 119);
            Toast.makeText(getContext(), "Grant phone permission to continue", Toast.LENGTH_LONG).show();            // Permission is not granted
        }

        //shared from UploadedRideAdapter.java
        sharedPreferences = requireContext().getSharedPreferences("ACTIVE_RIDEFILE", MODE_PRIVATE);
        CollectionReference requestedReference = db.collection("Rides").document(theRequestedId).collection("Booked By");

        requestedReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot passengersSnapshot : task.getResult().getDocuments()) {
                    requestedPassengersModel = new RequestedPassengersModel(
                            passengersSnapshot.getString("Name"),
                            passengersSnapshot.getString("Location Desc"),
                            passengersSnapshot.getId(),
                            passengersSnapshot.getDouble("Long"),
                            passengersSnapshot.getDouble("Lat"),
                            passengersSnapshot.getString("Status")
                    );

                    requestedPassengersModels.add(requestedPassengersModel);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new RequestedPassengersAdapter(requestedPassengersModels, getContext()));
            }
        });

        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().clear().apply();
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTheRequestedId() {
        return theRequestedId;
    }

    public void setTheRequestedId(String theRequestedId) {
        this.theRequestedId = theRequestedId;
        Log.i("The Ride set Id", theRequestedId);
    }


    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
//                        Log.e(TAG, "onActivityResult: PERMISSION GRANTED");
                    } else {
//                        Log.e(TAG, "onActivityResult: PERMISSION DENIED");
                    }
                }
            });
}