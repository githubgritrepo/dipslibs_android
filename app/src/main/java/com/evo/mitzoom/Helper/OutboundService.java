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

        final String CHANNELID = "Foreground Service ID";

        return super.onStartCommand(intent, flags, startId);
    }
    private Emitter.Listener outboundListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONArray dataArr = new JSONArray(args);
                Log.d("CEK","dataArr : "+dataArr);
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
