package com.LnPay.driver.uploadedRide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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

import com.LnPay.driver.R;
import com.LnPay.driver.uploadedRide.RequestedPassenger.RequestedPassengersSheet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class UploadedRidesAdapter extends RecyclerView.Adapter<UploadedRidesAdapter.uploadedViewHolder> {

    Context context;
    ArrayList<uploadedRidesModel> uploadedRidesModels;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;


    public UploadedRidesAdapter(Context context, ArrayList<uploadedRidesModel> uploadedRidesModels) {
        this.context = context;
        this.uploadedRidesModels = uploadedRidesModels;
    }

    @NonNull
    @Override
    public UploadedRidesAdapter.uploadedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_uploaded_rides, parent, false);
        return new uploadedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedRidesAdapter.uploadedViewHolder holder, int position) {

        uploadedRidesModel uploadedRidesModel = uploadedRidesModels.get(holder.getAdapterPosition());
        holder.rTimeView.setText(uploadedRidesModel.getRIDETIME());
        holder.rDateView.setText(uploadedRidesModel.getRIDEDATE());
        holder.rJourney.setText(uploadedRidesModel.getJOURNEY());
        String rNOP = String.valueOf(uploadedRidesModel.getNUMBEROFREQUESTS());
        holder.rNumberOfPassengers.setText(rNOP);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (holder.linearLayout.getForeground().getConstantState().equals(context.getResources().getDrawable(R.color.fadedBlue, null).getConstantState())) {
                    // Do what you have to do...
                    //Unselected
                    holder.linearLayout.setForeground(ContextCompat.getDrawable(context, R.color.transparentColor));
                    holder.requestedPassengersBtn.setForeground(ContextCompat.getDrawable(context, R.color.transparentColor));
                    holder.rNumberOfPassengers.setBackground(ContextCompat.getDrawable(context, R.drawable.img_circle));

                } else {
                    holder.toMapPrograssBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(context, UploadedRideMap.class);
                    applySharedToActiveRide(holder.getAdapterPosition());
                    context.startActivity(intent);
                    holder.toMapPrograssBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onLongClick(View v) {

//                if (Objects.equals(holder.linearLayout.getForeground().getConstantState(), context.getResources().getColor(R.color.transparentColor))) {
                if (holder.linearLayout.getForeground().getConstantState().equals(context.getResources().getDrawable(R.color.transparentColor, null).getConstantState())) {
                    // Do what you have to do...
                    //Selected
                    holder.linearLayout.setForeground(ContextCompat.getDrawable(context, R.color.fadedBlue));
                    holder.requestedPassengersBtn.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fadedBlue)));
                    holder.rNumberOfPassengers.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_delete));
                    return true;

                } else {
                    // Do what you have to do...
                    //Unselected
                    return true;
                }

            }
        });


        holder.requestedPassengersBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (holder.linearLayout.getForeground().getConstantState().equals(context.getResources().getDrawable(R.color.fadedBlue, null).getConstantState())) {

                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(context);

                    // Set the message show for the Alert time
                    builder.setMessage("Do you want to delete all seletcted rides?");
                    builder.setTitle("Delete");
                    builder.setCancelable(true);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                            db.collection("Rides").document(uploadedRidesModel.getDOCUMENTID()).update("driversStatus", "Cancelled");


                            HashMap<String, Object> update = new HashMap<>();
                            update.put("driversStatus", "Cancelled");
                            db.collection("Rides").document(uploadedRidesModels.get(holder.getAdapterPosition()).getDOCUMENTID()).update(update);
                            WorkManager.getInstance(context).cancelUniqueWork("UpdateMyLocId").getResult().isCancelled();


                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else {
                    RequestedPassengersSheet requestedPassengersSheet = new RequestedPassengersSheet();
                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    requestedPassengersSheet.setNumberOfRequests(Integer.parseInt(holder.rNumberOfPassengers.getText().toString()));
                    requestedPassengersSheet.setTheRequestedId(uploadedRidesModels.get(position).getDOCUMENTID());

                    //This sharedpreference from goes to RequestendPasesngersSheet.java
                    applySharedToActiveRide(position);
                    requestedPassengersSheet.show(manager, null);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return uploadedRidesModels.size();
    }

    public class uploadedViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ProgressBar toMapPrograssBar;
        private RelativeLayout requestedPassengersBtn;
        private TextView rNumberOfPassengers;
        private TextView rJourney;
        private TextView rDateView;
        private TextView rTimeView;


        public uploadedViewHolder(@NonNull View itemView) {
            super(itemView);

            toMapPrograssBar = itemView.findViewById(R.id.proceedToMapProgressbarrId);
            linearLayout = itemView.findViewById(R.id.uploadedLinearLayoutId);
            requestedPassengersBtn = itemView.findViewById(R.id.requestedPassengerRelativeLayoutId);
            rJourney = itemView.findViewById(R.id.journeyId);
            rDateView = itemView.findViewById(R.id.dateModelId);
            rTimeView = itemView.findViewById(R.id.timeModelId);
            rNumberOfPassengers = itemView.findViewById(R.id.numberOfRequestedPassengersId);

        }

    }

    private void applySharedToActiveRide(int position) {
        sharedPreferences = context.getSharedPreferences("ACTIVE_RIDEFILE", MODE_PRIVATE);
        sharedPreferences.edit().putString("TheEndLat", uploadedRidesModels.get(position).getENDLAT()).apply();
        sharedPreferences.edit().putString("TheEndLon", uploadedRidesModels.get(position).getENDLON()).apply();
        sharedPreferences.edit().putString("TheStLat", uploadedRidesModels.get(position).getSTLAT()).apply();
        sharedPreferences.edit().putString("TheStLon", uploadedRidesModels.get(position).getSTLON()).apply();
        sharedPreferences.edit().putString("TheRideId", uploadedRidesModels.get(position).getDOCUMENTID()).apply();
    }


}
