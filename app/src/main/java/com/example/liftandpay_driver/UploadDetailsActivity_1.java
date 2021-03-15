package com.example.liftandpay_driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UploadDetailsActivity_1 extends AppCompatActivity {
  SingleActionForAllClass singleActionForAllClass = new SingleActionForAllClass();
  private View headerView;
  private View footerView;
  private LinearLayout headerLayout;
  private LinearLayout footerLayout;
  private ImageButton proceedImgBtn;
  private AppCompatImageView backwardButton;
  private SharedPreferences sharedPreferences;
  private TextView rideCost, rideDistance;
  private EditText nSeats, nOccupants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_1);

        sharedPreferences = this.getSharedPreferences("RIDE_UPLOADFILE",MODE_PRIVATE);

        headerLayout = findViewById(R.id.headerView);
        footerLayout = findViewById(R.id.footerView);

        headerView = getLayoutInflater().inflate(R.layout.header_view,headerLayout,false);
        footerView = getLayoutInflater().inflate(R.layout.footer_view, footerLayout,false);
        headerLayout.addView(headerView);
        footerLayout.addView(footerView);

        proceedImgBtn = findViewById(R.id.btn_proceed_id);
        backwardButton = findViewById(R.id.btn_backward_id);

        rideCost = findViewById(R.id.rideCostId);
        rideDistance = findViewById(R.id.rideDistanceId);
        nSeats = findViewById(R.id.nSeatsId);
        nOccupants = findViewById(R.id.nOccupantsId);

        int theRideCostValue = sharedPreferences.getInt("TheRideCost",0);
        int theRideDistanceValue = sharedPreferences.getInt("TheRideDistance",0);
        int theNumberOfSeatsValue = sharedPreferences.getInt("TheNumberOfSeats",0);
        int theNumberOfOccupantsValue = sharedPreferences.getInt("TheNumberOfOccupants",1);

        sharedPreferences.edit().putString("TheRideCost",rideCost.getText().toString()).apply();
        sharedPreferences.edit().putString("TheRideDistance",rideDistance.getText().toString()).apply();
        sharedPreferences.edit().putString("TheNumberOfSeats",nSeats.getText().toString()).apply();
        sharedPreferences.edit().putString("TheNumberOfOccupants",nOccupants.getText().toString()).apply();

        proceedImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_1.this , UploadDetailsActivity_2.class));
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
            }
        });

        backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_1.this , UploadDetailsActivity_2.class));
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
                finish();
            }
        });

    }
}