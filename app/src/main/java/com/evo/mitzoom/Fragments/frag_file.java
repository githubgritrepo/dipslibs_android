package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.evo.mitzoom.Adapter.SectionsPagerAdapter;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.R;
import com.google.android.material.tabs.TabLayout;

public class frag_file extends Fragment {
    public static final String KEY_FRAGMENT = "fragment";
    private Fragment pageContent;
    private Context context;
    private ImageView btnBack;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        BaseMeetingActivity.btnFile.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        BaseMeetingActivity.btnFile.setClickable(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_file, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btnclose);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabs);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseMeetingActivity.btnFile.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                BaseMeetingActivity.btnFile.setClickable(true);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        sectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), context);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        if (savedInstanceState != null) {
            pageContent = getActivity().getSupportFragmentManager().getFragment(savedInstanceState, KEY_FRAGMENT);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragment, pageContent)
                    .commit();
        } else {
            int tabSelect = viewPager.getCurrentItem();
            pageContent = sectionsPagerAdapter.getItem(tabSelect);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragment, pageContent)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getActivity().getSupportFragmentManager().putFragment(outState, KEY_FRAGMENT, pageContent);
        super.onSaveInstanceState(outState);
    }
}
