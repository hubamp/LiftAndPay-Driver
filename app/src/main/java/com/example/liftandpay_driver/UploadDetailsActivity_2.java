package com.example.liftandpay_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.SignUp.PhoneAuthenticationActivity;
import com.example.liftandpay_driver.fastClass.CheckForSignUpWorker;
import com.example.liftandpay_driver.fastClass.SingleActionForAllClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class UploadDetailsActivity_2 extends AppCompatActivity {

    SingleActionForAllClass singleActionForAllClass = new SingleActionForAllClass();

    TextView nextButton;
    AppCompatImageView addImage;
//    SharedPreferences driverInfoPreferences;
    EditText name, email, about;


    String mUid = FirebaseAuth.getInstance().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference profileRef = storage.getReference().child("Driver").child(mUid).child("profile.png");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_2);


        name = findViewById(R.id.nameId);
        email = findViewById(R.id.emailId);
        about = findViewById(R.id.aboutId);
        nextButton = findViewById(R.id.btn_proceed_id);
        addImage = findViewById(R.id.addImageId);

        addImage.setDrawingCacheEnabled(true);
        addImage.buildDrawingCache();
//        driverInfoPreferences = getApplication().getSharedPreferences("DRIVERSFILE", MODE_PRIVATE);

        addImage.setDrawingCacheEnabled(true);
        addImage.buildDrawingCache();

        //  driverInfoPreferences.edit().apply();



         /*When it loads fresh*/
      /*  if (driverInfoPreferences.contains("TheEmail")) {
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
*/

        if (getIntent().getStringArrayExtra("NewUser") != null) {
            Snackbar.make(name, "The number is a new number \nkindly setup before signing up", 10000).setBackgroundTint(getColor(R.color.primaryColors)).show();
        }

        /*When the focus changes*/
      /*  name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });*/

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
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);

                HashMap<Object, Object> getDriverDetails = new HashMap<>();


                getDriverDetails.put("Name", name.getText().toString());
                getDriverDetails.put("Email", email.getText().toString());
                getDriverDetails.put("About", about.getText().toString());


                db.collection("Driver").document(Objects.requireNonNull(mUid)).set(getDriverDetails, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Bitmap bitmap = ((BitmapDrawable) addImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = profileRef.putBytes(data);



                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Timber.e(exception);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Timber.e("Profile Upload Succesful");
                            }
                        });

                        finish();
                    }
                });
            }
        });





    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                addImage.setImageURI(selectedImage);

                Bitmap bitmap = ((BitmapDrawable) addImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();



//                driverInfoPreferences.edit().putString("TheImageString", Arrays.toString(data)).apply();

            }
        }
    }


}