package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_form_opening extends Fragment {
    private Context context;
    private ImageView preview_ktp, preview_npwp, preview_signature;
    private EditText KelurahanDesa,Kecamatan,KabupatenKota,Provinsi,KodePos,Kewarganegaraan,Negara,namaSuami_Istri_OrangTua,JenisKelamin,jenisBuktiIdentitas,TanggalTerbitIdentitas, TanggalBerakhirIdentitas, NamaIbuKandung, NomorTelephone, NomorNPWP, Rt,Rw,Gelar,Nama,NIK,Email,Alamat,Agama,Status,NoHp;
    private AutoCompleteTextView JenisIdentitasLain, JumlahTanggungan, Pendidikan, statusRumah;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private Button btnProcess;
    private String idDips, objectCIF, gelar, nama, alamat, rt, rw , kelurahan_desa, kecamatan, kabupaten_kota, provinsi, kodepos, kewarganegaraan, negara, jenis_identitas_lain, jumlah_tanggungan, nama_suami_istri_ortu, jenis_kelamin, agama, status, pendidikan, nik, jenis_bukti_identitas, tanggal_terbit_identitas, tanggal_berakhir_identitas, nama_ibu_kandung, email, no_hp, no_telp, no_npwp, status_rumah;
    private SessionManager session;
    private byte[] KTP, NPWP, TTD;
    private String[] jenisIdentitasLain, jumlahTanggungan, pendidikanTerakhir, statusRumah_;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_opening_account, container, false);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);

        //Inisiasi Field Form
        Gelar = view.findViewById(R.id.et_gelar);
        Nama = view.findViewById(R.id.et_nama);
        Alamat = view.findViewById(R.id.et_alamat);
        Rt = view.findViewById(R.id.et_rt);
        Rw = view.findViewById(R.id.et_rw);
        KelurahanDesa = view.findViewById(R.id.et_kelurahanDesa);
        Kecamatan = view.findViewById(R.id.et_Kecamatan);
        KabupatenKota = view.findViewById(R.id.et_KabupatenKota);
        Provinsi = view.findViewById(R.id.et_Provinsi);
        KodePos = view.findViewById(R.id.et_kodepos);
        Kewarganegaraan = view.findViewById(R.id.et_kewarganegaraan);
        Negara = view.findViewById(R.id.et_Negara);
        JenisIdentitasLain = view.findViewById(R.id.et_identityType);
        JumlahTanggungan = view.findViewById(R.id.et_jumlahTanggungan);
        namaSuami_Istri_OrangTua = view.findViewById(R.id.et_namaSuami_Istri_Orangtua);
        JenisKelamin = view.findViewById(R.id.et_jenisKelamin);
        Agama = view.findViewById(R.id.et_agama);
        Status = view.findViewById(R.id.et_status);
        Pendidikan = view.findViewById(R.id.et_pendidikan);
        NIK = view.findViewById(R.id.et_nik);
        jenisBuktiIdentitas = view.findViewById(R.id.et_jenisBuktiIdentitas);
        TanggalTerbitIdentitas = view.findViewById(R.id.et_tglTerbitIdentitas);
        TanggalBerakhirIdentitas = view.findViewById(R.id.et_tglBerakhirIdentitas);
        NamaIbuKandung = view.findViewById(R.id.et_namaIbuKandung);
        Email = view.findViewById(R.id.et_email);
        NoHp = view.findViewById(R.id.et_no_hp);
        NomorTelephone = view.findViewById(R.id.et_no_telephone);
        NomorNPWP = view.findViewById(R.id.et_npwp);
        statusRumah = view.findViewById(R.id.et_statusRumah);

        btnProcess = view.findViewById(R.id.btnProses);
        preview_ktp = view.findViewById(R.id.Imageview_ktp);
        preview_npwp = view.findViewById(R.id.Imageview_npwp);
        preview_signature = view.findViewById(R.id.Imageview_tanda_tangan);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        idDips = session.getKEY_IdDips();
        //Get Data Image
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        ByteArraytoimg(KTP,preview_ktp);
        ByteArraytoimg(NPWP,preview_npwp);
        ByteArraytoimg(TTD,preview_signature);
        setActionBar();
        setOCR();
        //getSessionData();
        setDropdown();
        Email.setFilters(new InputFilter[]{new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9@._-]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }});
        textWatcher();
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validasi
                if (KodePos.getText().toString().isEmpty()){
                    KodePos.setError(getResources().getString(R.string.error_field));
                }
                else if (JenisIdentitasLain.getText().toString().isEmpty()){
                    JenisIdentitasLain.setError(getResources().getString(R.string.error_field));
                }
                else if (JumlahTanggungan.getText().toString().isEmpty()){
                    JumlahTanggungan.setError(getResources().getString(R.string.error_field));
                }
                else if (namaSuami_Istri_OrangTua.getText().toString().isEmpty()){
                    namaSuami_Istri_OrangTua.setError(getResources().getString(R.string.error_field));
                }
                else if (Pendidikan.getText().toString().isEmpty()){
                    Pendidikan.setError(getResources().getString(R.string.error_field));
                }
                else if (NamaIbuKandung.getText().toString().isEmpty()){
                    NamaIbuKandung.setError(getResources().getString(R.string.error_field));
                }
                else if (Email.getText().toString().isEmpty()){
                    Email.setError(getResources().getString(R.string.error_field));
                }
                else if (NoHp.getText().toString().isEmpty()){
                    NoHp.setError(getResources().getString(R.string.error_field));
                }
                else if (NomorTelephone.getText().toString().isEmpty()){
                    NomorTelephone.setError(getResources().getString(R.string.error_field));
                }
                else if (statusRumah.getText().toString().isEmpty()){
                    statusRumah.setError(getResources().getString(R.string.error_field));
                }
                else {
                    convertToJson();
                    /*JSONObject obj = new JSONObject();
                    try {
                        obj.put("nama",Nama2);
                        obj.put("nik",NIK2);
                        obj.put("email",Email2);
                        obj.put("nohp",NoHp2);
                        obj.put("alamat",Alamat2);
                        obj.put("agama",Agama2);
                        obj.put("status",Status2);
                        obj.put("produk",Produk);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String dataS = obj.toString();
                    session.saveCIF(dataS);*/
                    Fragment fragment = new frag_address_opening();
                    Bundle bundle = new Bundle();
                    bundle.putString("objectCIF",objectCIF);
                    bundle.putByteArray("ktp",KTP);
                    bundle.putByteArray("npwp",NPWP);
                    bundle.putByteArray("ttd",TTD);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                }
            }
        });
    }
    private void getSessionData(){
        if (!session.getCIF().isEmpty()){
            objectCIF = session.getCIF();
            try {
                JSONObject object = new JSONObject(objectCIF);
                kodepos = object.getString("kodepos");
                jenis_identitas_lain = object.getString("jenis_identitas_lain");
                jumlah_tanggungan = object.getString("jumlah_tanggungan");
                nama_suami_istri_ortu = object.getString("nama_suami_istri_ortu");
                pendidikan = object.getString("pendidikan");
                nama_ibu_kandung = object.getString("nama_ibu_kandung");
                email = object.getString("email");
                no_hp = object.getString("no_handphone");
                no_telp = object.getString("no_telepon");
                status_rumah = object.getString("status_rumah");

                KodePos.setText(kodepos);
                JenisIdentitasLain.setText(jenis_identitas_lain);
                JumlahTanggungan.setText(jumlah_tanggungan);
                namaSuami_Istri_OrangTua.setText(nama_suami_istri_ortu);
                Pendidikan.setText(pendidikan);
                NamaIbuKandung.setText(nama_ibu_kandung);
                Email.setText(email);
                NoHp.setText(no_hp);
                NomorTelephone.setText(no_telp);
                statusRumah.setText(status_rumah);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void convertToJson(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("gelar",Gelar.getText().toString());
            obj.put("nama",Nama.getText().toString());
            obj.put("alamat",Alamat.getText().toString());
            obj.put("rt",Rt.getText().toString());
            obj.put("rw",Rw.getText().toString());
            obj.put("kelurahan_desa",KelurahanDesa.getText().toString());
            obj.put("kecamatan",Kecamatan.getText().toString());
            obj.put("kabupaten_kota",KabupatenKota.getText().toString());
            obj.put("provinsi",Provinsi.getText().toString());
            obj.put("kodepos",KodePos.getText().toString());
            obj.put("kewarganegaraan",Kewarganegaraan.getText().toString());
            obj.put("negara",Negara.getText().toString());
            obj.put("jenis_identitas_lain",JenisIdentitasLain.getText().toString());
            obj.put("jumlah_tanggungan",JumlahTanggungan.getText().toString());
            obj.put("nama_suami_istri_ortu",namaSuami_Istri_OrangTua.getText().toString());
            obj.put("jenis_kelamin",JenisKelamin.getText().toString());
            obj.put("agama",Agama.getText().toString());
            obj.put("status_perkawinan",Status.getText().toString());
            obj.put("pendidikan",Pendidikan.getText().toString());
            obj.put("nik",NIK.getText().toString());
            obj.put("jenis_bukti_identitas",jenisBuktiIdentitas.getText().toString());
            obj.put("tanggal_terbit_identitas",TanggalTerbitIdentitas.getText().toString());
            obj.put("tanggal_berakhir_identitas",TanggalBerakhirIdentitas.getText().toString());
            obj.put("nama_ibu_kandung",NamaIbuKandung.getText().toString());
            obj.put("email",Email.getText().toString());
            obj.put("no_handphone",NoHp.getText().toString());
            obj.put("no_telepon",NomorTelephone.getText().toString());
            obj.put("no_npwp",NomorNPWP.getText().toString());
            obj.put("status_rumah",statusRumah.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        objectCIF = obj.toString();
        session.saveCIF(objectCIF);
    }
    private void textWatcher(){
        KodePos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),charSequence,Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        JenisIdentitasLain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jenis_id_lain = (String) adapterView.getItemAtPosition(i);
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),jenis_id_lain,JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }
        });
        JumlahTanggungan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jumlah_tanggungan = (String) adapterView.getItemAtPosition(i);
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),jumlah_tanggungan,namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }
        });
        namaSuami_Istri_OrangTua.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),charSequence,JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Pendidikan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pend = (String) adapterView.getItemAtPosition(i);
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),pend,NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }
        });
        jenisBuktiIdentitas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),charSequence,TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        NamaIbuKandung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),charSequence,Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),charSequence,NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        NoHp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),charSequence,NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        NomorTelephone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),charSequence, NomorNPWP.getText().toString(),statusRumah.getText().toString(),false,"","","","","","","","",false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        statusRumah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String statRumah = (String) adapterView.getItemAtPosition(i);
                Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),KodePos.getText().toString(),Kewarganegaraan.getText().toString(),Negara.getText().toString(),JenisIdentitasLain.getText().toString(),JumlahTanggungan.getText().toString(),namaSuami_Istri_OrangTua.getText().toString(),JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Pendidikan.getText().toString(),NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),NamaIbuKandung.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),NomorTelephone.getText().toString(), NomorNPWP.getText().toString(),statRumah,false,"","","","","","","","",false);
            }
        });
    }
    private void setActionBar(){
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
    }
    private void setDropdown(){
        jenisIdentitasLain = new String[]{"KIMS/KITAS", "Lainnya"};
        jumlahTanggungan = new String[]{"1 - 3", "4 - 6", ">7"};
        pendidikanTerakhir = new String[]{"SMP","SMA","D1 - D3","S1","S2","S3"};
        statusRumah_ = new String[]{"Milik Sendiri", "Milik Keluarga", "Kontrak"};

        ArrayAdapter<String> jenisIdentity = new ArrayAdapter<String>(context,R.layout.list_item, jenisIdentitasLain);
        JenisIdentitasLain.setAdapter(jenisIdentity);

        ArrayAdapter<String> jumlahtanggung = new ArrayAdapter<String>(context,R.layout.list_item, jumlahTanggungan);
        JumlahTanggungan.setAdapter(jumlahtanggung);

        ArrayAdapter<String> lastEducation = new ArrayAdapter<String>(context,R.layout.list_item, pendidikanTerakhir);
        Pendidikan.setAdapter(lastEducation);

        ArrayAdapter<String> statusRumahArray = new ArrayAdapter<String>(context,R.layout.list_item, statusRumah_);
        statusRumah.setAdapter(statusRumahArray);
    }
    private void setOCR(){
        //setEnabled False
        Gelar.setEnabled(false);
        Nama.setEnabled(false);
        Alamat.setEnabled(false);
        Rt.setEnabled(false);
        Rw.setEnabled(false);
        KelurahanDesa.setEnabled(false);
        Kecamatan.setEnabled(false);
        KabupatenKota.setEnabled(false);
        Provinsi.setEnabled(false);
        Kewarganegaraan.setEnabled(false);
        Negara.setEnabled(false);
        JenisKelamin.setEnabled(false);
        jenisBuktiIdentitas.setEnabled(false);
        Agama.setEnabled(false);
        Status.setEnabled(false);
        NIK.setEnabled(false);
        NomorNPWP.setEnabled(false);
        TanggalTerbitIdentitas.setEnabled(false);
        TanggalBerakhirIdentitas.setEnabled(false);

        //setOCR
        Gelar.setText("Tn");
        Nama.setText("Andi Wijaya Lesmana");
        Alamat.setText("JL RAYA CISEENG NO.15 BLOK G, RT 12, RW 16, Kel CIBENTANG, Kec CISEENG");
        Rt.setText("012");
        Rw.setText("016");
        KelurahanDesa.setText("CIBENTANG");
        Kecamatan.setText("CISEENG");
        KabupatenKota.setText("BOGOR");
        Provinsi.setText("JAWA BARAT");
        Kewarganegaraan.setText("WNI");
        Negara.setText("INDONESIA");
        JenisKelamin.setText("LAKI-LAKI");
        Agama.setText("Islam");
        Status.setText("Belum Kawin");
        NIK.setText("320124150585005");
        NomorNPWP.setText("09.123.123.3-123.000");
        TanggalTerbitIdentitas.setText("15-09-2013");
        TanggalBerakhirIdentitas.setText("SEUMUR HIDUP");
        jenisBuktiIdentitas.setText("KTP");
        Mirroring(Nama.getText().toString(),Gelar.getText().toString(),Alamat.getText().toString(),Rt.getText().toString(),Rw.getText().toString(),KelurahanDesa.getText().toString(),Kecamatan.getText().toString(),KabupatenKota.getText().toString(),Provinsi.getText().toString(),"",Kewarganegaraan.getText().toString(),Negara.getText().toString(),"","","",JenisKelamin.getText().toString(),Agama.getText().toString(),Status.getText().toString(),"",NIK.getText().toString(),jenisBuktiIdentitas.getText().toString(),TanggalTerbitIdentitas.getText().toString(),TanggalBerakhirIdentitas.getText().toString(),"","","","", NomorNPWP.getText().toString(),"",false,"","","","","","","","",false);
    }
    private void ByteArraytoimg(byte[] byteArray, ImageView gambar_profile){
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        gambar_profile.setImageBitmap(decodedBitmap);
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void Mirroring(CharSequence nama,CharSequence gelar,CharSequence alamat,CharSequence rt,CharSequence rw,CharSequence kelurahan,CharSequence kecamatan,CharSequence kabupaten, CharSequence provinsi,CharSequence kodepos,CharSequence kewarganegaraan,CharSequence negara,CharSequence jenis_identitas_lain,CharSequence jumlah_tanggungan,CharSequence nama_suami_istri_ortu,CharSequence jenis_kelamin,CharSequence agama,CharSequence status,CharSequence pendidikan,CharSequence nik,CharSequence jenis_bukti_identitas,CharSequence tanggal_terbit_identitas,CharSequence tanggal_berakhir_identitas,CharSequence nama_ibu_kandung,CharSequence email,CharSequence no_handphone,CharSequence no_telepon,CharSequence no_npwp,CharSequence status_rumah,boolean bool,CharSequence alamat2,CharSequence rt2,CharSequence rw2,CharSequence provinsi2,CharSequence kabupaten2,CharSequence kecamatan2,CharSequence kelurahan2,CharSequence kodepos2,boolean bool2){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama);
            jsonArray.put(gelar);
            jsonArray.put(alamat);
            jsonArray.put(rt);
            jsonArray.put(rw);
            jsonArray.put(kelurahan);
            jsonArray.put(kecamatan);
            jsonArray.put(kabupaten);
            jsonArray.put(provinsi);
            jsonArray.put(kodepos);
            jsonArray.put(kewarganegaraan);
            jsonArray.put(negara);
            jsonArray.put(jenis_identitas_lain);
            jsonArray.put(jumlah_tanggungan);
            jsonArray.put(nama_suami_istri_ortu);
            jsonArray.put(jenis_kelamin);
            jsonArray.put(agama);
            jsonArray.put(status);
            jsonArray.put(pendidikan);
            jsonArray.put(nik);
            jsonArray.put(jenis_bukti_identitas);
            jsonArray.put(tanggal_terbit_identitas);
            jsonArray.put(tanggal_berakhir_identitas);
            jsonArray.put(nama_ibu_kandung);
            jsonArray.put(email);
            jsonArray.put(no_handphone);
            jsonArray.put(no_telepon);
            jsonArray.put(no_npwp);
            jsonArray.put(status_rumah);
            jsonArray.put(bool);
            jsonArray.put(alamat2);
            jsonArray.put(rt2);
            jsonArray.put(rw2);
            jsonArray.put(provinsi2);
            jsonArray.put(kabupaten2);
            jsonArray.put(kecamatan2);
            jsonArray.put(kelurahan2);
            jsonArray.put(kodepos2);
            jsonArray.put(bool2);
            jsons.put("idDips",idDips);
            jsons.put("code",30);
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
