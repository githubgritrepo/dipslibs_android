package com.evo.mitzoom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
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

import com.evo.mitzoom.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import us.zoom.sdk.ZoomVideoSDK;

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
    private byte[] dataImage = new byte[0];
    private static int degreeFront = 0;
    private Context mContext;
    private OrientationEventListener morientationEventListener;
    private int mOrientation =  -1;
    private int rotationInDegree = 1;
    private int optimalWidth = 0;
    private int optimalHeight = 0;

    private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_camera);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initialElements();

        mContext = this;

        transHolder = transPreview.getHolder();
        transHolder.setFormat(PixelFormat.TRANSPARENT);
        transHolder.addCallback(surfaceCallbackTrans);
        transHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_BACK);

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (data.length > 0) {
                            dataImage = data;

                            Bitmap bitmap = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
                            Log.d("CEK","bitmap width : "+bitmap.getWidth());
                            Log.d("CEK","bitmap height : "+bitmap.getHeight());
                            Bitmap bitmapCrop = resizeAndCropCenter(bitmap, 320, false);

                            try {
                                File mediaFile = createTemporaryFile(dataImage);
                                try {
                                    String pathFile = mediaFile.getAbsolutePath();
                                    ExifInterface exif = new ExifInterface(pathFile);
                                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                    Log.d("CEK","rotation : "+rotation);
                                    /*rotationInDegree = exifToDegrees(rotation);
                                    Log.d("CEK","rotationInDegree : "+rotationInDegree);*/

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

                            String imgBase64 = imageRotateBase64(bitmapCrop);

                            byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);
                            Intent returnIntent = getIntent();
                            returnIntent.putExtra("result_camera",bytePhoto);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
                    }
                });
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

    }

    private void initialElements() {
        imgSwitch = findViewById(R.id.imgSwitch);
        preview = findViewById(R.id.mySurface);
        transPreview = findViewById(R.id.transSurface);
        btnTake = findViewById(R.id.takePicture);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        OrientationEvent();
    }

    private void previewHolder(){
        Log.d("CEK","previewHolder");
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void OrientationEvent() {
        if (morientationEventListener == null) {
            morientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int orientation) {
                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                        }
                    }
                    else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                        }
                    }
                    else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                        }
                    }
                    else { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        }
                    }

                    if (lastOrientation != mOrientation) {
                        AsyncChangeRotation asyncChangeRotation = new AsyncChangeRotation();
                        asyncChangeRotation.execute(mOrientation);
                    }
                }
            };
        }
        if (morientationEventListener.canDetectOrientation()) {
            morientationEventListener.enable();
        }
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

        if (morientationEventListener != null) {
            morientationEventListener.disable();
        }

        super.onPause();
    }

    private void changeRotation(int orientation) {
        //Camera.Parameters parameters = camera.getParameters();
        switch (orientation) {
            case ORIENTATION_PORTRAIT_NORMAL:
                Log.v("CameraActivity", "Orientation = 90");
                /*parameters.setPreviewSize(optimalWidth, optimalHeight);
                camera.setParameters(parameters);*/
                rotationInDegree = 90;
                break;
            case ORIENTATION_LANDSCAPE_NORMAL:
                Log.v("CameraActivity", "Orientation = 0");
                /*parameters.setPreviewSize(optimalHeight, optimalWidth);
                camera.setParameters(parameters);*/
                rotationInDegree = 0;
                break;
            case ORIENTATION_PORTRAIT_INVERTED:
                Log.v("CameraActivity", "Orientation = 270");
                /*parameters.setPreviewSize(optimalWidth, optimalHeight);
                camera.setParameters(parameters);*/
                rotationInDegree = 270;
                break;
            case ORIENTATION_LANDSCAPE_INVERTED:
                Log.v("CameraActivity", "Orientation = 180");
                /*parameters.setPreviewSize(optimalHeight, optimalWidth);
                camera.setParameters(parameters);*/
                rotationInDegree = 180;
                break;
        }
    }

    private class AsyncChangeRotation extends AsyncTask<Integer,Void,Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            Log.d("CEK","integers :"+integers);
            int orientation = integers[0];
            Log.d("CEK","orientation :"+orientation);
            changeRotation(orientation);
            return null;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;
        Log.d("CEK","targetRatio : "+targetRatio);
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        Log.d("CEK","minDiff : "+minDiff);

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            Log.d("CEK","size.width : "+size.width);
            Log.d("CEK","size.height : "+size.height);
            double ratio = (double) size.width / size.height;
            Log.d("CEK","ratio : "+ratio);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                Log.d("CEK","MASUK IF");
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
                break;
            }
        }

        Log.d("CEK","optimalSize : "+optimalSize);
        Log.d("CEK","minDiff 2 : "+minDiff);

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        Log.d("CEK","minDiff results : "+minDiff);
        Log.d("CEK","optimalSize results : "+optimalSize);
        return optimalSize;
    }

    private void initPreview(int width, int height) {
        Log.d("CEK","initPreview width : "+width+" | height : "+height);
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
                        CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                        Integer orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        Log.d("CEK","orientation initPreview : "+orientation);
                        int idCamera = Integer.parseInt(id);
                        Log.d("CEK","idCamera initPreview : "+idCamera);
                        if (idCamera == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            Log.d("CEK","MASUK DEPAN");
                            if (orientation == 90) {
                                camera.setDisplayOrientation(270);
                            } else {
                                camera.setDisplayOrientation(orientation);
                            }
                            break;
                        } else if (idCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            Log.d("CEK","MASUK BELAKANG");
                            camera.setDisplayOrientation(orientation);
                            break;
                        }

                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (!cameraConfigured) {
                Log.d("CEK","width : "+width);
                Log.d("CEK","height : "+height);
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                Camera.Size size = getOptimalPreviewSize(sizes, width, height);
                optimalWidth = size.width;
                optimalHeight = size.height;
                Log.d("CEK","size width : "+optimalWidth);
                Log.d("CEK","size height : "+optimalHeight);
                if (size != null) {
                    parameters.setPreviewSize(optimalWidth, optimalHeight);
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

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Log.d("CEK","surfaceCreated");
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Log.d("CEK","surfaceChanged");
            initPreview(width, height);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Log.d("CEK","surfaceDestroyed");
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

            double diffH = Math.ceil(height / 4);

            RectF rect = new RectF((float) 100,(float) diffH,(float) width-100,(float) (height-diffH));

            Canvas canvas = transHolder.lockCanvas();
            canvas.drawRect(rect,paint);

            transHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };

    private String imageRotateBase64(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        Log.d("CEK","rotationInDegree : "+rotationInDegree);
        if(rotationInDegree != 0) {
            matrix.postRotate(rotationInDegree);
        }
        /*if (useFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            matrix.setRotate(rotationInDegree);
        } else {
            matrix.setRotate(90);
        }
        matrix.postScale(-1, 1);*/

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