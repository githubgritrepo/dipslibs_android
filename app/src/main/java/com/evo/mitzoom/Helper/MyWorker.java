package com.evo.mitzoom.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("CEK","MASUK MyWorker doWORK");
        Intent serviceIntent = new Intent(getApplicationContext(), OutboundServiceNew.class);
        getApplicationContext().startForegroundService(serviceIntent);
        return Result.success();
    }
}
