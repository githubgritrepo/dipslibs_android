package com.evo.mitzoom.Fragments;

import android.content.Context;
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
        btn_Setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_inputdata());
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
}
