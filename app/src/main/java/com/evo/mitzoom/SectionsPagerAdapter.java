package com.evo.mitzoom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.evo.mitzoom.Fragments.AntarBank;
import com.evo.mitzoom.Fragments.AntarRekening;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mcontext;

    public SectionsPagerAdapter(@NonNull FragmentManager fm, Context mcontext) {
        super(fm);
        this.mcontext = mcontext;
    }

    private final int[] TAB_TITLES = new int[]{
            R.string.tab_text_1,
            R.string.tab_text_2
    };

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AntarRekening();
                break;
            case 1:
                fragment = new AntarBank();
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mcontext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
