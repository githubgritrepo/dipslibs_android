package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsOutboundCall;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_kartu_atm_detail extends Fragment {

    public static final int REQUEST_WRITE_PERMISSION = 786;
    private Context mContext;
    private SessionManager sessions;
    private ImageView btnBack;
    private View incl_ll_changeATM;
    private EditText et_nama_pemilik;
    private EditText et_alamat;
    private EditText et_no_cif;
    private EditText et_card_holder;
    private ImageView btnCamera;
    private LinearLayout btnGallery;
    private LinearLayout chooseImage;
    private Button btnProses;
    private String dataATM;
    private String jenis_layanan;
    private String noHP;
    private String email;
    private String idDips;
    private String noCIF;
    private ImageView viewImage;
    private TextView judul;
    private BroadcastReceiver smsReceiver = null;
    private JSONObject dataNasabah = null;
    private String numberOTP = "";
    private boolean flagTransfer = false;
    private int lasLenOTP;
    private boolean backSpaceOTP;
    private String no_handphone;
    private Handler handlerSuccess;
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
    private LinearLayout ll_no_ATM_CARD;
    private AutoCompleteTextView et_jenisChangeATM;
    private View incl_ll_ATMCard;
    private LinearLayout ll_PIN;
    private AutoCompleteTextView etTypeDesc;
    private LinearLayout ll_TypeDesc1;
    private LinearLayout ll_TypeDesc2;
    private TextView tvPengajuan;
    private EditText et_tgl;
    private EditText et_tgl2;
    private TextView tv_tnc;

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
                            sessions.saveNasabah(dataNasabah.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mContext,"Gagal Save Form",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, "Kode Terkirim ke nomor Hanphone Anda", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Kode Gagal Terkirim ke nomor Hanphone Anda", Toast.LENGTH_SHORT).show();
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
        mContext = getContext();
        sessions = new SessionManager(mContext);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECEIVE_SMS},
                1001);

        dataATM = getArguments().getString("DATA_ATM");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_kartu_atm_detail, container, false);

        btnBack = (ImageView) view.findViewById(R.id.btn_back_atm);
        judul = (TextView) view.findViewById(R.id.judul);
        et_nama_pemilik = (EditText) view.findViewById(R.id.et_nama_pemilik);
        et_alamat = (EditText) view.findViewById(R.id.et_alamat);
        et_no_cif = (EditText) view.findViewById(R.id.et_no_cif);
        et_card_holder = (EditText) view.findViewById(R.id.et_card_holder);
        btnCamera = (ImageView) view.findViewById(R.id.choose_camera);
        btnGallery = (LinearLayout) view.findViewById(R.id.choose_gallery);
        viewImage = view.findViewById(R.id.Imageview);
        chooseImage = view.findViewById(R.id.Choose_Image);
        btnProses = (Button) view.findViewById(R.id.btnProses);
        ll_no_ATM_CARD = (LinearLayout) view.findViewById(R.id.ll_no_ATM_CARD);
        tvPengajuan = (TextView) view.findViewById(R.id.tvPengajuan);
        incl_ll_changeATM = view.findViewById(R.id.incl_ll_changeATM);
        et_jenisChangeATM = (AutoCompleteTextView) view.findViewById(R.id.et_jenisChangeATM);
        incl_ll_ATMCard = view.findViewById(R.id.incl_ll_ATMCard);
        etTypeDesc = (AutoCompleteTextView) view.findViewById(R.id.etTypeDesc);
        ll_TypeDesc1 = (LinearLayout) view.findViewById(R.id.ll_TypeDesc1);
        ll_TypeDesc2 = (LinearLayout) view.findViewById(R.id.ll_TypeDesc2);
        et_tgl = (EditText) view.findViewById(R.id.et_tgl);
        et_tgl2 = (EditText) view.findViewById(R.id.et_tgl2);
        ll_PIN = (LinearLayout) view.findViewById(R.id.ll_PIN);
        tv_tnc = (TextView) view.findViewById(R.id.tv_tnc);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idDips = sessions.getKEY_IdDips();

        String dataJsonS = sessions.getNasabah();
        if (dataJsonS != null) {
            try {
                dataNasabah = new JSONObject(dataJsonS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject dataATMObj = new JSONObject(dataATM);
            String nama = dataATMObj.getString("nama");
            noHP = dataATMObj.getString("noHP");
            email = dataATMObj.getString("email");
            String alamat = dataATMObj.getString("alamat");
            jenis_layanan = dataATMObj.getString("jenis_layanan");

            noCIF = dataNasabah.getString("noCIF");

            judul.setText(jenis_layanan);
            et_nama_pemilik.setText(nama);
            et_alamat.setText(alamat);
            et_no_cif.setText(noCIF);

            if (jenis_layanan.indexOf("Penggantian") > -1 || jenis_layanan.indexOf("Blokir") > -1) {
                ll_no_ATM_CARD.setVisibility(View.VISIBLE);
                incl_ll_changeATM.setVisibility(View.VISIBLE);

                String[] typeChange = new String[]{"KARTU ATM", "PIN MAILER"};
                if (jenis_layanan.indexOf("Blokir") > -1) {
                    tvPengajuan.setText("dengan ini mengajukan untuk");
                    typeChange = new String[]{"Pemblokiran Kartu ATM", "Pembebasan Pemblokiran Kartu ATM"};
                }

                ArrayAdapter<String> adapterTypeChange = new ArrayAdapter<String>(mContext,R.layout.list_item, typeChange);
                et_jenisChangeATM.setAdapter(adapterTypeChange);

                et_jenisChangeATM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String getTypeChange = (String) adapterView.getItemAtPosition(i);
                        if (getTypeChange.equals("KARTU ATM") || getTypeChange.equals("Pemblokiran Kartu ATM")) {
                            incl_ll_ATMCard.setVisibility(View.VISIBLE);
                            ll_PIN.setVisibility(View.GONE);

                            String[] TypeDesc = new String[]{"Tertelan", "Hilang", "Rusak"};
                            ArrayAdapter<String> adapterTypeDesc = new ArrayAdapter<String>(mContext,R.layout.list_item, TypeDesc);
                            etTypeDesc.setAdapter(adapterTypeDesc);

                            etTypeDesc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    String getTypeDesc = (String) adapterView.getItemAtPosition(i);
                                    if (getTypeDesc.equals("Tertelan")) {
                                        ll_TypeDesc1.setVisibility(View.VISIBLE);
                                        ll_TypeDesc2.setVisibility(View.GONE);

                                        Calendar currentTime = Calendar.getInstance();

                                        DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                currentTime.set(Calendar.YEAR, year);
                                                currentTime.set(Calendar.MONTH, month);
                                                currentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                updateDates(currentTime,et_tgl);
                                            }
                                        };

                                        et_tgl.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new DatePickerDialog(getActivity(), dateDialog, currentTime
                                                        .get(Calendar.YEAR), currentTime.get(Calendar.MONTH),
                                                        currentTime.get(Calendar.DAY_OF_MONTH)).show();
                                            }
                                        });

                                    } else if (getTypeDesc.equals("Hilang")) {
                                        ll_TypeDesc1.setVisibility(View.GONE);
                                        ll_TypeDesc2.setVisibility(View.VISIBLE);

                                        Calendar currentTime = Calendar.getInstance();

                                        DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                currentTime.set(Calendar.YEAR, year);
                                                currentTime.set(Calendar.MONTH, month);
                                                currentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                updateDates(currentTime,et_tgl2);
                                            }
                                        };

                                        et_tgl2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new DatePickerDialog(getActivity(), dateDialog, currentTime
                                                        .get(Calendar.YEAR), currentTime.get(Calendar.MONTH),
                                                        currentTime.get(Calendar.DAY_OF_MONTH)).show();
                                            }
                                        });
                                    } else {
                                        ll_TypeDesc1.setVisibility(View.GONE);
                                        ll_TypeDesc2.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            incl_ll_ATMCard.setVisibility(View.GONE);
                            ll_PIN.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                SpannableString contentTnc = new SpannableString(tv_tnc.getText().toString());
                contentTnc.setSpan(new UnderlineSpan(), 0, contentTnc.length(), 0);
                tv_tnc.setText(contentTnc);

                ll_no_ATM_CARD.setVisibility(View.GONE);
                incl_ll_changeATM.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUp();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentPage(new frag_kartua_atm());
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermission()){
                    Toast.makeText(mContext, "Permission denied",Toast.LENGTH_SHORT).show();
                    return;
                }
                sessions.saveMedia(1);
                chooseFromCamera();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermission()){
                    Toast.makeText(mContext, "Permission denied",Toast.LENGTH_SHORT).show();
                    return;
                }
                sessions.saveMedia(2);
                chooseFromSD();
            }
        });

        tv_tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jenis_layanan.indexOf("Pengajuan") > -1) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
                    sweetAlertDialog.setTitle(getString(R.string.terms_condition2));
                    sweetAlertDialog.setContentText(getString(R.string.tnc_atm));
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();
                } else {
                    Toast.makeText(mContext,"Belum ada Syarat dan Ketentuan",Toast.LENGTH_SHORT).show();
                }
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
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
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
                    Toast.makeText(mContext, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    MirroringOTP(otp.getText().toString(),true);
                    sweetAlertDialog.dismiss();
                    //verifyOTP();
                    PopUpSuccesOtp();
                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    //resendOTP();
                }
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
        handlerSuccess = new Handler();
        handlerSuccess.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = new frag_kartu_atm_resi();
                Bundle bundle = new Bundle();
                bundle.putString("NAMA_KARTU",et_nama_pemilik.getText().toString().trim());
                fragment.setArguments(bundle);
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

    private void chooseFromSD() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 2);
    }
    private void chooseFromCamera() {
        Intent intent = new Intent(mContext, DipsCameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1){
                Log.e("CEK","RETURN CAMERA");
                sessions.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                byte[] resultRealCamera = data.getByteArrayExtra("real");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);
                Bitmap bitmap_real = BitmapFactory.decodeByteArray(resultRealCamera, 0, resultRealCamera.length);
                btnProses.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                btnProses.setClickable(true);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                viewImage.setImageBitmap(bitmap);
            }
            else if (requestCode == 2){
                sessions.saveFlagUpDoc(true);
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = mContext.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                btnProses.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                btnProses.setClickable(true);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                prosesOptimalImage(picturePath);
            }
        }
    }

    private void prosesOptimalImage(String picturePath) {
        File mediaFile = new File(picturePath);
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));
        Log.d("CEK", "file_size : "+file_size);

        int perDiff = 1;
        if (file_size > 3072) {
            perDiff = 8;
        } else if (file_size > 2048) {
            perDiff = 6;
        } else if (file_size > 1024) {
            perDiff = 4;
        } else if (file_size > 550) {
            perDiff = 2;
        }

        if (perDiff == 1) {
            viewImage.setImageBitmap(thumbnail);
            imgtoBase64(thumbnail);
        } else {
            getResizedBitmap(thumbnail, (thumbnail.getWidth() / perDiff), (thumbnail.getHeight() / perDiff));
        }
    }

    private void imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    public void getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        viewImage.setImageBitmap(resizedBitmap);
        imgtoBase64(resizedBitmap);
    }

    protected boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
            return false;
        }
        return true;
    }

    private void updateDates(Calendar currentTime, EditText ed_tgl) {
        int days = currentTime.get(Calendar.DAY_OF_WEEK);
        int tgl = currentTime.get(Calendar.DAY_OF_MONTH);
        int bln = currentTime.get(Calendar.MONTH);
        int thn = currentTime.get(Calendar.YEAR);

        String blnLabel = blnIndo(bln);

        String Tgl = String.format("%02d", tgl);

        String currentDate = Tgl + " " + blnLabel + " " + thn;

        ed_tgl.setText(currentDate);
    }

    private String blnIndo(int bln) {
        List<String> monthIn = new ArrayList<>();
        monthIn.add("Januari");
        monthIn.add("Februari");
        monthIn.add("Maret");
        monthIn.add("April");
        monthIn.add("Mei");
        monthIn.add("Juni");
        monthIn.add("Juli");
        monthIn.add("Agustus");
        monthIn.add("September");
        monthIn.add("Oktober");
        monthIn.add("November");
        monthIn.add("Desember");
        return monthIn.get(bln);

    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

}
