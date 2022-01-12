package com.example.liftandpay_driver.uploadRide;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.search.SearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadRideActivity extends AppCompatActivity {
    private View footerView;
    private LinearLayout footerLayout;
    private static final int REQUEST_CODE_AUTOCOMPLETE_STLOCATION = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_ENDLOCATION = 2;

    private DirectionsRoute route;


    //Declaration of progressbars
    private ProgressBar progressBar;
    private ProgressBar dateProgressBar;
    private ProgressBar timeProgressBar;
    private ProgressBar startProgressBar;
    private ProgressBar endProgressBar;
    private ProgressBar costProgressBar;
    private ProgressBar distanceProgressBar;

    private ImageButton proceedBtn;

    //Declaration of TextViews
    private TextView viewMapBtn;
    private TextView startLocation;
    private TextView endingLocation;
    private TextView distance;
    private TextView cost;
    private TextView date;
    private TextView time;
    private TextInputEditText numberOfOccuppants;

    //private TextInputEditText numberOfOccuppants;
    private Date times, dates;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String theDriverId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    private Map<String, Object> ride;

    private String lastNumber;

    //ImageView
    private ImageView backBtn;

    String[] startInfo = new String[3];
    String[] endInfo = new String[3];

    private Point pointOne;
    private Point pointTwo;

    private double sLat, sLong, eLat, eLong;

    private static String myName,myPlate;


    private FusedLocationProviderClient fusedLocationProviderClient;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(UploadRideActivity.this, getString(R.string.mapbox_access_token));//This comes before oncreate

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_ride);
        footerLayout = findViewById(R.id.footerView);
        footerView = getLayoutInflater().inflate(R.layout.footer_view, footerLayout, false);
        footerLayout.addView(footerView);

        //ProgressBar initialisation
        progressBar = findViewById(R.id.btnProgress);
        dateProgressBar = findViewById(R.id.dateProgress);
        timeProgressBar = findViewById(R.id.timeProgress);
        proceedBtn = findViewById(R.id.btn_proceed_id);
        startProgressBar = findViewById(R.id.stProgressId);
        endProgressBar = findViewById(R.id.endProgressId);
        viewMapBtn = findViewById(R.id.viewMapId);
        costProgressBar = findViewById(R.id.costProgress);
        distanceProgressBar = findViewById(R.id.distanceProgress);

        final Calendar calendar = Calendar.getInstance();

        // initialising the date variables
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);

        db.collection("Driver").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                myName = task.getResult().getString("Name");
                myPlate = task.getResult().getString("Numberplate");
            }
        });


        //SharedPreferences initialisation
        sharedPreferences = this.getSharedPreferences("FILENAME", MODE_PRIVATE);

        backBtn = findViewById(R.id.btn_backward_id);

        backBtn.setOnClickListener(view -> {
                    finish();
                }
        );

        endInfo[0] = "";
        endInfo[1] = "";
        endInfo[2] = "";

        startInfo[0] = "";
        startInfo[1] = "";
        startInfo[2] = "";

        //TextView initialisation
        startLocation = findViewById(R.id.startingLocationId);
        endingLocation = findViewById(R.id.endingLocationId);
        distance = findViewById(R.id.distanceUploadId);
        cost = findViewById(R.id.costId);
        date = findViewById(R.id.dateText);
        time = findViewById(R.id.timeText);
        numberOfOccuppants = findViewById(R.id.numberOfOccupantsId);

        Places.initialize(getApplicationContext(), "AIzaSyAnvGY2L3NUvvuMMrg1wbYK3x74Zo8NQQA");

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        proceedBtn.setAnimation(animation);

        //Searching Location
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UploadRideActivity.this, SearchActivity.class);
                startActivityIfNeeded(i, REQUEST_CODE_AUTOCOMPLETE_STLOCATION);
            }
        });

        endingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UploadRideActivity.this, SearchActivity.class);
                startActivityIfNeeded(i, REQUEST_CODE_AUTOCOMPLETE_ENDLOCATION);
            }
        });


        ride = new HashMap<>();
        String startLocationText = sharedPreferences.getString("TheRideStartingLocation", "No place selected");
        String endingLocationText = sharedPreferences.getString("TheRideEndingLocation", "No place selected");
        String distanceText = sharedPreferences.getString("TheRideDistance", "");
        String costText = sharedPreferences.getString("TheRideCost", "");
        String dateText = sharedPreferences.getString("TheRideDate", "Not Selected");
        String timeText = sharedPreferences.getString("TheRideTime", "Not Selected");
//      int occupantsNumber =  sharedPreferences.getInt("TheRideNumberOfOccupants",0);


        if (
                !startLocationText.equals("No place selected") ||
                        !endingLocationText.equals("No place selected") ||
                        !distanceText.isEmpty() && !costText.isEmpty() ||
                        !dateText.equals("Not Selected") ||
                        !timeText.equals("Not Selected")) {

            startLocation.setText(startLocationText);
            endingLocation.setText(endingLocationText);
            distance.setText(distanceText);
            cost.setText(costText);
            date.setText(dateText);
            time.setText(timeText);
//          numberOfOccuppants.setText((CharSequence) numberOfOccuppants);
        }

        MaterialDatePicker.Builder<Long> dateBulder = MaterialDatePicker.Builder.datePicker();


//        MaterialDatePicker<Long> materialDatePicker = dateBulder.build();
        dateBulder.setTitleText("Set your ride date");

        date.setOnClickListener(v -> {

           DatePickerDialog datePicker = new DatePickerDialog(UploadRideActivity.this);

           dateProgressBar.setVisibility(View.INVISIBLE);


            datePicker = new DatePickerDialog(UploadRideActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                    // adding the selected date in the edittext

                    SimpleDateFormat d =new SimpleDateFormat("dd-MM-yyyy");

                    try {
                        String datee = dayOfMonth + "-" + (month+1) + "-" + year;
                        dates = d.parse(datee);
                        SimpleDateFormat chosenDate = new SimpleDateFormat("EEE, d MMM yyyy");
                        date.setText(chosenDate.format(dates));
                        dateProgressBar.setVisibility(View.INVISIBLE);
                        sharedPreferences.edit().putString("TheRideDate", Objects.requireNonNull(date.getText()).toString()).apply();

                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
            }, year, month, day);

            sharedPreferences.edit().putString("TheRideDate", Objects.requireNonNull(date.getText()).toString()).apply();

            datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

            datePicker.show();
            dateProgressBar.setVisibility(View.VISIBLE);
//            materialDatePicker.show(getSupportFragmentManager(), "Date Picker");
        });

        time.setOnClickListener(v -> {
            timeProgressBar.setVisibility(View.VISIBLE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth
                    , new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hour, int minute) {
                    DecimalFormat formatter = new DecimalFormat("00");
                    String timeString = hour + ":" + formatter.format(minute);
                    SimpleDateFormat f24hours = new SimpleDateFormat("HH:mm");

                    try {
                        times = f24hours.parse(timeString);
                        SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm:aa");
                        time.setText(f12Hours.format(times));
                        sharedPreferences.edit().putString("TheRideTime", Objects.requireNonNull(time.getText()).toString()).apply();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, 12, 0, false);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.show();
            timeProgressBar.setVisibility(View.INVISIBLE);
        });


       /* materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {


                date.setText(materialDatePicker.getHeaderText());
                dateProgressBar.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putString("TheRideDate", Objects.requireNonNull(date.getText()).toString()).apply();
            }
        });

        materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateProgressBar.setVisibility(View.INVISIBLE);

            }
        });*/


        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                proceedBtn.setVisibility(View.INVISIBLE);


                String phoneNumber = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();

                ride = new HashMap<>();

                if (
                        startLocation.getText().toString().equals(null)
                                || endingLocation.getText().toString().equals(null)
                                || distanceText.equals(null)
                                || cost.getText().toString().equals(null)
                                || date.getText().toString().equals(null)
                                || time.getText().toString().equals(null)
                                || (phoneNumber != null ? phoneNumber.equals(null) : false)
                                || mAuth.getUid().equals(null)
                ) {
                    Toast.makeText(UploadRideActivity.this, "he", Toast.LENGTH_SHORT).show();
                } else {
                    ride.put("startLocation", startLocation.getText().toString());
                    ride.put("endLocation", endingLocation.getText().toString());
                    ride.put("rideDistance", distanceText);
                    ride.put("rideCost", cost.getText().toString());
                    ride.put("rideDate", date.getText().toString());
                    ride.put("rideTime", time.getText().toString());
                    ride.put("startLon", sLong);
                    ride.put("startLat", sLat);
                    ride.put("endLon", eLong);
                    ride.put("endLat", eLat);
                    ride.put("phone number", phoneNumber);
                    ride.put("driverName", myName);
                    ride.put("plate", myPlate);
                    ride.put("myId", mAuth.getUid());
                    ride.put("driversStatus","Pending");
                    ride.put("Number Of Occupants", Integer.parseInt(numberOfOccuppants.getText().toString()));


                    db.collection("Driver").document(theDriverId).collection("Rides").document("Pending").get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                                    if (!task.getResult().exists()) {
                                        ArrayList<String> availableRideIds = new ArrayList<>();
                                        Map<String, Object> data = new HashMap<>();
                                        availableRideIds.add(theDriverId + " " + 0);

                                        data.put("LastNumber", "0");
                                        data.put("AvailableRideIds", availableRideIds);
                                        task.getResult().getReference().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task001) {
                                                Log.e("Task002", "Completed");

                                                if (task001.isSuccessful()) {
                                                    Log.e("Task002", "Successful");


                                                    db.collection("Rides").document(theDriverId + " " + 0).set(ride).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task003) {
                                                            if (task003.isSuccessful()) {
                                                                Snackbar.make(UploadRideActivity.this, time, "Uploaded successfully", 5000)
                                                                        .setTextColor(Color.WHITE)
                                                                        .setBackgroundTint(getResources().getColor(R.color.success)).show();

                                                                openDialog();
                                                            }
                                                        }
                                                    });


                                                }
                                            }
                                        });
                                    }
                                    else if (task.isSuccessful())
                                    {
                                        Log.e("Task001", "Successful");

                                        lastNumber = (task.getResult().getString("LastNumber"));
                                        lastNumber = String.valueOf(Integer.parseInt(lastNumber) + 1);


                                        task.getResult().getReference().update("LastNumber", lastNumber);
                                        ArrayList<String> availableRideIds = new ArrayList<>((Collection<? extends String>) task.getResult().get("AvailableRideIds"));
                                        availableRideIds.add(theDriverId + " " + lastNumber);

                                        task.getResult().getReference().update("AvailableRideIds", availableRideIds).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task002) {
                                                Log.e("Task002", "Completed");

                                                if (task002.isSuccessful()) {
                                                    Log.e("Task002", "Successful");

                                                    String size = String.valueOf(Integer.valueOf((task.getResult().getString("LastNumber"))) + 1);
                                                    db.collection("Rides").document(theDriverId + " " + size).set(ride).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task003) {
                                                            if (task003.isSuccessful()) {
                                                                Snackbar.make(UploadRideActivity.this, time, "Uploaded successfully", 5000)
                                                                        .setTextColor(Color.WHITE)
                                                                        .setBackgroundTint(getResources().getColor(R.color.success)).show();
                                                                openDialog();
                                                            }
                                                        }
                                                    });


                                                }

                                            }
                                        });


                                    }


                                }
                            }).
                            addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadRideActivity.this, "Could not upload. Restart app and Try again", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadRideActivity.this);
        builder.setMessage("You will be alerted as soon as a passenger requests to join.")
                .setPositiveButton("Thank you!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UploadRideActivity.this.finish();
                    }
                });
        builder.show();
    }

    //Activities after Requesting A location from search
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //request from the starting Location
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_STLOCATION) {

            Log.e("onActivity001", "started");


            Log.e("onActivity001", "Result is ok");


            if (data != null) {

                Log.e("onActivity001", "Data not null");

                if (data.hasExtra(new SearchActivity().theNameID))
                    Log.e("onActivity001", "has Name");
                if (data.hasExtra(new SearchActivity().theLatID))
                    Log.e("onActivity001", "has Latitude");
                if (data.hasExtra(new SearchActivity().theLonID))
                    Log.e("onActivity001", "has Longitude");
                String datas = data.getExtras().getString("theLocationName") + " " + data.getExtras().getDouble("theLat", 0.0) + " " + data.getExtras().getDouble("theLon", 0.0);
                Log.e("onActivity001-Data", datas);

                startLocation.setText(data.getExtras().getString("theLocationName"));

                Log.e("Result", data.getExtras().getString(new SearchActivity().theNameID));
                startInfo[0] = data.getExtras().getString("theLocationName");
                startInfo[1] = String.valueOf(data.getExtras().getDouble("theLon", 0.0));
                startInfo[2] = String.valueOf(data.getExtras().getDouble("theLat", 0.0));

                Log.i("onActivity001", "Lon :"+endInfo[1]);
                Log.i("onActivity001", "Lat :"+endInfo[2]);


            } else {
                Log.e("onActivity001", "doesn't contain");

            }

            viewOnMap();

        }


        //Request from the destination location
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_ENDLOCATION) {
            Log.e("onActivity002", "started");

            Log.e("onActivity002", "Result is ok");

            if (data != null) {

                Log.e("onActivity002", "Data not null");

                if (data.hasExtra(new SearchActivity().theNameID))
                    Log.e("onActivity002", "has Name");
                if (data.hasExtra(new SearchActivity().theLatID))
                    Log.e("onActivity002", "has Latitude");
                if (data.hasExtra(new SearchActivity().theLonID))
                    Log.e("onActivity002", "has Longitude");
                String datas = data.getExtras().getString("theLocationName") + " " + data.getExtras().getDouble("theLat", 0.0) + " " + data.getExtras().getDouble("theLon", 0.0);
                Log.e("onActivity002-Data", datas);

                endingLocation.setText(data.getExtras().getString("theLocationName"));

                endInfo[0] = data.getExtras().getString("theLocationName");
                endInfo[1] = String.valueOf(data.getExtras().getDouble("theLon", 0.0));
                endInfo[2] = String.valueOf(data.getExtras().getDouble("theLat", 0.0));

                Log.i("onActivity002", "Lon :"+endInfo[1]);
                Log.i("onActivity002", "Lat :"+endInfo[2]);


            } else {
                Log.e("onActivity002", "doesn't contain");

            }

            viewOnMap();
        }

        checkConvert();
    }


    public void viewOnMap() {
        viewMapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(UploadRideActivity.this, startInfo[1], Toast.LENGTH_LONG).show();

                if (
                        !endInfo[0].isEmpty() &&
                                !endInfo[1].isEmpty() &&
                                !endInfo[2].isEmpty() &&
                                !startInfo[0].isEmpty() &&
                                !startInfo[1].isEmpty() &&
                                !startInfo[2].isEmpty()
                ) {
                    Intent intent = new Intent(UploadRideActivity.this, ViewMapActivity.class);
                    intent.putExtra("StartingLatitude", startInfo[2]);
                    intent.putExtra("StartingLongitude", startInfo[1]);
                    intent.putExtra("StartingName", startLocation.getText().toString());
                    intent.putExtra("StoppingLatitude", endInfo[2]);
                    intent.putExtra("StoppingLongitude", endInfo[1]);
                    intent.putExtra("StoppingName", endingLocation.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }

    //
    private void checkConvert() {

        if
        (
                !endInfo[0].isEmpty() &&
                        !endInfo[1].isEmpty() &&
                        !endInfo[2].isEmpty() &&
                        !startInfo[0].isEmpty() &&
                        !startInfo[1].isEmpty() &&
                        !startInfo[2].isEmpty()
        ) {

            Log.e("Conversion", "All ride details are available");
            sLat = Double.parseDouble(startInfo[2]);
            sLong = Double.parseDouble(startInfo[1]);
            eLat = Double.parseDouble(endInfo[2]);
            eLong = Double.parseDouble(endInfo[1]);

            pointOne = Point.fromLngLat(sLong, sLat);
            pointTwo = Point.fromLngLat(eLong, eLat);

            setRouteDistance(pointOne, pointTwo);
        }
        else  Log.e("Conversion", "Some ride details are not available");

    }

    //Distance and cost calculations
    private void setRouteDistance(Point origin, Point destination) {
        distanceProgressBar.setVisibility(View.VISIBLE);
        costProgressBar.setVisibility(View.VISIBLE);
        distance.setVisibility(View.GONE);
        cost.setVisibility(View.GONE);

        NavigationRoute.builder(UploadRideActivity.this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d("TAG", "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e("Routing","No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e("Routing","No routes found");
                            return;
                        }

                        route = response.body().routes().get(0);
                        Toast.makeText(UploadRideActivity.this, "received", Toast.LENGTH_LONG).show();
                        double routeKilo = route.distance() / 1000;
                        routeKilo = truncate(routeKilo, 3);
                        double routeMoney = routeKilo * 0.4;
                        routeMoney = truncate(routeMoney, 2);

                        String distanceKilo = String.valueOf(routeKilo) + "km";
                        String costPerPassenger = "GHC" + String.valueOf(routeMoney) + "/passenger";

                        distanceProgressBar.setVisibility(View.GONE);
                        costProgressBar.setVisibility(View.GONE);
                        distance.setVisibility(View.VISIBLE);
                        cost.setVisibility(View.VISIBLE);
                        distance.setText(distanceKilo);
                        cost.setText(costPerPassenger);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                        Log.e("Routing",t.toString());
                        distanceProgressBar.setVisibility(View.GONE);
                        costProgressBar.setVisibility(View.GONE);
                        distance.setVisibility(View.VISIBLE);
                        cost.setVisibility(View.VISIBLE);

                    }
                });
    }

    public double truncate(double originalValue, int numberOfDecimalPlaces) {
        if (numberOfDecimalPlaces == 3) {
            originalValue = Math.round(originalValue * 1000.0) / 1000.0;
        } else if (numberOfDecimalPlaces == 2)
            originalValue = Math.round(originalValue * 100.0) / 100.0;
        return originalValue;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().remove("TheDriverLatitude").apply();
        sharedPreferences.edit().remove("TheDriverLongitude").apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
