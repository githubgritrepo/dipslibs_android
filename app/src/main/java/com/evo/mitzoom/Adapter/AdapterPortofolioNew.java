package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Fragments.frag_dialog_rtgs;
import com.evo.mitzoom.GlideApp;
import com.evo.mitzoom.Model.PortfolioModel;
import com.evo.mitzoom.R;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class AdapterPortofolioNew extends RecyclerView.Adapter<AdapterPortofolioNew.ViewHolder> {

    private JSONArray dataList;
    private Context mContext;
    private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();

    public AdapterPortofolioNew(Context ctx, JSONArray dataList){
        this.dataList = dataList;
        this.mContext = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio_new,parent,false);
        return new ViewHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String typeProd = dataList.getJSONObject(position).getString("typeProduct");
            String linkIcon = "";
            if (dataList.getJSONObject(position).has("icon")) {
                linkIcon = dataList.getJSONObject(position).getString("icon");
            }
            JSONArray dataListPorto = dataList.getJSONObject(position).getJSONArray("dataList");

            holder.tv_nama_product.setText(typeProd);

            if (!linkIcon.isEmpty()) {
                GlideApp.with(mContext)
                        .load(linkIcon)
                        .placeholder(R.drawable.porto1)
                        .into(holder.img_logo_porto);
            } else {
                holder.img_logo_porto.setImageDrawable(mContext.getDrawable(R.drawable.porto1));
            }

            for (int i = 0; i < dataListPorto.length(); i++) {
                String idRelatifs = "11" + i;
                int idRelatif = Integer.valueOf(idRelatifs);
                Log.e("CEK","dataListPorto ke-"+i+" | idRelatif : "+idRelatif);
                String namaProduk = "";
                String noRekening = "";
                String jumlahDana = "";
                String kurs = "";
                try {
                    namaProduk = dataListPorto.getJSONObject(i).getString("namaProduk");
                    noRekening = dataListPorto.getJSONObject(i).getString("noRekening");
                    jumlahDana = String.valueOf(dataListPorto.getJSONObject(i).getLong("jumlahDana"));
                    kurs = dataListPorto.getJSONObject(i).getString("kurs");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RelativeLayout rlExpandLayout = new RelativeLayout(mContext);
                rlExpandLayout.setId(idRelatif);

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMarginEnd(20);
                linearLayout.setLayoutParams(lp);

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView tv = new TextView(mContext);
                tv.setText(namaProduk);
                tv.setLayoutParams(lp2);
                tv.setTextColor(Color.BLACK);
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                tv.setMaxLines(2);

                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                linearLayout.addView(tv);

                BigDecimal parsed = frag_dialog_rtgs.parseCurrencyValue(jumlahDana);
                String formatted = frag_dialog_rtgs.numberFormat.format(parsed);

                String dataN = kurs + " " + formatted;
                TextView tv2 = new TextView(mContext);
                tv2.setId(R.id.tv_nominal_porto);
                tv2.setText(dataN);
                tv2.setLayoutParams(lp2);
                tv2.setTextColor(Color.BLACK);
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                linearLayout.addView(tv2);

                rlExpandLayout.setLayoutParams(relativeParams);
                rlExpandLayout.addView(linearLayout);

                RelativeLayout.LayoutParams relativeParams2 = new RelativeLayout.LayoutParams(40, 40);
                relativeParams2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                relativeParams2.addRule(RelativeLayout.ALIGN_PARENT_END);
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(relativeParams2);
                imageView.setImageResource(R.drawable.ic_eye);
                imageView.setId(R.id.open_eye);
                rlExpandLayout.addView(imageView);

                ImageView imageView2 = new ImageView(mContext);
                imageView2.setLayoutParams(relativeParams2);
                imageView2.setImageResource(R.drawable.ic_eye2);
                imageView2.setId(R.id.close_eye);
                imageView2.setVisibility(View.GONE);
                rlExpandLayout.addView(imageView2);

                holder.container.addView(rlExpandLayout);

                if (i < dataListPorto.length()-1) {
                    LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    lp3.setMargins(0, 5, 0, 5);
                    View views = new View(mContext);
                    views.setLayoutParams(lp3);
                    views.setBackgroundColor(Color.BLACK);

                    holder.container.addView(views);
                }
            }

            expansionsCollection.add(holder.expansionLayout);

            int countChild = holder.container.getChildCount();
            Log.e("CEK",typeProd+" countChild : "+countChild);
            for (int i = 0; i < countChild; i++) {
                int getId = holder.container.getChildAt(i).getId();
                Log.e("CEK","getId ke-"+i+" | "+getId);
                if (getId > 0) {
                    RelativeLayout rlExpandLayout = (RelativeLayout) holder.container.getChildAt(i);
                    Log.e("CEK","rlExpandLayout getChildCount : "+rlExpandLayout.getChildCount());
                    LinearLayout llExp = (LinearLayout) rlExpandLayout.getChildAt(0);
                    TextView tvNominal = (TextView) llExp.getChildAt(1);
                    String getNominal = tvNominal.getText().toString();

                    if (rlExpandLayout.getChildAt(1).getId() == R.id.open_eye) {
                        rlExpandLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String getNominal2 = tvNominal.getText().toString();
                                Log.e("CEK","MASUK EYE OPEN : "+getNominal2);
                                String[] sp = getNominal2.split(" ");
                                String nominal = sp[1];
                                nominal = nominal.replace(nominal,"XXXXXX");
                                String hidNominal = sp[0] + " " + nominal;
                                tvNominal.setText(hidNominal);
                                view.setVisibility(View.GONE);
                                rlExpandLayout.getChildAt(2).setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    if (rlExpandLayout.getChildAt(2).getId() == R.id.close_eye) {
                        rlExpandLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("CEK","MASUK EYE CLOSE : "+getNominal);
                                tvNominal.setText(getNominal);
                                view.setVisibility(View.GONE);
                                rlExpandLayout.getChildAt(1).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_logo_porto;
        private final TextView tv_nama_product;
        private final ExpansionLayout expansionLayout;
        private final LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_logo_porto = (ImageView) itemView.findViewById(R.id.img_logo_porto);
            tv_nama_product = (TextView) itemView.findViewById(R.id.tv_nama_product);
            expansionLayout = (ExpansionLayout) itemView.findViewById(R.id.expansionLayout);
            container = (LinearLayout) itemView.findViewById(R.id.container);

        }
    }
}
