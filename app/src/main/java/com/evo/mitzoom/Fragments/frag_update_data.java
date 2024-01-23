package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
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
import com.evo.mitzoom.ui.DipsCameraSource;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
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

public class frag_update_data extends Fragment {

    private static final String TAG = "frag_update_data";
    private final int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private CheckBox checkBox;
    private SessionManager sessions;
    private String idDips;
    private boolean isSessionZoom;
    private LinearLayout TopBar;
    private ImageView btnBack;
    private TextView tvTitleService;
    private SwipeRefreshLayout swipe;
    private NestedScrollView nestedScroll;
    private Button btnUpdateData;
    private LinearLayout llFormBuild;
    private JSONArray idElement;
    private JSONObject dataObjTrx;
    private JSONArray dataTrxArr;
    private JSONArray dataTrxArrMirror;
    JSONObject objEl = new JSONObject();
    JSONObject mirrObj = new JSONObject();
    JSONObject payloadObj = null;
    private int lasLenChar;
    private boolean backSpaceChar;
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private boolean flagStuckSpin = false;
    private String provinsi = "";
    private String kodepos = "";
    private String kota_kabupaten = "";
    private String kecamatan = "";
    private String desa_kelurahan = "";
    private TextView tvSavedImg;
    private Button tvSkip, tvPrevious;
    private TextView tvSavedFile;
    private String picturePath;
    private int formId;
    private LinearLayout iconMainData;
    private LinearLayout iconWorks;
    private LinearLayout capture_eKTP;
    private int session = 1; // 1 = form informasi data utama, 2 = data pekerjaan, 3 = data keuangan
    JSONObject selfData = null;
    JSONObject workData = null;
    JSONObject financeData = null;
    private LinearLayout iconFinance;
    private Button btn_Lanjut;
    private final File mediaFilePhoto = null;
    private final File mediaFilePhotoCropSwafoto = null;
    private final String picturePathCrop = "";
    private String encodedImageCrop;
    private boolean ocrKTP = false;
    private String labelTrx = "";
    private String keyData = "";
    private JSONObject idFormObj;
    private JSONObject dataNasabahObj = null;
    private String no_handphone = "";
    private String nik = "";
    private String transactionId = "";
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private LinearLayout MenuBar;
    private LinearLayout ll_head;
    private TextView tvFotoKTP;
    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private PinView otp;
    private BroadcastReceiver smsReceiver = null;
    private String numberOTP = "";
    private boolean flagCheck = false;
    private boolean flagDataCore = false;
    private EditText edKodePos = null;
    private boolean actionSelected = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("idGenerateForm")) {
                formId = getArguments().getInt("idGenerateForm");
            }
            if (getArguments().containsKey("ocrKTP")) {
                ocrKTP = getArguments().getBoolean("ocrKTP");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_update_data, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvTitleService = (TextView) views.findViewById(R.id.tvTitleService);

        MenuBar = (LinearLayout) views.findViewById(R.id.MenuBar);

        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);
        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);
        btnUpdateData = (Button) views.findViewById(R.id.btnUpdateData);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);
        tvSkip = (Button) views.findViewById(R.id.tvSkip);
        tvPrevious = (Button) views.findViewById(R.id.tvPrevious);
        capture_eKTP = (LinearLayout) views.findViewById(R.id.capture_eKTP);
        iconMainData = (LinearLayout) views.findViewById(R.id.iconMainData);
        iconWorks = (LinearLayout) views.findViewById(R.id.iconWorks);
        iconFinance = (LinearLayout) views.findViewById(R.id.iconFinance);
        btn_Lanjut = (Button) views.findViewById(R.id.btn_Lanjut);
        checkBox = (CheckBox) views.findViewById(R.id.checkBox);

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

        labelTrx = "kycupdate";
        if (session == 1) {
            keyData = "datadiri";
        } else if (session == 2) {
            keyData = "pekerjaan";
        } else if (session == 3) {
            keyData = "keuangan";
        }

        iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
        iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
        iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
        tvSkip.setVisibility(View.VISIBLE);
        tvPrevious.setVisibility(View.GONE);
        dataTrxArr = new JSONArray();
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(false);
        btn_Lanjut.setEnabled(false);
        btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }

        if (ocrKTP) {
            processGetForm();
        } else {
            processCustGetDataCore();
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                if (session == 1) {
                    if (ocrKTP) {
                        processGetForm();
                    } else {
                        processCustGetDataCore();
                    }
                } else {
                    processGetForm();
                }
            }
        });
        btn_Lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    boolean flagNext = CheckValidationElement();
                    if (flagNext) {
                        try {
                            JSONObject reqFormMirroring = dataReqFormMirroring();
                            if (payloadObj != null) {
                                JSONObject dataPayloadObj = payloadObj.getJSONObject(labelTrx);
                                for(Iterator<String> iter = reqFormMirroring.keys(); iter.hasNext();) {
                                    if (iter.hasNext()) {
                                        String key = iter.next();
                                        if (key.equals("datatidaksesuai")) {
                                            dataPayloadObj.put(key, reqFormMirroring.getBoolean(key));
                                        } else {
                                            dataPayloadObj.put(key, reqFormMirroring.getJSONObject(key));
                                        }
                                    }
                                }
                                payloadObj.put(labelTrx, dataPayloadObj);
                            } else {
                                payloadObj = new JSONObject();
                                payloadObj.put(labelTrx, reqFormMirroring);
                            }

                            if (session == 1) {
                                String keyPernyataan = "alamatdomisili";
                                for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                                    String key = iter.next();
                                    String valKurung = "";
                                    int indx = key.indexOf("(");
                                    if (indx >= 0) {
                                        valKurung = key.substring(indx);
                                    }

                                    if (key.contains(keyPernyataan) || key.contains("domisili") || key.contains("pernyataan" + valKurung) ||
                                            (key.contains("alamat" + valKurung) && key.contains("berbeda" + valKurung))) {
                                        if (key.equals(keyPernyataan+valKurung)) {
                                            keyPernyataan = key;
                                        }
                                        break;
                                    }
                                }

                                boolean pernyataan = false;
                                if (objEl.has(keyPernyataan)) {
                                    pernyataan = objEl.getBoolean(keyPernyataan);
                                }

                                if (pernyataan) {
                                    ConnectionRabbitHttp.mirroringEndpoint(801);
                                    formId = 9;
                                    keyData = "datatidaksesuai";
                                    objEl = new JSONObject();
                                    checkBox.setChecked(false);
                                    flagCheck = false;
                                    btn_Lanjut.setEnabled(false);
                                    btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                    btn_Lanjut.setText(getResources().getString(R.string.btn_continue));
                                    iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                                    iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                                    iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                                    tvPrevious.setVisibility(View.GONE);
                                    capture_eKTP.setVisibility(View.GONE);
                                } else {
                                    ConnectionRabbitHttp.mirroringEndpoint(251);
                                    formId = 75; //menuju data pekerjaan
                                    session = 2;
                                    keyData = "pekerjaan";
                                    objEl = new JSONObject();
                                    checkBox.setChecked(false);
                                    flagCheck = false;
                                    btn_Lanjut.setEnabled(false);
                                    btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                    btn_Lanjut.setText(getResources().getString(R.string.btn_continue));
                                    iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                                    iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                                    iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                                    tvPrevious.setVisibility(View.VISIBLE);
                                    capture_eKTP.setVisibility(View.GONE);
                                }
                                processGetForm();
                            } else if (session == 2) {
                                ConnectionRabbitHttp.mirroringEndpoint(252);
                                formId = 76; //menuju data keuangan
                                session = 3;
                                keyData = "keuangan";
                                objEl = new JSONObject();
                                checkBox.setChecked(false);
                                flagCheck = false;
                                btn_Lanjut.setEnabled(false);
                                btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                btn_Lanjut.setText(getResources().getString(R.string.update));
                                iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                                iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                                iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                                checkBox.getBackground().setTint(getContext().getResources().getColor(R.color.zm_button));
                                tvSkip.setVisibility(View.GONE);
                                tvPrevious.setVisibility(View.VISIBLE);
                                capture_eKTP.setVisibility(View.GONE);
                                processGetForm();
                            } else if (session == 3) {
                                ((Activity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isSessionZoom) {
                                            BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                            BaseMeetingActivity.tvLoading.setVisibility(View.VISIBLE);
                                            BaseMeetingActivity.showProgress(true);
                                        } else {
                                            DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                            DipsSwafoto.tvLoading.setVisibility(View.VISIBLE);
                                            DipsSwafoto.showProgress(true);
                                        }
                                    }
                                });
                                //processSendOTP();
                                APISaveForm();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionRabbitHttp.mirroringEndpoint(15);
                getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagCheck = checkBox.isChecked();
                try {
                    JSONObject reqFormMirroring = dataReqFormMirroring();
                    mirrObj.put(labelTrx, reqFormMirroring);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (checkBox.isChecked()) {
                    checkBox.getBackground().setTint(getContext().getResources().getColor(R.color.zm_button));
                    btn_Lanjut.setEnabled(true);
                    btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                } else {
                    btn_Lanjut.setEnabled(false);
                    btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
            }
        });

        btnUpdateData.setAllCaps(false);
        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagDataCore || ocrKTP) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fragDest", "updateData");
                    bundle.putInt("formCode", 4); //4 Upload KTP, 22 Swafoto
                    Fragment fragment = new frag_swafoto();
                    fragment.setArguments(bundle);
                    getFragmentPageBackStack(fragment);
                }
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flagNext = CheckValidationElement();
                if (flagNext) {
                    try {
                        JSONObject reqFormMirroring = dataReqFormMirroring();
                        if (payloadObj != null) {
                            JSONObject dataPayloadObj = payloadObj.getJSONObject(labelTrx);
                            for(Iterator<String> iter = reqFormMirroring.keys(); iter.hasNext();) {
                                if (iter.hasNext()) {
                                    String key = iter.next();
                                    dataPayloadObj.put(key,reqFormMirroring.getJSONObject(key));
                                }
                            }
                            payloadObj.put(labelTrx, dataPayloadObj);
                        } else {
                            payloadObj = new JSONObject();
                            payloadObj.put(labelTrx, reqFormMirroring);
                        }

                        checkBox.setChecked(false);
                        flagCheck = false;
                        btn_Lanjut.setEnabled(false);
                        btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));

                        if (session == 1) {
                            String keyPernyataan = "alamatdomisili";
                            for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                                String key = iter.next();
                                String valKurung = "";
                                int indx = key.indexOf("(");
                                if (indx >= 0) {
                                    valKurung = key.substring(indx);
                                }

                                if (key.contains(keyPernyataan) || key.contains("domisili") || key.contains("pernyataan" + valKurung) ||
                                        (key.contains("alamat" + valKurung) && key.contains("berbeda" + valKurung))) {
                                    if (key.equals(keyPernyataan+valKurung)) {
                                        keyPernyataan = key;
                                    }
                                    break;
                                }
                            }

                            boolean pernyataan = false;
                            if (objEl.has(keyPernyataan)) {
                                pernyataan = objEl.getBoolean(keyPernyataan);
                            }

                            if (pernyataan) {
                                ConnectionRabbitHttp.mirroringEndpoint(801);
                                formId = 9;
                                keyData = "datatidaksesuai";
                                objEl = new JSONObject();
                                checkBox.setChecked(false);
                                flagCheck = false;
                                btn_Lanjut.setEnabled(false);
                                btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                btn_Lanjut.setText(getResources().getString(R.string.btn_continue));
                                iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                                iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                                iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                                tvPrevious.setVisibility(View.GONE);
                                capture_eKTP.setVisibility(View.GONE);
                            } else {
                                ConnectionRabbitHttp.mirroringEndpoint(251);
                                formId = 75; //menuju data pekerjaan
                                session = 2;
                                keyData = "pekerjaan";
                                objEl = new JSONObject();
                                checkBox.setChecked(false);
                                flagCheck = false;
                                btn_Lanjut.setEnabled(false);
                                btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                btn_Lanjut.setText(getResources().getString(R.string.btn_continue));
                                iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                                iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                                iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                                tvPrevious.setVisibility(View.VISIBLE);
                                capture_eKTP.setVisibility(View.GONE);
                            }
                            processGetForm();
                        } else if (session == 2) {
                            ConnectionRabbitHttp.mirroringEndpoint(252);
                            formId = 76; //menuju data keuangan
                            session = 3;
                            keyData = "keuangan";
                            objEl = new JSONObject();
                            btn_Lanjut.setText(getResources().getString(R.string.update));
                            iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                            iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                            iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                            checkBox.getBackground().setTint(getContext().getResources().getColor(R.color.zm_button));
                            tvSkip.setVisibility(View.GONE);
                            tvPrevious.setVisibility(View.VISIBLE);
                            capture_eKTP.setVisibility(View.GONE);
                        }
                        processGetForm();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        tvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flagNext = CheckValidationElement();
                if (flagNext) {
                    try {
                        JSONObject reqFormMirroring = dataReqFormMirroring();
                        if (payloadObj != null) {
                            JSONObject dataPayloadObj = payloadObj.getJSONObject(labelTrx);
                            for(Iterator<String> iter = reqFormMirroring.keys(); iter.hasNext();) {
                                if (iter.hasNext()) {
                                    String key = iter.next();
                                    dataPayloadObj.put(key,reqFormMirroring.getJSONObject(key));
                                }
                            }
                            payloadObj.put(labelTrx, dataPayloadObj);
                        } else {
                            payloadObj = new JSONObject();
                            payloadObj.put(labelTrx, reqFormMirroring);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    checkBox.setChecked(false);
                    flagCheck = false;
                    btn_Lanjut.setEnabled(false);
                    btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                    if (session == 2) {
                        ConnectionRabbitHttp.mirroringEndpoint(250);
                        formId = 77; //menuju data utama
                        session = 1;
                        keyData = "datadiri";
                        objEl = new JSONObject();
                        btn_Lanjut.setText(getResources().getString(R.string.btn_continue));
                        iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                        iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                        iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                        checkBox.getBackground().setTint(getContext().getResources().getColor(R.color.zm_button));
                        tvSkip.setVisibility(View.VISIBLE);
                        tvPrevious.setVisibility(View.GONE);
                        capture_eKTP.setVisibility(View.VISIBLE);
                    } else if (session == 3) {
                        ConnectionRabbitHttp.mirroringEndpoint(251);
                        formId = 75; //menuju data pekerjaan
                        session = 2;
                        keyData = "pekerjaan";
                        objEl = new JSONObject();
                        btn_Lanjut.setText(getResources().getString(R.string.btn_continue));
                        iconMainData.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif_success));
                        iconWorks.getBackground().setTint(getContext().getResources().getColor(R.color.bg_cif));
                        iconFinance.getBackground().setTint(getContext().getResources().getColor(R.color.btnFalse));
                        checkBox.getBackground().setTint(getContext().getResources().getColor(R.color.zm_button));
                        tvSkip.setVisibility(View.VISIBLE);
                        capture_eKTP.setVisibility(View.GONE);
                    }
                    processGetForm();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

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
    public void onDetach() {
        super.onDetach();
    }

    private void getKodePos(){
        if (!desa_kelurahan.isEmpty() && !kecamatan.isEmpty() && !kota_kabupaten.isEmpty() && !provinsi.isEmpty()) {
            JSONObject json = new JSONObject();
            try {
                json.put("kelurahan", desa_kelurahan);
                json.put("kecamatan", kecamatan);
                json.put("kabupaten", kota_kabupaten);
                json.put("provinsi", provinsi);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String authAccess = "Bearer " + sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
            ApiService API = Server.getAPIService();
            Call<JsonObject> call = API.getKodePos(requestBody, authAccess, exchangeToken);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        String dataS = response.body().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            kodepos = dataObj.getJSONObject("data").getString("kodepos");
                            if (dataObj.has("token")) {
                                String accessToken = dataObj.getString("token");
                                String exchangeToken = dataObj.getString("exchange");
                                sessions.saveAuthToken(accessToken);
                                sessions.saveExchangeToken(exchangeToken);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }

                    if (edKodePos != null) {
                        edKodePos.setText(kodepos);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }

    private void getFragmentPage(Fragment fragment) {
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
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack("FragUpdateData")
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("FragUpdateData")
                    .commit();
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

    private boolean CheckValidationElement() {
        boolean flagNext = true;
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
                            boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                            if (idEl == idDataEl) {

                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    String results = ed.getText().toString();
                                    if (requiredDataEl && results.isEmpty()) {
                                        Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                        checkEmpty = true;
                                    } else if (nameDataEl.contains("noponsel") && results.isEmpty()) {
                                        Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                        checkEmpty = true;
                                    } else if (nameDataEl.contains("email")) {
                                        boolean cekFlag = validationEmail(results);
                                        if (!cekFlag) {
                                            checkEmpty = true;
                                        }
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
                                            Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                            checkEmpty = true;
                                        }
                                        objEl.put(nameDataEl, results);
                                    } else if (requiredDataEl) {
                                        Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                            checkEmpty = true;
                                        }
                                        objEl.put(nameDataEl, results);
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        if (spin.getSelectedItem() != null) {
                                            String results = spin.getSelectedItem().toString();

                                            if (results.toLowerCase().contains("pilih")) {
                                                Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                        } else {
                                            checkEmpty = true;
                                        }
                                        break;
                                    }
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    String results = autoText.getText().toString();
                                    if (requiredDataEl && results.isEmpty()) {
                                        Toast.makeText(mContext, labelDataEl + " " + getString(R.string.alertRTGS), Toast.LENGTH_SHORT).show();
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
        }

        return flagNext;
    }

    private void processCustGetDataCore() {
        JSONObject dataReq = new JSONObject();
        try {
            dataReq.put("idDips",idDips);
            dataReq.put("noCif",sessions.getNoCIF()); //cif mba tari 21525677
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataReq.toString());
        Server.getAPIService().CustGetDataCore(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    flagDataCore = true;
                    try {
                        JSONObject dataBody = new JSONObject(response.body().toString());
                        JSONObject dataObj = dataBody.getJSONObject("data");

                        for(Iterator<String> iter = dataObj.keys(); iter.hasNext();) {
                            if (iter.hasNext()) {
                                String key = iter.next();
                                if (dataObj.has(key) && !dataObj.isNull(key)) {
                                    dataNasabahObj.put(key,dataObj.get(key));
                                }
                            }
                        }

                        sessions.saveNasabah(dataNasabahObj.toString());

                        processGetForm();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {

                    /*if (response.code() == 400) {
                        flagDataCore = false;
                        processCustGetDataCore();
                    } else {*/
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
                    //}
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
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId, authAccess, exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    nestedScroll.fullScroll(View.FOCUS_UP);
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

                        if (payloadObj != null) {
                            JSONObject dataPayloadObj = payloadObj.getJSONObject(labelTrx);
                            if (formId == 9) {
                                if (dataPayloadObj.has("datadiri")) {
                                    if (dataPayloadObj.getJSONObject("datadiri").has(keyData)) {
                                        processMatchDataFromeSession();
                                    } else {
                                        processMatchDataFromeKTP();
                                        if (ocrKTP) {
                                            processMatchData();
                                            processDataFromOCR();
                                        }
                                    }
                                }
                            } else {
                                if (dataPayloadObj.has(keyData)) {
                                    processMatchDataFromeSession();
                                } else {
                                    processMatchDataFromeKTP();
                                    if (ocrKTP) {
                                        processMatchData();
                                        processDataFromOCR();
                                    }
                                }
                            }
                        } else {
                            processMatchDataFromeKTP();
                            if (ocrKTP) {
                                processMatchData();
                                processDataFromOCR();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void APISaveForm() {
        JSONObject dataObjCIF = new JSONObject();
        try {
            dataObjCIF.put("formCode","KYC Update");
            dataObjCIF.put("idDips",idDips);
            dataObjCIF.put("payload",payloadObj.getJSONObject(labelTrx));
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
                        JSONObject objidForm = new JSONObject();
                        objidForm.put("idForm",idForm);
                        idFormObj.put(labelTrx,objidForm);
                        ConnectionRabbitHttp.mirroringKey(idFormObj);
                        processApprovalStatus();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    running = true;
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                running = true;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                });
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
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                });
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
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                });
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        TopBar.setVisibility(View.GONE);
        nestedScroll.setVisibility(View.GONE);
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
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                    BaseMeetingActivity.tvLoading.setVisibility(View.VISIBLE);
                                    BaseMeetingActivity.showProgress(true);
                                } else {
                                    DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                    DipsSwafoto.tvLoading.setVisibility(View.VISIBLE);
                                    DipsSwafoto.showProgress(true);
                                }
                            }
                        });
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

    private void resendOTP() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
            }
        });
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
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });
                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_failed));
                    textTitleOTP.setText(R.string.titleWrongOTP);
                    otp.setText("");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                running = true;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                });
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processApprovalStatus() {

        String idForm = "";
        try {
            idForm = idFormObj.getJSONObject(labelTrx).getString("idForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        String finalIdForm = idForm;
        Server.getAPIService().ApprovalStatus(idForm,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.rlprogress.setBackgroundColor(mContext.getResources().getColor(R.color.white_transparent));
                                BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.rlprogress.setBackgroundColor(mContext.getResources().getColor(R.color.white_transparent));
                                DipsSwafoto.tvLoading.setVisibility(View.GONE);
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    });

                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (dataObj.has("data")) {
                            if (dataObj.getJSONObject("data").has("status")) {
                                String status = dataObj.getJSONObject("data").getString("status");
                                if (status.equalsIgnoreCase("rejected")) {
                                    dialogFailedApproval();
                                    return;
                                }
                            }

                            dialogSuccessApproval();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processApprovalStatus();
                                }
                            });
                        }
                    },10000);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
        });
    }

    private void dialogSuccessApproval() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setText(R.string.success);
        tvBodyDialog.setText(R.string.msg_success_approve);

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                ConnectionRabbitHttp.mirroringEndpoint(14);
                getFragmentPage(new frag_portfolio_new());
            }
        });
    }

    private void dialogFailedApproval() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_warning));
        tvTitleDialog.setVisibility(View.GONE);
        tvBodyDialog.setText(R.string.msg_failed_approve);
        btnCancelDialog.setText("Batalkan");
        btnConfirmDialog.setText("Periksa Ulang");

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                Bundle bundle = new Bundle();
                bundle.putBoolean("ocrKTP",true);
                bundle.putInt("idGenerateForm",59);
                Fragment fragment = new frag_update_data();
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringEndpoint(15);
                }
                getFragmentPage(new frag_service_new());
            }
        });
    }

    private void processMatchData() {
        String dataOCR = sessions.getOCR();
        if (dataOCR != null) {
            try {
                JSONObject dataObjOCR = new JSONObject(dataOCR);
                String namaOCR = dataObjOCR.getString("nama");
                String provinsiOCR = dataObjOCR.getString("provinsi");
                String kabkotOCR = dataObjOCR.getString("kotakabupaten");
                String nikOCR = dataObjOCR.getString("nik");
                String ttlOCR = dataObjOCR.getString("ttl");
                String tempatlahirOCR = dataObjOCR.getString("tempatlahir");
                String jeniskelaminOCR = dataObjOCR.getString("jeniskelamin");
                String alamatOCR = dataObjOCR.getString("alamat");
                String rtrwOCR = dataObjOCR.getString("rtrw");
                String desaOCR = dataObjOCR.getString("desakelurahan");
                String kecamatanOCR = dataObjOCR.getString("kecamatan");
                String agamaOCR = dataObjOCR.getString("agama");
                String kawinOCR = dataObjOCR.getString("statusperkawinan");
                String wargaOCR = dataObjOCR.getString("kewarganegaraan");
                String namaIbuKandung = dataObjOCR.getString("namaibukandung");

                String tgllahirOCR = "";
                if (ttlOCR.contains(",")) {
                    String[] sp = ttlOCR.split(",");
                    tgllahirOCR = sp[1].trim();
                } else {
                    tgllahirOCR = ttlOCR;
                }

                String rtOCR = "";
                String rwOCR = "";
                if (rtrwOCR.contains("/")) {
                    String[] sp = rtrwOCR.split("/");
                    rtOCR = sp[0];
                    rwOCR = sp[1];
                }

                String address2 = rtOCR+rwOCR;

                String getDataNasabah = sessions.getNasabah();
                JSONObject dataEKTP = new JSONObject(getDataNasabah);
                dataEKTP.put("namaCust",namaOCR);
                dataEKTP.put("propinsi",provinsiOCR);
                dataEKTP.put("Prov1",provinsiOCR);
                dataEKTP.put("address5",kabkotOCR);
                dataEKTP.put("address4",kecamatanOCR);
                dataEKTP.put("address3",desaOCR);
                dataEKTP.put("nomorId",nikOCR);
                dataEKTP.put("tempatLahir",tempatlahirOCR);
                dataEKTP.put("tglLahir",tgllahirOCR);
                dataEKTP.put("jenisKelamin",jeniskelaminOCR);
                dataEKTP.put("address1",alamatOCR);
                dataEKTP.put("address2",address2);
                dataEKTP.put("agama",agamaOCR);
                dataEKTP.put("statusNikah",kawinOCR);
                String negaraAsal = wargaOCR;
                if (negaraAsal.equals("WNI")) {
                    negaraAsal = "ID";
                }
                dataEKTP.put("negaraAsal",negaraAsal);
                dataEKTP.put("namaIbu",namaIbuKandung);
                sessions.saveNasabah(dataEKTP.toString());

                for (Iterator<String> iter = objEl.keys(); iter.hasNext(); ) {
                    if (iter.hasNext()) {
                        String key = iter.next();
                        String valKurung = "";
                        int indx = key.indexOf("(");
                        if (indx >= 0) {
                            valKurung = key.substring(indx);
                        }
                        if (key.contains("nama") && key.contains("identitas")) {
                            objEl.put(key, namaOCR);
                        } else if (key.contains("ibu")) {
                            objEl.put(key, namaIbuKandung);
                        } else if (key.contains("kode") && key.contains("pos")) {
                            objEl.put(key, kodepos);
                        } else if (key.contains("provinsi")) {
                            objEl.put(key, provinsiOCR);
                            provinsi = provinsiOCR;
                        } else if (key.contains("kabupaten") || key.contains("kota")) {
                            objEl.put(key, kabkotOCR);
                            kota_kabupaten = kabkotOCR;
                        } else if (key.contains("noidentitas") || key.contains("nomoridentitas") || (key.contains("nomor") && key.contains("identitas"))) {
                            objEl.put(key, nikOCR);
                        } else if (key.contains("tempat") && key.contains("lahir")) {
                            objEl.put(key, tempatlahirOCR);
                        } else if (key.contains("tanggal") && key.contains("lahir")) {
                            objEl.put(key, tgllahirOCR);
                        } else if (key.contains("kelamin")) {
                            if (sessions.getLANG().equals("en")) {
                                if (jeniskelaminOCR.toLowerCase().contains("laki")) {
                                    objEl.put(key, "Male");
                                } else {
                                    objEl.put(key, "Female");
                                }
                            } else {
                                objEl.put(key, jeniskelaminOCR);
                            }
                        } else if (key.contains("alamat") && key.contains("identitas")) {
                            objEl.put(key, alamatOCR);
                        } else if (key.equals("rt" + valKurung)) {
                            objEl.put(key, rtOCR);
                        } else if (key.equals("rw" + valKurung)) {
                            objEl.put(key, rwOCR);
                        } else if (key.contains("kelurahan") || key.contains("desa")) {
                            objEl.put(key, desaOCR);
                            desa_kelurahan = desaOCR;
                        } else if (key.contains("kecamatan")) {
                            objEl.put(key, kecamatanOCR);
                            kecamatan = kecamatanOCR;
                        } else if (key.contains("agama")) {
                            objEl.put(key, agamaOCR);
                        } else if (key.contains("nikah") || key.contains("menikah")) {
                            objEl.put(key, kawinOCR);
                        } else if (key.contains("warganegara")) {
                            if (sessions.getLANG().equals("en")) {
                                objEl.put(key, "Indonesian citizens");
                            } else {
                                objEl.put(key, wargaOCR);
                            }
                        } else if (key.contains("jenisidentitas")) {
                            if (sessions.getLANG().equals("en")) {
                                objEl.put(key, "ID card");
                            } else {
                                objEl.put(key, "KTP");
                            }
                        } else if (key.contains("negara") && wargaOCR.equals("WNI")) {
                            objEl.put(key, "Indonesia");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void processDataFromOCR() {
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
                                    if (objEl.has(nameDataEl) && sessions.getOCR() != null) {
                                        String valEl = objEl.getString(nameDataEl);
                                        ed.setText(valEl);
                                    } else if (sessions.getCIF() != null) {
                                        String dataCIF = sessions.getCIF();
                                        JSONObject objCIF = new JSONObject(dataCIF);
                                        if (objCIF.has("dataktp")) {
                                            JSONObject objDataKTP = objCIF.getJSONObject("dataktp");
                                            int indx = nameDataEl.indexOf("(");
                                            if (indx >= 0) {
                                                nameDataEl = nameDataEl.substring(0,indx);
                                            }
                                            if (objDataKTP.has(nameDataEl)) {
                                                String valEl = objDataKTP.getString(nameDataEl);
                                                ed.setText(valEl);
                                            } else if (nameDataEl.contains("nama") && (nameDataEl.contains("identitas") || nameDataEl.contains("lengkap"))) {
                                                for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                    if (iter.hasNext()) {
                                                        String key = iter.next();
                                                        if (key.contains("nama") && (key.contains("identitas") || key.contains("lengkap"))) {
                                                            String valEl = objDataKTP.getString(key);
                                                            ed.setText(valEl);
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else if (nameDataEl.contains("alamat") && nameDataEl.contains("identitas")) {
                                                for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                    if (iter.hasNext()) {
                                                        String key = iter.next();
                                                        if (key.contains("alamat") && key.contains("identitas")) {
                                                            String valEl = objDataKTP.getString(key);
                                                            ed.setText(valEl);
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else if ((nameDataEl.contains("no") && nameDataEl.contains("identitas")) || (nameDataEl.contains("nomor") && nameDataEl.contains("identitas"))) {
                                                for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                    if (iter.hasNext()) {
                                                        String key = iter.next();
                                                        if (key.contains("nomor") && key.contains("identitas")) {
                                                            String valEl = objDataKTP.getString(key);
                                                            ed.setText(valEl);
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else if (nameDataEl.contains("kelurahan")) {
                                                for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                    if (iter.hasNext()) {
                                                        String key = iter.next();
                                                        if (key.contains("kelurahan")) {
                                                            String valEl = objDataKTP.getString(key);
                                                            ed.setText(valEl);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();
                                        String valEl = "";
                                        if (objEl.has(nameDataEl) && sessions.getOCR() != null) {
                                            valEl = objEl.getString(nameDataEl);
                                        }
                                        else if (sessions.getCIF() != null) {
                                            String dataCIF = sessions.getCIF();
                                            JSONObject objCIF = new JSONObject(dataCIF);
                                            if (objCIF.has("dataktp")) {
                                                JSONObject objDataKTP = objCIF.getJSONObject("dataktp");
                                                int indx = nameDataEl.indexOf("(");
                                                if (indx >= 0) {
                                                    nameDataEl = nameDataEl.substring(0,indx);
                                                }
                                                if (objDataKTP.has(nameDataEl)) {
                                                    valEl = objDataKTP.getString(nameDataEl);
                                                } else if (nameDataEl.contains("nama") && (nameDataEl.contains("identitas") || nameDataEl.contains("lengkap"))) {
                                                    for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                        if (iter.hasNext()) {
                                                            String key = iter.next();
                                                            if (key.contains("nama") && (key.contains("identitas") || key.contains("lengkap"))) {
                                                                valEl = objDataKTP.getString(key);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else if (nameDataEl.contains("alamat") && nameDataEl.contains("identitas")) {
                                                    for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                        if (iter.hasNext()) {
                                                            String key = iter.next();
                                                            if (key.contains("alamat") && key.contains("identitas")) {
                                                                valEl = objDataKTP.getString(key);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else if ((nameDataEl.contains("no") && nameDataEl.contains("identitas")) || (nameDataEl.contains("nomor") && nameDataEl.contains("identitas"))) {
                                                    for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                        if (iter.hasNext()) {
                                                            String key = iter.next();
                                                            if (key.contains("nomor") && key.contains("identitas")) {
                                                                valEl = objDataKTP.getString(key);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else if (nameDataEl.contains("kelurahan")) {
                                                    for(Iterator<String> iter = objDataKTP.keys(); iter.hasNext();) {
                                                        if (iter.hasNext()) {
                                                            String key = iter.next();
                                                            if (key.contains("kelurahan")) {
                                                                valEl = objDataKTP.getString(key);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (!valEl.isEmpty() && valEl != null ) {
                                            String valRad = valEl.toLowerCase();

                                            String valKurung = "";
                                            int indx = valEl.indexOf("(");
                                            if (indx >= 0) {
                                                valKurung = valEl.substring(indx);
                                            }

                                            if (valEl.toLowerCase().equals("kawin" + valKurung)) {
                                                valRad = "menikah";
                                            }
                                            if (labelRad.toLowerCase().equals(valRad)) {
                                                rb.setChecked(true);
                                                break;
                                            } else {

                                                if (valRad.contains("laki") && valRad.contains("-")) {
                                                    String[] sp = valRad.split("-");
                                                    valRad = sp[0] + " - " + sp[1];
                                                    if (labelRad.toLowerCase().equals(valRad)) {
                                                        rb.setChecked(true);
                                                        break;
                                                    }
                                                } else if ((valRad.contains("man") || valRad.contains("woman")) && (labelRad.toLowerCase().contains("kelamin") || labelRad.toLowerCase().contains("gender"))) {
                                                    rb.setChecked(true);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    String labelCheck = chk.getText().toString();
                                    if (objEl.has(nameDataEl)) {
                                        boolean valEl = objEl.getBoolean(nameDataEl);
                                        chk.setChecked(valEl);
                                    } else if (objEl.has(labelCheck)) {
                                        boolean valEl = objEl.getBoolean(labelCheck);
                                        chk.setChecked(valEl);
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    if (objEl.has(nameDataEl)) {
                                        String valEl = objEl.getString(nameDataEl);
                                        for (int ch = 0; ch < spin.getCount(); ch++) {
                                            if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                                spin.setSelection(ch);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        if (objEl.has(nameDataEl)) {
                                            String valEl = objEl.getString(nameDataEl);
                                            for (int ch = 0; ch < spin.getCount(); ch++) {
                                                if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                                    spin.setSelection(ch);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    if (objEl.has(nameDataEl)) {
                                        String valEl = objEl.getString(nameDataEl);
                                        autoText.setText(valEl);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getKodePos();
                    }
                });
            }
        },1000);
    }

    private void processMatchDataFromeKTP() {
        String getNasabah = sessions.getNasabah();
        JSONObject dataNasabah = null;
        try {
            dataNasabah = new JSONObject(getNasabah);
            int child = llFormBuild.getChildCount();

            if (child > 0 && idElement.length() > 0) {
                for (int i = 0; i < child; i++) {
                    int idEl = llFormBuild.getChildAt(i).getId();
                    if (idEl > 0 || idEl < -1) {
                        for (int j = 0; j < idElement.length(); j++) {
                            try {
                                int idDataEl = idElement.getJSONObject(j).getInt("id");
                                String nameDataEl = idElement.getJSONObject(j).getString("name");
                                int indx = nameDataEl.indexOf("(");
                                String newNameDataEl = nameDataEl;
                                if (indx >= 0) {
                                    newNameDataEl = nameDataEl.substring(0,indx);
                                }
                                if (idEl == idDataEl) {
                                    if (llFormBuild.getChildAt(i) instanceof EditText) {
                                        EditText ed = (EditText) llFormBuild.getChildAt(i);

                                        for(Iterator<String> iter = dataNasabah.keys(); iter.hasNext();) {
                                            if (iter.hasNext()) {
                                                String key = iter.next();
                                                String lowerKey = key.toLowerCase();
                                                if (newNameDataEl.equals(lowerKey)) {
                                                    String valEl = dataNasabah.getString(key);
                                                    if (valEl.equals("null") || valEl == null) {
                                                        valEl = "";
                                                    }
                                                    ed.setText(valEl);
                                                    objEl.put(nameDataEl, valEl);
                                                    break;
                                                }
                                            }
                                        }

                                        if (newNameDataEl.contains("nama") && newNameDataEl.contains("identitas")) {
                                            String valEl = "";
                                            if(dataNasabah.has("namaCust")) {
                                                valEl = dataNasabah.getString("namaCust");
                                            } else if(dataNasabah.has("namaLengkap")) {
                                                valEl = dataNasabah.getString("namaLengkap");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if ((newNameDataEl.contains("no") || newNameDataEl.contains("nomor")) && nameDataEl.contains("identitas")) {
                                            if(dataNasabah.has("nomorId")) {
                                                String valEl = dataNasabah.getString("nomorId");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if ((newNameDataEl.contains("no") || newNameDataEl.contains("nomor")) && (newNameDataEl.contains("ponsel") || newNameDataEl.contains("handphone"))) {
                                            if(dataNasabah.has("noHandphone")) {
                                                String valEl = dataNasabah.getString("noHandphone");
                                                if (valEl.equals("null") || valEl == null) {
                                                    valEl = "";
                                                }
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            } else if (dataNasabah.has("noHp")) {
                                                String valEl = dataNasabah.getString("noHp");
                                                if (valEl.equals("null") || valEl == null) {
                                                    valEl = "";
                                                }
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        }
                                        else if ((newNameDataEl.contains("no") || newNameDataEl.contains("nomor")) && newNameDataEl.contains("telepon")) {
                                            if(dataNasabah.has("noTelp")) {
                                                String valEl = dataNasabah.getString("noTelp");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        }
                                        else if (newNameDataEl.contains("tanggal") && newNameDataEl.contains("lahir")) {
                                            if (dataNasabah.has("tglLahir")) {
                                                String gettglLahir = dataNasabah.getString("tglLahir");
                                                if (gettglLahir.contains("-")) {
                                                    String[] sp = gettglLahir.split("-");
                                                    if (sp[0].trim().length() == 2) {
                                                        ed.setText(gettglLahir);
                                                        objEl.put(nameDataEl, gettglLahir);
                                                    } else if (sp[0].trim().length() == 4) {
                                                        String tahun = sp[0].trim();
                                                        String bln = sp[1].trim();
                                                        String tgl = sp[2].trim();
                                                        String valEl = tgl+"-"+bln+"-"+tahun;
                                                        ed.setText(valEl);
                                                        objEl.put(nameDataEl, valEl);
                                                    }
                                                } else if (gettglLahir.charAt(0) != '0') {
                                                    String tahun = gettglLahir.substring(0, 4);
                                                    String bln = gettglLahir.substring(4, 6);
                                                    String tgl = gettglLahir.substring(6, 8);
                                                    String valEl = tgl+"-"+bln+"-"+tahun;
                                                    ed.setText(valEl);
                                                    objEl.put(nameDataEl, valEl);
                                                } else {
                                                    String tahun = gettglLahir.substring(6, 8);
                                                    String bln = gettglLahir.substring(4, 6);
                                                    String tgl = gettglLahir.substring(0, 4);
                                                    String valEl = tgl+"-"+bln+"-"+tahun;
                                                    ed.setText(valEl);
                                                    objEl.put(nameDataEl, valEl);
                                                }
                                            }
                                        }
                                        else if (newNameDataEl.contains("alamat") && newNameDataEl.contains("usaha")) {
                                            String valEl = "";
                                            if (dataNasabah.has("empAddress1")) {
                                                String empAddress1 = dataNasabah.getString("empAddress1");
                                                valEl = empAddress1;
                                            }
                                            if (dataNasabah.has("empAddress2")) {
                                                String empAddress2 = dataNasabah.getString("empAddress2");
                                                valEl += " "+empAddress2;
                                            }
                                            valEl = valEl.trim();
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        }
                                        else if (newNameDataEl.contains("alamat") && newNameDataEl.contains("tinggal")) {
                                            String valEl = "";
                                            if (dataNasabah.has("domisili1")) {
                                                valEl = dataNasabah.getString("domisili1");
                                            }

                                            if(dataNasabah.has("domisili2")) {
                                                String domisili2 = dataNasabah.getString("domisili2");
                                                if (!domisili2.substring(0,2).equalsIgnoreCase("rt")) {
                                                    int indxRT = domisili2.toLowerCase().indexOf("rt");
                                                    if (indxRT > -1) {
                                                        String subdomisili2 = domisili2.substring(0, indxRT).trim();
                                                        valEl += " " + subdomisili2;
                                                    }
                                                }
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        }
                                        else if (newNameDataEl.contains("alamat") && newNameDataEl.contains("identitas")) {
                                            String valEl = "";
                                            if (dataNasabah.has("address1")) {
                                                valEl = dataNasabah.getString("address1");

                                                if(dataNasabah.has("address2")) {
                                                    String address2 = dataNasabah.getString("address2");
                                                    if (!address2.substring(0,2).equalsIgnoreCase("rt")) {
                                                        if(address2.matches("\\d+(?:\\.\\d+)?")) {
                                                        } else {
                                                            int indxRT = address2.toLowerCase().indexOf("rt");
                                                            if (indxRT > -1) {
                                                                String subaddress2 = address2.substring(0, indxRT).trim();
                                                                valEl += " " + subaddress2;
                                                            }
                                                        }
                                                    }
                                                }

                                            } else if(dataNasabah.has("alamat")) {
                                                valEl = dataNasabah.getString("alamat");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.equals("rt")) {
                                            String valEl = "";
                                            String keyGetRT = "address2";
                                            if (formId == 9) {
                                                keyGetRT = "domisili2";
                                            }
                                            if(dataNasabah.has("rt")) {
                                                valEl = dataNasabah.getString("rt");
                                            } else if (dataNasabah.has(keyGetRT)) {
                                                String address2 = dataNasabah.getString(keyGetRT);
                                                if (address2.length() == 6) {
                                                    valEl = address2.substring(0, 3);
                                                } else if (address2.length() > 6) {
                                                    if (address2.toLowerCase().contains("rt")) {
                                                        int indxRT = address2.toLowerCase().indexOf("rt");
                                                        String getRT = "";
                                                        if (indxRT > -1) {
                                                            if (address2.toLowerCase().contains("rw")) {
                                                                int indxRW = address2.toLowerCase().indexOf("rw");
                                                                getRT = address2.substring(indxRT + 2, indxRW);
                                                            } else {
                                                                getRT = address2.substring(indxRT + 2);
                                                            }
                                                            valEl = getRT.replace(".", "").replace(" ", "");
                                                        } else {
                                                            valEl = getRT;
                                                        }
                                                    }
                                                }
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.equals("rw")) {
                                            String valEl = "";
                                            String keyGetRW = "address2";
                                            if (formId == 9) {
                                                keyGetRW = "domisili2";
                                            }
                                            if(dataNasabah.has("rw")) {
                                                valEl = dataNasabah.getString("rw");
                                            } else if (dataNasabah.has(keyGetRW)) {
                                                String address2 = dataNasabah.getString(keyGetRW);
                                                if (address2.length() == 6) {
                                                    valEl = address2.substring(3);
                                                } else if (address2.length() > 6) {
                                                    if (address2.toLowerCase().contains("rw")) {
                                                        int indxRW = address2.toLowerCase().indexOf("rw");
                                                        String getRW = address2.substring(indxRW + 2);
                                                        valEl = getRW.replace(".", "").replace(" ", "");
                                                    }
                                                }
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("kelurahan")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kelurahan")) {
                                                valEl = dataNasabah.getString("kelurahan");
                                            } else if (dataNasabah.has("address3")) {
                                                valEl = dataNasabah.getString("address3");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("kecamatan")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kecamatan")) {
                                                valEl = dataNasabah.getString("kecamatan");
                                            } else if (dataNasabah.has("address4")) {
                                                valEl = dataNasabah.getString("address4");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("kabupaten")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kabupaten")) {
                                                valEl = dataNasabah.getString("kabupaten");
                                            } else if (dataNasabah.has("address5")) {
                                                valEl = dataNasabah.getString("address5");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("provinsi")) {
                                            String valEl = "";
                                            if(dataNasabah.has("propinsi")) {
                                                valEl = dataNasabah.getString("propinsi");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("kodepos")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kodePos")) {
                                                valEl = dataNasabah.getString("kodePos");
                                            } else if (dataNasabah.has("zipCode")) {
                                                valEl = dataNasabah.getString("zipCode");
                                            }
                                            if (!valEl.isEmpty()) {
                                                int intZipCode = Integer.parseInt(valEl);
                                                ed.setText(String.valueOf(intZipCode));
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("jumlah") && newNameDataEl.contains("anak")) {
                                            if(dataNasabah.has("jumAnak")) {
                                                String valEl = dataNasabah.getString("jumAnak");
                                                if (!valEl.isEmpty()) {
                                                    int intvalEl = Integer.parseInt(valEl);
                                                    ed.setText(String.valueOf(intvalEl));
                                                    objEl.put(nameDataEl, valEl);
                                                }
                                            }
                                        } else if (newNameDataEl.contains("jumlah") && newNameDataEl.contains("istri")) {
                                            if(dataNasabah.has("jumIstri")) {
                                                String valEl = dataNasabah.getString("jumIstri");
                                                if (!valEl.isEmpty()) {
                                                    int intvalEl = Integer.parseInt(valEl);
                                                    ed.setText(String.valueOf(intvalEl));
                                                    objEl.put(nameDataEl, valEl);
                                                }
                                            }
                                        } else if (newNameDataEl.contains("jumlah") && newNameDataEl.contains("tanggung")) {
                                            if(dataNasabah.has("jumTanggung")) {
                                                String valEl = dataNasabah.getString("jumTanggung");
                                                if (!valEl.isEmpty()) {
                                                    int intvalEl = Integer.parseInt(valEl);
                                                    ed.setText(String.valueOf(intvalEl));
                                                    objEl.put(nameDataEl, valEl);
                                                }
                                            }
                                        } else if (newNameDataEl.contains("kelamin")) {
                                            String valEl = "";
                                            if(dataNasabah.has("jenisKelamin")) {
                                                valEl = dataNasabah.getString("jenisKelamin");
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("agama")) {
                                            if (dataNasabah.has("agama")) {
                                                String valEl = dataNasabah.getString("agama");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("kawin")) {
                                            if (dataNasabah.has("statusKawin")) {
                                                String valEl = dataNasabah.getString("statusKawin");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("npwp")) {
                                            if (dataNasabah.has("npwp")) {
                                                String valEl = dataNasabah.getString("npwp");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("nama") && newNameDataEl.contains("ibu")) {
                                            if (dataNasabah.has("namaIbu")) {
                                                String valEl = dataNasabah.getString("namaIbu");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("nomor") && newNameDataEl.contains("identitas")) {
                                            if (dataNasabah.has("nik")) {
                                                String valEl = dataNasabah.getString("nik");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("jenis") && newNameDataEl.contains("identitas")) {
                                            ed.setText("KTP");
                                            objEl.put(nameDataEl, "KTP");
                                        } else if (newNameDataEl.contains("tanggal") && newNameDataEl.contains("berakhir")) {
                                            ed.setText("Seumur Hidup");
                                            objEl.put(nameDataEl, "Seumur Hidup");
                                        } else if (newNameDataEl.contains("warganegara")) {
                                            if (dataNasabah.has("negaraAsal")) {
                                                String negaraAsal = dataNasabah.getString("negaraAsal").replace("+","");
                                                String warganegara = "WNI";
                                                if (!negaraAsal.equals("ID") && !negaraAsal.equals("62")) {
                                                    warganegara = "WNA";
                                                }
                                                ed.setText(warganegara);
                                                objEl.put(nameDataEl, warganegara);
                                            }
                                        } else if (newNameDataEl.contains("negara")) {
                                            if (dataNasabah.has("negaraAsal")) {
                                                String negaraAsal = dataNasabah.getString("negaraAsal").replace("+","");
                                                String negara = "";
                                                if (negaraAsal.equals("ID") || negaraAsal.equals("62")) {
                                                    negara = "Indonesia";
                                                } else if (negaraAsal.equals("US")) {
                                                    negara = "United States of America";
                                                } else if (negaraAsal.equals("CN")) {
                                                    negara = "China";
                                                } else {
                                                    negara = "-";
                                                }
                                                ed.setText(negara);
                                                objEl.put(nameDataEl, negara);
                                            }
                                        } else if (newNameDataEl.contains("tanggalterbit")) {
                                            String valEl = dataNasabah.getString("tanggalTerbit");
                                            String getTgl = valEl;
                                            if (getTgl.contains("-")) {
                                                String[] sp = getTgl.split("-");
                                                if (sp[0].trim().length() == 2) {
                                                    valEl = getTgl;
                                                } else if (sp[0].trim().length() == 4) {
                                                    String tahun = sp[0].trim();
                                                    String bln = sp[1].trim();
                                                    String tgl = sp[2].trim();
                                                    valEl = tgl+"-"+bln+"-"+tahun;
                                                }
                                            } else if (getTgl.charAt(0) != '0') {
                                                String tahun = getTgl.substring(0, 4);
                                                String bln = getTgl.substring(4, 6);
                                                String tgl = getTgl.substring(6, 8);
                                                valEl = tgl+"-"+bln+"-"+tahun;
                                            } else {
                                                String tahun = getTgl.substring(4, 8);
                                                String bln = getTgl.substring(2, 4);
                                                String tgl = getTgl.substring(0, 2);
                                                valEl = tgl+"-"+bln+"-"+tahun;
                                            }
                                            ed.setText(valEl);
                                            objEl.put(nameDataEl, valEl);
                                        } else if (newNameDataEl.contains("nama") && newNameDataEl.contains("perusahaan")) {
                                            if (dataNasabah.has("namaUsaha")) {
                                                String valEl = dataNasabah.getString("namaUsaha");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        } else if (newNameDataEl.contains("kode") && (newNameDataEl.contains("telepon") || newNameDataEl.contains("telp"))) {
                                            if (dataNasabah.has("kodeTelp")) {
                                                String valEl = dataNasabah.getString("kodeTelp");
                                                ed.setText(valEl);
                                                objEl.put(nameDataEl, valEl);
                                            }
                                        }

                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                        mirrObj.put(labelTrx, reqFormMirroring);
                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                    } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                        if (newNameDataEl.contains("alamatdomisili")) {
                                            if (dataNasabah.has("domisili1")) {
                                                if (!dataNasabah.isNull("domisili1")) {
                                                    String domisili1 = dataNasabah.getString("domisili1");
                                                    if (!domisili1.isEmpty()) {
                                                        CheckBox checkBoxDom = (CheckBox) llFormBuild.getChildAt(i);
                                                        checkBoxDom.setChecked(true);
                                                        try {
                                                            objEl.put(nameDataEl, true);
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            mirrObj.put(labelTrx, reqFormMirroring);
                                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);

                                            String valEl = "";
                                            if (dataNasabah.has(newNameDataEl)) {
                                                valEl = dataNasabah.getString(newNameDataEl);
                                            } else if (newNameDataEl.contains("kelamin")) {
                                                valEl = dataNasabah.getString("jenisKelamin");
                                            } else if (newNameDataEl.contains("status") && newNameDataEl.contains("menikah")) {
                                                valEl = dataNasabah.getString("statusNikah");
                                            } else if (newNameDataEl.contains("pendidikan")) {
                                                valEl = dataNasabah.getString("pendAkhir");
                                            } else if (newNameDataEl.contains("cabang")) {
                                                valEl = dataNasabah.getString("branchCode");
                                            } else if (newNameDataEl.contains("kerja")) {
                                                valEl = dataNasabah.getString("jenisKerja");
                                            } else if (newNameDataEl.contains("bidang") && newNameDataEl.contains("usaha")) {
                                                valEl = dataNasabah.getString("badanUsaha");
                                            } else if (newNameDataEl.contains("jabatan")) {
                                                valEl = dataNasabah.getString("jobPosition");
                                            } else if (newNameDataEl.equals("sumberdana")) {
                                                valEl = dataNasabah.getString("sourceIncome");
                                            } else if (newNameDataEl.contains("tujuan") && newNameDataEl.contains("pengguna")) {
                                                valEl = dataNasabah.getString("sourceFund");
                                            } else if (newNameDataEl.contains("penghasilan")) {
                                                valEl = dataNasabah.getString("hasilBulan");
                                            } else if (newNameDataEl.contains("matauang")) {
                                                valEl = "IDR";
                                            }

                                            if (!valEl.isEmpty()) {
                                                for (int ch = 0; ch < spin.getCount(); ch++) {
                                                    if (valEl.length() <= 5) {
                                                        if (valEl.matches("\\d+(?:\\.\\d+)?")) {
                                                            FormSpin dataSpin = (FormSpin) spin.getItemAtPosition(ch);
                                                            String valueCode = dataSpin.getCode();
                                                            String results = dataSpin.getName();
                                                            if (valueCode.equals(valEl)) {
                                                                spin.setSelection(ch);

                                                                objEl.put(nameDataEl, results);
                                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                                break;
                                                            }
                                                        } else {
                                                            FormSpin dataSpin = (FormSpin) spin.getItemAtPosition(ch);
                                                            String results = dataSpin.getName();

                                                            if (spin.getItemAtPosition(ch).toString().equalsIgnoreCase(valEl)) {
                                                                spin.setSelection(ch);

                                                                objEl.put(nameDataEl, results);
                                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                                break;
                                                            } else if (valEl.equalsIgnoreCase("laki-laki") && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("male")) {
                                                                spin.setSelection(ch);

                                                                objEl.put(nameDataEl, results);
                                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                                break;
                                                            } else if ((valEl.equalsIgnoreCase("perempuan") || valEl.equalsIgnoreCase("wanita")) && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("female")) {
                                                                spin.setSelection(ch);

                                                                objEl.put(nameDataEl, results);
                                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                                break;
                                                            }
                                                        }
                                                    } else {
                                                        FormSpin dataSpin = (FormSpin) spin.getItemAtPosition(ch);
                                                        String results = dataSpin.getName();

                                                        if (spin.getItemAtPosition(ch).toString().equalsIgnoreCase(valEl)) {
                                                            spin.setSelection(ch);

                                                            objEl.put(nameDataEl, results);
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            mirrObj.put(labelTrx, reqFormMirroring);
                                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            break;
                                                        } else if (valEl.equalsIgnoreCase("laki-laki") && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("male")) {
                                                            spin.setSelection(ch);

                                                            objEl.put(nameDataEl, results);
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            mirrObj.put(labelTrx, reqFormMirroring);
                                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            break;
                                                        } else if ((valEl.equalsIgnoreCase("perempuan") || valEl.equalsIgnoreCase("wanita")) && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("female")) {
                                                            spin.setSelection(ch);

                                                            objEl.put(nameDataEl, results);
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            mirrObj.put(labelTrx, reqFormMirroring);
                                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                        } else if (rl.getChildAt(0) instanceof AutoCompleteTextView) {
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) rl.getChildAt(0);
                                            if (newNameDataEl.contains("cabang")) {
                                                String valEl = dataNasabah.getString("branchCode");

                                                ListAdapter listA = autoText.getAdapter();
                                                if (listA == null) {
                                                    break;
                                                }
                                                if (listA.getCount() == 0) {
                                                    break;
                                                }

                                                ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                                                for (int ch = 0; ch < listA.getCount(); ch++) {
                                                    String getItem = listA.getItem(ch).toString();
                                                    if (listA.getCount() > 1) {
                                                        Object item = listA.getItem(ch);
                                                        if (item instanceof FormSpin) {
                                                            FormSpin dataSpin = (FormSpin) item;
                                                            int idData = dataSpin.getId();
                                                            String nameInd = dataSpin.getName();
                                                            String valCode = dataSpin.getCode();
                                                            String nameEng = dataSpin.getNameEng();
                                                            dataDropDown.add(new FormSpin(idData, valCode, nameInd, nameEng));

                                                            if (!valEl.isEmpty()) {
                                                                if (valEl.equals(valCode)) {
                                                                    autoText.setText(nameInd);
                                                                    objEl.put(nameDataEl, nameInd);
                                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                                }
                                                            }

                                                        }
                                                    }

                                                }

                                            }
                                        }
                                    } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                        LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                        if (ll.findViewById(R.id.llCurrency) != null) {
                                        } else {
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


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void processMatchDataFromeSession() {
        try {
            JSONObject objDataSession = null;
            if (formId == 9) {
                objDataSession = payloadObj.getJSONObject(labelTrx).getJSONObject("datadiri").getJSONObject(keyData);
            } else {
                objDataSession = payloadObj.getJSONObject(labelTrx).getJSONObject(keyData);
            }
            if (objDataSession.has("datasudahbenar")) {
                boolean chk = objDataSession.getBoolean("datasudahbenar");
                checkBox.setChecked(chk);
                flagCheck = chk;
                if (chk) {
                    btn_Lanjut.setEnabled(true);
                    btn_Lanjut.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                }
            }

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

                                        if (objDataSession.has(nameDataEl)) {
                                            String valEl = objDataSession.getString(nameDataEl);
                                            ed.setText(valEl);
                                        }

                                    } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                        if (objDataSession.has(nameDataEl)) {
                                            boolean b = objDataSession.getBoolean(nameDataEl);
                                            checkBox.setChecked(b);
                                        }

                                    } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);

                                            String valEl = "";
                                            if (objDataSession.has(nameDataEl)) {
                                                valEl = objDataSession.getString(nameDataEl);
                                            }

                                            if (!valEl.isEmpty()) {
                                                for (int ch = 0; ch < spin.getCount(); ch++) {
                                                    if (valEl.length() <= 5) {
                                                        if (valEl.matches("\\d+(?:\\.\\d+)?")) {
                                                            FormSpin dataSpin = (FormSpin) spin.getItemAtPosition(ch);
                                                            String valueCode = dataSpin.getCode();
                                                            if (valueCode.equals(valEl)) {
                                                                spin.setSelection(ch);
                                                                break;
                                                            }
                                                        } else {
                                                            if (spin.getItemAtPosition(ch).toString().equalsIgnoreCase(valEl)) {
                                                                spin.setSelection(ch);
                                                                break;
                                                            } else if (valEl.equalsIgnoreCase("laki-laki") && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("male")) {
                                                                spin.setSelection(ch);
                                                                break;
                                                            } else if ((valEl.equalsIgnoreCase("perempuan") || valEl.equalsIgnoreCase("wanita")) && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("female")) {
                                                                spin.setSelection(ch);
                                                                break;
                                                            }
                                                        }
                                                    } else {
                                                        if (spin.getItemAtPosition(ch).toString().equalsIgnoreCase(valEl)) {
                                                            spin.setSelection(ch);
                                                            break;
                                                        } else if (valEl.equalsIgnoreCase("laki-laki") && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("male")) {
                                                            spin.setSelection(ch);
                                                            break;
                                                        } else if ((valEl.equalsIgnoreCase("perempuan") || valEl.equalsIgnoreCase("wanita")) && spin.getItemAtPosition(ch).toString().equalsIgnoreCase("female")) {
                                                            spin.setSelection(ch);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                        } else if (rl.getChildAt(0) instanceof AutoCompleteTextView) {
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) rl.getChildAt(0);
                                            if (objDataSession.has(nameDataEl)) {
                                                String valEl = objDataSession.getString(nameDataEl);
                                                ListAdapter listA = autoText.getAdapter();
                                                if (listA == null) {
                                                    break;
                                                }
                                                if (listA.getCount() == 0) {
                                                    break;
                                                }

                                                ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                                                for (int ch = 0; ch < listA.getCount(); ch++) {
                                                    String getItem = listA.getItem(ch).toString();
                                                    if (listA.getCount() > 1) {
                                                        Object item = listA.getItem(ch);
                                                        if (item instanceof FormSpin) {
                                                            FormSpin dataSpin = (FormSpin) item;
                                                            int idData = dataSpin.getId();
                                                            String nameInd = dataSpin.getName();
                                                            String valCode = dataSpin.getCode();
                                                            String nameEng = dataSpin.getNameEng();
                                                            dataDropDown.add(new FormSpin(idData, valCode, nameInd, nameEng));

                                                            if (!valEl.isEmpty()) {
                                                                if (valEl.equals(valCode) || valEl.equals(nameInd)) {
                                                                    autoText.setText(nameInd);
                                                                    objEl.put(nameDataEl, nameInd);
                                                                    String getDataNasabah = sessions.getNasabah();
                                                                    JSONObject dataEKTP = new JSONObject(getDataNasabah);
                                                                    dataEKTP.put(nameDataEl,nameInd);
                                                                    sessions.saveNasabah(dataEKTP.toString());

                                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                                }
                                                            }

                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                        LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                        if (ll.findViewById(R.id.llCurrency) != null) {
                                        } else {
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
                                    if (nameDataEl.contains("kode") && nameDataEl.contains("pos")){
                                        edKodePos = ed;
                                    }

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
                                            if (nameDataEl.equals("npwp" + finalValKurung)) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (nameDataEl.equals("npwp" + finalValKurung)) {
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
                                                processEnableComp(results);
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
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
                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
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
                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
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
                                                actionSelected = false;
                                                String accountType = "";
                                                int beneficiaryCode = 0;
                                                String swiftCode = "";
                                                String cityCode = "";
                                                String jenislayanan = "";
                                                String itemSelect = spin.getSelectedItem().toString();
                                                if (spin.getSelectedItemId() > 0) {
                                                    actionSelected = true;
                                                } else if (spin.getSelectedItemId() == 0 && (!itemSelect.toLowerCase().contains("pilih") && !itemSelect.toLowerCase().contains("choose"))) {
                                                    actionSelected = true;
                                                }

                                                if (nameDataEl.contains("sumberdana")) {
                                                    FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                    String results = dataSpin.getName();
                                                    accountType = dataSpin.getCode();
                                                    results = results.replaceAll("\n", " / ");
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

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                getKodePos();
                                                                            }
                                                                        });
                                                                    }
                                                                },1000);
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

                                                try {
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
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
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                                Object item = adapterView.getItemAtPosition(position);
                                                if (item instanceof FormSpin) {
                                                    FormSpin dataSpin = (FormSpin) item;
                                                    int idData = dataSpin.getId();
                                                    String results = dataSpin.getName();
                                                    String valCode = dataSpin.getCode();

                                                    try {
                                                        objEl.put(nameDataEl, results);
                                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                                        mirrObj.put(labelTrx, reqFormMirroring);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });

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
                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
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
                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                mirrObj.put(labelTrx, reqFormMirroring);
                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
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
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    mirrObj.put(labelTrx, reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
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
                                                            String[] mimetypes = {"application/pdf", "application/doc", "text/*"};

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
        JSONObject dataFormObj = new JSONObject();
        try {
            JSONObject dataFormObj2 = new JSONObject(objEl.toString());
            dataFormObj2.put("datasudahbenar",flagCheck);
            if (formId == 9) {
                if (payloadObj != null) {
                    JSONObject dataPayloadObj = payloadObj.getJSONObject(labelTrx);
                    if (dataPayloadObj.has("datadiri")) {
                        JSONObject dataDiri = new JSONObject(dataPayloadObj.getJSONObject("datadiri").toString());
                        dataDiri.put(keyData,dataFormObj2);
                        dataFormObj.put("datadiri",dataDiri);
                    }
                }
            } else {
                String lbpernyataan = "alamatdomisili";
                for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                    String key = iter.next();
                    String valKurung = "";
                    int indx = key.indexOf("(");
                    if (indx >= 0) {
                        valKurung = key.substring(indx);
                    }

                    if (key.contains(lbpernyataan) || key.contains("domisili") || key.contains("pernyataan" + valKurung) ||
                            (key.contains("alamat" + valKurung) && key.contains("berbeda" + valKurung))) {
                        if (key.equals(lbpernyataan+valKurung)) {
                            lbpernyataan = key;
                        }
                        break;
                    }
                }

                if (dataFormObj2.has(lbpernyataan)) {
                    boolean chk = dataFormObj2.getBoolean(lbpernyataan);
                    dataFormObj.put("datatidaksesuai", chk);
                }

                dataFormObj.put(keyData,dataFormObj2);
            }
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

    private void processGetDynamicURL(Spinner spin, String urlPath, String nameDataEl) {
        flagStuckSpin = false;
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrl(urlPath, authAccess, exchangeToken).enqueue(new Callback<JsonObject>() {
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

                            String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                            String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                            if (sessions.getLANG().equals("en")) {
                                labelIdn = labelEng;
                            }

                            valueCode = labelIdn;
                            if (dataArr.getJSONObject(i).has("valueCode")) {
                                valueCode = dataArr.getJSONObject(i).getString("valueCode");
                            } else if (dataArr.getJSONObject(i).has("kodeCabang")) {
                                valueCode = dataArr.getJSONObject(i).getString("kodeCabang");
                            } else if (dataArr.getJSONObject(i).has("swiftCode") && dataArr.getJSONObject(i).has("cityCode")) {
                                String swiftCode = dataArr.getJSONObject(i).getString("swiftCode");
                                String cityCode = dataArr.getJSONObject(i).getString("cityCode");
                                valueCode = swiftCode + " | " + cityCode;
                            } else {
                                valueCode = idSData;
                            }

                            if (dataArr.getJSONObject(i).has("beneficiaryCode")) {
                                String beneficiaryCode = dataArr.getJSONObject(i).getString("beneficiaryCode");
                                idData = Integer.parseInt(beneficiaryCode);
                            }

                            dataDropDown.add(new FormSpin(idData, valueCode, labelIdn, labelEng));
                            if (i == 0) {
                                if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                    String newNameDataEl = nameDataEl;
                                    if (nameDataEl.contains("(")) {
                                        int indxProv = nameDataEl.indexOf("(");
                                        newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                    }
                                    valSpinProv.put(newNameDataEl, idData);
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

                        if (!actionSelected) {
                            if (payloadObj != null) {
                                JSONObject dataPayloadObj = payloadObj.getJSONObject(labelTrx);
                                if (formId == 9) {
                                    if (dataPayloadObj.has("datadiri")) {
                                        if (dataPayloadObj.getJSONObject("datadiri").has(keyData)) {
                                            processMatchDataFromeSession();
                                        } else {
                                            processMatchDataFromeKTP();
                                            if (ocrKTP) {
                                                processMatchData();
                                                processDataFromOCR();
                                            }
                                        }
                                    }
                                } else {
                                    if (dataPayloadObj.has(keyData)) {
                                        processMatchDataFromeSession();
                                    } else {
                                        processMatchDataFromeKTP();
                                        if (ocrKTP) {
                                            processMatchData();
                                            processDataFromOCR();
                                        }
                                    }
                                }
                            } else {
                                processMatchDataFromeKTP();
                                if (ocrKTP) {
                                    processMatchData();
                                    processDataFromOCR();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, R.string.msg_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                            } else if ((nameDataEl.contains("kabupaten") || nameDataEl.contains("district") || nameDataEl.contains("kota") || nameDataEl.contains("city")) && (getnameDataEl.contains("kecamatan") || getnameDataEl.contains("subdistrict"))) {
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
                                    String urlNew = urlPath.replace(":id_provinsi", idSpin).replace(":id_kabupaten", idSpin2);

                                    if (idKabKot != 0) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            } else if ((nameDataEl.contains("kecamatan") || nameDataEl.contains("subdistrict")) && (getnameDataEl.contains("kelurahan") || getnameDataEl.contains("urbanvillage") || getnameDataEl.contains("desa") || getnameDataEl.contains("village"))) {
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
                                    String urlNew = urlPath.replace(":id_provinsi", idSpin).replace(":id_kabupaten", idSpin2).replace(":id_kecamatan", idSpin3);

                                    if (idKec != 0) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
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
            if (requestCode == REQUESTCODE_GALLERY) {
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
                    sessions.saveFlagUpDoc(true);
                }
            }
        }

    }

}