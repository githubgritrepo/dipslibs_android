package com.evo.mitzoom.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.evo.mitzoom.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DipsSplashScreen extends AppCompatActivity {

    private static final String TAG = "CEK_DipsSplashScreen";
    private TextView tvVersion;
    private RelativeLayout rlBGTransparant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_splash_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.e(TAG,"MASUK onCreate");
        tvVersion = findViewById(R.id.tvVersion);
        rlBGTransparant = findViewById(R.id.rlBGTransparant);


        //Untuk mengambil version dari Apps
        try {
            PackageInfo info = getApplication().getPackageManager().getPackageInfo(getPackageName(),0);
            String version = info.versionName;
            version = "V "+version;
            tvVersion.setText(version);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //Cek balikan dari liveness
        boolean cekConstain = getIntent().hasExtra("RESPONSECODE");
        if (cekConstain) {
            //Jika ada, maka muncul pop up untuk nasabah yang ditolak
            rlBGTransparant.setVisibility(View.VISIBLE);
            dialogShowError();
        }
        else {
            processNext();
        }
    }

    private void dialogShowError() {
        String bankName = getString(R.string.bank_name);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);
        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);
        btnCancelDialog.setVisibility(View.VISIBLE);
        String tvBody1 = getString(R.string.warn_not_use_app);
        String tvBody2 = getString(R.string.warn_hub_calcenter);
        String bodyGab = tvBody1 + "\n\n" + tvBody2;
        bodyGab = bodyGab.replace("Bank XYZ",bankName).replace("XYZ Bank", bankName);
        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_warning));
        tvTitleDialog.setText(getString(R.string.failed));
        tvBodyDialog.setText(bodyGab);
        btnCancelDialog.setText(getString(R.string.call_center));
        btnConfirmDialog.setText(getString(R.string.exit));
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismissWithAnimation();
                Intent dialPhoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1500977"));
                startActivity(dialPhoneIntent);
                finishAffinity();
            }
        });
        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismissWithAnimation();
                OutApps();
            }
        });
    }

    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private void processNext() {
        Log.e("CEK","processNext");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setelah loading maka akan langsung berpindah ke home activity
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startApp();
                    }
                });

            }
        },1000);
    }

    private void startApp() {
        Log.e(TAG,"startApp");
        startActivity(new Intent(DipsSplashScreen.this, DipsChooseLanguage.class));
        finishAffinity();
    }
}