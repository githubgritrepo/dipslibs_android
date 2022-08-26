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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private String idDips;
    private JSONObject objectCIF = null;
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
        String dataJsonS = session.getCIF();
        if (dataJsonS != null) {
            try {
                objectCIF = new JSONObject(dataJsonS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
                else if (NoHp.getText().toString().trim().isEmpty()){
                    NoHp.setError(getResources().getString(R.string.error_field));
                }
                else if (NomorTelephone.getText().toString().isEmpty()){
                    NomorTelephone.setError(getResources().getString(R.string.error_field));
                }
                else if (statusRumah.getText().toString().isEmpty()){
                    statusRumah.setError(getResources().getString(R.string.error_field));
                }
                else {
                    String no_handphone = NoHp.getText().toString().trim();
                    if (!no_handphone.substring(0,2).equals("08")) {
                        Toast.makeText(context,"Harus Nomor Handphone",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonCIF = dataCIFJson(false, false);
                    session.saveCIF(jsonCIF.toString());

                    Fragment fragment = new frag_address_opening();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("ktp",KTP);
                    bundle.putByteArray("npwp",NPWP);
                    bundle.putByteArray("ttd",TTD);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                }
            }
        });
    }
    /*private void getSessionData(){
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
    }*/
    private void textWatcher(){
        KodePos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        JenisIdentitasLain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
            }
        });
        JumlahTanggungan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
            }
        });
        namaSuami_Istri_OrangTua.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Pendidikan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
            }
        });
        jenisBuktiIdentitas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
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
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
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
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
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
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
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
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        statusRumah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonCIF = dataCIFJson(false, false);
                Mirroring(jsonCIF);
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
//        Gelar.setEnabled(false);
        Nama.setEnabled(false);
//        Alamat.setEnabled(false);
//        Rt.setEnabled(false);
//        Rw.setEnabled(false);
//        KelurahanDesa.setEnabled(false);
//        Kecamatan.setEnabled(false);
//        KabupatenKota.setEnabled(false);
//        Provinsi.setEnabled(false);
//        Kewarganegaraan.setEnabled(false);
//        Negara.setEnabled(false);
//        JenisKelamin.setEnabled(false);
//        jenisBuktiIdentitas.setEnabled(false);
//        Agama.setEnabled(false);
//        Status.setEnabled(false);
        NIK.setEnabled(false);
//        NomorNPWP.setEnabled(false);
//        TanggalTerbitIdentitas.setEnabled(false);
//        TanggalBerakhirIdentitas.setEnabled(false);

        //setOCR

        try {
            JSONArray dataArrCIF = objectCIF.getJSONArray("data");

            Gelar.setText(dataArrCIF.get(1).toString());
            Nama.setText(dataArrCIF.get(0).toString());
            Alamat.setText(dataArrCIF.get(2).toString());
            Rt.setText(dataArrCIF.get(3).toString());
            Rw.setText(dataArrCIF.get(4).toString());
            KelurahanDesa.setText(dataArrCIF.get(5).toString());
            Kecamatan.setText(dataArrCIF.get(6).toString());
            KabupatenKota.setText(dataArrCIF.get(7).toString());
            Provinsi.setText(dataArrCIF.get(8).toString());
            Kewarganegaraan.setText(dataArrCIF.get(10).toString());
            Negara.setText(dataArrCIF.get(11).toString());
            JenisKelamin.setText(dataArrCIF.get(15).toString());
            Agama.setText(dataArrCIF.get(16).toString());
            Status.setText(dataArrCIF.get(17).toString());
            NIK.setText(dataArrCIF.get(19).toString());
            NomorNPWP.setText("");
            TanggalTerbitIdentitas.setText("");
            TanggalBerakhirIdentitas.setText("SEUMUR HIDUP");
            jenisBuktiIdentitas.setText("KTP");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonCIF = dataCIFJson(false, false);
        Mirroring(jsonCIF);
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

    private JSONObject dataCIFJson(boolean boolForm, boolean bool2Address) {
        String nama = Nama.getText().toString();
        String gelar = Gelar.getText().toString();
        String alamat = Alamat.getText().toString();
        String rt = Rt.getText().toString();
        String rw = Rw.getText().toString();
        String kelurahan = KelurahanDesa.getText().toString();
        String kecamatan = Kecamatan.getText().toString();
        String kabupaten = KabupatenKota.getText().toString();
        String provinsi = Provinsi.getText().toString();
        String kodepos = KodePos.getText().toString();
        String kewarganegaraan = Kewarganegaraan.getText().toString();
        String negara = Negara.getText().toString();
        String jenis_identitas_lain = JenisIdentitasLain.getText().toString();
        String jumlah_tanggungan = JumlahTanggungan.getText().toString();
        String nama_suami_istri_ortu = namaSuami_Istri_OrangTua.getText().toString();
        String jenis_kelamin = JenisKelamin.getText().toString();
        String agama = Agama.getText().toString();
        String status = Status.getText().toString();
        String pendidikan = Pendidikan.getText().toString();
        String nik = NIK.getText().toString();
        String jenis_bukti_identitas = jenisBuktiIdentitas.getText().toString();
        String tanggal_terbit_identitas = TanggalTerbitIdentitas.getText().toString();
        String tanggal_berakhir_identitas = TanggalBerakhirIdentitas.getText().toString();
        String nama_ibu_kandung = NamaIbuKandung.getText().toString();
        String email = Email.getText().toString();
        String no_handphone = NoHp.getText().toString();
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(1);
            no_handphone = "62" + sub_no_handphone;
        }
        String no_telepon = NomorTelephone.getText().toString();
        String no_npwp = NomorNPWP.getText().toString();
        String status_rumah = statusRumah.getText().toString();

        JSONObject jsons = objectCIF;
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
            jsonArray.put(boolForm);
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put(bool2Address);
            jsons.put("idDips",idDips);
            jsons.put("code",30);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsons;
    }

    private void Mirroring(JSONObject jsons){

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
