package com.LnPay.driver.uploadedRide.RequestedPassenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.LnPay.driver.R;
import com.LnPay.driver.chats.ChatActivity;
import com.LnPay.driver.pAPickUpLocation.ViewPickUpLocation;
import com.LnPay.driver.uploadedRide.RequestedPassenger.reviews.reviewAdapter;
import com.LnPay.driver.uploadedRide.RequestedPassenger.reviews.reviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestedPassengerProfile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<reviewModel> reviewModels;

    private TextView chatbtn;
    private TextView next;
    private TextView pAName;
    private String thePassengerId;
    private String thePassengerName;
    private String thePassengerProfile;
    private ImageView pAProfileImage;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_passenger_profile);

        sharedPreferences = getSharedPreferences("PASSENGER_REQUESTFILE",MODE_PRIVATE);
        chatbtn = findViewById(R.id.pAChatbtn_id);
        next = findViewById(R.id.pANxtBtnId);
        pAProfileImage = findViewById(R.id.pAImage);
        pAName = findViewById(R.id.pAName);

        //was taken from RequestedPassengersAdapter.java
        thePassengerId = sharedPreferences.getString("ThePassengersId",null);
        thePassengerName = sharedPreferences.getString("ThePassengersName",null);
        thePassengerProfile = getIntent().getStringExtra(thePassengerId);

        if (thePassengerProfile != null)
        Picasso.get().load(thePassengerProfile).into(pAProfileImage);

        pAName.setText(thePassengerName);


        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestedPassengerProfile.this, ChatActivity.class);
                intent.putExtra("passengerId",thePassengerId);
                intent.putExtra("passengerName",thePassengerName);
                intent.putExtra("passengerProfile",thePassengerProfile);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(RequestedPassengerProfile.this,LinearLayoutManager.VERTICAL,false));
        reviewModels = new ArrayList<>();

        reviewModel reviewModel = new reviewModel();
//        reviewModels.add(reviewModel);
        recyclerView.setAdapter(new reviewAdapter(reviewModels));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().clear().apply();
    }


}