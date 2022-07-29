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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.math.BigDecimal;

public class frag_new_account_cs2 extends Fragment {
    private Context context;
    private ImageView btnBack;
    private Button btnProses;
    private String idDips, rekSumberdana, nama, tgl, produk, nominal;
    private CheckBox pernyataan;
    private LinearLayout icon_isi_form, icon_konfirmasi_data, icon_resi;
    private TextView tv_reksumber, tv_namaLengkap, tv_tgl, tv_produk, tv_nominal;
    private SessionManager session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_new_account_cs2, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btn_back_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        icon_konfirmasi_data = view.findViewById(R.id.icon_konfirmasi_data);
        icon_resi = view.findViewById(R.id.icon_resi);
        btnProses = view.findViewById(R.id.btnProses_konfirmasi_data_cs);
        tv_reksumber = view.findViewById(R.id.RekeningSumber_cs);
        tv_namaLengkap = view.findViewById(R.id.nama_lengkap_new_acc_cs);
        tv_produk = view.findViewById(R.id.tipe_produk_rek_new_acc_cs);
        tv_tgl = view.findViewById(R.id.tgl_pembukaan_new_acc_cs);
        tv_nominal = view.findViewById(R.id.setoran_awal_new_acc_cs);
        pernyataan = view.findViewById(R.id.pernyataan_cs);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        Bundle arg = getArguments();
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        rekSumberdana = arg.getString("rek_sumber_dana");
        nama = arg.getString("nama_lengkap");
        tgl = arg.getString("tgl");
        produk = arg.getString("produk");
        nominal = arg.getString("nominal");
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        tv_reksumber.setText(rekSumberdana);
        tv_namaLengkap.setText(nama);
        tv_produk.setText(produk);
        tv_tgl.setText(tgl);
        tv_nominal.setText("Rp"+nominal);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new frag_new_account_cs();
                Bundle bundle = new Bundle();
                bundle.putString("rek_sumber_dana",rekSumberdana);
                bundle.putString("nama_lengkap",nama);
                bundle.putString("tgl",tgl);
                bundle.putString("produk",produk);
                bundle.putString("nominal",nominal);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                icon_resi.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show();

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
}
