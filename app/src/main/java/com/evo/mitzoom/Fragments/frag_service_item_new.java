package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Environment;
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
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterFile;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.HideSoftKeyboard;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.FileModel;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_item_new extends Fragment {
    private final int REQUESTCODE_CAPTURE = 1;
    private final int REQUESTCODE_FILE = 202;
    private final int REQUESTCODE_GALLERY = 2;

    private Context mContext;
    private SessionManager sessions;
    private RabbitMirroring rabbitMirroring;

    private TextView tvTitleService;
    private SwipeRefreshLayout swipe;
    private ImageView btnBack;
    private LinearLayout llFormBuild;
    private Button btnProses;
    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private ImageView viewImage = null;
    private LinearLayout llFileGallery = null;
    private ImageView imgBin = null;
    private LinearLayout chooseImage = null;

    private String noPengaduan;
    private int formCode;
    private int form_id = 0;
    private JSONArray idElement;
    private int lasLenChar;
    private boolean backSpaceChar;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String keys = "";
    private JSONObject dataForms = null;
    private String idDips;
    private boolean isSessionZoom;
    private String imgBase64;
    private byte[] imageBytes = new byte[0];
    private BroadcastReceiver smsReceiver = null;
    private PinView otp;
    private String namaLengkap = "";
    private String email = "";
    private String no_handphone = "";
    private String tempatLahir = "";
    private String tanggalLahir = "";
    private String namaIbu = "";
    private String numberOTP = "";
    private JSONObject dataNasabahObj;
    JSONObject valSpinProv = new JSONObject();
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinLabel = new JSONObject();
    JSONObject valSpinAutoComplete = new JSONObject();

    JSONObject objEl = new JSONObject();
    private boolean flagStuckSpin = false;
    private RecyclerView rv_item_file = null;
    private ArrayList dataFiles = new ArrayList<>();
    private ArrayList dataFilesMedia = new ArrayList<>();
    private String keyUpFile = "";
    private String keyUpImage = "";
    private String id_Perihal = "";
    private String FilePaths = "";
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private String transactionId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        formCode = sessions.getFormCode();

        String dataNasabah = sessions.getNasabah();
        ConnectionRabbitHttp.init(mContext);
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                if (dataNasabahObj.has("namaLengkap")) {
                    namaLengkap = dataNasabahObj.getString("namaLengkap");
                }
                if (dataNasabahObj.has("email")) {
                    email = dataNasabahObj.getString("email");
                    if (email == "null") {
                        email = "";
                    }
                }
                if (dataNasabahObj.has("tempatLahir")) {
                    tempatLahir = dataNasabahObj.getString("tempatLahir");
                    if (tempatLahir == "null") {
                        tempatLahir = "";
                    }
                }
                if (dataNasabahObj.has("tanggalLahir")) {
                    tanggalLahir = dataNasabahObj.getString("tanggalLahir");
                    if (tanggalLahir == "null") {
                        tanggalLahir = "";
                    }
                }
                if (dataNasabahObj.has("namaIbu")) {
                    namaIbu = dataNasabahObj.getString("namaIbu");
                    if (namaIbu == "null") {
                        namaIbu = "";
                    }
                }
                /*if (dataNasabahObj.has("alamat")) {
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

        boolean flagDoc = sessions.getFlagUpDoc();
        int valMedia = sessions.getMedia();
        if (!flagDoc && valMedia == 1) {
            sessions.saveFlagUpDoc(true);
        }

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
        View views = inflater.inflate(R.layout.fragment_dynamic_layout, container, false);

        btnBack = views.findViewById(R.id.btnBack);
        tvTitleService = views.findViewById(R.id.tvTitleService);
        swipe = views.findViewById(R.id.swipe);

        llFormBuild = views.findViewById(R.id.llFormBuild);

        scrollOTP = views.findViewById(R.id.scrollOTP);
        inclOTP = views.findViewById(R.id.inclOTP);
        imgDialog = views.findViewById(R.id.imgDialog);
        textTitleOTP = views.findViewById(R.id.textIBMB);
        btnVerifikasi = views.findViewById(R.id.btnVerifikasi);
        TimerOTP = views.findViewById(R.id.timer_otp);
        Resend_Otp = views.findViewById(R.id.btn_resend_otp);
        otp = views.findViewById(R.id.otp);

        btnProses = views.findViewById(R.id.btnProses);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String headlines = "";
        if (formCode == 359) {
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
                //RabbitMirroring.MirroringSendEndpoint(15);
                ConnectionRabbitHttp.mirroringEndpoint(15);
                getFragmentPage(new frag_service_new());
            }
        });

        processGetForm(form_id);

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* hide keyboard */
                HideSoftKeyboard.hideSoftKeyboard(getActivity());
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
                                    final boolean[] checkEmpty2 = {false};
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                            RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                            } else if (requiredDataEl){
                                                Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                            CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                            chk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    boolean isChk = chk.isChecked();
                                                    checkEmpty2[0] = !isChk;
                                                }
                                            });
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                            RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                            if (rl.getChildAt(0) instanceof Spinner) {
                                                if (objEl.getString(nameDataEl).toLowerCase().contains("pilih")) {
                                                    Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                break;
                                            }
                                        } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                            String results = autoText.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                                break;
                                            }
                                        }
                                    }
                                    checkEmpty = checkEmpty2[0];
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
                        if (!keyUpImage.isEmpty()) {
                            if (FilePaths.isEmpty()) {
                                Toast.makeText(mContext, getString(R.string.alert_upimage), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (!keyUpFile.isEmpty()) {
                            if (dataFiles.size() == 0) {
                                Toast.makeText(mContext, getString(R.string.alert_upfile), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
                        JSONObject objAPI = JSONReqAPI();
                        processSendFormCompaint(objAPI);
                    }
                }
            }
        });
    }

    private JSONObject JSONReqAPI() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(new Date());

        JSONObject objAPI = new JSONObject();
        try {
            JSONObject objData = new JSONObject(objEl.toString());
            String keyTgl = "tanggalpengaduan";
            String keyUpTtd = "tandatangan";
            List<String> keyUpFile = new ArrayList<String>();
            int loop = 0;
            for(Iterator<String> iter = objData.keys(); iter.hasNext();) {
                String key = iter.next();
                String valKurung = "";
                int indx = key.indexOf("(");
                if (indx >= 0) {
                    valKurung = key.substring(indx);
                }

                if ((key.contains("tanggal") || key.contains("tgl")) && (key.contains("pengaduan"+valKurung) || key.contains("komplain"+valKurung))) {
                    keyTgl = key;
                } else if (key.contains("file") && key.contains("pendukung"+valKurung)) {
                    keyUpFile.add(loop,key);
                    loop++;
                } else if (key.contains("tangan")) {
                    keyUpTtd = key;
                }
            }

            objData.remove(keyTgl);
            objData.remove(keyUpTtd);
            for (int op = 0; op < keyUpFile.size(); op++) {
                objData.remove(keyUpFile.get(op));
            }

            if (objData.has("pernyataan")) {
                objData.remove("pernyataan");
            }
            if (objData.has("idDips")) {
                objData.remove("idDips");
            }

            objAPI.put("idPerihal",id_Perihal);
            objAPI.put("tanggal",timeStamp);
            objAPI.put("idDips",idDips);
            objAPI.put("data",objData);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return objAPI;
    }

    private void processGetForm(int formId) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                if (response.isSuccessful()) {
                    llFormBuild.setVisibility(View.VISIBLE);
                    btnProses.setVisibility(View.VISIBLE);
                    String dataS = response.body().toString();
                    llFormBuild.removeAllViewsInLayout();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = MyParserFormBuilder.getForm();
                        processValidationActionForm();
                        objEl.put("nama", namaLengkap);
                        objEl.put("alamatemail", email);
                        objEl.put("nomorhandphone", no_handphone);
                        objEl.put("tempatlahir", tempatLahir);
                        objEl.put("tanggallahir", tanggalLahir);
                        objEl.put("namaibukandung", namaIbu);
                        dataForms.put(keys,objEl);
                        ConnectionRabbitHttp.mirroringKey(dataForms);
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
                                    objEl.put(nameDataEl, "");
                                    ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {

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
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataForms.put(keys,objEl);
                                                if (isSessionZoom) {
                                                    //RabbitMirroring.MirroringSendKey(dataForms);
                                                    ConnectionRabbitHttp.mirroringKey(dataForms);
                                                }
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

                                    if (nameDataEl.contains("nama") && nameDataEl.contains("ibu")) {

                                    }
                                    else if (nameDataEl.contains("nama")) {
                                        objEl.put(nameDataEl, namaLengkap);
                                        dataForms.put(keys,objEl);
                                        ed.setText(namaLengkap);
                                    } else if (nameDataEl.contains("email")) {
                                        objEl.put(nameDataEl, email);
                                        dataForms.put(keys,objEl);
                                        ed.setText(email);
                                    } else if ((nameDataEl.contains("no") || nameDataEl.contains("nomor")) && nameDataEl.contains("telepon"+valKurung)) {
                                        objEl.put(nameDataEl, no_handphone);
                                        dataForms.put(keys,objEl);
                                        ed.setText(no_handphone);
                                    } else if (nameDataEl.contains("tanggal") && (nameDataEl.contains("pengaduan") || nameDataEl.contains("komplain") )) {
                                        String dates = ed.getText().toString();
                                        if (!dates.isEmpty()) {
                                            objEl.put(nameDataEl, dates);
                                            dataForms.put(keys,objEl);
                                        }
                                    }
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    objEl.put(nameDataEl, "");

                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataForms.put(keys,objEl);
                                                    if (isSessionZoom) {
                                                        //RabbitMirroring.MirroringSendKey(dataForms);
                                                        ConnectionRabbitHttp.mirroringKey(dataForms);
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
                                                //RabbitMirroring.MirroringSendKey(dataForms);
                                                ConnectionRabbitHttp.mirroringKey(dataForms);
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
                                                    //RabbitMirroring.MirroringSendKey(dataForms);
                                                    ConnectionRabbitHttp.mirroringKey(dataForms);
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
                                        } else if (sessions.getRekNasabah() != null) {
                                            if (nameDataEl.contains("nomor") && nameDataEl.contains("rekening")) {
                                                String getListRekNasabah = sessions.getRekNasabah();
                                                JSONArray listRekNasabah = new JSONArray(getListRekNasabah);
                                                ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                                                for (int ij = 0; ij < listRekNasabah.length(); ij++) {
                                                    String accountNo = listRekNasabah.getJSONObject(ij).getString("accountNo");
                                                    String accountName = listRekNasabah.getJSONObject(ij).getString("accountName");
                                                    String labelGab = accountNo;
                                                    if (listRekNasabah.getJSONObject(ij).has("prodName")) {
                                                        String prodName = listRekNasabah.getJSONObject(i).getString("prodName").replace("R/K","").trim();
                                                        labelGab = prodName + "\n" + accountNo + " - " + accountName;
                                                    }
                                                    dataDropDown.add(new FormSpin(ij, accountNo, labelGab, labelGab));
                                                }
                                                ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.dropdown_multiline, dataDropDown);
                                                spin.setAdapter(adapter2);
                                            }
                                        }

                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                int idData = dataSpin.getId();
                                                String getCodeType = dataSpin.getCode();
                                                String results = dataSpin.getName();
                                                try {
                                                    if (nameDataEl.contains("nomor") && nameDataEl.contains("rekening")) {
                                                        objEl.put(nameDataEl, getCodeType);
                                                    } else {
                                                        objEl.put(nameDataEl, results);
                                                    }
                                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                        String newNameDataEl = nameDataEl;
                                                        if (nameDataEl.contains("(")) {
                                                            int indxProv = nameDataEl.indexOf("(");
                                                            newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                                        }
                                                        valSpinProv.put(newNameDataEl,idData);
                                                    } else {
                                                        valSpin.put(nameDataEl, idData);
                                                        if (nameDataEl.contains("jenis") && (nameDataEl.contains("pengaduan") || nameDataEl.contains("komplain"))) {
                                                            String resultsEng = dataSpin.getNameEng();
                                                            valSpinLabel.put(nameDataEl, resultsEng);
                                                        } else if (nameDataEl.contains("hal") && (nameDataEl.contains("pengaduan") || nameDataEl.contains("komplain"))) {
                                                            id_Perihal = getCodeType;
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                if (isSessionZoom) {
                                                    //RabbitMirroring.MirroringSendKey(dataForms);
                                                    ConnectionRabbitHttp.mirroringKey(dataForms);
                                                }
                                                if (flagStuckSpin) {
                                                    processGetSpinChild(nameDataEl);
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
                                                    //RabbitMirroring.MirroringSendKey(dataForms);
                                                    ConnectionRabbitHttp.mirroringKey(dataForms);
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
                                                    //RabbitMirroring.MirroringSendKey(dataForms);
                                                    ConnectionRabbitHttp.mirroringKey(dataForms);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    if (ll.getChildCount() > 1) {
                                        if (ll.getChildAt(1) instanceof TextView) {
                                            keyUpFile = nameDataEl;
                                            if (ll.getChildAt(0) instanceof LinearLayout) {
                                                LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);

                                                TextView tvll = (TextView) ll2.getChildAt(1);
                                                String txt = tvll.getText().toString();
                                                tvSavedFile = (TextView) ll.getChildAt(1);
                                                ll2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (dataFiles.size() >= 3) {
                                                            Toast.makeText(mContext,R.string.max_upfile, Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        sessions.saveMedia(2);
                                                        Intent intent = new Intent();
                                                        intent.setType("*/*");
                                                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                        String[] mimetypes = { "application/pdf", "application/doc", "text/*", "image/jpeg", "image/png" };
                                                /*String[] mimeTypes =
                                                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                                                "text/plain",
                                                                "application/pdf",
                                                                "application/zip", "application/vnd.android.package-archive"};*/

                                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                        startActivityForResult(intent, REQUESTCODE_FILE);
                                                    }
                                                });
                                            }
                                        } else if (ll.getChildAt(1) instanceof RecyclerView) {
                                            keyUpFile = nameDataEl;
                                            LinearLayout llUploadFile = ll.findViewById(R.id.llUploadFile);
                                            rv_item_file = ll.findViewById(R.id.rv_item_file);
                                            rv_item_file.setHasFixedSize(false);

                                            llUploadFile.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (dataFiles.size() >= 3) {
                                                        Toast.makeText(mContext,R.string.max_upfile, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    sessions.saveMedia(2);
                                                    Intent intent = new Intent();
                                                    intent.setType("*/*");
                                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                    String[] mimetypes = { "application/pdf", "application/doc", "text/*", "image/jpeg", "image/png" };
                                                    /*String[] mimeTypes =
                                                            {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                                                    "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                                                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                                                    "text/plain",
                                                                    "application/pdf",
                                                                    "application/zip", "application/vnd.android.package-archive"};*/

                                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                    startActivityForResult(intent, REQUESTCODE_FILE);
                                                }
                                            });
                                        } else {
                                            keyUpImage = nameDataEl;
                                            ImageView btnCamera = ll.findViewById(R.id.choose_camera);
                                            chooseImage = ll.findViewById(R.id.Choose_Image);
                                            LinearLayout btnGallery = ll.findViewById(R.id.choose_gallery);
                                            llFileGallery = ll.findViewById(R.id.llFileGallery);
                                            imgBin = ll.findViewById(R.id.imgBin);
                                            tvSavedImg = ll.findViewById(R.id.tvNameGallery);
                                            viewImage = ll.findViewById(R.id.Imageview);

                                            imgBin.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    viewImage.setVisibility(View.GONE);
                                                    chooseImage.setVisibility(View.VISIBLE);
                                                    llFileGallery.setVisibility(View.GONE);
                                                }
                                            });
                                            btnCamera.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    /*if (!requestPermission()) {
                                                        Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }*/
                                                    sessions.saveMedia(1);
                                                    Intent intent = new Intent(mContext, DipsCameraActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivityForResult(intent, REQUESTCODE_CAPTURE);
                                                }
                                            });
                                            btnGallery.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    /*if (!requestPermission()) {
                                                        Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }*/
                                                    sessions.saveMedia(2);
                                                    chooseFromSD();
                                                }
                                            });
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

    private void processSendFormCompaint(JSONObject objAPI) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objAPI.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.formComplaint(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
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
                        noPengaduan = dataObj.getJSONObject("data").getString("noPengaduan");
                        sessions.saveNoComplaint(noPengaduan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    processSendComplainMedia();
                } else {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
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

    private void processSendComplainMedia() {
        /*File file = new File(FilePaths);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);*/

        RequestBody requestnoComplaint = RequestBody.create(MediaType.parse("text/plain"), noPengaduan);

        int lenMedia = dataFilesMedia.size();

        MultipartBody multipartBody = null;
        if (lenMedia == 1) {
            Uri uri = (Uri) dataFilesMedia.get(0);
            File fileMedia = null;
            try {
                fileMedia = fromUri(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String extension = fileMedia.getPath().substring(fileMedia.getPath().lastIndexOf(".") );
            String mimeTypeMap =MimeTypeMap.getFileExtensionFromUrl(extension);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
            RequestBody requestFileMedia = RequestBody.create(MediaType.parse(type),fileMedia);

            multipartBody = new MultipartBody.Builder()
                    //.addPart(MultipartBody.Part.createFormData("ktp", file.getName(), requestFile))
                    .addPart(MultipartBody.Part.createFormData("noPengaduan", null, requestnoComplaint))
                    .addPart(MultipartBody.Part.createFormData("buktiPendukung1", fileMedia.getName(), requestFileMedia))
                    .build();
        } else if (lenMedia == 2) {
            Uri uri = (Uri) dataFilesMedia.get(0);
            File fileMedia = null;
            try {
                fileMedia = fromUri(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String extension = fileMedia.getPath().substring(fileMedia.getPath().lastIndexOf(".") );
            String mimeTypeMap =MimeTypeMap.getFileExtensionFromUrl(extension);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
            RequestBody requestFileMedia = RequestBody.create(MediaType.parse(type),fileMedia);

            Uri uri2 = (Uri) dataFilesMedia.get(1);
            File fileMedia2 = null;
            try {
                fileMedia2 = fromUri(uri2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String extension2 = fileMedia2.getPath().substring(fileMedia2.getPath().lastIndexOf(".") );
            String mimeTypeMap2 =MimeTypeMap.getFileExtensionFromUrl(extension2);
            String type2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap2);
            RequestBody requestFileMedia2 = RequestBody.create(MediaType.parse(type2),fileMedia2);

            multipartBody = new MultipartBody.Builder()
                    //.addPart(MultipartBody.Part.createFormData("ktp", file.getName(), requestFile))
                    .addPart(MultipartBody.Part.createFormData("noPengaduan", null, requestnoComplaint))
                    .addPart(MultipartBody.Part.createFormData("buktiPendukung1", fileMedia.getName(), requestFileMedia))
                    .addPart(MultipartBody.Part.createFormData("buktiPendukung2", fileMedia2.getName(), requestFileMedia2))
                    .build();
        } else if (lenMedia == 3) {
            Uri uri = (Uri) dataFilesMedia.get(0);
            File fileMedia = null;
            try {
                fileMedia = fromUri(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String extension = fileMedia.getPath().substring(fileMedia.getPath().lastIndexOf(".") );
            String mimeTypeMap =MimeTypeMap.getFileExtensionFromUrl(extension);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
            RequestBody requestFileMedia = RequestBody.create(MediaType.parse(type),fileMedia);

            Uri uri2 = (Uri) dataFilesMedia.get(1);
            File fileMedia2 = null;
            try {
                fileMedia2 = fromUri(uri2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String extension2 = fileMedia2.getPath().substring(fileMedia2.getPath().lastIndexOf(".") );
            String mimeTypeMap2 =MimeTypeMap.getFileExtensionFromUrl(extension2);
            String type2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap2);
            RequestBody requestFileMedia2 = RequestBody.create(MediaType.parse(type2),fileMedia2);

            Uri uri3 = (Uri) dataFilesMedia.get(2);
            File fileMedia3 = null;
            try {
                fileMedia3 = fromUri(uri3);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String extension3 = fileMedia3.getPath().substring(fileMedia3.getPath().lastIndexOf(".") );
            String mimeTypeMap3 =MimeTypeMap.getFileExtensionFromUrl(extension3);
            String type3 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap3);
            RequestBody requestFileMedia3 = RequestBody.create(MediaType.parse(type3),fileMedia3);

            multipartBody = new MultipartBody.Builder()
                    //.addPart(MultipartBody.Part.createFormData("ktp", file.getName(), requestFile))
                    .addPart(MultipartBody.Part.createFormData("noPengaduan", null, requestnoComplaint))
                    .addPart(MultipartBody.Part.createFormData("buktiPendukung1", fileMedia.getName(), requestFileMedia))
                    .addPart(MultipartBody.Part.createFormData("buktiPendukung2", fileMedia2.getName(), requestFileMedia2))
                    .addPart(MultipartBody.Part.createFormData("buktiPendukung3", fileMedia3.getName(), requestFileMedia3))
                    .build();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        String contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.formComplaintMedia(contentType,authAccess,exchangeToken,multipartBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    JSONObject dataMirr = null;
                    try {
                        dataMirr = new JSONObject(objEl.toString());
                        dataMirr.put("noPengaduan",noPengaduan);
                        dataForms.put(keys,dataMirr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //RabbitMirroring.MirroringSendKey(dataForms);
                    ConnectionRabbitHttp.mirroringKey(dataForms);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //RabbitMirroring.MirroringSendEndpoint(130);
                                    ConnectionRabbitHttp.mirroringEndpoint(130);
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("newComplain",true);
                                    Fragment fragment = new frag_service_resi();
                                    fragment.setArguments(bundle);
                                    getFragmentPage(fragment);
                                }
                            });
                        }
                    },1000);

                    //processSendOTP();
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

    private File fromUri(Uri uri) throws IOException {
        InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
        Cursor c = mContext.getContentResolver().query(uri,null, null, null, null);
        c.moveToFirst();
        @SuppressLint("Range") String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }

        return tempFile;
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
                        //RabbitMirroring.MirroringSendEndpoint(11);
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

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());
        Server.getAPIService().ValidateOTP(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //RabbitMirroring.MirroringSendEndpoint(130);
                    ConnectionRabbitHttp.mirroringEndpoint(130);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("newComplain",true);
                    Fragment fragment = new frag_service_resi();
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                } else {
                    running = true;
                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_failed));
                    textTitleOTP.setText(R.string.titleWrongOTP);
                    otp.setText("");
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        swipe.setVisibility(View.GONE);

        String noHandphone = "089783434***";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"***");
        }

        String contentText = textTitleOTP.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textTitleOTP.setText(contentText);

        tvTitleService.setText("One Time Password");

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
                        if (isSessionZoom) {
                            BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                            BaseMeetingActivity.tvLoading.setVisibility(View.VISIBLE);
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                            DipsSwafoto.tvLoading.setVisibility(View.VISIBLE);
                            DipsSwafoto.showProgress(true);
                        }
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

    private void resendOTP() {
        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }
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

    private void processGetDynamicURL(Spinner spin, String urlPath, String nameDataEl) {
        flagStuckSpin = false;
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrl(urlPath,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        String nameOpr = dataObj.getString("name");
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        if (nameOpr.equals("GetList")) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = 0;
                                String idJenis = "0";
                                if (dataArr.getJSONObject(i).has("id")) {
                                    idData = dataArr.getJSONObject(i).getInt("id");
                                    idJenis = dataArr.getJSONObject(i).getString("idJenis");
                                }
                                String jenisLabel = dataArr.getJSONObject(i).getString("jenis");
                                String labelIdn = jenisLabel;
                                String labelEng = jenisLabel;
                                if (jenisLabel.contains("{")) {
                                    JSONObject jenisObj = dataArr.getJSONObject(i).getJSONObject("jenis");
                                    labelIdn = jenisObj.getString("labelIdn");
                                    labelEng = jenisObj.getString("labelEng");
                                    if (sessions.getLANG().equals("en")) {
                                        labelIdn = labelEng;
                                    }
                                }
                                dataDropDown.add(new FormSpin(idData, idJenis, labelIdn, labelEng));
                            }
                            flagStuckSpin = true;
                        } else if (nameOpr.equals("GetByJenis")) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = 0;
                                String idPerihal = "0";
                                if (dataArr.getJSONObject(i).has("id")) {
                                    idData = dataArr.getJSONObject(i).getInt("id");
                                    idPerihal = dataArr.getJSONObject(i).getString("idPerihal");
                                }

                                String perihal = dataArr.getJSONObject(i).getString("perihal");
                                dataDropDown.add(new FormSpin(idData, idPerihal, perihal, perihal));
                            }
                            flagStuckSpin = true;
                        } else {
                            for (int i = 0; i < dataArr.length(); i++) {
                                int idData = 0;
                                String idSData = "";

                                if (dataArr.getJSONObject(i).has("ids")) {
                                    idSData = dataArr.getJSONObject(i).getString("ids").trim();
                                    idData = Integer.parseInt(idSData);
                                } else if (dataArr.getJSONObject(i).has("id")) {
                                    idData = dataArr.getJSONObject(i).getInt("id");

                                }
                                String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                                String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                                if (sessions.getLANG().equals("en")) {
                                    labelIdn = labelEng;
                                }
                                dataDropDown.add(new FormSpin(idData, labelIdn, labelIdn, labelEng));
                                if (i == 0) {
                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                        String newNameDataEl = nameDataEl;
                                        if (nameDataEl.contains("(")) {
                                            int indxProv = nameDataEl.indexOf("(");
                                            newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                        }
                                        valSpinProv.put(newNameDataEl,idData);
                                    } else {
                                        valSpin.put(nameDataEl, idData);
                                        valSpinLabel.put(nameDataEl,labelEng);
                                    }
                                    processGetSpinChild(nameDataEl);
                                    if ((nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                        flagStuckSpin = true;
                                    }
                                }
                            }
                        }
                        ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
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
                                if (!urlPath.isEmpty()) {
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    if (idProv != 0) {
                                        String urlNew = urlPath.replace(":id_provinsi", idSpin);

                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            } else if ((nameDataEl.contains("kabupaten") || nameDataEl.contains("kota")) && getnameDataEl.contains("kecamatan")) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    int idKabKot = 0;
                                    if (valSpinProv.has("kabupaten")) {
                                        idKabKot = valSpinProv.getInt("kabupaten");
                                    } else if (valSpinProv.has("district")) {
                                        idKabKot = valSpinProv.getInt("district");
                                    } else if (valSpinProv.has("kota")) {
                                        idKabKot = valSpinProv.getInt("kota");
                                    } else if (valSpinProv.has("city")) {
                                        idKabKot = valSpinProv.getInt("city");
                                    } else if (valSpinProv.has("kabupatenkota")) {
                                        idKabKot = valSpinProv.getInt("kabupatenkota");
                                    } else if (valSpinProv.has("kotakabupaten")) {
                                        idKabKot = valSpinProv.getInt("kotakabupaten");
                                    } else if (valSpinProv.has("districtcity")) {
                                        idKabKot = valSpinProv.getInt("districtcity");
                                    } else if (valSpinProv.has("citydistrict")) {
                                        idKabKot = valSpinProv.getInt("citydistrict");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    String idSpin2 = String.valueOf(idKabKot);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin).replace(":id_kabupaten",idSpin2);
                                    if (idKabKot != 0) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            } else if (nameDataEl.contains("kecamatan") && (getnameDataEl.contains("kelurahan") || getnameDataEl.contains("desa"))) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    int idKabKot = 0;
                                    if (valSpinProv.has("kabupaten")) {
                                        idKabKot = valSpinProv.getInt("kabupaten");
                                    } else if (valSpinProv.has("district")) {
                                        idKabKot = valSpinProv.getInt("district");
                                    } else if (valSpinProv.has("kota")) {
                                        idKabKot = valSpinProv.getInt("kota");
                                    } else if (valSpinProv.has("city")) {
                                        idKabKot = valSpinProv.getInt("city");
                                    } else if (valSpinProv.has("kabupatenkota")) {
                                        idKabKot = valSpinProv.getInt("kabupatenkota");
                                    } else if (valSpinProv.has("kotakabupaten")) {
                                        idKabKot = valSpinProv.getInt("kotakabupaten");
                                    } else if (valSpinProv.has("districtcity")) {
                                        idKabKot = valSpinProv.getInt("districtcity");
                                    } else if (valSpinProv.has("citydistrict")) {
                                        idKabKot = valSpinProv.getInt("citydistrict");
                                    }
                                    int idKec = 0;
                                    if (valSpinProv.has("kecamatan")) {
                                        idKec = valSpinProv.getInt("kecamatan");
                                    } else if (valSpinProv.has("subdistrict")) {
                                        idKec = valSpinProv.getInt("subdistrict");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    String idSpin2 = String.valueOf(idKabKot);
                                    String idSpin3 = String.valueOf(idKec);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin).replace(":id_kabupaten",idSpin2).replace(":id_kecamatan",idSpin3);

                                    if (idKec != 0) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            } else if ((nameDataEl.contains("jenis") && (nameDataEl.contains("pengaduan") || nameDataEl.contains("komplain"))) &&
                                    (getnameDataEl.contains("hal") && (getnameDataEl.contains("pengaduan") || getnameDataEl.contains("komplain")))) {

                                int idJenis = 0;
                                idJenis = valSpin.getInt(nameDataEl);
                                if (idJenis != 0) {
                                    String typeComplaint = valSpinLabel.getString(nameDataEl);
                                    String urlNew = urlPath.replace(":jenisPengaduan", typeComplaint);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUESTCODE_CAPTURE) {
                sessions.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);

                if (llFileGallery != null) {
                    llFileGallery.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);

                    try {
                        File mediaFile = createTemporaryFile(resultCamera);
                        String fileName = mediaFile.getName();
                        FilePaths = mediaFile.getAbsolutePath();
                        tvSavedImg.setText(fileName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                imgtoBase64(bitmap);

            } else if (requestCode == REQUESTCODE_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = mContext.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);

                FilePaths = picturePath;

                File files = new File(picturePath);
                String fileName = files.getName();
                tvSavedImg.setText(fileName);
                c.close();
                prosesOptimalImage(picturePath);
                if (llFileGallery != null) {
                    llFileGallery.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                }
            } else if (requestCode == REQUESTCODE_FILE) {
                Uri uri = data.getData();
                if (dataFiles.size() > 3) {
                    Toast.makeText(mContext, R.string.max_upfile, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uri != null) {
                    dataFilesMedia = new ArrayList();
                    String paths = uri.getPath();
                    dataFilesMedia.add(uri);
                    Cursor c = mContext.getContentResolver().query(uri,null, null, null, null);
                    c.moveToFirst();
                    @SuppressLint("Range") String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    if (tvSavedFile != null) {
                        tvSavedFile.setText(fileName);
                        if (!keyUpFile.isEmpty()) {
                            try {
                                JSONArray fileArr = new JSONArray();
                                fileArr.put(fileName);
                                objEl.put(keyUpFile,fileArr);
                                dataForms.put(keys,objEl);
                                ConnectionRabbitHttp.mirroringKey(dataForms);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    if (rv_item_file != null) {
                        dataFiles = new ArrayList();
                        dataFiles.add(new FileModel("1", fileName, R.color.item_file_silver, ""));
                        rv_item_file.setVisibility(View.VISIBLE);
                        setRecyler();
                        if (!keyUpFile.isEmpty()) {
                            try {
                                JSONArray fileArr = new JSONArray();
                                fileArr.put(fileName);
                                objEl.put(keyUpFile,fileArr);
                                dataForms.put(keys,objEl);
                                ConnectionRabbitHttp.mirroringKey(dataForms);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    c.close();

                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getContentResolver().openInputStream(uri)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (data.getClipData().getItemCount() > 0) {
                    dataFiles = new ArrayList();
                    dataFilesMedia = new ArrayList();

                    JSONArray fileArr = new JSONArray();
                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uriFile = data.getClipData().getItemAt(i).getUri();
                        Cursor c = mContext.getContentResolver().query(uriFile,null, null, null, null);
                        c.moveToFirst();
                        @SuppressLint("Range") String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        if (rv_item_file != null) {
                            dataFilesMedia.add(uriFile);
                            String paths = uriFile.getPath();
                            dataFiles.add(new FileModel("1", fileName, R.color.item_file_silver, ""));
                        }
                        c.close();

                        if (!keyUpFile.isEmpty()) {
                            fileArr.put(fileName);
                        }
                    }

                    try {
                        objEl.put(keyUpFile,fileArr);
                        dataForms.put(keys,objEl);
                        ConnectionRabbitHttp.mirroringKey(dataForms);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if (rv_item_file != null) {
                        rv_item_file.setVisibility(View.VISIBLE);
                        setRecyler();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            sessions.saveFlagUpDoc(true);
        }
    }

    private void prosesOptimalImage(String picturePath) {
        File mediaFile = new File(picturePath);
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));

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
        if (viewImage != null) {
            viewImage.setImageBitmap(bitmap);
        }
        imageBytes = baos.toByteArray();
        imgBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        if (isSessionZoom && !imgBase64.isEmpty()) {
            try {
                objEl.put(keyUpImage, imgBase64);
                dataForms.put(keys,objEl);
                //RabbitMirroring.MirroringSendKey(dataForms);
                ConnectionRabbitHttp.mirroringKey(dataForms);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private File createTemporaryFile(byte[] byteImage) throws Exception {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.close();

        return mediaFile;
    }

    private void chooseFromSD() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    private void setRecyler(){
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_item_file.setLayoutManager(recylerViewLayoutManager);

        AdapterFile recyclerViewAdapter = new AdapterFile(getContext(), dataFiles);
        rv_item_file.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

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
