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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class frag_new_account extends Fragment {
    private Context context;
    private ImageView btnBack;
    private LinearLayout icon_isi_form;
    private Button btnproses;
    private EditText et_nama, et_tgl_daftar, et_nominal_daftar;
    private AutoCompleteTextView et_productType;
    private String nama, tgl, produk, nominal;
    private String tgl_daftar;
    String[] rektype;
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_new_account, container, false);
        btnBack = view.findViewById(R.id.btn_back_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        et_productType = view.findViewById(R.id.et_productType);
        btnproses = view.findViewById(R.id.btnProses_isi_form);
        et_nama = view.findViewById(R.id.et_nama_new);
        et_tgl_daftar = view.findViewById(R.id.et_tgl_daftar);
        et_nominal_daftar = view.findViewById(R.id.et_nominal_setoran);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rektype = new String[]{
                getResources().getString(R.string.saving_new_acc),
                getResources().getString(R.string.saving_new_acc2),
                getResources().getString(R.string.giro_new_acc),
                getResources().getString(R.string.deposito_new_acc),
                getResources().getString(R.string.asuransi_new_acc),
                getResources().getString(R.string.Reksadana_new_acc)
        };
        Bundle arg = getArguments();
        if (arg != null){
            nama = arg.getString("nama_lengkap");
            produk = arg.getString("produk");
            nominal = arg.getString("nominal");

            et_nama.setText(nama);
            et_productType.setText(produk);
            et_nominal_daftar.setText(nominal);
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        tgl_daftar = df.format(c.getTime());
        btnproses.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentPage(new frag_berita());
            }
        });
        et_nominal_daftar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_nominal_daftar.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_nominal_daftar.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_nominal_daftar.setText(formatted);
                et_nominal_daftar.setSelection(formatted.length());
                et_nominal_daftar.addTextChangedListener(this);
            }
        });
        et_tgl_daftar.setText(tgl_daftar);

        ArrayAdapter<String> adapterTypeProduct = new ArrayAdapter<String>(context,R.layout.list_item, rektype);
        et_productType.setAdapter(adapterTypeProduct);
        et_productType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = et_productType.getText().toString();
            }
        });

        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nama = et_nama.getText().toString();
                tgl = et_tgl_daftar.getText().toString();
                produk = et_productType.getText().toString();
                nominal = et_nominal_daftar.getText().toString();
                if (nama.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else if (produk.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else if (nominal.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static BigDecimal parseCurrencyValue(String value) {
        try {
            String replaceRegex = String.format("[%s,.\\s]", Objects.requireNonNull(numberFormat.getCurrency()).getDisplayName());
            String currencyValue = value.replaceAll(replaceRegex, "");
            return new BigDecimal(currencyValue);
        } catch (Exception e) {

        }
        return BigDecimal.ZERO;
    }

}
