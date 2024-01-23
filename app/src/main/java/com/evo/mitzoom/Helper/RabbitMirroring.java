package com.evo.mitzoom.Helper;

import android.content.Context;
import android.content.Intent;
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
    private static Connection connection = null;
    private static Thread publishThread;
    private static Thread publishEndpointThread;
    private static Context mContext;
    private static SessionManager sessions;
    private static final String TAG = "RabbitMirroring";
    private static Channel chSendKey = null;
    private static Channel chSendEndpoint = null;

    public RabbitMirroring(Context mContext) {
        RabbitMirroring.mContext = mContext;

        setupConnectionFactory();
    }

    private void setupConnectionFactory() {
        sessions = new SessionManager(mContext);

        String uriRabbit = Server.BASE_URL_RABBITMQ;
        try {
            connectionFactory.setAutomaticRecoveryEnabled(false);
            connectionFactory.setUri(uriRabbit);
            connection = connectionFactory.newConnection();
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void MirroringSendKey(JSONObject jsons) {
        if (connection != null) {
            publishThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        chSendKey = connection.createChannel();
                        chSendKey.confirmSelect();

                        String csID = sessions.getCSID();

                        JSONObject datax = dataMirroring(jsons);
                        String dataxS = datax.toString();

                        chSendKey.basicPublish("dips361-cs-send-key", "dips.direct.cs." + csID + ".send.key", false, null, dataxS.getBytes());
                        chSendKey.waitForConfirmsOrDie();

                    } catch (IOException | InterruptedException e) {
                        try {
                            Thread.sleep(4000); //sleep and then try again
                            MirroringSendKey(jsons);
                        } catch (InterruptedException ignored) {

                        }
                    }
                }
            });
            publishThread.start();
        }
    }

    public static void MirroringSendEndpoint(int kodeEndPoint) {
        if (connection != null) {
            publishEndpointThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsons = null;
                        try {
                            jsons = new JSONObject();
                            jsons.put("endpoint", kodeEndPoint);
                            if (kodeEndPoint == 99) {
                                if (sessions.getKEY_IdDips() != null) {
                                    jsons.put("idDips", sessions.getKEY_IdDips());
                                }
                                Intent intentOutbound = new Intent(mContext, OutboundServiceNew.class);
                                mContext.stopService(intentOutbound);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        chSendEndpoint = connection.createChannel();
                        chSendEndpoint.confirmSelect();

                        String csID = sessions.getCSID();
                        JSONObject datax = dataMirroring(jsons);
                        String dataxS = datax.toString();

                        chSendEndpoint.basicPublish("dips361-cs-send-endpoint", "dips.direct.cs." + csID + ".send.endpoint", false, null, dataxS.getBytes());
                        chSendEndpoint.waitForConfirmsOrDie();

                    } catch (IOException | InterruptedException e) {
                        try {
                            Thread.sleep(4000); //sleep and then try again
                        } catch (InterruptedException e1) {

                        }
                    }
                }
            });
            publishEndpointThread.start();
        }
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

        if (connection != null) {
            if (chSendKey != null) {
                try {
                    if (chSendKey.isOpen()) {
                        chSendKey.close();
                    }
                } catch (IOException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
            if (chSendEndpoint != null) {
                try {
                    if (chSendEndpoint.isOpen()) {
                        chSendEndpoint.close();
                    }
                } catch (IOException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                if (connection.isOpen()) {
                    connection.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
