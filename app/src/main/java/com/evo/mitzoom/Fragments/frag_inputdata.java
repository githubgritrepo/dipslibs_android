package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.google.android.material.button.MaterialButton;

public class frag_inputdata extends Fragment {
    private Context context;
    private EditText et_NamaNasabah, et_NikNasabah;
    private MaterialButton btnNext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
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
}
