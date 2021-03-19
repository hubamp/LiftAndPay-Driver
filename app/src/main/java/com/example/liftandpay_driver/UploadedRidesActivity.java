package com.example.liftandpay_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

public class UploadedRidesActivity extends AppCompatActivity {

    private ArrayList<uploadedRidesModel> uploadedRidesModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageButton uploadBtn;
    private RelativeLayout mainLayout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String theDriverId = mAuth.getUid();
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_rides);


       recyclerView = findViewById(R.id.recyclerViewId);
       uploadBtn = findViewById(R.id.addRidebtnId);
       mainLayout = findViewById(R.id.mainLayout);

       uploadBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(UploadedRidesActivity.this, UploadRideActivity.class);
               startActivity(intent);
           }
       });


 /*       uploadedRidesModel uploadedRidesModel = new uploadedRidesModel(
                "documentng()" ,
                "document.getData().toString()",
                "toString()",
                4);
        uploadedRidesModels.add(uploadedRidesModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));
*/


        CollectionReference pendingRides = db.collection("Driver").document(theDriverId).collection("Pending Rides");

        db.collection("Driver").document("oS6CXZeTbDWsLdN34lfXgtPeRHn1").collection("Pending Rides")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Snackbar.make(recyclerView, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                        {
                            Toast.makeText(UploadedRidesActivity.this, "It works "+ documentSnapshot.getData()
                                    , Toast.LENGTH_LONG).show();

                        }

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Toast.makeText(UploadedRidesActivity.this,"This is successful "+queryDocumentSnapshots.getDocuments().size()
                                , Toast.LENGTH_LONG).show();

                    }
                });



        pendingRides.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                uploadedRidesModels.clear();
                for (QueryDocumentSnapshot document : value) {
                    uploadedRidesModel uploadedRidesModel = new uploadedRidesModel(
                            document.getData().get("startLocation").toString()+" - "+document.getData().get("endLocation").toString() ,
                            document.getData().get("Ride Date").toString(),
                            document.getData().get("Ride Time").toString(),
                            4
                    );
                    uploadedRidesModels.add(uploadedRidesModel);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this,LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));
            }
        });

        /*pendingRides
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Toast.makeText(UploadedRidesActivity.this, ""+ document.getData().get("Ride Time")+" "+document.getData().get("Ride Date"), Toast.LENGTH_LONG).show();
                                uploadedRidesModel uploadedRidesModel = new uploadedRidesModel(
                                        document.getData().get("startLocation").toString()+" - "+document.getData().get("endLocation").toString() ,
                                        document.getData().get("Ride Date").toString(),
                                        document.getData().get("Ride Time").toString(),
                                       4
                                );
                                uploadedRidesModels.add(uploadedRidesModel);
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(UploadedRidesActivity.this,LinearLayoutManager.VERTICAL,false));
                            recyclerView.setAdapter(new UploadedRidesAdapter(UploadedRidesActivity.this, uploadedRidesModels));

                        } else {
                            Toast.makeText(UploadedRidesActivity.this, ""+ task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });*/







    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent intent = new Intent(UploadedRidesActivity.this, PhoneAuthenticationActivity.class);
            startActivity(intent);
            finish();
        }
    }
}