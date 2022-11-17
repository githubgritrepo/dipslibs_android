package com.evo.mitzoom.Helper;

import android.content.Context;
import android.util.Log;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Session.SessionManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabbitMirroring {

    //RabitMQ
    static ConnectionFactory connectionFactory = new ConnectionFactory();
    private static Thread publishThread;
    private static Thread publishEndpointThread;
    private static Context mContext;
    private static SessionManager sessions;

    public RabbitMirroring(Context mContext) {
        this.mContext = mContext;

        setupConnectionFactory();
    }

    private void setupConnectionFactory() {
        sessions = new SessionManager(mContext);

        String uriRabbit = Server.BASE_URL_RABBITMQ;
        try {
            connectionFactory.setAutomaticRecoveryEnabled(false);
            connectionFactory.setUri(uriRabbit);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static void MirroringSendKey(JSONObject jsons) {
        Log.e("CEK","MASUK MirroringSendKey : "+jsons.toString());
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = connectionFactory.newConnection();
                    Channel ch = connection.createChannel();
                    ch.confirmSelect();

                    String csID = sessions.getCSID();
                    Log.e("CEK","csID : "+csID);

                    JSONObject datax = dataMirroring(jsons);
                    String dataxS = datax.toString();

                    Log.e("CEK","dataxS : "+dataxS.toString());

                    ch.basicPublish("dips361-cs-send-key","dips.direct.cs."+csID+".send.key",false,null,dataxS.getBytes());
                    ch.waitForConfirmsOrDie();

                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e("CEK", "publishToAMQP Connection broken: " + e.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        MirroringSendKey(jsons);
                    } catch (InterruptedException e1) {

                    }
                }
            }
        });
        publishThread.start();
    }

    public static void MirroringSendEndpoint(int kodeEndPoint) {
        Log.e("CEK","MASUK MirroringSendEndpoint");
        publishEndpointThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsons = null;
                    try {
                        jsons = new JSONObject();
                        jsons.put("endpoint",kodeEndPoint);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Connection connection = connectionFactory.newConnection();
                    Channel ch = connection.createChannel();
                    ch.confirmSelect();

                    String csID = sessions.getCSID();
                    Log.e("CEK","csID : "+csID);

                    JSONObject datax = dataMirroring(jsons);
                    String dataxS = datax.toString();

                    Log.e("CEK","dataxS : "+dataxS.toString());

                    ch.basicPublish("dips361-cs-send-endpoint","dips.direct.cs."+csID+".send.endpoint",false,null,dataxS.getBytes());
                    ch.waitForConfirmsOrDie();

                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e("CEK", "publishToAMQP Connection broken: " + e.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                    } catch (InterruptedException e1) {

                    }
                }
            }
        });
        publishEndpointThread.start();
    }

    private static JSONObject dataMirroring(JSONObject dataObj) {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("from","Cust");
            jsObj.put("to","CS");
            jsObj.put("created",unixTime);
            jsObj.put("transaction",dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj;
    }

    public static void closeThreadConnection() {
        if (publishThread != null) {
            publishThread.interrupt();
        }

        if (publishEndpointThread != null) {
            publishEndpointThread.interrupt();
        }
    }
}
