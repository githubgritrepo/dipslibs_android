package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.frag_deposito_confirm_aro;
import com.evo.mitzoom.Fragments.frag_deposito_online_aro;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.zoom.sdk.ZoomVideoSDK;

public class AdapterDeposit extends RecyclerView.Adapter<AdapterDeposit.ItemHolder> {

    private final Context mContext;
    private final SessionManager sessions;
    private final String idService;
    private final boolean isSessionZoom;
    private Fragment fragment;
    private final JSONArray dataArr;

    public AdapterDeposit(Context mContext, Fragment fragment, JSONArray dataArr, String idService) {
        this.mContext = mContext;
        this.fragment = fragment;
        this.dataArr = dataArr;
        this.idService = idService;
        this.isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        ConnectionRabbitHttp.init(mContext);

        sessions = new SessionManager(mContext);
    }

    @NonNull
    @Override
    public AdapterDeposit.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_deposit, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeposit.ItemHolder holder, int position) {
        try {
            JSONObject dataObj = dataArr.getJSONObject(position);
            String noDeposit = "";
            String dueDate = "";
            String nominal = "";
            String flagAro = "";
            String timePeriod = "";
            if (dataObj.has("depositoNo")) {
                noDeposit = dataObj.getString("depositoNo");
            }
            if (dataObj.has("dueDate")) {
                dueDate = dataObj.getString("dueDate");
            } else if (dataObj.has("tanggalJTempo")) {
                dueDate = dataObj.getString("tanggalJTempo");
            }
            if (dataObj.has("nominal")) {
                nominal = dataObj.getString("nominal");
            }
            /*if (dataObj.has("flagAro")) {
                flagAro = dataObj.getString("flagAro");
            }*/
            /*if (dataObj.has("timePeriod")) {
                timePeriod = dataObj.getString("timePeriod");
            }*/
            String labelAro = "";
            String currency = "Rp.";
            String jatuhTempo = "";

            /*if (flagAro.equals("1")) {
                holder.rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            }*/

            /*if (sessions.getLANG().equals("en") && timePeriod.toLowerCase().indexOf("bulan") > 0) {
                timePeriod = timePeriod.replace("bulan","months").replace("Bulan","Months").replace("BULAN","MONTHS");
            }*/

            if (noDeposit.equals("null")) {
                noDeposit = "";
            }

            if (dataObj.has("depositType")) {
                if (!dataObj.isNull("depositType")) {
                    JSONObject depositTypeObj = dataObj.getJSONObject("depositType");
                    currency = depositTypeObj.getString("currency");
                    timePeriod = depositTypeObj.getString("months");
                } else if (dataObj.has("jangkaWaktu")) {
                    String jangkaWaktu = dataObj.getString("jangkaWaktu");
                    int timePeriodS = Integer.parseInt(jangkaWaktu);
                    if (sessions.getLANG().equals("id")) {
                        timePeriod = timePeriodS+" Bulan";
                    } else {
                        if (timePeriodS > 1) {
                            timePeriod = timePeriodS + " Months";
                        } else {
                            timePeriod = timePeriodS + " Month";
                        }
                    }
                }
            } else {
                if (dataObj.has("mataUang")) {
                    currency = dataObj.getString("mataUang");
                }
                if (dataObj.has("jangkaWaktu")) {
                    String jangkaWaktu = dataObj.getString("jangkaWaktu");
                    int timePeriodS = Integer.parseInt(jangkaWaktu);
                    if (sessions.getLANG().equals("id")) {
                        timePeriod = timePeriodS+" Bulan";
                    } else {
                        if (timePeriodS > 1) {
                            timePeriod = timePeriodS + " Months";
                        } else {
                            timePeriod = timePeriodS + " Month";
                        }
                    }
                }
            }

            if (dataObj.has("flagAro")) {
                JSONObject flagAroObj = dataObj.getJSONObject("flagAro");
                String valueCode = flagAroObj.getString("valueCode");
                if (valueCode.equals("1")) {
                    holder.rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_depo1));
                } else if (valueCode.equals("2")) {
                    holder.rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_depo2));
                } else if (valueCode.equals("3")) {
                    holder.rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_depo3));
                }
                labelAro = flagAroObj.getString("labelIdn");
                if (sessions.getLANG().equals("en")) {
                    labelAro = flagAroObj.getString("labelEng");
                }
                labelAro = labelAro.replace("(","").replace(")","");
            }

            String titleCard = noDeposit+" ("+labelAro+")";
            //nominal = currency+" "+nominal;

            if (!dueDate.isEmpty()) {
                if (dueDate.indexOf("-") > 0) {
                    String[] sp = dueDate.split("-");
                    String thn = sp[0].trim();
                    String bln = sp[1].trim();
                    String tgl = sp[2].trim();

                    String bulan = fullMonths(bln);
                    jatuhTempo = tgl+" "+bulan+ " "+thn;
                }
            }

            holder.tvTitle.setText(titleCard);
            holder.tvValueJangka.setText(timePeriod);
            holder.tvValueTempo.setText(jatuhTempo);
            holder.tvNominal.setText(nominal);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (!idService.equals("194")) {
                        fragment = new frag_deposito_confirm_aro();
                        bundle.putString("idService", idService);
                        String labelserv = mContext.getResources().getString(R.string.pencairan_deposito);
                        if (idService.equals("193")) {
                            labelserv = mContext.getResources().getString(R.string.perubahan_aro_non_aro);
                            //ConnectionRabbitHttp.mirroringEndpoint(212);
                        } else if (idService.equals("192")) {
                            //ConnectionRabbitHttp.mirroringEndpoint(211);
                        }
                        bundle.putString("labelserv", labelserv);
                        bundle.putString("data", dataObj.toString());
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    } else {
                        String labelserv = mContext.getResources().getString(R.string.e_bilyet_deposito);
                        bundle.putBoolean("resi", true);
                        bundle.putString("labelserv", labelserv);
                        bundle.putString("data", dataObj.toString());
                        fragment = new frag_deposito_online_aro();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    }
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
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

    private void getFragmentPageBackStack(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack("FragDeposit")
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("FragDeposit")
                    .commit();
        }

    }

    private String fullMonths(String bln) {
        switch (bln) {
            case "01":
                return "Januari";
            case "02":
                return "Februari";
            case "03":
                return "Maret";
            case "04":
                return "April";
            case "05":
                return "Mei";
            case "06":
                return "Juni";
            case "07":
                return "Juli";
            case "08":
                return "Agustus";
            case "09":
                return "September";
            case "10":
                return "Oktober";
            case "11":
                return "November";
            case "12":
                return "Desember";
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return dataArr.length();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rlHead;
        private final TextView tvTitle;
        private final ImageView imgPencil;
        private final TextView tvValueJangka;
        private final TextView tvValueTempo;
        private final TextView tvNominal;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            rlHead = (RelativeLayout) itemView.findViewById(R.id.rlHead);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitleCardDeposit);
            imgPencil = (ImageView) itemView.findViewById(R.id.imgPencil);
            tvValueJangka = (TextView) itemView.findViewById(R.id.tvValueJangka);
            tvValueTempo = (TextView) itemView.findViewById(R.id.tvValueTempo);
            tvNominal = (TextView) itemView.findViewById(R.id.tvNominal);

            if (idService.equals("193")) {
                imgPencil.setVisibility(View.VISIBLE);
            }
        }
    }
}
