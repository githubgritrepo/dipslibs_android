package com.evo.mitzoom.Fragments;

import android.content.Context;
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

import com.evo.mitzoom.Adapter.ItemBankAdapter;
import com.evo.mitzoom.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class frag_portfolio extends Fragment {
    private Context context;
    PieChart pieChart;
    PieData pieData;
    Legend lgnd;
    NestedScrollView nestedScrollView;
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        lgnd = pieChart.getLegend();
        lgnd.setFormSize(10f);
        lgnd.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lgnd.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        lgnd.setOrientation(Legend.LegendOrientation.VERTICAL);
        lgnd.setDrawInside(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieEntryList.add(new PieEntry(14,"Tabungan"));
        pieEntryList.add(new PieEntry(5,"Giro"));
        pieEntryList.add(new PieEntry(3,"Deposito Berjangka"));
        pieEntryList.add(new PieEntry(2,"Qtr"));
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
