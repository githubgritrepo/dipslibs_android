package com.evo.mitzoom.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.SingleMediaScanner;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_new_account2 extends Fragment {
    private Context context;
    private ImageView btnBack;
    private String nama, tgl, produk, nominal, idDips;
    private TextView tv_nama, tv_tgl, tv_produk, tv_nominal;
    private Button btnProses;
    private LinearLayout icon_isi_form, icon_konfirmasi_data, icon_resi;
    private CheckBox pernyataan;
    private SessionManager session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_new_account2, container, false);
        tv_nama = view.findViewById(R.id.nama_lengkap_new_acc);
        tv_produk = view.findViewById(R.id.tipe_produk_rek_new_acc);
        tv_tgl = view.findViewById(R.id.tgl_pembukaan_new_acc);
        tv_nominal = view.findViewById(R.id.setoran_awal_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        icon_konfirmasi_data = view.findViewById(R.id.icon_konfirmasi_data);
        icon_resi = view.findViewById(R.id.icon_resi);
        btnBack = (ImageView) view.findViewById(R.id.btn_back_new_acc);
        btnProses = view.findViewById(R.id.btnProses_konfirmasi_data);
        pernyataan = view.findViewById(R.id.pernyataan2);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        Bundle arg = getArguments();
        nama = arg.getString("nama_lengkap");
        tgl = arg.getString("tgl");
        produk = arg.getString("produk");
        nominal = arg.getString("nominal");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new frag_new_account();
                Bundle bundle = new Bundle();
                bundle.putString("nama_lengkap",nama);
                bundle.putString("produk",produk);
                bundle.putString("nominal",nominal);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        tv_nama.setText(nama);
        tv_produk.setText(produk);
        tv_tgl.setText(tgl);
        tv_nominal.setText("Rp"+nominal);
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                icon_resi.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                saved();

            }
        });
    }
    private void saved(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyMMdd");
        String tgl_daftar;
        tgl_daftar = df.format(c.getTime());
        int nominal_amount = Integer.parseInt(nominal.replace(".",""));
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("namaLengkap",nama);
            jsons.put("tanggalPembukaan",tgl_daftar);
            jsons.put("tipeProduk",produk);
            jsons.put("setoranAwal",nominal_amount);
            jsons.put("mataUang","rupiah");
            jsons.put("status","diajukan");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("CEK SAVE NEW ACC",""+jsons);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.createAccount(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        String url = jsObj.getString("url");
                        String endpoint = Server.BASE_URL_API;
                        generateBarcode(endpoint+url);
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
    private void generateBarcode(String content) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_barcode_rtgs, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setConfirmText("Download");
        sweetAlertDialog.setCancelText("Tutup");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        byte[] imgByte = null;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE,200,200);
            ImageView barcodeFormRTGS = (ImageView) dialogView.findViewById(R.id.barcodeFormRTGS);
            barcodeFormRTGS.setImageBitmap(bitmap);
            imgByte = imgtoByteArray(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        byte[] finalImgByte = imgByte;
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());
                        String filename = "Barcode_No_Formulir_Daftar_"+timeStamp+".jpg";
                        createTemporaryFile(finalImgByte, filename);

                        String appName = getString(R.string.app_name_dips);

                        String contents = "File disimpan di folder Phone/DCIM/" + appName + "/" + filename;

                        SweetAlertDialog sAW = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                        sAW.setContentText(contents);
                        sAW.hideConfirmButton();
                        sAW.setCancelText("Tutup");
                        sAW.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                getFragmentPage(new frag_berita());
                                sAW.dismiss();
                            }
                        });
                        sAW.setCancelable(false);
                        sAW.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());
                        String filename = "Barcode_No_Formulir_Daftar_"+timeStamp+".jpg";
                        createTemporaryFile(finalImgByte, filename);

                        String appName = getString(R.string.app_name_dips);

                        String contents = "File disimpan di folder Phone/DCIM/" + appName + "/" + filename;

                        SweetAlertDialog sAW = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                        sAW.setContentText(contents);
                        sAW.hideConfirmButton();
                        sAW.setCancelText("Tutup");
                        sAW.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                getFragmentPage(new frag_berita());
                                sAW.dismiss();
                            }
                        });
                        sAW.setCancelable(false);
                        sAW.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private byte[] imgtoByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }
    private File createTemporaryFile(byte[] byteImage, String filename) throws Exception {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), IMAGE_DIRECTORY_NAME);

        /*if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        } else if (!mediaStorageDir.mkdirs()) {
            return null;
        }*/

        String[] myFiles;

        myFiles = mediaStorageDir.list();
        if (myFiles != null) {
            for (int i = 0; i < myFiles.length; i++) {
                Log.d("CEK","myFiles ke-"+i+" : "+myFiles[i]);
                File myFile = new File(mediaStorageDir, myFiles[i]);
                myFile.delete();
            }
        }

        mediaStorageDir.mkdirs();

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.flush();
        fos.close();

        //galleryAddPic(mediaFile.getPath());
        new SingleMediaScanner(context,mediaFile);

        return mediaFile;
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
