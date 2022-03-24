package com.LnPay.driver.pAPickUpLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.LnPay.driver.R;
import com.LnPay.driver.chats.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class ViewPickUpLocation extends AppCompatActivity implements OnMapReadyCallback {


    private MapView mapView;
    private MapboxMap mapboxMap;

    private FloatingActionButton chatFab;
    private double initialzoom;
    private CameraPosition initialCameraPos;

    private static LatLngBounds initlatLngBounds, newLatLngBounds;

    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    private final String symbolIconId = "symbolIconId";
    private final String mapBoxStyleUrl = "mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";

    private AlertDialog routDialog;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    private SharedPreferences passengerRequests_sharedPreference, activeRide_sharedPreference;

    private String thePassengerId, thePassengerName;

    private AlertDialog.Builder routingDialog;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String mUid = FirebaseAuth.getInstance().getUid();


    private TextView approveBtn, declinedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(ViewPickUpLocation.this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_view_pick_up_location);

        //This sharedpreference is from ApproveRequestAdapter.java
        passengerRequests_sharedPreference = getSharedPreferences("PASSENGER_REQUESTFILE", MODE_PRIVATE);

        //This sharedpreference is from UploadedRidesAdapter.java
        activeRide_sharedPreference = getSharedPreferences("ACTIVE_RIDEFILE", MODE_PRIVATE);

        chatFab = findViewById(R.id.chatFabId);
        approveBtn = findViewById(R.id.pANxtBtnId);
        declinedBtn = findViewById(R.id.pADeclinedBtnId);

        thePassengerId = passengerRequests_sharedPreference.getString("ThePassengersId", null);
        thePassengerName = passengerRequests_sharedPreference.getString("ThePassengersName", null);

        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPickUpLocation.this, ChatActivity.class);
                intent.putExtra("passengerId", thePassengerId);
                startActivity(intent);
            }
        });


        approveBtn.setOnClickListener(View -> {


            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(ViewPickUpLocation.this);

            // Set the message show for the Alert time
            builder.setMessage("Do you want to Approve " + thePassengerName + " for this ride?");
            builder.setTitle("Approve");
            builder.setCancelable(true);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AlertDialog.Builder approvingDlgBuild = new AlertDialog.Builder(ViewPickUpLocation.this);

                   AlertDialog approvingDlg = approvingDlgBuild.setMessage("Approving ...").create();

                    db.collection("Rides")
                            .document(activeRide_sharedPreference
                                    .getString("TheRideId", null))
                            .collection("Booked By").document(thePassengerId)
                            .update("Status", "Approved")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    approvingDlg.dismiss();
                                    dialog.dismiss();
                                    Snackbar.make(chatFab, "Approved", 3000).setBackgroundTint(ContextCompat.getColor(ViewPickUpLocation.this, R.color.mapbox_plugins_green)).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    approvingDlg.dismiss();
                                    dialog.dismiss();
                                    Snackbar.make(chatFab, "Approval Incomplete", 3000).setBackgroundTint(ContextCompat.getColor(ViewPickUpLocation.this, R.color.mapbox_navigation_route_layer_congestion_red)).show();

                                }
                            });
                }
            });
            builder.create().show();
        });

        declinedBtn.setOnClickListener(View -> {
            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(ViewPickUpLocation.this);

            // Set the message show for the Alert time
            builder.setMessage("Do you want to Decline " + thePassengerName + "'s request?");
            builder.setTitle("Decline");
            builder.setCancelable(true);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    db.collection("Rides")
                            .document(activeRide_sharedPreference
                                    .getString("TheRideId", null))
                            .collection("Booked By").document(thePassengerId)
                            .update("Status", "Declined")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    dialog.dismiss();
                                    Snackbar.make(chatFab, "Declined", 3000).setBackgroundTint(ContextCompat.getColor(ViewPickUpLocation.this, R.color.primaryColors)).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialog.dismiss();
                                    Snackbar.make(chatFab, "Approval Incomplete", 3000).setBackgroundTint(ContextCompat.getColor(ViewPickUpLocation.this, R.color.failure)).show();

                                }
                            });
                }
            });
            builder.create().show();
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(ViewPickUpLocation.this);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;


        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        ViewPickUpLocation.this.getResources(), R.drawable.mapbox_logo_icon));
                setUpSource(style);
                setupLayer(style);
                addDestinationIconSymbolLayer(style);


            }
        });


        double stLat = Double.parseDouble(activeRide_sharedPreference.getString("TheStLat", "0"));
        double stLon = Double.parseDouble(activeRide_sharedPreference.getString("TheStLon", "0"));
        double endLat = Double.parseDouble(activeRide_sharedPreference.getString("TheEndLat", "0"));
        double endLon = Double.parseDouble(activeRide_sharedPreference.getString("TheEndLon", "0"));

        if (stLat != 0 && stLon != 0 && endLat != 0 && endLon != 0) {
            LatLng points = new LatLng(stLat, stLon);
            LatLng pointd = new LatLng(endLat, endLon);

            initlatLngBounds = new LatLngBounds.Builder()
                    .include(points)
                    .include(pointd)
                    .build();

            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(initlatLngBounds, 150));

            routingDialog = new AlertDialog.Builder(ViewPickUpLocation.this).setMessage("Routing ...");
            routingDialog.setCancelable(false);
            routDialog = routingDialog.show();
            getRoute(Point.fromLngLat(stLon, stLat), Point.fromLngLat(endLon, endLat));
                        /*    passengerPickUpLocMarker(originPoint);
                            passengerPickUpLocMarker(originPoint);*/
        } else {
            Snackbar.make(chatFab, "stLat: " + stLat + "\nstLon: " + stLon + "\neLat: " + endLat + "\nelon: " + endLon, 3000).setBackgroundTint(ContextCompat.getColor(ViewPickUpLocation.this, R.color.mapbox_navigation_route_layer_congestion_red)
            ).show();
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
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    private void passengerPickUpLocMarker(Point point) {
        if (mapboxMap.getStyle() != null) {
            GeoJsonSource sanciangkoFlood1 = mapboxMap.getStyle().getSourceAs("destination-source-id");
            if (sanciangkoFlood1 != null) {
                sanciangkoFlood1.setGeoJson(FeatureCollection.fromFeature(
                        Feature.fromGeometry(Point.fromLngLat(point.longitude(), point.latitude()))
                ));


            }
        }
    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
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

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            navigationMapRoute.addRoute(currentRoute);
                        }
                        routDialog.dismiss();

                        double pickupLat = Double.parseDouble(passengerRequests_sharedPreference.getString("ThePickupLatitude", null));
                        double pickupLon = Double.parseDouble(passengerRequests_sharedPreference.getString("ThePickupLongitude", null));

                        passengerPickUpLocMarker(Point.fromLngLat(pickupLon, pickupLat));


                        mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().zoom(15).target(new LatLng(pickupLat, pickupLon)).build()), 2000);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(
                                        initlatLngBounds, 150), 3000);
                            }
                        }, 3000);

                    }


                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Timber.e(t.toString());
                        routDialog.dismiss();
                        Toast.makeText(ViewPickUpLocation.this, t.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });
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
        mapView.onStop();

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

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