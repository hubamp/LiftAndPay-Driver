package com.example.liftandpay_driver.fastClass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class NewMessageNotificationWorker extends Worker {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();
    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("BACKGROUNDFILE", MODE_PRIVATE);
    private int numberOfBookedPassengers, totalNumberOfBookedPassengers;


    public NewMessageNotificationWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {


       db.collection("Chat").document(mUid).collection("Passengers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

               for (DocumentSnapshot docs : queryDocumentSnapshots.getDocuments())
               {
                   db.collection("Chat").document(mUid).collection("Passengers").document(docs.getId())
                           .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                               @Override
                               public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {


                                   if(Objects.requireNonNull(value.get("ChatMode")).toString().equals("2"))
                                   {
                                       Log.e("New Message", value.getString("Message"));
                                       buildNotification(value.getId(), value.getString("Message"), 202);

                                   }
                               }
                           });

               }


                   }
               }
       );

        return Result.success();

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
