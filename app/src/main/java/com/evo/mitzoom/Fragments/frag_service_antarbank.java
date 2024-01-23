package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterQR;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Adapter.OnClickUploadImageListener;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.HideSoftKeyboard;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_antarbank extends Fragment {

    public static int REQUESTCODE_GALLERY_QRCODE = 3;
    private final int REQUESTCODE_FILE = 202;
    private int REQUESTCODE_GALLERY = 2;
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ArrayList<ItemModel> dataItems = null;
    private ArrayList<String> nameItemQR = null;
    private ArrayList<String> noFormQR;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapterPager;
    private RecyclerView.LayoutManager recylerViewLayoutManagerPager;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private TextView tvAddUpQRCode;
    private RecyclerView rv_item;
    private OnClickUploadImageListener uploadImageListener;
    public static int intPos = -1;
    private LinearLayout llFormBuild;
    private JSONArray idElement;
    private JSONArray idElementMulti = null;
    private JSONObject dataObjTrx;
    private JSONArray dataTrxArr;
    private JSONArray dataTrxArrMirror;
    private JSONArray dataSelectedSource;
    JSONObject objEl = new JSONObject();
    JSONObject objElAwal = new JSONObject();
    JSONObject mirrObj = new JSONObject();
    private int lasLenChar;
    private boolean backSpaceChar;
    JSONObject valSpin = new JSONObject();
    JSONObject valSpinProv = new JSONObject();
    private boolean flagStuckSpin = false;
    private String provinsi,kodepos="",kota_kabupaten,kecamatan,desa_kelurahan;
    private TextView tvSavedImg;
    private TextView tvSavedFile;
    private String picturePath;
    private String labelserv = "";
    private LinearLayout llLayout;
    private RecyclerView rv_itemPage;
    private TextView tvTrxQR;
    private LinearLayout llDeleteTrx;
    private TextView tvNoFormulir;
    int selected_position = 0;
    private Button btnAddForm;
    private Button btnContinue;
    private NestedScrollView nestedScroll;
    private int flagData = 0; //0 = default, 1 = match data, 2 = nihil data
    private int formId;
    private int formIdAwal;
    private boolean selectedpager = false;
    private boolean reCheck = false;
    private String labelTrx = "";
    private boolean flagHitAPIForm = false;
    private String messageError = "";
    private TextView tvAlertRek = null;
    private String noRekSource = "";
    private long nominalRek = Long.valueOf(0);
    private TextView tvAlertNominal = null;
    private ArrayList<FormSpin>  dataDropDownSource;
    private long longNumCurrent = 0;
    private String dataNominal = "";
    private String idService = "";
    private long biayaLayanan = 0;
    private String jenislayanan = "";
    private String labelTypeServ = "";
    private EditText edNamePenerima;
    private String destAccount = "";
    private JSONArray dataTrxArrNew;
    private final Handler handlerTimer = new Handler();
    private final Runnable myHandlerTimer = new Runnable() {
        @Override
        public void run() {
            if (labelTrx.equals("antarbank")) {
                if (!jenislayanan.equalsIgnoreCase("rtgs") && !jenislayanan.equalsIgnoreCase("skn")) {
                    if (jenislayanan.equalsIgnoreCase("online")) {
                        processInquiryOnline(selected_position);
                    }
                }
            } else {
                processInquiryOnline(selected_position);
            }
        }
    };
    private String minNominal = "0";
    private String maxNominal = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();

        ConnectionRabbitHttp.init(mContext);

        if (getArguments() != null) {
            if (getArguments().containsKey("idGenerateForm")) {
                formIdAwal = getArguments().getInt("idGenerateForm");
            }
            if (getArguments().containsKey("idService")) {
                idService = getArguments().getString("idService");
            }
            if (getArguments().containsKey("reCheck")) {
                reCheck = getArguments().getBoolean("reCheck");
            }
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            if (getArguments().containsKey("dataTrxArr")) {
                String getdataTrxArr = getArguments().getString("dataTrxArr");
                try {
                    dataTrxArr = new JSONArray(getdataTrxArr);

                    JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                    formIdAwal = dataTrx.getInt("idGenerateForm");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("idElementMulti")) {
                String getidElement = getArguments().getString("idElementMulti");
                try {
                    idElementMulti = new JSONArray(getidElement);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("nameItemQR")) {
                nameItemQR = getArguments().getStringArrayList("nameItemQR");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_service_antarbank, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);
        rv_item = (RecyclerView) views.findViewById(R.id.rv_item);
        nestedScroll = (NestedScrollView) views.findViewById(R.id.nestedScroll);

        tvAddUpQRCode = (TextView) views.findViewById(R.id.tvAddUpQRCode);

        rv_itemPage = (RecyclerView) views.findViewById(R.id.rv_itemPage);
        tvTrxQR = (TextView) views.findViewById(R.id.tvTrxQR);
        llDeleteTrx = (LinearLayout) views.findViewById(R.id.llDeleteTrx);
        tvNoFormulir = (TextView) views.findViewById(R.id.tvNoFormulir);

        llLayout = (LinearLayout) views.findViewById(R.id.llLayout);
        llFormBuild = (LinearLayout) views.findViewById(R.id.llFormBuild);

        btnAddForm = (Button) views.findViewById(R.id.btnAddForm);
        btnContinue = (Button) views.findViewById(R.id.btnContinue);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!labelserv.isEmpty()) {
            tvtitleHead.setText(labelserv);
        }

        formId = formIdAwal;
        if (formId == 48 || (idService.equals("33") && isSessionZoom)) {
            labelTrx = "antarbank";
        } else if (formId == 49 || (idService.equals("4") && isSessionZoom)) {
            labelTrx = "interbank";
        } else if (formId == 56 || (idService.equals("16") && isSessionZoom)) {
            labelTrx = "privatetransaction";
        }

        if (!reCheck) {
            dataTrxArr = new JSONArray();
        }
        dataTrxArrMirror = new JSONArray();
        dataSelectedSource = new JSONArray();
        if (idElementMulti == null) {
            idElementMulti = new JSONArray();
        }
        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }

        processGetForm(formId);
        if (nameItemQR == null) {
            addData();
        } else {
            setNameItemQR();
        }
        setRecyler();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionRabbitHttp.mirroringEndpoint(199);
                getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        tvAddUpQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideSoftKeyboard.hideSoftKeyboard(getActivity());
                addUpQRCode();
                longNumCurrent = 0;
                minNominal = "0";
                maxNominal = "0";
            }
        });

        btnAddForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideSoftKeyboard.hideSoftKeyboard(getActivity());
                formId = formIdAwal;
                longNumCurrent = 0;
                minNominal = "0";
                maxNominal = "0";
                addItemsPager();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideSoftKeyboard.hideSoftKeyboard(getActivity());
                if (labelTrx.equals("antarbank")) {
                    if (!jenislayanan.isEmpty()) {
                        int loopInq = 0 ;
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }

                        biayaLayanan = 0;
                        processGetFeeCharge(loopInq);
                    } else {
                        Toast.makeText(mContext,labelTypeServ+" "+getString(R.string.alertRTGS),Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    jenislayanan = "interbank";
                    int loopInq = 0 ;
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }

                    biayaLayanan = 0;
                    processGetFeeCharge(loopInq);
                }
                /*else {
                    processInquiryOnline(loopInq);
                }*/
            }
        });

        llDeleteTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (dataTrxArr.getJSONObject(selected_position).has("noForm")) {
                        String noForm = dataTrxArr.getJSONObject(selected_position).getString("noForm");
                        for (int i = 0; i < noFormQR.size(); i++) {
                            String getNoForm = noFormQR.get(i);
                            if (noForm.equals(getNoForm)) {
                                noFormQR.remove(i);
                                dataItems.remove(i);
                                nameItemQR.remove(i);
                                recyclerViewAdapter.notifyDataSetChanged();
                                if (dataItems.size() == 0) {
                                    addData();
                                    setRecyler();
                                }
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                dataTrxArr.remove(selected_position);
                dataTrxArrMirror.remove(selected_position);
                idElementMulti.remove(selected_position);
                dataSelectedSource.remove(selected_position);
                recyclerViewAdapterPager.notifyItemRemoved(selected_position);

                try {
                    mirrObj.put(labelTrx,dataTrxArrMirror);
                    mirrObj.put("activeIndex",selected_position);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                ConnectionRabbitHttp.mirroringKey(mirrObj);

                if (dataTrxArr.length() > 0) {
                    selected_position = dataTrxArr.length() - 1;
                    setRecylerPager();
                    recyclerViewAdapterPager.notifyItemChanged(selected_position);
                } else {
                    llLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addData() {
        dataItems = new ArrayList<>();
        noFormQR = new ArrayList<>();
        nameItemQR = new ArrayList<>();
        String to = "to";
        if (sessions.getLANG().equals("id")) {
            to = "ke";
        }
        for (int i = 1; i <= 1; i++) {
            String strQr = getResources().getString(R.string.insertqrcode_trx)+" "+to+" "+i;
            String ij = String.valueOf(i);
            dataItems.add(new ItemModel(ij,strQr,0));
            nameItemQR.add(strQr);
        }
    }

    private void addUpQRCode() {
        int sizeItem = dataItems.size() + 1;
        String to = "to";
        if (sessions.getLANG().equals("id")) {
            to = "ke";
        }
        String strQr = getResources().getString(R.string.insertqrcode_trx)+" "+to+" "+sizeItem;
        String ij = String.valueOf(sizeItem);
        dataItems.add(new ItemModel(ij,strQr,0));
        nameItemQR.add(strQr);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void setNameItemQR() {
        dataItems = new ArrayList<>();
        noFormQR = new ArrayList<>();
        int idQr = 1;
        for (int i = 0; i < nameItemQR.size(); i++) {
            String strQr = nameItemQR.get(i);
            String ij = String.valueOf(idQr);
            dataItems.add(new ItemModel(ij,strQr,0));
            idQr++;
        }
    }

    private void setRecyler(){
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_item.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new AdapterQR(frag_service_antarbank.this, getContext(), dataItems);
        uploadImageListener = (OnClickUploadImageListener) recyclerViewAdapter;
        rv_item.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    private void setRecylerPager() {
        Log.e("masuk setRecylerPager","");
        recylerViewLayoutManagerPager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_itemPage.setLayoutManager(recylerViewLayoutManagerPager);

        recyclerViewAdapterPager = new AdapterNumPager();
        rv_itemPage.setAdapter(recyclerViewAdapterPager);
        recyclerViewAdapterPager.notifyItemInserted(dataTrxArr.length() - 1);
    }

    private void addItemsPager() {
        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }
        processGenerateNoForm();
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
                    tvAlertNominal.setVisibility(View.GONE);
                    if (tvAlertRek != null) {
                        tvAlertRek.setVisibility(View.GONE);
                    }
                    JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                    String label = dataTrx.getString("label");
                    int idGenerateForm = dataTrx.getInt("idGenerateForm");
                    if (idGenerateForm != formId && !reCheck) {
                        formId = idGenerateForm;
                        selectedpager = true;
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(true);
                        } else {
                            DipsSwafoto.showProgress(true);
                        }
                        dataTrx.put("idGenerateForm",formId);
                        dataTrxArr.put(selected_position,dataTrx);
                        processGetForm(formId);
                    }
                    else {
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
                        btnContinue.setEnabled(true);
                        btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        processNihilDataForm();
                        processMatchDataForm(dataParse);
                    }
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

    private void processMatchDataForm(JSONObject dataParse) {
        flagData = 1;
        int child = llFormBuild.getChildCount();
        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            if (idEl == idDataEl) {
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    if (dataParse.has(nameDataEl)) {
                                        String valEl = dataParse.getString(nameDataEl);
                                        ed.setText(valEl);
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();
                                        if (dataParse.has(nameDataEl)) {
                                            String valEl = dataParse.getString(nameDataEl);
                                            String valRad = valEl.toLowerCase();

                                            String valKurung = "";
                                            int indx = valEl.indexOf("(");
                                            if (indx >= 0) {
                                                valKurung = valEl.substring(indx);
                                            }

                                            if (valEl.toLowerCase().equals("kawin" + valKurung)) {
                                                valRad = "menikah";
                                            }
                                            if (labelRad.toLowerCase().equals(valRad)) {
                                                rb.setChecked(true);
                                                break;
                                            } else {
                                                if (valRad.contains("laki") && valRad.contains("-")) {
                                                    String[] sp = valRad.split("-");
                                                    valRad = sp[0] + " - " + sp[1];
                                                    if (labelRad.toLowerCase().equals(valRad)) {
                                                        rb.setChecked(true);
                                                        break;
                                                    }
                                                }
                                                else if ((valRad.contains("man") || valRad.contains("woman")) && (labelRad.toLowerCase().contains("kelamin") || labelRad.toLowerCase().contains("gender"))) {
                                                    rb.setChecked(true);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    String labelCheck = chk.getText().toString();
                                    if (dataParse.has(nameDataEl)) {
                                        boolean valEl = dataParse.getBoolean(nameDataEl);
                                        chk.setChecked(valEl);
                                    } else if (dataParse.has(labelCheck)) {
                                        boolean valEl = dataParse.getBoolean(labelCheck);
                                        chk.setChecked(valEl);
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    if (dataParse.has(nameDataEl)) {
                                        String valEl = dataParse.getString(nameDataEl);
                                        for (int ch = 0; ch < spin.getCount(); ch++) {
                                            if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                                spin.setSelection(ch);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        if (spin.getCount() == 0) {
                                            break;
                                        }
                                        if (dataParse.has(nameDataEl)) {
                                            String valEl = dataParse.getString(nameDataEl);
                                            if (nameDataEl.contains("sumber") && nameDataEl.contains("dana")) {
                                                String noRekSelected = "";
                                                if (valEl.contains("/")) {
                                                    String[] sp = valEl.split(" / ");
                                                    String no_nama_Rek = sp[1].trim();
                                                    if (no_nama_Rek.indexOf("-") > 0) {
                                                        String[] sp2 = no_nama_Rek.split("-");
                                                        noRekSelected = sp2[0].trim();
                                                    }
                                                }
                                                for (int ch = 0; ch < spin.getCount(); ch++) {
                                                    String dataAcc = spin.getItemAtPosition(ch).toString();
                                                    String noRek = "";
                                                    if (dataAcc.contains("\n")) {
                                                        String[] sp = dataAcc.split("\n");
                                                        String no_nama_Rek = sp[1].trim();
                                                        if (no_nama_Rek.indexOf("-") > 0) {
                                                            String[] sp2 = no_nama_Rek.split("-");
                                                            noRek = sp2[0].trim();
                                                        }
                                                    }
                                                    if (!noRek.isEmpty()) {
                                                        if (noRek.equals(noRekSelected)) {
                                                            spin.setSelection(ch);
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else {
                                                for (int ch = 0; ch < spin.getCount(); ch++) {
                                                    String dataSpin = spin.getItemAtPosition(ch).toString();
                                                    if (dataSpin.equals(valEl)) {
                                                        spin.setSelection(ch);
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                        break;
                                    } else if (rl.getChildAt(0) instanceof AutoCompleteTextView) {
                                        AutoCompleteTextView autoText = (AutoCompleteTextView) rl.getChildAt(0);
                                        if (dataParse.has(nameDataEl)) {
                                            String valEl = dataParse.getString(nameDataEl);

                                            ListAdapter listA = autoText.getAdapter();
                                            if (listA == null) {
                                                break;
                                            }
                                            if (listA.getCount() == 0) {
                                                break;
                                            }
                                            ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                                            for (int ch = 0; ch < listA.getCount(); ch++) {
                                                String getItem = listA.getItem(ch).toString();
                                                if (listA.getCount() > 1) {
                                                    Object item = listA.getItem(ch);
                                                    if (item instanceof FormSpin) {
                                                        FormSpin dataSpin = (FormSpin) item;
                                                        int idData = dataSpin.getId();
                                                        String nameInd = dataSpin.getName();
                                                        String valCode = dataSpin.getCode();
                                                        String nameEng = dataSpin.getNameEng();
                                                        dataDropDown.add(new FormSpin(idData, valCode, nameInd, nameEng));
                                                    }
                                                }
                                                if (!valEl.isEmpty()) {
                                                    if (valEl.equals(getItem) || getItem.contains(valEl)) {
                                                        int beneficiaryCode = 0;
                                                        String swiftCode = "";
                                                        String cityCode = "";

                                                        Object item = listA.getItem(ch);
                                                        if (item instanceof FormSpin) {
                                                            FormSpin dataSpin = (FormSpin) item;
                                                            int idData = dataSpin.getId();
                                                            String results = dataSpin.getName();
                                                            String valCode = dataSpin.getCode();
                                                            autoText.setText(results);

                                                            objEl.put(nameDataEl, results);

                                                            if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                                beneficiaryCode = idData;
                                                                if (valCode.contains("|")) {
                                                                    String[] sp = valCode.split("\\|");
                                                                    swiftCode = sp[0].trim();
                                                                    cityCode = sp[1].trim();
                                                                }
                                                            } else if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                                                                if (results.contains("\n")) {
                                                                    String[] sp = results.split("\n");
                                                                    String noRek = sp[1].trim();
                                                                    objEl.put(nameDataEl, noRek);
                                                                    destAccount = noRek;
                                                                }
                                                            }
                                                        }

                                                        if (!flagHitAPIForm) {
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                            getObjTrx.put("data", reqFormMirroring);

                                                            if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                                getObjTrx.put("beneficiaryCode", beneficiaryCode);
                                                                getObjTrx.put("swiftCode", swiftCode);
                                                                getObjTrx.put("cityCode", cityCode);
                                                            }
                                                            dataTrxArr.put(selected_position, getObjTrx);
                                                            if (isSessionZoom) {
                                                                dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                                mirrObj.put(labelTrx, dataTrxArrMirror);
                                                                mirrObj.put("activeIndex", selected_position);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (listA.getCount() > 1) {
                                                ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
                                                autoText.setAdapter(adapter2);
                                            }
                                        }
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    if (ll.findViewById(R.id.llCurrency) != null) {
                                        EditText tvContentCurr = ll.findViewById(R.id.tvContentCurr);
                                        if (dataParse.has(nameDataEl)) {
                                            String valEl = dataParse.getString(nameDataEl);
                                            tvContentCurr.setText(valEl);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            flagHitAPIForm = false;
        }
    }

    private void processNihilDataForm() {
        flagData = 2;
        int child = llFormBuild.getChildCount();

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            if (idEl == idDataEl) {
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    ed.setText("");
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.clearCheck();
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    chk.setChecked(false);
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setSelection(0);
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        if (nameDataEl.contains("sumberdana")) {
                                            if (dataSelectedSource.length() > 0) {
                                                ArrayList<FormSpin> getSpinDataSource = (ArrayList<FormSpin>) dataSelectedSource.get(selected_position);
                                                AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext, R.layout.dropdown_multiline, getSpinDataSource);
                                                spin.setAdapter(adapterSourceAcc);
                                            }
                                        }
                                        spin.setSelection(0);
                                    } else if (rl.getChildAt(0) instanceof AutoCompleteTextView) {
                                        AutoCompleteTextView autoText = (AutoCompleteTextView) rl.getChildAt(0);
                                        autoText.setText("");
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    autoText.setText("");
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    if (ll.findViewById(R.id.llCurrency) != null) {
                                        EditText tvContentCurr = ll.findViewById(R.id.tvContentCurr);
                                        tvContentCurr.setText("");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static int randomId() {
        Random random=new Random();
        int dataInt = random.nextInt(99999999);
        return dataInt;
    }


    private void processGetFeeCharge(int loopInq) {
        JSONObject objReq = new JSONObject();
        try {
            JSONObject dataTrx = dataTrxArr.getJSONObject(loopInq);
            JSONObject dataParse = dataTrx.getJSONObject("data");

            if (!dataTrx.has("accountType")) {
                Toast.makeText(mContext,mContext.getResources().getString(R.string.source_account)+" "+mContext.getResources().getString(R.string.alertRTGS),Toast.LENGTH_LONG).show();
                return;
            }

            String typeService = jenislayanan.toLowerCase();
            if (dataParse.has("jenislayanan")) {
                typeService = dataParse.getString("jenislayanan").toLowerCase();
            }

            ParseDataTrxNew(loopInq);
            objReq.put("transaction",typeService);
            objReq.put("currency","IDR");

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer " + sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().GetFeeCharge(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            JSONObject dataObj = dataBody.getJSONObject("data");
                            if (dataObj.has("amount")) {
                                biayaLayanan = dataObj.getLong("amount");
                            }

                            String fee = biayaLayanan + "00";

                            dataParse.put("adminFee", fee);
                            dataTrx.put("data", dataParse);
                            dataTrxArrNew.put(loopInq, dataTrx);

                            int intAplhabet = randomId();
                            JSONObject dataObjEl = new JSONObject();
                            dataObjEl.put("id", intAplhabet);
                            dataObjEl.put("name", "adminFee");
                            dataObjEl.put("CompoName", "autocomplete");
                            dataObjEl.put("required", false);
                            dataObjEl.put("keyIndo", "biayalayanan");
                            dataObjEl.put("label", getString(R.string.biaya_layanan));

                            JSONArray getElement = new JSONArray(idElementMulti.getJSONArray(loopInq).toString());
                            boolean flagFee = false;
                            if (getElement.length() > 0) {
                                for (int el = 0; el < getElement.length(); el++) {
                                    JSONObject ObjGetEl = getElement.getJSONObject(el);
                                    if (ObjGetEl.getString("name").equals("adminFee")) {
                                        flagFee = true;
                                        break;
                                    }
                                }
                            }

                            if (!flagFee) {
                                getElement.put(getElement.length(), dataObjEl);
                                idElementMulti.put(loopInq, getElement);
                            }

                            if (loopInq < dataTrxArrNew.length() - 1) {
                                int addLoopInq = loopInq + 1;
                                processGetFeeCharge(addLoopInq);
                            }

                            if (loopInq == dataTrxArrNew.length() - 1) {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.showProgress(false);
                                } else {
                                    DipsSwafoto.showProgress(false);
                                }
                                Bundle bundle = new Bundle();
                                bundle.putString("labelserv",labelserv);
                                bundle.putString("idElementMulti", idElementMulti.toString());
                                bundle.putString("dataTrxArr", dataTrxArrNew.toString());
                                bundle.putString("messageError", messageError);
                                bundle.putStringArrayList("nameItemQR",nameItemQR);
                                sendDataFragment(bundle, new frag_service_confirm_antarbank());
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            JSONObject getDataObj = dataTrx.getJSONObject("data");
                            getDataObj.put("adminFee", 000);
                            dataTrx.put("data", getDataObj);
                            dataTrxArr.put(loopInq, dataTrx);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        if (loopInq == dataTrxArr.length() - 1) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void ParseDataTrxNew(int loopInq) {
        try {
            JSONObject dataTrx = dataTrxArr.getJSONObject(loopInq);
            JSONObject dataParse = dataTrx.getJSONObject("data");
            String accountType = dataTrx.getString("accountType");
            String beneficiaryCode = "";
            if (dataTrx.has("beneficiaryCode")) {
                int getbeneficiaryCode = dataTrx.getInt("beneficiaryCode");
                beneficiaryCode = String.valueOf(getbeneficiaryCode);
                if (beneficiaryCode.length() < 3) {
                    if (beneficiaryCode.length() == 1) {
                        beneficiaryCode = "00" + beneficiaryCode;
                    } else if (beneficiaryCode.length() == 2) {
                        beneficiaryCode = "0" + beneficiaryCode;
                    }
                }
            }
            String swiftCode = "";
            String cityCode = "";
            if (dataTrx.has("swiftCode")) {
                swiftCode = dataTrx.getString("swiftCode");
            }
            if (dataTrx.has("cityCode")) {
                cityCode = dataTrx.getString("cityCode");
            }
            String systemTraceAuditNumber = dataTrx.getString("systemTraceAuditNumber");
            String retrievalReferenceNumber = dataTrx.getString("retrievalReferenceNumber");

            JSONObject jsons = new JSONObject();

            String settlementDate = new SimpleDateFormat("dd-MM-yyyy",
                    Locale.getDefault()).format(new Date());

            long unixTime = System.currentTimeMillis() / 1000L;
            String epochTimes = String.valueOf(unixTime);

            String amount = "0";
            String rekPenerima = "";
            String berita = "";
            String sumberRek = "";
            String typeService = "";
            String transactionType = "";

            for (int j = 0; j < idElement.length(); j++) {
                String nameDataEl = idElement.getJSONObject(j).getString("name");
                if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                    rekPenerima = dataParse.getString(nameDataEl);
                }
                else if (nameDataEl.contains("nominal") && nameDataEl.contains("transaksi")) {
                    amount = dataParse.getString(nameDataEl);
                    amount = amount.replace(",", "");
                    amount = amount.replace(".", "");
                    amount = amount + "00";
                }
                else if (nameDataEl.contains("berita")) {
                    berita = dataParse.getString(nameDataEl);
                }
                else if (nameDataEl.contains("sumber")) {
                    sumberRek = dataParse.getString(nameDataEl);
                }
                else if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                    typeService = dataParse.getString(nameDataEl);
                }
            }

            String fromAccount = "";
            String issueName = "";
            String[] sp = sumberRek.split(" / ");
            if (sp.length > 1) {
                String nameNoRek = sp[1];
                String[] sp2 = nameNoRek.split("-");
                fromAccount = sp2[0].trim();
                issueName = sp2[1].trim();
            }

            jsons.put("currencyCode", "360"); //360 is IDR
            jsons.put("accountType", accountType);
            jsons.put("systemTraceAuditNumber", systemTraceAuditNumber);
            jsons.put("retrievalReferenceNumber", retrievalReferenceNumber);
            jsons.put("localDateTime", epochTimes);
            jsons.put("amount", amount);
            jsons.put("fromAccountNumber", fromAccount);
            jsons.put("toAccountNumber", rekPenerima);
            jsons.put("issuerCustomerName", issueName);
            if (typeService.equals("RTGS") || typeService.equals("SKN")) {
                transactionType = "ONLINE";
                jsons.put("benefitRecipients", "2");
                jsons.put("residentType", "2");
                jsons.put("destSwiftCode", swiftCode);
                jsons.put("destCityCode", cityCode);
            } else {
                transactionType = typeService;
            }
            if (labelTrx.equals("antarbank")) {
                jsons.put("beneficiaryCode", beneficiaryCode);
                jsons.put("settlementDate", settlementDate);
                if (dataParse.has("adminFee")) {
                    jsons.put("adminFee", dataParse.getString("adminFee"));
                }
                jsons.put("transactionType", transactionType);
            }
            else if (labelTrx.equals("interbank") || labelTrx.equals("privatetransaction")) {
                jsons.put("message", berita);
            }

            for(Iterator<String> iter = jsons.keys(); iter.hasNext();) {
                if (iter.hasNext()) {
                    String key = iter.next();
                    dataParse.put(key,jsons.get(key));
                }
            }
            dataParse.put("message", berita);

            dataTrx.put("data", dataParse);
            dataTrxArrNew = new JSONArray(dataTrxArr.toString());
            dataTrxArrNew.put(loopInq, dataTrx);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processInquiryOnline(int loopInq) {

        JSONObject jsons = null;
        JSONObject dataTrx = null;
        JSONObject dataParse = null;
        try {
            dataTrx = dataTrxArr.getJSONObject(loopInq);
            if (idElementMulti.get(loopInq) != null) {
                String label = dataTrx.getString("label");
                String noForm = dataTrx.getString("noForm");
                String accountType = dataTrx.getString("accountType");
                String beneficiaryCode = "";
                if (dataTrx.has("beneficiaryCode")) {
                    int getbeneficiaryCode = dataTrx.getInt("beneficiaryCode");
                    beneficiaryCode = String.valueOf(getbeneficiaryCode);
                    if (beneficiaryCode.length() < 3) {
                        if (beneficiaryCode.length() == 1) {
                            beneficiaryCode = "00" + beneficiaryCode;
                        } else if (beneficiaryCode.length() == 2) {
                            beneficiaryCode = "0" + beneficiaryCode;
                        }
                    }
                }
                String swiftCode = "";
                String cityCode = "";
                if (dataTrx.has("swiftCode")) {
                    swiftCode = dataTrx.getString("swiftCode");
                }
                if (dataTrx.has("cityCode")) {
                    cityCode = dataTrx.getString("cityCode");
                }
                String systemTraceAuditNumber = dataTrx.getString("systemTraceAuditNumber");
                String retrievalReferenceNumber = dataTrx.getString("retrievalReferenceNumber");
                dataParse = dataTrx.getJSONObject("data");
                //jsons = new JSONObject(dataParse.toString());
                jsons = new JSONObject();

                String settlementDate = new SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault()).format(new Date());

                long unixTime = System.currentTimeMillis() / 1000L;
                String epochTimes = String.valueOf(unixTime);

                String amount = "0";
                String rekPenerima = "";
                String berita = "";
                String sumberRek = "";
                String typeService = "";
                String transactionType = "";

                for (int j = 0; j < idElement.length(); j++) {
                    String nameDataEl = idElement.getJSONObject(j).getString("name");
                    if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                        rekPenerima = dataParse.getString(nameDataEl);
                    }
                    else if (nameDataEl.contains("nominal") && nameDataEl.contains("transaksi")) {
                        amount = dataParse.getString(nameDataEl);
                        amount = amount.replace(",", "");
                        amount = amount.replace(".", "");
                        amount = amount + "00";
                    }
                    else if (nameDataEl.contains("berita")) {
                        berita = dataParse.getString(nameDataEl);
                    } else if (nameDataEl.contains("sumber")) {
                        sumberRek = dataParse.getString(nameDataEl);
                    }
                    else if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                        typeService = dataParse.getString(nameDataEl);
                    }
                }

                if (typeService.toLowerCase().contains("fast")) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    toFragmentMaintenance();
                    return;
                }

                String fromAccount = "";
                String issueName = "";
                String[] sp = sumberRek.split(" / ");
                if (sp.length > 1) {
                    String nameNoRek = sp[1];
                    String[] sp2 = nameNoRek.split("-");
                    fromAccount = sp2[0].trim();
                    issueName = sp2[1].trim();
                }

                jsons.put("currencyCode", "360"); //360 is IDR
                jsons.put("accountType", accountType);
                jsons.put("systemTraceAuditNumber", systemTraceAuditNumber);
                jsons.put("retrievalReferenceNumber", retrievalReferenceNumber);
                jsons.put("localDateTime", epochTimes);
                jsons.put("amount", amount);
                jsons.put("fromAccountNumber", fromAccount);
                jsons.put("toAccountNumber", rekPenerima);
                jsons.put("issuerCustomerName", issueName);
                if (typeService.equals("RTGS") || typeService.equals("SKN")) {
                    transactionType = "ONLINE";
                    jsons.put("benefitRecipients", "2");
                    jsons.put("residentType", "2");
                    jsons.put("destSwiftCode", swiftCode);
                    jsons.put("destCityCode", cityCode);
                } else {
                    transactionType = typeService;
                }
                if (labelTrx.equals("antarbank")) {
                    jsons.put("beneficiaryCode", beneficiaryCode);
                    jsons.put("settlementDate", settlementDate);
                    if (dataParse.has("adminFee")) {
                        jsons.put("adminFee", dataParse.getString("adminFee"));
                    } else {
                        jsons.put("adminFee", "0");
                    }
                    jsons.put("transactionType", transactionType);
                }
                else if (labelTrx.equals("interbank") || labelTrx.equals("privatetransaction")) {
                    jsons.put("message", berita);
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

                String authAccess = "Bearer " + sessions.getAuthToken();
                String exchangeToken = sessions.getExchangeToken();

                for(Iterator<String> iter = jsons.keys(); iter.hasNext();) {
                    if (iter.hasNext()) {
                        String key = iter.next();
                        dataParse.put(key,jsons.get(key));
                    }
                }
                dataParse.put("message", berita);

                JSONObject finalDataParse = dataParse;

                JSONObject finalDataTrx = dataTrx;
                finalDataTrx.put("data", finalDataParse);

                dataTrxArrNew = new JSONArray(dataTrxArr.toString());
                dataTrxArrNew.put(loopInq, finalDataTrx);

                Call<JsonObject> APIRequest = null;
                if (labelTrx.equals("antarbank")) {
                    APIRequest = Server.getAPIService().InquiryOnline(requestBody, authAccess, exchangeToken);
                } else if (labelTrx.equals("interbank") || labelTrx.equals("privatetransaction")) {
                    APIRequest = Server.getAPIService().InquiryOverbook(requestBody, authAccess, exchangeToken);
                }

                APIRequest.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        int errCode = response.code();
                        if (response.isSuccessful()) {
                            String dataS = response.body().toString();
                            try {
                                JSONObject dataObj = new JSONObject(dataS);
                                JSONObject dataBody = dataObj.getJSONObject("data");
                                JSONObject dataBody1 = dataBody.getJSONObject("data");
                                String destCustomerName = dataBody1.getString("destCustomerName");
                                btnContinue.setEnabled(true);
                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));

                                edNamePenerima.setText(destCustomerName);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        } else {
                            messageError = getString(R.string.confirm_trx_error1);
                            try {

                                tvAlertRek.setText(getString(R.string.account_receive_not_found));
                                tvAlertRek.setVisibility(View.VISIBLE);
                                edNamePenerima.setText("");
                                btnContinue.setEnabled(false);
                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));

                                String msg = "";
                                if (response.body() != null) {
                                    String dataS = response.body().toString();
                                    JSONObject dataObj = new JSONObject(dataS);
                                    if (dataObj.has("message")) {
                                        msg = dataObj.getString("message");
                                    }
                                } else {
                                    if (response.errorBody().toString().isEmpty()) {
                                        String dataS = response.errorBody().toString();
                                        JSONObject dataObj = new JSONObject(dataS);
                                        if (dataObj.has("message")) {
                                            msg = dataObj.getString("message");
                                        }
                                    } else {
                                        String dataS = null;
                                        dataS = response.errorBody().string();
                                        JSONObject dataObj = new JSONObject(dataS);
                                        if (dataObj.has("message")) {
                                            msg = dataObj.getString("message");
                                        }
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        messageError = getString(R.string.confirm_trx_error2);
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                });
            } else {
                int addLoopInq = loopInq + 1;
                if (addLoopInq < dataTrxArr.length()) {
                    processInquiryOnline(addLoopInq);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processGenerateNoForm() {
        flagHitAPIForm = false;
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().GenerateNoForm(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    flagData = 0;
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String dataNoForm = dataObj.getString("data");
                        String noForm = dataNoForm;
                        String retrievalReferenceNumber = "";
                        String systemTraceAuditNumber = "";
                        destAccount = "";
                        noRekSource = "";

                        if (noForm.contains("{")) {
                            JSONObject parseForm = new JSONObject(dataNoForm);
                            noForm = parseForm.getString("noForm");
                            retrievalReferenceNumber = parseForm.getString("retrievalReferenceNumber");
                            systemTraceAuditNumber = parseForm.getString("systemTraceAuditNumber");
                        }
                        dataObjTrx = new JSONObject();
                        try {
                            dataObjTrx.put("label","non_qr");
                            dataObjTrx.put("idGenerateForm",formId);
                            dataObjTrx.put("noForm",noForm);
                            dataObjTrx.put("systemTraceAuditNumber",systemTraceAuditNumber);
                            dataObjTrx.put("retrievalReferenceNumber",retrievalReferenceNumber);
                            dataObjTrx.put("data",objElAwal);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        dataTrxArr.put(dataObjTrx);
                        parsingDataSourceAccount();
                        processNihilDataForm();
                        setRecylerPager();
                        if (llLayout.getVisibility() != View.VISIBLE) {
                            llLayout.setVisibility(View.VISIBLE);
                        }

                        selected_position = dataTrxArr.length() - 1;
                        recyclerViewAdapterPager.notifyItemChanged(selected_position);
                        nestedScroll.fullScroll(View.FOCUS_UP);
                        try {
                            idElementMulti.put(selected_position,idElement);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if (isSessionZoom) {
                            JSONObject reqFormMirroring = dataReqFormMirroring();
                            dataTrxArrMirror.put(selected_position,reqFormMirroring);
                            mirrObj.put(labelTrx,dataTrxArrMirror);
                            mirrObj.put("activeIndex",selected_position);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                        }
                        btnContinue.setEnabled(true);
                        btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void parsingDataSourceAccount() {
        ArrayList<FormSpin> dataNewDropDownSource = new ArrayList<>();
        String textSelect = getString(R.string.choose_source_fund);
        dataNewDropDownSource.add(new FormSpin(0,"0",textSelect,textSelect));
        for (int i = 0; i < dataDropDownSource.size(); i++) {
            int id = dataDropDownSource.get(i).getId();
            String code = dataDropDownSource.get(i).getCode();
            String dataAcc = dataDropDownSource.get(i).getName();
            String dataAccEng = dataDropDownSource.get(i).getNameEng();
            if (dataAcc.contains("\n")) {
                String[] sp = dataAcc.split("\n");
                String prodName = sp[0].trim();
                String no_nama_Rek = sp[1].trim();
                String noRek = "";
                String accountName = "";
                if (no_nama_Rek.indexOf("-") > 0) {
                    String[] sp2 = no_nama_Rek.split("-");
                    noRek = sp2[0].trim();
                    accountName = sp2[1].trim();
                }

                String nominal = "";
                String labelIdn = "";
                if (sp.length > 2) {
                    nominal = sp[2];
                }

                if (noRek.equals(noRekSource)) {
                    String[] sp3 = nominal.split(" ");
                    String acctCur = sp3[0].trim();
                    long sub = nominalRek - longNumCurrent;

                    Double d = Double.valueOf(sub);
                    NumberFormat formatter = null;
                    if (sessions.getLANG().equals("id")) {
                        formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                    } else {
                        formatter = NumberFormat.getInstance(new Locale("en", "US"));
                    }
                    formatter.setMinimumFractionDigits(2);
                    String formattedNumber = formatter.format(d);

                    labelIdn = prodName+"\n"+noRek+" - "+accountName+"\n"+acctCur+" "+formattedNumber;
                    dataNewDropDownSource.add(new FormSpin(id,code,labelIdn,labelIdn));
                } else {
                    labelIdn = prodName+"\n"+noRek+" - "+accountName+"\n"+nominal;
                    dataNewDropDownSource.add(new FormSpin(id,code,labelIdn,labelIdn));
                }
            }
        }

        try {
            dataSelectedSource.put(selected_position,dataDropDownSource);
            int selected_position_Next = dataTrxArr.length() - 1;
            dataSelectedSource.put(selected_position_Next,dataNewDropDownSource);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetForm(int formId) {
        flagHitAPIForm = true;
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    llFormBuild.removeAllViewsInLayout();
                    llFormBuild.setVisibility(View.VISIBLE);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        new MyParserFormBuilder(mContext, dataForm, llFormBuild,idService);
                        idElement = MyParserFormBuilder.getForm();

                        idElementMulti.put(selected_position,idElement);

                        tvAlertRek = (TextView) llFormBuild.findViewById(R.id.et_rek_penerima);
                        tvAlertNominal = (TextView) llFormBuild.findViewById(R.id.et_nominal);
                        processValidationActionForm();
                        SelectedPagerMatch();
                        ReCheckMatch();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void ReCheckMatch() {
        if (reCheck) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }

                            setRecylerPager();
                            if (llLayout.getVisibility() != View.VISIBLE) {
                                llLayout.setVisibility(View.VISIBLE);
                            }
                            selected_position = dataTrxArr.length() - 1;
                            processNihilDataForm();
                            recyclerViewAdapterPager.notifyItemChanged(selected_position);
                            try {
                                if (isSessionZoom) {
                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                    dataTrxArrMirror.put(selected_position,reqFormMirroring);
                                    mirrObj.put(labelTrx,dataTrxArrMirror);
                                    mirrObj.put("activeIndex",selected_position);
                                    ConnectionRabbitHttp.mirroringKey(mirrObj);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            btnContinue.setEnabled(true);
                            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                        }
                    });
                }
            },5000);
        }
    }

    private void SelectedPagerMatch() {
        if (selectedpager) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                                String label = dataTrx.getString("label");
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
                                processNihilDataForm();
                                processMatchDataForm(dataParse);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            },5000);
        }
    }

    private void processValidationActionForm() {
        int child = llFormBuild.getChildCount();
        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            String CompoName = idElement.getJSONObject(j).getString("CompoName");
                            String CompoLabel = idElement.getJSONObject(j).getString("label");
                            String valKurung = "";
                            int indx = nameDataEl.indexOf("(");
                            if (indx >= 0) {
                                valKurung = nameDataEl.substring(indx);
                            }
                            String urlPath = "";
                            if (idElement.getJSONObject(j).has("url")) {
                                urlPath = idElement.getJSONObject(j).getString("url");
                            }
                            if (idEl == idDataEl) {
                                objElAwal.put(nameDataEl,"");
                                String finalValKurung = valKurung;
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    if (nameDataEl.contains("nama") && nameDataEl.contains("penerima")) {
                                        edNamePenerima = ed;
                                    }
                                    if (!CompoName.equals("datalist")) {
                                        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View view, boolean b) {

                                            }
                                        });
                                    }
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (flagData != 2) {
                                                String inputData = charSequence.toString();
                                                if (!noRekSource.isEmpty()) {
                                                }

                                                if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                                                    tvAlertRek.setVisibility(View.GONE);
                                                    if (!inputData.isEmpty()) {
                                                        if (inputData.length() >= 5) {
                                                            if (inputData.startsWith("0888") || inputData.startsWith("8888")) {
                                                                btnContinue.setEnabled(false);
                                                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                            } else {
                                                                btnContinue.setEnabled(true);
                                                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                            }
                                                        } else {
                                                            btnContinue.setEnabled(true);
                                                            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                        }
                                                    }
                                                }

                                                try {
                                                    objEl.put(nameDataEl, charSequence);
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                    getObjTrx.put("data", reqFormMirroring);
                                                    dataTrxArr.put(selected_position, getObjTrx);
                                                    if (isSessionZoom) {
                                                        dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                        mirrObj.put(labelTrx, dataTrxArrMirror);
                                                        mirrObj.put("activeIndex",selected_position);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (nameDataEl.equals("npwp"+finalValKurung)) {
                                                ed.removeTextChangedListener(this);
                                                backSpaceChar = lasLenChar > s.length();
                                                if (!backSpaceChar) {
                                                    String dataNPWP = s.toString();
                                                    String formatNPWP = "";
                                                    if (dataNPWP.length() == 2 || dataNPWP.length() == 6 || dataNPWP.length() == 10 || dataNPWP.length() == 16) {
                                                        formatNPWP = ".";
                                                    } else if (dataNPWP.length() == 12) {
                                                        formatNPWP = "-";
                                                    }
                                                    String cekBuilder = new StringBuilder(dataNPWP).insert(dataNPWP.length(), formatNPWP).toString();
                                                    ed.setText(cekBuilder);
                                                    ed.setSelection(cekBuilder.length());
                                                }
                                                ed.addTextChangedListener(this);
                                            } else if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                                                String dataVal = s.toString();
                                                destAccount = dataVal;
                                                if (dataVal.contains("\n")) {
                                                    String[] strings = dataVal.split("\\r?\\n");
                                                    String titleAcc = strings[0] + "\n";
                                                    String acc = strings[1] + "\n";
                                                    if (acc.contains("-")) {
                                                        String[] sp = acc.split("-");
                                                        String norekPenerima = sp[0].trim();
                                                        ed.setText(norekPenerima);
                                                    }
                                                }

                                                if (destAccount.length() > 3 && !noRekSource.isEmpty()) {
                                                    handlerTimer.removeCallbacks(myHandlerTimer);
                                                    handlerTimer.postDelayed(myHandlerTimer,3000);
                                                }
                                            } else if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                String dataVal = s.toString();
                                                FormSpin dataSpin = (FormSpin) s;
                                            }
                                        }
                                    });
                                    objEl.put(nameDataEl, "");
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    objEl.put(nameDataEl, "");

                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            if (flagData != 2) {
                                                int selectedId = rg.getCheckedRadioButtonId();
                                                if (selectedId > 0 || selectedId < -1) {
                                                    RadioButton rb = rg.findViewById(selectedId);
                                                    String results = rb.getText().toString();
                                                    processEnableComp(results);
                                                    try {
                                                        objEl.put(nameDataEl, results);

                                                        if (!flagHitAPIForm) {
                                                            JSONObject reqFormMirroring = dataReqFormMirroring();
                                                            JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                            getObjTrx.put("data", reqFormMirroring);
                                                            dataTrxArr.put(selected_position, getObjTrx);
                                                            if (isSessionZoom) {
                                                                dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                                mirrObj.put(labelTrx, dataTrxArrMirror);
                                                                mirrObj.put("activeIndex", selected_position);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            }
                                                            if (nameDataEl.contains("tipe") && nameDataEl.contains("layanan")) {
                                                                if (results.toLowerCase().contains("proxy") && formId != 55) {
                                                                    formId = 55;
                                                                    if (!results.isEmpty()) {
                                                                        reCheck = true;
                                                                        if (isSessionZoom) {
                                                                            BaseMeetingActivity.showProgress(true);
                                                                        } else {
                                                                            DipsSwafoto.showProgress(true);
                                                                        }
                                                                        processGetForm(formId);
                                                                    }

                                                                }
                                                                else if (formId != 54) {
                                                                    formId = 54;

                                                                    if (!results.isEmpty()) {
                                                                        reCheck = true;
                                                                        if (isSessionZoom) {
                                                                            BaseMeetingActivity.showProgress(true);
                                                                        } else {
                                                                            DipsSwafoto.showProgress(true);
                                                                        }
                                                                        getObjTrx.put("idGenerateForm", formId);
                                                                        dataTrxArr.put(selected_position, getObjTrx);
                                                                        llFormBuild.removeAllViewsInLayout();
                                                                        processGetForm(formId);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    objEl.put(nameDataEl, false);

                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    chk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (flagData != 2) {
                                                boolean isChk = chk.isChecked();
                                                if (isChk) {
                                                    try {
                                                        objEl.put(nameDataEl, isChk);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    try {
                                                        objEl.put(nameDataEl, isChk);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                JSONObject reqFormMirroring = dataReqFormMirroring();
                                                try {
                                                    JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                    getObjTrx.put("data", reqFormMirroring);
                                                    dataTrxArr.put(selected_position, getObjTrx);

                                                    if (isSessionZoom) {
                                                        dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                        mirrObj.put(labelTrx, dataTrxArrMirror);
                                                        mirrObj.put("activeIndex",selected_position);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    objEl.put(nameDataEl, "");
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            if (flagData != 2) {
                                                String results = spin.getSelectedItem().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                    getObjTrx.put("data", reqFormMirroring);
                                                    dataTrxArr.put(selected_position, getObjTrx);
                                                    if (isSessionZoom) {
                                                        dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                        mirrObj.put(labelTrx, dataTrxArrMirror);
                                                        mirrObj.put("activeIndex",selected_position);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        objEl.put(nameDataEl, "");
                                        Spinner spin = (Spinner) rl.getChildAt(0);

                                        boolean flagDot = false;
                                        if (!urlPath.isEmpty()) {
                                            String[] spUrl = urlPath.split("/");
                                            int indexs = spUrl.length - 1;
                                            String check = spUrl[indexs];
                                            if (check.isEmpty()) {
                                                indexs = spUrl.length - 2;
                                                check = spUrl[indexs];
                                            }
                                            if (check.contains(":")) {
                                                flagDot = true;
                                            }
                                            if (!flagDot) {
                                                if (nameDataEl.contains("sumberdana") || (nameDataEl.contains("rekening") && nameDataEl.contains("penerima"))) {
                                                    btnAddForm.setEnabled(false);
                                                    processGetDynamicURLSumberDana(spin,urlPath,nameDataEl);
                                                } else {
                                                    processGetDynamicURL(spin, urlPath, nameDataEl);
                                                }
                                            }
                                        }

                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                if (flagData != 2) {
                                                    String accountType = "";
                                                    int beneficiaryCode = 0;
                                                    String swiftCode = "";
                                                    String cityCode = "";
                                                    if (nameDataEl.contains("sumberdana")) {
                                                        tvAlertNominal.setVisibility(View.GONE);
                                                        FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                        String results = dataSpin.getName();
                                                        accountType = dataSpin.getCode();
                                                        if (results.indexOf("\n") > 0) {
                                                            String[] sp = results.split("\n");
                                                            String typeAccount = sp[0].trim();
                                                            String no_nama_Rek = sp[1].trim();
                                                            if (no_nama_Rek.indexOf("-") > 0) {
                                                                String[] sp2 = no_nama_Rek.split("-");
                                                                noRekSource = sp2[0].trim();
                                                            }
                                                            String valueNominalRek = sp[2].trim();
                                                            String getNominalRek = valueNominalRek.replaceAll("\\D+","");
                                                            nominalRek = Long.valueOf(getNominalRek) / 100;

                                                            if (!dataNominal.isEmpty()) {
                                                                if (longNumCurrent > nominalRek) {
                                                                    String contexAlert = "";
                                                                    if (idService.equals("191")) {
                                                                        contexAlert = mContext.getResources().getString(R.string.balance_insufficient);
                                                                    } else {
                                                                        contexAlert = mContext.getResources().getString(R.string.alert_nominal);
                                                                    }
                                                                    tvAlertNominal.setText(contexAlert);
                                                                    tvAlertNominal.setVisibility(View.VISIBLE);
                                                                    btnContinue.setEnabled(false);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                                } else {
                                                                    tvAlertNominal.setVisibility(View.GONE);
                                                                    btnContinue.setEnabled(true);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                                }
                                                            }

                                                        } else {
                                                            btnContinue.setEnabled(false);
                                                            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                        }
                                                        results = results.replaceAll("\n"," / ");
                                                        try {
                                                            objEl.put(nameDataEl, results);
                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    } else {
                                                        if (tvAlertRek != null) {
                                                            tvAlertRek.setVisibility(View.GONE);
                                                        }

                                                        FormSpin dataSpin = (FormSpin) spin.getSelectedItem();
                                                        int idData = dataSpin.getId();
                                                        String results = dataSpin.getName();
                                                        try {
                                                            if (flagHitAPIForm) {
                                                                JSONObject dataTrx = dataTrxArr.getJSONObject(selected_position);
                                                                JSONObject dataParse = dataTrx.getJSONObject("data");
                                                                if (dataParse.has(nameDataEl)) {
                                                                    String valEl = dataParse.getString(nameDataEl);
                                                                    if (!valEl.isEmpty()) {
                                                                        results = valEl;
                                                                        for (int ch = 0; ch < spin.getCount(); ch++) {
                                                                            if (spin.getItemAtPosition(ch).toString().equals(results)) {
                                                                                spin.setSelection(ch);
                                                                                objEl.put(nameDataEl, results);
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                objEl.put(nameDataEl, results);
                                                            }
                                                            if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                                                String newNameDataEl = nameDataEl;
                                                                if (nameDataEl.contains("(")) {
                                                                    int indxProv = nameDataEl.indexOf("(");
                                                                    newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                                                }
                                                                valSpinProv.put(newNameDataEl, idData);
                                                                if (nameDataEl.contains("provinsi")) {
                                                                    provinsi = results;
                                                                    kodepos = "";
                                                                } else if (nameDataEl.contains("kabupaten") || nameDataEl.contains("kota")) {
                                                                    kota_kabupaten = results;
                                                                    kodepos = "";
                                                                } else if (nameDataEl.contains("kecamatan")) {
                                                                    kecamatan = results;
                                                                    kodepos = "";
                                                                } else if (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa")) {
                                                                    desa_kelurahan = results;
                                                                    kodepos = "";
                                                                }
                                                            } else if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                                                                jenislayanan = results;
                                                                labelTypeServ = CompoLabel;
                                                                if (jenislayanan.equalsIgnoreCase("online")) {
                                                                    edNamePenerima.setEnabled(false);
                                                                    edNamePenerima.setBackground(mContext.getResources().getDrawable(R.drawable.bg_textinput_disable));
                                                                    edNamePenerima.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                                                } else {
                                                                    edNamePenerima.setEnabled(true);
                                                                    edNamePenerima.setBackground(mContext.getResources().getDrawable(R.drawable.bg_textinput));
                                                                    edNamePenerima.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                                                }
                                                            } else if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                                beneficiaryCode = idData;
                                                                String valCode = dataSpin.getCode();
                                                                if (valCode.contains("|")) {
                                                                    String[] sp = valCode.split("\\|");
                                                                    swiftCode = sp[0].trim();
                                                                    cityCode = sp[1].trim();
                                                                }
                                                            }  else {
                                                                valSpin.put(nameDataEl, idData);
                                                            }
                                                            if (flagStuckSpin) {
                                                                processGetSpinChild(nameDataEl);
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    if (!flagHitAPIForm) {
                                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                                        try {
                                                            JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                            getObjTrx.put("data", reqFormMirroring);
                                                            if (nameDataEl.contains("sumberdana")) {
                                                                getObjTrx.put("accountType", accountType);
                                                            }
                                                            if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                                getObjTrx.put("beneficiaryCode", beneficiaryCode);
                                                                getObjTrx.put("swiftCode", swiftCode);
                                                                getObjTrx.put("cityCode", cityCode);
                                                            }
                                                            dataTrxArr.put(selected_position, getObjTrx);

                                                            if (isSessionZoom) {
                                                                dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                                mirrObj.put(labelTrx, dataTrxArrMirror);
                                                                mirrObj.put("activeIndex", selected_position);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            }

                                                            if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                                                                if (!jenislayanan.isEmpty()) {
//                                                                    if (jenislayanan.toLowerCase().contains("fast") && !flagHitAPIForm) {
//                                                                        formId = 54;
//                                                                        reCheck = true;
//                                                                        if (isSessionZoom) {
//                                                                            BaseMeetingActivity.showProgress(true);
//                                                                        } else {
//                                                                            DipsSwafoto.showProgress(true);
//                                                                        }
//                                                                        getObjTrx.put("idGenerateForm", formId);
//                                                                        dataTrxArr.put(selected_position, getObjTrx);
//                                                                        llFormBuild.removeAllViewsInLayout();
//                                                                        processGetForm(formId);
//                                                                    }
//                                                                    else{
//                                                                        if (formId != 48){
//                                                                            formId = 48;
//                                                                            reCheck = true;
//                                                                            if (isSessionZoom) {
//                                                                                BaseMeetingActivity.showProgress(true);
//                                                                            } else {
//                                                                                DipsSwafoto.showProgress(true);
//                                                                            }
//                                                                            getObjTrx.put("idGenerateForm", formId);
//                                                                            dataTrxArr.put(selected_position, getObjTrx);
//                                                                            llFormBuild.removeAllViewsInLayout();
//                                                                            processGetForm(formId);
//                                                                        }
//
//                                                                    }

                                                                    GetLimitTransaction(selected_position);
                                                                }
                                                            }

                                                            if (destAccount.length() > 3 && !noRekSource.isEmpty()) {
                                                                if (destAccount.equals(noRekSource) && idService.equals("16")) {
                                                                    if (tvAlertRek != null) {
                                                                        String alertNotSame = mContext.getResources().getString(R.string.alert_norek_notmatch);
                                                                        tvAlertRek.setVisibility(View.VISIBLE);
                                                                        tvAlertRek.setText(alertNotSame);
                                                                    }
                                                                    edNamePenerima.setText("");
                                                                    btnContinue.setEnabled(false);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                                } else {
                                                                    if (tvAlertRek != null) {
                                                                        tvAlertRek.setVisibility(View.GONE);
                                                                    }
                                                                    handlerTimer.removeCallbacks(myHandlerTimer);
                                                                    handlerTimer.postDelayed(myHandlerTimer, 3000);
                                                                }
                                                            }
                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                        break;
                                    } else if (rl.getChildAt(0) instanceof AutoCompleteTextView) {
                                        objEl.put(nameDataEl, "");
                                        AutoCompleteTextView autoText = (AutoCompleteTextView) rl.getChildAt(0);

                                        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                                if (flagData != 2) {
                                                    int beneficiaryCode = 0;
                                                    String swiftCode = "";
                                                    String cityCode = "";

                                                    Object item = adapterView.getItemAtPosition(position);
                                                    if (item instanceof FormSpin) {
                                                        FormSpin dataSpin = (FormSpin) item;
                                                        int idData = dataSpin.getId();
                                                        String results = dataSpin.getName();
                                                        String valCode = dataSpin.getCode();

                                                        try {
                                                            if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                                objEl.put(nameDataEl, results);
                                                                beneficiaryCode = idData;
                                                                if (valCode.contains("|")) {
                                                                    String[] sp = valCode.split("\\|");
                                                                    swiftCode = sp[0].trim();
                                                                    cityCode = sp[1].trim();
                                                                }
                                                            } else if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                                                                if (results.contains("\n")) {
                                                                    String[] sp = results.split("\n");
                                                                    String noRek = sp[1].trim();
                                                                    objEl.put(nameDataEl, noRek);
                                                                    destAccount = noRek;

                                                                    if (destAccount.length() > 3 && !noRekSource.isEmpty()) {
                                                                        edNamePenerima.setText("");
                                                                        if (destAccount.equals(noRekSource) && idService.equals("16")) {
                                                                            if (tvAlertRek != null) {
                                                                                String alertNotSame = mContext.getResources().getString(R.string.alert_norek_notmatch);
                                                                                tvAlertRek.setVisibility(View.VISIBLE);
                                                                                tvAlertRek.setText(alertNotSame);
                                                                            }
                                                                            btnContinue.setEnabled(false);
                                                                            btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                                        } else {
                                                                            if (tvAlertRek != null) {
                                                                                tvAlertRek.setVisibility(View.GONE);
                                                                            }
                                                                            handlerTimer.removeCallbacks(myHandlerTimer);
                                                                            handlerTimer.postDelayed(myHandlerTimer, 3000);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        }

                                                    }

                                                    if (!flagHitAPIForm) {
                                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                                        try {
                                                            JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                            getObjTrx.put("data", reqFormMirroring);

                                                            if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                                                getObjTrx.put("beneficiaryCode", beneficiaryCode);
                                                                getObjTrx.put("swiftCode", swiftCode);
                                                                getObjTrx.put("cityCode", cityCode);
                                                            }
                                                            dataTrxArr.put(selected_position, getObjTrx);

                                                            if (isSessionZoom) {
                                                                dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                                mirrObj.put(labelTrx, dataTrxArrMirror);
                                                                mirrObj.put("activeIndex", selected_position);
                                                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                            }

                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                                else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    objEl.put(nameDataEl, "");

                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            if (flagData != 2) {
                                                String results = autoText.getText().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                    getObjTrx.put("data", reqFormMirroring);
                                                    dataTrxArr.put(selected_position, getObjTrx);
                                                    if (isSessionZoom) {
                                                        dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                        mirrObj.put(labelTrx, dataTrxArrMirror);
                                                        mirrObj.put("activeIndex",selected_position);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                    autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            if (flagData != 2) {
                                                String results = autoText.getText().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    JSONObject reqFormMirroring = dataReqFormMirroring();
                                                    JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                    getObjTrx.put("data", reqFormMirroring);
                                                    dataTrxArr.put(selected_position, getObjTrx);
                                                    if (isSessionZoom) {
                                                        dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                        mirrObj.put(labelTrx, dataTrxArrMirror);
                                                        mirrObj.put("activeIndex",selected_position);
                                                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                    break;
                                }
                                else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    if (ll.findViewById(R.id.llCurrency) != null) {
                                        EditText tvContentCurr = ll.findViewById(R.id.tvContentCurr);
                                        try {
                                            objEl.put(nameDataEl, "");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        tvContentCurr.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if (flagData != 2) {
                                                    try {
                                                        objEl.put(nameDataEl, s);
                                                        dataNominal = s.toString();
                                                        if (!dataNominal.isEmpty()) {
                                                            String number = dataNominal.replaceAll("\\D+", "");
                                                            if (!maxNominal.equals("0")) {
                                                                if (number.length() > maxNominal.length()) {
                                                                    return;
                                                                }
                                                            }
                                                            longNumCurrent = Long.valueOf(number);

                                                            if (longNumCurrent > nominalRek) {
                                                                String contexAlert = "";
                                                                if (idService.equals("191")) {
                                                                    contexAlert = mContext.getResources().getString(R.string.balance_insufficient);
                                                                } else {
                                                                    contexAlert = mContext.getResources().getString(R.string.alert_nominal);
                                                                }
                                                                tvAlertNominal.setText(contexAlert);
                                                                tvAlertNominal.setVisibility(View.VISIBLE);
                                                                btnContinue.setEnabled(false);
                                                                btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                            } else {
                                                                long longMinNom = Long.valueOf(minNominal);
                                                                long longMaxNom = Long.valueOf(maxNominal);
                                                                if (longMinNom > 0 && longMaxNom > 0) {
                                                                    if (longNumCurrent < longMinNom || longNumCurrent > longMaxNom) {
                                                                        NumberFormat formatter = null;
                                                                        if (sessions.getLANG().equals("id")) {
                                                                            formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                                                                        } else {
                                                                            formatter = NumberFormat.getInstance(new Locale("en", "US"));
                                                                        }
                                                                        formatter.setMinimumFractionDigits(2);
                                                                        String minFormat = formatter.format(longMinNom);
                                                                        String maxFormat = formatter.format(longMaxNom);
                                                                        String contexAlert = "";
                                                                        if (sessions.getLANG().equals("id")) {
                                                                            contexAlert = "Minimal transaksi Rp " + minFormat + " dan Maksimal Rp " + maxFormat;
                                                                        } else {
                                                                            contexAlert = "Minimum transaction is Rp " + minFormat + " and maximum is Rp " + maxFormat;
                                                                        }
                                                                        tvAlertNominal.setText(contexAlert);
                                                                        tvAlertNominal.setVisibility(View.VISIBLE);

                                                                        btnContinue.setEnabled(false);
                                                                        btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                                                    } else {
                                                                        tvAlertNominal.setVisibility(View.GONE);
                                                                        btnContinue.setEnabled(true);
                                                                        btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                                    }
                                                                } else {
                                                                    tvAlertNominal.setVisibility(View.GONE);
                                                                    btnContinue.setEnabled(true);
                                                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                                                }
                                                            }
                                                        }

                                                        JSONObject reqFormMirroring = dataReqFormMirroring();
                                                        JSONObject getObjTrx = dataTrxArr.getJSONObject(selected_position);
                                                        getObjTrx.put("data", reqFormMirroring);
                                                        dataTrxArr.put(selected_position, getObjTrx);
                                                        if (isSessionZoom) {
                                                            dataTrxArrMirror.put(selected_position, reqFormMirroring);
                                                            mirrObj.put(labelTrx, dataTrxArrMirror);
                                                            mirrObj.put("activeIndex",selected_position);
                                                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                            }
                                        });

                                    } else {
                                        if (ll.getChildCount() > 1) {
                                            if (ll.getChildAt(0) instanceof LinearLayout) {
                                                LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);

                                                TextView tvll = (TextView) ll2.getChildAt(1);
                                                String txt = tvll.getText().toString();
                                                if (txt.toLowerCase().indexOf("gambar") > 0 || txt.toLowerCase().indexOf("image") > 0) {
                                                    tvSavedImg = (TextView) ll.getChildAt(1);
                                                    ll2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            REQUESTCODE_GALLERY = 201;
                                                            sessions.saveMedia(2);
                                                            chooseFromSD();
                                                        }
                                                    });
                                                } else {
                                                    tvSavedFile = (TextView) ll.getChildAt(1);
                                                    ll2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent();
                                                            intent.setType("*/*");
                                                            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                            String[] mimetypes = { "application/pdf", "application/doc", "text/*" };

                                                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                                            startActivityForResult(intent, REQUESTCODE_FILE);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private JSONObject dataReqFormMirroring() {
        JSONObject dataFormObj = null;
        try {
            dataFormObj = new JSONObject(objEl.toString());
            dataFormObj.put("noForm",tvNoFormulir.getText());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return dataFormObj;
    }

    private void processEnableComp(String results) {
        int child = llFormBuild.getChildCount();

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            String nameDataElGab = "";
                            if (idElement.getJSONObject(j).has("nameGab")) {
                                nameDataElGab = idElement.getJSONObject(j).getString("nameGab");
                            }
                            if (idEl == idDataEl) {
                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    if (!nameDataElGab.isEmpty()) {
                                        if (nameDataElGab.equals(nameDataEl)) {
                                            if (results.toLowerCase().contains("lain") || results.toLowerCase().contains("other")) {
                                                ed.setEnabled(true);
                                                ed.setFocusableInTouchMode(true);
                                            } else {
                                                ed.setEnabled(false);
                                                ed.setFocusable(false);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private void GetLimitTransaction(int loopInq) {
        try {

            JSONObject dataTrx = dataTrxArr.getJSONObject(loopInq);
            JSONObject dataParse = dataTrx.getJSONObject("data");

            String typeService = "";
            String transactionType = "";
            for (int j = 0; j < idElement.length(); j++) {
                String nameDataEl = idElement.getJSONObject(j).getString("name");
                if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                    typeService = dataParse.getString(nameDataEl);
                }
            }

            /*if (typeService.equals("RTGS") || typeService.equals("SKN")) {
                transactionType = "ONLINE";
            } else {
                transactionType = typeService;
            }*/

            JSONObject objReq = new JSONObject();
            objReq.put("transactionType",typeService.toUpperCase());

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());
            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().LimitTransaction(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataObj = new JSONObject(response.body().toString());
                            JSONObject objData = dataObj.getJSONObject("data");
                            if (objData.has("minNominal")) {
                                minNominal = objData.getString("minNominal");
                            }
                            if (objData.has("maxNominal")) {
                                maxNominal = objData.getString("maxNominal");
                            }

                            long longMinNom = Long.valueOf(minNominal);
                            long longMaxNom = Long.valueOf(maxNominal);
                            if (longMinNom > 0 && longMaxNom > 0) {

                                if (longNumCurrent < longMinNom || longNumCurrent > longMaxNom) {
                                    NumberFormat formatter = null;
                                    if (sessions.getLANG().equals("id")) {
                                        formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                                    } else {
                                        formatter = NumberFormat.getInstance(new Locale("en", "US"));
                                    }
                                    formatter.setMinimumFractionDigits(2);
                                    String minFormat = formatter.format(longMinNom);
                                    String maxFormat = formatter.format(longMaxNom);
                                    String contexAlert = "";
                                    if (sessions.getLANG().equals("id")) {
                                        contexAlert = "Minimal transaksi Rp " + minFormat + " dan Maksimal Rp " + maxFormat;
                                    } else {
                                        contexAlert = "Minimum transaction is Rp " + minFormat + " and maximum is Rp " + maxFormat;
                                    }
                                    tvAlertNominal.setText(contexAlert);
                                    tvAlertNominal.setVisibility(View.VISIBLE);

                                    btnContinue.setEnabled(false);
                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_text_grey));
                                } else {
                                    tvAlertNominal.setVisibility(View.GONE);
                                    btnContinue.setEnabled(true);
                                    btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                                }
                            }
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

    private void processGetDynamicURLSumberDana(Spinner spinner, String urlPath, String nameDataEl) {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",sessions.getNoCIF());
            jsons.put("bahasa",sessions.getLANG());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrlPost(urlPath,requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                btnAddForm.setEnabled(true);
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
                        JSONObject objData = dataObj.getJSONObject("data");
                        JSONArray dataArr = objData.getJSONArray("portotabungan");
                        dataDropDownSource = new ArrayList<>();
                        int len = dataArr.length() + 1;
                        String[] sourceAcc = new String[len];
                        String textSelect = getString(R.string.choose_source_fund);
                        sourceAcc[0] = textSelect;
                        dataDropDownSource.add(new FormSpin(0,"0",textSelect,textSelect));
                        int loopSource = 1;
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = i + 1;

                            String prodName = dataArr.getJSONObject(i).getString("prodName").replace("R/K","").trim();
                            String prodCode = dataArr.getJSONObject(i).getString("prodCode");
                            if (prodCode.equals("T21")) {
                                continue;
                            }
                            if (dataArr.getJSONObject(i).has("acctStatus")) {
                                String acctStatus = dataArr.getJSONObject(i).getString("acctStatus");
                                if (!acctStatus.equals("A")) {
                                    continue;
                                }
                            }
                            String accountNo = dataArr.getJSONObject(i).getString("accountNo");
                            String accountName = dataArr.getJSONObject(i).getString("accountName");
                            String acctCur = dataArr.getJSONObject(i).getString("acctCur");
                            String availBalance = dataArr.getJSONObject(i).getString("availBalance");
                            String accountType = dataArr.getJSONObject(i).getString("accountType");
                            availBalance = availBalance.substring(0,availBalance.length() - 2);

                            if (acctCur.equals("IDR")) {
                                acctCur = "Rp.";
                            }

                            Double d = Double.valueOf(availBalance);
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);
                            String formattedNumber = formatter.format(d);
                            String labelIdn = "";
                            if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                                labelIdn = prodName + "\n" + accountNo + " - " + accountName;
                            } else {
                                labelIdn = prodName + "\n" + accountNo + " - " + accountName + "\n" + acctCur + " " + formattedNumber;
                            }
                            sourceAcc[loopSource] = labelIdn;
                            loopSource++;

                            dataDropDownSource.add(new FormSpin(idData,accountType,labelIdn,labelIdn));
                        }
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDownSource);
                        spinner.setAdapter(adapterSourceAcc);

                        SelectedPagerMatch();
                        ReCheckMatch();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
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
    private void processGetDynamicURL(Spinner spin, String urlPath, String nameDataEl) {
        flagStuckSpin = false;
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrl(urlPath,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = 0;
                            String idSData = "";
                            String valueCode = "";

                            if (dataArr.getJSONObject(i).has("ids")) {
                                idSData = dataArr.getJSONObject(i).getString("ids").trim();
                                idData = Integer.parseInt(idSData);
                            } else if (dataArr.getJSONObject(i).has("id")) {
                                idData = dataArr.getJSONObject(i).getInt("id");

                            }

                            String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                            String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                            if (sessions.getLANG().equals("en")) {
                                labelIdn = labelEng;
                            }
                            valueCode = labelIdn;
                            if (dataArr.getJSONObject(i).has("beneficiaryCode")) {
                                String beneficiaryCode = dataArr.getJSONObject(i).getString("beneficiaryCode");
                                idData = Integer.parseInt(beneficiaryCode);
                            }
                            if (dataArr.getJSONObject(i).has("swiftCode") && dataArr.getJSONObject(i).has("cityCode")) {
                                String swiftCode = dataArr.getJSONObject(i).getString("swiftCode");
                                String cityCode = dataArr.getJSONObject(i).getString("cityCode");
                                valueCode = swiftCode+" | "+cityCode;
                            }
                            dataDropDown.add(new FormSpin(idData,valueCode,labelIdn,labelEng));
                            if (i == 0) {
                                if (nameDataEl.contains("provinsi") || nameDataEl.contains("kabupaten") || nameDataEl.contains("kota") || nameDataEl.contains("kecamatan") || (nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                    String newNameDataEl = nameDataEl;
                                    if (nameDataEl.contains("(")) {
                                        int indxProv = nameDataEl.indexOf("(");
                                        newNameDataEl = nameDataEl.substring(0,indxProv).trim();
                                    }
                                    valSpinProv.put(newNameDataEl,idData);
                                } else {
                                    valSpin.put(nameDataEl, idData);
                                }
                                processGetSpinChild(nameDataEl);
                                if ((nameDataEl.contains("kelurahan") || nameDataEl.contains("desa"))) {
                                    flagStuckSpin = true;
                                }
                            }
                        }
                        ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
                        spin.setAdapter(adapter2);

                        SelectedPagerMatch();
                        ReCheckMatch();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processGetSpinChild(String nameDataEl) {
        int child = llFormBuild.getChildCount();
        for (int i = 0; i < child; i++) {
            int idEl = llFormBuild.getChildAt(i).getId();
            for (int j = 0; j < idElement.length(); j++) {
                try {
                    int idDataEl = idElement.getJSONObject(j).getInt("id");
                    String getnameDataEl = idElement.getJSONObject(j).getString("name");
                    String urlPath = "";
                    if (idElement.getJSONObject(j).has("url")) {
                        urlPath = idElement.getJSONObject(j).getString("url");
                    }

                    if (idEl == idDataEl) {
                        if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                            if ((nameDataEl.contains("provinsi") || nameDataEl.contains("province")) && (getnameDataEl.contains("kabupaten") || getnameDataEl.contains("district") || getnameDataEl.contains("kota") || getnameDataEl.contains("city"))) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    if (idProv != 0) {
                                        String urlNew = urlPath.replace(":id_provinsi", idSpin);

                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            }
                            else if ((nameDataEl.contains("kabupaten") || nameDataEl.contains("district") || nameDataEl.contains("kota") || nameDataEl.contains("city")) && (getnameDataEl.contains("kecamatan") || getnameDataEl.contains("subdistrict"))) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    int idKabKot = 0;
                                    if (valSpinProv.has("kabupaten")) {
                                        idKabKot = valSpinProv.getInt("kabupaten");
                                    } else if (valSpinProv.has("district")) {
                                        idKabKot = valSpinProv.getInt("district");
                                    } else if (valSpinProv.has("kota")) {
                                        idKabKot = valSpinProv.getInt("kota");
                                    } else if (valSpinProv.has("city")) {
                                        idKabKot = valSpinProv.getInt("city");
                                    } else if (valSpinProv.has("kabupatenkota")) {
                                        idKabKot = valSpinProv.getInt("kabupatenkota");
                                    } else if (valSpinProv.has("kotakabupaten")) {
                                        idKabKot = valSpinProv.getInt("kotakabupaten");
                                    } else if (valSpinProv.has("districtcity")) {
                                        idKabKot = valSpinProv.getInt("districtcity");
                                    } else if (valSpinProv.has("citydistrict")) {
                                        idKabKot = valSpinProv.getInt("citydistrict");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    String idSpin2 = String.valueOf(idKabKot);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin).replace(":id_kabupaten",idSpin2);
                                    if (idKabKot != 0) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            }
                            else if ((nameDataEl.contains("kecamatan") || nameDataEl.contains("subdistrict")) && (getnameDataEl.contains("kelurahan") || getnameDataEl.contains("urbanvillage") || getnameDataEl.contains("desa") || getnameDataEl.contains("village"))) {
                                if (!urlPath.isEmpty()) {
                                    int idProv = 0;
                                    if (valSpinProv.has("provinsi")) {
                                        idProv = valSpinProv.getInt("provinsi");
                                    } else if (valSpinProv.has("province")) {
                                        idProv = valSpinProv.getInt("province");
                                    }
                                    int idKabKot = 0;
                                    if (valSpinProv.has("kabupaten")) {
                                        idKabKot = valSpinProv.getInt("kabupaten");
                                    } else if (valSpinProv.has("district")) {
                                        idKabKot = valSpinProv.getInt("district");
                                    } else if (valSpinProv.has("kota")) {
                                        idKabKot = valSpinProv.getInt("kota");
                                    } else if (valSpinProv.has("city")) {
                                        idKabKot = valSpinProv.getInt("city");
                                    } else if (valSpinProv.has("kabupatenkota")) {
                                        idKabKot = valSpinProv.getInt("kabupatenkota");
                                    } else if (valSpinProv.has("kotakabupaten")) {
                                        idKabKot = valSpinProv.getInt("kotakabupaten");
                                    } else if (valSpinProv.has("districtcity")) {
                                        idKabKot = valSpinProv.getInt("districtcity");
                                    } else if (valSpinProv.has("citydistrict")) {
                                        idKabKot = valSpinProv.getInt("citydistrict");
                                    }
                                    int idKec = 0;
                                    if (valSpinProv.has("kecamatan")) {
                                        idKec = valSpinProv.getInt("kecamatan");
                                    } else if (valSpinProv.has("subdistrict")) {
                                        idKec = valSpinProv.getInt("subdistrict");
                                    }
                                    String idSpin = String.valueOf(idProv);
                                    String idSpin2 = String.valueOf(idKabKot);
                                    String idSpin3 = String.valueOf(idKec);
                                    String urlNew = urlPath.replace(":id_provinsi",idSpin).replace(":id_kabupaten",idSpin2).replace(":id_kecamatan",idSpin3);

                                    if (idKec != 0) {
                                        RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                        if (rl.getChildAt(0) instanceof Spinner) {
                                            Spinner spin = (Spinner) rl.getChildAt(0);
                                            processGetDynamicURL(spin, urlNew, getnameDataEl);
                                        }
                                    }
                                }
                            }
                            else {
                                flagStuckSpin = false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void chooseFromSD() {
        picturePath = "";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUESTCODE_GALLERY);
    }

    private void barcodeDecoder(Uri selectedImage) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null)
            {
                return;
            }
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();

            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            Result results = reader.decode(bBitmap);

            if (uploadImageListener != null) {
                uploadImageListener.onClickUpload(selectedImage,rv_item.findViewHolderForAdapterPosition(intPos));
                RecyclerView.ViewHolder viewHolderForAdapterPosition = rv_item.findViewHolderForAdapterPosition(intPos);
                TextView tvContent = (TextView) viewHolderForAdapterPosition.itemView.findViewById(R.id.tvContentQr);
                nameItemQR.set(intPos,tvContent.getText().toString());
            }

            GetBarcodeData(results);
        } catch (FileNotFoundException | NotFoundException e) {
            messageBarcodeFailed();
            e.printStackTrace();
        }
    }

    private void GetBarcodeData(Result results) {
        String resulText = results.getText();
        if (resulText.contains("http")) {
            if (isSessionZoom) {
                BaseMeetingActivity.showProgress(true);
            } else {
                DipsSwafoto.showProgress(true);
            }
            getBarcodeDataByURL(results);
        }
    }

    private void getBarcodeDataByURL(Result results) {
        flagHitAPIForm = false;
        String urlPath = results.getText();
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();

        Server.getAPIService().getDynamicUrl(urlPath,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    flagData = 1;
                    String dataS = response.body().toString();
                    dataObjTrx = new JSONObject();
                    try {
                        JSONObject dataObjBody = new JSONObject(dataS);
                        JSONObject dataBody1 = dataObjBody.getJSONObject("data");
                        String retrievalReferenceNumber = dataBody1.getString("retrievalReferenceNumber");
                        String systemTraceAuditNumber = dataBody1.getString("systemTraceAuditNumber");
                        JSONObject dataBank = dataBody1.getJSONObject("data");
                        String noForm = dataBody1.getString("noForm");

                        String bankpenerima = "";
                        String rekeningpenerima = "";
                        String nominaltransaksi = "";
                        String jenis_layanan = "";
                        String tujuantransaksi = "";
                        String berita = "";
                        destAccount = "";
                        noRekSource = "";

                        if (dataBank.has("bankpenerima")) {
                            bankpenerima = dataBank.getString("bankpenerima");
                        }
                        if (dataBank.has("rekeningpenerima")) {
                            rekeningpenerima = dataBank.getString("rekeningpenerima");
                        }
                        if (dataBank.has("nominaltransaksi")) {
                            nominaltransaksi = dataBank.getString("nominaltransaksi");
                        }
                        if (dataBank.has("jenislayanan")) {
                            jenis_layanan = dataBank.getString("jenislayanan");
                        }
                        if (dataBank.has("tujuantransaksi")) {
                            tujuantransaksi = dataBank.getString("tujuantransaksi");
                        }
                        if (dataBank.has("berita")) {
                            berita = dataBank.getString("berita");
                        }
                        //String jenispenduduk = dataBank.getString("jenispenduduk");

                        JSONObject dataObjEl = new JSONObject();
                        for (int j = 0; j < idElement.length(); j++) {
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            if (nameDataEl.contains("bank") && nameDataEl.contains("penerima")) {
                                dataObjEl.put(nameDataEl,bankpenerima);
                            }
                            else if (nameDataEl.contains("rekening") && nameDataEl.contains("penerima")) {
                                dataObjEl.put(nameDataEl,rekeningpenerima);
                            }
                            else if (nameDataEl.contains("nama") && nameDataEl.contains("penerima")) {
                                //dataObjEl.put(nameDataEl,namapenerima);
                            }
                            else if (nameDataEl.contains("nominal") && nameDataEl.contains("transaksi")) {
                                dataObjEl.put(nameDataEl,nominaltransaksi);
                            }
                            else if (nameDataEl.contains("jenis") && nameDataEl.contains("layanan")) {
                                dataObjEl.put(nameDataEl,jenis_layanan);
                            }
                            else if (nameDataEl.contains("tujuan") && nameDataEl.contains("transaksi")) {
                                dataObjEl.put(nameDataEl,tujuantransaksi);
                            }
                            else if (nameDataEl.contains("jenis") && nameDataEl.contains("penduduk")) {
                                //dataObjEl.put(nameDataEl,jenispenduduk);
                            }
                            else if (nameDataEl.contains("berita")) {
                                dataObjEl.put(nameDataEl,berita);
                            }
                        }


                        dataObjTrx.put("label","qr");
                        dataObjTrx.put("idGenerateForm",formId);
                        dataObjTrx.put("noForm",noForm);
                        dataObjTrx.put("systemTraceAuditNumber",systemTraceAuditNumber);
                        dataObjTrx.put("retrievalReferenceNumber",retrievalReferenceNumber);
                        dataObjTrx.put("data",dataObjEl);
                        dataTrxArr.put(dataObjTrx);

                        noFormQR.add(noForm);

                        setRecylerPager();
                        if (llLayout.getVisibility() != View.VISIBLE) {
                            llLayout.setVisibility(View.VISIBLE);
                        }
                        selected_position = dataTrxArr.length() - 1;
                        processNihilDataForm();
                        recyclerViewAdapterPager.notifyItemChanged(selected_position);
                        edNamePenerima.setText("");
                        try {
                            idElementMulti.put(selected_position,idElement);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if (isSessionZoom) {
                            JSONObject reqFormMirroring = dataReqFormMirroring();
                            dataTrxArrMirror.put(selected_position,reqFormMirroring);
                            mirrObj.put(labelTrx,dataTrxArrMirror);
                            mirrObj.put("activeIndex",selected_position);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                        }
                        btnContinue.setEnabled(true);
                        btnContinue.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (response.code() == 500) {
                        Toast.makeText(mContext,"QRCode Expired",Toast.LENGTH_LONG).show();
                        if (dataItems.size() > 0) {
                            if (noFormQR.size() > 0) {
                                noFormQR.remove(dataItems.size() - 1);
                            }
                            dataItems.remove(dataItems.size() - 1);
                            nameItemQR.remove(dataItems.size() - 1);
                            recyclerViewAdapter.notifyDataSetChanged();
                            if (dataItems.size() == 0) {
                                addData();
                                setRecyler();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
            }
        });
    }

    private void messageBarcodeFailed() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(mContext.getDrawable(R.drawable.v_dialog_warning));
        tvBodyDialog.setText(getString(R.string.qrcode_notmatch));

        SweetAlertDialog dialogEnd = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        dialogEnd.setCustomView(dialogView);
        dialogEnd.setCancelable(false);
        dialogEnd.hideConfirmButton();
        dialogEnd.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEnd.dismissWithAnimation();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_GALLERY_QRCODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                barcodeDecoder(selectedImage);
            }
        }
        else if (requestCode == REQUESTCODE_GALLERY){
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = mContext.getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);
            c.close();

            ExifInterface exif = null;
            int rotation = 0;
            try {
                exif = new ExifInterface(picturePath);
                rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (REQUESTCODE_GALLERY == 201) {

            } else {

            }
        }
    }

    private void sendDataFragment(Bundle bundle, Fragment fragment){
        fragment.setArguments(bundle);
        getFragmentPage(fragment);
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

    private void toFragmentMaintenance(){
        if (isSessionZoom) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, new frag_maintenance())
                    .addToBackStack("myFragMaintenance")
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, new frag_maintenance())
                    .addToBackStack("myFragMaintenance")
                    .commit();
        }

    }
}