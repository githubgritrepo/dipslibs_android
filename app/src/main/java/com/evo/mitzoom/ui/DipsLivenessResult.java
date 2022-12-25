package com.evo.mitzoom.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DipsLivenessResult extends AppCompatActivity {

    private Context mContext;
    private SessionManager sessions;
    private ImageView mask_view;
    private String idDips;
    private TextView tip_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_liveness_result);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mContext = this;
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();

        if (idDips == null) {
            idDips = "";
        } else {
            if (!foregroundServiceRunning()) {
                idDips = "";
            }
        }

        mask_view = (ImageView) findViewById(R.id.mask_view);
        tip_text_view = (TextView) findViewById(R.id.tip_text_view);

        AnimationCall();

        byte[] resultImage = getIntent().getExtras().getByteArray("RESULT_IMAGE_AI");
        String imgBase64 = Base64.encodeToString(resultImage, Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(resultImage, 0, resultImage.length);
        mask_view.setImageBitmap(bitmap);

        processCaptureIdentifyAuth(imgBase64);
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

                        sessions.saveFLOW(1);
                        sessions.saveIdDips(idDips);

                        Intent intent = null;
                        if (!noCIF.isEmpty()) {
                            intent = new Intent(mContext, DipsWaitingRoom.class);
                        } else {
                            intent = new Intent(mContext, DipsSwafoto.class);
                            intent.putExtra("CUSTNAME", custName);
                        }
                        startActivity(intent);
                        finishAffinity();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else {
                    Intent intent = new Intent(mContext, DipsSplashScreen.class);
                    intent.putExtra("RESPONSECODE", response.code());
                    startActivity(intent);
                    finishAffinity();

                    /*if (response.code() < 500) {
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
                    }*/
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK","onFailure MESSAGE : "+t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}