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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_summary_rtgs extends Fragment {
    private Context context;
    private TextView Timer, Resend_Otp;
    private ImageView btnBack;
    private Button btnTransfer;
    private LayoutInflater inflater;
    private View dialogView;
    private Handler handlerSuccess;
    private Button btnVerifikasi;
    public int getMinutes = 2;
    public int seconds = 60;
    private PinView otp;
    private int selPos;
    private String oldString, newString;
    public boolean running = true;
    private SessionManager session;
    private String idDips;
    private Handler handler;
    private Runnable myRunnable;
    private String dataRTGS;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private ArrayList<Integer> layouts = new ArrayList<Integer>();
    private ArrayList<String> dataAccount = new ArrayList<String>();
    private ArrayList<String> dataNoForm = new ArrayList<String>();
    private ArrayList<String> dataBankName = new ArrayList<String>();
    private ArrayList<String> dataAccountReceive = new ArrayList<>();
    private ArrayList<String> dataNameReceive = new ArrayList<>();
    private ArrayList<String> dataNominal = new ArrayList<>();
    private ArrayList<String> dataService = new ArrayList<>();
    private ArrayList<String> dataBenefit = new ArrayList<>();
    private ArrayList<String> dataPopulation = new ArrayList<>();
    private ArrayList<String> dataNews = new ArrayList<>();
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
            jsons.put("formCode","RTGS");
            jsons.put("idDips",idDips);
            jsons.put("phone",no_handphone);
            jsons.put("payload",dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","Request : "+jsons.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIService().saveForm(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","Response : "+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int errCode = jsObj.getInt("err_code");
                        if (errCode == 0) {
                            JSONObject dataJs = jsObj.getJSONObject("data");
                            String idForm = dataJs.getString("idForm");
                            dataNasabah.put("idFormRTGS",idForm);
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

    private void verifyOTP(SweetAlertDialog sweetAlertDialog) {
        JSONObject jsons = new JSONObject();
        try {
            String idForm = dataNasabah.getString("idFormRTGS");
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
                            sweetAlertDialog.dismiss();
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
            String idForm = dataNasabah.getString("idFormRTGS");
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
        dataRTGS = session.getRTGS();
        Log.d("CEK","dataRTGS : "+dataRTGS);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECEIVE_SMS},
                1001);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summary_rtgs, container, false);
        btnBack = view.findViewById(R.id.btn_back5);
        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        btnTransfer = view.findViewById(R.id.btnTransfer);
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

        if (dataRTGS != null) {
            savedRTGS();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(true,true,1,1);
                Fragment fragment = new frag_rtgs();
                Bundle bundle = new Bundle();
                bundle.putBoolean("SUMMARY",true);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagTransfer = true;
                Mirroring(true,false,1,1);
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

    private void initPager() {
        if (myViewPagerAdapter == null) {
            myViewPagerAdapter = new MyViewPagerAdapter();
        }
        pager.setAdapter(myViewPagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mirroringPagerRTGS(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        circleIndicator.setViewPager(pager);
    }

    private JSONArray savedRTGS() {
        JSONArray dataRTGSArr = new JSONArray();
        Log.d("CEK","MASUK savedRTGS");
        try {
            JSONArray jsArr = new JSONArray(dataRTGS);
            int len = jsArr.length();
            int idx = 1;
            for (int i = 0; i < len; i++) {
                String dataArr = jsArr.get(i).toString();
                JSONObject dataJs = new JSONObject(dataArr);
                String idForm = dataJs.getString("idForm");
                String sourceAccount = dataJs.getString("sourceAccount");
                String sourceBank = dataJs.getString("sourceBank");
                String sourceTypeService = dataJs.getString("sourceTypeService");
                String sourceBenefit = dataJs.getString("sourceBenefit");
                String sourcePopulation = dataJs.getString("sourcePopulation");
                String rek_penerima = dataJs.getString("rek_penerima");
                String nama_penerima = dataJs.getString("nama_penerima");
                String nominal = dataJs.getString("nominal");
                String berita = dataJs.getString("berita");

                layouts.add(R.layout.content_summary_rtgs);

                dataNoForm.add(idForm);
                dataAccount.add(sourceAccount);
                dataBankName.add(sourceBank);
                dataAccountReceive.add(rek_penerima);
                dataNameReceive.add(nama_penerima);
                dataNominal.add(nominal);
                dataService.add(sourceTypeService);
                dataBenefit.add(sourceBenefit);
                dataPopulation.add(sourcePopulation);
                dataNews.add(berita);

                dataRTGSArr.put(i, idForm);
                dataRTGSArr.put(i, sourceAccount);
                dataRTGSArr.put(i, sourceBank);
                dataRTGSArr.put(i, rek_penerima);
                dataRTGSArr.put(i, nama_penerima);
                dataRTGSArr.put(i, nominal);
                dataRTGSArr.put(i, sourceTypeService);
                dataRTGSArr.put(i, sourceBenefit);
                dataRTGSArr.put(i, sourcePopulation);
                dataRTGSArr.put(i, berita);

                initPager();

                if (i == 0) {
                    Mirroring(true,false,idx,len);
                }

                idx++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return dataRTGSArr;
    }

    private JSONArray getRTGS() {
        JSONArray dataRTGSArr = new JSONArray();
        Log.d("CEK","MASUK getRTGS");
        try {
            JSONArray jsArr = new JSONArray(dataRTGS);
            int len = jsArr.length();
            String namaNasabah = dataNasabah.getString("nama");
            String alamatNasabah = dataNasabah.getString("alamat");
            for (int i = 0; i < len; i++) {
                JSONObject saveObj = new JSONObject();

                String dataArr = jsArr.get(i).toString();
                JSONObject dataJs = new JSONObject(dataArr);
                String idForm = dataJs.getString("idForm");
                String sourceAccount = dataJs.getString("sourceAccount");
                String sourceBank = dataJs.getString("sourceBank");
                String sourceTypeService = dataJs.getString("sourceTypeService");
                String sourceBenefit = dataJs.getString("sourceBenefit");
                String sourcePopulation = dataJs.getString("sourcePopulation");
                String rek_penerima = dataJs.getString("rek_penerima");
                String nama_penerima = dataJs.getString("nama_penerima");
                String nominal = dataJs.getString("nominal");
                String berita = dataJs.getString("berita");

                saveObj.put("idForm", idForm);
                saveObj.put("namaNasabah", namaNasabah);
                saveObj.put("alamatNasabah", alamatNasabah);
                saveObj.put("sourceAccount", sourceAccount);
                saveObj.put("sourceBank", sourceBank);
                saveObj.put("rek_penerima", rek_penerima);
                saveObj.put("nama_penerima", nama_penerima);
                saveObj.put("nominal", nominal);
                saveObj.put("sourceTypeService", sourceTypeService);
                saveObj.put("sourceBenefit", sourceBenefit);
                saveObj.put("sourcePopulation", sourcePopulation);
                saveObj.put("berita", berita);

                dataRTGSArr.put(saveObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataRTGSArr;
    }

    private void mirroringPagerRTGS(int position) {
        int lenL = layouts.size();
        int idx = position + 1;

        try {
            String noFormulir = dataNoForm.get(position);
            if (dataAccount.size() == 0 || (dataAccount.size() == position) || (dataAccount.size() > 0 && dataAccount.get(position).isEmpty())) {
                dataAccount.add("");
            }
            if (dataBankName.size() == 0 || (dataBankName.size() == position) || (dataBankName.size() > 0 && dataBankName.get(position).isEmpty())) {
                dataBankName.add("");
            }
            if (dataAccountReceive.size() == 0 || (dataAccountReceive.size() == position) || (dataAccountReceive.size() > 0 && dataAccountReceive.get(position).isEmpty())) {
                dataAccountReceive.add("");
            }
            if (dataNameReceive.size() == 0 || (dataNameReceive.size() == position) || (dataNameReceive.size() > 0 && dataNameReceive.get(position).isEmpty())) {
                dataNameReceive.add("");
            }
            if (dataNominal.size() == 0 || (dataNominal.size() == position) || (dataNominal.size() > 0 && dataNominal.get(position).isEmpty())) {
                dataNominal.add("0");
            }
            if (dataService.size() == 0 || (dataService.size() == position) || (dataService.size() > 0 && dataService.get(position).isEmpty())) {
                dataService.add("");
            }
            if (dataBenefit.size() == 0 || (dataBenefit.size() == position) || (dataBenefit.size() > 0 && dataBenefit.get(position).isEmpty())) {
                dataBenefit.add("");
            }
            if (dataPopulation.size() == 0 || (dataPopulation.size() == position) || (dataPopulation.size() > 0 && dataPopulation.get(position).isEmpty())) {
                dataPopulation.add("");
            }
            if (dataNews.size() == 0 || (dataNews.size() == position) || (dataNews.size() > 0 && dataNews.get(position).isEmpty())) {
                dataNews.add("");
            }

            /*String SourceAccount = dataAccount.get(position);
            String SumberBank = dataBankName.get(position);
            String JenisLayanan = dataService.get(position);
            String posSourceBenefit = dataBenefit.get(position);
            String posSourcePopulation = dataPopulation.get(position);
            String rek_penerima = dataAccountReceive.get(position);
            String nama_penerima = dataNameReceive.get(position);
            String nominal = dataNominal.get(position);
            String berita = dataNews.get(position);*/

            Mirroring(false,false,idx,lenL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void PopUp(){
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
                    Mirroring2(false, numberOTP);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpaceOTP = lasLenOTP > s.length();
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
                }
            }
        });
        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (otp.getText().toString().equalsIgnoreCase("")){
                if (numberOTP.isEmpty()) {
                    Toast.makeText(context, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Mirroring2(true, otp.getText().toString());
                    sweetAlertDialog.dismiss();
                    PopUpSuccesOtp();
                    //verifyOTP(sweetAlertDialog);
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
                sweetAlertDialog.dismiss();
                popUpBerhasil();
            }
        },2000);
    }
    private void popUpBerhasil(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.successTransaction));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Mirroring3(true);
                Fragment fragment = new frag_new_resi();
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
                sweetAlertDialog.dismiss();
            }
        });
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
    private void Mirroring(boolean bool, boolean back, int page, int pageAll){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(back);
            jsonArray.put(page);
            jsonArray.put(pageAll);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",17);
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
                if (flagTransfer) {
                    PopUp();
                    /*try {
                        Thread.sleep(1500);
                        JSONArray dataRTGSArr = getRTGS();
                        APISaveForm(dataRTGSArr);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring2(Boolean bool,CharSequence s){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(s);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",18);
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
    private void Mirroring3(boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",19);
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

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts.get(position), container, false);
            container.addView(view);

            iniatilizeElement(view,position);

            return view;
        }

        private void iniatilizeElement(View view, int position) {
            int positionE = position;
            int indexMirror = position + 1;

            TextView tv_RekeningSumber = (TextView) view.findViewById(R.id.RekeningSumber);
            TextView tv_RekeningPenerima = (TextView) view.findViewById(R.id.RekeningPenerima);
            TextView tv_JenisLayanan = (TextView) view.findViewById(R.id.JenisLayanan);
            TextView tv_PenerimaManfaat = (TextView) view.findViewById(R.id.PenerimaManfaat);
            TextView tv_JenisPenduduk = (TextView) view.findViewById(R.id.JenisPenduduk);
            TextView tv_Berita = (TextView) view.findViewById(R.id.Berita);
            TextView tv_Biaya = (TextView) view.findViewById(R.id.Biaya);
            TextView tv_Nominal = (TextView) view.findViewById(R.id.Nominal);

            if (dataAccount.size() > 0 ) {
                if (positionE < dataAccount.size()) {
                    tv_RekeningSumber.setText(dataAccount.get(positionE));
                }
            }

            if (dataBankName.size() > 0 ) {
                if (positionE < dataBankName.size()) {
                    String NamaBank = dataBankName.get(positionE);
                    String RekPenerima = dataAccountReceive.get(positionE);
                    String NamaPenerima = dataNameReceive.get(positionE);

                    tv_RekeningPenerima.setText(NamaBank+"\n"+RekPenerima+" - "+NamaPenerima);
                }
            }

            if (dataService.size() > 0 ) {
                if (positionE < dataService.size()) {
                    tv_JenisLayanan.setText(dataService.get(positionE));
                }
            }

            if (dataBenefit.size() > 0 ) {
                if (positionE < dataBenefit.size()) {
                    tv_PenerimaManfaat.setText(dataBenefit.get(positionE));
                }
            }

            if (dataPopulation.size() > 0 ) {
                if (positionE < dataPopulation.size()) {
                    tv_JenisPenduduk.setText(dataPopulation.get(positionE));
                }
            }

            if (dataNews.size() > 0 ) {
                if (positionE < dataNews.size()) {
                    tv_Berita.setText(dataNews.get(positionE));
                }
            }

            tv_Biaya.setText(getResources().getString(R.string.mata_uang)+" 2.500");

            if (dataNominal.size() > 0 ) {
                if (positionE < dataNominal.size()) {
                    tv_Nominal.setText(getResources().getString(R.string.mata_uang)+" "+dataNominal.get(positionE));
                }
            }

        }

        @Override
        public int getCount() {
            return layouts.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
