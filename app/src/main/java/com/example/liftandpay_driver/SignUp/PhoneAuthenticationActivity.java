package com.example.liftandpay_driver.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.Dashboard;
import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.UploadDetailsActivity_2;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();//FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressBar progressBar;
    private TextView infoText;

    private boolean isNewUser;
    private SharedPreferences driversFile;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        EditText phoneNumberText = findViewById(R.id.phoneNumberId);
        EditText countryCodeText = findViewById(R.id.countryCodeId);
        Button verifyNumberBtn = findViewById(R.id.verifyPhoneNumberBtnId);
        progressBar = findViewById(R.id.verifyProgressId);
        infoText = findViewById(R.id.infoText);

        driversFile = getSharedPreferences("DRIVERSFILE", MODE_PRIVATE);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                if (getIntent().getStringExtra("Readiness") != null) {

                    HashMap<Object, Object> getDriverDetails = new HashMap<>();
                    HashMap<Object, Object> getName = new HashMap<>();

                    getName.put("Name", driversFile.getString("TheName", "null"));
                    getDriverDetails.put("Name", driversFile.getString("TheName", "null"));
                    getDriverDetails.put("Email", driversFile.getString("TheEmail", "null"));
                    getDriverDetails.put("About", driversFile.getString("TheAbout", "null"));
                    getDriverDetails.put("Car Model", driversFile.getString("TheCarModel", "null"));
                    getDriverDetails.put("Numberplate", driversFile.getString("TheCarNumberPlate", "null"));
                    getDriverDetails.put("Number Of Seats", driversFile.getString("TheCarNumberOfSeats", "null"));
                    getDriverDetails.put("Car color", driversFile.getString("TheCarColor", "null"));


                    mAuth.signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(PhoneAuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    mAuth = FirebaseAuth.getInstance();

                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(PhoneAuthenticationActivity.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();

                                        db.collection("DriversPhone").document(phoneNumber).set(getName);

                                        Intent intent = new Intent(PhoneAuthenticationActivity.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();

                                        db.collection("Driver").document(Objects.requireNonNull(mAuth.getUid())).set(getDriverDetails).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mAuth.signOut();
                                                Toast.makeText(PhoneAuthenticationActivity.this, "Signup failed", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    } else {
                                        mAuth.signOut();
                                        Toast.makeText(PhoneAuthenticationActivity.this, "Signup failed", Toast.LENGTH_LONG).show();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    infoText.setText(e.getMessage());
                                }
                            });

                } else {
                    mAuth.signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(PhoneAuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(PhoneAuthenticationActivity.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(PhoneAuthenticationActivity.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    infoText.setText(e.getMessage());
                                }
                            });
                }


                Toast.makeText(PhoneAuthenticationActivity.this, "Successful", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                infoText.setText(e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                infoText.setText("Waiting for code");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setText("Validiating ...");
                    }
                }, 10000);

                PhoneAuthProvider.getCredential(verificationId, token.toString());


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.GONE);
                        verifyNumberBtn.setVisibility(View.VISIBLE);
                    }
                }, 8000);

            }
        };


        verifyNumberBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                setVerificationCode(PhoneNumber);

                String number = phoneNumberText.getText().toString();
                String countryCode = "+" + countryCodeText.getText().toString();
                phoneNumber = countryCode + number;

                progressBar.setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);

                if (!number.isEmpty() && !countryCode.isEmpty()) {

                    //Go through the database to check if phone number exists
                    db.collection("DriversPhone").document(phoneNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            if (documentSnapshot.exists()) {

                                Log.i("User Existence", "TRUE");
                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder(mAuth)
                                                .setPhoneNumber(phoneNumber)       // Phone number to verify
                                                .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(PhoneAuthenticationActivity.this)                 // Activity (for callback binding)
                                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);
                            } else {

                                Log.i("User Existence", "FALSE");


                                String bundle = getIntent().getStringExtra("Readiness");


                                if (bundle != null && bundle.equals("Ready")) {
                                    Log.i("Bundle Info", "equal to null and contains readiness");

                                    Log.i("User Existence", "UNDER_SURVEILLANCE");
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(mAuth)
                                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                                    .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                                                    .setActivity(PhoneAuthenticationActivity.this)                 // Activity (for callback binding)
                                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);
                                } else {
                                    Log.i("Bundle Info", "Not null and contains readiness");
                                    Intent intent = new Intent(PhoneAuthenticationActivity.this, UploadDetailsActivity_2.class);
                                    intent.putExtra("NewUser", "New");
                                    startActivity(intent);

                                    progressBar.setVisibility(View.GONE);
                                    verifyNumberBtn.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                    });

                } else {
                    Toast.makeText(PhoneAuthenticationActivity.this, "Enter Your Phone Number", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(PhoneAuthenticationActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }
}