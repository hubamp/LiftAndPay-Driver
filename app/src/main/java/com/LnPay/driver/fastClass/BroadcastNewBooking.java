package com.LnPay.driver.fastClass;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class BroadcastNewBooking extends JobService {

    FirebaseFirestore db =FirebaseFirestore.getInstance();
    String mUid = FirebaseAuth.getInstance().getUid();
    private boolean jobCancelled;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroundWork(jobParameters);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("BookedStat", "onStopJob: Pre-Cancelled");
        jobCancelled = true;
        return true;
    }


    public void doBackgroundWork(JobParameters params) {
        db.collection("Driver").document(mUid).collection("Rides").document("Pending").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                assert documentSnapshot != null;
                if (!documentSnapshot.exists()) {

                    Log.e("bookingAvailability", "Does not exist");
                } else {

                    if (documentSnapshot.contains("AvailableRideIds")) {

                        Log.e("bookingAvailability", "Contains the ride");

                        ArrayList<String> availableRideIds = new ArrayList<>((Collection<? extends String>) documentSnapshot.get("AvailableRideIds"));
                        if (availableRideIds.isEmpty()) {
                            Log.e("bookingAvailability", "Exists but empty");

                        } else {

                            Log.e("bookingAvailability", "Exists with item");

                           String lastAvailableRideId = availableRideIds.get(availableRideIds.size() - 1).trim();
                            Log.i("The Ride Id", lastAvailableRideId);

                           db.collection("Rides").document(lastAvailableRideId).collection("Booked By").addSnapshotListener(new EventListener<QuerySnapshot>() {
                               @Override
                               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                   if (jobCancelled){
                                       return;
                                   }

                                   assert value != null;
                                   if (!value.isEmpty())
                                   {

                                       for ( DocumentChange documentChange : value.getDocumentChanges()) {

                                           if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                                               Log.e("Booking Status", "Has Changed");


                                                  if (!Objects.equals(documentChange.getDocument().get("NotifyFlag"), "1")) {

                                                     /* HashMap<String,String> map = new HashMap<String, String>();
                                                      map.put("NotifyFlag","");*/

                                                      buildNotification(documentChange.getDocument().getString("Name") + " booked your ride",
                                                              "Pick me at " + documentChange.getDocument().getString("Location Desc"),
                                                              value.size());
                                                      documentChange.getDocument().getReference().update("NotifyFlag","1");
                                                  }

                                           }
                                       }
                                   }

                               }
                           });


                        }
                    }
                }
            }
        });

        jobFinished(params, true);
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
