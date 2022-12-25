package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterCIF;
import com.evo.mitzoom.Adapter.AdapterPortofolioNew;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_cif_full extends Fragment {

    private String TAG = "CEK_frag_cif_full";
    private Context mContext;
    private SessionManager sessions;
    private boolean isCust;
    private boolean isSwafoto;
    private int chkFlow;
    private byte[] bytePhoto;
    private byte[] KTP = new byte[0];
    private byte[] KTP_SWAFOTO = new byte[0];
    private byte[] NPWP = new byte[0];
    private byte[] TTD = new byte[0];
    private int formCode;
    private String idDips;
    private View inclHead;
    private SwipeRefreshLayout swipe;
    private LinearLayout TopBar;
    private LinearLayout ll_head;
    private TextView tvFotoKTP;
    private LinearLayout iconKtp;
    private LinearLayout iconSwafoto;
    private LinearLayout iconNpwp;
    private LinearLayout iconSignature;
    private LinearLayout iconForm;
    private Button btnProses;
    private RecyclerView rv_item_expand;
    private JSONArray dataCIFArr = null;
    private JSONObject objValCIF;
    private int keysCount;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private BroadcastReceiver smsReceiver = null;
    private PinView otp;
    private String no_handphone = "";
    private String numberOTP = "";
    private String newString = "";
    private Handler handler = null;
    private Runnable myRunnable = null;
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private boolean isChk = false;
    private String transactionId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        chkFlow = sessions.getFLOW();
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();
        String valDataCIF = sessions.getCIF();
        try {
            objValCIF = new JSONObject(valDataCIF);
            Log.e(TAG,"CIF FULL objValCIF : "+objValCIF.toString());
            JSONObject objEl = objValCIF.getJSONObject("datadiri");
            no_handphone = objEl.getString("noponsel");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        Log.e(TAG,mContext+" isSessionZoom : "+isSessionZoom);
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

        String base64KTP = sessions.getKEY_KTP();
        if (base64KTP != null) {
            KTP = Base64.decode(base64KTP, Base64.DEFAULT);
        }

        String base64SWAFOTO = sessions.getKEY_SWAFOTO();
        if (base64SWAFOTO != null) {
            KTP_SWAFOTO = Base64.decode(base64SWAFOTO, Base64.DEFAULT);
        }

        String base64NPWP = sessions.getKEY_NPWP();
        if (base64NPWP != null) {
            NPWP = Base64.decode(base64NPWP, Base64.DEFAULT);
        }

        String base64TTD = sessions.getKEY_TTD();
        if (base64TTD != null) {
            TTD = Base64.decode(base64TTD, Base64.DEFAULT);
        }

        Log.e(TAG,mContext+" isCust : "+isCust);
        Log.e(TAG,mContext+" isSwafoto : "+isSwafoto);
        Log.e(TAG,mContext+" chkFlow : "+chkFlow);
        Log.e(TAG,mContext+" formCode : "+formCode);
        Log.e(TAG,mContext+" idDips : "+idDips);
        Log.e(TAG,mContext+" KTP : "+KTP.length);
        Log.e(TAG,mContext+" KTP_SWAFOTO : "+KTP_SWAFOTO.length);
        Log.e(TAG,mContext+" NPWP : "+NPWP.length);
        Log.e(TAG,mContext+" TTD : "+TTD.length);
    }

    @Override
    public void onResume() {
        super.onResume();

        smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                numberOTP = "";
                String dataSMS = intent.getExtras().getString("smsMessage");
                Log.e("CEK","MASUK dataSMS : "+dataSMS);
                String[] sp = dataSMS.split(" ");
                for (int i = 0; i < sp.length; i++) {
                    String word = sp[i].toString();
                    if(word.matches("\\d+(?:\\.\\d+)?")) {
                        numberOTP = word.replaceAll("[^0-9]", "");
                        if (numberOTP.length() == 6) {
                            otp.setText(numberOTP);
                            newString = myFilter(numberOTP);
                            otp.setText(newString);
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
        View views = inflater.inflate(R.layout.fragment_frag_cif_full, container, false);

        inclHead = views.findViewById(R.id.inclHead);
        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);
        iconKtp = (LinearLayout) views.findViewById(R.id.icon_ktp);
        iconSwafoto = (LinearLayout) views.findViewById(R.id.icon_swafoto);
        iconNpwp = (LinearLayout) views.findViewById(R.id.icon_npwp);
        iconSignature = (LinearLayout) views.findViewById(R.id.icon_signature);
        iconForm = (LinearLayout) views.findViewById(R.id.icon_form);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);

        rv_item_expand = (RecyclerView) views.findViewById(R.id.rv_item_expand);

        btnProses = (Button) views.findViewById(R.id.btnProses);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (KTP.length > 0) {
            iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
        }
        if (KTP_SWAFOTO.length > 0) {
            iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
        }
        if (NPWP.length > 0) {
            iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
        }
        if (TTD.length > 0) {
            iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
        }

        iconForm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));

        tvFotoKTP.setText(getString(R.string.pembukaan_akun));

        if (isSessionZoom) {
            TopBar.setVisibility(View.VISIBLE);
            ll_head.setVisibility(View.VISIBLE);
        } else {
            TopBar.setVisibility(View.GONE);
            ll_head.setVisibility(View.VISIBLE);
        }

        dataCIFArr = new JSONArray();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataCIFArr = new JSONArray();
                processGetCIFForm();
            }
        });

        keysCount = 0;
        for(Iterator<String> iter = objValCIF.keys(); iter.hasNext();) {
            String key = iter.next();
            keysCount++;
        }
        Log.e(TAG,"keysCount : "+keysCount);

        processGetCIFForm();

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"SEDANG DIPROSES...!!",Toast.LENGTH_SHORT).show();
                int childRecyle = rv_item_expand.getChildCount();
                Log.e(TAG,"childRecyle : "+childRecyle);
                if (childRecyle > 0) {
                    for (int i = 0; i < childRecyle; i++) {
                        if (rv_item_expand.getChildAt(i) instanceof LinearLayout) {
                            int countChild = ((LinearLayout) rv_item_expand.getChildAt(i)).getChildCount();
                            Log.e(TAG,"countChild : "+countChild);
                            if (countChild > 0) {
                                if (((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0) instanceof ExpansionHeader) {
                                    if (((ExpansionHeader) ((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0)).getChildAt(0) instanceof RelativeLayout) {
                                        TextView tvHeader = (TextView) ((ExpansionHeader) ((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0)).getChildAt(0).findViewById(R.id.tv_nama_product);
                                        Log.e(TAG,"tvHeader : "+tvHeader.getText());
                                    }
                                }

                                if (((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0) instanceof ExpansionLayout) {
                                    LinearLayout container = (LinearLayout) ((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0).findViewById(R.id.container);
                                    int childBodyExpand = container.getChildCount();
                                    Log.e(TAG,"childBodyExpand : "+childBodyExpand);
                                }
                            }
                        }
                    }
                }

                APISaveForm();
            }
        });
    }

    private void processSendOTP() {
        String noHp = no_handphone;
        if (noHp.substring(0,1).equals("0")) {
            noHp = "62"+no_handphone.substring(1);
        }
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("msisdn",noHp);
            dataObjOTP.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","processSendOTP : "+dataObjOTP.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());

        Server.getAPIService().SendOTP(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processSendOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processSendOTP : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        transactionId = dataObj.getJSONObject("data").getString("transactionId");
                        rabbitMirroring.MirroringSendEndpoint(11);
                        PopUpOTP();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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

        Log.e("CEK","processValidateOTP : "+dataObjOTP.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());
        Server.getAPIService().ValidateOTP(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processValidateOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processValidateOTP : "+dataS);
                    PopUpSuccesOtp();
                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processGetCIFForm() {
        int formId = 10;
        processGetForm(formId);
    }

    private void processGetForm(int formId) {
        Log.e(TAG, this+" MASUK processGetForm formId : "+formId);
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e(TAG,"response processGetForm : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e(TAG,"response dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        Log.e(TAG,"dataForm : "+dataForm);
                        if (formId == 10) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("labelForm","Data Diri");
                            obj.put("nameForm","datadiri");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                            if (objValCIF.has("alamatberbeda")) {
                                processGetForm(9);
                            } else if (objValCIF.has("pekerjaan")) {
                                processGetForm(5);
                            }
                        } else if (formId == 9) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("labelForm","Alamat Domisili");
                            obj.put("nameForm","alamatberbeda");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                            if (objValCIF.has("pekerjaan")) {
                                processGetForm(5);
                            }
                        } else if (formId == 5) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("labelForm","Pekerjaan");
                            obj.put("nameForm","pekerjaan");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                            if (objValCIF.has("keuangan")) {
                                processGetForm(14);
                            }
                        } else if (formId == 14) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("labelForm","Keuangan");
                            obj.put("nameForm","keuangan");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG,"keysCount : "+keysCount+" | dataCIFArr.length : "+dataCIFArr.length());
                    if (keysCount == dataCIFArr.length()) {
                        btnProses.setVisibility(View.VISIBLE);
                        rabbitMirroring.MirroringSendKey(objValCIF);
                        setRecylerExpand();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }

    private void setRecylerExpand() {
        Log.e(TAG,"MASUK setRecylerExpand");
        Log.e(TAG,"dataCIFArr : ");
        Log.e(TAG,dataCIFArr.toString());

        AdapterCIF dataExpand = new AdapterCIF(dataCIFArr, mContext, rabbitMirroring);

        recylerViewLayoutManager = new LinearLayoutManager(mContext);

        rv_item_expand.setLayoutManager(recylerViewLayoutManager);
        rv_item_expand.setAdapter(dataExpand);
    }

    private void PopUpOTP(){
        String noHandphone = "089783434XXX";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"XXX");
        }

        View dialogView = getLayoutInflater().inflate(R.layout.item_otp, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        TextView textIBMB = (TextView) dialogView.findViewById(R.id.textIBMB);
        String contentText = textIBMB.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textIBMB.setText(contentText);

        Button btnVerifikasi = (Button) dialogView.findViewById(R.id.btnVerifikasi);
        TextView Timer = (TextView) dialogView.findViewById(R.id.timer_otp);
        TextView Resend_Otp = (TextView) dialogView.findViewById(R.id.btn_resend_otp);
        otp = (PinView) dialogView.findViewById(R.id.otp);
        otp.setAnimationEnable(true);
        otp.addTextChangedListener(new TextWatcher() {
            private boolean backSpaceOTP;
            private int lasLenOTP;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lasLenOTP = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String wordOTP = s.toString();
                Log.e("CEK","wordOTP : "+wordOTP);
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    String getNumberOTP=wordOTP.replaceAll("[^0-9]", "");
                    if (getNumberOTP.length() > 1 && getNumberOTP.length() <= 6) {
                        getNumberOTP = getNumberOTP.substring(getNumberOTP.length()-1,getNumberOTP.length());
                    }
                    if (numberOTP.length() < 6) {
                        numberOTP += getNumberOTP;
                    }
                    JSONObject otpObj = new JSONObject();
                    try {
                        otpObj.put("otp",numberOTP);
                        rabbitMirroring.MirroringSendKey(otpObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpaceOTP = lasLenOTP > s.length();
                Log.e(TAG, "backSpaceOTP : " + backSpaceOTP);
                if (backSpaceOTP) {
                    int lenOTP = numberOTP.length();
                    if (lenOTP > 0) {
                        numberOTP = numberOTP.substring(0, lenOTP - 1);
                    }
                }
                newString = myFilter(s.toString());
                otp.removeTextChangedListener(this);
                handler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        otp.setText(newString);
                    }
                };
                otp.addTextChangedListener(this);
                handler.postDelayed(myRunnable, 1500);
                if (otp.length() == 6 || otp.length() == 0){
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Log.d("TAG","STOP Loop");
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
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    sweetAlertDialog.dismiss();
                    //PopUpSuccesOtp();
                    if (!transactionId.isEmpty()) {
                        processValidateOTP();
                    }
                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    //resendOTP();
                }
            }
        });
    }

    private static JSONObject dataMirroring(JSONObject dataObj) {
        long unixTime = System.currentTimeMillis() / 1000L;

        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("from","Cust");
            jsObj.put("to","CS");
            jsObj.put("created",unixTime);
            jsObj.put("transaction",dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj;
    }

    private void PopUpSuccesOtp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.otp_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.otp_content));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                //rabbitMirroring.MirroringSendEndpoint(12);
                //PopUpSuccesRegistration();
                rabbitMirroring.MirroringSendEndpoint(13);
                getFragmentPage(new frag_cif_resi());
            }
        },5000);
    }

    private void PopUpSuccesRegistration(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.reg_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.reg_content));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.activation));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.Blue));
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                rabbitMirroring.MirroringSendEndpoint(361);
                PopUpTnc();
            }
        });
    }

    private void PopUpTnc(){
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        CheckBox checktnc = (CheckBox) dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);

        checktnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChk = checktnc.isChecked();
                if (isChk) {
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                } else {
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChk) {
                    sweetAlertDialog.dismiss();
                    rabbitMirroring.MirroringSendEndpoint(13);
                    getFragmentPage(new frag_cif_resi());
                } else {
                    Toast.makeText(mContext, R.string.accept_state,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void APISaveForm() {
        String valDataCIF = sessions.getCIF();
        JSONObject dataObjCIF = new JSONObject();
        try {
            JSONObject getObjValCIF = new JSONObject(valDataCIF);
            Log.e(TAG,"APISaveForm CIF FULL objValCIF : "+getObjValCIF.toString());
            dataObjCIF.put("formCode","Opening Cif");
            dataObjCIF.put("idDips",idDips);
            dataObjCIF.put("payload",getObjValCIF);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjCIF.toString());
        Server.getAPIService().saveForm(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","APISaveForm code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","APISaveForm dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String idForm = dataObj.getJSONObject("data").getString("idForm");
                        JSONObject idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);
                        rabbitMirroring.MirroringSendKey(idFormObj);

                        processSendOTP();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    public String myFilter(String s) {
        String digits;
        digits = s.replaceAll("[0-9]", "*");
        if (s.equals("")) return "";
        return digits;
    }
    public void runTimer(TextView timer_run, TextView resend) {
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

}