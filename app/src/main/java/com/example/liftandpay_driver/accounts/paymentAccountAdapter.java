package com.example.liftandpay_driver.accounts;

import static com.example.liftandpay_driver.API.paystack.paystack.authUrl;

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

import com.example.liftandpay_driver.API.paystack.paystack;
import com.example.liftandpay_driver.History.RideHistoryAdapter;
import com.example.liftandpay_driver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class paymentAccountAdapter extends RecyclerView.Adapter<paymentAccountAdapter.AccountHolder> {

    private Context context;
    private ArrayList<accountModel> accountModels = new ArrayList<>();
    private static paystack.postDataResults myPost;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String rideId;
    private String mUid = FirebaseAuth.getInstance().getUid();


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


        holder.singlePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When Pay button is clicked, do a post request to paystack and get the authorization url
                /*This authorization url is what will be required to get to the paystack API */

                db.collection("Driver").document(mUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        documentSnapshot.getString("email");

                    }
                });
                myPost = new paystack().
                        postData(
                                "hubamp@gmail.com",
                                accountModels.get(holder.getAdapterPosition()).getTotalAmount(),
                                mUid,
                                rideId
                        );

                dataNullCheck();

//                Log.e("PaymentAdapterAccount", myPost.getAuthUrl());

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


    private int time = 1000;

    private void dataNullCheck() {

        if (authUrl == null) {

            if (time < 10000) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dataNullCheck();
                    }
                }, time + 200);
                Log.e("payment URL NOT RETRIEVED", "myPost.getAuthUrl()");
            }
            else{
                Toast.makeText(context,"Failed to open",Toast.LENGTH_LONG).show();
            }

        } else {
            Log.e("payment URL RETRIEVED", authUrl);
            Intent i = new Intent(context, Payment.class);
            i.putExtra(
                    "authUrl",
                    authUrl);
            context.startActivity(i);

            authUrl = null;
        }
    }
}
