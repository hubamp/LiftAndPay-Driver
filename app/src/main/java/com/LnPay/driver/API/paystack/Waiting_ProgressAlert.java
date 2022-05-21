package com.LnPay.driver.API.paystack;


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

import com.LnPay.driver.R;
import com.LnPay.driver.accounts.AccountActivity;
import com.google.firebase.firestore.FirebaseFirestore;


public class Waiting_ProgressAlert extends AlertDialog {


    public Waiting_ProgressAlert(@NonNull Context context) {
        super(context);

        Waiting_ProgressAlert waiting_progressAlert = new Waiting_ProgressAlert(context);
        waiting_progressAlert.setView(LayoutInflater.from(context).inflate(R.layout.alert_payment_progress, null));
        waiting_progressAlert.setCancelable(false);

    }



}
