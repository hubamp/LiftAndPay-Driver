package com.example.liftandpay_driver.uploadRide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.fastClass.CurrentLocationClass;
import com.example.liftandpay_driver.proceedAlert;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.lang.String.format;

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

//  private TextInputEditText numberOfOccuppants;
    private Date dates;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String theDriverId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    private Map<String, Object> ride;

    String[] startInfo = new String[3];
    String[] endInfo = new String[3];
    private LatLng locationOne;
    private LatLng locationTwo;
    private Point pointOne;
    private Point pointTwo;


    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(UploadRideActivity.this, getString(R.string.mapbox_access_token));//This comes before oncreate

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ride);
        footerLayout = findViewById(R.id.footerView);
        footerView = getLayoutInflater().inflate(R.layout.footer_view, footerLayout,false);
        footerLayout.addView(footerView);

        //ProgressBar initialisation
        progressBar = findViewById(R.id.btnProgress);
        dateProgressBar = findViewById(R.id.dateProgress);
        timeProgressBar = findViewById(R.id.timeProgress);
        proceedBtn = findViewById(R.id.btn_proceed_id);
        startProgressBar = findViewById(R.id.stProgressId);
        endProgressBar =findViewById(R.id.endProgressId);
        viewMapBtn= findViewById(R.id.viewMapId);
        costProgressBar = findViewById(R.id.costProgress);
        distanceProgressBar = findViewById(R.id.distanceProgress);

        //SharedPreferences initialisation
        sharedPreferences = this.getSharedPreferences("FILENAME",MODE_PRIVATE);


        endInfo[0] ="";
        endInfo[1] ="";
        endInfo[2] ="";

        startInfo[0] ="";
        startInfo[1] ="";
        startInfo[2] ="";


        //TextView initialisation
        startLocation = findViewById(R.id.startingLocationId);
        endingLocation = findViewById(R.id.endingLocationId);
        distance = findViewById(R.id.distanceUploadId);
        cost = findViewById(R.id.costId);
        date = findViewById(R.id.dateText);
        time = findViewById(R.id.timeText);
        //numberOfOccuppants = findViewById(R.id.numberOfOccupantsId);

        Places.initialize(getApplicationContext(), "AIzaSyAnvGY2L3NUvvuMMrg1wbYK3x74Zo8NQQA");

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        proceedBtn.setAnimation(animation);

        //Searching Location
       CurrentLocationClass currentLocationClass = new CurrentLocationClass();

       startLocation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               currentLocationClass.popSearchBasedOnCurrentLocation(UploadRideActivity.this,startProgressBar, 1);
           }
       });

       endingLocation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               currentLocationClass.popSearchBasedOnCurrentLocation(UploadRideActivity.this,endProgressBar,2);
           }
       });



      ride = new HashMap<>();
      String startLocationText =  sharedPreferences.getString("TheRideStartingLocation","No place selected");
      String endingLocationText = sharedPreferences.getString("TheRideEndingLocation","No place selected");
      String distanceText = sharedPreferences.getString("TheRideDistance","");
      String costText =   sharedPreferences.getString("TheRideCost","");
      String dateText =  sharedPreferences.getString("TheRideDate","Not Selected");
      String timeText =  sharedPreferences.getString("TheRideTime","Not Selected");
//      int occupantsNumber =  sharedPreferences.getInt("TheRideNumberOfOccupants",1);


      if(
              !startLocationText.equals("No place selected") ||
              !endingLocationText.equals("No place selected") ||
              !distanceText.isEmpty() && !costText.isEmpty() ||
              !dateText.equals("Not Selected") ||
              !timeText.equals("Not Selected"))
      {

          startLocation.setText(startLocationText);
          endingLocation.setText(endingLocationText);
          distance.setText(distanceText);
          cost.setText(costText);
          date.setText(dateText);
          time.setText(timeText);
//          numberOfOccuppants.setText((CharSequence) numberOfOccuppants);
      }


        MaterialDatePicker.Builder<Long> dateBulder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker<Long> materialDatePicker = dateBulder.build();
        dateBulder.setTitleText("Set your ride date");

        date.setOnClickListener(v -> {
            dateProgressBar.setVisibility(View.VISIBLE);
            materialDatePicker.show(getSupportFragmentManager(), "Date Picker");
        });

        time.setOnClickListener(v -> {
            timeProgressBar.setVisibility(View.VISIBLE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth
                    ,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hour  , int minute) {
                    DecimalFormat formatter = new DecimalFormat("00");
                    String  timeString = hour + ":" + formatter.format(minute);
                    SimpleDateFormat f24hours = new SimpleDateFormat("HH:mm");

                    try {
                        dates = f24hours.parse(timeString);
                        SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm:aa");
                        time.setText(f12Hours.format(dates));
                        sharedPreferences.edit().putString("TheRideTime", Objects.requireNonNull(time.getText()).toString()).apply();

                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, 12, 0, false);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.show();
            timeProgressBar.setVisibility(View.INVISIBLE);
        });


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection)
            {
                date.setText( materialDatePicker.getHeaderText());
                sharedPreferences.edit().putString("TheRideDate", Objects.requireNonNull(date.getText()).toString()).apply();
            }
        });




        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                proceedBtn.setVisibility(View.INVISIBLE);

                sharedPreferences.edit().putString("TheRideStartingLocation", Objects.requireNonNull(startLocation.getText()).toString()).apply();
                sharedPreferences.edit().putString("TheRideEndingLocation", Objects.requireNonNull(endingLocation.getText()).toString()).apply();
                sharedPreferences.edit().putString("TheRideDistance", Objects.requireNonNull(distance.getText()).toString()).apply();
                sharedPreferences.edit().putString("TheRideCost", Objects.requireNonNull(cost.getText()).toString()).apply();
                sharedPreferences.edit().putString("TheRideDate", Objects.requireNonNull(date.getText()).toString()).apply();
                sharedPreferences.edit().putString("TheRideTime", Objects.requireNonNull(time.getText()).toString()).apply();

                ride = new HashMap<>();

                ride.put("startLocation", startLocation.getText().toString());
                ride.put("endLocation" , endingLocation.getText().toString());
                ride.put("Ride Distance" , distanceText);
                ride.put("Ride Cost" , cost.getText().toString());
                ride.put("Ride Date" , date.getText().toString());
                ride.put("Ride Time" , time.getText().toString());
                ride.put("startLon" , locationOne.getLongitude());
                ride.put("startLat" , locationOne.getLatitude());
                ride.put("endLon" , locationTwo.getLongitude());
                ride.put("endLat" , locationTwo.getLatitude());

                CollectionReference pendingRidesDb = FirebaseFirestore.getInstance().collection("Driver").document(theDriverId).collection("Pending Rides");


                db.collection("Driver").document(theDriverId).collection("Pending Rides").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                String size = ""+ task.getResult().size();

                        db.collection("Driver").document(theDriverId).collection("Pending Rides").document(theDriverId+" "+ size).set(ride)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                                   task.getResult().size();
                                                   db.collection("Rides").document(theDriverId + " "+size).set(ride);
                                                }
                                            });

                                        Toast.makeText(UploadRideActivity.this, "Uploaded successfully",Toast.LENGTH_LONG).show();
                                        openDialog();
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadRideActivity.this, "Could not upload. Restart app and Try again",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    public void openDialog() {
        proceedAlert proceedAlert = new proceedAlert();
        proceedAlert.show(getSupportFragmentManager(), "example dialog");
    }

//Activities after Requesting A location from search
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

        //request from the starting Location
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_STLOCATION) {
            startInfo[0] =selectedCarmenFeature.text();
            endInfo[0] =   endingLocation.getText().toString();

            //Compare the two locations if they are equal.
            if(startInfo[0].equals(endInfo[0])) Toast.makeText(this,"Starting Point Cannot the same as ending point", Toast.LENGTH_LONG).show();
            else {
                startLocation.setText(selectedCarmenFeature.text());
                startInfo[1] = String.valueOf(new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                                ((Point) selectedCarmenFeature.geometry()).longitude()).getLongitude());
                startInfo[2] = String.valueOf(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).longitude()).getLatitude());

                viewOnMap();

            }

        }

        //Request from the destination location
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_ENDLOCATION) {
            endInfo[0] = selectedCarmenFeature.text();
            startInfo[0]= startLocation.getText().toString();

            //Compare the two locations if they are equal.
            if(endInfo[0].equals(startInfo[0])) Toast.makeText(this,"Starting Point Cannot the same as ending point", Toast.LENGTH_LONG).show();
            else {
                endingLocation.setText(selectedCarmenFeature.text());
                endInfo[1] = String.valueOf(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).longitude()).getLongitude());
                endInfo[2] = String.valueOf(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).latitude()).getLatitude());

                viewOnMap();
            }


        }
        checkConvert();
    }

    public void viewOnMap(){
        viewMapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(UploadRideActivity.this,startInfo[1],Toast.LENGTH_LONG).show();

                if (
                        !endInfo[0].isEmpty() &&
                                !endInfo[1].isEmpty() &&
                                !endInfo[2].isEmpty() &&
                                !startInfo[0].isEmpty() &&
                                !startInfo[1].isEmpty() &&
                                !startInfo[2].isEmpty()
                )
                {
                    Intent intent = new Intent(UploadRideActivity.this, ViewMapActivity.class);
                    intent.putExtra("StartingLatitude",startInfo[2] );
                    intent.putExtra("StartingLongitude",startInfo[1]);
                    intent.putExtra("StartingName",startLocation.getText().toString());
                    intent.putExtra("StoppingLatitude", endInfo[2]);
                    intent.putExtra("StoppingLongitude", endInfo[1]);
                    intent.putExtra("StoppingName",endingLocation.getText().toString());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(UploadRideActivity.this,"Lacking Location Info, Cannot open map",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //
    private void checkConvert(){

        if (
                !endInfo[0].isEmpty() &&
                        !endInfo[1].isEmpty() &&
                        !endInfo[2].isEmpty() &&
                        !startInfo[0].isEmpty() &&
                        !startInfo[1].isEmpty() &&
                        !startInfo[2].isEmpty()
        ) {
            double sLat = Double.parseDouble(startInfo[2]);
            double sLong = Double.parseDouble(startInfo[1]);
            double eLat = Double.parseDouble(endInfo[2]);
            double eLong = Double.parseDouble(endInfo[1]);

            locationOne = new LatLng(sLat, sLong);
            locationTwo = new LatLng(eLat, eLong);
            pointOne = Point.fromLngLat(locationOne.getLongitude(), locationOne.getLatitude());
            pointTwo = Point.fromLngLat(locationTwo.getLongitude(), locationTwo.getLatitude());

            setRouteDistance(pointOne,pointTwo);
        }
        else
        {
            Toast.makeText(UploadRideActivity.this,"Location not retrieved, Search again.",Toast.LENGTH_LONG).show();
        }
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
                            Timber.e("No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Timber.e("No routes found");
                            return;
                        }

                        route = response.body().routes().get(0);
                        Toast.makeText(UploadRideActivity.this,"received",Toast.LENGTH_LONG).show();
                        double routeKilo = route.distance() / 1000;
                        routeKilo = truncate(routeKilo,3);
                        double routeMoney = routeKilo * 0.4;
                        routeMoney = truncate(routeMoney,2);

                        String distanceKilo = String.valueOf(routeKilo) + "km";
                        String costPerPassenger = "GHC" +String.valueOf(routeMoney) +"/passenger";


                        distanceProgressBar.setVisibility(View.GONE);
                        costProgressBar.setVisibility(View.GONE);
                        distance.setVisibility(View.VISIBLE);
                        cost.setVisibility(View.VISIBLE);
                        distance.setText(distanceKilo);
                        cost.setText(costPerPassenger);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Timber.e(t.toString());
                        distanceProgressBar.setVisibility(View.GONE);
                        costProgressBar.setVisibility(View.GONE);
                        distance.setVisibility(View.VISIBLE);
                        cost.setVisibility(View.VISIBLE);

                    }
                });
    }

    public double truncate(double originalValue,int numnerOfDecimalPlaces) {
        if (numnerOfDecimalPlaces == 3) {
            originalValue = Math.round(originalValue * 1000.0) / 1000.0;
        } else if (numnerOfDecimalPlaces == 2)
            originalValue =  Math.round(originalValue * 100.0) / 100.0;
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
