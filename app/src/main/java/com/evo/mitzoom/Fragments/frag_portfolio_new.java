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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterPortofolioNew;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private JSONObject dataNasabah = new JSONObject();
    private String customerName;
    private JSONArray listTypeProduk;
    private JSONArray typeProdukListArr;
    private String noCif = "";
    private RabbitMirroring rabbitMirroring;
    private boolean isSessionZoom = false;
    private String typeProduct = "";
    private String percentProduct = "";
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));
    private SwipeRefreshLayout swipe;

    private static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getContext();
        sessions = new SessionManager(mContext);

        super.onCreate(savedInstanceState);

        if (sessions.getNoCIF() != null) {
            noCif = sessions.getNoCIF();
        }

        ConnectionRabbitHttp.init(mContext);

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            JSONObject dataCIF = new JSONObject();
            try {
                dataCIF.put("noCif",noCif);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ConnectionRabbitHttp.mirroringKey(dataCIF);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_portfolio_new, container, false);

        pieChart = views.findViewById(R.id.pieChart);
        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);
        nestedScrollView = views.findViewById(R.id.nestedz);
        btnService = views.findViewById(R.id.btnPnL);
        tvtanggal = views.findViewById(R.id.date);
        rv_item_expand = views.findViewById(R.id.rv_item_expand);
        
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
            SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyy", Locale.US);
            TanggalSekarang = df.format(c.getTime());
            tvtanggal.setText(TanggalSekarang);
        }
        else if (bahasa.equalsIgnoreCase("id")){
            SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyy",new Locale("id", "ID"));
            TanggalSekarang = df.format(c.getTime());
            tvtanggal.setText(TanggalSekarang);
        }

        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataNasabah.length() > 0) {
                    //RabbitMirroring.MirroringSendEndpoint(15);
                    ConnectionRabbitHttp.mirroringEndpoint(15);
                    getFragmentPage(new frag_service_new());
                }
            }
        });

        getPortofolio();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                swipe.setRefreshing(false);
                getPortofolio();
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

    private void getPortofolio() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",noCif);
            jsons.put("bahasa",bahasa);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().GetNewPortofolio(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
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

        List<Integer> colorsProd = new ArrayList<>();

        typeProdukListArr = new JSONArray();
        for (int i = 0; i < listTypeProduk.length(); i++) {
            JSONObject dataValProduk = new JSONObject();

            try {
                String type = listTypeProduk.getJSONObject(i).getString("type");
                double persentase = listTypeProduk.getJSONObject(i).getDouble("persentase");
                if (persentase == 0) {
                    persentase = 100;
                }

                if (type.equalsIgnoreCase("tabungan") || type.equalsIgnoreCase("saving")) {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_tabungan));
                } else if (type.equalsIgnoreCase("deposito") || type.equalsIgnoreCase("deposit")) {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_deposito));
                } else if (type.equalsIgnoreCase("pinjaman") || type.equalsIgnoreCase("loan")) {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_kredit));
                } else if (type.equalsIgnoreCase("giro")) {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_giro));
                } else if (type.toLowerCase().contains("banca")) {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_banca));
                } else if (type.toLowerCase().contains("reksa")) {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_reksa));
                } else {
                    colorsProd.add(mContext.getResources().getColor(R.color.zm_button));
                }

                double d = 0;
                String valS = String.valueOf(persentase);
                if (valS.contains(".") || valS.contains(",")) {
                    d = (double) persentase * 100;
                } else {
                    d = (double) persentase / 10;
                }
                String percent = String.format("%.1f", d);

                if (i == 0) {
                    typeProduct = type;
                    percentProduct = percent;
                }

                if (!percent.isEmpty()) {
                    String typeProduk = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                    percent = percent.replace(",", ".");
                    pieEntryList.add(new PieEntry(Float.parseFloat(percent), typeProduk));

                    JSONArray dataList = parseGetProduct(type);

                    dataValProduk.put("typeProduct",typeProduk);
                    dataValProduk.put("dataList",dataList);
                    typeProdukListArr.put(dataValProduk);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        pieDataSet.setColors(colorsProd);
        /*if (pieEntryList.size() > 5) {
            pieDataSet.setColors(MATERIAL_COLORS);
        } else {
            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        }*/

        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setEntryLabelTextSize(12f);
        if (listTypeProduk.length() == 1 && !typeProduct.isEmpty() && !percentProduct.isEmpty()) {
            if (percentProduct.equals("0,0") || percentProduct.equals("0.0")) {
                if (typeProduct.equalsIgnoreCase("tabungan") || typeProduct.equalsIgnoreCase("saving")) {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_tabungan));
                } else if (typeProduct.equalsIgnoreCase("deposito") || typeProduct.equalsIgnoreCase("deposit")) {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_deposito));
                } else if (typeProduct.equalsIgnoreCase("pinjaman") || typeProduct.equalsIgnoreCase("loan")) {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_kredit));
                } else if (typeProduct.equalsIgnoreCase("giro")) {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_giro));
                } else if (typeProduct.toLowerCase().contains("banca")) {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_banca));
                } else if (typeProduct.toLowerCase().contains("reksa")) {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_reksa));
                } else {
                    pieChart.setTransparentCircleColor(mContext.getResources().getColor(R.color.zm_text));
                }
                pieChart.setTransparentCircleRadius(70f);
            }
        }
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.invalidate();

    }

    private JSONArray parseGetProduct(String type) throws JSONException {
        JSONArray prod = null;
        if (type.equalsIgnoreCase("tabungan") || type.equalsIgnoreCase("saving")) {
            prod = dataNasabah.getJSONArray("portotabungan");
            JSONArray rekTabungan = new JSONArray();
            for (int i = 0; i < prod.length()+1; i++) {
                if (i == 0) {
                    JSONObject chObj = new JSONObject();
                    chObj.put("accountNo",getString(R.string.choose_please));
                    chObj.put("accountName",getString(R.string.choose_please));
                    rekTabungan.put(chObj);
                } else {
                    int j = i - 1;
                    JSONObject jsonObject = prod.getJSONObject(j);
                    rekTabungan.put(jsonObject);
                }
            }
            sessions.saveRekNasabah(rekTabungan.toString());
        } else if (type.equalsIgnoreCase("deposito") || type.equalsIgnoreCase("deposit")) {
            prod = dataNasabah.getJSONArray("portodeposito");
        } else if (type.equalsIgnoreCase("pinjaman") || type.equalsIgnoreCase("loan")) {
            prod = dataNasabah.getJSONArray("portoloan");
        }

        JSONArray dataList = new JSONArray();

        for (int i = 0; i < prod.length(); i++) {
            String prodName = "";
            String accountName = "";
            if (type.equalsIgnoreCase("pinjaman") || type.equalsIgnoreCase("loan")) {
                prodName = prod.getJSONObject(i).getString("loanProductName");
                if (prod.getJSONObject(i).has("accountName")) {
                    accountName = prod.getJSONObject(i).getString("accountName");
                }
            } else {
                if (prod.getJSONObject(i).has("accountName")) {
                    accountName = prod.getJSONObject(i).getString("accountName");
                }
                prodName = prod.getJSONObject(i).getString("prodName");
            }

            String noRekening = "";
            long jumlahDana = 0;
            String kurs = "";
            if (type.equalsIgnoreCase("tabungan") || type.equalsIgnoreCase("saving")) {
                noRekening = prod.getJSONObject(i).getString("accountNo");
                String balance = prod.getJSONObject(i).getString("clearBalance");
                jumlahDana = (long) Double.parseDouble(balance);
                kurs = prod.getJSONObject(i).getString("acctCur");


            } else if (type.equalsIgnoreCase("deposito") || type.equalsIgnoreCase("deposit")) {
                noRekening = prod.getJSONObject(i).getString("rekKredit");
                String balance = prod.getJSONObject(i).getString("nominal");
                jumlahDana = (long) Double.parseDouble(balance);
                kurs = prod.getJSONObject(i).getString("mataUang");
            } else if (type.equalsIgnoreCase("pinjaman") || type.equalsIgnoreCase("loan")) {
                noRekening = prod.getJSONObject(i).getString("loanNo");
                String balance = prod.getJSONObject(i).getString("plafond");
                jumlahDana = (long) Double.parseDouble(balance);
                kurs = prod.getJSONObject(i).getString("mataUang");
            }

            JSONObject dataListPorto = new JSONObject();
            dataListPorto.put("namaProduk",prodName);
            dataListPorto.put("accountName",accountName);
            dataListPorto.put("noRekening",noRekening);
            dataListPorto.put("jumlahDana",jumlahDana);
            dataListPorto.put("kurs",kurs);
            dataList.put(dataListPorto);
        }

        return dataList;

    }

    private void setRecylerExpand() {

        AdapterPortofolioNew dataExpand = new AdapterPortofolioNew(mContext, typeProdukListArr);

        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        rv_item_expand.setLayoutManager(recylerViewLayoutManager);
        rv_item_expand.setAdapter(dataExpand);
    }

    public static BigDecimal parseCurrencyValue(String value) {
        try {
            String replaceRegex = String.format("[%s,.\\s]", Objects.requireNonNull(numberFormat.getCurrency()).getDisplayName());
            String currencyValue = value.replaceAll(replaceRegex, "");
            return new BigDecimal(currencyValue);
        } catch (Exception e) {
        }
        return BigDecimal.ZERO;
    }

}