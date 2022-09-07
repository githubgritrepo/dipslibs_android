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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_new_account_cs2 extends Fragment {
    private Context context;
    private ImageView btnBack;
    private Button btnProses;
    private String idDips, rekSumberdana, nama, tgl, produk, nominal;
    private int typeSend;
    private CheckBox pernyataan;
    private LinearLayout icon_isi_form, icon_konfirmasi_data, icon_resi;
    private TextView tv_reksumber, tv_namaLengkap, tv_tgl, tv_produk, tv_nominal;
    private SessionManager session;
    private LayoutInflater inflater;
    private View dialogView;
    private Handler handlerSuccess;
    private Button btnVerifikasi;
    private TextView Timer, Resend_Otp;
    private PinView otp;
    private Handler handler;
    private Runnable myRunnable;
    public int getMinutes = 2;
    public int seconds = 60;
    private int selPos;
    private String oldString, newString;
    public boolean running = true;
    private boolean pernyataan_ = false;
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
            jsons.put("formCode","newaccount");
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
                            dataNasabah.put("idFormNewAccount",idForm);
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
            String idForm = dataNasabah.getString("idFormNewAccount");
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
            String idForm = dataNasabah.getString("idFormNewAccount");
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
        View view = inflater.inflate(R.layout.frag_new_account_cs2, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btn_back_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        icon_konfirmasi_data = view.findViewById(R.id.icon_konfirmasi_data);
        icon_resi = view.findViewById(R.id.icon_resi);
        btnProses = view.findViewById(R.id.btnProses_konfirmasi_data_cs);
        tv_reksumber = view.findViewById(R.id.RekeningSumber_cs);
        tv_namaLengkap = view.findViewById(R.id.nama_lengkap_new_acc_cs);
        tv_produk = view.findViewById(R.id.tipe_produk_rek_new_acc_cs);
        tv_tgl = view.findViewById(R.id.tgl_pembukaan_new_acc_cs);
        tv_nominal = view.findViewById(R.id.setoran_awal_new_acc_cs);
        pernyataan = view.findViewById(R.id.pernyataan_cs);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Bundle arg = getArguments();
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        rekSumberdana = arg.getString("rek_sumber_dana");
        typeSend = arg.getInt("typesend");
        nama = arg.getString("nama_lengkap");
        tgl = arg.getString("tgl");
        produk = arg.getString("produk");
        nominal = arg.getString("nominal");
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    pernyataan_ = true;
                    Mirroring(pernyataan_, false,false);
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    pernyataan_ = false;
                    Log.d("CHECK","FALSE");
                    Mirroring(pernyataan_, false,false);
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        tv_reksumber.setText(rekSumberdana);
        tv_namaLengkap.setText(nama);
        tv_produk.setText(produk);
        tv_tgl.setText(tgl);
        tv_nominal.setText("Rp"+nominal);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(pernyataan_, false,true);
                Fragment fragment;
                fragment = new frag_new_account_cs();
                Bundle bundle = new Bundle();
                bundle.putString("rek_sumber_dana",rekSumberdana);
                bundle.putString("nama_lengkap",nama);
                bundle.putString("tgl",tgl);
                bundle.putString("produk",produk);
                bundle.putString("nominal",nominal);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(pernyataan_, true,false);
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

    private void PopUpOTP(){
        String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
        String noHandphone = no_handphone.replace(sub_no_handphone,"XXX");

        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_otp,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        TextView textIBMB = (TextView) dialogView.findViewById(R.id.textIBMB);
        String contentText = textIBMB.getText().toString();
        Log.e("CEK","contentText : "+contentText+" | no_handphone : "+noHandphone);
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        Log.e("CEK","contentText new : "+contentText);
        textIBMB.setText(contentText);

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
                Matcher matcher = pattern.matcher(wordOTP);
                if (matcher.find()) {
                    String getNumberOTP=wordOTP.replaceAll("[^0-9]", "");
                    if (getNumberOTP.length() > 1 && getNumberOTP.length() <= 6) {
                        getNumberOTP = getNumberOTP.substring(getNumberOTP.length()-1,getNumberOTP.length());
                    }
                    if (numberOTP.length() < 6) {
                        numberOTP += getNumberOTP;
                    }
                    Mirroring2(numberOTP, false);
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
                handler.postDelayed(myRunnable, 1500);
                otp.addTextChangedListener(this);
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
                    Mirroring2(otp.getText().toString(),true);
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
        sweetAlertDialog.setCancelable(true);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        handlerSuccess = new Handler();
        handlerSuccess.postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                Fragment fragment;
                fragment = new frag_new_account_cs_resi();
                Bundle bundle = new Bundle();
                bundle.putString("nama_lengkap",nama);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        },2000);
    }
    public String myFilter(String s) {
        String digits;
        digits = s.replaceAll("[0-9]", "*");
        if (s.equals("")) return "";
        return digits;
    }
    public void runTimer(TextView timer_run, TextView resend) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
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
                handler.postDelayed(this,1000);
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
    private void Mirroring2(CharSequence s, Boolean bool){
        Log.d("OTP","ini hit OTP");
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(s);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",364);
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
    private void Mirroring(Boolean bool_pernyataan, Boolean bool_submit, Boolean bool_back){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool_pernyataan);
            jsonArray.put(bool_submit);
            jsonArray.put(bool_back);
            jsons.put("idDips",idDips);
            jsons.put("code",363);
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
                if (bool_submit) {
                    icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                    PopUpOTP();
                    try {
                        Thread.sleep(1500);
                        JSONArray dataForm = new JSONArray();
                        dataForm.put(rekSumberdana);
                        dataForm.put(typeSend);
                        dataForm.put(nama);
                        dataForm.put(tgl);
                        dataForm.put(produk);
                        dataForm.put(nominal);
                        APISaveForm(dataForm);
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
