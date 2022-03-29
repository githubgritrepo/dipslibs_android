package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evo.mitzoom.Dashboard;
import com.evo.mitzoom.R;
import com.google.android.material.textfield.TextInputEditText;

public class AntarRekening extends Fragment {

    private TextInputEditText et_NoRekeningTujuan;
    private TextInputEditText et_Nominal;
    private TextInputEditText et_Message;
    private Button btnCancel,btnClear;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_antar_rekening, container, false);

        initElement(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_NoRekeningTujuan.setText("");
                et_Nominal.setText("");
                et_Message.setText("");
            }
        });

    }

    private void initElement(View view) {
        et_NoRekeningTujuan = (TextInputEditText) view.findViewById(R.id.et_NoRekeningTujuan);
        et_Nominal = (TextInputEditText) view.findViewById(R.id.et_Nominal);
        et_Message = (TextInputEditText) view.findViewById(R.id.et_Message);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnClear = (Button) view.findViewById(R.id.btnClear);
    }
}