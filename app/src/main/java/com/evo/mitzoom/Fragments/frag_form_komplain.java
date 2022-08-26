package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_form_komplain extends Fragment {
    private Context context;
    private ImageView btnBack;
    private EditText perihal, tanggal, detailKomplain;
    private String Tgl, idDips;
    private CheckBox pernyataan;
    private Button btnProses;
    private int state;
    private SessionManager session;
    private Boolean pernyataan_bool = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_komplain, container, false);
        perihal = view.findViewById(R.id.et_hal);
        tanggal = view.findViewById(R.id.et_tgl_komplain);
        detailKomplain = view.findViewById(R.id.et_detail);
        btnBack = view.findViewById(R.id.btn_back_formKomplain);
        pernyataan = view.findViewById(R.id.pernyataan_komplain);
        btnProses = view.findViewById(R.id.btnProses_komplain);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        Bundle arg = getArguments();
        state = arg.getInt("state");
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //STATE 1 UNTUK BACK KE WAITING ROOM
                //STATE 2 UNTUK BACK KE FRAGMENT DENGAN CS
                switch (state){
                    case 1:
                        getFragmentPage1(new frag_berita());
                        return;
                    case 2:Mirroring(perihal.getText().toString(),tanggal.getText().toString(), detailKomplain.getText().toString(),pernyataan_bool, false, true);
                        getFragmentPage2(new frag_service());
                        return;
                }
            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        Tgl = df.format(c.getTime());
        tanggal.setText(Tgl);
        Mirroring(perihal.getText().toString(),tanggal.getText().toString(), detailKomplain.getText().toString(),pernyataan_bool, false, false);
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    pernyataan_bool = true;
                    Mirroring(perihal.getText().toString(),tanggal.getText().toString(), detailKomplain.getText().toString(),pernyataan_bool, false, false);
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    pernyataan_bool = false;
                    Mirroring(perihal.getText().toString(),tanggal.getText().toString(), detailKomplain.getText().toString(),pernyataan_bool, false, false);
                    Log.d("CHECK","FALSE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        textWatcher();
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hal = perihal.getText().toString();
                String detail = detailKomplain.getText().toString();
                if (hal.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else if (detail.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Terima kasih!, Komplain anda sedang kami proses", Toast.LENGTH_SHORT).show();
                    switch (state){
                        case 1:
                            getFragmentPage1(new frag_berita());
                            return;
                        case 2:
                            Mirroring(perihal.getText().toString(),tanggal.getText().toString(), detailKomplain.getText().toString(),pernyataan_bool, true, false);
                            getFragmentPage2(new frag_portfolio());
                            return;
                    }
                }
            }
        });
    }
    private void textWatcher(){
        perihal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(charSequence,tanggal.getText().toString(), detailKomplain.getText().toString(),pernyataan_bool, false, false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        detailKomplain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(perihal.getText().toString(),tanggal.getText().toString(), charSequence,pernyataan_bool, false, false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void Mirroring(CharSequence perihal_, CharSequence tanggal_, CharSequence detailKomplain_, Boolean pernyataan_, Boolean action, Boolean back){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(perihal_);
            jsonArray.put(tanggal_);
            jsonArray.put(detailKomplain_);
            jsonArray.put(pernyataan_);
            jsonArray.put(action);
            jsonArray.put(back);
            jsons.put("idDips",idDips);
            jsons.put("code",351);
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
    private void getFragmentPage1(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void getFragmentPage2(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
