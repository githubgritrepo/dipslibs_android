package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GridProductAdapter extends RecyclerView.Adapter<GridProductAdapter.GriViewHolder> {

    int[] gambar;
    int position;
    Context ctx;
    MaterialStyledDialog materialStyledDialog;

    public GridProductAdapter(Context ctx, int[] gambar) {
        this.ctx = ctx;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpAds();
                }
            });
        }
    }

    private void popUpAds() {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_ads,null);
        materialStyledDialog = new MaterialStyledDialog.Builder(ctx)
                .setHeaderDrawable(R.drawable.bannerads)
                .setCancelable(false)
                .setCustomView(view, 20, 20, 20, 0)
                .setHeaderScaleType(ImageView.ScaleType.FIT_XY)
                .show();
        ExtendedFloatingActionButton materialButton = view.findViewById(R.id.btnback);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialStyledDialog.dismiss();
            }
        });
    }

}
