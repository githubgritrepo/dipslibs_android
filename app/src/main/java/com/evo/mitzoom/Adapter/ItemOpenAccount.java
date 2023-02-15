package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.util.ArrayList;

public class ItemOpenAccount extends RecyclerView.Adapter<ItemOpenAccount.ItemHolder>{

    private final RabbitMirroring rabbitMirroring;
    private final ArrayList<ItemModel> dataList;
    private final Context mContext;
    private SessionManager sessions;
    private String idDips;
    private Bundle bundle;
    private Fragment fragment;

    public ItemOpenAccount(ArrayList<ItemModel> dataList, Context mContext, RabbitMirroring rabbitMirroring) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.rabbitMirroring = rabbitMirroring;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid2, parent, false);
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        return new ItemHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.tvLabelItem.setVisibility(View.VISIBLE);
        holder.tvLabelItem.setText(dataList.get(position).getNamaItem());
        holder.ads.setImageResource(dataList.get(position).getGambarItem());

        if (position == 0) {
            holder.cvItems.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
            holder.tvLabelItem.setTextColor(mContext.getColor(R.color.white));
        }

        holder.cvItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (position == 0) {
                    holder.cvItems.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    holder.tvLabelItem.setTextColor(mContext.getColor(R.color.white));
                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView tvLabelItem;
        private final ImageView ads;
        private final CardView cvItems;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            cvItems = itemView.findViewById(R.id.cvItems);
            ads = itemView.findViewById(R.id.ads);
            tvLabelItem = itemView.findViewById(R.id.tvLabelItem);

        }
    }
}
