package com.example.liftandpay_driver.uploadedRide.RequestedPassenger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_approve_requested_passenger,parent, false);
        return new ApproveRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveRequestHolder holder, int position) {

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,RequestedPassengerProfile.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return approveRequestModels.size();
    }

    public static class ApproveRequestHolder extends RecyclerView.ViewHolder {

        private LinearLayout mainLayout;
        public ApproveRequestHolder(@NonNull View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.mainRequestedPassengerLayout);
        }
    }
}
