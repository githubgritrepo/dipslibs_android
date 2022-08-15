package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

public class frag_address_opening extends Fragment {
    private Context context;
    private CheckBox alamatPernyataan;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm, isianForm;
    private EditText alamatBerbeda,RtBerbeda,RwBerbeda,ProvinsiBerbeda,KabupatenKotaBerbeda,KecamatanBerbeda,KelurahanBerbeda,Kodepos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_addres_opening, container, false);
        alamatPernyataan = view.findViewById(R.id.alamat_berbeda);
        alamatBerbeda = view.findViewById(R.id.et_alamatBerbeda);
        RtBerbeda = view.findViewById(R.id.et_rtBerbeda);
        RwBerbeda = view.findViewById(R.id.et_rwBerbeda);
        isianForm = view.findViewById(R.id.isianForm);
        ProvinsiBerbeda = view.findViewById(R.id.et_ProvinsiBerbeda);
        KabupatenKotaBerbeda = view.findViewById(R.id.et_KabupatenKotaBerbeda);
        KecamatanBerbeda = view.findViewById(R.id.et_KecamatanBerbeda);
        KelurahanBerbeda = view.findViewById(R.id.et_kelurahanDesaBerbeda);
        Kodepos = view.findViewById(R.id.et_kodeposBerbeda);
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
        alamatPernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alamatPernyataan.isChecked()){
                    isianForm.setVisibility(View.VISIBLE);
                }
                else {
                    isianForm.setVisibility(View.GONE);
                }
            }
        });
    }
}
