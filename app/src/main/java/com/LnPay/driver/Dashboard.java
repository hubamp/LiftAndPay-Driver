package com.LnPay.driver;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
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

import com.LnPay.driver.API.paystack.paystack;
import com.LnPay.driver.accounts.Payment;
import com.LnPay.driver.fastClass.BroadcastNewBooking;
import com.LnPay.driver.fastClass.BroadcastNewMessage;
import com.LnPay.liftandpay_api.LnPayAPI_Interface;
import com.LnPay.liftandpay_api.driver;
import com.LnPay.liftandpay_api.driverInterface;
import com.LnPay.liftandpay_api.driverModel;
import com.LnPay.liftandpay_api.myObserver;
import com.LnPay.driver.History.RideHistoryList;
import com.LnPay.driver.SignUp.PhoneAuthenticationActivity;

import com.LnPay.driver.accounts.AccountActivity;
import com.LnPay.driver.chats.ChatList;
import com.LnPay.driver.fastClass.BookedNotificationWorker;
import com.LnPay.driver.fastClass.CheckForSignUpWorker;
import com.LnPay.driver.fastClass.NewChatNotificationWorker;
import com.LnPay.driver.menu.MenuListActivity;
import com.LnPay.driver.menu.ProfileActivity;
import com.LnPay.driver.uploadRide.UploadRideActivity;
import com.LnPay.driver.uploadedRide.RequestedPassenger.RequestedPassengersSheet;
import com.LnPay.driver.uploadedRide.UploadedRideMap;
import com.LnPay.driver.uploadedRide.UploadedRidesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    private TextView deleteBtn, shareBtn;

    private TextView journeyName, dateTime, distanceCost, no_Seats, no_Requests;

    private ListenerRegistration lastRideListener;

    private LinearLayout rideHistoryBtn, profileBtn, messageBtn, accountsBtn;
    private LinearLayout requestedPassengerLayout;
    private Button checkRideBtn;
    private SharedPreferences sharedPreferences, activeRidesShared;
    private ImageView menuBtn;


    driver thisDriver;


    public void scheduledJob() {
        ComponentName componentName = new ComponentName(this, BroadcastNewBooking.class);
        JobInfo info = new JobInfo.Builder(200, componentName)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.i("BaguvixMain", "scheduledJob: Successful");
        } else {
            Log.i("BaguvixMain", "scheduledJob: Unsuccessful");
        }
    }

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
        shareBtn = findViewById(R.id.shareBtn);

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

        /*Library Testing Implementation starts here*/

  /*   Thread paymentThread =   new Thread(new Runnable() {
            @Override
            public void run() {

       *//*    Log.i("Payment auth Dashboard",s.getAuthUrl()   );
                Log.e("payment URL RETRIEVED", s.getAuthUrl());
                Intent i = new Intent(Dashboard.this, Payment.class);
                i.putExtra(
                        "authUrl",
                        s.getAuthUrl());
                startActivity(i);*//*

                paystack response = new paystack(Dashboard.this);
                response.checkPendingCharge("yv1nqknhx9",null);
                Log.i("Charge Looper"," Responded");
            }
        });

     paymentThread.start();*/
/*



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LnPayAPI_Interface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LnPayAPI_Interface driverInterface = retrofit.create(LnPayAPI_Interface.class);



        driverInterface.getDrivers().enqueue(new Callback<driverModel>()
             {
                 @Override
                 public void onResponse(Call<driverModel> call, Response<driverModel> response)
                 {
                     driver di = new driver();
                     di.setId("1");


                     ArrayList<driver> drivers = response.body().getDrivers();

                     for (int i=0; i < drivers.size(); i++ )
                     {
                         if (drivers.get(i).getId().equals("1"))
                         {
                             Log.i("driverInterfaceLog",drivers.get(i).getPhone_number()+" Answer");
                         }
                     }
                     Log.i("driverInterfaceLog","Received Here");
                     Log.i("driverInterfaceLog",response.body().getDrivers().indexOf(di)+"yeah");

                 }

                 @Override
                 public void onFailure(Call<driverModel> call, Throwable t)
                 {
                     Log.i("driverInterfaceLog",t.getMessage());

                 }
             });

//        Log.i("driverInterfaceLog",thisDriver.getPhone_number());
*/
        /*Library Testing Implementation starts here*/


        /**/
        scheduledJob();
        /**/

        startNotificationWorker(Dashboard.this);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MenuListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);


                /* Intent intent = new Intent(Dashboard.this, PhoneAuthenticationActivity.class);
                startActivity(intent);
                mAuth.signOut();
                finish();*/

            }
        });


       /* imageMenu.setOnClickListener(view ->{
            Intent intent = new Intent(Dashboard.this, PhoneAuthenticationActivity.class);
            startActivity(intent);
            mAuth.signOut();
            finish();
        });*/


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
                Toast.makeText(Dashboard.this, "Coming soon", Toast.LENGTH_LONG).show();
//                startActivity(intent);
            }
        });

        accountsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, AccountActivity.class);
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
                            Log.i("The Ride Id", lastAvailableRideId);


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

                                        if (value002.getString("driversStatus") != null) {
                                            Log.i("driversStatus","Not null");

                                            if (!value002.getString("driversStatus").equals("Completed")) {

                                              if(!value002.getString("driversStatus").equals("Cancelled"))  {
                                                    Log.i("driversStatus", "is neither cancelled nor completed");

                                                    String cost = value002.get("currency") + (String.valueOf(value002.get("rideCost"))) + "/passenger";
                                                    String dateTimes = value002.getString("rideDate") + " " + value002.getString("rideTime");
                                                    String journey = value002.getString("startLocation") + "\n" + "to" + "\n" + value002.getString("endLocation");
                                                    String distance = value002.getString("rideDistance");
                                                    String driverName = value002.getString("driverName");
                                                    double endLon = value002.getDouble("endLon");
                                                    double endLat = value002.getDouble("endLat");
                                                    double stLat = value002.getDouble("startLat");
                                                    double stLon = value002.getDouble("startLon");


                                                    shareBtn.setOnClickListener(View -> {

                                                        SharingLinkAlert shareDialog = new SharingLinkAlert(Dashboard.this);

                                                        shareDialog.setPurpose("bkng")
                                                                .setTheDriverId(mUid)
                                                                .setStartLat(stLat)
                                                                .setEndLat(endLat)
                                                                .setStartLon(stLon)
                                                                .setEndLon(endLon)
                                                                .setTheRideId(lastAvailableRideId)
                                                                .setStartTime(dateTimes)
                                                                .setJourney(journey.replaceAll("\n", " "))
                                                                .setDistance(distance)
                                                                .setDriverName(driverName)
                                                                .build();

                                                    });


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
                                                                    Toast.makeText(Dashboard.this, "No request yet", Toast.LENGTH_SHORT).show();
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
                                                                    activeRidesShared = getSharedPreferences("ACTIVE_RIDEFILE", MODE_PRIVATE);
                                                                    activeRidesShared.edit().putString("TheEndLat", String.valueOf(endLat)).apply();
                                                                    activeRidesShared.edit().putString("TheEndLon", String.valueOf(endLon)).apply();
                                                                    activeRidesShared.edit().putString("TheStLat", String.valueOf(stLat)).apply();
                                                                    activeRidesShared.edit().putString("TheStLon", String.valueOf(stLon)).apply();
                                                                    activeRidesShared.edit().putString("TheRideId", lastAvailableRideId).apply();

                                                                    requestedPassengersSheet.show(manager, "null");
                                                                });
                                                            }
                                                        }

                                                    });
                                                }
                                              else {
                                                  Log.i("driversStatus","is either cancelled or completed");

                                                  parentLayout.setVisibility(View.VISIBLE);
                                                  noRideLayout.setVisibility(View.VISIBLE);
                                                  rideAvailableLayout.setVisibility(View.INVISIBLE);
                                                  pendingRideText.setText("Pending Ride");
                                              }
                                            }
                                            else {
                                                Log.i("driversStatus","is either cancelled or completed");

                                                parentLayout.setVisibility(View.VISIBLE);
                                                noRideLayout.setVisibility(View.VISIBLE);
                                                rideAvailableLayout.setVisibility(View.INVISIBLE);
                                                pendingRideText.setText("Pending Ride");
                                            }
                                        }
                                        else {
                                            Log.i("driversStatus","null");

                                        }
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
        /*OneTimeWorkRequest bookedNotificationWorker = new OneTimeWorkRequest.Builder(BookedNotificationWorker.class).build();
        OneTimeWorkRequest chatNotificationWorker = new OneTimeWorkRequest.Builder(NewChatNotificationWorker.class).build();

        WorkManager.getInstance(context).enqueue(bookedNotificationWorker);
        WorkManager.getInstance(context).enqueue(chatNotificationWorker);*/
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


    @Override
    protected void onStop() {
        super.onStop();

        startNotificationWorker(Dashboard.this);

        HashMap<String, Object> data = new HashMap<>();
        data.put("LastSeen", new Timestamp(new Date()));
        db.collection("Driver")
                .document(mAuth.getUid()).update(data);
    }


}