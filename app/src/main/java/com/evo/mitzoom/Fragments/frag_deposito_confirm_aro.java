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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.HideSoftKeyboard;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_deposito_confirm_aro extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "frag_deposito_confirm_aro";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private LinearLayout TopBar;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private RelativeLayout rlHead;
    private TextView tvTitle;
    private ImageView imgPencil;
    private RelativeLayout rlContent;
    private TextView tvValueJangka;
    private TextView tvValueTempo;
    private TextView tvNominal;
    private TextView tvNoRef;
    private TextView tvNama;
    private TextView tvNoRek;
    private LinearLayout llSpinNoRek;
    private Spinner spinSourceAccount;
    private TextView tvTypeRek;
    private TextView tvJenisDepo;
    private TextView tvNominalDepo;
    private TextView tvValueJangkaTime;
    private TextView tvInstruksi;
    private LinearLayout llSpinInstruksi;
    private Spinner spininstruksi;
    private TextView tvValueBunga;
    private TextView tvTrxDate;
    private EditText edCairDate;
    private TextView tvCairDate;
    private String labelserv = "";
    private String idService = "";
    private String noCif;
    private JSONObject dataObj = null;
    private DatePickerDialog dpd;
    private int year;
    private int month;
    private int day;
    private String tanggal, waktu;
    private Button btnOperation;
    private String idDips = "";
    private String referenceNumber = "";
    private String noDeposit;
    private JSONObject dataNasabahObj;
    private String no_handphone = "";
    private String transactionId = "";
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
    private int loopStatus = 0;
    private JSONObject dataTrx;
    private JSONObject mirrObj;
    //private JSONObject reqMirr;
    private String keyMirr = "";
    private String ratePercent;
    private final String labelTrx = "";
    private String norekKredit = "";
    private String noRekSource = "";
    private String labelAro = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        idDips = sessions.getKEY_IdDips();

        if (isSessionZoom) {
            ConnectionRabbitHttp.init(mContext);
        }
        if (getArguments() != null) {
            if (getArguments().containsKey("data")) {
                String dataS = getArguments().getString("data");
                try {
                    dataObj = new JSONObject(dataS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("idService")) {
                idService = getArguments().getString("idService");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
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

        noCif = sessions.getNoCIF();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_deposito_confirm_aro, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        rlHead = (RelativeLayout) views.findViewById(R.id.rlHead);
        tvTitle = (TextView) views.findViewById(R.id.tvTitleCardDeposit);
        imgPencil = (ImageView) views.findViewById(R.id.imgPencil);
        tvValueJangka = (TextView) views.findViewById(R.id.tvValueJangka);
        tvValueTempo = (TextView) views.findViewById(R.id.tvValueTempo);
        tvNominal = (TextView) views.findViewById(R.id.tvNominal);

        rlContent = (RelativeLayout) views.findViewById(R.id.rlContent);

        tvNoRef = (TextView) views.findViewById(R.id.tvNoRef);
        tvNama = (TextView) views.findViewById(R.id.tvNama);
        tvNoRek = (TextView) views.findViewById(R.id.tvNoRek);
        llSpinNoRek = (LinearLayout) views.findViewById(R.id.llSpinNoRek);
        spinSourceAccount = (Spinner) views.findViewById(R.id.spinNoRek);
        tvTypeRek = (TextView) views.findViewById(R.id.tvTypeRek);
        tvJenisDepo = (TextView) views.findViewById(R.id.tvJenisDepo);
        tvNominalDepo = (TextView) views.findViewById(R.id.tvNominalDepo);
        tvValueJangkaTime = (TextView) views.findViewById(R.id.tvValueJangkaTime);
        tvInstruksi = (TextView) views.findViewById(R.id.tvInstruksi);
        llSpinInstruksi = (LinearLayout) views.findViewById(R.id.llSpinInstruksi);
        spininstruksi = (Spinner) views.findViewById(R.id.spininstruksi);
        tvValueBunga = (TextView) views.findViewById(R.id.tvValueBunga);
        tvTrxDate = (TextView) views.findViewById(R.id.tvTrxDate);
        edCairDate = (EditText) views.findViewById(R.id.edCairDate);
        tvCairDate = (TextView) views.findViewById(R.id.tvCairDate);
        btnOperation = (Button) views.findViewById(R.id.btnOperation);

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

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        mirrObj = new JSONObject();
        //reqMirr = new JSONObject();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack("FragDeposit", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ConnectionRabbitHttp.mirroringEndpoint(210);
            }
        });

        if (isSessionZoom) {
            if (idService.equals("192")) {
                ConnectionRabbitHttp.mirroringEndpoint(211);
            } else {
                ConnectionRabbitHttp.mirroringEndpoint(212);
            }
        }

        if (idService.equals("192")) {
            keyMirr = "pencairandeposito";
            btnOperation.setText(getResources().getString(R.string.cairkan));
        } else if (idService.equals("193")){
            keyMirr = "perubahandeposito";
            btnOperation.setText(getResources().getString(R.string.save));
        } else {
            keyMirr = "perubahandeposito";
        }
        btnOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideSoftKeyboard.hideSoftKeyboard(getActivity());
                if (idService.equals("192")) {
                    processSendOTP();
                } else {
                    if (!referenceNumber.isEmpty()) {
                        processEditARO();
                    }
                }
            }
        });

        try {
            noDeposit = dataObj.getString("depositoNo");
            referenceNumber = dataObj.getString("referenceNumber");
            String dueDate = "";
            if (dataObj.has("dueDate")) {
                dueDate = dataObj.getString("dueDate");
            } else if (dataObj.has("tanggalJTempo")) {
                dueDate = dataObj.getString("tanggalJTempo");
            }
            String nominal = dataObj.getString("nominal");
            String flagAro = "";
            String nasabah = "";
            /*if (dataObj.has("account")) {
                JSONObject dataAccount = dataObj.getJSONObject("account");
                nasabah = dataAccount.getString("accountName");
                noRek = dataAccount.getString("accountNo");
            } else */
            if (dataObj.has("nasabah")) {
                nasabah = dataObj.getString("nasabah");
            }
            if (dataObj.has("rekKredit")) {
                norekKredit = dataObj.getString("rekKredit").trim();
                if (norekKredit.contains("{")) {
                    JSONObject rekKredit = dataObj.getJSONObject("rekKredit");
                    norekKredit = rekKredit.getString("value").trim();
                }

                if (norekKredit.isEmpty()) {
                    llSpinNoRek.setVisibility(View.VISIBLE);
                    tvNoRek.setVisibility(View.GONE);
                    processGetSourceAccount();
                }
            }
            ratePercent = dataObj.getString("bungaPtahun");
            String timePeriod = "";
            String placementDate = dataObj.getString("tanggalPenempatan");
            String inputFormat = "yyyy-MM-dd";
            String outputFormat = "dd-MM-yyyy";
            if (placementDate.indexOf(" ") > 0) {
                inputFormat = "yyyy-MM-dd HH:mm";
                outputFormat = "dd-MM-yyyy HH:mm";
            }
            placementDate = ConveredDate(placementDate,inputFormat,outputFormat);
            String currency = "IDR";
            String jatuhTempo = "";
            String jatuhTempo2 = "";

            if (noDeposit.equals("null")) {
                noDeposit = "";
            }

            if (dataObj.has("depositType")) {
                if (!dataObj.isNull("depositType")) {
                    JSONObject depositTypeObj = dataObj.getJSONObject("depositType");
                    currency = depositTypeObj.getString("currency");
                    timePeriod = depositTypeObj.getString("months");
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
                    rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_depo1));
                } else if (valueCode.equals("2")) {
                    rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_depo2));
                } else if (valueCode.equals("3")) {
                    rlHead.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_depo3));
                }
                labelAro = flagAroObj.getString("labelIdn");
                if (sessions.getLANG().equals("en")) {
                    labelAro = flagAroObj.getString("labelEng");
                }
                labelAro = labelAro.replace("(","").replace(")","");
            }

            String titleCard = noDeposit+" ("+labelAro+")";

            if (!dueDate.isEmpty()) {
                if (dueDate.indexOf("-") > 0) {
                    String[] sp = dueDate.split("-");
                    String thn = sp[0].trim();
                    String bln = sp[1].trim();
                    String tgl = sp[2].trim();

                    String bulan = fullMonths(bln);
                    jatuhTempo = tgl+" "+bulan+ " "+thn;
                    jatuhTempo2 = tgl+"-"+bln+ "-"+thn;
                }
            }

            if (currency.equals("IDR")) {
                currency = "Rupiah";
            }

            tvTitle.setText(titleCard);
            tvValueJangka.setText(timePeriod);
            tvValueTempo.setText(jatuhTempo);
            tvNominal.setText(nominal);

            tvNoRef.setText(noDeposit);
            tvNama.setText(nasabah);
            tvNoRek.setText(norekKredit);
            tvTypeRek.setText("Tabungan Victoria");
            tvJenisDepo.setText(currency);
            tvNominalDepo.setText(nominal);
            tvValueJangkaTime.setText(timePeriod);
            tvValueBunga.setText(ratePercent);
            tvTrxDate.setText(placementDate);

            dataObj.put("tiperekening","Tabungan Victoria");

            if (idService.equals("192")) {
                try {
                    dataObj.put("referenceNumber", referenceNumber);
                    dataObj.put("tanggalpencairan", edCairDate.getText().toString());
                    dataObj.put("instruksi",labelAro);
                    mirrObj.put(keyMirr, dataObj);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                tvInstruksi.setVisibility(View.VISIBLE);
                llSpinInstruksi.setVisibility(View.GONE);
                tvCairDate.setVisibility(View.GONE);
                edCairDate.setVisibility(View.VISIBLE);

                tvInstruksi.setText(labelAro);

                edCairDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);
                        dpd = null;
                        if (dpd == null) {
                            dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(frag_deposito_confirm_aro.this,
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH)
                            );
                        } else {
                            dpd.initialize(
                                    frag_deposito_confirm_aro.this,
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH)
                            );
                        }

                        // restrict to weekdays only
                        ArrayList<Calendar> weekdays = new ArrayList<Calendar>();
                        Calendar day = Calendar.getInstance();
                        for (int i = 0; i < 30; i++) {
                            if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                Calendar d = (Calendar) day.clone();
                                weekdays.add(d);
                            }
                            day.add(Calendar.DATE, 1);
                        }
                        Calendar[] weekdayDays = weekdays.toArray(new Calendar[weekdays.size()]);
                        dpd.setSelectableDays(weekdayDays);

                        dpd.setOnCancelListener(dialog -> {
                            dpd = null;
                        });
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });
            } else {
                tvInstruksi.setVisibility(View.GONE);
                spininstruksi.setVisibility(View.VISIBLE);
                llSpinInstruksi.setVisibility(View.VISIBLE);
                tvCairDate.setVisibility(View.VISIBLE);
                edCairDate.setVisibility(View.GONE);

                tvCairDate.setText(jatuhTempo2);

                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processInstruksiARO();

                try {
                    dataObj.put("referenceNumber", referenceNumber);
                    dataObj.put("tanggalpencairan", jatuhTempo2);
                    mirrObj.put(keyMirr, dataObj);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
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

    private void processEditARO() {
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        FormSpin dataSpin = (FormSpin) spininstruksi.getSelectedItem();
        String valueCodeARO = dataSpin.getCode();
        if (valueCodeARO.isEmpty()) {
            Toast.makeText(mContext,getString(R.string.instruksi)+" "+getString(R.string.alertRTGS),Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject dataObjSave = new JSONObject();
        try {
            dataObjSave.put("referenceNumber",referenceNumber);
            dataObjSave.put("flagAro",valueCodeARO);
            dataObjSave.put("noDeposito",noDeposit);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjSave.toString());
        Server.getAPIService().DepositEditARO(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    processSendOTP();
                } else {
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
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void APISaveForm() {
        dataTrx = new JSONObject();

        JSONObject dataObjSave = new JSONObject();

        try {

            dataTrx.put("nomorreferensi",tvNoRef.getText().toString());
            dataTrx.put("noReff",tvNoRef.getText().toString());
            if (!referenceNumber.isEmpty()) {
                dataTrx.put("referenceNumber", referenceNumber);
            }
            dataTrx.put("noDeposito",noDeposit);
            if (idService.equals("193")) {
                FormSpin dataSpin = (FormSpin) spininstruksi.getSelectedItem();
                String valueCodeARO = dataSpin.getCode();
                dataTrx.put("flagAro", valueCodeARO);
            }
            dataTrx.put("nama",tvNama.getText().toString());
            dataTrx.put("name",tvNama.getText().toString());
            if (norekKredit.isEmpty()) {
                dataTrx.put("nomorrekening", noRekSource);
                dataTrx.put("noRek", noRekSource);
            } else {
                dataTrx.put("nomorrekening", tvNoRek.getText().toString());
                dataTrx.put("noRek", tvNoRek.getText().toString());
            }
            dataTrx.put("tiperekening",tvTypeRek.getText().toString());
            dataTrx.put("tabungan",tvTypeRek.getText().toString());
            dataTrx.put("jenisdeposito",tvJenisDepo.getText().toString());
            dataTrx.put("nominaldeposito",tvNominalDepo.getText().toString());
            dataTrx.put("nominal",tvNominalDepo.getText().toString());
            dataTrx.put("jangkawaktu",tvValueJangkaTime.getText().toString());
            dataTrx.put("jangkaWaktu",tvValueJangkaTime.getText().toString());
            dataTrx.put("bungadidapat",ratePercent);
            dataTrx.put("interest",tvValueBunga.getText().toString());
            dataTrx.put("tanggaltransaksi",tvTrxDate.getText().toString());
            dataTrx.put("transactionDate",tvTrxDate.getText().toString());
            if (idService.equals("192")) {
                dataTrx.put("instruksi", tvInstruksi.getText().toString());
                dataTrx.put("tanggalpencairan", edCairDate.getText().toString());
                dataTrx.put("dueDate", edCairDate.getText().toString());
            } else {
                dataTrx.put("instruksi", spininstruksi.getSelectedItem());
                dataTrx.put("tanggalpencairan", tvCairDate.getText().toString());
                dataTrx.put("dueDate", tvCairDate.getText().toString());
            }
            dataTrx.put("tipeTransaksi", labelserv);

            if (idService.equals("192")) {
                dataObjSave.put("formCode", "Break Deposit");
            } else {
                dataObjSave.put("formCode", "Update ARO");
            }
            dataObjSave.put("idDips", idDips);
            dataObjSave.put("payload", dataTrx);
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
                        String idForm = dataObj.getJSONObject("data").getString("idForm");
                        JSONObject idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);

                        dataTrx.put("idForm",idForm);

                        JSONObject dataMirr = new JSONObject();
                        try {
                            dataMirr.put(keyMirr,dataTrx);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        ConnectionRabbitHttp.mirroringKey(dataMirr);
                        processApprovalStatus();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
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

    private void processSendOTP() {
        String noHp = no_handphone;
        if (noHp.charAt(0) == '0') {
            noHp = "62"+no_handphone.substring(1);
        }
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("msisdn",noHp);
            dataObjOTP.put("idDips",idDips);
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
        rlContent.setVisibility(View.GONE);
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
                        //RabbitMirroring.MirroringSendKey(otpObj);
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
                        loopStatus = 0;
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
            dataObjOTP.put("idDips", idDips);
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

    private void processApprovalStatus() {
        String idForm = "";
        try {
            idForm = dataTrx.getString("idForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        String finalIdForm = idForm;
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
                                Bundle bundle = new Bundle();
                                bundle.putString("idForm", finalIdForm);
                                bundle.putString("dataValueForm",dataTrx.toString());
                                if (idService.equals("192")) {
                                    bundle.putString("typeTransaction", "break");
                                    ConnectionRabbitHttp.mirroringEndpoint(274);
                                } else {
                                    bundle.putString("typeTransaction", "aro");
                                    ConnectionRabbitHttp.mirroringEndpoint(273);
                                }
                                Fragment fragment = new frag_service_antarbank_resi();
                                fragment.setArguments(bundle);
                                getFragmentPage(fragment);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    /*if (loopStatus >= 99) {
                        Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
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
                    }*/
                    //if (loopStatus < 99) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        processApprovalStatus();
                                    }
                                });
                                //loopStatus++;
                            }
                        },10000);
                    //}
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
                                processApprovalStatus();
                            }
                        });
                    }
                },10000);
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

    private void processGetSourceAccount() {
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

        Server.getAPIService().GetNewPortofolio(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }

                if (response.isSuccessful()) {
                    if (idService.equals("192")) {
                        try {
                            dataObj.put("referenceNumber", referenceNumber);
                            dataObj.put("tanggalpencairan", edCairDate.getText().toString());
                            dataObj.put("instruksi", labelAro);
                            mirrObj.put(keyMirr, dataObj);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            dataObj.put("referenceNumber", referenceNumber);
                            dataObj.put("tanggalpencairan", tvCairDate.getText().toString());
                            mirrObj.put(keyMirr, dataObj);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataBody = new JSONObject(dataS);
                        if (dataBody.has("token")) {
                            String accessToken = dataBody.getString("token");
                            String exchangeToken = dataBody.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        JSONObject objData = dataBody.getJSONObject("data");
                        JSONArray dataArr = objData.getJSONArray("portotabungan");
                        ArrayList<FormSpin> dataDropDownSource = new ArrayList<>();
                        int len = dataArr.length() + 1;
                        String[] sourceAcc = new String[len];
                        String textSelect = getString(R.string.choose_nomor_rekening);
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
                            String labelIdn = accountNo + " - " + accountName;
                            String labelEn = prodName + "\n" + accountNo + " - " + accountName + "\n" + acctCur + " " + formattedNumber;
                            sourceAcc[loopSource] = labelIdn;
                            loopSource++;

                            dataDropDownSource.add(new FormSpin(idData,accountType,labelIdn,labelEn));
                        }
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDownSource);
                        spinSourceAccount.setAdapter(adapterSourceAcc);

                        spinSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                FormSpin dataSpin = (FormSpin) spinSourceAccount.getSelectedItem();
                                String results = dataSpin.getName();
                                String resultsEng = dataSpin.getNameEng();
                                if (resultsEng.indexOf("\n") > 0) {
                                    try {
                                        String[] sp = resultsEng.split("\n");
                                        String prodName = sp[0].trim();
                                        tvTypeRek.setText(prodName);
                                        dataObj.put("tiperekening",prodName);
                                        String no_nama_Rek = sp[1].trim();
                                        if (no_nama_Rek.indexOf("-") > 0) {
                                            String[] sp2 = no_nama_Rek.split("-");
                                            noRekSource = sp2[0].trim();
                                        }
                                        String represults = results.replaceAll("\n"," / ");
                                        dataObj.put("rekKredit", noRekSource);
                                        mirrObj.put(keyMirr, dataObj);
                                        btnOperation.setEnabled(true);
                                        btnOperation.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                        ConnectionRabbitHttp.mirroringKey(mirrObj);

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    btnOperation.setEnabled(false);
                                    btnOperation.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
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
                            JSONObject dataBody = new JSONObject(dataS);
                            if (dataBody.has("message")) {
                                msg = dataBody.getString("message");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String dataS = null;
                        try {
                            dataS = response.errorBody().string();
                            JSONObject dataBody = new JSONObject(dataS);
                            if (dataBody.has("message")) {
                                msg = dataBody.getString("message");
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

    private void processInstruksiARO() {
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().InstruksiARO(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        JSONArray dataArr = dataBody.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        int posMatch = 0;
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = 0;
                            String idSData = "";
                            String valueCode = "";

                            if (dataArr.getJSONObject(i).has("ids")) {
                                idSData = dataArr.getJSONObject(i).getString("ids").trim();
                                idData = Integer.parseInt(idSData);
                            } else if (dataArr.getJSONObject(i).has("id")) {
                                idData = dataArr.getJSONObject(i).getInt("id");

                            }

                            String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                            String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                            if (dataArr.getJSONObject(i).has("valueCode")) {
                                valueCode = dataArr.getJSONObject(i).getString("valueCode");
                                JSONObject flagAroObj = dataObj.getJSONObject("flagAro");
                                String getValueCode = flagAroObj.getString("valueCode");
                                if (valueCode.equals(getValueCode)) {
                                    posMatch = i;
                                }
                            }
                            dataDropDown.add(new FormSpin(idData,valueCode,labelIdn,labelEng));
                        }

                        ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
                        spininstruksi.setAdapter(adapter2);
                        spininstruksi.setSelection(posMatch);
                        spininstruksi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                FormSpin dataSpin = (FormSpin) spininstruksi.getSelectedItem();
                                String results = dataSpin.getName();
                                int idSpin = dataSpin.getId();
                                if (idSpin != 0) {
                                    try {
                                        JSONObject flagAroObj = dataObj.getJSONObject("flagAro");
                                        flagAroObj.put("labelIdn", results);
                                        flagAroObj.put("labelEng", results);
                                        dataObj.put("flagAro", flagAroObj);
                                        dataObj.put("instruksi",results);
                                        mirrObj.put(keyMirr, dataObj);
                                        btnOperation.setEnabled(true);
                                        btnOperation.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                } else {
                                    btnOperation.setEnabled(false);
                                    btnOperation.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String msg = "";
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
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int addmonths = (month + 1);
        String months = String.valueOf(addmonths);
        if (addmonths < 10) {
            months = "0"+months;
        }
        String days = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10 ) {
            days = "0"+days;
        }

        String times = new SimpleDateFormat("HH:mm",
                Locale.getDefault()).format(new Date());

        //tanggal = days+"-"+months+"-"+year+" "+times;
        tanggal = days+"-"+months+"-"+year;
        edCairDate.setText(tanggal);
        btnOperation.setEnabled(true);
        btnOperation.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));

        if (idService.equals("192")) {
            //ConnectionRabbitHttp.mirroringEndpoint(212);
            try {
                dataObj.put("tanggalpencairan", edCairDate.getText().toString());
                mirrObj.put(keyMirr, dataObj);
                ConnectionRabbitHttp.mirroringKey(mirrObj);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}