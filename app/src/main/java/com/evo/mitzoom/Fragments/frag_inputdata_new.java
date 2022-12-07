package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsCameraSource;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_inputdata_new extends Fragment {

    private Context mContext;
    private boolean isCust;
    public int seconds = 0;
    public boolean running = true;
    private SessionManager session;
    private SwipeRefreshLayout swipe;
    private LinearLayout llFormBuild;
    private MaterialButton btnNext;
    private LayoutInflater inflater;
    private View dialogView;
    private SweetAlertDialog sweetAlertDialogTNC;
    private String idDips;
    private String Nama = "";
    private String NIK = "";
    private JSONArray idElement = new JSONArray();
    private boolean isSwafoto = false;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    JSONObject objEl = new JSONObject();
    private RabbitMirroring rabbitMirroring;
    private byte[] bytePhoto = new byte[0];
    private int chkFlow;
    private int lasLenChar;
    private boolean backSpaceChar;
    private String custName = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments().containsKey("RESULT_IMAGE_AI")) {
            bytePhoto = getArguments().getByteArray("RESULT_IMAGE_AI");
        }
        if (getArguments().containsKey("CUSTNAME")) {
            custName = getArguments().getString("CUSTNAME");
        }
        session = new SessionManager(mContext);
        isCust = session.getKEY_iSCust();
        chkFlow = session.getFLOW();

        if (chkFlow == 0) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_form_builder, container, false);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        llFormBuild = (LinearLayout) view.findViewById(R.id.llFormBuild);
        btnNext = (MaterialButton) view.findViewById(R.id.btnNext);
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

        processGetForm();

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
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            Log.e("CEK", "MASUK EDITTEXT ke-" + i);
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
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
                                                    Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                                break;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                            Log.e("CEK", "MASUK LinearLayout ke-" + i);
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

                    if (flagNext) {
                        Log.e("CEL","REQUEST : "+objEl.toString());
                        CekDataByNIK(objEl);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (chkFlow == 0) {
            rabbitMirroring.closeThreadConnection();
        }
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

    private void processGetForm() {
        Log.e("CEK","MASUK processGetForm");
        Server.getAPIWAITING_PRODUCT().getFormBuilder(7).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","response processGetForm : "+response.code());
                if (response.isSuccessful()) {
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
                            if (idEl == idDataEl) {

                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp")) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                objEl.put("idDips",idDips);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (chkFlow == 0) {
                                                rabbitMirroring.MirroringSendKey(objEl);
                                            }
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
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    objEl.put(nameDataEl, false);
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    objEl.put(nameDataEl, "");
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            Log.e("CEK","onItemSelected : "+spin.isSelected());
                                            Log.e("CEK","getSelectedItem : "+spin.getSelectedItem().toString());
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
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    objEl.put(nameDataEl, "");
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
                                                    /*String[] mimeTypes =
                                                            {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                                                    "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                                                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                                                    "text/plain",
                                                                    "application/pdf",
                                                                    "application/zip", "application/vnd.android.package-archive"};*/

                                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                                        startActivityForResult(intent, 202);
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
                            if (session.getCSID() != null) {
                                try {
                                    jsons.put("idDips",idDips);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (chkFlow == 0) {
                                    rabbitMirroring.MirroringSendKey(jsons);
                                }
                            }
                            isSwafoto = dataObj.getBoolean("isSwafoto");
                            if (dataObj.has("noCif")) {
                                if (!dataObj.isNull("noCif")) {
                                    String noCif = dataObj.getString("noCif");
                                    if (!isSwafoto){
                                        session.saveIsCust(true);
                                        session.saveIsSwafoto(isSwafoto);
                                        session.saveFormCOde(4);
                                        Fragment fragment = new frag_cif();
                                        Bundle bundle = new Bundle();
                                        bundle.putByteArray("RESULT_IMAGE_AI",bytePhoto);
                                        fragment.setArguments(bundle);
                                        if (chkFlow == 0) {
                                            RabbitMirroring.MirroringSendEndpoint(3);
                                            getFragmentPage(fragment);
                                        } else {
                                            getFragmentPageDefault(fragment);
                                        }
                                    } else {
                                        if (chkFlow == 1) {
                                            Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                            intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                                            intent.putExtra("CUSTNAME",custName);
                                            startActivity(intent);
                                            ((Activity) mContext).finishAffinity();
                                        } else {
                                            Fragment fragment = new frag_portfolio_new();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("noCif",noCif);
                                            fragment.setArguments(bundle);
                                            session.clearCIF();
                                            getFragmentPage(fragment);
                                        }
                                    }
                                } else {
                                    if (chkFlow == 1) {
                                        Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                        intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                                        intent.putExtra("CUSTNAME",custName);
                                        startActivity(intent);
                                        ((Activity) mContext).finishAffinity();
                                    } else {
                                        PopUpTnc();
                                    }
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
                        intent.putExtra("RESULT_IMAGE_AI",bytePhoto);
                        intent.putExtra("CUSTNAME",custName);
                        startActivity(intent);
                        ((Activity) mContext).finishAffinity();
                    }
                    Log.d("CEK","MASUK ELSE");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
                    session.saveFLOW(0);
                    sweetAlertDialogTNC.dismiss();
                    sweetAlertDialogTNC.cancel();

                    session.saveIsCust(true);
                    session.saveIsSwafoto(isSwafoto);
                    session.saveFormCOde(4);
                    Fragment fragment = new frag_cif();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("RESULT_IMAGE_AI",bytePhoto);
                    fragment.setArguments(bundle);

                    if (chkFlow == 0) {
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
            if (requestCode == 1) {
                Log.e("CEK","RETURN CAMERA");
                session.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);

                if (data.getExtras() != null) {
                    int resultCekData = data.getExtras().getInt("result_cek_data");
                    if (resultCekData == 0){
                        PopUpTnc();
                    }
                    else {
                        session.clearCIF();
                        if (chkFlow == 0) {
                            RabbitMirroring.MirroringSendEndpoint(14);
                            getFragmentPage(new frag_portfolio_new());
                        } else {
                            getFragmentPageDefault(new frag_portfolio_new());
                        }
                    }
                }
            } else if (requestCode == 201) {
                Log.e("CEK","RESULT GAMBAR");
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = mContext.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                File files = new File(picturePath);
                String fileName = files.getName().toString();
                Log.e("CEK","RESULT picturePath : "+picturePath);
                Log.e("CEK","RESULT files.getName : "+fileName);
                tvSavedImg.setText("filename : "+fileName);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            } else if (requestCode == 202) {
                Log.e("CEK","RESULT FILE");
                Uri uri = data.getData();
                if (uri != null) {
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
            session.saveFlagUpDoc(true);
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
