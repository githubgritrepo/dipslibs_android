package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evo.mitzoom.R;

import me.relex.circleindicator.CircleIndicator;

public class FormRtgs extends Fragment {

    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private int[] layouts;
    private Context mContext;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        layouts = new int[] {R.layout.frag_dialog_rtgs};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_rtgs, container, false);

        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPager();
    }

    private void initPager() {
        if (myViewPagerAdapter != null) {
            myViewPagerAdapter = new MyViewPagerAdapter();
        }
        pager.setAdapter(myViewPagerAdapter);
        circleIndicator.setViewPager(pager);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            Button btnAdd = (Button) view.findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layouts[layouts.length] = R.layout.frag_dialog_rtgs;
                    initPager();
                }
            });

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}