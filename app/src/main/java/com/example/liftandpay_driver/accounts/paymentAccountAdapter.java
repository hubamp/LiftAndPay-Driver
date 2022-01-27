package com.example.liftandpay_driver.accounts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.API.paystack.paystack;
import com.example.liftandpay_driver.History.RideHistoryAdapter;
import com.example.liftandpay_driver.R;

import java.util.ArrayList;

public class paymentAccountAdapter extends RecyclerView.Adapter<paymentAccountAdapter.AccountHolder> {

    private Context context;
    private ArrayList<accountModel> accountModels = new ArrayList<>();

    public paymentAccountAdapter(Context context, ArrayList<accountModel> accountModels) {
        this.context = context;
        this.accountModels = accountModels;
    }

    @NonNull
    @Override
    public paymentAccountAdapter.AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_ridepayment, parent, false);
        return new paymentAccountAdapter.AccountHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull paymentAccountAdapter.AccountHolder holder, int position) {

        String totalAmount = accountModels.get(holder.getAdapterPosition()).getTotalAmount()+"";
        holder.stLoc.setText(accountModels.get(holder.getAdapterPosition()).getStart());
        holder.endLoc.setText(accountModels.get(holder.getAdapterPosition()).getEnd());
        holder.rideCost.setText(totalAmount);

        //When Pay button is clicked
        holder.singlePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,Payment.class);
                new paystack().postData("huba@gmail.com",5.0);
                context.startActivity(i);
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
