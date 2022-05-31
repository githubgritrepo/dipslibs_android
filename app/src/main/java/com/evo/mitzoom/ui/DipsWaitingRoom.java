package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsSplashScreen.setLocale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSlide;
import com.evo.mitzoom.Adapter.GridAdapter;
import com.evo.mitzoom.Adapter.GridProductAdapter;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Constants.AuthConstants;
import com.evo.mitzoom.Fragments.frag_berita;
import com.evo.mitzoom.Fragments.frag_list_produk;
import com.evo.mitzoom.Fragments.frag_service;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.util.ErrorMsgUtil;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import me.relex.circleindicator.CircleIndicator;
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

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Server.BASE_URL_API);
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CEK","MASUK onCreate");
        super.onCreate(savedInstanceState);
        mContext = this;

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        AnimationCall();


        mSocket.on("waiting", waitingListener);
        mSocket.connect();

        sessions = new SessionManager(mContext);
        sessions.saveRTGS(null);
        String lang = sessions.getLANG();
        setLocale(this,lang);
        setContentView(R.layout.activity_dips_waiting_room);

        isCust = getIntent().getExtras().getBoolean("ISCUSTOMER");
        custName = getIntent().getExtras().getString("CUSTNAME");
        idDips = getIntent().getExtras().getString("idDips");
        NameSession = getIntent().getExtras().getString("SessionName");
        SessionPass = getIntent().getExtras().getString("SessionPass");
        myTicket = findViewById(R.id.myticket2);
        lastTicket = findViewById(R.id.last_ticket2);
        processGetTicket(myTicket);
        AnimationCall = findViewById(R.id.AnimationCall);

        Log.d("CEK", "idDips : "+idDips);

        initializeSdk();

        /*btnSchedule = (MaterialButton) findViewById(R.id.btnSchedule);
        btnEndCall = findViewById(R.id.end_call);*/
        preview = (SurfaceView) findViewById(R.id.mySurface2);

        getFragmentPage(new frag_berita());

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);

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
        AnimationCall();
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

        mSocket.disconnect();
        mSocket.off("waiting");
    }

    private void previewHolder(){
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initializeSdk() {
        ZoomVideoSDKInitParams params = new ZoomVideoSDKInitParams();
        params.domain = AuthConstants.WEB_DOMAIN; // Required
        params.enableLog = true; // Optional for debugging
        params.videoRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;
        params.audioRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;
        params.shareRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;

        int initResult = ZoomVideoSDK.getInstance().initialize(this, params);
        if (initResult != ZoomVideoSDKErrors.Errors_Success) {
            Toast.makeText(this, ErrorMsgUtil.getMsgByErrorCode(initResult), Toast.LENGTH_LONG).show();
        }

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
                            PopUpSucces();
                        } else {
                              if (lastQueue.trim().equals(myTicketNumber) && Session_name.trim().equals(idDips) ){
                                lastTicket.setText("A"+lastQueue.substring(lastQueue.length()-3,lastQueue.length()));
                                NameSession = Session_name;
                                SessionPass = Session_password;
                                PopUpSucces();
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

    private void PopUpSchedule(){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_schedule,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(DipsWaitingRoom.this,R.layout.list_item, time);
        btnclose = dialogView.findViewById(R.id.btn_close_schedule);
        et_Date = dialogView.findViewById(R.id.et_Date);
        et_time = dialogView.findViewById(R.id.et_time);
        et_time.setAdapter(adapterTime);
        btnSchedule2 = dialogView.findViewById(R.id.btnSchedule2);

        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DipsWaitingRoom.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tanggal = dayOfMonth+"/"+(month + 1)+"/"+year;
                        et_Date.setText(tanggal);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        btnSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggal = et_Date.getText().toString();
                waktu = et_time.getText().toString();
                if (tanggal.trim().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else if (waktu.trim().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Jadwal panggilan anda "+tanggal+" jam "+waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismissWithAnimation();
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setContentText(getResources().getString(R.string.content_after_schedule));
                    sweetAlertDialog.setConfirmText(getResources().getString(R.string.done));
                    sweetAlertDialog.show();
                    Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
                    btnConfirm.setBackgroundTintList(DipsWaitingRoom.this.getResources().getColorStateList(R.color.Blue));
                }

            }
        });
        btnSchedule2.setBackgroundTintList(DipsWaitingRoom.this.getResources().getColorStateList(R.color.Blue));
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

    private void PopUpSucces(){
        if (dialogSuccess == null) {
            dialogSuccess = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.SUCCESS_TYPE);
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
                    sweetAlertDialog.dismissWithAnimation();
                    processJoinVideo();
                    dialogSuccess = null;
                }
            });
            dialogSuccess.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    PopUpSchedule();
                    dialogSuccess = null;
                }
            });
        }
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

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Signature(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        String signatures = "";
                        if (jsObj.has("signature")) {
                            if (!jsObj.isNull("signature")) {
                                signatures = jsObj.getString("signature");
                                processCreateVideo(signatures);
                            }
                        }
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
    private void processCreateVideo(String signatures) {
        JWT jwt = new JWT(signatures);
        Map<String, Claim> allClaims = jwt.getClaims();
        String name = allClaims.get("user_identity").asString();
        String sessionName = allClaims.get("tpc").asString();
        String sessionPass = allClaims.get("session_key").asString();

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
        sessionContext.sessionPassword = sessionPass;

        ZoomVideoSDKSession session = ZoomVideoSDK.getInstance().joinSession(sessionContext);

        if(null==session){
            return;
        }

        Intent intent = new Intent(this, DipsVideoConfren.class);
        intent.putExtra("name", name);
        intent.putExtra("password", sessionPass);
        intent.putExtra("sessionName", sessionName);
        intent.putExtra("render_type", renderType);
        intent.putExtra("ISCUSTOMER", isCust);
        startActivity(intent);
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