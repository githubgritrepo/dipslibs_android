package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.evo.mitzoom.R;

import java.util.ArrayList;

public class AdapterSlide extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context mContext;

    public AdapterSlide(ArrayList<Integer> images, Context mContext) {
        this.images = images;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide,container,false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.images);
        myImage.setImageResource(images.get(position));
        container.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
