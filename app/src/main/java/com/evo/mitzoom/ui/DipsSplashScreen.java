package com.evo.mitzoom.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DipsSplashScreen extends AppCompatActivity {

    private Context mContext;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    public static final int REQUEST_READ_PERMISSION = 787;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_READ_PHONE_STATE = 787;
    private static final int ATTACHMENT_MANAGE_ALL_FILE = 308;
    private static final int REQUEST_ALL = 888;
    private ImageView imgSplash;
    private TextView tvVersion;
    private SessionManager sessions;
    private SweetAlertDialog sweetAlertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_splash_screen);

        imgSplash = (ImageView) findViewById(R.id.imgSplash);
        tvVersion = (TextView) findViewById(R.id.tvVersion);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mContext = this;

        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),0);
            String version = info.versionName;
            version = "V "+version;
            tvVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }

        sessions = new SessionManager(mContext);


    }

    @Override
    protected void onResume() {
        super.onResume();
        reqPermission();
    }

    private void reqPermission(){
        if (ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DipsSplashScreen.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_ALL);
        }
        else{
            processNext();
        }
    }

    private void processNext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doWork();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chooseLanguage();
                            }
                        });
                    }
                }).start();
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doWork();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chooseLanguage();
                        }
                    });
                }
            }).start();
        }
    }
    private void chooseLanguage() {
        imgSplash.setVisibility(View.INVISIBLE);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.choose_language, null);
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(DipsSplashScreen.this, SweetAlertDialog.NORMAL_TYPE);
        }
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.groupradio);
        Button btnNext = (Button) dialogView.findViewById(R.id.btnNext);
        btnNext.setBackgroundTintList(getResources().getColorStateList(R.color.Blue));
        radioGroup.clearCheck();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(DipsSplashScreen.this,
                            getResources().getString(R.string.select_language),
                            Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    sweetAlertDialog.dismissWithAnimation();
                    RadioButton radioButton = (RadioButton) radioGroup.findViewById(selectedId);
                    int idRb = radioButton.getId();
                    String langCode = "";
                    switch(idRb) {
                        case R.id.rbId:
                            langCode = "id";
                            sessions.saveLANG(langCode);
                            setLocale(DipsSplashScreen.this,langCode);
                            startApp();
                            break;
                        case R.id.rbEn:
                            langCode = "en";
                            sessions.saveLANG(langCode);
                            setLocale(DipsSplashScreen.this,langCode);
                            startApp();
                            break;
                    }
                }
            }
        });
    }
    private void startApp() {
        startActivity(new Intent(DipsSplashScreen.this, DipsCapture.class));
        finishAffinity();
    }
    private void doWork() {
        for (int progress=0; progress<=100; progress+=20) {
            try {
                Thread.sleep(800);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNext();
            } else {
                Toast.makeText(DipsSplashScreen.this,"Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbId:
                if (checked)
                    break;
            case R.id.rbEn:
                if (checked)
                    break;
        }
    }
}