package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_address_opening extends Fragment {
    private Context context;
    private CheckBox alamatPernyataan;
    private byte[] KTP, NPWP, TTD;
    private SessionManager session;
    private boolean cek = false;
    private JSONObject objectCIF;
    private Button btnProses, btnKembali;
    private List<String> listProv = new ArrayList<>();
    private List<String> idProv = new ArrayList<>();
    private List<String> listKab = new ArrayList<>();
    private List<String> idKab = new ArrayList<>();
    private List<String> listKec = new ArrayList<>();
    private List<String> idKec = new ArrayList<>();
    private List<String> listKel = new ArrayList<>();
    private List<String> idKel = new ArrayList<>();
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm, isianForm;
    private EditText alamatBerbeda,RtBerbeda,RwBerbeda,Kodepos;
    private AutoCompleteTextView ProvinsiBerbeda,KabupatenKotaBerbeda,KecamatanBerbeda,KelurahanBerbeda;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_addres_opening, container, false);
        btnProses = view.findViewById(R.id.btnProsesAddress);
        btnKembali = view.findViewById(R.id.btnKembali);
        alamatPernyataan = view.findViewById(R.id.alamat_berbeda);
        alamatBerbeda = view.findViewById(R.id.et_alamatBerbeda);
        RtBerbeda = view.findViewById(R.id.et_rtBerbeda);
        RwBerbeda = view.findViewById(R.id.et_rwBerbeda);
        isianForm = view.findViewById(R.id.isianForm);
        ProvinsiBerbeda = view.findViewById(R.id.et_ProvinsiBerbeda);
        KabupatenKotaBerbeda = view.findViewById(R.id.et_KabupatenKotaBerbeda);
        KecamatanBerbeda = view.findViewById(R.id.et_KecamatanBerbeda);
        KelurahanBerbeda = view.findViewById(R.id.et_kelurahanDesaBerbeda);
        Kodepos = view.findViewById(R.id.et_kodeposBerbeda);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setActionBar();
        String dataJsonS = session.getCIF();
        try {
            objectCIF = new JSONObject(dataJsonS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getProv();
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        alamatPernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alamatPernyataan.isChecked()){
                    cek = true;
                    isianForm.setVisibility(View.VISIBLE);
                    dataCIFJson(true,false);
                    Mirroring();
                }
                else {
                    cek = false;
                    dataCIFJson(false,false);
                    Mirroring();
                    isianForm.setVisibility(View.GONE);
                }
            }
        });
        textWatcher();
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCIFJson(cek,true);
                Mirroring();
                Fragment fragment = new frag_data_pekerjaan();
                Bundle bundle = new Bundle();
                bundle.putByteArray("ktp",KTP);
                bundle.putByteArray("npwp",NPWP);
                bundle.putByteArray("ttd",TTD);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new frag_form_opening();
                Bundle bundle = new Bundle();
                bundle.putByteArray("ktp",KTP);
                bundle.putByteArray("npwp",NPWP);
                bundle.putByteArray("ttd",TTD);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
    }

    private void textWatcher(){
        alamatBerbeda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataCIFJson(cek,false);
                Mirroring();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        RtBerbeda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataCIFJson(cek,false);
                Mirroring();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        RwBerbeda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataCIFJson(cek,false);
                Mirroring();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ProvinsiBerbeda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String prov_ = (String) adapterView.getItemAtPosition(i);
                idKab = new ArrayList<>();
                listKab = new ArrayList<>();
                KabupatenKotaBerbeda.setText("");
                KecamatanBerbeda.setText("");
                KelurahanBerbeda.setText("");
                getKab(idProv.get(i));
                dataCIFJson(cek,false);
                Mirroring();
            }
        });
        KabupatenKotaBerbeda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kab_ = (String) adapterView.getItemAtPosition(i);
                KecamatanBerbeda.setText("");
                KelurahanBerbeda.setText("");
                idKec = new ArrayList<>();
                listKec = new ArrayList<>();
                getKec(idKab.get(i));
                dataCIFJson(cek,false);
                Mirroring();
            }
        });
        KecamatanBerbeda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kec_ = (String) adapterView.getItemAtPosition(i);
                idKel = new ArrayList<>();
                listKel = new ArrayList<>();
                KelurahanBerbeda.setText("");
                getKel(idKec.get(i));
                dataCIFJson(cek,false);
                Mirroring();
            }
        });
        KelurahanBerbeda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kel_ = (String) adapterView.getItemAtPosition(i);
                dataCIFJson(cek,false);
                Mirroring();
            }
        });
        Kodepos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataCIFJson(cek,false);
                Mirroring();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void setActionBar(){
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
    }

    private void dataCIFJson(boolean boolForm, boolean bool2Address) {
        String alamat2 = alamatBerbeda.getText().toString();
        String rt = RtBerbeda.getText().toString();
        String rw = RwBerbeda.getText().toString();
        String provinsi = ProvinsiBerbeda.getText().toString();
        String kabkota = KabupatenKotaBerbeda.getText().toString();
        String kecamatan = KecamatanBerbeda.getText().toString();
        String kelurahan = KelurahanBerbeda.getText().toString();
        String kodepos = Kodepos.getText().toString();

        try {
            JSONArray dataArrCIF = objectCIF.getJSONArray("data");
            dataArrCIF.put(29,boolForm);
            dataArrCIF.put(30,alamat2);
            dataArrCIF.put(31,rt);
            dataArrCIF.put(32,rw);
            dataArrCIF.put(33,provinsi);
            dataArrCIF.put(34,kabkota);
            dataArrCIF.put(35,kecamatan);
            dataArrCIF.put(36,kelurahan);
            dataArrCIF.put(37,kodepos);
            dataArrCIF.put(38,bool2Address);

            objectCIF.put("data",dataArrCIF);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void Mirroring(){

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objectCIF.toString());
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
    private void getProv(){
        ApiService API = Server.getAPIService();
        Call<JsonArray> call = API.getProv();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    String data = response.body().toString();
                    try {
                        JSONArray jArray = new JSONArray(data);
                        for (int i=0; i<jArray.length(); i++) {
                           JSONObject obj_prov = jArray.getJSONObject(i);
                            String idProv_ = obj_prov.getString("idProv");
                           String prov = obj_prov.getString("name");
                           idProv.add(idProv_);
                           listProv.add(prov);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> daftarProv = new ArrayAdapter<String>(context,R.layout.list_item, listProv);
                    ProvinsiBerbeda.setAdapter(daftarProv);

                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getKab(String id){
        ApiService API = Server.getAPIService();
        Call<JsonArray> call = API.ardGetKab(id);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    String data = response.body().toString();
                    try {
                        JSONArray jArray = new JSONArray(data);
                        for (int i=0; i<jArray.length(); i++) {
                            JSONObject obj_prov = jArray.getJSONObject(i);
                            String idKab_ = obj_prov.getString("idKab");
                            String prov = obj_prov.getString("name");
                            idKab.add(idKab_);
                            listKab.add(prov);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> daftarKab = new ArrayAdapter<String>(context,R.layout.list_item, listKab);
                    KabupatenKotaBerbeda.setAdapter(daftarKab);

                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getKec(String id){
        ApiService API = Server.getAPIService();
        Call<JsonArray> call = API.ardGetKec(id);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    String data = response.body().toString();
                    try {
                        JSONArray jArray = new JSONArray(data);
                        for (int i=0; i<jArray.length(); i++) {
                            JSONObject obj_prov = jArray.getJSONObject(i);
                            String idKec_ = obj_prov.getString("idKec");
                            String prov = obj_prov.getString("name");
                            idKec.add(idKec_);
                            listKec.add(prov);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> daftarKec = new ArrayAdapter<String>(context,R.layout.list_item, listKec);
                    KecamatanBerbeda.setAdapter(daftarKec);

                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getKel(String id){
        ApiService API = Server.getAPIService();
        Call<JsonArray> call = API.ardGetKel(id);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    String data = response.body().toString();
                    try {
                        JSONArray jArray = new JSONArray(data);
                        for (int i=0; i<jArray.length(); i++) {
                            JSONObject obj_prov = jArray.getJSONObject(i);
                            String idKel_ = obj_prov.getString("idKel");
                            String prov = obj_prov.getString("name");
                            idKel.add(idKel_);
                            listKel.add(prov);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> daftarKel = new ArrayAdapter<String>(context,R.layout.list_item, listKel);
                    KelurahanBerbeda.setAdapter(daftarKel);

                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
