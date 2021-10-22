package com.example.liftandpay_driver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.liftandpay_driver.SignUp.PhoneAuthenticationActivity;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(SplashScreen.this);

        lottieAnimationView = findViewById(R.id.lottie);
        lottieAnimationView.animate().setDuration(4000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
// Using handler with postDelayed called runnable run method

            @Override

            public void run() {
                // close this activity

                Intent i;

                if (mAuth.getCurrentUser() == null) {
                    i = new Intent(SplashScreen.this, PhoneAuthenticationActivity.class);
                } else {
                    i = new Intent(SplashScreen.this, Dashboard.class);
                }
                startActivity(i);
                finish();

            }

        }, 4 * 1000); // wait for 4 seconds

    }

}