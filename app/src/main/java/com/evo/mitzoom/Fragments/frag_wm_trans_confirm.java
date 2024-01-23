package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.collection.LongSparseArray;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wm_trans_confirm extends Fragment {

    private static final String TAG = "frag_wm_trans_confirm";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private String labelserv = "";
    private JSONObject dataPurchase = null;
    private JSONObject dataSales = null;
    private NestedScrollView nestedScroll;
    private TextView tvLabelSource;
    private TextView tvSourceAccount;
    private TextView tvCatgProd;
    private TextView tvInfoProdVal;
    private TextView tvManageInvesVal;
    private LinearLayout llPurchase;
    private LinearLayout llPurchasePeriod;
    private TextView tvPurchase;
    private LinearLayout llPeriodically;
    private TextView tvDates;
    private TextView tvPeriod;
    private TextView tvLabelPurchase;
    private TextView tvPurchasCosts;
    private TextView tvLabelTax;
    private TextView tvTax;
    private TextView tvNominalInvest;
    private TextView TvTotal;
    private LinearLayout llSale;
    private TextView tvLabelNABSale;
    private TextView tvNABSale;
    private TextView tvAmountSale;
    private TextView tvTotalDisbursementUnit;
    private TextView tvPercentDisbursementUnit;
    private Button btnNext;
    private LinearLayout TopBar;
    private LinearLayout ll_head;
    private TextView tvFotoKTP;
    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private PinView otp;
    private BroadcastReceiver smsReceiver = null;
    private String numberOTP = "";
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private String transactionId = "";
    private String formCode = "WM SUB";
    private JSONObject dataTrx = null;
    private String no_handphone = "";
    private String productCode = "";
    private String amountInvest = "0";
    private String transactionCategory = "";
    private String custAccountNo = "";
    private long totalFeeAmount = 0;
    private String spDates = "0";
    private String tenor = "0";
    private JSONObject mirrObj = new JSONObject();
    private String inAccountNo = "";
    private boolean isRedeemAll = false;
    private JSONObject dataNasabahObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("dataPurchase")) {
                String dataPurchaseS = getArguments().getString("dataPurchase");
                try {
                    dataPurchase = new JSONObject(dataPurchaseS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("dataSales")) {
                String dataSaleS = getArguments().getString("dataSales");
                try {
                    dataSales = new JSONObject(dataSaleS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            if (getArguments().containsKey("mirrObj")) {
                String mirrObjS = getArguments().getString("mirrObj");
                try {
                    mirrObj = new JSONObject(mirrObjS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String dataNasabah = sessions.getNasabah();
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                /*if (dataNasabahObj.has("namaLengkap")) {
                    namaLengkap = dataNasabahObj.getString("namaLengkap");
                }
                if (dataNasabahObj.has("alamat")) {
                    alamat = dataNasabahObj.getString("alamat");
                }*/
                if (dataNasabahObj.has("noHp")) {
                    no_handphone = dataNasabahObj.getString("noHp");
                }
                /*if (dataNasabahObj.has("nik")) {
                    nik = dataNasabahObj.getString("nik");
                }
                if (dataNasabahObj.has("branchCode")) {
                    branchCode = dataNasabahObj.getString("branchCode");
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_trans_confirm, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);
        scrollOTP = (NestedScrollView) views.findViewById(R.id.scrollOTP);
        inclOTP = views.findViewById(R.id.inclOTP);
        imgDialog = (ImageView) views.findViewById(R.id.imgDialog);
        textTitleOTP = (TextView) views.findViewById(R.id.textIBMB);
        btnVerifikasi = (Button) views.findViewById(R.id.btnVerifikasi);
        TimerOTP = (TextView) views.findViewById(R.id.timer_otp);
        Resend_Otp = (TextView) views.findViewById(R.id.btn_resend_otp);
        otp = (PinView) views.findViewById(R.id.otp);

        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);
        tvLabelSource = (TextView) views.findViewById(R.id.tvLabelSource);
        tvSourceAccount = (TextView) views.findViewById(R.id.tvSourceAccount);
        tvCatgProd = (TextView) views.findViewById(R.id.tvCatgProd);
        tvInfoProdVal = (TextView) views.findViewById(R.id.tvInfoProdVal);
        tvManageInvesVal = (TextView) views.findViewById(R.id.tvManageInvesVal);
        llPurchasePeriod = (LinearLayout) views.findViewById(R.id.llPurchasePeriod);
        tvPurchase = (TextView) views.findViewById(R.id.tvPurchase);
        llPeriodically = (LinearLayout) views.findViewById(R.id.llPeriodically);
        tvDates = (TextView) views.findViewById(R.id.tvDates);
        tvPeriod = (TextView) views.findViewById(R.id.tvPeriod);
        tvLabelPurchase = (TextView) views.findViewById(R.id.tvLabelPurchase);
        tvPurchasCosts = (TextView) views.findViewById(R.id.tvPurchasCosts);
        tvLabelTax = (TextView) views.findViewById(R.id.tvLabelTax);
        tvTax = (TextView) views.findViewById(R.id.tvTax);
        llPurchase = (LinearLayout) views.findViewById(R.id.llPurchase);
        tvNominalInvest = (TextView) views.findViewById(R.id.tvNominalInvest);
        TvTotal = (TextView) views.findViewById(R.id.TvTotal);
        llSale = (LinearLayout) views.findViewById(R.id.llSale);
        tvLabelNABSale = (TextView) views.findViewById(R.id.tvLabelNABSale);
        tvNABSale = (TextView) views.findViewById(R.id.tvNABSale);
        tvAmountSale = (TextView) views.findViewById(R.id.tvAmountSale);
        tvTotalDisbursementUnit = (TextView) views.findViewById(R.id.tvTotalDisbursementUnit);
        tvPercentDisbursementUnit = (TextView) views.findViewById(R.id.tvPercentDisbursementUnit);

        btnNext = (Button) views.findViewById(R.id.btnNext);

        return views;
    }

    @Override
    public void onResume() {
        super.onResume();

        smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                numberOTP = "";
                String dataSMS = intent.getExtras().getString("smsMessage");
                String[] sp = dataSMS.split(" ");
                for (int i = 0; i < sp.length; i++) {
                    String word = sp[i];
                    if(word.matches("\\d+(?:\\.\\d+)?")) {
                        numberOTP = word.replaceAll("[^0-9]", "");
                        if (numberOTP.length() == 6) {
                            otp.setText(numberOTP);
                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(smsReceiver,new IntentFilter("getotp"));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (dataPurchase != null) {
            llSale.setVisibility(View.GONE);
            ConnectionRabbitHttp.mirroringEndpoint(291);
            try {
                String currency = "Rp";
                long totalTaxAmount = 0;
                NumberFormat formatter = null;
                if (sessions.getLANG().equals("id")) {
                    formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                } else {
                    formatter = NumberFormat.getInstance(new Locale("en", "US"));
                }

                if (!sessions.getLANG().equals("id")) {
                    if (dataPurchase.has("currency")) {
                        currency = dataPurchase.getString("currency");
                    }
                }

                if (dataPurchase.has("productCode")) {
                    productCode = dataPurchase.getString("productCode");
                }

                if (dataPurchase.has("rekeningsumberdana")) {
                    String sourceAccount = dataPurchase.getString("rekeningsumberdana");
                    if (sourceAccount.contains(" / ")) {
                        String[] sp = sourceAccount.split(" / ");
                        String no_nama_Rek = sp[1].trim();
                        String[] sp2 = no_nama_Rek.split("-");
                        custAccountNo = sp2[0].trim();
                        tvSourceAccount.setText(no_nama_Rek);
                    }
                }
                if (dataPurchase.has("category")) {
                    String category = dataPurchase.getString("category");
                    tvCatgProd.setText(category);
                }
                if (dataPurchase.has("productName")) {
                    String productName = dataPurchase.getString("productName");
                    tvInfoProdVal.setText(productName);
                }
                if (dataPurchase.has("fundHouseName")) {
                    String fundHouseName = dataPurchase.getString("fundHouseName");
                    tvManageInvesVal.setText(fundHouseName);
                }
                if (dataPurchase.has("transactionCategory")) {
                    transactionCategory = dataPurchase.getString("transactionCategory");
                }
                if (dataPurchase.has("pembelian")) {
                    formCode = "WM INST";
                    llPurchasePeriod.setVisibility(View.VISIBLE);
                    llPeriodically.setVisibility(View.VISIBLE);

                    String purchase = dataPurchase.getString("pembelian");
                    tvPurchase.setText(purchase);

                    spDates = dataPurchase.getString("cycleDate");
                    String spPeriode = dataPurchase.getString("spPeriode");
                    tenor = dataPurchase.getString("tenor");
                    tvDates.setText(spDates);
                    tvPeriod.setText(spPeriode);
                }
                if (dataPurchase.has("totalFeeAmount")) {
                    totalFeeAmount = dataPurchase.getLong("totalFeeAmount");

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(totalFeeAmount);
                    
                    String FeeAmount = currency+" "+formatted;
                    
                    tvPurchasCosts.setText(FeeAmount);
                }
                if (dataPurchase.has("totalTaxAmount")) {
                    totalTaxAmount = dataPurchase.getLong("totalTaxAmount");

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(totalTaxAmount);

                    String FeeAmount = currency+" "+formatted;

                    tvTax.setText(FeeAmount);
                }
                if (dataPurchase.has("amountInvest")) {
                    amountInvest = dataPurchase.getString("amountInvest");

                    long amountLong = Long.parseLong(amountInvest);

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(amountLong);

                    String amount = currency+" "+formatted;

                    tvNominalInvest.setText(amount);
                }

                long totalAmount = Long.parseLong(amountInvest) + totalFeeAmount + totalTaxAmount;
                formatter.setMinimumFractionDigits(2);
                String formatted = formatter.format(totalAmount);

                JSONObject formBuyMirr = mirrObj.getJSONObject("reksadana");
                JSONObject dataFormBuyMirr = formBuyMirr.getJSONObject("formpembelian");

                dataFormBuyMirr.put("subsFee",dataPurchase.getLong("totalFeeAmount"));
                dataFormBuyMirr.put("tax",dataPurchase.getLong("totalTaxAmount"));
                dataFormBuyMirr.put("totalFeeAmount",dataPurchase.getLong("totalFeeAmount"));
                dataFormBuyMirr.put("totalTaxAmount",dataPurchase.getLong("totalTaxAmount"));

                dataFormBuyMirr.put("total",formatted);
                formBuyMirr.put("formpembelian",dataFormBuyMirr);
                mirrObj.put("reksadana",formBuyMirr);
                ConnectionRabbitHttp.mirroringKey(mirrObj);

                String total_Amount = currency+" "+formatted;

                TvTotal.setText(total_Amount);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (dataSales != null) {
            formCode = "WM RED";
            ConnectionRabbitHttp.mirroringEndpoint(303);
            try {

//                tvLabelPurchase.setText(mContext.getResources().getString(R.string.biaya_penjualan));
//                tvLabelTax.setText(mContext.getResources().getString(R.string.tax_cost_sales));

                tvLabelPurchase.setVisibility(View.GONE);
                tvPurchasCosts.setVisibility(View.GONE);
                tvLabelTax.setVisibility(View.GONE);
                tvTax.setVisibility(View.GONE);

                llSale.setVisibility(View.VISIBLE);
                llPurchase.setVisibility(View.GONE);

                String currency = "Rp";
                long totalTaxAmount = 0;
                NumberFormat formatter = null;
                if (sessions.getLANG().equals("id")) {
                    formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                } else {
                    formatter = NumberFormat.getInstance(new Locale("en", "US"));
                }

                if (!sessions.getLANG().equals("id")) {
                    if (dataSales.has("currency")) {
                        currency = dataSales.getString("currency");
                    }
                }

                if (dataSales.has("rekeningsumberdana")) {
                    String sourceAccount = dataSales.getString("rekeningsumberdana");
                    if (sourceAccount.contains(" / ")) {
                        String[] sp = sourceAccount.split(" / ");
                        String no_nama_Rek = sp[1].trim();
                        String[] sp2 = no_nama_Rek.split("-");
                        custAccountNo = sp2[0].trim();
                        tvSourceAccount.setText(no_nama_Rek);
                    }
                }

                if (dataSales.has("sale")) {
                    String flagSale = dataSales.getString("sale");
                    if (flagSale.equals("full")) {
                        isRedeemAll = true;
                    }
                }

                if (dataSales.has("category")) {
                    String category = dataSales.getString("category");
                    tvCatgProd.setText(category);
                }

                if (dataSales.has("productCode")) {
                    productCode = dataSales.getString("productCode");
                }

                if (dataSales.has("productName")) {
                    String productName = dataSales.getString("productName");
                    tvInfoProdVal.setText(productName);
                }
                if (dataSales.has("fundHouseName")) {
                    String fundHouseName = dataSales.getString("fundHouseName");
                    tvManageInvesVal.setText(fundHouseName);
                }

                if (dataSales.has("investmentAccountNo")) {
                    inAccountNo = dataSales.getString("investmentAccountNo");
                }

                if (dataSales.has("totalFeeAmount")) {
                    totalFeeAmount = dataSales.getLong("totalFeeAmount");

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(totalFeeAmount);

                    String FeeAmount = currency+" "+formatted;

                    tvPurchasCosts.setText(FeeAmount);
                } else {
                    tvLabelPurchase.setVisibility(View.GONE);
                    tvPurchasCosts.setVisibility(View.GONE);
                }
                if (dataSales.has("totalTaxAmount")) {
                    totalTaxAmount = dataSales.getLong("totalTaxAmount");

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(totalTaxAmount);

                    String FeeAmount = currency+" "+formatted;

                    tvTax.setText(FeeAmount);
                } else {
                    tvLabelTax.setVisibility(View.GONE);
                    tvTax.setVisibility(View.GONE);
                }

                if (dataSales.has("totalFeeAmount")) {
                    totalFeeAmount = dataSales.getLong("totalFeeAmount");

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(totalFeeAmount);

                    String FeeAmount = currency+" "+formatted;

                    tvPurchasCosts.setText(FeeAmount);
                }
                if (dataSales.has("totalTaxAmount")) {
                    totalTaxAmount = dataSales.getLong("totalTaxAmount");

                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(totalTaxAmount);

                    String FeeAmount = currency+" "+formatted;

                    tvTax.setText(FeeAmount);
                }

                long nomNav = 0;
                if (dataSales.has("nav")) {
                    nomNav = dataSales.getLong("nav");
                    formatter.setMinimumFractionDigits(2);
                    String formatted = formatter.format(nomNav);

                    String nominal = "";
                    if (sessions.getLANG().equals("id")) {
                        nominal = "Rp. " + formatted;
                    } else {
                        currency = "USD";
                        if (dataSales.has("currency")) {
                            currency = dataSales.getString("currency");
                        }
                        nominal = currency + " " + formatted;
                    }
                    tvNABSale.setText(nominal);
                }

                if (dataSales.has("navDate")) {
                    String navDate = dataSales.getString("navDate");
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

                long nomUnits = 0;
                String jumlahUnitPencairan2 = "";
                if (dataSales.has("disbursement_units")) {
                    String disbursement = dataSales.getString("disbursement_units");
                    if (disbursement.contains("(")) {
                        String[] sp = disbursement.split("\\(");
                        String sUnits = sp[0].trim();
                        jumlahUnitPencairan2 = sUnits;
                        String cleanUnits = sUnits.replaceAll("[$,.]", "");
                        nomUnits = Long.valueOf(cleanUnits);
                        String txt = sUnits + " Unit";
                        tvTotalDisbursementUnit.setText(txt);

                        String txt2 = "(" + sp[1].trim();
                        tvPercentDisbursementUnit.setText(txt2);
                    } else {
                        tvTotalDisbursementUnit.setText(disbursement);
                    }

                }

                long totalAmount = nomNav * nomUnits;
                formatter.setMinimumFractionDigits(2);
                String formatUnits = currency+" "+formatter.format(totalAmount);

                tvAmountSale.setText(formatUnits);

                JSONObject formJualMirr = mirrObj.getJSONObject("reksadana");
                JSONObject dataFormJualMirr = formJualMirr.getJSONObject("formpenjualan");

                dataFormJualMirr.put("jumlahunitpencairan",nomUnits);
                dataFormJualMirr.put("jumlahunitpencairan2",jumlahUnitPencairan2);
                dataFormJualMirr.put("nav",nomNav);
                formJualMirr.put("formpenjualan",dataFormJualMirr);
                mirrObj.put("reksadana",formJualMirr);
                ConnectionRabbitHttp.mirroringKey(mirrObj);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processSendOTP();
            }
        });

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

    private void APISaveForm() {
        dataTrx = new JSONObject();
        JSONObject dataObjSave = new JSONObject();

        try {
            dataObjSave.put("formCode", formCode);
            dataObjSave.put("idDips", sessions.getKEY_IdDips());
            if (dataPurchase != null) {
                long netAmount = Long.parseLong(amountInvest);
                long navPerUnit = 0;
                if (dataPurchase.has("navPerUnit")) {
                    navPerUnit = dataPurchase.getLong("navPerUnit");
                }
                long feeAmount = totalFeeAmount * 100;
                long valUnits = (netAmount * 100) / navPerUnit;
                dataPurchase.put("cif",sessions.getNoCIF());
                dataPurchase.put("charges", "0");
                dataPurchase.put("custAccountNo",custAccountNo);
                dataPurchase.put("feeAmount", feeAmount);
                dataPurchase.put("host", "WMBE");
                dataPurchase.put("inAccountNo", "");
                dataPurchase.put("isRedeemAll", false);
                dataPurchase.put("netAmount", netAmount);
                dataPurchase.put("productCodeTo", "");
                dataPurchase.put("units", valUnits);
                dataObjSave.put("payload", dataPurchase);
            }
            else if (dataSales != null) {
                long netAmount = Long.parseLong(amountInvest);
                String totalDis = tvTotalDisbursementUnit.getText().toString();
                totalDis = totalDis.replace("Unit","").trim();
                String cleanString = totalDis.replaceAll("[$,.]", "");
                long unitVal = Long.parseLong(cleanString);
                long feeAmount = totalFeeAmount * 100;
                dataSales.put("cif",sessions.getNoCIF());
                dataSales.put("transactionCategory", "RED");
                dataSales.put("inAccountNo", inAccountNo);
                dataSales.put("netAmount", netAmount);
                dataSales.put("isRedeemAll", isRedeemAll);
                dataSales.put("charges", "0");
                dataSales.put("custAccountNo",custAccountNo);
                dataSales.put("feeAmount", totalFeeAmount);
                dataSales.put("host", "WMBE");
                dataSales.put("productCodeTo", "");
                dataSales.put("units", unitVal);
                dataObjSave.put("payload", dataSales);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjSave.toString());
        Server.getAPIService().saveForm(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        String idForm = dataObj.getJSONObject("data").getString("idForm");
                        JSONObject idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);

                        JSONObject formBuyMirr = new JSONObject();
                        formBuyMirr.put("formpembelian",idFormObj);
                        dataTrx.put("reksadana",formBuyMirr);
                        ConnectionRabbitHttp.mirroringKey(dataTrx);

                        if (dataPurchase != null) {
                            processApprovalStatus(idForm,dataPurchase);
                        } else if (dataSales != null) {
                            processApprovalStatus(idForm,dataSales);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    running = true;
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    try {
                        String msg = "";
                        if (response.body() != null) {
                            String dataS = response.body().toString();
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } else {
                            if (response.errorBody().toString().isEmpty()) {
                                String dataS = response.errorBody().toString();
                                JSONObject dataObj = new JSONObject(dataS);
                                if (dataObj.has("message")) {
                                    msg = dataObj.getString("message");
                                }
                            } else {
                                String dataS = null;
                                dataS = response.errorBody().string();
                                JSONObject dataObj = new JSONObject(dataS);
                                if (dataObj.has("message")) {
                                    msg = dataObj.getString("message");
                                }
                            }
                        }
                        Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                running = true;
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processApprovalStatus(String idForm,JSONObject objSaveForm) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().ApprovalStatus(idForm,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                DipsSwafoto.tvLoading.setVisibility(View.GONE);
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });

                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }

                        if (dataObj.getJSONObject("data").has("status")) {
                            String getStatus = dataObj.getJSONObject("data").getString("status");
                            if (getStatus.equals("reject")) {
                                popUpReject();
                            } else {
                                try {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("idForm", dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
                                    if (dataPurchase != null) {
                                        if (formCode.equals("WM SUB")) {
                                            bundle.putString("typeTransaction", "sub");
                                        } else {
                                            bundle.putString("typeTransaction", "inst");
                                        }
                                        ConnectionRabbitHttp.mirroringEndpoint(311);
                                    } else if (dataSales != null) {
                                        bundle.putString("typeTransaction", "red");
                                        ConnectionRabbitHttp.mirroringEndpoint(312);
                                    }
                                    Fragment fragment = new frag_wm_trans_resi();
                                    fragment.setArguments(bundle);
                                    getFragmentPage(fragment);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //processInquiryWMRegister(objSaveForm);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processApprovalStatus(idForm,objSaveForm);
                                }
                            });
                        }
                    },10000);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processApprovalStatus(idForm,objSaveForm);
                            }
                        });
                    }
                },10000);
            }
        });
    }

    private void processInquiryWMRegister(JSONObject objSaveForm) {
        JSONObject jsons = new JSONObject();
        try {
            long netAmount = Long.parseLong(amountInvest);
            jsons.put("idForm",dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
            jsons.put("cif",sessions.getNoCIF());
            jsons.put("productCode",productCode);
            jsons.put("custAccountNo",custAccountNo);
            if (dataSales != null) {
                String totalDis = tvTotalDisbursementUnit.getText().toString();
                totalDis = totalDis.replace("Unit","").trim();
                String cleanString = totalDis.replaceAll("[$,.]", "");
                long unitVal = Long.parseLong(cleanString);

                jsons.put("transactionCategory", "RED");
                jsons.put("inAccountNo", inAccountNo);
                jsons.put("netAmount", netAmount);
                jsons.put("units", unitVal);
                jsons.put("isRedeemAll", isRedeemAll);
                jsons.put("charges", "0");
                jsons.put("productCodeTo", "");
                jsons.put("remark", dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
                jsons.put("refNo", dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
                jsons.put("host", "WMBE");
                jsons.put("feeAmount", totalFeeAmount);
            }
            else {
                long navPerUnit = 0;
                if (dataPurchase.has("navPerUnit")) {
                    navPerUnit = dataPurchase.getLong("navPerUnit");
                }
                long feeAmount = totalFeeAmount * 100;
                long valUnits = (netAmount * 100) / navPerUnit;
                jsons.put("transactionCategory", transactionCategory);
                if (formCode.equals("WM SUB")) {
                    jsons.put("inAccountNo", "");
                    jsons.put("netAmount", netAmount);
                    jsons.put("units", valUnits);
                    jsons.put("isRedeemAll", false);
                    jsons.put("charges", "0");
                    jsons.put("productCodeTo", "");
                    jsons.put("remark", dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
                    jsons.put("refNo", dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
                    jsons.put("host", "WMBE");
                    jsons.put("feeAmount", feeAmount);
                } else {
                    int cycleDate = Integer.parseInt(spDates);
                    int tenorLong = Integer.parseInt(tenor);
                    jsons.put("amount", amountInvest);
                    jsons.put("tenor", tenorLong);
                    jsons.put("cycleDate", cycleDate);
                }
            }

            Iterator<String> keys = objSaveForm.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                jsons.put(key, objSaveForm.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("payload","processInquiryWMRegister = "+jsons);
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        Server.getAPIService().InquiryWMRegister(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                        BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                        DipsSwafoto.tvLoading.setVisibility(View.GONE);
                        DipsSwafoto.showProgress(false);
                    }
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("idForm", dataTrx.getJSONObject("reksadana").getJSONObject("formpembelian").getString("idForm"));
                        if (dataPurchase != null) {
                            if (formCode.equals("WM SUB")) {
                                bundle.putString("typeTransaction", "sub");
                            } else {
                                bundle.putString("typeTransaction", "inst");
                            }
                            ConnectionRabbitHttp.mirroringEndpoint(311);
                        } else if (dataSales != null) {
                            bundle.putString("typeTransaction", "red");
                            ConnectionRabbitHttp.mirroringEndpoint(312);
                        }
                        Fragment fragment = new frag_wm_trans_resi();
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (isSessionZoom) {
                        BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                        BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                        DipsSwafoto.tvLoading.setVisibility(View.GONE);
                        DipsSwafoto.showProgress(false);
                    }

                    popUpReject();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                    BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                    DipsSwafoto.tvLoading.setVisibility(View.GONE);
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void popUpReject() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_warning));
        tvTitleDialog.setText(mContext.getResources().getString(R.string.transaksi_anda_gagal));
        tvBodyDialog.setText(mContext.getResources().getString(R.string.please_tryagain_later));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                ConnectionRabbitHttp.mirroringEndpoint(14);
                getFragmentPage(new frag_portfolio_new());
            }
        });
    }

    private void processSendOTP() {
        String noHp = no_handphone;
        if (noHp.charAt(0) == '0') {
            noHp = "62"+no_handphone.substring(1);
        }
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("msisdn",noHp);
            dataObjOTP.put("idDips",sessions.getKEY_IdDips());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());

        Server.getAPIService().SendOTP(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        transactionId = dataObj.getJSONObject("data").getString("transactionId");
                        ConnectionRabbitHttp.mirroringEndpoint(11);
                        JSONObject dataMirr = null;
                        try {
                            dataMirr = new JSONObject();
                            dataMirr.put("noponsel",no_handphone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ConnectionRabbitHttp.mirroringKey(dataMirr);
                        getMinutes = 2;
                        seconds = 60;
                        running = true;
                        pageOTP();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        TopBar.setVisibility(View.GONE);
        nestedScroll.setVisibility(View.GONE);
        ll_head.setVisibility(View.VISIBLE);

        String noHandphone = "089783434***";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"***");
        }

        String contentText = textTitleOTP.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textTitleOTP.setText(contentText);

        tvFotoKTP.setText("One Time Password");

        otp.setAnimationEnable(true);
        otp.setPasswordHidden(true);
        otp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp.length() == 6) {
                    numberOTP = otp.getText().toString();
                    JSONObject otpObj = new JSONObject();
                    try {
                        otpObj.put("otp",numberOTP);
                        ConnectionRabbitHttp.mirroringKey(otpObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(mContext, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!transactionId.isEmpty()) {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                    BaseMeetingActivity.tvLoading.setVisibility(View.VISIBLE);
                                    BaseMeetingActivity.showProgress(true);
                                } else {
                                    DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                    DipsSwafoto.tvLoading.setVisibility(View.VISIBLE);
                                    DipsSwafoto.showProgress(true);
                                }
                            }
                        });
                        running = false;
                        processValidateOTP();
                    }
                }
            }
        });
        runTimer(TimerOTP, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    otp.setText("");
                    resendOTP();
                }
            }
        });
    }

    private void runTimer(TextView timer_run, TextView resend) {
        Handler handlerTimer = new Handler();
        handlerTimer.post(new Runnable() {
            @Override
            public void run() {
                int minutes = getMinutes;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0 && minutes == 0){
                    running = false;
                    resend.setClickable(true);
                } else if (seconds == 0 && minutes > 0){
                    seconds = 59;
                }
                if (seconds == 59) {
                    getMinutes--;
                }
                handlerTimer.postDelayed(this,1000);
            }
        });
    }

    private void resendOTP() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
            }
        });
        processSendOTP();
    }

    private void processValidateOTP() {
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("transactionId", transactionId);
            dataObjOTP.put("idDips", sessions.getKEY_IdDips());
            dataObjOTP.put("token", numberOTP);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());
        Server.getAPIService().ValidateOTP(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    APISaveForm();
                } else {
                    running = true;
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });
                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_failed));
                    textTitleOTP.setText(R.string.titleWrongOTP);
                    otp.setText("");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                running = true;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                });
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

}