package com.evo.mitzoom.Helper;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.evo.mitzoom.ui.DipsOutboundCall;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("CEK","MyBroadcastReceiver getAction : "+intent.getAction());
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_INPUT_METHOD_CHANGED.equals(intent.getAction())
                || Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {*/
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context,OutboundService.class);
            ContextCompat.startForegroundService(context, serviceIntent);

        } else {
            Intent alarmIntent = new Intent(context.getApplicationContext(), DipsOutboundCall.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.getApplicationContext().startActivity(alarmIntent);
        }

    }

}
