package com.evo.mitzoom.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.evo.mitzoom.ui.DipsOutboundCall;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("CEK","MyBroadcastReceiver getAction : "+intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context,OutboundService.class);
            context.startForegroundService(serviceIntent);

        } else {
            Intent alarmIntent = new Intent(context.getApplicationContext(), DipsOutboundCall.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.getApplicationContext().startActivity(alarmIntent);
        }

    }
}
