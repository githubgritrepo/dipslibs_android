package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.GlideApp;
import com.evo.mitzoom.Model.PortfolioModel;
import com.evo.mitzoom.R;

import java.util.ArrayList;

public class AdapterPortofolio extends RecyclerView.Adapter<AdapterPortofolio.ItemHolder> {
    private final ArrayList<PortfolioModel> dataList;
    private final Context ctx;

    public AdapterPortofolio(Context ctx, ArrayList<PortfolioModel> dataList){
        this.dataList = dataList;
        this.ctx = ctx;
    }

    @Override
    public AdapterPortofolio.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterPortofolio.ItemHolder holder, int position) {
      holder.NamaPortfolio.setText(dataList.get(position).getNamaPortfolio());
      holder.NominalPortfolio.setText(dataList.get(position).getNominalPortfolio());
      //holder.GambarItem.setImageResource(dataList.get(position).getGambarPortfolio());
      String linkIcon = dataList.get(position).getLinkIcon();

      GlideApp.with(ctx)
            .load(linkIcon)
            .placeholder(R.drawable.porto1)
            .into(holder.GambarItem);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private final TextView NamaPortfolio;
        private final TextView NominalPortfolio;
        private final ImageView GambarItem;

        public ItemHolder(View itemView) {
            super(itemView);
            NamaPortfolio = itemView.findViewById(R.id.tv_nama_porto);
            NominalPortfolio = itemView.findViewById(R.id.tv_nominal_porto);
            GambarItem = itemView.findViewById(R.id.img_logo_porto);
        }
    }
}
