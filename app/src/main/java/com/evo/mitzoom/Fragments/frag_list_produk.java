package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsWaitingRoom;

public class frag_list_produk extends Fragment {
    private Context context;
    private NestedScrollView nested;
    private TextView btn_tabungan_a, btn_tabungan_b;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_daftar_produk, container, false);
        btn_tabungan_a = view.findViewById(R.id.btn_tabungan_a);
        btn_tabungan_b = view.findViewById(R.id.btn_tabungan_b);
        nested = view.findViewById(R.id.nested);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 0){
                    DipsWaitingRoom.smoothBottomBar.setVisibility(View.INVISIBLE);
                }
                else {
                    DipsWaitingRoom.smoothBottomBar.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_tabungan_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tabungan = "Tabungan A";
                sendDataFragment("headline",Tabungan,new frag_tabungan());
            }
        });
        btn_tabungan_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tabungan = "Tabungan B";
                sendDataFragment("headline",Tabungan,new frag_tabungan());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DipsWaitingRoom.smoothBottomBar.setVisibility(View.VISIBLE);
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void sendDataFragment(String tag, String Text, Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString(tag,Text);
        fragment.setArguments(bundle);
        getFragmentPage(fragment);
    }
}
