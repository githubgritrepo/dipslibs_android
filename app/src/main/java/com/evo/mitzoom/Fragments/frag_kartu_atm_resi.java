package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

public class frag_kartu_atm_resi extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private ImageView imgResume;
    private Button btnOK;
    private Button btnUnduhKartu;
    private TextView cardName;
    private String valCardName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);

        if (getArguments() != null) {
            valCardName = getArguments().getString("NAMA_KARTU");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_kartu_atm_resi, container, false);

        imgResume = (ImageView) v.findViewById(R.id.imgResume);
        cardName = (TextView) v.findViewById(R.id.cardName);
        btnOK = (Button) v.findViewById(R.id.btnSelesai);
        btnUnduhKartu = (Button) v.findViewById(R.id.btnUnduhKartu);

        if (!valCardName.isEmpty()) {
            cardName.setText(valCardName);
        }

        return v;
    }
}