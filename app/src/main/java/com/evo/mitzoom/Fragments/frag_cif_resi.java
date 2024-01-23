package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
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
    private ImageView imgResume;
    private Button btnOK;
    private String idDips;
    private Button btnUnduh;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvPlsActiveAccount;
    private TextView tvMsgThanks;
    private byte[] bytePhoto = null;
    private String dataCIF;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private SwipeRefreshLayout swipe;
    private JSONObject objValCIF;
    private String no_handphone;
    private String pdfFile = "";
    private DownloadManager manager;
    private int formCode = 0;
    private String idForm = "";
    private int loopStatus = 0;
    private String filenames = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        dataCIF = sessions.getCIF();
        if (dataCIF != null) {
            try {
                objValCIF = new JSONObject(dataCIF);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        ConnectionRabbitHttp.init(mContext);
        if (getArguments() != null) {
            formCode = getArguments().getInt("formCode");
            if (getArguments().containsKey("idForm")) {
                idForm = getArguments().getString("idForm");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_new_resi, container, false);

        tvTitle = v.findViewById(R.id.tvTitle);
        tvSubTitle = v.findViewById(R.id.tvSubTitle);
        swipe = v.findViewById(R.id.swipe);
        imgResume = v.findViewById(R.id.imgResume);
        tvPlsActiveAccount = (TextView) v.findViewById(R.id.tvPlsActiveAccount);
        tvMsgThanks = v.findViewById(R.id.tvMsgThanks);
        btnOK = v.findViewById(R.id.btnSelesai);
        btnUnduh = v.findViewById(R.id.btnUnduh);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = sessions.getKEY_IdDips();

        swipe.setRefreshing(true);
        if (formCode == 13 || formCode == 0) {
            getResumeResi();
        } else if (formCode == 131) {
            getResumeResiCIFReady();
        }

        String titleSuccess = getString(R.string.selamat_npembukaan_akun_berhasil);
        titleSuccess = titleSuccess.replace("Akun","Rekening");

        String titleHeadline = getString(R.string.headline_cardless);
        titleHeadline = titleHeadline.replace("XYZ",getString(R.string.bank_name2)).replace("XYZ",getString(R.string.bank_name2));
        titleHeadline = titleHeadline.replace("Gunakan Aplikasi XYZ Mobile Banking untuk pengalaman transaksi penuh keuntungan.","");

        tvTitle.setText(titleSuccess);
        tvSubTitle.setVisibility(View.GONE);
        tvMsgThanks.setText(titleHeadline);

        btnUnduh.setEnabled(false);
        btnUnduh.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (formCode == 13 || formCode == 0) {
                    getResumeResi();
                } else if (formCode == 131) {
                    getResumeResiCIFReady();
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RabbitMirroring.MirroringSendEndpoint(14);
                ConnectionRabbitHttp.mirroringEndpoint(14);
                sessions.clearCIF();
                sessions.clearPartData();
                bytePhoto = null;
                getFragmentPage(new frag_portfolio_new());
            }
        });

        btnUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bytePhoto == null) {
                    Toast.makeText(mContext,"Tidak dapat mengunduh Formulir",Toast.LENGTH_SHORT).show();
                    return;
                }

                //processDownload();
                processDownloadbyUrl();
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
        downloadTaskHelper.execute(pdfFile,filenames);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTaskHelper.cancel(true); //cancel the task
            }
        });
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

    private void getResumeResiCIFReady() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getResiCIFReady(idForm,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    swipe.setRefreshing(false);
                    btnUnduh.setEnabled(true);
                    btnUnduh.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    assert response.body() != null;
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String base64Image = dataObj.getJSONObject("data").getString("image");
                        pdfFile = dataObj.getJSONObject("data").getString("pdf");
                        filenames = pdfFile.substring(pdfFile.lastIndexOf("/") );
                        bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                        RelativeLayout.LayoutParams lpImg = new RelativeLayout.LayoutParams(250, 300);
                        lpImg.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        lpImg.setMargins(10,120,10,10);
                        imgResume.setLayoutParams(lpImg);
                        imgResume.setScaleX(2.5f);
                        imgResume.setScaleY(3f);
                        imgResume.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgResume.setImageBitmap(bitmap);
                        imgResume.setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (loopStatus < 3) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getResumeResiCIFReady();
                                    }
                                });
                                loopStatus++;
                            }
                        },10000);
                    } else {
                        swipe.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getResumeResi() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getResiCIF(idDips,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    swipe.setRefreshing(false);
                    btnUnduh.setEnabled(true);
                    btnUnduh.setBackgroundTintList(ContextCompat.getColorStateList(mContext,R.color.zm_button));
                    assert response.body() != null;
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String base64Image = dataObj.getJSONObject("data").getString("image");
                        pdfFile = dataObj.getJSONObject("data").getString("pdf");
                        filenames = pdfFile.substring(pdfFile.lastIndexOf("/") );
                        bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                        imgResume.setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (loopStatus < 3) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getResumeResi();
                                    }
                                });
                                loopStatus++;
                            }
                        },10000);
                    } else {
                        swipe.setRefreshing(false);
                    }
                    //Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
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
