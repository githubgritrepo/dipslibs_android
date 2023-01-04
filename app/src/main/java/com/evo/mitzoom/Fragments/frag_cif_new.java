package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsCameraSource;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
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

    private int REQUEST_WRITE_PERMISSION = 786;
    private int REQUESTCODE_CAPTURE = 1;
    private int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_SWAFOTO = 10;
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
    private byte[] imageBytes = new byte[0];
    private String encodedImage;
    private boolean flagOCR = false;
    private String tmptLahir = "-";
    private String provinsi, kota_kabupaten, nik, nama, ttl, jeniskelamin, golongan_darah, alamat, rtrw, desa_kelurahan, kecamatan, agama, status_perkawinan, kewarganegaraan, pekerjaan = "";
    private JSONObject datasReqOCR = null;
    private int lasLenChar;
    private boolean backSpaceChar;
    private JSONObject objValCIF;
    private String no_handphone = "";
    private String numberOTP = "";
    private String newString = "";
    private Handler handler = null;
    private Runnable myRunnable = null;
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private String transactionId = "";
    private JSONObject idFormObj;
    JSONObject valSpin = new JSONObject();
    private boolean flagStuckSpin = false;
    private JSONObject reqFormMirroring;
    private int loopStatus = 0;
    private int getRequestCode = 0;
    private File mediaFilePhoto = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        Log.e("CEK",mContext+" isSessionZoom : "+isSessionZoom);
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_cif, container, false);

        inclHead = views.findViewById(R.id.inclHead);
        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvAlertDoc = (TextView) views.findViewById(R.id.tvAlertDoc);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);
        iconKtp = (LinearLayout) views.findViewById(R.id.icon_ktp);
        iconSwafoto = (LinearLayout) views.findViewById(R.id.icon_swafoto);
        iconNpwp = (LinearLayout) views.findViewById(R.id.icon_npwp);
        iconSignature = (LinearLayout) views.findViewById(R.id.icon_signature);
        iconForm = (LinearLayout) views.findViewById(R.id.icon_form);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);

        inclBodyUpload = views.findViewById(R.id.inclBodyUpload);
        btnCamera = (ImageView) views.findViewById(R.id.choose_camera);
        btnGallery = (LinearLayout) views.findViewById(R.id.choose_gallery);
        btnNext = (Button) views.findViewById(R.id.btnNext);
        imgDelete = (ImageView) views.findViewById(R.id.imgDelete);
        viewImage = (ImageView) views.findViewById(R.id.Imageview);
        LL = (LinearLayout) views.findViewById(R.id.BackgroundLL);
        llOR = (LinearLayout) views.findViewById(R.id.llOR);
        chooseImage = (LinearLayout) views.findViewById(R.id.Choose_Image);

        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);

        scrollOTP = (NestedScrollView) views.findViewById(R.id.scrollOTP);
        inclOTP = views.findViewById(R.id.inclOTP);
        imgDialog = (ImageView) views.findViewById(R.id.imgDialog);
        textTitleOTP = (TextView) views.findViewById(R.id.textIBMB);
        btnVerifikasi = (Button) views.findViewById(R.id.btnVerifikasi);
        TimerOTP = (TextView) views.findViewById(R.id.timer_otp);
        Resend_Otp = (TextView) views.findViewById(R.id.btn_resend_otp);
        otp = (PinView) views.findViewById(R.id.otp);

        btnProses = (Button) views.findViewById(R.id.btnProses);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            }
        }
        if (formCode == 8) {
            keysData = "datadiri";
        } else if (formCode == 801) {
            keysData = "datatidaksesuai";
        } else if (formCode == 802) {
            keysData = "pekerjaan";
        } else if (formCode == 803) {
            keysData = "keuangan";
        }

        Log.e("CEK", mContext + " isCust : " + isCust);
        Log.e("CEK", mContext + " isSwafoto : " + isSwafoto);
        Log.e("CEK", mContext + " formCode : " + formCode);
        Log.e("CEK", mContext + " idDips : " + idDips);
        Log.e("CEK", mContext + " form_id : " + form_id);

        if (formCode == 22) {
            tvFotoKTP.setText(R.string.ktp_swafoto);
        } else {
            tvFotoKTP.setText(getString(R.string.pembukaan_akun));
        }

        if (isSessionZoom) {
            TopBar.setVisibility(View.VISIBLE);
            ll_head.setVisibility(View.VISIBLE);
        } else {
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
                    //processGetForm(form_id);
                }
            });

            //processGetForm(form_id);

        } else {
            sessions.saveOCR(null);
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
            } else if (formCode > 6) {
                iconSwafoto.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }

            if (IMG_BYTE.length == 0 && formCode == 7) {
                iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                tvAlertDoc.setText(getString(R.string.alert_ttd));
            } else {
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
                    } else if (formCode == 6) {
                        IMG_BYTE = imageBytes;
                    } else if (formCode == 7) {
                        IMG_BYTE = imageBytes;
                    }

                    if ((!flagOCR && imageBytes.length == 0 && formCode == 4) || (IMG_BYTE.length == 0 && formCode == 22) || (IMG_BYTE.length == 0 && formCode == 6) || (IMG_BYTE.length == 0 && formCode == 7)){
                        Toast.makeText(mContext, getResources().getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (imageBytes.length > 0 && formCode == 4) {
                        if (flagOCR) {
                            IMG_BYTE = imageBytes;
                            PopUpOCR();
                        } else {
                            Toast.makeText(mContext, "Maaf, OCR masih dalam proses...!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (formCode == 22) {
                            if (!picturePath.isEmpty()) {
                                String fieldName = "foto";
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
                        } else if (formCode == 6) {
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
                        } else if (formCode == 7) {
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
                    String word = sp[i].toString();
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
            rabbitMirroring.closeThreadConnection();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(STATE_IMGBYTE,IMG_BYTE);
    }

    private void PopUpOCR(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_ocr, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        EditText NIK = (EditText) dialogView.findViewById(R.id.et_nik_ocr);
        EditText Nama = (EditText) dialogView.findViewById(R.id.et_name_ocr);
        EditText TTL = (EditText) dialogView.findViewById(R.id.et_ttl_ocr);
        EditText TTL2 = (EditText) dialogView.findViewById(R.id.et_ttl2_ocr);
        EditText et_gender= (EditText) dialogView.findViewById(R.id.et_gender);
        EditText et_goldar= (EditText) dialogView.findViewById(R.id.et_goldar);
        EditText et_address= (EditText) dialogView.findViewById(R.id.et_address);
        EditText et_rtrw= (EditText) dialogView.findViewById(R.id.et_rtrw);
        EditText et_provinsi= (EditText) dialogView.findViewById(R.id.et_provinsi);
        EditText et_kabkot= (EditText) dialogView.findViewById(R.id.et_kabkot);
        EditText et_kecamatan= (EditText) dialogView.findViewById(R.id.et_kecamatan);
        EditText et_desa= (EditText) dialogView.findViewById(R.id.et_desa);
        EditText et_religion= (EditText) dialogView.findViewById(R.id.et_religion);
        EditText et_status_kawin= (EditText) dialogView.findViewById(R.id.et_status_kawin);
        EditText et_warga= (EditText) dialogView.findViewById(R.id.et_warga);
        EditText et_work= (EditText) dialogView.findViewById(R.id.et_work);
        Button btnOCRCancel = (Button) dialogView.findViewById(R.id.btncncl);
        Button btnOCRNext = (Button) dialogView.findViewById(R.id.btnlnjt);

        String tglLahir = "-";
        if (ttl.indexOf(",") > 0) {
            String[] sp = ttl.split(",");
            tglLahir = sp[1].toString().trim();
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
                if (isSessionZoom) {
                    nik = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    nama = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    tmptLahir = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    ttl = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    jeniskelamin = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    golongan_darah = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    golongan_darah = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    rtrw = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    provinsi = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    kota_kabupaten = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    kecamatan = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    desa_kelurahan = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    agama = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    status_perkawinan = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    kewarganegaraan = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
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
                if (isSessionZoom) {
                    pekerjaan = s.toString();
                    JSONObject dataReq = dataReqOCR();
                    JSONObject reqOCR = new JSONObject();
                    try {
                        reqOCR.put("ocr",dataReq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    rabbitMirroring.MirroringSendKey(reqOCR);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnOCRNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (isSessionZoom) {
                nik = NIK.getText().toString().trim();
                nama = Nama.getText().toString().trim();
                tmptLahir = TTL.getText().toString().trim();
                ttl = TTL2.getText().toString().trim();
                if (!picturePath.isEmpty()) {
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
                    //processFormDataAttachment(fieldName,picturePath);
                }
                //}
                sweetAlertDialog.dismiss();
            }
        });
        btnOCRCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
                IMG_BYTE = new byte[0];
                imageBytes = new byte[0];
            }
        });

    }

    private JSONObject dataReqOCR() {
        try {
            datasReqOCR.put("nik",nik);
            datasReqOCR.put("nama",nama);
            datasReqOCR.put("tempatlahir",tmptLahir);
            datasReqOCR.put("ttl",ttl);
            datasReqOCR.put("idDips", idDips);
            datasReqOCR.put("code", 5);
            datasReqOCR.put("provinsi",provinsi);
            datasReqOCR.put("kotakabupaten",kota_kabupaten);
            datasReqOCR.put("jeniskelamin",jeniskelamin);
            datasReqOCR.put("golongandarah",golongan_darah);
            datasReqOCR.put("alamat",alamat);
            datasReqOCR.put("rtrw",rtrw);
            datasReqOCR.put("desakelurahan",desa_kelurahan);
            datasReqOCR.put("kecamatan",kecamatan);
            datasReqOCR.put("agama",agama);
            datasReqOCR.put("statusperkawinan",status_perkawinan);
            datasReqOCR.put("kewarganegaraan",kewarganegaraan);
            datasReqOCR.put("pekerjaan",pekerjaan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sessions.saveOCR(datasReqOCR.toString());

        return datasReqOCR;
    }

    private void processFormDataAttachment(String fieldName, String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);

        RequestBody requestidDips = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idDips));

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = null;
        MultipartBody multipartBody = null;
        String contentType = "";

        multipartBody = new MultipartBody.Builder()
                .addPart(MultipartBody.Part.createFormData(fieldName,file.getName(),requestFile))
                .addPart(MultipartBody.Part.createFormData("idDips",null,requestidDips))
                .build();
        contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();

        call = API.formAttachment(contentType,multipartBody);

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
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode == 200 || errCode == 202) {
                            if (isSessionZoom) {
                                if (formCode == 4 && flagOCR) {
                                    JSONObject dataReq = dataReqOCR();
                                    JSONObject reqOCR = new JSONObject();
                                    try {
                                        reqOCR.put("ocr", dataReq);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    rabbitMirroring.MirroringSendKey(reqOCR);
                                    rabbitMirroring.MirroringSendEndpoint(22);
                                } else if (formCode == 22) {
                                    rabbitMirroring.MirroringSendEndpoint(6);
                                } else if (formCode == 6) {
                                    rabbitMirroring.MirroringSendEndpoint(7);
                                } else if (formCode == 7) {
                                    rabbitMirroring.MirroringSendEndpoint(8);
                                }
                            }

                            Bundle bundle = new Bundle();
                            if (formCode == 4 && flagOCR) {
                                if (!isSwafoto) {
                                    sessions.saveKTP(encodedImage);
                                    sessions.saveFormCOde(22);
                                }
                            } else if (formCode == 22) {
                                sessions.saveFormCOde(6);
                                sessions.saveSWAFOTO(encodedImage);
                            } else if (formCode == 6) {
                                sessions.saveFormCOde(7);
                                sessions.saveNPWP(encodedImage);
                            } else if (formCode == 7) {
                                sessions.saveFormCOde(8);
                                sessions.saveTTD(encodedImage);
                                bundle.putInt("form_id",10);
                            }

                            if (getRequestCode != 0 && getRequestCode != REQUESTCODE_GALLERY) {
                                if (file.exists()) {
                                    try {
                                        file.getCanonicalFile().delete();
                                        if (file.exists()) {
                                            ((Activity) mContext).getApplicationContext().deleteFile(file.getName());
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
                        } else {
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
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
                } else {
                    if (fieldName.equals("ktp")) {
                        IMG_BYTE = new byte[0];
                    }
                    if (response.body() != null) {
                        Log.e("CEK","response body : "+response.body().toString());
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
                        rabbitMirroring.MirroringSendKey(otpObj);
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
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
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
            Log.e("CEK","APISaveForm CIF FULL objValCIF : "+getObjValCIF.toString());
            dataObjCIF.put("formCode","Opening Cif");
            dataObjCIF.put("idDips",idDips);
            dataObjCIF.put("payload",getObjValCIF);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjCIF.toString());
        Server.getAPIService().saveForm(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","APISaveForm code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","APISaveForm dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String idForm = dataObj.getJSONObject("data").getString("idForm");
                        idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);
                        rabbitMirroring.MirroringSendKey(idFormObj);

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
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
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

        Log.e("CEK","processValidateOTP : "+dataObjOTP.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());
        Server.getAPIService().ValidateOTP(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processValidateOTP code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processValidateOTP : "+dataS);
                    processApprovalStatus();
                    /*rabbitMirroring.MirroringSendEndpoint(13);
                    getFragmentPage(new frag_cif_resi());*/
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

        Server.getAPIService().ApprovalStatus(idForm).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK", this+" processApprovalStatus code : "+response.code());
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
                    Log.e("CEK","processApprovalStatus dataS : "+dataS);
                    rabbitMirroring.MirroringSendEndpoint(13);
                    getFragmentPage(new frag_cif_resi());
                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                    if (loopStatus < 10) {
                        try {
                            Thread.sleep(5000);
                            processApprovalStatus();
                            loopStatus++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CEK", this+" processApprovalStatus onFailure : "+t.getMessage());
                /*if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }*/
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
                if (loopStatus < 10) {
                    try {
                        Thread.sleep(5000);
                        processApprovalStatus();
                        loopStatus++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void processSendOTP() {
        String noHp = no_handphone;
        if (noHp.substring(0,1).equals("0")) {
            noHp = "62"+no_handphone.substring(1);
        }
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("msisdn",noHp);
            dataObjOTP.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","processSendOTP : "+dataObjOTP.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());

        Server.getAPIService().SendOTP(requestBody).enqueue(new Callback<JsonObject>() {
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
                        transactionId = dataObj.getJSONObject("data").getString("transactionId");
                        rabbitMirroring.MirroringSendEndpoint(11);
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

        Log.e("CEK", "bitmapCrop.getWidth() : "+thumbnail.getWidth()+" | bitmapCrop.getHeight() : "+thumbnail.getHeight());
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
            File mediaFilesCrop = null;
            try {
                mediaFilesCrop = createTemporaryFile(imageBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int file_mediaFilesCrop = Integer.parseInt(String.valueOf(mediaFilesCrop.length()/1024));

            Bitmap bitmapOptimal = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            Log.e("CEK", "file_mediaFilesCrop :"+file_mediaFilesCrop+" | bitmapOptimal.getWidth() : "+bitmapOptimal.getWidth()+" | bitmapOptimal.getHeight() : "+bitmapOptimal.getHeight());

            imgtoBase64OCR(bitmapOptimal);
        } else if (formCode == 22) {
            keys = "swafoto";
            imgBase64 = encodedImage;
        } else if (formCode == 6) {
            keys = "npwp";
            imgBase64 = encodedImage;
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

    private void imgtoBase64OCR(Bitmap bitmap) {
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);*/
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
        ocrKTP();
    }

    private void ocrKTP(){
        Log.d("Masuk OCR","");
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("image",encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.ocrKtp(requestBody);
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
                                tmptLahir = sp[0].toString().trim();
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
                flagOCR = false;
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
            if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
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
                btnNext.setVisibility(View.VISIBLE);
                //btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                btnNext.setClickable(true);
                imgDelete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
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
                    btnNext.setVisibility(View.VISIBLE);
                    //btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btnNext.setClickable(true);
                    imgDelete.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                    imgtoBase64(thumbnail);
                    if (formCode == 4) {
                        imgtoBase64OCR(thumbnail);
                    }
                }
            } else if (requestCode == REQUESTCODE_SWAFOTO){
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
                btnNext.setVisibility(View.VISIBLE);
                //btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                btnNext.setClickable(true);
                imgDelete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                viewImage.setImageBitmap(bitmap);
                processSendImage(bitmap);
            }
        }
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
