package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

public class frag_new_account_cs_resi extends Fragment {
    private Context context;
    private LinearLayout icon_isi_form, icon_konfirmasi_data, icon_resi;
    private ImageView btnBack;
    private TextView tvnama;
    private Button unduh, selesai;
    private String nama;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_new_account_cs_resi, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btn_back_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        icon_konfirmasi_data = view.findViewById(R.id.icon_konfirmasi_data);
        icon_resi = view.findViewById(R.id.icon_resi);
        tvnama = view.findViewById(R.id.nama_);
        unduh = view.findViewById(R.id.formulir_unduh);
        selesai = view.findViewById(R.id.btnSelesaiCs);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack.setVisibility(View.GONE);
        Bundle arg = getArguments();
        nama = arg.getString("nama_lengkap");
        tvnama.setText(nama);
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        icon_resi.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        unduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Formulir berhasil diunduh", Toast.LENGTH_SHORT).show();
            }
        });
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentPage(new frag_portfolio());
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
