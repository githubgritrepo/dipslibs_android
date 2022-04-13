package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;

public class frag_aktivasi_ibmb extends Fragment {
    private Context context;
    private TextView UserId, Password, Konfirmasi_password, Mpin, Konfirmasi_mpin;
    private Button btnProses;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aktivasi_ibmb, container, false);
        UserId = view.findViewById(R.id.et_userId);
        Password = view.findViewById(R.id.et_password_ibmb);
        Konfirmasi_password = view.findViewById(R.id.et_konfirmasi_password_ibmb);
        Mpin = view.findViewById(R.id.et_mpin);
        Konfirmasi_mpin = view.findViewById(R.id.et_konfirmasi_mpin);
        btnProses = view.findViewById(R.id.btnProsesIbmb);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserId.setText("Andi Setiawan");
        Password.setText("Andi123456");
        Konfirmasi_password.setText("Andi123456");
        Mpin.setText("123456");
        Konfirmasi_mpin.setText("123456");
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_aktivasi_berhasil());
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
