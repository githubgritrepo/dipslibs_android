package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Model.PortfolioModel;
import com.evo.mitzoom.R;

import java.util.ArrayList;

public class AdapterPortofolio extends RecyclerView.Adapter<AdapterPortofolio.ItemHolder> {
    private ArrayList<PortfolioModel> dataList;
    private Context ctx;

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
      holder.GambarItem.setImageResource(dataList.get(position).getGambarPortfolio());

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView NamaPortfolio;
        private TextView NominalPortfolio;
        private ImageView GambarItem;

        public ItemHolder(View itemView) {
            super(itemView);
            NamaPortfolio = itemView.findViewById(R.id.tv_nama_porto);
            NominalPortfolio = itemView.findViewById(R.id.tv_nominal_porto);
            GambarItem = itemView.findViewById(R.id.img_logo_porto);
        }
    }
}
