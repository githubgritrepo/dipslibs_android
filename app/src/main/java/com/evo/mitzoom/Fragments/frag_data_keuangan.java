package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_data_keuangan extends Fragment {
    private Context context;
    private Button btnProses, btnKembali;
    private byte[] KTP, NPWP, TTD;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_data_keuangan, container, false);
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
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                PopUpSuccesRegistration();
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
}
