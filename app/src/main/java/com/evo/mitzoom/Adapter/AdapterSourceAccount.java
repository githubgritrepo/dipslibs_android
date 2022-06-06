package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evo.mitzoom.R;

public class AdapterSourceAccount extends ArrayAdapter<String> {

    private Context mContext;
    private String[] sourceAcc;

    public AdapterSourceAccount(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.sourceAcc = objects;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_souceacc, parent, false);
        String dataAcc = sourceAcc[position];
        String[] strings = dataAcc.split("\\r?\\n");
        String titleAcc = strings[0]+"\n";
        String acc = strings[1]+"\n";
        String nominal = strings[2];

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(titleAcc).append(acc).append(nominal);
        builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleAcc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold

        TextView tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText(builder);
        if (position == 0) {
            tvContent.setBackgroundColor(mContext.getResources().getColor(R.color.blue_btn_bg));
        } else if (position % 2 == 0) {
            tvContent.setBackgroundColor(mContext.getResources().getColor(R.color.blue_btn_bg));
        }

        return view;
    }
}
