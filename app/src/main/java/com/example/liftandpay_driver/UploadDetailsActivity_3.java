package com.example.liftandpay_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liftandpay_driver.SignUp.PhoneAuthenticationActivity;
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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class UploadDetailsActivity_3 extends AppCompatActivity {

    SingleActionForAllClass singleActionForAllClass = new SingleActionForAllClass();

    private SharedPreferences driverInfoPreferences;
    private TextView nextButton;
    private TextInputEditText carModel, numberPlate, numberOfSeats, color;
    private ImageView mainCarImage, sideCarImage;
    private boolean errorWhileValidating = false;
    private String mUid = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mainImgRef = storage.getReference().child("Driver").child(mUid).child("main.png");
    StorageReference sideCarRef = storage.getReference().child("Driver").child(mUid).child("otherImage.png");

//    private FirebaseFirestore db = F

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_details_3);

        nextButton = findViewById(R.id.btn_proceed_id);
        carModel = findViewById(R.id.carModelId);
        numberPlate = findViewById(R.id.numberPlateId);
        numberOfSeats = findViewById(R.id.numberOfSeatsId);
        color = findViewById(R.id.colorInput);

        mainCarImage = findViewById(R.id.mainImage);
        sideCarImage = findViewById(R.id.sideImage);

        mainCarImage.setDrawingCacheEnabled(true);
        sideCarImage.setDrawingCacheEnabled(true);
        mainCarImage.buildDrawingCache();
        sideCarImage.buildDrawingCache();

        driverInfoPreferences = getApplication().getSharedPreferences("DRIVERSFILE", MODE_PRIVATE);


        mainCarImage.setDrawingCacheEnabled(true);
        mainCarImage.buildDrawingCache();
        sideCarImage.setDrawingCacheEnabled(true);
        sideCarImage.buildDrawingCache();


        carModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                driverInfoPreferences.edit().putString("TheCarModel", carModel.getText().toString()).apply();
            }
        });

        numberPlate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                driverInfoPreferences.edit().putString("TheCarNumberPlate", numberPlate.getText().toString()).apply();

            }
        });


        numberOfSeats.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                driverInfoPreferences.edit().putString("TheCarNumberOfSeats", numberOfSeats.getText().toString()).apply();
            }
        });


        color.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                driverInfoPreferences.edit().putString("TheCarColor", color.getText().toString()).apply();
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs(carModel);
                validateInputs(numberOfSeats);
                validateInputs(numberPlate);



                if (!errorWhileValidating) {

                    HashMap<Object, Object> getDriverDetails = new HashMap<>();

                    getDriverDetails.put("Car Model", Objects.requireNonNull(carModel.getText()).toString());
                    getDriverDetails.put("Numberplate", Objects.requireNonNull(numberPlate.getText()).toString());
                    getDriverDetails.put("Number Of Seats",Integer.valueOf(Objects.requireNonNull(numberOfSeats.getText()).toString()));
                    getDriverDetails.put("Car color", Objects.requireNonNull(color.getText()).toString());

                    db.collection("Driver").document(Objects.requireNonNull(mUid)).set(getDriverDetails, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Bitmap bitmap = ((BitmapDrawable) mainCarImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            Bitmap bitmap1 = ((BitmapDrawable) sideCarImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                            byte[] data1 = baos.toByteArray();

                            UploadTask uploadTask = mainImgRef.putBytes(data);
                            UploadTask uploadTask1 = sideCarRef.putBytes(data1);



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

                            uploadTask1.addOnFailureListener(new OnFailureListener() {
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
                            overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
                        }
                    });
                   } else {
                    Snackbar.make(color, "Complete form before you can continue", 4000).setBackgroundTint(ContextCompat.getColor(UploadDetailsActivity_3.this, R.color.failure)).show();
                }

                errorWhileValidating = false;
            }
        });


        mainCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 123);
            }
        });

        sideCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 321);
            }
        });

       /* backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadDetailsActivity_3.this , UploadDetailsActivity_2.class));
                overridePendingTransition(singleActionForAllClass.ENTRY_ANIMATION_FOR_ACTIVITY, singleActionForAllClass.EXIT_ANIMATION_FOR_ACTIVITY);
                UploadDetailsActivity_3.this.finish();
            }
        });*/
    }


    private void validateInputs(TextInputEditText inputView) {

        if (inputView.getText().toString().trim().equals("")) {
            inputView.setError("Required");
            errorWhileValidating = true;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {

            if (data != null) {
                mainCarImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) mainCarImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();

//                driverInfoPreferences.edit().putString("TheMainCarImageString", Arrays.toString(datas)).apply();

            }
        }

        if (requestCode == 321) {

            if (data != null) {
                sideCarImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) mainCarImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();

//                driverInfoPreferences.edit().putString("TheSideImageString", Arrays.toString(datas)).apply();

            }
        }
    }


}