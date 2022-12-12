package com.evo.mitzoom.ui.Alternative;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Fragments.frag_berita;
import com.evo.mitzoom.Fragments.frag_inputdata_new;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DipsSwafoto extends AppCompatActivity {

    private Context mContext;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    public static int CAM_ID = 0;
    private static final String KEY_USE_FACING = "use_facing";
    public static Integer useFacing = null;
    private Camera camera = null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private SessionManager sessions;
    private DisplayMetrics displayMetrics;
    String idDips;
    boolean isCust = false;
    String custName;
    private ImageView iconAudio;
    private Button btnChat;
    private Button btnFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mContext = this;
        sessions = new SessionManager(mContext);
        sessions.saveRTGS(null);
        sessions.saveCSID(null);
        sessions.saveIsSwafoto(false);
        idDips = sessions.getKEY_IdDips();
        isCust = sessions.getKEY_iSCust();
        String lang = sessions.getLANG();
        setLocale(this,lang);
        setContentView(R.layout.activity_dips_swafoto);

        CardView cardSurf = (CardView) findViewById(R.id.cardSurf);
        preview = (SurfaceView) findViewById(R.id.mySurface);
        iconAudio = (ImageView) findViewById(R.id.icon_audio);
        btnChat = (Button) findViewById(R.id.icon_chat);
        btnFile = (Button) findViewById(R.id.icon_file);

        btnFile.setBackgroundTintList(getResources().getColorStateList(R.color.btnFalse));
        btnChat.setBackgroundTintList(getResources().getColorStateList(R.color.btnFalse));
        btnFile.setClickable(false);
        btnChat.setClickable(false);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDisp = displayMetrics.widthPixels;
        int dyWidth = (int) Math.ceil(widthDisp / 2);

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);

        ViewGroup.LayoutParams lp = cardSurf.getLayoutParams();
        Log.e("CEK","dyWidth : "+dyWidth);
        /*lp.width = dyWidth;
        cardSurf.setLayoutParams(lp);*/

        boolean cekConstain = getIntent().getExtras().containsKey("RESULT_IMAGE_AI");
        byte[] resultImage = new byte[0];
        custName = getIntent().getExtras().getString("CUSTNAME");
        if (cekConstain) {
            resultImage = getIntent().getExtras().getByteArray("RESULT_IMAGE_AI");
        }

        Fragment fragment = new frag_inputdata_new();
        Bundle bundle = new Bundle();
        sessions.saveIsCust(isCust);
        bundle.putByteArray("RESULT_IMAGE_AI", resultImage);
        bundle.putString("CUSTNAME",custName);
        fragment.setArguments(bundle);
        getFragmentPage(fragment);

        SweetAlertDialog sweet = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
        sweet.setContentText(getString(R.string.prepare_id_card));
        sweet.setCancelable(false);
        sweet.setConfirmText(getString(R.string.btn_continue));
        sweet.show();
        Button btnConfirm = (Button) sweet.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(getResources().getColorStateList(R.color.Blue));

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("CEK","MASUK onResume");

        camera = Camera.open(useFacing);
        //startPreview();
        cameraConfigured = false;
        previewHolder();
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
        Log.e("CEK","MASUK ONPAUSE");
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
            initPreview(width, height);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

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

    private void setCameraDisplayOrientation(){
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
        } else {  // back-facing
            //result = (info.orientation - degrees + 360) % 360;
            result = 180;
        }
        camera.setDisplayOrientation(result);
    }

    private boolean getFragmentPage(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    public void onClickClose(View view) {
        OutApps();
    }

    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}