package com.LnPay.driver.History;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.historyViewHolder> {

    Context context;
    ArrayList<historyModel> historyModels;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;


    public RideHistoryAdapter(Context context, ArrayList<historyModel> historyModels) {
        this.context = context;
        this.historyModels = historyModels;
    }

    @NonNull
    @Override
    public RideHistoryAdapter.historyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_ride_histories, parent, false);
        return new historyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHistoryAdapter.historyViewHolder holder, int position) {

        historyModel historyModel = historyModels.get(holder.getAdapterPosition());
        holder.rTimeView.setText(historyModel.getRIDETIME());
        holder.rDateView.setText(historyModel.getRIDEDATE());
        holder.rJourney.setText(historyModel.getJOURNEY());
        holder.rStatus.setText(historyModel.getRIDESTATUS());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {

                holder.toMapPrograssBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, RideHistoryInfo.class);
                intent.putExtra("TheRideId", historyModels.get(holder.getAdapterPosition()).getDOCUMENTID());
                intent.putExtra("TheEndLat", historyModels.get(holder.getAdapterPosition()).getENDLAT());
                intent.putExtra("TheEndLon", historyModels.get(holder.getAdapterPosition()).getENDLON());
                intent.putExtra("TheStLat", historyModels.get(holder.getAdapterPosition()).getSTLAT());
                intent.putExtra("TheStLon", historyModels.get(holder.getAdapterPosition()).getSTLON());
                context.startActivity(intent);
                holder.toMapPrograssBar.setVisibility(View.INVISIBLE);

            }
        });

    }


    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class historyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ProgressBar toMapPrograssBar;
        private TextView rStatus;
        private TextView rJourney;
        private TextView rDateView;
        private TextView rTimeView;


        public historyViewHolder(@NonNull View itemView) {
            super(itemView);

            toMapPrograssBar = itemView.findViewById(R.id.proceedToMapProgressbarrId);
            linearLayout = itemView.findViewById(R.id.uploadedLinearLayoutId);
            rJourney = itemView.findViewById(R.id.journeyId);
            rDateView = itemView.findViewById(R.id.dateModelId);
            rTimeView = itemView.findViewById(R.id.timeModelId);
            rStatus = itemView.findViewById(R.id.rideStatusId);


        }

    }




}
