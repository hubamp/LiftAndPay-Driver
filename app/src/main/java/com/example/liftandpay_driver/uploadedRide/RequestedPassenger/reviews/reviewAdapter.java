package com.example.liftandpay_driver.uploadedRide.RequestedPassenger.reviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.chats.model_ChatActivity.messageModel;

import java.util.ArrayList;


public class reviewAdapter extends RecyclerView.Adapter {
    private Context context;
    private int theViewtype;
    private final ArrayList<reviewModel> reviewModels;

    public reviewAdapter(ArrayList<reviewModel> reviewModels) {
        this.reviewModels = reviewModels;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_passenger_review,parent,false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }
}
