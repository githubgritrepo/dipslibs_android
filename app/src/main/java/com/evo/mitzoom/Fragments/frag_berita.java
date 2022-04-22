package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.evo.mitzoom.Adapter.AdapterSlide;
import com.evo.mitzoom.Adapter.GridProductAdapter;
import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsWaitingRoom;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class frag_berita extends Fragment {
    private Context context;
    int [] gambar = {R.drawable.rtgs, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4,R.drawable.ads1, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4,R.drawable.ads1, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4,R.drawable.ads1, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4};
    private RecyclerView rv_product;
    private ViewPager mPager;
    private GridProductAdapter gridAdapter;
    private static final Integer[] img = {R.drawable.adsv1, R.drawable.adsv2, R.drawable.adsv3};
    private ArrayList<Integer> imgArray = new ArrayList<Integer>();
    private CircleIndicator circleIndicator;
    private int currentPage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_berita, container, false);
        rv_product = view.findViewById(R.id.rv_product);
        mPager = view.findViewById(R.id.pager);
        circleIndicator = view.findViewById(R.id.indicator);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_product.setLayoutManager(new GridLayoutManager(context,2));
        gridAdapter = new GridProductAdapter(context,gambar);
        rv_product.setAdapter(gridAdapter);
        initPager();
    }
    private void initPager() {
        for (int i = 0; i < img.length; i++) {
            imgArray.add(img[i]);
            mPager.setAdapter(new AdapterSlide(imgArray, context));
            circleIndicator.setViewPager(mPager);
        }

        Handler handler = new Handler();
        Runnable updates = new Runnable() {
            @Override
            public void run() {
                if (currentPage == img.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage, true);
                currentPage++;
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updates);
            }
        }, 2500, 2500);
    }
}
