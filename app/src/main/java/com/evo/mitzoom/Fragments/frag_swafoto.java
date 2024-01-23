package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsCameraSource;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_swafoto extends Fragment {

    private static final String TAG = "frag_swafoto";
    private final int REQUESTCODE_CAPTURE = 1;
    private final int REQUESTCODE_SWAFOTO = 10;
    private final int REQUEST_WRITE_PERMISSION = 786;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private SessionManager sessions;
    private String idDips;
    private boolean isSessionZoom;
    private ImageView btnBack;
    private TextView tvTitleService;
    private TextView tvAlertDoc;
    private View inclBodyUpload;
    private ImageView btnCamera;
    private LinearLayout btnGallery;
    private Button btnNext;
    private ImageView viewImage;
    private LinearLayout LL;
    private LinearLayout chooseImage;
    private ImageView imgDelete;
    private LinearLayout llOR;
    private File mediaImgKTP = null;
    private File mediaFilePhoto = null;
    private File mediaFilePhotoCropSwafoto = null;
    private byte[] IMG_BYTE = new byte[0];
    private String picturePath = "";
    private String picturePathCrop = "";
    private byte[] imageBytes = new byte[0];
    private String encodedImageCrop;
    private String tmptLahir = "-";
    private String provinsi,kodepos, kota_kabupaten, nik, nama, ttl, jeniskelamin, golongan_darah, alamat, rtrw, desa_kelurahan, kecamatan, agama, status_perkawinan, kewarganegaraan, pekerjaan = "", namaIbuKandung = "";
    private JSONObject datasReqOCR = null;
    private boolean flagMother = false;
    private String fragDest = "";
    private Button btnOCRNext;
    private int formCode = 22;
    private String imgPathKTP = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (getArguments() != null) {
            if (getArguments().containsKey("fragDest")) {
                fragDest = getArguments().getString("fragDest");
            }
            if (getArguments().containsKey("formCode")) {
                formCode = getArguments().getInt("formCode");
            }
            if (getArguments().containsKey("pathKTP")) {
                imgPathKTP = getArguments().getString("pathKTP");
                mediaImgKTP = new File(imgPathKTP);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_swafoto, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvTitleService = (TextView) views.findViewById(R.id.tvTitleService);

        tvAlertDoc = (TextView) views.findViewById(R.id.tvAlertDoc);
        inclBodyUpload = views.findViewById(R.id.inclBodyUpload);
        btnCamera = views.findViewById(R.id.choose_camera);
        btnGallery = views.findViewById(R.id.choose_gallery);
        btnNext = views.findViewById(R.id.btnNext);
        imgDelete = views.findViewById(R.id.imgDelete);
        viewImage = views.findViewById(R.id.Imageview);
        LL = views.findViewById(R.id.BackgroundLL);
        llOR = views.findViewById(R.id.llOR);
        chooseImage = views.findViewById(R.id.Choose_Image);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().popBackStack("FragUpdateData", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });

        if (formCode == 22) {
            tvAlertDoc.setText(getString(R.string.alert_swafoto));
            llOR.setVisibility(View.GONE);
            btnGallery.setVisibility(View.GONE);
            sessions.saveMedia(1);
            chooseFromCamera();
        }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if (imageBytes.length > 0 && formCode == 4) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fragDest", "updateData");
                    bundle.putInt("formCode", 22); //4 Upload KTP, 22 Swafoto
                    bundle.putString("pathKTP",picturePath);
                    Fragment fragment = new frag_swafoto();
                    fragment.setArguments(bundle);
                    getFragmentPageBackStack(fragment);
                }
            }
        });

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

    private void chooseFromSD() {
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    protected boolean requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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

    private void processSwafotoCheck() {
        File filePhoto = mediaFilePhoto;
        File filePhotoCrop = mediaFilePhotoCropSwafoto;
        if (mediaImgKTP != null) {
            filePhotoCrop = mediaImgKTP;
        }

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
                .addPart(MultipartBody.Part.createFormData("secondImage", mediaFilePhotoCropSwafoto.getName(), requestFileCrop))
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
                            imgtoBase64OCR();
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
        ocrKTP();
    }

    private void ocrKTP(){
        String baseImages = encodedImageCrop;
        if (!imgPathKTP.isEmpty()) {
            Bitmap bitmapKTP = BitmapFactory.decodeFile(imgPathKTP);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapKTP.compress(Bitmap.CompressFormat.JPEG,100, baos);
            byte[] imageBytesCrop = baos.toByteArray();
            baseImages = Base64.encodeToString(imageBytesCrop, Base64.NO_WRAP);
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
                                    provinsi = dataObj.getString("provinsi").trim();
                                }
                            }
                            if (dataObj.has("kota_kabupaten")) {
                                if (!dataObj.isNull("kota_kabupaten")) {
                                    kota_kabupaten = dataObj.getString("kota_kabupaten").trim();
                                }
                            }
                            if (dataObj.has("nik")) {
                                if (!dataObj.isNull("nik")) {
                                    nik = dataObj.getString("nik").trim();
                                }
                            }
                            if (dataObj.has("nama")) {
                                if (!dataObj.isNull("nama")) {
                                    nama = dataObj.getString("nama").trim();
                                }
                            }
                            if (dataObj.has("ttl")) {
                                if (!dataObj.isNull("ttl")) {
                                    ttl = dataObj.getString("ttl").trim();
                                }
                            }
                            if (dataObj.has("jeniskelamin")) {
                                if (!dataObj.isNull("jeniskelamin")) {
                                    jeniskelamin = dataObj.getString("jeniskelamin").trim();
                                }
                            }
                            if (dataObj.has("golongan_darah")) {
                                if (!dataObj.isNull("golongan_darah")) {
                                    golongan_darah = dataObj.getString("golongan_darah").trim();
                                }
                            }
                            if (dataObj.has("alamat")) {
                                if (!dataObj.isNull("alamat")) {
                                    alamat = dataObj.getString("alamat").trim();
                                }
                            }
                            if (dataObj.has("rtrw")) {
                                if (!dataObj.isNull("rtrw")) {
                                    rtrw = dataObj.getString("rtrw").trim();
                                }
                                if (rtrw.equals("null")) {
                                    rtrw = "";
                                }
                            }
                            if (dataObj.has("desa_kelurahan")) {
                                if (!dataObj.isNull("desa_kelurahan")) {
                                    desa_kelurahan = dataObj.getString("desa_kelurahan").trim();
                                }
                            }
                            if (dataObj.has("kecamatan")) {
                                if (!dataObj.isNull("kecamatan")) {
                                    kecamatan = dataObj.getString("kecamatan").trim();
                                }
                            }
                            if (dataObj.has("agama")) {
                                if (!dataObj.isNull("agama")) {
                                    agama = dataObj.getString("agama").trim();
                                }
                            }
                            if (dataObj.has("status_perkawinan")) {
                                if (!dataObj.isNull("status_perkawinan")) {
                                    status_perkawinan = dataObj.getString("status_perkawinan").trim();
                                }
                            }
                            if (dataObj.has("kewarganegaraan")) {
                                if (!dataObj.isNull("kewarganegaraan")) {
                                    kewarganegaraan = dataObj.getString("kewarganegaraan").trim();
                                }
                            }
                            if (dataObj.has("pekerjaan")) {
                                if (!dataObj.isNull("pekerjaan")) {
                                    pekerjaan = dataObj.getString("pekerjaan").trim();
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
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
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

                    if (!picturePath.isEmpty() && !namaIbuKandung.isEmpty()) {
                        sweetAlertDialog.cancel();
                        sweetAlertDialog.dismissWithAnimation();

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("ocrKTP",true);
                        bundle.putString("picturePath",picturePath);
                        bundle.putString("picturePathCrop",picturePathCrop);
                        Fragment fragment = null;
                        if (fragDest.equals("cifNew")) {
                            sessions.saveFormCOde(22);
                            fragment = new frag_cif_new();
                        } else {
                            //bundle.putInt("idGenerateForm", 59);
                            bundle.putInt("idGenerateForm", 77);
                            fragment = new frag_update_data();
                        }
                        fragment.setArguments(bundle);
                        getFragmentPage(fragment);
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
                llOR.setVisibility(View.GONE);
                btnGallery.setVisibility(View.GONE);
                imgDelete.setVisibility(View.GONE);
            }
        });

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);

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

    private void dialogFailedValidation(String kasus) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);
        if (kasus.equals("swafotocheck") || kasus.equals("OCR")){
            btnCancelDialog.setVisibility(View.VISIBLE);
            tvTitleDialog.setVisibility(View.GONE);
            imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_info));
            if (kasus.equals("swafotocheck") || kasus.equals("OCR")) {
                if (kasus.equals("OCR")) {
                    tvBodyDialog.setText(getString(R.string.ktpnotread));
                } else {
                    tvBodyDialog.setText(getString(R.string.capture_back));
                }
                btnConfirmDialog.setText(getString(R.string.ambil_gambar));
            }
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

                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
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
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUESTCODE_SWAFOTO){
                sessions.saveFlagUpDoc(true);
                String filePaths = data.getStringExtra("result_camera");
                String filePathsCrop = data.getStringExtra("result_cropImage");
                Bitmap bitmap = BitmapFactory.decodeFile(filePaths);
                Bitmap bitmapCrop = BitmapFactory.decodeFile(filePathsCrop);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapCrop.compress(Bitmap.CompressFormat.JPEG,100, baos);
                byte[] imageBytesCrop = baos.toByteArray();
                encodedImageCrop = Base64.encodeToString(imageBytesCrop, Base64.NO_WRAP);

                mediaFilePhoto = new File(filePaths);
                mediaFilePhotoCropSwafoto = new File(filePathsCrop);
                picturePath = filePaths;
                picturePathCrop = filePathsCrop;

                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }

                processSwafotoCheck();

            } else if (requestCode == REQUESTCODE_CAPTURE){
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
                btnNext.setClickable(true);
                imgDelete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                viewImage.setImageBitmap(bitmap);
                Bitmap thumbnail = prosesOptimalImage(picturePath,1);
                imgtoBase64(thumbnail);
            } else if (requestCode == REQUESTCODE_GALLERY){
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
                        btnNext.setClickable(true);
                        imgDelete.setVisibility(View.VISIBLE);
                        viewImage.setVisibility(View.VISIBLE);
                        chooseImage.setVisibility(View.GONE);
                    }
                    imgtoBase64(thumbnail);
                }
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

    private void imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        imageBytes = baos.toByteArray();
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

}