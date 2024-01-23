package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.HideSoftKeyboard;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_blokir_saldo extends Fragment {
    private ImageView btnBack;
    private NestedScrollView nestedScroll;
    private LinearLayout llFormBuild;
    private Button btnProcess;
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private int formId;
    private JSONArray idElement;
    JSONObject objEl = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        ConnectionRabbitHttp.init(mContext);

        if (getArguments() != null) {
            if (getArguments().containsKey("idGenerateForm")) {
                formId = getArguments().getInt("idGenerateForm");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_blokir_saldo, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        btnProcess = (Button) views.findViewById(R.id.btnProcess);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (isSessionZoom) {
//            BaseMeetingActivity.showProgress(true);
//        } else {
//            DipsSwafoto.showProgress(true);
//        }
        processGetForm(78);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionRabbitHttp.mirroringEndpoint(1000);
                getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

    }

    private void processGetForm(int formId) {
        Log.e("LOG","formId "+formId);
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

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

                                            } else {

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
                                                if (CompoName.contains("sumberdana") || (nameDataEl.contains("rekening") && nameDataEl.contains("penerima"))) {
                                                    processGetDynamicURLSumberDana(spin,urlPath);
                                                } else {
                                                    processGetDynamicURL(spin, urlPath, nameDataEl);
                                                }
                                            }
                                        }

                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                        break;
                                    } else if (rl.getChildAt(0) instanceof AutoCompleteTextView) {
                                        objEl.put(nameDataEl, "");
                                        AutoCompleteTextView autoText = (AutoCompleteTextView) rl.getChildAt(0);

                                        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                            }
                                        });
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    objEl.put(nameDataEl, "");

                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        }
                                    });
                                    autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {

                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    if (ll.findViewById(R.id.llCurrency) != null) {
                                        EditText tvContentCurr = ll.findViewById(R.id.tvContentCurr);
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
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                            }
                                        });

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
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);

                            String labelIdn = prodName+"\n"+accountNo+" - "+accountName;
                            sourceAcc[i] = labelIdn;

                            dataDropDown.add(new FormSpin(idData,accountType,labelIdn,labelIdn));
                        }
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDown);
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
                            dataDropDown.add(new FormSpin(idData,labelIdn,labelIdn,labelEng));

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
}
