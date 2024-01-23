package com.evo.mitzoom.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
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

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterDownloadResi;
import com.evo.mitzoom.Adapter.AdapterFailResi;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.DownloadTaskHelper;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.common.io.Files;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_antarbank_resi extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private SwipeRefreshLayout swipe;
    private ImageView imgResume;
    private RecyclerView rv_item;
    private LinearLayout llFailed;
    private RecyclerView rv_item_fail;
    private TextView tvMsgThanks;
    private Button btnOK;
    private Button btnAllUnduh;
    private Button btnUnduh;

    private boolean isSessionZoom;
    private int formCode = 0;
    private String idDips;
    private JSONArray dataTrxArr;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapterFail;
    private RecyclerView.LayoutManager recylerViewLayoutManagerFail;
    private JSONArray dataResiArr = new JSONArray();
    private JSONArray dataDownloadResi;
    private JSONObject dataValueForm = null;
    private String idFormDepo = "";
    private String typeTransaction = "";
    private String pdfFile = "";
    private String filenames = "";
    private byte[] bytePhoto = null;
    private JSONArray dataSuccessResi = null;
    private JSONArray dataFailResi = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        idDips = sessions.getKEY_IdDips();

        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        dataTrxArr = new JSONArray();
        if (getArguments() != null) {
            if (getArguments().containsKey("formCode")) {
                formCode = getArguments().getInt("formCode");
            }
            if (getArguments().containsKey("dataTrxArr")) {
                String getdataTrxArr = getArguments().getString("dataTrxArr");
                try {
                    dataTrxArr = new JSONArray(getdataTrxArr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("dataValueForm")) {
                String getData = getArguments().getString("dataValueForm");
                try {
                    dataValueForm = new JSONObject(getData);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("idForm")) {
                idFormDepo = getArguments().getString("idForm");
            }
            if (getArguments().containsKey("typeTransaction")) {
                typeTransaction = getArguments().getString("typeTransaction");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_service_antarbank_resi, container, false);

        tvTitle = (TextView) views.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) views.findViewById(R.id.tvSubTitle);
        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);
        imgResume = (ImageView) views.findViewById(R.id.imgResume);
        rv_item = (RecyclerView) views.findViewById(R.id.rv_item);

        llFailed = (LinearLayout) views.findViewById(R.id.llFailed);
        rv_item_fail = (RecyclerView) views.findViewById(R.id.rv_item_notyet);
        tvMsgThanks = (TextView) views.findViewById(R.id.tvMsgThanks);
        btnOK = (Button) views.findViewById(R.id.btnSelesai);
        btnUnduh = (Button) views.findViewById(R.id.btnUnduh);
        btnAllUnduh = (Button) views.findViewById(R.id.btnAllUnduh);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataResiArr = new JSONArray();
        dataDownloadResi = new JSONArray();
        swipe.setRefreshing(true);

        if (dataTrxArr.length() > 1) {
            imgResume.setVisibility(View.GONE);
            btnUnduh.setVisibility(View.GONE);
            rv_item.setVisibility(View.VISIBLE);
            btnAllUnduh.setVisibility(View.VISIBLE);
        }

        parseTrxResi();
        int loopResi = 0;
        getResumeResi(loopResi);

        getFailResi();

        String titleHeadline = getString(R.string.redaksi_form);

        titleHeadline = titleHeadline.replace("Bank XYZ",getString(R.string.bank_name)).replace("XYZ Bank",getString(R.string.bank_name));
        tvMsgThanks.setText(titleHeadline);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataResiArr = new JSONArray();
                dataDownloadResi = new JSONArray();
                int loopResi = 0;
                getResumeResi(loopResi);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RabbitMirroring.MirroringSendEndpoint(14);
                ConnectionRabbitHttp.mirroringEndpoint(14);
                sessions.clearCIF();
                getFragmentPage(new frag_portfolio_new());
            }
        });

        btnAllUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataResiArr.length() > 1) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    getResumeResilZip();
                    /*for (int i = 0; i < dataDownloadResi.length(); i++) {
                        try {
                            JSONObject dataObj = dataDownloadResi.getJSONObject(i);
                            String pdfFile = dataObj.getString("pdfFile");
                            String filename = dataObj.getString("filename");

                            ProgressDialog mProgressDialog = new ProgressDialog(mContext);
                            mProgressDialog.setMessage(mContext.getString(R.string.label_downloaded));
                            mProgressDialog.setIndeterminate(true);
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.setCancelable(true);

                            DownloadTaskHelper downloadTaskHelper = new DownloadTaskHelper(mContext, mProgressDialog);
                            downloadTaskHelper.execute(pdfFile,filename);

                            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    downloadTaskHelper.cancel(true); //cancel the task
                                }
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }*/
                }
            }
        });

        btnUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bytePhoto == null) {
                    Toast.makeText(mContext,"Tidak dapat mengunduh Formulir",Toast.LENGTH_SHORT).show();
                    return;
                }

                processDownloadbyUrl();
            }
        });

    }

    private void parseTrxResi() {
        dataFailResi = new JSONArray();
        dataSuccessResi = new JSONArray();
        if (dataValueForm == null) {
            for (int i = 0; i < dataTrxArr.length(); i++) {
                try {
                    JSONObject dataTrx = dataTrxArr.getJSONObject(i);
                    JSONObject dataParse = dataTrx.getJSONObject("data");
                    if (dataParse.has("statusApprove")) {
                        boolean statusApprove = dataParse.getBoolean("statusApprove");
                        if (!statusApprove) {
                            dataFailResi.put(dataParse);
                        } else {
                            dataSuccessResi.put(dataParse);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            //try {
                /*JSONObject dataTrx = dataValueForm.getJSONObject(0);
                JSONObject dataParse = dataTrx.getJSONObject("data");*/
                dataSuccessResi.put(dataValueForm);
            /*} catch (JSONException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    private void getFailResi() {
        if (dataFailResi.length() > 0) {
            llFailed.setVisibility(View.VISIBLE);
            setRecyclerFail();
        }
    }

    private void setRecyclerFail() {
        recylerViewLayoutManagerFail = new LinearLayoutManager(getContext());
        rv_item_fail.setLayoutManager(recylerViewLayoutManagerFail);
        recyclerViewAdapterFail = new AdapterFailResi(mContext, dataFailResi);
        rv_item_fail.setAdapter(recyclerViewAdapterFail);
        recyclerViewAdapterFail.notifyDataSetChanged();
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

    private void getResumeResilZip() {
        try {
            JSONObject objReq = new JSONObject();

            JSONArray idFormMulti = new JSONArray();

            for (int i = 0; i < dataTrxArr.length(); i++) {
                JSONObject dataTrx = dataTrxArr.getJSONObject(i);
                JSONObject dataParse = dataTrx.getJSONObject("data");
                String idForm = dataParse.getString("idForm");
                idFormMulti.put(idForm);
            }

            objReq.put("idForm",idFormMulti);
            objReq.put("bahasa",sessions.getLANG());

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer " + sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();

            Server.getAPIService().getResiMultiZip(requestBody,authAccess,exchangeToken).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    if (response.isSuccessful()) {
                        InputStream ins = response.body().byteStream();

                        File dir = createDir();
                        if (!dir.exists()) {
                            if (!dir.mkdirs()) {
                            }
                        }
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());

                        String filename = "Transaction_"+timeStamp+".zip";
                        //String filename2 = "Transaction_ke-2_"+timeStamp+".zip";

                        File mediaFile = new File(dir.getPath() + File.separator +
                                filename);
                        /*File mediaFile2 = new File(dir.getPath() + File.separator +
                                filename2);*/

                        try {
                            Files.asByteSink(mediaFile).write(response.body().bytes());

                            /*FileOutputStream output = new FileOutputStream(mediaFile);

                            byte[] data = new byte[4096];
                            int count;
                            while ((count = ins.read(data)) != -1) {
                                output.write(data, 0, count);
                            }
                            output.close();*/
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Toast.makeText(mContext, R.string.file_downloaded, Toast.LENGTH_LONG).show();
                    } else {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private File createDir() {
        String appName = mContext.getString(R.string.app_name_dips);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), appName);

        return mediaStorageDir;
    }

    private void getResumeResi(int loopResi) {
        String noForm = "";
        String typeService = "";
        String idForm = "";
        try {
            if (dataValueForm != null) {
                typeService = "deposit";
                idForm = idFormDepo;

                ApiGetResi(loopResi,typeService,idForm);

            } else {
                typeService = "transaction";
                JSONObject dataParse = dataSuccessResi.getJSONObject(loopResi);

                if (dataParse.has("jenislayanan")) {
                    typeTransaction = dataParse.getString("jenislayanan");
                    typeTransaction = typeTransaction.toLowerCase();
                }
                idForm = dataParse.getString("idForm");

                if (typeTransaction.isEmpty()) {
                    typeTransaction = "interbank";
                }

                ApiGetResi(loopResi,typeService,idForm);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void ApiGetResi(int loopResi, String typeService, String idForm) {
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().getResiTransaction(typeService,typeTransaction,idForm,authAccess,exchangeToken,sessions.getLANG()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
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

                        dataObj.put("trxTo",loopResi+1);

                        dataResiArr.put(dataObj);

                        if (dataValueForm == null) {

                            if (loopResi < dataSuccessResi.length() - 1) {
                                int addLoopResi = loopResi + 1;
                                getResumeResi(addLoopResi);
                            }

                            if (loopResi == dataSuccessResi.length()-1) {
                                swipe.setRefreshing(false);
                                if (isSessionZoom) {
                                    BaseMeetingActivity.showProgress(false);
                                } else {
                                    DipsSwafoto.showProgress(false);
                                }

                                if (dataResiArr.length() > 1) {
                                    btnAllUnduh.setEnabled(true);
                                    btnAllUnduh.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.zm_button));
                                    imgResume.setVisibility(View.GONE);
                                    btnUnduh.setVisibility(View.GONE);
                                    rv_item.setVisibility(View.VISIBLE);
                                    btnAllUnduh.setVisibility(View.VISIBLE);

                                    recylerViewLayoutManager = new LinearLayoutManager(getContext());
                                    rv_item.setLayoutManager(recylerViewLayoutManager);
                                    recyclerViewAdapter = new AdapterDownloadResi(mContext, dataResiArr, dataDownloadResi);
                                    rv_item.setAdapter(recyclerViewAdapter);
                                    recyclerViewAdapter.notifyDataSetChanged();

                                } else {
                                    String base64Image = dataResiArr.getJSONObject(0).getJSONObject("data").getString("image");
                                    pdfFile = dataResiArr.getJSONObject(0).getJSONObject("data").getString("pdf");
                                    filenames = pdfFile.substring(pdfFile.lastIndexOf("/"));
                                    bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                    imgResume.setImageBitmap(bitmap);
                                }
                            }

                        } else {
                            swipe.setRefreshing(false);
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }

                            String base64Image = dataResiArr.getJSONObject(0).getJSONObject("data").getString("image");
                            pdfFile = dataResiArr.getJSONObject(0).getJSONObject("data").getString("pdf");
                            filenames = pdfFile.substring(pdfFile.lastIndexOf("/"));
                            bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                            /*LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1200);
                            lpImg.setMargins(0,-140,0,-120);
                            imgResume.setLayoutParams(lpImg);
                            imgResume.setScaleX(0.7f);
                            imgResume.setScaleY(0.7f);
                            imgResume.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
                            imgResume.setImageBitmap(bitmap);

                            btnUnduh.setText(mContext.getResources().getString(R.string.unduh_resi));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    swipe.setRefreshing(false);
                    String msg = "";
                    if (response.body() != null) {
                        String dataS = response.body().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (response.errorBody().toString().isEmpty()) {
                            String dataS = response.errorBody().toString();
                            try {
                                JSONObject dataObj = new JSONObject(dataS);
                                if (dataObj.has("message")) {
                                    msg = dataObj.getString("message");
                                }
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
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        if (isSessionZoom) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame2, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}