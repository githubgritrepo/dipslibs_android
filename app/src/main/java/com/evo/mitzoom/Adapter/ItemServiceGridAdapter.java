package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.frag_open_account_product;
import com.evo.mitzoom.Fragments.frag_service_item;
import com.evo.mitzoom.Fragments.frag_service_item_new;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsWaitingRoom;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ItemServiceGridAdapter extends RecyclerView.Adapter<ItemServiceGridAdapter.ItemHolder>{

    private final RabbitMirroring rabbitMirroring;
    private ArrayList<ItemModel> dataList;
    private Context mContext;
    private SessionManager sessions;
    private String idDips;
    private Bundle bundle;
    private Fragment fragment;

    public ItemServiceGridAdapter(ArrayList<ItemModel> dataList, Context mContext, RabbitMirroring rabbitMirroring) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.rabbitMirroring = rabbitMirroring;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid, parent, false);
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        return new ItemHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.tvLabelItem.setVisibility(View.VISIBLE);
        holder.tvLabelItem.setText(dataList.get(position).getNamaItem());
        holder.ads.setImageResource(dataList.get(position).getGambarItem());

        holder.ads.setOnClickListener(v -> {
            switch (dataList.get(position).getId()){
                case "0" :
                    rabbitMirroring.MirroringSendEndpoint(201);
                    fragment = new frag_open_account_product();
                    getFragmentPage(fragment);
                    break;
                case "2" :/* SUDAH MIRRORING */
                    int intLayout = 35;
                    rabbitMirroring.MirroringSendEndpoint(intLayout);
                    //fragment = new frag_form_komplain();
                    /*String dataNasabah = sessions.getNasabah();
                    String no_handphone = "";
                    if (!dataNasabah.isEmpty()) {
                        try {
                            JSONObject dataNasabahObj = new JSONObject(dataNasabah);
                            if (dataNasabahObj.has("noHp")) {
                                no_handphone = dataNasabahObj.getString("noHp");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (no_handphone.isEmpty()) {
                        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

                        ImageView imgDialog = (ImageView) dialogView.findViewById(R.id.imgDialog);
                        TextView tvTitleDialog = (TextView) dialogView.findViewById(R.id.tvTitleDialog);
                        TextView tvBodyDialog = (TextView) dialogView.findViewById(R.id.tvBodyDialog);
                        Button btnCancelDialog = (Button) dialogView.findViewById(R.id.btnCancelDialog);
                        Button btnConfirmDialog = (Button) dialogView.findViewById(R.id.btnConfirmDialog);

                        tvTitleDialog.setVisibility(View.GONE);

                        imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_info));
                        tvBodyDialog.setText("");

                        SweetAlertDialog dialogEnd = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
                        dialogEnd.setCustomView(dialogView);
                        dialogEnd.setCancelable(false);
                        dialogEnd.hideConfirmButton();
                        dialogEnd.show();
                    }*/
                    fragment = new frag_service_item_new();
                    bundle = new Bundle();
                    bundle.putInt("form_id",43);
                    sessions.saveFormCOde(intLayout);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    break;
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)mContext).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView tvLabelItem;
        private final ImageView ads;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            ads = (ImageView) itemView.findViewById(R.id.ads);
            tvLabelItem = (TextView) itemView.findViewById(R.id.tvLabelItem);
        }
    }
}
