package com.example.liftandpay_driver.uploadedRide.RequestedPassenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.chats.ChatActivity;
import com.example.liftandpay_driver.chats.model_ChatActivity.messageAdapter;
import com.example.liftandpay_driver.chats.model_ChatActivity.messageModel;
import com.example.liftandpay_driver.pAPickUpLocation.ViewPickUpLocation;
import com.example.liftandpay_driver.uploadedRide.RequestedPassenger.reviews.reviewAdapter;
import com.example.liftandpay_driver.uploadedRide.RequestedPassenger.reviews.reviewModel;

import java.util.ArrayList;

public class RequestedPassengerProfile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<reviewModel> reviewModels;

    private TextView chatbtn;
    private TextView next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_passenger_profile);

        chatbtn = findViewById(R.id.pAChatbtn_id);
        next = findViewById(R.id.pANxtBtnId);

        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestedPassengerProfile.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestedPassengerProfile.this , ViewPickUpLocation.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.reviewRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(RequestedPassengerProfile.this,LinearLayoutManager.VERTICAL,true));
        reviewModels = new ArrayList<>();

        reviewModel reviewModel = new reviewModel();
        reviewModels.add(reviewModel);
        reviewModels.add(reviewModel);
        reviewModels.add(reviewModel);
        reviewModels.add(reviewModel);
        recyclerView.setAdapter(new reviewAdapter(reviewModels));

    }
}