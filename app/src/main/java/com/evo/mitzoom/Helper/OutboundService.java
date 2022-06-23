package com.evo.mitzoom.Helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsOutboundCall;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OutboundService extends Service implements SocketEventListener.Listener{
    private Socket mSocket;
    private static final String EVENT_OUTBOUND = "outbound";
    private static final String EVENT_CALL = "call";
    public static int NOTIFICATION_IDOutbound;
    public static String idDips;
    public static String username_agent;
    public static String customerName;
    public static String password_session;
    public static String imagesAgent;
    private static String TAG = "OutboundService";
    private Context mContext;
    private boolean toOutbound = false;
    private SessionManager sessions;
    private ConcurrentHashMap<String, SocketEventListener> listenersMap;
    private Boolean isConnected = true;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        sessions = new SessionManager(mContext);
        listenersMap = new ConcurrentHashMap<>();
        idDips = getAuthCredentialIDDiPS();
        if (idDips.isEmpty()) {
            idDips = sessions.getKEY_IdDips();
        } else {
            sessions.saveIdDips(idDips);
        }

        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        getSocketListener();

        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.on(entry.getKey(), entry.getValue());
        }

        //mSocket.on("outbound", outboundListener);
        mSocket.connect();
    }

    private void getSocketListener() {
        listenersMap.put(Socket.EVENT_CONNECT, new SocketEventListener(Socket.EVENT_CONNECT, this));
        listenersMap.put(Socket.EVENT_DISCONNECT, new SocketEventListener(Socket.EVENT_DISCONNECT, this));
        listenersMap.put(Socket.EVENT_CONNECT_ERROR, new SocketEventListener(Socket.EVENT_CONNECT_ERROR, this));
        listenersMap.put(EVENT_OUTBOUND, new SocketEventListener(EVENT_OUTBOUND, this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"MASUK onStartCommand");
        //callOutbound(idDips);

        if (intent != null) {
            if (!mSocket.connected()) {
                mSocket.connect();
                Log.i(TAG, "connecting socket...");
            } else {
                callOutbound(idDips);
                processThreadNotif();
            }
        }



        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void processThreadNotif() {
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
    }

    private Emitter.Listener outboundListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            callbackCall(args);
        }
    };

    private void callbackCall(Object[] args) {
        Log.i(TAG,"masuk call");
        try {
            JSONArray dataArr = new JSONArray(args);
            Log.i(TAG,"dataArr Outbound : "+dataArr);
            int code = (int) dataArr.get(0);
            if (code == 0 && toOutbound == false) {
                Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
                intent.setAction("calloutbound");

                PendingIntent pendingIntent = null;
                pendingIntent = PendingIntent.getBroadcast
                        (getApplicationContext(), 0, intent, 0);

                long addTimes = System.currentTimeMillis() + 5000;

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,addTimes,pendingIntent);

                username_agent = dataArr.get(1).toString();
                password_session = dataArr.get(2).toString();
                customerName = dataArr.get(3).toString();
                boolean isCust = (boolean) dataArr.get(4);
                imagesAgent = dataArr.get(5).toString();
                sessions.saveIsCust(isCust);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            showNotificationOutbound();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                toOutbound = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG,"MASUK onTaskRemoved");
        callOutbound(idDips);
    }

    public static String getPassword_session(){
        return password_session;
    }

    public static String getCustomerName(){
        return customerName;
    }

    public static String getImagesAgent(){
        return imagesAgent;
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
        mSocket.emit(EVENT_CALL,"join",object);
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

    private File createDir() {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), IMAGE_DIRECTORY_NAME);

        return mediaStorageDir;
    }

    private String getAuthCredentialIDDiPS() {
        String getidDips = "";
        String filename = "Auth_Credential.json";
        File dir = createDir();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }
        File mediaFile;
        mediaFile = new File(dir.getPath() + File.separator +
                filename);

        try {
            FileInputStream fis = new FileInputStream(mediaFile);

            String jString = null;
            try {
                FileChannel fc = fis.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                jString = Charset.defaultCharset().decode(bb).toString();
            }
            finally {
                fis.close();
            }

            JSONObject obj = new JSONObject(jString);
            getidDips = obj.getString("idDips");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getidDips;
    }

    private void showNotificationOutbound() {
        String CHANNEL_ID = "OutboundCall";
        String CHANNEL_NAME = "Outbound Call";
        NOTIFICATION_IDOutbound = 101;

        NotificationManager notificationManagerCompat = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(getApplicationContext(), DipsOutboundCall.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntentCall = null;
        pendingIntentCall = PendingIntent.getActivity
                (getApplicationContext(), 0, intent1, 0);

        Intent intent = new Intent("close_app");
        PendingIntent pIntentReject = PendingIntent.getBroadcast(this, (int)
                System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        String nameApps = getResources().getString(R.string.app_name_dips);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.dips361)
                .setContentTitle(nameApps+" Memanggil...")
                .setContentText("Panggilan Masuk dari "+username_agent)
                .addAction(R.drawable.ic_call,"Accept Call",pendingIntentCall)
                .addAction(R.drawable.ic_call_end,"Reject Call",pIntentReject)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent))
                .setVibrate(new long[]{1000, 5000, 1000, 5000, 1000})
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_SOUND);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 5000, 1000, 5000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(NOTIFICATION_IDOutbound, builder.build());
        }
    }

    @Override
    public void onEventCall(String event, Object... args) {
        Log.i(TAG, "onEventCall : "+event);
        switch (event) {
            case Socket.EVENT_CONNECT:
                Log.i(TAG, "socket connected");
                callOutbound(idDips);
                processThreadNotif();
                isConnected = true;
                break;
            case Socket.EVENT_DISCONNECT:
                Log.i(TAG, "socket disconnected");
                isConnected = false;
                break;
            case Socket.EVENT_CONNECT_ERROR:
                Log.i(TAG, "socket ERROR");
                isConnected = false;
                // reconnect
                mSocket.connect();
                break;
            case EVENT_OUTBOUND:
                callbackCall(args);
                break;
        }
    }
}
