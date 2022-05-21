package com.LnPay.driver.accounts;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.LnPay.driver.API.paystack.paystack;
import com.LnPay.driver.Dashboard;
import com.LnPay.driver.History.RideHistoryInfo;
import com.LnPay.driver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class paymentAccountAdapter extends RecyclerView.Adapter<paymentAccountAdapter.AccountHolder> {

    private Context context;
    private ArrayList<accountModel> accountModels = new ArrayList<>();
    private static paystack.postDataResults myPost;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String rideId;
    private final String mUid = FirebaseAuth.getInstance().getUid();


    public paymentAccountAdapter(Context context, ArrayList<accountModel> accountModels) {
        this.context = context;
        this.accountModels = accountModels;
    }

    @NonNull
    @Override
    public paymentAccountAdapter.AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_ridepayment, parent, false);
        return new paymentAccountAdapter.AccountHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull paymentAccountAdapter.AccountHolder holder, int position) {

        String totalAmount = accountModels.get(holder.getAdapterPosition()).getTotalAmount() + "";
        holder.stLoc.setText(accountModels.get(holder.getAdapterPosition()).getStart());
        holder.endLoc.setText(accountModels.get(holder.getAdapterPosition()).getEnd());
        holder.rideCost.setText(totalAmount);

        rideId = accountModels.get(holder.getAdapterPosition()).getRideId();

        //if there the amount is 0 display paid.

        holder.singlePayBtn.setEnabled(true);
        holder.singlePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When Pay button is clicked, do a post request to paystack and get the authorization url
                /*This authorization url is what will be required to get to the paystack API */

//                holder.toMapPrograssBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, RideHistoryInfo.class);
                intent.putExtra("TheRideId", accountModels.get(holder.getAdapterPosition()).getRideId());
                intent.putExtra("TheEndLat", accountModels.get(holder.getAdapterPosition()).getEndLat());
                intent.putExtra("TheEndLon", accountModels.get(holder.getAdapterPosition()).getEndLon());
                intent.putExtra("TheStLat", accountModels.get(holder.getAdapterPosition()).getStartLat());
                intent.putExtra("TheStLon", accountModels.get(holder.getAdapterPosition()).getStartLon());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return accountModels.size();
    }

    public class AccountHolder extends RecyclerView.ViewHolder {

        private TextView stLoc;
        private TextView endLoc;
        private TextView rideCost;
        private TextView singlePayBtn;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);

            singlePayBtn = itemView.findViewById(R.id.singlePay);
            stLoc = itemView.findViewById(R.id.startingId);
            endLoc = itemView.findViewById(R.id.endingId);
            rideCost = itemView.findViewById(R.id.rideCostId);
        }
    }


}
