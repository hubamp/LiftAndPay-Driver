package com.example.liftandpay_driver.fastClass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.liftandpay_driver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class CurrentLocationClass {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;
    private Point loc;
    private View view;

    public CurrentLocationClass(){

    }


/*
    public static void popSearchBasedOnCurrentLocation(Context context){


               FusedLocationProviderClient fusedLocationProviderClient = getFusedLocationProviderClient(context);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else
                {
                    Toast.makeText(context,"Location Error",Toast.LENGTH_LONG).show();
                }

                fusedLocationProviderClient.getLastLocation()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Failed to get Location",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                            public void onSuccess(Location location) {
                               Point loc = Point.fromLngLat(location.getLongitude(),location.getLatitude());
                                Toast.makeText(context,loc.toString(),Toast.LENGTH_SHORT).show();

                                Activity activity = (Activity) context;
                                Intent intent = new PlaceAutocomplete.IntentBuilder()
                                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() :context.getString(R.string.mapbox_access_token))
                                        .placeOptions(PlaceOptions.builder()
                                                .backgroundColor(Color.parseColor("#BDC3FA"))
                                                .proximity(loc)
                                                .limit(10)
                                                .country("ISO 3166-2")
                                                .geocodingTypes()
                                                .build(PlaceOptions.MODE_FULLSCREEN))
                                        .build(activity);
                                activity.startActivityForResult(intent, -1);
                            }

                        });

            }

   public void popSearchBasedOnCurrentLocation(Context context1, ProgressBar progressBar, int requestCode){

        this.context = context1;
        progressBar.setVisibility(View.VISIBLE);

        fusedLocationProviderClient = getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        else
        {
            Toast.makeText(context1, "Location not enabled",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }



        fusedLocationProviderClient.getLastLocation()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed to get Location",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    public void onSuccess(Location location) {
                        double offsetNumber = 0.2;
                        loc = Point.fromLngLat(location.getLongitude(),location.getLatitude());
                        Point southWest = Point.fromLngLat(location.getLongitude()-offsetNumber,location.getLatitude()-offsetNumber);
                        Point northEast = Point.fromLngLat(location.getLongitude()+offsetNumber,location.getLatitude()+offsetNumber);
//                        Toast.makeText(context,""+loc,Toast.LENGTH_LONG).show();

                        Activity activity = (Activity) context;
                        Intent intent = new PlaceAutocomplete.IntentBuilder()
                                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() :context.getString(R.string.mapbox_access_token))
                                .placeOptions(PlaceOptions.builder()
                                        .backgroundColor(Color.parseColor("#BDC3FA"))
//                                        .proximity(loc)
                                        .limit(5)
                                        .bbox(southWest,northEast)
                                        .geocodingTypes(com.mapbox.api.geocoding.v5.GeocodingCriteria.TYPE_POI)
                                        .build(PlaceOptions.MODE_FULLSCREEN))
                                .build(activity);
                        activity.startActivityForResult(intent,requestCode);
                        progressBar.setVisibility(View.INVISIBLE);

                    }

                });
    }*/


}
