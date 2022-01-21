package com.example.liftandpay_driver.History.RequestedPassengers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;

import java.util.ArrayList;

public class ReqstdPassAdapter extends RecyclerView.Adapter<ReqstdPassAdapter.reqstdPassHolder> {

    private Context context;
    private ArrayList<reqstdPassModel> reqstdPassModels = new ArrayList<>();


    public ReqstdPassAdapter(Context context, ArrayList<reqstdPassModel> reqstdPassModels) {
        this.context = context;
        this.reqstdPassModels = reqstdPassModels;
    }

    @NonNull
    @Override
    public ReqstdPassAdapter.reqstdPassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_requested_passenger, parent, false);
        return new ReqstdPassAdapter.reqstdPassHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReqstdPassAdapter.reqstdPassHolder holder, int position) {

        holder.pAStat.setText(reqstdPassModels.get(holder.getAdapterPosition()).getStatus());
        holder.pAName.setText(reqstdPassModels.get(holder.getAdapterPosition()).getName());
        holder.pALoc.setText(reqstdPassModels.get(holder.getAdapterPosition()).getPickupLocation());

    }

    @Override
    public int getItemCount() {
        return reqstdPassModels.size();
    }

    public class reqstdPassHolder extends RecyclerView.ViewHolder {

        private TextView pAName;
        private TextView pAStat;
        private TextView pALoc;

        public reqstdPassHolder(@NonNull View itemView) {
            super(itemView);

            pAName = itemView.findViewById(R.id.pANameId);
            pAStat = itemView.findViewById(R.id.statusId);
            pALoc  = itemView.findViewById(R.id.pickupLocationId);
        }
    }
}
