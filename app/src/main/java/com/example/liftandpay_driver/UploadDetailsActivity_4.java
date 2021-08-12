package com.example.liftandpay_driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liftandpay_driver.fastClass.SingleActionForAllClass;

public class UploadDetailsActivity_4 extends AppCompatActivity {
    SingleActionForAllClass singleActionForAllClass = new SingleActionForAllClass();


    private TextView nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_4);

        nextButton = findViewById(R.id.btn_proceed_id);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_4.this , UploadDetailsActivity_3.class));
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
            }
        });

       /* backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_2.this , UploadDetailsActivity_1.class));
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
                finish();
            }
        });*/
    }
}