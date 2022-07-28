package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Adapter.AdapterFile;
import com.evo.mitzoom.Adapter.AdapterPortofolio;
import com.evo.mitzoom.Adapter.SectionsPagerAdapter;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Model.FileModel;
import com.evo.mitzoom.Model.PortfolioModel;
import com.evo.mitzoom.R;

import java.util.ArrayList;

public class frag_daftar_file extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private ArrayList<FileModel> data;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Toast.makeText(context, "Create", Toast.LENGTH_SHORT).show();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daftar_file, container, false);
        recyclerView = view.findViewById(R.id.rv_item_file);
        recyclerView.setHasFixedSize(true);
        Toast.makeText(context, "Create View", Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addData();
        setRecyler();
        Toast.makeText(context, "View Created", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(context, "DestroyView", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "Destroy ", Toast.LENGTH_SHORT).show();
    }

    private void setRecyler(){
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new AdapterFile(getContext(), data);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }
    private void addData(){
        data = new ArrayList<>();
        data.add(new FileModel("1","Form Pembukaan Rekening.Docx",R.color.item_file_silver,"https://google.com/"));
        data.add(new FileModel("2","Brosur Produk Bank.pdf",R.color.white,"https://google.com/"));
        data.add(new FileModel("3","Form Pembukaan Rekening.Docx",R.color.item_file_silver,"https://google.com/"));
        data.add(new FileModel("4","Brosur Produk Bank.pdf",R.color.white,"https://google.com/"));
    }
}
