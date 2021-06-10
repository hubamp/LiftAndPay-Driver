package com.example.liftandpay_driver.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.chats.ChatActivity;
import com.example.liftandpay_driver.chats.ChatList;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;

public class MenuListActivity extends AppCompatActivity {

    private ImageButton backBtn;

    //individual menu declaration
    private LinearLayout profileView;
    private LinearLayout messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //menu initiallization
        backBtn = findViewById(R.id.backButton_P1);
        profileView = findViewById(R.id.profileViewId);
        messageView = findViewById(R.id.messageViewId);


        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuListActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuListActivity.this, ChatList.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuListActivity.this, UploadedRidesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}