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

import com.evo.mitzoom.Adapter.ItemBankAdapter;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;

import java.util.ArrayList;

public class frag_item extends Fragment {
    private Context context;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    ArrayList<ItemModel> data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_item_bank, container, false);
        recyclerView = view.findViewById(R.id.rv_item);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addData();
        recyclerViewAdapter = new ItemBankAdapter(context, data);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void addData(){
        data = new ArrayList<>();
        data.add(new ItemModel("1",getResources().getString(R.string.products_information),R.drawable.item1));
        data.add(new ItemModel("2",getResources().getString(R.string.create_new_account),R.drawable.item2));
        data.add(new ItemModel("3",getResources().getString(R.string.credit_simulation),R.drawable.item3));
        data.add(new ItemModel("4",getResources().getString(R.string.call_center),R.drawable.item4));
    }
}
