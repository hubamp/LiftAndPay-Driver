package com.LnPay.driver.fastClass;

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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.LnPay.driver.R;
import com.LnPay.driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BookedNotificationWorker extends Worker {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();
    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("BACKGROUNDFILE", MODE_PRIVATE);
    private int numberOfBookedPassengers, totalNumberOfBookedPassengers;
    private static List<DocumentChange> docChange;


    public BookedNotificationWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        db.collection("Rides").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                Log.i("New Book", "Book Notification Started");

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges(MetadataChanges.EXCLUDE)) {
                    String theID = new StringFunction(doc.getDocument().getId()).splitStringWithAndGet(" ", 0);

                    if (theID.equals(mUid)) {
                        db.collection("Rides").document(doc.getDocument().getId()).collection("Booked By")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                    @Override
                                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                        totalNumberOfBookedPassengers = totalNumberOfBookedPassengers + value.size();


                                        docChange = value.getDocumentChanges();
                                        Log.e("Previous : Current001", "" + sharedPreferences.getInt("TheTotalNumberOfPassengers", -1) + " : " + totalNumberOfBookedPassengers);

                                    }
                                });

                        db.collection("Rides").document(doc.getDocument().getId()).collection("Booked By")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                    @Override
                                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                                        if (sharedPreferences.getInt("TheTotalNumberOfPassengers", -1) < totalNumberOfBookedPassengers) {
                                            for (DocumentChange documentChange : docChange) {

                                                if (documentChange.getType()!= DocumentChange.Type.MODIFIED) {
                                                    Log.e("Previous : Current", "" + sharedPreferences.getInt("TheTotalNumberOfPassengers", -1) + " : " + totalNumberOfBookedPassengers);
                                                    buildNotification(documentChange.getDocument().getString("Name") + " booked your ride", "Pick me at " + documentChange.getDocument().getString("Location Desc"), docChange.size());
                                                }
                                            }
                                        }

                                    }
                                });


                    }
                }
            }
        });

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
