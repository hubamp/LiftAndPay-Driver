package com.example.liftandpay_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UploadRideActivity extends AppCompatActivity {
    private View footerView;
    private LinearLayout footerLayout;
    private ProgressBar progressBar;
    private ProgressBar dateProgressBar;
    private ProgressBar timeProgressBar;
    private ImageButton proceedBtn;
    private TextInputEditText startLocation;
    private TextInputEditText endingLocation;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ride);
        footerLayout = findViewById(R.id.footerView);
        footerView = getLayoutInflater().inflate(R.layout.footer_view, footerLayout,false);
        footerLayout.addView(footerView);
        progressBar = findViewById(R.id.btnProgress);
        dateProgressBar = findViewById(R.id.dateProgress);
        timeProgressBar = findViewById(R.id.timeProgress);
        proceedBtn = findViewById(R.id.btn_proceed_id);

        sharedPreferences = this.getSharedPreferences("FILENAME",MODE_PRIVATE);

        startLocation = findViewById(R.id.startingLocationId);
        endingLocation = findViewById(R.id.endingLocationId);
        distance = findViewById(R.id.distanceId);
        cost = findViewById(R.id.costId);
        date = findViewById(R.id.dateText);
        time = findViewById(R.id.timeText);
//        numberOfOccuppants = findViewById(R.id.numberOfOccupantsId);

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        proceedBtn.setAnimation(animation);


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
                String path = ""+ dateText + timeText;


// Add a new document with a generated ID
                db.collection("Driver").document(theDriverId).collection("Pending Rides").document(path).set(ride)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
}
