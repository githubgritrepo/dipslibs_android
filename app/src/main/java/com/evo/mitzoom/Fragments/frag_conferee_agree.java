package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.R;

public class frag_conferee_agree extends Fragment {
    private Context context;
    private Button btn_Setuju,btn_tidak;
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
        View view = inflater.inflate(R.layout.frag_start_conferee, container, false);
        btn_Setuju = view.findViewById(R.id.btn_accept);
        btn_tidak = view.findViewById(R.id.btn_not);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_tidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutApps();
            }
        });
        btn_Setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCust == false) {
                //Jika muka tidak terdaftar maka menuju ke masukan nama & NIK
                getFragmentPage(new frag_inputdata());
                }
                else{
                //Jika muka terdaftar maka langsung menuju ke portfolio
                getFragmentPage(new frag_portfolio());
                }
                BaseMeetingActivity.btnChat.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                BaseMeetingActivity.btnChat.setClickable(true);
            }
        });
    }
    private void getFragmentPage(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putBoolean("ISCUST",isCust);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
        ((Activity)context).finish();
        System.exit(0);
    }
}
