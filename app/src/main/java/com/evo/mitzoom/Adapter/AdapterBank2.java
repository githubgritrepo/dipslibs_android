package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evo.mitzoom.Model.BankItem;
import com.evo.mitzoom.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterBank2 extends ArrayAdapter<BankItem> {
    private List<BankItem> bankItemList;

    public AdapterBank2(@NonNull Context context, @NonNull List<BankItem> bankList ){
        super(context, 0, bankList);
        bankItemList = new ArrayList<>(bankList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return bankFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bank_autocomplete_row, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        ImageView imageViewFlag = convertView.findViewById(R.id.image_view_flag);

        BankItem bankItem = getItem(position);

        if (bankItem != null) {
            textViewName.setText(bankItem.getBankName());
            imageViewFlag.setImageResource(bankItem.getFlagImage());
        }

        return convertView;
    }

    private Filter bankFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<BankItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(bankItemList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BankItem item : bankItemList) {
                    if (item.getBankName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((BankItem) resultValue).getBankName();
        }
    };

}
