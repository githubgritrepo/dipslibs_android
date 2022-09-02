package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Constants.MyConstants;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_opening_account extends Fragment {
    public static final int REQUEST_WRITE_PERMISSION = 786;
    private Context context;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private ImageView viewImage,btnCamera;
    private LinearLayout btnGallery;
    private Button btnNext, delete, btnOCR1, btnOCR2;
    private LinearLayout chooseImage;
    private byte[] KTP;
    private LayoutInflater inflater;
    private View dialogView;
    private LinearLayout LL;
    private String idDips, KTP_BASE64, provinsi, kota_kabupaten, nik, nama, ttl, jeniskelamin, golongan_darah, alamat, rtrw, desa_kelurahan, kecamatan, agama, status_perkawinan, kewarganegaraan, pekerjaan;
    private SessionManager session;
    private TextView NIK, Nama, TTL, TTL2;
    private boolean flagOCR = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_opening_account, container, false);
        btnCamera = view.findViewById(R.id.choose_camera);
        btnGallery = view.findViewById(R.id.choose_gallery);
        btnNext = view.findViewById(R.id.btnNext);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        viewImage = view.findViewById(R.id.Imageview);
        chooseImage = view.findViewById(R.id.Choose_Image);
        delete = view.findViewById(R.id.delete);
        LL = view.findViewById(R.id.BackgroundLL);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        PopUp();
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermission()){
                    Toast.makeText(context, "Permission denied",Toast.LENGTH_SHORT).show();
                    return;
                }
                session.saveMedia(1);
                chooseFromCamera();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermission()){
                    Toast.makeText(context, "Permission denied",Toast.LENGTH_SHORT).show();
                    return;
                }
                session.saveMedia(2);
                chooseFromSD();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(false,"");
                LL.setBackground(context.getResources().getDrawable(R.drawable.bg));
                btnNext.setClickable(false);
                btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                viewImage.setVisibility(View.GONE);
                chooseImage.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (KTP == null){
                    Toast.makeText(context, getResources().getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                }
                else{
                    //Mirroring(true,"");
                    //Mirroring2(true,"320124150585005","Andi Wijaya Lesmana","Bogor","13-03-1985");
                    //ocrKTP();
                    saveImage();
                    if (flagOCR) {
                        PopUpOCR(KTP);
                    } else {
                        Toast.makeText(context,"Maaf, OCR masih dalam proses...!!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
            return false;
        }
        return true;
    }
    private void sendDataFragment(String tag, byte[] Text, Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putByteArray(tag,Text);
        fragment.setArguments(bundle);
        getFragmentPage(fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
    private void PopUp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.popupktp));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
    }
    private void chooseFromSD() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 2);
    }
    private void chooseFromCamera() {
        Intent intent = new Intent(context, DipsCameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1){
                Log.e("CEK","RETURN CAMERA");
                session.saveFlagUpDoc(true);
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                byte[] resultRealCamera = data.getByteArrayExtra("real");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);
                Bitmap bitmap_real = BitmapFactory.decodeByteArray(resultRealCamera, 0, resultRealCamera.length);
                LL.setBackgroundResource(0);
                btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
                btnNext.setClickable(true);
                delete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                viewImage.setImageBitmap(bitmap);
                processSendImage(bitmap);
                imgtoBase64OCR(bitmap_real);
            }
            else if (requestCode == 2){
                session.saveFlagUpDoc(true);
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = context.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                LL.setBackgroundResource(0);
                btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
                btnNext.setClickable(true);
                delete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                prosesOptimalImage(picturePath);
                /*Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                getResizedBitmap(thumbnail, (thumbnail.getWidth()/4), (thumbnail.getHeight()/4));*/
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
        imgtoBase64OCR(thumbnail);
    }
    private void processSendImage(Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    for (int k = 0; k < 10; k++){
                        int onOfCamera = session.getCamera();
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
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void PopUpOCR(byte[] DataKTP){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_ocr,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        NIK = dialogView.findViewById(R.id.et_nik_ocr);
        Nama = dialogView.findViewById(R.id.et_name_ocr);
        TTL = dialogView.findViewById(R.id.et_ttl_ocr);
        TTL2= dialogView.findViewById(R.id.et_ttl2_ocr);
        btnOCR1 = dialogView.findViewById(R.id.btncncl);
        btnOCR2 = dialogView.findViewById(R.id.btnlnjt);
        NIK.setText(nik);
        Nama.setText(nama);;
        TTL.setText(ttl);
        TTL2.setText(ttl);

        //TextWatcher
        NIK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring2(false,s,Nama.getText().toString(),TTL.getText().toString(),TTL2.getText().toString());
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
                Mirroring2(false, NIK.getText().toString(), s,TTL.getText().toString(),TTL2.getText().toString());
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
                Mirroring2(false, NIK.getText().toString(), Nama.getText().toString(), s,TTL2.getText().toString());
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
                Mirroring2(false, NIK.getText().toString(), Nama.getText().toString(), TTL.getText().toString(),s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnOCR2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring(true,"");
                Mirroring2(true,NIK.getText().toString(),Nama.getText().toString(),TTL.getText().toString(),TTL2.getText().toString());
                sweetAlertDialog.dismiss();
                JSONObject dataCIF = dataCIFJson();
                session.saveCIF(dataCIF.toString());
                sendDataFragment("ktp",DataKTP,new frag_opening_account2());
            }
        });
        btnOCR1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring2(false,"","","","");
                sweetAlertDialog.dismiss();
            }
        });

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
        //return resizedBitmap;
        //imgtoByteArray(resizedBitmap);
        imgtoBase64(resizedBitmap);
    }
    private void imgtoBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        Mirroring(false,encodedImage);
        KTP = imageBytes;
        KTP_BASE64 = encodedImage;
    }
    private void imgtoBase64OCR(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        ocrKTP(encodedImage);
    }
    private void saveImage(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("image",KTP_BASE64);
            jsons.put("idDips",idDips);
            jsons.put("filename","coba_gambar");
            jsons.put("fieldname","ktp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.SaveImage(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        String message = jsObj.getString("message");
                        Log.d("CEK","MESSAGE = "+message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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
                if (response.isSuccessful()) {
                    flagOCR = true;
                    String dataS = response.body().toString();
                    Log.d("Response OCR",""+dataS);
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        provinsi = jsObj.getString("provinsi");
                        kota_kabupaten = jsObj.getString("kota_kabupaten");
                        nik = jsObj.getString("nik");
                        nama = jsObj.getString("nama");
                        ttl = jsObj.getString("ttl");
                        jeniskelamin = jsObj.getString("jeniskelamin");
                        golongan_darah = jsObj.getString("golongan_darah");
                        alamat = jsObj.getString("alamat");
                        rtrw = jsObj.getString("rtrw");
                        desa_kelurahan = jsObj.getString("desa_kelurahan");
                        kecamatan = jsObj.getString("kecamatan");
                        agama = jsObj.getString("agama");
                        status_perkawinan = jsObj.getString("status_perkawinan");
                        kewarganegaraan = jsObj.getString("kewarganegaraan");
                        pekerjaan = jsObj.getString("pekerjaan");

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
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JSONObject dataCIFJson() {
        String dataNik = NIK.getText().toString().trim();
        String dataNama = Nama.getText().toString().trim();

        String dataTitle = "";
        if (!jeniskelamin.trim().isEmpty() && jeniskelamin.trim() != null) {
            if (jeniskelamin.toUpperCase().equals("LAKI-LAKI")) {
                dataTitle = "Tn";
            } else if (jeniskelamin.toUpperCase().equals("PEREMPUAN")) {
                dataTitle = "Ny";
            }
        }

        String dataRt = "";
        String dataRw = "";
        if (!jeniskelamin.trim().isEmpty() && jeniskelamin.trim() != null) {
            if (rtrw.indexOf("/") > 0) {
                String[] sp = rtrw.split("/");
                dataRt = sp[0];
                dataRw = sp[1];
            } else {
                dataRt = rtrw;
                dataRw = rtrw;
            }
        }

        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(dataNama);
            jsonArray.put(dataTitle);
            jsonArray.put(alamat);
            jsonArray.put(dataRt);
            jsonArray.put(dataRw);
            jsonArray.put(desa_kelurahan);
            jsonArray.put(kecamatan);
            jsonArray.put(kota_kabupaten);
            jsonArray.put(provinsi);
            jsonArray.put("");
            jsonArray.put(kewarganegaraan);
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put(jeniskelamin);
            jsonArray.put(agama);
            jsonArray.put(status_perkawinan);
            jsonArray.put("");
            jsonArray.put(dataNik);
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put(false);
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put("");
            jsonArray.put(false);
            jsons.put("idDips",idDips);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsons;
    }

    private void Mirroring(Boolean bool, String base64){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(base64);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",4);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
                //Mirroring(false, base64);
            }
        });
    }
    private void Mirroring2(Boolean bool, CharSequence nik, CharSequence nama, CharSequence tempat, CharSequence ttl){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nik);
            jsonArray.put(nama);
            jsonArray.put(tempat);
            jsonArray.put(ttl);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",5);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
}
