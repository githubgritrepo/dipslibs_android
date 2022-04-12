package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsSplashScreen;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.google.android.material.button.MaterialButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_inputdata extends Fragment {
    private Context context;
    private EditText et_NamaNasabah, et_NikNasabah;
    private MaterialButton btnNext;
    private boolean isCust;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        isCust = getArguments().getBoolean("ISCUST");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_input_nik_nama, container, false);
        et_NamaNasabah = view.findViewById(R.id.et_nama);
        et_NikNasabah = view.findViewById(R.id.et_nik);
        btnNext = view.findViewById(R.id.btnNext);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Popup();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCust == false) {
                    getFragmentPage(new frag_item()); //untuk menu non Customer
                } else {
                    //untuk Menu Customer
                    getFragmentPage(new frag_item());
                }
            }
        });
    }
    private void Popup(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.content_input));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
