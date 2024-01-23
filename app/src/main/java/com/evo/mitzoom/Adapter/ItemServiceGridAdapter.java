package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Fragments.frag_assurance;
import com.evo.mitzoom.Fragments.frag_blokir_saldo;
import com.evo.mitzoom.Fragments.frag_deposito_online;
import com.evo.mitzoom.Fragments.frag_deposito_online_aro;
import com.evo.mitzoom.Fragments.frag_ibmb;
import com.evo.mitzoom.Fragments.frag_open_account_product;
import com.evo.mitzoom.Fragments.frag_service_antarbank;
import com.evo.mitzoom.Fragments.frag_service_item_new;
import com.evo.mitzoom.Fragments.frag_service_new;
import com.evo.mitzoom.Fragments.frag_update_data;
import com.evo.mitzoom.Fragments.frag_wealth_management;
import com.evo.mitzoom.Fragments.frag_wm_transactions;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsTransactionsCreate;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class ItemServiceGridAdapter extends RecyclerView.Adapter<ItemServiceGridAdapter.ItemHolder>{

    private final ArrayList<ItemModel> dataList;
    private final Context mContext;
    private boolean isSessionZoom = false;
    private SessionManager sessions;
    private String idDips;
    private String NIK = "";
    private Fragment fragment;
    private JSONObject dataNasabahObj = null;

    public ItemServiceGridAdapter(ArrayList<ItemModel> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        ConnectionRabbitHttp.init(mContext);
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid, parent, false);
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        String dataNasabah = sessions.getNasabah();
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
        String labelserv = dataList.get(position).getNamaItem();
        holder.tvLabelItem.setVisibility(View.VISIBLE);
        holder.tvLabelItem.setText(labelserv);
        holder.ads.setImageResource(dataList.get(position).getGambarItem());

        holder.ads.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            switch (dataList.get(position).getId()){
                case "0" :
                    ConnectionRabbitHttp.mirroringEndpoint(201);
                    fragment = new frag_open_account_product();
                    bundle.putInt("idTNC",9);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                    break;
                case "1" :
                    if (isSessionZoom) {

                    } else {
                        Intent intent = new Intent(mContext, DipsTransactionsCreate.class);
                        intent.putExtra("form_id", 44);
                        intent.putExtra("labelserv", labelserv);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finishAffinity();
                    }
                    break;
                case "2" :/* SUDAH MIRRORING */
                    if (isSessionZoom) {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
                        processInqCIFbyNIK();
                    } else {
                        //DipsSwafoto.showProgress(true);
                        Intent intent = new Intent(mContext, DipsTransactionsCreate.class);
                        intent.putExtra("form_id", 47);
                        intent.putExtra("labelserv", labelserv);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finishAffinity();
                    }
                    break;
                case "3" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(199);
                        bundle.putInt("idTrx",Integer.parseInt(dataList.get(position).getId()));
                        bundle.putString("subLayanan",labelserv);
                        fragment = new frag_service_new();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    } else {
                        Intent intent = new Intent(mContext, DipsTransactionsCreate.class);
                        intent.putExtra("form_id", 53);
                        intent.putExtra("labelserv", labelserv);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finishAffinity();
                    }
                    break;
                case "4" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(190);
                        bundle.putInt("idGenerateForm",49);
                        bundle.putString("idService", dataList.get(position).getId());
                        bundle.putString("labelserv", labelserv);
                        fragment = new frag_service_antarbank();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    } else {
                        Intent intent = new Intent(mContext, DipsTransactionsCreate.class);
                        intent.putExtra("form_id", 57);
                        intent.putExtra("labelserv", labelserv);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finishAffinity();
                    }
                    break;
                case "5" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(250);
                        //bundle.putInt("idGenerateForm",59);
                        bundle.putInt("idGenerateForm",77);
                        fragment = new frag_update_data();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "10" :
                    if (isSessionZoom) {
                        //ConnectionRabbitHttp.mirroringEndpoint(250);
                        //bundle.putInt("idGenerateForm",59);
                        ConnectionRabbitHttp.mirroringEndpoint(240);
                        bundle.putInt("idGenerateForm", 69);
                        fragment = new frag_ibmb();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "16" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(200);
                        bundle.putInt("idGenerateForm",56);
                        bundle.putString("idService", dataList.get(position).getId());
                        bundle.putString("labelserv", labelserv);
                        fragment = new frag_service_antarbank();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "18" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(280);
                        bundle.putInt("idTrx",Integer.parseInt(dataList.get(position).getId()));
                        bundle.putString("subLayanan",labelserv);
                        fragment = new frag_service_new();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    }
                    break;
                case "19" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(197);
                        bundle.putInt("idTrx",Integer.parseInt(dataList.get(position).getId()));
                        bundle.putString("subLayanan",labelserv);
                        fragment = new frag_service_new();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    }
                    break;
                case "20" :
                    if (isSessionZoom) {
                        bundle.putInt("idTrx",Integer.parseInt(dataList.get(position).getId()));
                        fragment = new frag_assurance();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "33" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(191);
                        bundle.putInt("idGenerateForm",48);
                        bundle.putString("idService", dataList.get(position).getId());
                        bundle.putString("labelserv", labelserv);
                        fragment = new frag_service_antarbank();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "181" :
                    if (isSessionZoom) {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
                        processStatusProfile(labelserv);
                    }
                    break;
                case "191" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(196);
                        bundle.putInt("idGenerateForm",52);
                        bundle.putString("idService", dataList.get(position).getId());
                        bundle.putString("labelserv", labelserv);
                        fragment = new frag_deposito_online();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                        getFragmentPage(fragment);
                    }
                    break;
                case "192" :
                case "193" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(210);
                        fragment = new frag_deposito_online_aro();
                        bundle.putString("idService", dataList.get(position).getId());
                        bundle.putString("labelserv", labelserv);
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "194" :
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(275);
                        fragment = new frag_deposito_online_aro();
                        bundle.putString("idService", dataList.get(position).getId());
                        bundle.putString("labelserv", labelserv);
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
                case "100":
                    //Maintenance and Administration
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(1000);
                        bundle.putInt("idTrx",Integer.parseInt(dataList.get(position).getId()));
                        bundle.putString("subLayanan",labelserv);
                        fragment = new frag_service_new();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    }
                    break;
                case "101":
                    //Blokir Saldo
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringEndpoint(1002);
                        bundle.putInt("idGenerateForm",78);
                        fragment = new frag_blokir_saldo();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                    break;
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame2, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void getFragmentPageBackStack(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack("FragService")
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("FragService")
                    .commit();
        }

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

    private void processStatusProfile(String labelserv) {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("nik",NIK);
            dataObj.put("noCif",sessions.getNoCIF());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObj.toString());
            Server.getAPIService().StatusSID(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            String status = dataBody.getJSONObject("data").getString("status");
                            if (status.equalsIgnoreCase("active")) {
                                Bundle bundle = new Bundle();
                                bundle.putString("labelserv", labelserv);
                                fragment = new frag_wm_transactions();
                                fragment.setArguments(bundle);
                                getFragmentPageBackStack(fragment);
                            } else {
                                ConnectionRabbitHttp.mirroringEndpoint(281);
                                Bundle bundle = new Bundle();
                                bundle.putInt("idGenerateForm",66);
                                bundle.putString("labelserv", labelserv);
                                bundle.putString("statusSID", "onprocesss");
                                fragment = new frag_wealth_management();
                                fragment.setArguments(bundle);
                                getFragmentPageBackStack(fragment);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (response.code() == 404) {
                            ConnectionRabbitHttp.mirroringEndpoint(281);
                            Bundle bundle = new Bundle();
                            bundle.putInt("idGenerateForm",66);
                            bundle.putString("labelserv", labelserv);
                            fragment = new frag_wealth_management();
                            fragment.setArguments(bundle);
                            getFragmentPageBackStack(fragment);
                        } else {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                            String msg = "";
                            if (response.body() != null) {
                                String dataS = response.body().toString();
                                try {
                                    JSONObject dataObj = new JSONObject(dataS);
                                    msg = dataObj.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (response.errorBody().toString().isEmpty()) {
                                    String dataS = response.errorBody().toString();
                                    try {
                                        JSONObject dataObj = new JSONObject(dataS);
                                        msg = dataObj.getString("message");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String dataS = null;
                                    try {
                                        dataS = response.errorBody().string();
                                        JSONObject dataObj = new JSONObject(dataS);
                                        msg = dataObj.getString("message");
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
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

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObj.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.InquieryCIFbyNIK(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
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
                                //RabbitMirroring.MirroringSendEndpoint(intLayout);
                                ConnectionRabbitHttp.mirroringEndpoint(intLayout);
                                fragment = new frag_service_item_new();
                                Bundle bundle = new Bundle();
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
