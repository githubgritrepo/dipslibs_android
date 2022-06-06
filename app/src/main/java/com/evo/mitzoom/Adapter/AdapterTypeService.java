package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evo.mitzoom.Model.TypeServiceItem;
import com.evo.mitzoom.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterTypeService extends ArrayAdapter<TypeServiceItem> {
    private List<TypeServiceItem> typeServiceItemList;

    public AdapterTypeService (@NonNull Context context, @NonNull List<TypeServiceItem> typeServiceItems){
        super(context,0,typeServiceItems);
        typeServiceItemList = new ArrayList<>(typeServiceItems);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return typeServiceFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.type_service_autocomplete_row, parent, false
            );
        }

        TextView textViewHeadline = convertView.findViewById(R.id.tv_headline);
        TextView textViewContent = convertView.findViewById(R.id.tv_content);

        TypeServiceItem ts = getItem(position);

        if (ts != null) {
            textViewHeadline.setText(ts.getHeadline());
            textViewContent.setText(ts.getContent());
        }

        return convertView;
    }

    private Filter typeServiceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<TypeServiceItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(typeServiceItemList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (TypeServiceItem item : typeServiceItemList) {
                    if (item.getHeadline().toLowerCase().contains(filterPattern)) {
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
            return ((TypeServiceItem) resultValue).getHeadline();
        }
    };



}
