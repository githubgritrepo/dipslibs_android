package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

public class frag_dialog_rtgs extends Fragment {

    private ImageView btnBack;
    private TextView tvCurr;
    private Button btnProsesRTGS, btnAdd;
    private Context mContext;
    private SessionManager sessions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dialog_rtgs, container, false);

        btnBack = (ImageView) view.findViewById(R.id.btn_back4);
        btnProsesRTGS = (Button) view.findViewById(R.id.btnProsesRTGS);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        tvCurr = (TextView) view.findViewById(R.id.tvCurr);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnProsesRTGS.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_cif)));
        btnAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_schedule)));

        String lang = sessions.getLANG();
        if (lang.equals("en")) {
            tvCurr.setText("IDR");
        } else {
            tvCurr.setText("Rp.");
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_berita());
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

}