package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.FormRtgs;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class GridProductAdapter extends RecyclerView.Adapter<GridProductAdapter.GriViewHolder> {

    int[] gambar;
    int position;
    Context ctx;
    MaterialStyledDialog materialStyledDialog;
    SessionManager sessions;

    public GridProductAdapter(Context ctx, int[] gambar) {
        this.ctx = ctx;
        this.gambar = gambar;
        this.sessions = new SessionManager(this.ctx);
    }

    @NonNull
    @Override
    public GridProductAdapter.GriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid,parent,false);
        return new GriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridProductAdapter.GriViewHolder holder, int position) {
        int pos = position;
        holder.ads.setImageResource(gambar[position]);
        holder.ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0) {
                    ((FragmentActivity)ctx).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_frame, new FormRtgs())
                            .addToBackStack(null)
                            .commit();
                }
                else if (pos == 1){
                    Toast.makeText(ctx, "Transfer Remittance", Toast.LENGTH_SHORT).show();
                }
                else {
                    popUpAds();
                }
            }
        });
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
