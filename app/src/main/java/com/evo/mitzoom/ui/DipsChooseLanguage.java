package com.evo.mitzoom.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.DownloadTaskHelper;
import com.evo.mitzoom.Helper.LocaleHelper;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.Model.Request.JsonCaptureIdentify;
import com.evo.mitzoom.Model.Response.CaptureIdentify;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

public class DipsChooseLanguage extends AppCompatActivity {

    public static final int REQUEST_CODE_LIVENESS = 1001;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    public static final int REQUEST_READ_PERMISSION = 787;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_READ_PHONE_STATE = 787;
    private static final int ATTACHMENT_MANAGE_ALL_FILE = 308;
    protected static final int REQUEST_BATTERY_OP = 0x49ff;
    private static final int REQUEST_ALL = 888;
    private SessionManager sessions;
    private Context mContext;
    private RadioGroup radioGroup;
    private Button btnNext;
    private TextView tvVersion;
    private RelativeLayout rlload;
    private String idDips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_choose_language);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        mContext = this;
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        sessions.saveKTP(null);
        sessions.saveSWAFOTO(null);
        sessions.saveNPWP(null);
        sessions.saveTTD(null);

        if (idDips == null) {
            idDips = "";
        } else {
            if (!foregroundServiceRunning()) {
                idDips = "";
            }
        }

        radioGroup = (RadioGroup) findViewById(R.id.groupradio);
        btnNext = (Button) findViewById(R.id.btnNext);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        rlload = (RelativeLayout) findViewById(R.id.rlload);

        radioGroup.clearCheck();

        onClickListener();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("CEK","MASUK onResume");
        reqPermission();

        long unixTime = System.currentTimeMillis() / 1000L;
        long expiredTimesAuth = sessions.getExpiredTimeAdvanceAI();
        String cekExp = String.valueOf(expiredTimesAuth);
        if (cekExp.length() > 10) {
            expiredTimesAuth = Long.parseLong(cekExp.substring(0,10));
        }
        if (expiredTimesAuth == 0 || expiredTimesAuth < unixTime) {
            sessions.saveAuthAdvanceAI(null,0);
            new AsyncAuth().execute();
        }

        Log.e("CEK","openBatteryOptimizationDialogIfNeeded");
        Log.e("CEK","isOptimizingBattery : "+isOptimizingBattery());
        Log.e("CEK","getBatteryOptimizationPreferenceKey : "+getPreferences().getBoolean(getBatteryOptimizationPreferenceKey(), true));

        if (isOptimizingBattery() && getPreferences().getBoolean(getBatteryOptimizationPreferenceKey(), true)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            Uri uri = Uri.parse("package:" + getPackageName());
            intent.setData(uri);
            try {
                startActivityForResult(intent, REQUEST_BATTERY_OP);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Your device does not support opting out of battery optimization", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(OutboundServiceNew.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void onClickListener() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(mContext,
                                    getResources().getString(R.string.select_language),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
                else {
//                    sweetAlertDialog.dismissWithAnimation();
                    RadioButton radioButton = (RadioButton) radioGroup.findViewById(selectedId);
                    int idRb = radioButton.getId();
                    switch(idRb) {
                        case R.id.rbId:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String langCode = "id";
                                    sessions.saveLANG(langCode);
                                    //LocaleHelper.setLocale(DipsChooseLanguage.this,langCode);
                                    setLocale(DipsChooseLanguage.this,langCode);
                                    startApp();
                                    /*Intent intent = new Intent(mContext, DipsCameraSource.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(intent, 10);*/
                                    //radioGroup.clearCheck();
                                }
                            });
                            break;
                        case R.id.rbEn:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String langCode = "en";
                                    sessions.saveLANG(langCode);
                                    //LocaleHelper.setLocale(DipsChooseLanguage.this,langCode);
                                    setLocale(DipsChooseLanguage.this,langCode);
                                    /*Intent intent = new Intent(mContext, DipsCameraSource.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(intent, 10);*/
                                    startApp();
                                    //radioGroup.clearCheck();
                                }
                            });
                            break;
                    }
                }
            }
        });
    }

    private void startApp() {
        /*Intent intent = new Intent(mContext,DipsCapture.class);
        startActivity(intent);
        finishAffinity();*/

        String licenseAI = sessions.getAuthAdvanceAI();
        Log.e("CEK","licenseAI : "+licenseAI);
        if (licenseAI != null) {
            startLivenessDetection(licenseAI);
        } else {
            Toast.makeText(mContext,"Terjadi Kesalahan", Toast.LENGTH_LONG).show();
            onResume();
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

    private void reqPermission(){
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_ALL);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()){
                    Intent getpermission = new Intent();
                    getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("CEK","MASUK PERMISSION_GRANTED");
            } else {
                Toast.makeText(mContext,"Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startLivenessDetection(String licenseAI) {
        Market yourMarket = Market.Indonesia;
        GuardianLivenessDetectionSDK.init(getApplication(), yourMarket);
        GuardianLivenessDetectionSDK.letSDKHandleCameraPermission();
        String yourLicense = licenseAI;
        Log.e("CEK","yourLicense : "+yourLicense);
        GuardianLivenessDetectionSDK.setCameraType(CameraType.FRONT);// The back camera is CameraType.BACK
        GuardianLivenessDetectionSDK.setActionSequence(true, Detector.DetectionType.BLINK);
        GuardianLivenessDetectionSDK.setResultPictureSize(600); // Settable input range: [300,1000], unit: pixels
        GuardianLivenessDetectionSDK.setActionTimeoutMills(20000);
        String checkResult = GuardianLivenessDetectionSDK.setLicenseAndCheck(yourLicense);
        Log.e("CEK","checkResult : "+checkResult);
        if ("SUCCESS".equals(checkResult)) {
            startActivityForResult(new Intent(this, LivenessActivity.class), REQUEST_CODE_LIVENESS);
        } else {
            Log.e("LivenessSDK", "License check failed:" + checkResult);
        }
    }

    private String imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIVENESS) {
            if (LivenessResult.isSuccess()) {// Success
                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();// picture
                String imgBase64 = imgtoBase64(livenessBitmap);
                byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);

                //processCaptureIdentifyAuth(imgBase64);
                sessions.saveNoCIF(null);

                Intent intent = new Intent(mContext, DipsLivenessResult.class);
                intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                intent.putExtra("idDips", idDips);
                startActivity(intent);
                finishAffinity();

                /*rlload.setVisibility(View.VISIBLE);
                String livenessId = LivenessResult.getLivenessId();// livenessId
                processCaptureIdentify(imgBase64);*/
            } else {// Failure
                rlload.setVisibility(View.GONE);
                //String errorCode = LivenessResult.getErrorCode();// error code
                String errorMsg = LivenessResult.getErrorMsg();// error message
                //String transactionId = LivenessResult.getTransactionId(); // Transaction number, which can be used to troubleshoot problems with us
                if (errorMsg != null) {
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_BATTERY_OP) {

        }
    }

    protected boolean isOptimizingBattery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            return pm != null
                    && !pm.isIgnoringBatteryOptimizations(getPackageName());
        } else {
            return false;
        }
    }

    private String getBatteryOptimizationPreferenceKey() {
        @SuppressLint("HardwareIds") String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return "show_battery_optimization" + (device == null ? "" : device);
    }

    private void setNeverAskForBatteryOptimizationsAgain() {
        getPreferences().edit().putBoolean(getBatteryOptimizationPreferenceKey(), false).apply();
    }

    protected SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private class AsyncAuth extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            processGetAuthAdvanceAI();
            return null;
        }
    }

    private void processGetAuthAdvanceAI() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("licenseEffectiveSeconds",86400);
            jsons.put("applicationId","default,com.evo.mitzoom");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String AccessKey = "9daf9d6e9dfe6cdd";//bankvictoria_ebd
        //String AccessKey = "75140e9a1d9c161f";//evolusi_test

        Log.e("CEK","REUQEST AUTH AI : "+jsons.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIServiceAdvanceAI().AuthLicenseLiveness(requestBody,AccessKey).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","RESPONSE AUTH AI : "+response.code());
                if (response.isSuccessful()) {
                    Log.e("CEK","RESPONSE AUTH AI SUCCESS : "+response.body().toString());
                    try {
                        JSONObject dataObj = new JSONObject(response.body().toString());
                        String sttcode = dataObj.getString("code");
                        if (sttcode.equals("SUCCESS")) {
                            String dataLicense = dataObj.getJSONObject("data").getString("license");
                            long expireTimestamp = dataObj.getJSONObject("data").getLong("expireTimestamp");
                            sessions.saveAuthAdvanceAI(dataLicense,expireTimestamp);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK","onFailure AUTH AI : "+t.getMessage());
            }
        });
    }

    private void processCaptureIdentifyAuth(String imgBase64) {
        if (!NetworkUtil.hasDataNetwork(mContext)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);

        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips","");
            jsons.put("image",imgBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*String filename = "CaptureAuth_"+idDips+".txt";
        try {
            createTemporaryFile(jsons.toString(),filename);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.CaptureAuth(requestBody);
        Log.e("CEK","REQUEST CALL : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","RESPONSE CODE: "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","dataS: "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONObject dataCustomer = dataObj.getJSONObject("data").getJSONObject("customer");
                        JSONObject dataToken = dataObj.getJSONObject("data").getJSONObject("token");

                        boolean isSwafoto = dataCustomer.getBoolean("isSwafoto");

                        String noCIF = "";
                        boolean isCust;
                        if (dataCustomer.isNull("noCif")) {
                            isCust = false;
                        } else {
                            isCust = true;
                            noCIF = dataCustomer.getString("noCif");
                        }
                        String custName = dataCustomer.getString("namaLengkap");
                        String idDipsNew = dataCustomer.getString("idDips");
                        Log.e("CEK","idDipsNew : "+idDipsNew+" | idDips : "+idDips);
                        /*if (idDips != null && OutboundService.mSocket != null && idDipsNew != idDips) {
                            OutboundService.leaveOutbound(idDips);
                        }*/
                        String accessToken = dataToken.getString("accessToken");

                        sessions.saveIdDips(idDipsNew);
                        sessions.saveIsCust(isCust);
                        sessions.saveAuthToken(accessToken);

                        idDips = idDipsNew;

                        sessions.saveIdDips(idDips);

                        Intent intent = null;
                        if (!noCIF.isEmpty()) {
                            intent = new Intent(mContext, DipsWaitingRoom.class);
                            intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                        } else {
                            intent = new Intent(mContext, DipsSwafoto.class);
                            intent.putExtra("RESULT_IMAGE_AI", bytePhoto);
                            intent.putExtra("CUSTNAME", custName);
                        }
                        startActivity(intent);
                        finishAffinity();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else {
                    if (response.code() < 500) {
                        if (response.code() == 401) {
                            Intent intent = new Intent(mContext, DipsSplashScreen.class);
                            intent.putExtra("RESPONSECODE", response.code());
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            String dataErr = null;
                            try {
                                dataErr = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.e("CEK", "dataErr : " + dataErr);
                            if (dataErr != null) {
                                try {
                                    JSONObject dataObj = new JSONObject(dataErr);
                                    if (dataObj.has("message")) {
                                        String message = dataObj.getString("message");
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mContext, R.string.msg_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(mContext, R.string.msg_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processCaptureIdentify(String imgBase64) {

        if (!NetworkUtil.hasDataNetwork(mContext)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        JsonCaptureIdentify jsons = new JsonCaptureIdentify();
        jsons.setIdDips(idDips);
        jsons.setImage(imgBase64);

        ApiService API = Server.getAPIService();
        Call<CaptureIdentify> call = API.CaptureAdvanceAI(jsons);
        Log.e("CEK","REQUEST CALL : "+call.request().url());
        call.enqueue(new Callback<CaptureIdentify>() {
            @Override
            public void onResponse(Call<CaptureIdentify> call, Response<CaptureIdentify> response) {
                Log.e("CEK","RESPONSE CODE: "+response.code());
                if (response.body() != null) {
                    Log.e("CEK","Response Body : "+response.body().toString());
                }
                if (response.isSuccessful() && response.body() != null) {
                    int errCode = response.body().getErr_code();
                    if (errCode == 0) {
                        SweetAlertDialog sweetDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
                        sweetDialog.setTitleText("Warning!!!");
                        sweetDialog.setContentText(getResources().getString(R.string.not_using_dips));
                        sweetDialog.setConfirmText("OK");
                        sweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetDialog.dismissWithAnimation();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                                startActivity(intent);
                                finish();
                            }
                        });
                        sweetDialog.show();
                        return;
                    }

                    String idDipsOld = sessions.getKEY_IdDips();
                    /*if (idDipsOld != null && OutboundService.mSocket != null) {
                        OutboundService.leaveOutbound(idDipsOld);
                    }*/

                    boolean isCust = response.body().isCustomer();
                    String custName = response.body().getName();
                    String idDips = response.body().getIdDips();
                    String sessionName = response.body().getDataSession().getNameSession();
                    String sessionPass = response.body().getDataSession().getPass();

                    sessions.saveIdDips(idDips);
                    sessions.saveIsCust(isCust);

                    Intent intent = new Intent(mContext,DipsWaitingRoom.class);
                    sessions.saveIsCust(isCust);
                    intent.putExtra("CUSTNAME",custName);
                    intent.putExtra("idDips", idDips);
                    intent.putExtra("SessionName", sessionName);
                    intent.putExtra("SessionPass", sessionPass);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(mContext, R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CaptureIdentify> call, Throwable t) {
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}