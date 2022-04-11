package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_tabungan extends Fragment {
    private Context context;
    private ImageView btnBack;
    private TextView Headline, tnc;
    private Button btnCreateAccount;
    private String headline;
    private LayoutInflater inflater;
    private View dialogView;
    private NestedScrollView nestedScrollView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_tabungan, container, false);
        btnBack = view.findViewById(R.id.btn_back3);
        Headline = view.findViewById(R.id.nama_tabungan);
        btnCreateAccount = view.findViewById(R.id.btnNewRek);
        tnc = view.findViewById(R.id.tnc);
        nestedScrollView = view.findViewById(R.id.Nested);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DipsWaitingRoom.smoothBottomBar.setVisibility(View.INVISIBLE);
        Bundle arg = getArguments();
        btnCreateAccount.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        headline = arg.getString("headline");
        Headline.setText(headline);
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tnc();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DipsWaitingRoom.smoothBottomBar.setVisibility(View.VISIBLE);
                getFragmentPage(new frag_list_produk());
            }
        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_opening_account());
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
    private void tnc(){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_tnc,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.show();
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_tnc);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
            }
        });
    }
}
