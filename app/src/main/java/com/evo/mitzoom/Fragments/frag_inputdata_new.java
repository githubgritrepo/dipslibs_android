package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterFile;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.FileModel;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_inputdata_new extends Fragment {

    private final int REQUESTCODE_CAPTURE = 1;
    private final int REQUESTCODE_FILE = 202;
    private Context mContext;
    private boolean isCust;
    public int seconds = 0;
    public boolean running = true;
    private SessionManager session;
    private SwipeRefreshLayout swipe;
    private LinearLayout llFormBuild;
    private Button btnNext;
    private LayoutInflater inflater;
    private View dialogView;
    private SweetAlertDialog sweetAlertDialogTNC;
    private String idDips;
    private final String Nama = "";
    private final String NIK = "";
    private JSONArray idElement = new JSONArray();
    private boolean isSwafoto = false;
    private TextView tvSavedImg = null;
    private TextView tvSavedFile = null;
    JSONObject objEl = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinLabel = new JSONObject();
    JSONObject valSpinAutoComplete = new JSONObject();
    private RabbitMirroring rabbitMirroring;
    private int lasLenChar;
    private boolean backSpaceChar;
    private String custName = "";
    private String imgBase64;
    private byte[] imageBytes;
    private boolean isSessionZoom;
    private final String keys = "";

    final String STATE_CUSTNAME = "custName";
    final String STATE_ELEMENTARRAY = "elementArray";
    final String STATE_ELEMENTObj = "elementObj";
    private boolean flagStuckSpin = false;
    private ImageView viewImage = null;
    private LinearLayout llFileGallery = null;
    private ImageView imgBin = null;
    private LinearLayout chooseImage = null;
    private RecyclerView rv_item_file = null;
    private ArrayList dataFiles = new ArrayList<>();
    private String keyUpFile = "";
    private String keyUpImage = "";
    private String id_Perihal = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        session = new SessionManager(mContext);
        isCust = session.getKEY_iSCust();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_form_builder, container, false);
        swipe = view.findViewById(R.id.swipe);
        llFormBuild = view.findViewById(R.id.llFormBuild);
        btnNext = view.findViewById(R.id.btnNext);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idDips = session.getKEY_IdDips();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                processGetForm();
            }
        });

        if (savedInstanceState == null) {
            Log.e("CEK","MASUK NULL SAVED INSTANACE");
            if (getArguments().containsKey("CUSTNAME")) {
                custName = getArguments().getString("CUSTNAME");
            }
            processGetForm();
        } else {
            Log.e("CEK","MASUK SAVED INSTANACE");
            custName = savedInstanceState.getString(STATE_CUSTNAME);
            String getIdElement = savedInstanceState.getString(STATE_ELEMENTARRAY);
            String getElementObj = savedInstanceState.getString(STATE_ELEMENTObj);

            try {
                idElement = new JSONArray(getIdElement);
                objEl = new JSONObject(getElementObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    String label = idElement.getJSONObject(j).getString("label");
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            Log.e("CEK", "MASUK EDITTEXT ke-" + i);
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, label+" "+getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            else if (results.isEmpty()){
                                                Toast.makeText(mContext, label+" "+getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            else if (nameDataEl.contains("email")) {
                                                boolean cekFlag = validationEmail(results);
                                                if (!cekFlag) {
                                                    checkEmpty = true;
                                                }
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        }
                                        else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                            RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            Log.e("CEK", "MASUK RadioGroup ke-" + i+" | selectedId : "+selectedId+" | requiredDataEl  : "+requiredDataEl);
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                Log.e("CEK","RadioGroup results : "+results+" | requiredDataEl : "+requiredDataEl);
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, nameDataEl+" "+getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            } else if (requiredDataEl) {
                                                Toast.makeText(mContext, nameDataEl+" "+getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            break;
                                        }
                                        else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                            Log.e("CEK", "MASUK CheckBox ke-" + i);
                                            CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                            boolean isChk = chk.isChecked();
                                            if (isChk) {
                                                objEl.put(nameDataEl, isChk);
                                            }
                                            break;
                                        }
                                        else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                            Log.e("CEK", "MASUK Spinner ke-" + i);
                                            Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                            if (spin.isSelected()) {
                                                String results = spin.getSelectedItem().toString();
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, nameDataEl+" "+getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
                                            break;
                                        }
                                        else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                            Log.e("CEK", "MASUK AutoCompleteTextView ke-" + i);
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                            String results = autoText.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl+" "+getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                                break;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        }
                                        /*else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                            Log.e("CEK", "MASUK LinearLayout ke-" + i);
                                            LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                            Log.e("CEK", "LinearLayout getChildCount : " + ll.getChildCount());
                                            if (ll.getChildCount() > 1) {
                                                if (ll.getChildAt(0) instanceof LinearLayout) {
                                                    LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
                                                    TextView tvll = (TextView) ll2.getChildAt(1);
                                                    String txt = tvll.getText().toString();
                                                    Log.e("CEK", "tvll : " + txt);
                                                    keys = nameDataEl;
                                                    objEl.put(nameDataEl, imageBytes);
                                                }
                                            }
                                        }*/
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

                    Log.e("CEL","REQUEST : "+objEl.toString());

                    if (flagNext) {
                        for (int j = 0; j < idElement.length(); j++) {
                            try {
                                String nameDataEl = idElement.getJSONObject(j).getString("name");
                                if (objEl.has(nameDataEl)) {
                                    String keyIndoEl = idElement.getJSONObject(j).getString("keyIndo");
                                    Object getObj = objEl.get(nameDataEl);
                                    objEl.put(keyIndoEl,getObj);
                                    /*if (session.getLANG().equals("en")) {
                                        objEl.remove(nameDataEl);
                                    }*/
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        boolean validate = true;
                        for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                            String key = iter.next();
                            String valKurung = "";
                            int indx = key.indexOf("(");
                            if (indx >= 0) {
                                valKurung = key.substring(indx);
                            }
                            if (key.contains("nik")) {
                                if (objEl.has("nik"+valKurung)) {
                                    String getNIK = "";
                                    try {
                                        getNIK = objEl.getString("nik");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!getNIK.isEmpty() && getNIK.length() < 16) {
                                        validate = false;
                                    }
                                    break;
                                }
                            }
                        }

                        Log.e("CEL","REQUEST SEND : "+objEl.toString());

                        /*String timeStamp = new SimpleDateFormat("yyyy-MM-dd",
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

                            objAPI.put("idJenis",id_Perihal);
                            objAPI.put("tanggal",timeStamp);
                            objAPI.put("idDips",idDips);
                            objAPI.put("data",objData);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        Log.e("CEK","PAYLOAD API KOMPLAIN : "+objAPI.toString());*/

                        if (validate) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                                keyboard(mContext);
                            } else {
                                keyboard(mContext);
                                DipsSwafoto.showProgress(true);
                            }
                            CekDataByNIK(objEl);
                        } else {
                            Log.e("CEK","ELSE : "+validate);
                            Toast.makeText(mContext, R.string.doesn_match, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void keyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()){
            ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isSessionZoom) {
            RabbitMirroring.closeThreadConnection();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CUSTNAME,custName);
        outState.putString(STATE_ELEMENTARRAY,idElement.toString());
        outState.putString(STATE_ELEMENTObj,objEl.toString());
    }

    private JSONObject dataMirroring(JSONObject dataObj) {
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

    private boolean validationEmail(String data) {
        boolean flag = Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(data).matches();

        if (!flag) {
            Toast.makeText(mContext, getString(R.string.invalidate_email), Toast.LENGTH_SHORT).show();
        }
        return flag;
        /*String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        boolean flag = false;
        // onClick of button perform this simplest code.
        if (data.matches(emailPattern))
        {
            flag = true;
        }
        else
        {
            Toast.makeText(mContext, getString(R.string.invalidate_email), Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return flag;*/
    }

    private void processGetForm() {
        Log.e("CEK","MASUK processGetForm");
        Server.getAPIWAITING_PRODUCT().getFormBuilder(7).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","response processGetForm : "+response.code());
                if (response.isSuccessful()) {
                    btnNext.setVisibility(View.VISIBLE);
                    String dataS = response.body().toString();
                    Log.e("CEK","response dataS : "+dataS);
                    llFormBuild.removeAllViewsInLayout();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        Log.e("CEK","dataForm : "+dataForm);
                        MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = MyParserFormBuilder.getForm();
                        Log.e("CEK","dataElement : "+ idElement);
                        processValidationActionForm();
                        Log.e("CEK","DATA FORM : "+objEl.toString());
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
        Log.e("CEK","MASUK processValidationActionForm");
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
                                    Log.e("CEK","MASUK EditText : "+nameDataEl);
                                    if (!urlPath.isEmpty()) {
                                        AutoCompleteTextView AutoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
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
                                                processGetDynamicURLAutoComplete(AutoText, urlPath, nameDataEl);
                                            }
                                        }
                                        AutoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Log.e("CEK","MASUK AutoText setOnItemClickListener");
                                                String txt = AutoText.getText().toString().trim();
                                                Log.e("CEK","MASUK AutoText setOnItemClickListener txt : "+txt+" | getItemAtPosition : "+parent.getItemAtPosition(position));
                                                FormSpin dataSpin = (FormSpin) parent.getItemAtPosition(position);
                                                int idData = dataSpin.getId();
                                                String results = dataSpin.getName();
                                                Log.e("CEK","dataSpin.getId() : "+idData+" | results : "+results);
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    if (nameDataEl.contains("jenis") && (nameDataEl.contains("pengaduan") || nameDataEl.contains("komplain"))) {
                                                        valSpinAutoComplete.put(nameDataEl, results);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                processGetSpinChildAutoComplete(nameDataEl);
                                            }
                                        });
                                        AutoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View view, boolean b) {
                                                Log.e("CEK","onFocusChange : "+b);
                                            }
                                        });
                                    } else {
                                        EditText ed = (EditText) llFormBuild.getChildAt(i);

                                        ed.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                if (nameDataEl.equals("npwp" + finalValKurung)) {
                                                    lasLenChar = charSequence.length();
                                                }
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                try {
                                                    objEl.put(nameDataEl, charSequence);
                                                    objEl.put("idDips", idDips);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            /*if (isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(objEl);
                                            }*/
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                if (nameDataEl.equals("npwp" + finalValKurung)) {
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
                                    }
                                    objEl.put(nameDataEl, "");
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    Log.e("CEK","MASUK RadioGroup");
                                    objEl.put(nameDataEl, "");
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    Log.e("CEK","MASUK CheckBox");
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
                                        }
                                    });
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Log.e("CEK","MASUK Spinner");
                                    objEl.put(nameDataEl, "");
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = spin.getSelectedItem().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
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
                                    Log.e("CEK","MASUK RelativeLayout");
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
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

                                        objEl.put(nameDataEl, "");
                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                Log.e("CEK","getSelectedItem : "+spin.getSelectedItem().toString());
                                                FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                int idData = dataSpin.getId();
                                                String getCodeType = dataSpin.getCode();
                                                String results = dataSpin.getName();
                                                Log.e("CEK","dataSpin.getId() : "+idData);
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                        valSpinProv.put(nameDataEl,idData);
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

                                                Log.e("CEK","flagStuckSpin : "+flagStuckSpin);
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
                                    Log.e("CEK","MASUK AutoCompleteTextView");
                                    objEl.put(nameDataEl, "");
                                    AutoCompleteTextView AutoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
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
                                            processGetDynamicURLAutoComplete(AutoText, urlPath, nameDataEl);
                                        }
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    Log.e("CEK", "LinearLayout getChildCount : " + ll.getChildCount());
                                    if (ll.getChildCount() > 1) {
                                        if (ll.getChildAt(1) instanceof TextView) {
                                            keyUpFile = nameDataEl;
                                            if (ll.getChildAt(0) instanceof LinearLayout) {
                                                LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
                                                Log.e("CEK", "MASUK LinearLayout CHILD ke-" + i);

                                                TextView tvll = (TextView) ll2.getChildAt(1);
                                                String txt = tvll.getText().toString();
                                                Log.e("CEK", "tvll : " + txt);
                                                if (txt.toLowerCase().indexOf("gambar") > 0 || txt.toLowerCase().indexOf("image") > 0 || txt.toLowerCase().indexOf("tangan") > 0) {
                                                    tvSavedImg = (TextView) ll.getChildAt(1);
                                                    ll2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            chooseFromSD();
                                                        }
                                                    });
                                                } else {
                                                    tvSavedFile = (TextView) ll.getChildAt(1);
                                                    ll2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (dataFiles.size() >= 3) {
                                                                Toast.makeText(mContext,R.string.max_upfile, Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
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
                                            }
                                        } else if (ll.getChildAt(1) instanceof RecyclerView) {
                                            keyUpFile = nameDataEl;
                                            Log.e("CEK","MASUK UPLOAD FILE RecyclerView");
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
                                            Log.e("CEK","MASUK UPLOAD GALLERY");
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
                                                    session.saveMedia(1);
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
                                                    session.saveMedia(2);
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

    private void processGetDynamicURLAutoComplete(AutoCompleteTextView autoText, String urlPath, String nameDataEl) {
        Log.e("CEK","processGetDynamicURLAutoComplete : "+urlPath);
        Server.getAPIService().getDynamicUrl(urlPath).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processGetDynamicURL code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK", "processGetDynamicURL dataS : " + dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
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
                                dataDropDown.add(new FormSpin(idData, idJenis, jenisLabel, jenisLabel));
                            }
                        }
                        ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
                        autoText.setAdapter(adapter2);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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
                                if (dataArr.getJSONObject(i).has("id")) {
                                    idData = dataArr.getJSONObject(i).getInt("id");
                                }
                                String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                                String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                                dataDropDown.add(new FormSpin(idData, labelIdn, labelIdn, labelEng));
                                if (i == 0) {
                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                        valSpinProv.put(nameDataEl,idData);
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

    private void processGetSpinChildAutoComplete(String nameDataEl) {
        Log.e("CEK","processGetSpinChildAutoComplete nameDataEl : "+nameDataEl);
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
                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                            Log.e("CEK", "MASUK EditText : " + nameDataEl);
                            if ((nameDataEl.contains("jenis") && (nameDataEl.contains("pengaduan") || nameDataEl.contains("komplain"))) &&
                                    (getnameDataEl.contains("hal") && (getnameDataEl.contains("pengaduan") || getnameDataEl.contains("komplain")))) {
                                if (!urlPath.isEmpty()) {
                                    AutoCompleteTextView AutoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    if (!urlPath.isEmpty()) {
                                        String typeComplaint = valSpinAutoComplete.getString(nameDataEl);
                                        String urlNew = urlPath.replace(":jenisPengaduan",typeComplaint);

                                        Log.e("CEK", "urlNew : "+urlNew);
                                        processGetDynamicURLAutoComplete(AutoText, urlPath, nameDataEl);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    if (idProv != 0) {
                                        String urlNew = urlPath.replace(":id_provinsi", idSpin);

                                        Log.e("CEK", "urlNew : " + urlNew);

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

                                    Log.e("CEK", "urlNew : "+urlNew);
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

                                    Log.e("CEK", "urlNew : "+urlNew);

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

                                    Log.e("CEK", "urlNew : " + urlNew);
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
        startActivityForResult(intent, 201);
    }

    private void CekDataByNIK(JSONObject jsons){
        Log.e("CEL","MASUK CekDataByNIK");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.CekByNIK(requestBody);
        Log.e("CEL","URL CekDataByNIK : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","RESPONSE CekDataByNIK  : "+response.code());
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful() && response.body().size() > 0) {
                    //Mirroring(true, Nama, NIK);
                    String dataS = response.body().toString();
                    Log.e("CEK","RESPONSE  : "+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int err_code = jsObj.getInt("code");
                        String message = jsObj.getString("message");

                        if (err_code == 200) {
                            JSONObject dataObj = jsObj.getJSONObject("data");
                            idDips = dataObj.getString("idDips");
                            session.saveIdDips(idDips);

                            String getDataNasabah = session.getNasabah();
                            Log.e("CEK","getDataNasabah : "+getDataNasabah);
                            JSONObject dataNasabahObj = null;
                            if (getDataNasabah != null && !getDataNasabah.isEmpty()) {
                                dataNasabahObj = new JSONObject(getDataNasabah);
                            }

                            String namaIdentitas = "";
                            if (dataObj.has("namaLengkap")) {
                                namaIdentitas = dataObj.getString("namaLengkap");
                            }
                            String no_handphone = "";
                            if (dataObj.has("noHp")) {
                                no_handphone = dataObj.getString("noHp");
                            }
                            String noIdentitas = "";
                            if (dataObj.has("nik")) {
                                noIdentitas = dataObj.getString("nik");
                            }
                            String branchCode = "";
                            if (dataObj.has("branchCode")) {
                                branchCode = dataObj.getString("branchCode");
                            }

                            dataNasabahObj.put("noHp",no_handphone);
                            dataNasabahObj.put("namaLengkap",namaIdentitas);
                            dataNasabahObj.put("nik",noIdentitas);
                            dataNasabahObj.put("branchCode",branchCode);
                            session.saveNasabah(dataNasabahObj.toString());

                            if (session.getCSID() != null) {
                                try {
                                    jsons.put("idDips",idDips);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (isSessionZoom) {
                                    RabbitMirroring.MirroringSendKey(jsons);
                                }
                            }
                            isSwafoto = dataObj.getBoolean("isSwafoto");
                            if (dataObj.has("noCif")) {
                                if (!dataObj.isNull("noCif")) {
                                    String noCif = dataObj.getString("noCif");
                                    session.saveNoCIF(noCif);
                                    session.saveIsCust(true);
                                    if (!isSwafoto){
                                        session.saveIsSwafoto(isSwafoto);
                                        session.saveFormCOde(4);
                                        Fragment fragment = new frag_cif_new();
                                        Bundle bundle = new Bundle();
                                        fragment.setArguments(bundle);
                                        if (isSessionZoom) {
                                            RabbitMirroring.MirroringSendEndpoint(3);
                                            getFragmentPage(fragment);
                                        } else {
                                            getFragmentPageDefault(fragment);
                                        }
                                    } else {
                                        //if (chkFlow == 1) {
                                        Log.e("CEK_INPUT_DATA","getNoCIF : "+session.getNoCIF());
                                            Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                            intent.putExtra("CUSTNAME",custName);
                                            startActivity(intent);
                                            ((Activity) mContext).finishAffinity();
                                        /*} else {
                                            Fragment fragment = new frag_portfolio_new();
                                            session.saveNoCIF(noCif);
                                            session.clearCIF();
                                            getFragmentPage(fragment);
                                        }*/
                                    }
                                } else {
                                    //if (chkFlow == 1) {
                                    Log.e("CEK_INPUT_DATA","getNoCIF : "+session.getNoCIF());
                                        Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                        intent.putExtra("CUSTNAME",custName);
                                        startActivity(intent);
                                        ((Activity) mContext).finishAffinity();
                                    /*} else {
                                        PopUpTnc();
                                    }*/
                                }
                            }
                        } else {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }

                        /*if (err_code == 0){
                            PopUpTnc();
                        }
                        else {
                            Mirroring4(false);
                            session.clearCIF();
                            getFragmentPage(new frag_portfolio());
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (response.code() == 404) {
                        Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                        intent.putExtra("CUSTNAME",custName);
                        startActivity(intent);
                        ((Activity) mContext).finishAffinity();
                    }
                    Log.d("CEK","MASUK ELSE");
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

    private void PopUpTnc(){
        Log.e("CEK","MASUK PopUpTnc");
        inflater = ((Activity)mContext).getLayoutInflater();
        if (sweetAlertDialogTNC == null) {
            dialogView = inflater.inflate(R.layout.item_tnc,null);
            sweetAlertDialogTNC = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
            sweetAlertDialogTNC.setCustomView(dialogView);
            sweetAlertDialogTNC.hideConfirmButton();
            sweetAlertDialogTNC.setCancelable(false);
        }
        sweetAlertDialogTNC.show();
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setClickable(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    Log.d("CHECK","TRUE");
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.Blue));
                    btn.setClickable(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                    btn.setClickable(false);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    sweetAlertDialogTNC.dismiss();
                    sweetAlertDialogTNC.cancel();

                    session.saveIsCust(true);
                    session.saveIsSwafoto(isSwafoto);
                    session.saveFormCOde(4);
                    Fragment fragment = new frag_cif_new();
                    Bundle bundle = new Bundle();
                    fragment.setArguments(bundle);

                    if (isSessionZoom) {
                        RabbitMirroring.MirroringSendEndpoint(3);
                        getFragmentPage(fragment);
                    } else {
                        getFragmentPageDefault(fragment);
                    }
                }
                else {
                    btn.setClickable(false);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("CEK","onActivityResult : "+resultCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUESTCODE_CAPTURE) {
                Log.e("CEK","RETURN CAMERA");
                session.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);

                if (llFileGallery != null) {
                    llFileGallery.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);

                    try {
                        File mediaFile = createTemporaryFile(resultCamera);
                        String fileName = mediaFile.getName();
                        tvSavedImg.setText(fileName);
                        if (!keyUpImage.isEmpty()) {
                            objEl.put(keyUpImage,fileName);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                imgtoBase64(bitmap);
            } else if (requestCode == 201) {
                session.saveFlagUpDoc(true);
                Log.e("CEK","RESULT GAMBAR");
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = mContext.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                File files = new File(picturePath);
                String fileName = files.getName();
                Log.e("CEK","RESULT picturePath : "+picturePath);
                Log.e("CEK","RESULT files.getName : "+fileName);
                tvSavedImg.setText(fileName);
                c.close();
                prosesOptimalImage(picturePath);
                if (llFileGallery != null) {
                    llFileGallery.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                }
                if (!keyUpImage.isEmpty()) {
                    try {
                        objEl.put(keyUpImage,fileName);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (requestCode == REQUESTCODE_FILE) {
                Log.e("CEK","RESULT FILE");
                Uri uri = data.getData();
                Log.e("CEK","uri : "+uri);
                if (dataFiles.size() > 3) {
                    Toast.makeText(mContext, R.string.max_upfile, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uri != null) {

                    Cursor c = mContext.getContentResolver().query(uri,null, null, null, null);
                    c.moveToFirst();
                    @SuppressLint("Range") String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.e("CEK","RESULT fileName : "+fileName);
                    if (tvSavedFile != null) {
                        tvSavedFile.setText(fileName);
                        if (!keyUpFile.isEmpty()) {
                            try {
                                objEl.put(keyUpFile,fileName);
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
                                objEl.put(keyUpFile,fileName);
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
                    Log.e("CEK","MULTI uri : "+data.getClipData().getItemCount());

                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uriFile = data.getClipData().getItemAt(i).getUri();
                        Log.e("CEK","uriFile : "+uriFile.toString());
                        Cursor c = mContext.getContentResolver().query(uriFile,null, null, null, null);
                        c.moveToFirst();
                        @SuppressLint("Range") String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.e("CEK","RESULT fileName : "+fileName);
                        if (rv_item_file != null) {
                            dataFiles.add(new FileModel("1", fileName, R.color.item_file_silver, ""));
                        }
                        c.close();
                        int intk = i + 1;
                        if (!keyUpFile.isEmpty()) {
                            try {
                                objEl.put(keyUpFile+intk,fileName);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    if (rv_item_file != null) {
                        rv_item_file.setVisibility(View.VISIBLE);
                        setRecyler();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            session.saveFlagUpDoc(true);
        }
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
        if (viewImage != null) {
            viewImage.setImageBitmap(bitmap);
        }
        imageBytes = baos.toByteArray();
        imgBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        if (isSessionZoom && !imgBase64.isEmpty()) {
            JSONObject dataImg = new JSONObject();
            try {
                dataImg.put(keys,imgBase64);
                RabbitMirroring.MirroringSendKey(dataImg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setRecyler(){
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_item_file.setLayoutManager(recylerViewLayoutManager);

        AdapterFile recyclerViewAdapter = new AdapterFile(getContext(), dataFiles);
        rv_item_file.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

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
