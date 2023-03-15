package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evo.mitzoom.Helper.GraphicFaceTracker;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DipsCameraSource extends AppCompatActivity implements CameraSource.PictureCallback {

    private Context mContext;
    private SessionManager sessions;
    private SurfaceView preview;
    private SurfaceView transPreview;
    private FaceDetector detector;
    public static Integer useFacing = null;
    private CameraSource cameraSource;
    private SurfaceHolder previewHolder = null;
    private SurfaceHolder transHolder = null;
    public static boolean flagCapture = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        Log.e("CEK","LANG : "+lang);
        setLocale(this, lang);
        //LocaleHelper.setLocale(this,lang);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_dips_camera_source);

        preview = findViewById(R.id.mySurface);
        transPreview = findViewById(R.id.transSurface);
        TextView tvHeader = findViewById(R.id.tvHeader);
        TextView tvContent = findViewById(R.id.tvContent);
        LinearLayout llMsg = findViewById(R.id.llMsg);
        llMsg.getBackground().setAlpha(150);
        tvHeader.setText(R.string.ktp_swafoto);
        tvContent.setText(R.string.content_swafoto2);

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
        useFacing = CameraSource.CAMERA_FACING_FRONT;
        cameraSource = new CameraSource.Builder(this, detector)
                .setFacing(useFacing)
                .setRequestedFps(2.0f)
                .setRequestedPreviewSize(1024,768)
                .setAutoFocusEnabled(true)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
                .build();

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        transHolder = transPreview.getHolder();
        transHolder.setFormat(PixelFormat.TRANSPARENT);
        transHolder.addCallback(surfaceCallbackTrans);
        transHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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

    private int posCX = 0;
    private int posCY = 0;
    private int posWidth = 0;
    private int posHeight = 0;
    SurfaceHolder.Callback surfaceCallbackTrans = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            float rad = (float) width / 2;

            Log.e("CEK","rad : "+rad);

            int NUM_DASHES = 20;
            float DASH_PORTION = (float) 0.3;
            float GAP_PORTION = (float) 0.25;
            double circumference = 2 * Math.PI * rad;
            float dashPlusGapSize = (float) (circumference / NUM_DASHES);
            float[] intervals = new float[]{ 5, 5 };
            intervals[0] = dashPlusGapSize * DASH_PORTION;
            intervals[1] = dashPlusGapSize * GAP_PORTION;
            DashPathEffect dashPath = new DashPathEffect(intervals, 0);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getResources().getColor(R.color.zm_v1_red_A100));
            paint.setAlpha(130);
            paint.setPathEffect(dashPath);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            Log.e("CEK","surfaceChanged width : "+width+" | height : "+height);

            double diffH = Math.ceil(height / 1.5);
            double diffw = Math.ceil(width / 5);

            double surfRight = (width - diffw);
            double surfBottom = Math.ceil(height / 1.1);

            Log.e("CEK","surfaceChanged diffw : "+diffw+" | diffH : "+diffH+" | surfRight : "+surfRight+" | surfBottom : "+surfBottom);

            RectF rect = new RectF((float) diffw,(float) diffH,(float) surfRight,(float) surfBottom);

            Canvas canvas = transHolder.lockCanvas();
            canvas.drawRect(rect,paint);

            transHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };

    private void startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraSource.start(previewHolder);
            detector.setProcessor(new LargestFaceFocusingProcessor(null,
                    new GraphicFaceTracker(mContext,"camerasource")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clickImage() {
        if (cameraSource != null) {
            cameraSource.takePicture(null, this);
        }
    }

    public void captureImage() {
        Log.e("CEK",this+" MASUK captureImage");
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
        }, 300);
    }

    @Override
    public void onPictureTaken(@NonNull byte[] dataPhoto) {
        if (dataPhoto.length > 0) {
            cameraSource.stop();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    processCropImage(dataPhoto);
                } else {
                    processCropImage(dataPhoto);
                }
            } else {
                processCropImage(dataPhoto);
            }

        }
    }

    private void processCropImage(byte[] dataPhoto) {
        Bitmap realBitmap = BitmapFactory.decodeByteArray(dataPhoto, 0, dataPhoto.length);
        Bitmap bitmapCrop = resizeAndCropCenter(realBitmap, 600, false);

        int rotationInDegree = 1;
        try {
            File mediaFile = createTemporaryFile(dataPhoto);
            try {
                String pathFile = mediaFile.getPath();
                Bitmap bitmapCropShape = getResizedBitmap(realBitmap, realBitmap.getWidth(), realBitmap.getHeight());
                //Bitmap bitmapCrop = prosesOptimalImage(realBitmap, mediaFile);
                ExifInterface exif = new ExifInterface(pathFile);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                rotationInDegree = rotation;
                rotationInDegree = exifToDegrees(rotationInDegree);

                String imgBase64 = imageRotateBase64(bitmapCrop, rotationInDegree);

                String imgBase64Shape = imageRotateBase64(bitmapCropShape, rotationInDegree);

                if (!imgBase64.isEmpty()) {
                    byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);

                    byte[] shape_bytePhoto = Base64.decode(imgBase64Shape, Base64.NO_WRAP);

                    File fileImage = createTemporaryFile(bytePhoto);
                    File fileImageCrop = createTemporaryFile(shape_bytePhoto);

                    String filePath = fileImage.getAbsolutePath();
                    String filePathCrop = fileImageCrop.getAbsolutePath();
                    Log.e("CEK","filePath : "+filePath);
                    Log.e("CEK","filePathCrop : "+filePathCrop);

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

                    Intent returnIntent = getIntent();
                    returnIntent.putExtra("result_camera", filePath);
                    returnIntent.putExtra("result_cropImage", filePathCrop);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.e("CEK","getResizedBitmap width : "+width+" | height : "+height+" | newWidth : "+newWidth+" | newHeight : "+newHeight);

        float sx = 0;
        float sy = 0;
        if (useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            sx = ((float) newWidth) / width;
            sy = ((float) newHeight) / height;
        } else {
            sx = ((float) newHeight) / height;
            sy = ((float) newWidth) / width;
        }

        int cx = (int) (width / 4.9);
        int cy = (int) (height / 1.6);
        int widthB = (int) (width / 1.7);
        int heightB = (int) (height / 3.6);

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        if (width < height) {
            matrix.postScale(-1, 1);
        } else {
            cx = (int) (height / 8);
            cy = (int) (width / 10);
            widthB = (int) (height / 1.9);
            heightB = (int) (width / 2);
            matrix.postScale(1, -1); //xiaomi cx itu brarti sudut Y dan cy brarti sudut X
            //untuk cx dan width di hitung dari sisi kiri ke kanan dan cy dan height dari bawah ke atas
        }

        Log.e("CEK","getResizedBitmap cx : "+cx+" | cy : "+cy+" | widthB : "+widthB+" | heightB : "+heightB);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, cx, cy, widthB, heightB, matrix, false);

        return resizedBitmap;
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS",
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
}