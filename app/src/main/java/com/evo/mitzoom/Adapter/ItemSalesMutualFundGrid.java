package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import us.zoom.sdk.ZoomVideoSDK;

public class ItemSalesMutualFundGrid extends RecyclerView.Adapter<ItemSalesMutualFundGrid.ItemHolder> {

    private final Context mContext;
    private final JSONArray dataItems;
    private final SessionManager sessions;
    private final boolean isSessionZoom;
    private final int catgServ;
    private final String labelserv;

    public ItemSalesMutualFundGrid(Context mContext, JSONArray dataItems, String labelserv, int catg) {
        this.mContext = mContext;
        this.dataItems = dataItems;
        this.sessions = new SessionManager(mContext);
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        this.labelserv = labelserv;
        this.catgServ = catg;
    }

    @NonNull
    @Override
    public ItemSalesMutualFundGrid.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wm_sales, parent, false);
        return new ItemHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSalesMutualFundGrid.ItemHolder holder, int position) {
        try {
            JSONObject dataObj = dataItems.getJSONObject(position);
            String productCode = dataObj.getString("productCode");
            holder.tvTitleExpandPasar.setText(productCode);
            
            if (dataObj.has("investmentAccountList")) {
                JSONArray investmentAccountList = dataObj.getJSONArray("investmentAccountList");
                JSONArray invesAccList = new JSONArray();
                for (int ij = 0; ij < investmentAccountList.length(); ij++) {
                    JSONObject dataItemObj = new JSONObject(investmentAccountList.getJSONObject(ij).toString());

                    for(Iterator<String> iter = dataObj.keys(); iter.hasNext();) {
                        if (iter.hasNext()) {
                            String key = iter.next();
                            if (!key.equals("investmentAccountList")) {
                                if (dataObj.has(key) && !dataObj.isNull(key)) {
                                    dataItemObj.put(key, dataObj.get(key));
                                }
                            }
                        }
                    }

                    invesAccList.put(dataItemObj);
                }

                setRecylerProd(holder,invesAccList);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return dataItems.length();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitleExpandPasar;
        private final RecyclerView rv_itemProdPasar;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            tvTitleExpandPasar = (TextView) itemView.findViewById(R.id.tvTitleExpandPasar);
            rv_itemProdPasar = (RecyclerView) itemView.findViewById(R.id.rv_itemProdPasar);

        }
    }

    private void setRecylerProd(ItemHolder holder, JSONArray investmentAccountList) {
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);
        holder.rv_itemProdPasar.setLayoutManager(recylerViewLayoutManager);

        ItemProdMutualFundGrid recyclerProdViewAdapter = new ItemProdMutualFundGrid(mContext, investmentAccountList, labelserv, catgServ);
        holder.rv_itemProdPasar.setAdapter(recyclerProdViewAdapter);
        recyclerProdViewAdapter.notifyDataSetChanged();
    }
    
}
