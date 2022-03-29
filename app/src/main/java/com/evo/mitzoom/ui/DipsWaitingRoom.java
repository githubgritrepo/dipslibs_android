package com.evo.mitzoom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.Adapter.AdapterSlide;
import com.evo.mitzoom.Adapter.GridAdapter;
import com.evo.mitzoom.Adapter.GridProductAdapter;
import com.evo.mitzoom.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;

public class DipsWaitingRoom extends AppCompatActivity {

    private Context mContext;
    private ViewPager mPager;
    private ArrayList<Integer> imgArray = new ArrayList<Integer>();
    private int currentPage;
    private static final Integer[] img = {R.drawable.adsv1, R.drawable.adsp2, R.drawable.adsp4};
    public static int CAM_ID = 0;
    private static final String KEY_USE_FACING = "use_facing";
    public static Integer useFacing = null;
    private Camera camera = null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private static int degreeFront = 0;
    int [] gambar = {R.drawable.adsp2, R.drawable.adsp4, R.drawable.adsp2, R.drawable.adsp4, R.drawable.adsp2, R.drawable.adsp4};
    String [] nama = {"ads1", "ads2", "ads1", "ads2", "ads1", "ads2"};
    private RecyclerView rv_product;
    private GridProductAdapter gridAdapter;
    private MaterialButton btnSchedule,btnSchedule2;
    private ImageButton btnEndCall;
    private LayoutInflater inflater;
    private View dialogView;
    private ImageView btnclose;
    private TextView et_Date;
    private AutoCompleteTextView et_time;
    private int year, month, day, waktu_tunggu = 5000;
    private String tanggal, waktu;
    String [] time = {"08.00 - 10.00", "10.00 - 12.00", "12.00 - 14.00", "14.00 - 16.00", "16.00 - 17.00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_waiting_room);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        int isCust = getIntent().getExtras().getInt("ISCUSTOMER");

        mContext = this;

        initPager();

        rv_product = (RecyclerView) findViewById(R.id.rv_product);
        btnSchedule = (MaterialButton) findViewById(R.id.btnSchedule);
        btnEndCall = findViewById(R.id.end_call);

        preview = (SurfaceView) findViewById(R.id.mySurface);

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        rv_product.setLayoutManager(new GridLayoutManager(this,2));
        gridAdapter = new GridProductAdapter(gambar);
        rv_product.setAdapter(gridAdapter);

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpSchedule();
            }
        });
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndCall();
            }
        });

        PopUpWaiting();
        PopUpSucces();

    }

    @Override
    protected void onResume() {
        super.onResume();

        camera = Camera.open(useFacing);
        startPreview();

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

    private void initPager() {
        for (int i = 0; i < img.length; i++) {
            imgArray.add(img[i]);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new AdapterSlide(imgArray, mContext));
            CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.indicator);
            circleIndicator.setViewPager(mPager);
        }

        Handler handler = new Handler();
        Runnable updates = new Runnable() {
            @Override
            public void run() {
                if (currentPage == img.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage, true);
                currentPage++;
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updates);
            }
        }, 2500, 2500);
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
                datePickerDialog.show();
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
                tanggal = et_Date.getText().toString();
                waktu = et_time.getText().toString();
                if (tanggal.trim().equals("")){
                    Toast.makeText(getApplicationContext(), "Tanggal belum diisi", Toast.LENGTH_SHORT).show();
                }
                else if (waktu.trim().equals("")){
                    Toast.makeText(getApplicationContext(), "Waktu Belum diisi", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Jadwal panggilan anda "+tanggal+" jam "+waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                }

            }
        });
    }

    private void EndCall(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText("Apakah anda ingin mengakhiri panggilan ?");
        sweetAlertDialog.setConfirmText("Akhiri Panggilan");
        sweetAlertDialog.setCancelText("Tidak");
        sweetAlertDialog.show();
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Toast.makeText(DipsWaitingRoom.this,"Panggilan anda telah diakhiri", Toast.LENGTH_LONG);
                startActivity(new Intent(getApplicationContext(), DipsSplashScreen.class));

            }
        });
    }

    private void PopUpWaiting(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setContentText("Saat ini customer support sedang dalam proses dengan customer lain. Anda dapat menunggu di ruang tunggu atau menjadwalkan panggilan.");
                sweetAlertDialog.setConfirmText("Bersedia menunggu");
                sweetAlertDialog.setCancelText("Jadwalkan panggilan");
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        /*Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);*/
                    }
                });
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        PopUpSchedule();
                    }
                });
            }
        },waktu_tunggu);
    }
    private void PopUpSucces(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsWaitingRoom.this, SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setContentText("Customer Support sudah siap melayani anda.");
                sweetAlertDialog.setConfirmText("Lanjut");
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
            }
        },120000);
    }

    public void setCameraDisplayOrientation()
    {
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
}