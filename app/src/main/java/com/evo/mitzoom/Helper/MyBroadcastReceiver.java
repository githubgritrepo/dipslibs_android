package com.evo.mitzoom.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, OutboundService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}