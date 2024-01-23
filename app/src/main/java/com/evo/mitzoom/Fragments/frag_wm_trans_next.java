package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
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

public class frag_wm_trans_next extends Fragment {

    private static final String TAG = "frag_wm_trans_next";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private String labelserv = "";
    private JSONObject dataObjProd = null;
    private JSONObject objEl = new JSONObject();
    private Spinner spinSourceAccount;
    private TextView tvInfoProdVal;
    private TextView tvManageInvesVal;
    private TextView tvPriceUnit;
    private TextView tvPurchaseFeeVal;
    private TextView tvPurchaseTaxVal;
    private TextView tvCurrency;
    private EditText tvContentCurr;
    private CheckBox chkState1;
    private Switch switchBuy;
    private boolean flagSwitch = false;
    private LinearLayout llDatesPriode;
    private Spinner spDates;
    private Spinner spPeriode;
    private CheckBox chkState2;
    private Button btnNext;
    private ArrayList<FormSpin> dataDropDownSource;
    private Button btnPrice1;
    private Button btnPrice2;
    private Button btnPrice3;
    private Button btnPrice4;
    private Button btnPrice5;
    private Button btnPrice6;
    private ArrayList<Integer> dataListDate;
    private ArrayList<String> dataListPeriode;
    private boolean flagChkState1 = false;
    private String productCode = "";
    private boolean flagSourceAccount = false;
    private TextView tvAlertSourceAcc;
    private TextView tvNABUnits;
    private String transactionCategory;
    private String labelTrx;
    private String labelBuyKey;
    private JSONObject mirrObj;
    private JSONObject formBuyMirr;
    private JSONObject dataFormBuyMirr;
    private long longNumCurrent = 0;
    private long nominalRek = 0;

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
                    objEl = new JSONObject(dataObjProdS);
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
        View views = inflater.inflate(R.layout.frag_wm_trans_next, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        spinSourceAccount = (Spinner) views.findViewById(R.id.spinSourceAccount);
        tvInfoProdVal = (TextView) views.findViewById(R.id.tvInfoProdVal);
        tvManageInvesVal = (TextView) views.findViewById(R.id.tvManageInvesVal);
        tvNABUnits = (TextView) views.findViewById(R.id.tvNABUnits);
        tvPriceUnit = (TextView) views.findViewById(R.id.tvPriceUnit);
        tvPurchaseFeeVal = (TextView) views.findViewById(R.id.tvPurchaseFeeVal);
        tvPurchaseTaxVal = (TextView) views.findViewById(R.id.tvPurchaseTaxVal);

        btnPrice1 = (Button) views.findViewById(R.id.btnPrice1);
        btnPrice2 = (Button) views.findViewById(R.id.btnPrice2);
        btnPrice3 = (Button) views.findViewById(R.id.btnPrice3);
        btnPrice4 = (Button) views.findViewById(R.id.btnPrice4);
        btnPrice5 = (Button) views.findViewById(R.id.btnPrice5);
        btnPrice6 = (Button) views.findViewById(R.id.btnPrice6);

        tvCurrency = (TextView) views.findViewById(R.id.tvCurrency);
        tvContentCurr = (EditText) views.findViewById(R.id.tvContentCurr);
        tvAlertSourceAcc = (TextView) views.findViewById(R.id.tvAlertSourceAcc);

        chkState1 = (CheckBox) views.findViewById(R.id.chkState1);

        switchBuy = (Switch) views.findViewById(R.id.switchBuy);

        llDatesPriode = (LinearLayout) views.findViewById(R.id.llDatesPriode);
        spDates = (Spinner) views.findViewById(R.id.spDates);
        spPeriode = (Spinner) views.findViewById(R.id.spPeriode);

        chkState2 = (CheckBox) views.findViewById(R.id.chkState2);

        btnNext = (Button) views.findViewById(R.id.btnNext);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConnectionRabbitHttp.mirroringEndpoint(293);

        labelTrx = "reksadana";
        labelBuyKey = "formpembelian";

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        transactionCategory = "SUB";
        mirrObj = new JSONObject();
        formBuyMirr = new JSONObject();
        dataFormBuyMirr = new JSONObject();

        addDataDate();
        addDataPeriode();
        ArrayAdapter<Integer> adapterDates = new ArrayAdapter<Integer>(mContext, R.layout.dropdown_multiline, dataListDate);
        spDates.setAdapter(adapterDates);
        ArrayAdapter<String> adapterPeriode = new ArrayAdapter<String>(mContext, R.layout.dropdown_multiline, dataListPeriode);
        spPeriode.setAdapter(adapterPeriode);

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }
        //processGetSourceAccount();
        processSourceAccountWM();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        String productName = "";
        String fundHouseName = "";

        try {
            for(Iterator<String> iter = dataObjProd.keys(); iter.hasNext();) {
                if (iter.hasNext()) {
                    String key = iter.next();
                    if (dataObjProd.has(key) && !dataObjProd.isNull(key)) {
                        dataFormBuyMirr.put(key,dataObjProd.get(key));
                    }
                }
            }

            dataFormBuyMirr.put("rekeningsumberdana","");
            dataFormBuyMirr.put("nilaiinvestasi","");
            dataFormBuyMirr.put("checklist1",false);
            dataFormBuyMirr.put("pembelianberkala",false);
            dataFormBuyMirr.put("setiaptanggal",spDates.getSelectedItem().toString());
            dataFormBuyMirr.put("periode",spPeriode.getSelectedItem().toString());
            dataFormBuyMirr.put("checklist2",false);
            formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
            mirrObj.put(labelTrx,formBuyMirr);
            ConnectionRabbitHttp.mirroringKey(mirrObj);

            if (dataObjProd.has("productCode")) {
                productCode = dataObjProd.getString("productCode");
            }
            if (dataObjProd.has("productName")) {
                productName = dataObjProd.getString("productName");
                tvInfoProdVal.setText(productName);
            }
            if (dataObjProd.has("fundHouseName")) {
                fundHouseName = dataObjProd.getString("fundHouseName");
                tvManageInvesVal.setText(fundHouseName);
            }
            if (dataObjProd.has("fundHouseName")) {
                tvManageInvesVal.setText(dataObjProd.getString("fundHouseName"));
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
                    tvCurrency.setText("Rp");
                } else {
                    String currency = "USD";
                    if (dataObjProd.has("currency")) {
                        currency = dataObjProd.getString("currency");
                        tvCurrency.setText(currency);
                    }
                    nominal = currency+" "+formatted;
                }
                tvPriceUnit.setText(nominal);
            }

            if (dataObjProd.has("subsFee")) {
                double subsFee = dataObjProd.getDouble("subsFee");
                String imbalHasil = subsFee+" %";
                tvPurchaseFeeVal.setText(imbalHasil);
            }
            if (dataObjProd.has("tax")) {
                double tax = dataObjProd.getDouble("tax");
                String taxS = tax+" %";
                tvPurchaseTaxVal.setText(taxS);
            }

            if (dataObjProd.has("navDate")) {
                String navDate = null;
                navDate = dataObjProd.getString("navDate");
                if (navDate.contains("T")) {
                    String[] sp = navDate.split("T");
                    String dates = sp[0];
                    String inputFormat = "yyyy-MM-dd";
                    String outputFormat = "dd/MM/yy";
                    String NABDate = ConveredDate(dates, inputFormat, outputFormat);
                    String redNabUnit = getString(R.string.harga_per_unit);
                    String NABUnits = redNabUnit + " (" + NABDate+")";
                    tvNABUnits.setText(NABUnits);
                }
            }

            tvContentCurr.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String val = s.toString();
                    if (!val.isEmpty()) {
                        tvContentCurr.removeTextChangedListener(this);

                        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
                        String cleanString = val.replaceAll("[$,.]", "");
                        switch (cleanString) {
                            case "100000":
                                btnPrice1.callOnClick();
                                break;
                            case "500000":
                                btnPrice2.callOnClick();
                                break;
                            case "1000000":
                                btnPrice3.callOnClick();
                                break;
                            case "5000000":
                                btnPrice4.callOnClick();
                                break;
                            case "10000000":
                                btnPrice5.callOnClick();
                                break;
                            case "50000000":
                                btnPrice6.callOnClick();
                                break;
                            default:
                                btnPrice1.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                btnPrice1.setBackgroundTintList(null);
                                btnPrice2.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                btnPrice2.setBackgroundTintList(null);
                                btnPrice3.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                btnPrice3.setBackgroundTintList(null);
                                btnPrice4.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                btnPrice4.setBackgroundTintList(null);
                                btnPrice5.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                btnPrice5.setBackgroundTintList(null);
                                btnPrice6.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                btnPrice6.setBackgroundTintList(null);
                                break;
                        }
                        BigDecimal parsed = new BigDecimal(cleanString);
                        String nfS = nf.format(parsed);

                        try {
                            dataFormBuyMirr.put("nilaiinvestasi",nfS);
                            formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                            mirrObj.put(labelTrx,formBuyMirr);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        String number = nfS.replaceAll("\\D+", "");
                        longNumCurrent = Long.valueOf(number);

                        if (longNumCurrent > nominalRek) {
                            tvAlertSourceAcc.setVisibility(View.VISIBLE);
                            btnNext.setEnabled(false);
                            btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                        } else {
                            tvAlertSourceAcc.setVisibility(View.GONE);
                            if (longNumCurrent < 100000) {
                                btnNext.setEnabled(false);
                                btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                            } else {
                                btnNext.setEnabled(true);
                                btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                            }
                        }
                        tvContentCurr.setText(nfS);
                        tvContentCurr.setSelection(nfS.length());
                        tvContentCurr.addTextChangedListener(this);
                    }
                }
            });

            switchBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    flagSwitch = checked;
                    try {
                        dataFormBuyMirr.put("pembelianberkala",checked);
                        formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                        mirrObj.put(labelTrx,formBuyMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if (checked) {
                        llDatesPriode.setVisibility(View.VISIBLE);
                        switchBuy.setThumbTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        switchBuy.setTrackTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    } else {
                        llDatesPriode.setVisibility(View.GONE);
                        switchBuy.setThumbTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                        switchBuy.setTrackTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                    }
                }
            });

            btnPrice1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String txt = btnPrice1.getText().toString().replace("Rp", "").trim();
                    tvContentCurr.setText(txt);

                    btnPrice1.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnPrice1.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btnPrice2.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice2.setBackgroundTintList(null);
                    btnPrice3.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice3.setBackgroundTintList(null);
                    btnPrice4.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice4.setBackgroundTintList(null);
                    btnPrice5.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice5.setBackgroundTintList(null);
                    btnPrice6.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice6.setBackgroundTintList(null);
                }
            });
            btnPrice2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String txt = btnPrice2.getText().toString().replace("Rp", "").trim();
                    tvContentCurr.setText(txt);

                    btnPrice1.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice1.setBackgroundTintList(null);
                    btnPrice2.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnPrice2.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btnPrice3.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice3.setBackgroundTintList(null);
                    btnPrice4.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice4.setBackgroundTintList(null);
                    btnPrice5.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice5.setBackgroundTintList(null);
                    btnPrice6.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice6.setBackgroundTintList(null);
                }
            });
            btnPrice3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String txt = btnPrice3.getText().toString().replace("Rp", "").trim();
                    tvContentCurr.setText(txt);

                    btnPrice1.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice1.setBackgroundTintList(null);
                    btnPrice2.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice2.setBackgroundTintList(null);
                    btnPrice3.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnPrice3.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btnPrice4.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice4.setBackgroundTintList(null);
                    btnPrice5.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice5.setBackgroundTintList(null);
                    btnPrice6.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice6.setBackgroundTintList(null);
                }
            });
            btnPrice4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String txt = btnPrice4.getText().toString().replace("Rp", "").trim();
                    tvContentCurr.setText(txt);

                    btnPrice1.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice1.setBackgroundTintList(null);
                    btnPrice2.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice2.setBackgroundTintList(null);
                    btnPrice3.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice3.setBackgroundTintList(null);
                    btnPrice4.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnPrice4.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btnPrice5.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice5.setBackgroundTintList(null);
                    btnPrice6.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice6.setBackgroundTintList(null);
                }
            });
            btnPrice5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String txt = btnPrice5.getText().toString().replace("Rp", "").trim();
                    tvContentCurr.setText(txt);

                    btnPrice1.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice1.setBackgroundTintList(null);
                    btnPrice2.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice2.setBackgroundTintList(null);
                    btnPrice3.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice3.setBackgroundTintList(null);
                    btnPrice4.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice4.setBackgroundTintList(null);
                    btnPrice5.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnPrice5.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btnPrice6.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice6.setBackgroundTintList(null);
                }
            });
            btnPrice6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String txt = btnPrice6.getText().toString().replace("Rp", "").trim();
                    tvContentCurr.setText(txt);

                    btnPrice1.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice1.setBackgroundTintList(null);
                    btnPrice2.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice2.setBackgroundTintList(null);
                    btnPrice3.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice3.setBackgroundTintList(null);
                    btnPrice4.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice4.setBackgroundTintList(null);
                    btnPrice5.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                    btnPrice5.setBackgroundTintList(null);
                    btnPrice6.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnPrice6.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                }
            });

        } catch (JSONException e) {

        }

        spDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String valsp = spDates.getSelectedItem().toString();
                try {
                    dataFormBuyMirr.put("setiaptanggal",valsp);
                    formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                    mirrObj.put(labelTrx,formBuyMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spPeriode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String valsp = spPeriode.getSelectedItem().toString();
                try {
                    dataFormBuyMirr.put("periode",valsp);
                    formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                    mirrObj.put(labelTrx,formBuyMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        chkState1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                flagChkState1 = checked;

                try {
                    dataFormBuyMirr.put("checklist1",checked);
                    formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                    mirrObj.put(labelTrx,formBuyMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (checked && flagSwitch) {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                } else if (checked && !flagSwitch && flagSourceAccount) {
                    if (longNumCurrent > nominalRek) {
                        tvAlertSourceAcc.setVisibility(View.VISIBLE);
                        btnNext.setEnabled(false);
                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                    } else {
                        tvAlertSourceAcc.setVisibility(View.GONE);
                        if (longNumCurrent < 100000) {
                            btnNext.setEnabled(false);
                            btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                        } else {
                            btnNext.setEnabled(true);
                            btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        }
                    }
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                }
            }
        });

        chkState2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                try {
                    dataFormBuyMirr.put("checklist2",checked);
                    formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                    mirrObj.put(labelTrx,formBuyMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (checked && flagChkState1 && flagSourceAccount) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String amountInvest = tvContentCurr.getText().toString();
                    String amount = amountInvest.replaceAll("[$,.]", "");
                    objEl.put("amountInvest",amount);
                    dataFormBuyMirr.put("amountInvest",amount);
                    objEl.put("nilaiinvestasi",amountInvest);
                    dataFormBuyMirr.put("nilaiinvestasi",amountInvest);

                    double subsFee = dataObjProd.getDouble("subsFee");
                    long fee = (long) ((Long.parseLong(amount) * subsFee) / 100);

                    double tax = dataObjProd.getDouble("tax");
                    long longTax = (long) ((Long.parseLong(amount) * tax) / 100);

                    objEl.put("totalFeeAmount",fee);
                    NumberFormat formatter = null;
                    if (sessions.getLANG().equals("id")) {
                        formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                    } else {
                        formatter = NumberFormat.getInstance(new Locale("en", "US"));
                    }
                    formatter.setMinimumFractionDigits(2);
                    String formattedFee = formatter.format(fee);
                    dataFormBuyMirr.put("totalFeeAmount",formattedFee);
                    objEl.put("totalTaxAmount",longTax);
                    String formattedlongTax = formatter.format(longTax);
                    dataFormBuyMirr.put("totalTaxAmount",formattedlongTax);
                    if (flagSwitch) {
                        transactionCategory = "INST";
                        objEl.put("pembelian","berkala");
                        objEl.put("cycleDate",spDates.getSelectedItem().toString());
                        objEl.put("spPeriode",spPeriode.getSelectedItem().toString());
                        String[] sp = spPeriode.getSelectedItem().toString().split(" ");
                        String tenor = sp[0].trim();
                        objEl.put("tenor",tenor);

                        dataFormBuyMirr.put("pembelian","berkala");
                        dataFormBuyMirr.put("cycleDate",spDates.getSelectedItem().toString());
                        dataFormBuyMirr.put("spPeriode",spPeriode.getSelectedItem().toString());
                        dataFormBuyMirr.put("tenor",tenor);
                    }
                    objEl.put("transactionCategory",transactionCategory);
                    dataFormBuyMirr.put("transactionCategory",transactionCategory);

                    formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                    mirrObj.put(labelTrx,formBuyMirr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processInquiryWMFeeTax();
            }
        });

    }

    private void addDataDate() {
        dataListDate = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            dataListDate.add(i);
        }
    }

    private void addDataPeriode() {
        dataListPeriode = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String data = i+" "+getString(R.string.bulan);
            if (i > 1 && sessions.getLANG().equals("en")) {
                data = data+"s";
            }
            dataListPeriode.add(data);
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

    private void processInquiryWMFeeTax() {

        String valNom = tvContentCurr.getText().toString();
        String netAmount = valNom.replaceAll("[$,.]", "");

        JSONObject jsons = new JSONObject();
        try {
            jsons.put("productCode",productCode);
            jsons.put("transactionCategory",transactionCategory);
            jsons.put("cif",sessions.getNoCIF());
            jsons.put("netAmount",netAmount);
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
                        long totalNavUnits = 0;
                        if (dataBody.has("totalFeeAmount")) {
                            totalFeeAmount = dataBody.getLong("totalFeeAmount") / 10000;
                        }
                        if (dataBody.has("totalTaxAmount")) {
                            totalTaxAmount = dataBody.getLong("totalTaxAmount");
                        }

                        if (dataBody.has("navPerUnit")) {
                            totalNavUnits = dataBody.getLong("navPerUnit");
                        }

                        try {
                            if (flagSwitch) {
                                objEl.put("pembelian","berkala");
                            }
                            String amountInvest = tvContentCurr.getText().toString();
                            String amount = amountInvest.replaceAll("[$,.]", "");
                            objEl.put("amountInvest",amount);
                            objEl.put("spDates",spDates.getSelectedItem().toString());
                            objEl.put("spPeriode",spPeriode.getSelectedItem().toString());
                            objEl.put("totalFeeAmount",totalFeeAmount);
                            objEl.put("totalTaxAmount",totalTaxAmount);
                            objEl.put("navPerUnit",totalNavUnits);

                            Bundle bundle = new Bundle();
                            bundle.putString("dataPurchase", objEl.toString());
                            bundle.putString("labelserv", labelserv);
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
//                            if (!prodCode.equals("T21")) {
//                                continue;
//                            }
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
                                    String[] sp = results.split("\n");
                                    String typeAccount = sp[0].trim();
                                    String no_nama_Rek = sp[1].trim();
                                    if (no_nama_Rek.indexOf("-") > 0) {
                                        String[] sp2 = no_nama_Rek.split("-");
                                        String noRekSource = sp2[0].trim();
                                    }
                                    String valueNominalRek = sp[2].trim();
                                    String getNominalRek = valueNominalRek.replaceAll("\\D+","");
                                    nominalRek = Long.valueOf(getNominalRek) / 100;

                                    try {
                                        String represults = results.replaceAll("\n"," / ");
                                        objEl.put("rekeningsumberdana",represults);

                                        dataFormBuyMirr.put("rekeningsumberdana",represults);
                                        formBuyMirr.put(labelBuyKey,dataFormBuyMirr);
                                        mirrObj.put(labelTrx,formBuyMirr);
                                        ConnectionRabbitHttp.mirroringKey(mirrObj);

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    if (flagChkState1 && !flagSwitch && longNumCurrent > 0) {
                                        btnNext.setEnabled(true);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                    } else if (flagChkState1 && flagSwitch) {
                                        btnNext.setEnabled(false);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                                    } else if (!flagChkState1) {
                                        btnNext.setEnabled(false);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_icon_grey));
                                    }

                                    if (longNumCurrent > nominalRek) {
                                        tvAlertSourceAcc.setVisibility(View.VISIBLE);
                                        btnNext.setEnabled(false);
                                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                    } else {
                                        tvAlertSourceAcc.setVisibility(View.GONE);
                                        if (longNumCurrent < 100000) {
                                            btnNext.setEnabled(false);
                                            btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                        } else {
                                            btnNext.setEnabled(true);
                                            btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                        }
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