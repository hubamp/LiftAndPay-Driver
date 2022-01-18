package com.example.liftandpay_driver.fastClass;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.liftandpay_driver.uploadedRide.UploadedRideMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.HashMap;
import java.util.Map;

public class UpdatedDriverLocationWorker extends Worker {

    private int SECONDS = 7000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String rideIdTag = "ride_id_desc";
    public Map<String, Object> locationHashMap = new HashMap();
    private FusedLocationProviderClient fusedLocationClient;


    public UpdatedDriverLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String rideDesc = getInputData().getString(rideIdTag);
                updateLoc(rideDesc);
                handler.postDelayed(this, SECONDS);
            }
        }, SECONDS);


        return Result.success();
    }


    private void updateLoc(String theRideId) {
        Log.i("Updating Task", "Running");
        Log.i("UpdatedLoc - rideDesc001", theRideId);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {

                return  null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                locationHashMap.put("driversLat", task.getResult().getLatitude());
                locationHashMap.put("driversLon",task.getResult().getLongitude());
                locationHashMap.put("driversBearing", task.getResult().getBearing());


                Log.i("UpdatedLoc - hashMapBearing001", "" + locationHashMap.get("driversBearing"));
                Log.i("UpdatedLoc - hashMapLat001", "" + locationHashMap.get("driversLat"));
                Log.i("UpdatedLoc - hashMapLon001", "" + locationHashMap.get("driversLon"));
                Log.i("UpdatedLoc - hashMaptheRideID", "" + theRideId);


                db.collection("Rides").document(theRideId.trim()).set(locationHashMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.i("Updating Task", "Completed");

                        if (task.isSuccessful()) {
                            Log.i("Updating Task", "Successful");

                        } else {
                            Log.i("Updating Task", "Unsuccessful");

                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Updating Task", "Failed");
                                Log.i("Updating Task", e.getMessage());

                            }
                        });
            }
        });





    }

}
