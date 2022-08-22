package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Model.FileModel;
import com.evo.mitzoom.R;

import java.util.ArrayList;

public class AdapterFile extends RecyclerView.Adapter<AdapterFile.ItemHolder> {
    private ArrayList<FileModel> dataList;
    private Context ctx;

    public AdapterFile(Context ctx, ArrayList<FileModel> dataList){
        this.dataList = dataList;
        this.ctx = ctx;
    }

    @Override
    public AdapterFile.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterFile.ItemHolder holder, int position) {
        holder.Nama_File.setText(dataList.get(position).getNamaFile());
        holder.item.setBackgroundColor(ctx.getResources().getColor(dataList.get(position).getColorItem()));
        holder.ic_unduh_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Download File "+holder.Nama_File.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView Nama_File;
        private ImageView ic_unduh_;
        private LinearLayout item;

        public ItemHolder(View itemView) {
            super(itemView);
            Nama_File = itemView.findViewById(R.id.nama_file);
            ic_unduh_ = itemView.findViewById(R.id.icon_unduh);
            item = itemView.findViewById(R.id.item_file_bg);
        }
    }
}
