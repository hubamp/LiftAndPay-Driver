package com.example.liftandpay_driver.uploadedRide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.services.android.navigation.ui.v5.camera.NavigationCamera;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;


import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class UploadedRideMap extends FragmentActivity implements OnMapReadyCallback, PermissionsListener {

    private TextView startBtn;
    private ProgressBar startedProgessBar;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    private final String symbolIconId = "symbolIconId";
    private final String mapBoxStyleUrl = "mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";

    private Map<String, Object> driversLoc = new HashMap<>();
    ImageView deleteBtn;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
    private PermissionsManager permissionsManager;
    private SharedPreferences activeRide_sharedPreference;
    private String rideId;

    private DirectionsRoute currentRoute;
    private MapboxNavigation mapboxNavigation;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(UploadedRideMap.this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_uploaded_ride_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        startBtn = findViewById(R.id.startBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        startedProgessBar = findViewById(R.id.startedProgress);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(UploadedRideMap.this);

    }



    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        activeRide_sharedPreference = getSharedPreferences("ACTIVE_RIDEFILE", MODE_PRIVATE);

        //from UploadedRideAdapter
        rideId = activeRide_sharedPreference.getString("TheRideId", null);

        this.mapboxMap = mapboxMap;
        locationComponent = mapboxMap.getLocationComponent();


        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        UploadedRideMap.this.getResources(), R.drawable.mapbox_logo_icon));
                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);
                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);
                addDestinationIconSymbolLayer(style);

                enableLocationComponent(style);




                deleteBtn.setOnClickListener(view -> {
                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(UploadedRideMap.this);

                    // Show the delete message
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
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                });


            }
        });


        double stLat = Double.parseDouble(activeRide_sharedPreference.getString("TheStLat", "0"));
        double stLon = Double.parseDouble(activeRide_sharedPreference.getString("TheStLon", "0"));
        double endLat = Double.parseDouble(activeRide_sharedPreference.getString("TheEndLat", "0"));
        double endLon = Double.parseDouble(activeRide_sharedPreference.getString("TheEndLon", "0"));


        if (stLat != 0 && stLon != 0 && endLat != 0 && endLon != 0) {
            LatLng points = new LatLng(stLat, stLon);
            LatLng pointd = new LatLng(endLat, endLon);

            ///////////
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(points)
                    .include(pointd)
                    .build();

            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));

            Point destinationPoint = Point.fromLngLat(pointd.getLongitude(), pointd.getLatitude());
            Point originPoint = Point.fromLngLat(points.getLongitude(), points.getLatitude());
            getRoute(originPoint, destinationPoint);

        } else {
            Toast.makeText(getApplicationContext(), "The Cordinates are null, Route could not render", Toast.LENGTH_LONG).show();
        }


    }


    @SuppressLint("WrongConstant")
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter

            locationComponent.activateLocationComponent(UploadedRideMap.this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.GPS);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.tiltWhileTracking(32);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    private RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(RouteProgress routeProgress) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "R.string.user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "R.string.user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));

     /*   loadedMapStyle.addImage("passenger-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_compass_icon));
*/
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);

       /* SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("passenger-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true));

        loadedMapStyle.addLayer(destinationSymbolLayer);
*/

    }

    private void addMarkerToDestination(double lat, double lon) {
        GeoJsonSource theMainStyle = mapboxMap.getStyle().getSourceAs("destination-source-id");

        if (theMainStyle != null) {
            theMainStyle.setGeoJson(FeatureCollection.fromFeature(
                    Feature.fromGeometry(Point.fromLngLat(lon, lat))
            ));

        }
    }

    private void passengerPickUpLocMarker(double Lat, double Lon) {
        if (mapboxMap.getStyle() != null) {
            SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);


            SymbolOptions symbolOptions = new SymbolOptions()
                    .withLatLng(new LatLng(Lat, Lon))
                    .withIconImage("destination-icon-id")
                    .withIconSize(1.3f);

// Use the manager to draw the symbol.
            symbolManager.create(symbolOptions);

        }
    }

   /* private void passengerCurrentLocMarker(double Lat, double Lon) {
        if (mapboxMap.getStyle() != null) {
            SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);


            SymbolOptions symbolOptions = new SymbolOptions()
                    .withLatLng(new LatLng(Lat, Lon))
                    .withIconImage("passenger-icon-id")
                    .withIconSize(1.3f);

// Use the manager to draw the symbol.
            symbolManager.create(symbolOptions);

        }
    }*/


    private void getRoute(Point origin, Point destination) {

        NavigationRoute.Builder navigationRoute = NavigationRoute.builder(this);

        navigationRoute
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination);

        //Check the for approved passengers and plot their pickup locations
        db.collection("Rides").document(rideId).collection("Booked By").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        int i = 0;
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            i++;
                            if (Objects.equals(snapshot.getString("Status"), "Approved")) {
//                                                    Point point = Point.fromLngLat(snapshot.getDouble("Long"), snapshot.getDouble("Lat"));
                                passengerPickUpLocMarker(snapshot.getDouble("Lat"), snapshot.getDouble("Long"));

                                snapshot.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                        value.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                                if (Objects.requireNonNull(value).getDouble("pALat") != null & value.getDouble("pALat") != null)
                                                {
                                                    double pALat =value.getDouble("pALat");
                                                    double pALon = value.getDouble("pALon");
                                                    addMarkerToDestination( pALat, pALon);
                                                    Point myWayPoint = Point.fromLngLat( pALon , pALon);



                                                    navigationRoute.addWaypoint(myWayPoint);
                                                }
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Timber.e(e.toString());
            }
        });




        navigationRoute.build().getRoute(new Callback<DirectionsResponse>() {
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

                currentRoute = response.body().routes().get(0);

                currentRoute.legs().get(0);


              // Draw the route on the map


                    startBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder
                                    = new AlertDialog
                                    .Builder(UploadedRideMap.this);


                            // Set the message show for the Alert time
                            builder.setMessage("Do you want to start this ride? \nAll the passengers will be notified of your current location");
                            builder.setTitle("Start");
                            builder.setCancelable(true);
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    startBtn.setTextColor(ContextCompat.getColor(UploadedRideMap.this, R.color.success));

                                    db.collection("Rides").document(rideId).update("driversStatus", "Started").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("MissingPermission")
                                        @Override
                                        public void onSuccess(Void unused) {
                                            startBtn.setText("Journey Started");
                                            v.setClickable(false);
                                            v.setFocusable(false);
                                            v.setBackground(ContextCompat.getDrawable(UploadedRideMap.this, R.color.transparentColor));



                                            mapboxNavigation.startTripSession();

//                                            updateDriversLocation();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    Timber.e(e);
                                                }
                                            });

                                }
                            });
                            builder.create().show();

                        }
                    });




            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Timber.e(t.toString());

            }
        });


    }


    private class MainActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<UploadedRideMap> activityWeakReference;

        MainActivityLocationCallback(UploadedRideMap activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            UploadedRideMap activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }


                driversLoc.put("driversLat", result.getLastLocation().getLatitude());
                driversLoc.put("driversLon", result.getLastLocation().getLongitude());

// Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            UploadedRideMap activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateDriversLocation() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationEngineRequest request = new LocationEngineRequest.Builder(5000)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(10000).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }


    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mapboxNavigation.registerLocationObserver(locationObserver);

    }

    @Override
    MapboxNavigation mapboxNavigation = new MapboxNavigation(...);
    NavigationMapRoute navigationMapRoute = new NavigationMapRoute.Builder(...)
            .withMapboxNavigation(mapboxNavigation)
	.withVanishRouteLineEnabled(true)
.build();

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        activeRide_sharedPreference.edit().clear().apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        activeRide_sharedPreference.edit().clear().apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        activeRide_sharedPreference.edit().clear().apply();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}