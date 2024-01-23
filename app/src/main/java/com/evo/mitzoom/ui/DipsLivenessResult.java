package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.OutboundServiceNew;
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
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class DipsLivenessResult extends AppCompatActivity {

    private static final String TAG = "CEK_DipsLivenessResult";
    private Context mContext;
    private SessionManager sessions;
    private ImageView mask_view;
    private String idDips;
    private TextView tip_text_view;
    private RelativeLayout llCircle;
    private ImageView imgCheck;
    public static final int REQUEST_CODE_LIVENESS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        String lang = sessions.getLANG();
        setLocale(this, lang);
        //LocaleHelper.setLocale(this,lang);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_dips_liveness_result);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (idDips == null) {
            idDips = "";
        } else {
            if (!foregroundServiceRunning()) {
                idDips = "";
            }
        }

        mask_view = findViewById(R.id.mask_view);
        tip_text_view = findViewById(R.id.tip_text_view);
        llCircle = findViewById(R.id.llCircle);
        imgCheck = findViewById(R.id.imgCheck);

        AnimationCall();

        byte[] resultImage = getIntent().getExtras().getByteArray("RESULT_IMAGE_AI");
        if (resultImage.length > 0) {
            String imgBase64 = Base64.encodeToString(resultImage, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(resultImage, 0, resultImage.length);
            mask_view.setImageBitmap(bitmap);
            //processH5Advance(imgBase64);
            processCaptureIdentifyAuth(imgBase64);
        }
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

    private void AnimationCall(){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            int count = 0;

            @Override
            public void run() {
                count++;

                if (count == 1)
                {
                    tip_text_view.setText(getResources().getString(R.string.please_wait));
                }
                else if (count == 2)
                {
                    tip_text_view.setText(getResources().getString(R.string.please_wait1));
                }
                else if (count == 3)
                {
                    tip_text_view.setText(getResources().getString(R.string.please_wait2));
                }
                else if (count == 4)
                {
                    tip_text_view.setText(getResources().getString(R.string.please_wait3));
                }

                if (count == 4)
                    count = 0;

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
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

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.CaptureAuth(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        try {
                            /*String dataHard = "{\n" +
                                    "        \"id\": 930,\n" +
                                    "        \"namaLengkap\": \"NUR RAHMAWATI\",\n" +
                                    "        \"gelar\": \"\",\n" +
                                    "        \"alamat\": \"DESA PURBASARI\",\n" +
                                    "        \"noHp\": \"085349596636\",\n" +
                                    "        \"idDips\": \"NEsNem0nV15apqeO\",\n" +
                                    "        \"noCif\": \"80704972\",\n" +
                                    "        \"email\": \"NR.RAHMAWATI04@GMAIL.COM\",\n" +
                                    "        \"nik\": \"6201054410990003\",\n" +
                                    "        \"jenisKelamin\": \"PEREMPUAN\",\n" +
                                    "        \"foto\": \"/home/hadi/dips/customer/faceData/foto-NEsNem0nV15apqeO.png\",\n" +
                                    "        \"isSwafoto\": true,\n" +
                                    "        \"meta\": \"{\\\"filename\\\":\\\"foto-NEsNem0nV15apqeO.png\\\"}\",\n" +
                                    "        \"blacklist\": false,\n" +
                                    "        \"branchCode\": \"088\",\n" +
                                    "        \"createdAt\": \"2023-10-11T03:36:08.491Z\",\n" +
                                    "        \"updatedAt\": \"2023-10-30T09:35:09.730Z\"\n" +
                                    "    }";*/
                            /*String dataHard = "{\n" +
                                    "\"id\":301,\n" +
                                    "\"namaLengkap\":\"MOHAMMAD RAFII BURHANUDDIN\",\n" +
                                    "\"gelar\":null,\n" +
                                    "\"alamat\":\"JL. GANESHA TENGAH NO.84\",\n" +
                                    "\"noHp\":\"081215702727\",\n" +
                                    "\"idDips\":\"sz76KGMmz3N0lghG\",\n" +
                                    "\"noCif\":\"51691813\",\n" +
                                    "\"email\":\"MOHAMMADRAFII480@GMAIL.COM\",\n" +
                                    "\"nik\":\"3374150804000005\",\n" +
                                    "\"jenisKelamin\":\"LAKI-LAKI\",\n" +
                                    "\"foto\":\"/home/hadi/dips/customer/faceData/foto-sz76KGMmz3N0lghG.png\",\n" +
                                    "\"isSwafoto\":true,\n" +
                                    "\"meta\":\"{\\\"filename\\\":\\\"foto-sz76KGMmz3N0lghG.png\\\"}\",\n" +
                                    "\"blacklist\":false,\n" +
                                    "\"branchCode\":\"020\",\n" +
                                    "\"createdAt\":\"2023-07-12T09:42:59.572Z\",\n" +
                                    "\"updatedAt\":\"2023-07-12T09:44:22.294Z\"\n" +
                                    "}";*/
                            /*String dataHard = "{\n" +
                                    "        \"id\": 410,\n" +
                                    "        \"namaLengkap\": \"FAJAR DWI SAKTI\",\n" +
                                    "        \"gelar\": null,\n" +
                                    "        \"alamat\": \"TAMAN WISMA ASRI BLOK BB 31/14\",\n" +
                                    "        \"noHp\": \"087778297027\",\n" +
                                    "        \"idDips\": \"q9uRDwcR6px5tiVx\",\n" +
                                    "        \"noCif\": \"08800207\",\n" +
                                    "        \"email\": \"FAJAR_101188@YAHOO.COM\",\n" +
                                    "        \"nik\": \"3275031011880017\",\n" +
                                    "        \"jenisKelamin\": \"Laki - Laki\",\n" +
                                    "        \"foto\": \"/home/hadi/dips/customer/faceData/foto-q9uRDwcR6px5tiVx.png\",\n" +
                                    "        \"isSwafoto\": true,\n" +
                                    "        \"meta\": \"{\\\"filename\\\":\\\"foto-q9uRDwcR6px5tiVx.png\\\"}\",\n" +
                                    "        \"blacklist\": false,\n" +
                                    "        \"branchCode\": \"088\",\n" +
                                    "        \"createdAt\": \"2023-07-27T07:13:11.376Z\",\n" +
                                    "        \"updatedAt\": \"2023-07-27T07:16:00.389Z\"\n" +
                                    "    }";*/
                            /*String dataHard = "{\n" +
                                    "\"id\":544,\n" +
                                    "\"namaLengkap\":\"KAHFI GHIFARI\",\n" +
                                    "\"gelar\":\"S1\",\n" +
                                    "\"alamat\":\"PERUM PESONA CILEBUT I BLOK i-03 NO1\",\n" +
                                    "\"noHp\":\"081398850764\",\n" +
                                    "\"idDips\":\"6ou3snkozn7h0ttC\",\n" +
                                    "\"noCif\":\"29411169\",\n" +
                                    "\"email\":\"kahfighifari1146@gmail.com\",\n" +
                                    "\"nik\":\"3201040705960007\",\n" +
                                    "\"jenisKelamin\":\"LAKI-LAKI\",\n" +
                                    "\"foto\":\"/home/hadi/dips/customer/faceData/foto-6ou3snkozn7h0ttC.png\",\n" +
                                    "\"isSwafoto\":true,\n" +
                                    "\"meta\":\"{\\\"filename\\\":\\\"foto-6ou3snkozn7h0ttC.png\\\"}\",\n" +
                                    "\"blacklist\":false,\n" +
                                    "\"branchCode\":\"088\",\n" +
                                    "\"createdAt\":\"2023-08-18T03:28:48.571Z\",\n" +
                                    "\"updatedAt\":\"2023-08-18T03:31:48.615Z\"\n" +
                                    "}";*/

                            //======= DATA KAHFI GHIFARI NASABAH BARU =======///
                            /*String dataHard = "{\n" +
                                    "\"id\":544,\n" +
                                    "\"namaLengkap\":\"KAHFI GHIFARI\",\n" +
                                    "\"gelar\":null,\n" +
                                    "\"alamat\":null,\n" +
                                    "\"noHp\":null,\n" +
                                    "\"idDips\":\"6ou3snkozn7h0ttC\",\n" +
                                    "\"noCif\":null,\n" +
                                    "\"email\":null,\n" +
                                    "\"nik\":\"3201040705960007\",\n" +
                                    "\"jenisKelamin\":\"LAKI-LAKI\",\n" +
                                    "\"foto\":\"/home/hadi/dips/customer/faceData/foto-6ou3snkozn7h0ttC.png\",\n" +
                                    "\"isSwafoto\":true,\n" +
                                    "\"meta\":\"{\\\"filename\\\":\\\"foto-6ou3snkozn7h0ttC.png\\\"}\",\n" +
                                    "\"blacklist\":false,\n" +
                                    "\"branchCode\":null,\n" +
                                    "\"createdAt\":\"2023-08-18T03:28:48.571Z\",\n" +
                                    "\"updatedAt\":\"2023-08-18T03:31:48.615Z\"\n" +
                                    "}";*/
                            //JSONObject dataCustomer = new JSONObject(dataHard);
                            JSONObject dataCustomer = dataObj.getJSONObject("data").getJSONObject("customer");
                            JSONObject dataToken = dataObj.getJSONObject("data").getJSONObject("token");

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
                            boolean blacklist = dataCustomer.getBoolean("blacklist");
                            if (blacklist) {
                                Intent intent = new Intent(mContext, DipsSplashScreen.class);
                                intent.putExtra("BLACKLIST", blacklist);
                                startActivity(intent);
                                finishAffinity();
                                return;
                            }
                            llCircle.setVisibility(View.VISIBLE);
                            imgCheck.setVisibility(View.VISIBLE);
                            tip_text_view.setVisibility(View.GONE);
                            boolean isSwafoto = dataCustomer.getBoolean("isSwafoto");
                            String accessToken = "";
                            String exchangeToken = "";
                            if (dataToken.has("accessToken")) {
                                accessToken = dataToken.getString("accessToken");
                                exchangeToken = dataToken.getString("exchangeToken");
                            } else {
                                accessToken = dataToken.getString("token");
                                exchangeToken = dataToken.getString("exchange");
                            }

                            /*noCIF = "";
                            if (noCIF.isEmpty()) {*/
                                /*idDipsNew = "sz76KGMmz3N0lghG";
                                noCIF = "51691813";
                                isCust = false;
                                isSwafoto = true;*/
                            //}

                            sessions.saveIdDips(idDipsNew);
                            sessions.saveIsCust(isCust);
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                            sessions.saveNoCIF(noCIF);
                            sessions.saveNasabahName(custName);
                            sessions.saveNasabah(dataCustomer.toString());
                            sessions.saveIsSwafoto(isSwafoto);

                            idDips = idDipsNew;

                            sessions.saveIdDips(idDips);

                            boolean finalIsSwafoto = isSwafoto;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = null;
                                            String noCif = sessions.getNoCIF();
                                            //intent = new Intent(mContext, DipsSwafoto.class);
                                            if (!noCif.isEmpty()) {
                                                intent = new Intent(mContext, DipsConnectionForm.class);
                                            } else if (finalIsSwafoto) {
                                                intent = new Intent(mContext, DipsWaitingRoom.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            } else {
                                                intent = new Intent(mContext, DipsSwafoto.class);
                                                intent.putExtra("formCode", 4); //4 Upload KTP, 22 Swafoto
                                                intent.putExtra("OCRKTP",true);
                                            }
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                    });

                                }
                            }, 1000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else {
                    Intent intent = new Intent(mContext, DipsSplashScreen.class);
                    intent.putExtra("BLACKLIST", true);
                    startActivity(intent);
                    finishAffinity();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                if (t.getMessage().contains("connect")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processCaptureIdentifyAuth(imgBase64);
                        }
                    }, 4000);
                }
            }
        });
    }

    private void processH5Advance(String imgBase64) {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("image",imgBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIService().H5Advance(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    processCaptureIdentifyAuth(imgBase64);
                }
                else {
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            msg = dataObj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String dataS = null;
                        try {
                            dataS = response.errorBody().string();
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String licenseAI = sessions.getAuthAdvanceAI();
                    if (licenseAI != null) {
                        dialogShowError(licenseAI);
                    } else {
                        Toast.makeText(mContext,"Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        onResume();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dialogShowError(String license) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);
        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);
        btnCancelDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_warning));
        tvTitleDialog.setText(getString(R.string.title_gagal_livenes));
        tvBodyDialog.setText(getString(R.string.body_gagal_livenes));
        btnConfirmDialog.setText(getString(R.string.button_gagal_livenes));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismissWithAnimation();
                startLivenessDetection(license);
            }
        });
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
        String checkResult = GuardianLivenessDetectionSDK.setLicenseAndCheck(yourLicense);
        if ("SUCCESS".equals(checkResult)) {
            startActivityForResult(new Intent(this, LivenessActivity.class), REQUEST_CODE_LIVENESS);
        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIVENESS) {
            if (LivenessResult.isSuccess()) {// Success
                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();// picture
                String imgBase64 = imgtoBase64(livenessBitmap);
                byte[] bytePhoto = Base64.decode(imgBase64, Base64.NO_WRAP);
                try {
                    File pathPhotoLiveness = createTemporaryFile(bytePhoto);
                    String filePath = pathPhotoLiveness.getAbsolutePath();
                    sessions.savePhotoLiveness(filePath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                sessions.saveNoCIF(null);
                Intent intent = new Intent(mContext, DipsLivenessResult.class);
                intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                intent.putExtra("idDips", idDips);
                startActivity(intent);
                finishAffinity();
            } else {// Failure
                String errorMsg = LivenessResult.getErrorMsg();// error message
                //String transactionId = LivenessResult.getTransactionId(); // Transaction number, which can be used to troubleshoot problems with us
                if (errorMsg != null) {
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private File createTemporaryFile(byte[] byteImage) throws Exception {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.close();

        return mediaFile;
    }

    private String imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return encodedImage;
    }
}