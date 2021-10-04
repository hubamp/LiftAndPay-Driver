package com.example.liftandpay_driver.fastClass;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdatedDriverLocationWorker extends Worker {

    private Handler handler = new Handler(Looper.getMainLooper());

    public UpdatedDriverLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("UpdatedLoc", "Driver location updated");
                handler.postDelayed(this,4000);
            }
        }, 4000);


        return Result.success();
    }

}
