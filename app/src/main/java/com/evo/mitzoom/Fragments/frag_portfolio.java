package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
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

import com.evo.mitzoom.Adapter.AdapterPortofolio;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    List<PieEntry> pieEntryList = new ArrayList<>();
    ExtendedFloatingActionButton extendedFloatingActionButton;
    private SessionManager sessionManager;
    private String bahasa;
    private boolean cekCust;
    ImageView btnToogleShow, btnToogleHide;

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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        btnToogleHide.setVisibility(View.VISIBLE);
        btnToogleShow.setVisibility(View.GONE);
        if (cekCust){
            setChartNasabah();
            setLegendChart();
            addDataDanaPihakKetigaMasking();
            addDataInvestasiMasking();
            addDataKreditMasking();
        }
        else {
            addDataDanaPihakKetigaNewMasking();
            setChartNewNasabah();
            setLegendChart();
            recyclerView2.setVisibility(View.GONE);
            recyclerView3.setVisibility(View.GONE);
            DanaPihakKetiga.setVisibility(View.GONE);
            Investasi.setVisibility(View.GONE);
            Kredit.setVisibility(View.GONE);
        }
        setRecyler();
        btnToogleShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToogleShow.setVisibility(View.GONE);
                btnToogleHide.setVisibility(View.VISIBLE);
                if (cekCust){
                    addDataDanaPihakKetigaMasking();
                    addDataInvestasiMasking();
                    addDataKreditMasking();
                }
                else {
                    addDataDanaPihakKetigaNewMasking();
                }
                setRecyler();
            }
        });
        btnToogleHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToogleShow.setVisibility(View.VISIBLE);
                btnToogleHide.setVisibility(View.GONE);
                if (cekCust){
                    addDataDanaPihakKetiga();
                    addDataInvestasi();
                    addDataKredit();
                }
                else {
                    addDataDanaPihakKetigaEmpty();
                }
                setRecyler();
            }
        });
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
                getFragmentPage(new frag_service());
            }
        });


    }
    private void setRecyler(){
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
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        if (pieEntryList.size() < 1){
            pieEntryList.add(new PieEntry(3,"Giro"));
            pieEntryList.add(new PieEntry(2,"Tabungan"));
            pieEntryList.add(new PieEntry(1,"Deposito"));
            pieEntryList.add(new PieEntry(4,"Reksa Dana"));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.invalidate();
    }
    private void setChartNewNasabah(){
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        if (pieEntryList.size() < 1){
            pieEntryList.add(new PieEntry(100,getResources().getString(R.string.tabungan_a)));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
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
        data.add(new PortfolioModel("1",getResources().getString(R.string.GIRO_DIPS),getResources().getString(R.string.mata_uang)+" 15.000.000",R.drawable.porto1));
        data.add(new PortfolioModel("3",getResources().getString(R.string.TABUNGAN_DIPS),getResources().getString(R.string.mata_uang)+" 12.000.000",R.drawable.porto2));
        data.add(new PortfolioModel("4",getResources().getString(R.string.DEPOSITO_DIPS),getResources().getString(R.string.mata_uang)+" 100.000.000",R.drawable.porto3));
    }
    private void addDataDanaPihakKetigaEmpty(){
        data = new ArrayList<>();
        data.add(new PortfolioModel("3",getResources().getString(R.string.TABUNGAN_DIPS),getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto2));
    }
    private void addDataDanaPihakKetigaMasking(){
        data = new ArrayList<>();
        data.add(new PortfolioModel("1",getResources().getString(R.string.GIRO_DIPS),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto1));
        data.add(new PortfolioModel("3",getResources().getString(R.string.TABUNGAN_DIPS),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto2));
        data.add(new PortfolioModel("4",getResources().getString(R.string.DEPOSITO_DIPS),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto3));
    }
    private void addDataDanaPihakKetigaNewMasking(){
        data = new ArrayList<>();
        data.add(new PortfolioModel("1",getResources().getString(R.string.TABUNGAN_DIPS),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto2));
    }

    /// Data Investasi
    private void addDataInvestasi(){
        data2 = new ArrayList<>();
        data2.add(new PortfolioModel("1","DiPS Wealthlink",getResources().getString(R.string.mata_uang)+" 15.000.000",R.drawable.porto4));
        data2.add(new PortfolioModel("2","DiPS Protect Life",getResources().getString(R.string.mata_uang)+" 12.000.000,00",R.drawable.porto5));
        data2.add(new PortfolioModel("3","DiPS Money Market Fund",getResources().getString(R.string.mata_uang)+" 12.000.000,00",R.drawable.porto6));
        data2.add(new PortfolioModel("4","ORI 022",getResources().getString(R.string.mata_uang)+" 100.000.000,00",R.drawable.porto7));
        data2.add(new PortfolioModel("5","SR 014",getResources().getString(R.string.mata_uang)+" 80.000,00",R.drawable.porto7));
    }
    private void addDataInvestasiEmpty(){
        data2 = new ArrayList<>();
        data2.add(new PortfolioModel("1","DiPS Wealthlink",getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto4));
        data2.add(new PortfolioModel("2","DiPS Protect Life",getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto5));
        data2.add(new PortfolioModel("3","DiPS Money Market Fund",getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto6));
        data2.add(new PortfolioModel("4","ORI 022",getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto7));
        data2.add(new PortfolioModel("5","SR 014",getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto7));
    }
    private void addDataInvestasiMasking(){
        data2 = new ArrayList<>();
        data2.add(new PortfolioModel("1","DiPS Wealthlink",getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto4));
        data2.add(new PortfolioModel("2","DiPS Protect Life",getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto5));
        data2.add(new PortfolioModel("3","DiPS Money Market Fund",getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto6));
        data2.add(new PortfolioModel("4","ORI 022",getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto7));
        data2.add(new PortfolioModel("5","SR 014",getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto7));
    }

    /// Data Kredit
    private void addDataKredit(){
        data3 = new ArrayList<>();
        data3.add(new PortfolioModel("1",getResources().getString(R.string.DIPS_MODAL_KERJA),getResources().getString(R.string.mata_uang)+" 15.000.000,00",R.drawable.porto8));
        data3.add(new PortfolioModel("2",getResources().getString(R.string.DIPS_INVESTMENT),getResources().getString(R.string.mata_uang)+" 12.000.000,00",R.drawable.porto8));
    }
    private void addDataKreditEmpty(){
        data3 = new ArrayList<>();
        data3.add(new PortfolioModel("1",getResources().getString(R.string.DIPS_MODAL_KERJA),getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto8));
        data3.add(new PortfolioModel("2",getResources().getString(R.string.DIPS_INVESTMENT),getResources().getString(R.string.mata_uang)+" 0",R.drawable.porto8));
    }
    private void addDataKreditMasking(){
        data3 = new ArrayList<>();
        data3.add(new PortfolioModel("1",getResources().getString(R.string.DIPS_MODAL_KERJA),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto8));
        data3.add(new PortfolioModel("2",getResources().getString(R.string.DIPS_INVESTMENT),getResources().getString(R.string.mata_uang)+" XXXXXX",R.drawable.porto8));
    }
}
