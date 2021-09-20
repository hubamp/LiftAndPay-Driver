package com.example.liftandpay_driver.accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;

import java.util.ArrayList;
import java.util.List;

public class Received extends Fragment {
    List<Amount> list_amount;
    RecyclerView recyclerview;
     AmountAdapter amountAdapter;

    public Received(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.all, container, false);
        recyclerview = view.findViewById(R.id.recyclerview);
        loadAccount();

        amountAdapter = new  AmountAdapter(getContext(),list_amount);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false );
        recyclerview.setLayoutManager(layoutManager);
//        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(amountAdapter);

        return view;
    }

    void loadAccount() {
        list_amount = new ArrayList<>();
        list_amount.add(new Amount(30.20, "Tonny", "21-11-2016"));
        list_amount.add(new Amount(100.70, "Diga", "17-2-2021"));
        list_amount.add(new Amount(60.00, "Bra Jed", "9-3-2021"));
        list_amount.add(new Amount(10.50, "Chancellor", "9-4-2021"));
    }


}