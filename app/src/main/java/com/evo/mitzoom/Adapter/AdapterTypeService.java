package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evo.mitzoom.R;

public class AdapterTypeService extends ArrayAdapter<String> {

    private Context mContext;
    private String[] sources;

    public AdapterTypeService(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.sources = objects;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item3, parent, false);

        TextView tvContent = (TextView) view.findViewById(R.id.tvContent);

        String datax = sources[position];

        tvContent.setText(datax);

        return view;
    }
}
