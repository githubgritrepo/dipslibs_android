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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.HideSoftKeyboard;
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
    private HorizontalScrollView hsvHead;
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
    private LinearLayout capture_eKTP;
    private Button btnUpdateData;
    private ImageView viewImage;
    private LinearLayout LL;
    private LinearLayout chooseImage;
    private TextView tvAlertDoc;
    private ImageView imgDelete;
    private LinearLayout llOR;
    private LinearLayout llFormBuild;
    private LinearLayout llDataCorrectForm;
    private CheckBox chkDataCorrectForm;
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
    private String imgPathKTP = "";
    private String picturePath = "";
    private String picturePathCrop = "";
    private byte[] imageBytes = new byte[0];
    private String encodedImage;
    private String encodedImageCrop;
    private boolean flagOCR = false;
    private String tmptLahir = "-";
    private String provinsi = "";
    private String kodepos = "";
    private String kota_kabupaten = "";
    private String nik = "";
    private String nama = "";
    private String ttl = "";
    private String jeniskelamin = "";
    private String golongan_darah = "";
    private String alamat = "";
    private String rtrw = "";
    private String desa_kelurahan = "";
    private String kecamatan = "";
    private String agama = "";
    private String status_perkawinan = "";
    private String kewarganegaraan = "";
    private String pekerjaan = "";
    private String namaIbuKandung = "";
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
    private File mediaImgKTP = null;
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
    private boolean swaOCR = false;
    private boolean flagMother = false;
    private final int loopValidMother = 0;
    private TextView tvFeKTP = null;
    private RelativeLayout rlContent;
    private String tglTerbit = "-";
    private Button btnOCRNext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();
        ConnectionRabbitHttp.init(mContext);

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_cif, container, false);

        inclHead = views.findViewById(R.id.inclHead);
        TopBar = views.findViewById(R.id.TopBar);
        hsvHead = (HorizontalScrollView) views.findViewById(R.id.hsvHead);

        ll_head = views.findViewById(R.id.ll_head);
        tvAlertDoc = views.findViewById(R.id.tvAlertDoc);
        tvFotoKTP = views.findViewById(R.id.tvFotoKTP);
        iconKtp = views.findViewById(R.id.icon_ktp);
        tvFeKTP = (TextView) views.findViewById(R.id.tvFeKTP);
        iconSwafoto = views.findViewById(R.id.icon_swafoto);
        iconNpwp = views.findViewById(R.id.icon_npwp);
        iconSignature = views.findViewById(R.id.icon_signature);
        iconForm = views.findViewById(R.id.icon_form);
        capture_eKTP = (LinearLayout) views.findViewById(R.id.capture_eKTP);
        btnUpdateData = (Button) views.findViewById(R.id.btnUpdateData);

        rlContent = (RelativeLayout) views.findViewById(R.id.rlContent);

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

        llDataCorrectForm = (LinearLayout) views.findViewById(R.id.llDataCorrect);
        chkDataCorrectForm = (CheckBox) views.findViewById(R.id.chkDataCorrect);

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
        tglTerbit = "-";

        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            }

            if (getArguments().containsKey("pathKTP")) {
                imgPathKTP = getArguments().getString("pathKTP");
                mediaImgKTP = new File(imgPathKTP);
            }

            if (getArguments().containsKey("NPWP")) {
                npwp = getArguments().getString("NPWP");
            }

            if (getArguments().containsKey("swaOCR")) {
                swaOCR = getArguments().getBoolean("swaOCR");
            }
            if (getArguments().containsKey("ocrKTP")) {
                ocrKTP = getArguments().getBoolean("ocrKTP");
            }
            if (getArguments().containsKey("picturePath")) {
                picturePath = getArguments().getString("picturePath");
                mediaFilePhoto = new File(picturePath);
            }
            if (getArguments().containsKey("picturePathCrop")) {
                picturePathCrop = getArguments().getString("picturePathCrop");
                mediaFilePhotoCropSwafoto = new File(picturePathCrop);
            }
        }

        if (formCode == 22) {
            keysData = "dataKtp";
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
            iconKtp.setVisibility(View.GONE);
            tvFeKTP.setVisibility(View.GONE);
        }
        else {
            TopBar.setVisibility(View.GONE);
            ll_head.setVisibility(View.VISIBLE);
        }

        if ((formCode > 7 && formCode != 22) || (formCode == 22 && isSessionZoom)) {
            dataFormCIF = new JSONObject();
            inclBodyUpload.setVisibility(View.GONE);
            llFormBuild.setVisibility(View.VISIBLE);
            if (formCode == 8) {
                //form_id = 10;
                form_id = 74;
            } else if (formCode == 22) {
                form_id = 72;
                btnProses.setEnabled(false);
                btnProses.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
            }

            iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            if (formCode == 22) {
                iconKtp.setVisibility(View.GONE);
                tvFeKTP.setVisibility(View.GONE);
                ll_head.setVisibility(View.VISIBLE);
                capture_eKTP.setVisibility(View.VISIBLE);
                llDataCorrectForm.setVisibility(View.VISIBLE);

                chkDataCorrectForm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chkDataCorrectForm.isChecked()) {
                            btnProses.setEnabled(true);
                            btnProses.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        } else {
                            btnProses.setEnabled(false);
                            btnProses.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                        }
                    }
                });
                btnUpdateData.setAllCaps(false);
                btnUpdateData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chooseFromCamera();
                        Bundle bundle = new Bundle();
                        bundle.putString("fragDest","cifNew");
                        bundle.putInt("formCode", 4); //4 Upload KTP, 22 Swafoto
                        Fragment fragment = new frag_swafoto();
                        fragment.setArguments(bundle);
                        getFragmentPageBackStack(fragment);
                    }
                });
                tvFotoKTP.setText(getResources().getText(R.string.pembukaan_account));
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            } else {
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconForm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            }

            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    tglTerbit = "-";
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
                btnNext.setText(mContext.getResources().getString(R.string.lewati));
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

                    if (mediaImgKTP != null) {
                        if (mediaImgKTP.exists()) {
                            try {
                                mediaImgKTP.getCanonicalFile().delete();
                                if (mediaImgKTP.exists()) {
                                    getActivity().getApplicationContext().deleteFile(mediaImgKTP.getName());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

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
                            ConnectionRabbitHttp.mirroringKey(reqOCR);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("pathKTP",picturePath);
                            bundle.putBoolean("swaOCR",true);
                            sessions.saveFormCOde(22);
                            if (isSessionZoom) {
                                ConnectionRabbitHttp.mirroringEndpoint(7);
                            }
                            sendDataFragment(bundle, new frag_cif_new());
                            //Toast.makeText(mContext, "Maaf, OCR masih dalam proses...!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if ((imageBytes.length == 0 && formCode == 6)){
                        Bundle bundle = new Bundle();
                        bundle.putString("NPWP",npwp);
                        sessions.saveFormCOde(7);
                        if (isSessionZoom) {
                            ConnectionRabbitHttp.mirroringEndpoint(7);
                        }
                        sendDataFragment(bundle, new frag_cif_new());
                    }
                    else {
                        if (formCode == 22) {
                            if (swaOCR && flagOCR) {
                                PopUpOCR();
                                JSONObject dataReq = dataReqOCR2();
                                JSONObject reqOCR = new JSONObject();
                                try {
                                    reqOCR.put("ocr",dataReq);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //RabbitMirroring.MirroringSendKey(reqOCR);
                                ConnectionRabbitHttp.mirroringKey(reqOCR);
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

                    if (flagNext) {
                        HideSoftKeyboard.hideSoftKeyboard(getActivity());
                        processNext();
                    }
                }
            }
        });
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
    public void onDestroy() {
        super.onDestroy();

        if (isSessionZoom) {
            RabbitMirroring.closeThreadConnection();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void processNext() {
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

            if (sessions.getNasabah() != null) {
                String getDataNasabah = sessions.getNasabah();
                JSONObject dataEKTP = new JSONObject(getDataNasabah);
                for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                    String key = iter.next();
                    String nameDataEl = key;
                    int indx = key.indexOf("(");
                    if (indx >= 0) {
                        nameDataEl = key.substring(0,indx);
                    }

                    if (nameDataEl.contains("nama") && nameDataEl.contains("identitas")) {
                        dataEKTP.put("namaCust",objEl.getString(key));
                    } else if (nameDataEl.contains("alamat") && nameDataEl.contains("identitas")) {
                        dataEKTP.put("address1",objEl.getString(key));
                    } else if (nameDataEl.equals("rt")) {
                        dataEKTP.put("address2",objEl.getString(key));
                    } else if (nameDataEl.equals("rw")) {
                        if (dataEKTP.has("address2")) {
                            String datRT = dataEKTP.getString("address2");
                            String datRTRW = datRT + objEl.getString(key);
                            dataEKTP.put("address2",datRTRW);
                        }
                    } else if (nameDataEl.contains("kelurahan")) {
                        dataEKTP.put("address3",objEl.getString(key));
                    } else if (nameDataEl.contains("kecamatan")) {
                        dataEKTP.put("address4",objEl.getString(key));
                    } else if (nameDataEl.contains("kabupaten")) {
                        dataEKTP.put("address5",objEl.getString(key));
                    } else if (nameDataEl.contains("provinsi")) {
                        dataEKTP.put("propinsi",objEl.getString(key));
                        dataEKTP.put("Prov1",objEl.getString(key));
                    } else if (nameDataEl.contains("warganegara")) {
                        String negaraAsal = objEl.getString(key);
                        if (negaraAsal.equals("WNI")) {
                            negaraAsal = "ID";
                        }
                        dataEKTP.put("negaraAsal",negaraAsal);
                    } else if (nameDataEl.contains("kelamin")) {
                        dataEKTP.put("jenisKelamin",objEl.getString(key));
                    } else if (nameDataEl.contains("agama")) {
                        dataEKTP.put("agama",objEl.getString(key));
                    } else if (nameDataEl.contains("status") && (nameDataEl.contains("menikah") || nameDataEl.contains("kawin"))) {
                        dataEKTP.put("statusNikah",objEl.getString(key));
                    } else if (nameDataEl.contains("pendidikan")) {
                        dataEKTP.put("pendAkhir",objEl.getString(key));
                    } else if ((nameDataEl.contains("no") || nameDataEl.contains("nomor")) && nameDataEl.contains("identitas")) {
                        dataEKTP.put("nomorId",objEl.getString(key));
                    }
                }
                sessions.saveNasabah(dataEKTP.toString());
            }

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
                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
                    ConnectionRabbitHttp.mirroringEndpoint(intLayoutSelf);
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
        } else if (formCode == 22) {
            Bundle bundle = new Bundle();
            sessions.saveFormCOde(6);
            if (isSessionZoom) {
                ConnectionRabbitHttp.mirroringEndpoint(6);
            }
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
    }

    private void processGetForm(int formId) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                if (response.isSuccessful()) {
                    btnProses.setVisibility(View.VISIBLE);
                    String dataS = response.body().toString();
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
                        MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = MyParserFormBuilder.getForm();
                        processValidationActionForm();
                        dataFormCIF.put(keysData,objEl);
                        reqFormMirroring = dataReqFormMirroring();
                        ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
                        if (formCode == 8) {
                            processMatchDataFromeKTP();
                            getKodePos();

                        } else if (formCode == 22) {
                            if (ocrKTP) {
                                processMatchData();
                                processDataFromOCR();
                            } else {
                                processMatchDataFromeKTP();
                                getKodePos();
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
                                if (indx >= 0) {
                                    nameDataEl = nameDataEl.substring(0,indx);
                                }
                                if (idEl == idDataEl) {
                                    if (llFormBuild.getChildAt(i) instanceof EditText) {
                                        EditText ed = (EditText) llFormBuild.getChildAt(i);

                                        for(Iterator<String> iter = dataNasabah.keys(); iter.hasNext();) {
                                            if (iter.hasNext()) {
                                                String key = iter.next();
                                                String lowerKey = key.toLowerCase();
                                                if (nameDataEl.equals(lowerKey)) {
                                                    String valEl = dataNasabah.getString(key);
                                                    if (valEl.equals("null") || valEl == null) {
                                                        valEl = "";
                                                    }
                                                    ed.setText(valEl);
                                                    break;
                                                }
                                            }
                                        }

                                        if (nameDataEl.contains("nama") && nameDataEl.contains("identitas")) {
                                            String valEl = "";
                                            if(dataNasabah.has("namaCust")) {
                                                valEl = dataNasabah.getString("namaCust");
                                            } else if(dataNasabah.has("namaLengkap")) {
                                                valEl = dataNasabah.getString("namaLengkap");
                                            }
                                            ed.setText(valEl);
                                        } else if ((nameDataEl.contains("no") || nameDataEl.contains("nomor")) && nameDataEl.contains("identitas")) {
                                            if(dataNasabah.has("nomorId")) {
                                                String valEl = dataNasabah.getString("nomorId");
                                                ed.setText(valEl);
                                            }
                                        } else if ((nameDataEl.contains("no") || nameDataEl.contains("nomor")) && (nameDataEl.contains("ponsel") || nameDataEl.contains("handphone"))) {
                                            if(dataNasabah.has("noHandphone")) {
                                                String valEl = dataNasabah.getString("noHandphone");
                                                if (valEl.equals("null") || valEl == null) {
                                                    valEl = "";
                                                }
                                                ed.setText(valEl);
                                            } else if (dataNasabah.has("noHp")) {
                                                String valEl = dataNasabah.getString("noHp");
                                                if (valEl.equals("null") || valEl == null) {
                                                    valEl = "";
                                                }
                                                ed.setText(valEl);
                                            }
                                        }
                                        else if ((nameDataEl.contains("no") || nameDataEl.contains("nomor")) && nameDataEl.contains("telepon")) {
                                            if(dataNasabah.has("noTelp")) {
                                                String valEl = dataNasabah.getString("noTelp");
                                                ed.setText(valEl);
                                            }
                                        }
                                        else if (nameDataEl.contains("tanggal") && nameDataEl.contains("lahir")) {
                                            if (dataNasabah.has("tglLahir")) {
                                                String gettglLahir = dataNasabah.getString("tglLahir");
                                                if (gettglLahir.contains("-")) {
                                                    String[] sp = gettglLahir.split("-");
                                                    if (sp[0].trim().length() == 2) {
                                                        ed.setText(gettglLahir);

                                                        String tahun = sp[2].trim();
                                                        String bln = sp[1].trim();
                                                        String tgl = sp[0].trim();

                                                        if (!tahun.isEmpty()) {
                                                            if(tahun.matches("\\d+(?:\\.\\d+)?")) {
                                                                int intThn = Integer.parseInt(tahun);
                                                                int addThn = intThn + 17;
                                                                tglTerbit = tgl+"-"+bln+"-"+addThn;
                                                            }
                                                        }
                                                    } else if (sp[0].trim().length() == 4) {
                                                        String tahun = sp[0].trim();
                                                        String bln = sp[1].trim();
                                                        String tgl = sp[2].trim();
                                                        String valEl = tgl+"-"+bln+"-"+tahun;
                                                        ed.setText(valEl);

                                                        if (!tahun.isEmpty()) {
                                                            if(tahun.matches("\\d+(?:\\.\\d+)?")) {
                                                                int intThn = Integer.parseInt(tahun);
                                                                int addThn = intThn + 17;
                                                                tglTerbit = tgl+"-"+bln+"-"+addThn;
                                                            }
                                                        }
                                                    }
                                                } else if (gettglLahir.charAt(0) != '0') {
                                                    String tahun = gettglLahir.substring(0, 4);
                                                    String bln = gettglLahir.substring(4, 6);
                                                    String tgl = gettglLahir.substring(6, 8);
                                                    String valEl = tgl+"-"+bln+"-"+tahun;
                                                    ed.setText(valEl);

                                                    if (!tahun.isEmpty()) {
                                                        if(tahun.matches("\\d+(?:\\.\\d+)?")) {
                                                            int intThn = Integer.parseInt(tahun);
                                                            int addThn = intThn + 17;
                                                            tglTerbit = tgl+"-"+bln+"-"+addThn;
                                                        }
                                                    }
                                                } else {
                                                    String tahun = gettglLahir.substring(6, 8);
                                                    String bln = gettglLahir.substring(4, 6);
                                                    String tgl = gettglLahir.substring(0, 4);
                                                    String valEl = tgl+"-"+bln+"-"+tahun;
                                                    ed.setText(valEl);

                                                    if (!tahun.isEmpty()) {
                                                        if(tahun.matches("\\d+(?:\\.\\d+)?")) {
                                                            int intThn = Integer.parseInt(tahun);
                                                            int addThn = intThn + 17;
                                                            tglTerbit = tgl+"-"+bln+"-"+addThn;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if (nameDataEl.contains("alamat") && nameDataEl.contains("usaha")) {
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
                                        }
                                        else if (nameDataEl.contains("alamat") && nameDataEl.contains("identitas")) {
                                            String valEl = "";
                                            if (dataNasabah.has("address1")) {
                                                valEl = dataNasabah.getString("address1");
                                            } else if(dataNasabah.has("alamat")) {
                                                valEl = dataNasabah.getString("alamat");
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.equals("rt")) {
                                            String valEl = "";
                                            if(dataNasabah.has("rt")) {
                                                valEl = dataNasabah.getString("rt");
                                            } else if (dataNasabah.has("address2")) {
                                                String address2 = dataNasabah.getString("address2");
                                                if (address2.length() == 6) {
                                                    valEl = address2.substring(0, 3);
                                                } else if (address2.length() > 6) {
                                                    if (address2.toLowerCase().contains("rt")) {
                                                        int indxRT = address2.toLowerCase().indexOf("rt");
                                                        String getRT = "";
                                                        if (address2.toLowerCase().contains("rw")) {
                                                            int indxRW = address2.toLowerCase().indexOf("rw");
                                                            getRT = address2.substring(indxRT + 2, indxRW);
                                                        } else {
                                                            getRT = address2.substring(indxRT + 2);
                                                        }
                                                        valEl = getRT.replace(".", "").replace(" ", "");
                                                    }
                                                }
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.equals("rw")) {
                                            String valEl = "";
                                            if(dataNasabah.has("rw")) {
                                                valEl = dataNasabah.getString("rw");
                                            } else if (dataNasabah.has("address2")) {
                                                String address2 = dataNasabah.getString("address2");
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
                                        } else if (nameDataEl.contains("kelurahan")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kelurahan")) {
                                                valEl = dataNasabah.getString("kelurahan");
                                            } else if (dataNasabah.has("address3")) {
                                                valEl = dataNasabah.getString("address3");
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.contains("kecamatan")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kecamatan")) {
                                                valEl = dataNasabah.getString("kecamatan");
                                            } else if (dataNasabah.has("address4")) {
                                                valEl = dataNasabah.getString("address4");
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.contains("kabupaten")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kabupaten")) {
                                                valEl = dataNasabah.getString("kabupaten");
                                            } else if (dataNasabah.has("address5")) {
                                                valEl = dataNasabah.getString("address5");
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.contains("provinsi")) {
                                            String valEl = "";
                                            if(dataNasabah.has("propinsi")) {
                                                valEl = dataNasabah.getString("propinsi");
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.contains("kodepos")) {
                                            String valEl = "";
                                            if(dataNasabah.has("kodePos")) {
                                                valEl = dataNasabah.getString("kodePos");
                                            } else if (dataNasabah.has("zipCode")) {
                                                valEl = dataNasabah.getString("zipCode");
                                            }
                                            if (!valEl.isEmpty()) {
                                                int intZipCode = Integer.parseInt(valEl);
                                                ed.setText(String.valueOf(intZipCode));
                                            }
                                        } else if (nameDataEl.contains("jumlah") && nameDataEl.contains("anak")) {
                                            if(dataNasabah.has("jumAnak")) {
                                                String valEl = dataNasabah.getString("jumAnak");
                                                if (!valEl.isEmpty()) {
                                                    int intvalEl = Integer.parseInt(valEl);
                                                    ed.setText(String.valueOf(intvalEl));
                                                }
                                            }
                                        } else if (nameDataEl.contains("jumlah") && nameDataEl.contains("istri")) {
                                            if(dataNasabah.has("jumIstri")) {
                                                String valEl = dataNasabah.getString("jumIstri");
                                                if (!valEl.isEmpty()) {
                                                    int intvalEl = Integer.parseInt(valEl);
                                                    ed.setText(String.valueOf(intvalEl));
                                                }
                                            }
                                        } else if (nameDataEl.contains("jumlah") && nameDataEl.contains("tanggung")) {
                                            if(dataNasabah.has("jumTanggung")) {
                                                String valEl = dataNasabah.getString("jumTanggung");
                                                if (!valEl.isEmpty()) {
                                                    int intvalEl = Integer.parseInt(valEl);
                                                    ed.setText(String.valueOf(intvalEl));
                                                }
                                            }
                                        } else if (nameDataEl.contains("kelamin")) {
                                            String valEl = "";
                                            if(dataNasabah.has("jenisKelamin")) {
                                                valEl = dataNasabah.getString("jenisKelamin");
                                            }
                                            ed.setText(valEl);
                                        } else if (nameDataEl.contains("agama")) {
                                            if (dataNasabah.has("agama")) {
                                                String valEl = dataNasabah.getString("agama");
                                                ed.setText(valEl);
                                            }
                                        } else if (nameDataEl.contains("status") && (nameDataEl.contains("menikah") || nameDataEl.contains("kawin"))) {
                                            if (dataNasabah.has("statusKawin")) {
                                                String valEl = dataNasabah.getString("statusKawin");
                                                ed.setText(valEl);
                                            } else if (dataNasabah.has("statusNikah")) {
                                                String valEl = dataNasabah.getString("statusNikah");
                                                ed.setText(valEl);
                                            }
                                        } else if (nameDataEl.contains("npwp")) {
                                            if (dataNasabah.has("npwp")) {
                                                String valEl = dataNasabah.getString("npwp");
                                                ed.setText(valEl);
                                            }
                                        } else if (nameDataEl.contains("nama") && nameDataEl.contains("ibu")) {
                                            if (dataNasabah.has("namaIbu")) {
                                                String valEl = dataNasabah.getString("namaIbu");
                                                ed.setText(valEl);
                                            }
                                        } else if (nameDataEl.contains("nomor") && nameDataEl.contains("identitas")) {
                                            if (dataNasabah.has("nik")) {
                                                String valEl = dataNasabah.getString("nik");
                                                ed.setText(valEl);
                                            }
                                        } else if (nameDataEl.contains("jenis") && nameDataEl.contains("identitas")) {
                                            ed.setText("KTP");
                                        } else if (nameDataEl.contains("tanggal") && nameDataEl.contains("berakhir")) {
                                            ed.setText("Seumur Hidup");
                                        } else if (nameDataEl.contains("warganegara")) {
                                            ed.setText("WNI");
                                            /*if (dataNasabah.has("negaraAsal")) {
                                                String negaraAsal = dataNasabah.getString("negaraAsal");
                                                String warganegara = "WNI";
                                                if (!negaraAsal.equals("ID")) {
                                                    warganegara = "WNA";
                                                }
                                                ed.setText(warganegara);
                                            } else {
                                                ed.setText("WNI");
                                            }*/
                                        } else if (nameDataEl.contains("negara")) {
                                            ed.setText("Indonesia");
                                            /*if (dataNasabah.has("negaraAsal")) {
                                                String negaraAsal = dataNasabah.getString("negaraAsal");
                                                String negara = "";
                                                if (negaraAsal.equals("ID")) {
                                                    negara = "Indonesia";
                                                } else if (negaraAsal.equals("US")) {
                                                    negara = "United States of America";
                                                } else if (negaraAsal.equals("CN")) {
                                                    negara = "China";
                                                } else {
                                                    negara = "-";
                                                }
                                                ed.setText(negara);
                                            } else {
                                                ed.setText("Indonesia");
                                            }*/
                                        } else if (nameDataEl.contains("tanggalterbit")) {
                                            ed.setText(tglTerbit);
                                        }

                                    } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);

                                            String valEl = "";
                                            if (dataNasabah.has(nameDataEl)) {
                                                valEl = dataNasabah.getString(nameDataEl);
                                            } else if (nameDataEl.contains("kelamin")) {
                                                if (dataNasabah.has("jenisKelamin")) {
                                                    valEl = dataNasabah.getString("jenisKelamin");
                                                }
                                            } else if (nameDataEl.contains("status") && (nameDataEl.contains("menikah") || nameDataEl.contains("kawin"))) {
                                                if (dataNasabah.has("statusNikah")) {
                                                    valEl = dataNasabah.getString("statusNikah");
                                                }
                                            } else if (nameDataEl.contains("pendidikan")) {
                                                if (dataNasabah.has("pendAkhir")) {
                                                    valEl = dataNasabah.getString("pendAkhir");
                                                }
                                            } else if (nameDataEl.contains("cabang")) {
                                                if (dataNasabah.has("branchCode")) {
                                                    valEl = dataNasabah.getString("branchCode");
                                                }
                                            } else if (nameDataEl.contains("kerja")) {
                                                if (dataNasabah.has("jenisKerja")) {
                                                    valEl = dataNasabah.getString("jenisKerja");
                                                }
                                            } else if (nameDataEl.contains("bidang") && nameDataEl.contains("usaha")) {
                                                if (dataNasabah.has("badanUsaha")) {
                                                    valEl = dataNasabah.getString("badanUsaha");
                                                }
                                            } else if (nameDataEl.contains("jabatan")) {
                                                if (dataNasabah.has("jobPosition")) {
                                                    valEl = dataNasabah.getString("jobPosition");

                                                }
                                            } else if (nameDataEl.equals("sumberdana")) {
                                                if (dataNasabah.has("sourceIncome")) {
                                                    valEl = dataNasabah.getString("sourceIncome");
                                                }
                                            } else if (nameDataEl.contains("tujuan") && nameDataEl.contains("pengguna")) {
                                                if (dataNasabah.has("sourceFund")) {
                                                    valEl = dataNasabah.getString("sourceFund");
                                                }
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

                                        } else {
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
                                    else if (nameDataEl.contains("kode") && nameDataEl.contains("pos")){
                                        edKodePos = ed;
                                    }
                                    ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            if (nameDataEl.contains("kodepos")) {
                                                if (desa_kelurahan != null) {
                                                    if (b && !desa_kelurahan.isEmpty()) {
                                                        getKodePos();
                                                    }
                                                }
                                            }
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
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                if (nameDataEl.contains("provinsi")){
                                                    provinsi = charSequence.toString();
                                                    kodepos = "";
                                                } else if (nameDataEl.contains("kabupaten") || nameDataEl.contains("kota")) {
                                                    kota_kabupaten = charSequence.toString();
                                                    kodepos = "";
                                                }
                                                else if(nameDataEl.contains("kecamatan")){
                                                    kecamatan = charSequence.toString();
                                                    kodepos = "";
                                                }
                                                else if (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa")){
                                                    desa_kelurahan = charSequence.toString();
                                                    kodepos = "";
                                                }
                                                /*if (desa_kelurahan != null) {
                                                    if (!desa_kelurahan.isEmpty()) {
                                                        getKodePos();
                                                    }
                                                }*/
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
                                            String dataText = s.toString();

                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
                                                ed.removeTextChangedListener(this);
                                                backSpaceChar = lasLenChar > s.length();
                                                if (!backSpaceChar) {
                                                    String dataNPWP = dataText;
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
                                            } else if (nameDataEl.equals("rt") || nameDataEl.equals("rw")) {
                                                if (!dataText.isEmpty()) {
                                                    if (!dataText.equals("00") && !dataText.equals("0")) {
                                                        ed.removeTextChangedListener(this);
                                                        if (dataText.contains("/")) {
                                                            dataText = dataText.replace("/", "");
                                                        }
                                                        dataText = String.format("%03d", Integer.parseInt(dataText));
                                                        try {
                                                            objEl.put(nameDataEl, dataText);
                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                        ed.setText(dataText);
                                                        ed.setSelection(dataText.length());
                                                        ed.addTextChangedListener(this);
                                                    }
                                                }

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
                                                        //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                        ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
                                                //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
                                                    //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
                                                FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                int idData = dataSpin.getId();
                                                String results = dataSpin.getName();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormCIF.put(keysData,objEl);
                                                    if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                        String newNameDataEl = nameDataEl;
                                                        if (nameDataEl.contains("(")) {
                                                            int indxProv = nameDataEl.indexOf("(");
                                                            newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                                        }
                                                        valSpinProv.put(newNameDataEl,idData);
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
                                                        //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                        ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
                                                    }
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
                                                    } catch (JSONException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }

                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
                                                }
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
                                            String results = autoText.getText().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
                                                    //RabbitMirroring.MirroringSendKey(reqFormMirroring);
                                                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    reqFormMirroring = dataReqFormMirroring();
                                                    ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
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
            } else if (formCode == 22) {
                boolean valChk = chkDataCorrectForm.isChecked();
                dataFormObj2.put("datasudahbenar",valChk);
                dataFormObj.put(keysData,dataFormObj2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataFormObj;
    }

    private void processSendFormCIF(JSONObject jsons) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = null;
        if (formCode == 8 || formCode == 801 || formCode == 22) {
            call = API.AddDataSelf(requestBody,authAccess,exchangeToken);
        }
        else if (formCode == 802) {
            call = API.AddDataWork(requestBody,authAccess,exchangeToken);
        }
        else if (formCode == 803) {
            call = API.AddDataFinance(requestBody,authAccess,exchangeToken);
        }

        call.enqueue(new Callback<JsonObject>() {
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
                                //bundle.putInt("form_id",5);
                                bundle.putInt("form_id",75);
                            } else if (formCode == 801) {
                                intLayoutWork = 802;
                                //bundle.putInt("form_id",5);
                                bundle.putInt("form_id",75);
                            } else if (formCode == 802) {
                                intLayoutWork = 803;
                                //bundle.putInt("form_id",14);
                                bundle.putInt("form_id",76);
                            } else if (formCode == 803) {
                                //intLayoutWork = 804;
                                intLayoutWork = 240;
                            }

                            reqFormMirroring = dataReqFormMirroring();
                            if (intLayoutWork != 240) {
                                ConnectionRabbitHttp.mirroringKey(reqFormMirroring);
                                ConnectionRabbitHttp.mirroringEndpoint(intLayoutWork);
                                sessions.saveFormCOde(intLayoutWork);
                            }
                            if ((intLayoutWork == 804 && sessions.getCIF() != null) || intLayoutWork == 240) {
                                JSONObject dataFinance = dataObj.getJSONObject("data");
                                if (dataFinance.has("noCif")) {
                                    String noCif = dataFinance.getString("noCif");
                                    sessions.saveNoCIF(noCif);
                                }
                                String valDataCIF = sessions.getCIF();
                                try {
                                    objValCIF = new JSONObject(valDataCIF);
                                    JSONObject getObjEl = objValCIF.getJSONObject("datadiri");

                                    String getDataNasabah = sessions.getNasabah();
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

                                    dataNasabahObj.put("noHp",no_handphone);
                                    dataNasabahObj.put("namaLengkap",namaIdentitas);
                                    dataNasabahObj.put("nik",noIdentitas);
                                    dataNasabahObj.put("gelar",gelar);
                                    sessions.saveNasabah(dataNasabahObj.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (intLayoutWork == 240) {
                                    PopUpRegistrationCIF();
                                } else {
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
                                    processSendOTP();
                                }
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

    private void PopUpRegistrationCIF(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);
        btnConfirmDialog.setEnabled(false);
        btnConfirmDialog.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));

        btnCancelDialog.setVisibility(View.VISIBLE);

        tvTitleDialog.setText(getString(R.string.berhasil));
        btnConfirmDialog.setText(getString(R.string.label_ya));
        btnCancelDialog.setText(getString(R.string.tidak_not));

        imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_success));
        tvBodyDialog.setText(getString(R.string.contentSuccessRegistration));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                ConnectionRabbitHttp.mirroringEndpoint(240);
                /*sessions.saveFormCOde(240);
                Bundle bundle = new Bundle();
                bundle.putInt("form_id",24);
                Fragment fragment = new frag_cif_new();*/
                Fragment fragment = new frag_ibmb();
                getFragmentPageBackStack(fragment);
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                sweetAlertDialog.dismissWithAnimation();
                processSendOTP();
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

                        if (formCode == 8) {
                            processMatchDataFromeKTP();

                        } else if (formCode == 22) {
                            if (ocrKTP) {
                                processMatchData();
                                processDataFromOCR();
                            } else {
                                processMatchDataFromeKTP();
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
                                                if (valEl.equals("null") || valEl == null) {
                                                    valEl = "";
                                                }
                                                ed.setText(valEl);
                                            } else if (nameDataEl.contains("nama") && (nameDataEl.contains("identitas") || nameDataEl.contains("lengkap"))) {
                                                String valEl = "";
                                                if(objDataKTP.has("namaCust")) {
                                                    valEl = objDataKTP.getString("namaCust");
                                                } else if(objDataKTP.has("namaLengkap")) {
                                                    valEl = objDataKTP.getString("namaLengkap");
                                                }
                                                ed.setText(valEl);
                                            } else if (nameDataEl.contains("tanggal") && nameDataEl.contains("lahir")) {
                                                if (objDataKTP.has("tglLahir")) {
                                                    String gettglLahir = objDataKTP.getString("tglLahir");
                                                    String valEl = "";
                                                    if (gettglLahir.contains("-")) {
                                                        valEl = gettglLahir;
                                                    } else {
                                                        String tahun = gettglLahir.substring(0, 4);
                                                        String bln = gettglLahir.substring(4, 6);
                                                        String tgl = gettglLahir.substring(6, 8);
                                                        valEl = tgl + "-" + bln + "-" + tahun;
                                                    }
                                                    ed.setText(valEl);
                                                }
                                            } else if (nameDataEl.contains("alamat") && nameDataEl.contains("identitas")) {
                                                String valEl = "";
                                                if (objDataKTP.has("address1")) {
                                                    valEl = objDataKTP.getString("address1");
                                                } else if(objDataKTP.has("alamat")) {
                                                    valEl = objDataKTP.getString("alamat");
                                                }
                                                ed.setText(valEl);
                                            } else if ((nameDataEl.contains("no") && nameDataEl.contains("identitas")) || (nameDataEl.contains("nomor") && nameDataEl.contains("identitas"))) {
                                                if (objDataKTP.has("nik")) {
                                                    String valEl = objDataKTP.getString("nik");
                                                    ed.setText(valEl);
                                                } else {
                                                    for (Iterator<String> iter = objDataKTP.keys(); iter.hasNext(); ) {
                                                        if (iter.hasNext()) {
                                                            String key = iter.next();
                                                            if (key.contains("nomor") && key.contains("identitas")) {
                                                                String valEl = objDataKTP.getString(key);
                                                                ed.setText(valEl);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (nameDataEl.contains("nama") && nameDataEl.contains("ibu")) {
                                                if (objDataKTP.has("namaIbu")) {
                                                    String valEl = objDataKTP.getString("namaIbu");
                                                    ed.setText(valEl);
                                                } else if (objDataKTP.has("namaLengkapIbu")) {
                                                    String valEl = objDataKTP.getString("namaLengkapIbu");
                                                    ed.setText(valEl);
                                                }
                                            } else if ((nameDataEl.contains("no") || nameDataEl.contains("nomor")) && (nameDataEl.contains("ponsel") || nameDataEl.contains("handphone"))) {
                                                if(objDataKTP.has("noHandphone")) {
                                                    String valEl = objDataKTP.getString("noHandphone");
                                                    if (valEl.equals("null") || valEl == null) {
                                                        valEl = "";
                                                    }
                                                    ed.setText(valEl);
                                                } else if (objDataKTP.has("noHp")) {
                                                    String valEl = objDataKTP.getString("noHp");
                                                    if (valEl.equals("null") || valEl == null) {
                                                        valEl = "";
                                                    }
                                                    ed.setText(valEl);
                                                }
                                            } else if (nameDataEl.contains("provinsi")) {
                                                String valEl = "";
                                                if(objDataKTP.has("propinsi")) {
                                                    valEl = objDataKTP.getString("propinsi");
                                                }
                                                ed.setText(valEl);
                                            } else if (nameDataEl.contains("kabupaten")) {
                                                String valEl = "";
                                                if(objDataKTP.has("kabupaten")) {
                                                    valEl = objDataKTP.getString("kabupaten");
                                                } else if (objDataKTP.has("address5")) {
                                                    valEl = objDataKTP.getString("address5");
                                                }
                                                ed.setText(valEl);
                                            } else if (nameDataEl.contains("kecamatan")) {
                                                String valEl = "";
                                                if(objDataKTP.has("kecamatan")) {
                                                    valEl = objDataKTP.getString("kecamatan");
                                                } else if (objDataKTP.has("address4")) {
                                                    valEl = objDataKTP.getString("address4");
                                                }
                                                ed.setText(valEl);
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
                                            } else if (nameDataEl.contains("warganegara")) {
                                                ed.setText("WNI");
                                                /*if (objDataKTP.has("negaraAsal")) {
                                                    String negaraAsal = objDataKTP.getString("negaraAsal");
                                                    String warganegara = "WNI";
                                                    if (!negaraAsal.equals("ID")) {
                                                        warganegara = "WNA";
                                                    }
                                                    ed.setText(warganegara);
                                                }*/
                                            } else if (nameDataEl.contains("negara")) {
                                                ed.setText("Indonesia");
                                                /*if (objDataKTP.has("negaraAsal")) {
                                                    String negaraAsal = objDataKTP.getString("negaraAsal");
                                                    String negara = "";
                                                    if (negaraAsal.equals("ID")) {
                                                        negara = "Indonesia";
                                                    } else if (negaraAsal.equals("US")) {
                                                        negara = "United States of America";
                                                    } else if (negaraAsal.equals("CN")) {
                                                        negara = "China";
                                                    } else {
                                                        negara = "-";
                                                    }
                                                    ed.setText(negara);
                                                }*/
                                            } else if (nameDataEl.contains("jenis") && nameDataEl.contains("identitas")) {
                                                ed.setText("KTP");
                                            } else if (nameDataEl.contains("tanggal") && nameDataEl.contains("berakhir")) {
                                                ed.setText("Seumur Hidup");
                                            } else if (nameDataEl.contains("warganegara")) {
                                                ed.setText("WNI");
                                            } else if (nameDataEl.contains("negara")) {
                                                ed.setText("Indonesia");
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
                                        if (objEl.has(nameDataEl) && sessions.getOCR() != null) {
                                            String valEl = objEl.getString(nameDataEl);
                                            for (int ch = 0; ch < spin.getCount(); ch++) {
                                                if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                                    spin.setSelection(ch);
                                                    break;
                                                }
                                            }
                                        } else if (sessions.getCIF() != null) {
                                            String dataCIF = sessions.getCIF();
                                            JSONObject objCIF = new JSONObject(dataCIF);
                                            if (objCIF.has("dataktp")) {
                                                JSONObject objDataKTP = objCIF.getJSONObject("dataktp");
                                                int indx = nameDataEl.indexOf("(");
                                                if (indx >= 0) {
                                                    nameDataEl = nameDataEl.substring(0,indx);
                                                }
                                                String valEl = "";
                                                if (objDataKTP.has(nameDataEl)) {
                                                    valEl = objDataKTP.getString(nameDataEl);
                                                    for (int ch = 0; ch < spin.getCount(); ch++) {
                                                        if (spin.getItemAtPosition(ch).toString().contains(valEl)) {
                                                            spin.setSelection(ch);
                                                            break;
                                                        }
                                                    }
                                                } else if (nameDataEl.contains("status") && nameDataEl.contains("menikah")){
                                                    if (objDataKTP.has("statusperkawinan")) {
                                                        valEl = objDataKTP.getString("statusperkawinan");
                                                        for (int ch = 0; ch < spin.getCount(); ch++) {
                                                            if (spin.getItemAtPosition(ch).toString().contains(valEl)) {
                                                                spin.setSelection(ch);
                                                                break;
                                                            }
                                                        }
                                                    } else if (objDataKTP.has("statusKawin")) {
                                                        valEl = objDataKTP.getString("statusKawin");
                                                    }
                                                } else if (nameDataEl.contains("kelamin")) {
                                                    valEl = objDataKTP.getString("jenisKelamin");
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
                String namaIbuKandung = "";
                if (dataObjOCR.has("namaibukandung")) {
                    namaIbuKandung = dataObjOCR.getString("namaibukandung");
                }

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
                        } else if (key.contains("kabupaten") || key.contains("kota")) {
                            objEl.put(key, kabkotOCR);
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
                        } else if (key.contains("kecamatan")) {
                            objEl.put(key, kecamatanOCR);
                        } else if (key.contains("agama")) {
                            objEl.put(key, agamaOCR);
                        } else if (key.contains("nikah") || key.contains("menikah") || key.contains("kawin")) {
                            objEl.put(key, kawinOCR);
                        } else if (key.contains("warganegara")) {
                            if (sessions.getLANG().equals("en")) {
                                objEl.put(key, "Indonesian citizens");
                            } else {
                                objEl.put(key, wargaOCR);
                            }
                        } else if (key.contains("jenis") && key.contains("identitas")) {
                            if (sessions.getLANG().equals("en")) {
                                objEl.put(key, "ID card");
                            } else {
                                objEl.put(key, "KTP");
                            }
                        } else if (key.equals("negara") && wargaOCR.equals("WNI")) {
                            objEl.put(key, "Indonesia");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        TextView tvError = dialogView.findViewById(R.id.tvError);
        TextView tvMandatory = dialogView.findViewById(R.id.tvMandatory);
        CheckBox chkDataCorrect = dialogView.findViewById(R.id.chkDataCorrect);
        chkDataCorrect.setChecked(false);
        Button btnOCRCancel = dialogView.findViewById(R.id.btncncl);
        btnOCRNext = (Button) dialogView.findViewById(R.id.btnlnjt);

        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
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

                    if (!nik.isEmpty() && !nama.isEmpty() && !tmptLahir.isEmpty() && !ttl.isEmpty() && !jeniskelamin.isEmpty() &&
                            !alamat.isEmpty() && !rtrw.isEmpty() && !provinsi.isEmpty() && !kota_kabupaten.isEmpty() && !kecamatan.isEmpty() &&
                            !desa_kelurahan.isEmpty() && !agama.isEmpty() && !status_perkawinan.isEmpty() && !kewarganegaraan.isEmpty() &&
                            !pekerjaan.isEmpty() && !namaIbuKandung.isEmpty()) {
                        btnOCRNext.setEnabled(true);
                        btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    } else {
                        btnOCRNext.setEnabled(false);
                        btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                    }

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
                } else {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
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
                chkDataCorrect.setChecked(false);
                if (nik.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("nik",nik);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (nama.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("nama",nama);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (tmptLahir.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("tempatlahir",tmptLahir);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (ttl.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("ttl",ttl);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (jeniskelamin.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("jeniskelamin",jeniskelamin);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("golongandarah",golongan_darah);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (alamat.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("alamat",alamat);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (rtrw.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("rtrw",rtrw);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (provinsi.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("provinsi",provinsi);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (kota_kabupaten.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("kotakabupaten",kota_kabupaten);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (kecamatan.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("kecamatan",kecamatan);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (desa_kelurahan.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("desakelurahan",desa_kelurahan);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (agama.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("agama",agama);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (status_perkawinan.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("statusperkawinan",status_perkawinan);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (kewarganegaraan.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("kewarganegaraan",kewarganegaraan);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                if (pekerjaan.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }
                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("pekerjaan",pekerjaan);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                chkDataCorrect.setChecked(false);
                namaIbuKandung = charSequence.toString();
                if (namaIbuKandung.isEmpty()) {
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                }

                JSONObject dataReq = dataReqOCR2();
                JSONObject reqOCR = new JSONObject();
                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("namaibukandung",namaIbuKandung);
                    sessions.saveOCR(dataObjOCR.toString());
                    reqOCR.put("ocr",dataReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnOCRNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lbAlert = mContext.getResources().getString(R.string.alertRTGS);
                if (nik.isEmpty()) {
                    Toast.makeText(mContext,"NIK "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (nama.isEmpty()) {
                    Toast.makeText(mContext,"Nama "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (tmptLahir.isEmpty()) {
                    Toast.makeText(mContext,"Tempat lahir "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (ttl.isEmpty()) {
                    Toast.makeText(mContext,"Tanggal lahir "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (jeniskelamin.isEmpty()) {
                    Toast.makeText(mContext,"Jenis Kelamin "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } /*else if (golongan_darah.isEmpty()) {
                    Toast.makeText(mContext,"Golongan Darah "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                }*/ else if (alamat.isEmpty()) {
                    Toast.makeText(mContext,"Alamat "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (rtrw.isEmpty()) {
                    Toast.makeText(mContext,"Rt/RW "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (provinsi.isEmpty()) {
                    Toast.makeText(mContext,"provinsi "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (kota_kabupaten.isEmpty()) {
                    Toast.makeText(mContext,"Kabupaten/Kota "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (kecamatan.isEmpty()) {
                    Toast.makeText(mContext,"Kecamatan "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (desa_kelurahan.isEmpty()) {
                    Toast.makeText(mContext,"Kelurahan/Desa "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (agama.isEmpty()) {
                    Toast.makeText(mContext,"Agama "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (status_perkawinan.isEmpty()) {
                    Toast.makeText(mContext,"Status Perkawinan "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (pekerjaan.isEmpty()) {
                    Toast.makeText(mContext,"Pekerjaan "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                } else if (namaIbuKandung.isEmpty()) {
                    Toast.makeText(mContext,"Nama Ibu Kandung "+lbAlert,Toast.LENGTH_LONG).show();
                    return;
                }

                nik = NIK.getText().toString().trim();
                nama = Nama.getText().toString().trim();
                tmptLahir = TTL.getText().toString().trim();
                ttl = TTL2.getText().toString().trim();
                if (rtrw.contains("/")) {
                    String[] sp = rtrw.split("/");
                    String getRt = sp[0].trim();
                    String getRw = sp[1].trim();

                    String RT = String.format("%03d", Integer.parseInt(getRt));
                    String Rw = String.format("%03d", Integer.parseInt(getRw));
                    rtrw = RT+"/"+Rw;
                }

                String dataOCR = sessions.getOCR();
                try {
                    JSONObject dataObjOCR = new JSONObject(dataOCR);
                    dataObjOCR.put("nik",nik);
                    dataObjOCR.put("nama",nama);
                    dataObjOCR.put("tempatlahir",tmptLahir);
                    dataObjOCR.put("ttl",ttl);
                    dataObjOCR.put("jeniskelamin",jeniskelamin);
                    dataObjOCR.put("golongandarah",golongan_darah);
                    dataObjOCR.put("alamat",alamat);
                    dataObjOCR.put("rtrw",rtrw);
                    dataObjOCR.put("provinsi",provinsi);
                    dataObjOCR.put("kotakabupaten",kota_kabupaten);
                    dataObjOCR.put("kecamatan",kecamatan);
                    dataObjOCR.put("desakelurahan",desa_kelurahan);
                    dataObjOCR.put("agama",agama);
                    dataObjOCR.put("statusperkawinan",status_perkawinan);
                    dataObjOCR.put("kewarganegaraan",kewarganegaraan);
                    dataObjOCR.put("pekerjaan",pekerjaan);
                    dataObjOCR.put("namaibukandung",namaIbuKandung);
                    sessions.saveOCR(dataObjOCR.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (chkDataCorrect.isChecked() && flagMother) {
                    if (!picturePath.isEmpty() && !namaIbuKandung.isEmpty()) {
                        sweetAlertDialog.cancel();
                        sweetAlertDialog.dismissWithAnimation();
                        if (formCode == 22 && isSessionZoom) {
                            processMatchData();
                            processDataFromOCR();
                        } else {
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
                            if (isSessionZoom) {
                                JSONObject reqOCR = new JSONObject();
                                try {
                                    reqOCR.put("startValidasi", true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ConnectionRabbitHttp.mirroringKey(reqOCR);
                            }
                            processDTOTT();
                        }
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

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.65);

        sweetAlertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(sweetAlertDialog.getWindow().getAttributes());
        lp.width = newWidth;
        lp.height = newHeight;
        if (height > width) {
            double diffs = (double) height / width;
            if (diffs < 2.055) {
                lp.x=50;
            }
        } else {
            double diffs = (double) width / height;
            if (diffs < 2.055) {
                lp.y=50;
            }
        }
        sweetAlertDialog.getWindow().setAttributes(lp);
        sweetAlertDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

    }

    private JSONObject dataReqOCR2() {
        String rt = "";
        String rw = "";
        if (rtrw != null ) {
            if (!rtrw.equals("null")) {
                if (rtrw.contains("/")) {
                    String[] pisahrtrw = rtrw.split("/");
                    if (pisahrtrw.length == 2) {
                        rt = pisahrtrw[0];
                        rw = pisahrtrw[1];
                    } else if (pisahrtrw.length == 1) {
                        rt = pisahrtrw[0];
                    }
                } else {
                    if (rtrw.length() == 6) {
                        rt = rtrw.substring(0, 3);
                        rw = rtrw.substring(3, 6);
                    } else if (rtrw.length() > 3) {
                        rt = rtrw.substring(0, 3);
                        rw = rtrw.substring(3);
                    } else {
                        rt = rtrw;
                    }
                }
            }
        }

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

        sessions.saveOCR(datasReqOCR.toString());

        return datasReqOCR;
    }

    private JSONObject dataReqValidate() {

        String rt = rtrw;
        String rw = rtrw;
        if (rtrw.contains("/")) {
            String[] sp = rtrw.split("/");
            rt = sp[0].trim();
            rw = sp[1].trim();
        }

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
            jsons.put("agama",agama);
            jsons.put("rt",rt);
            jsons.put("rw",rw);
            jsons.put("kelurahan",desa_kelurahan);
            jsons.put("kecamatan",kecamatan);
            jsons.put("statusKawin",status_perkawinan);
            jsons.put("jenisPekerjaan",pekerjaan);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

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
                            btnOCRNext.setEnabled(true);
                            btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        }
                        else{
                            //loopValidMother++;
                            if (msg.contains("maksimum")) {
                                PopUpLimitValidateMother();
                            }
                            chkDataCorrect.setChecked(false);
                            flagMother = false;
                            btnOCRNext.setEnabled(false);
                            btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
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
                    btnOCRNext.setEnabled(false);
                    btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
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
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            msg = dataObj.getString("message");
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
                    Toast.makeText(mContext, "Failed,"+msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                chkDataCorrect.setChecked(false);
                flagMother = false;
                btnOCRNext.setEnabled(false);
                btnOCRNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
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
                if (response.isSuccessful()){
                    String dataS = response.body().toString();
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
                            CekDataByNIK();
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
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //dialogFailedValidation("IDEM");
                    if (response.code() == 400) {
                        CekDataByNIK();
                    } else {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                        Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
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
                if (response.isSuccessful()){
                    try {
                        String dataS = response.body().toString();
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
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
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
                //RabbitMirroring.MirroringSendKey(reqOCR);
                ConnectionRabbitHttp.mirroringKey(reqOCR);
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
                        //RabbitMirroring.MirroringSendKey(reqOCR);
                        ConnectionRabbitHttp.mirroringKey(reqOCR);
                    }
                    /*if (kasus.equals("Dukcapil")) {
                        PopUpOCR();
                    } else {*/
                        int ret = ZoomVideoSDK.getInstance().leaveSession(false);
                        sessions.clearPartData();
                        //RabbitMirroring.MirroringSendEndpoint(99);
                        ConnectionRabbitHttp.mirroringEndpoint(99);
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
                            processDukcapil();
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
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();
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

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.CekByNIK(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
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

                            if (dataObj.has("isSwafoto")) {
                                if (!dataObj.isNull("isSwafoto")) {
                                    sessions.saveIsSwafoto(dataObj.getBoolean("isSwafoto"));
                                }
                            }

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
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();
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
        call.enqueue(new Callback<JsonObject>() {
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

        call.enqueue(new Callback<JsonObject>() {
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
                                    /*RabbitMirroring.MirroringSendKey(reqOCR);
                                    RabbitMirroring.MirroringSendEndpoint(22);*/
                                    ConnectionRabbitHttp.mirroringKey(reqOCR);
                                    ConnectionRabbitHttp.mirroringEndpoint(22);
                                } else if (formCode == 22) {
                                    //RabbitMirroring.MirroringSendEndpoint(6);
                                    ConnectionRabbitHttp.mirroringEndpoint(6);
                                } else if (formCode == 6) {
                                    //RabbitMirroring.MirroringSendEndpoint(7);
                                    ConnectionRabbitHttp.mirroringEndpoint(7);
                                } else if (formCode == 7) {
                                    //RabbitMirroring.MirroringSendEndpoint(8);
                                    ConnectionRabbitHttp.mirroringEndpoint(8);
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
                                //bundle.putString("NPWP",npwp);
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

                    if (mediaImgKTP != null) {
                        if (mediaImgKTP.exists()) {
                            try {
                                mediaImgKTP.getCanonicalFile().delete();
                                if (mediaImgKTP.exists()) {
                                    getActivity().getApplicationContext().deleteFile(mediaImgKTP.getName());
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

    private void processFormDataAttachment2(String keys, String picturePathAttach) {
        File file = new File(picturePathAttach);
        if (!imgPathKTP.isEmpty() && keys.equals("ktp")) {
            file = new File(imgPathKTP);
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);
        RequestBody requestidDips = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idDips));
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

        call.enqueue(new Callback<JsonObject>() {
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
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode == 200 || errCode == 202) {
                            if (keys.equals("ktp")) {
                                /*if (sessions.getPhotoLiveness() != null) {
                                    processFormDataAttachment2("foto",sessions.getPhotoLiveness());
                                } else {*/
                                    processFormDataAttachment2("foto", picturePath);
                                //}
                            } else if (keys.equals("foto")) {
                                processFormDataAttachment2("ttd",picturePathCrop);
                            }
                            if (!isSessionZoom && formCode == 22 && keys.equals("ttd")) {

                                prosesCustByIdDips();
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
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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

    private void prosesCustByIdDips() {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataJson.toString());
        Server.getAPIService().CustByIdDips(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsObj = new JSONObject(response.body().toString());
                        if (jsObj.has("token")) {
                            String accessToken = jsObj.getString("token");
                            String exchangeToken = jsObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }

                        if (jsObj.has("data")) {
                            JSONObject dataObj = jsObj.getJSONObject("data");
                            if (dataObj.has("idDips")) {
                                idDips = dataObj.getString("idDips");
                                sessions.saveIdDips(idDips);
                            }

                            String getDataNasabah = sessions.getNasabah();
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

                            if (dataObj.has("noCif")) {
                                if (!dataObj.isNull("noCif")) {
                                    String noCif = dataObj.getString("noCif");
                                    if (!noCif.isEmpty()) {
                                        sessions.saveNoCIF(noCif);
                                        sessions.saveIsCust(true);
                                    }
                                }
                            }

                            if (dataObj.has("isSwafoto")) {
                                if (!dataObj.isNull("isSwafoto")) {
                                    sessions.saveIsSwafoto(dataObj.getBoolean("isSwafoto"));
                                }
                            }

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

                            if (mediaImgKTP != null) {
                                if (mediaImgKTP.exists()) {
                                    try {
                                        mediaImgKTP.getCanonicalFile().delete();
                                        if (mediaImgKTP.exists()) {
                                            getActivity().getApplicationContext().deleteFile(mediaImgKTP.getName());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            /*if (sessions.getPhotoLiveness() != null) {
                                File mediaFilePhotoLiveness = new File(sessions.getPhotoLiveness());
                                if (mediaFilePhotoLiveness != null) {
                                    if (mediaFilePhotoLiveness.exists()) {
                                        try {
                                            mediaFilePhotoLiveness.getCanonicalFile().delete();
                                            if (mediaFilePhotoLiveness.exists()) {
                                                getActivity().getApplicationContext().deleteFile(mediaFilePhotoLiveness.getName());
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }*/

                        }

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
        rlContent.setVisibility(View.GONE);
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
                        HideSoftKeyboard.hideSoftKeyboard(getActivity());
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
                    //RabbitMirroring.MirroringSendEndpoint(13);
                    ConnectionRabbitHttp.mirroringEndpoint(13);
                    Bundle bundle = new Bundle();
                    bundle.putInt("formCode",13);
                    sendDataFragment(bundle,new frag_cif_resi());
                } else {
                    /*if (loopStatus >= 99) {
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
                    }*/
                    //if (loopStatus < 99) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        processApprovalStatus();
                                    }
                                });
                                //loopStatus++;
                            }
                        },10000);
                    //}
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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

    private void runTimer(TextView timer_run, TextView resend) {
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

        if (thumbnail.getWidth() < 256 || thumbnail.getHeight() < 256) {
            perDiff--;
            prosesOptimalImage(picturePath,perDiff);
        }

        return thumbnail;
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
                        if (onOfCamera == 1) {
                            break;
                        }
                        Thread.sleep(500);
                    }
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
        if (formCode == 4) {
            imgBase64 = encodedImage;
            keys = "ktp";
            //imgtoBase64OCR();
        } else if (formCode == 22) {
            keys = "swafoto";
            imgBase64 = encodedImage;
        } else if (formCode == 6) {
            if (imageBytes.length > 0){
                btnNext.setText(mContext.getResources().getString(R.string.btn_continue));
            }
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
                //RabbitMirroring.MirroringSendKey(dataImg);
                ConnectionRabbitHttp.mirroringKey(dataImg);
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
        provinsi = "";
        kota_kabupaten = "";
        nik = "";
        nama = "";
        ttl = "";
        jeniskelamin = "";
        golongan_darah = "";
        alamat = "";
        rtrw = "";
        desa_kelurahan = "";
        kecamatan = "";
        agama = "";
        status_perkawinan = "";
        kewarganegaraan = "";
        pekerjaan = "";
        tmptLahir = "-";
        namaIbuKandung = "";
        ocrKTP();
    }

    private void ocrNPWP() {
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
        call.enqueue(new Callback<JsonObject>() {
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
        if (swaOCR) {
            if (!imgPathKTP.isEmpty()) {
                Bitmap bitmapKTP = BitmapFactory.decodeFile(imgPathKTP);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapKTP.compress(Bitmap.CompressFormat.JPEG,100, baos);
                byte[] imageBytesCrop = baos.toByteArray();
                baseImages = Base64.encodeToString(imageBytesCrop, Base64.NO_WRAP);
            } else {
                baseImages = encodedImageCrop;
            }
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
        call.enqueue(new Callback<JsonObject>() {
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
                                if (!dataObj.isNull("provinsi")) {
                                    provinsi = dataObj.getString("provinsi");
                                }
                            }
                            if (dataObj.has("kota_kabupaten")) {
                                if (!dataObj.isNull("kota_kabupaten")) {
                                    kota_kabupaten = dataObj.getString("kota_kabupaten");
                                }
                            }
                            if (dataObj.has("nik")) {
                                if (!dataObj.isNull("nik")) {
                                    nik = dataObj.getString("nik");
                                }
                            }
                            if (dataObj.has("nama")) {
                                if (!dataObj.isNull("nama")) {
                                    nama = dataObj.getString("nama");
                                }
                            }
                            if (dataObj.has("ttl")) {
                                if (!dataObj.isNull("ttl")) {
                                    ttl = dataObj.getString("ttl");
                                }
                            }
                            if (dataObj.has("jeniskelamin")) {
                                if (!dataObj.isNull("jeniskelamin")) {
                                    jeniskelamin = dataObj.getString("jeniskelamin");
                                }
                            }
                            if (dataObj.has("golongan_darah")) {
                                if (!dataObj.isNull("golongan_darah")) {
                                    golongan_darah = dataObj.getString("golongan_darah");
                                }
                            }
                            if (dataObj.has("alamat")) {
                                if (!dataObj.isNull("alamat")) {
                                    alamat = dataObj.getString("alamat");
                                }
                            }
                            if (dataObj.has("rtrw")) {
                                if (!dataObj.isNull("rtrw")) {
                                    rtrw = dataObj.getString("rtrw");
                                }
                                if (rtrw.equals("null")) {
                                    rtrw = "";
                                }
                            }
                            if (dataObj.has("desa_kelurahan")) {
                                if (!dataObj.isNull("desa_kelurahan")) {
                                    desa_kelurahan = dataObj.getString("desa_kelurahan");
                                }
                            }
                            if (dataObj.has("kecamatan")) {
                                if (!dataObj.isNull("kecamatan")) {
                                    kecamatan = dataObj.getString("kecamatan");
                                }
                            }
                            if (dataObj.has("agama")) {
                                if (!dataObj.isNull("agama")) {
                                    agama = dataObj.getString("agama");
                                }
                            }
                            if (dataObj.has("status_perkawinan")) {
                                if (!dataObj.isNull("status_perkawinan")) {
                                    status_perkawinan = dataObj.getString("status_perkawinan");
                                }
                            }
                            if (dataObj.has("kewarganegaraan")) {
                                if (!dataObj.isNull("kewarganegaraan")) {
                                    kewarganegaraan = dataObj.getString("kewarganegaraan");
                                }
                            }
                            if (dataObj.has("pekerjaan")) {
                                if (!dataObj.isNull("pekerjaan")) {
                                    pekerjaan = dataObj.getString("pekerjaan");
                                }
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

                            sessions.saveOCR(datasReqOCR.toString());

                            flagOCR = true;
                            PopUpOCR();
                            JSONObject dataReq = dataReqOCR2();
                            JSONObject reqOCR = new JSONObject();
                            try {
                                reqOCR.put("ocr",dataReq);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (isSessionZoom) {
                                ConnectionRabbitHttp.mirroringKey(reqOCR);
                            }
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
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    dialogFailedValidation("OCR");
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
            if (requestCode == REQUESTCODE_CAPTURE){
                sessions.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);

                try {
                    mediaFilePhoto = createTemporaryFile(resultCamera);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                picturePath = mediaFilePhoto.getAbsolutePath();
                LL.setBackgroundResource(0);
                //if (formCode != 4) {
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setClickable(true);
                    imgDelete.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                //}
                viewImage.setImageBitmap(bitmap);
                if (formCode != 4) {
                    processSendImage(bitmap);
                } else {
                    Bitmap thumbnail = prosesOptimalImage(picturePath,1);
                    imgtoBase64(thumbnail);
                }
            }
            else if (requestCode == REQUESTCODE_GALLERY){
                sessions.saveMedia(0);
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

                if (REQUESTCODE_GALLERY == 201) {

                } else {
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
                }
            }
            else if (requestCode == REQUESTCODE_SWAFOTO){
                sessions.saveFlagUpDoc(true);
                String filePaths = data.getStringExtra("result_camera");
                String filePathsCrop = data.getStringExtra("result_cropImage");
                Bitmap bitmap = BitmapFactory.decodeFile(filePaths);
                Bitmap bitmapCrop = BitmapFactory.decodeFile(filePathsCrop);

                if (swaOCR) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapCrop.compress(Bitmap.CompressFormat.JPEG,100, baos);
                    byte[] imageBytesCrop = baos.toByteArray();
                    encodedImageCrop = Base64.encodeToString(imageBytesCrop, Base64.NO_WRAP);
                }

                mediaFilePhoto = new File(filePaths);
                mediaFilePhotoCropSwafoto = new File(filePathsCrop);
                picturePath = filePaths;
                picturePathCrop = filePathsCrop;

                if (formCode == 22 && isSessionZoom) {
                    if (swaOCR) {
                        imgtoBase64OCR();
                    }
                } else {
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
    }

    private void processSwafotoCheck() {
        File filePhoto = mediaFilePhoto;
        File filePhotoCrop = mediaFilePhotoCropSwafoto;
        if (mediaImgKTP != null) {
            filePhotoCrop = mediaImgKTP;
        }
        /*if (sessions.getPhotoLiveness() != null) {
            filePhoto = new File(sessions.getPhotoLiveness());
        }*/
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), filePhoto);
        RequestBody requestFileCrop = RequestBody.create(MediaType.parse("image/jpeg"), filePhotoCrop);

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = null;
        MultipartBody multipartBody = null;
        String contentType = "";
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        multipartBody = new MultipartBody.Builder()
                .addPart(MultipartBody.Part.createFormData("firstImage", filePhoto.getName(), requestFile))
                .addPart(MultipartBody.Part.createFormData("secondImage", filePhotoCrop.getName(), requestFileCrop))
                .build();
        contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        call = API.swafotoCheck(contentType, authAccess, exchangeToken, multipartBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    btnNext.setClickable(true);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    String dataS = response.body().toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (swaOCR) {
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
                    btnNext.setClickable(false);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
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

    private void getFragmentPageBackStack(Fragment fragment){
        if (isSessionZoom) {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack("FragCIF")
                    .commit();
        } else {
            ((FragmentActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack("FragCIF")
                    .commit();
        }

    }

}
