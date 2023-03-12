package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.DatePicker;
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
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsCameraSource;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.evo.mitzoom.ui.RatingActivity;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_cif_new extends Fragment {

    private final int REQUEST_WRITE_PERMISSION = 786;
    private final int REQUESTCODE_CAPTURE = 1;
    private final int REQUESTCODE_FILE = 202;
    private final int REQUESTCODE_SWAFOTO = 10;
    private int REQUESTCODE_GALLERY = 2;

    private View inclHead;
    private SwipeRefreshLayout swipe;
    private LinearLayout TopBar;
    private LinearLayout ll_head;
    private View inclBodyUpload;
    private TextView tvFotoKTP;
    private ImageView btnCamera;
    private LinearLayout btnGallery;
    private Button btnNext;
    private LinearLayout iconKtp;
    private LinearLayout iconSwafoto;
    private LinearLayout iconNpwp;
    private LinearLayout iconSignature;
    private LinearLayout iconForm;
    private ImageView viewImage;
    private LinearLayout LL;
    private LinearLayout chooseImage;
    private TextView tvAlertDoc;
    private ImageView imgDelete;
    private LinearLayout llOR;
    private LinearLayout llFormBuild;
    private Button btnProses;
    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private BroadcastReceiver smsReceiver = null;
    private PinView otp;

    private Context mContext;
    private SessionManager sessions;
    private boolean isCust;
    private boolean isSwafoto;
    private int formCode;
    private String idDips;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private int form_id = 0;
    private String keysData = "";
    private JSONObject dataFormCIF = null;
    final String STATE_IMGBYTE = "IMGBYTEUPLOAD";
    private byte[] IMG_BYTE = new byte[0];
    private String picturePath = "";
    private String picturePathCrop = "";
    private byte[] imageBytes = new byte[0];
    private String encodedImage;
    private String encodedImageCrop;
    private boolean flagOCR = false;
    private String tmptLahir = "-";
    private String provinsi,kodepos="",kota_kabupaten, nik, nama, ttl, jeniskelamin, golongan_darah, alamat, rtrw, desa_kelurahan, kecamatan, agama, status_perkawinan, kewarganegaraan, pekerjaan = "", namaIbuKandung = "";
    private JSONObject datasReqOCR = null;
    private int lasLenChar;
    private boolean backSpaceChar;
    private JSONObject objValCIF;
    private String no_handphone = "";
    private String numberOTP = "";
    private final String newString = "";
    private final Handler handler = null;
    private final Runnable myRunnable = null;
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private String transactionId = "";
    private JSONObject idFormObj;
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private boolean flagStuckSpin = false;
    private JSONObject reqFormMirroring;
    private int loopStatus = 0;
    private int getRequestCode = 0;
    private File mediaFilePhoto = null;
    private File mediaFilePhotoCropSwafoto = null;
    private JSONArray idElement;
    JSONObject objEl = new JSONObject();
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String npwp = "-";
    private EditText edKodePos = null;
    JSONObject dataObjProDesa = new JSONObject();
    private boolean ocrKTP = false;
    private boolean flagMother = false;
    private int loopValidMother = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("CEK","frag_cif_new onCreate");

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        Log.e("CEK",mContext+" isSessionZoom : "+isSessionZoom);
        /*if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_cif, container, false);

        Log.e("CEK","frag_cif_new onCreateView");

        inclHead = views.findViewById(R.id.inclHead);
        TopBar = views.findViewById(R.id.TopBar);
        ll_head = views.findViewById(R.id.ll_head);
        tvAlertDoc = views.findViewById(R.id.tvAlertDoc);
        tvFotoKTP = views.findViewById(R.id.tvFotoKTP);
        iconKtp = views.findViewById(R.id.icon_ktp);
        iconSwafoto = views.findViewById(R.id.icon_swafoto);
        iconNpwp = views.findViewById(R.id.icon_npwp);
        iconSignature = views.findViewById(R.id.icon_signature);
        iconForm = views.findViewById(R.id.icon_form);

        swipe = views.findViewById(R.id.swipe);

        inclBodyUpload = views.findViewById(R.id.inclBodyUpload);
        btnCamera = views.findViewById(R.id.choose_camera);
        btnGallery = views.findViewById(R.id.choose_gallery);
        btnNext = views.findViewById(R.id.btnNext);
        imgDelete = views.findViewById(R.id.imgDelete);
        viewImage = views.findViewById(R.id.Imageview);
        LL = views.findViewById(R.id.BackgroundLL);
        llOR = views.findViewById(R.id.llOR);
        chooseImage = views.findViewById(R.id.Choose_Image);

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

        Log.e("CEK","frag_cif_new onViewCreated");

        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            } else if (getArguments().containsKey("NPWP")) {
                npwp = getArguments().getString("NPWP");
            } else if (getArguments().containsKey("OCRKTP")) {
                ocrKTP = getArguments().getBoolean("OCRKTP");
            }
        }
        if (formCode == 8) {
            keysData = "datadiri";
        }
        if (formCode == 801) {
            keysData = "datatidaksesuai";
        }
        else if (formCode == 802) {
            keysData = "pekerjaan";
        }
        else if (formCode == 803) {
            keysData = "keuangan";
        }

        Log.e("CEK", mContext + " isCust : " + isCust);
        Log.e("CEK", mContext + " isSwafoto : " + isSwafoto);
        Log.e("CEK", mContext + " formCode : " + formCode);
        Log.e("CEK", mContext + " idDips : " + idDips);
        Log.e("CEK", mContext + " form_id : " + form_id);

        if (formCode == 22) {
            tvFotoKTP.setText(R.string.ktp_swafoto);
        }
        else {
            if (isSessionZoom) {
                tvFotoKTP.setText(getString(R.string.pembukaan_akun));
            } else {
                if (formCode == 22) {
                    tvFotoKTP.setText(getString(R.string.ktp_swafoto));
                } else {
                    tvFotoKTP.setText(getString(R.string.Photo_ktp));
                }
            }
        }

        if (isSessionZoom) {
            TopBar.setVisibility(View.VISIBLE);
            ll_head.setVisibility(View.VISIBLE);
        }
        else {
            TopBar.setVisibility(View.GONE);
            ll_head.setVisibility(View.VISIBLE);
        }

        if (formCode > 7 && formCode != 22) {
            dataFormCIF = new JSONObject();
            inclBodyUpload.setVisibility(View.GONE);
            llFormBuild.setVisibility(View.VISIBLE);
            if (formCode == 8) {
                form_id = 10;
            }

            iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            iconForm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));

            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    dataFormCIF = new JSONObject();
                    processGetForm(form_id);
                }
            });

            processGetForm(form_id);

            processAction();

        }
        else {

            if (formCode == 22) {
                llOR.setVisibility(View.GONE);
                btnGallery.setVisibility(View.GONE);
            }

            inclBodyUpload.setVisibility(View.VISIBLE);
            llFormBuild.setVisibility(View.GONE);
            btnProses.setVisibility(View.GONE);
            imgDelete.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            btnNext.setClickable(false);

            if (formCode == 4 && isSessionZoom) {
                //PopUp();
                sessions.saveOCR(null);
            }

            if (IMG_BYTE.length == 0 && formCode == 4) {
                iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                tvAlertDoc.setText(getString(R.string.alert_ktp));
            } else if (formCode > 4 || formCode == 22) {
                iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }

            if (IMG_BYTE.length == 0 && formCode == 22) {
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                tvAlertDoc.setText(getString(R.string.alert_swafoto));
            } else if (formCode == 6) {
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }

            if (IMG_BYTE.length == 0 && formCode == 6) {
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                tvAlertDoc.setText(getString(R.string.alert_npwp));
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setClickable(true);
            } else if (formCode == 7) {
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }

            if (IMG_BYTE.length == 0 && formCode == 7) {
                iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                tvAlertDoc.setText(getString(R.string.alert_ttd));
            } else if (formCode > 7 && formCode != 22) {
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }

            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!requestPermission()) {
                        Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sessions.saveMedia(1);
                    chooseFromCamera();
                }
            });
            btnGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!requestPermission()) {
                        Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sessions.saveMedia(2);
                    REQUESTCODE_GALLERY = 2;
                    chooseFromSD();
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LL.setBackground(mContext.getResources().getDrawable(R.drawable.bg));
                    btnNext.setVisibility(View.GONE);
                    btnNext.setClickable(false);
                    viewImage.setVisibility(View.GONE);
                    chooseImage.setVisibility(View.VISIBLE);
                    if (formCode == 22) {
                        llOR.setVisibility(View.GONE);
                        btnGallery.setVisibility(View.GONE);
                    }
                    imgDelete.setVisibility(View.GONE);
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("CEK","btnNext formCode : "+formCode+" | IMG_BYTE : "+IMG_BYTE.length);
                    if (formCode == 22) {
                        IMG_BYTE = imageBytes;
                        isSwafoto = true;
                        sessions.saveIsSwafoto(isSwafoto);
                    }
                    else if (formCode == 6) {
                        IMG_BYTE = imageBytes;
                    }
                    else if (formCode == 7) {
                        IMG_BYTE = imageBytes;
                    }

                    if ((!flagOCR && imageBytes.length == 0 && formCode == 4) || (IMG_BYTE.length == 0 && formCode == 22) || (IMG_BYTE.length == 0 && formCode == 7)){
                        Toast.makeText(mContext, getResources().getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (imageBytes.length > 0 && formCode == 4) {
                        if (flagOCR) {
                            IMG_BYTE = imageBytes;
                            PopUpOCR();
                            JSONObject dataReq = dataReqOCR2();
                            JSONObject reqOCR = new JSONObject();
                            try {
                                reqOCR.put("ocr",dataReq);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RabbitMirroring.MirroringSendKey(reqOCR);
                        } else {
                            Toast.makeText(mContext, "Maaf, OCR masih dalam proses...!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if ((imageBytes.length == 0 && formCode == 6)){
                        Bundle bundle = new Bundle();
                        bundle.putString("NPWP",npwp);
                        sessions.saveFormCOde(7);
                        RabbitMirroring.MirroringSendEndpoint(7);
                        sendDataFragment(bundle, new frag_cif_new());
                    }
                    else {
                        if (formCode == 22) {
                            if (ocrKTP && flagOCR) {
                                PopUpOCR();
                                JSONObject dataReq = dataReqOCR2();
                                JSONObject reqOCR = new JSONObject();
                                try {
                                    reqOCR.put("ocr",dataReq);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                RabbitMirroring.MirroringSendKey(reqOCR);
                            } else {
                                if (!picturePath.isEmpty()) {
                                    String fieldName = "foto";
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isSessionZoom) {
                                                BaseMeetingActivity.showProgress(true);
                                            } else {
                                                DipsSwafoto.showProgress(true);
                                            }
                                        }
                                    });
                                    processFormDataAttachment(fieldName, picturePath);
                                }
                            }
                        }
                        else if (formCode == 6) {
                            if (!picturePath.isEmpty()) {
                                String fieldName = "npwp";
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
                                processFormDataAttachment(fieldName,picturePath);
                            }
                        }
                        else if (formCode == 7) {
                            if (!picturePath.isEmpty()) {
                                String fieldName = "ttd";
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
                                processFormDataAttachment(fieldName,picturePath);
                            }
                        }
                    }
                }
            });

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

    private void processAction() {
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
                                    String labelDataEl = idElement.getJSONObject(j).getString("label");
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            } else if (nameDataEl.contains("noponsel") && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            } else if (requiredDataEl){
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                            RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                            if (rl.getChildAt(0) instanceof Spinner) {
                                                if (objEl.getString(nameDataEl).toLowerCase().contains("pilih")) {
                                                    Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                break;
                                            }
                                        } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                            String results = autoText.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, labelDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
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
                        processNext();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                numberOTP = "";
                String dataSMS = intent.getExtras().getString("smsMessage");
                Log.e("CEK","MASUK dataSMS : "+dataSMS);
                String[] sp = dataSMS.split(" ");
                for (int i = 0; i < sp.length; i++) {
                    String word = sp[i];
                    if(word.matches("\\d+(?:\\.\\d+)?")) {
                        numberOTP = word.replaceAll("[^0-9]", "");
                        if (numberOTP.length() == 6) {
                            otp.setText(numberOTP);
                            /*newString = myFilter(numberOTP);
                            otp.setText(newString);*/
                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(smsReceiver,new IntentFilter("getotp"));
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
        //outState.putByteArray(STATE_IMGBYTE,IMG_BYTE);
    }

    private void processNext() {
        Log.e("CEK","MASUK NEXT");
        String getCif = sessions.getCIF();
        try {
            JSONObject dataCIF = null;
            if (getCif != null) {
                dataCIF = new JSONObject(getCif);
            } else {
                dataCIF = new JSONObject();
            }
            dataCIF.put(keysData,objEl);
            dataFormCIF.put(keysData,objEl);
            sessions.saveCIF(dataCIF.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject reqFormSend = dataReqForm();

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

        if (objEl.has(keyPernyataan) && formCode == 8) {
            try {
                boolean pernyataan = objEl.getBoolean(keyPernyataan);
                Bundle bundle = new Bundle();

                if (pernyataan) {
                    int intLayoutSelf = 801;
                    reqFormMirroring = dataReqFormMirroring();
                    RabbitMirroring.MirroringSendKey(reqFormMirroring);
                    RabbitMirroring.MirroringSendEndpoint(intLayoutSelf);
                    bundle.putInt("form_id",9);
                    sessions.saveFormCOde(intLayoutSelf);
                    sessions.saveFormReq(reqFormSend.toString());
                    sessions.saveFormReqMirroring(reqFormMirroring.toString());
                    sendDataFragment(bundle, new frag_cif_new());
                } else {
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
                    processSendFormCIF(reqFormSend);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
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
            processSendFormCIF(reqFormSend);
        }
    }

    private void processGetForm(int formId) {
        Log.e("CEK", this+" MASUK processGetForm formId : "+formId);
        Log.e("CEK", this+" MASUK formCode : "+formCode);
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","response processGetForm : "+response.code());
                if (response.isSuccessful()) {
                    btnProses.setVisibility(View.VISIBLE);
                    String dataS = response.body().toString();
                    Log.e("CEK","response dataS : "+dataS);
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
                        Log.e("CEK","dataForm : "+dataForm);
                        MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = MyParserFormBuilder.getForm();
                        Log.e("CEK","dataElement : "+ idElement);
                        processValidationActionForm();
                        dataFormCIF.put(keysData,objEl);
                        reqFormMirroring = dataReqFormMirroring();
                        Log.e("CEK","DATA dataFormCIF : "+dataFormCIF.toString());
                        Log.e("CEK","DATA reqFormMirroring : "+reqFormMirroring.toString());
                        RabbitMirroring.MirroringSendKey(reqFormMirroring);
                        if (formCode == 8) {
                            if (sessions.getOCR() != null) {
                                String dataOCR = sessions.getOCR();
                                Log.e("CEK","dataOCR : "+ dataOCR);
                                try {
                                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                                    desa_kelurahan = dataObjOCR.getString("desakelurahan");
                                    kecamatan = dataObjOCR.getString("kecamatan");
                                    kota_kabupaten = dataObjOCR.getString("kotakabupaten");
                                    provinsi = dataObjOCR.getString("provinsi");
                                    getKodePos();
                                } catch (JSONException e) {

                                }
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
            }
        });
    }

    private void processValidationActionForm() {
        Log.e("CEK","processValidationActionForm");
        try {
            objEl.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
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
                                    if (nameDataEl.contains("npwp") && !npwp.isEmpty()) {
                                        ed.setText(npwp);
                                    }
                                    else if (nameDataEl.contains("kode") || nameDataEl.contains("pos")){
                                        edKodePos = ed;
                                    }
                                    ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            Log.e("CEK","onFocusChange : "+b);
                                        }
                                    });
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            Log.e("CEK",nameDataEl+" : "+charSequence);
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
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
                                                    dataFormCIF.put(keysData,objEl);
                                                    if (isSessionZoom) {
                                                        reqFormMirroring = dataReqFormMirroring();
                                                        RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                                dataFormCIF.put(keysData,objEl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (isSessionZoom) {
                                                reqFormMirroring = dataReqFormMirroring();
                                                RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormCIF.put(keysData,objEl);
                                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                        valSpinProv.put(nameDataEl,idData);
                                                        if (nameDataEl.contains("provinsi")){
                                                            provinsi = results;
                                                            kodepos = "";
                                                        } else if (nameDataEl.contains("kabupaten") || nameDataEl.contains("kota")) {
                                                            kota_kabupaten = results;
                                                            kodepos = "";
                                                        }
                                                        else if(nameDataEl.contains("kecamatan")){
                                                            kecamatan = results;
                                                            kodepos = "";
                                                        }
                                                        else if (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa")){
                                                            desa_kelurahan = results;
                                                            kodepos = "";
                                                            getKodePos();
                                                        }
                                                    }
                                                    else {
                                                        valSpin.put(nameDataEl, idData);
                                                    }
                                                    if (isSessionZoom) {
                                                        reqFormMirroring = dataReqFormMirroring();
                                                        RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    private void setKodepos(){
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
                                    if (nameDataEl.contains("kode") && nameDataEl.contains("pos")) {
                                        ed.setText(kodepos);
                                    }
                                    ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            Log.e("CEK","onFocusChange : "+b);
                                        }
                                    });
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            Log.e("CEK",nameDataEl+" : "+charSequence);
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    RabbitMirroring.MirroringSendKey(reqFormMirroring);
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
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
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
                                    Log.e("CEK","processEnableComp nameDataEl : "+nameDataEl+" | nameDataElGab : "+nameDataElGab);
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

    private JSONObject dataReqForm() {
        JSONObject dataFormObj = null;
        try {
            if (sessions.getFormReq() != null) {
                String forms = sessions.getFormReq();
                dataFormObj = new JSONObject(forms);
            } else {
                dataFormObj = new JSONObject();
            }
            JSONObject dataFormObj2 = new JSONObject(objEl.toString());

            JSONObject dataSelfObj = new JSONObject();

            String lbalamattempattinggalsaatini = "alamattempattinggalsaatini";
            String lbrt = "rt";
            String lbrw = "rw";
            String lbkelurahandesa = "kelurahandesa";
            String lbkecamatan = "kecamatan";
            String lbkabupatenkota = "kabupatenkota";
            String lbprovinsi = "provinsi";
            String lbkodepos = "kodepos";
            String lbnotelp = "notelp";
            String lbpernyataan = "alamatdomisili";

            for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                Log.e("CEK","iter : "+ iter);
                Log.e("CEK","iter next : "+iter.next());
                if(iter.hasNext()) {
                    String key = iter.next();
                    String valKurung = "";
                    int indx = key.indexOf("(");
                    if (indx >= 0) {
                        valKurung = key.substring(indx);
                    }

                    if (key.equals(lbalamattempattinggalsaatini + valKurung)) {
                        lbalamattempattinggalsaatini = key;
                    }
                    if (key.equals(lbrt + valKurung)) {
                        lbrt = key;
                    }
                    if (key.equals(lbrw + valKurung)) {
                        lbrw = key;
                    }
                    if (key.equals(lbkelurahandesa + valKurung)) {
                        lbkelurahandesa = key;
                    }
                    if (key.equals(lbkecamatan + valKurung)) {
                        lbkecamatan = key;
                    }
                    if (key.equals(lbkabupatenkota + valKurung)) {
                        lbkabupatenkota = key;
                    }
                    if (key.equals(lbprovinsi + valKurung)) {
                        lbprovinsi = key;
                    }
                    if (key.equals(lbkodepos + valKurung)) {
                        lbkodepos = key;
                    }
                    if (key.equals(lbnotelp + valKurung)) {
                        lbnotelp = key;
                    }
                    if (key.equals(lbpernyataan + valKurung) || key.contains("domisili" + valKurung) || key.contains("pernyataan" + valKurung) ||
                            (key.contains("alamat" + valKurung) && key.contains("berbeda" + valKurung))) {
                        lbpernyataan = key;
                    }

                    Log.e("CEK", "key : " + key + " | valKurung : " + valKurung);
                }

            }

            if (formCode == 801) {
                String alamat2 = "";
                if (dataFormObj2.has(lbalamattempattinggalsaatini)) {
                    alamat2 = dataFormObj2.getString(lbalamattempattinggalsaatini);
                    dataFormObj2.remove(lbalamattempattinggalsaatini);
                }
                String rt2 = "";
                if (dataFormObj2.has(lbrt)) {
                    rt2 = dataFormObj2.getString(lbrt);
                    dataFormObj2.remove(lbrt);
                }
                String rw2 = "";
                if (dataFormObj2.has(lbrw)) {
                    rw2 = dataFormObj2.getString(lbrw);
                    dataFormObj2.remove(lbrw);
                }
                String kelurahandesa2 = "";
                if (dataFormObj2.has(lbkelurahandesa)) {
                    kelurahandesa2 = dataFormObj2.getString(lbkelurahandesa);
                    dataFormObj2.remove(lbkelurahandesa);
                }
                String kecamatan2 = "";
                if (dataFormObj2.has(lbkecamatan)) {
                    kecamatan2 = dataFormObj2.getString(lbkecamatan);
                    dataFormObj2.remove(lbkecamatan);
                }
                String kabupatenkota2 = "";
                if (dataFormObj2.has(lbkabupatenkota)) {
                    kabupatenkota2 = dataFormObj2.getString(lbkabupatenkota);
                    dataFormObj2.remove(lbkabupatenkota);
                }
                String provinsi2 = "";
                if (dataFormObj2.has(lbprovinsi)) {
                    provinsi2 = dataFormObj2.getString(lbprovinsi);
                    dataFormObj2.remove(lbprovinsi);
                }
                String kodepos2 = "";
                if (dataFormObj2.has(lbkodepos)) {
                    kodepos2 = dataFormObj2.getString(lbkodepos);
                    dataFormObj2.remove(lbkodepos);
                }
                String notelp2 = "";
                if (dataFormObj2.has(lbnotelp)) {
                    notelp2 = dataFormObj2.getString(lbnotelp);
                    dataFormObj2.remove(lbnotelp);
                }

                dataSelfObj.put("alamat2",alamat2);
                dataSelfObj.put("rt2",rt2);
                dataSelfObj.put("rw2",rw2);
                dataSelfObj.put("kelurahandesa2",kelurahandesa2);
                dataSelfObj.put("kecamatan2",kecamatan2);
                dataSelfObj.put("kabupatenkota2",kabupatenkota2);
                dataSelfObj.put("provinsi2",provinsi2);
                dataSelfObj.put("kodepos2",kodepos2);
                dataSelfObj.put("notelp2",notelp2);

            }

            if (dataFormObj2.has(lbpernyataan)) {
                boolean pernyataan = dataFormObj2.getBoolean(lbpernyataan);
                dataFormObj.put("datatidaksesuai",pernyataan);
                dataFormObj2.remove(lbpernyataan);
            }

            if (dataFormObj2.has("idDips")) {
                String getidDips = dataFormObj2.getString("idDips");
                if (formCode != 801) {
                    dataFormObj.put("idDips", getidDips);
                }
                dataFormObj2.remove("idDips");
            }

            if (formCode == 8 || formCode == 801) {
                if (dataFormObj.has("datadiri")) {
                    JSONObject selfObj = dataFormObj.getJSONObject("datadiri");
                    selfObj.put("datatidaksesuai",dataSelfObj);
                    dataFormObj.put("datadiri", selfObj);
                } else {
                    dataFormObj.put("datadiri", dataFormObj2);
                }
            } else if (formCode == 802) {
                dataFormObj.put("datapekerjaan",dataFormObj2);
            } else if (formCode == 803) {
                dataFormObj.put("datakeuangan",dataFormObj2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataFormObj;
    }

    private JSONObject dataReqFormMirroring() {
        JSONObject dataFormObj = null;
        try {
            if (sessions.getFormReqMirroring() != null) {
                String forms = sessions.getFormReqMirroring();
                dataFormObj = new JSONObject(forms);
            } else {
                dataFormObj = new JSONObject();
            }

            JSONObject dataFormObj2 = new JSONObject(objEl.toString());

            if (dataFormObj2.has("idDips")) {
                String getidDips = dataFormObj2.getString("idDips");
                if (formCode != 801) {
                    dataFormObj.put("idDips", getidDips);
                }
                dataFormObj2.remove("idDips");
            }

            if (formCode == 8 || formCode == 801) {
                if (dataFormObj.has("datadiri")) {
                    JSONObject selfObj = dataFormObj.getJSONObject("datadiri");
                    selfObj.put("datatidaksesuai",dataFormObj2);
                    dataFormObj.put("datadiri", selfObj);

                    String lbpernyataan = "alamatdomisili";

                    for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                        if(iter.hasNext()) {
                            String key = iter.next();
                            String valKurung = "";
                            int indx = key.indexOf("(");
                            if (indx >= 0) {
                                valKurung = key.substring(indx);
                            }

                            if (key.equals(lbpernyataan + valKurung) || key.contains("domisili" + valKurung) || key.contains("pernyataan" + valKurung) ||
                                    (key.contains("alamat") && key.contains("berbeda" + valKurung))) {
                                lbpernyataan = key;
                            }
                        }

                    }

                    if (selfObj.has(lbpernyataan)) {
                        boolean chk = selfObj.getBoolean(lbpernyataan);
                        dataFormObj.put("datatidaksesuai", chk);
                    }
                } else if (formCode == 8){
                    dataFormObj.put("datadiri", dataFormObj2);
                }
            } else if (formCode == 802) {
                dataFormObj.put(keysData,dataFormObj2);
            } else if (formCode == 803) {
                dataFormObj.put(keysData,dataFormObj2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","dataReqFormMirroring : "+dataFormObj);
        return dataFormObj;
    }

    private void processSendFormCIF(JSONObject jsons) {
        Log.e("CEK","processSendFormCIF : "+jsons.toString());
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = null;
        if (formCode == 8 || formCode == 801) {
            call = API.AddDataSelf(requestBody,authAccess,exchangeToken);
        }
        else if (formCode == 802) {
            call = API.AddDataWork(requestBody,authAccess,exchangeToken);
        }
        else if (formCode == 803) {
            call = API.AddDataFinance(requestBody,authAccess,exchangeToken);
        }

        Log.e("CEK","processSendFormCIF call : "+call.request());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processSendFormCIF code : "+response.code());
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
                    Log.e("CEK","processSendFormCIF dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode >= 200 && errCode <= 300) {
                            sessions.saveFormReq(null);
                            sessions.saveFormReqMirroring(null);

                            Bundle bundle = new Bundle();
                            int intLayoutWork = formCode;

                            String keyPernyataan = "alamatdomisili";
                            for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                                if (iter.hasNext()) {
                                    String key = iter.next();
                                    String valKurung = "";
                                    int indx = key.indexOf("(");
                                    if (indx >= 0) {
                                        valKurung = key.substring(indx);
                                    }
                                    if (key.contains(keyPernyataan) || key.contains("domisili") || key.contains("pernyataan" + valKurung) ||
                                            (key.contains("alamat") && key.contains("berbeda" + valKurung))) {
                                        if (key.equals(keyPernyataan + valKurung)) {
                                            keyPernyataan = key;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (objEl.has(keyPernyataan) && formCode == 8) {
                                intLayoutWork = 802;
                                bundle.putInt("form_id",5);
                            } else if (formCode == 801) {
                                intLayoutWork = 802;
                                bundle.putInt("form_id",5);
                            } else if (formCode == 802) {
                                intLayoutWork = 803;
                                bundle.putInt("form_id",14);
                            } else if (formCode == 803) {
                                intLayoutWork = 804;
                            }

                            reqFormMirroring = dataReqFormMirroring();
                            RabbitMirroring.MirroringSendKey(reqFormMirroring);
                            RabbitMirroring.MirroringSendEndpoint(intLayoutWork);
                            sessions.saveFormCOde(intLayoutWork);
                            if (intLayoutWork == 804 && sessions.getCIF() != null) {
                                JSONObject dataFinance = dataObj.getJSONObject("data");
                                if (dataFinance.has("noCif")) {
                                    String noCif = dataFinance.getString("noCif");
                                    sessions.saveNoCIF(noCif);
                                }
                                //sendDataFragment(bundle, new frag_cif_full());
                                String valDataCIF = sessions.getCIF();
                                try {
                                    objValCIF = new JSONObject(valDataCIF);
                                    Log.e("CEK","CIF FULL objValCIF : "+ objValCIF);
                                    JSONObject getObjEl = objValCIF.getJSONObject("datadiri");

                                    String getDataNasabah = sessions.getNasabah();
                                    Log.e("CEK","getDataNasabah : "+getDataNasabah);
                                    JSONObject dataNasabahObj = null;
                                    if (getDataNasabah != null && !getDataNasabah.isEmpty()) {
                                        dataNasabahObj = new JSONObject(getDataNasabah);
                                    }

                                    String keyNoponsel = "noponsel";
                                    String keyGelar = "";
                                    String keyNamaIdentitas = "";
                                    String keyNoIdentitas = "";
                                    for(Iterator<String> iter = getObjEl.keys(); iter.hasNext();) {
                                        if (iter.hasNext()) {
                                            String key = iter.next();
                                            String valKurung = "";
                                            int indx = key.indexOf("(");
                                            if (indx >= 0) {
                                                valKurung = key.substring(indx);
                                            }
                                            if (key.contains(keyNoponsel)) {
                                                if (key.equals(keyNoponsel + valKurung)) {
                                                    keyNoponsel = key;
                                                    break;
                                                }
                                            } else if (key.contains("nama") && key.contains("identitas"+valKurung)) {
                                                keyNamaIdentitas = key;
                                            } else if (key.contains("noidentitas"+valKurung) || key.contains("nomoridentitas"+valKurung)) {
                                                keyNoIdentitas = key;
                                            } else if (key.contains("gelar"+valKurung)) {
                                                keyGelar = key;
                                            }
                                        }
                                    }
                                    Log.e("CEK","keyNamaIdentitas : "+keyNamaIdentitas);
                                    Log.e("CEK","keyNoIdentitas : "+keyNoIdentitas);
                                    no_handphone = getObjEl.getString(keyNoponsel);
                                    String namaIdentitas = "";
                                    if (!keyNamaIdentitas.isEmpty()) {
                                        namaIdentitas = getObjEl.getString(keyNamaIdentitas);
                                    }
                                    String noIdentitas = "";
                                    if (!keyNoIdentitas.isEmpty()) {
                                        noIdentitas = getObjEl.getString(keyNoIdentitas);
                                    }
                                    String gelar = "";
                                    if (!keyGelar.isEmpty()) {
                                        gelar = getObjEl.getString(keyGelar);
                                    }

                                    Log.e("CEK","no_handphone : "+no_handphone+" | namaIdentitas : "+namaIdentitas);
                                    Log.e("CEK","noIdentitas : "+noIdentitas+" | gelar : "+gelar);

                                    dataNasabahObj.put("noHp",no_handphone);
                                    dataNasabahObj.put("namaLengkap",namaIdentitas);
                                    dataNasabahObj.put("nik",noIdentitas);
                                    dataNasabahObj.put("gelar",gelar);
                                    Log.e("CEK","dataNasabahObj : "+ dataNasabahObj);
                                    sessions.saveNasabah(dataNasabahObj.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                                APISaveForm();
                            } else {
                                sendDataFragment(bundle, new frag_cif_new());
                            }

                        } else {
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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

    private void processGetDynamicURL(Spinner spin, String urlPath, String nameDataEl) {
        flagStuckSpin = false;
        Log.e("CEK","processGetDynamicURL : "+urlPath);
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrl(urlPath,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processGetDynamicURL code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processGetDynamicURL dataS : "+dataS);
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
                            if (i == 0) {
                                if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                    valSpinProv.put(nameDataEl,idData);
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
                            if ((nameDataEl.contains("provinsi") || nameDataEl.contains("province")) && (getnameDataEl.contains("kabupaten") || getnameDataEl.contains("district") || getnameDataEl.contains("kota") || getnameDataEl.contains("city"))) {
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

                                    Log.e("CEK", "urlNew : "+urlNew);
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

                                    Log.e("CEK", "urlNew : "+urlNew);

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

    private void processDataFromOCR() {
        Log.e("CEK","processDataFromOCR : "+objEl.toString());
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
                                    String valEl = objEl.getString(nameDataEl);
                                    ed.setText(valEl);
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();
                                        String valEl = objEl.getString(nameDataEl);
                                        String valRad = valEl.toLowerCase();

                                        String valKurung = "";
                                        int indx = valEl.indexOf("(");
                                        if (indx >= 0) {
                                            valKurung = valEl.substring(indx);
                                        }

                                        if (valEl.toLowerCase().equals("kawin"+valKurung)) {
                                            valRad = "menikah";
                                        }
                                        Log.e("CEK","labelRad : "+labelRad+" | valEl : "+valEl);
                                        if (labelRad.toLowerCase().equals(valRad)) {
                                            rb.setChecked(true);
                                            break;
                                        } else {

                                            if (valRad.contains("laki") && valRad.contains("-")) {
                                                String[] sp = valRad.split("-");
                                                valRad = sp[0]+" - "+sp[1];
                                                if (labelRad.toLowerCase().equals(valRad)) {
                                                    rb.setChecked(true);
                                                    break;
                                                }
                                            } else if ((valRad.contains("man") || valRad.contains("woman")) && (labelRad.toLowerCase().contains("kelamin") || labelRad.toLowerCase().contains("gender"))) {
                                                rb.setChecked(true);
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
                                    String valEl = objEl.getString(nameDataEl);
                                    for (int ch = 0; ch < spin.getCount(); ch++) {
                                        if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                            spin.setSelection(ch);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
                                    autoText.setText(valEl);
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

    private void getKodePos(){
        JSONObject json = new JSONObject();
        Log.e("CEK","MASUK GETKODEPOS");
        try {
            json.put("kelurahan", desa_kelurahan);
            json.put("kecamatan", kecamatan);
            json.put("kabupaten", kota_kabupaten);
            json.put("provinsi",provinsi);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CEK","Kelurahan : "+desa_kelurahan+" | kecamatan : "+kecamatan+" | kabupaten : "+kota_kabupaten+" | provinsi : "+provinsi);
        Log.e("CEK","PAYLOAD GET KODE POS "+ json);
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Log.e("CEK","PAYLOAD GET KODE POS 2"+requestBody);
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.getKodePos(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    String dataS = response.body().toString();
                    Log.d("CEK RESPONSE Berhasil",""+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        kodepos = dataObj.getJSONObject("data").getString("kodepos");
                        Log.e("INI KODE POS",""+kodepos);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("CEK RESPONSE GAGAL",""+response);
                }
                if (formCode == 8){
                    processMatchData();
                    processDataFromOCR();
                }
                else {
                    edKodePos.setText(kodepos);
                    //setKodepos();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK RESPONSE GAGAL",""+t.getMessage());
                if (formCode == 8){
                    processMatchData();
                    processDataFromOCR();
                }
                else {
                    //setKodepos();
                }
            }
        });
    }
    private void processMatchData() {
        Log.e("CEK","processMatchData");
        String dataOCR = sessions.getOCR();
        Log.e("CEK","dataOCR : "+ dataOCR);
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

            Log.e("CEK","namaOCR : "+namaOCR);
            for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                if (iter.hasNext()) {
                    String key = iter.next();
                    String valKurung = "";
                    int indx = key.indexOf("(");
                    if (indx >= 0) {
                        valKurung = key.substring(indx);
                    }
                    if (key.contains("nama") && key.contains("identitas")) {
                        objEl.put(key, namaOCR);
                    }
                    else if (key.contains("ibu")){
                        objEl.put(key, namaIbuKandung);
                    }
                    else if(key.contains("kode") && key.contains("pos")){
                        objEl.put(key,kodepos);
                    }
                    else if (key.contains("provinsi")) {
                        objEl.put(key, provinsiOCR);
                    }
                    else if (key.contains("kabupaten") || key.contains("kota")) {
                        objEl.put(key, kabkotOCR);
                    }
                    else if (key.contains("noidentitas") || key.contains("nomoridentitas")) {
                        objEl.put(key, nikOCR);
                    }
                    else if (key.contains("tempat") && key.contains("lahir")) {
                        objEl.put(key, tempatlahirOCR);
                    }
                    else if (key.contains("tanggal") && key.contains("lahir")) {
                        objEl.put(key, tgllahirOCR);
                    }
                    else if (key.contains("kelamin")) {
                        if (sessions.getLANG().equals("en")) {
                            if (jeniskelaminOCR.toLowerCase().contains("laki")) {
                                objEl.put(key, "Male");
                            } else {
                                objEl.put(key, "Female");
                            }
                        } else {
                            objEl.put(key, jeniskelaminOCR);
                        }
                    }
                    else if (key.contains("alamat") && key.contains("identitas")) {
                        objEl.put(key, alamatOCR);
                    }
                    else if (key.equals("rt" + valKurung)) {
                        objEl.put(key, rtOCR);
                    }
                    else if (key.equals("rw" + valKurung)) {
                        objEl.put(key, rwOCR);
                    }
                    else if (key.contains("kelurahan") || key.contains("desa")) {
                        objEl.put(key, desaOCR);
                    }
                    else if (key.contains("kecamatan")) {
                        objEl.put(key, kecamatanOCR);
                    }
                    else if (key.contains("agama")) {
                        objEl.put(key, agamaOCR);
                    }
                    else if (key.contains("nikah") || key.contains("menikah")) {
                        objEl.put(key, kawinOCR);
                    }
                    else if (key.contains("warganegara")) {
                        if (sessions.getLANG().equals("en")) {
                            objEl.put(key, "Indonesian citizens");
                        } else {
                            objEl.put(key, wargaOCR);
                        }
                    }
                    else if (key.contains("jenisidentitas")) {
                        if (sessions.getLANG().equals("en")) {
                            objEl.put(key, "ID card");
                        } else {
                            objEl.put(key, "KTP");
                        }
                    }
                    else if (key.contains("negara") && wargaOCR.equals("WNI")) {
                        objEl.put(key, "Indonesia");
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void PopUpLimitValidateMother(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_info));
        tvBodyDialog.setText(R.string.content_waiting_mother);

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismiss();
            }
        });
    }

    private void PopUpOCR(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_ocr, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        EditText NIK = dialogView.findViewById(R.id.et_nik_ocr);
        EditText Nama = dialogView.findViewById(R.id.et_name_ocr);
        EditText TTL = dialogView.findViewById(R.id.et_ttl_ocr);
        EditText TTL2 = dialogView.findViewById(R.id.et_ttl2_ocr);
        EditText et_gender= dialogView.findViewById(R.id.et_gender);
        EditText et_goldar= dialogView.findViewById(R.id.et_goldar);
        EditText et_address= dialogView.findViewById(R.id.et_address);
        EditText et_rtrw= dialogView.findViewById(R.id.et_rtrw);
        EditText et_provinsi= dialogView.findViewById(R.id.et_provinsi);
        EditText et_kabkot= dialogView.findViewById(R.id.et_kabkot);
        EditText et_kecamatan= dialogView.findViewById(R.id.et_kecamatan);
        EditText et_desa= dialogView.findViewById(R.id.et_desa);
        EditText et_religion= dialogView.findViewById(R.id.et_religion);
        EditText et_status_kawin= dialogView.findViewById(R.id.et_status_kawin);
        EditText et_warga= dialogView.findViewById(R.id.et_warga);
        EditText et_work= dialogView.findViewById(R.id.et_work);
        EditText et_nama_ibuKandung = dialogView.findViewById(R.id.et_nama_ibu_kandung);
        TextView tvError = (TextView) dialogView.findViewById(R.id.tvError);
        TextView tvMandatory = (TextView) dialogView.findViewById(R.id.tvMandatory);
        CheckBox chkDataCorrect = (CheckBox) dialogView.findViewById(R.id.chkDataCorrect);
        Button btnOCRCancel = dialogView.findViewById(R.id.btncncl);
        Button btnOCRNext = dialogView.findViewById(R.id.btnlnjt);

        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        Log.e("CEK","PopUpOCR width : "+width+" | height : "+height);
        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);
        Log.e("CEK","PopUpOCR newWidth : "+newWidth+" | newHeight : "+newHeight);

        sweetAlertDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        sweetAlertDialog.getWindow().setLayout(newWidth,newHeight);
        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        String tglLahir = "-";
        if (ttl.indexOf(",") > 0) {
            String[] sp = ttl.split(",");
            tglLahir = sp[1].trim();
        }
        else{
            tglLahir = ttl;
        }

        NIK.setText(nik);
        Nama.setText(nama);
        TTL.setText(tmptLahir);
        TTL2.setText(tglLahir);
        et_gender.setText(jeniskelamin);
        et_goldar.setText(golongan_darah);
        et_address.setText(alamat);
        et_rtrw.setText(rtrw);
        et_provinsi.setText(provinsi);
        et_kabkot.setText(kota_kabupaten);
        et_kecamatan.setText(kecamatan);
        et_desa.setText(desa_kelurahan);
        et_religion.setText(agama);
        et_status_kawin.setText(status_perkawinan);
        et_warga.setText(kewarganegaraan);
        et_work.setText(pekerjaan);
        et_nama_ibuKandung.setText(namaIbuKandung);

        Calendar currentTimeOCR = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                currentTimeOCR.set(Calendar.YEAR, year);
                currentTimeOCR.set(Calendar.MONTH, month);
                currentTimeOCR.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String Tgl = String.format("%02d", dayOfMonth);
                String bln = String.format("%02d", month+1);

                String getDates = Tgl+"-"+bln+"-"+year;
                TTL2.setText(getDates);
            }
        };

        chkDataCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkDataCorrect.isChecked()) {
                    tvError.setVisibility(View.GONE);
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
                    processValidateIbuKandung(chkDataCorrect,tvError,tvMandatory);
                }
            }
        });

        TTL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(mContext, dateDialog, currentTimeOCR
                        .get(Calendar.YEAR), currentTimeOCR.get(Calendar.MONTH),
                        currentTimeOCR.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //TextWatcher
        NIK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nik = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nama = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TTL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tmptLahir = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TTL2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ttl = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                jeniskelamin = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_goldar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                golongan_darah = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alamat = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_rtrw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rtrw = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_provinsi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                provinsi = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_kabkot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kota_kabupaten = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_kecamatan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kecamatan = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_desa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                desa_kelurahan = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_religion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                agama = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_status_kawin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                status_perkawinan = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_warga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kewarganegaraan = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_work.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pekerjaan = s.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_nama_ibuKandung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvMandatory.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                namaIbuKandung = charSequence.toString();
                if (isSessionZoom) {
                    JSONObject dataReq = dataReqOCR2();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnOCRNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nik = NIK.getText().toString().trim();
                nama = Nama.getText().toString().trim();
                tmptLahir = TTL.getText().toString().trim();
                ttl = TTL2.getText().toString().trim();
                Log.e("CEK","picturePath : "+picturePath);
                if (chkDataCorrect.isChecked() && flagMother) {
                    if (!picturePath.isEmpty() && !namaIbuKandung.isEmpty()) {
                        sweetAlertDialog.cancel();
                        sweetAlertDialog.dismissWithAnimation();
                        String fieldName = "ktp";
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
                        //dataReqOCR();
                        if (isSessionZoom) {
                            JSONObject reqOCR = new JSONObject();
                            try {
                                reqOCR.put("startValidasi", true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RabbitMirroring.MirroringSendKey(reqOCR);
                        }
                        processDukcapil();
                    }
                    else{
                        Toast.makeText(mContext, ""+getString(R.string.validate_nama_ibu), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (chkDataCorrect.isChecked() && !flagMother) {
                        Toast.makeText(mContext, getString(R.string.please_wait3), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, getString(R.string.accept_mother), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnOCRCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismissWithAnimation();
                IMG_BYTE = new byte[0];
                imageBytes = new byte[0];
                LL.setBackground(mContext.getResources().getDrawable(R.drawable.bg));
                btnNext.setVisibility(View.GONE);
                btnNext.setClickable(false);
                viewImage.setVisibility(View.GONE);
                chooseImage.setVisibility(View.VISIBLE);
                if (formCode == 22) {
                    llOR.setVisibility(View.GONE);
                    btnGallery.setVisibility(View.GONE);
                }
                imgDelete.setVisibility(View.GONE);
            }
        });

    }

    private JSONObject dataReqOCR2() {
        String[] pisahrtrw = rtrw.split("/");
        String rt = pisahrtrw[0];
        String rw = pisahrtrw[1];

        String tglLahir = "-";
        if (ttl.indexOf(",") > 0) {
            String[] sp = ttl.split(",");
            tglLahir = sp[1].trim();
        }
        else{
            tglLahir = ttl;
        }

        JSONObject datasReqOCR2 = new JSONObject();
        try {
            datasReqOCR2.put("noidentitas",nik);
            datasReqOCR2.put("kecamatan",kecamatan);
            datasReqOCR2.put("agama",agama);
            datasReqOCR2.put("jeniskelamin",jeniskelamin);
            datasReqOCR2.put("statusmenikah",status_perkawinan);
            datasReqOCR2.put("golongandarah",golongan_darah);
            datasReqOCR2.put("pekerjaan",pekerjaan);
            datasReqOCR2.put("alamatsesuaiidentitas",alamat);
            datasReqOCR2.put("kewarganegaraan",kewarganegaraan);
            datasReqOCR2.put("rt",rt);
            datasReqOCR2.put("rw",rw);
            datasReqOCR2.put("kelurahandesa",desa_kelurahan);
            datasReqOCR2.put("kabupaten",kota_kabupaten);
            datasReqOCR2.put("namasesuaiidentitas",nama);
            datasReqOCR2.put("provinsi",provinsi);
            datasReqOCR2.put("tempatlahir",tmptLahir);
            datasReqOCR2.put("tanggallahir",tglLahir);
            datasReqOCR2.put("namaibukandung",namaIbuKandung);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","datasReqOCR2 : "+ datasReqOCR2);

        return datasReqOCR2;
    }
    private JSONObject dataReqOCR() {
        try {
            datasReqOCR.put("noidentitas",nik);
            datasReqOCR.put("kecamatan",kecamatan);
            datasReqOCR.put("agama",agama);
            datasReqOCR.put("jeniskelamin",jeniskelamin);
            datasReqOCR.put("statusperkawinan",status_perkawinan);
            datasReqOCR.put("nama",nama);
            datasReqOCR.put("tempatlahir",tmptLahir);
            datasReqOCR.put("ttl",ttl);
            datasReqOCR.put("idDips", idDips);
            datasReqOCR.put("provinsi",provinsi);
            datasReqOCR.put("kotakabupaten",kota_kabupaten);
            datasReqOCR.put("golongandarah",golongan_darah);
            datasReqOCR.put("alamat",alamat);
            datasReqOCR.put("rtrw",rtrw);
            datasReqOCR.put("desakelurahan",desa_kelurahan);
            datasReqOCR.put("kewarganegaraan",kewarganegaraan);
            datasReqOCR.put("pekerjaan",pekerjaan);
            datasReqOCR.put("namaibukandung",namaIbuKandung);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","datasReqOCR : "+datasReqOCR.toString());

        sessions.saveOCR(datasReqOCR.toString());

        return datasReqOCR;
    }

    private JSONObject dataReqValidate() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("propinsi",provinsi);
            jsons.put("kabupaten",kota_kabupaten);
            jsons.put("nik",nik);
            jsons.put("namaLengkap",nama);
            jsons.put("tempatlahir",tmptLahir);
            jsons.put("tglLahir",ttl);
            jsons.put("jenisKelamin",jeniskelamin);
            jsons.put("namaLengkapIbu",namaIbuKandung);
            jsons.put("alamat",alamat);
            jsons.put("rt",rtrw);
            jsons.put("rw",rtrw);
            jsons.put("kelurahan",desa_kelurahan);
            jsons.put("kecamatan",kecamatan);
            jsons.put("statusKawin",status_perkawinan);
            jsons.put("jenisPekerjaan",pekerjaan);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","dataReqValidateIbu : "+jsons.toString());

        return jsons;
    }

    private JSONObject dataReqValidateFace() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("nik",nik);
            jsons.put("image",encodedImage);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","dataReqValidateFace : "+jsons.toString());

        return jsons;
    }

    private void processValidateIbuKandung(CheckBox chkDataCorrect, TextView tvError, TextView tvMandatory){
        JSONObject jsons = dataReqValidate();
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.validasiIbuKandung(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","response ValidateIbuKandung : "+response.code());
                if (response.isSuccessful()){
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
                    try {
                        String dataS = response.body().toString();
                        Log.e("HASIL ValidateIbuKandung",""+dataS);
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String status = dataObj.getString("status");
                        String msg = dataObj.getString("message");
                        if (status.equals("oke")){
                            flagMother = true;
                        }
                        else{
                            //loopValidMother++;
                            if (msg.contains("maksimum")) {
                                PopUpLimitValidateMother();
                            }
                            chkDataCorrect.setChecked(false);
                            flagMother = false;
                            tvError.setVisibility(View.VISIBLE);
                            tvMandatory.setVisibility(View.GONE);
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    chkDataCorrect.setChecked(false);
                    flagMother = false;
                    try {
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
                        String dataS = response.body().toString();
                        Log.d("HASIL ValidateIbuKandung",""+dataS);
                        JSONObject dataObj = new JSONObject(dataS);
                        String status = dataObj.getString("status");
                        String msg = dataObj.getString("message");
                        Toast.makeText(mContext, "Failed "+status+","+msg, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                chkDataCorrect.setChecked(false);
                flagMother = false;
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
                Toast.makeText(mContext, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processValidateNasabahFace() {
        JSONObject jsons = dataReqValidateFace();
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.validasiWajahNasabah(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","Response Code processValidateNasabahFace : "+response.code());
                if (response.isSuccessful()){
                    String dataS = response.body().toString();
                    Log.e("CEK",""+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }

                        String status = dataObj.getString("status");
                        String msg = dataObj.getString("message");
                        if (status.equals("oke")){
                            processDTOTT();
                        }
                        else{
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
                            dialogFailedValidation("IDEM");
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        Log.e("CEK","ERROR RESPON : "+dataS);
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
                            Log.e("CEK","ERROR RESPON 2 : "+dataS);
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //dialogFailedValidation("IDEM");
                    if (response.code() == 400) {
                        processDTOTT();
                    }
                    Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
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
            }
        });
    }

    private void processDukcapil(){
        JSONObject jsons = dataReqValidate();
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.validasiDukcapil(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEL","RESPONSE Code processDukcapil : "+response.code());
                if (response.isSuccessful()){
                    try {
                        String dataS = response.body().toString();
                        Log.d("HASIL VALIDASI DUKCAPIL",""+dataS);
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String status = dataObj.getString("status");
                        String msg = dataObj.getString("message");
                        if (status.equals("oke")){
                            processValidateNasabahFace();
                        }
                        else{
                            //Ketika Gagal melakukan validasi dukcapil
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
                            dialogFailedValidation("Dukcapil");
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
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
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        Log.e("CEK","ERROR RESPON : "+dataS);
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
                            Log.e("CEK","ERROR RESPON 2 : "+dataS);
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
                Toast.makeText(mContext, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dialogFailedValidation(String kasus) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);
        if (kasus.equals("swafotocheck") || kasus.equals("OCR")){
            btnCancelDialog.setVisibility(View.GONE);
            tvTitleDialog.setVisibility(View.GONE);
            imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_info));
            if (kasus.equals("swafotocheck") || kasus.equals("OCR")) {
                if (kasus.equals("OCR")) {
                    tvBodyDialog.setText(getString(R.string.ktpnotread));
                } else {
                    tvBodyDialog.setText(getString(R.string.capture_back));
                }
                btnConfirmDialog.setText(getString(R.string.ambil_gambar));
            }/* else {
                tvBodyDialog.setText(getString(R.string.validate_dukcapil));
                btnConfirmDialog.setText(getString(R.string.btn_validate_dukcapil));
            }*/
        }
        else{
            btnCancelDialog.setVisibility(View.VISIBLE);
            tvTitleDialog.setVisibility(View.VISIBLE);
            tvTitleDialog.setText(getString(R.string.failed));
            imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_warning));
            tvBodyDialog.setText(getString(R.string.validate_dtott));
            btnCancelDialog.setText(getString(R.string.call_center));
            btnConfirmDialog.setText("OK");
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismissWithAnimation();
                JSONObject reqOCR = new JSONObject();
                try {
                    reqOCR.put("startValidasi",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RabbitMirroring.MirroringSendKey(reqOCR);
                Intent dialPhoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1500977"));
                startActivity(dialPhoneIntent);
                ((Activity)mContext).finishAffinity();
            }
        });
        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.cancel();
                sweetAlertDialog.dismissWithAnimation();

                if (kasus.equals("swafotocheck")  || kasus.equals("OCR")) {
                    sessions.saveMedia(1);
                    chooseFromCamera();
                } else {
                    if (kasus.equals("Dukcapil")) {
                        JSONObject reqOCR = new JSONObject();
                        try {
                            reqOCR.put("startValidasi", false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RabbitMirroring.MirroringSendKey(reqOCR);
                    }
                    /*if (kasus.equals("Dukcapil")) {
                        PopUpOCR();
                    } else {*/
                        int ret = ZoomVideoSDK.getInstance().leaveSession(false);
                        sessions.clearPartData();
                        RabbitMirroring.MirroringSendEndpoint(99);
                        OutApps();
                    //}
                }
            }
        });
    }
    private void processDTOTT(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("propinsi",provinsi);
            jsons.put("kabupaten",kota_kabupaten);
            jsons.put("nik",nik);
            jsons.put("namaLengkap",nama);
            jsons.put("tempatlahir",tmptLahir);
            jsons.put("tglLahir",ttl);
            jsons.put("jenisKelamin",jeniskelamin);
            jsons.put("namaLengkapIbu",namaIbuKandung);
            jsons.put("alamat",alamat);
            jsons.put("rt",rtrw);
            jsons.put("rw",rtrw);
            jsons.put("kelurahan",desa_kelurahan);
            jsons.put("kecamatan",kecamatan);
            jsons.put("statusKawin",status_perkawinan);
            jsons.put("jenisPekerjaan",pekerjaan);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.validasiDttot(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        String dataS = response.body().toString();
                        Log.d("HASIL VALIDASI DTOTT",""+dataS);
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String status = dataObj.getString("status");
                        String msg = dataObj.getString("message");
                        if (status.equals("oke")){
                            CekDataByNIK();
                            //processFormDataAttachment(fieldName,filePath);
                        }
                        else{
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
                            dialogFailedValidation("DTOTT");
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
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
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        Log.e("CEK","ERROR RESPON : "+dataS);
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
                            Log.e("CEK","ERROR RESPON 2 : "+dataS);
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

            }
        });
    }

    private void CekDataByNIK(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("nama",nama);
            jsons.put("nik",nik);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEL","REQUEST CekDataByNIK : "+jsons.toString());

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.CekByNIK(requestBody,authAccess,exchangeToken);
        Log.e("CEL","URL CekDataByNIK : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEL","RESPONSE Code CekDataByNIK : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","RESPONSE CekDataByNIK : "+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        if (jsObj.has("token")) {
                            String accessToken = jsObj.getString("token");
                            String exchangeToken = jsObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        int err_code = jsObj.getInt("code");
                        String message = jsObj.getString("message");
                        if (err_code == 200) {
                            JSONObject dataObj = jsObj.getJSONObject("data");
                            idDips = dataObj.getString("idDips");
                            sessions.saveIdDips(idDips);

                            String getDataNasabah = sessions.getNasabah();
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
                            sessions.saveNasabah(dataNasabahObj.toString());
                            String getDataNasabah2 = sessions.getNasabah();
                            Log.e("CEK","getDataNasabah2 : "+getDataNasabah2);

                            if (dataObj.has("noCif")) {
                                if (!dataObj.isNull("noCif")) {
                                    String noCif = dataObj.getString("noCif");
                                    if (!noCif.isEmpty()) {
                                        sessions.saveNoCIF(noCif);
                                        sessions.saveIsCust(true);
                                        processValidationDataNasabah();
                                    } else {
                                        processFormDataAttachment2("ktp",picturePathCrop);
                                    }
                                } else {
                                    processFormDataAttachment2("ktp",picturePathCrop);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                        Log.e("CEK","ERROR RESPON : "+dataS);
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
                            Log.e("CEK","ERROR RESPON 2 : "+dataS);
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

    private void processValidationDataNasabah() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("namaLengkap",nama);
            jsons.put("nik",nik);
            jsons.put("tglLahir",ttl);
            jsons.put("tempatlahir",tmptLahir);
            jsons.put("namaLengkapIbu",namaIbuKandung);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.validasiDataNasabah(requestBody,authAccess,exchangeToken);
        Log.e("CEL","URL validasiDataNasabah : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEL","RESPONSE Code validasiDataNasabah : "+response.code());

                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","RESPONSE validasiDataNasabah : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String status = dataObj.getString("status");
                        String msg = dataObj.getString("message");
                        if (status.equals("oke")){
                            processFormDataAttachment2("ktp",picturePathCrop);
                        } else {
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
                            dialogFailedValidation("IDEM");
                        }
                    } catch (JSONException e) {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                        throw new RuntimeException(e);
                    }
                } else {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    dialogFailedValidation("IDEM");
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        Log.e("CEK","ERROR RESPON : "+dataS);
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
                            Log.e("CEK","ERROR RESPON 2 : "+dataS);
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
    private void processFormDataAttachment(String fieldName, String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);
        RequestBody requestidDips = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idDips));
        Log.e("CEK","fieldName : "+fieldName+" | filePath : "+filePath+" | requestidDips : "+requestidDips);
        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = null;
        MultipartBody multipartBody = null;
        String contentType = "";
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        multipartBody = new MultipartBody.Builder()
                .addPart(MultipartBody.Part.createFormData(fieldName,file.getName(),requestFile))
                .addPart(MultipartBody.Part.createFormData("idDips",null,requestidDips))
                .build();
        contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        call = API.formAttachment(contentType,authAccess,exchangeToken,multipartBody);

        Log.e("CEK","processFormDataAttachment call url : "+call.request().url());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processFormDataAttachment response code : "+response.code());
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
                    Log.e("CEK","processFormDataAttachment : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode == 200 || errCode == 202) {
                            if (isSessionZoom) {
                                if (formCode == 4 && flagOCR) {
                                    JSONObject dataReq = dataReqOCR2();
                                    JSONObject reqOCR = new JSONObject();
                                    try {
                                        reqOCR.put("ocr", dataReq);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    RabbitMirroring.MirroringSendKey(reqOCR);
                                    RabbitMirroring.MirroringSendEndpoint(22);
                                } else if (formCode == 22) {
                                    RabbitMirroring.MirroringSendEndpoint(6);
                                } else if (formCode == 6) {
                                    RabbitMirroring.MirroringSendEndpoint(7);
                                } else if (formCode == 7) {
                                    RabbitMirroring.MirroringSendEndpoint(8);
                                }
                            }

                            Bundle bundle = new Bundle();
                            if (formCode == 4 && flagOCR) {
                                if (!isSwafoto) {
                                    //sessions.saveKTP(encodedImage);
                                    sessions.saveFormCOde(22);
                                }
                            }
                            else if (formCode == 22) {
                                sessions.saveFormCOde(6);
                                //sessions.saveSWAFOTO(encodedImage);
                            }
                            else if (formCode == 6) {
                                sessions.saveFormCOde(7);
                                //sessions.saveNPWP(encodedImage);
                                bundle.putString("NPWP",npwp);
                            }
                            else if (formCode == 7) {
                                sessions.saveFormCOde(8);
                                //sessions.saveTTD(encodedImage);
                                bundle.putInt("form_id",10);
                                bundle.putString("NPWP",npwp);
                            }

                            if (getRequestCode != 0 && getRequestCode != REQUESTCODE_GALLERY) {
                                if (file.exists()) {
                                    try {
                                        file.getCanonicalFile().delete();
                                        if (file.exists()) {
                                            mContext.getApplicationContext().deleteFile(file.getName());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (!isSessionZoom && formCode == 22) {
                                Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                intent.putExtra("CUSTNAME","Customer");
                                startActivity(intent);
                                ((Activity) mContext).finishAffinity();
                            } else {
                                sendDataFragment(bundle, new frag_cif_new());
                            }
                        }
                        else {
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mediaFilePhoto != null) {
                        if (mediaFilePhoto.exists()) {
                            try {
                                mediaFilePhoto.getCanonicalFile().delete();
                                if (mediaFilePhoto.exists()) {
                                    getActivity().getApplicationContext().deleteFile(mediaFilePhoto.getName());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (mediaFilePhotoCropSwafoto != null) {
                        if (mediaFilePhotoCropSwafoto.exists()) {
                            try {
                                mediaFilePhotoCropSwafoto.getCanonicalFile().delete();
                                if (mediaFilePhotoCropSwafoto.exists()) {
                                    getActivity().getApplicationContext().deleteFile(mediaFilePhotoCropSwafoto.getName());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    if (fieldName.equals("ktp")) {
                        IMG_BYTE = new byte[0];
                    }
                    if (response.body() != null) {
                        Log.e("CEK","response body : "+ response.body());
                    } else {
                        Log.e("CEK","response errorBody : "+response.errorBody().toString());
                    }
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
                if (fieldName.equals("ktp")) {
                    IMG_BYTE = new byte[0];
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processFormDataAttachment2(String keys, String picturePath) {
        File file = new File(picturePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);
        RequestBody requestidDips = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idDips));
        Log.e("CEK","filePath : "+ picturePath +" | requestidDips : "+requestidDips);
        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = null;
        MultipartBody multipartBody = null;
        String contentType = "";
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        multipartBody = new MultipartBody.Builder()
                .addPart(MultipartBody.Part.createFormData(keys,file.getName(),requestFile))
                .addPart(MultipartBody.Part.createFormData("idDips",null,requestidDips))
                .build();
        contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        call = API.formAttachment(contentType,authAccess,exchangeToken,multipartBody);

        Log.e("CEK","processFormDataAttachment call url : "+call.request().url());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processFormDataAttachment response code : "+response.code());
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
                    Log.e("CEK","processFormDataAttachment : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode == 200 || errCode == 202) {
                            if (keys.equals("ktp")) {
                                processFormDataAttachment2("foto",picturePath);
                            } else if (keys.equals("foto")) {
                                processFormDataAttachment2("ttd",picturePathCrop);
                            }
                            if (!isSessionZoom && formCode == 22 && keys.equals("ttd")) {
                                Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                intent.putExtra("CUSTNAME",nama);
                                startActivity(intent);
                                ((Activity) mContext).finishAffinity();

                                if (mediaFilePhoto != null) {
                                    if (mediaFilePhoto.exists()) {
                                        try {
                                            mediaFilePhoto.getCanonicalFile().delete();
                                            if (mediaFilePhoto.exists()) {
                                                getActivity().getApplicationContext().deleteFile(mediaFilePhoto.getName());
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                if (mediaFilePhotoCropSwafoto != null) {
                                    if (mediaFilePhotoCropSwafoto.exists()) {
                                        try {
                                            mediaFilePhotoCropSwafoto.getCanonicalFile().delete();
                                            if (mediaFilePhotoCropSwafoto.exists()) {
                                                getActivity().getApplicationContext().deleteFile(mediaFilePhotoCropSwafoto.getName());
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (response.body() != null) {
                        Log.e("CEK","response body : "+ response.body());
                    } else {
                        Log.e("CEK","response errorBody : "+response.errorBody().toString());
                    }
                    //Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
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
    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ((Activity)mContext).overridePendingTransition(0,0);
        ((Activity)mContext).finishAffinity();
    }

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        swipe.setVisibility(View.GONE);
        TopBar.setVisibility(View.GONE);
        ll_head.setVisibility(View.VISIBLE);

        String noHandphone = "089783434XXX";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"XXX");
        }

        String contentText = textTitleOTP.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textTitleOTP.setText(contentText);

        tvFotoKTP.setText("One Time Password");

        otp.setAnimationEnable(true);
        otp.setPasswordHidden(true);
        otp.addTextChangedListener(new TextWatcher() {
            /*private boolean backSpaceOTP;
            private int lasLenOTP;*/

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //lasLenOTP = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*String wordOTP = s.toString();
                Log.e("CEK","wordOTP : "+wordOTP);
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    String getNumberOTP=wordOTP.replaceAll("[^0-9]", "");
                    if (getNumberOTP.length() > 1 && getNumberOTP.length() <= 6) {
                        getNumberOTP = getNumberOTP.substring(getNumberOTP.length()-1,getNumberOTP.length());
                    }
                    if (numberOTP.length() < 6) {
                        numberOTP += getNumberOTP;
                    }
                    JSONObject otpObj = new JSONObject();
                    try {
                        otpObj.put("otp",numberOTP);
                        rabbitMirroring.MirroringSendKey(otpObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp.length() == 6) {
                    numberOTP = otp.getText().toString();
                    JSONObject otpObj = new JSONObject();
                    try {
                        Log.e("CEK","numberOTP : "+numberOTP);
                        otpObj.put("otp",numberOTP);
                        RabbitMirroring.MirroringSendKey(otpObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                /*backSpaceOTP = lasLenOTP > s.length();
                Log.e("CEK", "backSpaceOTP : " + backSpaceOTP);
                if (backSpaceOTP) {
                    int lenOTP = numberOTP.length();
                    if (lenOTP > 0) {
                        numberOTP = numberOTP.substring(0, lenOTP - 1);
                    }
                }
                newString = myFilter(s.toString());
                otp.removeTextChangedListener(this);
                handler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        otp.setText(newString);
                    }
                };
                otp.addTextChangedListener(this);
                handler.postDelayed(myRunnable, 1500);
                if (otp.length() == 6 || otp.length() == 0){
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Log.d("TAG","STOP Loop");
                }*/
            }
        });

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(mContext, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    /*handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);*/
                    if (!transactionId.isEmpty()) {
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

    private void APISaveForm() {
        String valDataCIF = sessions.getCIF();
        JSONObject dataObjCIF = new JSONObject();
        try {
            JSONObject getObjValCIF = new JSONObject(valDataCIF);
            Log.e("CEK","APISaveForm CIF FULL objValCIF : "+ getObjValCIF);
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
                Log.e("CEK","APISaveForm code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","APISaveForm dataS : "+dataS);
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
                        processSendOTP();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                    //Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
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

        Log.e("CEK","processValidateOTP : "+ dataObjOTP);

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());
        Server.getAPIService().ValidateOTP(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processValidateOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processValidateOTP : "+dataS);
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
                    RabbitMirroring.MirroringSendKey(idFormObj);
                    processApprovalStatus();
                } else {
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
        Log.e("CEK", this+" processApprovalStatus PARAMS : "+idFormObj.toString());
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
                Log.e("CEK", this+" processApprovalStatus code : "+response.code());
                if (response.isSuccessful()) {
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
                    String dataS = response.body().toString();
                    Log.e("CEK","processApprovalStatus dataS : "+dataS);
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
                            Log.e("CEK","branchCode : "+branchCode);
                            if (!branchCode.isEmpty()) {
                                String getDataNasabah = sessions.getNasabah();
                                Log.e("CEK","getDataNasabah : "+getDataNasabah);
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
                    RabbitMirroring.MirroringSendEndpoint(13);
                    Bundle bundle = new Bundle();
                    bundle.putInt("formCode",13);
                    sendDataFragment(bundle,new frag_cif_resi());
                } else {
                    if (loopStatus >= 10) {
                        Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
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
                    if (loopStatus < 10) {
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
                        /*try {
                            Thread.sleep(10000);
                            processApprovalStatus();
                            loopStatus++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK", this+" processApprovalStatus onFailure : "+t.getMessage());
                if (loopStatus >= 10) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                if (loopStatus < 10) {
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

        Log.e("CEK","processSendOTP : "+ dataObjOTP);

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
                Log.e("CEK","processSendOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processSendOTP : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        transactionId = dataObj.getJSONObject("data").getString("transactionId");
                        RabbitMirroring.MirroringSendEndpoint(11);
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

    private Bitmap prosesOptimalImage(String picturePath, int perDiff) {
        File mediaFile = new File(picturePath);
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));
        Log.d("CEK", "file_size : "+file_size);

        if (perDiff == 1) {
            if (file_size > 4096) {
                perDiff = 8;
            } else if (file_size > 3072) {
                perDiff = 6;
            } else if (file_size > 2048) {
                perDiff = 4;
            } else if (file_size > 1024) {
                perDiff = 2;
            }
        }

        if (perDiff == 1) {
            viewImage.setImageBitmap(thumbnail);
        } else {
            thumbnail = getResizedBitmap(thumbnail, (thumbnail.getWidth() / perDiff), (thumbnail.getHeight() / perDiff));
        }

        /*try {
            byte[] downsizedImageBytes = getDownsizedImageBytes(thumbnail, thumbnail.getWidth(), thumbnail.getHeight());
            File mediaFile2 = createTemporaryFile(downsizedImageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        Log.e("CEK", "bitmapCrop.getWidth() : "+thumbnail.getWidth()+" | bitmapCrop.getHeight() : "+thumbnail.getHeight());
        if (thumbnail.getWidth() < 256 || thumbnail.getHeight() < 256) {
            perDiff--;
            prosesOptimalImage(picturePath,perDiff);
        }

        return thumbnail;
    }

    public byte[] getDownsizedImageBytes(Bitmap fullBitmap, int scaleWidth, int scaleHeight) throws IOException {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true);

        Log.e("CEK", "scaledBitmap.getWidth() : "+scaledBitmap.getWidth()+" | scaledBitmap.getHeight() : "+scaledBitmap.getHeight());

        // 2. Instantiate the downsized image content as a byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] downsizedImageBytes = baos.toByteArray();

        return downsizedImageBytes;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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
        viewImage.setImageBitmap(resizedBitmap);
        return resizedBitmap;
    }

    private void processSendImage(Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    for (int k = 0; k < 10; k++){
                        int onOfCamera = sessions.getCamera();
                        Log.d("CEK","onOfCamera loop-"+k+" : "+onOfCamera);
                        if (onOfCamera == 1) {
                            Log.d("CEK","MASUK CAMERA AKTIF");
                            /*Log.d("CEK","MASUK KIRIM IMAGE");
                            imgtoBase64(bitmap);*/
                            break;
                        }
                        Thread.sleep(500);
                    }
                    Log.d("CEK","MASUK KIRIM IMAGE");
                    imgtoBase64(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        String imgBase64 = "";
        String keys = "";
        Log.e("CEK", "formCode : "+formCode);
        if (formCode == 4) {
            imgBase64 = encodedImage;
            keys = "ktp";
            imgtoBase64OCR();
        } else if (formCode == 22) {
            keys = "swafoto";
            imgBase64 = encodedImage;
        } else if (formCode == 6) {
            keys = "npwp";
            imgBase64 = encodedImage;
            ocrNPWP();
        } else if (formCode == 7) {
            keys = "ttd";
            imgBase64 = encodedImage;
        }
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

    private void imgtoBase64OCR() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setClickable(true);
                imgDelete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
            }
        });
        ocrKTP();
    }

    private void ocrNPWP() {
        Log.d("Masuk OCR NPWP", "");
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("image", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.ocrNpwp(requestBody,authAccess,exchangeToken);
        Log.e("CEK", "url ocrNPWP : " + call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Response OCR",""+response.code());
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
                    Log.d("Response OCR",""+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        npwp = jsObj.getJSONObject("data").getString("npwpId");
                        if (jsObj.has("token")) {
                            String accessToken = jsObj.getString("token");
                            String exchangeToken = jsObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            }
        });
    }

    private void ocrKTP(){
        String baseImages = encodedImage;
        Log.d("Masuk OCR","ocrKTP : "+ocrKTP);
        if (ocrKTP) {
            baseImages = encodedImageCrop;
        }
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("image",baseImages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.ocrKtp(requestBody,authAccess,exchangeToken);
        Log.e("CEK","url ocrKTP : "+call.request().url());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Response OCR",""+response.code());
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
                    Log.d("Response OCR",""+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int errCode = jsObj.getInt("code");
                        String message = jsObj.getString("message");
                        if (jsObj.has("token")) {
                            String accessToken = jsObj.getString("token");
                            String exchangeToken = jsObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (errCode == 200) {
                            JSONObject dataObj = jsObj.getJSONObject("data");
                            datasReqOCR = dataObj;
                            if (dataObj.has("provinsi")) {
                                provinsi = dataObj.getString("provinsi");
                            }
                            if (dataObj.has("kota_kabupaten")) {
                                kota_kabupaten = dataObj.getString("kota_kabupaten");
                            }
                            if (dataObj.has("nik")) {
                                nik = dataObj.getString("nik");
                            }
                            if (dataObj.has("nama")) {
                                nama = dataObj.getString("nama");
                            }
                            if (dataObj.has("ttl")) {
                                ttl = dataObj.getString("ttl");
                            }
                            if (dataObj.has("jeniskelamin")) {
                                jeniskelamin = dataObj.getString("jeniskelamin");
                            }
                            if (dataObj.has("golongan_darah")) {
                                golongan_darah = dataObj.getString("golongan_darah");
                            }
                            if (dataObj.has("alamat")) {
                                alamat = dataObj.getString("alamat");
                            }
                            if (dataObj.has("rtrw")) {
                                rtrw = dataObj.getString("rtrw");
                            }
                            if (dataObj.has("desa_kelurahan")) {
                                desa_kelurahan = dataObj.getString("desa_kelurahan");
                            }
                            if (dataObj.has("kecamatan")) {
                                kecamatan = dataObj.getString("kecamatan");
                            }
                            if (dataObj.has("agama")) {
                                agama = dataObj.getString("agama");
                            }
                            if (dataObj.has("status_perkawinan")) {
                                status_perkawinan = dataObj.getString("status_perkawinan");
                            }
                            if (dataObj.has("kewarganegaraan")) {
                                kewarganegaraan = dataObj.getString("kewarganegaraan");
                            }
                            if (dataObj.has("pekerjaan")) {
                                pekerjaan = dataObj.getString("pekerjaan");
                            }

                            if (ttl.indexOf(",") > 0) {
                                String[] sp = ttl.split(",");
                                tmptLahir = sp[0].trim();
                            }

                            datasReqOCR.put("tempatlahir",tmptLahir);
                            datasReqOCR.put("kotakabupaten",kota_kabupaten);
                            datasReqOCR.put("golongandarah",golongan_darah);
                            datasReqOCR.put("desakelurahan",desa_kelurahan);
                            datasReqOCR.put("statusperkawinan",status_perkawinan);

                            datasReqOCR.remove("kota_kabupaten");
                            datasReqOCR.remove("golongan_darah");
                            datasReqOCR.remove("kota_kabupaten");
                            datasReqOCR.remove("desa_kelurahan");
                            datasReqOCR.remove("status_perkawinan");

                            flagOCR = true;
                            PopUpOCR();
                            JSONObject dataReq = dataReqOCR2();
                            JSONObject reqOCR = new JSONObject();
                            try {
                                reqOCR.put("ocr",dataReq);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RabbitMirroring.MirroringSendKey(reqOCR);
                        } else {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    flagOCR = false;
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        Log.e("CEK","ERROR RESPON : "+dataS);
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
                            Log.e("CEK","ERROR RESPON 2 : "+dataS);
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    dialogFailedValidation("OCR");
                    //Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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
                flagOCR = false;
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected boolean requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
                return false;
            }
        } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                }
            } else if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    private void chooseFromSD() {
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    private void chooseFromCamera() {
        picturePath = "";
        if (formCode == 22){
            Intent intent = new Intent(mContext, DipsCameraSource.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUESTCODE_SWAFOTO);
        } else {
            Intent intent = new Intent(mContext, DipsCameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUESTCODE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            getRequestCode = requestCode;
            Log.e("CEK","requestCode : "+requestCode+" | isCust : "+isCust);
            if (requestCode == REQUESTCODE_CAPTURE){
                Log.e("CEK","RETURN CAMERA");
                sessions.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);

                try {
                    mediaFilePhoto = createTemporaryFile(resultCamera);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                picturePath = mediaFilePhoto.getAbsolutePath();
                Log.e("CEK","onActivityResult picturePath : "+picturePath);
                LL.setBackgroundResource(0);
                if (formCode != 4) {
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setClickable(true);
                    imgDelete.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                }
                viewImage.setImageBitmap(bitmap);
                processSendImage(bitmap);
            }
            else if (requestCode == REQUESTCODE_GALLERY){
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = mContext.getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();

                Bitmap thumbnail = prosesOptimalImage(picturePath,1);

                ExifInterface exif = null;
                int rotation = 0;
                try {
                    exif = new ExifInterface(picturePath);
                    rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("CEK","rotation : "+rotation);
                Log.e("CEK","onActivityResult picturePath : "+picturePath);
                Log.e("CEK","REQUESTCODE_GALLERY : "+REQUESTCODE_GALLERY);

                if (REQUESTCODE_GALLERY == 201) {

                } else {
                    sessions.saveFlagUpDoc(true);
                    LL.setBackgroundResource(0);
                    if (formCode != 4) {
                        btnNext.setVisibility(View.VISIBLE);
                        //btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        btnNext.setClickable(true);
                        imgDelete.setVisibility(View.VISIBLE);
                        viewImage.setVisibility(View.VISIBLE);
                        chooseImage.setVisibility(View.GONE);
                    }
                    imgtoBase64(thumbnail);
                    /*if (formCode == 4) {
                        imgtoBase64OCR();
                    }*/
                }
            }
            else if (requestCode == REQUESTCODE_SWAFOTO){
                sessions.saveFlagUpDoc(true);
                String filePaths = data.getStringExtra("result_camera");
                String filePathsCrop = data.getStringExtra("result_cropImage");
                Bitmap bitmap = BitmapFactory.decodeFile(filePaths);
                Bitmap bitmapCrop = BitmapFactory.decodeFile(filePathsCrop);

                if (ocrKTP) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapCrop.compress(Bitmap.CompressFormat.JPEG,100, baos);
                    byte[] imageBytesCrop = baos.toByteArray();
                    encodedImageCrop = Base64.encodeToString(imageBytesCrop, Base64.NO_WRAP);
                }

                mediaFilePhoto = new File(filePaths);
                mediaFilePhotoCropSwafoto = new File(filePathsCrop);
                picturePath = filePaths;
                picturePathCrop = filePathsCrop;

                LL.setBackgroundResource(0);
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setClickable(true);
                imgDelete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                viewImage.setImageBitmap(bitmap);
                processSendImage(bitmap);
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
                processSwafotoCheck();
            }
        }
    }

    private void processSwafotoCheck() {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), mediaFilePhoto);
        RequestBody requestFileCrop = RequestBody.create(MediaType.parse("image/jpeg"), mediaFilePhotoCropSwafoto);

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = null;
        MultipartBody multipartBody = null;
        String contentType = "";
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        multipartBody = new MultipartBody.Builder()
                .addPart(MultipartBody.Part.createFormData("firstImage", mediaFilePhoto.getName(), requestFile))
                .addPart(MultipartBody.Part.createFormData("secondImage", mediaFilePhotoCropSwafoto.getName(), requestFileCrop))
                .build();
        contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        call = API.swafotoCheck(contentType, authAccess, exchangeToken, multipartBody);

        Log.e("CEK", "processSwafotoCheck call url : " + call.request().url());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processSwafotoCheck response code : "+response.code());
                if (response.isSuccessful()) {
                    btnNext.setClickable(true);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    String dataS = response.body().toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (ocrKTP) {
                                imgtoBase64OCR();
                            }
                        }
                    },500);
                } else {
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
                    String msg = "";
                    if (response.body() != null) {
                        String dataS = response.body().toString();
                        try {
                            Log.e("CEK","processSwafotoCheck DIATAS 300 dataS : "+dataS);
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
                                Log.e("CEK","processSwafotoCheck DIATAS 300 dataS-2 : "+dataS);
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.e("CEK","processSwafotoCheck DIATAS 300 : "+msg);
                    btnNext.setClickable(false);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                    //Toast.makeText(mContext, R.string.capture_back,Toast.LENGTH_SHORT).show();
                    dialogFailedValidation("swafotocheck");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
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
        });
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.close();

        return mediaFile;
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
