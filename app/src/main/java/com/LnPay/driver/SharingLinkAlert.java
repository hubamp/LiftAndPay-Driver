package com.LnPay.driver;


import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.FirebaseFirestore;


public class SharingLinkAlert extends AlertDialog.Builder {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String baseLink = "https://www.liftandpay.com/myride.php?";
    private String purpose;
    private String theDriverId;
    private double startLat, startLon;
    private double endLat, endLon;
    private String theRideId;
    private String startTime;
    private String journey;
    private String distance;
    private String driverName;
    private TextView copyBtn;
    private TextView generatedLink;
    private AlertDialog.Builder sharingDialog;
    private View view;

    private Context context;


    public SharingLinkAlert(@NonNull Context context) {
        super(context);

        this.context = context;
        sharingDialog = new AlertDialog.Builder(context);
        view = LayoutInflater.from(context).inflate(R.layout.alert_generate_shareable_link, null);
        copyBtn = view.findViewById(R.id.copyBtn);
        generatedLink = view.findViewById(R.id.generatedlink);

    }


    public SharingLinkAlert setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public SharingLinkAlert setTheDriverId(String theDriverId) {
        this.theDriverId = theDriverId;
        return this;
    }

    public SharingLinkAlert setStartLat(double startLat) {
        this.startLat = startLat;
        return this;
    }

    public SharingLinkAlert setStartLon(double startLon) {
        this.startLon = startLon;
        return this;
    }

    public SharingLinkAlert setEndLat(double endLat) {
        this.endLat = endLat;
        return this;
    }

    public SharingLinkAlert setEndLon(double endLon) {
        this.endLon = endLon;
        return this;
    }

    public SharingLinkAlert setTheRideId(String theRideId) {
        this.theRideId = theRideId;
        return this;
    }

    public SharingLinkAlert setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public SharingLinkAlert setJourney(String journey) {
        this.journey = journey;
        return this;
    }

    public SharingLinkAlert setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public SharingLinkAlert setDriverName(String driverName) {
        this.driverName = driverName;
        return this;
    }


    public void build() {

        baseLink = baseLink +
                "p='" + purpose + "'" + "," +
                "RIID='" + theRideId + "'" + ",";

        baseLink = baseLink.replace(" ", "%20");
        generatedLink.setText(baseLink);

        copyBtn.setOnClickListener(View -> {

            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("MyRide", baseLink);

            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied", Toast.LENGTH_LONG).show();
        });


        sharingDialog.setView(view);
        sharingDialog.show();
    }
}
