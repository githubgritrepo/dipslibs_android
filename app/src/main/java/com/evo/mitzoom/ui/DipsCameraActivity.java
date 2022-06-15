package com.evo.mitzoom.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DipsCameraActivity extends AppCompatActivity {

    public static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static int CAM_ID = 0;
    private SurfaceView preview = null;
    private SurfaceView transPreview = null;
    private SurfaceHolder previewHolder = null;
    private SurfaceHolder transHolder = null;
    private Camera camera = null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private static final String KEY_USE_FACING = "use_facing";
    private static final int RESULT_CODE = 110;
    public static Integer useFacing = null;
    private ImageView btnTake, imgSwitch;
    private ImageView btn_back;
    private byte[] dataImage = new byte[0];
    private static int degreeFront = 0;
    private Context mContext;
    private OrientationEventListener morientationEventListener;
    private int mOrientation =  -1;
    private int rotationInDegree = 1;
    private int optimalWidth = 0;
    private int optimalHeight = 0;
    private int surfWIdth = 0;
    private int surfHeight = 0;
    private double surfLeft = 0;
    private double surfTop = 0;
    private double surfRight = 0;
    private double surfBottom = 0;
    private SessionManager sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_camera);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initialElements();

        mContext = this;

        sessions = new SessionManager(mContext);

        transHolder = transPreview.getHolder();
        transHolder.setFormat(PixelFormat.TRANSPARENT);
        transHolder.addCallback(surfaceCallbackTrans);
        transHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_BACK);

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            if (data.length > 0) {
                                dataImage = data;

                                Bitmap bitmap = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
                                //Bitmap bitmapCrop = resizeAndCropCenter2(bitmap, 640, false);
                                Bitmap bitmapCrop = getResizedBitmap(bitmap, (bitmap.getWidth() / 2), (bitmap.getHeight() / 2));

                                int rotation = 0;
                                try {
                                    File mediaFile = createTemporaryFile(dataImage);
                                    try {
                                        String pathFile = mediaFile.getAbsolutePath();
                                        ExifInterface exif = new ExifInterface(pathFile);
                                        rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                        Log.d("CEK", "rotation : " + rotation);

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

                                String imgBase64 = imageRotateBase64(bitmapCrop, rotation);

                                byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);
                                Intent returnIntent = getIntent();
                                returnIntent.putExtra("result_camera", bytePhoto);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        }
                    });
                } else {
                    onPause();
                    onResume();
                }
            }
        });

        imgSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useFacing != null) {
                    Intent intent = getIntent();
                    if (useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        useFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                    } else {
                        useFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }

                    onPause();
                    onResume();
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
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initialElements() {
        imgSwitch = findViewById(R.id.imgSwitch);
        preview = findViewById(R.id.mySurface);
        transPreview = findViewById(R.id.transSurface);
        btnTake = findViewById(R.id.takePicture);
        btn_back = findViewById(R.id.btn_back);
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideStatusBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                requestPermission();
            }
        } else {
            int resultPerm = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
            if (resultPerm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                requestPermission();
            }
        }

        cameraConfigured = false;
        previewHolder();
    }

    private void previewHolder(){
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
    public void onBackPressed() {
        super.onBackPressed();
        sessions.saveMedia(0);
    }

    public void hideStatusBar() {
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
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

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                Camera.Size size = getOptimalPreviewSize(sizes, width, height);
                optimalWidth = size.width;
                optimalHeight = size.height;
                if (size != null) {
                    parameters.setPreviewSize(optimalWidth, optimalHeight);
                    camera.setParameters(parameters);
                    try {
                        camera.setPreviewDisplay(previewHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    SurfaceHolder.Callback surfaceCallbackTrans = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getResources().getColor(R.color.zm_v1_red_A100));
            paint.setAlpha(130);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            double diffH = Math.ceil(height / 2.8);
            double diffw = Math.ceil(width / 14);

            surfWIdth = width;
            surfHeight = height;
            surfLeft = diffw;
            surfTop = diffH;
            surfRight = (width-diffw);
            surfBottom = (height-diffH);

            RectF rect = new RectF((float) diffw,(float) diffH,(float) surfRight,(float) surfBottom);

            Canvas canvas = transHolder.lockCanvas();
            canvas.drawRect(rect,paint);

            transHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };

    private String imageRotateBase64(Bitmap bitmap, int rotation) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        if (useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotationInDegree = exifToDegrees(rotation);
            matrix.setRotate(rotationInDegree);
            matrix.postScale(-1, 1);
        } else {
            matrix.setRotate(90);
        }

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
        else if (exifOrientation == ExifInterface.ORIENTATION_UNDEFINED && useFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {  return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_UNDEFINED && useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            if (degreeFront == 90) {
                return 270;
            } else {
                return 90;
            }
        }
        return 0;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},  REQUEST_WRITE_PERMISSION);
            } else {
                camera = Camera.open(useFacing);
                startPreview();
            }
        } else {
            int resultPerm = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (resultPerm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            } else {
                camera = Camera.open(useFacing);
                startPreview();
            }
        }
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
            result = (info.orientation - degrees + 360) % 360;
            //result = 180;
        }
        camera.setDisplayOrientation(result);
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

        int cx = (int) (width / 2.77);
        int cy = (int) (height / 5.2);
        int diffH = cy * 2;

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(sx, sy);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, cx, cy, (int) (width * 0.3), (height-diffH), matrix, false);
        bm.recycle();

        return resizedBitmap;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(mContext,"Write External Stroge Denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                Toast.makeText(mContext,"Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

}