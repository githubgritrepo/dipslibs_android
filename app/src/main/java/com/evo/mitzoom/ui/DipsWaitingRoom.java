package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.Manifest;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Constants.AuthConstants;
import com.evo.mitzoom.Fragments.frag_berita;
import com.evo.mitzoom.Helper.OutboundService;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.util.ErrorMsgUtil;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
import us.zoom.sdk.ZoomVideoSDKErrors;
import us.zoom.sdk.ZoomVideoSDKInitParams;
import us.zoom.sdk.ZoomVideoSDKRawDataMemoryMode;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKSessionContext;
import us.zoom.sdk.ZoomVideoSDKVideoOption;

public class DipsWaitingRoom extends AppCompatActivity {

    private Context mContext;
    protected final static int REQUEST_VIDEO_AUDIO_CODE = 1010;
    protected int renderType = BaseMeetingActivity.RENDER_TYPE_ZOOMRENDERER;
    private static final Integer[] img = {R.drawable.adsv1, R.drawable.adsv2, R.drawable.adsv3};
    public static int CAM_ID = 0;
    private static final String KEY_USE_FACING = "use_facing";
    public static Integer useFacing = null;
    private Camera camera = null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private static int degreeFront = 0;
    private MaterialButton btnSchedule,btnSchedule2, btnEndCall;
    private LayoutInflater inflater;
    private View dialogView;
    private ImageView btnclose;
    private TextView et_Date, AnimationCall;
    private AutoCompleteTextView et_time;
    private int year, month, day, waktu_tunggu = 6000;
    private String tanggal, waktu;
    String [] time = {"08.00 - 10.00", "10.00 - 12.00", "12.00 - 14.00", "14.00 - 16.00", "16.00 - 17.00"};
    String NameSession;
    String idDips;
    String SessionPass;
    String myTicketNumber;
    boolean isCust;
    String custName;
    private Handler handlerSuccess;
    private boolean stopPopSuccess = false;
    private boolean doubleBackToExitPressedOnce = false;
    private SessionManager sessions;
    private TextView myTicket, lastTicket;
    private SweetAlertDialog dialogWaiting;
    private SweetAlertDialog dialogSuccess;
    private SweetAlertDialog dialogConfirm;
    private DisplayMetrics displayMetrics;
    private String Savetanggal;
    private int Savewaktu;
    //RabitMQ
    ConnectionFactory connectionFactory = new ConnectionFactory();
    private Socket mSocket;
    private Thread subscribeThread;
    private Thread subscribeReqTicketThread;
    public static Thread subscribeThreadCall;
    private Thread subscribeAllTicketInfoCall;
    private Thread publishThread;
    private Thread publishQSTicketThread;
    private Thread publishCallAcceptThread;
    private boolean isSwafoto = false;
    private int chkFlow;
    public static Channel channelCall = null;

    {
        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {}
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mContext = this;
        sessions = new SessionManager(mContext);
        sessions.saveRTGS(null);
        sessions.saveCSID(null);
        idDips = sessions.getKEY_IdDips();
        isCust = sessions.getKEY_iSCust();
        chkFlow = sessions.getFLOW();
        String lang = sessions.getLANG();
        setLocale(this,lang);
        setContentView(R.layout.activity_dips_waiting_room);

        myTicket = findViewById(R.id.myticket);
        lastTicket = findViewById(R.id.last_ticket);
        AnimationCall = findViewById(R.id.AnimationCall);
        CardView cardSurf = (CardView) findViewById(R.id.cardSurf);
        preview = (SurfaceView) findViewById(R.id.mySurface);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDisp = displayMetrics.widthPixels;
        int dyWidth = (int) Math.ceil(widthDisp / 2);

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);

        /*ViewGroup.LayoutParams lp = cardSurf.getLayoutParams();
        lp.width = dyWidth;
        cardSurf.setLayoutParams(lp);*/

        initializeSdk();
        AnimationCall();
        setupConnectionFactory(); //RabbitMQ

        custName = getIntent().getExtras().getString("CUSTNAME");

        boolean cekConstain = getIntent().getExtras().containsKey("RESULT_IMAGE_AI");
        if (cekConstain) {
            byte[] resultImage = getIntent().getExtras().getByteArray("RESULT_IMAGE_AI");
            String imgBase64 = Base64.encodeToString(resultImage, Base64.NO_WRAP);
            //processCaptureIdentify(imgBase64);
            //processCaptureIdentifyAuth(imgBase64);
            initialWaitingRoom();
        } else {
            /*NameSession = getIntent().getExtras().getString("SessionName");
            SessionPass = getIntent().getExtras().getString("SessionPass");*/

            initialWaitingRoom();
        }
        getFragmentPage(new frag_berita());

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("CEK","MASUK onResume");

        camera = Camera.open(useFacing);
        //startPreview();
        cameraConfigured = false;
        previewHolder();
        stopPopSuccess = false;

        /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.i("Tag", "active connection");
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Log.i("Tag", "losing active connection");
                isNetworkConnected();
            }
        });*/
    }

    /*private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())) {
            //Do something
            Log.e("CEK","isNetworkConnected MASUK IF");
            return false;
        }
        return true;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        Log.e("CEK","MASUK ONPAUSE");
        if (inPreview) {
            camera.stopPreview();
        }

        if (camera != null) {
            camera.release();
            camera = null;
            inPreview = false;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("waiting");
        }
        if (publishThread != null) {
            publishThread.interrupt();
        }
        if (publishQSTicketThread != null) {
            publishQSTicketThread.interrupt();
        }
        if (publishCallAcceptThread != null) {
            publishCallAcceptThread.interrupt();
        }
        if (subscribeThread != null) {
            subscribeThread.interrupt();
        }
        if (subscribeReqTicketThread != null) {
            subscribeReqTicketThread.interrupt();
        }
        if (subscribeThreadCall != null) {
            subscribeThreadCall.interrupt();
        }
        if (subscribeAllTicketInfoCall != null) {
            subscribeAllTicketInfoCall.interrupt();
        }
    }

    private void initialWaitingRoom() {
        Log.d("CEK", "idDips : "+idDips);
        subscribeReqTicket();
        //subscribeAllTicketInfo(); //RabbitMQ

        /*mSocket.on("waiting", waitingListener);
        mSocket.connect();*/

        //processGetTicket(myTicket);

        savedAuthCredentialIDDiPS(idDips);
    }

    private void serviceOutbound() {
        Intent serviceIntent = new Intent(this, OutboundServiceNew.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void processCaptureIdentifyAuth(String imgBase64) {
        if (!NetworkUtil.hasDataNetwork(mContext)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("image",imgBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String dataCapture = jsons.toString();

        /*String filename = "Auth_Customer_Capture.txt";
        try {
            createTemporaryFile(dataCapture,filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("CEK","processCaptureIdentifyAuth REQUEST json : "+jsons.toString());*/

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.CaptureAuth(requestBody);
        Log.e("CEK","REQUEST CALL : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","RESPONSE CODE: "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","dataS: "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONObject dataCustomer = dataObj.getJSONObject("data").getJSONObject("customer");
                        JSONObject dataToken = dataObj.getJSONObject("data").getJSONObject("token");

                        isSwafoto = dataCustomer.getBoolean("isSwafoto");

                        String noCIF = "";
                        if (dataCustomer.isNull("noCif")) {
                            isCust = false;
                        } else {
                            isCust = true;
                            noCIF = dataCustomer.getString("noCif");
                        }
                        custName = dataCustomer.getString("namaLengkap");
                        String idDipsNew = dataCustomer.getString("idDips");
                        Log.e("CEK","idDipsNew : "+idDipsNew+" | idDips : "+idDips);
                        /*if (idDips != null && OutboundService.mSocket != null && idDipsNew != idDips) {
                            OutboundService.leaveOutbound(idDips);
                        }*/
                        String accessToken = dataToken.getString("accessToken");

                        sessions.saveIdDips(idDipsNew);
                        sessions.saveIsCust(isCust);
                        sessions.saveAuthToken(accessToken);

                        idDips = idDipsNew;

                        getIntent().getExtras().clear();
                        getIntent().removeExtra("RESULT_IMAGE_AI");

                        initialWaitingRoom();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else {
                    if (response.code() < 500) {
                        String dataErr = null;
                        try {
                            dataErr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e("CEK", "dataErr : " + dataErr);
                        if (dataErr != null) {
                            try {
                                JSONObject dataObj = new JSONObject(dataErr);
                                if (dataObj.has("message")) {
                                    String message = dataObj.getString("message");
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.msg_error, Toast.LENGTH_SHORT).show();
                        }
                        //OutApps();
                    } else {
                        Toast.makeText(mContext, R.string.msg_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                OutApps();
            }
        });
    }

    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private void processCaptureIdentify(String imgBase64) {
        if (!NetworkUtil.hasDataNetwork(mContext)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        JsonCaptureIdentify jsons = new JsonCaptureIdentify();
        jsons.setIdDips(idDips);
        jsons.setImage(imgBase64);

        Log.e("CEK","processCaptureIdentify REQUEST idDips : "+idDips);

        ApiService API = Server.getAPIService();
        //Call<CaptureIdentify> call = API.CaptureAdvanceAI(jsons);
        Call<CaptureIdentify> call = API.CaptureIdentify(jsons);
        Log.e("CEK","processCaptureIdentify URL CALL : "+call.request().url());
        call.enqueue(new Callback<CaptureIdentify>() {
            @Override
            public void onResponse(Call<CaptureIdentify> call, Response<CaptureIdentify> response) {
                Log.e("CEK","processCaptureIdentify RESPONSE CODE: "+response.code());
                if (response.isSuccessful() && response.body() != null) {
                    int errCode = response.body().getErr_code();
                    if (errCode == 0) {
                        SweetAlertDialog sweetDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
                        sweetDialog.setTitleText("Warning!!!");
                        sweetDialog.setContentText(getResources().getString(R.string.not_using_dips));
                        sweetDialog.setConfirmText("OK");
                        sweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetDialog.dismissWithAnimation();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                            }
                        });
                        sweetDialog.show();
                        return;
                    }

                    isCust = response.body().isCustomer();
                    custName = response.body().getName();
                    String idDipsNew = response.body().getIdDips();
                    NameSession = response.body().getDataSession().getNameSession();
                    //SessionPass = response.body().getDataSession().getPass();

                    /*if (idDips != null && OutboundService.mSocket != null && idDipsNew != idDips) {
                        OutboundService.leaveOutbound(idDips);
                    }*/

                    sessions.saveIdDips(idDipsNew);
                    sessions.saveIsCust(isCust);

                    getIntent().getExtras().clear();
                    getIntent().removeExtra("RESULT_IMAGE_AI");

                    initialWaitingRoom();
                } else {
                    Toast.makeText(mContext, R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CaptureIdentify> call, Throwable t) {
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupConnectionFactory() {
        String uriRabbit = Server.BASE_URL_RABBITMQ;
        try {
            connectionFactory.setAutomaticRecoveryEnabled(false);
            connectionFactory.setUri(uriRabbit);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private JSONObject dataGetTicket() {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject custObj = new JSONObject();
        try {
            custObj.put("custId",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("from","Cust");
            jsObj.put("to","QS");
            jsObj.put("created",unixTime);
            jsObj.put("transaction",custObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj;
    }

    private JSONObject reqQSTicket() {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject custObj = new JSONObject();
        try {
            custObj.put("custId",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("from","Cust");
            jsObj.put("to","QS");
            jsObj.put("created",unixTime);
            jsObj.put("transaction",custObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj;
    }

    private JSONObject reqAcceptCall() {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject custObj = new JSONObject();
        try {
            custObj.put("status","ack");
            custObj.put("custId",idDips);
            custObj.put("msg","OK");
            custObj.put("ticket",myTicketNumber);
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

    private void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = connectionFactory.newConnection();
                    Channel ch = connection.createChannel();
                    ch.confirmSelect();

                    JSONObject dataTicketObj = dataGetTicket();
                    String dataTicket = dataTicketObj.toString();

                    //ch.queueDeclare("dips.queue.service.req.ticket",true,false,false,null);
                    ch.basicPublish("","dips.queue.service.req.ticket",false,null,dataTicket.getBytes());
                    ch.waitForConfirmsOrDie();

                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e("CEK", "publishToAMQP Connection broken: " + e.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        publishToAMQP();
                    } catch (InterruptedException e1) {

                    }
                }
            }
        });
        publishThread.start();
    }

    private void publishQSReqTicket() {
        publishQSTicketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = connectionFactory.newConnection();
                    Channel ch = connection.createChannel();
                    ch.confirmSelect();

                    JSONObject dataTicketObj = reqQSTicket();
                    String dataTicket = dataTicketObj.toString();

                    //ch.queueDeclare("dips.queue.qs.req.ticket",true,false,false,null);
                    ch.basicPublish("","dips.queue.qs.req.ticket",false,null,dataTicket.getBytes());
                    ch.waitForConfirmsOrDie();

                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e("CEK", "publishQSReqTicket Connection broken: " + e.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        publishQSReqTicket();
                    } catch (InterruptedException e1) {

                    }
                }
            }
        });
        publishQSTicketThread.start();
    }

    void subscribe()
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    AMQP.Queue.DeclareOk q = channel.queueDeclare();
                    channel.exchangeDeclare("dips361-cust-ticket", "direct", true);
                    channel.queueBind(q.getQueue(), "dips361-cust-ticket", "dips.direct.cust."+idDips+".ticket");
                    channel.basicConsume(q.getQueue(), true, new DeliverCallback() {
                        @Override
                        public void handle(String consumerTag, Delivery message) throws IOException {
                            String getMessage = new String(message.getBody());
                            Log.e("CEK","Success subscribeThread getMessage : "+getMessage);
                            try {
                                JSONObject dataObj = new JSONObject(getMessage);
                                String getTicket = dataObj.getJSONObject("transaction").getString("ticket");
                                Log.e("CEK","subscribeThread getTicket : "+getTicket);
                                int myTicketInt = Integer.parseInt(getTicket);
                                Log.e("CEK","subscribeThread myTicketInt : "+myTicketInt);
                                myTicketNumber = String.format("%03d", myTicketInt).toString();
                                Log.e("CEK","subscribeThread myTicketNumber : "+myTicketNumber);
                                String myticketContent = "A" + myTicketNumber;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myTicket.setText(myticketContent);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                            Log.e("CEK","subscribeThread consumerTag : "+consumerTag);
                        }
                    });
                    publishToAMQP(); //RabbitMQ
                } catch (Exception e1) {
                    Log.e("CEK", "subscribe Connection broken: " + e1.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        subscribe();
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        subscribeThread.start();
    }

    void subscribeReqTicket()
    {
        subscribeReqTicketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("CEK","MASUK subscribeReqTicket");
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    AMQP.Queue.DeclareOk q = channel.queueDeclare();
                    channel.exchangeDeclare("dips361-cust-req-ticket", "direct", true);
                    channel.queueBind(q.getQueue(), "dips361-cust-req-ticket", "dips.direct.cust."+idDips+".req.ticket");
                    channel.basicConsume(q.getQueue(), true, new DeliverCallback() {
                        @Override
                        public void handle(String consumerTag, Delivery message) throws IOException {
                            String getMessage = new String(message.getBody());
                            Log.e("CEK","Success subscribeReqTicket getMessage : "+getMessage);
                            try {
                                JSONObject dataObj = new JSONObject(getMessage);
                                String ticketLast = dataObj.getJSONObject("transaction").getString("ticket");
                                int ticketLastInt = Integer.parseInt(ticketLast);
                                Log.e("CEK", "subscribeReqTicket ticketLast : " +ticketLast);
                                String lastQueue = String.format("%03d", ticketLastInt).toString();
                                Log.e("CEK", "subscribeReqTicket lastQueue : " +lastQueue);
                                String lastTicketContent = "A" + lastQueue;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lastTicket.setText(lastTicketContent);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            subscribeCall(); //RabbitMQ
                            subscribe(); //RabbitMQ
                        }
                    }, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                            Log.e("CEK","subscribeReqTicket consumerTag : "+consumerTag);
                        }
                    });

                    publishQSReqTicket();
                } catch (Exception e1) {
                    Log.e("CEK", "subscribeReqTicket Connection broken: " + e1.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        subscribeReqTicket();
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        subscribeReqTicketThread.start();
    }

    void subscribeCall()
    {
        subscribeThreadCall = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("CEK","MASUK subscribeCall");
                    Connection connection = connectionFactory.newConnection();
                    channelCall = connection.createChannel();
                    channelCall.basicQos(1);
                    AMQP.Queue.DeclareOk q = channelCall.queueDeclare();
                    Log.e("CEK","subscribeCall getQueue : "+q.getQueue());
                    channelCall.exchangeDeclare("dips361-cust-call", "direct", true);
                    channelCall.queueBind(q.getQueue(), "dips361-cust-call", "dips.direct.cust."+idDips+".call");
                    Log.e("CEK","AFTER subscribeCall queueBind getChannelNumber : "+channelCall.getChannelNumber());
                    channelCall.basicConsume(q.getQueue(), true, new DeliverCallback() {
                        @Override
                        public void handle(String consumerTag, Delivery message) throws IOException {
                            String getMessage = new String(message.getBody());
                            Log.e("CEK","Success subscribeCall getMessage : "+getMessage);
                            try {
                                JSONObject dataObj = new JSONObject(getMessage);
                                int getTicket = dataObj.getJSONObject("transaction").getInt("ticket");
                                Log.e("CEK","subscribeCall getTicket : "+getTicket);
                                String getQueue = String.format("%03d", getTicket);
                                String lastTicketContent = "A" + getQueue;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lastTicket.setText(lastTicketContent);
                                    }
                                });
                                Log.e("CEK","subscribeCall getQueue : "+getQueue+" | myTicketNumber : "+myTicketNumber);
                                if (getQueue.equals(myTicketNumber)) {
                                    Log.e("CEK","subscribeCall MASUK IF");
                                    String csId = dataObj.getJSONObject("transaction").getString("csId");
                                    String password = dataObj.getJSONObject("transaction").getString("password");
                                    Log.e("CEK","subscribeCall csId : "+csId);
                                    Log.e("CEK","subscribeCall password : "+password);

                                    NameSession = idDips;
                                    SessionPass = password;
                                    sessions.saveCSID(csId);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {
                                                try {
                                                    PopUpSucces(csId);
                                                } catch (WindowManager.BadTokenException e) {
                                                    Log.e("WindowManagerBad ", e.toString());
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("CEK","subscribeCall MASUK ELSE WAITING");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PopUpWaiting();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                            Log.e("CEK","subscribeCall consumerTag : "+consumerTag);
                        }
                    });

                } catch (Exception e1) {
                    Log.e("CEK", "subscribeCall Connection broken: " + e1.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        subscribeCall();
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        subscribeThreadCall.start();
    }

    void subscribeAllTicketInfo()
    {
        subscribeAllTicketInfoCall = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.basicQos(1);
                    AMQP.Queue.DeclareOk q = channel.queueDeclare();
                    channel.exchangeDeclare("", "broadcast", true);
                    channel.queueBind(q.getQueue(), "", "dips.broadcast.all.ticket.info");
                    channel.basicConsume(q.getQueue(), true, new DeliverCallback() {
                        @Override
                        public void handle(String consumerTag, Delivery message) throws IOException {
                            String getMessage = new String(message.getBody());
                            Log.e("CEK","Success subscribeAllTicketInfo getMessage : "+getMessage);
                            Log.e("CEK","Success subscribeAllTicketInfo consumerTag : "+consumerTag);
                        }
                    }, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                            Log.e("CEK","subscribeAllTicketInfo consumerTag : "+consumerTag);
                        }
                    });

                } catch (Exception e1) {
                    Log.e("CEK", "subscribeAllTicketInfo Connection broken: " + e1.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                        subscribeAllTicketInfo();
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        subscribeAllTicketInfoCall.start();
    }

    private void publishCallAccept(String csId) {
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
                    Log.e("CEK", "publishCallAccept Connection broken: " + e.getClass().getName());
                    try {
                        Thread.sleep(4000); //sleep and then try again
                    } catch (InterruptedException e1) {

                    }
                }
            }
        });
        publishCallAcceptThread.start();
    }

    private void AnimationCall(){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            int count = 0;

            @Override
            public void run() {
                count++;

                if (count == 1)
                {
                    AnimationCall.setText(getResources().getString(R.string.call_is_being_connected));
                }
                else if (count == 2)
                {
                    AnimationCall.setText(getResources().getString(R.string.call_is_being_connected1));
                }
                else if (count == 3)
                {
                    AnimationCall.setText(getResources().getString(R.string.call_is_being_connected2));
                }
                else if (count == 4)
                {
                    AnimationCall.setText(getResources().getString(R.string.call_is_being_connected3));
                }

                if (count == 4)
                    count = 0;

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    /*@Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            this.moveTaskToBack(true);
            stopPopSuccess = true;
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,"Tekan sekali lagi untuk minimize", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }*/

    private void previewHolder(){
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initializeSdk() {
        Log.e("CEK","initializeSdk");
        ZoomVideoSDKInitParams params = new ZoomVideoSDKInitParams();
        params.domain = AuthConstants.WEB_DOMAIN; // Required
        params.enableLog = true; // Optional for debugging
        params.videoRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;
        params.audioRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;
        params.shareRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;

        int initResult = ZoomVideoSDK.getInstance().initialize(DipsWaitingRoom.this, params);
        if (initResult != ZoomVideoSDKErrors.Errors_Success) {
            Toast.makeText(this, ErrorMsgUtil.getMsgByErrorCode(initResult), Toast.LENGTH_LONG).show();
        }

    }

    private void savedAuthCredentialIDDiPS(String idDips) {
        try {
            JSONObject dataObj = new JSONObject();
            dataObj.put("idDips",idDips);

            String dataAuth = dataObj.toString();

            String filename = "Auth_Credential.json";
            try {
                createTemporaryFile(dataAuth,filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private File createDir() {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), IMAGE_DIRECTORY_NAME);

        if (mediaStorageDir.exists()) {
            try {
                mediaStorageDir.getCanonicalFile().delete();
                if (mediaStorageDir.exists()) {
                    getApplicationContext().deleteFile(mediaStorageDir.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaStorageDir;
    }

    private File createTemporaryFile(String dataAuth, String filename) throws Exception {
        File mediaStorageDir = createDir();

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        FileWriter writer = new FileWriter(mediaFile);
        writer.append(dataAuth);
        writer.flush();
        writer.close();

        return mediaFile;
    }

    private Emitter.Listener waitingListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONArray dataArr = new JSONArray(args);
                Log.d("CEK","dataArr : "+dataArr);
                //Status
                int statusCode = dataArr.getInt(0);
                //Nama Sesi
                String Session_name = dataArr.getString(1);
                //Antrian Terakhir
                String lastQueue = dataArr.getString(2);
                //Password Session
                String Session_password = dataArr.getString(3);
                //Username Agent
                //String Username_agent = dataArr.getString(4);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (statusCode == 0) {
                            lastTicket.setText("A"+lastQueue.substring(lastQueue.length()-3,lastQueue.length()));
                            NameSession = Session_name;
                            SessionPass = Session_password;
                            PopUpSucces(null);
                        } else {
                              if (lastQueue.trim().equals(myTicketNumber) && Session_name.trim().equals(idDips) ){
                                lastTicket.setText("A"+lastQueue.substring(lastQueue.length()-3,lastQueue.length()));
                                NameSession = Session_name;
                                SessionPass = Session_password;
                                PopUpSucces(null);
                            }
                            else{
                                //Ambil Data Antrian Terakhir terbaru dari Socket
                                String NEWQUEUE = lastQueue.substring(lastQueue.length()-3,lastQueue.length());

                                //Ambil Antrian Terakhir Yang Ter set Pada Apps Saat ini
                                String[] CutLASTQUEUE = lastTicket.getText().toString().split("A");
                                String RECENTQUEUE = CutLASTQUEUE[1];

                                //Validasi apabila Antrian Terbaru (NEWQUEUE) > Antrian yang Ter set (RECENTQUEUE) maka TextView Update
                                if (Integer.valueOf(NEWQUEUE) > Integer.valueOf(RECENTQUEUE)){
                                    lastTicket.setText("A"+lastQueue.substring(lastQueue.length()-3,lastQueue.length()));
                                }
                                PopUpWaiting();
                            }
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (manager == null) {
                    Log.i("CEK", "camera manager is null");
                    return;
                }
                try {
                    for (String id: manager.getCameraIdList()) {
                        CAM_ID = Integer.valueOf(id);
                        setCameraDisplayOrientation();
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                Camera.Size size = getOptimalPreviewSize(sizes, width, height);

                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }
    
    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void PopUpWaiting(){
        if (dialogWaiting == null) {
            dialogWaiting = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.WARNING_TYPE);
            dialogWaiting.setContentText(getResources().getString(R.string.headline_waiting));
            dialogWaiting.setConfirmText(getResources().getString(R.string.waiting));
            dialogWaiting.setCancelable(false);
            dialogWaiting.show();
            dialogWaiting.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    dialogWaiting = null;
                }
            });
            Button btnConfirm = (Button) dialogWaiting.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
            btnConfirm.setBackgroundTintList(DipsWaitingRoom.this.getResources().getColorStateList(R.color.Blue));
        }
    }

    private void PopUpSucces(String csId){
        if (dialogSuccess == null) {
            Log.e("CEK","dialogSuccess");
            dialogSuccess = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.SUCCESS_TYPE);
        }
        dialogSuccess.setContentText(getResources().getString(R.string.headline_success));
        dialogSuccess.setConfirmText(getResources().getString(R.string.btn_continue));
        dialogSuccess.setCancelText(getResources().getString(R.string.cancel));
        dialogSuccess.setCancelable(false);
        dialogSuccess.show();
        Button btnConfirm = (Button) dialogSuccess.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        Button btnCancel = (Button) dialogSuccess.findViewById(cn.pedant.SweetAlert.R.id.cancel_button);
        btnConfirm.setBackgroundTintList(DipsWaitingRoom.this.getResources().getColorStateList(R.color.Blue));
        btnCancel.setBackgroundTintList(DipsWaitingRoom.this.getResources().getColorStateList(R.color.button_end_call));
        dialogSuccess.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (!SessionPass.isEmpty()) {
                    sweetAlertDialog.dismissWithAnimation();
                    sweetAlertDialog.cancel();
                    dialogSuccess = null;
                    publishCallAccept(csId); //RabbitMQ
                    processJoinVideo();
                    //Popup();
                } else {
                    Toast.makeText(mContext,"Password Conference belum ada",Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogSuccess.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                sweetAlertDialog.cancel();
                EndCall();
                dialogSuccess = null;
            }
        });
    }

    private void EndCall(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getString(R.string.headline_close_conversation));
        sweetAlertDialog.showCancelButton(false);
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.Blue));
    }

    private void Popup(){
        if (dialogConfirm == null) {
            dialogConfirm = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        }
        dialogConfirm.setContentText(getResources().getString(R.string.content_input));
        dialogConfirm.setConfirmText(getResources().getString(R.string.btn_continue));
        dialogConfirm.show();
        dialogConfirm.setCancelable(false);
        Button btnConfirm = (Button) dialogConfirm.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.Blue));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm.dismiss();
                dialogConfirm.cancel();
                processJoinVideo();
            }
        });
    }

    public void setCameraDisplayOrientation(){
        if (camera == null)
        {
            Log.d("CEK","setCameraDisplayOrientation - camera null");
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CAM_ID, info);

        WindowManager winManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
            degreeFront = result;
        } else {  // back-facing
            //result = (info.orientation - degrees + 360) % 360;
            result = 180;
        }
        camera.setDisplayOrientation(result);
    }
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            initPreview(width, height);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };
    protected boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_VIDEO_AUDIO_CODE);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_VIDEO_AUDIO_CODE) {
            if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }
    protected void onPermissionGranted() {
        processJoinVideo();
    }
    private void processGetTicket(TextView my_Ticket){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Ticket(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        String idDips = jsObj.getString("idDips");
                        String queueID = jsObj.getString("queueID").toString();
                        String lastQueueID = jsObj.getString("lastQueueID");
                        myTicketNumber = queueID;
                        my_Ticket.setText("A"+queueID.substring(queueID.length()-3,queueID.length()));
                        lastTicket.setText("A"+lastQueueID.substring(lastQueueID.length()-3,lastQueueID.length()));

                        JSONObject object = new JSONObject();
                        try {
                            object.put("room", queueID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("call","join",object);

                        Log.d("CEK DATA","idDips : "+idDips+"\n queueID : "+queueID+"\n lastquueID : "+lastQueueID);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(OutboundServiceNew.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void processJoinVideo() {
        if (!requestPermission())
            return;
        if (!NetworkUtil.hasDataNetwork(this)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        if (null == ZoomVideoSDK.getInstance()) {
            Toast.makeText(this, "Please initialize SDK", Toast.LENGTH_LONG).show();
            return;
        }

        processSignature();
    }
    private void processSignature() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("sessionName",NameSession);
            jsons.put("role",0);
            jsons.put("sessionKey",SessionPass);
            jsons.put("userIdentity", custName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","processSignature REQ : "+jsons.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Signature(requestBody);
        Log.e("CEK","Signature URL : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","Signature RESPONSE "+response.code());
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    Log.e("CEK","Signature dataS : "+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        String signatures = "";
                        if (jsObj.has("data")) {
                            JSONObject dataSign = jsObj.getJSONObject("data");
                            if (dataSign.has("signature")) {
                                if (!dataSign.isNull("signature")) {
                                    signatures = dataSign.getString("signature");
                                    processCreateVideo(signatures);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,"Terjadi Kesalahan. Silakan dicoba kembali",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void processCreateVideo(String signatures) {
        JWT jwt = new JWT(signatures);
        Map<String, Claim> allClaims = jwt.getClaims();
        String name = allClaims.get("user_identity").asString();
        String sessionName = allClaims.get("tpc").asString();
        String sessionPassKey = allClaims.get("session_key").asString();
        Log.e("CEK","processCreateVideo sessionPassKey : "+sessionPassKey);

        ZoomVideoSDKSessionContext sessionContext = new ZoomVideoSDKSessionContext();

        ZoomVideoSDKAudioOption audioOption = new ZoomVideoSDKAudioOption();
        audioOption.connect = true;
        audioOption.mute = false;
        sessionContext.audioOption = audioOption;

        ZoomVideoSDKVideoOption videoOption = new ZoomVideoSDKVideoOption();
        videoOption.localVideoOn = true;
        sessionContext.videoOption = videoOption;

        sessionContext.sessionName = sessionName;
        sessionContext.userName = name;
        sessionContext.token = signatures;
        //Optional
        sessionContext.sessionPassword = sessionPassKey;

        ZoomVideoSDKSession session = ZoomVideoSDK.getInstance().joinSession(sessionContext);

        if(null==session){
            return;
        }

        sessions.saveIsSwafoto(isSwafoto);
        sessions.saveIsCust(isCust);

        Intent intent = new Intent(this, DipsVideoConfren.class);
        intent.putExtra("name", name);
        intent.putExtra("password", sessionPassKey);
        intent.putExtra("sessionName", sessionName);
        intent.putExtra("render_type", renderType);
        startActivity(intent);
        finish();
    }
    private boolean getFragmentPage(Fragment fragment){
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("ISCUST", isCust);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

}