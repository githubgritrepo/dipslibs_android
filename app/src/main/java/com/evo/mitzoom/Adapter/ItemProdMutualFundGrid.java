package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.os.Bundle;
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

import com.evo.mitzoom.Fragments.frag_wm_detail_porto;
import com.evo.mitzoom.Fragments.frag_wm_detail_product;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import us.zoom.sdk.ZoomVideoSDK;

public class ItemProdMutualFundGrid extends RecyclerView.Adapter<ItemProdMutualFundGrid.ItemHolder> {

    private final Context mContext;
    private final JSONArray filteredList;
    private final SessionManager sessions;
    private final boolean isSessionZoom;
    private final String labelserv;
    private final int catgServ;

    public ItemProdMutualFundGrid(Context mContext, JSONArray dataItems, String labelserv, int catg) {
        this.mContext = mContext;
        this.filteredList = dataItems;
        this.sessions = new SessionManager(mContext);
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        this.labelserv = labelserv;
        this.catgServ = catg;
        ConnectionRabbitHttp.init(mContext);
    }

    @NonNull
    @Override
    public ItemProdMutualFundGrid.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prod_reksadana, parent, false);
        return new ItemHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemProdMutualFundGrid.ItemHolder holder, int position) {
        try {
            JSONObject dataObj = filteredList.getJSONObject(position);
            if (dataObj.has("productName")) {
                String productName = dataObj.getString("productName");
                holder.tvTitleProd.setText(productName);
            }
            if (dataObj.has("fundHouseName")) {
                String fundHouseName = dataObj.getString("fundHouseName");
                holder.tvPtProd.setText(fundHouseName);
            }
            if (dataObj.has("navDate")) {
                String navDate = dataObj.getString("navDate");
                if (navDate.contains("T")) {
                    String[] sp = navDate.split("T");
                    String dates = sp[0];
                    String inputFormat = "yyyy-MM-dd";
                    String outputFormat = "dd/MM/yy";
                    String NABDate = ConveredDate(dates, inputFormat, outputFormat);
                    holder.tvNABDate.setText(NABDate);
                }
            }

            long nomNav = 0;
            if (dataObj.has("nav")) {
                nomNav = dataObj.getLong("nav");

                NumberFormat formatter = null;
                if (sessions.getLANG().equals("id")) {
                    formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                } else {
                    formatter = NumberFormat.getInstance(new Locale("en", "US"));
                }
                formatter.setMinimumFractionDigits(2);
                String formatted = formatter.format(nomNav);

                String nominal = "";
                if (sessions.getLANG().equals("id")) {
                    nominal = "Rp. " + formatted;
                } else {
                    String currency = "USD";
                    if (dataObj.has("currency")) {
                        currency = dataObj.getString("currency");
                    }
                    nominal = currency + " " + formatted;
                }
                
                if (catgServ == 2) {
                    holder.tvNominal.setText(nominal);
                } else if (catgServ == 3) {
                    String getTxtNAB = holder.tvNAB.getText().toString();
                    int indexNAB = getTxtNAB.indexOf("NAB");
                    String txtNAB = getTxtNAB.substring(indexNAB,3).trim();
                    String perTxtDate = getTxtNAB.substring(indexNAB+3).trim();
                    String newTxtNAB = txtNAB + " " + nominal + " " + perTxtDate;
                    holder.tvNAB.setText(newTxtNAB);

                    if (dataObj.has("units")) {
                        long units = dataObj.getLong("units");
                        double diffUnits = (double) units / 10000;

                        formatter.setMinimumFractionDigits(2);
                        String formattDiffUnits = formatter.format(diffUnits);

                        String txtUnits = formattDiffUnits + " Unit";
                        holder.tvNominal.setText(txtUnits);
                    }
                }
            }

            long navunit = 0;
            if (dataObj.has("navPerUnit")) {
                long navPerUnit = dataObj.getLong("navPerUnit");
                navunit = navPerUnit / 1000000;
            }

            String imbalHasil;
            if (catgServ == 2) {
                if (dataObj.has("subsFee")) {
                    double subsFee = dataObj.getDouble("subsFee");
                    imbalHasil = "0% /Thn";
                    if (sessions.getLANG().equals("id")) {
                        imbalHasil = subsFee + "% /Thn";
                    } else {
                        imbalHasil = subsFee + "% /Years";
                    }
                    if (imbalHasil.charAt(0) == '-') {
                        holder.imgImbalHasil.setImageDrawable(mContext.getResources().getDrawable(R.drawable.downred));
                    }
                    holder.tvPercent.setText(imbalHasil);
                }
            } else if (catgServ == 3) {
                double diffImbal = (double) (nomNav - navunit) / navunit;

                NumberFormat formatter = null;
                if (sessions.getLANG().equals("id")) {
                    formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                } else {
                    formatter = NumberFormat.getInstance(new Locale("en", "US"));
                }
                formatter.setMinimumFractionDigits(2);
                String formattedNumber = formatter.format(diffImbal);

                imbalHasil = formattedNumber + "%";

                if (imbalHasil.charAt(0) == '-') {
                    holder.imgImbalHasil.setImageDrawable(mContext.getResources().getDrawable(R.drawable.downred));
                    holder.tvPercent.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                }

                holder.tvPercent.setTextColor(mContext.getResources().getColor(R.color.button_schedule));
                holder.tvLabaRugi.setVisibility(View.VISIBLE);
                holder.tvPercent.setText(imbalHasil);
            }

            if (catgServ == 2) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("dataObjProd", dataObj.toString());
                        bundle.putString("labelserv", labelserv);
                        Fragment fragment = new frag_wm_detail_product();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    }
                });
            } else if (catgServ == 3) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("dataObjProd", dataObj.toString());
                        Fragment fragment = new frag_wm_detail_porto();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    }
                });
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.length();
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

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitleProd;
        private final TextView tvPtProd;
        private final TextView tvPercent;
        private final TextView tvLabaRugi;
        private final TextView tvNABDate;
        private final TextView tvNominal;
        private final ImageView imgImbalHasil;
        private final TextView tvNAB;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            tvTitleProd = (TextView) itemView.findViewById(R.id.tvTitleProd);
            tvPtProd = (TextView) itemView.findViewById(R.id.tvPtProd);
            tvPercent = (TextView) itemView.findViewById(R.id.tvPercent);
            imgImbalHasil = (ImageView) itemView.findViewById(R.id.imgImbalHasil);
            tvNAB = (TextView) itemView.findViewById(R.id.tvNAB);
            tvLabaRugi = (TextView) itemView.findViewById(R.id.tvLabaRugi);
            tvNABDate = (TextView) itemView.findViewById(R.id.tvNABDate);
            tvNominal = (TextView) itemView.findViewById(R.id.tvNominal);

        }
    }
}
