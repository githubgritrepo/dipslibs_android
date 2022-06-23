package com.evo.mitzoom.Helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.IntegrationActivity;
import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsOutboundCall;
import com.evo.mitzoom.ui.DipsVideoConfren;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKAudioOption;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKSessionContext;
import us.zoom.sdk.ZoomVideoSDKVideoOption;


public class OutboundService extends Service {
    private Socket mSocket;
    public static String idDips;
    public static String username_agent;
    public static String password_session;
    private static String TAG = "OutboundService";
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        /*PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();*/

        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on("outbound", outboundListener);
        mSocket.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"MASUK onStartCommand");

        idDips = intent.getStringExtra("idDips");
        callOutbound(idDips);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.i(TAG+"_Service", "Service is running idDips : "+idDips+" | mSocket.connected() : "+mSocket.connected());
                            if (!mSocket.connected()) {
                                Log.i(TAG,"CONNECTED AGAIN");
                                mSocket.connect();
                                callOutbound(idDips);
                            }
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

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    private Emitter.Listener outboundListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG,"masuk call");
            try {
                JSONArray dataArr = new JSONArray(args);
                Log.i(TAG,"dataArr Outbound : "+dataArr);
                int code = (int) dataArr.get(0);
                if (code == 0) {
                    Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);

                    PendingIntent pendingIntent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getBroadcast
                                (getApplicationContext(), 0, intent, 0);
                    }
                    else
                    {
                        pendingIntent = PendingIntent.getBroadcast
                                (getApplicationContext(), 0, intent, 0);
                    }

                    Log.i(TAG,"currentTimeMillis : "+System.currentTimeMillis());
                    long addTimes = System.currentTimeMillis() + 5000;
                    Log.i(TAG,"currentTimeMillis add : "+addTimes);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,addTimes,pendingIntent);

                    username_agent = dataArr.get(1).toString();
                    password_session = dataArr.get(2).toString();
                    //acceptCall(usernameAgent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG,"MASUK onTaskRemoved");
    }

    public static String getPassword_session(){
        return password_session;
    }

    public static String getIdDips(){
        return idDips;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void callOutbound(String queueID) {
        Log.i(TAG,"MASUK callOutbound");
        JSONObject object = new JSONObject();
        try {
            object.put("room", queueID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("call","join",object);
    }

    public static void acceptCall() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("username",username_agent);
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
                    Log.i(TAG+"_ACCEPT",""+response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public static void rejectCall(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("username",username_agent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.rejectCall(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    Log.i(TAG+"_REJECT",""+response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
