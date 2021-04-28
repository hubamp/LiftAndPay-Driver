package com.example.liftandpay_driver.uploadedRide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liftandpay_driver.ApproveRequestAdapter;
import com.example.liftandpay_driver.ApproveRequestModel;
import com.example.liftandpay_driver.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class RequestedPassengersSheet extends BottomSheetDialogFragment {


private RecyclerView recyclerView;
private ArrayList<ApproveRequestModel> approveRequestModels = new ArrayList<>();
private ApproveRequestModel approveRequestModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requested_passengers_sheet, container, false);

        recyclerView = v.findViewById(R.id.requestedRecyclerView);

        approveRequestModel = new ApproveRequestModel("Hubert Amponsah", "0200254997");
        approveRequestModels.add(approveRequestModel);
        approveRequestModels.add(approveRequestModel);
        approveRequestModels.add(approveRequestModel);
        approveRequestModels.add(approveRequestModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new ApproveRequestAdapter(approveRequestModels, getContext()));

        return v;
    }
}