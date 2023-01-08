package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.SingleMediaScanner;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_new_resi extends Fragment {

    private Context mContext;
    private ImageView imgResume;
    private SessionManager sessions;
    private String dataRTGS;
    private Button btnOK;
    private String idDips;
    private Button btnUnduhResi;
    private byte[] bytePhoto = null;
    private JSONObject dataNasabah = null;
    private String no_handphone;
    private String namaNasabah = "";
    private String alamatNasabah = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        dataRTGS = sessions.getRTGS();
        Log.d("CEK","dataRTGS : "+dataRTGS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_new_resi, container, false);

        imgResume = v.findViewById(R.id.imgResume);
        btnOK = v.findViewById(R.id.btnSelesai);
        btnUnduhResi = (Button) v.findViewById(R.id.btnUnduhResi);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String dataJsonS = sessions.getNasabah();
        if (dataJsonS != null) {
            try {
                dataNasabah = new JSONObject(dataJsonS);
                no_handphone = dataNasabah.getString("noHP");
                namaNasabah = dataNasabah.getString("nama");
                alamatNasabah = dataNasabah.getString("alamat");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getResume();

        idDips = sessions.getKEY_IdDips();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CEK","MASUK BUTTON OK");
                sessions.clearPartData();
                String linkResi = "";
                Mirroring(true,1,1, linkResi);
                getFragmentPage(new frag_portfolio_new());
            }
        });

        btnUnduhResi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CEK","MASUK BUTTON UnduhResi");
                if (bytePhoto == null) {
                    Toast.makeText(mContext,"Tidak dapat mengunduh Formulir",Toast.LENGTH_SHORT).show();
                    return;
                }

                processDownload();
            }
        });

    }

    private void processDownload() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        try {
            JSONArray jsArr = new JSONArray(dataRTGS);
            int len = jsArr.length();
            if (len > 0) {
                String no_Form = "";
                for (int i = 0; i < 1; i++) {
                    String dataArr = jsArr.get(i).toString();
                    JSONObject dataJs = new JSONObject(dataArr);
                    no_Form = dataJs.getString("idForm");
                }

                String filename = "No_Formulir-"+no_Form +"-"+timeStamp+ ".jpg";
                createTemporaryFile(bytePhoto, filename);

                String appName = getString(R.string.app_name_dips);

                String contents = "File disimpan di folder Phone/DCIM/" + appName + "/" + filename;

                SweetAlertDialog sAW = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
                sAW.setContentText(contents);
                sAW.hideConfirmButton();
                sAW.setCancelText("Tutup");
                sAW.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sAW.dismiss();
                    }
                });
                sAW.setCancelable(false);
                sAW.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File createTemporaryFile(byte[] byteImage, String filename) throws Exception {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), IMAGE_DIRECTORY_NAME);

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

        new SingleMediaScanner(mContext,mediaFile);

        return mediaFile;
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void Mirroring(boolean bool, int page, int pageAll, String linkResi){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(page);
            jsonArray.put(pageAll);
            jsonArray.put(bool);
            jsonArray.put(linkResi);
            jsons.put("idDips",idDips);
            jsons.put("code",20);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CEK","PARAM MIRRORING : "+jsons.toString());
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

    private void getResume() {
        Log.e("CEK","MASUK GETRESUME");
        try {
            JSONArray jsArr = new JSONArray(dataRTGS);
            int len = jsArr.length();
            if (len > 0) {
                for (int i = 0; i < 1; i++) {
                    String dataArr = jsArr.get(i).toString();
                    JSONObject dataJs = new JSONObject(dataArr);
                    String idForm = dataJs.getString("idForm");
                    String sourceAccount = dataJs.getString("sourceAccount");
                    String sourceBank = dataJs.getString("sourceBank");
                    String sourceTypeService = dataJs.getString("sourceTypeService");
                    String sourceBenefit = dataJs.getString("sourceBenefit");
                    String sourcePopulation = dataJs.getString("sourcePopulation");
                    String rek_penerima = dataJs.getString("rek_penerima");
                    String nama_penerima = dataJs.getString("nama_penerima");
                    String alamat_penerima = dataJs.getString("alamat_penerima");
                    String nominal = dataJs.getString("nominal");
                    String berita = dataJs.getString("berita");

                    nominal = nominal.replace(".","");

                    int index = 5;
                    String etc = " ";
                    if (sourceTypeService.trim().toLowerCase().equals("rtgs")) {
                        index = 3;
                    } else if (sourceTypeService.trim().toLowerCase().equals("setoran")) {
                        index = 0;
                    } else if (sourceTypeService.trim().toLowerCase().equals("pemindahbukuan")) {
                        index = 1;
                    } else if (sourceTypeService.trim().toLowerCase().equals("kliring")) {
                        index = 2;
                    } else if (sourceTypeService.trim().toLowerCase().equals("inkaso")) {
                        index = 4;
                    } else {
                        etc = sourceTypeService.trim();
                    }

                    boolean penduduk = true;
                    if (sourcePopulation.trim().toLowerCase().indexOf("bukan") > -1) {
                        penduduk = false;
                    }

                    String noRek = "";
                    String namaRek = "";
                    String valueRek = "";
                    if (sourceAccount.indexOf("\n") > 0) {
                        String[] sp = sourceAccount.split("\n");
                        String typeAccount = sp[0].trim();
                        String no_nama_Rek = sp[1].trim();
                        if (no_nama_Rek.indexOf("-") > 0) {
                            String[] sp2 = no_nama_Rek.split("-");
                            noRek = sp2[0].trim();
                            namaRek = sp2[1].trim();
                        }
                        valueRek = sp[2].trim();
                    }

                    if (alamatNasabah.isEmpty()) {
                        alamatNasabah = " ";
                    }

                    int biaya = 2500;

                    Log.e("CEK","index : "+index+" | etc : "+etc+" | idForm : "+idForm+" | penduduk : "+penduduk);
                    Log.e("CEK","namaRek : "+namaRek+" | noRek : "+noRek+" | nominal : "+nominal+" | biaya : "+biaya);
                    Log.e("CEK","nama_penerima : "+nama_penerima+" | sourceBank : "+sourceBank+" | rek_penerima : "+rek_penerima+" | berita : "+berita);

                    ApiService API = Server.getAPIService();
                    Call<JsonObject> call = API.GetResumeTransaction(index,etc,idForm,penduduk,namaRek,alamatNasabah,no_handphone,noRek,nominal,biaya,
                            nama_penerima,alamat_penerima,sourceBank,rek_penerima,berita,"Hadi");
                    Log.e("CEK","request : "+call.request().toString());
                    String linkResi = call.request().url().toString();
                    Log.e("CEK","request 2 : "+linkResi);
                    String URLServ = Server.BASE_URL_API;
                    Log.e("CEK","URLServ : "+URLServ);
                    String finalLinkResi = linkResi.replace(URLServ,"");
                    Log.e("CEK","finalLinkResi : "+finalLinkResi);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.body() != null) {
                                Log.e("CEK", "response : " + response.body().toString());
                            }
                            if (response.isSuccessful()) {
                                btnUnduhResi.setEnabled(true);
                                Mirroring(false,1,1, finalLinkResi);
                                String dataS = response.body().toString();
                                try {
                                    JSONObject obj = new JSONObject(dataS);
                                    String base64Image = obj.getString("image");
                                    bytePhoto = Base64.decode(base64Image, Base64.NO_WRAP);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                    imgResume.setImageBitmap(bitmap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}