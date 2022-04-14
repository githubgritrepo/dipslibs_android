package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.evo.mitzoom.Fragments.frag_list_produk;
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
        TextView nama = myImageLayout.findViewById(R.id.nama_item_caroseoul);
        if (position == 0){
        }
        else if (position == 1){
            nama.setText(R.string.create_new_account);
        }
        else if (position == 2){
            nama.setText(R.string.credit_simulation);
        }
        else {

        }
        myImage.setImageResource(images.get(position));
        container.addView(myImageLayout, 0);
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0 :
                        return;
                    case 1:
                        getFragmentPage(new frag_list_produk());
                        return;
                    case 2:
                        Toast.makeText(mContext, R.string.credit_simulation, Toast.LENGTH_SHORT).show();
                        return;

                }
            }
        });
        return myImageLayout;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)mContext).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
