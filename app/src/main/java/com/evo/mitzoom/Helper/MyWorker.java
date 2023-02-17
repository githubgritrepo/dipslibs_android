package com.evo.mitzoom.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public static final String EXTRA_START = "START";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean start = getInputData().getBoolean(EXTRA_START,false);
        Log.e("CEK","MASUK MyWorker doWORK : "+start);
        Intent serviceIntent = new Intent(getApplicationContext(), OutboundServiceNew.class);
        if (start) {
            getApplicationContext().startForegroundService(serviceIntent);
        } else {
            getApplicationContext().stopService(serviceIntent);
            OutboundServiceNew.stopServiceSocket();
        }
        return Result.success();
    }
}
