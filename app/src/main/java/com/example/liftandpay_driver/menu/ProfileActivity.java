package com.example.liftandpay_driver.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.liftandpay_driver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, mainName, email, phone, location, noRides;
    private String mUID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ShapeableImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        name = findViewById(R.id.driverName);
        mainName = findViewById(R.id.mainNameId);
        email = findViewById(R.id.driverEmail);
        phone = findViewById(R.id.driverNumber);
        location = findViewById(R.id.driverLocation);
        noRides = findViewById(R.id.numberOfRides);
        profileImage = findViewById(R.id.imageProfileId);

        db.collection("Driver").document(mUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                name.setText(documentSnapshot.getString("Name"));
                mainName.setText(documentSnapshot.getString("Name"));

                if (documentSnapshot.contains("Email"))
                    email.setText(documentSnapshot.getString("Email"));


                phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                );

                if (documentSnapshot.contains("Location")) location.setText("Not Set");

                noRides.setText("0");
            }
        });


        storage.getReference().child("Driver").child(mUID).child("profile.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(profileImage);
                        }

                    }
                }
        );
    }
}