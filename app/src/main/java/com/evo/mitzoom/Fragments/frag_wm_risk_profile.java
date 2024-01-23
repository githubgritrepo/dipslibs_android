package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wm_risk_profile extends Fragment {

    private static final String TAG = "frag_wealth_management";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private LinearLayout llQuestions;
    private TextView tvNoBegin;
    private TextView tvNoEnd;
    private ProgressBar progressBarline;
    private LinearLayout llFormBuild;
    private Button btnStart;
    private Button btnNext;
    private Button btnProcess;
    private int idTNC = 0;
    private String dataTnC = "";
    private JSONArray questionData = new JSONArray();
    private JSONArray dataFormArr = new JSONArray();
    private JSONArray dataAnswers = new JSONArray();
    private JSONArray dataQuestion = new JSONArray();
    private boolean questions = false;
    private int selectedQuestions = 0;
    private JSONArray idElement;
    private String labelserv = "";
    private JSONObject dataNasabahObj;
    private String no_handphone = "";
    private String nik = "";
    private String labelTrx = "";
    private JSONObject mirrObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("idTNC")) {
                idTNC = getArguments().getInt("idTNC");
            }
            if (getArguments().containsKey("questions")) {
                questions = getArguments().getBoolean("questions");
            }
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
            if (getArguments().containsKey("selectedQuestions")) {
                selectedQuestions = getArguments().getInt("selectedQuestions");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
        }

        String dataNasabah = sessions.getNasabah();
        ConnectionRabbitHttp.init(mContext);
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
                if (dataNasabahObj.has("nik")) {
                    nik = dataNasabahObj.getString("nik");
                }
                /*if (dataNasabahObj.has("branchCode")) {
                    branchCode = dataNasabahObj.getString("branchCode");
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_risk_profile, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        llQuestions = (LinearLayout) views.findViewById(R.id.llQuestions);
        tvNoBegin = (TextView) views.findViewById(R.id.tvNoBegin);
        tvNoEnd = (TextView) views.findViewById(R.id.tvNoEnd);
        progressBarline = (ProgressBar) views.findViewById(R.id.progressBarline);

        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);

        btnStart = (Button) views.findViewById(R.id.btnStart);
        btnNext = (Button) views.findViewById(R.id.btnNext);
        btnProcess = (Button) views.findViewById(R.id.btnProcess);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labelTrx = "reksadana";

        if (questions) {
            mirrObj = new JSONObject();
            ConnectionRabbitHttp.mirroringEndpoint(284);
            btnStart.setVisibility(View.GONE);
            llQuestions.setVisibility(View.VISIBLE);
            if (selectedQuestions == 0 && questionData.length() == 0) {
                questionData = new JSONArray();
                dataAnswers = new JSONArray();
                dataFormArr = new JSONArray();
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processGetQuestion();
            } else {
                if (selectedQuestions < dataFormArr.length()-1) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    btnProcess.setEnabled(true);
                    btnProcess.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    btnProcess.setVisibility(View.VISIBLE);
                }

                int noBegin = selectedQuestions + 1;
                String noBeginStr = String.valueOf(noBegin);

                tvNoBegin.setText(noBeginStr);
                String noEnd = String.valueOf(dataFormArr.length());
                tvNoEnd.setText(noEnd);
                double diff = (double) dataAnswers.length() / noBegin;
                int valProg = (int) Math.ceil((double) 100 / diff);
                progressBarline.setProgress(valProg);
                progressBarline.setProgressTintList(mContext.getResources().getColorStateList(R.color.zm_button));

                try {
                    llFormBuild.removeAllViewsInLayout();
                    llFormBuild.setVisibility(View.VISIBLE);

                    JSONObject formGenerateSelect = dataFormArr.getJSONObject(selectedQuestions);
                    JSONArray formGenerateArr = new JSONArray();
                    formGenerateArr.put(formGenerateSelect);

                    if (isSessionZoom) {
                        int questionNo = questionData.getJSONObject(selectedQuestions).getInt("questionNo");
                        String answer = questionData.getJSONObject(selectedQuestions).getString("answer");
                        JSONObject reqFormMirroring = new JSONObject();
                        reqFormMirroring.put("sortQuestion",questionNo);
                        reqFormMirroring.put("sortAnswer",answer);
                        JSONObject quesAns = new JSONObject();
                        quesAns.put("pertanyaanprofilrisiko",reqFormMirroring);
                        mirrObj.put(labelTrx, quesAns);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    }

                    new MyParserFormBuilder(mContext, formGenerateArr.toString(), llFormBuild);
                    idElement = MyParserFormBuilder.getForm();
                    validationDataQuestions();
                    processValidationActionForm();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedQuestions == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("idTNC",23);
                        bundle.putString("labelserv", labelserv);

                        Fragment fragments = new frag_wm_risk_profile();
                        fragments.setArguments(bundle);
                        getFragmentPage(fragments);
                    } else {
                        selectedQuestions--;
                        Bundle bundle = new Bundle();
                        bundle.putInt("idTNC",idTNC);
                        bundle.putBoolean("questions",questions);
                        bundle.putString("labelserv", labelserv);
                        bundle.putString("questionData",questionData.toString());
                        bundle.putString("dataAnswers",dataAnswers.toString());
                        bundle.putString("dataFormArr",dataFormArr.toString());
                        bundle.putInt("selectedQuestions",selectedQuestions);

                        Fragment fragments = new frag_wm_risk_profile();
                        fragments.setArguments(bundle);
                        getFragmentPage(fragments);
                    }
                }
            });

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
                                        String labelDataEl = idElement.getJSONObject(j).getString("label");
                                        //boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                        boolean requiredDataEl = true;
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
                                                } else if (nameDataEl.contains("email")) {
                                                    boolean cekFlag = validationEmail(results);
                                                    if (!cekFlag) {
                                                        checkEmpty = true;
                                                    }
                                                }
                                                break;
                                            } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                                RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                                int selectedId = rg.getCheckedRadioButtonId();
                                                if (selectedId > 0 || selectedId < -1) {
                                                    RadioButton rb = rg.findViewById(selectedId);
                                                    String results = rb.getText().toString();
                                                    if (requiredDataEl && results.isEmpty()) {
                                                        Toast.makeText(mContext, R.string.alert_radio, Toast.LENGTH_SHORT).show();
                                                        checkEmpty = true;
                                                    }
                                                } else if (requiredDataEl){
                                                    Toast.makeText(mContext, R.string.alert_radio, Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                break;
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
                            selectedQuestions++;
                            Bundle bundle = new Bundle();
                            bundle.putInt("idTNC",idTNC);
                            bundle.putBoolean("questions",true);
                            bundle.putString("labelserv", labelserv);
                            bundle.putString("questionData",questionData.toString());
                            bundle.putString("dataAnswers",dataAnswers.toString());
                            bundle.putString("dataFormArr",dataFormArr.toString());
                            bundle.putInt("selectedQuestions",selectedQuestions);

                            Fragment fragments = new frag_wm_risk_profile();
                            fragments.setArguments(bundle);
                            getFragmentPage(fragments);
                        }
                    }
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
                                        //boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                        boolean requiredDataEl = true;
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
                                                } else if (nameDataEl.contains("email")) {
                                                    boolean cekFlag = validationEmail(results);
                                                    if (!cekFlag) {
                                                        checkEmpty = true;
                                                    }
                                                }
                                                break;
                                            } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                                RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                                int selectedId = rg.getCheckedRadioButtonId();
                                                if (selectedId > 0 || selectedId < -1) {
                                                    RadioButton rb = rg.findViewById(selectedId);
                                                    String results = rb.getText().toString();
                                                    if (requiredDataEl && results.isEmpty()) {
                                                        Toast.makeText(mContext, R.string.alert_radio, Toast.LENGTH_SHORT).show();
                                                        checkEmpty = true;
                                                    }
                                                } else if (requiredDataEl){
                                                    Toast.makeText(mContext, R.string.alert_radio, Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                break;
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
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(true);
                            } else {
                                DipsSwafoto.showProgress(true);
                            }
                            if (!nik.isEmpty()) {
                                processCreateRiskProfile();
                            }
                        }
                    }
                }
            });
        } else {
            ConnectionRabbitHttp.mirroringEndpoint(283);
            sessions.saveFlagQuestion(false);
            llQuestions.setVisibility(View.GONE);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack("fragWealth", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("questions",true);
                    /*bundle.putString("questionData",questionData.toString());
                    bundle.putString("dataAnswers",dataAnswers.toString());
                    bundle.putString("dataFormArr",dataFormArr.toString());*/
                    bundle.putInt("idTNC",idTNC);
                    bundle.putString("labelserv", labelserv);
                    bundle.putInt("selectedQuestions",selectedQuestions);

                    Fragment fragments = new frag_wm_risk_profile();
                    fragments.setArguments(bundle);
                    getFragmentPage(fragments);
                }
            });

            if (isSessionZoom) {
                BaseMeetingActivity.showProgress(true);
            } else {
                DipsSwafoto.showProgress(true);
            }
            processGetTNC();
        }

    }

    private void processValidationActionForm() {
        int child = llFormBuild.getChildCount();

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                boolean checkEmpty = false;
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            String labelDataEl = idElement.getJSONObject(j).getString("label");
                            boolean requiredDataEl = true;
                            if (idEl == idDataEl) {

                                if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = rg.findViewById(selectedId);
                                                String results = rb.getText().toString();

                                                validateAnswer(results);
                                            }
                                        }
                                    });
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

    private void validationDataQuestions() {
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
                                    try {
                                        JSONArray chkDataAns = dataAnswers.getJSONObject(selectedQuestions).getJSONArray("data");
                                        for (int kl = 0; kl < chkDataAns.length(); kl++) {
                                            String answer = chkDataAns.getJSONObject(kl).getString("answer");
                                            String sortAnswer = chkDataAns.getJSONObject(kl).getString("sortAnswer");
                                            String results = questionData.getJSONObject(selectedQuestions).getString("answer");
                                            if (sortAnswer.equals(results)) {
                                                ed.setText(answer);
                                                break;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();

                                        String valRad = "";
                                        try {
                                            JSONArray chkDataAns = dataAnswers.getJSONObject(selectedQuestions).getJSONArray("data");
                                            for (int kl = 0; kl < chkDataAns.length(); kl++) {
                                                String answer = chkDataAns.getJSONObject(kl).getString("answer");
                                                String sortAnswer = chkDataAns.getJSONObject(kl).getString("sortAnswer");
                                                String results = questionData.getJSONObject(selectedQuestions).getString("answer");
                                                if (sortAnswer.equals(results)) {
                                                    valRad = answer;
                                                    break;
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (labelRad.equals(valRad)) {
                                            rb.setChecked(true);
                                            break;
                                        }
                                    }
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

    private void validateAnswer(String results) {
        try {
            JSONArray chkDataAns = dataAnswers.getJSONObject(selectedQuestions).getJSONArray("data");
            for (int kl = 0; kl < chkDataAns.length(); kl++) {
                String answer = chkDataAns.getJSONObject(kl).getString("answer");
                if (answer.equals(results)) {
                    String sortAnswer = chkDataAns.getJSONObject(kl).getString("sortAnswer");
                    String risk = chkDataAns.getJSONObject(kl).getString("risk");
                    JSONObject selectedAnswers = new JSONObject(questionData.getJSONObject(selectedQuestions).toString());
                    selectedAnswers.put("answer",sortAnswer);
                    selectedAnswers.put("type",risk);
                    questionData.put(selectedQuestions,selectedAnswers);

                    if (isSessionZoom) {
                        JSONObject quesAns = new JSONObject(mirrObj.getJSONObject(labelTrx).toString());
                        JSONObject reqFormMirroring = new JSONObject(quesAns.getJSONObject("pertanyaanprofilrisiko").toString());
                        reqFormMirroring.put("sortAnswer",sortAnswer);
                        quesAns.put("pertanyaanprofilrisiko",reqFormMirroring);
                        mirrObj.put(labelTrx, quesAns);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    }

    private void processCreateRiskProfile() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",sessions.getKEY_IdDips());
            jsons.put("nik",nik);
            jsons.put("questionData",questionData);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().CreateRiskProfile(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                            if (dataBody.getJSONObject("data").has("riskProfile")) {
                                String riskProfile = dataBody.getJSONObject("data").getString("riskProfile");
                                        
                                Bundle bundle = new Bundle();
                                bundle.putInt("idGenerateForm",66);
                                bundle.putInt("idTNC",22);
                                bundle.putString("labelserv", labelserv);
                                bundle.putBoolean("riskprofile", true);
                                //bundle.putString("valueRiskProfile", riskProfile);
                                bundle.putString("questionData",questionData.toString());
                                bundle.putString("dataAnswers",dataAnswers.toString());
                                bundle.putString("dataFormArr",dataFormArr.toString());

                                ConnectionRabbitHttp.mirroringEndpoint(281);

                                Fragment fragment = new frag_wealth_management();
                                fragment.setArguments(bundle);
                                getFragmentPage(fragment);
                            }
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

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void processGetQuestion() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetQuestion(sessions.getLANG(),authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    try {
                        JSONObject dataBody = new JSONObject(response.body().toString());
                        dataQuestion = dataBody.getJSONArray("data");
                        int noBegin = selectedQuestions + 1;
                        String noBeginStr = String.valueOf(noBegin);
                        tvNoBegin.setText(noBeginStr);
                        String noEnd = String.valueOf(dataQuestion.length());

                        tvNoEnd.setText(noEnd);
                        double diff = (double) dataQuestion.length() / noBegin;
                        int valProg = (int) Math.ceil((double)100 / diff);
                        progressBarline.setProgress(valProg);
                        progressBarline.setProgressTintList(mContext.getResources().getColorStateList(R.color.zm_button));

                        sessions.saveFlagQuestion(true);
                        int loop = 0;
                        parsingQuestions(loop);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
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
    }

    private void parsingQuestions(int i) {
        try {
            JSONObject dataObjQuote = new JSONObject();
            JSONObject dataElementForm = new JSONObject();

            String question = "";
            if (dataQuestion.getJSONObject(i).has("question")) {
                question = dataQuestion.getJSONObject(i).getString("question");
            }

            JSONObject dataCompObj = new JSONObject();
            dataCompObj.put("name", "RadioButton");
            dataCompObj.put("placeholder", new JSONObject("{\n" +
                    "\"placeholderIdn\":\"" + question + "\",\n" +
                    "\"placeholderEng\":\"" + question + "\"\n" +
                    "}"));
            dataCompObj.put("label", new JSONObject("{\n" +
                    "\"labelIdn\":\"" + question + "\",\n" +
                    "\"labelEng\":\"" + question + "\"\n" +
                    "}"));
            dataCompObj.put("type", "radio");
            dataCompObj.put("tagHTML", "input");

            JSONArray dataCompArr = new JSONArray();
            dataCompArr.put(dataCompObj);

            dataElementForm.put("name", "Radio Button");
            dataElementForm.put("components", dataCompArr);
            dataFormArr.put(dataElementForm);

            if (dataQuestion.getJSONObject(i).has("sortQuestion")) {
                int sortQuestion = dataQuestion.getJSONObject(i).getInt("sortQuestion");
                dataObjQuote.put("questionNo", sortQuestion);
                dataObjQuote.put("answer", "");
                dataObjQuote.put("type", "");
                questionData.put(i, dataObjQuote);
            }
            if (dataQuestion.getJSONObject(i).has("id")) {
                String questionId = dataQuestion.getJSONObject(i).getString("id");
                processGetAnswers(i, questionId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processGetAnswers(int indexs, String questionId) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetAnswers(sessions.getLANG(),questionId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    try {
                        JSONObject dataBody = new JSONObject(response.body().toString());
                        JSONArray dataBodyAnswers = dataBody.getJSONArray("data");
                        dataAnswers.put(indexs,new JSONObject("{\n" +
                                "\"data\":"+dataBodyAnswers+"}"));

                        JSONObject dataElementForm = new JSONObject(dataFormArr.getJSONObject(indexs).toString());

                        JSONArray compEl = new JSONArray(dataFormArr.getJSONObject(indexs).getJSONArray("components").toString());

                        for (int i = 0; i < dataBodyAnswers.length(); i++) {
                            String sortAnswer = "";
                            String risk = "";
                            String answer = "";
                            if (dataBodyAnswers.getJSONObject(i).has("sortAnswer")) {
                                sortAnswer = dataBodyAnswers.getJSONObject(i).getString("sortAnswer");
                            }
                            if (dataBodyAnswers.getJSONObject(i).has("risk")) {
                                risk = dataBodyAnswers.getJSONObject(i).getString("risk");
                            }
                            if (dataBodyAnswers.getJSONObject(i).has("answer")) {
                                answer = dataBodyAnswers.getJSONObject(i).getString("answer");
                            }

                            if (i == 0) {
                                JSONObject dataCompObj = new JSONObject(compEl.getJSONObject(0).toString());
                                dataCompObj.put("placeholder",new JSONObject("{\n" +
                                        "\"placeholderIdn\":\""+answer+"\",\n" +
                                        "\"placeholderEng\":\""+answer+"\"\n" +
                                        "}"));
                                compEl.put(0,dataCompObj);
                            } else {
                                JSONObject dataCompObj = new JSONObject();
                                dataCompObj.put("name","RadioButton");
                                dataCompObj.put("placeholder",new JSONObject("{\n" +
                                        "\"placeholderIdn\":\""+answer+"\",\n" +
                                        "\"placeholderEng\":\""+answer+"\"\n" +
                                        "}"));
                                dataCompObj.put("label",new JSONObject("{\n" +
                                        "\"labelIdn\":\"\",\n" +
                                        "\"labelEng\":\"\"\n" +
                                        "}"));
                                dataCompObj.put("type","radio");
                                dataCompObj.put("tagHTML","input");

                                compEl.put(dataCompObj);
                            }

                        }

                        dataElementForm.put("components",compEl);
                        dataFormArr.put(indexs,dataElementForm);
                        if (indexs == dataQuestion.length()-1) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }

                            llFormBuild.removeAllViewsInLayout();
                            llFormBuild.setVisibility(View.VISIBLE);

                            JSONObject formGenerateSelect = dataFormArr.getJSONObject(selectedQuestions);
                            JSONArray formGenerateArr = new JSONArray();
                            formGenerateArr.put(formGenerateSelect);

                            if (isSessionZoom) {
                                int questionNo = questionData.getJSONObject(selectedQuestions).getInt("questionNo");
                                String answer = questionData.getJSONObject(selectedQuestions).getString("answer");
                                JSONObject reqFormMirroring = new JSONObject();
                                reqFormMirroring.put("sortQuestion",questionNo);
                                reqFormMirroring.put("sortAnswer",answer);
                                JSONObject quesAns = new JSONObject();
                                quesAns.put("pertanyaanprofilrisiko",reqFormMirroring);
                                mirrObj.put(labelTrx, quesAns);
                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                            }

                            new MyParserFormBuilder(mContext, formGenerateArr.toString(), llFormBuild);
                            idElement = MyParserFormBuilder.getForm();
                            btnNext.setVisibility(View.VISIBLE);
                            processValidationActionForm();
                        }

                        int finalIndex = indexs + 1;
                        if (finalIndex < dataQuestion.length()) {
                            parsingQuestions(finalIndex);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
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
                    btnStart.setEnabled(true);
                    btnStart.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));

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

                        TextView tvBody = new TextView(mContext);

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

                        llFormBuild.addView(tvBody);

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