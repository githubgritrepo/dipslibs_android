package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private BroadcastReceiver smsReceiver = null;
    private PinView otp;
    private JSONObject objValCIF;
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
    private int lasLenChar;
    private boolean backSpaceChar;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String picturePath = "";
    private TextView tvTitleReady;
    private ImageView btn_back;
    private boolean flagStuckSpin = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        Log.e("CEK",mContext+" isSessionZoom : "+isSessionZoom);
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_ready_account, container, false);

        btn_back = (ImageView) views.findViewById(R.id.btn_back);
        tvTitleReady = (TextView) views.findViewById(R.id.tvTitleReady);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        btnProses = (Button) views.findViewById(R.id.btnProses);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);

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

        form_id = 19;
        String label = getString(R.string.info_first_data);
        label = label.replaceAll("\n"," ");
        tvTitleReady.setText(label);

        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
                if (form_id == 29 || form_id == 20 || form_id == 21 || form_id == 22) {
                    label = getString(R.string.data_account_open);
                    label = label.replaceAll("\n","");
                    tvTitleReady.setText(getString(R.string.data_account_open));
                } else if (form_id == 30) {
                    label = getString(R.string.facilities_services);
                    label = label.replaceAll("\n","");
                    tvTitleReady.setText(getString(R.string.facilities_services));
                }
            }
        }

        dataFormObj = new JSONObject();

        if (form_id == 19) {
            keysData = "datautama";
        } else if (form_id == 20) {
            keysData = "giro";
        } else if (form_id == 22) {
            keysData = "tabunganberjangka";
        } else if (form_id == 30) {
            keysData = "fasilitaslayanan";
        }

        processGetForm(form_id);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Fragment fragment = new frag_cif_new();
                if (form_id == 29) {
                    bundle.putInt("form_id",19);
                    rabbitMirroring.MirroringSendEndpoint(150);
                } else if (form_id == 20) {
                    bundle.putInt("form_id",29);
                    rabbitMirroring.MirroringSendEndpoint(151);
                } else if (form_id == 21) {
                    bundle.putInt("form_id",20);
                    rabbitMirroring.MirroringSendEndpoint(152);
                } else if (form_id == 22) {
                    bundle.putInt("form_id",21);
                    rabbitMirroring.MirroringSendEndpoint(153);
                } else if (form_id == 30) {
                    bundle.putInt("form_id",22);
                    rabbitMirroring.MirroringSendEndpoint(154);
                } else {
                    fragment = new frag_open_account_product();
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
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            } else if (nameDataEl.contains("noponsel") && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
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
                                                    Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                            String results = autoText.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                        //processNext();
                    }
                }
            }
        });

    }

    private void processGetForm(int formId) {
        Log.e("CEK", this+" MASUK processGetForm formId : "+formId);
        Log.e("CEK", this+" MASUK formCode : "+formCode);
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                BaseMeetingActivity.showProgress(false);
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
                        JSONObject ObjCif = new JSONObject();
                        ObjCif.put(keysData,objEl);
                        dataFormObj.put("pembukaanakun",ObjCif);
                        Log.e("CEK","DATA FORM : "+dataFormObj.toString());
                        //rabbitMirroring.MirroringSendKey(dataFormObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
                BaseMeetingActivity.showProgress(false);
            }
        });
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
                            String urlPath = "";
                            if (idElement.getJSONObject(j).has("url")) {
                                urlPath = idElement.getJSONObject(j).getString("url");
                            }
                            if (idEl == idDataEl) {

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
                                            if (nameDataEl.equals("npwp")) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            Log.e("CEK",nameDataEl+" : "+charSequence);
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormObj.put(keysData,objEl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            /*if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataFormObj);
                                            }*/
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (nameDataEl.equals("npwp")) {
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
                                                    dataFormObj.put(keysData,objEl);
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

                                            try {
                                                dataFormObj.put(keysData,objEl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
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
                                                dataFormObj.put(keysData,objEl);
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
                                                    dataFormObj.put(keysData,objEl);
                                                    valSpin.put(nameDataEl,idData);
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
                                                dataFormObj.put(keysData,objEl);
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
                                                dataFormObj.put(keysData,objEl);
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
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = dataArr.getJSONObject(i).getInt("id");
                            String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                            String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                            dataDropDown.add(new FormSpin(idData,labelIdn,labelIdn,labelEng));
                            if (i == 0) {
                                valSpin.put(nameDataEl,idData);
                                processGetSpinChild(nameDataEl);
                                if ((nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                    flagStuckSpin = true;
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
                                    int idProv = valSpin.getInt("provinsi");
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
                                    int idProv = valSpin.getInt("provinsi");
                                    int idKabKot = 0;
                                    if (valSpin.has("kabupaten")) {
                                        idKabKot = valSpin.getInt("kabupaten");
                                    } else if (valSpin.has("kota")) {
                                        idKabKot = valSpin.getInt("kota");
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
                                    int idProv = valSpin.getInt("provinsi");
                                    int idKec = valSpin.getInt("kecamatan");
                                    int idKabKot = 0;
                                    if (valSpin.has("kabupaten")) {
                                        idKabKot = valSpin.getInt("kabupaten");
                                    } else if (valSpin.has("kota")) {
                                        idKabKot = valSpin.getInt("kota");
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