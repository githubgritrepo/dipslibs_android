package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.frag_wm_transactions;
import com.evo.mitzoom.R;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import us.zoom.sdk.ZoomVideoSDK;

public class ItemCatgProdMutualFund extends RecyclerView.Adapter<ItemCatgProdMutualFund.ItemHolder> {


    private final Context mContext;
    private final JSONArray dataItems;
    private final String dataRisk;
    private boolean isSessionZoom = false;

    public ItemCatgProdMutualFund(Context mContext, JSONArray dataItems, String dataRisk) {
        this.mContext = mContext;
        this.dataItems = dataItems;
        this.dataRisk = dataRisk;
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
    }

    @NonNull
    @Override
    public ItemCatgProdMutualFund.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_catgproduct, parent, false);
        return new ItemHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCatgProdMutualFund.ItemHolder holder, int position) {
        try {
            //String imgStreamStr = dataItems.getJSONObject(position).getString("imgStreamStr");
            String categoryId = "";
            String labelProd = dataItems.getJSONObject(position).getString("invesmentName");
            JSONArray dataCatgProdArr = dataItems.getJSONObject(position).getJSONArray("listProduct");
            //String categoryId = dataItems.getJSONObject(position).getString("id");
//            if (!imgStreamStr.isEmpty()) {
//                Bitmap bitmap = stringToBitmap(imgStreamStr);
//                holder.imgCatgProd.setImageBitmap(bitmap);
//            }
            holder.tvContent.setText(labelProd);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("labelserv",labelProd);
                    bundle.putString("categoryCode",categoryId);
                    bundle.putString("dataCatgProdArr",dataCatgProdArr.toString());
                    bundle.putString("dataRisk",dataRisk);
                    bundle.putInt("catg",2);
                    Fragment fragments = new frag_wm_transactions();
                    fragments.setArguments(bundle);
                    getFragmentPage(fragments);
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Bitmap stringToBitmap(String in){
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void getFragmentPage(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame2, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public int getItemCount() {
        return dataItems.length();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCatgProd;
        private final TextView tvContent;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            imgCatgProd = (ImageView) itemView.findViewById(R.id.imgCatgProd);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }
}
