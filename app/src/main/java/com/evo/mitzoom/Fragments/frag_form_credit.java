package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_form_credit extends Fragment {
    private Context context;
    private Button btnProses;
    private CheckBox pernyataan;
    private EditText et_nominal,et_nama, et_nik, et_nohp, et_email, et_alamat, et_agama, et_status;
    private ImageView  btnBack;
    private LayoutInflater inflater;
    private View dialogView;
    private Button btnVerifikasi;
    private PinView otp;
    private String newString, idDips, tenor_ = "";
    public int getMinutes = 2, state, seconds = 60;
    public boolean running = true, pernyataan__ = false;
    private Handler handler = null;
    private Runnable myRunnable = null;
    private Handler handlerSuccess;
    private TextView judul, tenor, nominalPengajuan, Timer, Resend_Otp;
    private RadioGroup radioTenor;
    private LinearLayout LLNominal;
    private SessionManager session;
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_credit, container, false);
        btnProses = view.findViewById(R.id.btnProses_formKredit);
        btnBack = view.findViewById(R.id.btn_back_formCredit);
        pernyataan = view.findViewById(R.id.pernyataan_formKredit);
        et_nama = view.findViewById(R.id.et_nama_form);
        et_nik = view.findViewById(R.id.et_nik_form);
        et_nohp = view.findViewById(R.id.et_no_hp_form);
        et_email = view.findViewById(R.id.et_email_form);
        et_alamat = view.findViewById(R.id.et_alamat_form);
        et_agama = view.findViewById(R.id.et_agama_form);
        et_status = view.findViewById(R.id.et_status_form);
        et_nominal = view.findViewById(R.id.et_nominalPengajuan);
        tenor = view.findViewById(R.id.tenor);
        radioTenor = view.findViewById(R.id.group_tenor);
        nominalPengajuan = view.findViewById(R.id.tv_nominalPengajuan);
        LLNominal = view.findViewById(R.id.nominal_pengajuanLL);
        judul = view.findViewById(R.id.judul);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        Bundle arg = getArguments();
        state = arg.getInt("state");
        Log.d("STATE",""+state);
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        switch (state){
            case 1:
                judul.setText(getString(R.string.CREDIT_FORM));
                tenor.setVisibility(View.VISIBLE);
                radioTenor.setVisibility(View.VISIBLE);
                nominalPengajuan.setVisibility(View.VISIBLE);
                LLNominal.setVisibility(View.VISIBLE);
                break;
            case 2:
                judul.setText(getString(R.string.KYC_UPDATING));
                tenor.setVisibility(View.GONE);
                radioTenor.setVisibility(View.GONE);
                nominalPengajuan.setVisibility(View.GONE);
                LLNominal.setVisibility(View.GONE);
                break;
            case 3:
                judul.setText(getString(R.string.FINANCIAL_PLANNING));
                tenor.setVisibility(View.GONE);
                radioTenor.setVisibility(View.GONE);
                nominalPengajuan.setVisibility(View.GONE);
                LLNominal.setVisibility(View.GONE);
                break;
            case 4:
                judul.setText(getString(R.string.POWER_OF_ATTORNEY));
                tenor.setVisibility(View.GONE);
                radioTenor.setVisibility(View.GONE);
                nominalPengajuan.setVisibility(View.GONE);
                LLNominal.setVisibility(View.GONE);
                break;
        }
        textWatcher();
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    pernyataan__ = true;
                    switch (state){
                        case 1:
                            MirroringCREDIT(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),tenor_,et_nominal.getText().toString(),pernyataan__,false,false);
                            getFragmentPage(new frag_service());
                            break;
                        case 2:
                            MirroringKYCUpdating(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),pernyataan__,false,false);
                            getFragmentPage(new frag_service());
                            break;
                        case 3:
                            MirroringKYCUpdating(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),pernyataan__,false,false);
                            getFragmentPage(new frag_service());
                            break;
                        case 4:
                            MirroringKYCUpdating(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),pernyataan__,false,false);
                            getFragmentPage(new frag_service());
                            break;
                    }
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    pernyataan__ = false;
                    switch (state){
                        case 1:
                            getFragmentPage(new frag_service());
                            break;
                        case 2:
                            MirroringKYCUpdating(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),pernyataan__,false,true);
                            getFragmentPage(new frag_service());
                            break;
                        case 3:
                            MirroringKYCUpdating(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),pernyataan__,false,true);
                            getFragmentPage(new frag_service());
                            break;
                        case 4:
                            MirroringKYCUpdating(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),pernyataan__,false,true);
                            getFragmentPage(new frag_service());
                            break;
                    }
                    Log.d("CHECK","FALSE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state){
                    case 1:
                        MirroringCREDIT("","","","","","","","","",pernyataan__,false,true);
                        getFragmentPage(new frag_service());
                        break;
                    case 2:
                        Log.d("CASE 2","");
                        MirroringKYCUpdating("","","","","","","",pernyataan__,false,true);
                        getFragmentPage(new frag_service());
                        break;
                    case 3:
                        MirroringKYCUpdating("","","","","","","",pernyataan__,false,true);
                        getFragmentPage(new frag_service());
                        break;
                    case 4:
                        MirroringKYCUpdating("","","","","","","",pernyataan__,false,true);
                        getFragmentPage(new frag_service());
                        break;
                }

            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state){
                    case 1:
                        break;
                    case 2:
                        PopUp(2);
                        break;
                    case 3:
                        PopUp(3);
                        break;
                    case 4:
                        PopUp(4);
                        break;
                }
            }
        });
    }
    private void textWatcher(){
        switch (state){
            case 1:
               //FORM CREDIT
                et_nama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(charSequence, et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), charSequence,et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nohp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),charSequence,et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),charSequence,et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_alamat.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),charSequence,et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_agama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),charSequence,et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_status.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),charSequence, pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                int selectedId = radioTenor.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) radioTenor.findViewById(selectedId);
                int idRb = radioButton.getId();
                switch(idRb) {
                    case R.id.duabelas_Bulan:
                        tenor_ = "12 Bulan";
                        MirroringCREDIT(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),tenor_,et_nominal.getText().toString(),pernyataan__,false,false);
                        break;
                    case R.id.duaempat_Bulan:
                        tenor_ = "24 Bulan";
                        MirroringCREDIT(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),tenor_,et_nominal.getText().toString(),pernyataan__,false,false);
                        break;
                    case R.id.tigalima_Bulan:
                        tenor_ = "35 Bulan";
                        MirroringCREDIT(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),tenor_,et_nominal.getText().toString(),pernyataan__,false,false);
                        break;
                }
                et_nominal.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        et_nominal.removeTextChangedListener(this);
                        BigDecimal parsed = parseCurrencyValue(et_nominal.getText().toString());
                        String formatted = numberFormat.format(parsed);
                        et_nominal.setText(formatted);
                        MirroringCREDIT(et_nama.getText().toString(),et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(),tenor_,formatted,pernyataan__,false,false);
                        et_nominal.setSelection(formatted.length());
                        et_nominal.addTextChangedListener(this);
                    }
                });
                break;
            case 2:
               //KYC UPDATING
                et_nama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(charSequence, et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), charSequence,et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nohp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),charSequence,et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),charSequence,et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_alamat.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),charSequence,et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_agama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),charSequence,et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_status.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),charSequence, pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                break;
            case 3:
                //RENCANA KEUANGAN
                et_nama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(charSequence, et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), charSequence,et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nohp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),charSequence,et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),charSequence,et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_alamat.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),charSequence,et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_agama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),charSequence,et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_status.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),charSequence, pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                break;
            case 4:
               //SURAT KUASA
                et_nama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(charSequence, et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), charSequence,et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_nohp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),charSequence,et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),charSequence,et_alamat.getText().toString(),et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_alamat.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),charSequence,et_agama.getText().toString(),et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_agama.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),charSequence,et_status.getText().toString(), pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                et_status.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MirroringKYCUpdating(et_nama.getText().toString(), et_nik.getText().toString(),et_nohp.getText().toString(),et_email.getText().toString(),et_alamat.getText().toString(),et_agama.getText().toString(),charSequence, pernyataan__,false,false );
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                break;
        }
    }
    private void PopUp(int cek_state){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_otp,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        btnVerifikasi = dialogView.findViewById(R.id.btnVerifikasi);
        Timer = dialogView.findViewById(R.id.timer_otp);
        Resend_Otp = dialogView.findViewById(R.id.btn_resend_otp);
        otp = dialogView.findViewById(R.id.otp);
        otp.setAnimationEnable(true);
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*selPos = otp.getSelectionStart();
                oldString = myFilter(s.toString());*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    switch (cek_state){
                        case 1:
                            break;
                        case 2:
                            MirroringOTPKYC(s,false);
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                newString = myFilter(s.toString());
                otp.removeTextChangedListener(this);
                handler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        otp.setText(newString);
                    }
                };
                otp.addTextChangedListener(this);
                handler.postDelayed(myRunnable, 1500);
                if (otp.length() == 6 || otp.length() == 0){
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Log.d("TAG","STOP Loop");
                }
            }
        });
        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    switch (cek_state){
                        case 2:
                            MirroringOTPKYC(otp.getText().toString(),true);
                            break;
                        case 3:
                            MirroringOTPKYC(otp.getText().toString(),true);
                            break;
                        case 4:
                            MirroringOTPKYC(otp.getText().toString(),true);
                            break;
                    }
                    sweetAlertDialog.dismiss();
                    PopUpSuccesOtp();
                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    Toast.makeText(context, "Kode Terkirim", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void PopUpSuccesOtp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.otp_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.otp_content));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        handlerSuccess = new Handler();
        handlerSuccess.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = new frag_portfolio();
                getFragmentPage(fragment);
                sweetAlertDialog.dismiss();
            }
        },5000);
    }
    public String myFilter(String s) {
        String digits;
        digits = s.replaceAll("[0-9]", "*");
        if (s.equals("")) return "";
        return digits;
    }
    public void runTimer(TextView timer_run, TextView resend) {
        Handler handlerTimer = new Handler();
        handlerTimer.post(new Runnable() {
            @Override
            public void run() {
                int minutes = getMinutes;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0 && minutes == 0){
                    running = false;
                    resend.setClickable(true);
                } else if (seconds == 0 && minutes > 0){
                    seconds = 59;
                }
                if (seconds == 59) {
                    getMinutes--;
                }
                handlerTimer.postDelayed(this,1000);
            }
        });
    }
    public static BigDecimal parseCurrencyValue(String value) {
        try {
            String replaceRegex = String.format("[%s,.\\s]", Objects.requireNonNull(numberFormat.getCurrency()).getDisplayName());
            String currencyValue = value.replaceAll(replaceRegex, "");
            return new BigDecimal(currencyValue);
        } catch (Exception e) {
            Log.e("MyApp", e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }
    private void MirroringKYCUpdating(CharSequence nama, CharSequence nik, CharSequence nohp, CharSequence email, CharSequence alamat, CharSequence agama, CharSequence status, Boolean bool_pernyataan, Boolean bool_submit, Boolean bool_back){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama);
            jsonArray.put(nik);
            jsonArray.put(nohp);
            jsonArray.put(email);
            jsonArray.put(alamat);
            jsonArray.put(agama);
            jsonArray.put(status);
            jsonArray.put(bool_pernyataan);
            jsonArray.put(bool_submit);
            jsonArray.put(bool_back);
            jsons.put("idDips",idDips);
            jsons.put("code",381);
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
    private void MirroringOTPKYC(CharSequence s, boolean bool){
        Log.d("OTP","ini hit OTP");
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(s);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",382);
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
    private void MirroringCREDIT(CharSequence nama, CharSequence nik, CharSequence nohp, CharSequence email, CharSequence alamat, CharSequence agama, CharSequence status, CharSequence tenor, CharSequence nominal,Boolean bool_pernyataan, Boolean bool_submit, Boolean bool_back){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama);
            jsonArray.put(nik);
            jsonArray.put(nohp);
            jsonArray.put(email);
            jsonArray.put(alamat);
            jsonArray.put(agama);
            jsonArray.put(status);
            jsonArray.put(tenor);
            jsonArray.put(nominal);
            jsonArray.put(bool_pernyataan);
            jsonArray.put(bool_submit);
            jsonArray.put(bool_back);
            jsons.put("idDips",idDips);
            jsons.put("code",441);
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
