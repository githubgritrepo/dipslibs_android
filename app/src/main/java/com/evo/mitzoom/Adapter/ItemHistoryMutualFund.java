package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.frag_wm_trans_resi;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import us.zoom.sdk.ZoomVideoSDK;

public class ItemHistoryMutualFund extends RecyclerView.Adapter<ItemHistoryMutualFund.ItemHolder> {

    private final Context mContext;
    private final JSONArray filteredList;
    private final SessionManager sessions;
    private final boolean isSessionZoom;

    public ItemHistoryMutualFund(Context mContext, JSONArray dataItems) {
        this.mContext = mContext;
        this.filteredList = dataItems;
        this.sessions = new SessionManager(mContext);
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        ConnectionRabbitHttp.init(mContext);
    }

    @NonNull
    @Override
    public ItemHistoryMutualFund.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wm_history, parent, false);
        return new ItemHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHistoryMutualFund.ItemHolder holder, int position) {
        try {
            JSONObject dataObj = filteredList.getJSONObject(position);
            if (dataObj.has("idCategory")) {
                String idCategory = dataObj.getString("idCategory");
                if (idCategory.equals("SUB")) {
                    String txt = mContext.getResources().getString(R.string.purchase).toUpperCase();
                    holder.tvTypeWM.setText(txt);
                } else if (idCategory.equals("RED")) {
                    String txt = mContext.getResources().getString(R.string.penjualan).toUpperCase();
                    holder.tvTypeWM.setText(txt);
                }
            }

            if (dataObj.has("transactionDate")) {
                String transactionDate = dataObj.getString("transactionDate");
                if (transactionDate.contains("T")) {
                    String[] sp = transactionDate.split("T");
                    String dates = sp[0].trim();
                    String times = sp[1].trim();
                    if (times.contains(".")) {
                        int indx = times.indexOf(".");
                        times = times.substring(0,indx);
                    }

                    String inputFormat = "yyyy-MM-dd";
                    String outputFormat = "dd/MM/yy";
                    String newDates = ConveredDate(dates, inputFormat, outputFormat);

                    transactionDate = times+" "+newDates;
                }

                holder.tvDateTimes.setText(transactionDate);
            }

            if (dataObj.has("productName")) {
                String productName = dataObj.getString("productName");
                holder.tvTitleProd.setText(productName);
            }

            if (dataObj.has("subType")) {
                String subType = dataObj.getString("subType");
                holder.tvPtProd.setText(subType);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String idForm = "";
                        if (dataObj.has("referenceNo")) {
                            idForm = dataObj.getString("referenceNo");
                        }

                        String idCategory = "";
                        if (dataObj.has("idCategory")) {
                            idCategory = dataObj.getString("idCategory");
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("idForm", idForm);
                        bundle.putString("dataObj", dataObj.toString());

                        if (idCategory.equals("SUB")) {
                            bundle.putString("typeTransaction", "sub");
                            ConnectionRabbitHttp.mirroringEndpoint(311);
                        } else if (idCategory.equals("RED")) {
                            bundle.putString("typeTransaction", "red");
                            ConnectionRabbitHttp.mirroringEndpoint(312);
                        }

                        Fragment fragment = new frag_wm_trans_resi();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.length();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvTypeWM;
        private final TextView tvDateTimes;
        private final TextView tvTitleProd;
        private final TextView tvPtProd;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            tvTypeWM = (TextView) itemView.findViewById(R.id.tvTypeWM);
            tvDateTimes = (TextView) itemView.findViewById(R.id.tvDateTimes);
            tvTitleProd = (TextView) itemView.findViewById(R.id.tvTitleProd);
            tvPtProd = (TextView) itemView.findViewById(R.id.tvPtProd);

        }
    }

    private String ConveredDate(String dataDate, String InputFormat, String OutputFormat) {
        DateFormat inputFormat = new SimpleDateFormat(InputFormat);
        SimpleDateFormat d= new SimpleDateFormat(OutputFormat);
        try {
            Date convertedDate = inputFormat.parse(dataDate);
            String datetime = d.format(convertedDate);
            return datetime;
        }catch (ParseException e) {

        }
        return null;
    }

    private void getFragmentPageBackStack(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack("WMHistory")
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("WMHistory")
                    .commit();
        }

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


}
