package com.evo.mitzoom.ui;

import static com.evo.mitzoom.Helper.MyWorker.EXTRA_START;
import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Constants.AuthConstants;
import com.evo.mitzoom.Fragments.frag_berita;
import com.evo.mitzoom.Helper.MyWorker;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.util.ErrorMsgUtil;
import com.evo.mitzoom.util.NetworkUtil;
import com.evo.mitzoom.view.CircularSurfaceView;
import com.google.gson.JsonObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
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

public class DipsWaitingRoom extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "CEK_DipsWaitingRoom";
    private Context mContext;
    protected final static int REQUEST_VIDEO_AUDIO_CODE = 1010;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    protected int renderType = BaseMeetingActivity.RENDER_TYPE_ZOOMRENDERER;
    private static final Integer[] img = {R.drawable.adsv1, R.drawable.adsv2, R.drawable.adsv3};
    public static int CAM_ID = 0;
    private static final String KEY_USE_FACING = "use_facing";
    public static Integer useFacing = null;
    private Camera camera = null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private CircularSurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private static int degreeFront = 0;
    private LayoutInflater inflater;
    private ImageView btnclose;
    private TextView AnimationCall;
    static String NameSession;
    private static String idDips;
    String SessionPass;
    private static String myTicketNumber;
    boolean isCust;
    String custName;
    private final boolean doubleBackToExitPressedOnce = false;
    private SessionManager sessions;
    private TextView myTicket, lastTicket;
    private SweetAlertDialog dialogConfirm;
    private DisplayMetrics displayMetrics;
    private int Savewaktu;
    //RabitMQ
    private static final ConnectionFactory connectionFactory = new ConnectionFactory();
    public static Connection connection = null;
    public static Channel channelSubscribeReqTicket = null;
    public static Channel channelSubscribe = null;
    public static Channel channelCall = null;
    private Thread subscribeThread;
    private Thread subscribeReqTicketThread;
    public static Thread subscribeThreadCall;
    private Thread publishThread;
    private Thread publishQSTicketThread;
    private static Thread publishCallAcceptThread;
    private final boolean isSwafoto = false;

    ArrayList<String> time = new ArrayList<>();
    List<Integer> periodeInt = new ArrayList<>();
    HashMap<Integer,String> dataPeriode = new HashMap<>();
    HashMap<String,Integer> dataPeriodeId = new HashMap<>();
    private int year;
    private int month;
    private int day;
    private final int waktu_tunggu = 6000;
    private String tanggal, waktu;
    private String Savetanggal;
    private List<Integer> indeksNotFound;
    private DatePickerDialog dpd;
    private EditText et_Date;
    private Spinner et_time;
    private JSONArray tanggalPenuh;
    private JSONArray periodePenuh;
    private Button btnSchedule,btnSchedule2, btnEndCall;
    public static RelativeLayout rlprogress;
    private boolean isConfigure;
    private boolean flagShowJoin = false;
    private WorkManager workManager = null;
    private int timesWaiting = 0;
    private int countWaiting = 0;
    private int loopWaiting = 0;
    private Thread popupWaitingThread;
    private boolean startWaiting = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        setLocale(this,lang);
        //LocaleHelper.setLocale(this,lang);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sessions.saveRTGS(null);
        sessions.saveCSID(null);
        idDips = sessions.getKEY_IdDips();
        isCust = sessions.getKEY_iSCust();

        setContentView(R.layout.activity_dips_waiting_room);

        myTicket = findViewById(R.id.myticket);
        lastTicket = findViewById(R.id.last_ticket);
        AnimationCall = findViewById(R.id.AnimationCall);
        CardView cardSurf = findViewById(R.id.cardSurf);
        preview = findViewById(R.id.mySurface);
        rlprogress = findViewById(R.id.rlprogress);
        btnSchedule = (Button) findViewById(R.id.btnSchedule);
        btnEndCall = (Button) findViewById(R.id.end_call);

        /*displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDisp = displayMetrics.widthPixels;
        int dyWidth = (int) Math.ceil(widthDisp / 2);*/

        previewHolder();

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);

        /*ViewGroup.LayoutParams lp = cardSurf.getLayoutParams();
        lp.width = dyWidth;
        cardSurf.setLayoutParams(lp);*/

        sessions.saveScheduledDate(null);
        sessions.saveScheduledTime(null);
        new AsyncProcess().execute();

        initializeSdk();
        AnimationCall();
        setupConnectionFactory(); //RabbitMQ

        custName = getIntent().getExtras().getString("CUSTNAME");

        initialWaitingRoom();

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time.size() > 0) {
                    PopUpSchedule();
                } else {
                    Toast.makeText(mContext,getString(R.string.please_wait),Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndCall();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG,"MASUK onResume inPreview : "+inPreview+" | camera : "+camera);

        isConfigure = false;

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            requestPermissionWrite();
        }
    }

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
        Log.e(TAG,"MASUK ONPAUSE");
        if (inPreview) {
            camera.stopPreview();
        }

        if (camera != null) {
            camera.release();
            camera = null;
            inPreview = false;
            cameraConfigured = false;
        }

        Log.e(TAG,"inPreview : "+inPreview+" | camera : "+camera);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
        if (popupWaitingThread != null) {
            popupWaitingThread.interrupt();
        }

        /*if (connection != null) {
            try {
                if (channelSubscribeReqTicket != null) {
                    channelSubscribeReqTicket.close();
                }
                if (channelSubscribe != null) {
                    channelSubscribe.close();
                }
                if (channelCall != null) {
                    channelCall.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }*/
    }

    private void requestPermissionWrite() {
        Log.e(TAG,"MASUK requestPermissionWrite");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},  REQUEST_WRITE_PERMISSION);
            } else {
                if (camera == null) {
                    camera = Camera.open(useFacing);
                    startPreview();
                }
            }
        } else {
            if (camera == null) {
                camera = Camera.open(useFacing);
                startPreview();
            }
        }

    }

    private void initialWaitingRoom() {
        Log.d(TAG, "idDips : "+idDips);
        subscribeReqTicket();
        funcThreadPopupWaiting();
    }

    private void funcThreadPopupWaiting() {
        popupWaitingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"timesWaiting : "+timesWaiting);
                Log.e(TAG,"countWaiting : "+countWaiting);
                while (startWaiting) {
                    Log.e(TAG,"timesWaiting LOOP : "+timesWaiting);
                    Log.e(TAG,"countWaiting LOOP : "+countWaiting);
                    if (timesWaiting > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (countWaiting > loopWaiting) {
                                            PopUpWaiting();
                                            loopWaiting++;
                                        }
                                    }
                                });
                            }
                        },timesWaiting);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        popupWaitingThread.start();
    }

    private void serviceOutbound() {
        Intent serviceIntent = new Intent(this, OutboundServiceNew.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    public static void showProgress(Boolean bool){

        if (bool){
            rlprogress.setVisibility(View.VISIBLE);
        }else {
            rlprogress.setVisibility(View.GONE);
        }
    }

    private void OutApps(){
        if (connection != null) {
            try {
                if (channelSubscribeReqTicket != null) {
                    if (channelSubscribeReqTicket.isOpen()) {
                        channelSubscribeReqTicket.close();
                    }
                }
                if (channelSubscribe != null) {
                    if (channelSubscribe.isOpen()) {
                        channelSubscribe.close();
                    }
                }
                if (channelCall != null) {
                    if (channelCall.isOpen()) {
                        channelCall.close();
                    }
                }
                if (connection != null) {
                    if (connection.isOpen()) {
                        connection.close();
                    }
                }
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private void setupConnectionFactory() {
        String uriRabbit = Server.BASE_URL_RABBITMQ;
        try {
            connectionFactory.setAutomaticRecoveryEnabled(true);
            connectionFactory.setUri(uriRabbit);
            connection = connectionFactory.newConnection();
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
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

    private static JSONObject reqAcceptCall(String labelAction) {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject custObj = new JSONObject();
        try {
            custObj.put("status","ack");
            custObj.put("action",labelAction);
            custObj.put("custId",NameSession);
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
        if (connection != null) {
            publishThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        JSONObject dataTicketObj = dataGetTicket();
                        String dataTicket = dataTicketObj.toString();

                        //ch.queueDeclare("dips.queue.service.req.ticket",true,false,false,null);
                        ch.basicPublish("", "dips.queue.service.req.ticket", false, null, dataTicket.getBytes());
                        ch.waitForConfirmsOrDie();

                    } catch (IOException | InterruptedException e) {
                        Log.e(TAG, "publishToAMQP Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(4000); //sleep and then try again
                            publishToAMQP();
                        } catch (InterruptedException ignored) {

                        }
                    }
                }
            });
            publishThread.start();
        }
    }

    private void publishQSReqTicket() {
        if (connection != null) {
            publishQSTicketThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        JSONObject dataTicketObj = reqQSTicket();
                        String dataTicket = dataTicketObj.toString();

                        //ch.queueDeclare("dips.queue.qs.req.ticket",true,false,false,null);
                        ch.basicPublish("", "dips.queue.qs.req.ticket", false, null, dataTicket.getBytes());
                        ch.waitForConfirmsOrDie();

                    } catch (IOException | InterruptedException e) {
                        Log.e(TAG, "publishQSReqTicket Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(4000); //sleep and then try again
                            publishQSReqTicket();
                        } catch (InterruptedException ignored) {

                        }
                    }
                }
            });
            publishQSTicketThread.start();
        }
    }

    void subscribe() {
        if (connection != null) {
            subscribeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        channelSubscribe = connection.createChannel();
                        channelSubscribe.basicQos(1);
                        AMQP.Queue.DeclareOk q = channelSubscribe.queueDeclare();
                        channelSubscribe.exchangeDeclare("dips361-cust-ticket", "direct", true);
                        channelSubscribe.queueBind(q.getQueue(), "dips361-cust-ticket", "dips.direct.cust." + idDips + ".ticket");
                        channelSubscribe.basicConsume(q.getQueue(), true, new DeliverCallback() {
                            @Override
                            public void handle(String consumerTag, Delivery message) throws IOException {
                                String getMessage = new String(message.getBody());
                                Log.e(TAG, "Success subscribeThread getMessage : " + getMessage);
                                try {
                                    JSONObject dataObj = new JSONObject(getMessage);
                                    String getTicket = dataObj.getJSONObject("transaction").getString("ticket");
                                    Log.e(TAG, "subscribeThread getTicket : " + getTicket);
                                    int myTicketInt = Integer.parseInt(getTicket);
                                    Log.e(TAG, "subscribeThread myTicketInt : " + myTicketInt);
                                    myTicketNumber = String.format("%03d", myTicketInt);
                                    Log.e(TAG, "subscribeThread myTicketNumber : " + myTicketNumber);
                                    String myticketContent = myTicketNumber;
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
                                Log.e(TAG, "subscribeThread consumerTag : " + consumerTag);
                            }
                        });
                        publishToAMQP(); //RabbitMQ
                    } catch (Exception e1) {
                        Log.e(TAG, "subscribe Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(4000); //sleep and then try again
                            subscribe();
                            //getNewMyTicketsV2();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
            subscribeThread.start();
        }
    }

   /* private void getNewMyTicketsV2(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("custId",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.getTicketV2(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String ticket = dataObj.getString("ticket");
                        Log.d("Ticket",""+ticket);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }*/

    void subscribeReqTicket() {
        if (connection != null) {
            subscribeReqTicketThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e(TAG, "MASUK subscribeReqTicket");
                        channelSubscribeReqTicket = connection.createChannel();
                        channelSubscribeReqTicket.basicQos(1);
                        AMQP.Queue.DeclareOk q = channelSubscribeReqTicket.queueDeclare();
                        channelSubscribeReqTicket.exchangeDeclare("dips361-cust-req-ticket", "direct", true);
                        channelSubscribeReqTicket.queueBind(q.getQueue(), "dips361-cust-req-ticket", "dips.direct.cust." + idDips + ".req.ticket");
                        channelSubscribeReqTicket.basicConsume(q.getQueue(), true, new DeliverCallback() {
                            @Override
                            public void handle(String consumerTag, Delivery message) throws IOException {
                                String getMessage = new String(message.getBody());
                                Log.e(TAG, "Success subscribeReqTicket getMessage : " + getMessage);
                                try {
                                    JSONObject dataObj = new JSONObject(getMessage);
                                    String ticketLast = dataObj.getJSONObject("transaction").getString("ticket");
                                    int ticketLastInt = Integer.parseInt(ticketLast);
                                    Log.e(TAG, "subscribeReqTicket ticketLast : " + ticketLast);
                                    String lastQueue = String.format("%03d", ticketLastInt);
                                    Log.e(TAG, "subscribeReqTicket lastQueue : " + lastQueue);
                                    String lastTicketContent = lastQueue;
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
                                //getNewMyTicketsV2();
                            }
                        }, new CancelCallback() {
                            @Override
                            public void handle(String consumerTag) throws IOException {
                                Log.e(TAG, "subscribeReqTicket consumerTag : " + consumerTag);
                            }
                        });

                        publishQSReqTicket();
                    } catch (Exception e1) {
                        Log.e(TAG, "subscribeReqTicket Connection broken: " + e1.getClass().getName());
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
    }

    void subscribeCall() {
        if (connection != null) {
            subscribeThreadCall = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e(TAG, "MASUK subscribeCall");
                        ;
                        channelCall = connection.createChannel();
                        channelCall.basicQos(1);
                        AMQP.Queue.DeclareOk q = channelCall.queueDeclare();
                        Log.e(TAG, "subscribeCall getQueue : " + q.getQueue());
                        channelCall.exchangeDeclare("dips361-cust-call", "direct", true);
                        channelCall.queueBind(q.getQueue(), "dips361-cust-call", "dips.direct.cust." + idDips + ".call");
                        Log.e(TAG, "AFTER subscribeCall queueBind getChannelNumber : " + channelCall.getChannelNumber());
                        channelCall.basicConsume(q.getQueue(), true, new DeliverCallback() {
                            @Override
                            public void handle(String consumerTag, Delivery message) throws IOException {
                                String getMessage = new String(message.getBody());
                                Log.e(TAG, "Success subscribeCall getMessage : " + getMessage);
                                try {
                                    JSONObject dataObj = new JSONObject(getMessage);
                                    String actionCall = "";
                                    if (dataObj.getJSONObject("transaction").has("action")) {
                                        actionCall = dataObj.getJSONObject("transaction").getString("action");
                                    }
                                    if (actionCall.equals("info")) {
                                        String csId = dataObj.getJSONObject("transaction").getString("csId");
                                        sessions.saveCSID(csId);
                                    } else {
                                        int getTicket = dataObj.getJSONObject("transaction").getInt("ticket");
                                        Log.e(TAG, "subscribeCall getTicket : " + getTicket);
                                        String getQueue = String.format("%03d", getTicket);
                                        String lastTicketContent = getQueue;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                lastTicket.setText(lastTicketContent);
                                            }
                                        });
                                        Log.e(TAG, "subscribeCall getQueue : " + getQueue + " | myTicketNumber : " + myTicketNumber);
                                        if (getQueue.equals(myTicketNumber)) {
                                            Log.e(TAG, "subscribeCall MASUK IF");
                                            String sessionId = idDips;
                                            if (dataObj.getJSONObject("transaction").has("sessionId")) {
                                                sessionId = dataObj.getJSONObject("transaction").getString("sessionId");
                                            }
                                            String csId = dataObj.getJSONObject("transaction").getString("csId");
                                            String password = dataObj.getJSONObject("transaction").getString("password");
                                            Log.e(TAG, "subscribeCall sessionId : " + sessionId);
                                            Log.e(TAG, "subscribeCall csId : " + csId);
                                            Log.e(TAG, "subscribeCall password : " + password);

                                            NameSession = sessionId;
                                            SessionPass = password;
                                            sessions.saveCSID(csId);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (!isFinishing()) {
                                                        try {
                                                            if (flagShowJoin == false) {
                                                                PopUpSucces(csId);
                                                            }
                                                        } catch (
                                                                WindowManager.BadTokenException e) {
                                                            Log.e("WindowManagerBad ", e.toString());
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.e(TAG, "subscribeCall MASUK ELSE WAITING");
                                            /*runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    PopUpWaiting();
                                                }
                                            });*/
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new CancelCallback() {
                            @Override
                            public void handle(String consumerTag) throws IOException {
                                Log.e(TAG, "subscribeCall consumerTag : " + consumerTag);
                            }
                        });

                    } catch (Exception e1) {
                        Log.e(TAG, "subscribeCall Connection broken: " + e1.getClass().getName());
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
    }

    public static void publishCallAccept(String csId, String labelAction) {
        if (connection != null) {
            publishCallAcceptThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        JSONObject dataTicketObj = reqAcceptCall(labelAction);
                        String dataTicket = dataTicketObj.toString();

                        Log.e(TAG, "publishCallAccept dips.direct.cs." + csId + ".accept.user REQ : " + dataTicket);

                        ch.exchangeDeclare("dips361-cs-accept-user", "direct", true);
                        ch.basicPublish("dips361-cs-accept-user", "dips.direct.cs." + csId + ".accept.user", false, null, dataTicket.getBytes());
                        ch.waitForConfirmsOrDie();

                    } catch (IOException | InterruptedException e) {
                        Log.e(TAG, "publishCallAccept Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(4000); //sleep and then try again
                            publishCallAccept(csId, labelAction);
                        } catch (InterruptedException ignored) {

                        }
                    }
                }
            });
            publishCallAcceptThread.start();
        }
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

    private void previewHolder(){
        previewHolder = preview.getHolder();
        previewHolder.setFormat(PixelFormat.TRANSLUCENT);
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initializeSdk() {
        Log.e(TAG,"initializeSdk");
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

    private void createTemporaryFile(String dataAuth, String filename) throws Exception {
        File mediaStorageDir = createDir();

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        FileWriter writer = new FileWriter(mediaFile);
        writer.append(dataAuth);
        writer.flush();
        writer.close();

    }

    private void initPreview(int width, int height, SurfaceHolder holder) {
        if (camera != null && holder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(holder);
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (manager == null) {
                    Log.i(TAG, "camera manager is null");
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
                isConfigure = true;
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
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            camera.setParameters(parameters);
            inPreview = true;
//            if (isConfigure) {
//                Log.d(TAG,"MASUK isConfigure");
//                try {
//                    Thread.sleep(500);
//                    optimalCamera();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private void optimalCamera() {
        if (camera != null) {
            if (inPreview) {
                camera.stopPreview();
            }
            camera.release();

            if (useFacing != null) {
                if (useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    useFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    useFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                }

                isConfigure = false;
                camera = Camera.open(useFacing);
                startPreview();

                try {
                    camera.setPreviewDisplay(previewHolder);
                    //camera.setDisplayOrientation(90);
                    CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    if (manager == null) {
                        Log.i(TAG, "camera manager is null");
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
                    e.printStackTrace();
                }
            }

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
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        LinearLayout llBtnWaiting = dialogView.findViewById(R.id.llBtnWaiting);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        if (sessions.getLANG().equals("en")) {
            llBtnWaiting.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            llBtnWaiting.setOrientation(LinearLayout.VERTICAL);
        }

        tvTitleDialog.setVisibility(View.GONE);
        btnCancelDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_info));
        tvBodyDialog.setText(getString(R.string.headline_waiting));
        btnCancelDialog.setText(getString(R.string.schedule_a_task));
        btnConfirmDialog.setText(getString(R.string.waiting));

        if (!((Activity) mContext).isFinishing()) {
            SweetAlertDialog dialogWaiting = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.NORMAL_TYPE);
            dialogWaiting.setCustomView(dialogView);
            dialogWaiting.hideConfirmButton();
            dialogWaiting.setCancelable(false);
            dialogWaiting.show();

            btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogWaiting.dismissWithAnimation();
                }
            });
            btnCancelDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogWaiting.dismissWithAnimation();
                    PopUpSchedule();
                }
            });
        }
    }

    private void PopUpSchedule(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_schedule, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        ImageView btnclose = dialogView.findViewById(R.id.btn_close_schedule);
        et_Date = dialogView.findViewById(R.id.et_Date);
        et_time = dialogView.findViewById(R.id.et_time);

        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(mContext,R.layout.list_item, time);

        et_time.setAdapter(adapterTime);
        btnSchedule2 = dialogView.findViewById(R.id.btnSchedule2);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                dpd = null;
                if (dpd == null) {
                    dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(DipsWaitingRoom.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                } else {
                    dpd.initialize(
                            DipsWaitingRoom.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                }

                // restrict to weekdays only
                ArrayList<Calendar> weekdays = new ArrayList<Calendar>();
                Calendar day = Calendar.getInstance();
                int loopAdd = 0;
                for (int i = 0; i < 30; i++) {
                    if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        if (tanggalPenuh.length() > 0) {
                            for (int tg = 0; tg < tanggalPenuh.length(); tg++) {
                                try {
                                    String tglFull = tanggalPenuh.getString(tg);
                                    int yearChk = day.get(Calendar.YEAR);
                                    int monthChk = day.get(Calendar.MONTH);
                                    int dayChk = day.get(Calendar.DAY_OF_MONTH);

                                    int addmonths = (monthChk + 1);
                                    String months = String.valueOf(addmonths);
                                    if (addmonths < 10) {
                                        months = "0"+months;
                                    }
                                    String days = String.valueOf(dayChk);
                                    if (dayChk < 10 ) {
                                        days = "0"+days;
                                    }

                                    String tglChk = yearChk + "-" + months + "-" + days;
                                    if (!tglFull.equals(tglChk)) {
                                        Calendar d = (Calendar) day.clone();
                                        weekdays.add(d);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Calendar d = (Calendar) day.clone();
                            weekdays.add(d);
                        }
                    } else {
                        loopAdd++;
                    }
                    day.add(Calendar.DATE, 1);
                }
                Calendar[] weekdayDays = weekdays.toArray(new Calendar[weekdays.size()]);
                dpd.setSelectableDays(weekdayDays);
                //dpd.setMaxDate(day);

                dpd.setOnCancelListener(dialog -> {
                    Log.e("DatePickerDialog", "Dialog was cancelled");
                    dpd = null;
                });
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");

            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
            }
        });
        btnSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggal = et_Date.getText().toString().trim().trim();
                waktu = et_time.getSelectedItem().toString();
                if (tanggal.trim().equals("")){
                    Toast.makeText(mContext.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else if (waktu.trim().equals("")){
                    Toast.makeText(mContext.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (idDips.isEmpty()) {
                        idDips = sessions.getKEY_IdDips();
                    }
                    //Toast.makeText(context.getApplicationContext(), getResources().getString(R.string.schedule) + tanggal + " & " + getResources().getString(R.string.jam) + waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                    String csId = sessions.getCSID();
                    if (csId != null && !csId.isEmpty()) {
                        publishCallAccept(csId, "cancel"); //RabbitMQ
                    }
                    sessions.saveIDSchedule(0);
                    saveSchedule();
                }

            }
        });
    }

    private void saveSchedule(){
        int periodeId = dataPeriodeId.get(waktu);
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("tanggal",Savetanggal);
            jsons.put("periodeId",periodeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Log.e(TAG,"PARAMS saveSchedule : "+ jsons);
        Log.d("PARAMS JADWAL","idDips = "+idDips+", Tanggal = "+Savetanggal);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.saveSchedule(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG,"saveSchedule Respon Code : "+response.code());
                if (response.isSuccessful() && response.body().size() > 0) {
                    Log.e(TAG,"saveSchedule Respon : "+ response.body());

                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int idSchedule = dataObj.getJSONObject("data").getInt("id");
                        sessions.saveIDSchedule(idSchedule);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*if (channelCall != null) {
                        try {
                            Log.e(TAG,"MASUK channelCall abort close");
                            channelCall.close();
                        } catch (IOException | TimeoutException e) {
                            e.printStackTrace();
                        }
                        if (subscribeThreadCall != null) {
                            Log.e(TAG,"MASUK subscribeThreadCall interrupt");
                            subscribeThreadCall.interrupt();
                        }
                    }*/

                    //doWorkMyWorker();
                    serviceOutbound();
                    PopUpEndSchedule();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void doWorkMyWorker() {
        workManager = WorkManager.getInstance(mContext);

        String jam = waktu;
        Log.e("CEK","doWorkMyWorker Savetanggal : "+Savetanggal);
        Log.e("CEK","doWorkMyWorker jam : "+jam);
        if (jam.indexOf("-") > 0) {
            String[] sp = jam.split("-");
            for (int i = 0; i < sp.length; i++) {
                workManager.cancelUniqueWork(idDips+"_timesStart"+i);
            }

            for (int i = 0; i < sp.length; i++) {
                Calendar currentDate = Calendar.getInstance();

                String[] timeArray = sp[i].split(":");
                String[] spDate = Savetanggal.split("-");
                String thn = spDate[0].toString().trim();
                String getBln = spDate[1].toString().trim();
                int bln = Integer.parseInt(getBln) - 1;
                String tgl = spDate[2].toString().trim();

                Log.e("CEK","DATETIMES-"+i+" : "+Savetanggal+" "+sp[i]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(thn),bln,Integer.parseInt(tgl));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
                calendar.set(Calendar.SECOND, 0);

                long timeCurrentMilis = currentDate.getTimeInMillis();
                long timeInMilis = calendar.getTimeInMillis();
                long timeDiff = timeInMilis - timeCurrentMilis;

                boolean start = true;
                if (i > 0) {
                    start = false;
                }

                Data data = new Data.Builder()
                        .putBoolean(EXTRA_START,start)
                        .build();

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .build();

                workManager.enqueueUniqueWork(idDips,
                        ExistingWorkPolicy.KEEP, (OneTimeWorkRequest) workRequest);
            }
        } else {
            workManager.cancelUniqueWork(idDips);

            Calendar currentDate = Calendar.getInstance();

            String[] timeArray = jam.split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
            calendar.set(Calendar.SECOND, 0);

            long timeCurrentMilis = currentDate.getTimeInMillis();
            long timeInMilis = calendar.getTimeInMillis();
            long timeDiff = timeInMilis - timeCurrentMilis;

            Data data = new Data.Builder()
                    .putBoolean(EXTRA_START,true)
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .setConstraints(constraints)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build();

            workManager.enqueueUniqueWork(idDips,
                    ExistingWorkPolicy.KEEP, (OneTimeWorkRequest) workRequest);
        }
    }

    private void PopUpEndSchedule() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_success));
        tvBodyDialog.setText(getString(R.string.content_after_schedule));
        btnConfirmDialog.setText(getString(R.string.done));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismiss();
                OutApps();
            }
        });
    }

    private void PopUpSucces(String csId){
        flagShowJoin = true;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);
        btnCancelDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_success));
        tvBodyDialog.setText(getString(R.string.headline_success));
        btnCancelDialog.setText(getString(R.string.reject));
        btnConfirmDialog.setText(getString(R.string.btn_continue));

        SweetAlertDialog dialogSuccess = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.NORMAL_TYPE);
        dialogSuccess.setCustomView(dialogView);
        dialogSuccess.setCancelable(false);
        dialogSuccess.hideConfirmButton();
        dialogSuccess.show();

        dialogSuccess.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                flagShowJoin = false;
            }
        });

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SessionPass.isEmpty()) {
                    dialogSuccess.cancel();
                    dialogSuccess.dismissWithAnimation();
                    publishCallAccept(csId,"accept"); //RabbitMQ
                    processJoinVideo();
                    //Popup();
                } else {
                    Toast.makeText(mContext,"Password Conference belum ada",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSuccess.cancel();
                dialogSuccess.dismissWithAnimation();
                publishCallAccept(csId,"cancel"); //RabbitMQ
                EndCallAccept();
            }
        });
    }

    private void EndCallAccept(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_info));
        tvBodyDialog.setText(R.string.caliing_comeback);

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.showCancelButton(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
    }

    private void EndCall(){

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);
        btnCancelDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_info));
        tvBodyDialog.setText(getString(R.string.headline_endcall));
        btnCancelDialog.setText(getString(R.string.no));
        btnConfirmDialog.setText(getString(R.string.end_call));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                //Toast.makeText(context,getResources().getString(R.string.end_call2), Toast.LENGTH_LONG).show();
                OutApps();
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
    }

    public void setCameraDisplayOrientation(){
        if (camera == null)
        {
            Log.d(TAG,"setCameraDisplayOrientation - camera null");
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
            Log.e(TAG,"surfaceChanged width : "+width+" | height : "+height);
            initPreview(width, height, holder);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };
    protected boolean requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_VIDEO_AUDIO_CODE);
                return false;
            }
        } else if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_VIDEO_AUDIO_CODE);
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

        Log.e(TAG,"processSignature REQ : "+ jsons);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Signature(requestBody);
        Log.e(TAG,"Signature URL : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG,"Signature RESPONSE "+response.code());
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    Log.e(TAG,"Signature dataS : "+dataS);
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
        Log.e(TAG,"processCreateVideo sessionPassKey : "+sessionPassKey);

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

    private class AsyncProcess extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ConfigListPortal();
            processGetCheckSchedule();
            processGetScheduleTimes();
            return null;
        }
    }

    private void processGetCheckSchedule() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetCheckSchedule(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        Log.e("CEK","processGetCheckSchedule : "+dataObj.toString());
                        sessions.saveScheduledDate(dataObj.toString());
                        int errCode = dataObj.getInt("code");
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (errCode == 200) {
                            tanggalPenuh = dataObj.getJSONObject("data").getJSONArray("tanggalPenuh");
                            periodePenuh = dataObj.getJSONObject("data").getJSONArray("periodePenuh");


                            Log.e(TAG,"tanggalPenuh : "+tanggalPenuh.toString());
                            Log.e(TAG,"periodePenuh : "+periodePenuh.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void ConfigListPortal() {
        Log.e("CEK","ConfigListPortal");
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().ConfigList(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","ConfigListPortal RESPONSE CODE : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","ConfigListPortal dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONArray dataConfig = dataObj.getJSONArray("data");
                        int l = 0;
                        while (dataConfig.length() > l) {
                            String nameList = dataConfig.getJSONObject(l).getString("name");
                            if (nameList.contains("waitingTimer")) {
                                JSONObject valList = dataConfig.getJSONObject(l).getJSONObject("value");
                                timesWaiting = valList.getInt("time");
                                countWaiting = valList.getInt("count");
                            }
                            l++;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void processGetScheduleTimes() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetScheduleTimes(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("code");
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (errCode == 200) {
                            JSONArray dataArrTimes = dataObj.getJSONArray("data");
                            Log.e("CEK","processGetScheduleTimes : "+dataArrTimes.toString());
                            sessions.saveScheduledTime(dataArrTimes.toString());
                            Log.e(TAG,"dataArrTimes : "+dataArrTimes);
                            for (int i = 0; i < dataArrTimes.length(); i++) {
                                int periodeId = dataArrTimes.getJSONObject(i).getInt("id");
                                String periode = dataArrTimes.getJSONObject(i).getString("periode");
                                time.add(periode);
                                periodeInt.add(periodeId);
                                dataPeriode.put(periodeId,periode);
                                dataPeriodeId.put(periode,periodeId);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                getFragmentPage(new frag_berita());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.e(TAG,"onDateSet");
        int addmonths = (month + 1);
        String months = String.valueOf(addmonths);
        if (addmonths < 10) {
            months = "0"+months;
        }
        String days = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10 ) {
            days = "0"+days;
        }
        tanggal = days+"/"+months+"/"+year;
        Savetanggal = year + "-" + months + "-" + days;
        et_Date.setText(tanggal);

        if (periodePenuh.length() > 0) {
            ArrayList<String> times_new = new ArrayList<>(time);
            for (int i = 0; i < periodePenuh.length(); i++) {
                try {
                    String tglFull = periodePenuh.getJSONObject(i).getString("tanggal");
                    int periodeId = periodePenuh.getJSONObject(i).getInt("periodeId");
                    if (tglFull.equals(Savetanggal)) {
                        String valP = dataPeriode.get(periodeId);
                        for (int j = 0; j < times_new.size(); j++) {
                            String times = times_new.get(j);
                            if (valP.equals(times)) {
                                times_new.remove(j);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(mContext,R.layout.list_item, times_new);
            et_time.setAdapter(adapterTime);
        } else {
            ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(mContext,R.layout.list_item, time);
            et_time.setAdapter(adapterTime);
        }
    }
}