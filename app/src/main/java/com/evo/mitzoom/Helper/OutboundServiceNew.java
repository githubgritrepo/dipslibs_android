package com.evo.mitzoom.Helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsOutboundCall;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class OutboundServiceNew extends Service {
    private PowerManager.WakeLock wakeLock;
    public static final int POST_CONNECTIVITY_CHANGE_PING_INTERVAL = 30;
    //RabitMQ
    static ConnectionFactory connectionFactory = new ConnectionFactory();
    private Thread subscribeThreadCallOutbound;
    private static Thread publishCallAcceptThread;
    public static int IDSERVICES = 1001;
    public static int NOTIFICATION_IDOutbound = 101;
    private static String TAG = "OutboundServiceNew";
    private static Context mContext;
    private static SessionManager sessions;
    private static String idDips;
    public static String username_agent;
    public static String customerName;
    public static String password_session;
    public static String imagesAgent;
    public static String nameAgent;
    private static String csId;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG,"MASUK onCreate");

        mContext = this;
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        setupConnectionFactory(); //RabbitMQ
        subscribeCall();
        final PowerManager pm = ContextCompat.getSystemService(mContext, PowerManager.class);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Outbound:Service");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"MASUK ONDESTROY");
        if (subscribeThreadCallOutbound != null) {
            subscribeThreadCallOutbound.interrupt();
        }
        if (publishCallAcceptThread != null) {
            publishCallAcceptThread.interrupt();
        }
        stopForeground(IDSERVICES);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? null : intent.getAction();
        Log.e(TAG, "onStartCommand action : "+action);

        idDips = sessions.getKEY_IdDips();
        sessions.saveIdDips(idDips);

        stopForeground(IDSERVICES);

        notifyForeground();

        synchronized (this) {
            WakeLockHelper.acquire(wakeLock);

            WakeLockHelper.release(wakeLock);
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyForeground() {
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Your idDips = "+idDips)
                .setSmallIcon(R.mipmap.dips361);

        startForeground(IDSERVICES, notification.build());
    }

    private void setupConnectionFactory() {
        String uriRabbit = Server.BASE_URL_RABBITMQ;
        try {
            connectionFactory.setAutomaticRecoveryEnabled(true);
            connectionFactory.setUri(uriRabbit);
            connectionFactory.setNetworkRecoveryInterval(10000);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    void subscribeCall()
    {
        subscribeThreadCallOutbound = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG,"MASUK subscribeCall");
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    AMQP.Queue.DeclareOk q = channel.queueDeclare();
                    Log.e(TAG,"subscribeCall getQueue : "+q.getQueue());
                    channel.exchangeDeclare("dips361-cust-call", "direct", true);
                    channel.queueBind(q.getQueue(), "dips361-cust-call", "dips.direct.cust."+idDips+".call");
                    Log.e(TAG,"AFTER subscribeCall queueBind getChannelNumber : "+channel.getChannelNumber());
                    channel.basicConsume(q.getQueue(), true, new DeliverCallback() {
                        @Override
                        public void handle(String consumerTag, Delivery message) throws IOException {
                            String getMessage = new String(message.getBody());
                            Log.e(TAG,"Success subscribeCall getMessage : "+getMessage);
                            try {
                                JSONObject dataObj = new JSONObject(getMessage);
                                int getTicket = dataObj.getJSONObject("transaction").getInt("ticket");
                                csId = dataObj.getJSONObject("transaction").getString("csId");
                                String password = dataObj.getJSONObject("transaction").getString("password");
                                Log.e(TAG,"subscribeCall csId : "+csId);
                                Log.e(TAG,"subscribeCall password : "+password);
                                Log.e(TAG,"subscribeCall getTicket : "+getTicket);
                                String getQueue = String.format("%03d", getTicket);
                                Log.e(TAG,"subscribeCall getQueue : "+getQueue);

                                password_session = password;
                                customerName = "Fulan";
                                imagesAgent = "";
                                nameAgent = "Ade";

                                Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
                                intent.setAction("calloutbound");

                                PendingIntent pendingIntent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                    pendingIntent = PendingIntent.getBroadcast
                                            (mContext, 0, intent, PendingIntent.FLAG_MUTABLE);
                                }
                                else
                                {
                                    pendingIntent = PendingIntent.getBroadcast
                                            (mContext, 0, intent, 0);
                                }

                                long addTimes = System.currentTimeMillis() + 2000;

                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                alarmManager.set(AlarmManager.RTC_WAKEUP,addTimes,pendingIntent);

                                showNotificationOutbound();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                            Log.e(TAG,"subscribeCall consumerTag : "+consumerTag);
                        }
                    });

                } catch (ShutdownSignalException e) {
                    Log.e(TAG, "subscribeCall ShutdownSignalException: " + e.getMessage());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        subscribeCall();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException | TimeoutException e1) {
                    Log.e(TAG, "subscribeCall Connection broken: " + e1.getMessage());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        subscribeCall();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "subscribeCall InterruptedExceptionn: " + e.getMessage());
                    }
                }
            }
        });
        subscribeThreadCallOutbound.start();
    }

    private static JSONObject reqAcceptCall() {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject custObj = new JSONObject();
        try {
            custObj.put("status","ack");
            custObj.put("custId",idDips);
            custObj.put("msg","OK");
            custObj.put("ticket",sessions.getIDSchedule());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("from","Cust");
            jsObj.put("to","CS");
            jsObj.put("created",unixTime);
            jsObj.put("transaction",custObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj;
    }

    private static void publishCallAccept() {
        Log.e("CEK","publishCallAccept");
        publishCallAcceptThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = connectionFactory.newConnection();
                    Channel ch = connection.createChannel();
                    ch.confirmSelect();

                    JSONObject dataTicketObj = reqAcceptCall();
                    String dataTicket = dataTicketObj.toString();

                    ch.exchangeDeclare("dips361-cs-accept-user", "direct", true);
                    ch.basicPublish("dips361-cs-accept-user","dips.direct.cs."+csId+".accept.user",false,null,dataTicket.getBytes());
                    ch.waitForConfirmsOrDie();

                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, "publishCallAccept Connection broken: " + e.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        publishCallAccept();
                    } catch (InterruptedException e1) {

                    }
                }
            }
        });
        publishCallAcceptThread.start();
    }

    public static void acceptCall() {
        Log.e("CEK","acceptCall : "+sessions.getIDSchedule());
        if (sessions.getIDSchedule() > 0) {
            publishCallAccept();
        } else {
            Toast.makeText(mContext,"Tidak berhasil Call",Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotificationOutbound() {
        Log.e(TAG,"showNotificationOutbound");
        username_agent = "Ade";
        String CHANNEL_ID = "OutboundCall";
        String CHANNEL_NAME = "Outbound Call";

        NotificationManager notificationManagerCompat = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(getApplicationContext(), DipsOutboundCall.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntentCall = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntentCall = PendingIntent.getActivity
                    (this, 0, intent1, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntentCall = PendingIntent.getActivity
                    (this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        Intent intent2 = new Intent(getApplicationContext(), DipsOutboundCall.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.setAction("endcall");

        PendingIntent pendingIntentEnd = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntentEnd = PendingIntent.getActivity
                    (this, 0, intent2, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntentEnd = PendingIntent.getActivity
                    (this, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        String nameApps = getResources().getString(R.string.app_name_dips);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.dips361)
                .setContentTitle(nameApps+" Memanggil...")
                .setContentText("Panggilan Masuk dari "+username_agent)
                .addAction(R.drawable.ic_call,"Accept Call",pendingIntentCall)
                .addAction(R.drawable.ic_call_end,"Reject Call",pendingIntentEnd)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent))
                .setVibrate(new long[]{1000, 2000, 1000, 2000, 1000})
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_CALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);
            channel.setSound(null,null);
            channel.setVibrationPattern(new long[]{1000, 2000, 1000, 2000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        if (notificationManagerCompat != null) {
            Notification notification = builder.build();
            notification.flags = notification.flags | Notification.FLAG_INSISTENT;
            notificationManagerCompat.notify(NOTIFICATION_IDOutbound, notification);
        }
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

    public static String getNameAgent(){
        return nameAgent;
    }
}
