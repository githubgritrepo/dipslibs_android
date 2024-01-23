package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterDeposit;
import com.evo.mitzoom.Adapter.AdapterQR;
import com.evo.mitzoom.Adapter.OnClickUploadImageListener;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.DownloadTaskHelper;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_deposito_online_aro extends Fragment {

    private static final String TAG = "frag_deposito_online_aro";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private RadioGroup rgChooseType;
    private RecyclerView rv_item;
    private String noCif;
    private JSONArray dataArr = new JSONArray();
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private String labelserv = "";
    private String idService = "";
    private boolean resi = false;
    private JSONObject dataObjDepo = null;
    private SwipeRefreshLayout swipe;
    private ImageView imgResume;
    private TextView tvSyarat;
    private Button btnUnduh;
    private String dataTnC = "";
    private boolean flagViewTNC = false;
    private RadioButton valIDR;
    private RadioButton valAsing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        if (isSessionZoom) {
            ConnectionRabbitHttp.init(mContext);
        }
        if (getArguments() != null) {
            if (getArguments().containsKey("idService")) {
                idService = getArguments().getString("idService");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            if (getArguments().containsKey("resi")) {
                resi = getArguments().getBoolean("resi");
            }
            if (getArguments().containsKey("data")) {
                String dataS = getArguments().getString("data");
                try {
                    dataObjDepo = new JSONObject(dataS);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        noCif = sessions.getNoCIF();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_deposito_online_aro, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        rgChooseType = (RadioGroup) views.findViewById(R.id.rgChooseType);
        valIDR = (RadioButton) views.findViewById(R.id.valIDR);
        valAsing = (RadioButton) views.findViewById(R.id.valAsing);

        rv_item = (RecyclerView) views.findViewById(R.id.rv_item);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);
        imgResume = (ImageView) views.findViewById(R.id.imgResume);
        tvSyarat = (TextView) views.findViewById(R.id.tvSyarat);
        btnUnduh = (Button) views.findViewById(R.id.btnUnduh);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        if (idService.equals("194")) {
            rgChooseType.setVisibility(View.VISIBLE);
            rgChooseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    if (checkedId == R.id.valIDR) {
                        valIDR.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
                        valAsing.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_text)));
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
                        processActiveDeposit();
                    } else if (checkedId == R.id.valAsing) {
                        valIDR.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_text)));
                        valAsing.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
                        dataArr = new JSONArray();
                        setRecyler();
                    }
                }
            });
        }

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }

        if (resi) {
            String noDepo = "";
            if (dataObjDepo.has("depositoNo")) {
                try {
                    noDepo = dataObjDepo.getString("depositoNo");
                    JSONObject mirrObj = new JSONObject();
                    JSONObject dataMirr = new JSONObject();
                    dataMirr.put("nodepo",noDepo);
                    mirrObj.put("ebilyet",dataMirr);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            swipe.setVisibility(View.VISIBLE);
            swipe.setRefreshing(false);
            ConnectionRabbitHttp.mirroringEndpoint(276);
            processGetResiEBilyet();

            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipe.setRefreshing(false);
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    ConnectionRabbitHttp.mirroringEndpoint(276);
                    processGetResiEBilyet();
                }
            });

        } else {
            processActiveDeposit();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resi) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    ConnectionRabbitHttp.mirroringEndpoint(275);
                } else {
                    getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    ConnectionRabbitHttp.mirroringEndpoint(197);
                }
            }
        });

    }

    private void processGetResiEBilyet() {
        try {
            String noDeposit = "";
            if (dataObjDepo.has("depositoNo")) {
                noDeposit = dataObjDepo.getString("depositoNo");
            }

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().getResiEBilyet(noDeposit,sessions.getKEY_IdDips(),authAccess,exchangeToken,sessions.getLANG()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    if (response.isSuccessful()) {
                        processGetTNC();
                        assert response.body() != null;
                        try {
                            JSONObject dataObj = new JSONObject(response.body().toString());
                            String base64Image = dataObj.getJSONObject("data").getString("image");
                            String pdfFile = dataObj.getJSONObject("data").getString("pdf");
                            String filenames = pdfFile.substring(pdfFile.lastIndexOf("/"));
                            byte[] bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);

                            btnUnduh.setEnabled(true);
                            btnUnduh.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));

                            imgResume.setImageBitmap(bitmap);
                            tvSyarat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!flagViewTNC) {
                                        PopUpTnc();
                                    }
                                }
                            });
                            btnUnduh.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ProgressDialog mProgressDialog = new ProgressDialog(mContext);
                                    mProgressDialog.setMessage(mContext.getString(R.string.label_downloaded));
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
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void PopUpTnc() {
        flagViewTNC = true;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialogTNC = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        TextView tvBody = dialogView.findViewById(R.id.tvBody);
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        checkBox.setVisibility(View.GONE);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
        btn.setClickable(true);
        btn.setText(getString(R.string.tutup));

        sweetAlertDialogTNC.setCustomView(dialogView);
        sweetAlertDialogTNC.hideConfirmButton();
        sweetAlertDialogTNC.setCancelable(true);

        if (!dataTnC.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvBody.setText(Html.fromHtml(dataTnC, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        int idx = source.indexOf(",");
                        idx += 1;
                        String new_source = source.substring(idx);
                        byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                        int intH = d.getIntrinsicHeight();
                        int intW = d.getIntrinsicWidth();
                        d.setBounds(0, 0, intW, intH);
                        return d;
                    }
                }, null));
            }
        }

        sweetAlertDialogTNC.show();

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);

        //sweetAlertDialogTNC.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        sweetAlertDialogTNC.getWindow().setLayout(newWidth,newHeight);
        sweetAlertDialogTNC.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                flagViewTNC = false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialogTNC.cancel();
                sweetAlertDialogTNC.dismissWithAnimation();
            }
        });
    }

    private void processGetTNC() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getTNC(25,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        String cekdataTnC = dataObj.getJSONObject("data").getString("data");
                        if (cekdataTnC.contains("{")) {
                            JSONObject labelTNC = new JSONObject(cekdataTnC);
                            String language = sessions.getLANG();
                            if (language.equals("id")) {
                                dataTnC = labelTNC.getString("labelIdn");
                            } else {
                                dataTnC = labelTNC.getString("labelEng");
                            }
                        } else {
                            dataTnC = cekdataTnC;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processActiveDeposit() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().ActiveDeposit(noCif,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    try {
                        JSONObject dataBody = new JSONObject(response.body().toString());
                        dataArr = dataBody.getJSONArray("data");
                        setRecyler();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String msg = "";
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
                    Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setRecyler(){
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_item.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new AdapterDeposit(mContext,frag_deposito_online_aro.this,dataArr,idService);
        rv_item.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

}