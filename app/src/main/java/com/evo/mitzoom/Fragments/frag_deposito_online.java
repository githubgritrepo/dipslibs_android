package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_deposito_online extends Fragment {

    private static final String TAG = "frag_deposito_online";
    
    public static int REQUESTCODE_GALLERY_QRCODE = 3;
    private final int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private NestedScrollView nestedScroll;
    private LinearLayout llFormBuild;
    private JSONArray idElement;
    private Button btnContinue;
    private final int flagData = 0; //0 = default, 1 = match data, 2 = nihil data
    private int formId;
    private int lasLenChar;
    private boolean backSpaceChar;
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private boolean flagStuckSpin = false;
    private String provinsi,kodepos="",kota_kabupaten,kecamatan,desa_kelurahan;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String picturePath;
    private String labelserv = "";
    private JSONObject dataObjTrx;
    private JSONArray dataTrxArr;
    private JSONArray dataTrxArrMirror;
    JSONObject objEl = new JSONObject();
    JSONObject mirrObj = new JSONObject();
    private static String nominalInput = "";
    private static String prefix = "";
    private static String jenisPenduduk = "";
    private TextView tvBungaValue = null;
    private TextView tvCurrency = null;

    private JSONArray idElementReq = null;
    private String labelTrx = "";
    private JSONObject reqForm = null;
    private String depoCode = "";
    private String flagAro = "";
    private String idService = "";
    private TextView tvAlertNominal = null;
    private String noRekSource = "";
    private long nominalRek = Long.valueOf(0);
    private long longNumCurrent = 0;
    private String getMinNominal = "";
    private String timePeriode = "";
    private TextView tvTH1;
    private TextView tvTH2;
    private TextView tvTH3;
    private TextView tvTH4;
    private TextView tvTR1TD;
    private TextView tvTR1TD1;
    private TextView tvTR1TD2;
    private TextView tvTR1TD3;
    private TextView tvTR1TD4;
    private TextView tvTR2TD;
    private TextView tvTR2TD1;
    private TextView tvTR2TD2;
    private TextView tvTR2TD3;
    private TextView tvTR2TD4;
    private TextView tvTR3TD;
    private TextView tvTR3TD1;
    private TextView tvTR3TD2;
    private TextView tvTR3TD3;
    private TextView tvTR3TD4;
    private TextView tvTR4TD;
    private TextView tvTR4TD1;
    private TextView tvTR4TD2;
    private TextView tvTR4TD3;
    private TextView tvTR4TD4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (isSessionZoom) {
            ConnectionRabbitHttp.init(mContext);
        }

        if (getArguments() != null) {
            if (getArguments().containsKey("idGenerateForm")) {
                formId = getArguments().getInt("idGenerateForm");
            }
            if (getArguments().containsKey("idService")) {
                idService = getArguments().getString("idService");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_deposito_online, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead); 
        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        
        btnContinue = (Button) views.findViewById(R.id.btnContinue);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labelTrx = "deposito";

        dataTrxArr = new JSONArray();
        dataTrxArrMirror = new JSONArray();

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }

        processGetForm();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ConnectionRabbitHttp.mirroringEndpoint(197);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideSoftKeyboard.hideSoftKeyboard(getActivity());
                processConfirmDeposit();
            }
        });
        
    }

    private void processConfirmDeposit() {
        ConfirmLabel();

        Bundle bundle = new Bundle();
        bundle.putString("idElement",idElementReq.toString());
        bundle.putString("dataValueForm",reqForm.toString());

        if (isSessionZoom) {
            ConnectionRabbitHttp.mirroringEndpoint(198);
        }
        Fragment fragments = new frag_deposito_confirm();
        fragments.setArguments(bundle);
        getFragmentPageBackStack(fragments);
    }

    private void processTableRateDepo() {
        try {
            JSONObject dataReq = new JSONObject();
            dataReq.put("chanel","IBMB");
            dataReq.put("currency",prefix);
            dataReq.put("resident","PENDUDUK");

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataReq.toString());

            Server.getAPIService().TableRateDepo(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                            JSONArray dataArr = dataBody.getJSONArray("data");
                            int th = 1;
                            int tr = 1;
                            int td = 1;
                            for (int i = 0; i < dataArr.length(); i++) {
                                String minNominal = dataArr.getJSONObject(i).getString("minNominal");
                                String maxNominal = dataArr.getJSONObject(i).getString("maxNominal");
                                String months = dataArr.getJSONObject(i).getString("months");
                                double ratePercent = dataArr.getJSONObject(i).getDouble("ratePercent");

                                String bilangRibu = "Rb";
                                for(int min=0; min < 3; min++)
                                {
                                    long getMinNom = Long.valueOf(minNominal) / 1000;
                                    String minNomS = String.valueOf(getMinNom);
                                    String chkRibu = minNomS;
                                    if (minNomS.length() > 3) {
                                        chkRibu = minNomS.substring(minNomS.length() - 3);
                                    }
                                    if (chkRibu.equals("000")) {
                                        minNominal = minNomS;
                                        if (min == 0) {
                                            bilangRibu = "Jt";
                                        } else if (min == 1) {
                                            bilangRibu = "M";
                                        }
                                    } else {
                                        minNominal = minNomS+" "+bilangRibu;
                                        break;
                                    }
                                }

                                bilangRibu = "Rb";
                                for(int min=0; min < 3; min++)
                                {
                                    long getMaxNom = Long.valueOf(maxNominal) / 1000;
                                    String maxNomS = String.valueOf(getMaxNom);
                                    String chkRibu = maxNomS;
                                    if (maxNomS.length() > 3) {
                                        chkRibu = maxNomS.substring(maxNomS.length() - 3);
                                    }
                                    if (chkRibu.equals("000")) {
                                        maxNominal = maxNomS;
                                        if (min == 0) {
                                            bilangRibu = "Jt";
                                        } else if (min == 1) {
                                            bilangRibu = "M";
                                        }
                                    } else {
                                        maxNominal = maxNomS+" "+bilangRibu;
                                        break;
                                    }
                                }

                                String nomDepo = minNominal + " <= " + maxNominal;

                                if (i == 0) {
                                    getMinNominal = dataArr.getJSONObject(i).getString("minNominal");
                                }

                                String rate = ratePercent + "%";

                                if (!months.isEmpty()) {
                                    if (i == 0) {
                                        tvTH1.setText(months);
                                        tvTR1TD.setText(nomDepo);
                                        tvTR1TD1.setText(rate);
                                    } else {
                                        String monthsPre = dataArr.getJSONObject(i-1).getString("months");
                                        if (!months.equals(monthsPre)) {
                                            th++;
                                            td++;
                                            tr = 1;
                                            if (th == 2) {
                                                tvTH2.setText(months);
                                            } else if (th == 3) {
                                                tvTH3.setText(months);
                                            } if (th == 4) {
                                                tvTH4.setText(months);
                                            }
                                        }
                                    }

                                    if (th == 1) {
                                        if (i == 1) {
                                            tvTR2TD.setText(nomDepo);
                                            tvTR2TD1.setText(rate);
                                        } else if (i == 2) {
                                            tvTR3TD.setText(nomDepo);
                                            tvTR3TD1.setText(rate);
                                        } else if (i == 3) {
                                            tvTR4TD.setText(nomDepo);
                                            tvTR4TD1.setText(rate);
                                        }
                                    }

                                    if (th == 2) {
                                        if (tr == 1) {
                                            tvTR1TD2.setText(rate);
                                        } else if (tr == 2) {
                                            tvTR2TD2.setText(rate);
                                        } else if (tr == 3) {
                                            tvTR3TD2.setText(rate);
                                        } else if (tr == 4) {
                                            tvTR4TD2.setText(rate);
                                        }
                                    }

                                    if (th == 3) {
                                        if (tr == 1) {
                                            tvTR1TD3.setText(rate);
                                        } else if (tr == 2) {
                                            tvTR2TD3.setText(rate);
                                        } else if (tr == 3) {
                                            tvTR3TD3.setText(rate);
                                        } else if (tr == 4) {
                                            tvTR4TD3.setText(rate);
                                        }
                                    }

                                    if (th == 4) {
                                        if (tr == 1) {
                                            tvTR1TD4.setText(rate);
                                        } else if (tr == 2) {
                                            tvTR2TD4.setText(rate);
                                        } else if (tr == 3) {
                                            tvTR3TD4.setText(rate);
                                        } else if (tr == 4) {
                                            tvTR4TD4.setText(rate);
                                        }
                                    }

                                    tr++;
                                }
                            }

                            if (th < 4) {
                                if (th == 3) {
                                    tvTH4.setVisibility(View.GONE);
                                    tvTR1TD4.setVisibility(View.GONE);
                                    tvTR2TD4.setVisibility(View.GONE);
                                    tvTR3TD4.setVisibility(View.GONE);
                                    tvTR4TD4.setVisibility(View.GONE);
                                } else if (th == 2) {
                                    tvTH4.setVisibility(View.GONE);
                                    tvTR1TD4.setVisibility(View.GONE);
                                    tvTR2TD4.setVisibility(View.GONE);
                                    tvTR3TD4.setVisibility(View.GONE);
                                    tvTR4TD4.setVisibility(View.GONE);

                                    tvTH3.setVisibility(View.GONE);
                                    tvTR1TD3.setVisibility(View.GONE);
                                    tvTR2TD3.setVisibility(View.GONE);
                                    tvTR3TD3.setVisibility(View.GONE);
                                    tvTR4TD3.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processDepoCode(JSONObject dataReq) {
        depoCode = "";
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataReq.toString());

        Server.getAPIService().DepoCode(requestBody, authAccess, exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject dataBody = new JSONObject(response.body().toString());
                        JSONObject dataObj = dataBody.getJSONObject("data");
                        if (dataObj.has("depoCode")) {
                            depoCode = dataObj.getString("depoCode");
                        }

                        if (!depoCode.isEmpty() && !nominalInput.isEmpty()) {
                            JSONObject dataObjDepo = new JSONObject();
                            dataObjDepo.put("depoCode",depoCode);
                            dataObjDepo.put("amount",Double.valueOf(nominalInput.replace(",","").replace(".","")));
                            processInterestPercentDepo(dataObjDepo);
                        } else {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
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
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void processInterestPercentDepo(JSONObject dataReq) {
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataReq.toString());

        Server.getAPIService().InterestPercentDepo(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    try {
                        JSONObject dataObj = new JSONObject(response.body().toString());
                        String dataPercent = dataObj.getString("data");
                        tvBungaValue.setText(dataPercent+"%");
                        reqForm = dataReqFormMirroring();
                        ConfirmLabel();
                        mirrObj.put(labelTrx, reqForm);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    tvBungaValue.setText("0%");
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
                tvBungaValue.setText("0%");
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
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
                        new MyParserFormBuilder(mContext, dataForm, llFormBuild, idService);
                        idElement = MyParserFormBuilder.getForm();

                        tvAlertNominal = (TextView) llFormBuild.findViewById(R.id.et_nominal);

                        int intAplhabet = randomId();

                        JSONObject dataObjEl = new JSONObject();
                        dataObjEl.put("id", intAplhabet);
                        dataObjEl.put("name", "bungadidapat");
                        dataObjEl.put("CompoName", "autocomplete");
                        dataObjEl.put("required", false);
                        dataObjEl.put("keyIndo", "bungadidapat");
                        dataObjEl.put("label", getString(R.string.bunga_didapat));
                        idElement.put(idElement.length(),dataObjEl);

                        TextView tvBunga = new TextView(mContext);
                        tvBunga.setText(getString(R.string.bunga_didapat));
                        tvBunga.setTextSize(14f);
                        tvBunga.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_text)));
                        llFormBuild.addView(tvBunga);

                        tvBungaValue = new TextView(mContext);
                        tvBungaValue.setId(intAplhabet);
                        tvBungaValue.setTextSize(13f);
                        tvBungaValue.setText("0%");
                        tvBungaValue.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_text)));
                        llFormBuild.addView(tvBungaValue);

                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        LinearLayout ln = (LinearLayout) inflater.inflate(R.layout.expand_suku_bunga, null, false);
                        llFormBuild.addView(ln);

                        tvTH1 = (TextView) llFormBuild.findViewById(R.id.tvTH1);
                        tvTH2 = (TextView) llFormBuild.findViewById(R.id.tvTH2);
                        tvTH3 = (TextView) llFormBuild.findViewById(R.id.tvTH3);
                        tvTH4 = (TextView) llFormBuild.findViewById(R.id.tvTH4);

                        tvTR1TD = (TextView) llFormBuild.findViewById(R.id.tvTR1TD);
                        tvTR1TD1 = (TextView) llFormBuild.findViewById(R.id.tvTR1TD1);
                        tvTR1TD2 = (TextView) llFormBuild.findViewById(R.id.tvTR1TD2);
                        tvTR1TD3 = (TextView) llFormBuild.findViewById(R.id.tvTR1TD3);
                        tvTR1TD4 = (TextView) llFormBuild.findViewById(R.id.tvTR1TD4);

                        tvTR2TD = (TextView) llFormBuild.findViewById(R.id.tvTR2TD);
                        tvTR2TD1 = (TextView) llFormBuild.findViewById(R.id.tvTR2TD1);
                        tvTR2TD2 = (TextView) llFormBuild.findViewById(R.id.tvTR2TD2);
                        tvTR2TD3 = (TextView) llFormBuild.findViewById(R.id.tvTR2TD3);
                        tvTR2TD4 = (TextView) llFormBuild.findViewById(R.id.tvTR2TD4);

                        tvTR3TD = (TextView) llFormBuild.findViewById(R.id.tvTR3TD);
                        tvTR3TD1 = (TextView) llFormBuild.findViewById(R.id.tvTR3TD1);
                        tvTR3TD2 = (TextView) llFormBuild.findViewById(R.id.tvTR3TD2);
                        tvTR3TD3 = (TextView) llFormBuild.findViewById(R.id.tvTR3TD3);
                        tvTR3TD4 = (TextView) llFormBuild.findViewById(R.id.tvTR3TD4);

                        tvTR4TD = (TextView) llFormBuild.findViewById(R.id.tvTR4TD);
                        tvTR4TD1 = (TextView) llFormBuild.findViewById(R.id.tvTR4TD1);
                        tvTR4TD2 = (TextView) llFormBuild.findViewById(R.id.tvTR4TD2);
                        tvTR4TD3 = (TextView) llFormBuild.findViewById(R.id.tvTR4TD3);
                        tvTR4TD4 = (TextView) llFormBuild.findViewById(R.id.tvTR4TD4);
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

    private void ConfirmLabel() {
        idElementReq = new JSONArray();
        reqForm = dataReqFormMirroring();
        int indeks = 0;
        for (int i = 0; i < idElement.length(); i++) {
            try {
                String nameDataEl = idElement.getJSONObject(i).getString("name");
                String labelEl = idElement.getJSONObject(i).getString("label");
                if (nameDataEl.contains("sumber") && nameDataEl.contains("dana")) {
                    idElementReq.put(indeks,idElement.getJSONObject(i));
                    indeks++;
                    JSONObject dataObjEl = new JSONObject();
                    int intAplhabet = randomId();
                    dataObjEl.put("id", intAplhabet);
                    dataObjEl.put("name", "jenisdeposito");
                    dataObjEl.put("CompoName", "autocomplete");
                    dataObjEl.put("required", false);
                    dataObjEl.put("keyIndo", "jenisdeposito");
                    dataObjEl.put("label", getString(R.string.jenis_deposito));
                    idElementReq.put(indeks,dataObjEl);
                    String depositType = "Rupiah";
                    if (!prefix.equals("IDR")) {
                        depositType = "Valuta Asing";
                    }
                    reqForm.put("jenisdeposito",depositType);
                    indeks++;
                } else if (nameDataEl.contains("nominal")) {
                    idElementReq.put(indeks,idElement.getJSONObject(i));
                    indeks++;
                } else if (nameDataEl.contains("jangka") && nameDataEl.contains("waktu")) {
                    idElementReq.put(indeks,idElement.getJSONObject(i));
                    indeks++;
                } else if (nameDataEl.contains("instruksi")) {
                    idElementReq.put(indeks,idElement.getJSONObject(i));
                    indeks++;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            int intAplhabet = randomId();
            JSONObject dataObjEl = new JSONObject();
            dataObjEl.put("id", intAplhabet);
            dataObjEl.put("name", "bungadidapat");
            dataObjEl.put("CompoName", "autocomplete");
            dataObjEl.put("required", false);
            dataObjEl.put("keyIndo", "bungadidapat");
            dataObjEl.put("label", getString(R.string.bunga_didapat));
            idElementReq.put(indeks,dataObjEl);
            reqForm.put("bungadidapat",tvBungaValue.getText().toString());
            reqForm.put("interest",tvBungaValue.getText().toString());
            indeks++;

            intAplhabet = randomId();
            dataObjEl = new JSONObject();
            dataObjEl.put("id", intAplhabet);
            dataObjEl.put("name", "tanggaltransaksi");
            dataObjEl.put("CompoName", "autocomplete");
            dataObjEl.put("required", false);
            dataObjEl.put("keyIndo", "tanggaltransaksi");
            dataObjEl.put("label", getString(R.string.tanggal_transaksi));
            idElementReq.put(indeks,dataObjEl);
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm",
                    Locale.getDefault()).format(new Date());
            reqForm.put("tanggaltransaksi",timeStamp);
            reqForm.put("transactionDate",timeStamp);
            reqForm.put("depocode",depoCode);
            reqForm.put("flagAro",flagAro);
            reqForm.put("tipeTransaksi",labelserv);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private static int randomId() {
        Random random=new Random();
        int dataInt = random.nextInt(99999999);
        return dataInt;
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
                                            if (flagData != 2) {
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
                                            if (flagData != 2) {
                                                int selectedId = rg.getCheckedRadioButtonId();
                                                if (selectedId > 0 || selectedId < -1) {
                                                    RadioButton rb = rg.findViewById(selectedId);
                                                    String results = rb.getText().toString();
                                                    processEnableComp(results);
                                                    try {
                                                        objEl.put(nameDataEl, results);
                                                        if (nameDataEl.contains("jenis") && nameDataEl.contains("penduduk")) {
                                                            jenisPenduduk = results;
                                                            if (!jenisPenduduk.isEmpty() && !prefix.isEmpty()) {
                                                                processTableRateDepo();
                                                            }
                                                        }
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
                                            if (flagData != 2) {
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
                                            if (flagData != 2) {
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
                                                if (flagData != 2) {
                                                    String accountType = "";
                                                    int beneficiaryCode = 0;
                                                    String swiftCode = "";
                                                    String cityCode = "";
                                                    String jenislayanan = "";
                                                    if (nameDataEl.contains("sumberdana")) {
                                                        tvAlertNominal.setVisibility(View.GONE);
                                                        FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                        String results = dataSpin.getName();
                                                        accountType = dataSpin.getCode();
                                                        if (results.indexOf("\n") > 0) {
                                                            String[] sp = results.split("\n");
                                                            String typeAccount = sp[0].trim();
                                                            String no_nama_Rek = sp[1].trim();
                                                            if (no_nama_Rek.indexOf("-") > 0) {
                                                                String[] sp2 = no_nama_Rek.split("-");
                                                                noRekSource = sp2[0].trim();
                                                            }
                                                            String valueNominalRek = sp[2].trim();
                                                            String getNominalRek = valueNominalRek.replaceAll("\\D+","");
                                                            nominalRek = Long.valueOf(getNominalRek) / 100;

                                                            if (!nominalInput.isEmpty()) {
                                                                if (longNumCurrent > nominalRek) {
                                                                    tvAlertNominal.setVisibility(View.VISIBLE);
                                                                    btnContinue.setEnabled(false);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                                } else {
                                                                    tvAlertNominal.setVisibility(View.GONE);
                                                                    btnContinue.setEnabled(true);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                                }
                                                            }
                                                        } else {
                                                            btnContinue.setEnabled(false);
                                                            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                        }
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
                                                            }  else if (nameDataEl.contains("mata") && nameDataEl.contains("uang")) {
                                                                prefix = dataSpin.getCode();
                                                                String currency = prefix;
                                                                if (prefix.equals("IDR")) {
                                                                    currency = "Rp";
                                                                }
                                                                tvCurrency.setText(currency);
                                                                if (!prefix.isEmpty()) {
                                                                    processTableRateDepo();
                                                                }
                                                            } else if (nameDataEl.contains("instruksi")) {
                                                                flagAro = dataSpin.getCode();
                                                            } else if (nameDataEl.contains("jangka") && nameDataEl.contains("waktu")) {
                                                                if (idData != 0) {
                                                                    timePeriode = results;

                                                                    String prefixLocal = prefix;
                                                                    if (prefixLocal.equals("Rp")) {
                                                                        prefixLocal = "IDR";
                                                                    }
                                                                    if (jenisPenduduk.isEmpty()) {
                                                                        jenisPenduduk = "PENDUDUK";
                                                                    }
                                                                    JSONObject dataObjDepo = new JSONObject();
                                                                    dataObjDepo.put("months", timePeriode);
                                                                    dataObjDepo.put("currency", prefixLocal);
                                                                    dataObjDepo.put("chanel", "IBMB");
                                                                    dataObjDepo.put("resident", jenisPenduduk);
                                                                    if (isSessionZoom) {
                                                                        BaseMeetingActivity.showProgress(true);
                                                                    } else {
                                                                        DipsSwafoto.showProgress(true);
                                                                    }
                                                                    processDepoCode(dataObjDepo);
                                                                }
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
                                            if (flagData != 2) {
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
                                        }
                                    });
                                    autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            if (flagData != 2) {
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
                                                if (flagData != 2) {
                                                    try {
                                                        objEl.put(nameDataEl, s);
                                                        nominalInput = String.valueOf(s);
                                                        if (!nominalInput.isEmpty()) {
                                                            String number = nominalInput.replaceAll("\\D+", "");
                                                            longNumCurrent = Long.valueOf(number);

                                                            if (longNumCurrent > nominalRek) {
                                                                String contexAlert = "";
                                                                if (idService.equals("191")) {
                                                                    contexAlert = mContext.getResources().getString(R.string.balance_insufficient);
                                                                } else {
                                                                    contexAlert = mContext.getResources().getString(R.string.alert_nominal);
                                                                }
                                                                tvAlertNominal.setText(contexAlert);
                                                                tvAlertNominal.setVisibility(View.VISIBLE);
                                                                btnContinue.setEnabled(false);
                                                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                            } else {
                                                                tvAlertNominal.setVisibility(View.GONE);
                                                                btnContinue.setEnabled(true);
                                                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                            }

                                                            if (!getMinNominal.isEmpty()) {
                                                                Long minNom = Long.valueOf(getMinNominal);
                                                                if (longNumCurrent >= minNom) {
                                                                    String prefixLocal = prefix;
                                                                    if (prefixLocal.equals("Rp")) {
                                                                        prefixLocal = "IDR";
                                                                    }
                                                                    if (jenisPenduduk.isEmpty()) {
                                                                        jenisPenduduk = "PENDUDUK";
                                                                    }
                                                                    JSONObject dataObjDepo = new JSONObject();
                                                                    dataObjDepo.put("months", timePeriode);
                                                                    dataObjDepo.put("currency", prefixLocal);
                                                                    dataObjDepo.put("chanel", "IBMB");
                                                                    dataObjDepo.put("resident", jenisPenduduk);
                                                                    if (isSessionZoom) {
                                                                        BaseMeetingActivity.showProgress(true);
                                                                    } else {
                                                                        DipsSwafoto.showProgress(true);
                                                                    }
                                                                    processDepoCode(dataObjDepo);
                                                                } else {
                                                                    NumberFormat nf = null;
                                                                    if (sessions.getLANG().equals("id")) {
                                                                        nf = NumberFormat.getInstance(new Locale("id", "ID"));
                                                                    } else {
                                                                        nf = NumberFormat.getInstance(new Locale("en", "US"));
                                                                    }
                                                                    String cleanString = getMinNominal.replaceAll("[$,.]", "");
                                                                    BigDecimal parsed = new BigDecimal(cleanString);
                                                                    String nfS = nf.format(parsed);

                                                                    String contexAlert = "Mininum Deposito "+nfS;
                                                                    tvAlertNominal.setText(contexAlert);
                                                                    tvAlertNominal.setVisibility(View.VISIBLE);
                                                                    btnContinue.setEnabled(false);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                                }
                                                            }
                                                        }
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

    private JSONObject dataReqFormMirroring() {
        JSONObject dataFormObj = null;
        try {
            dataFormObj = new JSONObject(objEl.toString());
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

                            if (acctCur.equals("IDR")) {
                                acctCur = "Rp.";
                            }

                            Double d = Double.valueOf(availBalance);
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
                        if (nameDataEl.contains("mata") && nameDataEl.contains("uang")) {
                            for (int ch = 0; ch < spin.getCount(); ch++) {
                                FormSpin dataSpin = (FormSpin) spin.getItemAtPosition(ch);
                                if (dataSpin.getCode().equals("IDR")) {
                                    spin.setSelection(ch);
                                    break;
                                }
                            }
                        } else if (nameDataEl.contains("jangka") && nameDataEl.contains("waktu")) {
                            for (int ch = 0; ch < spin.getCount(); ch++) {
                                if (spin.getItemAtPosition(ch).toString().trim().equals("1 Bulan") || spin.getItemAtPosition(ch).toString().trim().equals("1 bulan") || spin.getItemAtPosition(ch).toString().trim().equals("1 month") || spin.getItemAtPosition(ch).toString().trim().equals("1 Month")) {
                                    spin.setSelection(ch);
                                    break;
                                }
                            }
                        }
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

    private void chooseFromSD() {
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
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
                    .addToBackStack("myFragBack")
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("myFragBack")
                    .commit();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_GALLERY){
            sessions.saveMedia(0);
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
    
}