package com.example.liftandpay_driver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UploadedRidesAdapter extends RecyclerView.Adapter<UploadedRidesAdapter.uploadedViewHolder> {

    Context context;
    ArrayList<uploadedRidesModel> uploadedRidesModels;

    public UploadedRidesAdapter(Context context, ArrayList<uploadedRidesModel> uploadedRidesModels) {
        this.context = context;
        this.uploadedRidesModels = uploadedRidesModels;
    }

    @NonNull
    @Override
    public UploadedRidesAdapter.uploadedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_rides_model,parent, false);

        return new uploadedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedRidesAdapter.uploadedViewHolder holder, int position) {

        uploadedRidesModel uploadedRidesModel = uploadedRidesModels.get(position);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.toMapPrograssBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, UploadedRideMap.class);
                context.startActivity(intent);
            }
        });

        holder.requestedPassengersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestedPassengersSheet requestedPassengersSheet = new RequestedPassengersSheet();
                FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                requestedPassengersSheet.show(manager,null);
            }
        });


        holder.rTimeView.setText(uploadedRidesModel.getRIDETIME());
        holder.rDateView.setText(uploadedRidesModel.getRIDEDATE());
        holder.rJourney.setText(uploadedRidesModel.getJOURNEY());
    }

    @Override
    public int getItemCount() {
        return uploadedRidesModels.size();
    }


    public class uploadedViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ProgressBar toMapPrograssBar;
        private ImageView addRideBtn;
        private RelativeLayout requestedPassengersBtn;

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


        }
    }
}
