package com.evo.mitzoom.Fragments;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.ItemServiceAdapter;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_service extends Fragment {
    private Context context;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    ArrayList<ItemModel> data;
    ImageView btnBack;
    private SessionManager sessionManager;
    private String idDips;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessionManager = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_services, container, false);
        recyclerView = view.findViewById(R.id.rv_item);
        btnBack = view.findViewById(R.id.btn_back4);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = sessionManager.getKEY_IdDips();
        addData();
        recyclerViewAdapter = new ItemServiceAdapter(context, data);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(14,true);
                getFragmentPage(new frag_portfolio());
            }
        });
    }
    private void addData(){
        data = new ArrayList<>();
        data.add(new ItemModel("0",getResources().getString(R.string.pembukaan_akun),R.drawable.item1)); //Sudah
        data.add(new ItemModel("1",getResources().getString(R.string.Formulir_Transfer),R.drawable.item2)); //Sudah
        data.add(new ItemModel("2",getResources().getString(R.string.CUSTOMER_COMPLAIN),R.drawable.item1)); //Sudah
        data.add(new ItemModel("3",getResources().getString(R.string.INTERBANK_TRANSACTION),R.drawable.item2)); //Sudah
        data.add(new ItemModel("4",getResources().getString(R.string.KYC_UPDATING),R.drawable.item1)); //Sudah
        data.add(new ItemModel("5",getResources().getString(R.string.DORMANT_ACTIVATION),R.drawable.item2)); //Sudah
        data.add(new ItemModel("6",getResources().getString(R.string.FOREX),R.drawable.item1)); //Sudah
        data.add(new ItemModel("7",getResources().getString(R.string.ATM_CARD),R.drawable.item2)); //Sudah
        data.add(new ItemModel("8",getResources().getString(R.string.POWER_OF_ATTORNEY),R.drawable.item1)); //Sudah
        data.add(new ItemModel("9",getResources().getString(R.string.IBMB_REGISTRATION),R.drawable.item2)); //Sudah
        data.add(new ItemModel("10",getResources().getString(R.string.CREDIT_FORM),R.drawable.item1)); //Sudah
        data.add(new ItemModel("11",getResources().getString(R.string.REGISTER_WEALTH),R.drawable.item2)); //Sudah
        data.add(new ItemModel("12",getResources().getString(R.string.WEALTH_RISK_PROFILE),R.drawable.item1));
        data.add(new ItemModel("13",getResources().getString(R.string.FINANCIAL_PLANNING),R.drawable.item2)); //Sudah
        data.add(new ItemModel("14",getResources().getString(R.string.WEALTH),R.drawable.item1)); //Sudah
        data.add(new ItemModel("15",getString(R.string.cardless),R.drawable.item2));
    }
    private void Mirroring(int nextCode, boolean bool){
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
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
