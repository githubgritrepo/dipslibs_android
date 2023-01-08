package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

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
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.GraphicFaceTracker;
import com.evo.mitzoom.Helper.LocaleHelper;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DipsCapture extends AppCompatActivity implements CameraSource.PictureCallback {

    private Context mContext;
    private static final String KEY_USE_FACING = "use_facing";
    private static final int REQUEST_ALL = 888;
    private boolean doubleBackToExitPressedOnce = false;
    public static Integer useFacing = null;
    private static int degreeFront = 0;
    public static int CAM_ID = 0;
    private CameraSource cameraSource;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;
    private SurfaceView preview = null;
    private SurfaceView transPreview = null;
    private SurfaceHolder previewHolder = null;
    private SurfaceHolder transHolder = null;
    private FaceDetector detector;
    private RelativeLayout rlprogress;
    private SessionManager sessions;
    public static boolean flagCapture = false;
    private String idDips;
    private boolean isSwafoto;
    private boolean isCust;
    private String custName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        setLocale(this, lang);
        //LocaleHelper.setLocale(this,lang);

        idDips = sessions.getKEY_IdDips();

        if (idDips == null) {
            idDips = "";
        }

        //idDips = "1665126733117";

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_dips_capture);

        preview = (SurfaceView) findViewById(R.id.mySurface);
        transPreview = (SurfaceView) findViewById(R.id.transSurface);
        rlprogress = (RelativeLayout) findViewById(R.id.rlprogress);
        LinearLayout llMsg = (LinearLayout) findViewById(R.id.llMsg);
        llMsg.getBackground().setAlpha(150);

        TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("CEK","MASUK IF PERMISSION");
            return;
            //requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_ALL);
        }
        String mPhoneNumber = tMgr.getLine1Number();
        Log.e("CEK","mPhoneNumber : "+mPhoneNumber);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(DipsCapture.this,"Permission Denied", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        detector = new FaceDetector.Builder(this)
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking\
                .setClassificationType(/* eyes open and smile */ FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            //System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,"Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (detector.isOperational()) {
            detector.release();
        }
        if (cameraSource != null) {
            cameraSource.stop();
            cameraSource.release();
        }
        flagCapture = false;
    }

    private void setupSurfaceHolder() {
        Log.e("CEK","setupSurfaceHolder");
        useFacing = CameraSource.CAMERA_FACING_FRONT;
        cameraSource = new CameraSource.Builder(this, detector)
                .setFacing(useFacing)
                .setRequestedFps(2.0f)
                .setRequestedPreviewSize(1024,768)
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
        flagCapture = true;
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
            Log.e("CEK","START CAMERA");
            cameraSource.start(previewHolder);
            detector.setProcessor(new LargestFaceFocusingProcessor(detector,
                    new GraphicFaceTracker(mContext,"capture")));
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

    private Bitmap prosesOptimalImage(Bitmap bitmap, File mediaFile) {
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));
        Log.d("CEK", "file_size : "+file_size);

        int perDiff = 1;
        if (file_size > 3072) {
            perDiff = 6;
        } else if (file_size > 2048) {
            perDiff = 4;
        } else if (file_size > 1024) {
            perDiff = 2;
        }/* else if (file_size > 550) {
            perDiff = 2;
        }*/

        Bitmap bitmapCrop = getResizedBitmap(bitmap, (bitmap.getWidth() / perDiff), (bitmap.getHeight() / perDiff));

        return bitmapCrop;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float sx = 0;
        float sy = 0;
        if (useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            sx = ((float) newWidth) / width;
            sy = ((float) newHeight) / height;
        } else {
            sx = ((float) newHeight) / height;
            sy = ((float) newWidth) / width;
        }

        int cx = (int) (width / 3.8);
        int cy = (int) (height / 4.7);

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(sx, sy);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, cx, cy, (int) (width * 0.5), (int) (height * 0.47), matrix, false);
        //bm.recycle();

        return resizedBitmap;
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Log.e("CEK","surfaceCreated START CAMERA");
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
            Log.e("CEK","width : "+width);
            Log.e("CEK","height : "+height);
            Canvas canvas = transHolder.lockCanvas();

            int diff1 = 4;
            if (width < 680) {
                diff1 = 5;
            } else if (width > 1000) {
                diff1 = 6;
            }
            //double cx = Math.ceil(width / 2);
            float leftright = (float) width / diff1;
            float cTop = (float) (height / diff1);
            //float marginTop = (float) (cTop * 3) / diff1;
            //int margins = MarginSurf * 2;
            float rad = (float) width / 2;

            Log.e("CEK","rad : "+rad);
            //double cy = rad + MarginSurf;

            int NUM_DASHES = 20;
            float DASH_PORTION = (float) 0.75;
            float GAP_PORTION = (float) 0.25;
            double circumference = 2 * Math.PI * rad;
            float dashPlusGapSize = (float) (circumference / NUM_DASHES);
            float[] intervals = new float[]{ 5, 5 };
            intervals[0] = dashPlusGapSize * DASH_PORTION;
            intervals[1] = dashPlusGapSize * GAP_PORTION;
            Log.e("CEK","intervals 0 : "+intervals[0]);
            Log.e("CEK","intervals 1 : "+intervals[1]);
            DashPathEffect dashPath = new DashPathEffect(intervals, 0);

            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setPathEffect(dashPath);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);

            //canvas.drawCircle((float) cx,(float) cy,(float) rad,p);

            float lefts = leftright;
            float tops = cTop;
            float rights = (width - leftright);
            float bottoms = (height - cTop);
            Log.e("CEK","lefts : "+lefts+" | lefts : "+tops+" | rights : "+rights+" | bottoms : "+bottoms);

            RectF rect = new RectF(leftright, tops, rights, bottoms);
            canvas.drawOval(rect,p);

            transHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };

    @Override
    public void onPictureTaken(@NonNull byte[] dataPhoto) {
        if (dataPhoto.length > 0) {
            cameraSource.stop();
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                processCropImage(dataPhoto);
            } else
            {
                processCropImage(dataPhoto);
            }
        }
    }

    private void processCropImage(byte[] dataPhoto) {
        Bitmap realBitmap = BitmapFactory.decodeByteArray(dataPhoto, 0, dataPhoto.length);
        //Bitmap bitmapCrop = resizeAndCropCenter(realBitmap, 640, false);

        int rotationInDegree = 1;
        try {
            File mediaFile = createTemporaryFile(dataPhoto);
            try {
                String pathFile = mediaFile.getPath();
                Bitmap bitmapCrop = prosesOptimalImage(realBitmap, mediaFile);
                ExifInterface exif = new ExifInterface(pathFile);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                rotationInDegree = rotation;
                rotationInDegree = exifToDegrees(rotationInDegree);

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

                String imgBase64 = imageRotateBase64(bitmapCrop, rotationInDegree);

                if (!imgBase64.isEmpty()) {
                    byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);

                    showProgress(true);
                    processCaptureIdentifyAuth(imgBase64);

                    /*View dialogView = getLayoutInflater().inflate(R.layout.layout_show_image, null);
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
                    sweetAlertDialog.setCustomView(dialogView);
                    sweetAlertDialog.setCancelable(false);

                    ImageView imgCapture = (ImageView) dialogView.findViewById(R.id.imgCapture);
                    imgCapture.setImageBitmap(bitmap);

                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            showProgress(true);
                            //processCaptureIdentify(imgBase64);
                            //Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                            processCaptureIdentifyAuth(imgBase64);
                        }
                    });
                    sweetAlertDialog.show();*/
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File createTemporaryFile(byte[] byteImage) throws Exception {
        String appName = getString(R.string.app_name_dips);
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
        else if (exifOrientation == ExifInterface.ORIENTATION_UNDEFINED && useFacing == CameraSource.CAMERA_FACING_FRONT) { return 0; }
        return 0;
    }

    private String imageRotateBase64(Bitmap bitmap, int rotationInDegree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        Log.d("CEK","useFacing : "+useFacing);
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

    private void processCaptureIdentifyAuth(String imgBase64) {
        if (!NetworkUtil.hasDataNetwork(mContext)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);

        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips","");
            jsons.put("image",imgBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*String filename = "CaptureAuth_"+idDips+".txt";
        try {
            createTemporaryFile(jsons.toString(),filename);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.CaptureAuth(requestBody);
        Log.e("CEK","REQUEST CALL : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                showProgress(false);
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

                        sessions.saveIdDips(idDips);

                        Intent intent = null;
                        if (!noCIF.isEmpty()) {
                            intent = new Intent(mContext, DipsWaitingRoom.class);
                            intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                        } else {
                            intent = new Intent(mContext, DipsSwafoto.class);
                            intent.putExtra("RESULT_IMAGE_AI", bytePhoto);
                            intent.putExtra("CUSTNAME", custName);
                        }
                        startActivity(intent);
                        finishAffinity();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else {
                    startCamera();
                    flagCapture = false;
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
                showProgress(false);
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processCaptureIdentify(String imgBase64) {

        if (!NetworkUtil.hasDataNetwork(mContext)) {
            showProgress(false);
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        JsonCaptureIdentify jsons = new JsonCaptureIdentify();
        //jsons.setIdDips(idDips);
        jsons.setImage(imgBase64);

        ApiService API = Server.getAPIService();
        //Call<CaptureIdentify> call = API.CaptureAdvanceAI(jsons);
        Call<CaptureIdentify> call = API.CaptureIdentify(jsons);
        Log.e("CEK","REQUEST CALL : "+call.request().url());
        call.enqueue(new Callback<CaptureIdentify>() {
            @Override
            public void onResponse(Call<CaptureIdentify> call, Response<CaptureIdentify> response) {
                flagCapture = false;
                Log.e("CEK","RESPONSE CODE: "+response.code());
                if (response.body() != null) {
                    Log.e("CEK","Response Body : "+response.body().toString());
                }
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    int errCode = response.body().getErr_code();
                    if (errCode == 0) {
                        SweetAlertDialog sweetDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
                        sweetDialog.setTitleText("Warning!!!");
                        sweetDialog.setContentText(getResources().getString(R.string.not_using_dips));
                        sweetDialog.setConfirmText("OK");
                        sweetDialog.show();
                        sweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetDialog.cancel();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                                //System.exit(0);
                            }
                        });
                        startCamera();
                        return;
                    }

                    String idDipsOld = sessions.getKEY_IdDips();
                    /*if (idDipsOld != null && OutboundService.mSocket != null) {
                        OutboundService.leaveOutbound(idDipsOld);
                    }*/

                    boolean isCust = response.body().isCustomer();
                    String custName = response.body().getName();
                    String idDips = response.body().getIdDips();
                    String sessionName = response.body().getDataSession().getNameSession();
                    String sessionPass = response.body().getDataSession().getPass();

                    sessions.saveIdDips(idDips);
                    sessions.saveIsCust(isCust);

                    Intent intent = new Intent(DipsCapture.this,DipsWaitingRoom.class);
                    sessions.saveIsCust(isCust);
                    intent.putExtra("CUSTNAME",custName);
                    intent.putExtra("idDips", idDips);
                    intent.putExtra("SessionName", sessionName);
                    intent.putExtra("SessionPass", sessionPass);
                    startActivity(intent);
                    finish();

                } else {
                    startCamera();
                    Toast.makeText(mContext, R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CaptureIdentify> call, Throwable t) {
                /*String filename = "base64_capture.txt";
                try {
                    createTemporaryFile(imgBase64,filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                flagCapture = false;
                showProgress(false);
                startCamera();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createDir() {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), IMAGE_DIRECTORY_NAME);

        return mediaStorageDir;
    }

    private File createTemporaryFile(String dataX, String filename) throws Exception {
        File mediaStorageDir = createDir();

        mediaStorageDir.mkdirs();

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        FileWriter writer = new FileWriter(mediaFile);
        writer.append(dataX);
        writer.flush();
        writer.close();

        return mediaFile;
    }

    private void showProgress(Boolean bool){

        if (bool){
            rlprogress.setVisibility(View.VISIBLE);
        }else {
            rlprogress.setVisibility(View.GONE);
        }
    }

}