package com.example.liftandpay_driver.uploadedRide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.Ratings;
import com.example.liftandpay_driver.fastClass.UpdatedDriverLocationWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.turf.TurfMeta;
import com.mapbox.turf.TurfTransformation;


import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.example.liftandpay_driver.fastClass.DistanceCalc.distanceBtnCoordinates;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;


public class UploadedRideMap extends FragmentActivity implements OnMapReadyCallback, PermissionsListener {

    private TextView startBtn;
    private ProgressBar startedProgessBar;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private Handler handler = new Handler();
    private int LOCATION_UPDATE_TIME_INTERVAL_IN_SECONDS = 7000;

    private String passengersStatus;
    private String rideStatus;
    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    private final String TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID = "TURF_CALCULATION_FILL_LAYER_GEOJSON_SOURCE_ID";
    private final String symbolIconId = "symbolIconId";
    private final String mapBoxStyleUrl = "mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";

    private Map<String, Object> driversLoc = new HashMap<>();
    ImageView cancelBtn;

    private TextToSpeech textToSpeech;
    private Vibrator vibrator;

    private LatLngBounds.Builder latLngBounds;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
    private PermissionsManager permissionsManager;
    private SharedPreferences activeRide_sharedPreference;
    private String rideId;

    Map driverLocMap = new HashMap<>();
    private List<DirectionsRoute> currentRoute = new ArrayList<>();
    private MapboxNavigation mapboxNavigation;

    private NavigationMapRoute navigationMapRoute;
    private NavigationRoute.Builder navigationRoute;
    private Point myWayPoint;
    private List<Point> allWayPoints = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);

    private static Point originPoint;
    private Point destinationPoint;

    private TextView distanceToPoint;

    private int theDriverRangeInMeters;

    // Not static final because they will be adjusted by the seekbars and spinner menu
    private final String circleUnit = UNIT_KILOMETERS;
    private final int circleSteps = 180;
    private final double circleRadius = 0.1;

    private static int nearByReportId = 0;
    private OneTimeWorkRequest updateDriverLocRequest;
    private Data data;

    private List<BookedBy> bookedBIES = new ArrayList<>();
    private List<BookedBy> newBookesBIES = new ArrayList<>();
    private List<Double> rawDistance = new ArrayList<>();
    private List<Double> newRawDistance = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(UploadedRideMap.this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_uploaded_ride_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        startBtn = findViewById(R.id.startBtn);
        cancelBtn = findViewById(R.id.cancelRideBtn);
        startedProgessBar = findViewById(R.id.startedProgress);
        distanceToPoint = findViewById(R.id.distanceToWayPoint);

        locationEngine = LocationEngineProvider.getBestLocationEngine(UploadedRideMap.this);
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
        mapboxNavigation = new MapboxNavigation(UploadedRideMap.this, Mapbox.getAccessToken());


        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);

                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        UploadedRideMap.this.getResources(), R.drawable.mapbox_logo_icon));
                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);
                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);
                addDestinationIconSymbolLayer(style);
                lineLayer(style);


                locationComponent = mapboxMap.getLocationComponent();


                // enableLocationComponent(style);


                //    originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(), locationComponent.getLastKnownLocation().getLatitude());

                cancelBtn.setOnClickListener(view -> {
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


                            db.collection("Rides").document(rideId).update("driversStatus", "Cancelled").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onSuccess(Void unused) {
                                    rideStatus = "cancelled";
                                    Ratings ratings = new Ratings(UploadedRideMap.this,bookedBIES.get(0).getPassengersId(),passengersStatus,rideStatus);

                                    switchStartBtn(toCANCELLED);
                                    Log.i("LastLocation", originPoint.latitude() + " : " + originPoint.longitude());

                                    mapboxNavigation.stopNavigation();
                                    Toast.makeText(UploadedRideMap.this, "Navigation Cancelled", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    textToSpeech.speak("Ride is cancelled", TextToSpeech.QUEUE_FLUSH, null);
                                    WorkManager.getInstance(UploadedRideMap.this).cancelUniqueWork("UpdateMyLocId").getResult().isCancelled();

                                }
                            });

                        }
                    });
                    builder.create().show();
                });


            }
        });

        initPolygonCircleFillLayer();

        double stLat = Double.parseDouble(activeRide_sharedPreference.getString("TheStLat", "0"));
        double stLon = Double.parseDouble(activeRide_sharedPreference.getString("TheStLon", "0"));
        double endLat = Double.parseDouble(activeRide_sharedPreference.getString("TheEndLat", "0"));
        double endLon = Double.parseDouble(activeRide_sharedPreference.getString("TheEndLon", "0"));


        if (stLat != 0 && stLon != 0 && endLat != 0 && endLon != 0) {
            LatLng points = new LatLng(stLat, stLon);
            LatLng pointd = new LatLng(endLat, endLon);


            ///////////
            latLngBounds = new LatLngBounds.Builder()
                    .include(points)
                    .include(pointd);


            destinationPoint = Point.fromLngLat(pointd.getLongitude(), pointd.getLatitude());
            originPoint = Point.fromLngLat(points.getLongitude(), points.getLatitude());
            Log.i("DestinationLocation", destinationPoint.latitude() + " : " + destinationPoint.longitude());
            //  Log.i("OriginLocation2",originPoint.toString());


            //Check for approved passengers and plot their pickup locations
            db.collection("Rides").document(rideId).collection("Booked By").get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {


                            //plot the the destination marker
                            setDestinationPoint(destinationPoint.latitude(), destinationPoint.longitude());


                            //fetch all passenger who have booked and have been approved
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                                if (Objects.equals(snapshot.getString("Status"), "Approved")) {

                                    passengersStatus = "Approved";

//                                    Point point = Point.fromLngLat(snapshot.getDouble("Long"), snapshot.getDouble("Lat"));
                                    Point point = passengerPickUpLocMarker(snapshot.getDouble("Lat"), snapshot.getDouble("Long"));

                                    double distanceFromOrigin = distanceBtnCoordinates(originPoint.latitude(), originPoint.longitude(),
                                            point.latitude(), point.longitude());

                                    rawDistance.add(distanceFromOrigin);
                                    bookedBIES.add(new BookedBy(
                                            snapshot.getId(),
                                            point,
                                            snapshot.getString("Location Desc"),
                                            distanceFromOrigin
                                    ));


                                    myWayPoint = Point.fromLngLat(point.longitude(), point.latitude());
                                    allWayPoints.add(myWayPoint);
                                    latLngBounds.include(new LatLng(point.latitude(), point.longitude()));
                                    drawPolygonCircle(myWayPoint);


                                    Log.e("Way Point", myWayPoint.toString());

                                }


                            }

                            //sort according to distance algorithm implementation
                            bookedBIES = new BookedBy().rearrangeBookedBies(
                                    bookedBIES,
                                    newBookesBIES,
                                    rawDistance,
                                    newRawDistance
                            );

                            //Update the driver's location
                            updateDriversLocation();

                            //route from trip origin to trip destination
                            getRoute(originPoint, destinationPoint);
                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 250));

                            //Actions when start button is clicked
                            startBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (startBtn.getText().toString().equals("Confirm Passenger Pickup")) {

                                        AlertDialog.Builder builder
                                                = new AlertDialog
                                                .Builder(UploadedRideMap.this);

                                        // Set the message show for the Alert time
                                        builder.setMessage("Confirm that you have picked the passenger");
                                        builder.setTitle("Confirmation");
                                        builder.setCancelable(true);
                                        builder.setCancelable(true);
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        textToSpeech.speak("Continue ride", TextToSpeech.QUEUE_FLUSH, null);

                                                        removeWayPoint(0);

                                                        switchStartBtn(toSTARTED);
                                                        getRoute(originPoint, destinationPoint);

                                                    }
                                                });

                                        builder.show();

                                    } else {

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

                                                enableLocationComponent(mapboxMap.getStyle());
                                                textToSpeech.speak("Ride has started", TextToSpeech.QUEUE_FLUSH, null);


                                                getRoute(originPoint, destinationPoint);

                                                //  updateDriversLocation();

                                                db.collection("Rides").document(rideId).update("driversStatus", "Started").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @SuppressLint("MissingPermission")
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        rideStatus = "Started";
                                                        switchStartBtn(toSTARTED);
                                                        Log.i("LastLocation", originPoint.latitude() + " : " + originPoint.longitude());

                                                        mapboxNavigation.startNavigation(currentRoute.get(0));

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
                                }
                            });


                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Timber.e(e.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                }
            });


        } else {
            Toast.makeText(getApplicationContext(), "The Cordinates are null, Route could not render", Toast.LENGTH_LONG).show();
        }


        mapboxNavigation.addProgressChangeListener(new ProgressChangeListener() {
            @Override
            public void onProgressChange(Location location, RouteProgress routeProgress) {

                theDriverRangeInMeters = 100;

                for (Point thisPoint : allWayPoints) {
                    double distanceFromWayPoint = distanceBtnCoordinates(location.getLatitude(), location.getLongitude(), thisPoint.latitude(), thisPoint.longitude());

                    String distanceToPnt = (distanceFromWayPoint * 1000) + "m";
                    distanceToPoint.setText(distanceToPnt);

                    Log.i("DistanceBetween", "" + distanceFromWayPoint * 1000 + " meters");
                    if ((distanceFromWayPoint * 1000) < 100 && (distanceFromWayPoint * 1000) > 20) {
                        theDriverRangeInMeters = 100;
                        switchStartBtn(toCONFIRM_PICKUP);

                        nearByReportId++;
                        if (nearByReportId == 1) {
                            textToSpeech.speak("You are almost at the pickup area", TextToSpeech.QUEUE_FLUSH, null, "");

                            //Alert passenger that driver is almost around.
                            db.collection("Rides").document(rideId).collection("Booked By").document(bookedBIES.get(0).passengersId)
                                    .update("Status", "Driver almost there");

                            passengersStatus = "Driver almost there";
                        }


                    } else if ((distanceFromWayPoint * 1000) <= 20) {
                        theDriverRangeInMeters = 20;
                        switchStartBtn(toCONFIRM_PICKUP);

                        nearByReportId++;
                        vibrator.vibrate(4000);
                        if (nearByReportId == 2) {
                            textToSpeech.speak("You are at the pickup point", TextToSpeech.QUEUE_FLUSH, null);

                            //Alert passenger that driver is at the pickup point.

                        }

                    } else {
                        theDriverRangeInMeters = 500;
                        switchStartBtn(toONGOING);
                        nearByReportId = 0;
                    }
                }



/*
                routeProgress.remainingWaypoints();
                routeProgress.currentState();
                routeProgress.inTunnel();
                routeProgress.directionsRoute().distance();
                routeProgress.directionsRoute();*/
            }
        });

        mapboxNavigation.addOffRouteListener(new OffRouteListener() {
            @Override
            public void userOffRoute(Location location) {

                Snackbar.make(mapView, "Set route engine", 5000).show();

                originPoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());

                getRoute(originPoint, destinationPoint);
                Log.i("LastLocation", originPoint.latitude() + " : " + originPoint.longitude());

                mapboxNavigation.startNavigation(currentRoute.get(0));

            }
        });

    }

    private void removeWayPoint(int i) {
        allWayPoints.remove(i);
        bookedBIES.remove(i);
    }


    @SuppressLint("WrongConstant")
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter

            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(UploadedRideMap.this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.GPS);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS);
            locationComponent.tiltWhileTracking(50);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }


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


    private void lineLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new LineLayer("LINE_LAYER_ID", geojsonSourceLayerId).withProperties(lineColor(1100)));
    }


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("waypoint-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));

        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_light));

        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);


    }

    /**
     * Add a {@link FillLayer} to display a {@link Polygon} in a the shape of a circle.
     */
    private void initPolygonCircleFillLayer() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
// Create and style a FillLayer based on information that will come from the Turf calculation
                FillLayer fillLayer = new FillLayer("FILL_LAYER_ID",
                        geojsonSourceLayerId);
                fillLayer.setProperties(
                        fillColor(ContextCompat.getColor(UploadedRideMap.this, R.color.primaryColors)),
                        fillOpacity(.7f));
                style.addLayerBelow(fillLayer, "CIRCLE_CENTER_LAYER_ID");
            }
        });
    }

    private void addMarkerToDestination(double lat, double lon) {
        GeoJsonSource theMainStyle = Objects.requireNonNull(mapboxMap.getStyle()).getSourceAs("destination-source-id");

        if (theMainStyle != null) {
            theMainStyle.setGeoJson(FeatureCollection.fromFeature(
                    Feature.fromGeometry(Point.fromLngLat(lon, lat))
            ));

        }
    }

    private void addMarkerToWayPoint(double lat, double lon) {
        GeoJsonSource theMainStyle = Objects.requireNonNull(mapboxMap.getStyle()).getSourceAs("waypoint-source-id");

        if (theMainStyle != null) {
            theMainStyle.setGeoJson(FeatureCollection.fromFeature(
                    Feature.fromGeometry(Point.fromLngLat(lon, lat))
            ));

        }
    }

    /**
     * Update the {@link FillLayer} based on the GeoJSON retrieved via
     * .
     *
     * @param circleCenter the center coordinate to be used in the Turf calculation.
     */
    private void drawPolygonCircle(Point circleCenter) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
// Use Turf to calculate the Polygon's coordinates

                Polygon polygonArea = TurfTransformation.circle(circleCenter, circleRadius, circleSteps, circleUnit);
                GeoJsonSource polygonCircleSource = style.getSourceAs(geojsonSourceLayerId);
                if (polygonCircleSource != null) {
                    polygonCircleSource.setGeoJson(Polygon.fromOuterInner(
                            LineString.fromLngLats(TurfMeta.coordAll(polygonArea, false))));
                }
            }
        });
    }

    private void setDestinationPoint(double Lat, double Lon) {
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

    private Point passengerPickUpLocMarker(double Lat, double Lon) {
        if (mapboxMap.getStyle() != null) {
            SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            SymbolOptions symbolOptions = new SymbolOptions()
                    .withLatLng(new LatLng(Lat, Lon))
                    .withIconImage("waypoint-icon-id")
                    .withIconSize(1.3f);


// Use the manager to draw the symbol.
            symbolManager.create(symbolOptions);

        }

        return Point.fromLngLat(Lon, Lat);
    }


    private void getRoute(Point origin, Point destination) {


        navigationRoute = null;
        navigationRoute = NavigationRoute.builder(UploadedRideMap.this);

        Log.i("Origin", origin.toString());
        Log.i("Destination", destination.toString());

        navigationRoute
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination);

        for (Point ways : allWayPoints) {
            navigationRoute.addWaypoint(ways);
        }

        navigationRoute.enableRefresh(true).profile(DirectionsCriteria.PROFILE_DRIVING);

        /*MapboxNavigationOptions mapboxNavigationOptions = MapboxNavigationOptions.builder()
                .enableRefreshRoute(true)
                .refreshIntervalInMilliseconds(5000)
                .build();*/


        navigationRoute.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                Log.d("TAG", "Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found ");
                    return;
                }

                currentRoute.clear();

                currentRoute.add(response.body().routes().get(0));
                if (response.body().routes().size() > 1) {
                    currentRoute.add(response.body().routes().get(1));
                }


                navigationMapRoute.updateRouteVisibilityTo(true);

                navigationMapRoute.addRoutes(currentRoute);

                navigationMapRoute.addProgressChangeListener(mapboxNavigation);

                navigationMapRoute.showAlternativeRoutes(true);
                navigationMapRoute.updateRouteVisibilityTo(true);

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Timber.e(t.toString());

            }
        });

    }

    private static class BookedBy {
        private String passengersId;
        private Point passengerPickUpPoint;
        private String passengersPickUpLocation;
        private double distanceFromOrigin;

        public BookedBy(String passengersId, Point passengerPickUpPoint, String passengersPickUpLocation, double distanceFromOrigin) {
            this.passengersId = passengersId;
            this.passengerPickUpPoint = passengerPickUpPoint;
            this.passengersPickUpLocation = passengersPickUpLocation;
            this.distanceFromOrigin = distanceFromOrigin;
        }

        public BookedBy() {
        }

        public List<BookedBy> rearrangeBookedBies(List<BookedBy> bookedBies,
                                                  List<BookedBy> newBookedBies,
                                                  List<Double> distances,
                                                  List<Double> newDistances) {
            int totalSize = distances.size();
            while (totalSize != 0) {

                int indx = distances.indexOf(Collections.max(distances));
                newDistances.add(0, distances.get(indx));
                newBookedBies.add(0, bookedBies.get(indx));

                distances.remove(indx);
                bookedBies.remove(indx);

                totalSize--;

            }

            return newBookedBies;

        }

        public String getPassengersId() {
            return passengersId;
        }

        public Point getPassengerPickUpPoint() {
            return passengerPickUpPoint;
        }

        public String getPassengersPickUpLocation() {
            return passengersPickUpLocation;
        }

        public double getDistanceFromOrigin() {
            return distanceFromOrigin;
        }
    }

    private static class MainActivityLocationCallback
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


//                driversLoc.put("driversLat", result.getLastLocation().getLatitude());
//                driversLoc.put("driversLon", result.getLastLocation().getLongitude());

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


        data = new Data.Builder()
                .putString(UpdatedDriverLocationWorker.rideIdTag, rideId)
                .build();


        updateDriverLocRequest = new OneTimeWorkRequest.
                Builder(UpdatedDriverLocationWorker.class).
                setInputData(data).
                build();

        WorkManager.getInstance(getApplicationContext()).beginUniqueWork("UpdateMyLocId", ExistingWorkPolicy.REPLACE, updateDriverLocRequest).enqueue();

    }

    String toCONFIRM_PICKUP = "Confirm Pickup";
    String toSTARTED = "Started";
    String toCANCELLED = "Cancelled";
    String toONGOING = "Journey Continues";

    /**
     * Switches among the start button statuses
     * <p>
     * toCONFIRM_PICKUP, toSTARTED, toCANCELLED ,toONGOING
     *
     * @param status One of {@link #toCONFIRM_PICKUP}, {@link #toSTARTED}, {@link #toCANCELLED} or {@link #toONGOING}.
     * @attr ref android.R.styleable#View_visibility
     */
    private void switchStartBtn(String status) {

        switch (status) {
            case "Confirm Pickup":
                startBtn.setText("Confirm Passenger Pickup");
                startBtn.setClickable(true);
                startBtn.setFocusable(true);
                startBtn.setTextColor(ContextCompat.getColor(UploadedRideMap.this, R.color.primaryColors));
                startBtn.setBackground(ContextCompat.getDrawable(UploadedRideMap.this, R.drawable.ripple));
                break;

            case "Started":
                startBtn.setText("Journey Started");
                startBtn.setClickable(false);
                startBtn.setFocusable(false);
                startBtn.setTextColor(ContextCompat.getColor(UploadedRideMap.this, R.color.success));
                startBtn.setBackground(ContextCompat.getDrawable(UploadedRideMap.this, R.color.transparentColor));
                break;

            case "Journey Continues":
                startBtn.setText("Journey continues");
                startBtn.setClickable(false);
                startBtn.setFocusable(false);
                startBtn.setTextColor(ContextCompat.getColor(UploadedRideMap.this, R.color.success));
                startBtn.setBackground(ContextCompat.getDrawable(UploadedRideMap.this, R.color.transparentColor));
                break;

            case "Cancelled":
                startBtn.setText("CANCELLED");
                startBtn.setClickable(false);
                startBtn.setFocusable(false);
                startBtn.setTextColor(ContextCompat.getColor(UploadedRideMap.this, R.color.mapbox_navigation_route_layer_congestion_red));
                startBtn.setBackground(ContextCompat.getDrawable(UploadedRideMap.this, R.color.transparentColor));
                break;


            default:
                startBtn.setText("Start Ride");
                startBtn.setClickable(true);
                startBtn.setFocusable(true);
                startBtn.setTextColor(ContextCompat.getColor(UploadedRideMap.this, R.color.primaryColors));
                startBtn.setBackground(ContextCompat.getDrawable(UploadedRideMap.this, R.drawable.ripple));
        }
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

    }


    @Override
    protected void onStop() {
        super.onStop();
        mapboxNavigation.stopNavigation();
        mapView.onStop();
        activeRide_sharedPreference.edit().clear().apply();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

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
        mapboxNavigation.onDestroy();
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