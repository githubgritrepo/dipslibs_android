package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_data_pekerjaan extends Fragment {

    private Context context;
    private byte[] KTP, NPWP, TTD;
    private Button btnProses, btnKembali;
    private List<String> listProv = new ArrayList<>();
    private List<String> idProv = new ArrayList<>();
    private List<String> listKab = new ArrayList<>();
    private List<String> idKab = new ArrayList<>();
    private List<String> listKec = new ArrayList<>();
    private List<String> idKec = new ArrayList<>();
    private List<String> listKel = new ArrayList<>();
    private List<String> idKel = new ArrayList<>();
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private String[] jumlahKaryawan_, pekerjaan_, bidangUsaha_, jabatan_, tahun, bulan;
    private EditText namaPerusahaan, alamatPerusahaan, rt,rw, kodepos, noTelp, noFax, bagian_dept;
    private AutoCompleteTextView jumlahKaryawan, pekerjaan, bidangUsaha, jabatan, Provinsi,KabupatenKota,Kecamatan,Kelurahan, lamakerja_tahun, lamakerja_bulan, totalkerja_tahun, totalkerja_bulan;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_data_pekerjaan, container, false);
        btnProses = view.findViewById(R.id.btnProses_Pekerjaan);
        btnKembali = view.findViewById(R.id.btnKembali_Pekerjaan);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);

        //Inisiasi Field
        namaPerusahaan = view.findViewById(R.id.namaPerusahaan);
        alamatPerusahaan = view.findViewById(R.id.alamatPerusahaan);
        rt = view.findViewById(R.id.rt_alamat_perusahaan);
        rw = view.findViewById(R.id.rw_alamat_perusahaan);
        Provinsi = view.findViewById(R.id.et_ProvinsiPekerjaan);
        KabupatenKota = view.findViewById(R.id.et_KabupatenKotaPekerjaan);
        Kecamatan = view.findViewById(R.id.et_Kecamatan_pekerjaan);
        Kelurahan = view.findViewById(R.id.et_kelurahanDesa_pekerjaan);
        kodepos = view.findViewById(R.id.et_kodepos_pekerjaan);
        jumlahKaryawan = view.findViewById(R.id.act_JumlahKaryawan);
        noTelp = view.findViewById(R.id.et_no_telephone_kantor);
        noFax = view.findViewById(R.id.et_noFax_kantor);
        lamakerja_tahun = view.findViewById(R.id.act_lama_bekerja_tahun);
        lamakerja_bulan = view.findViewById(R.id.act_lama_bekerja_bulan);
        totalkerja_tahun = view.findViewById(R.id.act_total_bekerja_tahun);
        totalkerja_bulan = view.findViewById(R.id.act_total_bekerja_bulan);
        pekerjaan = view.findViewById(R.id.act_pekerjaan);
        bidangUsaha = view.findViewById(R.id.act_bidangUsaha);
        jabatan = view.findViewById(R.id.act_jabatan);
        bagian_dept = view.findViewById(R.id.bagian_dept);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar();
        setDropdown();
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new frag_data_keuangan();
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
                Fragment fragment = new frag_address_opening();
                Bundle bundle = new Bundle();
                bundle.putByteArray("ktp",KTP);
                bundle.putByteArray("npwp",NPWP);
                bundle.putByteArray("ttd",TTD);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
    }
    private void setTextWatcher(){
        Provinsi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String prov_ = (String) adapterView.getItemAtPosition(i);
                idKab = new ArrayList<>();
                listKab = new ArrayList<>();
                KabupatenKota.setText("");
                Kecamatan.setText("");
                Kelurahan.setText("");
                getKab(idProv.get(i));
            }
        });
        KabupatenKota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kab_ = (String) adapterView.getItemAtPosition(i);
                Kecamatan.setText("");
                Kelurahan.setText("");
                idKec = new ArrayList<>();
                listKec = new ArrayList<>();
                getKec(idKab.get(i));
            }
        });
        Kecamatan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kec_ = (String) adapterView.getItemAtPosition(i);
                idKel = new ArrayList<>();
                listKel = new ArrayList<>();
                Kelurahan.setText("");
                getKel(idKec.get(i));
            }
        });
        Kelurahan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kel_ = (String) adapterView.getItemAtPosition(i);
            }
        });
    }
    private void setDropdown(){
        tahun = new String[]{"<5 Tahun","5 - 10 Tahun","> 10 Tahun"};
        bulan = new String[]{"1 Bulan","2 Bulan", "3 Bulan", "4 Bulan", "5 Bulan", "6 Bulan", "7 Bulan","8 Bulan", "9 Bulan", "10 Bulan", "11 Bulan", "12 Bulan" };
        jumlahKaryawan_ = new String[]{"< 25 Orang", "25 - 50 Orang", "> 50 Orang"};
        pekerjaan_ = new String[]{"Pegawai Negeri","TNI/POLRI","Wiraswasta","Pegawai Swasta", "Profesional", "Pensiunan", "Ibu Rumah Tangga", "Pelajar/Mahasiswa", "Dosen/Guru"};
        bidangUsaha_ = new String[]{"Kontruksi", "Real Estate/Property", "Kesehatan", "Perkebunan", "Keuangan/Bank/Non Bank", "Transportasi", "Industri", "Pertambangan", "Perdagangan"};
        jabatan_ = new String[]{"Komisaris", "Direktur", "Manajer", "Kepala Divisi", "Kepala Bagian", "Kepala Seksi", "Staff"};

        ArrayAdapter<String> th = new ArrayAdapter<String>(context,R.layout.list_item, tahun);
        lamakerja_tahun.setAdapter(th);
        totalkerja_tahun.setAdapter(th);

        ArrayAdapter<String> bln = new ArrayAdapter<String>(context,R.layout.list_item, bulan);
        lamakerja_bulan.setAdapter(bln);
        lamakerja_bulan.setAdapter(bln);

        ArrayAdapter<String> jk = new ArrayAdapter<String>(context,R.layout.list_item, jumlahKaryawan_);
        jumlahKaryawan.setAdapter(jk);

        ArrayAdapter<String> pkj = new ArrayAdapter<String>(context,R.layout.list_item, pekerjaan_);
        pekerjaan.setAdapter(pkj);

        ArrayAdapter<String> bdgush = new ArrayAdapter<String>(context,R.layout.list_item, bidangUsaha_);
        bidangUsaha.setAdapter(bdgush);

        ArrayAdapter<String> jbtn = new ArrayAdapter<String>(context,R.layout.list_item, jabatan_);
        jabatan.setAdapter(jbtn);
        getProv();
        setTextWatcher();
    }
    private void setActionBar(){
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
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
                    Provinsi.setAdapter(daftarProv);

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
                    KabupatenKota.setAdapter(daftarKab);

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
                    Kecamatan.setAdapter(daftarKec);

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
                    Kelurahan.setAdapter(daftarKel);

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
