package com.example.liftandpay_driver.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressBar progressBar;
    private TextView infoText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        EditText phoneNumberText = findViewById(R.id.phoneNumberId);
        EditText countryCodeText = findViewById(R.id.countryCodeId);
        Button verifyNumberBtn = findViewById(R.id.verifyPhoneNumberBtnId);
        progressBar = findViewById(R.id.verifyProgressId);
        infoText = findViewById(R.id.infoText);


       /* mAuth.signInWithEmailAndPassword("hubamp@gmail.com", "123456")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(PhoneAuthenticationActivity.this,"Successful",Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(PhoneAuthenticationActivity.this,"Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });*/

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Toast.makeText(PhoneAuthenticationActivity.this,"Successful",Toast.LENGTH_LONG).show();

                mAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(PhoneAuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(PhoneAuthenticationActivity.this,"Successfully Signed Up",Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(PhoneAuthenticationActivity.this , UploadedRidesActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                infoText.setText(e.getMessage());
                                Toast.makeText(PhoneAuthenticationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
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

                infoText.setText("Code Sent");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setText("Validiating ...");
                    }
                },4000);


                PhoneAuthProvider.getCredential(verificationId, token.toString());

            }
        };


            verifyNumberBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                setVerificationCode(PhoneNumber);
                    String number  = phoneNumberText.getText().toString();
                    String countryCode = "+"+ countryCodeText.getText().toString();
                    String phoneNumber =  countryCode + number;
                    Toast.makeText(getBaseContext(), "This is your phone number " + phoneNumber , Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);

                    if(!number.isEmpty() && !countryCode.isEmpty()) {
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(PhoneAuthenticationActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    }
                    else
                    {
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

        if(currentUser != null)
        {
            Intent intent = new Intent(PhoneAuthenticationActivity.this , UploadedRidesActivity.class);
            startActivity(intent);
            finish();
        }
    }




}