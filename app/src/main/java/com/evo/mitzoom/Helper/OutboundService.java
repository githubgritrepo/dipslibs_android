package com.evo.mitzoom.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class OutboundService extends Service {
    private Socket mSocket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on("outbound", outboundListener);
        mSocket.connect();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("Service", "Service is running...");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.mipmap.dips361);

        startForeground(1001, notification.build());

        return super.onStartCommand(intent, flags, startId);
    }
    private Emitter.Listener outboundListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONArray dataArr = new JSONArray(args);
                Log.d("OUTBOUND","dataArr Outbound : "+dataArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
