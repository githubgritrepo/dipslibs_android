package com.evo.mitzoom.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evo.mitzoom.R;

public class frag_maintenance extends Fragment {

    private ImageView btn_back4;
    private TextView tvtitleHead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_maintenance, container, false);

        btn_back4 = (ImageView) views.findViewById(R.id.btn_back4);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack("myFragMaintenance", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }


}