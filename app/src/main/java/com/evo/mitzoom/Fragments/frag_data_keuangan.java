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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_data_keuangan extends Fragment {
    private SessionManager session;
    private Context context;
    private String idDips;
    private Button btnProses, btnKembali;
    private byte[] KTP, NPWP, TTD;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private AutoCompleteTextView jenisPekerjaan, penghasilanBulan, sumberDana, tujuanPenggunaan, penghasilanTambahan, jenisRekening, namaProduk, mataUang, perkiraan, frekuensi, perkiraan2, frekuensi2, perkiraan3, frekuensi3, perkiraan4, frekuensi4;
    private String [] jenisPekerjaan_, penghasilanBulan_, sumberDana_, tujuanPenggunaan_, penghasilanTambahan_, jenisRekening_, namaProduk_, mataUang_, perkiraan_, frekuensi_;
    private String typeWork = "";
    private String monthIncome = "";
    private String sourceFund = "";
    private String intendedUse = "";
    private String extraIncome = "";
    private String accountType = "";
    private String productName  = "";
    private String currency = "";
    private String estimate = "";
    private String estimate2 = "";
    private String estimate3 = "";
    private String estimate4 = "";
    private String frequency = "";
    private String frequency2 = "";
    private String frequency3 = "";
    private String frequency4 = "";
    private JSONObject objectCIF;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_data_keuangan, container, false);
        jenisPekerjaan = view.findViewById(R.id.act_jenisPekerjaan);
        penghasilanBulan = view.findViewById(R.id.act_penghasilanBulan);
        sumberDana = view.findViewById(R.id.act_sumberDana);
        tujuanPenggunaan = view.findViewById(R.id.act_tujuanPenggunaan);
        penghasilanTambahan = view.findViewById(R.id.act_penghasilanTambahan);
        jenisRekening = view.findViewById(R.id.act_jenisRekening);
        namaProduk = view.findViewById(R.id.act_namaProduk);
        mataUang = view.findViewById(R.id.act_mataUang);
        perkiraan = view.findViewById(R.id.act_perkiraan);
        frekuensi = view.findViewById(R.id.act_frekuensi);
        perkiraan2 = view.findViewById(R.id.act_perkiraan2);
        frekuensi2 = view.findViewById(R.id.act_frekuensi2);
        perkiraan3 = view.findViewById(R.id.act_perkiraan3);
        frekuensi3 = view.findViewById(R.id.act_frekuensi3);
        perkiraan4 = view.findViewById(R.id.act_perkiraan4);
        frekuensi4 = view.findViewById(R.id.act_frekuensi4);

        btnProses = view.findViewById(R.id.btnProses_Keuangan);
        btnKembali = view.findViewById(R.id.btnKembali_Keuangan);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
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

        jenisPekerjaan_ = new String[]{"Karyawan","Wiraswasta"};
        penghasilanBulan_ = new String[]{"< 10jt","> 10 - 50jt","> 50 - 100jt","> 100 - 500jt", "> 500jt"};
        sumberDana_ = new String[]{"Gaji","Bisnis/Usaha","Tabungan Pribadi"};
        tujuanPenggunaan_ = new String[]{"Pengeluaran Rutin", "Pembayaran", "Bisnis"};
        penghasilanTambahan_ = new String[]{"< 10jt","> 10 - 50jt","> 50 - 100jt","> 100 - 500jt", "> 500jt"};
        jenisRekening_ = new String[]{"Giro","Tabungan","Deposito","Pinjaman"};
        namaProduk_ = new String[]{"Tabungan A","Tabungan B"};
        mataUang_ = new String[]{"Rupiah"};
        perkiraan_ = new String[]{"< 10jt","> 10 - 50jt","> 50 - 100jt","> 100 - 500jt","> 500jt"};
        frekuensi_ = new String[]{"0-10 Kali","11-20 Kali", "21-30 Kali"};

        ArrayAdapter<String> jp= new ArrayAdapter<String>(context,R.layout.list_item, jenisPekerjaan_);
        jenisPekerjaan.setAdapter(jp);

        ArrayAdapter<String> pb= new ArrayAdapter<String>(context,R.layout.list_item, penghasilanBulan_);
        penghasilanBulan.setAdapter(pb);

        ArrayAdapter<String> sd= new ArrayAdapter<String>(context,R.layout.list_item, sumberDana_);
        sumberDana.setAdapter(sd);

        ArrayAdapter<String> tp= new ArrayAdapter<String>(context,R.layout.list_item, tujuanPenggunaan_);
        tujuanPenggunaan.setAdapter(tp);

        ArrayAdapter<String> pt= new ArrayAdapter<String>(context,R.layout.list_item, penghasilanTambahan_);
        penghasilanTambahan.setAdapter(pt);

        ArrayAdapter<String> jk= new ArrayAdapter<String>(context,R.layout.list_item, jenisRekening_);
        jenisRekening.setAdapter(jk);

        ArrayAdapter<String> np= new ArrayAdapter<String>(context,R.layout.list_item, namaProduk_);
        namaProduk.setAdapter(np);

        ArrayAdapter<String> mu= new ArrayAdapter<String>(context,R.layout.list_item, mataUang_);
        mataUang.setAdapter(mu);

        ArrayAdapter<String> p= new ArrayAdapter<String>(context,R.layout.list_item, perkiraan_);
        perkiraan.setAdapter(p);
        perkiraan2.setAdapter(p);
        perkiraan3.setAdapter(p);
        perkiraan4.setAdapter(p);

        ArrayAdapter<String> f= new ArrayAdapter<String>(context,R.layout.list_item, frekuensi_);
        frekuensi.setAdapter(p);
        frekuensi2.setAdapter(p);
        frekuensi3.setAdapter(p);
        frekuensi4.setAdapter(p);

        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accountType.isEmpty()) {
                    Toast.makeText(context,"Silakan Pilih Jenis Rekening",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (productName.isEmpty()) {
                    Toast.makeText(context,"Silakan Pilih Nama Produk",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currency.isEmpty()) {
                    Toast.makeText(context,"Silakan Pilih Mata Uang",Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONArray dataArrCIF = objectCIF.getJSONArray("data");
                    dataArrCIF.put(39,productName);

                    objectCIF.put("data",dataArrCIF);
                    session.saveCIF(objectCIF.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                Mirroring(true);
            }
        });
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new frag_data_pekerjaan();
                Bundle bundle = new Bundle();
                bundle.putByteArray("ktp",KTP);
                bundle.putByteArray("npwp",NPWP);
                bundle.putByteArray("ttd",TTD);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });

        jenisPekerjaan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                typeWork = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        penghasilanBulan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                monthIncome = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        sumberDana.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                sourceFund = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        tujuanPenggunaan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                intendedUse = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        penghasilanTambahan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                extraIncome = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        jenisRekening.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                accountType = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        namaProduk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                productName = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        mataUang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                currency = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        perkiraan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                estimate = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        perkiraan2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                estimate2 = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        perkiraan3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                estimate3 = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        perkiraan4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                estimate4 = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        frekuensi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                frequency = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        frekuensi2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                frequency2 = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        frekuensi3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                frequency3 = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });

        frekuensi4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                frequency4 = (String) parent.getItemAtPosition(position);
                Mirroring(false);
            }
        });
    }
    private void PopUpSuccesRegistration(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.reg_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.reg_content));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.activation));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Mirroring2(true);
                getFragmentPage(new frag_aktivasi_ibmb());
                sweetAlertDialog.dismiss();
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
    private void Mirroring(boolean bool) {
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(typeWork);
            jsonArray.put(monthIncome);
            jsonArray.put(sourceFund);
            jsonArray.put(intendedUse);
            jsonArray.put(extraIncome);
            jsonArray.put(accountType);
            jsonArray.put(productName);
            jsonArray.put(currency);
            jsonArray.put(estimate);
            jsonArray.put(estimate2);
            jsonArray.put(estimate3);
            jsonArray.put(estimate4);
            jsonArray.put(frequency);
            jsonArray.put(frequency2);
            jsonArray.put(frequency3);
            jsonArray.put(frequency4);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",32);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        Server.getAPIService().Mirroring(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (bool) {
                    APISaveForm(jsonArray);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void Mirroring2(Boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",9);
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

    private void APISaveForm(JSONArray jsonsMoney) {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data",jsonsMoney);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsons = new JSONObject();
        try {
            JSONArray dataArrCIF = objectCIF.getJSONArray("data");
            String no_handphone = dataArrCIF.get(25).toString();
            jsons.put("formCode","CIF3");
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
                            PopUpSuccesRegistration();
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
