package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterPortofolioNew;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_portfolio_new extends Fragment {

    private Context mContext;
    PieChart pieChart;
    PieData pieData;
    private SessionManager sessions;
    private NestedScrollView nestedScrollView;
    private Button btnService;
    private TextView tvtanggal;
    private String bahasa;
    private boolean cekCust;
    private RecyclerView rv_item_expand;
    private static final int[] MATERIAL_COLORS = {
            rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db"), rgb("#ed0ff1"),
            rgb("#90e610"), rgb("#f2ad0c"), rgb("#0af28a"), rgb("#f20a4c"), rgb("#f20a7a")
    };
    private String idDips;
    private JSONObject dataNasabah;
    private String customerName;
    private JSONArray listTypeProduk;
    private JSONArray typeProdukListArr;
    private String noCif = "";
    private RabbitMirroring rabbitMirroring;
    private boolean isSessionZoom = false;

    private static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        sessions = new SessionManager(mContext);

        if (sessions.getNoCIF() != null) {
            noCif = sessions.getNoCIF();
        }

        if (noCif.isEmpty()) {
            noCif = "obnllnnxo";
        }

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
            JSONObject dataCIF = new JSONObject();
            try {
                dataCIF.put("noCif",noCif);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            rabbitMirroring.MirroringSendKey(dataCIF);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_portfolio_new, container, false);

        pieChart = views.findViewById(R.id.pieChart);
        nestedScrollView = (NestedScrollView) views.findViewById(R.id.nestedz);
        btnService = (Button) views.findViewById(R.id.btnPnL);
        tvtanggal = (TextView) views.findViewById(R.id.date);
        rv_item_expand = (RecyclerView) views.findViewById(R.id.rv_item_expand);
        
        bahasa = sessions.getLANG();
        cekCust = sessions.getKEY_iSCust();

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }

        idDips = sessions.getKEY_IdDips();
        Calendar c = Calendar.getInstance();
        String TanggalSekarang = "";
        if (bahasa.equalsIgnoreCase("en")){
            SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMMM yyy", Locale.US);
            TanggalSekarang = df.format(c.getTime());
            tvtanggal.setText(TanggalSekarang);
        }
        else if (bahasa.equalsIgnoreCase("id")){
            SimpleDateFormat df = new SimpleDateFormat("EEEE dd MMMM yyy",new Locale("id", "ID"));
            TanggalSekarang = df.format(c.getTime());
            tvtanggal.setText(TanggalSekarang);
        }

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rabbitMirroring.MirroringSendEndpoint(15);
                getFragmentPage(new frag_service_new());
            }
        });

        getPortofolio();
        
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void getPortofolio() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",noCif);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK", this+" getPortofolio PARAMS : "+jsons.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().GetNewPortofolio(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","getPortofolio CODE: "+response.code());
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","getPortofolio dataS: "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("code");
                        if (errCode == 200) {
                            dataNasabah = dataObj.getJSONObject("data");
                            customerName = dataNasabah.getString("customerName");
                            listTypeProduk = dataNasabah.getJSONArray("listTypeProduk");

                            setChartNasabah();
                            setRecylerExpand();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLegendChart(){
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextSize(12);
        l.setDrawInside(false);
    }

    private void setChartNasabah() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);

        setLegendChart();

        typeProdukListArr = new JSONArray();
        for (int i = 0; i < listTypeProduk.length(); i++) {
            JSONObject dataValProduk = new JSONObject();

            try {
                String type = listTypeProduk.getJSONObject(i).getString("type");
                int persentase = listTypeProduk.getJSONObject(i).getInt("persentase");

                double d = (double) persentase / 10;
                String percent = String.format("%.1f", d);
                Log.e("CEK","percent : "+percent);

                if (!percent.isEmpty()) {
                    String typeProduk = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                    percent = percent.replace(",", ".");
                    pieEntryList.add(new PieEntry(Float.parseFloat(percent), typeProduk));

                    JSONObject dataListPorto = parseGetProduct(type);
                    JSONArray dataList = new JSONArray();
                    dataList.put(dataListPorto);

                    dataValProduk.put("typeProduct",typeProduk);
                    dataValProduk.put("dataList",dataList);
                    typeProdukListArr.put(dataValProduk);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        if (pieEntryList.size() > 5) {
            pieDataSet.setColors(MATERIAL_COLORS);
        } else {
            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        }

        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setHoleColor(mContext.getColor(R.color.zm_bg2));
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.invalidate();

    }

    private JSONObject parseGetProduct(String type) throws JSONException {
        JSONArray prod = null;
        if (type.equals("tabungan")) {
            prod = dataNasabah.getJSONArray("portotabungan");
        } else if (type.equals("deposito")) {
            prod = dataNasabah.getJSONArray("portodeposito");
        } else if (type.equals("pinjaman")) {
            prod = dataNasabah.getJSONArray("portoloan");
        }

        JSONObject dataListPorto = new JSONObject();

        for (int i = 0; i < prod.length(); i++) {
            String prodName = "";
            if (type.equals("pinjaman")) {
                prodName = prod.getJSONObject(i).getString("loanProductName");
            } else {
                prodName = prod.getJSONObject(i).getString("prodName");
            }

            String noRekening = "";
            long jumlahDana = 0;
            String kurs = "";
            if (type.equals("tabungan")) {
                noRekening = prod.getJSONObject(i).getString("accountNo");
                String balance = prod.getJSONObject(i).getString("availBalance");
                jumlahDana = (long) Double.parseDouble(balance);
                kurs = prod.getJSONObject(i).getString("acctCur");
            } else if (type.equals("deposito")) {
                noRekening = prod.getJSONObject(i).getString("rekKredit");
                String balance = prod.getJSONObject(i).getString("nominal");
                jumlahDana = (long) Double.parseDouble(balance);
                kurs = prod.getJSONObject(i).getString("mataUang");
            } else if (type.equals("pinjaman")) {
                noRekening = prod.getJSONObject(i).getString("loanNo");
                String balance = prod.getJSONObject(i).getString("plafond");
                jumlahDana = (long) Double.parseDouble(balance);
                kurs = prod.getJSONObject(i).getString("mataUang");
            }

            dataListPorto.put("namaProduk",prodName);
            dataListPorto.put("noRekening",noRekening);
            dataListPorto.put("jumlahDana",jumlahDana);
            dataListPorto.put("kurs",kurs);
        }

        return dataListPorto;

    }

    private void setRecylerExpand() {
        Log.e("CEK","MASUK setRecylerExpand");

        AdapterPortofolioNew dataExpand = new AdapterPortofolioNew(mContext, typeProdukListArr);

        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        rv_item_expand.setLayoutManager(recylerViewLayoutManager);
        rv_item_expand.setAdapter(dataExpand);
    }

}