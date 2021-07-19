package com.example.liftandpay_driver.uploadedRide.RequestedPassenger;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import static android.content.Context.MODE_PRIVATE;

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requested_passengers_sheet, container, false);

        recyclerView = v.findViewById(R.id.requestedRecyclerView);

        //shared from UploadedRideAdapter.java
        sharedPreferences = getContext().getSharedPreferences("ACTIVE_RIDEFILE",MODE_PRIVATE);
        CollectionReference requestedReference = db.collection("Rides").document(theRequestedId).collection("Booked By");

        requestedReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot passengersSnapshot : task.getResult().getDocuments())
                {
                    requestedPassengersModel = new RequestedPassengersModel(passengersSnapshot.getString("Name"), passengersSnapshot.getId(), passengersSnapshot.getId(),
                            passengersSnapshot.getDouble("Long"),  passengersSnapshot.getDouble("Lat"),
                            passengersSnapshot.getString("Status"));
                    requestedPassengersModels.add(requestedPassengersModel);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
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
    }
}