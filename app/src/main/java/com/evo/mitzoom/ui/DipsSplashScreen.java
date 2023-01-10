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

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DipsSplashScreen extends AppCompatActivity {

    private static String TAG = "CEK_DipsSplashScreen";
    private Context mContext;
    private ImageView imgSplash;
    private TextView tvVersion;
    private SessionManager sessions;
    private SweetAlertDialog sweetAlertDialog = null;
    private RelativeLayout rlBGTransparant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_splash_screen);

        Log.e(TAG,"MASUK onCreate");

        imgSplash = (ImageView) findViewById(R.id.imgSplash);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        rlBGTransparant = (RelativeLayout) findViewById(R.id.rlBGTransparant);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        mContext = this;

        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),0);
            String version = info.versionName;
            version = "V "+version;
            tvVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sessions = new SessionManager(mContext);

        boolean cekConstain = getIntent().hasExtra("RESPONSECODE");
        if (cekConstain) {
            rlBGTransparant.setVisibility(View.VISIBLE);
            dialogShowError();
        } else {
            processNext();
        }

    }

    private void dialogShowError() {
        String bankName = getString(R.string.bank_name);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = (ImageView) dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = (TextView) dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = (TextView) dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = (Button) dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = (Button) dialogView.findViewById(R.id.btnConfirmDialog);

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

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialPhoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1500977"));
                startActivity(dialPhoneIntent);
                finishAffinity();
            }
        });

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OutApps();
            }
        });

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
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
        },5000);
    }

    private void startApp() {
        Log.e(TAG,"startApp");
        startActivity(new Intent(DipsSplashScreen.this, DipsChooseLanguage.class));
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
}