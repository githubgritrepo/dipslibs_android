package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
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

public class frag_ibmb extends Fragment {

    private static final String TAG = "frag_ibmb";
    private final int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private LinearLayout TopBar;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private TextView tvTitle;
    private LinearLayout llFormBuild;
    private CheckBox checktnc;
    private Button btnNext;
    private int formId = 0;
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
    private JSONArray dataTrxArr;
    JSONObject objEl = new JSONObject();
    JSONObject mirrObj = new JSONObject();
    private String labelTrx = "";
    private TextView tvCurrency = null;
    private JSONObject objValCIF = null;
    private String nameBackStack = "";
    private JSONObject idFormObj;
    private String idDips;
    private JSONObject dataNasabahObj;
    private String no_handphone = "";
    private String transactionId = "";
    private String numberOTP = "";
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private PinView otp;
    private BroadcastReceiver smsReceiver = null;
    private LinearLayout ll_head;
    private TextView tvFotoKTP;
    private NestedScrollView nestedScroll;
    private int loopStatus = 0;
    private TextView tvAlert_user = null;
    private String password = "";
    private String confirmPass = "";
    private String getMpin = "";
    private String getConfirmMpin = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        idDips = sessions.getKEY_IdDips();

        ConnectionRabbitHttp.init(mContext);

        if (getArguments() != null) {
            if (getArguments().containsKey("idGenerateForm")) {
                formId = getArguments().getInt("idGenerateForm");
            }
        }

        String dataNasabah = sessions.getNasabah();
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                if (dataNasabahObj.has("noHp")) {
                    no_handphone = dataNasabahObj.getString("noHp");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        View views = inflater.inflate(R.layout.frag_ibmb, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);

        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);

        tvTitle = (TextView) views.findViewById(R.id.tvTitle);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        checktnc = (CheckBox) views.findViewById(R.id.checktnc);
        btnNext = (Button) views.findViewById(R.id.btnNext);

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

        tvtitleHead.setText(getString(R.string.IBMB_REGISTRATION));

        labelTrx = "ibmb";

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formId > 0) {
                    if (formId == 69) {
                        //nameBackStack = "FragIBMB";
                    } else if (formId == 65) {
                       // nameBackStack = "FragIBMB1";
                        nameBackStack = "FragIBMB";
                    } else if (formId == 68) {
                        nameBackStack = "FragIBMB2";
                    }
                    getActivity().getSupportFragmentManager().popBackStack(nameBackStack, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    getActivity().getSupportFragmentManager().popBackStack("FragCIF", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        if (no_handphone.isEmpty()) {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

            ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
            TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
            TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
            Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
            Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

            tvTitleDialog.setVisibility(View.GONE);

            imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_warning));
            tvBodyDialog.setText(R.string.no_mobile_number);

            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
            sweetAlertDialog.setCustomView(dialogView);
            sweetAlertDialog.hideConfirmButton();
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();

            btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            return;
        }

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }

        if (formId > 0) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));

            if (formId == 69) {
                tvTitle.setText(R.string.informasi_data_diri);
            } else {
                tvTitle.setText(R.string.registrasi_aplikasi);
            }

            if (formId == 68) {
                DialogLostConnect();
            }

            if (sessions.getCIF() != null) {
                String valDataCIF = sessions.getCIF();
                try {
                    objValCIF = new JSONObject(valDataCIF);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


            checktnc.setVisibility(View.GONE);
            btnNext.setText(getString(R.string.selanjutnya));
            processGetForm();
        } else {
            checktnc.setVisibility(View.VISIBLE);
            processGetTNC();
        }

        checktnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject mirr = new JSONObject();
                JSONObject tnc = new JSONObject();
                try {
                    if (checktnc.isChecked()){
                        btnNext.setEnabled(true);
                        tnc.put("tnc",true);
                        mirr.put("ibmb",tnc);
                        ConnectionRabbitHttp.mirroringKey(mirr);
                        btnNext.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    }
                    else {
                        btnNext.setEnabled(false);
                        tnc.put("tnc",false);
                        mirr.put("ibmb",tnc);
                        ConnectionRabbitHttp.mirroringKey(mirr);
                        btnNext.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_text_grey));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formId == 0) {
                    Bundle bundle = new Bundle();
                    Fragment fragment = new frag_ibmb();
                    ConnectionRabbitHttp.mirroringEndpoint(242);
                    bundle.putInt("idGenerateForm", 65);
                    fragment.setArguments(bundle);
                    getFragmentPageBackStack(fragment);
                } else if (formId > 0) {
                    checkElement();
                }
            }
        });
    }

    private void DialogLostConnect(){

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        String contentBody = "Demi keamanan data, Anda akan terputus sesaat dengan Agent pada sesi pengisian form Registrasi Aplikasi, dan otomatis terhubung kembali setelah menyelesaikan proses registrasi.";

        imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_info));
        tvBodyDialog.setText(contentBody);
        btnConfirmDialog.setText("OK");

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
    }

    private void checkElement() {
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

                                    if (nameDataEl.equals("katasandi")) {
                                        password = results;
                                    } else if (nameDataEl.contains("konfirmasi") && nameDataEl.contains("sandi")) {
                                        confirmPass = results;
                                    } else if (nameDataEl.equals("mpin")) {
                                        getMpin = results;
                                    } else if (nameDataEl.contains("konfirmasi") && nameDataEl.contains("mpin")) {
                                        getConfirmMpin = results;
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
                if (formId == 69) {
                    Bundle bundle = new Bundle();
                    Fragment fragment = new frag_ibmb();
                    ConnectionRabbitHttp.mirroringEndpoint(242);
                    bundle.putInt("idGenerateForm", 65);
                    fragment.setArguments(bundle);
                    getFragmentPageBackStack(fragment);
                }
                else if (formId == 65) {
                    if (!password.equals(confirmPass)) {
                        Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Bundle bundle = new Bundle();
                    Fragment fragment = new frag_ibmb();
                    ConnectionRabbitHttp.mirroringEndpoint(243);
                    bundle.putInt("idGenerateForm", 68);
                    fragment.setArguments(bundle);
                    getFragmentPageBackStack(fragment);
                } else if (formId == 68) {
                    if (!getMpin.equals(getConfirmMpin)) {
                        Toast.makeText(mContext, getString(R.string.mpin_not_match), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    processSendOTP();
                }
            }
        }
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
                        tvAlert_user = (TextView) llFormBuild.findViewById(R.id.alert_user);
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
                                                    if (nameDataEl.contains("sandi") || nameDataEl.contains("mpin")) {
                                                        JSONObject mirr = new JSONObject();
                                                        JSONObject reqFormMirr = new JSONObject(reqFormMirroring.toString());
                                                        reqFormMirr.put(nameDataEl,"*****");
                                                        mirr.put(labelTrx, reqFormMirr);
                                                        ConnectionRabbitHttp.mirroringKey(mirr);
                                                    } else {
                                                        mirrObj.put(labelTrx, reqFormMirroring);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                }
                                                if (nameDataEl.contains("pengguna") && tvAlert_user != null) {
                                                    if (!charSequence.toString().isEmpty()) {
                                                        if (ed.getText().toString().trim().length() < 7) {
                                                            tvAlert_user.setVisibility(View.VISIBLE);
                                                            tvAlert_user.setText("Minimal 7 karakter");
                                                        } else {
                                                            tvAlert_user.setVisibility(View.GONE);
                                                        }
                                                    } else {
                                                        tvAlert_user.setVisibility(View.GONE);
                                                    }
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
                                                            //investmentGoal = dataSpin.getCode();;
                                                        } else if (nameDataEl.contains("pendapatan")) {
                                                            //souceOfFund = dataSpin.getCode();;
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

    private JSONObject dataReqFormMirroring() {
        JSONObject dataFormObj = null;
        try {
            dataFormObj = new JSONObject(objEl.toString());
            dataFormObj.put("from","cif");
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

    private void processGetTNC() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getTNC(24,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataTnC = "";
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

    private void APISaveForm() {
        String valDataCIF = sessions.getCIF();
        JSONObject dataObjCIF = new JSONObject();
        try {
            JSONObject getObjValCIF = new JSONObject(valDataCIF);
            if (getObjValCIF.has("dataKtp")) {
                getObjValCIF.remove("dataKtp");
            }
            dataObjCIF.put("formCode","Opening Cif");
            dataObjCIF.put("idDips",idDips);
            dataObjCIF.put("payload",getObjValCIF);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjCIF.toString());
        Server.getAPIService().saveForm(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        String idForm = dataObj.getJSONObject("data").getString("idForm");
                        idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);
                        ConnectionRabbitHttp.mirroringKey(idFormObj);
                        processApprovalStatus();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    running = true;
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
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
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        nestedScroll.setVisibility(View.GONE);
        TopBar.setVisibility(View.GONE);
        ll_head.setVisibility(View.VISIBLE);

        String noHandphone = "089783434***";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"***");
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
                        loopStatus = 0;
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
                    APISaveForm();
                } else {
                    running = true;
                    if (response.code() == 400) {
                        ConnectionRabbitHttp.mirroringKey(idFormObj);
                        processApprovalStatus();
                    } else {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                        imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_failed));
                        textTitleOTP.setText(R.string.titleWrongOTP);
                        otp.setText("");
                    }
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

    private void processApprovalStatus() {
        String idForm = "";
        try {
            idForm = idFormObj.getString("idForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().ApprovalStatus(idForm,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                DipsSwafoto.tvLoading.setVisibility(View.GONE);
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (dataObj.getJSONObject("data").has("noCif")) {
                            String noCif = dataObj.getJSONObject("data").getString("noCif");
                            sessions.saveNoCIF(noCif);
                        }
                        if (dataObj.getJSONObject("data").has("branchCode")) {
                            String branchCode = dataObj.getJSONObject("data").getString("branchCode");
                            if (!branchCode.isEmpty()) {
                                String getDataNasabah = sessions.getNasabah();
                                if (!getDataNasabah.isEmpty()) {
                                    JSONObject dataNasabahObj = new JSONObject(getDataNasabah);
                                    dataNasabahObj.put("branchCode",branchCode);
                                    sessions.saveNasabah(dataNasabahObj.toString());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ConnectionRabbitHttp.mirroringEndpoint(13);
                    Bundle bundle = new Bundle();
                    bundle.putInt("formCode",13);
                    Fragment fragment = new frag_cif_resi();
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                } else {
                    if (loopStatus >= 99) {
                        Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                    BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                                    BaseMeetingActivity.showProgress(false);
                                } else {
                                    DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                    DipsSwafoto.tvLoading.setVisibility(View.GONE);
                                    DipsSwafoto.showProgress(false);
                                }
                            }
                        });
                    }
                    if (loopStatus < 99) {
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
                if (loopStatus >= 99) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                DipsSwafoto.tvLoading.setVisibility(View.GONE);
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });
                }
                if (loopStatus < 99) {
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

    private void getFragmentPage(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame2, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void getFragmentPageBackStack(Fragment fragment){
        if (formId == 0) {
            nameBackStack = "FragIBMB";
        } else if (formId == 69) {
            nameBackStack = "FragIBMB1";
        } else if (formId == 65) {
            nameBackStack = "FragIBMB2";
        } else if (formId == 68) {
            nameBackStack = "FragIBMB3";
        }
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack(nameBackStack)
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("FragIBMB")
                    .commit();
        }

    }
}