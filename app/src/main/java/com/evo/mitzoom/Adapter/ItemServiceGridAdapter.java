package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Fragments.frag_open_account_product;
import com.evo.mitzoom.Fragments.frag_service_item_new;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class ItemServiceGridAdapter extends RecyclerView.Adapter<ItemServiceGridAdapter.ItemHolder>{

    private final RabbitMirroring rabbitMirroring;
    private final ArrayList<ItemModel> dataList;
    private final Context mContext;
    private boolean isSessionZoom = false;
    private SessionManager sessions;
    private String idDips;
    private String NIK = "";
    private Bundle bundle;
    private Fragment fragment;
    private JSONObject dataNasabahObj = null;

    public ItemServiceGridAdapter(ArrayList<ItemModel> dataList, Context mContext, RabbitMirroring rabbitMirroring) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.rabbitMirroring = rabbitMirroring;
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        Log.e("CEK","ItemServiceGridAdapter isSessionZoom : "+isSessionZoom);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid, parent, false);
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        String dataNasabah = sessions.getNasabah();
        Log.e("CEK",mContext+" dataNasabah : "+dataNasabah);
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                if (dataNasabahObj.has("nik")) {
                    NIK = dataNasabahObj.getString("nik");
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
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
                    RabbitMirroring.MirroringSendEndpoint(201);
                    fragment = new frag_open_account_product();
                    getFragmentPage(fragment);
                    break;
                case "2" :/* SUDAH MIRRORING */
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    processInqCIFbyNIK();
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

            ads = itemView.findViewById(R.id.ads);
            tvLabelItem = itemView.findViewById(R.id.tvLabelItem);
        }
    }

    private void processInqCIFbyNIK() {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("noKtp",NIK);
            dataObj.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","REQUEST processInqCIFbyNIK : "+dataObj.toString());

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObj.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.InquieryCIFbyNIK(requestBody,authAccess,exchangeToken);
        Log.e("CEK","REQUEST processInqCIFbyNIK URL : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","RESPONSE CODE processInqCIFbyNIK : "+response.code());
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","RESPONSE processInqCIFbyNIK : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String tempatLahir = dataObj.getJSONObject("data").getString("tempatLahir");
                        String tanggalLahir = dataObj.getJSONObject("data").getString("tanggalLahir");
                        String namaIbu = dataObj.getJSONObject("data").getString("namaIbu");

                        String tahun = tanggalLahir.substring(0, 4);
                        String bln = tanggalLahir.substring(4,6);
                        String tgl = tanggalLahir.substring(6,8);
                        String tgllahir = tgl+"-"+bln+"-"+tahun;

                        dataNasabahObj.put("tempatLahir",tempatLahir);
                        dataNasabahObj.put("tanggalLahir",tgllahir);
                        dataNasabahObj.put("namaIbu",namaIbu);
                        sessions.saveNasabah(dataNasabahObj.toString());

                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int intLayout = 359;
                                RabbitMirroring.MirroringSendEndpoint(intLayout);
                                fragment = new frag_service_item_new();
                                bundle = new Bundle();
                                bundle.putInt("form_id",43);
                                sessions.saveFormCOde(intLayout);
                                fragment.setArguments(bundle);
                                getFragmentPage(fragment);
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }
}
