package com.evo.mitzoom.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.io.IOException;
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

public class DipsChooseLanguage extends AppCompatActivity {

    public static final int REQUEST_CODE_LIVENESS = 1001;
    public static final int READ_EXTERNAL_STORAGE = 780;
    public static final int REQUEST_CAMERA = 781;
    public static final int WRITE_EXTERNAL_STORAGE = 782;
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
    private String dataTnC = "";
    private boolean flagViewTNC = false;

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

            Log.e("CEK","MASUK IF reqPermission");
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_NUMBERS}, REQUEST_ALL);
        } else {
            Log.e("CEK","MASUK ELSE reqPermission");
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()){
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
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                    }
                }
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                }
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()){
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivityForResult(getpermission,ATTACHMENT_MANAGE_ALL_FILE);
                    }
                }
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
        GuardianLivenessDetectionSDK.setResultPictureSize(300); // Settable input range: [300,1000], unit: pixels
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

    private class AsyncProcess extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            processGetTNC();
            return null;
        }
    }

    private void processGetTNC() {
        Server.getAPIService().getTNC(1).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String cekdataTnC = dataObj.getJSONObject("data").getString("data");
                        if (cekdataTnC.contains("{")) {
                            JSONObject labelTNC = new JSONObject(cekdataTnC);
                            String language = sessions.getLANG();
                            if (language.equals("id")) {
                                dataTnC = labelTNC.getString("labelIdn");
                            } else {
                                dataTnC = labelTNC.getString("labelEng");
                            }
                        } else {
                            dataTnC = cekdataTnC;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = "";
                    if (response.body() != null) {
                        String dataS = response.body().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            msg = dataObj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (response.errorBody().toString().isEmpty()) {
                            String dataS = response.errorBody().toString();
                            try {
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String dataS = null;
                            try {
                                dataS = response.errorBody().string();
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopUpTnc(){
        flagViewTNC = true;
        Log.e("CEK","MASUK PopUpTnc");
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        //if (sweetAlertDialogTNC == null) {
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialogTNC = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialogTNC.setCustomView(dialogView);
        sweetAlertDialogTNC.hideConfirmButton();
        sweetAlertDialogTNC.setCancelable(true);
        //}
        TextView tvBody = (TextView) dialogView.findViewById(R.id.tvBody);
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);

        if (!dataTnC.isEmpty()) {
            tvBody.setText(Html.fromHtml(dataTnC, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int idx = source.indexOf(",");
                    idx += 1;
                    String new_source = source.substring(idx);
                    byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Drawable d = new BitmapDrawable(((Activity) mContext).getResources(), bitmap);
                    int intH = d.getIntrinsicHeight();
                    int intW = d.getIntrinsicWidth();
                    d.setBounds(0, 0, intW, intH);
                    return d;
                }
            }, null));
        }
        btn.setClickable(false);

        sweetAlertDialogTNC.show();

        int width = (int)(((Activity)mContext).getResources().getDisplayMetrics().widthPixels);
        int height = (int)(((Activity)mContext).getResources().getDisplayMetrics().heightPixels);

        Log.e("CEK","PopUpTnc width : "+width+" | height : "+height);
        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);
        Log.e("CEK","PopUpTnc newWidth : "+newWidth+" | newHeight : "+newHeight);

        //sweetAlertDialogTNC.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        sweetAlertDialogTNC.getWindow().setLayout(newWidth,newHeight);
        sweetAlertDialogTNC.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.e("CEK","onDismiss");
                flagViewTNC = false;
            }
        });
        sweetAlertDialogTNC.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.e("CEK","onCancel");
                flagViewTNC = false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialogTNC.dismissWithAnimation();
            }
        });

    }

}