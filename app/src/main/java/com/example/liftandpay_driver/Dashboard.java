package com.example.liftandpay_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.History.RideHistoryList;
import com.example.liftandpay_driver.SignUp.PhoneAuthenticationActivity;

import com.example.liftandpay_driver.accounts.Payment;
import com.example.liftandpay_driver.chats.ChatList;
import com.example.liftandpay_driver.fastClass.BookedNotificationWorker;
import com.example.liftandpay_driver.fastClass.CheckForSignUpWorker;
import com.example.liftandpay_driver.fastClass.NewChatNotificationWorker;
import com.example.liftandpay_driver.menu.MenuListActivity;
import com.example.liftandpay_driver.menu.ProfileActivity;
import com.example.liftandpay_driver.uploadRide.UploadRideActivity;
import com.example.liftandpay_driver.uploadedRide.RequestedPassenger.RequestedPassengersSheet;
import com.example.liftandpay_driver.uploadedRide.UploadedRideMap;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class Dashboard extends AppCompatActivity {

    private ImageView sendToUploadedRide;
    private RelativeLayout noRideLayout;
    private RelativeLayout rideAvailableLayout;
    private RelativeLayout parentLayout;
    private LinearLayout rideUploadLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();
    private TextView pendingRideText;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ImageView imageMenu;

    private String lastAvailableRideId;
    private String passengersStatus;
    private String rideStatus;

    private TextView deleteBtn, editBtn;

    private TextView journeyName, dateTime, distanceCost, no_Seats, no_Requests;

    private ListenerRegistration lastRideListener;

    private LinearLayout rideHistoryBtn, profileBtn, messageBtn, accountsBtn;
    private LinearLayout requestedPassengerLayout;
    private Button checkRideBtn;
    private SharedPreferences sharedPreferences, activeRidesShared;
    private ImageView menuBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sendToUploadedRide = findViewById(R.id.allUploadedRideBtn);

        parentLayout = findViewById(R.id.currentRideLayoutId);
        noRideLayout = findViewById(R.id.noRideLayoutId);
        rideAvailableLayout = findViewById(R.id.rideavailablelayout);
        rideUploadLayout = findViewById(R.id.mainRideUpload);
        pendingRideText = findViewById(R.id.pendingRideTextId);
        deleteBtn = findViewById(R.id.deleteBtn);
        editBtn = findViewById(R.id.editBtn);

        menuBtn = findViewById(R.id.imageMenu);

        journeyName = findViewById(R.id.locationNameId);
        dateTime = findViewById(R.id.dateTimeId);
        distanceCost = findViewById(R.id.distanceCostId);
        no_Seats = findViewById(R.id.numberOfOccupantsId);
        no_Requests = findViewById(R.id.numberOfRequestedPassengersId);

        rideHistoryBtn = findViewById(R.id.rideHistoryBtn);
        profileBtn = findViewById(R.id.profileBtn);
        messageBtn = findViewById(R.id.messageBtn);
        accountsBtn = findViewById(R.id.accountBtn);
        checkRideBtn = findViewById(R.id.checkRide);

        requestedPassengerLayout = findViewById(R.id.requestedPassengers);



        startNotificationWorker(Dashboard.this);

         menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MenuListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });


        /*imageMenu.setOnClickListener(view ->{
            Intent intent = new Intent(Dashboard.this, PhoneAuthenticationActivity.class);
            startActivity(intent);
            mAuth.signOut();
            finish();
        });*/

        editBtn.setOnClickListener(View->{

        Ratings ratings = new Ratings(Dashboard.this,mUid,passengersStatus,rideStatus);

        });

        rideUploadLayout.setOnClickListener(View -> {
            Intent intent = new Intent(Dashboard.this, UploadRideActivity.class);
            startActivity(intent);
        });

        rideHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // codes go here
                Intent i = new Intent(Dashboard.this, RideHistoryList.class);
                startActivity(i);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, ChatList.class);
                startActivity(intent);
            }
        });

        accountsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Payment.class);
                startActivity(intent);
            }
        });

        sendToUploadedRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, UploadedRidesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        db.collection("Driver").document(mUid).collection("Rides").document("Pending").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                assert value != null;
                if (!value.exists()) {
                    parentLayout.setVisibility(View.VISIBLE);
                    noRideLayout.setVisibility(View.VISIBLE);
                    rideAvailableLayout.setVisibility(View.INVISIBLE);
                    pendingRideText.setText("Pending Ride");
                    Log.e("available000", "Does not exist");
                } else {

                    if (value.contains("AvailableRideIds")) {

                        Log.e("available002", "Contains the ride");

                        ArrayList<String> availableRideIds = new ArrayList<>((Collection<? extends String>) value.get("AvailableRideIds"));
                        if (availableRideIds.isEmpty()) {
                            parentLayout.setVisibility(View.VISIBLE);
                            noRideLayout.setVisibility(View.VISIBLE);
                            rideAvailableLayout.setVisibility(View.INVISIBLE);
                            pendingRideText.setText("Pending Ride");
                            Log.e("available001", "Exists but empty");

                        } else {
                            parentLayout.setVisibility(View.VISIBLE);
                            noRideLayout.setVisibility(View.INVISIBLE);
                            rideAvailableLayout.setVisibility(View.VISIBLE);
                            pendingRideText.setText("Pending Ride");
                            Log.e("available002", "Exists with item");

                            lastAvailableRideId = availableRideIds.get(availableRideIds.size() - 1).trim();
                            Log.i("The Ride Id",lastAvailableRideId);


                            //Setting up the deleteBtn
                            deleteBtn.setOnClickListener(view -> {
                                AlertDialog.Builder builder
                                        = new AlertDialog
                                        .Builder(Dashboard.this);

                                // Set the message show for the Alert time
                                builder.setMessage("Do you want to cancel this ride?");
                                builder.setTitle("Cancel");
                                builder.setCancelable(true);
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancelTheRide(lastAvailableRideId);
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            });

                            //Listen to last ride from ride
                            lastRideListener = db.collection("Rides").document(lastAvailableRideId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value002, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                                    Log.e("Last Ride", value002.getReference().getPath());


                                    if (value002.exists()) {
                                        Log.e("lastRide", "Ride exists");
                                        String cost = value002.get("currency")+(String.valueOf(value002.get("rideCost")))+ "/passenger";
                                        String dateTimes = value002.getString("rideDate") + " " + value002.getString("rideTime");
                                        String journey = value002.getString("startLocation") + "\n" + "to" + "\n" + value002.getString("endLocation");
                                        double endLon = value002.getDouble("endLon");
                                        double endLat = value002.getDouble("endLat");
                                        double stLat = value002.getDouble("startLat");
                                        double stLon = value002.getDouble("startLon");


                                        distanceCost.setText(cost);
                                        dateTime.setText(dateTimes);
                                        journeyName.setText(journey);

                                        //View check map
                                        checkRideBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(Dashboard.this, UploadedRideMap.class);

                                                sharedPreferences = getSharedPreferences("ACTIVE_RIDEFILE", MODE_PRIVATE);
                                                sharedPreferences.edit().putString("TheEndLat", "" + endLat).apply();
                                                sharedPreferences.edit().putString("TheEndLon", "" + endLon).apply();
                                                sharedPreferences.edit().putString("TheStLat", "" + stLat).apply();
                                                sharedPreferences.edit().putString("TheStLon", "" + stLon).apply();
                                                sharedPreferences.edit().putString("TheRideId", "" + lastAvailableRideId).apply();


                                                startActivity(intent);
                                            }
                                        });


                                        //Listen to requested passengers
                                        value002.getReference().collection("Booked By").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value003, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                                assert value003 != null;
                                                if (value003.isEmpty()) {
                                                    Log.e("Request", "Empty");
                                                    no_Requests.setText("0");

                                                    requestedPassengerLayout.setOnClickListener(view -> {
                                                        Toast.makeText(Dashboard.this, "No request yet",Toast.LENGTH_SHORT).show();
                                                    });
                                                } else {
                                                    Log.e("Request", "Not Empty");
                                                    no_Requests.setText("" + value003.size());

                                                    for (DocumentChange changes : value003.getDocumentChanges()) {
                                                        if (changes.getType() == DocumentChange.Type.MODIFIED) {
                                                            no_Requests.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Dashboard.this, R.color.success)));
                                                        }
                                                    }

                                                    //Setting up click function for the Requested Passengers
                                                    requestedPassengerLayout.setOnClickListener(view -> {
                                                        RequestedPassengersSheet requestedPassengersSheet = new RequestedPassengersSheet();
                                                        FragmentManager manager = getSupportFragmentManager();

                                                        requestedPassengersSheet.setNumberOfRequests(Integer.parseInt(no_Requests.getText().toString()));
                                                        requestedPassengersSheet.setTheRequestedId(lastAvailableRideId);
                                                        activeRidesShared = getSharedPreferences("ACTIVE_RIDEFILE",MODE_PRIVATE);
                                                        activeRidesShared.edit().putString("TheEndLat", String.valueOf(endLat)).apply();
                                                        activeRidesShared.edit().putString("TheEndLon", String.valueOf(endLon)).apply();
                                                        activeRidesShared.edit().putString("TheStLat", String.valueOf(stLat)).apply();
                                                        activeRidesShared.edit().putString("TheStLon", String.valueOf(stLon)).apply();
                                                        activeRidesShared.edit().putString("TheRideId",lastAvailableRideId).apply();

                                                        requestedPassengersSheet.show(manager, "null");
                                                    });
                                                }
                                            }

                                        });


                                    } else {
                                        Log.e("lastRide", "does not exists");
                                    }

                                }
                            });

                        }
                    } else {
                        Log.e("available002", "does not contains the ride");

                    }

                }
            }
        });


    }



    private void cancelTheRide(String theRideId) {

        db.collection("Driver").document(mUid).collection("Rides").document("Pending").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                Log.e("Delete Ride", "Request Completed");
                if (task.isSuccessful()) {
                    Log.e("Delete Ride", "Request Successful");

                    DocumentReference rideIdRef = db.collection("Rides").document(theRideId);

                    if (task.getResult().contains("AvailableRideIds")) {

                        rideIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task001) {

                                if (task001.isSuccessful()) {
                                    /*ArrayList<String> availableRideIds = new ArrayList<>((Collection<? extends String>) task.getResult().get("AvailableRideIds"));
                                    availableRideIds.remove(theRideId);
                                    task.getResult().getReference().update("AvailableRideIds", availableRideIds);
*/
                                   /* HashMap<String,Object> update = new HashMap<>();
                                    update.put("driversStatus","Cancelled");
                                    db.collection("Rides").document(theRideId).update(update);
*/
                                    WorkManager.getInstance(Dashboard.this).cancelUniqueWork("UpdateMyLocId").getResult().isCancelled();

                                    Log.e("Delete Ride", "Deleted Successfully");
                                }
                            }
                        });

                    }
                }
            }
        });
    }


    static void startNotificationWorker(Context context) {
        OneTimeWorkRequest bookedNotificationWorker = new OneTimeWorkRequest.Builder(BookedNotificationWorker.class).build();
        OneTimeWorkRequest chatNotificationWorker = new OneTimeWorkRequest.Builder(NewChatNotificationWorker.class).build();

        WorkManager.getInstance(context).enqueue(bookedNotificationWorker);
        WorkManager.getInstance(context).enqueue(chatNotificationWorker);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(Dashboard.this, PhoneAuthenticationActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        OneTimeWorkRequest checkForSignUpWorker = new OneTimeWorkRequest.Builder(CheckForSignUpWorker.class).build();
        WorkManager.getInstance(Dashboard.this).enqueue(checkForSignUpWorker);
    }
}