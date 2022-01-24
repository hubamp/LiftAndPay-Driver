package com.example.liftandpay_driver.accounts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.liftandpay_driver.History.RideHistoryInfo;
import com.example.liftandpay_driver.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {




    private RecyclerView recyclerView;
    private paymentAccountAdapter paymentAccountAdapter;
    private ArrayList<accountModel> accountModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        recyclerView = findViewById(R.id.paymentRecycler);


        accountModels.add(new accountModel("","",5.5));
        accountModels.add(new accountModel("","",5.5));
        accountModels.add(new accountModel("","",5.5));
        accountModels.add(new accountModel("","",5.5));
        accountModels.add(new accountModel("","",5.5));

        paymentAccountAdapter = new paymentAccountAdapter(AccountActivity.this,accountModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(AccountActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(paymentAccountAdapter);

    }
}