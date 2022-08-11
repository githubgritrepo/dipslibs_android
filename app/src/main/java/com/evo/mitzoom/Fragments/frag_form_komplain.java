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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class frag_form_komplain extends Fragment {
    private Context context;
    private ImageView btnBack;
    private EditText perihal, tanggal, detailKomplain;
    private String Tgl;
    private CheckBox pernyataan;
    private Button btnProses;
    private int state;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_komplain, container, false);
        perihal = view.findViewById(R.id.et_hal);
        tanggal = view.findViewById(R.id.et_tgl_komplain);
        detailKomplain = view.findViewById(R.id.et_detail);
        btnBack = view.findViewById(R.id.btn_back_formKomplain);
        pernyataan = view.findViewById(R.id.pernyataan_komplain);
        btnProses = view.findViewById(R.id.btnProses_komplain);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arg = getArguments();
        state = arg.getInt("state");
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state){
                    case 1:
                        getFragmentPage1(new frag_berita());
                        return;
                    case 2:
                        getFragmentPage2(new frag_service());
                        return;
                }
            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        Tgl = df.format(c.getTime());
        tanggal.setText(Tgl);
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
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hal = perihal.getText().toString();
                String detail = detailKomplain.getText().toString();
                if (hal.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else if (detail.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Terima kasih!, Komplain anda sedang kami proses", Toast.LENGTH_SHORT).show();
                    switch (state){
                        case 1:
                            getFragmentPage1(new frag_berita());
                            return;
                        case 2:
                            getFragmentPage2(new frag_portfolio());
                            return;
                    }
                }
            }
        });
    }
    private void getFragmentPage1(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void getFragmentPage2(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
