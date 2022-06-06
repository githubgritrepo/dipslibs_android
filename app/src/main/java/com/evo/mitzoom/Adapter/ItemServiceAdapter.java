package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.frag_rtgs;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;

import java.util.ArrayList;

public class ItemServiceAdapter extends RecyclerView.Adapter<ItemServiceAdapter.ItemHolder> {
    private ArrayList<ItemModel> dataList;
    private Context ctx;


    public ItemServiceAdapter(Context ctx, ArrayList<ItemModel> dataList){
        this.dataList = dataList;
        this.ctx = ctx;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.NamaItem.setText(dataList.get(position).getNamaItem());
        holder.GambarItem.setImageResource(dataList.get(position).getGambarItem());
        holder.parent_layout.setOnClickListener(v -> {
            switch (dataList.get(position).getId()){
                case "1" :
                    getFragmentPage(new frag_rtgs());
                    return;
                case "2" :
                    Toast.makeText(ctx, R.string.SKN, Toast.LENGTH_SHORT).show();
                    return;
                case "3" :
                    Toast.makeText(ctx, R.string.FOREX, Toast.LENGTH_SHORT).show();
                    return;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }
    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView NamaItem;
        private ImageView GambarItem;
        private CardView parent_layout;

        public ItemHolder(View itemView) {
            super(itemView);
            NamaItem = itemView.findViewById(R.id.nama_item);
            GambarItem = itemView.findViewById(R.id.images_item);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)ctx).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}
