package com.example.liftandpay_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UploadDetailsActivity_1 extends AppCompatActivity {
  private View headerView;
  private View footerView;
  private LinearLayout headerLayout;
  private LinearLayout footerLayout;
  private ImageButton proceedImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_1);

        headerLayout = findViewById(R.id.headerView);
        footerLayout = findViewById(R.id.footerView);

        headerView = getLayoutInflater().inflate(R.layout.header_view,headerLayout,false);
        footerView = getLayoutInflater().inflate(R.layout.footer_view, footerLayout,false);
        headerLayout.addView(headerView);
        footerLayout.addView(footerView);

        proceedImgBtn = findViewById(R.id.btn_proceed_id);


        proceedImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_1.this , UploadDetailsActivity_2.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }
}