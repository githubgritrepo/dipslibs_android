package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_cardless extends Fragment {

    private Context mContext;
    AutoCompleteTextView et_source_accountpager;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    private Handler handler = null;
    private Runnable myRunnable = null;
    public int getMinutes = 2;
    public int seconds = 60;
    public boolean running = true;
    private ImageView btnBack;
    private Button btnProses;
    private EditText et_nominal;
    private EditText et_description;
    private String newString, idDips, rsd_1 = "", rsd_2 = "", rsd_3 = "";
    private SessionManager session;
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        session = new SessionManager(mContext);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_cardless, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btn_back);
        et_source_accountpager = (AutoCompleteTextView) view.findViewById(R.id.et_source_account);
        et_nominal = (EditText) view.findViewById(R.id.et_nominal);
        et_description = (EditText) view.findViewById(R.id.et_description);
        btnProses= (Button) view.findViewById(R.id.btnProses);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.list_item_souceacc,sourceAcc);
        et_source_accountpager.setAdapter(adapterSourceAcc);
        et_source_accountpager.setBackground(mContext.getResources().getDrawable(R.drawable.blue_button_background));
        textWatcher();
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rekSumberdana = et_source_accountpager.getText().toString();
                String nominal = et_nominal.getText().toString();
                String descrip = et_description.getText().toString();

                if (rekSumberdana.trim().isEmpty() || nominal.trim().isEmpty() || descrip.trim().isEmpty()) {
                    Toast.makeText(mContext, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                Mirroring(rsd_1,rsd_2,rsd_3,et_nominal.getText().toString(),et_description.getText().toString(),true,false);
                PopUp();

            }
        });
    }

    private void textWatcher(){
        et_source_accountpager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sumber_dana = (String) adapterView.getItemAtPosition(i);
                String[] sumber = sumber_dana.split("\n");
                rsd_1 = sumber[0];
                rsd_2 = sumber[1];
                rsd_3 = sumber[3];
                Mirroring(rsd_1,rsd_2,rsd_3,et_nominal.getText().toString(),et_description.getText().toString(),false,false);
            }
        });
        et_nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_nominal.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_nominal.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_nominal.setText(formatted);
                Mirroring(rsd_1,rsd_2,rsd_3,formatted,et_description.getText().toString(),false,false);
                et_nominal.setSelection(formatted.length());
                et_nominal.addTextChangedListener(this);
            }
        });
        et_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(rsd_1,rsd_2,rsd_3,et_nominal.getText().toString(),charSequence,false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void PopUp(){
        View dialogView = getLayoutInflater().inflate(R.layout.item_otp, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btnVerifikasi = (Button) dialogView.findViewById(R.id.btnVerifikasi);
        TextView Timer = (TextView) dialogView.findViewById(R.id.timer_otp);
        TextView Resend_Otp = (TextView) dialogView.findViewById(R.id.btn_resend_otp);
        PinView otp = (PinView) dialogView.findViewById(R.id.otp);
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
                    MirroringOTP(s,false);
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
                    Toast.makeText(mContext, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    MirroringOTP(otp.getText().toString(),true);
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
                    Toast.makeText(mContext, "Kode Terkirim", Toast.LENGTH_SHORT).show();
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
    private void PopUpSuccesOtp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.otp_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.otp_content));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = new frag_resi_cardless();
                getFragmentPage(fragment);
                sweetAlertDialog.dismiss();
            }
        },5000);
    }
    public static BigDecimal parseCurrencyValue(String value) {
        try {
            String replaceRegex = String.format("[%s,.\\s]", Objects.requireNonNull(numberFormat.getCurrency()).getDisplayName());
            String currencyValue = value.replaceAll(replaceRegex, "");
            return new BigDecimal(currencyValue);
        } catch (Exception e) {
            Log.e("MyApp", e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void Mirroring(CharSequence rek_sumber_1, CharSequence rek_sumber_2, CharSequence rek_sumber_3, CharSequence nominal_, CharSequence ket, Boolean btnsubmit, Boolean btnback){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(rek_sumber_1);
            jsonArray.put(rek_sumber_2);
            jsonArray.put(rek_sumber_3);
            jsonArray.put(nominal_);
            jsonArray.put(ket);
            jsonArray.put(btnsubmit);
            jsonArray.put(btnback);
            jsons.put("idDips",idDips);
            jsons.put("code",362);
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
    private void MirroringOTP(CharSequence s, boolean bool){
        Log.d("OTP","ini hit OTP");
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(s);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",492);
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