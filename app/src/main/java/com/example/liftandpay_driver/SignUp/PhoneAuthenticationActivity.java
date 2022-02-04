package com.example.liftandpay_driver.SignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressBar progressBar;
    private TextView infoText;


    private String phoneNumber;

    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        EditText phoneNumberText = findViewById(R.id.phoneNumberId);
        EditText countryCodeText = findViewById(R.id.countryCodeId);
        Button verifyNumberBtn = findViewById(R.id.verifyPhoneNumberBtnId);
        progressBar = findViewById(R.id.verifyProgressId);
        infoText = findViewById(R.id.infoText);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                mAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(PhoneAuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    HashMap<String,Object> data = new HashMap<>();
                                    data.put("Name","Anonymous");
                                    db.collection("Driver")
                                            .document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (!task.getResult().exists()) {

                                                task.getResult().getReference().set(data);
                                                Toast.makeText(PhoneAuthenticationActivity.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(PhoneAuthenticationActivity.this, Dashboard.class);
                                                startActivity(intent);
                                            }
                                            else {

                                                Toast.makeText(PhoneAuthenticationActivity.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(PhoneAuthenticationActivity.this, Dashboard.class);
                                                startActivity(intent);
                                            }
                                            finish();
                                        }
                                    });
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


                    Log.i("User Existence", "TRUE");
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(PhoneAuthenticationActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);


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