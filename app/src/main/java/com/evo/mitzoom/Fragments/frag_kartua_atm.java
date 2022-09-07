package com.evo.mitzoom.Fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_kartua_atm extends Fragment {
    private ImageView btnBack;
    private Context context;
    private AutoCompleteTextView etJenisLayanan;
    private EditText etNama,etNohp,etEmail,etAlamat,etTgl;
    private Button btnProses;
    private String Tgl, idDips;
    private CheckBox pernyataan;
    String[] sourceService;
    private LayoutInflater inflater;
    private View dialogView;
    private Button btnVerifikasi;
    private PinView otp;
    private TextView Timer, Resend_Otp;
    private String newString;
    public int getMinutes = 2;
    public int seconds = 60;
    public boolean running = true;
    private Handler handler = null;
    private Runnable myRunnable = null;
    private boolean pernyatan = false;
    private SessionManager session;
    private Handler handlerSuccess;
    private BroadcastReceiver smsReceiver = null;
    private JSONObject dataNasabah = null;
    private String numberOTP = "";
    private boolean flagTransfer = false;
    private int lasLenOTP;
    private boolean backSpaceOTP;
    private String no_handphone;

    private void APISaveForm(JSONArray jsonsIBMB) {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data",jsonsIBMB);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsons = new JSONObject();
        try {
            jsons.put("formCode","ATM");
            jsons.put("idDips",idDips);
            jsons.put("phone",no_handphone);
            jsons.put("payload",dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIService().saveForm(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int errCode = jsObj.getInt("err_code");
                        if (errCode == 0) {
                            JSONObject dataJs = jsObj.getJSONObject("data");
                            String idForm = dataJs.getString("idForm");
                            dataNasabah.put("idFormATM",idForm);
                            session.saveNasabah(dataNasabah.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context,"Gagal Save Form",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void verifyOTP() {
        JSONObject jsons = new JSONObject();
        try {
            String idForm = dataNasabah.getString("idFormATM");
            jsons.put("idForm",idForm);
            jsons.put("otpCode",numberOTP);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","PARAMS verifyOTP : "+jsons.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().VerifyOTP(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","response verifyOTP : "+response.code());
                if (response.body() != null) {
                    Log.e("CEK","response body verifyOTP : "+response.body().toString());
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int errCode = jsObj.getInt("err_code");
                        if (errCode == 0 ){
                            PopUpSuccesOtp();
                        } else {
                            String msg = jsObj.getString("message");
                            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
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

    private void resendOTP() {
        JSONObject jsons = new JSONObject();
        try {
            String idForm = dataNasabah.getString("idFormATM");
            jsons.put("idForm",idForm);
            jsons.put("phone",no_handphone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().ResendOTP(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int errCode = jsObj.getInt("err_code");
                        if (errCode == 0 ){
                            Toast.makeText(context, "Kode Terkirim ke nomor Hanphone Anda", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Kode Gagal Terkirim ke nomor Hanphone Anda", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECEIVE_SMS},
                1001);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_kartu_atm, container, false);
        btnBack = view.findViewById(R.id.btn_back_atm);
        etNama = view.findViewById(R.id.et_nama_atm);
        etNohp = view.findViewById(R.id.et_no_hp_atm);
        etEmail = view.findViewById(R.id.et_email_atm);
        etAlamat = view.findViewById(R.id.et_alamat_atm);
        etTgl = view.findViewById(R.id.et_tgl_transaksi_atm);
        btnProses = view.findViewById(R.id.btnProses_atm);
        pernyataan = view.findViewById(R.id.pernyataan_atm);
        etJenisLayanan = view.findViewById(R.id.et_jenisLayananATM);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();

        String dataJsonS = session.getNasabah();
        if (dataJsonS != null) {
            try {
                dataNasabah = new JSONObject(dataJsonS);
                no_handphone = dataNasabah.getString("noHP");
                etNohp.setText(no_handphone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sourceService = new String[]{"Blokir Kartu ATM", "Aktivasi Kartu ATM", "Minta Kartu ATM"};
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        Tgl = df.format(c.getTime());
        btnProses.setEnabled(false);
        etTgl.setText(Tgl);
        Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));

        ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(context,R.layout.list_item, sourceService);
        etJenisLayanan.setAdapter(adapterPopulation);
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    pernyatan = true;
                    Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    pernyatan = false;
                    Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
                    Log.d("CHECK","FALSE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        textWacther();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,true);
                getFragmentPage(new frag_service());
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,true,false);
            }
        });
    }
    private void textWacther(){
        etNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(charSequence, etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etNohp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                no_handphone = charSequence.toString();
                Mirroring(etNama.getText().toString(), charSequence,etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),charSequence,etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etAlamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),charSequence,Tgl,etJenisLayanan.getText().toString(),pernyatan,false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etJenisLayanan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jenisLayanan = (String) adapterView.getItemAtPosition(i);
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,jenisLayanan,pernyatan,false,false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                numberOTP = "";
                String dataSMS = intent.getExtras().getString("smsMessage");
                Log.e("CEK","MASUK dataSMS : "+dataSMS);
                String[] sp = dataSMS.split(" ");
                for (int i = 0; i < sp.length; i++) {
                    String word = sp[i].toString();
                    if(word.matches("\\d+(?:\\.\\d+)?")) {
                        numberOTP = word.replaceAll("[^0-9]", "");
                        if (numberOTP.length() == 6) {
                            otp.setText(numberOTP);
                            newString = myFilter(numberOTP);
                            otp.setText(newString);
                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(smsReceiver,new IntentFilter("getotp"));

    }

    @Override
    public void onPause() {
        Log.e("CEK","MASUK onPause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(smsReceiver);
        super.onPause();
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
                lasLenOTP = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String wordOTP = s.toString();
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    String getNumberOTP=wordOTP.replaceAll("[^0-9]", "");
                    if (getNumberOTP.length() > 1 && getNumberOTP.length() <= 6) {
                        getNumberOTP = getNumberOTP.substring(getNumberOTP.length()-1,getNumberOTP.length());
                    }
                    if (numberOTP.length() < 6) {
                        numberOTP += getNumberOTP;
                    }
                    MirroringOTP(numberOTP,false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpaceOTP = lasLenOTP > s.length();
                Log.e("CEK", "backSpaceOTP : " + backSpaceOTP);
                if (backSpaceOTP) {
                    int lenOTP = numberOTP.length();
                    if (lenOTP > 0) {
                        numberOTP = numberOTP.substring(0, lenOTP - 1);
                    }
                }
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
                    MirroringOTP(otp.getText().toString(),true);
                    sweetAlertDialog.dismiss();
                    verifyOTP();
                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    resendOTP();
                }
            }
        });
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
                Fragment fragment = new frag_portfolio();
                getFragmentPage(fragment);
                sweetAlertDialog.dismiss();
            }
        },5000);
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
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void MirroringOTP(CharSequence s, boolean bool){
        Log.d("OTP","ini hit OTP");
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(s);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",412);
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
    private void Mirroring(CharSequence nama, CharSequence nohp, CharSequence email, CharSequence alamat, CharSequence tanggal, CharSequence jenisLayanan,boolean pernyataan_, boolean submit, boolean back){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama);
            jsonArray.put(nohp);
            jsonArray.put(email);
            jsonArray.put(alamat);
            jsonArray.put(tanggal);
            jsonArray.put(jenisLayanan);
            jsonArray.put(pernyataan_);
            jsonArray.put(submit);
            jsonArray.put(back);
            jsons.put("idDips",idDips);
            jsons.put("code",411);
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
                if (submit) {
                    PopUp();
                    try {
                        Thread.sleep(1500);
                        APISaveForm(jsonArray);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
}
