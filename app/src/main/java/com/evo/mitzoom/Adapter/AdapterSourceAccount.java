package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evo.mitzoom.Model.BankItem;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterSourceAccount extends ArrayAdapter<FormSpin> {

    private final Context mContext;
    private String[] sourceAcc;
    private final ArrayList<FormSpin> sourceAccForm;

    public AdapterSourceAccount(@NonNull Context context, int resource, ArrayList<FormSpin> sourceAccForm) {
        super(context, resource, sourceAccForm);
        this.mContext = context;
        //this.sourceAcc = objects;
        this.sourceAccForm = sourceAccForm;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public FormSpin getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dropdown_multiline, parent, false);

        TextView tvContent = view.findViewById(R.id.tvContent);
        String dataAcc = sourceAccForm.get(position).getName();
        if (dataAcc.contains("\n")) {
            String[] strings = dataAcc.split("\\r?\\n");
            String titleAcc = strings[0] + "\n";
            String acc = strings[1] + "\n";
            String nominal = "";
            if (strings.length > 2) {
                nominal = strings[2];
            }

            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (nominal.isEmpty()) {
                acc = acc.replace("\n", "");
                builder.append(titleAcc).append(acc);
            } else {
                builder.append(titleAcc).append(acc).append(nominal);
            }
            builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleAcc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
            tvContent.setText(builder);
        } else {
            tvContent.setText(dataAcc);
        }

        return view;
    }
}
