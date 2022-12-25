package com.evo.mitzoom.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.OutboundService;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import ai.advance.liveness.lib.CameraType;
import ai.advance.liveness.lib.Detector;
import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.lib.Market;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DipsSplashScreen extends AppCompatActivity {

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
                startApp();

            }
        },5000);
    }

    private void startApp() {
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