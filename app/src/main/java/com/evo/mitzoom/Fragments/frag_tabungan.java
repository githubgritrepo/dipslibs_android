package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;

import java.nio.charset.StandardCharsets;

public class frag_tabungan extends Fragment {
    private Context context;
    private ImageView btnBack;
    private TextView Headline, tnc;
    private Button btnCreateAccount;
    private String headline;
    private LayoutInflater inflater;
    private View dialogView;
    private NestedScrollView nestedScrollView;
    private TextView tvBody;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_tabungan, container, false);
        btnBack = view.findViewById(R.id.btn_back3);
        Headline = view.findViewById(R.id.nama_tabungan);
        nestedScrollView = view.findViewById(R.id.Nested);
        tvBody = (TextView) view.findViewById(R.id.tvBody);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arg = getArguments();
        headline = arg.getString("headline");
        if (arg.containsKey("body")){
            byte[] bodyByte = arg.getByteArray("body");
            String strBody = new String(bodyByte, StandardCharsets.UTF_8);
            tvBody.setText(Html.fromHtml(strBody, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int idx = source.indexOf(",");
                    idx += 1;
                    String new_source = source.substring(idx);
                    byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Drawable d = new BitmapDrawable(((Activity) context).getResources(), bitmap);
                    int intH = d.getIntrinsicHeight();
                    int intW = d.getIntrinsicWidth();
                    d.setBounds(0, 0, intW, intH);
                    return d;
                }
            }, null));
        }
        Headline.setText(headline);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_list_produk());
            }
        });
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
