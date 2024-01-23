package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
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

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wealth_management extends Fragment {

    private static final String TAG = "frag_wealth_management";
    private final int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private NestedScrollView nestedScroll;
    private RelativeLayout rlResiko;
    private TextView msgErrResiko;
    private ImageView imgSuccess;
    private ImageView imgNotYet;
    private LinearLayout llFormBuild;
    private RelativeLayout rlRequest;
    private TextView tvReqProd;
    private Button btnProcess;
    private int formId = 0;
    private String labelserv = "";
    private JSONArray idElement;
    private int lasLenChar;
    private boolean backSpaceChar;
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private boolean flagStuckSpin = false;
    private String provinsi,kodepos="",kota_kabupaten,kecamatan,desa_kelurahan;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String picturePath;
    JSONObject objEl = new JSONObject();
    JSONObject mirrObj = new JSONObject();
    private String labelTrx = "";
    private TextView tvCurrency = null;
    private static String nominalInput = "";
    private boolean riskprofile;
    private String valueRiskProfile = "";
    private JSONArray questionData = new JSONArray();
    private JSONArray dataFormArr = new JSONArray();
    private JSONArray dataAnswers = new JSONArray();
    private int idTNC = 0;
    private String dataTnC = "";
    private boolean flagViewTNC = false;
    private JSONObject dataNasabahObj;
    private String namaLengkap = "";
    private String jenisKelamin = "";
    private String no_handphone = "";
    private String nik = "";
    private String investmentGoal = "";
    private String souceOfFund = "";
    private JSONArray idElementReq = null;
    private String statusSID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("idGenerateForm")) {
                formId = getArguments().getInt("idGenerateForm");
            }
            if (getArguments().containsKey("idTNC")) {
                idTNC = getArguments().getInt("idTNC");
            }
            if (getArguments().containsKey("statusSID")) {
                statusSID = getArguments().getString("statusSID");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            if (getArguments().containsKey("riskprofile")) {
                riskprofile = getArguments().getBoolean("riskprofile");
            }
            /*if (getArguments().containsKey("valueRiskProfile")) {
                valueRiskProfile = getArguments().getString("valueRiskProfile");
            }*/
            if (getArguments().containsKey("questionData")) {
                String questionDataStr = getArguments().getString("questionData");
                try {
                    questionData = new JSONArray(questionDataStr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("dataAnswers")) {
                String dataAnswersStr = getArguments().getString("dataAnswers");
                try {
                    dataAnswers = new JSONArray(dataAnswersStr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("dataFormArr")) {
                String dataFormArrStr = getArguments().getString("dataFormArr");
                try {
                    dataFormArr = new JSONArray(dataFormArrStr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String dataNasabah = sessions.getNasabah();
        ConnectionRabbitHttp.init(mContext);
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                if (dataNasabahObj.has("namaLengkap")) {
                    namaLengkap = dataNasabahObj.getString("namaLengkap");
                }
                if (dataNasabahObj.has("jenisKelamin")) {
                    jenisKelamin = dataNasabahObj.getString("jenisKelamin");
                }
                if (dataNasabahObj.has("noHp")) {
                    no_handphone = dataNasabahObj.getString("noHp");
                }
                if (dataNasabahObj.has("nik")) {
                    nik = dataNasabahObj.getString("nik");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wealth_management, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);
        rlResiko = (RelativeLayout) views.findViewById(R.id.rlResiko);
        msgErrResiko = (TextView) views.findViewById(R.id.msgErrResiko);
        imgSuccess = (ImageView) views.findViewById(R.id.imgSuccess);
        imgNotYet = (ImageView) views.findViewById(R.id.imgNotYet);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        rlRequest = (RelativeLayout) views.findViewById(R.id.rlRequest);
        tvReqProd = (TextView) views.findViewById(R.id.tvReqProd);

        btnProcess = (Button) views.findViewById(R.id.btnProcess);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labelTrx = "reksadana";

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
            tvReqProd.setText(labelserv);
        }

        if (!statusSID.isEmpty()) {
            processGetRiskProfile();
        } else {
            if (riskprofile) {
                rlResiko.setBackground(mContext.getDrawable(R.drawable.bg_border_green));
                msgErrResiko.setVisibility(View.GONE);
                imgSuccess.setVisibility(View.VISIBLE);
                imgNotYet.setVisibility(View.GONE);
                btnProcess.setEnabled(true);
                btnProcess.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                processGetTNC();
            }

            if (isSessionZoom) {
                BaseMeetingActivity.showProgress(true);
            } else {
                DipsSwafoto.showProgress(true);
            }

            if (idTNC == 0) {
                processGetRiskProfile();
            } else {
                processGetForm();
            }
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionRabbitHttp.mirroringEndpoint(15);
                getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
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
                                    String labelDataEl = idElement.getJSONObject(j).getString("label");
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            } else if (nameDataEl.contains("noponsel") && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            objEl.put(nameDataEl, results);
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
                                                objEl.put(nameDataEl, results);
                                            } else if (requiredDataEl){
                                                Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(mContext, labelDataEl + " "+getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
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
                    if (flagNext) {
                        if (!flagViewTNC) {
                            ConnectionRabbitHttp.mirroringEndpoint(282);
                            PopUpTnc();
                        }
                    }
                }
            }
        });

        rlResiko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("idTNC",23);
                bundle.putString("labelserv",labelserv);
                if (riskprofile && questionData.length() > 0) {
                    bundle.putBoolean("questions",true);
                    bundle.putString("questionData",questionData.toString());
                    bundle.putString("dataAnswers",dataAnswers.toString());
                    bundle.putString("dataFormArr",dataFormArr.toString());
                    bundle.putInt("selectedQuestions",0);
                }
                Fragment fragments = new frag_wm_risk_profile();
                fragments.setArguments(bundle);
                getFragmentPageBackStack(fragments);
            }
        });
    }

    private void PopUpTnc() {
        flagViewTNC = true;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialogTNC = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        TextView tvBody = dialogView.findViewById(R.id.tvBody);
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setClickable(false);
        btn.setText(getString(R.string.selanjutnya));

        sweetAlertDialogTNC.setCustomView(dialogView);
        sweetAlertDialogTNC.hideConfirmButton();
        sweetAlertDialogTNC.setCancelable(true);

        if (!dataTnC.isEmpty()) {
            tvBody.setText(Html.fromHtml(dataTnC, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int idx = source.indexOf(",");
                    idx += 1;
                    String new_source = source.substring(idx);
                    byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                    int intH = d.getIntrinsicHeight();
                    int intW = d.getIntrinsicWidth();
                    d.setBounds(0, 0, intW, intH);
                    return d;
                }
            }, null));
        }

        sweetAlertDialogTNC.show();

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);

        sweetAlertDialogTNC.getWindow().setLayout(newWidth,newHeight);
        sweetAlertDialogTNC.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                flagViewTNC = false;
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btn.setClickable(true);
                    JSONObject tncCheckObj = new JSONObject();
                    try {
                        tncCheckObj.put("tnc",true);
                        if (mirrObj.has(labelTrx)) {
                            JSONObject dataFormObj = mirrObj.getJSONObject(labelTrx);
                            dataFormObj.put("formsid",tncCheckObj);
                            mirrObj.put(labelTrx,dataFormObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    }
                }
                else {
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                    btn.setClickable(false);
                    JSONObject tncCheckObj = new JSONObject();
                    try {
                        tncCheckObj.put("tnc",false);
                        if (mirrObj.has(labelTrx)) {
                            JSONObject dataFormObj = mirrObj.getJSONObject(labelTrx);
                            dataFormObj.put("formsid",tncCheckObj);
                            mirrObj.put(labelTrx,dataFormObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (isSessionZoom) {
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    }
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    sweetAlertDialogTNC.cancel();
                    sweetAlertDialogTNC.dismissWithAnimation();
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    processCreateSID();
                }
                else {
                    btn.setClickable(false);
                }
            }
        });
    }

    private void processCreateSID() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("nik",nik);
            jsons.put("noCif",sessions.getNoCIF());
            jsons.put("investmentGoal",investmentGoal);
            jsons.put("souceOfFund",souceOfFund);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().CreateSID(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            if (dataBody.getJSONObject("data").has("status")) {
                                statusSID = dataBody.getJSONObject("data").getString("status");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        processGetRiskProfile();
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
                        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetRiskProfile() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("nik",nik);
            jsons.put("bahasa",sessions.getLANG());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().GetRiskProfile(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            valueRiskProfile = dataBody.getString("data");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if (!valueRiskProfile.isEmpty()) {
                            if (idTNC == 0) {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.showProgress(true);
                                } else {
                                    DipsSwafoto.showProgress(true);
                                }
                                if (!statusSID.isEmpty()) {
                                    processInqCIFbyNIK();
                                } else {
                                    riskprofile = true;
                                    rlResiko.setBackground(mContext.getDrawable(R.drawable.bg_border_green));
                                    msgErrResiko.setVisibility(View.GONE);
                                    imgSuccess.setVisibility(View.VISIBLE);
                                    imgNotYet.setVisibility(View.GONE);
                                    btnProcess.setEnabled(true);
                                    btnProcess.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                    processGetForm();
                                }
                            } else {
                                processInqCIFbyNIK();
                            }
                        } else {
                            processInqCIFbyNIK();
                            /*Fragment fragment = new frag_wm_risk_profile_resi();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("successs",true);
                            bundle.putString("labelserv",labelserv);
                            bundle.putString("statusSID",statusSID);
                            bundle.putString("idElement",idElementReq.toString());
                            fragment.setArguments(bundle);
                            getFragmentPage(fragment);*/
                        }
                    } else {
                        if (response.code() == 404) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            processGetForm();
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
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processInqCIFbyNIK() {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("noKtp",nik);
            dataObj.put("idDips",sessions.getKEY_IdDips());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObj.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.InquieryCIFbyNIK(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
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
                        String tempatLahir = dataObj.getJSONObject("data").getString("tempatLahir");
                        String tanggalLahir = dataObj.getJSONObject("data").getString("tanggalLahir");
                        String namaIbu = dataObj.getJSONObject("data").getString("namaIbu");

                        String tahun = tanggalLahir.substring(0, 4);
                        String bln = tanggalLahir.substring(4,6);
                        String tgl = tanggalLahir.substring(6,8);
                        String tgllahir = tgl+"-"+bln+"-"+tahun;

                        dataNasabahObj.put("tempatLahir",tempatLahir);
                        dataNasabahObj.put("tanggalLahir",tgllahir);
                        dataNasabahObj.put("namaIbu",namaIbu);
                        sessions.saveNasabah(dataNasabahObj.toString());

                        parsingDataElementForm(tempatLahir,tgllahir);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Fragment fragment = new frag_wm_risk_profile_resi();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("successs",true);
                                bundle.putString("labelserv",labelserv);
                                bundle.putString("statusSID",statusSID);
                                bundle.putString("idElement",idElementReq.toString());
                                fragment.setArguments(bundle);
                                getFragmentPage(fragment);
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
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
                    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                    Fragment fragment = new frag_wm_risk_profile_resi();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("successs",false);
                    bundle.putString("labelserv",labelserv);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
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

    private void parsingDataElementForm(String tempatLahir, String tgllahir) {
        try {
            idElementReq = new JSONArray();
            JSONObject dataeEl = new JSONObject();
            dataeEl.put("value",namaLengkap);
            dataeEl.put("label",getString(R.string.name));
            dataeEl.put("name","namalengkap");
            idElementReq.put(dataeEl);
            dataeEl = new JSONObject();
            dataeEl.put("value",nik);
            dataeEl.put("label",getString(R.string.no_ektp));
            dataeEl.put("name","noektp");
            idElementReq.put(dataeEl);
            dataeEl = new JSONObject();
            dataeEl.put("value","-");
            dataeEl.put("label",getString(R.string.no_npwp));
            dataeEl.put("name","nonpwp");
            idElementReq.put(dataeEl);
            dataeEl = new JSONObject();
            dataeEl.put("value",jenisKelamin);
            dataeEl.put("label",getString(R.string.jenis_kelamin));
            dataeEl.put("name","jeniskelamin");
            idElementReq.put(dataeEl);
            dataeEl = new JSONObject();
            dataeEl.put("value",tempatLahir);
            dataeEl.put("label",getString(R.string.ttl));
            dataeEl.put("name","tempatlahir");
            idElementReq.put(dataeEl);
            dataeEl = new JSONObject();
            dataeEl.put("value",tgllahir);
            dataeEl.put("label",getString(R.string.birthdate));
            dataeEl.put("name","tanggallahir");
            idElementReq.put(dataeEl);
            dataeEl = new JSONObject();
            dataeEl.put("value",valueRiskProfile);
            dataeEl.put("label",getString(R.string.profil_resiko));
            dataeEl.put("name","profilresiko");
            idElementReq.put(dataeEl);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetTNC() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getTNC(idTNC,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        String cekdataTnC = dataObj.getJSONObject("data").getString("data");
                        if (cekdataTnC.contains("{")) {
                            JSONObject labelTNC = new JSONObject(cekdataTnC);
                            String language = sessions.getLANG();
                            if (language.equals("id")) {
                                dataTnC = labelTNC.getString("labelIdn");
                            } else {
                                dataTnC = labelTNC.getString("labelEng");
                            }
                        } else {
                            dataTnC = cekdataTnC;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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

    private void processGetForm() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    llFormBuild.removeAllViewsInLayout();
                    llFormBuild.setVisibility(View.VISIBLE);
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
                        new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = MyParserFormBuilder.getForm();
                        processValidationActionForm();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                            String CompoName = idElement.getJSONObject(j).getString("CompoName");
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
                                    if (!CompoName.equals("datalist")) {
                                        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View view, boolean b) {

                                            }
                                        });
                                    }
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                if (isSessionZoom) {
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
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
                                    objEl.put(nameDataEl, "");
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    objEl.put(nameDataEl, "");

                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                processEnableComp(results);
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    if (isSessionZoom) {
                                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                                        mirrObj.put(labelTrx, reqFormMirroring);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
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
                                                if (isSessionZoom) {
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    objEl.put(nameDataEl, "");
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = spin.getSelectedItem().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                if (isSessionZoom) {
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
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
                                else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
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
                                                if (nameDataEl.contains("sumberdana")) {
                                                    processGetDynamicURLSumberDana(spin,urlPath);
                                                } else {
                                                    processGetDynamicURL(spin, urlPath, nameDataEl);
                                                }
                                            }
                                        }

                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                String accountType = "";
                                                int beneficiaryCode = 0;
                                                String swiftCode = "";
                                                String cityCode = "";
                                                String jenislayanan = "";
                                                if (nameDataEl.contains("sumberdana")) {
                                                    FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                    String results = dataSpin.getName();
                                                    accountType = dataSpin.getCode();
                                                    results = results.replaceAll("\n"," / ");
                                                    try {
                                                        objEl.put(nameDataEl, results);
                                                    } catch (JSONException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                } else {
                                                    FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                    int idData = dataSpin.getId();
                                                    String results = dataSpin.getName();
                                                    try {
                                                        objEl.put(nameDataEl, results);
                                                        if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                            String newNameDataEl = nameDataEl;
                                                            if (nameDataEl.contains("(")) {
                                                                int indxProv = nameDataEl.indexOf("(");
                                                                newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                                            }
                                                            valSpinProv.put(newNameDataEl, idData);
                                                            if (nameDataEl.contains("provinsi")) {
                                                                provinsi = results;
                                                                kodepos = "";
                                                            } else if (nameDataEl.contains("kabupaten") || nameDataEl.contains("kota")) {
                                                                kota_kabupaten = results;
                                                                kodepos = "";
                                                            } else if (nameDataEl.contains("kecamatan")) {
                                                                kecamatan = results;
                                                                kodepos = "";
                                                            } else if (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa")) {
                                                                desa_kelurahan = results;
                                                                kodepos = "";
                                                            }
                                                        } else if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                                                            jenislayanan = results;
                                                        } else if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                            beneficiaryCode = idData;
                                                            String valCode = dataSpin.getCode();
                                                            if (valCode.contains("|")) {
                                                                String[] sp = valCode.split("\\|");
                                                                swiftCode = sp[0].trim();
                                                                cityCode = sp[1].trim();
                                                            }
                                                        } else if (nameDataEl.contains("investasi")) {
                                                            investmentGoal = dataSpin.getCode();
                                                        } else if (nameDataEl.contains("pendapatan")) {
                                                            souceOfFund = dataSpin.getCode();
                                                        } else {
                                                            valSpin.put(nameDataEl, idData);
                                                        }
                                                        if (flagStuckSpin) {
                                                            processGetSpinChild(nameDataEl);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                if (isSessionZoom) {
                                                    try {
                                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                                        mirrObj.put(labelTrx, reqFormMirroring);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                        break;
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    objEl.put(nameDataEl, "");

                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = autoText.getText().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                try {
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
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
                                                try {
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    if (ll.findViewById(R.id.llCurrency) != null) {
                                        tvCurrency = (TextView) ll.findViewById(R.id.tvCurrency);
                                        EditText tvContentCurr = (EditText) ll.findViewById(R.id.tvContentCurr);
                                        try {
                                            objEl.put(nameDataEl, "");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        tvContentCurr.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    try {
                                                        objEl.put(nameDataEl, s);
                                                        nominalInput = String.valueOf(s);

                                                        try {
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            mirrObj.put(labelTrx, reqFormMirroring);
                                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                            }
                                        });

                                    } else {
                                        if (ll.getChildCount() > 1) {
                                            if (ll.getChildAt(0) instanceof LinearLayout) {
                                                LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);

                                                TextView tvll = (TextView) ll2.getChildAt(1);
                                                String txt = tvll.getText().toString();
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
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    private JSONObject dataReqFormMirroring() {
        JSONObject dataFormObj = new JSONObject();
        try {
            JSONObject dataFormObj2 = new JSONObject(objEl.toString());
            dataFormObj2.put("profilrisiko",riskprofile);

            dataFormObj.put("formsid",dataFormObj2);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return dataFormObj;
    }

    private void processEnableComp(String results) {
        int child = llFormBuild.getChildCount();

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            String nameDataElGab = "";
                            if (idElement.getJSONObject(j).has("nameGab")) {
                                nameDataElGab = idElement.getJSONObject(j).getString("nameGab");
                            }
                            if (idEl == idDataEl) {
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    if (!nameDataElGab.isEmpty()) {
                                        if (nameDataElGab.equals(nameDataEl)) {
                                            if (results.toLowerCase().contains("lain") || results.toLowerCase().contains("other")) {
                                                ed.setEnabled(true);
                                                ed.setFocusableInTouchMode(true);
                                            } else {
                                                ed.setEnabled(false);
                                                ed.setFocusable(false);
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
        }
    }

    private void processGetDynamicURLSumberDana(Spinner spinner, String urlPath) {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",sessions.getNoCIF());
            jsons.put("bahasa",sessions.getLANG());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrlPost(urlPath,requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        JSONObject objData = dataObj.getJSONObject("data");
                        JSONArray dataArr = objData.getJSONArray("portotabungan");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        String[] sourceAcc = new String[dataArr.length()];
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

                            Double d = Double.valueOf(availBalance);
                            //DecimalFormat formatter = new DecimalFormat("#,###.##");
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);
                            String formattedNumber = formatter.format(d);

                            String labelIdn = prodName+"\n"+accountNo+" - "+accountName+"\n"+acctCur+" "+formattedNumber;
                            sourceAcc[i] = labelIdn;

                            dataDropDown.add(new FormSpin(idData,accountType,labelIdn,labelIdn));
                        }
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDown);
                        //ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.dropdown_multiline, dataDropDown);
                        spinner.setAdapter(adapterSourceAcc);
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
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
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
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

                            String labelIdn = "";
                            String labelEng = "";
                            if (dataArr.getJSONObject(i).has("labelIdn")) {
                                labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                                labelEng = dataArr.getJSONObject(i).getString("labelEng");
                            } else if (dataArr.getJSONObject(i).has("prefix")) {
                                String name = dataArr.getJSONObject(i).getString("name");
                                String prefix = dataArr.getJSONObject(i).getString("prefix");
                                labelIdn = prefix+" - "+name;
                                labelEng = labelIdn;
                            }
                            if (sessions.getLANG().equals("en")) {
                                labelIdn = labelEng;
                            }
                            valueCode = labelIdn;
                            if (dataArr.getJSONObject(i).has("beneficiaryCode")) {
                                String beneficiaryCode = dataArr.getJSONObject(i).getString("beneficiaryCode");
                                idData = Integer.parseInt(beneficiaryCode);
                            }
                            if (dataArr.getJSONObject(i).has("swiftCode") && dataArr.getJSONObject(i).has("cityCode")) {
                                String swiftCode = dataArr.getJSONObject(i).getString("swiftCode");
                                String cityCode = dataArr.getJSONObject(i).getString("cityCode");
                                valueCode = swiftCode+" | "+cityCode;
                            }
                            if (dataArr.getJSONObject(i).has("prefix")) {
                                valueCode = dataArr.getJSONObject(i).getString("prefix");
                            }
                            if (dataArr.getJSONObject(i).has("valueCode")) {
                                valueCode = dataArr.getJSONObject(i).getString("valueCode");
                            }
                            dataDropDown.add(new FormSpin(idData,valueCode,labelIdn,labelEng));
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
                                }
                                processGetSpinChild(nameDataEl);
                                if ((nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                    flagStuckSpin = true;
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
                            if ((nameDataEl.contains("provinsi") || nameDataEl.contains("province")) && (getnameDataEl.contains("kabupaten") || getnameDataEl.contains("district") || getnameDataEl.contains("kota") || getnameDataEl.contains("city"))) {
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
                            }
                            else if ((nameDataEl.contains("kabupaten") || nameDataEl.contains("district") || nameDataEl.contains("kota") || nameDataEl.contains("city")) && (getnameDataEl.contains("kecamatan") || getnameDataEl.contains("subdistrict"))) {
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
                            }
                            else if ((nameDataEl.contains("kecamatan") || nameDataEl.contains("subdistrict")) && (getnameDataEl.contains("kelurahan") || getnameDataEl.contains("urbanvillage") || getnameDataEl.contains("desa") || getnameDataEl.contains("village"))) {
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
                            }
                            else {
                                flagStuckSpin = false;
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
        if (requestCode == REQUESTCODE_GALLERY){
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = mContext.getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);
            c.close();

            //Bitmap thumbnail = prosesOptimalImage(picturePath,1);

            ExifInterface exif = null;
            int rotation = 0;
            try {
                exif = new ExifInterface(picturePath);
                rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (REQUESTCODE_GALLERY == 201) {

            } else {

            }
        }
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