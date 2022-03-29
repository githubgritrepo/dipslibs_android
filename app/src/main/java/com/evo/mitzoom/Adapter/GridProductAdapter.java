package com.evo.mitzoom.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;

public class GridProductAdapter extends RecyclerView.Adapter<GridProductAdapter.GriViewHolder> {

    int[] gambar;

    public GridProductAdapter(int[] gambar) {
        this.gambar = gambar;
    }

    @NonNull
    @Override
    public GridProductAdapter.GriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid,parent,false);
        return new GriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridProductAdapter.GriViewHolder holder, int position) {
        holder.ads.setImageResource(gambar[position]);
    }

    @Override
    public int getItemCount() {
        return gambar.length;
    }

    public class GriViewHolder extends RecyclerView.ViewHolder {

        ImageView ads;
        public GriViewHolder(@NonNull View itemView) {
            super(itemView);

            ads = (ImageView) itemView.findViewById(R.id.ads);
        }
    }
}
