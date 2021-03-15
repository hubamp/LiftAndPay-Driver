package com.example.liftandpay_driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ApproveRequestAdapter extends RecyclerView.Adapter<ApproveRequestAdapter.ApproveRequestHolder> {
    private ArrayList<ApproveRequestModel>  approveRequestModels;
    private final Context context;


    public ApproveRequestAdapter(ArrayList<ApproveRequestModel> approveRequestModels, Context context) {
        this.approveRequestModels = approveRequestModels;
        this.context = context;
    }


    @NonNull
    @Override
    public ApproveRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.approve_requested_passenger,parent, false);
        return new ApproveRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveRequestHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return approveRequestModels.size();
    }

    public static class ApproveRequestHolder extends RecyclerView.ViewHolder {
        public ApproveRequestHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
