package com.example.liftandpay_driver.uploadRide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class ViewMapActivity extends AppCompatActivity implements OnMapReadyCallback
     {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private TextView distanceText;
    private TextView stPoint1, stPoint2;
    private MarkerViewManager markerViewManager;
    private MarkerView marker1, marker2;

    Bundle cordinates;
    private String stLat,stLong,stName, endLat,endLong,endName;
    private LatLng locationOne;
    private LatLng locationTwo;
    private Point pointOne;
    private Point pointTwo;
    private Snackbar snackbar;

    private TextView locationNameOne, locationNameTwo;

    private DirectionsRoute route;
    private NavigationMapRoute navigationMapRoute;


         @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_view_map);
        cordinates = getIntent().getExtras();

        distanceText = findViewById(R.id.distanceId);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        View customView = LayoutInflater.from(ViewMapActivity.this).inflate(
                R.layout.markerview, null);
        View customView2 = LayoutInflater.from(ViewMapActivity.this).inflate(
                R.layout.markerview, null);

        stPoint1 = customView.findViewById(R.id.locationNameId);
        stPoint2 = customView2.findViewById(R.id.locationNameId);




        this.mapboxMap = mapboxMap;
        markerViewManager = new MarkerViewManager(mapView, mapboxMap);
//        marker1 = new MarkerView(new LatLng(LAT,LONG), customView);


        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addMarkerIconsToMap(style);

                stName = cordinates.getString("StartingName","null");
                endName = cordinates.getString("StoppingName","null");

                stPoint1.setText(""+ stName);
                stPoint2.setText(""+endName);


                customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                customView2.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

                if(!stName.equals("null") && !endName.equals("null")) {
                    marker1 = new MarkerView(locationOne, customView);
                    markerViewManager.addMarker(marker1);locationNameTwo = customView2.findViewById(R.id.locationNameId);
                    marker2 = new MarkerView(locationTwo,customView2);
                    markerViewManager.addMarker(marker2);

                }
                else
                {
                    snackbar.setText("Could not fetch names").show();
                }

            }
        });
    }

         private void getRoute(Point origin, Point destination) {
             NavigationRoute.builder(this)
                     .accessToken(Mapbox.getAccessToken())
                     .origin(origin)
                     .destination(destination)
                     .build()
                     .getRoute(new Callback<DirectionsResponse>() {
                         @Override
                         public void onResponse(Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
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
                             double routeKilo = route.distance() / 1000;
                             distanceText.setText(String.format("%skm", routeKilo));
// Draw the route on the map
                             if (navigationMapRoute != null) {
                                 navigationMapRoute.removeRoute();
                             } else {
                                 navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                             }
                             navigationMapRoute.addRoute(route);
                         }

                         @Override
                         public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                             Timber.e(t.toString());
                             snackbar.setText(t.toString()).setDuration(5000).show();

                         }
                     });
         }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addMarkerIconsToMap(@NonNull Style loadedMapStyle) {
        if (cordinates == null) {
            stLat = null;
            stLong = null;
            endLat = null;
            endLong = null;


            Toast.makeText(this, "Error whiles fetching Locations", Toast.LENGTH_LONG).show();
            this.finish();
        }

        else {

            stLong = cordinates.getString("StartingLongitude");
            stLat = cordinates.getString("StartingLatitude");
            endLong = cordinates.getString("StoppingLongitude");
            endLat = cordinates.getString("StoppingLatitude");

            double sLat = Double.parseDouble(stLat);
            double sLong = Double.parseDouble(stLong);
            double eLat = Double.parseDouble(endLat);
            double eLong = Double.parseDouble(endLong);


            locationOne = new LatLng(sLat, sLong);
            locationTwo = new LatLng(eLat, eLong);
            pointOne = Point.fromLngLat(locationOne.getLongitude(), locationOne.getLatitude());
            pointTwo = Point.fromLngLat(locationTwo.getLongitude(), locationTwo.getLatitude());


            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(locationOne)
                    .include(locationTwo)
                    .build();

            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150), 5000);
            getRoute(pointOne,pointTwo);

        }
    }

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
        markerViewManager.onDestroy();
    }
}