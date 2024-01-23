package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterFailResi extends RecyclerView.Adapter<AdapterFailResi.ViewHolder> {

    private final Context mContet;
    private final JSONArray dataTrx;

    public AdapterFailResi(Context mContext, JSONArray dataTrxFail) {
        this.mContet = mContext;
        this.dataTrx = dataTrxFail;
    }

    @NonNull
    @Override
    public AdapterFailResi.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_not_resi_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFailResi.ViewHolder holder, int position) {
        try {
            int loopPos = position + 1;

            JSONObject dataObj = dataTrx.getJSONObject(position);

            if (dataObj.has("jenislayanan")) {
                if (!dataObj.isNull("jenislayanan")) {
                    String typeService = dataObj.getString("jenislayanan");
                    String labelService = "";
                    if (typeService.equals("ONLINE") || typeService.equals("RTGS") || typeService.equals("SKN")) {
                        labelService = loopPos + ". Antar Bank - " + typeService;
                    } else if (typeService.contains("private")) {
                        labelService = loopPos + ". Transaksi Sendiri";
                    } else {
                        labelService = loopPos + ". Antar Rekening";
                    }

                    holder.tvTitle.setText(labelService);
                }
            }

            if (dataObj.has("messageApprove")) {
                if (!dataObj.isNull("messageApprove")) {
                    String messageApprove = dataObj.getString("messageApprove");
                    holder.tvMessages.setText(messageApprove);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return dataTrx.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvMessages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvMessages = (TextView) itemView.findViewById(R.id.tvMessages);
        }
    }
}
