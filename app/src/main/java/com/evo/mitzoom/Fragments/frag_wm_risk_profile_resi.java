package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.RatingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wm_risk_profile_resi extends Fragment {

    private static final String TAG = "frag_wealth_management";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private RecyclerView rv_itemView;
    private Button btnOK;
    private String labelserv = "";
    private LinearLayoutManager recylerViewLayoutManager;
    private AdapterItemView recyclerViewAdapter;
    private JSONArray idElement = null;
    private boolean flagSuccess = false;
    private String statusSID = "";
    private final JSONObject mirrObj = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("successs")) {
                flagSuccess = getArguments().getBoolean("successs");
            }
            if (getArguments().containsKey("statusSID")) {
                statusSID = getArguments().getString("statusSID");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            if (getArguments().containsKey("idElement")) {
                String getidElement = getArguments().getString("idElement");

                try {
                    idElement = new JSONArray(getidElement);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String dataNasabah = sessions.getNasabah();
        ConnectionRabbitHttp.init(mContext);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_risk_profile_resi, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        rv_itemView = (RecyclerView) views.findViewById(R.id.rv_itemView);

        btnOK = (Button) views.findViewById(R.id.btnOK);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConnectionRabbitHttp.mirroringEndpoint(292);

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        try {
            if (isSessionZoom) {
                String alertS = "berhasil";
                if (!statusSID.isEmpty()) {
                    alertS = "proses";
                }
                JSONObject reqFormMirroring = new JSONObject();
                reqFormMirroring.put("alert",alertS);
                JSONObject resisid = new JSONObject();
                resisid.put("resisid",reqFormMirroring);
                mirrObj.put("reksadana", resisid);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        dialogInfo();

        if (idElement != null) {
            setRecyler();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("idTrx",18);
                bundle.putString("subLayanan",getString(R.string.wealth_transactions));
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringEndpoint(15);
                }
                Fragment fragment = new frag_service_new();
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });

        btnOK.setEnabled(true);
        btnOK.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("idTrx",18);
                bundle.putString("subLayanan",getString(R.string.wealth_transactions));
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringEndpoint(280);
                }
                Fragment fragment = new frag_service_new();
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });

    }

    private void setRecyler() {
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_itemView.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new AdapterItemView();
        rv_itemView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private class AdapterItemView extends RecyclerView.Adapter<AdapterItemView.ViewHolder> {

        @NonNull
        @Override
        public AdapterItemView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_confirmview, parent, false);
            return new AdapterItemView.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterItemView.ViewHolder holder, int position) {
            try {
                String valEl = idElement.getJSONObject(position).getString("value");
                String labelEl = idElement.getJSONObject(position).getString("label");
                String name = idElement.getJSONObject(position).getString("name");
                holder.tvLabelElement.setText(labelEl);
                if (labelEl.toLowerCase().contains("profil") || labelEl.toLowerCase().contains("profile")) {
                    valEl = valEl.toUpperCase();
                }
                holder.tvContentElement.setText(valEl);

                JSONObject resisid = new JSONObject(mirrObj.getJSONObject("reksadana").toString());
                JSONObject reqFormMirroring = new JSONObject(resisid.getJSONObject("resisid").toString());
                reqFormMirroring.put(name,valEl);
                resisid.put("resisid",reqFormMirroring);
                mirrObj.put("reksadana", resisid);

                if (isSessionZoom && position == idElement.length() - 1) {
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return idElement.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvLabelElement;
            private final TextView tvContentElement;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvLabelElement = (TextView) itemView.findViewById(R.id.tvLabelElement);
                tvContentElement = (TextView) itemView.findViewById(R.id.tvContentElement);

                tvLabelElement.setTypeface(null, Typeface.NORMAL);
                tvContentElement.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    private void dialogInfo() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_success));
        if (statusSID.isEmpty() || statusSID.equals("request")) {
            tvTitleDialog.setText("Pendaftaran Berhasil.");
        } else {
            tvTitleDialog.setVisibility(View.GONE);
        }
        tvBodyDialog.setText("SID & IFUA Anda sedang dalam proses.");

        SweetAlertDialog dialogEnd = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        dialogEnd.setCustomView(dialogView);
        dialogEnd.setCancelable(false);
        dialogEnd.hideConfirmButton();
        dialogEnd.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEnd.cancel();
                dialogEnd.dismissWithAnimation();

                try {
                    JSONObject resisid = new JSONObject(mirrObj.getJSONObject("reksadana").toString());
                    JSONObject reqFormMirroring = new JSONObject(resisid.getJSONObject("resisid").toString());
                    reqFormMirroring.remove("alert");
                    resisid.put("resisid",reqFormMirroring);
                    mirrObj.put("reksadana", resisid);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        if (isSessionZoom) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame2, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

}