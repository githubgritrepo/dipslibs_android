package com.evo.mitzoom.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class OtpReceiver extends BroadcastReceiver {
    public OtpReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] objects = (Object[]) bundle.get("pdus");
                String message = "";
                for (int i = 0; i < objects.length; i++) {
                    SmsMessage smsMessage = getIncomingMessage(objects[i], bundle);
                    String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                    String sendNumber = phoneNumber;
                    message = smsMessage.getDisplayMessageBody();
                    Log.i("ISI_SMS", "senderNum: " + sendNumber + "; message: " + message);
                }
                if (!message.isEmpty()) {
                    Log.e("CEK","MESSAGE : "+message);
                    Intent newIntent = new Intent("getotp");
                    newIntent.putExtra("smsMessage", message);
                    LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(newIntent);
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSms = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSms = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSms;
    }
}
