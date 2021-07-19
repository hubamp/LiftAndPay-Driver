package com.example.liftandpay_driver.threads;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

public class bookedNotification extends Worker {
    public bookedNotification(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        Toast.makeText(getApplicationContext(), "Hello Hello world", Toast.LENGTH_SHORT).show();
        return Result.success();
    }
}
