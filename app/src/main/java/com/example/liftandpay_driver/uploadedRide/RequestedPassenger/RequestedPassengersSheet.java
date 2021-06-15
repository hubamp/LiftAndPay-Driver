package com.example.liftandpay_driver.uploadedRide.RequestedPassenger;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liftandpay_driver.uploadedRide.RequestedPassenger.ApproveRequestAdapter;
import com.example.liftandpay_driver.uploadedRide.RequestedPassenger.ApproveRequestModel;
import com.example.liftandpay_driver.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RequestedPassengersSheet extends BottomSheetDialogFragment {

//For Recycler View
private RecyclerView recyclerView;
private ArrayList<ApproveRequestModel> approveRequestModels = new ArrayList<>();
private ApproveRequestModel approveRequestModel;
private String name;
private String number;

//Variables
    private int numberOfRequests;

//For firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requested_passengers_sheet, container, false);

        recyclerView = v.findViewById(R.id.requestedRecyclerView);

        for(int i = 0 ; i< numberOfRequests; i++){
            approveRequestModel = new ApproveRequestModel("Hubert Amponsah", "0200254997");
            approveRequestModels.add(approveRequestModel);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new ApproveRequestAdapter(approveRequestModels, getContext()));

        return v;
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
}