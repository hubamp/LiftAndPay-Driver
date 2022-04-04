package com.LnPay.driver.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.LnPay.driver.R;
import com.LnPay.driver.chats.ChatList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class MenuListActivity extends AppCompatActivity {

    private ImageButton backBtn;

    //individual menu declaration
    private LinearLayout profileView;
    private LinearLayout messageView,logout;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String mUID = FirebaseAuth.getInstance().getUid();

    private ImageView profilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //menu initiallization
        backBtn = findViewById(R.id.backButton_P1);
        profileView = findViewById(R.id.profileViewId);
        profilePicture = findViewById(R.id.profilePicture);
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
//                Toast.makeText(MenuListActivity.this,"Coming soon", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MenuListActivity.this, ChatList.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*logout.setOnLongClickListener(view->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MenuListActivity.this, PhoneAuthenticationActivity.class);
            startActivity(intent);
            return true;
        });*/

        storage.getReference().child("Driver").child(mUID).child("profile.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(profilePicture);
                        }

                    }
                }
        );
    }
}