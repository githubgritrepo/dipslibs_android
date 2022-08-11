package com.evo.mitzoom.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.evo.mitzoom.Fragments.frag_daftar_file;
import com.evo.mitzoom.Fragments.frag_kirim_file;
import com.evo.mitzoom.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final Context mcontext;
    public SectionsPagerAdapter(@NonNull FragmentManager fm, Context mcontext) {
        super(fm);
        this.mcontext = mcontext;
    }

    private final int[] TAB_TITLES = new int[]{
            R.string.tab_1,
            R.string.tab_2
    };
    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new frag_daftar_file();
                break;
            case 1:
                fragment = new frag_kirim_file();
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
