package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.FormRtgs;
import com.evo.mitzoom.Fragments.frag_form_komplain;
import com.evo.mitzoom.Fragments.frag_new_account;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GridProductAdapter extends RecyclerView.Adapter<GridProductAdapter.GriViewHolder> {

    int[] gambar;
    int position;
    Context ctx;
    MaterialStyledDialog materialStyledDialog;
    SessionManager sessions;
    private LayoutInflater inflater;
    private View dialogView;
    private SweetAlertDialog sweetAlertDialogTNC;

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
                    PopUpTnc();
                }
                else if (pos == 2){
                    ((FragmentActivity)ctx).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_frame, new frag_form_komplain())
                            .addToBackStack(null)
                            .commit();
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
    private void PopUpTnc(){
        sweetAlertDialogTNC = new SweetAlertDialog(ctx, SweetAlertDialog.NORMAL_TYPE);
        inflater = ((Activity)ctx).getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_tnc,null);
        if (sweetAlertDialogTNC == null) {
            sweetAlertDialogTNC = new SweetAlertDialog(ctx, SweetAlertDialog.NORMAL_TYPE);
        }
        else{
            sweetAlertDialogTNC.setCustomView(dialogView);
            sweetAlertDialogTNC.hideConfirmButton();
            sweetAlertDialogTNC.setCancelable(false);
            sweetAlertDialogTNC.show();
            CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
            Button btn = dialogView.findViewById(R.id.btnnexttnc);
            btn.setClickable(false);
            btn.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.btnFalse));
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()){
                        Log.d("CHECK","TRUE");
                        btn.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.Blue));
                        btn.setClickable(true);
                    }
                    else {
                        Log.d("CHECK","FALSE");
                        btn.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.btnFalse));
                        btn.setClickable(false);
                    }
                }
            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()){
                        ((FragmentActivity)ctx).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.layout_frame, new frag_new_account())
                                .addToBackStack(null)
                                .commit();
                        sweetAlertDialogTNC.dismiss();
                    }
                    else {
                        btn.setClickable(false);
                    }
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
