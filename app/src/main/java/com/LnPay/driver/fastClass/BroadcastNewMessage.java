package com.LnPay.driver.fastClass;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.LnPay.driver.R;
import com.LnPay.driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class BroadcastNewMessage extends JobService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();
    private static String passengerName;
    private boolean jobCancelled;

    SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("BACKGROUNDFILE", MODE_PRIVATE);


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroundWork(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("Baguvix", "onStopJob: Pre-Cancelled");
        jobCancelled = true;
        return true;
    }

    private void doBackgroundWork(JobParameters params){

        Log.i("New Chat", "Chat Notification Started");

        db.collection("Chat").document(mUid).collection("Passengers").addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        Log.i("New Chat", "Checking For Notification");


                        for ( DocumentChange documentChange : value.getDocumentChanges()) {

                            if (jobCancelled){
                                return;
                            }


                            db.collection("Passenger").document(documentChange.getDocument().getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    passengerName = documentSnapshot.getString("Name");

                                    switch (documentChange.getType()) {
                                        case MODIFIED:
                                            if (Objects.requireNonNull(documentChange.getDocument().get("ChatMode")).toString().equals("2"))
                                                buildNotification(passengerName, documentChange.getDocument().getString("Message"), documentChange.getNewIndex());

                                        case ADDED:
                                            int newDocumentSize = value.getDocuments().size();
                                            int oldDocumentSize = sharedPreferences.getInt("TheTotalNumberOfChats", -1);

                                            if (oldDocumentSize < newDocumentSize) {

                                                Log.i("New Chat", documentChange.getDocument().getString("Message"));
                                                Log.i("New Total old", "" + oldDocumentSize);
                                                Log.i("New Total new", "" + newDocumentSize);
                                                buildNotification(passengerName, documentChange.getDocument().getString("Message"), 200);
                                                sharedPreferences.edit().putInt("TheTotalNumberOfChats", newDocumentSize).apply();

                                            }
                                            oldDocumentSize = sharedPreferences.getInt("TheTotalNumberOfChats", -1);
                                            Log.i("New Total old 01", "" + oldDocumentSize);

                                        case REMOVED:
                                            newDocumentSize = value.getDocuments().size();
                                            sharedPreferences.edit().putInt("TheTotalNumberOfChats", newDocumentSize).apply();


                                    }

                                }
                            });
                        }

                        jobFinished(params,true);
                    }
                }
        );

    }

    private void buildNotification(String title, String contentText, int chanDesc) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = "Hello" + chanDesc;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("liftandpayId", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.shouldVibrate();
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), UploadedRidesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "liftandpayId")
                .setSmallIcon(R.drawable.img_circle)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(chanDesc, builder.build());

    }
}
