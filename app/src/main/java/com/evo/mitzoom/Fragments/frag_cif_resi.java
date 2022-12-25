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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Helper.SingleMediaScanner;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.chrisbanes.photoview.PhotoView;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_cif_resi extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private PhotoView imgResume;
    private Button btnOK;
    private String idDips;
    private Button btnUnduh;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvMsgThanks;
    private byte[] bytePhoto = null;
    private String dataCIF;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private SwipeRefreshLayout swipe;
    private JSONObject objValCIF;
    private String no_handphone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        dataCIF = sessions.getCIF();
        Log.e("CEK","dataCIF : "+dataCIF);
        try {
            objValCIF = new JSONObject(dataCIF);
            Log.e("CEK","CIF FULL objValCIF : "+objValCIF.toString());
            JSONObject objEl = objValCIF.getJSONObject("datadiri");
            no_handphone = objEl.getString("noponsel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_new_resi, container, false);

        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) v.findViewById(R.id.tvSubTitle);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        imgResume = (PhotoView) v.findViewById(R.id.imgResume);
        tvMsgThanks = (TextView) v.findViewById(R.id.tvMsgThanks);
        btnOK = v.findViewById(R.id.btnSelesai);
        btnUnduh = (Button) v.findViewById(R.id.btnUnduh);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String titleSuccess = getString(R.string.selamat_npembukaan_akun_berhasil);
        titleSuccess = titleSuccess.replace("Akun","Rekening");

        String titleHeadline = getString(R.string.headline_cardless);
        titleHeadline = titleHeadline.replace("Bank XYZ",getString(R.string.bank_name));
        titleHeadline = titleHeadline.replace("Gunakan Aplikasi XYZ Mobile Banking untuk pengalaman transaksi penuh keuntungan.","");

        tvTitle.setText(titleSuccess);
        tvSubTitle.setVisibility(View.GONE);
        tvMsgThanks.setText(titleHeadline);

        idDips = sessions.getKEY_IdDips();

        btnUnduh.setEnabled(false);
        btnUnduh.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResumeResi();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CEK","MASUK BUTTON OK");
                rabbitMirroring.MirroringSendEndpoint(14);
                sessions.clearCIF();
                getFragmentPage(new frag_portfolio_new());
            }
        });

        btnUnduh.setOnClickListener(new View.OnClickListener() {
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

        getResumeResi();

    }

    private void processDownload() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        try {
            JSONObject obj = new JSONObject(dataCIF);
            String noidentitas = obj.getJSONObject("datadiri").getString("noidentitas");
            String no_Form = noidentitas;

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

    private void getResumeResi() {
        Server.getAPIService().getResiCIF().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","getResumeResi CODE : "+response.code());
                if (response.isSuccessful()) {
                    btnUnduh.setEnabled(true);
                    btnUnduh.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String base64Image = dataObj.getJSONObject("data").getString("image");
                        bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                        imgResume.setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
