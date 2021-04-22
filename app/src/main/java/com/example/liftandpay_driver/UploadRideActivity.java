package com.example.liftandpay_driver;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liftandpay_driver.fastClass.CurrentLocationClass;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class UploadRideActivity extends AppCompatActivity {
    private View footerView;
    private LinearLayout footerLayout;
    private static final int REQUEST_CODE_AUTOCOMPLETE_STLOCATION = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_ENDLOCATION = 2;


    //Declaration of progressbars
    private ProgressBar progressBar;
    private ProgressBar dateProgressBar;
    private ProgressBar timeProgressBar;
    private ProgressBar startProgressBar;
    private ProgressBar endProgressBar;

    private ImageButton proceedBtn;

    //Declaration of TextViews
    private TextView viewMapBtn;
    private TextView startLocation;
    private TextView endingLocation;
    private TextView distance;
    private TextView cost;
    private TextView date;
    private TextView time;

//    private TextInputEditText numberOfOccuppants;
    private Date dates;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String theDriverId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    private Map<String, Object> ride;

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



        //SharedPreferences initialisation
        sharedPreferences = this.getSharedPreferences("FILENAME",MODE_PRIVATE);


        startLocation = findViewById(R.id.startingLocationId);
        endingLocation = findViewById(R.id.endingLocationId);
        distance = findViewById(R.id.distanceId);
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



    private void saveToShared()
    {
        String startLocationText =  sharedPreferences.getString("TheRideStartingLocation","No place selected");
        String endingLocationText = sharedPreferences.getString("TheRideEndingLocation","No place selected");
        String distanceText = sharedPreferences.getString("TheRideDistance","");
        String costText =   sharedPreferences.getString("TheRideCost","");
        String dateText =  sharedPreferences.getString("TheRideDate","Not Selected");
        String timeText =  sharedPreferences.getString("TheRideTime","Not Selected");
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String startInfo[] = new String[5];
        double startInfo1;

        String endInfo[] = new String[5];
        CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_STLOCATION) {
            startInfo[0] =selectedCarmenFeature.text();
            endInfo[0] =   endingLocation.getText().toString();

            //Compare the two locations if they are equal.
            if(startInfo[0].equals(endInfo[0])) Toast.makeText(this,"Starting Point Cannot the same as ending point", Toast.LENGTH_LONG).show();
            else {
                startLocation.setText(selectedCarmenFeature.text());
                startInfo[1] = Objects.requireNonNull(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                ((Point) selectedCarmenFeature.geometry()).longitude()).toString());
                startInfo[2] =  Objects.requireNonNull(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).latitude()).toString());

                Toast.makeText(this, Objects.requireNonNull(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).longitude()).toString()), Toast.LENGTH_LONG).show();

            }

        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE_ENDLOCATION) {
            endInfo[0] = selectedCarmenFeature.text();
            startInfo[0]= startLocation.getText().toString();

            //Compare the two locations if they are equal.
            if(endInfo[0].equals(startInfo[0])) Toast.makeText(this,"Starting Point Cannot the same as ending point", Toast.LENGTH_LONG).show();
            else {
                endingLocation.setText(selectedCarmenFeature.text());
                endInfo[1] = Objects.requireNonNull(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).longitude()).toString());
                endInfo[2] =  Objects.requireNonNull(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).latitude()).toString());

            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().remove("TheDriverLatitude").apply();
        sharedPreferences.edit().remove("TheDriverLongitude").apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().remove("TheDriverLatitude").apply();
        sharedPreferences.edit().remove("TheDriverLongitude").apply();
    }
}
