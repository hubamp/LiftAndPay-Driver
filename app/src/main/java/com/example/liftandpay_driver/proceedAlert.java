package com.example.liftandpay_driver;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDialogFragment;

public class proceedAlert extends AppCompatDialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("YOUR RIDE HAS BEEN UPLOADED")
                    .setMessage("You will be alerted as soon as a passenger requests to join.")
                    .setPositiveButton("Thank you!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText( getContext(), "You are welcome",Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    });
            return builder.create();
        }
    }

