package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingActivity extends AppCompatActivity {
    private final String TAG = "CEK_RatingActivity";
    private ImageView thumbs_up, thumbs_down;
    private Button btnSend;
    private RatingBar rating;
    private EditText kritik;
    private Context mContext;
    private SessionManager sessions;
    private String thumb = "";
    private String idDips = "";
    private String idAgent = "";
    private RelativeLayout rlprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        idDips = sessions.getKEY_IdDips();
        idAgent = sessions.getCSID();
        setLocale(this, lang);
        //LocaleHelper.setLocale(this,lang);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rating);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        Log.e(TAG,"idDips : "+idDips+" | idAgent : "+idAgent);
        rlprogress = findViewById(R.id.rlprogress);
        rating = findViewById(R.id.ratingBar);
        rating.setClickable(false);
        rating.setFocusable(false);
        rating.setRating(0);
        thumbs_up = findViewById(R.id.thumbs_up);
        thumbs_down = findViewById(R.id.thumbs_down);
        btnSend = findViewById(R.id.btnSend);
        kritik = findViewById(R.id.et_kritik);
        btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.btnFalse));
        thumbs_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating.setClickable(false);
                rating.setFocusable(false);
                rating.setRating(0);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.zm_button));
                thumbs_down.setImageResource(R.drawable.thumbs_down2);
                thumbs_up.setImageResource(R.drawable.thumbs_up);
                thumb = "thumbDown";
            }
        });
        thumbs_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating.setClickable(true);
                rating.setFocusable(true);
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.zm_button));
                thumbs_down.setImageResource(R.drawable.thumbs_down);
                thumb = "thumbUp";
            }
        });


       btnSend.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.e(TAG,"thumb : "+thumb+" | rating getNumStars : "+rating.getNumStars()+" | rating getRating : "+rating.getRating());
               Log.e(TAG,"idDips : "+idDips+" | idAgent : "+idAgent);
               if (idDips != null && idAgent != null) {
                   showProgress(true);
                   new AsyncRating().execute();
               }
           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void PopUp(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_success));
        tvBodyDialog.setText(getString(R.string.rating_pop_up_content));
        btnConfirmDialog.setText(getString(R.string.done));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(RatingActivity.this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismiss();
                sweetAlertDialog.cancel();
                OutApps();
            }
        });
    }

    private class AsyncRating extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RatingAgent();
            return null;
        }
    }

    private void RatingAgent() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idAgent",Integer.parseInt(idAgent));
            jsons.put("idDips",idDips);
            jsons.put("rating",thumb);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG,"RatingAgent : "+ jsons);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().RateAgent(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG,"RatingAgent ResponCode : "+response.code());
                if (response.isSuccessful()) {
                    RatingApps();
                } else {
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showProgress(false);
            }
        });
    }

    private void RatingApps() {
        String getKritik = kritik.getText().toString().trim();
        int getStar = (int) rating.getRating();
        String stars = String.valueOf(getStar);
        Log.e(TAG,"KRITIK : "+getKritik+" | stars : "+stars);
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("rating",stars);
            jsons.put("comment",getKritik);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG,"RatingApps : "+ jsons);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().RateApp(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG,"RatingApps ResponCode : "+response.code());
                showProgress(false);
                if (response.isSuccessful()) {
                    sessions.clearIdDiPSCSID();
                    PopUp();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showProgress(false);
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

    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}