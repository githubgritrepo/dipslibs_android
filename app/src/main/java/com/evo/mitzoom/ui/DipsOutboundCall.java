package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsSplashScreen.setLocale;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Constants.AuthConstants;
import com.evo.mitzoom.Helper.OutboundService;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.util.ErrorMsgUtil;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
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

public class DipsOutboundCall extends AppCompatActivity {
    private static String TAG = "DipsOutboundCall";
    private TextView incomingcall;
    private String passSession, idDips;
    public static ImageButton accept, reject;
    private SweetAlertDialog dialogConfirm;
    protected final static int REQUEST_VIDEO_AUDIO_CODE = 1010;
    protected int renderType = BaseMeetingActivity.RENDER_TYPE_ZOOMRENDERER;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    public static int CAM_ID = 0;
    private boolean cameraConfigured=false;
    private boolean inPreview=false;
    public static Integer useFacing = null;
    private static final String KEY_USE_FACING = "use_facing";
    private static int degreeFront = 0;
    private LayoutInflater inflater;
    private View dialogView;
    private ImageView btnclose;
    private TextView et_Date, textView, nama_agen;
    private AutoCompleteTextView et_time;
    private int year, month, day;
    private String tanggal, waktu;
    private String Savetanggal;
    private int Savewaktu;
    String [] time = {"08.00 - 10.00", "10.00 - 12.00", "12.00 - 14.00", "14.00 - 16.00", "16.00 - 17.00"};
    private MaterialButton btnSchedule2;
    private Context mContext;
    private SessionManager sessions;
    private boolean isCust = false;
    private String customerName = "Customer";
    private String imageAgent = null;
    private String nameAgent = null;
    private CircleImageView imgCS;
    private boolean startTimeOut = true;
    private int loop = 1;
    private String getAction = "";

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG,"MASUK Run Timeout");
            while (startTimeOut) {
                Log.i(TAG,"startTimeOut "+loop+" : "+startTimeOut);
                if (loop == 30) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OutApps();
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loop++;
            }
        }
    };
    private Handler handler;
    private Runnable myRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction() != null) {
            getAction = getIntent().getAction();
            Log.i("CEK","MASUK ACTION : "+getAction);
            if (getIntent().getAction().equals("closeapps") || getIntent().getAction().equals("endcall")) {
                finish();
                return;
            }
        }

        mContext = this;
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        isCust = sessions.getKEY_iSCust();
        String lang = sessions.getLANG();
        setLocale(this,lang);

        getPackageManager().getLaunchIntentForPackage("com.evo.mitzoom");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_dips_outbound_call);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            /*KeyguardManager keyguardManager = (KeyguardManager) getSystemService(this.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this,null);*/
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        initializeSdk();
        nama_agen = findViewById(R.id.nama_agen);
        incomingcall = findViewById(R.id.incomingcall);
        AnimationCall();
        imgCS = (CircleImageView) findViewById(R.id.imgCS);
        accept = findViewById(R.id.acceptCall);
        preview = (SurfaceView) findViewById(R.id.mySurfaceOutbound);
        reject = findViewById(R.id.rejectCall);
        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);
        passSession = OutboundService.getPassword_session();
        customerName = OutboundService.getCustomerName();
        imageAgent = OutboundService.getImagesAgent();
        nameAgent = OutboundService.getNameAgent();
        nama_agen.setText(nameAgent);

        Log.i("CEK","imageAgent  : "+imageAgent);
        Log.i("CEK","startTimeOut  : "+startTimeOut);
        String imageAgentnew = imageAgent.replace("https://dips.grit.id:6503/", Server.BASE_URL_API);

        Glide.with(mContext)
            .asBitmap()
            .load(imageAgentnew)
            .circleCrop()
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    imgCS.setImageBitmap(resource);
                }
        });

        new AsynTimeout().execute();
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("TIDAK DIANGKAT","");
                OutApps();
            }
        };
        handler.postDelayed(myRunnable, 30000);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeOut = false;
                handler.removeMessages(0);
                handler.removeCallbacks(myRunnable);
                Popup();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeOut = false;
                handler.removeMessages(0);
                handler.removeCallbacks(myRunnable);
                PopUpSchedule();
            }
        });

        if (getIntent().getAction() != null) {
            Log.i("CEK","MASUK ACTION : "+getAction);
            if (getAction.equals("endcall")) {
                startTimeOut = false;
                PopUpSchedule();
            }
        } else {
            new AsynTimeout().execute();
        }
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

    private class AsynTimeout extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG,"MASUK BACKGROUND AsynTimeout");
            new Thread(runnable).start();
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    while (startTimeOut) {
                        Log.i(TAG,"startTimeOut "+loop+" : "+startTimeOut);
                        if (loop == 30) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    OutApps();
                                }
                            });
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        loop++;
                    }
                }
            }).start();*/
            return null;
        }
    }

    private void PopUpSchedule(){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_schedule,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsOutboundCall.this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i(TAG,"MASUK DISMISS");
                startTimeOut = true;
                if (getAction.isEmpty()) {
                    new AsynTimeout().execute();
                }
            }
        });
        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(DipsOutboundCall.this,R.layout.list_item, time);
        btnclose = dialogView.findViewById(R.id.btn_close_schedule);
        et_Date = dialogView.findViewById(R.id.et_Date);
        textView = dialogView.findViewById(R.id.textHeadSchedule);
        textView.setText("Apakah anda ingin menjadwalkan panggilan ?");
        et_time = dialogView.findViewById(R.id.et_time);
        et_time.setAdapter(adapterTime);
        btnSchedule2 = dialogView.findViewById(R.id.btnSchedule2);

        et_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Savewaktu = position;

            }
        });
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DipsOutboundCall.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
                        Savetanggal = year+""+months+""+days;
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
                    saveSchedule();
                    OutboundService.rejectCall();
                    Toast.makeText(getApplicationContext(), "Jadwal panggilan anda "+tanggal+" jam "+waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismissWithAnimation();
                    sweetAlertDialog.setCancelable(false);
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsOutboundCall.this, SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setContentText(getResources().getString(R.string.content_after_schedule));
                    sweetAlertDialog.setConfirmText(getResources().getString(R.string.done));
                    sweetAlertDialog.show();
                    Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
                    btnConfirm.setBackgroundTintList(DipsOutboundCall.this.getResources().getColorStateList(R.color.Blue));
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("CEK","MASUK BUTTON CONFIRM");
                            sweetAlertDialog.dismiss();
                            OutApps();
                        }
                    });
                }

            }
        });
        btnSchedule2.setBackgroundTintList(DipsOutboundCall.this.getResources().getColorStateList(R.color.Blue));
    }
    private void saveSchedule(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("tanggal",Savetanggal);
            jsons.put("grup",Savewaktu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("PARAMS JADWAL","idDips = "+idDips+", Tanggal = "+Savetanggal+", Grup index Time of ["+Savewaktu+"]");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.saveSchedule(requestBody);
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

    private void OutApps(){
        startTimeOut = false;
        NotificationManager notificationManagerCompat = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.cancel(OutboundService.NOTIFICATION_IDOutbound);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        //DipsOutboundCall.this.overridePendingTransition(0,0);
        //System.exit(0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"MASUK RESUME");
        camera = Camera.open(useFacing);
        //startPreview();
        cameraConfigured = false;
        previewHolder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"MASUK Destroy");
        startTimeOut = false;
        Thread.interrupted();
        finish();
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

    private void previewHolder(){
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            initPreview(width,height);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };
    public void setCameraDisplayOrientation(){
        if (camera == null)
        {
            Log.d("CEK","setCameraDisplayOrientation - camera null");
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CAM_ID, info);

        WindowManager winManager = (WindowManager) DipsOutboundCall.this.getSystemService(Context.WINDOW_SERVICE);
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
                Toast.makeText(DipsOutboundCall.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
    private void AnimationCall(){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            int count = 0;

            @Override
            public void run() {
                count++;

                if (count == 1)
                {
                    incomingcall.setText(getResources().getString(R.string.incoming_call));
                }
                else if (count == 2)
                {
                    incomingcall.setText(getResources().getString(R.string.incoming_call1));
                }
                else if (count == 3)
                {
                    incomingcall.setText(getResources().getString(R.string.incoming_call2));
                }
                else if (count == 4)
                {
                    incomingcall.setText(getResources().getString(R.string.incoming_call3));
                }

                if (count == 4)
                    count = 0;

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }
    protected boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }
    protected void onPermissionGranted() {
        processJoinVideo();
    }
    private void Popup(){
        if (dialogConfirm == null) {
            dialogConfirm = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        }
        dialogConfirm.setContentText(this.getResources().getString(R.string.content_input));
        dialogConfirm.setConfirmText(this.getResources().getString(R.string.btn_continue));
        dialogConfirm.show();
        dialogConfirm.setCancelable(false);
        Button btnConfirm = (Button) dialogConfirm.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(this.getResources().getColorStateList(R.color.Blue));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutboundService.acceptCall();
                dialogConfirm.dismiss();
                processJoinVideo();
                NotificationManager notificationManagerCompat = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManagerCompat.cancel(OutboundService.NOTIFICATION_IDOutbound);
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
            jsons.put("sessionName",idDips);
            jsons.put("role",0);
            jsons.put("sessionKey",passSession);
            jsons.put("userIdentity", customerName);
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
                Toast.makeText(DipsOutboundCall.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void processCreateVideo(String signatures) {
        Log.i("CEK","masuk processCreateVideo");
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
            Log.i("CEK","SESSION NULL");
            return;
        }

        Log.i("CEK","LANJUUTT");

        Intent intent = new Intent(this, DipsVideoConfren.class);
        intent.putExtra("name", name);
        intent.putExtra("password", sessionPass);
        intent.putExtra("sessionName", sessionName);
        intent.putExtra("render_type", renderType);
        intent.putExtra("ISCUSTOMER", isCust);
        startActivity(intent);
        finish();
    }

}