package com.evo.mitzoom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.GraphicFaceTracker;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.evo.mitzoom.R;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DipsCapture extends AppCompatActivity implements CameraSource.PictureCallback {

    private Context mContext;
    private static final String KEY_USE_FACING = "use_facing";
    public static Integer useFacing = null;
    private static int degreeFront = 0;
    public static int CAM_ID = 0;
    private CameraSource cameraSource;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private SurfaceView preview = null;
    private SurfaceView transPreview = null;
    private SurfaceHolder previewHolder = null;
    private SurfaceHolder transHolder = null;
    private FaceDetector detector;
    private RelativeLayout rlprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_dips_capture);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mContext = this;

        preview = (SurfaceView) findViewById(R.id.mySurface);
        transPreview = (SurfaceView) findViewById(R.id.transSurface);
        rlprogress = (RelativeLayout) findViewById(R.id.rlprogress);
        LinearLayout llMsg = (LinearLayout) findViewById(R.id.llMsg);
        llMsg.getBackground().setAlpha(150);

        detector = new FaceDetector.Builder(this)
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking
                .setClassificationType(/* eyes open and smile */ FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .build();

        if (!detector.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available");
        } else {
            Log.w("MainActivity", "Detector Dependencies are available");
            if (preview != null) {
                setupSurfaceHolder();
            }
        }

    }

    private void setupSurfaceHolder() {
        useFacing = CameraSource.CAMERA_FACING_FRONT;
        cameraSource = new CameraSource.Builder(this, detector)
                .setFacing(useFacing)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        transHolder = transPreview.getHolder();
        transHolder.setFormat(PixelFormat.TRANSPARENT);
        transHolder.addCallback(surfaceCallbackTrans);
        transHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void captureImage() {
        // We add a delay of 200ms so that image captured is stable.
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clickImage();
                    }
                });
            }
        }, 200);
    }

    private void clickImage() {
        if (cameraSource != null) {
            cameraSource.takePicture(null, this);
        }
    }

    private void startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraSource.start(previewHolder);
            detector.setProcessor(new LargestFaceFocusingProcessor(detector,
                    new GraphicFaceTracker(this)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (w == size && h == size) return bitmap;
        // scale the image so that the shorter side equals to the target;
        // the longer side will be center-cropped.
        float scale = (float) size / Math.min(w,  h);
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) bitmap.recycle();
        return target;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            startCamera();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            cameraSource.stop();
        }
    };

    SurfaceHolder.Callback surfaceCallbackTrans = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Canvas canvas = transHolder.lockCanvas();

            int MarginSurf = 160;
            if (width < 680) {
                MarginSurf = 80;
            } else if (width > 1000) {
                MarginSurf = 240;
            }
            double cx = Math.ceil(width / 2);
            int margins = MarginSurf * 2;
            double rad = Math.ceil((width - margins) / 2);
            double cy = rad + MarginSurf;

            int NUM_DASHES = 20;
            float DASH_PORTION = (float) 0.75;
            float GAP_PORTION = (float) 0.25;
            double circumference = 2 * Math.PI * rad;
            float dashPlusGapSize = (float) (circumference / NUM_DASHES);
            float[] intervals = new float[]{ 5, 5 };
            intervals[0] = dashPlusGapSize * DASH_PORTION;
            intervals[1] = dashPlusGapSize * GAP_PORTION;
            DashPathEffect dashPath = new DashPathEffect(intervals, 0);

            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setPathEffect(dashPath);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);

            canvas.drawCircle((float) cx,(float) cy,(float) rad,p);

            transHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };

    @Override
    public void onPictureTaken(@NonNull byte[] dataPhoto) {
        if (dataPhoto.length > 0) {
            processCropImage(dataPhoto);
        }
    }

    private void processCropImage(byte[] dataPhoto) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(dataPhoto, 0, dataPhoto.length);
        Bitmap bitmapCrop = resizeAndCropCenter(bitmap, 320, false);

        int rotationInDegree = 1;
        try {
            File mediaFile = createTemporaryFile(dataPhoto);
            try {
                String pathFile = mediaFile.getAbsolutePath();
                ExifInterface exif = new ExifInterface(pathFile);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                rotationInDegree = rotation;

                if (mediaFile.exists()) {
                    try {
                        mediaFile.getCanonicalFile().delete();
                        if (mediaFile.exists()) {
                            getApplicationContext().deleteFile(mediaFile.getName());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String imgBase64 = imageRotateBase64(bitmapCrop, rotationInDegree);

        if (!imgBase64.isEmpty()) {
            showProgress(true);
            processCaptureIdentify(imgBase64);
        }
        //byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);

    }

    private File createTemporaryFile(byte[] byteImage) throws Exception {
        String appName = getString(R.string.app_name);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.close();

        return mediaFile;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        else if (exifOrientation == ExifInterface.ORIENTATION_NORMAL) {  return 270; }
        else if (exifOrientation == ExifInterface.ORIENTATION_UNDEFINED && useFacing == CameraSource.CAMERA_FACING_BACK) {  return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_UNDEFINED && useFacing == CameraSource.CAMERA_FACING_FRONT) {
            if (degreeFront == 90) {
                return 270;
            } else {
                return 90;
            }
        }
        return 0;
    }

    private String imageRotateBase64(Bitmap bitmap, int rotationInDegree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        if (useFacing == CameraSource.CAMERA_FACING_FRONT) {
            matrix.setRotate(rotationInDegree);
        } else {
            matrix.setRotate(90);
        }
        matrix.postScale(-1, 1);

        Bitmap rotBitmap = Bitmap.createBitmap(bitmap, 0, 0 , w, h, matrix, true);
        String imgBase64 = imgtoBase64(rotBitmap);

        return imgBase64;
    }

    private String imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return encodedImage;
    }

    private void processCaptureIdentify(String imgBase64) {

        if (!NetworkUtil.hasDataNetwork(mContext)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        JsonCaptureIdentify jsons = new JsonCaptureIdentify();
        jsons.setImage(imgBase64);

        ApiService API = Server.getAPIService();
        Call<CaptureIdentify> call = API.CaptureIdentify(jsons);
        call.enqueue(new Callback<CaptureIdentify>() {
            @Override
            public void onResponse(Call<CaptureIdentify> call, Response<CaptureIdentify> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    String dataS = response.body().toString();
//                    try {
                        //JSONObject jsObj = new JSONObject(dataS);
                        String msg = response.body().getMessage();


                        //if (jsObj.has("err_code")) {
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
                                        System.exit(0);
                                    }
                                });
                                sweetDialog.show();

                                return;
                            }

                            boolean isCust = response.body().isCustomer();
                            String custName = response.body().getName();
                            String sessionName = response.body().getDataSession().getNameSession();
                            String sessionPass = response.body().getDataSession().getPass();
                            Intent intent = new Intent(DipsCapture.this,DipsWaitingRoom.class);
                            intent.putExtra("ISCUSTOMER",isCust);
                            intent.putExtra("CUSTNAME",custName);
                            intent.putExtra("SessionName", sessionName);
                            intent.putExtra("SessionPass", sessionPass);
                            startActivity(intent);

//                        } else {
//                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
//                        }

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    Toast.makeText(mContext, R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CaptureIdentify> call, Throwable t) {
                showProgress(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(Boolean bool){

        if (bool){
            rlprogress.setVisibility(View.VISIBLE);
        }else {
            rlprogress.setVisibility(View.GONE);
        }
    }

}