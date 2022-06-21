package com.evo.mitzoom.Helper;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.evo.mitzoom.API.Server;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketBackgroundService extends Service implements SocketEventListener.Listener {

    private static final String EVENT_OUTBOUND = "outbound";
    private static final String TAG = "DiPS361";
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private Boolean isConnected = true;
    private Socket mSocket;
    private ConcurrentHashMap<String, SocketEventListener> listenersMap;


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    Log.w(TAG, "Connected");
                    /*Toast.makeText(SocketIOService.this,
                            R.string.connect, Toast.LENGTH_LONG).show();*/
                    break;
                case 2:
                    Log.w(TAG, "Disconnected");
                    /*Toast.makeText(SocketIOService.this,ss
                            R.string.disconnect, Toast.LENGTH_LONG).show();*/
                    break;
                case 3:
                    Log.w(TAG, "Error in Connection");
                    /*Toast.makeText(SocketIOService.this,
                            R.string.error_connect, Toast.LENGTH_LONG).show();*/
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listenersMap = new ConcurrentHashMap<>();

        HandlerThread thread = new HandlerThread(TAG+"Args",THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        getSocketListener();

        for (Map.Entry<String, SocketEventListener> entry : listenersMap.entrySet()) {
            mSocket.on(entry.getKey(), entry.getValue());
        }

        mSocket.connect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (intent != null) {

        }

        return START_STICKY;
    }

    @Override
    public void onEventCall(String event, Object... objects) {
        switch (event) {
            case Socket.EVENT_CONNECT:
                joinConverence();
                android.os.Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = 1;
                mServiceHandler.sendMessage(msg);
                isConnected = true;
                break;
            case Socket.EVENT_DISCONNECT:
                Log.w(TAG, "socket disconnected");
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 2;
                mServiceHandler.sendMessage(msg);
                break;
            case Socket.EVENT_CONNECT_ERROR:
                isConnected = false;
                msg = mServiceHandler.obtainMessage();
                msg.arg1 = 3;
                mServiceHandler.sendMessage(msg);
                // reconnect
                mSocket.connect();
                break;
            case EVENT_OUTBOUND:
                break;
        }
    }

    private boolean isSocketConnected() {
        if (null == mSocket) {
            return false;
        }
        if (!mSocket.connected()) {
            mSocket.connect();
            Log.i(TAG, "reconnecting socket...");
            return false;
        }

        return true;
    }

    private void getSocketListener() {
        listenersMap.put(Socket.EVENT_CONNECT, new SocketEventListener(Socket.EVENT_CONNECT, this));
        listenersMap.put(Socket.EVENT_DISCONNECT, new SocketEventListener(Socket.EVENT_DISCONNECT, this));
        listenersMap.put(Socket.EVENT_CONNECT_ERROR, new SocketEventListener(Socket.EVENT_CONNECT_ERROR, this));
        //listenersMap.put(Socket.EVENT_CONNECT_TIMEOUT, new SocketEventListener(Socket.EVENT_CONNECT_TIMEOUT, this));
        listenersMap.put(EVENT_OUTBOUND, new SocketEventListener(EVENT_OUTBOUND, this));
    }

    private void joinConverence() {

    }
}
