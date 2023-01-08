package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
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

public class frag_data_pekerjaan extends Fragment {

    private Context context;
    private SessionManager session;
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
    private String idDips;
    private JSONObject objectCIF = null;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private String[] jumlahKaryawan_, pekerjaan_, bidangUsaha_, jabatan_, tahun, bulan;
    private EditText namaPerusahaan, alamatPerusahaan, rt,rw, kodepos, noTelp, noFax, bagian_dept;
    private AutoCompleteTextView jumlahKaryawan, pekerjaan, bidangUsaha, jabatan, Provinsi,KabupatenKota,Kecamatan,Kelurahan, lamakerja_tahun, lamakerja_bulan, totalkerja_tahun, totalkerja_bulan;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
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

        String dataJsonS = session.getCIF();
        if (dataJsonS != null) {
            try {
                objectCIF = new JSONObject(dataJsonS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        idDips = session.getKEY_IdDips();
        setActionBar();
        setDropdown();
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),true);
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
        namaPerusahaan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(charSequence,alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        alamatPerusahaan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),charSequence,rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),charSequence,rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),charSequence,Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                Mirroring(namaPerusahaan.getText().toString(), alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),"","", "",prov_, kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
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
                Mirroring(namaPerusahaan.getText().toString(), alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),"","", kab_, Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
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
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),"",kec_,KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        Kelurahan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kel_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),kel_,Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        kodepos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), charSequence,jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        jumlahKaryawan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jmlKrywn_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jmlKrywn_,noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        noTelp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),charSequence,noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noFax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),charSequence,lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lamakerja_tahun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String lama_kerja_tahun_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lama_kerja_tahun_,lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        lamakerja_bulan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String lama_kerja_bulan_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(), lama_kerja_bulan_,totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        totalkerja_tahun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String total_kerja_tahun_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),total_kerja_tahun_,totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        totalkerja_bulan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String total_kerja_bulan_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),total_kerja_bulan_,pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        pekerjaan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pkrjn_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pkrjn_,bidangUsaha.getText().toString(),jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        bidangUsaha.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String bdgush_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bdgush_,jabatan.getText(),bagian_dept.getText().toString(),false);
            }
        });
        jabatan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jbtn_ = (String) adapterView.getItemAtPosition(i);
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jbtn_,bagian_dept.getText().toString(),false);
            }
        });
        bagian_dept.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(namaPerusahaan.getText().toString(),alamatPerusahaan.getText().toString(),rt.getText().toString(),rw.getText().toString(),Kelurahan.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(), kodepos.getText().toString(),jumlahKaryawan.getText().toString(),noTelp.getText().toString(),noFax.getText().toString(),lamakerja_tahun.getText().toString(),lamakerja_bulan.getText().toString(),totalkerja_tahun.getText().toString(),totalkerja_bulan.getText().toString(),pekerjaan.getText().toString(),bidangUsaha.getText().toString(),jabatan.getText(),charSequence,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        totalkerja_bulan.setAdapter(bln);

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
    private void Mirroring(CharSequence nama_per,CharSequence alamat,CharSequence rt,CharSequence rw,CharSequence kelurahan,CharSequence kecamatan,CharSequence kabupaten, CharSequence provinsi,CharSequence kodepos, CharSequence jml_karyawan, CharSequence no_telp,CharSequence no_fax,CharSequence lama_kerja_tahun, CharSequence lama_kerja_bulan, CharSequence total_kerja_tahun, CharSequence total_kerja_bulan,CharSequence pkrjn, CharSequence bdg_usaha, CharSequence jbtn, CharSequence bagian,boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama_per);
            jsonArray.put(alamat);
            jsonArray.put(rt);
            jsonArray.put(rw);
            jsonArray.put(provinsi);
            jsonArray.put(kabupaten);
            jsonArray.put(kecamatan);
            jsonArray.put(kelurahan);
            jsonArray.put(kodepos);
            jsonArray.put(jml_karyawan);
            jsonArray.put(no_telp);
            jsonArray.put(no_fax);
            jsonArray.put(lama_kerja_tahun);
            jsonArray.put(lama_kerja_bulan);
            jsonArray.put(total_kerja_tahun);
            jsonArray.put(total_kerja_bulan);
            jsonArray.put(pkrjn);
            jsonArray.put(bdg_usaha);
            jsonArray.put(jbtn);
            jsonArray.put(bagian);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",31);
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
                if (bool) {
                    APISaveForm(jsonArray);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }

    private void APISaveForm(JSONArray jsonsWork) {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data",jsonsWork);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsons = new JSONObject();
        try {
            JSONArray dataArrCIF = objectCIF.getJSONArray("data");
            String no_handphone = dataArrCIF.get(25).toString();
            jsons.put("formCode","CIF2");
            jsons.put("idDips",idDips);
            jsons.put("phone",no_handphone);
            jsons.put("payload",dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIService().saveForm(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int errCode = jsObj.getInt("code");
                        if (errCode == 200) {
                            Fragment fragment = new frag_data_keuangan();
                            Bundle bundle = new Bundle();
                            bundle.putByteArray("ktp", KTP);
                            bundle.putByteArray("npwp", NPWP);
                            bundle.putByteArray("ttd", TTD);
                            fragment.setArguments(bundle);
                            getFragmentPage(fragment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context,"Gagal Save Form",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
