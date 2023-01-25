package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.evo.mitzoom.API.ApiService;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_item extends Fragment {

    private int REQUEST_WRITE_PERMISSION = 786;
    private int REQUESTCODE_CAPTURE = 1;
    private int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_SWAFOTO = 10;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private SessionManager sessions;
    private String idDips;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private TextView tvTitleService;
    private SwipeRefreshLayout swipe;
    private ImageView btnBack;
    private LinearLayout llFormBuild;
    private Button btnProses;
    private int formCode;
    private int form_id = 0;
    private JSONArray idElement;
    private String keys = "";
    private JSONObject dataForms = null;
    JSONObject objEl = new JSONObject();
    private int lasLenChar;
    private boolean backSpaceChar;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String imgBase64;
    private byte[] imageBytes = new byte[0];
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
    private String transactionId = "";

    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private boolean flagStuckSpin = false;
    private String getCodeType = "";
    private String FilePaths = "";

    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private String noPengaduan;
    private String keyUpload = "";
    private JSONObject dataNasabahObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        formCode = sessions.getFormCode();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

        String dataNasabah = sessions.getNasabah();
        Log.e("CEK",mContext+" dataNasabah : "+dataNasabah);
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

        Log.e("CEK",mContext+" isSessionZoom : "+isSessionZoom+" | no_handphone : "+no_handphone);

        if (no_handphone.isEmpty()) {
            no_handphone = "089637407882";
        }
        
        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            }
        }
        sessions.saveNoComplaint(null);
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
                            /*newString = myFilter(numberOTP);
                            otp.setText(newString);*/
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
        View views = inflater.inflate(R.layout.fragment_dynamic_layout, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvTitleService = (TextView) views.findViewById(R.id.tvTitleService);
        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);

        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);

        scrollOTP = (NestedScrollView) views.findViewById(R.id.scrollOTP);
        inclOTP = views.findViewById(R.id.inclOTP);
        imgDialog = (ImageView) views.findViewById(R.id.imgDialog);
        textTitleOTP = (TextView) views.findViewById(R.id.textIBMB);
        btnVerifikasi = (Button) views.findViewById(R.id.btnVerifikasi);
        TimerOTP = (TextView) views.findViewById(R.id.timer_otp);
        Resend_Otp = (TextView) views.findViewById(R.id.btn_resend_otp);
        otp = (PinView) views.findViewById(R.id.otp);

        btnProses = (Button) views.findViewById(R.id.btnProses);
        
        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String headlines = "";
        if (formCode == 35) {
            headlines = getString(R.string.formulir_komplain);
            keys = "komplain";
        }

        tvTitleService.setText(headlines);

        dataForms = new JSONObject();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataForms = new JSONObject();
                processGetForm(form_id);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rabbitMirroring.MirroringSendEndpoint(14);
                getFragmentPage(new frag_service_new());
            }
        });

        processGetForm(form_id);

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
                                            Log.e("CEK", "MASUK EDITTEXT ke-" + i);
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                            Log.e("CEK", "MASUK RadioGroup ke-" + i);
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
                                            Log.e("CEK", "MASUK CheckBox ke-" + i);
                                            CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                            boolean isChk = chk.isChecked();
                                            if (isChk) {
                                                objEl.put(nameDataEl, isChk);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                            Log.e("CEK", "MASUK Spinner ke-" + i);
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
                                            Log.e("CEK", "MASUK AutoCompleteTextView ke-" + i);
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
                                            Log.e("CEK", "MASUK LinearLayout ke-" + i);
                                            LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                            Log.e("CEK", "LinearLayout getChildCount : " + ll.getChildCount());
                                            if (ll.getChildCount() > 1) {
                                                if (ll.getChildAt(0) instanceof LinearLayout) {
                                                    LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
                                                    TextView tvll = (TextView) ll2.getChildAt(1);
                                                    String txt = tvll.getText().toString();
                                                    Log.e("CEK", "tvll : " + txt);
                                                    //objEl.put(nameDataEl, imgBase64);
                                                }
                                            }
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

                    if (flagNext) {
                        if (FilePaths.isEmpty()) {
                            Toast.makeText(mContext,"Mohon untuk upload foto tanda tangan",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
                        processSendFormCompaint();
                    }
                }
            }
        });
    }

    private void processSendFormCompaint() {
        File file = new File(FilePaths);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);

        RequestBody requestgetCodeType = RequestBody.create(MediaType.parse("text/plain"), getCodeType);

        String tgl = "";
        String keluhan = "";
        try {
            String keyTanggal = "tanggal";
            String keyKeluhan = "keluhan";
            for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                String key = iter.next();
                String valKurung = "";
                int indx = key.indexOf("(");
                if (indx >= 0) {
                    valKurung = key.substring(indx);
                }

                if (key.equals(keyTanggal+valKurung)) {
                    keyTanggal = key;
                }

                if (key.equals(keyKeluhan+valKurung)) {
                    keyKeluhan = key;
                }
            }
            tgl = objEl.getString(keyTanggal);
            keluhan = objEl.getString(keyKeluhan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","processSendFormCompaint tgl : "+tgl+" | getCodeType : "+getCodeType);

        RequestBody requesttgl = RequestBody.create(MediaType.parse("text/plain"), tgl);
        RequestBody requestPrihal = RequestBody.create(MediaType.parse("text/plain"), keluhan);
        RequestBody requestidDips = RequestBody.create(MediaType.parse("text/plain"), idDips);

        MultipartBody multipartBody = new MultipartBody.Builder()
                .addPart(MultipartBody.Part.createFormData("ttd", file.getName(), requestFile))
                .addPart(MultipartBody.Part.createFormData("idJenis", null, requestgetCodeType))
                .addPart(MultipartBody.Part.createFormData("tanggal", null, requesttgl))
                .addPart(MultipartBody.Part.createFormData("prihal", null, requestPrihal))
                .addPart(MultipartBody.Part.createFormData("idDips", null, requestidDips))
                .build();
        String contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.formComplaint(contentType,multipartBody);
        Log.e("CEK","processSendFormCompaint call : "+call.request());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processSendFormCompaint code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processSendFormCompaint dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        noPengaduan = dataObj.getJSONObject("data").getString("noPengaduan");
                        sessions.saveNoComplaint(noPengaduan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject dataMirr = null;
                    try {
                        dataMirr = new JSONObject(objEl.toString());
                        dataMirr.put("noponsel",no_handphone);
                        dataMirr.put("noPengaduan",noPengaduan);
                        dataForms.put(keys,dataMirr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(dataForms);
                    processSendOTP();
                } else {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
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

    private void processGetForm(int formId) {
        Log.e("CEK", this+" MASUK processGetForm formId : "+formId);
        Log.e("CEK", this+" MASUK formCode : "+formCode);
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","response processGetForm : "+response.code());
                if (response.isSuccessful()) {
                    llFormBuild.setVisibility(View.VISIBLE);
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
                        keyUpload = "";
                        processValidationActionForm();
                        dataForms.put(keys,objEl);
                        Log.e("CEK","DATA FORM : "+dataForms.toString());
                        rabbitMirroring.MirroringSendKey(dataForms);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }

    private void processValidationActionForm() {
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
                                                rabbitMirroring.MirroringSendKey(dataForms);
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
                                                dataForms.put(keys,objEl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            /*if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataFormCIF);
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
                                                    dataForms.put(keys,objEl);
                                                    if (isSessionZoom) {
                                                        rabbitMirroring.MirroringSendKey(dataForms);
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

                                            try {
                                                dataForms.put(keys,objEl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataForms);
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
                                                dataForms.put(keys,objEl);
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataForms);
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
                                                getCodeType = dataSpin.getCode();
                                                String results = dataSpin.getName();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataForms.put(keys,objEl);
                                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                        valSpinProv.put(nameDataEl,idData);
                                                    } else {
                                                        valSpin.put(nameDataEl, idData);
                                                    }
                                                    if (isSessionZoom) {
                                                        rabbitMirroring.MirroringSendKey(dataForms);
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
                                                dataForms.put(keys,objEl);
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataForms);
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
                                                dataForms.put(keys,objEl);
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataForms);
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
                                            if (nameDataEl.toLowerCase().indexOf("gambar") > 0 || nameDataEl.toLowerCase().indexOf("image") > 0 || nameDataEl.toLowerCase().indexOf("tangan") > 0) {
                                                tvSavedImg = (TextView) ll.getChildAt(1);
                                                ll2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        REQUESTCODE_GALLERY = 201;
                                                        keyUpload = nameDataEl;
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
                        String nameOpr = dataObj.getString("name");
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        if (nameOpr.equals("GetList")) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = dataArr.getJSONObject(i).getInt("id");
                                String idJenis = dataArr.getJSONObject(i).getString("idJenis");
                                JSONObject jenisObj = dataArr.getJSONObject(i).getJSONObject("jenis");
                                String labelIdn = jenisObj.getString("labelIdn");
                                String labelEng = jenisObj.getString("labelEng");
                                dataDropDown.add(new FormSpin(idData, idJenis, labelIdn, labelEng));
                            }
                            flagStuckSpin = true;
                        } else {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = dataArr.getJSONObject(i).getInt("id");
                                String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                                String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                                dataDropDown.add(new FormSpin(idData, labelIdn, labelIdn, labelEng));
                                if (i == 0) {
                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                        valSpinProv.put(nameDataEl,idData);
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("CEK","onActivityResult : "+resultCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                Log.e("CEK","RETURN CAMERA");
                sessions.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);

            } else if (requestCode == REQUESTCODE_GALLERY) {
                Log.e("CEK","RESULT GAMBAR");
                sessions.saveFlagUpDoc(true);
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = mContext.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);

                FilePaths = picturePath;

                File files = new File(picturePath);
                String fileName = files.getName().toString();
                Log.e("CEK","RESULT picturePath : "+picturePath);
                Log.e("CEK","RESULT files.getName : "+fileName);
                tvSavedImg.setText("filename : "+fileName);
                c.close();
                prosesOptimalImage(picturePath);
            } else if (requestCode == REQUESTCODE_FILE) {
                Log.e("CEK","RESULT FILE");
                Uri uri = data.getData();
                if (uri != null) {
                    FilePaths = uri.getPath();
                    Cursor c = mContext.getContentResolver().query(uri,null, null, null, null);
                    c.moveToFirst();
                    String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    tvSavedFile.setText("filename : "+fileName);
                    Log.e("CEK","RESULT fileName : "+fileName);
                    c.close();
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getContentResolver().openInputStream(uri)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            sessions.saveFlagUpDoc(true);
        }
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
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
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
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
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
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Log.e("CEK","processValidateOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processValidateOTP : "+dataS);
                    rabbitMirroring.MirroringSendEndpoint(130);
                    getFragmentPage(new frag_service_resi());
                } else {
                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_failed));
                    textTitleOTP.setText(R.string.titleWrongOTP);
                    otp.setText("");
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

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        swipe.setVisibility(View.GONE);

        String noHandphone = "089783434XXX";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"XXX");
        }

        String contentText = textTitleOTP.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textTitleOTP.setText(contentText);

        tvTitleService.setText("One Time Password");

        otp.setAnimationEnable(true);
        otp.setPasswordHidden(true);
        otp.addTextChangedListener(new TextWatcher() {
            /*private boolean backSpaceOTP;
            private int lasLenOTP;*/

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //lasLenOTP = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*String wordOTP = s.toString();
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
                }*/
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
                /*backSpaceOTP = lasLenOTP > s.length();
                Log.e("CEK", "backSpaceOTP : " + backSpaceOTP);
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
                }*/
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
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
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

    private void resendOTP() {
        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }
        processSendOTP();
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

    private void prosesOptimalImage(String picturePath) {
        File mediaFile = new File(picturePath);
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));
        Log.d("CEK", "file_size : "+file_size);

        int perDiff = 1;
        if (file_size > 3072) {
            perDiff = 8;
        } else if (file_size > 2048) {
            perDiff = 6;
        } else if (file_size > 1024) {
            perDiff = 4;
        } else if (file_size > 550) {
            perDiff = 2;
        }

        if (perDiff == 1) {
            imgtoBase64(thumbnail);
        } else {
            getResizedBitmap(thumbnail, (thumbnail.getWidth() / perDiff), (thumbnail.getHeight() / perDiff));
        }
    }

    private void getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        imgtoBase64(resizedBitmap);
    }

    private void imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        imageBytes = baos.toByteArray();
        imgBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        if (isSessionZoom && !imgBase64.isEmpty()) {
            try {
                objEl.put(keyUpload, imgBase64);
                dataForms.put(keys,objEl);
                RabbitMirroring.MirroringSendKey(dataForms);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
}