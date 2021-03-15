package com.example.liftandpay_driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class UploadedRideMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng myLoc;

    Polyline polyline = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_ride_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();



        LatLng myLatLng = new LatLng(5.6231584,-0.197608);
        LatLng latLng1 = new LatLng(5.6131584,-0.196608);
        LatLng latLng2 = new LatLng(5.6331584,-0.296608);
        LatLng latLng3 = new LatLng(5.7131584,-0.196608);
        LatLng latLng4 = new LatLng(5.9131584,-0.196608);

                MarkerOptions markerOptions = new MarkerOptions().position(myLatLng);
                MarkerOptions markerOptions1 = new MarkerOptions().position(latLng4);

                //Create Marker
                Marker marker = mMap.addMarker((markerOptions));
                Marker marker1 = mMap.addMarker((markerOptions1));


                //Add Latlng and Marker
                latLngList.add(myLatLng);
                latLngList.add(latLng1);
                latLngList.add(latLng2);
                latLngList.add(latLng3);
                latLngList.add(latLng4);
                markerList.add(marker);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng3));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10f),  null);

                //Draw Polyline
                //clear content of polyline if not empty
                if(polyline != null) polyline.remove();

                //Create polylineoptions
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(latLngList).clickable(true);
                polyline = mMap.addPolyline(polylineOptions);

    }


    void getLocation() {
        fusedLocationProviderClient = getFusedLocationProviderClient(UploadedRideMap.this);

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
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(UploadedRideMap.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myLoc = new LatLng(location.getLatitude(), location.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(myLoc).title("My Location"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(8f),  null);
                    }
                });
    }
}