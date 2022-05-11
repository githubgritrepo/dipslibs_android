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
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DipsSplashScreen extends AppCompatActivity {

    private Context mContext;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ImageView imgSplash;
    private SessionManager sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_splash_screen);

        imgSplash = (ImageView) findViewById(R.id.imgSplash);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mContext = this;

        sessions = new SessionManager(mContext);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            PermissionCamera();
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
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DipsSplashScreen.this, SweetAlertDialog.NORMAL_TYPE);
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
        finish();
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

    private void PermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
            } else {
                //Toast.makeText(mContext,"Location Permission Denied", Toast.LENGTH_LONG).show();
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