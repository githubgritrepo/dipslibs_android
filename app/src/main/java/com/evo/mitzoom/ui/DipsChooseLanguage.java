package com.evo.mitzoom.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DipsChooseLanguage extends AppCompatActivity {

    private final String TAG = "CEK_DipsChooseLanguage";
    public static final int REQUEST_CODE_LIVENESS = 1001;
    protected final static int REQUEST_VIDEO_AUDIO_CODE = 1010;
    public static final int READ_EXTERNAL_STORAGE = 780;
    public static final int REQUEST_CAMERA = 781;
    public static final int WRITE_EXTERNAL_STORAGE = 782;
    public static final int REQUEST_RECEIVE_SMS = 783;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int ATTACHMENT_MANAGE_ALL_FILE = 308;
    protected static final int REQUEST_BATTERY_OP = 0x49ff;
    private static final int REQUEST_ALL = 888;
    private SessionManager sessions;
    private Context mContext;
    private RadioGroup radioGroup;
    private Button btnNext;
    private TextView tvVersion;
    private RelativeLayout rlprogress;
    private String idDips;
    private boolean isFlagALL_FILES_ACCESS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_dips_choose_language);

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

        radioGroup = findViewById(R.id.groupradio);
        btnNext = findViewById(R.id.btnNext);
        tvVersion = findViewById(R.id.tvVersion);
        rlprogress = findViewById(R.id.rlprogress);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        reqPermission();

        if (isFlagALL_FILES_ACCESS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!Settings.canDrawOverlays(this)) {

                    if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT))) {
                        final Intent intent =new Intent("miui.intent.action.APP_PERM_EDITOR");
                        intent.setClassName("com.miui.securitycenter",
                                "com.miui.permcenter.permissions.PermissionsEditorActivity");
                        intent.putExtra("extra_pkgname", getPackageName());
                        startActivity(intent);
                    }else {
                        Intent overlaySettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(overlaySettings, 1);
                    }
                }
            }
        }

        long unixTime = System.currentTimeMillis() / 1000L;
        long expiredTimesAuth = sessions.getExpiredTimeAdvanceAI();
        String cekExp = String.valueOf(expiredTimesAuth);
        if (cekExp.length() > 10) {
            expiredTimesAuth = Long.parseLong(cekExp.substring(0,10));
        }
        if (expiredTimesAuth == 0 || expiredTimesAuth < unixTime) {
            showProgress(true);
            sessions.saveAuthAdvanceAI(null,0);
            new AsyncAuth().execute();
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
                    RadioButton radioButton = radioGroup.findViewById(selectedId);
                    int idRb = radioButton.getId();
                    if (idRb == R.id.rbId) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String langCode = "id";
                                sessions.saveLANG(langCode);
                                //LocaleHelper.setLocale(DipsChooseLanguage.this,langCode);
                                setLocale(DipsChooseLanguage.this, langCode);
                                    /*Intent intent = new Intent(mContext, DipsCameraSource.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(intent, 100001);*/
                                startApp();
                                //radioGroup.clearCheck();
                            }
                        });
                    } else if (idRb == R.id.rbEn) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String langCode = "en";
                                sessions.saveLANG(langCode);
                                //LocaleHelper.setLocale(DipsChooseLanguage.this,langCode);
                                setLocale(DipsChooseLanguage.this, langCode);
                                    /*Intent intent = new Intent(mContext, DipsCameraSource.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(intent, 100001);*/
                                startApp();
                                //radioGroup.clearCheck();
                            }
                        });
                    }
                }
            }
        });
    }

    private void startApp() {
        String licenseAI = sessions.getAuthAdvanceAI();

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
        int id = view.getId();
        if (id == R.id.rbId) {
            if (checked)
                return;

            if (checked) {
            }
        } else if (id == R.id.rbEn) {
            if (checked) {
            }
        }
    }

    private void reqPermission(){
        String readImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
        }
        if (ActivityCompat.checkSelfPermission(mContext, readImagePermission) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,readImagePermission,Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_NUMBERS,Manifest.permission.RECORD_AUDIO}, REQUEST_ALL);
            }
        } else {

            if (ActivityCompat.checkSelfPermission(mContext, readImagePermission) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{readImagePermission}, READ_EXTERNAL_STORAGE);
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG,"MASUK ELSE reqPermission RECEIVE_SMS");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_RECEIVE_SMS);
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_VIDEO_AUDIO_CODE);
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                if (!Environment.isExternalStorageManager() && isFlagALL_FILES_ACCESS == false){
                    isFlagALL_FILES_ACCESS = true;

                    Intent getpermission = new Intent();
                    getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {

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
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager() && isFlagALL_FILES_ACCESS == false){
                        isFlagALL_FILES_ACCESS = true;
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
                    }
                }
            } else {
                Toast.makeText(mContext,"Permission Denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG,"READ_EXTERNAL_STORAGE PERMISSION_GRANTED");
                /*if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG,"READ_EXTERNAL_STORAGE PERMISSION_GRANTED CAMERA");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                    }
                }*/
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_VIDEO_AUDIO_CODE);
                }*/
            }
        } else if (requestCode == REQUEST_VIDEO_AUDIO_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                } else {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()){
                                Intent getpermission = new Intent();
                                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
                            }
                        }
                    }
                }*/
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()){
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
                    }
                }*/
            }
        }
    }

    public void startLivenessDetection(String licenseAI) {
        Market yourMarket = Market.Indonesia;
        GuardianLivenessDetectionSDK.init(getApplication(), yourMarket);
        GuardianLivenessDetectionSDK.letSDKHandleCameraPermission();
        String yourLicense = licenseAI;
        GuardianLivenessDetectionSDK.setCameraType(CameraType.FRONT);// The back camera is CameraType.BACK
        GuardianLivenessDetectionSDK.setActionSequence(true, Detector.DetectionType.BLINK);
        GuardianLivenessDetectionSDK.setResultPictureSize(300); // Settable input range: [300,1000], unit: pixels
        GuardianLivenessDetectionSDK.setActionTimeoutMills(20000);
        GuardianLivenessDetectionSDK.isDetectOcclusion(true);
        String checkResult = GuardianLivenessDetectionSDK.setLicenseAndCheck(yourLicense);

        if ("SUCCESS".equals(checkResult)) {
            startActivityForResult(new Intent(this, LivenessActivity.class), REQUEST_CODE_LIVENESS);
        } else {

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
                sessions.saveNoCIF(null);
                Intent intent = new Intent(mContext, DipsLivenessResult.class);
                intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                intent.putExtra("idDips", idDips);
                startActivity(intent);
                finishAffinity();
            } else {// Failure
                //String errorCode = LivenessResult.getErrorCode();// error code
                String errorMsg = LivenessResult.getErrorMsg();// error message
                //String transactionId = LivenessResult.getTransactionId(); // Transaction number, which can be used to troubleshoot problems with us
                if (errorMsg != null) {
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_BATTERY_OP) {
            setNeverAskForBatteryOptimizationsAgain();
        }
    }

    private void setNeverAskForBatteryOptimizationsAgain() {
        getPreferences().edit().putBoolean(getBatteryOptimizationPreferenceKey(), false).apply();
    }

    protected boolean isOptimizingBattery() {
        final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName());
        }
        return true;
    }

    private String getBatteryOptimizationPreferenceKey() {
        @SuppressLint("HardwareIds") String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return "show_battery_optimization" + (device == null ? "" : device);
    }

    protected SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private class AsyncAuth extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //processGetAuthAdvanceAI();
            processAPIGetAuthAdvanceAI();
            return null;
        }
    }

    private void processAPIGetAuthAdvanceAI() {
        String packageName = getApplicationContext().getPackageName();
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("applicationId",packageName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIService2().APIAuthLicenseLiveness(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });

                if (response.isSuccessful()) {

                    try {
                        JSONObject dataObj = new JSONObject(response.body().toString());
                        String dataLicense = dataObj.getJSONObject("data").getString("license");
                        long expireTimestamp = dataObj.getJSONObject("data").getLong("expireTimestamp");
                        sessions.saveAuthAdvanceAI(dataLicense,expireTimestamp);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });

            }
        });
    }

    private void processGetAuthAdvanceAI() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("licenseEffectiveSeconds",86400);
            jsons.put("applicationId","default,com.evo.mitzoom");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String AccessKey = "9daf9d6e9dfe6cdd";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIServiceAdvanceAI().AuthLicenseLiveness(requestBody,AccessKey).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

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

            }
        });
    }

    private void showProgress(Boolean bool){

        if (bool){
            rlprogress.setVisibility(View.VISIBLE);
        }else {
            rlprogress.setVisibility(View.GONE);
        }
    }

}