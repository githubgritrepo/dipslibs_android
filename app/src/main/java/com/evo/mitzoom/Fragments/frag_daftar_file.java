package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Adapter.AdapterFile;
import com.evo.mitzoom.Model.FileModel;
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
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daftar_file, container, false);
        recyclerView = view.findViewById(R.id.rv_item_file);
        recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addData();
        setRecyler();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
