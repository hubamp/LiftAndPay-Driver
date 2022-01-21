package com.example.liftandpay_driver.History;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.uploadedRide.RequestedPassenger.RequestedPassengersSheet;
import com.example.liftandpay_driver.uploadedRide.UploadedRideMap;
import com.example.liftandpay_driver.uploadedRide.uploadedRidesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
