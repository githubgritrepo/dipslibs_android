package com.evo.mitzoom.Fragments;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.DownloadTaskHelper;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Helper.SingleMediaScanner;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_resi extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private PhotoView imgResume;
    private Button btnOK;
    private String idDips;
    private Button btnUnduh;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvMsgThanks;
    private TextView tvMsgNum;
    private TextView tvMsgThanks2;
    private byte[] bytePhoto = null;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private String no_Form = "";
    private String noPengaduan = "";
    private SwipeRefreshLayout swipe;
    private String pdfFile = "";
    private DownloadManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

        no_Form = sessions.getNoComplaint();
        noPengaduan = sessions.getNoComplaint();

        Log.e("CEK",this+" noPengaduan : "+noPengaduan);

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
        tvMsgNum = (TextView) v.findViewById(R.id.tvMsgNum);
        tvMsgThanks2 = (TextView) v.findViewById(R.id.tvMsgThanks2);
        btnOK = v.findViewById(R.id.btnSelesai);
        btnUnduh = (Button) v.findViewById(R.id.btnUnduh);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipe.setRefreshing(true);
        getResumeResi();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResumeResi();
            }
        });

        String titleSuccess = getString(R.string.title_safe_complaint);
        String titleHeadline = getString(R.string.body_complaint);
        String titleHeadline2 = getString(R.string.body2_complaint);

        titleHeadline2 = titleHeadline2.replace("Bank XYZ",getString(R.string.bank_name)).replace("XYZ Bank",getString(R.string.bank_name));

        tvTitle.setText(titleSuccess);
        tvSubTitle.setVisibility(View.GONE);
        tvMsgNum.setVisibility(View.VISIBLE);
        tvMsgThanks2.setVisibility(View.VISIBLE);

        tvMsgThanks.setText(titleHeadline);
        tvMsgNum.setText(no_Form);
        tvMsgThanks2.setText(titleHeadline2);

        idDips = sessions.getKEY_IdDips();

        btnUnduh.setEnabled(false);
        btnUnduh.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));

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

                if (pdfFile.isEmpty()) {
                    Toast.makeText(mContext,"File Not Found",Toast.LENGTH_SHORT).show();
                    return;
                }
                processDownloadbyUrl();
            }
        });

    }

    private void getResumeResi() {
        Log.e("CEK","getResumeResi");
        Server.getAPIService().getResiComplaint(noPengaduan).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","getResumeResi CODE : "+response.code());
                if (response.isSuccessful()) {
                    btnUnduh.setEnabled(true);
                    btnUnduh.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    assert response.body() != null;
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String base64Image = dataObj.getJSONObject("data").getString("image");
                        pdfFile = dataObj.getJSONObject("data").getString("pdf");
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

    private void processDownloadbyUrl() {
        ProgressDialog mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(getString(R.string.label_downloaded));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        DownloadTaskHelper downloadTaskHelper = new DownloadTaskHelper(mContext, mProgressDialog);
        downloadTaskHelper.execute(pdfFile);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTaskHelper.cancel(true); //cancel the task
            }
        });
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

}
