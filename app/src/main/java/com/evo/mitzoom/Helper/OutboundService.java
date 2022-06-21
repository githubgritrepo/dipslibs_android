package com.evo.mitzoom.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.IntegrationActivity;
import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsOutboundCall;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OutboundService extends Service {
    private Socket mSocket;
    private String idDips;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on("outbound", outboundListener);
        mSocket.connect();

        idDips = intent.getStringExtra("idDips");
        callOutbound(idDips);

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
            Log.d("OUTBOUND","masuk call");
            try {
                JSONArray dataArr = new JSONArray(args);
                Log.d("OUTBOUND","dataArr Outbound : "+dataArr);
                int code = (int) dataArr.get(0);
                if (code == 0) {
                    Intent intent = new Intent(getApplicationContext(), DipsOutboundCall.class);
                    startActivity(intent);
                    /*String usernameAgent = dataArr.get(1).toString();
                    acceptCall(usernameAgent);*/
                }

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

    private void callOutbound(String queueID) {
        JSONObject object = new JSONObject();
        try {
            object.put("room", queueID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("call","join",object);
    }

    private void acceptCall(String usernameAgent) {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("username",usernameAgent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.acceptCall(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
