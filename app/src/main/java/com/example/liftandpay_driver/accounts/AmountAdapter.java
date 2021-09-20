package com.example.liftandpay_driver.accounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;

import java.util.List;

public class AmountAdapter extends RecyclerView.Adapter<AmountAdapter.AmountViewHolder> {

    Context Context;
    List<Amount> Amount;

    public AmountAdapter(Context context, List<Amount> amount){
        this.Context = context;
        this.Amount = amount;
    }

    @Override
    public AmountViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        AmountViewHolder amountViewHolder = null;
        View amountView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        amountViewHolder = new AmountViewHolder(amountView);

        return amountViewHolder;
    }

    @Override
    public void onBindViewHolder(AmountViewHolder holder, final int position){
        final AmountViewHolder amountViewHolder = (AmountViewHolder) holder;

        Amount amt = Amount.get(position);

        amountViewHolder.money.setText(String.valueOf(amt.getMoney()));

        amountViewHolder.name.setText(amt.getName());

        amountViewHolder.date.setText(amt.getDate());
    }
    @Override
    public int getItemCount(){
        return Amount.size();
    }

    public class AmountViewHolder extends RecyclerView.ViewHolder {

        private TextView money;
        private TextView name;
        private TextView date;


        public AmountViewHolder(@NonNull View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.money);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
        }
    }

}
