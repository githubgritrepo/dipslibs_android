package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.common.io.Files;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wm_detail_product extends Fragment {

    private static final String TAG = "frag_wm_detail_product";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private String labelserv = "";
    private JSONObject dataObjProd = null;
    private RelativeLayout rlHeadNAB;
    private TextView tvNABUnits;
    private TextView tvNABNominal;
    private TextView tvPercent;
    private NestedScrollView nestedScroll;
    private LineChart chart;
    private String rangeType = "D";
    private int rangeValue = 30;
    private RadioButton rb10D;
    private RadioButton rb1M;
    private RadioButton rb6M;
    private RadioButton rb1Y;
    private RadioGroup rgChooseChart;
    private LinearLayout llInfo;
    private TextView tvInfoProdVal;
    private TextView tvImbalHasilVal;
    private TextView tvTingkatRiskVal;
    private TextView tvManageInvesVal;
    private TextView tvPurchaseFeeVal;
    private TextView tvPurchaseTaxVal;
    private CheckBox chkPros;
    private CheckBox chkFund;
    private CheckBox chkTerm;
    private String productName;
    private String productCode;
    private String dataTnC = "";
    private RelativeLayout rlDownloadFile;
    private ImageView imgDownloadFile;
    private RelativeLayout rlFile;
    private PDFView pdfView;
    private Button btnRead;
    private String target = "";
    private Button btnBuy;
    private String labelTrx = "";
    private String rangeLabel = "";
    private JSONObject mirrObj;
    private JSONObject detailproductMirr;
    private JSONObject datadetailproductMirr;
    private String labelDetailKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("dataObjProd")) {
                String dataObjProdS = getArguments().getString("dataObjProd");
                try {
                    dataObjProd = new JSONObject(dataObjProdS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_detail_product, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        rlHeadNAB = (RelativeLayout) views.findViewById(R.id.rlHeadNAB);
        tvNABUnits = (TextView) views.findViewById(R.id.tvNABUnits);
        tvNABNominal = (TextView) views.findViewById(R.id.tvNABNominal);
        tvPercent = (TextView) views.findViewById(R.id.tvPercent);

        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);
        chart = (LineChart) views.findViewById(R.id.chart1);

        rgChooseChart = (RadioGroup) views.findViewById(R.id.rgChooseChart);
        rb10D = (RadioButton) views.findViewById(R.id.rb10D);
        rb1M = (RadioButton) views.findViewById(R.id.rb1M);
        rb6M = (RadioButton) views.findViewById(R.id.rb6M);
        rb1Y = (RadioButton) views.findViewById(R.id.rb1Y);

        llInfo = (LinearLayout) views.findViewById(R.id.llInfo);
        tvInfoProdVal = (TextView) views.findViewById(R.id.tvInfoProdVal);
        tvImbalHasilVal = (TextView) views.findViewById(R.id.tvImbalHasilVal);
        tvTingkatRiskVal = (TextView) views.findViewById(R.id.tvTingkatRiskVal);
        tvManageInvesVal = (TextView) views.findViewById(R.id.tvManageInvesVal);
        tvPurchaseFeeVal = (TextView) views.findViewById(R.id.tvPurchaseFeeVal);
        tvPurchaseTaxVal = (TextView) views.findViewById(R.id.tvPurchaseTaxVal);
        chkPros = (CheckBox) views.findViewById(R.id.chkPros);
        chkFund = (CheckBox) views.findViewById(R.id.chkFund);
        chkTerm = (CheckBox) views.findViewById(R.id.chkTerm);

        rlDownloadFile = (RelativeLayout) views.findViewById(R.id.rlDownloadFile);
        imgDownloadFile = (ImageView) views.findViewById(R.id.imgDownloadFile);
        rlFile = (RelativeLayout) views.findViewById(R.id.rlFile);
        pdfView = (PDFView) views.findViewById(R.id.pdfView);
        btnRead = (Button) views.findViewById(R.id.btnRead);

        btnBuy = (Button) views.findViewById(R.id.btnBuy);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labelTrx = "reksadana";
        labelDetailKey = "detailproduct";

        ConnectionRabbitHttp.mirroringEndpoint(288);

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }
        mirrObj = new JSONObject();
        detailproductMirr = new JSONObject();
        datadetailproductMirr = new JSONObject();

        if (dataObjProd != null) {

            try {

                for(Iterator<String> iter = dataObjProd.keys(); iter.hasNext();) {
                    if (iter.hasNext()) {
                        String key = iter.next();
                        if (dataObjProd.has(key) && !dataObjProd.isNull(key)) {
                            datadetailproductMirr.put(key,dataObjProd.get(key));
                        }
                    }
                }

                datadetailproductMirr.put("prospektus",false);
                datadetailproductMirr.put("fundfactsheet",false);
                datadetailproductMirr.put("syarat&ketentuan",false);
                detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                mirrObj.put(labelTrx,detailproductMirr);
                ConnectionRabbitHttp.mirroringKey(mirrObj);

                productCode = dataObjProd.getString("productCode");

                if (dataObjProd.has("productName")) {
                    productName = dataObjProd.getString("productName");
                    tvInfoProdVal.setText(productName);
                }
                if (dataObjProd.has("riskProfileProductName")) {
                    String riskProfileProductName = dataObjProd.getString("riskProfileProductName");
                    tvTingkatRiskVal.setText(riskProfileProductName);
                }
                if (dataObjProd.has("fundHouseName")) {
                    String fundHouseName = dataObjProd.getString("fundHouseName");
                    tvManageInvesVal.setText(fundHouseName);
                }
                if (dataObjProd.has("navDate")) {
                    String navDate = null;
                    navDate = dataObjProd.getString("navDate");
                    if (navDate.contains("T")) {
                        String[] sp = navDate.split("T");
                        String dates = sp[0];
                        String inputFormat = "yyyy-MM-dd";
                        String outputFormat = "dd MMMM yyyy";
                        String NABDate = ConveredDate(dates, inputFormat, outputFormat);
                        String redNabUnit = getString(R.string.nab_unit);
                        String NABUnits = redNabUnit + " " + NABDate;
                        tvNABUnits.setText(NABUnits);
                    }
                }
    
                if (dataObjProd.has("nav")) {
                    long nom = dataObjProd.getLong("nav");
                    NumberFormat formatter = null;
                    if (sessions.getLANG().equals("id")) {
                        formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                    } else {
                        formatter = NumberFormat.getInstance(new Locale("en", "US"));
                    }
                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(nom);
    
                    String nominal = "";
                    if (sessions.getLANG().equals("id")) {
                        nominal = "Rp. "+formatted;
                    } else {
                        String currency = "USD";
                        if (dataObjProd.has("currency")) {
                            currency = dataObjProd.getString("currency");
                        }
                        nominal = currency+" "+formatted;
                    }
                    tvNABNominal.setText(nominal);
                }

                if (dataObjProd.has("subsFee")) {
                    double subsFee = dataObjProd.getDouble("subsFee");
                    String imbalHasil = "0% /Thn";
                    String imbalHasil2 = subsFee+" %";
                    if (sessions.getLANG().equals("id")) {
                        imbalHasil = subsFee+"% /Thn";
                    } else {
                        imbalHasil = subsFee+"% /Years";
                    }
                    tvPercent.setText(imbalHasil);
                    tvImbalHasilVal.setText(imbalHasil2);
                    tvPurchaseFeeVal.setText(imbalHasil2);
                }
                if (dataObjProd.has("tax")) {
                    double tax = dataObjProd.getDouble("tax");
                    String taxS = tax+" %";
                    tvPurchaseTaxVal.setText(taxS);
                }

                chart.setViewPortOffsets(0, 0, 0, 0);
                //chart.setBackgroundColor(Color.rgb(104, 241, 175));

                // no description text
                chart.getDescription().setEnabled(false);

                // enable touch gestures
                chart.setTouchEnabled(true);

                // enable scaling and dragging
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);

                // if disabled, scaling can be done on x- and y-axis separately
                chart.setPinchZoom(false);

                chart.setDrawGridBackground(false);
                chart.setMaxHighlightDistance(300);

                XAxis x = chart.getXAxis();
                x.setLabelCount(6, false);
                x.setAxisLineColor(Color.BLACK);


                YAxis y = chart.getAxisLeft();
                y.setLabelCount(6, false);
                y.setTextColor(Color.BLACK);
                y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                y.setDrawGridLines(false);
                y.setAxisLineColor(Color.BLACK);

                chart.getAxisRight().setEnabled(false);

                chart.getLegend().setEnabled(false);

                chart.animateXY(2000, 2000);
                // redraw
                chart.invalidate();
                
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            
        }

        rangeLabel = "1M";
        processNavPerformance();
        processGetTNC();
        if (chkPros.isChecked() && chkFund.isChecked() && chkTerm.isChecked()) {
            btnBuy.setEnabled(true);
            btnBuy.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!target.isEmpty()) {
                    tvtitleHead.setText(labelserv);
                    rlHeadNAB.setVisibility(View.VISIBLE);
                    nestedScroll.setVisibility(View.VISIBLE);
                    rlFile.setVisibility(View.GONE);
                    rlDownloadFile.setVisibility(View.GONE);

                    target = "";
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                    ConnectionRabbitHttp.mirroringEndpoint(287);
                }
            }
        });

        rgChooseChart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb10D:
                            rb10D.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
                            rb1M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb6M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb1Y.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));

                            rangeType = "D";
                            rangeValue = 10;
                            rangeLabel = rb10D.getText().toString();
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            processNavPerformance();
                        break;
                    case R.id.rb1M:
                            rb10D.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb1M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
                            rb6M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb1Y.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));

                            rangeType = "D";
                            rangeValue = 30;
                            rangeLabel = rb1M.getText().toString();
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            processNavPerformance();
                        break;
                    case R.id.rb6M:
                            rb10D.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb1M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb6M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
                            rb1Y.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));

                            rangeType = "M";
                            rangeValue = 6;
                            rangeLabel = rb6M.getText().toString();
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            processNavPerformance();
                        break;
                    case R.id.rb1Y:
                            rb10D.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb1M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb6M.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_green)));
                            rb1Y.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));

                            rangeType = "M";
                            rangeValue = 12;
                            rangeLabel = rb1Y.getText().toString();
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            processNavPerformance();
                        break;
                }
            }
        });

        chkPros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChk = chkPros.isChecked();

                if (isChk) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    target = "prospectus";
                    chkPros.setChecked(false);
                    try {
                        datadetailproductMirr.put("prospektus",false);
                        detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                        mirrObj.put(labelTrx,detailproductMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    processGetFileDocuWM();
                } else {
                    btnBuy.setEnabled(false);
                    btnBuy.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                }
            }
        });

        chkFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChk = chkFund.isChecked();

                if (isChk) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    target = "fundfact";
                    chkFund.setChecked(false);
                    try {
                        datadetailproductMirr.put("fundfactsheet",false);
                        detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                        mirrObj.put(labelTrx,detailproductMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    processGetFileDocuWM();
                } else {
                    btnBuy.setEnabled(false);
                    btnBuy.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                }
            }
        });

        chkTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChk = chkTerm.isChecked();

                if (isChk) {
                    chkTerm.setChecked(false);
                    ConnectionRabbitHttp.mirroringEndpoint(304);

                    try {
                        datadetailproductMirr.put("syarat&ketentuan",false);
                        detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                        mirrObj.put(labelTrx,detailproductMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    PopUpTnc();
                } else {
                    btnBuy.setEnabled(false);
                    btnBuy.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                }
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionRabbitHttp.mirroringEndpoint(288);
                tvtitleHead.setText(labelserv);
                rlHeadNAB.setVisibility(View.VISIBLE);
                nestedScroll.setVisibility(View.VISIBLE);
                rlFile.setVisibility(View.GONE);
                rlDownloadFile.setVisibility(View.GONE);

                try {
                    if (target.equals("prospectus")) {
                        chkPros.setChecked(true);
                        datadetailproductMirr.put("prospektus",true);
                    } else {
                        chkFund.setChecked(true);
                        datadetailproductMirr.put("fundfactsheet",true);
                    }

                    datadetailproductMirr.put("syarat&ketentuan",false);
                    detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                    mirrObj.put(labelTrx,detailproductMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                target = "";

                if (chkPros.isChecked() && chkFund.isChecked() && chkTerm.isChecked()) {
                    btnBuy.setEnabled(true);
                    btnBuy.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("labelserv",labelserv);
                bundle.putString("dataObjProd",dataObjProd.toString());
                Fragment fragments = new frag_wm_trans_next();
                fragments.setArguments(bundle);
                getFragmentPage(fragments);
            }
        });

    }

    private void setData(ArrayList<Entry> values) {

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.RED);
            //set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(mContext.getResources().getColor(R.color.button_schedule));
            //set1.setFillColor(Color.MAGENTA);
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.gradient_chart);
            set1.setFillDrawable(drawable);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);

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

    private void processNavPerformance() {
        try {
            JSONObject objReq = new JSONObject();
            objReq.put("productCode",productCode);
            objReq.put("rangeType",rangeType);
            objReq.put("rangeValue",rangeValue);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().ProdWMSNavPerformance(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            JSONArray dataJSArr = dataBody.getJSONArray("data");

                            JSONObject dataChartMirr = new JSONObject();
                            dataChartMirr.put("range",rangeLabel);
                            dataChartMirr.put("data",dataJSArr);
                            datadetailproductMirr.put("chart",dataChartMirr);
                            detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                            mirrObj.put(labelTrx,detailproductMirr);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);

                            ArrayList<Entry> values = new ArrayList<>();
                            for (int i = 0; i < dataJSArr.length(); i++) {
                                String dates = dataJSArr.getJSONObject(i).getString("date");
                                long amount = dataJSArr.getJSONObject(i).getLong("amount");

                                String[] sp = dates.split("T");
                                String dateChart = sp[0];

                                values.add(new Entry(i, amount));
                            }

                            setData(values);
                            // redraw
                            chart.invalidate();
                            
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetFileDocuWM() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetFileDocWM(productCode,target,authAccess,exchangeToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }

                if (response.isSuccessful()) {
                    InputStream inputStream = null;

                    String Content_Type = response.headers().get("Content-Type");
                    inputStream = response.body().byteStream();
                    pdfView.fromStream(inputStream)
                            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                            .enableSwipe(true) // allows to block changing pages using swipe
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .scrollHandle(new DefaultScrollHandle(mContext))
                            .load();

                    rlHeadNAB.setVisibility(View.GONE);
                    nestedScroll.setVisibility(View.GONE);
                    rlFile.setVisibility(View.VISIBLE);
                    rlDownloadFile.setVisibility(View.VISIBLE);
                    InputStream finalInputStream = inputStream;
                    rlDownloadFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProcessDownloadFile();
                        }

                        private void ProcessDownloadFile() {

                            try {
                                File dir = createDir();
                                if (!dir.exists()) {
                                    if (!dir.mkdirs()) {
                                    }
                                }

                                File mediaFile = new File(dir.getPath() + File.separator +
                                        target+".pdf");

                                Files.asByteSink(mediaFile).write(response.body().bytes());
                                Toast.makeText(mContext, R.string.file_downloaded, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                            }

                        }
                    });

                    if (target.equals("prospectus")) {
                        ConnectionRabbitHttp.mirroringEndpoint(289);
                        tvtitleHead.setText(chkPros.getText());
                        String labelBtn = mContext.getString(R.string.i_have_read) + " " + chkPros.getText();
                        btnRead.setText(labelBtn);
                    } else {
                        ConnectionRabbitHttp.mirroringEndpoint(290);
                        tvtitleHead.setText(chkFund.getText());
                        String labelBtn = mContext.getString(R.string.i_have_read) + " " + chkFund.getText();
                        btnRead.setText(labelBtn);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private File createDir() {
        String appName = mContext.getString(R.string.app_name_dips);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), appName);

        return mediaStorageDir;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        int EOF = -1;
        int DEFAULT_BUFFER_SIZE = 1024 * 4;
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {

            }
            if (file.renameTo(newFile)) {

            }
        }
        return newFile;
    }

    private void processGetTNC() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getTNC(22,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
                        String cekdataTnC = dataObj.getJSONObject("data").getString("data");
                        if (cekdataTnC.contains("{")) {
                            JSONObject labelTNC = new JSONObject(cekdataTnC);
                            String language = sessions.getLANG();
                            if (language.equals("id")) {
                                dataTnC = labelTNC.getString("labelIdn");
                            } else {
                                dataTnC = labelTNC.getString("labelEng");
                            }
                        } else {
                            dataTnC = cekdataTnC;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = "";
                    if (response.body() != null) {
                        String dataS = response.body().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            msg = dataObj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (response.errorBody().toString().isEmpty()) {
                            String dataS = response.errorBody().toString();
                            try {
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String dataS = null;
                            try {
                                dataS = response.errorBody().string();
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopUpTnc() {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialogTNC = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        TextView tvBody = dialogView.findViewById(R.id.tvBody);
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        checkBox.setVisibility(View.GONE);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
        btn.setClickable(true);
        btn.setText("Saya telah membaca S & K");

        sweetAlertDialogTNC.setCustomView(dialogView);
        sweetAlertDialogTNC.hideConfirmButton();
        sweetAlertDialogTNC.setCancelable(true);

        if (!dataTnC.isEmpty()) {
            tvBody.setText(Html.fromHtml(dataTnC, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int idx = source.indexOf(",");
                    idx += 1;
                    String new_source = source.substring(idx);
                    byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                    int intH = d.getIntrinsicHeight();
                    int intW = d.getIntrinsicWidth();
                    d.setBounds(0, 0, intW, intH);
                    return d;
                }
            }, null));
        }

        sweetAlertDialogTNC.show();

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);

        sweetAlertDialogTNC.getWindow().setLayout(newWidth,newHeight);
        sweetAlertDialogTNC.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionRabbitHttp.mirroringEndpoint(288);
                sweetAlertDialogTNC.cancel();
                sweetAlertDialogTNC.dismissWithAnimation();
                chkTerm.setChecked(true);

                try {
                    datadetailproductMirr.put("syarat&ketentuan",true);
                    detailproductMirr.put(labelDetailKey,datadetailproductMirr);
                    mirrObj.put(labelTrx,detailproductMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (chkPros.isChecked() && chkFund.isChecked() && chkTerm.isChecked()) {
                    btnBuy.setEnabled(true);
                    btnBuy.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
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

    private void getFragmentPageBackStack(Fragment fragment){
        if (isSessionZoom) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack("fragWealth")
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("fragWealth")
                    .commit();
        }

    }

}