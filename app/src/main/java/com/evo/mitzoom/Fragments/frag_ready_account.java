package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class frag_ready_account extends Fragment {

    private int REQUEST_WRITE_PERMISSION = 786;
    private int REQUESTCODE_CAPTURE = 1;
    private int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_SWAFOTO = 10;
    private int REQUESTCODE_GALLERY = 2;

    private Context mContext;
    private SessionManager sessions;
    private String idDips;
    private int formCode;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private LinearLayout llFormBuild;
    private Button btnProses;

    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
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


    private SwipeRefreshLayout swipe;
    private int form_id = 0;
    private JSONArray idElement;
    private JSONObject dataFormObj = null;
    private String keysData = "";
    JSONObject objEl = new JSONObject();
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private int lasLenChar;
    private boolean backSpaceChar;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String picturePath = "";
    private TextView tvTitleReady;
    private ImageView btn_back;
    private boolean flagStuckSpin = false;
    private JSONObject idFormObj;
    private LinearLayout TopBar;
    private LinearLayout ll_head;
    private LinearLayout iconCircle1;
    private LinearLayout iconCircle2;
    private LinearLayout iconCircle3;
    private LinearLayout iconCircle4;
    private TextView tvFotoKTP;
    private String transactionId = "";
    private int loopStatus = 0;
    private JSONObject dataNasabahObj = null;
    String namaLengkap = "";
    String alamat = "";
    String nik = "";
    private String branchCode = "";
    private String NoCIF;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();
        NoCIF = sessions.getNoCIF();
        String dataNasabah = sessions.getNasabah();
        Log.e("CEK",mContext+" formCode : "+formCode+" | NoCIF : "+NoCIF);
        Log.e("CEK",mContext+" dataNasabah : "+dataNasabah);
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                if (dataNasabahObj.has("namaLengkap")) {
                    namaLengkap = dataNasabahObj.getString("namaLengkap");
                }
                if (dataNasabahObj.has("alamat")) {
                    alamat = dataNasabahObj.getString("alamat");
                }
                if (dataNasabahObj.has("noHp")) {
                    no_handphone = dataNasabahObj.getString("noHp");
                }
                if (dataNasabahObj.has("nik")) {
                    nik = dataNasabahObj.getString("nik");
                }
                if (dataNasabahObj.has("branchCode")) {
                    branchCode = dataNasabahObj.getString("branchCode");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("CEK",mContext+" namaLengkap : "+namaLengkap+" | alamat : "+alamat);
        Log.e("CEK",mContext+" no_handphone : "+no_handphone+" | nik : "+nik+" | branchCode : "+branchCode);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (no_handphone.isEmpty()) {
            no_handphone = "089637407882";
        }
        Log.e("CEK",mContext+" isSessionZoom : "+isSessionZoom+" | no_handphone : "+no_handphone);
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }
        
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
        View views = inflater.inflate(R.layout.frag_ready_account, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);
        iconCircle1 = (LinearLayout) views.findViewById(R.id.iconCircle1);
        iconCircle2 = (LinearLayout) views.findViewById(R.id.iconCircle2);
        iconCircle3 = (LinearLayout) views.findViewById(R.id.iconCircle3);
        iconCircle4 = (LinearLayout) views.findViewById(R.id.iconCircle4);

        btn_back = (ImageView) views.findViewById(R.id.btn_back);
        tvTitleReady = (TextView) views.findViewById(R.id.tvTitleReady);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        btnProses = (Button) views.findViewById(R.id.btnProses);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);

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

        ll_head.setVisibility(View.VISIBLE);
        tvFotoKTP.setText(getString(R.string.pembukaan_akun));

        form_id = 19;
        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            }
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                processGetForm(form_id);
            }
        });

        String label = getString(R.string.info_first_data);
        iconCircle1.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));

        if (formCode == 150) {
            label = label.replaceAll("\n"," ");
            tvTitleReady.setText(label);
            keysData = "datautama";
        } else if (formCode == 151) {
            keysData = "tiperekening";
        } else if (formCode == 152) {
            keysData = "giro";
        } else if (formCode == 153) {
            keysData = "tabungan";
        } else if (formCode == 154) {
            keysData = "tabunganberjangka";
        } else if (formCode == 155) {
            keysData = "fasilitaslayanan";
        } else if (formCode == 156) {
            keysData = "tnc35";
        }

        if (formCode > 150 && formCode < 156) {
            iconCircle1.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconCircle2.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            label = getString(R.string.data_account_open);
            label = label.replaceAll("\n"," ");
            tvTitleReady.setText(label);
        } else if (formCode == 155) {
            iconCircle1.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconCircle2.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconCircle3.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            label = getString(R.string.facilities_services);
            label = label.replaceAll("\n"," ");
            tvTitleReady.setText(label);
        } else if (formCode == 156) {
            iconCircle1.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconCircle2.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconCircle3.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconCircle4.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            label = getString(R.string.statement);
            label = label.replaceAll("\n"," ");
            tvTitleReady.setText(label);
        }

        dataFormObj = dataReqForm();
        Log.e("CEK","dataFormObj AWAL : "+dataFormObj.toString());
        if (dataFormObj.length() > 0) {
            if (formCode == 150) {
                if (dataFormObj.has(keysData)) {
                    try {
                        JSONObject getDatax = dataFormObj.getJSONObject(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (formCode == 151) {
                if (dataFormObj.has(keysData)) {
                    try {
                        String getDatax = dataFormObj.getString(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (formCode == 152) {
                if (dataFormObj.has(keysData)) {
                    try {
                        JSONObject getDatax = dataFormObj.getJSONObject(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (formCode == 153) {
                if (dataFormObj.has(keysData)) {
                    try {
                        String getDatax = dataFormObj.getString(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (formCode == 154) {
                if (dataFormObj.has(keysData)) {
                    try {
                        JSONObject getDatax = dataFormObj.getJSONObject(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (formCode == 155) {
                if (dataFormObj.has(keysData)) {
                    try {
                        JSONObject getDatax = dataFormObj.getJSONObject(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (formCode == 156) {
                if (dataFormObj.has(keysData)) {
                    try {
                        boolean getDatax = dataFormObj.getBoolean(keysData);
                        objEl.put(keysData,getDatax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        processGetForm(form_id);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Fragment fragment = new frag_ready_account();
                if (formCode == 151) {
                    bundle.putInt("form_id",19);
                    sessions.saveFormCOde(150);
                    rabbitMirroring.MirroringSendEndpoint(150);
                } else if (formCode == 152) {
                    bundle.putInt("form_id",29);
                    sessions.saveFormCOde(151);
                    rabbitMirroring.MirroringSendEndpoint(151);
                } else if (formCode == 153) {
                    bundle.putInt("form_id",29);
                    sessions.saveFormCOde(151);
                    rabbitMirroring.MirroringSendEndpoint(151);
                } else if (formCode == 154) {
                    bundle.putInt("form_id",29);
                    sessions.saveFormCOde(151);
                    rabbitMirroring.MirroringSendEndpoint(151);
                } else if (formCode == 155) {
                    String tiperekening = "";
                    if (dataFormObj.has("tiperekening")) {
                        try {
                            tiperekening = dataFormObj.getString("tiperekening");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (tiperekening.toLowerCase().trim().equals("tabungan")) {
                        bundle.putInt("form_id", 21);
                        sessions.saveFormCOde(153);
                        rabbitMirroring.MirroringSendEndpoint(153);
                    } else if (tiperekening.toLowerCase().trim().equals("giro")) {
                        bundle.putInt("form_id",20);
                        sessions.saveFormCOde(152);
                        rabbitMirroring.MirroringSendEndpoint(152);
                    } else if (tiperekening.toLowerCase().trim().contains("jangka")) {
                        bundle.putInt("form_id",22);
                        sessions.saveFormCOde(154);
                        rabbitMirroring.MirroringSendEndpoint(154);
                    }

                } else if (formCode == 156) {
                    bundle.putInt("form_id",30);
                    sessions.saveFormCOde(155);
                    rabbitMirroring.MirroringSendEndpoint(155);
                } else {
                    fragment = new frag_open_account_product();
                    sessions.saveFormCOde(201);
                    rabbitMirroring.MirroringSendEndpoint(201);
                }

                sendDataFragment(bundle, fragment);
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int child = llFormBuild.getChildCount();

                if (child > 0 && idElement.length() > 0) {
                    boolean flagNext = true;
                    for (int i = 0; i < child; i++) {
                        boolean checkEmpty = false;
                        int idEl = llFormBuild.getChildAt(i).getId();
                        if (idEl > 0 || idEl < -1) {
                            for (int j = 0; j < idElement.length(); j++) {
                                try {
                                    int idDataEl = idElement.getJSONObject(j).getInt("id");
                                    String nameDataEl = idElement.getJSONObject(j).getString("name");
                                    String labelDataEl = idElement.getJSONObject(j).getString("label");
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            } else if (nameDataEl.contains("noponsel") && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                            RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = (RadioButton) rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            } else if (requiredDataEl){
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                            CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                            boolean isChk = chk.isChecked();
                                            if (isChk) {
                                                objEl.put(nameDataEl, isChk);
                                            } else {
                                                objEl.put(nameDataEl, false);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                            Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                            if (spin.isSelected()) {
                                                String results = spin.getSelectedItem().toString();
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                            String results = autoText.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                                break;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                            LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (checkEmpty) {
                                flagNext = false;
                                break;
                            } else {
                                flagNext = true;
                            }
                        }
                    }

                    Log.e("CEK","flagNext : "+flagNext);
                    Log.e("CEK","objEl : "+objEl.toString());
                    if (flagNext) {
                        processNext();
                    }
                }
            }
        });

    }

    private void processNext() {
        dataFormObj = dataReqForm();
        if (formCode < 156) {
            Log.e("CEK","dataFormObj : "+dataFormObj.toString());
            sessions.saveFormReq(dataFormObj.toString());
            Bundle bundle = new Bundle();
            Fragment fragment = new frag_ready_account();
            if (formCode == 150) {
                bundle.putInt("form_id",29);
                sessions.saveFormCOde(151);
                rabbitMirroring.MirroringSendEndpoint(151);
            } else if (formCode == 151) {
                String tiperekening = "";
                try {
                    tiperekening = objEl.getString("tiperekening");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (tiperekening.toLowerCase().trim().equals("tabungan")) {
                    bundle.putInt("form_id", 21);
                    sessions.saveFormCOde(153);
                    rabbitMirroring.MirroringSendEndpoint(153);
                } else if (tiperekening.toLowerCase().trim().equals("giro")) {
                    bundle.putInt("form_id",20);
                    sessions.saveFormCOde(152);
                    rabbitMirroring.MirroringSendEndpoint(152);
                } else if (tiperekening.toLowerCase().trim().contains("jangka")) {
                    bundle.putInt("form_id",22);
                    sessions.saveFormCOde(154);
                    rabbitMirroring.MirroringSendEndpoint(154);
                }
            } else if (formCode == 152) {
                bundle.putInt("form_id",30);
                sessions.saveFormCOde(155);
                rabbitMirroring.MirroringSendEndpoint(155);
            } else if (formCode == 153) {
                bundle.putInt("form_id",30);
                sessions.saveFormCOde(155);
                rabbitMirroring.MirroringSendEndpoint(155);
            } else if (formCode == 154) {
                bundle.putInt("form_id",30);
                sessions.saveFormCOde(155);
                rabbitMirroring.MirroringSendEndpoint(155);
            } else if (formCode == 155) {
                bundle.putInt("form_id",35);
                sessions.saveFormCOde(156);
                rabbitMirroring.MirroringSendEndpoint(156);
            }

            sendDataFragment(bundle, fragment);
        } else {
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
            APISaveForm();
        }
    }

    private void APISaveForm() {
        JSONObject reqFormSend = dataReqForm();
        Log.e("CEK", "reqFormSend : " + reqFormSend.toString());
        JSONObject dataObjCIF = new JSONObject();
        try {
            dataObjCIF.put("formCode","Create Account");
            dataObjCIF.put("idDips",idDips);
            dataObjCIF.put("payload",reqFormSend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CEK", "APISaveForm : " + dataObjCIF.toString());

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
                        idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);

                        processSendOTP();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Log.e("CEK","processSendOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processSendOTP : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        transactionId = dataObj.getJSONObject("data").getString("transactionId");
                        rabbitMirroring.MirroringSendEndpoint(11);
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

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        swipe.setVisibility(View.GONE);
        TopBar.setVisibility(View.GONE);
        ll_head.setVisibility(View.VISIBLE);

        String noHandphone = "089783434XXX";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"XXX");
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
                        Log.e("CEK","numberOTP : "+numberOTP);
                        otpObj.put("otp",numberOTP);
                        rabbitMirroring.MirroringSendKey(otpObj);
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
                    /*handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);*/
                    if (!transactionId.isEmpty()) {
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
                    resendOTP();
                }
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
                    JSONObject reqFormSend = dataReqForm();
                    
                    try {
                        String idForm = idFormObj.getString("idForm");
                        
                        JSONObject dataObjAccount = reqFormSend.getJSONObject("pembukaanakun");
                        dataObjAccount.put("noponsel",no_handphone);
                        dataObjAccount.put("idForm",idForm);

                        reqFormSend.put("pembukaanakun", dataObjAccount);

                        Log.e("CEK","dataObjAccount : "+dataObjAccount.toString());
                        rabbitMirroring.MirroringSendKey(reqFormSend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
                    processApprovalStatus();
                } else {
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
        Log.e("CEK", this+" processApprovalStatus PARAMS : "+idFormObj.toString());
        String idForm = "";
        try {
            idForm = idFormObj.getString("idForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalIdForm = idForm;
        Server.getAPIService().ApprovalStatus(idForm).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK", this+" processApprovalStatus code : "+response.code());
                if (response.isSuccessful()) {
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
                    String dataS = response.body().toString();
                    Log.e("CEK","processApprovalStatus dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.getJSONObject("data").has("noCif")) {
                            if (!dataObj.getJSONObject("data").getString("noCif").isEmpty()) {
                                String noCif = dataObj.getJSONObject("data").getString("noCif");
                                sessions.saveNoCIF(noCif);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendEndpoint(131);
                    Bundle bundle = new Bundle();
                    bundle.putInt("formCode",131);
                    bundle.putString("idForm", finalIdForm);
                    sendDataFragment(bundle,new frag_cif_resi());
                } else {
                    if (loopStatus >= 10) {
                        Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
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
                    }
                    if (loopStatus < 10) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        processApprovalStatus();
                                    }
                                });
                                loopStatus++;
                            }
                        },10000);

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK", this+" processApprovalStatus onFailure : "+t.getMessage());
                if (loopStatus >= 10) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (loopStatus < 10) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processApprovalStatus();
                                }
                            });
                            loopStatus++;
                        }
                    },10000);
                }
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

    private JSONObject dataReqForm() {
        JSONObject reqFormSend = null;
        try {
            if (sessions.getFormReq() != null) {
                String forms = sessions.getFormReq();
                Log.e("CEK","sessions.getFormReq : "+forms);
                reqFormSend = new JSONObject(forms);
            } else {
                reqFormSend = new JSONObject();
            }

            JSONObject dataObj = null;
            if (reqFormSend.has("pembukaanakun")) {
                dataObj = reqFormSend.getJSONObject("pembukaanakun");
            }

            if (dataObj == null) {
                JSONObject ObjCif = new JSONObject();
                ObjCif.put(keysData, objEl);
                reqFormSend.put("pembukaanakun", ObjCif);
            } else {
                if (formCode == 151 || formCode == 153 || formCode == 156) {
                    String valGet = "";
                    for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                        String key = iter.next();
                        if (formCode == 151) {
                            if (key.contains("rekening") || key.contains("account")) {
                                valGet = objEl.getString(key);
                                break;
                            }
                        } else if (formCode == 153) {
                            if (key.contains("tabungan") || key.contains("saving")) {
                                valGet = objEl.getString(key);
                                break;
                            }
                        } else if (formCode == 156) {
                            if (key.contains("setuju") || key.contains("menyetujui") || key.contains("accept")) {
                                boolean valGetBol = objEl.getBoolean(key);
                                dataObj.put(keysData, valGetBol);
                                break;
                            }
                        }
                    }
                    if (formCode != 156) {
                        dataObj.put(keysData, valGet);
                    }
                    reqFormSend.put("pembukaanakun", dataObj);
                } else {
                    dataObj.put(keysData, objEl);
                    reqFormSend.put("pembukaanakun", dataObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reqFormSend;
    }

    private void processGetForm(int formId) {
        Log.e("CEK", this+" MASUK processGetForm formId : "+formId);
        Log.e("CEK", this+" MASUK formCode : "+formCode);
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                swipe.setRefreshing(false);
                Log.e("CEK","response processGetForm : "+response.code());
                if (response.isSuccessful()) {
                    btnProses.setVisibility(View.VISIBLE);
                    String dataS = response.body().toString();
                    Log.e("CEK","response dataS : "+dataS);
                    llFormBuild.removeAllViewsInLayout();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        Log.e("CEK","dataForm : "+dataForm);
                        MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = parseForm.getForm();
                        Log.e("CEK","dataElement : "+idElement.toString());
                        processValidationActionForm();
                        processMatchData();
                        Log.e("CEK","AFTER processMatchData : "+objEl.toString());
                        processDataFromOCR();
                        Log.e("CEK","DATA FORM : "+dataFormObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void processMatchData() {
        for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
            if (iter.hasNext()) {
                String key = iter.next();
                String valKurung = "";
                int indx = key.indexOf("(");
                if (indx >= 0) {
                    valKurung = key.substring(indx);
                }
                Log.e("CEK","key : "+key+" | valKurung : "+valKurung);
                try {
                    if (key.toLowerCase().contains("nama") && key.toLowerCase().contains("identitas"+valKurung)) {
                        Log.e("CEK","MASUK IF");
                        objEl.put(key, namaLengkap);
                    } else if (key.toLowerCase().contains("cif"+valKurung)) {
                        Log.e("CEK","MASUK IF 2");
                        objEl.put(key, NoCIF);
                    } else if (key.toLowerCase().contains("nama") && key.toLowerCase().contains("nasabah"+valKurung)) {
                        Log.e("CEK","MASUK IF 3");
                        objEl.put(key, namaLengkap);
                    } else if ((key.toLowerCase().contains("no") || key.toLowerCase().contains("nomor")) && key.toLowerCase().contains("identitas"+valKurung)) {
                        Log.e("CEK","MASUK IF 4");
                        objEl.put(key, nik);
                    } else if (key.toLowerCase().contains("tanggal") && key.toLowerCase().contains("rekening"+valKurung)) {
                        Log.e("CEK","MASUK IF 5");
                        String timeStamp = new SimpleDateFormat("dd-MM-yyyy",
                                Locale.getDefault()).format(new Date());
                        objEl.put(key, timeStamp);
                    } else if (key.toLowerCase().contains("kode") && key.toLowerCase().contains("cabang"+valKurung)) {
                        Log.e("CEK","MASUK IF 6");
                        objEl.put(key, branchCode);
                    }
                } catch (JSONException e) {

                }
            }
        }
    }

    private void processDataFromOCR() {
        Log.e("CEK","processDataFromOCR : "+objEl.toString());
        int child = llFormBuild.getChildCount();

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            if (idEl == idDataEl) {
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
                                    ed.setText(valEl);
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = (RadioButton) rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();
                                        String valEl = objEl.getString(nameDataEl);
                                        String valRad = valEl.toLowerCase();

                                        String valKurung = "";
                                        int indx = valEl.indexOf("(");
                                        if (indx >= 0) {
                                            valKurung = valEl.substring(indx);
                                        }

                                        if (valEl.toLowerCase().equals("kawin"+valKurung)) {
                                            valRad = "menikah";
                                        }
                                        Log.e("CEK","labelRad : "+labelRad+" | valEl : "+valEl);
                                        if (labelRad.toLowerCase().equals(valRad)) {
                                            rb.setChecked(true);
                                            break;
                                        } else {

                                            if (valRad.contains("laki") && valRad.contains("-")) {
                                                String[] sp = valRad.split("-");
                                                valRad = sp[0]+" - "+sp[1];
                                                if (labelRad.toLowerCase().equals(valRad)) {
                                                    rb.setChecked(true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    String labelCheck = chk.getText().toString();
                                    if (objEl.has(nameDataEl)) {
                                        boolean valEl = objEl.getBoolean(nameDataEl);
                                        chk.setChecked(valEl);
                                    } else if (objEl.has(labelCheck)) {
                                        boolean valEl = objEl.getBoolean(labelCheck);
                                        chk.setChecked(valEl);
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
                                    for (int ch = 0; ch < spin.getCount(); ch++) {
                                        if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                            spin.setSelection(ch);
                                            break;
                                        }
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
                                    autoText.setText(valEl);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void processValidationActionForm() {
        Log.e("CEK","processValidationActionForm");
        int child = llFormBuild.getChildCount();

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            String valKurung = "";
                            int indx = nameDataEl.indexOf("(");
                            if (indx >= 0) {
                                valKurung = nameDataEl.substring(indx);
                            }
                            String urlPath = "";
                            if (idElement.getJSONObject(j).has("url")) {
                                urlPath = idElement.getJSONObject(j).getString("url");
                            }
                            if (idEl == idDataEl) {
                                String finalValKurung = valKurung;
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            Log.e("CEK","onFocusChange : "+b);
                                            if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataFormObj);
                                            }
                                        }
                                    });
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp"+ finalValKurung)) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            Log.e("CEK",nameDataEl+" : "+charSequence);
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormObj = dataReqForm();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            /*if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataFormObj);
                                            }*/
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (nameDataEl.equals("npwp"+ finalValKurung)) {
                                                ed.removeTextChangedListener(this);
                                                backSpaceChar = lasLenChar > s.length();
                                                if (!backSpaceChar) {
                                                    String dataNPWP = s.toString();
                                                    Log.e("CEK", "dataNPWP : " + dataNPWP);
                                                    String formatNPWP = "";
                                                    if (dataNPWP.length() == 2 || dataNPWP.length() == 6 || dataNPWP.length() == 10 || dataNPWP.length() == 16) {
                                                        formatNPWP = ".";
                                                    } else if (dataNPWP.length() == 12) {
                                                        formatNPWP = "-";
                                                    }
                                                    String cekBuilder = new StringBuilder(dataNPWP).insert(dataNPWP.length(), formatNPWP).toString();
                                                    ed.setText(cekBuilder);
                                                    ed.setSelection(cekBuilder.length());
                                                }
                                                ed.addTextChangedListener(this);
                                            }
                                        }
                                    });
                                    objEl.put(nameDataEl, "");
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    objEl.put(nameDataEl, "");

                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = (RadioButton) rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormObj = dataReqForm();
                                                    if (isSessionZoom) {
                                                        rabbitMirroring.MirroringSendKey(dataFormObj);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    objEl.put(nameDataEl, false);

                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    chk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            boolean isChk = chk.isChecked();
                                            if (isChk) {
                                                try {
                                                    objEl.put(nameDataEl, isChk);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    objEl.put(nameDataEl, isChk);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            dataFormObj = dataReqForm();

                                            if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataFormObj);
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    objEl.put(nameDataEl, "");
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = spin.getSelectedItem().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormObj = dataReqForm();
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataFormObj);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        objEl.put(nameDataEl, "");
                                        Spinner spin = (Spinner) rl.getChildAt(0);

                                        boolean flagDot = false;
                                        if (!urlPath.isEmpty()) {
                                            String[] spUrl = urlPath.split("/");
                                            int indexs = spUrl.length - 1;
                                            String check = spUrl[indexs];
                                            if (check.isEmpty()) {
                                                indexs = spUrl.length - 2;
                                                check = spUrl[indexs];
                                            }
                                            if (check.contains(":")) {
                                                flagDot = true;
                                            }
                                            if (!flagDot) {
                                                processGetDynamicURL(spin, urlPath, nameDataEl);
                                            }
                                        }

                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                Log.e("CEK","getSelectedItem : "+spin.getSelectedItem().toString());
                                                FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                int idData = dataSpin.getId();
                                                String results = dataSpin.getName();
                                                Log.e("CEK","dataSpin.getId() : "+idData);
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormObj = dataReqForm();
                                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                        valSpinProv.put(nameDataEl,idData);
                                                    } else {
                                                        valSpin.put(nameDataEl, idData);
                                                    }
                                                    if (isSessionZoom) {
                                                        rabbitMirroring.MirroringSendKey(dataFormObj);
                                                    }
                                                    Log.e("CEK","flagStuckSpin : "+flagStuckSpin);
                                                    if (flagStuckSpin) {
                                                        processGetSpinChild(nameDataEl);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                        break;
                                    }
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    objEl.put(nameDataEl, "");

                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = autoText.getText().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormObj = dataReqForm();
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataFormObj);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            String results = autoText.getText().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormObj = dataReqForm();
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataFormObj);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    Log.e("CEK", "LinearLayout getChildCount : " + ll.getChildCount());
                                    if (ll.getChildCount() > 1) {
                                        if (ll.getChildAt(0) instanceof LinearLayout) {
                                            LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
                                            Log.e("CEK", "MASUK LinearLayout CHILD ke-" + i);

                                            TextView tvll = (TextView) ll2.getChildAt(1);
                                            String txt = tvll.getText().toString();
                                            Log.e("CEK", "tvll : " + txt);
                                            if (txt.toLowerCase().indexOf("gambar") > 0 || txt.toLowerCase().indexOf("image") > 0) {
                                                tvSavedImg = (TextView) ll.getChildAt(1);
                                                ll2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        REQUESTCODE_GALLERY = 201;
                                                        sessions.saveMedia(2);
                                                        chooseFromSD();
                                                    }
                                                });
                                            } else {
                                                tvSavedFile = (TextView) ll.getChildAt(1);
                                                ll2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent();
                                                        intent.setType("*/*");
                                                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                        String[] mimetypes = { "application/pdf", "application/doc", "text/*" };

                                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                                        startActivityForResult(intent, REQUESTCODE_FILE);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void processGetDynamicURL(Spinner spin, String urlPath, String nameDataEl) {
        flagStuckSpin = false;
        Log.e("CEK","processGetDynamicURL : "+urlPath);
        Server.getAPIService().getDynamicUrl(urlPath).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processGetDynamicURL code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processGetDynamicURL dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String nameAction = dataObj.getString("name");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        if (nameAction.equals("ProdukDropdown")) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = dataArr.getJSONObject(i).getInt("id");
                                String kode = dataArr.getJSONObject(i).getString("kode");
                                String namaProduk = dataArr.getJSONObject(i).getString("namaProduk");

                                dataDropDown.add(new FormSpin(idData, kode, namaProduk, namaProduk));
                            }
                        } else {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = dataArr.getJSONObject(i).getInt("id");
                                String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                                String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                                dataDropDown.add(new FormSpin(idData, labelIdn, labelIdn, labelEng));
                                if (i == 0) {
                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                        valSpinProv.put(nameDataEl, idData);
                                    } else {
                                        valSpin.put(nameDataEl, idData);
                                    }
                                    processGetSpinChild(nameDataEl);
                                    if ((nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                        flagStuckSpin = true;
                                    }
                                }
                            }
                        }
                        ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, android.R.layout.simple_spinner_dropdown_item, dataDropDown);
                        spin.setAdapter(adapter2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processGetSpinChild(String nameDataEl) {
        Log.e("CEK","processGetSpinChild nameDataEl : "+nameDataEl);
        int child = llFormBuild.getChildCount();
        for (int i = 0; i < child; i++) {
            int idEl = llFormBuild.getChildAt(i).getId();
            for (int j = 0; j < idElement.length(); j++) {
                try {
                    int idDataEl = idElement.getJSONObject(j).getInt("id");
                    String getnameDataEl = idElement.getJSONObject(j).getString("name");
                    String urlPath = "";
                    if (idElement.getJSONObject(j).has("url")) {
                        urlPath = idElement.getJSONObject(j).getString("url");
                    }

                    if (idEl == idDataEl) {
                        if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                            if (nameDataEl.contains("provinsi") && (getnameDataEl.contains("kabupaten") || getnameDataEl.contains("kota"))) {
                                Log.e("CEK","processGetSpinChild getnameDataEl : "+getnameDataEl);
                                if (!urlPath.isEmpty()) {
                                    int idProv = valSpinProv.getInt("provinsi");
                                    String idSpin = String.valueOf(idProv);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin);

                                    Log.e("CEK", "urlNew : "+urlNew);

                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        processGetDynamicURL(spin, urlNew, getnameDataEl);
                                    }
                                }
                            } else if ((nameDataEl.contains("kabupaten") || nameDataEl.contains("kota")) && getnameDataEl.contains("kecamatan")) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = valSpinProv.getInt("provinsi");
                                    int idKabKot = 0;
                                    if (valSpinProv.has("kabupaten")) {
                                        idKabKot = valSpinProv.getInt("kabupaten");
                                    } else if (valSpinProv.has("kota")) {
                                        idKabKot = valSpinProv.getInt("kota");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    String idSpin2 = String.valueOf(idKabKot);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin).replace(":id_kabupaten",idSpin2);

                                    Log.e("CEK", "urlNew : "+urlNew);

                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        processGetDynamicURL(spin, urlNew, getnameDataEl);
                                    }
                                }
                            } else if (nameDataEl.contains("kecamatan") && (getnameDataEl.contains("kelurahan") || getnameDataEl.contains("desa"))) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = valSpinProv.getInt("provinsi");
                                    int idKec = valSpinProv.getInt("kecamatan");
                                    int idKabKot = 0;
                                    if (valSpinProv.has("kabupaten")) {
                                        idKabKot = valSpinProv.getInt("kabupaten");
                                    } else if (valSpinProv.has("kota")) {
                                        idKabKot = valSpinProv.getInt("kota");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    String idSpin2 = String.valueOf(idKabKot);
                                    String idSpin3 = String.valueOf(idKec);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin).replace(":id_kabupaten",idSpin2).replace(":id_kecamatan",idSpin3);

                                    Log.e("CEK", "urlNew : "+urlNew);

                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        processGetDynamicURL(spin, urlNew, getnameDataEl);
                                    }
                                }
                            } else {
                                flagStuckSpin = true;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void chooseFromSD() {
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {

        }
    }

    private void sendDataFragment(Bundle bundle, Fragment fragment){
        fragment.setArguments(bundle);
        if (isSessionZoom) {
            getFragmentPage(fragment);
        } else {
            getFragmentPageDefault(fragment);
        }
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean getFragmentPageDefault(Fragment fragment){
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

}