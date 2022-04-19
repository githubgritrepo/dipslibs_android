package com.evo.mitzoom.Fragments;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhairytripathi.library.EditTextPin;
import com.evo.mitzoom.R;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_rtgs extends Fragment {
    private ImageView btnBack;
    private EditText et_NamaBank, et_RekPenerima, et_NamaPenerima, et_Berita, et_Nominal;
    private Context context;

    private Button btnProses;

    private String NamaBank, RekPenerima, NamaPenerima, Berita, Nominal;
    public static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_rtgs, container, false);
        btnBack = view.findViewById(R.id.btn_back4);
        et_NamaBank = view.findViewById(R.id.et_nama_bank);
        et_RekPenerima = view.findViewById(R.id.et_rek_penerima);
        et_NamaPenerima = view.findViewById(R.id.et_nama_penerima);
        et_Nominal = view.findViewById(R.id.et_nominal);
        et_Berita = view.findViewById(R.id.et_berita);
        btnProses = view.findViewById(R.id.btnProsesRTGS);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_Nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_Nominal.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_Nominal.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_Nominal.setText(formatted);
                et_Nominal.setSelection(formatted.length());
                et_Nominal.addTextChangedListener(this);
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NamaBank = et_NamaBank.getText().toString();
                RekPenerima = et_RekPenerima.getText().toString();
                NamaPenerima = et_NamaPenerima.getText().toString();
                Nominal = et_Nominal.getText().toString();
                Berita = et_Berita.getText().toString();
                if(NamaBank.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (RekPenerima.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (NamaPenerima.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (Nominal.equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (Berita.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else {
                    Fragment fragment = new frag_summary_rtgs();
                    Bundle bundle = new Bundle();
                    bundle.putString("namaBank",NamaBank);
                    bundle.putString("rekPenerima",RekPenerima);
                    bundle.putString("namaPenerima",NamaPenerima);
                    bundle.putString("nominal",Nominal);
                    bundle.putString("berita",Berita);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_service());
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

}
