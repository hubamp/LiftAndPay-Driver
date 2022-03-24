package com.LnPay.driver.SignUp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.LnPay.driver.Dashboard;
import com.LnPay.driver.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button signinBtn;

    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);





        signinBtn = findViewById(R.id.signin);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
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

    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            (result) -> {

                if(result.getIdpResponse()!=null) {
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("Name","Anonymous");
                    data.put("Signup_date", new Timestamp(new Date()));
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

            });


    private void startSignIn() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                ))
                .setTheme(R.style.FirebaseUITheme)
                .build();

        signInLauncher.launch(signInIntent);

    }

}