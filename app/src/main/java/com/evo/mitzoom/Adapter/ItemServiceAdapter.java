package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Fragments.frag_aktivasi_ibmb;
import com.evo.mitzoom.Fragments.frag_cardless;
import com.evo.mitzoom.Fragments.frag_form_credit;
import com.evo.mitzoom.Fragments.frag_form_komplain;
import com.evo.mitzoom.Fragments.frag_kartua_atm;
import com.evo.mitzoom.Fragments.frag_new_account_cs;
import com.evo.mitzoom.Fragments.frag_opening_account;
import com.evo.mitzoom.Fragments.frag_rtgs;
import com.evo.mitzoom.Fragments.frag_transaksi_valas;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemServiceAdapter extends RecyclerView.Adapter<ItemServiceAdapter.ItemHolder> {
    private ArrayList<ItemModel> dataList;
    private Context ctx;
    private SessionManager sessionManager;
    private String idDips;
    private LayoutInflater inflater;
    private View dialogView;
    private SweetAlertDialog sweetAlertDialogTNC;
    public ItemServiceAdapter(Context ctx, ArrayList<ItemModel> dataList){
        this.dataList = dataList;
        this.ctx = ctx;
    }
    private Bundle bundle;
    private Fragment fragment;
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
        sessionManager = new SessionManager(ctx);
        idDips = sessionManager.getKEY_IdDips();
        return new ItemHolder(view);
    }
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.NamaItem.setText(dataList.get(position).getNamaItem());
        holder.GambarItem.setImageResource(dataList.get(position).getGambarItem());
        holder.parent_layout.setOnClickListener(v -> {
            switch (dataList.get(position).getId()){
                case "0" :/* SUDAH MIRRORING */
                    Mirroring(36,true);
                    PopUpTnc("0");
                    return;
                case "1" : /* SUDAH MIRRORING */
                    Mirroring(16,true);
                    fragment = new frag_rtgs();
                    bundle = new Bundle();
                    /**STATE 1 UNTUK KE FORMULIR TRANSFER**/
                    bundle.putInt("state",1);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "2" :/* SUDAH MIRRORING */
                    Mirroring(35,true);
                    /**STATE 2 UNTUK BACK KE FRAGMENT DENGAN CS**/
                    fragment = new frag_form_komplain();
                    bundle = new Bundle();
                    bundle.putInt("state",2);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "3":
                    /**STATE 2 UNTUK KE FORMULIR TRANSFER**/
                    fragment = new frag_rtgs();
                    bundle = new Bundle();
                    bundle.putInt("state",2);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "4" :
                    fragment = new frag_form_credit();
                    bundle = new Bundle();
                    bundle.putInt("state",2);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "5" :
                    PopUpTnc("5");
                    return;
                case "6" :
                    PopUpTnc("6");
                    return;
                case "7" :
                    getFragmentPage(new frag_kartua_atm());
                    return;
                case "8" :
                    fragment = new frag_form_credit();
                    bundle = new Bundle();
                    bundle.putInt("state",4);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "9" :
                    getFragmentPage(new frag_aktivasi_ibmb());
                    return;
                case "10" :
                    fragment = new frag_form_credit();
                    bundle = new Bundle();
                    bundle.putInt("state",1);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "11" :
                    PopUpTnc("11");
                    return;
                case "12" :
                    return;
                case "13" :
                    fragment = new frag_form_credit();
                    bundle = new Bundle();
                    bundle.putInt("state",3);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "14" :
                    /**STATE 3 UNTUK KE FORMULIR KEKAYAAN**/
                    fragment = new frag_rtgs();
                    bundle = new Bundle();
                    bundle.putInt("state",3);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    return;
                case "15" :
                    getFragmentPage(new frag_cardless());
                    return;
            }
        });
    }
    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }
    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView NamaItem;
        private ImageView GambarItem;
        private CardView parent_layout;

        public ItemHolder(View itemView) {
            super(itemView);
            NamaItem = itemView.findViewById(R.id.nama_item);
            GambarItem = itemView.findViewById(R.id.images_item);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
    private void PopUpTnc(String statez){
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
                        if (statez.equals("0")){
                            Mirroring(361,true);
                            getFragmentPage(new frag_new_account_cs());
                            sweetAlertDialogTNC.dismiss();
                        }
                        else if (statez.equals("6")){
                            getFragmentPage(new frag_transaksi_valas());
                            sweetAlertDialogTNC.dismiss();
                        }
                        else {
                            getFragmentPage(new frag_opening_account());
                            sweetAlertDialogTNC.dismiss();
                        }
                    }
                    else {
                        btn.setClickable(false);
                    }
                }
            });
        }

    }
    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)ctx).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void Mirroring(int nextCode,boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nextCode);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",nextCode);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
}
