package com.example.liftandpay_driver.driverProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.UploadRideActivity;
import com.example.liftandpay_driver.UploadedRidesActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private LinearLayout profileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backBtn = findViewById(R.id.backButton);
        profileView = findViewById(R.id.profileViewId);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UploadedRidesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}