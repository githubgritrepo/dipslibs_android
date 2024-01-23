package com.evo.mitzoom.Fragments;

import static com.evo.mitzoom.Helper.MyWorker.EXTRA_START;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSlide;
import com.evo.mitzoom.Adapter.GridProductAdapter;
import com.evo.mitzoom.Helper.MyWorker;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_berita extends Fragment implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {
    private Context context;
    private RecyclerView rv_product;
    private ViewPager2 mPager;
    private GridProductAdapter gridAdapter;
    private static final Integer[]img = {R.drawable.adsv1, R.drawable.adsv2, R.drawable.adsv3};
    private final ArrayList<Integer> imgArray = new ArrayList<Integer>();
    private CircleIndicator circleIndicator;
    private int currentPage;
    private Button btnSchedule,btnSchedule2, btnEndCall;
    ArrayList<String> time = new ArrayList<>();
    List<Integer> periodeInt = new ArrayList<>();
    HashMap<Integer,String> dataPeriode = new HashMap<>();
    HashMap<String,Integer> dataPeriodeId = new HashMap<>();
    private int year;
    private int month;
    private int day;
    private final int waktu_tunggu = 6000;
    private String tanggal, waktu;
    private String Savetanggal;
    private int Savewaktu;
    private SessionManager sessions;
    private String idDips;
    private JSONArray dataArrSpanduk = null;
    private List<JSONObject> newDataProd;
    private SwipeRefreshLayout swipe;
    private RelativeLayout rl_real;
    private List<Integer> indeksNotFound;
    private DatePickerDialog dpd;
    private EditText et_Date;
    private Spinner et_time;
    private JSONArray tanggalPenuh;
    private JSONArray periodePenuh;
    private WorkManager workManager = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
        idDips = sessions.getKEY_IdDips();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_berita, container, false);
        swipe = view.findViewById(R.id.swipe);
        rl_real = view.findViewById(R.id.rl_real);
        rv_product = view.findViewById(R.id.rv_product);
        mPager = view.findViewById(R.id.pager);
        circleIndicator = view.findViewById(R.id.indicator);
       /* btnSchedule = view.findViewById(R.id.btnSchedule);
        btnEndCall = view.findViewById(R.id.end_call);*/

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rl_real.setVisibility(View.INVISIBLE);
        DipsWaitingRoom.showProgress(true);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rl_real.setVisibility(View.INVISIBLE);
                DipsWaitingRoom.showProgress(true);
                new AsyncProcess().execute();
                swipe.setRefreshing(false);
            }
        });

        parseGetSchedule();
        new AsyncProcess().execute();

        /*btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time.size() > 0) {
                    PopUpSchedule();
                } else {
                    Toast.makeText(context,getString(R.string.please_wait),Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndCall();
            }
        });*/

    }

    /*private void processGetCheckSchedule() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        Server.getAPIService().GetCheckSchedule(authAccess).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("code");
                        if (errCode == 200) {
                            tanggalPenuh = dataObj.getJSONObject("data").getJSONArray("tanggalPenuh");
                            periodePenuh = dataObj.getJSONObject("data").getJSONArray("periodePenuh");

                            l,,,,,,,,,,,,,,,,,
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
    }*/

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int addmonths = (month + 1);
        String months = String.valueOf(addmonths);
        if (addmonths < 10) {
            months = "0"+months;
        }
        String days = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10 ) {
            days = "0"+days;
        }
        tanggal = days+"/"+months+"/"+year;
        Savetanggal = year + "-" + months + "-" + days;
        et_Date.setText(tanggal);

        if (periodePenuh.length() > 0) {
            ArrayList<String> times_new = new ArrayList<>(time);
            for (int i = 0; i < periodePenuh.length(); i++) {
                try {
                    String tglFull = periodePenuh.getJSONObject(i).getString("tanggal");
                    int periodeId = periodePenuh.getJSONObject(i).getInt("periodeId");
                    if (tglFull.equals(Savetanggal)) {
                        String valP = dataPeriode.get(periodeId);
                        for (int j = 0; j < times_new.size(); j++) {
                            String times = times_new.get(j);
                            if (valP.equals(times)) {
                                times_new.remove(j);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(context,R.layout.list_item, times_new);
            et_time.setAdapter(adapterTime);
        } else {
            ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(context,R.layout.list_item, time);
            et_time.setAdapter(adapterTime);
        }
    }

    private class AsyncProcess extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            processGetSpanduk();
            processGetProduct();
            //processGetCheckSchedule();
            //processGetScheduleTimes();
            return null;
        }
    }

    private void parseGetSchedule() {
        if (sessions.getScheduledDate() != null) {
            try {
                JSONObject dataObj = new JSONObject(sessions.getScheduledDate());
                tanggalPenuh = dataObj.getJSONObject("data").getJSONArray("tanggalPenuh");
                periodePenuh = dataObj.getJSONObject("data").getJSONArray("periodePenuh");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (sessions.getScheduledTime() != null) {
            try {
                JSONArray dataArrTimes = new JSONArray(sessions.getScheduledTime());
                for (int i = 0; i < dataArrTimes.length(); i++) {
                    int periodeId = dataArrTimes.getJSONObject(i).getInt("id");
                    String periode = dataArrTimes.getJSONObject(i).getString("periode");
                    time.add(periode);
                    periodeInt.add(periodeId);
                    dataPeriode.put(periodeId,periode);
                    dataPeriodeId.put(periode,periodeId);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processGetSpanduk() {
        indeksNotFound = new ArrayList<>();
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getSpandukPublish(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    DipsWaitingRoom.showProgress(false);
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (dataObj.has("err_code")) {
                            int errCode = dataObj.getInt("err_code");
                            if (errCode == 0) {
                                dataArrSpanduk = dataObj.getJSONArray("data");
                                int len = dataArrSpanduk.length();
                                if (len > 0) {
                                    processSyncSpandukMedia(0);
                                }
                            }
                        } else {
                            int errCode = dataObj.getInt("code");
                            if (errCode == 200) {
                                dataArrSpanduk = dataObj.getJSONArray("data");
                                int len = dataArrSpanduk.length();
                                if (len > 0) {
                                    processSyncSpandukMedia(0);
                                }
                            }
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

    private void processSyncSpandukMedia(int i) {
        try {
            JSONObject dataStream = dataArrSpanduk.getJSONObject(i);
            int idSpanduk = dataStream.getInt("id");
            processSpandukMedia(idSpanduk,dataStream,i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processSpandukMedia(int idSpanduk, JSONObject dataStream, int indexs) {
        final int[] loops = {indexs};
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getSpandukMedia(idSpanduk,authAccess,exchangeToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String Content_Type = response.headers().get("Content-Type");
                    if (!Content_Type.contains("json")) {
                        InputStream in = response.body().byteStream();

                        if (indexs < dataArrSpanduk.length()-1) {
                            loops[0]++;
                            processSyncSpandukMedia(loops[0]);
                        }

                        processParsingMedia(in,Content_Type,dataStream,indexs);

                        if (indexs == dataArrSpanduk.length()-1) {
                            rl_real.setVisibility(View.VISIBLE);


                            mPager.setVisibility(View.VISIBLE);
                            circleIndicator.setVisibility(View.VISIBLE);
                            for (int k = 0; k < indeksNotFound.size(); k++) {
                                int indekNF = indeksNotFound.get(k);
                                dataArrSpanduk.remove(indekNF);
                            }
                            initPager();
                        }
                    }
                } else if(response.code() == 404) {
                    indeksNotFound.add(indexs);
                    if (indexs < dataArrSpanduk.length()-1) {
                        loops[0]++;
                        processSyncSpandukMedia(loops[0]);
                    }

                    if (indexs == dataArrSpanduk.length()-1) {
                        rl_real.setVisibility(View.VISIBLE);

                        mPager.setVisibility(View.VISIBLE);
                        circleIndicator.setVisibility(View.VISIBLE);
                        for (int k = 0; k < indeksNotFound.size(); k++) {
                            int indekNF = indeksNotFound.get(k);
                            dataArrSpanduk.remove(indekNF);
                        }
                        initPager();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void processParsingMedia(InputStream stream, String content_Type, JSONObject dataStream, int indexs) {
        if (content_Type.indexOf("image") > -1) {
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            try {
                dataStream.put("dataBitmap",bitmap);
                dataStream.put("content_Type",content_Type);
                dataArrSpanduk.put(indexs,dataStream);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            takeInputStream(stream,content_Type,dataStream,indexs);
        }
    }

    private void takeInputStream(InputStream stream, String content_Type, JSONObject dataStream, int indexs) {
        File mBufferFile;
        try {
            File bufferFile = File.createTempFile("test", ".mp4");
            BufferedOutputStream bufferOS = new BufferedOutputStream(
                    Files.newOutputStream(bufferFile.toPath()));

            BufferedInputStream bis = new BufferedInputStream(stream,2048);

            byte[] buffer = new byte[16384];
            int numRead;
            while ((numRead = bis.read(buffer)) != -1) {
                bufferOS.write(buffer, 0, numRead);
                bufferOS.flush();
            }
            mBufferFile = bufferFile;
            try {
                dataStream.put("dataStream",mBufferFile);
                dataStream.put("content_Type",content_Type);
                dataArrSpanduk.put(indexs,dataStream);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processGetProduct() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getNewProductPublish(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        int errCode = dataObj.getInt("code");
                        if (errCode == 200) {
                            JSONArray dataRows = dataObj.getJSONArray("data");
                            newDataProd = new ArrayList<JSONObject>();
                            List<JSONObject> newDataProdRight = new ArrayList<JSONObject>();
                            int indexsLeft = 0;
                            int indexsRight = 0;
                            for (int i = 0; i < dataRows.length(); i++) {
                                boolean isLeft = dataRows.getJSONObject(i).getBoolean("isLeft");
                                if (isLeft) {
                                    newDataProd.add(indexsLeft, dataRows.getJSONObject(i));
                                    indexsLeft++;
                                } else {
                                    newDataProdRight.add(indexsRight, dataRows.getJSONObject(i));
                                    indexsRight++;
                                }
                            }

                            for (int i = 0; i < newDataProdRight.size(); i++) {
                                List<JSONObject> prodRight = new ArrayList<JSONObject>();
                                prodRight.add(newDataProdRight.get(i));
                                int urutanRight = newDataProdRight.get(i).getInt("urutan");
                                for (int j = 0; j < newDataProd.size(); j++) {
                                    int urutanLeft = newDataProd.get(j).getInt("urutan");
                                    if (urutanRight == urutanLeft) {
                                        int loop = j+1;
                                        newDataProd.addAll(loop,prodRight);
                                        break;
                                    }
                                }
                            }

                            int spanCount = 2; // columns
                            int spacing = 10; // 20px
                            boolean includeEdge = true;
                            rv_product.setLayoutManager(new GridLayoutManager(context,spanCount));
                            rv_product.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
                            gridAdapter = new GridProductAdapter(context,newDataProd);
                            rv_product.setAdapter(gridAdapter);
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

    private void EndCall(){

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);
        btnCancelDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.v_dialog_info));
        tvBodyDialog.setText(getString(R.string.headline_endcall));
        btnCancelDialog.setText(getString(R.string.no));
        btnConfirmDialog.setText(getString(R.string.end_call));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                //Toast.makeText(context,getResources().getString(R.string.end_call2), Toast.LENGTH_LONG).show();
                OutApps();
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
    }
    private void OutApps(){
        if (DipsWaitingRoom.connection != null) {
            try {
                if (DipsWaitingRoom.channelSubscribeReqTicket != null) {
                    if (DipsWaitingRoom.channelSubscribeReqTicket.isOpen()) {
                        DipsWaitingRoom.channelSubscribeReqTicket.close();
                    }
                }
                if (DipsWaitingRoom.channelSubscribe != null) {
                    if (DipsWaitingRoom.channelSubscribe.isOpen()) {
                        DipsWaitingRoom.channelSubscribe.close();
                    }
                }
                if (DipsWaitingRoom.channelCall != null) {
                    if (DipsWaitingRoom.channelCall.isOpen()) {
                        DipsWaitingRoom.channelCall.close();
                    }
                }
                if (DipsWaitingRoom.connection != null) {
                    if (DipsWaitingRoom.connection.isOpen()) {
                        DipsWaitingRoom.connection.close();
                    }
                }
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
        ((Activity) context).finish();
        //System.exit(0);
    }
    private void initPager() {

        mPager.setAdapter(new AdapterSlide(context, dataArrSpanduk));
        mPager.setClipToPadding(false);
        mPager.setClipChildren(false);
        mPager.setOffscreenPageLimit(3);
        mPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(12));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1 - Math.abs(position);
                page.setScaleY(0.8f + v * 0.2f);
            }
        });
        mPager.setPageTransformer(transformer);
    }

    private void PopUpSchedule(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_schedule, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        ImageView btnclose = dialogView.findViewById(R.id.btn_close_schedule);
        et_Date = dialogView.findViewById(R.id.et_Date);
        et_time = dialogView.findViewById(R.id.et_time);

        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(context,R.layout.list_item, time);
        et_time.setAdapter(adapterTime);

        btnSchedule2 = dialogView.findViewById(R.id.btnSchedule2);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                dpd = null;
                if (dpd == null) {
                    dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                            frag_berita.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                } else {
                    dpd.initialize(
                            frag_berita.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                }

                // restrict to weekdays only
                ArrayList<Calendar> weekdays = new ArrayList<Calendar>();
                Calendar day = Calendar.getInstance();
                int loopAdd = 0;
                for (int i = 0; i < 30; i++) {
                    if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        if (tanggalPenuh.length() > 0) {
                            for (int tg = 0; tg < tanggalPenuh.length(); tg++) {
                                try {
                                    String tglFull = tanggalPenuh.getString(tg);
                                    int yearChk = day.get(Calendar.YEAR);
                                    int monthChk = day.get(Calendar.MONTH);
                                    int dayChk = day.get(Calendar.DAY_OF_MONTH);

                                    int addmonths = (monthChk + 1);
                                    String months = String.valueOf(addmonths);
                                    if (addmonths < 10) {
                                        months = "0"+months;
                                    }
                                    String days = String.valueOf(dayChk);
                                    if (dayChk < 10 ) {
                                        days = "0"+days;
                                    }

                                    String tglChk = yearChk + "-" + months + "-" + days;
                                    if (!tglFull.equals(tglChk)) {
                                        Calendar d = (Calendar) day.clone();
                                        weekdays.add(d);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Calendar d = (Calendar) day.clone();
                            weekdays.add(d);
                        }
                    } else {
                        loopAdd++;
                    }
                    day.add(Calendar.DATE, 1);
                }
                Calendar[] weekdayDays = weekdays.toArray(new Calendar[weekdays.size()]);
                dpd.setSelectableDays(weekdayDays);
                //dpd.setMaxDate(day);

                dpd.setOnCancelListener(dialog -> {
                    dpd = null;
                });
                dpd.show(requireFragmentManager(), "Datepickerdialog");

            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
            }
        });
        btnSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggal = et_Date.getText().toString().trim().trim();
                waktu = et_time.getSelectedItem().toString();
                if (tanggal.trim().equals("")){
                    Toast.makeText(context.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else if (waktu.trim().equals("")){
                    Toast.makeText(context.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (idDips.isEmpty()) {
                        idDips = sessions.getKEY_IdDips();
                    }
                    //Toast.makeText(context.getApplicationContext(), getResources().getString(R.string.schedule) + tanggal + " & " + getResources().getString(R.string.jam) + waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                    String csId = sessions.getCSID();
                    if (csId != null && !csId.isEmpty()) {
                        DipsWaitingRoom.publishCallAcceptHttp(csId, "cancel"); //RabbitMQ
                    }
                    sessions.saveIDSchedule(0);
                    DipsWaitingRoom.showProgress(true);
                    saveSchedule();
                }

            }
        });
    }

    private void serviceOutbound() {
        Intent serviceIntent = new Intent(context, OutboundServiceNew.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    private void saveSchedule(){
        int periodeId = dataPeriodeId.get(waktu);
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("tanggal",Savetanggal);
            jsons.put("periodeId",periodeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.saveSchedule(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                DipsWaitingRoom.showProgress(false);
                if (response.isSuccessful() && response.body().size() > 0) {

                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int idSchedule = dataObj.getJSONObject("data").getInt("id");
                        sessions.saveIDSchedule(idSchedule);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //doWorkMyWorker();
                    serviceOutbound();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

                    ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
                    TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
                    TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
                    Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
                    Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

                    tvTitleDialog.setVisibility(View.GONE);

                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.v_dialog_success));
                    tvBodyDialog.setText(getString(R.string.content_after_schedule));
                    btnConfirmDialog.setText(getString(R.string.done));

                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
                    sweetAlertDialog.setCustomView(dialogView);
                    sweetAlertDialog.hideConfirmButton();
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();

                    btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sweetAlertDialog.dismiss();
                            OutApps();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                DipsWaitingRoom.showProgress(false);
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void doWorkMyWorker() {
        workManager = WorkManager.getInstance(context);

        String jam = waktu;
        if (jam.indexOf("-") > 0) {
            String[] sp = jam.split("-");
            for (int i = 0; i < sp.length; i++) {
                workManager.cancelUniqueWork(idDips+"_timesStart"+i);
            }

            for (int i = 0; i < sp.length; i++) {
                Calendar currentDate = Calendar.getInstance();

                String[] timeArray = sp[i].split(":");
                String[] spDate = Savetanggal.split("-");
                String thn = spDate[0].trim();
                String getBln = spDate[1].trim();
                int bln = Integer.parseInt(getBln) - 1;
                String tgl = spDate[2].trim();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(thn),bln,Integer.parseInt(tgl));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
                calendar.set(Calendar.SECOND, 0);

                long timeCurrentMilis = currentDate.getTimeInMillis();
                long timeInMilis = calendar.getTimeInMillis();
                long timeDiff = timeInMilis - timeCurrentMilis;

                boolean start = i <= 0;

                Data data = new Data.Builder()
                        .putBoolean(EXTRA_START,start)
                        .build();

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .build();

                workManager.enqueueUniqueWork(idDips+"_timesStart"+i,
                        ExistingWorkPolicy.KEEP, (OneTimeWorkRequest) workRequest);
            }
        } else {
            workManager.cancelUniqueWork(idDips);
            Calendar currentDate = Calendar.getInstance();

            String[] timeArray = jam.split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
            calendar.set(Calendar.SECOND, 0);

            long timeCurrentMilis = currentDate.getTimeInMillis();
            long timeInMilis = calendar.getTimeInMillis();
            long timeDiff = timeInMilis - timeCurrentMilis;

            Data data = new Data.Builder()
                    .putBoolean(EXTRA_START,true)
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .setConstraints(constraints)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build();

            workManager.enqueueUniqueWork(idDips,
                    ExistingWorkPolicy.KEEP, (OneTimeWorkRequest) workRequest);
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
