package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsWaitingRoom;

public class frag_form_opening extends Fragment {
    private Context context;
    private ImageView iconKtp, iconNpwp, iconSignature, iconForm, preview_ktp, preview_npwp, preview_signature;;
    private TextView Nama,NIK,Email,Alamat;
    private AutoCompleteTextView Agama,Status;
    private String [] ListAgama = {"Islam", "Kristen", "Katolik", "Hindu", "Budha", "Konghucu"};
    private String [] ListStatus = {"Belum Kawin", "Menikah", "Duda", "Janda"};
    ArrayAdapter<String> adapterAgama;
    ArrayAdapter<String> adapterStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_opening_account, container, false);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        Nama = view.findViewById(R.id.et_nama);
        NIK = view.findViewById(R.id.et_nik);
        Email = view.findViewById(R.id.et_email);
        Alamat = view.findViewById(R.id.et_alamat);
        Agama = view.findViewById(R.id.et_agama);
        Status = view.findViewById(R.id.et_status);
        adapterAgama = new ArrayAdapter<String>(context,R.layout.list_item, ListAgama);
        adapterStatus = new ArrayAdapter<String>(context,R.layout.list_item, ListStatus);
        preview_ktp = view.findViewById(R.id.preview_ktp);
        preview_npwp = view.findViewById(R.id.preview_npwp);
        preview_signature = view.findViewById(R.id.preview_signature);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));

        Nama.setText("Andi Setiawan");
        NIK.setText("323432342304203");
        Alamat.setText("Rt.15 Rw/20 Maju, Kecamatan Suka Mulya, DKI Jakarta, Jawa Barat 12345");
        Agama.setText("Islam",false);
        Status.setText("Belum Kawin",false);
        Agama.setAdapter(adapterAgama);
        Status.setAdapter(adapterStatus);
    }

}
