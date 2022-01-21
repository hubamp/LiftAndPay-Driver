package com.example.liftandpay_driver.uploadedRide.RequestedPassenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class RequestedPassengersAdapter extends RecyclerView.Adapter<RequestedPassengersAdapter.ApproveRequestHolder> {
    private ArrayList<RequestedPassengersModel> requestedPassengersModels;
    private final Context context;
    private FirebaseStorage storage =  FirebaseStorage.getInstance();
    private static String passengersImageString;



    public RequestedPassengersAdapter(ArrayList<RequestedPassengersModel> requestedPassengersModels, Context context) {
        this.requestedPassengersModels = requestedPassengersModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ApproveRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_requested_passenger,parent, false);
        return new ApproveRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveRequestHolder holder, int position) {

        holder.pAId = requestedPassengersModels.get(position).getPASSENGERID();
        holder.pAPickUpLocationId.setText(requestedPassengersModels.get(position).getPICKUPLOCATION());
        holder.pAName.setText(requestedPassengersModels.get(position).getPASSENGERNAME());
        holder.pAPickUpLat = requestedPassengersModels.get(position).getPICKUPLAT();
        holder.pAPickUpLon = requestedPassengersModels.get(position).getPICKUPLONG();
        holder.pAStatus.setText(requestedPassengersModels.get(position).getSTATUS());

        storage.getReference().child("Passenger").child(requestedPassengersModels.get(holder.getAdapterPosition()).getPASSENGERID()).child("profile.png").getDownloadUrl().addOnCompleteListener(
              new OnCompleteListener<Uri>() {
                  @Override
                  public void onComplete(@NonNull @NotNull Task<Uri> task) {

                      if (task.isSuccessful()) {
                          passengersImageString =task.getResult().toString();
                          Picasso.get().load(task.getResult().toString()).into(holder.pAImage);
                      }

                  }
              }
      );


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,RequestedPassengerProfile.class);
                SharedPreferences sharedPreferences;
                sharedPreferences = context.getSharedPreferences("PASSENGER_REQUESTFILE",MODE_PRIVATE);
                sharedPreferences.edit().putString("ThePassengersId",holder.pAId).apply();
                sharedPreferences.edit().putString("ThePassengersName",holder.pAName.getText().toString()).apply();
                sharedPreferences.edit().putString("ThePickupLatitude", String.valueOf(holder.pAPickUpLat)).apply();
                sharedPreferences.edit().putString("ThePickupLongitude", String.valueOf(holder.pAPickUpLon)).apply();
                sharedPreferences.edit().putString("ThePassengersStatus", holder.pAStatus.getText().toString()).apply();
                sharedPreferences.edit().putString(holder.pAId, passengersImageString).apply();

                intent.putExtra(holder.pAId,passengersImageString);
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return requestedPassengersModels.size();
    }

    public static class ApproveRequestHolder extends RecyclerView.ViewHolder {

        private ImageView pAImage;
        private TextView pAName,pAStatus;
        private TextView pAPickUpLocationId;
        private  String pAId;
        private LinearLayout mainLayout;
        private double pAPickUpLat, pAPickUpLon;


        public ApproveRequestHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainRequestedPassengerLayout);
            pAName = itemView.findViewById(R.id.pANameId);
            pAPickUpLocationId = itemView.findViewById(R.id.pickupLocationId);
            pAStatus = itemView.findViewById(R.id.statusId);
            pAImage = itemView.findViewById(R.id.pAImage);
        }
    }

}
