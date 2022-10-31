package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterPortofolio;
import com.evo.mitzoom.Adapter.AdapterPortofolioNew;
import com.evo.mitzoom.Model.PortfolioModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_portfolio extends Fragment {
    private Context context;
    PieChart pieChart;
    PieData pieData;
    TextView tvtanggal, tvCurrency, DanaPihakKetiga, Investasi, Kredit;
    NestedScrollView nestedScrollView;
    String TanggalSekarang;
    RecyclerView recyclerView, recyclerView2, recyclerView3;
    RecyclerView.Adapter recyclerViewAdapter, recyclerViewAdapter2, recyclerViewAdapter3;
    RecyclerView.LayoutManager recylerViewLayoutManager, recylerViewLayoutManager2, recylerViewLayoutManager3;
    ArrayList<PortfolioModel> data, data2, data3;
    ExtendedFloatingActionButton extendedFloatingActionButton;
    private SessionManager sessionManager;
    private String idDips;
    private String bahasa;
    private boolean cekCust;
    ImageView btnToogleShow, btnToogleHide;
    private JSONObject dataNasabah = null;
    private JSONArray produkListPorto = null;
    int[] imgDana = {R.drawable.porto1,R.drawable.porto2,R.drawable.porto3};
    int[] imgInves = {R.drawable.porto4,R.drawable.porto5,R.drawable.porto6,R.drawable.porto7};
    int[] imgKredit = {R.drawable.porto8};
    private JSONArray typeProdukListArr;

    private static final int[] MATERIAL_COLORS = {
            rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db"), rgb("#ed0ff1"),
            rgb("#90e610"), rgb("#f2ad0c"), rgb("#0af28a"), rgb("#f20a4c"), rgb("#f20a7a")
    };
    private RecyclerView rv_item_expand;

    private static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessionManager = new SessionManager(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolio, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        nestedScrollView = view.findViewById(R.id.nestedz);
        extendedFloatingActionButton = view.findViewById(R.id.btnPnL);
        tvtanggal = view.findViewById(R.id.date);
        recyclerView = view.findViewById(R.id.rv_item_1);
        recyclerView2 = view.findViewById(R.id.rv_item_2);
        recyclerView3 = view.findViewById(R.id.rv_item_3);
        DanaPihakKetiga = view.findViewById(R.id.DanaPihakKetiga);
        Investasi = view.findViewById(R.id.DanaInvestasi);
        Kredit = view.findViewById(R.id.DanaKredit);
        bahasa = sessionManager.getLANG();
        cekCust = sessionManager.getKEY_iSCust();
        btnToogleShow = view.findViewById(R.id.btn_toogle_eye);
        btnToogleHide = view.findViewById(R.id.btn_toogle_eye2);
        tvCurrency = view.findViewById(R.id.currency);

        rv_item_expand = (RecyclerView) view.findViewById(R.id.rv_item_expand);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idDips = sessionManager.getKEY_IdDips();
        dataNasabah = new JSONObject();
        produkListPorto = new JSONArray();

        getPortofolio();

        Calendar c = Calendar.getInstance();
        System.out.println("Current Time =>"+c.getTime());
        if (bahasa.equalsIgnoreCase("en")){
            SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMMM yyy", Locale.US);
            TanggalSekarang = df.format(c.getTime());
            tvtanggal.setText(TanggalSekarang);
        }
        else if (bahasa.equalsIgnoreCase("id")){
            SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMMM yyy",new Locale("id", "ID"));
            TanggalSekarang = df.format(c.getTime());
            tvtanggal.setText(TanggalSekarang);
        }
        tvCurrency.setText(getResources().getString(R.string.currency));
        //btnToogleHide.setVisibility(View.VISIBLE);
        /*btnToogleShow.setVisibility(View.GONE);
        btnToogleShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToogleShow.setVisibility(View.GONE);
                btnToogleHide.setVisibility(View.VISIBLE);
                addDataDanaPihakKetigaMasking();
                addDataInvestasiMasking();
                addDataKreditMasking();
                setRecyler();
            }
        });
        btnToogleHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToogleShow.setVisibility(View.VISIBLE);
                btnToogleHide.setVisibility(View.GONE);
                addDataDanaPihakKetiga();
                addDataInvestasi();
                addDataKredit();
                setRecyler();
            }
        });*/
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 0){
                    extendedFloatingActionButton.setVisibility(View.GONE);
                }
                else {
                    extendedFloatingActionButton.setVisibility(View.VISIBLE);
                }

            }
        });
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(true);
                getFragmentPage(new frag_service());
            }
        });

    }

    private void getPortofolio() {
        Log.e("CEK","MASUK GET PORTOFOLIO idDips : "+idDips);
        Server.getAPIService().GetPortofolio(idDips).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","response body getPortofolio : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("err_code");
                        if (errCode == 0) {
                            JSONObject datas = dataObj.getJSONObject("data");
                            dataNasabah = datas;
                            sessionManager.saveNasabah(dataNasabah.toString());
                            produkListPorto = datas.getJSONArray("produk");

                            setChartNasabah();
                            setRecylerExpand();
                            /*addDataDanaPihakKetigaMasking();
                            addDataInvestasiMasking();
                            addDataKreditMasking();

                            setRecyler();*/
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

    private void setRecylerExpand() {
        Log.e("CEK","MASUK setRecylerExpand");

        AdapterPortofolioNew dataExpand = new AdapterPortofolioNew(context, typeProdukListArr);

        recylerViewLayoutManager = new LinearLayoutManager(context);

        rv_item_expand.setLayoutManager(recylerViewLayoutManager);
        rv_item_expand.setAdapter(dataExpand);
    }

    private void Mirroring(boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",15);
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

    private void setRecyler(){
        if (data2.size() == 0) {
            Investasi.setVisibility(View.GONE);
        } else {
            Investasi.setVisibility(View.VISIBLE);
        }
        if (data3.size() == 0) {
            Kredit.setVisibility(View.GONE);
        } else {
            Kredit.setVisibility(View.VISIBLE);
        }
        recyclerViewAdapter = new AdapterPortofolio(context, data);
        recyclerViewAdapter2 = new AdapterPortofolio(context, data2);
        recyclerViewAdapter3 = new AdapterPortofolio(context, data3);

        recylerViewLayoutManager = new LinearLayoutManager(context);
        recylerViewLayoutManager2 = new LinearLayoutManager(context);
        recylerViewLayoutManager3 = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView2.setLayoutManager(recylerViewLayoutManager2);
        recyclerView2.setAdapter(recyclerViewAdapter2);

        recyclerView3.setLayoutManager(recylerViewLayoutManager3);
        recyclerView3.setAdapter(recyclerViewAdapter3);
    }
    private void setChartNasabah(){
        List<PieEntry> pieEntryList = new ArrayList<>();
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);

        setLegendChart();

        int len = produkListPorto.length();
        typeProdukListArr = new JSONArray();
        int loop = 0;
        for (int i = 0; i < len; i++) {
            try {
                JSONArray listProduk = null;
                JSONObject produkObj = produkListPorto.getJSONObject(i);

                listProduk = produkObj.getJSONArray("list");
                for (int j = 0; j < listProduk.length(); j++) {
                    JSONObject dataValProduk = new JSONObject();

                    String jenis = listProduk.getJSONObject(j).getString("jenis");
                    String icon = listProduk.getJSONObject(j).getString("icon");
                    String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                    String typeProduk = jenis;
                    if (typeProdukListArr.length() > 0) {
                        boolean cekFlag = true;
                        for (int k = 0; k < typeProdukListArr.length(); k++) {
                            String cekTypeProduk = typeProdukListArr.getJSONObject(k).getString("typeProduct");
                            if (typeProduk.toLowerCase().equals(cekTypeProduk.toLowerCase())) {
                                cekFlag = false;

                                int valProduk = typeProdukListArr.getJSONObject(k).getInt("value");
                                int addVal = valProduk + 1;
                                typeProdukListArr.getJSONObject(k).put("value",addVal);

                                JSONArray dataList = typeProdukListArr.getJSONObject(k).getJSONArray("dataList");
                                dataList.put(listProduk.getJSONObject(j));
                                typeProdukListArr.getJSONObject(k).put("dataList",dataList);
                                break;
                            }
                        }

                        if (cekFlag) {
                            JSONArray dataList = new JSONArray();
                            dataList.put(listProduk.getJSONObject(j));

                            dataValProduk.put("value",1);
                            dataValProduk.put("typeProduct",typeProduk);
                            dataValProduk.put("nameProduct",namaProduk);
                            dataValProduk.put("icon",icon);
                            dataValProduk.put("dataList",dataList);
                            typeProdukListArr.put(loop,dataValProduk);
                            loop++;
                        }

                    } else {
                        JSONArray dataList = new JSONArray();
                        dataList.put(listProduk.getJSONObject(j));

                        dataValProduk.put("value",1);
                        dataValProduk.put("typeProduct",typeProduk);
                        dataValProduk.put("nameProduct",namaProduk);
                        dataValProduk.put("icon",icon);
                        dataValProduk.put("dataList",dataList);
                        typeProdukListArr.put(loop,dataValProduk);
                        loop++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("CEK","typeProdukListArr : "+typeProdukListArr);

        int totalVal = 0;
        for (int i = 0; i < typeProdukListArr.length(); i++) {
            try {
                int valProduk = typeProdukListArr.getJSONObject(i).getInt("value");
                totalVal += valProduk;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < typeProdukListArr.length(); i++) {
            try {
                String namaProduk = typeProdukListArr.getJSONObject(i).getString("typeProduct");
                int valProduk = typeProdukListArr.getJSONObject(i).getInt("value");

                String percent = "";
                if (dataNasabah.has("pai")) {
                    if (!dataNasabah.isNull("pai")) {
                        Log.e("CEK","MASUK PAI namaProduk : "+namaProduk);
                        if (dataNasabah.getJSONObject("pai").has(namaProduk)) {
                            JSONObject paiProduk = dataNasabah.getJSONObject("pai").getJSONObject(namaProduk);
                            Log.e("CEK","paiProduk : "+paiProduk.toString());
                            int getPercent = paiProduk.getInt("persenan");
                            double d = (double) getPercent / 10;
                            percent = String.format("%.1f", d);
                            Log.e("CEK","percent : "+percent);
                        }
                    }
                }

                /*if (percent.isEmpty()) {
                    double d = (double) valProduk / totalVal;
                    float valPercent = (float) d * 10;
                    percent = String.format("%.1f", valPercent);
                }*/
                if (!percent.isEmpty()) {
                    percent = percent.replace(",", ".");
                    pieEntryList.add(new PieEntry(Float.parseFloat(percent), namaProduk));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        if (pieEntryList.size() > 5) {
            pieDataSet.setColors(MATERIAL_COLORS);
        } else {
            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        }
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.invalidate();
    }
    private void setLegendChart(){
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(12);
        l.setDrawInside(false);
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    ///Data Pihak Ketiga
    private void addDataDanaPihakKetiga(){
        data = new ArrayList<>();
        int len = produkListPorto.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject produkObj = produkListPorto.getJSONObject(i);
                String catg = produkObj.getString("kategori");
                if (catg.trim().toLowerCase().equals("dana pihak ketiga")) {
                    JSONArray listProduk = produkObj.getJSONArray("list");
                    for (int j = 0; j < listProduk.length(); j++) {
                        String jenis = listProduk.getJSONObject(j).getString("jenis").trim();
                        String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                        String noRekening = listProduk.getJSONObject(j).getString("noRekening").trim();
                        String jumlahDana = String.valueOf(listProduk.getJSONObject(j).getLong("jumlahDana"));
                        String kurs = listProduk.getJSONObject(j).getString("kurs").trim();
                        String icon = listProduk.getJSONObject(j).getString("icon").trim();

                        BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                        String formatted = frag_dialog_rtgs.numberFormat.format(parsed);

                        namaProduk += " - "+noRekening;
                        String curs = getResources().getString(R.string.mata_uang);
                        if (!kurs.equals(curs)) {
                            if (curs.equals("Rp")) {
                                curs = curs;
                            } else {
                                curs = kurs;
                            }
                        }
                        String dataN = curs + " " + formatted;
                        int getImg = 0;
                        if (jenis.trim().toLowerCase().equals("giro")) {
                            getImg = imgDana[0];
                        } else if (jenis.trim().toLowerCase().equals("tabungan")) {
                            getImg = imgDana[1];
                        } else if (jenis.trim().toLowerCase().equals("deposito")) {
                            getImg = imgDana[2];
                        } else {
                            getImg = imgDana[2];
                        }

                        data.add(new PortfolioModel(String.valueOf(j),namaProduk,dataN,getImg,icon));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDataDanaPihakKetigaMasking(){
        data = new ArrayList<>();
        int len = produkListPorto.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject produkObj = produkListPorto.getJSONObject(i);
                String catg = produkObj.getString("kategori");
                if (catg.trim().toLowerCase().equals("dana pihak ketiga")) {
                    JSONArray listProduk = produkObj.getJSONArray("list");
                    for (int j = 0; j < listProduk.length(); j++) {
                        String jenis = listProduk.getJSONObject(j).getString("jenis").trim();
                        String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                        String noRekening = listProduk.getJSONObject(j).getString("noRekening").trim();
                        String jumlahDana = String.valueOf(listProduk.getJSONObject(j).getLong("jumlahDana"));
                        String kurs = listProduk.getJSONObject(j).getString("kurs").trim();
                        String icon = listProduk.getJSONObject(j).getString("icon").trim();

                        BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                        String formatted = frag_dialog_rtgs.numberFormat.format(parsed);
                        formatted = formatted.replace(formatted,"XXXXXX");

                        namaProduk += " - "+noRekening;
                        String curs = getResources().getString(R.string.mata_uang);
                        if (!kurs.equals(curs)) {
                            if (curs.equals("Rp")) {
                                curs = curs;
                            } else {
                                curs = kurs;
                            }
                        }
                        String dataN = curs + " " + formatted;
                        int getImg = 0;
                        if (jenis.trim().toLowerCase().equals("giro")) {
                            getImg = imgDana[0];
                        } else if (jenis.trim().toLowerCase().equals("tabungan")) {
                            getImg = imgDana[1];
                        } else if (jenis.trim().toLowerCase().equals("deposito")) {
                            getImg = imgDana[2];
                        } else {
                            getImg = imgDana[2];
                        }

                        data.add(new PortfolioModel(String.valueOf(j),namaProduk,dataN,getImg,icon));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /// Data Investasi
    private void addDataInvestasi(){
        data2 = new ArrayList<>();
        int len = produkListPorto.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject produkObj = produkListPorto.getJSONObject(i);
                String catg = produkObj.getString("kategori");
                if (catg.trim().toLowerCase().equals("investasi")) {
                    JSONArray listProduk = produkObj.getJSONArray("list");
                    for (int j = 0; j < listProduk.length(); j++) {
                        String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                        String noRekening = listProduk.getJSONObject(j).getString("noRekening").trim();
                        String jumlahDana = String.valueOf(listProduk.getJSONObject(j).getLong("jumlahDana"));
                        String kurs = listProduk.getJSONObject(j).getString("kurs").trim();
                        String icon = listProduk.getJSONObject(j).getString("icon").trim();

                        BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                        String formatted = frag_dialog_rtgs.numberFormat.format(parsed);

                        namaProduk += " - "+noRekening;
                        String curs = getResources().getString(R.string.mata_uang);
                        if (!kurs.equals(curs)) {
                            if (curs.equals("Rp")) {
                                curs = curs;
                            } else {
                                curs = kurs;
                            }
                        }
                        String dataN = curs + " " + formatted;
                        int getImg = 0;
                        int lenImg = imgInves.length;
                        if (j < lenImg-1) {
                            getImg = imgInves[j];
                        } else {
                            getImg = imgInves[lenImg-1];
                        }

                        data2.add(new PortfolioModel(String.valueOf(j),namaProduk,dataN,getImg,icon));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDataInvestasiMasking(){
        data2 = new ArrayList<>();
        int len = produkListPorto.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject produkObj = produkListPorto.getJSONObject(i);
                String catg = produkObj.getString("kategori");
                if (catg.trim().toLowerCase().equals("investasi")) {
                    JSONArray listProduk = produkObj.getJSONArray("list");
                    for (int j = 0; j < listProduk.length(); j++) {
                        String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                        String noRekening = listProduk.getJSONObject(j).getString("noRekening").trim();
                        String jumlahDana = String.valueOf(listProduk.getJSONObject(j).getLong("jumlahDana"));
                        String kurs = listProduk.getJSONObject(j).getString("kurs").trim();
                        String icon = listProduk.getJSONObject(j).getString("icon").trim();

                        BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                        String formatted = frag_dialog_rtgs.numberFormat.format(parsed);
                        formatted = formatted.replace(formatted,"XXXXXX");

                        namaProduk += " - "+noRekening;
                        String curs = getResources().getString(R.string.mata_uang);
                        if (!kurs.equals(curs)) {
                            if (curs.equals("Rp")) {
                                curs = curs;
                            } else {
                                curs = kurs;
                            }
                        }
                        String dataN = curs + " " + formatted;
                        int getImg = 0;
                        int lenImg = imgInves.length;
                        if (j < lenImg-1) {
                            getImg = imgInves[j];
                        } else {
                            getImg = imgInves[lenImg-1];
                        }

                        data2.add(new PortfolioModel(String.valueOf(j),namaProduk,dataN,getImg,icon));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /// Data Kredit
    private void addDataKredit(){
        data3 = new ArrayList<>();
        int len = produkListPorto.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject produkObj = produkListPorto.getJSONObject(i);
                String catg = produkObj.getString("kategori");
                if (catg.trim().toLowerCase().equals("kredit")) {
                    JSONArray listProduk = produkObj.getJSONArray("list");
                    for (int j = 0; j < listProduk.length(); j++) {
                        String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                        String noRekening = listProduk.getJSONObject(j).getString("noRekening").trim();
                        String jumlahDana = String.valueOf(listProduk.getJSONObject(j).getLong("jumlahDana"));
                        String kurs = listProduk.getJSONObject(j).getString("kurs").trim();
                        String icon = listProduk.getJSONObject(j).getString("icon").trim();

                        BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                        String formatted = frag_dialog_rtgs.numberFormat.format(parsed);

                        namaProduk += " - "+noRekening;
                        String curs = getResources().getString(R.string.mata_uang);
                        if (!kurs.equals(curs)) {
                            if (curs.equals("Rp")) {
                                curs = curs;
                            } else {
                                curs = kurs;
                            }
                        }
                        String dataN = curs + " " + formatted;
                        int getImg = 0;
                        int lenImg = imgKredit.length;
                        if (j < lenImg-1) {
                            getImg = imgKredit[j];
                        } else {
                            getImg = imgKredit[lenImg-1];
                        }

                        data3.add(new PortfolioModel(String.valueOf(j),namaProduk,dataN,getImg,icon));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*data3.add(new PortfolioModel("1",getResources().getString(R.string.DIPS_MODAL_KERJA),getResources().getString(R.string.mata_uang)+" 15.000.000,00",R.drawable.porto8));
        data3.add(new PortfolioModel("2",getResources().getString(R.string.DIPS_INVESTMENT),getResources().getString(R.string.mata_uang)+" 12.000.000,00",R.drawable.porto8));*/
    }
    private void addDataKreditMasking(){
        data3 = new ArrayList<>();
        int len = produkListPorto.length();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject produkObj = produkListPorto.getJSONObject(i);
                String catg = produkObj.getString("kategori");
                if (catg.trim().toLowerCase().equals("kredit")) {
                    JSONArray listProduk = produkObj.getJSONArray("list");
                    for (int j = 0; j < listProduk.length(); j++) {
                        String namaProduk = listProduk.getJSONObject(j).getString("namaProduk").trim();
                        String noRekening = listProduk.getJSONObject(j).getString("noRekening").trim();
                        String jumlahDana = String.valueOf(listProduk.getJSONObject(j).getLong("jumlahDana"));
                        String kurs = listProduk.getJSONObject(j).getString("kurs").trim();
                        String icon = listProduk.getJSONObject(j).getString("icon").trim();

                        BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                        String formatted = frag_dialog_rtgs.numberFormat.format(parsed);
                        formatted = formatted.replace(formatted,"XXXXXX");

                        namaProduk += " - "+noRekening;
                        String curs = getResources().getString(R.string.mata_uang);
                        if (!kurs.equals(curs)) {
                            if (curs.equals("Rp")) {
                                curs = curs;
                            } else {
                                curs = kurs;
                            }
                        }
                        String dataN = curs + " " + formatted;
                        int getImg = 0;
                        int lenImg = imgKredit.length;
                        if (j < lenImg-1) {
                            getImg = imgKredit[j];
                        } else {
                            getImg = imgKredit[lenImg-1];
                        }

                        data3.add(new PortfolioModel(String.valueOf(j),namaProduk,dataN,getImg,icon));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*data3.add(new PortfolioModel("1",getResources().getString(R.string.DIPS_MODAL_KERJA),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto8));
        data3.add(new PortfolioModel("2",getResources().getString(R.string.DIPS_INVESTMENT),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto8));*/
    }
}
