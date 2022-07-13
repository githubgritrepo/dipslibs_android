package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_aktivasi_ibmb extends Fragment {
    private Context context;
    private EditText UserId, Password, Konfirmasi_password, Mpin, Konfirmasi_mpin;
    private TextView Timer, Resend_Otp;
    private Button btnProses;
    private LayoutInflater inflater;
    private View dialogView;
    private Button btnVerifikasi;
    private PinView otp;
    private int selPos;
    private String oldString, newString;
    private Handler handlerSuccess;
    public int getMinutes = 2;
    public int seconds = 60;
    public boolean running = true;
    private String idDips, UserId2, Password2, Konfirmasi_password2, Mpin2, Konfirmasi_mpin2, Timer2, Resend_Otp2;
    private SessionManager session;
    private Handler handler = null;
    private Runnable myRunnable = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aktivasi_ibmb, container, false);
        UserId = view.findViewById(R.id.et_userId);
        Password = view.findViewById(R.id.et_password_ibmb);
        Konfirmasi_password = view.findViewById(R.id.et_konfirmasi_password_ibmb);
        Mpin = view.findViewById(R.id.et_mpin);
        Konfirmasi_mpin = view.findViewById(R.id.et_konfirmasi_mpin);
        btnProses = view.findViewById(R.id.btnProsesIbmb);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        session.saveKTP(null);
        session.saveNPWP(null);
        session.saveTTD(null);
        UserId.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9._-]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });
        Password.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });
        Konfirmasi_password.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });

        ///TextWatcher
        UserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,s,Password.getText().toString(),Konfirmasi_password.getText().toString(),Mpin.getText().toString(),Konfirmasi_mpin.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !Password.getText().toString().isEmpty() && !Konfirmasi_password.getText().toString().isEmpty() && !Mpin.getText().toString().isEmpty() && !Konfirmasi_mpin.getText().toString().isEmpty()){
                    btnProses.setClickable(true);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                }
                else {
                    btnProses.setClickable(false);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                }

            }
        });
        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,UserId.getText().toString(),s,Konfirmasi_password.getText().toString(),Mpin.getText().toString(),Konfirmasi_mpin.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!UserId.getText().toString().isEmpty() && !s.toString().isEmpty() && !Konfirmasi_password.getText().toString().isEmpty() && !Mpin.getText().toString().isEmpty() && !Konfirmasi_mpin.getText().toString().isEmpty()){
                    btnProses.setClickable(true);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                }
                else {
                    btnProses.setClickable(false);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                }

            }
        });
        Mpin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,UserId.getText().toString(),Password.getText().toString(),Konfirmasi_password.getText().toString(),s,Konfirmasi_mpin.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!UserId.getText().toString().isEmpty() && !Password.getText().toString().isEmpty() && !Konfirmasi_password.getText().toString().isEmpty() && !s.toString().isEmpty() && !Konfirmasi_mpin.getText().toString().isEmpty()){
                    btnProses.setClickable(true);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                }
                else {
                    btnProses.setClickable(false);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                }

            }
        });

        //Filter Konfirmasi
       Konfirmasi_password.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               Mirroring(false,UserId.getText().toString(),Password.getText().toString(),s,Mpin.getText().toString(),Konfirmasi_mpin.getText().toString());
           }

           @Override
           public void afterTextChanged(Editable s) {
               Password2 = Password.getText().toString();
                if (!s.toString().equals(Password2)){
                    Konfirmasi_password.setError(getResources().getString(R.string.error_pass));
                }
               else if (!UserId.getText().toString().isEmpty() && !Password.getText().toString().isEmpty() && !s.toString().isEmpty() && !Mpin.getText().toString().isEmpty() && !Konfirmasi_mpin.getText().toString().isEmpty()){
                   btnProses.setClickable(true);
                   btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
               }
               else {
                   btnProses.setClickable(false);
                   btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
               }

           }
       });
       Konfirmasi_mpin.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               Mirroring(false,UserId.getText().toString(),Password.getText().toString(),Konfirmasi_password.getText().toString(),Mpin.getText().toString(),s);

           }

           @Override
           public void afterTextChanged(Editable s) {
               Mpin2 = Mpin.getText().toString();
               if (!s.toString().equals(Mpin2)){
                   Konfirmasi_mpin.setError(getResources().getString(R.string.error_mpin));
               }
               else if (!UserId.getText().toString().isEmpty() && !Password.getText().toString().isEmpty() && !Konfirmasi_password.getText().toString().isEmpty() && !Mpin.getText().toString().isEmpty() && !s.toString().isEmpty()){
                   btnProses.setClickable(true);
                   btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
               }
               else {
                   btnProses.setClickable(false);
                   btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
               }
           }
       });

       btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserId2 = UserId.getText().toString();
                Password2 = Password.getText().toString();
                Konfirmasi_password2 = Konfirmasi_password.getText().toString();
                Mpin2 = Mpin.getText().toString();
                Konfirmasi_mpin2 = Konfirmasi_mpin.getText().toString();

                if (UserId2.isEmpty()){
                    UserId.setError(getResources().getString(R.string.error_field));
                }
                else if (Password2.isEmpty()){
                    Password.setError(getResources().getString(R.string.error_field));
                }
                else if (Konfirmasi_password2.isEmpty()){
                    Konfirmasi_password.setError(getResources().getString(R.string.error_field));
                }
                else if (Mpin2.isEmpty()){
                    Mpin.setError(getResources().getString(R.string.error_field));
                }
                else if (Konfirmasi_mpin2.isEmpty()){
                    Konfirmasi_mpin.setError(getResources().getString(R.string.error_field));
                }
                else if (Password2.length() < 8){
                    Toast.makeText(context, getResources().getString(R.string.min_password), Toast.LENGTH_SHORT).show();
                }
                else if(Mpin2.length() < 6){
                    Toast.makeText(context, getResources().getString(R.string.min_MPIN), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Mirroring(true,UserId2,Password2,Konfirmasi_password2,Mpin2,Konfirmasi_mpin2);
                    PopUp();
                }
            }
        });
    }
    private void PopUp(){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_otp,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        btnVerifikasi = dialogView.findViewById(R.id.btnVerifikasi);
        Timer = dialogView.findViewById(R.id.timer_otp);
        Resend_Otp = dialogView.findViewById(R.id.btn_resend_otp);
        otp = dialogView.findViewById(R.id.otp);
        otp.setAnimationEnable(true);
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*selPos = otp.getSelectionStart();
                oldString = myFilter(s.toString());*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    Mirroring2(false, s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                newString = myFilter(s.toString());
                otp.removeTextChangedListener(this);
                handler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        otp.setText(newString);
                    }
                };
                otp.addTextChangedListener(this);
                handler.postDelayed(myRunnable, 1500);
                if (otp.length() == 6 || otp.length() == 0){
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Log.d("TAG","STOP Loop");
                }
            }
        });

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Mirroring2(true, otp.getText().toString());
                    sweetAlertDialog.dismiss();
                    PopUpSuccesOtp();
                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    Toast.makeText(context, "Kode Terkirim", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public String myFilter(String s) {
        String digits;
        digits = s.replaceAll("[0-9]", "*");
        if (s.equals("")) return "";
        return digits;
    }
    private void PopUpSuccesOtp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.otp_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.otp_content));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        handlerSuccess = new Handler();
        handlerSuccess.postDelayed(new Runnable() {
            @Override
            public void run() {
                Mirroring3(true);
                Fragment fragment = new frag_aktivasi_berhasil();
                getFragmentPage(fragment);
                sweetAlertDialog.dismiss();
            }
        },5000);
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    public void runTimer(TextView timer_run, TextView resend) {
        Handler handlerTimer = new Handler();
        handlerTimer.post(new Runnable() {
            @Override
            public void run() {
                int minutes = getMinutes;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0 && minutes == 0){
                    running = false;
                    resend.setClickable(true);
                } else if (seconds == 0 && minutes > 0){
                    seconds = 59;
                }
                if (seconds == 59) {
                    getMinutes--;
                }
                handlerTimer.postDelayed(this,1000);
            }
        });
    }
    private void Mirroring(Boolean bool, CharSequence user_id, CharSequence pass, CharSequence konfirm_pass, CharSequence mpin, CharSequence konfirm_mpin){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(user_id);
            jsonArray.put(pass);
            jsonArray.put(konfirm_pass);
            jsonArray.put(mpin);
            jsonArray.put(konfirm_mpin);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",10);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring2(Boolean bool,CharSequence s){
        Log.d("OTP","ini hit OTP");
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(s);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",11);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring3(Boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",12);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
}
