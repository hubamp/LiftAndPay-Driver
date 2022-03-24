package com.LnPay.driver.fastClass;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.LnPay.driver.UploadDetailsActivity_2;
import com.LnPay.driver.UploadDetailsActivity_3;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

public class CheckForSignUpWorker extends Worker {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();

    Context activity;


    public CheckForSignUpWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        activity = context;
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {


        db.collection("Driver")
                .document(mUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.exists()) {
                    Log.i("Driver Details", "Exists");

                    if (
                            !value.contains("About") ||
                                    !value.contains("Car Model") ||
                                    !value.contains("Car color") ||
                                    !value.contains("Numberplate")
                    ) {
                        Log.i("Driver Details", "Incomplete");
                        Toast.makeText(getApplicationContext(), "Please complete your details", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), UploadDetailsActivity_3.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(i);

                    } else {
                        Log.i("Driver Details", "Complete");

                    }


                    if (
                            !value.contains("Name") || !value.contains("Email")
                    ) {
                        Log.i("Driver Details", "Incomplete");
                        Toast.makeText(getApplicationContext(), "Please complete your details", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), UploadDetailsActivity_2.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(i);

                    } else {
                        Log.i("Driver Details", "Complete");

                    }
                }


            }
        });


        return Result.success();

    }



}
