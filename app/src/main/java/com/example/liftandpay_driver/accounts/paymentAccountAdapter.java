package com.example.liftandpay_driver.accounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    }

    @Override
    public int getItemCount() {
        return accountModels.size();
    }

    public class AccountHolder extends RecyclerView.ViewHolder {
        public AccountHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
