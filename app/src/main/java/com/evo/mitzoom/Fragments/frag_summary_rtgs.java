package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.chaos.view.PinView;
import com.dhairytripathi.library.EditTextPin;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterBank2;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Adapter.AdapterTypeService;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

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
    private String getBerita = "";
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
        dataRTGS = session.getRTGS();
        Log.d("CEK","dataRTGS : "+dataRTGS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summary_rtgs, container, false);
        btnBack = view.findViewById(R.id.btn_back5);
        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        btnTransfer = view.findViewById(R.id.btnTransfer);
//        tv_RekeningSumber = view.findViewById(R.id.RekeningSumber);
//        tv_RekeningPenerima = view.findViewById(R.id.RekeningPenerima);
//        tv_JenisLayanan = view.findViewById(R.id.JenisLayanan);
//        tv_PenerimaManfaat = view.findViewById(R.id.PenerimaManfaat);
//        tv_JenisPenduduk = view.findViewById(R.id.JenisPenduduk);
//        tv_Berita = view.findViewById(R.id.Berita);
//        tv_Biaya = view.findViewById(R.id.Biaya);
//        tv_Nominal = view.findViewById(R.id.Nominal);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();

        if (dataRTGS != null) {
            savedRTGS();
        }

        /*Bundle terima = getArguments();
        RekeningSumber = terima.getString("rekeningSumber");
        JenisLayanan = terima.getString("jenisLayanan");
        NamaBank = terima.getString("namaBank");
        NamaPenerima = terima.getString("namaPenerima");
        PenerimaManfaat = terima.getString("penerimaManfaat");
        JenisPenduduk = terima.getString("jenisPenduduk");
        Berita = terima.getString("berita");
        Nominal = terima.getString("nominal");
        RekPenerima = terima.getString("rekPenerima");

        tv_RekeningSumber.setText(RekeningSumber);
        tv_RekeningPenerima.setText(NamaBank+"\n"+RekPenerima+" - "+NamaPenerima);
        tv_JenisLayanan.setText(JenisLayanan);
        tv_PenerimaManfaat.setText(PenerimaManfaat);
        tv_JenisPenduduk.setText(JenisPenduduk);
        tv_Berita.setText(Berita);
        tv_Biaya.setText("Rp2.500");
        tv_Nominal.setText("Rp"+Nominal);*/

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(true,true,1,1);
                getFragmentPage(new frag_rtgs());
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(true,false,1,1);
                PopUp();
            }
        });

        ///SetText
    }

    private void initPager() {
        if (myViewPagerAdapter == null) {
            myViewPagerAdapter = new MyViewPagerAdapter();
        }
        pager.setAdapter(myViewPagerAdapter);
        circleIndicator.setViewPager(pager);
    }

    private void savedRTGS() {
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

                initPager();

                /*Mirroring(true,"",sourceBank,rek_penerima,nama_penerima,nominal,
                        sourceTypeService,sourceBenefit,sourcePopulation,berita,idx,len);*/

                idx++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String dataArrF(int index) {
        ArrayList<String> arrF = new ArrayList<>();
        arrF.add(getResources().getString(R.string.label_first));
        arrF.add(getResources().getString(R.string.label_second));
        arrF.add(getResources().getString(R.string.label_third));
        arrF.add(getResources().getString(R.string.label_fourth));
        arrF.add(getResources().getString(R.string.label_fifth));

        return  arrF.get(index).toString();
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
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
        otp.setAnimationEnable(true);
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                selPos = otp.getSelectionStart();
                oldString = myFilter(s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring2(false,s);
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
                Fragment fragment = new frag_resi();
                Bundle bundle = new Bundle();
                /*bundle.putString("rekeningSumber",RekeningSumber);
                bundle.putString("jenisLayanan",JenisLayanan);
                bundle.putString("namaBank",NamaBank);
                bundle.putString("namaPenerima",NamaPenerima);
                bundle.putString("penerimaManfaat",PenerimaManfaat);
                bundle.putString("jenisPenduduk",JenisPenduduk);
                bundle.putString("berita",Berita);
                bundle.putString("nominal",Nominal);
                bundle.putString("rekPenerima",RekPenerima);*/
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
                int minutes = 0;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0){
                    running = false;
                    resend.setClickable(true);
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

            tv_Biaya.setText("Rp2.500");

            if (dataNominal.size() > 0 ) {
                if (positionE < dataNominal.size()) {
                    tv_Nominal.setText(dataNominal.get(positionE));
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
