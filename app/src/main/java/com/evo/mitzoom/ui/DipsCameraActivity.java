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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.android.material.appbar.AppBarLayout;

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
    Camera.Parameters parameters;
    private static final String KEY_USE_FACING = "use_facing";
    private static final int RESULT_CODE = 110;
    public static Integer useFacing = null;
    private AppBarLayout appbar;
    private FrameLayout flFrame;
    private LinearLayout llHeader;
    private TextView tvHeader;
    private TextView tvContent;
    private LinearLayout llMsg;
    private ImageView btnTake, imgSwitch;
    private ImageView btn_back;
    private byte[] dataImage = new byte[0];
    private static int degreeFront = 0;
    private Context mContext;
    private OrientationEventListener morientationEventListener;
    private final int mOrientation =  -1;
    private int rotationInDegree = 1;
    private final int optimalWidth = 0;
    private final int optimalHeight = 0;
    private SessionManager sessions;
    private boolean isConfigure;
    private final boolean cekSwafoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        setLocale(this,lang);
        //LocaleHelper.setLocale(this,lang);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_dips_camera);

        initialElements();
        previewHolder();

        Intent intent = getIntent();

        transPreview.setVisibility(View.VISIBLE);
        appbar.setVisibility(View.VISIBLE);
        llHeader.setVisibility(View.GONE);
        llMsg.setVisibility(View.GONE);

        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_BACK);

        transHolder = transPreview.getHolder();
        transHolder.setFormat(PixelFormat.TRANSPARENT);
        transHolder.addCallback(surfaceCallbackTrans);
        transHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    camera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            if (data.length > 0) {
                                dataImage = data;

                                Bitmap realBitmap = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
                                //Bitmap bitmapCrop = resizeAndCropCenter2(bitmap, 640, false);
                                /*int widthReal = realBitmap.getWidth();
                                int heightReal = realBitmap.getHeight();
                                Bitmap bitmapRes = getResizedBitmap(realBitmap, (widthReal / 2), (heightReal / 2));*/

                                int rotation = 0;
                                try {
                                    File mediaFile = createTemporaryFile(dataImage);
                                    try {
                                        String pathFile = mediaFile.getAbsolutePath();
                                        Bitmap bitmapCrop = prosesOptimalImage(realBitmap, mediaFile,1);

                                        ExifInterface exif = new ExifInterface(pathFile);
                                        rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                                        String imgBase64 = imageRotateBase64(bitmapCrop, rotation);
                                        byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);

                                        File mediaFilesCrop = null;
                                        try {
                                            mediaFilesCrop = createTemporaryFile(bytePhoto);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        int file_mediaFilesCrop = Integer.parseInt(String.valueOf(mediaFilesCrop.length()/1024));

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

                                        if (mediaFilesCrop.exists()) {
                                            try {
                                                mediaFilesCrop.getCanonicalFile().delete();
                                                if (mediaFilesCrop.exists()) {
                                                    getApplicationContext().deleteFile(mediaFilesCrop.getName());
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        Intent returnIntent = getIntent();
                                        returnIntent.putExtra("result_camera", bytePhoto);
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
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
    @Override
    protected void onResume() {
        super.onResume();

        isConfigure = false;

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

    public byte[] getDownsizedImageBytes(Bitmap fullBitmap, int scaleWidth, int scaleHeight) throws IOException {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true);

        // 2. Instantiate the downsized image content as a byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] downsizedImageBytes = baos.toByteArray();

        return downsizedImageBytes;
    }

    private Bitmap prosesOptimalImage(Bitmap bitmap, File mediaFile, int perDiff) {
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));

        if (perDiff == 1) {
            if (file_size > 4096) {
                perDiff = 8;
            } else if (file_size > 3072) {
                perDiff = 6;
            } else if (file_size > 2048) {
                perDiff = 4;
            } else if (file_size > 1024) {
                perDiff = 2;
            }
        }

        Bitmap bitmapDownSize = null;
        try {
            byte[] downsizedImageBytes = getDownsizedImageBytes(bitmap, (bitmap.getWidth() / perDiff), (bitmap.getHeight() / perDiff));
            bitmapDownSize = BitmapFactory.decodeByteArray(downsizedImageBytes, 0, downsizedImageBytes.length);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapCrop = getResizedBitmap(bitmapDownSize, bitmapDownSize.getWidth(), bitmapDownSize.getHeight());

        if (bitmapCrop.getWidth() < 256 || bitmapCrop.getHeight() < 256) {
            perDiff--;
            bitmapCrop = prosesOptimalImage(bitmap,mediaFile,perDiff);
        }

        return bitmapCrop;
    }

    private void initialElements() {
        appbar = findViewById(R.id.appbar);
        flFrame = findViewById(R.id.flFrame);
        llHeader = findViewById(R.id.llHeader);
        tvHeader = findViewById(R.id.tvHeader);
        tvContent = findViewById(R.id.tvContent);
        llMsg = findViewById(R.id.llMsg);
        imgSwitch = findViewById(R.id.imgSwitch);
        preview = findViewById(R.id.mySurface);
        transPreview = findViewById(R.id.transSurface);
        btnTake = findViewById(R.id.takePicture);
        btn_back = findViewById(R.id.btn_back);
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
            try {
                camera.setPreviewDisplay(previewHolder);
                //camera.setDisplayOrientation(90);
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
            parameters = camera.getParameters();
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            camera.setParameters(parameters);
            inPreview = true;
            if (isConfigure) {
                try {
                    Thread.sleep(500);
                    optimalCamera();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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

            double surfRight = (width - diffw);
            double surfBottom = (height - diffH);

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},  REQUEST_WRITE_PERMISSION);
            } else {
                camera = Camera.open(useFacing);
                startPreview();
            }
        } else {
            camera = Camera.open(useFacing);
            startPreview();
        }
    }
    public void setCameraDisplayOrientation()
    {
        if (camera == null)
        {
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
        Bitmap resizedBitmap = null;

        if (cekSwafoto) {
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
        } else {

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
            resizedBitmap = Bitmap.createBitmap(
                    bm, cx, cy, (int) (width * 0.3), (height - diffH), matrix, false);
        }
        //bm.recycle();

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