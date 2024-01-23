package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_confirm_antarbank extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private LinearLayout TopBar;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private NestedScrollView nestedScrollContent;
    private RecyclerView rv_itemPage;
    private TextView tvTrxQR;
    private LinearLayout llDeleteTrx;
    private TextView tvNoFormulir;
    private RecyclerView rv_itemView;
    private Button btnReCheck;
    private Button btnContinue;
    private boolean isSessionZoom;
    private JSONArray idElementMulti;
    private JSONArray dataTrxArr;
    private RecyclerView.Adapter recyclerViewAdapterPager;
    private RecyclerView.LayoutManager recylerViewLayoutManagerPager;
    int selected_position = 0;
    private LinearLayoutManager recylerViewLayoutManager;
    private AdapterItemView recyclerViewAdapter;
    private NestedScrollView scrollOTP;
    private View inclOTP;
    private ImageView imgDialog;
    private TextView textTitleOTP;
    private Button btnVerifikasi;
    private TextView TimerOTP;
    private TextView Resend_Otp;
    private PinView otp;
    private BroadcastReceiver smsReceiver = null;
    private LinearLayout ll_head;
    private TextView tvFotoKTP;

    private String numberOTP = "";
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;
    private String transactionId = "";
    private JSONObject dataNasabahObj;
    private String no_handphone = "";
    private String messageError = "";
    private String labelserv = "";
    private String idDips;
    private int formId;
    private final boolean selectedpager = false;
    private JSONObject mirrObj;
    private JSONArray dataTrxArrMirror;
    private int loopStatus = 0;
    private String labelTrx = "";
    private ArrayList<String> nameItemQR = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        idDips = sessions.getKEY_IdDips();
        ConnectionRabbitHttp.init(mContext);

        dataTrxArrMirror = new JSONArray();
        mirrObj = new JSONObject();
        
        if (getArguments() != null) {
            String getidElement = getArguments().getString("idElementMulti");
            String getdataTrxArr = getArguments().getString("dataTrxArr");
            if (getArguments().containsKey("messageError")) {
                messageError = getArguments().getString("messageError");
            }
            if (getArguments().containsKey("nameItemQR")) {
                nameItemQR = getArguments().getStringArrayList("nameItemQR");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            try {
                idElementMulti = new JSONArray(getidElement);
                dataTrxArr = new JSONArray(getdataTrxArr);
                JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                formId = dataTrx.getInt("idGenerateForm");

                if (formId == 48 || formId == 54 || formId == 55) {
                    labelTrx = "antarbank";
                } else if (formId == 49) {
                    labelTrx = "interbank";
                } else if (formId == 56) {
                    labelTrx = "privatetransaction";
                }

                for (int i = 0; i < dataTrxArr.length(); i++) {
                    JSONObject dataSelect = dataTrxArr.getJSONObject(i);
                    JSONObject reqFormMirroring = dataSelect.getJSONObject("data");
                    dataTrxArrMirror.put(i,reqFormMirroring);
                }
                mirrObj.put(labelTrx,dataTrxArrMirror);
                mirrObj.put("activeIndex",selected_position);
                ConnectionRabbitHttp.mirroringKey(mirrObj);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (labelTrx.equals("antarbank")) {
            ConnectionRabbitHttp.mirroringEndpoint(195);
        } else if (labelTrx.equals("privatetransaction")) {
            ConnectionRabbitHttp.mirroringEndpoint(202);
        } else {
            ConnectionRabbitHttp.mirroringEndpoint(194);
        }

        String dataNasabah = sessions.getNasabah();
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                /*if (dataNasabahObj.has("namaLengkap")) {
                    namaLengkap = dataNasabahObj.getString("namaLengkap");
                }
                if (dataNasabahObj.has("alamat")) {
                    alamat = dataNasabahObj.getString("alamat");
                }*/
                if (dataNasabahObj.has("noHp")) {
                    no_handphone = dataNasabahObj.getString("noHp");
                }
                /*if (dataNasabahObj.has("nik")) {
                    nik = dataNasabahObj.getString("nik");
                }
                if (dataNasabahObj.has("branchCode")) {
                    branchCode = dataNasabahObj.getString("branchCode");
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                numberOTP = "";
                String dataSMS = intent.getExtras().getString("smsMessage");
                String[] sp = dataSMS.split(" ");
                for (int i = 0; i < sp.length; i++) {
                    String word = sp[i];
                    if(word.matches("\\d+(?:\\.\\d+)?")) {
                        numberOTP = word.replaceAll("[^0-9]", "");
                        if (numberOTP.length() == 6) {
                            otp.setText(numberOTP);
                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(smsReceiver,new IntentFilter("getotp"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_service_confirm_antarbank, container, false);

        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);

        nestedScrollContent = (NestedScrollView) views.findViewById(R.id.nestedScrollContent);
        rv_itemPage = (RecyclerView) views.findViewById(R.id.rv_itemPage);
        tvTrxQR = (TextView) views.findViewById(R.id.tvTrxQR);
        llDeleteTrx = (LinearLayout) views.findViewById(R.id.llDeleteTrx);
        tvNoFormulir = (TextView) views.findViewById(R.id.tvNoFormulir);

        rv_itemView = (RecyclerView) views.findViewById(R.id.rv_itemView);

        scrollOTP = (NestedScrollView) views.findViewById(R.id.scrollOTP);
        inclOTP = views.findViewById(R.id.inclOTP);
        imgDialog = (ImageView) views.findViewById(R.id.imgDialog);
        textTitleOTP = (TextView) views.findViewById(R.id.textIBMB);
        btnVerifikasi = (Button) views.findViewById(R.id.btnVerifikasi);
        TimerOTP = (TextView) views.findViewById(R.id.timer_otp);
        Resend_Otp = (TextView) views.findViewById(R.id.btn_resend_otp);
        otp = (PinView) views.findViewById(R.id.otp);

        btnReCheck = (Button) views.findViewById(R.id.btnReCheck);
        btnContinue = (Button) views.findViewById(R.id.btnContinue);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (dataTrxArr.length() == 1) {
            llDeleteTrx.setVisibility(View.GONE);
        }

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSessionZoom) {
                    if (labelTrx.equals("antarbank")) {
                        ConnectionRabbitHttp.mirroringEndpoint(191);
                    } else if (labelTrx.equals("privatetransaction")) {
                        ConnectionRabbitHttp.mirroringEndpoint(200);
                    } else {
                        ConnectionRabbitHttp.mirroringEndpoint(190);
                    }

                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("reCheck",true);
                bundle.putString("labelserv",labelserv);
                bundle.putString("dataTrxArr",dataTrxArr.toString());
                bundle.putString("idElementMulti",idElementMulti.toString());
                bundle.putStringArrayList("nameItemQR",nameItemQR);
                sendDataFragment(bundle, new frag_service_antarbank());
            }
        });

        llDeleteTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataTrxArr.length() > 1) {
                    dataTrxArr.remove(selected_position);
                    dataTrxArrMirror.remove(selected_position);
                    idElementMulti.remove(selected_position);
                    recyclerViewAdapterPager.notifyItemRemoved(selected_position);
                    selected_position = dataTrxArr.length() - 1;
                    setRecylerPager();
                    recyclerViewAdapterPager.notifyItemChanged(selected_position);

                    try {
                        mirrObj.put(labelTrx,dataTrxArrMirror);
                        mirrObj.put("activeIndex",selected_position);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    ConnectionRabbitHttp.mirroringKey(mirrObj);

                    if (dataTrxArr.length() == 1) {
                        llDeleteTrx.setVisibility(View.GONE);
                    }
                }
            }
        });

        btnReCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSessionZoom) {
                    ConnectionRabbitHttp.mirroringEndpoint(191);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("reCheck",true);
                bundle.putString("dataTrxArr",dataTrxArr.toString());
                bundle.putString("idElementMulti",idElementMulti.toString());
                bundle.putStringArrayList("nameItemQR",nameItemQR);
                sendDataFragment(bundle, new frag_service_antarbank());
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }

                processSendOTP();
                /*int loopSave = 0;
                APISaveForm(loopSave);*/
            }
        });

        if (!messageError.isEmpty()) {
            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
            btnContinue.setEnabled(false);
            PopUp();
        } else {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
        }

        setRecyler();
        setRecylerPager();
    }

    private void PopUp(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        tvBodyDialog.setText(messageError);
        if (messageError.contains("server")) {
            imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_failed));
        } else {
            imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_info));
            btnConfirmDialog.setText(getResources().getString(R.string.label_ya));
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismiss();
                sweetAlertDialog.cancel();
            }
        });
    }

    private void setRecyler() {
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_itemView.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new AdapterItemView();
        rv_itemView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void setRecylerPager() {
        recylerViewLayoutManagerPager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_itemPage.setLayoutManager(recylerViewLayoutManagerPager);

        recyclerViewAdapterPager = new AdapterNumPager();
        rv_itemPage.setAdapter(recyclerViewAdapterPager);
        recyclerViewAdapterPager.notifyItemInserted(dataTrxArr.length() - 1);
    }

    private class AdapterItemView extends RecyclerView.Adapter<AdapterItemView.ViewHolder> {

        @NonNull
        @Override
        public AdapterItemView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_confirmview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterItemView.ViewHolder holder, int position) {
            try {
                String nameDataEl = idElementMulti.getJSONArray(selected_position).getJSONObject(position).getString("name");
                String labelEl = idElementMulti.getJSONArray(selected_position).getJSONObject(position).getString("label");
                holder.tvLabelElement.setText(labelEl);

                JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                JSONObject dataParse = dataTrx.getJSONObject("data");
                if (dataParse.has(nameDataEl)) {
                    String valEl = dataParse.getString(nameDataEl);
                    if (nameDataEl.toLowerCase().contains("sumberdana")) {
                        if (valEl.contains(" / ")) {
                            valEl = valEl.replace(" / ","\n");
                            String[] sp = valEl.split("\n");
                            valEl = sp[0]+"\n"+sp[1];
                        }
                    }

                    if (nameDataEl.toLowerCase().contains("rekening") && nameDataEl.toLowerCase().contains("penerima") && !messageError.isEmpty()) {
                        if (dataTrx.has("alertRekening")) {
                            String alertRekening = dataTrx.getString("alertRekening");
                            holder.tvContentElement.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_button)));
                            holder.tvContentElement.setText(alertRekening);
                        }
                    } else {
                        holder.tvContentElement.setText(valEl);
                    }

                    if (nameDataEl.toLowerCase().trim().equals("adminfee")) {
                        if (dataParse.has("adminFee")) {
                            String adminFee = dataParse.getString("adminFee");
                            adminFee = adminFee.substring(0,adminFee.length() - 2);
                            Double d = Double.valueOf(adminFee);
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);
                            String formattedNumber = formatter.format(d);
                            holder.tvContentElement.setText(formattedNumber);
                        }

                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            try {
                return idElementMulti.getJSONArray(selected_position).length();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvLabelElement;
            private final TextView tvContentElement;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvLabelElement = (TextView) itemView.findViewById(R.id.tvLabelElement);
                tvContentElement = (TextView) itemView.findViewById(R.id.tvContentElement);
            }
        }
    }

    private class AdapterNumPager extends RecyclerView.Adapter<AdapterNumPager.ViewHolder>{

        @NonNull
        @Override
        public AdapterNumPager.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.num_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterNumPager.ViewHolder holder, int position) {
            int pos = position + 1;
            String sPos = String.valueOf(pos);
            holder.tvNumPage.setText(sPos);
            holder.cvPage.setBackgroundTintList(selected_position == position ? AppCompatResources.getColorStateList(mContext,R.color.zm_button) : AppCompatResources.getColorStateList(mContext,R.color.zm_bg_grey));
            holder.tvNumPage.setTextColor(selected_position == position ? getResources().getColor(R.color.white) : getResources().getColor(R.color.zm_text));

            if (selected_position == position) {
                try {
                    mirrObj.put(labelTrx,dataTrxArrMirror);
                    mirrObj.put("activeIndex",selected_position);
                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                    JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                    String label = dataTrx.getString("label");
                    int idGenerateForm = dataTrx.getInt("idGenerateForm");
                    String noForm = dataTrx.getString("noForm");
                    JSONObject dataParse = dataTrx.getJSONObject("data");
                    int posSelected = selected_position + 1;
                    String sPosSelected = String.valueOf(posSelected);
                    tvNoFormulir.setText(noForm);
                    if (label.equals("non_qr")) {
                        String cLabel = getResources().getString(R.string.transaksi_non_qr) + " " + sPosSelected;
                        tvTrxQR.setText(cLabel);
                    } else {
                        String cLabel = getResources().getString(R.string.transaksi_qr) + " " + sPosSelected;
                        tvTrxQR.setText(cLabel);
                    }
                    setRecyler();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        @Override
        public int getItemCount() {
            return dataTrxArr.length();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView tvNumPage;
            private final CardView cvPage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                cvPage = (CardView) itemView.findViewById(R.id.cvPage);
                tvNumPage = (TextView) itemView.findViewById(R.id.tvNumPage);
            }

            @Override
            public void onClick(View v) {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = getAdapterPosition();
                notifyItemChanged(selected_position);
            }
        }
    }

    private void APISaveForm(int loopSave) {
        JSONObject dataObjSave = new JSONObject();
        try {
            JSONObject dataTrx = dataTrxArr.getJSONObject(loopSave);
            JSONObject dataParse = dataTrx.getJSONObject("data");

            String typeService = "";

            JSONArray getElement = new JSONArray(idElementMulti.getJSONArray(loopSave).toString());

            for (int j = 0; j < getElement.length(); j++) {
                String nameDataEl = getElement.getJSONObject(j).getString("name");
                if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                    typeService = dataParse.getString(nameDataEl);
                }
            }

            if (typeService.isEmpty()) {
                dataObjSave.put("formCode", labelTrx.toUpperCase());
            } else {
                dataObjSave.put("formCode", labelTrx.toUpperCase() + " - " + typeService);
            }
            dataObjSave.put("idDips", idDips);
            dataObjSave.put("payload", dataParse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer " + sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjSave.toString());
        Server.getAPIService().saveForm(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String idForm = dataObj.getJSONObject("data").getString("idForm");
                        JSONObject idFormObj = new JSONObject();
                        idFormObj.put("idForm",idForm);

                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd",
                                Locale.getDefault()).format(new Date());

                        JSONObject dataSelect = dataTrxArr.getJSONObject(loopSave);
                        JSONObject reqFormMirroring = dataSelect.getJSONObject("data");
                        reqFormMirroring.put("idForm",idForm);
                        reqFormMirroring.put("tanggaltransaksi",timeStamp);
                        dataTrxArrMirror.put(loopSave,reqFormMirroring);
                        mirrObj.put(labelTrx,dataTrxArrMirror);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (loopSave < dataTrxArr.length()-1) {
                        int addLoopSave= loopSave + 1;
                        APISaveForm(addLoopSave);
                    }
                    if (loopSave == dataTrxArr.length()-1) {
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                        int loopApprove = 0;
                        loopStatus = 0;
                        processApprovalStatus(loopApprove);
                    }
                } else {
                    running = true;
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                running = true;
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void processSendOTP() {
        String noHp = no_handphone;
        if (noHp.charAt(0) == '0') {
            noHp = "62"+no_handphone.substring(1);
        }
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("msisdn",noHp);
            dataObjOTP.put("idDips",idDips);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());

        Server.getAPIService().SendOTP(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        transactionId = dataObj.getJSONObject("data").getString("transactionId");
                        ConnectionRabbitHttp.mirroringEndpoint(11);
                        JSONObject dataMirr = null;
                        try {
                            dataMirr = new JSONObject();
                            dataMirr.put("noponsel",no_handphone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ConnectionRabbitHttp.mirroringKey(dataMirr);

                        getMinutes = 2;
                        seconds = 60;
                        running = true;
                        pageOTP();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,getString(R.string.msg_error),Toast.LENGTH_SHORT).show();
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

    private void pageOTP() {
        scrollOTP.setVisibility(View.VISIBLE);
        TopBar.setVisibility(View.GONE);
        nestedScrollContent.setVisibility(View.GONE);
        ll_head.setVisibility(View.VISIBLE);

        String noHandphone = "089783434***";
        if (!no_handphone.isEmpty()) {
            String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
            noHandphone = no_handphone.replace(sub_no_handphone,"***");
        }

        String contentText = textTitleOTP.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textTitleOTP.setText(contentText);

        tvFotoKTP.setText("One Time Password");

        otp.setAnimationEnable(true);
        otp.setPasswordHidden(true);
        otp.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp.length() == 6) {
                    numberOTP = otp.getText().toString();
                    JSONObject otpObj = new JSONObject();
                    try {
                        otpObj.put("otp",numberOTP);
                        ConnectionRabbitHttp.mirroringKey(otpObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(mContext, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!transactionId.isEmpty()) {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                    BaseMeetingActivity.tvLoading.setVisibility(View.VISIBLE);
                                    BaseMeetingActivity.showProgress(true);
                                } else {
                                    DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white));
                                    DipsSwafoto.tvLoading.setVisibility(View.VISIBLE);
                                    DipsSwafoto.showProgress(true);
                                }
                            }
                        });
                        running = false;
                        loopStatus = 0;
                        processValidateOTP();
                    }
                }
            }
        });
        runTimer(TimerOTP, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    otp.setText("");
                    resendOTP();
                }
            }
        });
    }

    private void processValidateOTP() {
        JSONObject dataObjOTP = new JSONObject();
        try {
            dataObjOTP.put("transactionId", transactionId);
            dataObjOTP.put("idDips", idDips);
            dataObjOTP.put("token", numberOTP);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), dataObjOTP.toString());
        Server.getAPIService().ValidateOTP(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    int loopSave = 0;
                    APISaveForm(loopSave);
                } else {
                    running = true;
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                                BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                            } else {
                                DipsSwafoto.showProgress(false);
                                DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                                DipsSwafoto.tvLoading.setVisibility(View.GONE);
                            }
                        }
                    });
                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_failed));
                    textTitleOTP.setText(R.string.titleWrongOTP);
                    otp.setText("");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                running = true;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                            BaseMeetingActivity.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                            BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                        } else {
                            DipsSwafoto.showProgress(false);
                            DipsSwafoto.rlprogress.setBackgroundColor(getResources().getColor(R.color.white_transparent));
                            DipsSwafoto.tvLoading.setVisibility(View.GONE);
                        }
                    }
                });
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processApprovalStatus(int loopApprove) {
        String idForm = "";
        try {
            JSONObject dataTrx = dataTrxArr.getJSONObject(loopApprove);
            JSONObject dataParse = dataTrx.getJSONObject("data");
            idForm = dataParse.getString("idForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().ApprovalStatus(idForm,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();

                    try {
                        JSONObject dataTrx = dataTrxArr.getJSONObject(loopApprove);
                        JSONObject dataParse = dataTrx.getJSONObject("data");

                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }

                        if (dataObj.getJSONObject("data").has("status")) {
                            String getStatus = dataObj.getJSONObject("data").getString("status");
                            dataParse.put("statusApprove", !getStatus.equals("reject"));
                        }

                        if (dataObj.getJSONObject("data").has("message")) {
                            String getMsg = dataObj.getJSONObject("data").getString("message");
                            dataParse.put("messageApprove", getMsg);
                        }

                        dataTrx.put("data",dataParse);
                        dataTrxArr.put(loopApprove,dataTrx);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if (loopApprove < dataTrxArr.length()-1) {
                        int addLoopApprove = loopApprove + 1;
                        processApprovalStatus(addLoopApprove);
                    }

                    if (loopApprove == dataTrxArr.length()-1) {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.showProgress(false);
                                    BaseMeetingActivity.rlprogress.setBackgroundColor(mContext.getResources().getColor(R.color.white_transparent));
                                    BaseMeetingActivity.tvLoading.setVisibility(View.GONE);
                                } else {
                                    DipsSwafoto.showProgress(false);
                                    DipsSwafoto.rlprogress.setBackgroundColor(mContext.getResources().getColor(R.color.white_transparent));
                                    DipsSwafoto.tvLoading.setVisibility(View.GONE);
                                }
                            }
                        });

                        if (loopApprove == 0) {
                            try {
                                JSONObject dataTrx = dataTrxArr.getJSONObject(loopApprove);
                                JSONObject dataParse = dataTrx.getJSONObject("data");
                                if (dataParse.has("statusApprove")) {
                                    boolean statusApprove = dataParse.getBoolean("statusApprove");
                                    if (!statusApprove) {
                                        popUpReject();
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("token")) {
                                String accessToken = dataObj.getString("token");
                                String exchangeToken = dataObj.getString("exchange");
                                sessions.saveAuthToken(accessToken);
                                sessions.saveExchangeToken(exchangeToken);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Bundle bundle = new Bundle();

                        bundle.putString("dataTrxArr",dataTrxArr.toString());
                        if (formId == 48 || formId == 54 || formId == 55) {
                            ConnectionRabbitHttp.mirroringEndpoint(193);
                            bundle.putInt("formCode",193);
                        } else if (formId == 49) {
                            ConnectionRabbitHttp.mirroringEndpoint(192);
                            bundle.putInt("formCode",192);
                        } else if (formId == 56) {
                            ConnectionRabbitHttp.mirroringEndpoint(203);
                            bundle.putInt("formCode",203);
                        }
                        sendDataFragment(bundle,new frag_service_antarbank_resi());
                    }

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processApprovalStatus(loopApprove);
                                }
                            });
                        }
                    },10000);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processApprovalStatus(loopApprove);
                            }
                        });
                    }
                },10000);
            }
        });
    }

    private void popUpReject() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_warning));
        tvTitleDialog.setText(mContext.getResources().getString(R.string.transaksi_anda_gagal));
        tvBodyDialog.setText(mContext.getResources().getString(R.string.please_tryagain_later));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
                ConnectionRabbitHttp.mirroringEndpoint(14);
                getFragmentPage(new frag_portfolio_new());
            }
        });
    }

    private void resendOTP() {
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
        processSendOTP();
    }

    private void runTimer(TextView timer_run, TextView resend) {
        Handler handlerTimer = new Handler();
        handlerTimer.post(new Runnable() {
            @Override
            public void run() {
                int minutes = getMinutes;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0 && minutes == 0){
                    running = false;
                    resend.setClickable(true);
                } else if (seconds == 0 && minutes > 0){
                    seconds = 59;
                }
                if (seconds == 59) {
                    getMinutes--;
                }
                handlerTimer.postDelayed(this,1000);
            }
        });
    }

    private void sendDataFragment(Bundle bundle, Fragment fragment){
        fragment.setArguments(bundle);
        if (isSessionZoom) {
            getFragmentPage(fragment);
        } else {
            getFragmentPageDefault(fragment);
        }
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean getFragmentPageDefault(Fragment fragment){
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

}