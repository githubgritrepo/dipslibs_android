package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wm_detail_porto extends Fragment {

    private static final String TAG = "frag_wm_detail_porto";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private LinearLayout TopBar;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private LinearLayout llMainMutualFund;
    private TextView tvTitleProd;
    private TextView tvPtProd;
    private TextView tvTotalUnit;
    private TextView tvNAB;
    private TextView tvNABDate;
    private TextView tvOriginalVal;
    private TextView tvNowVal;
    private TextView tvKurungBuka;
    private ImageView imgImbalHasil;
    private TextView tvPercent;
    private TextView tvKurungTutup;
    private Button btnSale;
    private JSONObject dataObjProd = null;
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout llNextSale;
    private Spinner spinSourceAccount;
    private TextView tvTitleProdNextSale;
    private TextView tvPtProdNextSale;
    private TextView tvFeeSale;
    private TextView tvTaxFeeSale;
    private TextView tvUnitSelf;
    private TextView tvLabelNABSale;
    private TextView tvNABSale;
    private EditText edTotalUnitDisburs;
    private TextView tvRemainingUnits;
    private CheckBox chkState;
    private Button btnNext;
    private String Sale = "";
    private ArrayList<FormSpin> dataDropDownSource;
    private boolean flagSourceAccount = false;
    private String labelTrx = "";
    private String labelDetailKey = "";
    private JSONObject mirrObj;
    private JSONObject formMirr;
    private JSONObject dataMirr;
    private String investmentAccountNo = "";
    private double diffUnits = 0;
    private String diffS = "";
    private JSONObject objEl = new JSONObject();
    private boolean flagChkState;
    private String productCode = "";
    private String invAccountNo = "";
    private double perCent = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        ConnectionRabbitHttp.init(mContext);
        
        if (getArguments() != null) {
            if (getArguments().containsKey("dataObjProd")) {
                String dataObjProdS = getArguments().getString("dataObjProd");
                try {
                    dataObjProd = new JSONObject(dataObjProdS);
                    objEl = new JSONObject(dataObjProdS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            if (getArguments().containsKey("sale")) {
                Sale = getArguments().getString("sale");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_detail_porto, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        llMainMutualFund = (LinearLayout) views.findViewById(R.id.llMainMutualFund);
        tvTitleProd = (TextView) views.findViewById(R.id.tvTitleProd);
        tvPtProd = (TextView) views.findViewById(R.id.tvPtProd);
        tvTotalUnit = (TextView) views.findViewById(R.id.tvTotalUnit);
        tvNAB = (TextView) views.findViewById(R.id.tvNAB);
        tvNABDate = (TextView) views.findViewById(R.id.tvNABDate);
        tvOriginalVal = (TextView) views.findViewById(R.id.tvOriginalVal);
        tvNowVal = (TextView) views.findViewById(R.id.tvNowVal);
        tvKurungBuka = (TextView) views.findViewById(R.id.tvKurungBuka);
        imgImbalHasil = (ImageView) views.findViewById(R.id.imgImbalHasil);
        tvPercent = (TextView) views.findViewById(R.id.tvPercent);
        tvKurungTutup = (TextView) views.findViewById(R.id.tvKurungTutup);
        btnSale = (Button) views.findViewById(R.id.btnSale);

        llNextSale = (LinearLayout) views.findViewById(R.id.llNextSale);
        spinSourceAccount = (Spinner) views.findViewById(R.id.spinSourceAccount);
        tvTitleProdNextSale = (TextView) views.findViewById(R.id.tvTitleProdNextSale);
        tvPtProdNextSale = (TextView) views.findViewById(R.id.tvPtProdNextSale);
        tvFeeSale = (TextView) views.findViewById(R.id.tvFeeSale);
        tvTaxFeeSale = (TextView) views.findViewById(R.id.tvTaxFeeSale);
        tvUnitSelf = (TextView) views.findViewById(R.id.tvUnitSelf);
        tvLabelNABSale = (TextView) views.findViewById(R.id.tvLabelNABSale);
        tvNABSale = (TextView) views.findViewById(R.id.tvNABSale);
        edTotalUnitDisburs = (EditText) views.findViewById(R.id.edTotalUnitDisburs);
        tvRemainingUnits = (TextView) views.findViewById(R.id.tvRemainingUnits);
        chkState = (CheckBox) views.findViewById(R.id.chkState);
        btnNext = (Button) views.findViewById(R.id.btnNext);
        
        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Sale.isEmpty()) {
            ConnectionRabbitHttp.mirroringEndpoint(302);
            tvtitleHead.setText(mContext.getResources().getString(R.string.penjualan_reksa_dana));
        } else {
            ConnectionRabbitHttp.mirroringEndpoint(301);
            tvtitleHead.setText(mContext.getResources().getString(R.string.detail_portofolio));
        }

        labelTrx = "reksadana";
        mirrObj = new JSONObject();
        formMirr = new JSONObject();
        dataMirr = new JSONObject();

        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialog);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Sale.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("dataObjProd", dataObjProd.toString());
                    Fragment fragment = new frag_wm_detail_porto();
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("labelserv",mContext.getResources().getString(R.string.penjualan_reksa_dana));
                    bundle.putInt("catg",3);
                    Fragment fragments = new frag_wm_transactions();
                    fragments.setArguments(bundle);
                    getFragmentPage(fragments);
                }
            }
        });

        if (dataObjProd != null) {
            try {
                if (Sale.isEmpty()) {
                    btnNext.setVisibility(View.GONE);
                    btnSale.setVisibility(View.VISIBLE);
                    labelDetailKey = "detailportfolio";

                    dataMirr.put("alert","");

                    for(Iterator<String> iter = dataObjProd.keys(); iter.hasNext();) {
                        if (iter.hasNext()) {
                            String key = iter.next();
                            dataMirr.put(key,dataObjProd.get(key));
                        }
                    }

                    formMirr.put(labelDetailKey,dataMirr);
                    mirrObj.put(labelTrx,formMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);

                    llMainMutualFund.setVisibility(View.VISIBLE);
                    llNextSale.setVisibility(View.GONE);
                    if (dataObjProd.has("productName")) {
                        String productName = dataObjProd.getString("productName");
                        tvTitleProd.setText(productName);
                    }
                    if (dataObjProd.has("fundHouseName")) {
                        String fundHouseName = dataObjProd.getString("fundHouseName");
                        tvPtProd.setText(fundHouseName);
                    }

                    if (dataObjProd.has("navDate")) {
                        String navDate = dataObjProd.getString("navDate");
                        if (navDate.contains("T")) {
                            String[] sp = navDate.split("T");
                            String dates = sp[0];
                            String inputFormat = "yyyy-MM-dd";
                            String outputFormat = "dd/MM/yy";
                            String NABDate = ConveredDate(dates, inputFormat, outputFormat);
                            tvNABDate.setText(NABDate);
                        }
                    }

                    long nomNav = 0;
                    if (dataObjProd.has("nav")) {
                        nomNav = dataObjProd.getLong("nav");

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
                            if (dataObjProd.has("currency")) {
                                currency = dataObjProd.getString("currency");
                            }
                            nominal = currency + " " + formatted;
                        }

                        tvNAB.setText(nominal);

                        if (dataObjProd.has("units")) {
                            long units = dataObjProd.getLong("units");
                            diffUnits = (double) units / 10000;

                            formatter.setMinimumFractionDigits(4);
                            String formatUnit = formatter.format(diffUnits);

                            String txtUnits = formatUnit + " Unit";
                            tvTotalUnit.setText(txtUnits);
                        }

                        double mulAmount = (double) diffUnits * nomNav;
                        String formatNow = formatter.format(mulAmount);

                        String nominalNow = "";
                        if (sessions.getLANG().equals("id")) {
                            nominalNow = "Rp. " + formatNow;
                        } else {
                            String currency = "USD";
                            if (dataObjProd.has("currency")) {
                                currency = dataObjProd.getString("currency");
                            }
                            nominalNow = currency + " " + formatNow;
                        }

                        tvNowVal.setText(nominalNow);

                    }

                    if (dataObjProd.has("amount")) {
                        long amount = dataObjProd.getLong("amount") / 100;

                        NumberFormat formatter = null;
                        if (sessions.getLANG().equals("id")) {
                            formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                        } else {
                            formatter = NumberFormat.getInstance(new Locale("en", "US"));
                        }
                        formatter.setMinimumFractionDigits(2);
                        String formatted = formatter.format(amount);

                        String nominal = "";
                        if (sessions.getLANG().equals("id")) {
                            nominal = "Rp. " + formatted;
                        } else {
                            String currency = "USD";
                            if (dataObjProd.has("currency")) {
                                currency = dataObjProd.getString("currency");
                            }
                            nominal = currency + " " + formatted;
                        }

                        tvOriginalVal.setText(nominal);
                    }

                    long navunit = 0;
                    if (dataObjProd.has("navPerUnit")) {
                        long navPerUnit = dataObjProd.getLong("navPerUnit");
                        navunit = navPerUnit / 1000000;
                    }
                    double diffImbal = (double) (nomNav - navunit) / navunit;

                    NumberFormat formatter = null;
                    if (sessions.getLANG().equals("id")) {
                        formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                    } else {
                        formatter = NumberFormat.getInstance(new Locale("en", "US"));
                    }
                    formatter.setMinimumFractionDigits(2);
                    String formattedNumber = formatter.format(diffImbal);

                    String imbalHasil = formattedNumber + "%";

                    if (imbalHasil.charAt(0) == '-') {
                        tvKurungBuka.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                        imgImbalHasil.setImageDrawable(mContext.getResources().getDrawable(R.drawable.downred));
                        tvKurungTutup.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    }

                    tvPercent.setText(imbalHasil);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSale.setVisibility(View.GONE);

                    labelDetailKey = "formpenjualan";

                    llMainMutualFund.setVisibility(View.GONE);
                    llNextSale.setVisibility(View.VISIBLE);

                    dataMirr.put("rekeningsumberdana","");
                    dataMirr.put("snk",false);
                    for(Iterator<String> iter = dataObjProd.keys(); iter.hasNext();) {
                        if (iter.hasNext()) {
                            String key = iter.next();
                            if (dataObjProd.has(key) && !dataObjProd.isNull(key)) {
                                dataMirr.put(key,dataObjProd.get(key));
                            }
                        }
                    }
                    //processGetSourceAccount();
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    processSourceAccountWM();

                    if (dataObjProd.has("investmentAccountNo")) {
                        investmentAccountNo = dataObjProd.getString("investmentAccountNo");
                    }

                    if (dataObjProd.has("productName")) {
                        String productName = dataObjProd.getString("productName");
                        tvTitleProdNextSale.setText(productName);
                        dataMirr.put("productName",productName);
                    }

                    if (dataObjProd.has("productCode")) {
                        productCode = dataObjProd.getString("productCode");
                    }

                    if (dataObjProd.has("investmentAccountNo")) {
                        invAccountNo = dataObjProd.getString("investmentAccountNo");
                    }

                    if (dataObjProd.has("fundHouseName")) {
                        String fundHouseName = dataObjProd.getString("fundHouseName");
                        tvPtProdNextSale.setText(fundHouseName);
                        dataMirr.put("fundHouseName",fundHouseName);
                    }

                    if (dataObjProd.has("subsFee")) {
                        double subsFee = dataObjProd.getDouble("subsFee");
                        String imbalHasil = subsFee+" %";
                        tvFeeSale.setText(imbalHasil);
                        dataMirr.put("subsFee",subsFee);
                    }

                    if (dataObjProd.has("tax")) {
                        double tax = dataObjProd.getDouble("tax");
                        String taxS = tax+" %";
                        tvTaxFeeSale.setText(taxS);
                        dataMirr.put("tax",tax);
                    }

                    long nomNav = 0;
                    if (dataObjProd.has("nav")) {
                        nomNav = dataObjProd.getLong("nav");
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
                            if (dataObjProd.has("currency")) {
                                currency = dataObjProd.getString("currency");
                            }
                            nominal = currency + " " + formatted;
                        }
                        tvNABSale.setText(nominal);
                    }

                    if (dataObjProd.has("navDate")) {
                        String navDate = dataObjProd.getString("navDate");
                        if (navDate.contains("T")) {
                            String[] sp = navDate.split("T");
                            String dates = sp[0];
                            String inputFormat = "yyyy-MM-dd";
                            String outputFormat = "dd/MM/yy";
                            String NABDate = ConveredDate(dates, inputFormat, outputFormat);

                            String labelNab = mContext.getResources().getString(R.string.nab);
                            labelNab += " ("+NABDate+")";
                            tvLabelNABSale.setText(labelNab);
                        }
                    }

                    if (dataObjProd.has("units")) {
                        long units = dataObjProd.getLong("units");
                        diffUnits = (double) units / 10000;

                        NumberFormat formatter = null;
                        if (sessions.getLANG().equals("id")) {
                            formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                        } else {
                            formatter = NumberFormat.getInstance(new Locale("en", "US"));
                        }
                        formatter.setMinimumFractionDigits(4);
                        String formatUnit = formatter.format(diffUnits);

                        String txtUnits = formatUnit + " Unit";
                        tvUnitSelf.setText(txtUnits);
                        dataMirr.put("units",units);
                    }

                    int valueDefPercent = 10;
                    if (Sale.equals("full")) {
                        valueDefPercent = 100;
                    }
                    double diff = (diffUnits * valueDefPercent) / 100;
                    double perCentUnit = (diff / diffUnits) * 100;
                    perCent = perCentUnit;
                    double diffSisa = diffUnits - diff;
                    double perCentSisaUnit = (diffSisa / diffUnits) * 100;

                    NumberFormat nf = null;
                    if (sessions.getLANG().equals("id")) {
                        nf = NumberFormat.getInstance(new Locale("id", "ID"));
                    } else {
                        nf = NumberFormat.getInstance(new Locale("en", "US"));
                    }
                    nf.setMinimumFractionDigits(4);
                    DecimalFormatSymbols sym = ((DecimalFormat) nf).getDecimalFormatSymbols();
                    char decSeparator = sym.getDecimalSeparator();
                    if (Character.toString(decSeparator).equals(",")) {
                        edTotalUnitDisburs.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
                    } else {
                        edTotalUnitDisburs.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                    }

                    NumberFormat nf2 = null;
                    if (sessions.getLANG().equals("id")) {
                        nf2 = NumberFormat.getInstance(new Locale("id", "ID"));
                    } else {
                        nf2 = NumberFormat.getInstance(new Locale("en", "US"));
                    }
                    nf2.setMinimumFractionDigits(2);
                    String perCentUnitS = nf2.format(perCentUnit);
                    String perCentSisaUnitS = nf2.format(perCentSisaUnit);

                    diffS = nf.format(diff);
                    String diffSSisa = nf.format(diffSisa);

                    String diffSPercent = diffS + " (" + perCentUnitS + "%)";
                    diffSSisa = diffSSisa+" ("+perCentSisaUnitS+"%)";

                    edTotalUnitDisburs.setText(diffSPercent);
                    dataMirr.put("jumlahunitpencairan",diffSPercent);
                    tvRemainingUnits.setText(diffSSisa);
                    dataMirr.put("sisaunit",diffSSisa);

                    edTotalUnitDisburs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                String totalUnit = edTotalUnitDisburs.getText().toString();
                                if (totalUnit.contains("(")) {
                                    int indx = totalUnit.indexOf("(");
                                    totalUnit = totalUnit.substring(0,indx).trim();
                                }
                                edTotalUnitDisburs.setSelection(totalUnit.length());
                            }
                        }
                    });
                    edTotalUnitDisburs.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String val = s.toString().trim();
                            if (!val.isEmpty()) {
                                if (val.contains("(")) {
                                    int indx = val.indexOf("(");
                                    val = val.substring(0,indx).trim();
                                }
                                if (!val.isEmpty()) {
                                    edTotalUnitDisburs.removeTextChangedListener(this);

                                    NumberFormat nf = null;
                                    if (sessions.getLANG().equals("id")) {
                                        nf = NumberFormat.getInstance(new Locale("id", "ID"));
                                    } else {
                                        nf = NumberFormat.getInstance(new Locale("en", "US"));
                                    }
                                    //nf.setMinimumFractionDigits(4);

                                    NumberFormat nf2 = null;
                                    if (sessions.getLANG().equals("id")) {
                                        nf2 = NumberFormat.getInstance(new Locale("id", "ID"));
                                    } else {
                                        nf2 = NumberFormat.getInstance(new Locale("en", "US"));
                                    }
                                    nf2.setMinimumFractionDigits(2);

                                    String cleanString = "";
                                    String nom = "";
                                    String Dec = "";
                                    String separator = "";
                                    String nomHit = "";
                                    if (Character.toString(decSeparator).equals(",")) {
                                        cleanString = val.replaceAll("[$.]", "");
                                        if (cleanString.contains(",")) {
                                            separator = ",";
                                            String[] sp = cleanString.split(separator);
                                            if (sp.length == 1) {
                                                nom = sp[0].trim();
                                                nomHit = nom;
                                            } else {
                                                nom = sp[0].trim();
                                                Dec = sp[1].trim();
                                                nomHit = nom+"."+Dec;
                                            }
                                        } else {
                                            nom = cleanString;
                                            nomHit = nom;
                                        }
                                    } else if (Character.toString(decSeparator).equals(".")) {
                                        cleanString = val.replaceAll("[$,]", "");
                                        if (cleanString.contains(".")) {
                                            separator = ".";
                                            String[] sp = cleanString.split(separator);
                                            if (sp.length == 1) {
                                                nom = sp[0].trim();
                                                nomHit = nom;
                                            } else {
                                                nom = sp[0].trim();
                                                Dec = sp[1].trim();
                                                nomHit = nom+"."+Dec;
                                            }
                                        } else {
                                            nom = cleanString;
                                            nomHit = nom;
                                        }
                                    }

                                    if (!nomHit.isEmpty()) {
                                        BigDecimal parsed = new BigDecimal(nomHit);
                                        String nfS = nf.format(parsed);

                                        double dVal = Double.valueOf(nomHit);
                                        perCent = (dVal / diffUnits) * 100;
                                        double diffSisa = diffUnits - dVal;
                                        double perCentSisaUnit = (diffSisa / diffUnits) * 100;
                                        String diffSSisa = nf.format(diffSisa);

                                        String perCentS = nf2.format(perCent);
                                        String perCentSisaUnitS = nf2.format(perCentSisaUnit);

                                        if (!separator.isEmpty() && Dec.isEmpty()) {
                                            nfS = nfS + separator;
                                        }

                                        if (Sale.equals("part")) {
                                            if (perCent < 10 || perCent > 50) {
                                                btnNext.setEnabled(false);
                                                btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                            } else {
                                                if (flagChkState && flagSourceAccount) {
                                                    btnNext.setEnabled(true);
                                                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                } else {
                                                    btnNext.setEnabled(false);
                                                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                }
                                            }
                                        }

                                        String newVal = nfS + " (" + perCentS + "%)";
                                        diffSSisa = diffSSisa + " (" + perCentSisaUnitS + "%)";
                                        tvRemainingUnits.setText(diffSSisa);

                                        edTotalUnitDisburs.setText(newVal);
                                        try {
                                            dataMirr.put("jumlahunitpencairan", newVal);
                                            dataMirr.put("sisaunit", diffSSisa);
                                            formMirr.put(labelDetailKey, dataMirr);
                                            mirrObj.put(labelTrx, formMirr);
                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        edTotalUnitDisburs.setSelection(nfS.length());
                                        edTotalUnitDisburs.addTextChangedListener(this);
                                    }
                                }
                            }
                        }
                    });

                    chkState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            flagChkState = checked;

                            try {
                                dataMirr.put("snk",checked);

                                formMirr.put(labelDetailKey,dataMirr);
                                mirrObj.put(labelTrx,formMirr);
                                ConnectionRabbitHttp.mirroringKey(mirrObj);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if (checked && flagSourceAccount) {
                                if (Sale.equals("part")) {
                                    if (perCent < 10 || perCent > 50) {
                                        btnNext.setEnabled(false);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                    } else {
                                        btnNext.setEnabled(true);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                    }
                                } else {
                                    btnNext.setEnabled(true);
                                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                }
                            } else {
                                btnNext.setEnabled(false);
                                btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                            }
                        }
                    });

                    formMirr.put(labelDetailKey,dataMirr);
                    mirrObj.put(labelTrx,formMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);

                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String disbursement = edTotalUnitDisburs.getText().toString();
                                String sUnits = disbursement;
                                if (disbursement.contains("(")) {
                                    String[] sp = disbursement.split("\\(");
                                    sUnits = sp[0].trim();
                                }
                                objEl.put("disbursement_units",disbursement);
                                objEl.put("amountToRedeem",sUnits);
                                objEl.put("sale",Sale);

                                dataMirr.put("amountToRedeem",sUnits);
                                formMirr.put(labelDetailKey,dataMirr);
                                mirrObj.put(labelTrx,formMirr);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            /*if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            processInquiryWMFeeTax();*/
                            Bundle bundle = new Bundle();
                            bundle.putString("dataSales", objEl.toString());
                            bundle.putString("mirrObj",mirrObj.toString());
                            Fragment fragment = new frag_wm_trans_confirm();
                            fragment.setArguments(bundle);
                            getFragmentPage(fragment);
                        }
                    });
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            btnSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SaleSheetBottom();
                }
            });
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

    private void SaleSheetBottom() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View views = inflater.inflate(R.layout.bottomsheet_sale_wm, null);
        bottomSheetDialog.setContentView(views);

        RadioGroup rgSaleUnit = (RadioGroup) views.findViewById(R.id.rgSaleUnit);
        Button btnNext = (Button) views.findViewById(R.id.btnNext);

        rgSaleUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.salePart) {
                    Sale = "part";
                    try {
                        dataMirr.put("alert", "half");
                        for (Iterator<String> iter = dataObjProd.keys(); iter.hasNext(); ) {
                            if (iter.hasNext()) {
                                String key = iter.next();
                                dataMirr.put(key, dataObjProd.get(key));
                            }
                        }

                        formMirr.put(labelDetailKey, dataMirr);
                        mirrObj.put(labelTrx, formMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    btnNext.setEnabled(true);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                } else if (checkedId == R.id.saleFull) {
                    Sale = "full";
                    try {
                        dataMirr.put("alert", "all");
                        for (Iterator<String> iter = dataObjProd.keys(); iter.hasNext(); ) {
                            if (iter.hasNext()) {
                                String key = iter.next();
                                dataMirr.put(key, dataObjProd.get(key));
                            }
                        }

                        formMirr.put(labelDetailKey, dataMirr);
                        mirrObj.put(labelTrx, formMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("dataObjProd", dataObjProd.toString());
                bundle.putString("sale", Sale);
                Fragment fragment = new frag_wm_detail_porto();
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });

        FrameLayout frameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (frameLayout != null) {
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setPeekHeight(500);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        bottomSheetDialog.show();
    }

    private void processInquiryWMFeeTax() {

        String totalUnit = edTotalUnitDisburs.getText().toString();
        int indx = totalUnit.indexOf("(");
        String totalUnits = totalUnit.substring(0, indx).trim();
        String cleanString = totalUnits.replaceAll("[$,.]", "");
        long tUnits = Long.valueOf(cleanString);

        JSONObject jsons = new JSONObject();
        try {
            jsons.put("productCode",productCode);
            jsons.put("transactionCategory","RED");
            jsons.put("cif",sessions.getNoCIF());
            jsons.put("invAccountNo",invAccountNo);
            jsons.put("units",cleanString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().InquiryWMFeeTax(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        long totalFeeAmount = 0;
                        long totalTaxAmount = 0;
                        if (dataBody.has("totalFeeAmount")) {
                            totalFeeAmount = dataBody.getLong("totalFeeAmount");
                        }
                        if (dataBody.has("totalTaxAmount")) {
                            totalTaxAmount = dataBody.getLong("totalTaxAmount");
                        }

                        try {
                            objEl.put("totalFeeAmount",totalFeeAmount);
                            objEl.put("totalTaxAmount",totalTaxAmount);

                            Bundle bundle = new Bundle();
                            bundle.putString("dataSales", objEl.toString());
                            bundle.putString("mirrObj",mirrObj.toString());
                            Fragment fragment = new frag_wm_trans_confirm();
                            fragment.setArguments(bundle);
                            getFragmentPage(fragment);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

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
            }
        });
    }

    private void processSourceAccountWM() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",sessions.getNoCIF());
            jsons.put("bahasa",sessions.getLANG());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().SourcAccountWM(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        JSONObject objData = dataObj.getJSONObject("data");
                        JSONArray dataArr = objData.getJSONArray("portotabungan");
                        dataDropDownSource = new ArrayList<>();
                        int len = dataArr.length() + 1;
                        String[] sourceAcc = new String[len];
                        String textSelect = getString(R.string.choose_source_fund);
                        sourceAcc[0] = textSelect;
                        dataDropDownSource.add(new FormSpin(0,"0",textSelect,textSelect));
                        int loopSource = 1;
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = i + 1;

                            String prodName = dataArr.getJSONObject(i).getString("prodName").replace("R/K","").trim();
                            String prodCode = dataArr.getJSONObject(i).getString("prodCode");
                            if (prodCode.equals("T21")) {
                                continue;
                            }
                            if (dataArr.getJSONObject(i).has("acctStatus")) {
                                String acctStatus = dataArr.getJSONObject(i).getString("acctStatus");
                                if (!acctStatus.equals("A")) {
                                    continue;
                                }
                            }
                            String accountNo = dataArr.getJSONObject(i).getString("accountNo");
                            String accountName = dataArr.getJSONObject(i).getString("accountName");
                            String acctCur = dataArr.getJSONObject(i).getString("acctCur");
                            String availBalance = dataArr.getJSONObject(i).getString("availBalance");
                            String accountType = dataArr.getJSONObject(i).getString("accountType");
                            availBalance = availBalance.substring(0,availBalance.length() - 2);

                            if (acctCur.equals("IDR")) {
                                acctCur = "Rp.";
                            }

                            Double d = Double.valueOf(availBalance);
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);
                            String formattedNumber = formatter.format(d);
                            String labelIdn = prodName + "\n" + accountNo + " - " + accountName + "\n" + acctCur + " " + formattedNumber;
                            sourceAcc[loopSource] = labelIdn;
                            loopSource++;

                            dataDropDownSource.add(new FormSpin(idData,accountType,labelIdn,labelIdn));
                        }
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDownSource);
                        spinSourceAccount.setAdapter(adapterSourceAcc);

                        spinSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                FormSpin dataSpin = (FormSpin) spinSourceAccount.getSelectedItem();
                                String results = dataSpin.getName();
                                if (results.indexOf("\n") > 0) {
                                    flagSourceAccount = true;
                                    try {
                                        String represults = results.replaceAll("\n"," / ");
                                        objEl.put("rekeningsumberdana",represults);

                                        dataMirr.put("rekeningsumberdana",represults);
                                        formMirr.put(labelDetailKey,dataMirr);
                                        mirrObj.put(labelTrx,formMirr);
                                        ConnectionRabbitHttp.mirroringKey(mirrObj);

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    if (flagChkState && flagSourceAccount) {
                                        btnNext.setEnabled(true);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                    } else {
                                        btnNext.setEnabled(false);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                                    }
                                } else {
                                    flagSourceAccount = false;
                                    btnNext.setEnabled(false);
                                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String dataS = null;
                        try {
                            dataS = response.errorBody().string();
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
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