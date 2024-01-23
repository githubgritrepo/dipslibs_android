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
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.DownloadTaskHelper;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;


public class frag_wm_trans_resi extends Fragment {

    private static final String TAG = "frag_wm_trans_resi";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private LinearLayout llTopHistory;
    private TextView tvTypeWMDate;
    private TextView tvTitleProd;
    private TextView tvPtProd;
    private LinearLayout tvllThanks;
    private ImageView imgResume;
    private SwipeRefreshLayout swipe;
    private TextView tvMsgThanks;
    private String typeService;
    private String typeTransaction = "";
    private String idForm = "";
    private Button btnOK;
    private Button btnUnduh;
    private String pdfFile = "";
    private String filenames = "";
    private byte[] bytePhoto = null;
    private JSONObject dataWMObj = null;
    private LinearLayout TopBar;
    private ImageView btnBack;
    private TextView tvtitleHead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        ConnectionRabbitHttp.init(mContext);

        if (getArguments() != null) {
            if (getArguments().containsKey("idForm")) {
                idForm = getArguments().getString("idForm");
            }

            if (getArguments().containsKey("typeTransaction")) {
                typeTransaction = getArguments().getString("typeTransaction");
            }

            if (getArguments().containsKey("dataObj")) {
                String dataObj = getArguments().getString("dataObj");
                try {
                    dataWMObj = new JSONObject(dataObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_trans_resi, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        tvTitle = (TextView) views.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) views.findViewById(R.id.tvSubTitle);

        llTopHistory = (LinearLayout) views.findViewById(R.id.llTopHistory);
        tvTypeWMDate = (TextView) views.findViewById(R.id.tvTypeWMDate);
        tvTitleProd = (TextView) views.findViewById(R.id.tvTitleProd);
        tvPtProd = (TextView) views.findViewById(R.id.tvPtProd);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);
        imgResume = (ImageView) views.findViewById(R.id.imgResume);
        tvllThanks = (LinearLayout) views.findViewById(R.id.tvllThanks);
        tvMsgThanks = (TextView) views.findViewById(R.id.tvMsgThanks);
        btnOK = (Button) views.findViewById(R.id.btnSelesai);
        btnUnduh = (Button) views.findViewById(R.id.btnUnduh);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeService = "wm";

        if (dataWMObj != null) {
            btnOK.setText(mContext.getResources().getString(R.string.back));
            TopBar.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            tvSubTitle.setVisibility(View.GONE);
            llTopHistory.setVisibility(View.VISIBLE);
            tvllThanks.setVisibility(View.GONE);

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectionRabbitHttp.mirroringEndpoint(310);
                    FragmentManager fm = getFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    }
                }
            });

            try {
                String typeWM = "";
                if (dataWMObj.has("idCategory")) {
                    String idCategory = dataWMObj.getString("idCategory");
                    String rwt = mContext.getResources().getString(R.string.riwayat_transaksi);
                    if (idCategory.equals("SUB")) {
                        String lbType = mContext.getResources().getString(R.string.purchase);
                        String titleRwt = rwt + " " + lbType;

                        tvtitleHead.setText(titleRwt);
                        typeWM = lbType.toUpperCase();
                    } else if (idCategory.equals("RED")) {
                        String lbType = mContext.getResources().getString(R.string.penjualan);
                        String titleRwt = rwt + " " + lbType;

                        tvtitleHead.setText(titleRwt);
                        typeWM = lbType.toUpperCase();
                    }
                }

                if (dataWMObj.has("transactionDate")) {
                    String transactionDate = dataWMObj.getString("transactionDate");
                    if (transactionDate.contains("T")) {
                        String[] spDates = transactionDate.split("T");
                        String dates = spDates[0].trim();
                        String times = spDates[1].trim();

                        String inputFormat = "yyyy-MM-dd";
                        String outputFormat = "dd/MM/yy";
                        String newDates = ConveredDate(dates, inputFormat, outputFormat);
                        String newTimes = times.substring(0, 8);

                        String timesDates = newTimes + " " + newDates;

                        typeWM += " - "+timesDates;
                    }
                }

                if (dataWMObj.has("productName")) {
                    String productName = dataWMObj.getString("productName");
                    tvTitleProd.setText(productName);
                }

                tvTypeWMDate.setText(typeWM);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            TopBar.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            tvSubTitle.setVisibility(View.VISIBLE);
            llTopHistory.setVisibility(View.GONE);
            tvllThanks.setVisibility(View.VISIBLE);
        }

        swipe.setRefreshing(true);
        getResumeResi();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResumeResi();
            }
        });

        String titleHeadline = tvMsgThanks.getText().toString();
        titleHeadline = titleHeadline.replace("Bank XYZ",getString(R.string.bank_name)).replace("XYZ Bank",getString(R.string.bank_name));
        tvMsgThanks.setText(titleHeadline);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataWMObj != null) {
                    ConnectionRabbitHttp.mirroringEndpoint(310);
                    FragmentManager fm = getFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    }
                } else {
                    ConnectionRabbitHttp.mirroringEndpoint(14);
                    sessions.clearCIF();
                    getFragmentPage(new frag_portfolio_new());
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

                if (pdfFile.isEmpty()) {
                    Toast.makeText(mContext,"File Not Found",Toast.LENGTH_SHORT).show();
                    return;
                }
                processDownloadbyUrl();
            }
        });

    }

    private String ConveredDate(String dataDate, String InputFormat, String OutputFormat) {
        DateFormat inputFormat = new SimpleDateFormat(InputFormat);
        SimpleDateFormat d= new SimpleDateFormat(OutputFormat);
        try {
            Date convertedDate = inputFormat.parse(dataDate);
            String datetime = d.format(convertedDate);
            return datetime;
        }catch (ParseException e) {

        }
        return null;
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

    private void getResumeResi() {
        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().getResiTransaction(typeService,typeTransaction,idForm,authAccess,exchangeToken,sessions.getLANG()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                if (response.isSuccessful()) {
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
                        /*RelativeLayout.LayoutParams lpImg = new RelativeLayout.LayoutParams(250, 300);
                        lpImg.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        lpImg.setMargins(10,120,10,10);
                        imgResume.setLayoutParams(lpImg);
                        imgResume.setScaleX(2.5f);
                        imgResume.setScaleY(3f);*/
                        //imgResume.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgResume.setImageBitmap(bitmap);
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