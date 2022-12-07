package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsCameraSource;
import com.evo.mitzoom.ui.DipsWaitingRoom;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_cif extends Fragment {

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
    private Button delete;
    private LinearLayout iconKtp;
    private LinearLayout iconNpwp;
    private LinearLayout iconSignature;
    private LinearLayout iconForm;
    private ImageView viewImage;
    private LinearLayout LL;
    private LinearLayout chooseImage;
    private View inclBody;
    private LinearLayout llFormBuild;
    private Button btnProses;
    private Context mContext;
    private SessionManager sessions;
    private boolean isCust;
    private boolean isSwafoto;
    private int chkFlow;
    private byte[] bytePhoto;
    private byte[] KTP = new byte[0];
    private byte[] KTP_SWAFOTO = new byte[0];
    private byte[] NPWP = new byte[0];
    private byte[] TTD = new byte[0];
    private String KTP_BASE64,NPWP_BASE64,TTD_BASE64;
    private String tmptLahir = "-";
    private String provinsi, kota_kabupaten, nik, nama, ttl, jeniskelamin, golongan_darah, alamat, rtrw, desa_kelurahan, kecamatan, agama, status_perkawinan, kewarganegaraan, pekerjaan = "";
    private boolean flagOCR = false;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private int formCode;
    private int form_id = 0;
    private JSONArray idElement;
    JSONObject objEl = new JSONObject();
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String idDips;
    private JSONObject datasReqOCR = null;
    private int lasLenChar;
    private boolean backSpaceChar;
    private byte[] imageBytes;
    private String encodedImage;
    private String keysData = "";
    private JSONObject dataFormCIF = null;
    private String picturePath = "";
    private File mediaFilePhoto = null;
    private int idPhoto = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        idPhoto = sessions.getIDCust();
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        chkFlow = sessions.getFLOW();
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();
        if (getArguments() != null) {
            if (getArguments().containsKey("RESULT_IMAGE_AI")) {
                bytePhoto = getArguments().getByteArray("RESULT_IMAGE_AI");
            }
            if (getArguments().containsKey("ktp")) {
                KTP = getArguments().getByteArray("ktp");
            }
            if (getArguments().containsKey("swafoto")) {
                KTP_SWAFOTO = getArguments().getByteArray("swafoto");
            }
            if (getArguments().containsKey("npwp")) {
                NPWP = getArguments().getByteArray("npwp");
            }
            if (getArguments().containsKey("ttd")) {
                TTD = getArguments().getByteArray("ttd");
            }
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            }
        }
        Log.e("CEK",mContext+" isCust : "+isCust);
        Log.e("CEK",mContext+" isSwafoto : "+isSwafoto);
        Log.e("CEK",mContext+" chkFlow : "+chkFlow);
        Log.e("CEK",mContext+" formCode : "+formCode);
        Log.e("CEK",mContext+" idDips : "+idDips);
        Log.e("CEK",mContext+" KTP : "+KTP.length);
        Log.e("CEK",mContext+" KTP_SWAFOTO : "+KTP_SWAFOTO.length);
        Log.e("CEK",mContext+" NPWP : "+NPWP.length);
        Log.e("CEK",mContext+" TTD : "+TTD.length);
        Log.e("CEK",mContext+" form_id : "+form_id);
        Log.e("CEK",mContext+" idPhoto : "+idPhoto);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        Log.e("CEK",mContext+" isSessionZoom : "+isSessionZoom);
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }
        if (formCode == 8) {
            keysData = "datadiri";
        } else if (formCode == 801) {
            keysData = "alamatberbeda";
        } else if (formCode == 802) {
            keysData = "pekerjaan";
        } else if (formCode == 803) {
            keysData = "keuangan";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_cif, container, false);

        inclHead = views.findViewById(R.id.inclHead);
        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);
        iconKtp = (LinearLayout) views.findViewById(R.id.icon_ktp);
        iconNpwp = (LinearLayout) views.findViewById(R.id.icon_npwp);
        iconSignature = (LinearLayout) views.findViewById(R.id.icon_signature);
        iconForm = (LinearLayout) views.findViewById(R.id.icon_form);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);
        
        inclBodyUpload = views.findViewById(R.id.inclBodyUpload);
        btnCamera = (ImageView) views.findViewById(R.id.choose_camera);
        btnGallery = (LinearLayout) views.findViewById(R.id.choose_gallery);
        btnNext = (Button) views.findViewById(R.id.btnNext);
        delete = (Button) views.findViewById(R.id.delete);
        viewImage = (ImageView) views.findViewById(R.id.Imageview);
        LL = (LinearLayout) views.findViewById(R.id.BackgroundLL);
        chooseImage = (LinearLayout) views.findViewById(R.id.Choose_Image);

        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);

        btnProses = (Button) views.findViewById(R.id.btnProses);
        
        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (formCode > 7) {
            dataFormCIF = new JSONObject();
            inclBodyUpload.setVisibility(View.GONE);
            llFormBuild.setVisibility(View.VISIBLE);
            if (formCode == 8 && form_id == 0) {
                form_id = 10;
            }

            if (formCode == 8 || form_id == 5 || form_id == 14) {
                if (KTP.length > 0) {
                    iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                }
                if (NPWP.length > 0) {
                    iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                }
                if (TTD.length > 0) {
                    iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
                }
                iconForm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            }

            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    processGetForm(form_id);
                }
            });

            processGetForm(form_id);
        } else {
            inclBodyUpload.setVisibility(View.VISIBLE);
            llFormBuild.setVisibility(View.GONE);
            btnProses.setVisibility(View.GONE);
            btnNext.setClickable(false);
            btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
        }

        if (KTP.length > 0 && KTP_SWAFOTO.length == 0) {
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

        if (KTP.length == 0 && isSessionZoom) {
            PopUp();
            sessions.saveOCR(null);
        }

        if (formCode < 8) {
            if (KTP.length == 0 && formCode == 4) {
                iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            }
            if (KTP.length > 0) {
                iconKtp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }
            if (NPWP.length == 0 && formCode == 6) {
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            }
            if (NPWP.length > 0) {
                iconNpwp.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif_success));
            }
            if (TTD.length == 0 && formCode == 7) {
                iconSignature.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
            }
            if (TTD.length > 0) {
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
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LL.setBackground(mContext.getResources().getDrawable(R.drawable.bg));
                    btnNext.setClickable(false);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                    viewImage.setVisibility(View.GONE);
                    chooseImage.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.GONE);
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("CEK","btnNext formCode : "+formCode+" | KTP : "+KTP.length+" | KTP_SWAFOTO : "+KTP_SWAFOTO.length);
                    if (formCode == 4) {
                        if (KTP.length > 0 && KTP_SWAFOTO.length == 0) {
                            KTP_SWAFOTO = imageBytes;
                            isSwafoto = true;
                            sessions.saveIsSwafoto(isSwafoto);
                        }
                    } else if (formCode == 6) {
                        NPWP = imageBytes;
                        NPWP_BASE64 = encodedImage;
                    } else if (formCode == 7) {
                        TTD = imageBytes;
                        TTD_BASE64 = encodedImage;
                    }

                    if ((NPWP.length == 0 && formCode == 6) || (TTD.length == 0 && formCode == 7)){
                        Toast.makeText(mContext, getResources().getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if (KTP_SWAFOTO.length == 0 || chkFlow == 0) {
                            //saveImage();
                            if (formCode == 4) {
                                if (flagOCR) {
                                    if (KTP.length == 0) {
                                        KTP = imageBytes;
                                        KTP_BASE64 = encodedImage;
                                    }

                                    if ((KTP.length == 0 && formCode == 4)) {
                                        Toast.makeText(mContext, getResources().getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                                    } else {
                                        PopUpOCR();
                                    }
                                } else {
                                    Toast.makeText(mContext, "Maaf, OCR masih dalam proses...!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (isSessionZoom) {
                                Bundle bundle = new Bundle();
                                if (formCode == 4) {
                                    bundle.putByteArray("ktp", KTP);
                                    bundle.putByteArray("swafoto", KTP_SWAFOTO);
                                    sessions.saveFormCOde(6);
                                    rabbitMirroring.MirroringSendEndpoint(6);
                                    if (!picturePath.isEmpty()) {
                                        String fieldName = "foto";
                                        processFormDataAttachment(fieldName,picturePath);
                                    }
                                } else if (formCode == 6) {
                                    bundle.putByteArray("ktp",KTP);
                                    bundle.putByteArray("swafoto", KTP_SWAFOTO);
                                    bundle.putByteArray("npwp",NPWP);
                                    sessions.saveFormCOde(7);
                                    rabbitMirroring.MirroringSendEndpoint(7);
                                    if (!picturePath.isEmpty()) {
                                        String fieldName = "npwp";
                                        processFormDataAttachment(fieldName,picturePath);
                                    }
                                } else if (formCode == 7) {
                                    bundle.putByteArray("ktp",KTP);
                                    bundle.putByteArray("swafoto", KTP_SWAFOTO);
                                    bundle.putByteArray("npwp",NPWP);
                                    bundle.putByteArray("ttd",TTD);
                                    bundle.putInt("form_id",10);
                                    sessions.saveFormCOde(8);
                                    rabbitMirroring.MirroringSendEndpoint(8);
                                    if (!picturePath.isEmpty()) {
                                        String fieldName = "ttd";
                                        processFormDataAttachment(fieldName,picturePath);
                                    }
                                }
                                sendDataFragment(bundle, new frag_cif());
                            } else {
                                Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                                intent.putExtra("RESULT_IMAGE_AI", bytePhoto);
                                startActivity(intent);
                                ((Activity) mContext).finishAffinity();
                            }
                        }
                    }
                }
            });
        } else {
            btnProses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext,"SEDANG DIPROSES...!!",Toast.LENGTH_SHORT).show();
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

                        Log.e("CEK","flagNext : "+flagNext);
                        Log.e("CEK","objEl : "+objEl.toString());
                        if (flagNext) {
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

                            if (objEl.has("pernyataan") && formCode == 8) {
                                try {
                                    boolean pernyataan = objEl.getBoolean("pernyataan");
                                    Bundle bundle = new Bundle();
                                    bundle.putByteArray("ktp",KTP);
                                    bundle.putByteArray("swafoto", KTP_SWAFOTO);
                                    bundle.putByteArray("npwp",NPWP);
                                    bundle.putByteArray("ttd",TTD);

                                    if (pernyataan) {
                                        int intLayoutSelf = 801;
                                        rabbitMirroring.MirroringSendKey(dataFormCIF);
                                        rabbitMirroring.MirroringSendEndpoint(intLayoutSelf);
                                        bundle.putInt("form_id",9);
                                        sessions.saveFormCOde(intLayoutSelf);
                                        sessions.saveFormReq(reqFormSend.toString());
                                        sendDataFragment(bundle, new frag_cif());
                                    } else {
                                        int intLayoutWork = 802;
                                        rabbitMirroring.MirroringSendKey(dataFormCIF);
                                        rabbitMirroring.MirroringSendEndpoint(intLayoutWork);
                                        bundle.putInt("form_id",5);
                                        sessions.saveFormCOde(intLayoutWork);
                                        processSendFormCIF(reqFormSend);
                                        sendDataFragment(bundle, new frag_cif());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Bundle bundle = new Bundle();
                                int intLayoutWork = 0;
                                if (formCode == 801) {
                                    intLayoutWork = 802;
                                    bundle.putInt("form_id",5);
                                } else if (formCode == 802) {
                                    intLayoutWork = 803;
                                    bundle.putInt("form_id",14);
                                } else if (formCode == 803) {
                                    intLayoutWork = 804;
                                }
                                bundle.putByteArray("ktp",KTP);
                                bundle.putByteArray("swafoto", KTP_SWAFOTO);
                                bundle.putByteArray("npwp",NPWP);
                                bundle.putByteArray("ttd",TTD);
                                rabbitMirroring.MirroringSendKey(dataFormCIF);
                                rabbitMirroring.MirroringSendEndpoint(intLayoutWork);
                                sessions.saveFormCOde(intLayoutWork);
                                Log.e("CEK","GET CIF : "+sessions.getCIF());
                                processSendFormCIF(reqFormSend);
                                if (intLayoutWork == 804 && sessions.getCIF() != null) {
                                    sendDataFragment(bundle, new frag_cif_full());
                                } else {
                                    sendDataFragment(bundle, new frag_cif());
                                }
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isSessionZoom) {
            rabbitMirroring.closeThreadConnection();
        }
    }

    private JSONObject dataReqForm() {
        JSONObject dataFormObj = null;
        try {
            String forms = objEl.toString();
            if (sessions.getFormReq() != null) {
                forms = sessions.getFormReq();
            }
            dataFormObj = new JSONObject(forms);
            if (formCode != 801) {
                dataFormObj.put("id", idPhoto);
            } else {
                JSONObject dataFormObj2 = new JSONObject(objEl.toString());

                String alamat2 = dataFormObj2.getString("alamattempattinggalsaatini");
                String rt2 = dataFormObj2.getString("rt");
                String rw2 = dataFormObj2.getString("rw");
                String kelurahandesa2 = dataFormObj2.getString("kelurahandesa");
                String kecamatan2 = dataFormObj2.getString("kecamatan");
                String kabupatenkota2 = dataFormObj2.getString("kabupatenkota");
                String provinsi2 = dataFormObj2.getString("provinsi");
                String kodepos2 = dataFormObj2.getString("kodepos");

                dataFormObj.put("alamat2",alamat2);
                dataFormObj.put("rt2",rt2);
                dataFormObj.put("rw2",rw2);
                dataFormObj.put("kelurahandesa2",kelurahandesa2);
                dataFormObj.put("kecamatan2",kecamatan2);
                dataFormObj.put("kabupatenkota2",kabupatenkota2);
                dataFormObj.put("provinsi2",provinsi2);
                dataFormObj.put("kodepos2",kodepos2);
            }

            if (dataFormObj.has("pernyataan")) {
                boolean pernyataan = dataFormObj.getBoolean("pernyataan");
                dataFormObj.put("datatidaksesuai",pernyataan);
                dataFormObj.remove("pernyataan");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataFormObj;
    }

    private void processSendFormCIF(JSONObject jsons) {
        Log.e("CEK","processSendFormCIF : "+jsons.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = null;
        if (formCode == 8 || formCode == 801) {
            call = API.AddDataSelf(requestBody);
        } else if (formCode == 802) {
            call = API.AddDataWork(requestBody);
        } else if (formCode == 803) {
            call = API.AddDataFinance(requestBody);
        }

        Log.e("CEK","processSendFormCIF call : "+call.request());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processSendFormCIF code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processSendFormCIF dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode >= 200 && errCode <= 300) {
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                            sessions.saveFormReq(null);
                        } else {
                            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processGetForm(int formId) {
        Log.e("CEK", this+" MASUK processGetForm formId : "+formId);
        Log.e("CEK", this+" MASUK formCode : "+formCode);
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId).enqueue(new Callback<JsonObject>() {
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
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        Log.e("CEK","dataForm : "+dataForm);
                        MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataForm, llFormBuild);
                        idElement = parseForm.getForm();
                        Log.e("CEK","dataElement : "+idElement.toString());
                        processValidationActionForm();
                        dataFormCIF.put(keysData,objEl);
                        Log.e("CEK","DATA FORM : "+dataFormCIF.toString());
                        rabbitMirroring.MirroringSendKey(dataFormCIF);
                        if (formCode == 8) {
                            if (sessions.getOCR() != null) {
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
            }
        });
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
                            Log.e("CEK","nameDataEl : "+nameDataEl);
                            if (idEl == idDataEl) {
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
                                    ed.setText(valEl);
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = (RadioButton) rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();
                                        String valEl = objEl.getString(nameDataEl);
                                        String valRad = valEl.toLowerCase();
                                        if (valEl.toLowerCase().equals("kawin")) {
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
                                            }
                                        }
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
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
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
                                    for (int ch = 0; ch < spin.getCount(); ch++) {
                                        if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                            spin.setSelection(ch);
                                            break;
                                        }
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
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

        /*try {
            for (int k = 0; k < idElement.length(); k++) {
                int idEl = idElement.getJSONObject(k).getInt("id");
                String nameEl = idElement.getJSONObject(k).getString("name");
                for(Iterator<String> iter = objEl.keys(); iter.hasNext();) {
                    String key = iter.next();
                    if (nameEl.equals(key)) {
                        String valEl = objEl.getString(key);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    private void processMatchData() {
        Log.e("CEK","processMatchData");
        String dataOCR = sessions.getOCR();
        Log.e("CEK","dataOCR : "+dataOCR.toString());
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

            String tgllahirOCR = "";
            if (ttlOCR.contains(",")) {
                String[] sp = ttlOCR.split(",");
                tgllahirOCR = sp[1].trim();
                String[] sp2 = tgllahirOCR.split("-");
                String tglOCR = sp2[0];
                String blnOCR = sp2[1];
                String thnOCR = sp2[2];
                int intBln = Integer.parseInt(blnOCR);
                String labelBln = blnIndo(intBln);
                tgllahirOCR = tglOCR+" "+labelBln+" "+thnOCR;
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
                String key = iter.next();
                Log.e("CEK","key : "+key);
                if (key.contains("nama") && key.contains("identitas")) {
                    objEl.put(key,namaOCR);
                } else if (key.contains("provinsi")) {
                    objEl.put(key,provinsiOCR);
                } else if (key.contains("kabupaten") || key.contains("kota")) {
                    objEl.put(key,kabkotOCR);
                } else if (key.contains("noidentitas") || key.contains("nomoridentitas")) {
                    objEl.put(key,nikOCR);
                } else if (key.contains("tempat") && key.contains("lahir")) {
                    objEl.put(key,tempatlahirOCR);
                } else if (key.contains("tanggal") && key.contains("lahir")) {
                    objEl.put(key,tgllahirOCR);
                } else if (key.contains("kelamin")) {
                    objEl.put(key,jeniskelaminOCR);
                } else if (key.contains("alamat") && key.contains("identitas")) {
                    objEl.put(key,alamatOCR);
                } else if (key.equals("rt")) {
                    objEl.put(key,rtOCR);
                } else if (key.equals("rw")) {
                    objEl.put(key,rwOCR);
                } else if (key.contains("kelurahan") || key.contains("desa")) {
                    objEl.put(key,desaOCR);
                } else if (key.contains("kecamatan")) {
                    objEl.put(key,kecamatanOCR);
                } else if (key.contains("agama")) {
                    objEl.put(key,agamaOCR);
                } else if (key.contains("nikah") || key.contains("menikah")) {
                    objEl.put(key,kawinOCR);
                } else if (key.contains("warganegara")) {
                    objEl.put(key,wargaOCR);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String blnIndo(int bln) {
        List<String> monthIn = new ArrayList<>();
        monthIn.add("Januari");
        monthIn.add("Februari");
        monthIn.add("Maret");
        monthIn.add("April");
        monthIn.add("Mei");
        monthIn.add("Juni");
        monthIn.add("Juli");
        monthIn.add("Agustus");
        monthIn.add("September");
        monthIn.add("Oktober");
        monthIn.add("November");
        monthIn.add("Desember");
        return monthIn.get(bln);

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
                                            Log.e("CEK",nameDataEl+" : "+charSequence);
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormCIF.put(keysData,objEl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (chkFlow == 0 || isSessionZoom) {
                                                rabbitMirroring.MirroringSendKey(dataFormCIF);
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

                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = (RadioButton) rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormCIF.put(keysData,objEl);
                                                    if (isSessionZoom) {
                                                        rabbitMirroring.MirroringSendKey(dataFormCIF);
                                                    }
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
                                                    dataFormCIF.put(keysData,objEl);
                                                    if (isSessionZoom) {
                                                        rabbitMirroring.MirroringSendKey(dataFormCIF);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
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
                                                dataFormCIF.put(keysData,objEl);
                                                if (isSessionZoom) {
                                                    rabbitMirroring.MirroringSendKey(dataFormCIF);
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
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
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
                                                    rabbitMirroring.MirroringSendKey(dataFormCIF);
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
                                                    rabbitMirroring.MirroringSendKey(dataFormCIF);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

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
                                                        REQUESTCODE_GALLERY = 201;
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

    private void chooseFromSD() {
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    private void chooseFromCamera() {
        picturePath = "";
        if ((chkFlow == 1 || isSessionZoom) && KTP.length > 0 && KTP_SWAFOTO.length == 0){
            Intent intent = new Intent(mContext, DipsCameraSource.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUESTCODE_SWAFOTO);
        } else {
            Intent intent = new Intent(mContext, DipsCameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUESTCODE_CAPTURE);
        }
    }

    private JSONObject dataReqOCR() {
        try {
            datasReqOCR.put("nik",nik);
            datasReqOCR.put("nama",nama);
            datasReqOCR.put("tempatlahir",tmptLahir);
            datasReqOCR.put("ttl",ttl);
            datasReqOCR.put("idDips", idDips);
            datasReqOCR.put("code", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sessions.saveOCR(datasReqOCR.toString());

        return datasReqOCR;
    }

    private void processFormDataAttachment(String fieldName, String filePath) {
        Log.e("CEK","processFormDataAttachment fieldName : "+fieldName+" | "+filePath+" | idPhoto : "+idPhoto+" | idDips : "+idDips);
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);

        RequestBody requestID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idPhoto));
        RequestBody requestidDips = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idDips));

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = null;
        MultipartBody multipartBody = null;
        String contentType = "";
        if (fieldName.equals("ktp")) {
            multipartBody = new MultipartBody.Builder()
                    .addPart(MultipartBody.Part.createFormData(fieldName,file.getName(),requestFile))
                    .build();
            contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();
        } else if (fieldName.equals("foto")) {
            multipartBody = new MultipartBody.Builder()
                    .addPart(MultipartBody.Part.createFormData(fieldName,file.getName(),requestFile))
                    .addPart(MultipartBody.Part.createFormData("id",null,requestID))
                    .addPart(MultipartBody.Part.createFormData("idDips",null,requestidDips))
                    .build();
            contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();
        } else {
            multipartBody = new MultipartBody.Builder()
                    .addPart(MultipartBody.Part.createFormData(fieldName,file.getName(),requestFile))
                    .addPart(MultipartBody.Part.createFormData("id",null,requestID))
                    .build();
            contentType = "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary();
        }

        call = API.formAttachment(contentType,multipartBody);

        Log.e("CEK","processFormDataAttachment call url : "+call.request().url());
        Log.e("CEK","processFormDataAttachment call body : "+call.request());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","processFormDataAttachment response code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","processFormDataAttachment : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("code");
                        String msg = dataObj.getString("message");
                        if (errCode == 200 || errCode == 202) {
                            int ids = dataObj.getJSONObject("data").getInt("id");
                            Log.e("CEK","ID CUST : "+ids);
                            sessions.saveIDCust(ids);
                            if (isSessionZoom) {
                                if (formCode == 4 && flagOCR && KTP_SWAFOTO.length == 0) {
                                    JSONObject dataReq = dataReqOCR();
                                    JSONObject reqOCR = new JSONObject();
                                    try {
                                        reqOCR.put("ocr", dataReq);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    rabbitMirroring.MirroringSendKey(reqOCR);
                                }
                            }
                            if (formCode == 4 && flagOCR) {
                                if (!isSwafoto) {
                                    Bundle bundle = new Bundle();
                                    bundle.putByteArray("ktp", KTP);
                                    bundle.putByteArray("RESULT_IMAGE_AI", bytePhoto);

                                    sendDataFragment(bundle, new frag_cif());
                                }
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopUpOCR(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_ocr, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        EditText NIK = (EditText) dialogView.findViewById(R.id.et_nik_ocr);
        EditText Nama = (EditText) dialogView.findViewById(R.id.et_name_ocr);
        EditText TTL = (EditText) dialogView.findViewById(R.id.et_ttl_ocr);
        EditText TTL2 = (EditText) dialogView.findViewById(R.id.et_ttl2_ocr);
        Button btnOCRCancel = (Button) dialogView.findViewById(R.id.btncncl);
        Button btnOCRNext = (Button) dialogView.findViewById(R.id.btnlnjt);
        NIK.setText(nik);
        Nama.setText(nama);;
        TTL.setText(ttl);
        TTL2.setText(ttl);

        btnOCRCancel.setBackgroundTintList(mContext.getColorStateList(R.color.button_end_call));
        btnOCRNext.setBackgroundTintList(mContext.getColorStateList(R.color.Blue));

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

        btnOCRNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSessionZoom) {
                    nik = NIK.getText().toString().trim();
                    nama = Nama.getText().toString().trim();
                    tmptLahir = TTL.getText().toString().trim();
                    ttl = TTL2.getText().toString().trim();
                    if (!picturePath.isEmpty()) {
                        String fieldName = "ktp";
                        processFormDataAttachment(fieldName,picturePath);
                    }
                    //rabbitMirroring.MirroringSendEndpoint(5);
                }
                sweetAlertDialog.dismiss();
            }
        });
        btnOCRCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
                KTP = new byte[0];
                KTP_BASE64 = "";
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.close();

        return mediaFile;
    }

    private void PopUp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.popupktp));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.Blue));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Log.e("CEK","requestCode : "+requestCode+" | chkFlow : "+chkFlow+" | KTP : "+KTP.length+" | KTP_SWAFOTO : "+KTP_SWAFOTO.length+" | isCust : "+isCust);
            if (requestCode == REQUESTCODE_CAPTURE){
                Log.e("CEK","RETURN CAMERA");
                sessions.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                byte[] resultRealCamera = data.getByteArrayExtra("real");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);
                Bitmap bitmap_real = BitmapFactory.decodeByteArray(resultRealCamera, 0, resultRealCamera.length);

                try {
                    mediaFilePhoto = createTemporaryFile(resultCamera);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                picturePath = mediaFilePhoto.getAbsolutePath();
                Log.e("CEK","onActivityResult picturePath : "+picturePath);
                LL.setBackgroundResource(0);
                btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                btnNext.setClickable(true);
                delete.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.red_btn_bg_pressed_color));
                delete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                viewImage.setImageBitmap(bitmap);
                processSendImage(bitmap);
                Log.e("CEK","IMAGE KTP : "+KTP.length);
                Log.e("CEK","IMAGE KTP_SWAFOTO : "+KTP_SWAFOTO.length);
                if (formCode == 4) {
                    if (KTP.length == 0 && KTP_SWAFOTO.length == 0) {
                        imgtoBase64OCR(bitmap_real);
                    }
                }
                /*if (KTP.length > 0 && KTP_SWAFOTO.length == 0) {
                    imgtoBase64OCR(bitmap_real);
                }*/
            }
            else if (requestCode == REQUESTCODE_GALLERY){
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = mContext.getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();

                Log.e("CEK","onActivityResult picturePath : "+picturePath);
                Log.e("CEK","REQUESTCODE_GALLERY : "+REQUESTCODE_GALLERY);
                Log.e("CEK","IMAGE KTP : "+KTP.length);
                Log.e("CEK","IMAGE KTP_SWAFOTO : "+KTP_SWAFOTO.length);

                if (REQUESTCODE_GALLERY == 201) {

                } else {
                    sessions.saveFlagUpDoc(true);
                    LL.setBackgroundResource(0);
                    btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                    btnNext.setClickable(true);
                    delete.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.red_btn_bg_pressed_color));
                    delete.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                    prosesOptimalImage(picturePath);
                }
            } else if (requestCode == REQUESTCODE_SWAFOTO){
                if (chkFlow == 1 || isSessionZoom) {
                    if (KTP.length > 0) {
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
                        btnNext.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));
                        btnNext.setClickable(true);
                        delete.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.red_btn_bg_pressed_color));
                        delete.setVisibility(View.VISIBLE);
                        viewImage.setVisibility(View.VISIBLE);
                        chooseImage.setVisibility(View.GONE);
                        viewImage.setImageBitmap(bitmap);
                        if (isSessionZoom) {
                            processSendImage(bitmap);
                        }
                    }
                } else {
                    if (isCust) {
                        sessions.clearCIF();
                        getFragmentPage(new frag_portfolio());
                    } else {
                        byte[] DataKTP = data.getExtras().getByteArray("DataKTP");
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("ktp",DataKTP);
                        sendDataFragment(bundle, new frag_opening_account2());
                    }
                }
            }
        }
    }

    private void prosesOptimalImage(String picturePath) {
        File mediaFile = new File(picturePath);
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        int file_size = Integer.parseInt(String.valueOf(mediaFile.length()/1024));
        Log.d("CEK", "file_size : "+file_size);

        int perDiff = 1;
        if (file_size > 3072) {
            perDiff = 8;
        } else if (file_size > 2048) {
            perDiff = 6;
        } else if (file_size > 1024) {
            perDiff = 4;
        } else if (file_size > 550) {
            perDiff = 2;
        }

        if (perDiff == 1) {
            viewImage.setImageBitmap(thumbnail);
            imgtoBase64(thumbnail);
        } else {
            getResizedBitmap(thumbnail, (thumbnail.getWidth() / perDiff), (thumbnail.getHeight() / perDiff));
        }
        if (formCode == 4) {
            if (KTP.length == 0 && KTP_SWAFOTO.length == 0) {
                imgtoBase64OCR(thumbnail);
            }
        }
        /*if (KTP.length > 0 && KTP_SWAFOTO.length == 0) {
            imgtoBase64OCR(thumbnail);
        }*/
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
                            Log.d("CEK","MASUK KIRIM IMAGE");
                            imgtoBase64(bitmap);
                            break;
                        }
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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
        imgtoBase64(resizedBitmap);
    }

    private void imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        String imgBase64 = "";
        String keys = "";
        Log.e("CEK", "formCode : "+formCode);
        Log.e("CEK", "KTP : "+KTP.length);
        Log.e("CEK", "KTP_SWAFOTO : "+KTP_SWAFOTO.length);
        if (formCode == 4) {
            if (KTP.length > 0 && KTP_SWAFOTO.length == 0) {
                imgBase64 = encodedImage;
                keys = "swafoto";
            } else {
                imgBase64 = encodedImage;
                keys = "ktp";
            }
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        ocrKTP(encodedImage);
    }

    private void ocrKTP(String base){
        Log.d("Masuk OCR","");
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("image",base);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.ocrKtp(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Response OCR",""+response.code());
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
                    Log.d("CEK","MASUK ELSE");
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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

    private void sendDataFragment(Bundle bundle, Fragment fragment){
        fragment.setArguments(bundle);
        if (chkFlow == 0 || isSessionZoom) {
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