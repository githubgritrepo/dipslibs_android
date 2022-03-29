package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.evo.mitzoom.R;

public class GridAdapter extends BaseAdapter {

    Context context;
    int[] gambar;
    String[] namanya;
    LayoutInflater inflter;

    public GridAdapter(Context applicationContext, int[] gambar, String[] namanya) {
        this.context = applicationContext;
        this.gambar = gambar;
        this.namanya = namanya;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return gambar.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflter.inflate(R.layout.grid,  null);
        }
        ImageView gambarHewan = view.findViewById(R.id.ads);
        gambarHewan.setImageResource(gambar[position]);
        return view;
    }
}
