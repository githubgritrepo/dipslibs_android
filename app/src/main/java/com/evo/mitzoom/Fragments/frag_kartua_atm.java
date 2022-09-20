package com.evo.mitzoom.Fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chaos.view.PinView;
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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_kartua_atm extends Fragment {
    private ImageView btnBack;
    private Context context;
    private AutoCompleteTextView etJenisLayanan;
    private EditText etNama,etNohp,etEmail,etAlamat,etTgl;
    private Button btnNext;
    private String Tgl, idDips;
    String[] sourceService;
    private SessionManager session;
    private JSONObject dataNasabah = null;
    private String no_handphone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECEIVE_SMS},
                1001);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_kartu_atm, container, false);
        btnBack = view.findViewById(R.id.btn_back_atm);
        etNama = view.findViewById(R.id.et_nama_atm);
        etNohp = view.findViewById(R.id.et_no_hp_atm);
        etEmail = view.findViewById(R.id.et_email_atm);
        etAlamat = view.findViewById(R.id.et_alamat_atm);
        etTgl = view.findViewById(R.id.et_tgl_transaksi_atm);
        btnNext = view.findViewById(R.id.btnNext);
        etJenisLayanan = view.findViewById(R.id.et_jenisLayananATM);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();

        String dataJsonS = session.getNasabah();
        if (dataJsonS != null) {
            try {
                dataNasabah = new JSONObject(dataJsonS);
                String nama = dataNasabah.getString("nama");
                no_handphone = dataNasabah.getString("noHP");

                etNama.setText(nama);
                etNohp.setText(no_handphone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sourceService = new String[]{"Pengajuan Kartu ATM", "Penggantian Kartu ATM", "Blokir Kartu ATM"};
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        Tgl = df.format(c.getTime());
        etTgl.setText(Tgl);
        Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),false,false);
        //btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));

        ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(context,R.layout.list_item, sourceService);
        etJenisLayanan.setAdapter(adapterPopulation);
        textWacther();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),false,true);
                getFragmentPage(new frag_service());
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),true,false);
                Fragment fragment = new frag_kartu_atm_detail();

                String dataATM = DataATM();
                Bundle bundle = new Bundle();
                bundle.putString("DATA_ATM",dataATM);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
    }

    private String DataATM() {
        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("nama",etNama.getText().toString().trim());
            jsObj.put("noHP",etNohp.getText().toString().trim());
            jsObj.put("email",etEmail.getText().toString().trim());
            jsObj.put("alamat",etAlamat.getText().toString().trim());
            jsObj.put("tgl",etTgl.getText().toString().trim());
            jsObj.put("jenis_layanan",etJenisLayanan.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj.toString();
    }
    private void textWacther(){
        etNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(charSequence, etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etNohp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                no_handphone = charSequence.toString();
                Mirroring(etNama.getText().toString(), charSequence,etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),charSequence,etAlamat.getText().toString(),Tgl,etJenisLayanan.getText().toString(),false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etAlamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),charSequence,Tgl,etJenisLayanan.getText().toString(),false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etJenisLayanan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jenisLayanan = (String) adapterView.getItemAtPosition(i);
                Mirroring(etNama.getText().toString(), etNohp.getText().toString(),etEmail.getText().toString(),etAlamat.getText().toString(),Tgl,jenisLayanan,false,false);
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

    private void Mirroring(CharSequence nama, CharSequence nohp, CharSequence email, CharSequence alamat, CharSequence tanggal, CharSequence jenisLayanan, boolean submit, boolean back){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama);
            jsonArray.put(nohp);
            jsonArray.put(email);
            jsonArray.put(alamat);
            jsonArray.put(tanggal);
            jsonArray.put(jenisLayanan);
            jsonArray.put(submit);
            jsonArray.put(back);
            jsons.put("idDips",idDips);
            jsons.put("code",411);
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
