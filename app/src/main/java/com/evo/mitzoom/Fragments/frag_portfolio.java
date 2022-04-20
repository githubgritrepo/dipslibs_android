package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Adapter.AdapterPortofolio;
import com.evo.mitzoom.Adapter.ItemBankAdapter;
import com.evo.mitzoom.Adapter.ItemServiceAdapter;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.Model.PortfolioModel;
import com.evo.mitzoom.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
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

public class frag_portfolio extends Fragment {
    private Context context;
    PieChart pieChart;
    PieData pieData;
    TextView tvtanggal;
    NestedScrollView nestedScrollView;
    String TanggalSekarang;
    RecyclerView recyclerView, recyclerView2, recyclerView3;
    RecyclerView.Adapter recyclerViewAdapter, recyclerViewAdapter2, recyclerViewAdapter3;
    RecyclerView.LayoutManager recylerViewLayoutManager, recylerViewLayoutManager2, recylerViewLayoutManager3;
    ArrayList<PortfolioModel> data, data2, data3;
    List<PieEntry> pieEntryList = new ArrayList<>();
    ExtendedFloatingActionButton extendedFloatingActionButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addDataDanaPihakKetiga();
        addDataInvestasi();
        addDataKredit();
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


        Calendar c = Calendar.getInstance();
        System.out.println("Current Time =>"+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM YYY");
        TanggalSekarang = df.format(c.getTime());
        tvtanggal.setText(TanggalSekarang);
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
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieEntryList.add(new PieEntry(3,"Giro"));
        pieEntryList.add(new PieEntry(2,"Tabungan"));
        pieEntryList.add(new PieEntry(1,"Deposito"));
        pieEntryList.add(new PieEntry(4,"Reksa Dana"));
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.invalidate();

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
    private void addDataDanaPihakKetiga(){
        data = new ArrayList<>();
        data.add(new PortfolioModel("1","Giro DiPS Rupiah - 021008120","Rp 15.000.000,00",R.drawable.porto1));
        data.add(new PortfolioModel("2","Giro DiPS USD - 022347001","$ 15.000",R.drawable.porto1));
        data.add(new PortfolioModel("3","Tabungan DiPS Rupiah - 011043021","Rp 12.000.000,00",R.drawable.porto2));
        data.add(new PortfolioModel("4","Deposito DiPS Rupiah - 052120987","Rp 100.000.000,00",R.drawable.porto3));
        data.add(new PortfolioModel("5","Deposito DiPS USD - 054999201","$ 80.000",R.drawable.porto3));
    }
    private void addDataInvestasi(){
        data2 = new ArrayList<>();
        data2.add(new PortfolioModel("1","DiPS Wealthlink","Rp 15.000.000,00",R.drawable.porto4));
        data2.add(new PortfolioModel("2","DiPS Protect Life","Rp 12.000.000",R.drawable.porto5));
        data2.add(new PortfolioModel("3","DiPS Money Market Fund","Rp 12.000.000,00",R.drawable.porto6));
        data2.add(new PortfolioModel("4","ORI 022","Rp 100.000.000,00",R.drawable.porto7));
        data2.add(new PortfolioModel("5","SR 014","Rp 80.000",R.drawable.porto7));
    }
    private void addDataKredit(){
        data3 = new ArrayList<>();
        data3.add(new PortfolioModel("1","DiPS Kredit Modal Kerja - 992101345","Rp 15.000.000,00",R.drawable.porto8));
        data3.add(new PortfolioModel("2","DiPS Kredit Investasi - 992201209","Rp 12.000.000",R.drawable.porto8));
    }
}
