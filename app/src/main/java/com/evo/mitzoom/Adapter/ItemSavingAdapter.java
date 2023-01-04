package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;

import org.json.JSONArray;
import org.json.JSONException;

public class ItemSavingAdapter extends RecyclerView.Adapter<ItemSavingAdapter.ViewHolder>{

    private JSONArray dataList;
    private Context mContext;

    public ItemSavingAdapter(Context mContext,JSONArray dataList) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tabungan,parent,false);
        return new ViewHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String nameProduct = dataList.getJSONObject(position).getString("name");
            holder.tvTitleList.setText(nameProduct);

            holder.llProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProd;
        private final TextView tvTitleList;
        private final LinearLayout llProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llProduct = (LinearLayout) itemView.findViewById(R.id.llProduct);
            imgProd = (ImageView) itemView.findViewById(R.id.imgProd);
            tvTitleList = (TextView) itemView.findViewById(R.id.tvTitleList);
        }
    }
}
