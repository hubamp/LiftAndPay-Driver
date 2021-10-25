package com.example.liftandpay_driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.fastClass.SingleActionForAllClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class UploadDetailsActivity_2 extends AppCompatActivity {

    SingleActionForAllClass singleActionForAllClass = new SingleActionForAllClass();

    TextView nextButton;
    AppCompatImageView addImage;
    SharedPreferences driverInfoPreferences;
    EditText name, email, about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_2);


        name = findViewById(R.id.nameId);
        email = findViewById(R.id.emailId);
        about = findViewById(R.id.aboutId);
        nextButton = findViewById(R.id.btn_proceed_id);
        addImage = findViewById(R.id.addImageId);
        driverInfoPreferences = getApplication().getSharedPreferences("DRIVERSFILE", MODE_PRIVATE);
      //  driverInfoPreferences.edit().apply();



         /*When it loads fresh*/
        if (driverInfoPreferences.contains("TheEmail")) {
            String em =driverInfoPreferences.getString("TheEmail","null");
            Toast.makeText(getApplicationContext(),em,Toast.LENGTH_LONG ).show();
            email.setText(driverInfoPreferences.getString("TheEmail", "null"));
            Log.i("Content Available",em);
        }

        if (driverInfoPreferences.contains("TheName")) {
            Log.i("Content Available","name");
            name.setText(driverInfoPreferences.getString("TheName", "null"));
        }

        if (driverInfoPreferences.contains("TheAbout")) {
            Log.i("Content Available",driverInfoPreferences.getString("TheAbout", "null"));
            about.setText(driverInfoPreferences.getString("TheAbout", "nothing"));
        }

        if (driverInfoPreferences.contains("TheImageString")) {
            addImage.setImageURI(Uri.parse(driverInfoPreferences.getString("TheImageString", "")));
        }


        if (getIntent().getStringArrayExtra("NewUser") != null) {
            Snackbar.make(name, "The number is a new number \nkindly setup before signing up", 10000).setBackgroundTint(getColor(R.color.primaryColors)).show();
        }

        /*When the focus changes*/
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                driverInfoPreferences.edit().putString("TheName", name.getText().toString()).apply();
                Log.i("Focus Changed","name");
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                driverInfoPreferences.edit().putString("TheEmail", email.getText().toString()).apply();
                Log.i("Focus Changed","Email");
            }
        });

        about.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                driverInfoPreferences.edit().putString("TheAbout", about.getText().toString()).apply();
                Log.i("Focus Changed","about");

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_2.this, UploadDetailsActivity_3.class));
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
            }
        });




    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                addImage.setImageURI(selectedImage);

                driverInfoPreferences.edit().putString("TheImageString", selectedImage.toString()).apply();

            }
        }
    }
}