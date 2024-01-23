package com.evo.mitzoom.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.ItemCatgProdMutualFund;
import com.evo.mitzoom.Adapter.ItemHistoryMutualFund;
import com.evo.mitzoom.Adapter.ItemProdMutualFundGrid;
import com.evo.mitzoom.Adapter.ItemSalesMutualFundGrid;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.ConnectionRabbitHttp;
import com.evo.mitzoom.Helper.JSONUtil;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_wm_transactions extends Fragment {

    private static final String TAG = "frag_wm_transactions";
    private Context mContext;
    private SessionManager sessions;
    private boolean isSessionZoom = false;
    private ImageView btnBack;
    private TextView tvtitleHead;
    private LinearLayout llMainMutualFund;
    private TextView tvValRiskProfile;
    private TextView title_manajemen_investasi;
    private RelativeLayout rlPurchase;
    private RelativeLayout rlSales;
    private RelativeLayout rlHistory;
    private TextView tvMutualFundSales;
    private RecyclerView rv_itemCatg;
    private LinearLayout llProdCatg;
    private EditText etSearch;
    private RelativeLayout rlFilter;
    private RecyclerView rv_itemProd;
    private RecyclerView rv_itemHistory;
    private LinearLayout llPenjualan;
    private RecyclerView rv_itemProdPasar;
    private LinearLayoutManager recylerViewLayoutManager;
    private ItemCatgProdMutualFund recyclerViewAdapter;
    private ItemProdMutualFundGrid recyclerProdViewAdapter;
    private ItemHistoryMutualFund recyclerHistoryWM;
    private ItemSalesMutualFundGrid recyclerSalesViewAdapter;
    private String labelserv = "";
    private JSONObject dataNasabahObj = null;
    private String nik = "";
    private JSONArray dataCatgProdRiskArr;
    private JSONArray dataCatgProdArr;
    private JSONArray dataItems = null;
    private String labelTrx = "";
    private JSONObject mirrObj;
    private int catg = 0;
    private int catgPrev = 0;
    private String categoryCode = "";
    private String dataRisk = "";
    private JSONArray filteredList;
    private RelativeLayout rlMenuFilter;
    private RadioGroup rgSortType;
    private RadioButton rbSortAsc;
    private RadioButton rbSortDesc;
    private Spinner spinManajerInvest;
    private Spinner spinImbalHasil;
    private LinearLayout llFilPurchase;
    private LinearLayout llFilHistory;
    private EditText etDates;
    private LinearLayout llImgClose;
    private Spinner spinTransaction;
    private Button btnApply;
    private String sort = "";
    private String manajInvest = "";
    private String imbalInvest = "";
    private String datesTrx = "";
    private String codeTypeTrx = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        
        if (getArguments() != null) {
            if (getArguments().containsKey("labelserv")) {
                labelserv = getArguments().getString("labelserv");
            }
            if (getArguments().containsKey("catg")) {
                catg = getArguments().getInt("catg");
            }
            if (getArguments().containsKey("catgPrev")) {
                catgPrev = getArguments().getInt("catgPrev");
            }
            if (getArguments().containsKey("categoryCode")) {
                categoryCode = getArguments().getString("categoryCode");
            }
            if (getArguments().containsKey("dataRisk")) {
                dataRisk = getArguments().getString("dataRisk");
            }
            if (getArguments().containsKey("filteredList")) {
                String dataFilter = getArguments().getString("filteredList");
                try {
                    filteredList = new JSONArray(dataFilter);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (getArguments().containsKey("sort")) {
                sort = getArguments().getString("sort");
            }
            if (getArguments().containsKey("manajInvest")) {
                manajInvest = getArguments().getString("manajInvest");
            }
            if (getArguments().containsKey("imbalInvest")) {
                imbalInvest = getArguments().getString("imbalInvest");
            }
            if (getArguments().containsKey("datesTrx")) {
                datesTrx = getArguments().getString("datesTrx");
            }
            if (getArguments().containsKey("codeTypeTrx")) {
                codeTypeTrx = getArguments().getString("codeTypeTrx");
            }
            if (getArguments().containsKey("dataCatgProdArr")){
                try {
                    JSONArray jsonArrAsli = new JSONArray(getArguments().getString("dataCatgProdArr"));
                    ArrayList<JSONObject> arrayBaru = new ArrayList<>();

                    for (int i = 0; i < jsonArrAsli.length(); i++){
                        JSONObject objectBaru = new JSONObject();
                        objectBaru.put("productCode",jsonArrAsli.getString(i));
                        arrayBaru.add(objectBaru);
                    }

                    dataCatgProdArr = new JSONArray(arrayBaru);
                    Log.e("productCode",""+arrayBaru);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        }


        ConnectionRabbitHttp.init(mContext);
        String dataNasabah = sessions.getNasabah();
        if (!dataNasabah.isEmpty()) {
            try {
                dataNasabahObj = new JSONObject(dataNasabah);
                /*if (dataNasabahObj.has("namaLengkap")) {
                    namaLengkap = dataNasabahObj.getString("namaLengkap");
                }
                if (dataNasabahObj.has("jenisKelamin")) {
                    jenisKelamin = dataNasabahObj.getString("jenisKelamin");
                }
                if (dataNasabahObj.has("noHp")) {
                    no_handphone = dataNasabahObj.getString("noHp");
                }*/
                if (dataNasabahObj.has("nik")) {
                    nik = dataNasabahObj.getString("nik");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.frag_wm_transactions, container, false);

        btnBack = (ImageView) views.findViewById(R.id.btnBack);
        tvtitleHead = (TextView) views.findViewById(R.id.tvtitleHead);

        llMainMutualFund = (LinearLayout) views.findViewById(R.id.llMainMutualFund);
        tvValRiskProfile = (TextView) views.findViewById(R.id.tvValRiskProfile);
        rlPurchase = (RelativeLayout) views.findViewById(R.id.rlPurchase);
        rlSales = (RelativeLayout) views.findViewById(R.id.rlSales);
        rlHistory = (RelativeLayout) views.findViewById(R.id.rlHistory);
        tvMutualFundSales = (TextView) views.findViewById(R.id.tvMutualFundSales);

        rv_itemCatg = (RecyclerView) views.findViewById(R.id.rv_itemCatg);
        title_manajemen_investasi = (TextView) views.findViewById(R.id.title_manajemen_investasi);

        llProdCatg = (LinearLayout) views.findViewById(R.id.llProdCatg);
        etSearch = (EditText) views.findViewById(R.id.etSearch);
        rlFilter = (RelativeLayout) views.findViewById(R.id.rlFilter);
        rv_itemProd = (RecyclerView) views.findViewById(R.id.rv_itemProd);
        rv_itemHistory = (RecyclerView) views.findViewById(R.id.rv_itemHistory);

        rlMenuFilter = (RelativeLayout) views.findViewById(R.id.rlMenuFilter);
        rgSortType = (RadioGroup) views.findViewById(R.id.rgSortType);
        rbSortAsc = (RadioButton) views.findViewById(R.id.rbSortAsc);
        rbSortDesc = (RadioButton) views.findViewById(R.id.rbSortDesc);
        llFilPurchase = (LinearLayout) views.findViewById(R.id.llFilPurchase);
        spinManajerInvest = (Spinner) views.findViewById(R.id.spinManajerInvest);
        spinImbalHasil = (Spinner) views.findViewById(R.id.spinImbalHasil);
        llFilHistory = (LinearLayout) views.findViewById(R.id.llFilHistory);
        etDates = (EditText) views.findViewById(R.id.etDates);
        llImgClose = (LinearLayout) views.findViewById(R.id.llImgClose);
        spinTransaction = (Spinner) views.findViewById(R.id.spinTransaction);
        btnApply = (Button) views.findViewById(R.id.btnApply);

        llPenjualan = (LinearLayout) views.findViewById(R.id.llPenjualan);
        rv_itemProdPasar = (RecyclerView) views.findViewById(R.id.rv_itemProdPasar);
        
        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("catg",""+catg);
        labelTrx = "reksadana";
        mirrObj = new JSONObject();

        if (catg > 0) {
            tvtitleHead.setText(labelserv);
            llMainMutualFund.setVisibility(View.GONE);
            if (catg == 1) {
                ConnectionRabbitHttp.mirroringEndpoint(286);
                rv_itemCatg.setVisibility(View.VISIBLE);
                title_manajemen_investasi.setVisibility(View.VISIBLE);
                llProdCatg.setVisibility(View.GONE);
                dataItems = new JSONArray();
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processGetManagementInvest();
                //processGetCatgProdRisk();
            }
            else if (catg == 2) {
                rv_itemCatg.setVisibility(View.GONE);
                title_manajemen_investasi.setVisibility(View.GONE);
                llProdCatg.setVisibility(View.VISIBLE);
                rv_itemProd.setVisibility(View.VISIBLE);
                dataItems = new JSONArray();
                filteredList = new JSONArray();
                setRecylerProd();
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                if (dataCatgProdArr.length() > 0) {
                    int loop = 0;
                    processProdWMSDetail(loop);
                } else {
                    JSONObject listproductsMirr = new JSONObject();
                    try {
                        listproductsMirr.put("listproducts", dataCatgProdArr);
                        mirrObj.put(labelTrx, listproductsMirr);
                        ConnectionRabbitHttp.mirroringKey(mirrObj);
                        DialogNullProduct();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
                //processGetProdByCatg();
            }
            else if (catg == 22) {
                sort = "asc";

                tvtitleHead.setText("Filter");
                rlMenuFilter.setVisibility(View.VISIBLE);
                if (catgPrev == 2) {
                    llFilPurchase.setVisibility(View.VISIBLE);
                    llFilHistory.setVisibility(View.GONE);
                    processFilter();
                } else {
                    llFilHistory.setVisibility(View.VISIBLE);
                    llFilPurchase.setVisibility(View.GONE);

                    Calendar currentTime = Calendar.getInstance();
                    if (!datesTrx.isEmpty()) {
                        String[] sp = datesTrx.split("-");
                        String tglTrx = sp[0];
                        String blnTrx = sp[1];
                        String thnTrx = sp[2];

                        currentTime.set(Integer.parseInt(thnTrx),Integer.parseInt(blnTrx)-1,Integer.parseInt(tglTrx));
                        etDates.setText(datesTrx);
                        llImgClose.setVisibility(View.VISIBLE);
                    }

                    DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            currentTime.set(Calendar.YEAR, year);
                            currentTime.set(Calendar.MONTH, month);
                            currentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            String Tgl = String.format("%02d", dayOfMonth);
                            String bln = String.format("%02d", month + 1);

                            String getDates = Tgl + "-" + bln + "-" + year;
                            etDates.setText(getDates);
                            llImgClose.setVisibility(View.VISIBLE);
                        }
                    };

                    llImgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etDates.setText("");
                            llImgClose.setVisibility(View.GONE);
                        }
                    });

                    etDates.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new DatePickerDialog(mContext, dateDialog, currentTime
                                    .get(Calendar.YEAR), currentTime.get(Calendar.MONTH),
                                    currentTime.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });

                    processFilterHistoryWM();
                }

            }
            else if (catg == 3) {
                rv_itemCatg.setVisibility(View.GONE);
                title_manajemen_investasi.setVisibility(View.GONE);
                llPenjualan.setVisibility(View.VISIBLE);
                dataItems = new JSONArray();
                setRecylerSalesProd();
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processGetPortoWM();
            } else if (catg == 4) {
                rv_itemCatg.setVisibility(View.GONE);
                title_manajemen_investasi.setVisibility(View.GONE);
                llProdCatg.setVisibility(View.VISIBLE);
                rv_itemProd.setVisibility(View.VISIBLE);
                dataItems = new JSONArray();
                filteredList = new JSONArray();
                setRecylerProd();
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(true);
                } else {
                    DipsSwafoto.showProgress(true);
                }
                processHistoryTransWM();
            }
        }
        else {
            ConnectionRabbitHttp.mirroringEndpoint(285);

            tvtitleHead.setText(labelserv);
            llMainMutualFund.setVisibility(View.VISIBLE);
            rv_itemCatg.setVisibility(View.GONE);
            title_manajemen_investasi.setVisibility(View.GONE);
            if (isSessionZoom) {
                BaseMeetingActivity.showProgress(true);
            } else {
                DipsSwafoto.showProgress(true);
            }
            processGetRiskProfile();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (catg > 0) {
                    if (catg != 22) {
                        if ((catg == 2 || catg == 4)  && getActivity().getSupportFragmentManager().getBackStackEntryCount() > 6) {
                            if (catg == 4) {
                                FragmentManager.BackStackEntry stack = getActivity().getSupportFragmentManager().getBackStackEntryAt(4);
                                getActivity().getSupportFragmentManager().popBackStack(stack.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            } else {
                                FragmentManager.BackStackEntry stack = getActivity().getSupportFragmentManager().getBackStackEntryAt(5);
                                getActivity().getSupportFragmentManager().popBackStack(stack.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        } else {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    } else {
                        getActivity().getSupportFragmentManager().popBackStack("SearchPurchase", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                } else {
                    ConnectionRabbitHttp.mirroringEndpoint(280);
                    getActivity().getSupportFragmentManager().popBackStack("FragService", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                query = query.toString().toLowerCase();

                filteredList = new JSONArray();
                if (dataItems != null) {
                    for (int i = 0; i < dataItems.length(); i++) {

                        try {
                            JSONObject dataObj = dataItems.getJSONObject(i);

                            if (dataObj.has("productName")) {
                                String productName = dataObj.getString("productName").toLowerCase();
                                if (productName.contains(query)) {
                                    filteredList.put(dataItems.getJSONObject(i));
                                }
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    recylerViewLayoutManager.removeAllViews();
                    setRecylerProd();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rgSortType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rbSortAsc:
                        sort = "asc";
                        break;
                    case R.id.rbSortDesc:
                        sort = "desc";
                        break;
                }
            }
        });

        rlFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Fragment fragments = new frag_wm_transactions();
                bundle.putString("labelserv",labelserv);
                bundle.putInt("catg",22);
                bundle.putInt("catgPrev",catg);
                bundle.putString("dataRisk",dataRisk);
                bundle.putString("filteredList",dataItems.toString());
                bundle.putString("manajInvest", manajInvest);
                bundle.putString("imbalInvest", imbalInvest);
                bundle.putString("datesTrx", datesTrx);
                bundle.putString("codeTypeTrx", codeTypeTrx);
                fragments.setArguments(bundle);
                getFragmentPageBackStack(fragments,"SearchPurchase");
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("labelserv", labelserv);
                bundle.putString("categoryCode", categoryCode);
                bundle.putString("dataRisk", dataRisk);
                bundle.putString("sort", sort);

                if (catgPrev == 2) {
                    manajInvest = spinManajerInvest.getSelectedItem().toString();
                    imbalInvest = spinImbalHasil.getSelectedItem().toString();

                    if (manajInvest.equals(mContext.getResources().getString(R.string.choose_please))) {
                        manajInvest = "";
                    }

                    if (imbalInvest.equals(mContext.getResources().getString(R.string.choose_please))) {
                        imbalInvest = "";
                    }

                    bundle.putString("manajInvest", manajInvest);
                    bundle.putString("imbalInvest", imbalInvest);
                } else {
                    FormSpin dataSpin = (FormSpin) spinTransaction.getSelectedItem();
                    String codeTrx = dataSpin.getCode();
                    if (codeTrx.equals("0")) {
                        codeTrx = "";
                    }

                    datesTrx = etDates.getText().toString();

                    bundle.putString("datesTrx", datesTrx);
                    bundle.putString("codeTypeTrx", codeTrx);
                }

                bundle.putInt("catg",catgPrev);
                Fragment fragments = new frag_wm_transactions();
                fragments.setArguments(bundle);
                getFragmentPage(fragments);
            }
        });

        rlPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pembelian ReksaDana
                Bundle bundle = new Bundle();
                bundle.putString("labelserv",getString(R.string.pembelian_reksa_dana));
                bundle.putString("dataRisk",dataRisk);
                bundle.putInt("catg",1);
                Fragment fragments = new frag_wm_transactions();
                fragments.setArguments(bundle);
                getFragmentPage(fragments);
            }
        });

        rlSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("labelserv",tvMutualFundSales.getText().toString());
                bundle.putString("dataRisk",dataRisk);
                bundle.putInt("catg",3);
                Fragment fragments = new frag_wm_transactions();
                fragments.setArguments(bundle);
                getFragmentPage(fragments);
            }
        });

        rlHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("labelserv",mContext.getResources().getString(R.string.riwayat_transaksi));
                bundle.putString("dataRisk",dataRisk);
                bundle.putInt("catg",4);
                Fragment fragments = new frag_wm_transactions();
                fragments.setArguments(bundle);
                getFragmentPage(fragments);
            }
        });

    }

    private void processFilterHistoryWM() {
        ArrayList<FormSpin> dataDropDownSpin = new ArrayList<>();
        String textSelect = mContext.getResources().getString(R.string.choose_please);
        dataDropDownSpin.add(new FormSpin(0,"0",textSelect,textSelect));

        int posMatch = 0;

        for (int i = 0; i < filteredList.length(); i++) {
            try {
                JSONObject dataObjFilter = filteredList.getJSONObject(i);

                if (dataObjFilter.has("idCategory")) {
                    String idCategory = dataObjFilter.getString("idCategory");
                    String txt = "";
                    if (idCategory.equals("SUB")) {
                        txt = mContext.getResources().getString(R.string.purchase).toUpperCase();
                    } else if (idCategory.equals("RED")) {
                        txt = mContext.getResources().getString(R.string.penjualan).toUpperCase();
                    }

                    if (dataDropDownSpin.size() > 1) {
                        ArrayList<String> dataNameType = new ArrayList<>();
                        for (int k = 0; k < dataDropDownSpin.size(); k++) {
                            FormSpin dataSpin = (FormSpin) dataDropDownSpin.get(k);
                            String nameTrx = dataSpin.getName();
                            dataNameType.add(nameTrx);
                        }
                        if (!dataNameType.contains(txt)) {
                            dataDropDownSpin.add(new FormSpin(0,idCategory,txt,txt));
                        }
                    } else {
                        dataDropDownSpin.add(new FormSpin(0,idCategory,txt,txt));
                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayAdapter<FormSpin> adapterTrx = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDownSpin);
        spinTransaction.setAdapter(adapterTrx);
        if (!codeTypeTrx.isEmpty()) {
            for (int k = 0; k < dataDropDownSpin.size(); k++) {
                FormSpin dataSpin = (FormSpin) dataDropDownSpin.get(k);
                String codeTrx = dataSpin.getCode();
                if (codeTrx.equals(codeTypeTrx)) {
                    spinTransaction.setSelection(k);
                }
            }
        }
    }

    private void processFilter() {
        ArrayList<String> dataManInves = new ArrayList<>();
        ArrayList<String> dataImbalInves = new ArrayList<>();
        dataManInves.add(mContext.getResources().getString(R.string.choose_please));
        dataImbalInves.add(mContext.getResources().getString(R.string.choose_please));

        for (int i = 0; i < filteredList.length(); i++) {
            try {
                JSONObject dataObjFilter = filteredList.getJSONObject(i);
                if (dataObjFilter.has("fundHouseName")) {
                    String fundHouseName = dataObjFilter.getString("fundHouseName");
                    if (dataManInves.size() > 0) {
                        if (!dataManInves.contains(fundHouseName)) {
                            dataManInves.add(fundHouseName);
                        }
                    } else {
                        dataManInves.add(fundHouseName);
                    }
                }

                if (dataObjFilter.has("subsFee")) {
                    double subsFee = dataObjFilter.getDouble("subsFee");
                    String imbalHasil = "0% /Thn";
                    if (sessions.getLANG().equals("id")) {
                        imbalHasil = subsFee + "% /Thn";
                    } else {
                        imbalHasil = subsFee + "% /Years";
                    }

                    if (dataImbalInves.size() > 0) {
                        if (!dataImbalInves.contains(imbalHasil)) {
                            dataImbalInves.add(imbalHasil);
                        }
                    } else {
                        dataImbalInves.add(imbalHasil);
                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayAdapter<String> adapterManInves = new ArrayAdapter<String>(mContext, R.layout.dropdown_multiline, dataManInves);
        ArrayAdapter<String> adapterImbalInves = new ArrayAdapter<String>(mContext, R.layout.dropdown_multiline, dataImbalInves);

        spinManajerInvest.setAdapter(adapterManInves);
        spinImbalHasil.setAdapter(adapterImbalInves);

        if (!manajInvest.isEmpty()) {
            for (int k = 0; k < dataManInves.size(); k++) {
                String dataSpin = dataManInves.get(k);
                if (dataSpin.equals(manajInvest)) {
                    spinManajerInvest.setSelection(k);
                }
            }
        }

        if (!imbalInvest.isEmpty()) {
            for (int k = 0; k < dataImbalInves.size(); k++) {
                String dataSpin = dataImbalInves.get(k);
                if (dataSpin.equals(imbalInvest)) {
                    spinImbalHasil.setSelection(k);
                }
            }
        }
    }

    private void processGetCatgProdRisk() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetCategoryProductRisk(sessions.getLANG(),authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject dataBody = new JSONObject(response.body().toString());
                        dataCatgProdRiskArr = dataBody.getJSONArray("data");
                        int loop = 0;
                        parsingCatgProdRisk(loop);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
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
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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

    private void processGetManagementInvest() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetManajemenInvest(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject dataBody = new JSONObject(response.body().toString());
                        dataItems = dataBody.getJSONArray("data");
                        setRecyler();
                        recyclerViewAdapter.notifyDataSetChanged();
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
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
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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

    private void processHistoryTransWM() {
        try {
            JSONObject objReq = new JSONObject();
            objReq.put("cif", sessions.getNoCIF());
            objReq.put("status","ALL");
            objReq.put("beginDate", "");
            objReq.put("endDate", "");

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer " + sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().HistoryTranWM(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                            JSONObject objData = dataBody.getJSONObject("data");
                            filteredList = objData.getJSONArray("data");
                            dataItems = filteredList;

                            ConnectionRabbitHttp.mirroringEndpoint(310);

                            if (!sort.isEmpty()) {
                                filteredList = new JSONArray();
                            }
                            int loop = 0;

                            for (int k = 0; k < dataItems.length(); k++) {
                                JSONObject dataObj = dataItems.getJSONObject(k);

                                if (!sort.isEmpty()) {
                                    if (sort.equals("asc")) {
                                        dataItems = JSONUtil.sort(dataItems, new Comparator() {
                                            @Override
                                            public int compare(Object a, Object b) {
                                                JSONObject ja = (JSONObject) a;
                                                JSONObject jb = (JSONObject) b;
                                                return ja.optString("productName", "").toLowerCase().compareTo(jb.optString("productName", "").toLowerCase());
                                            }
                                        });
                                    } else if (sort.equals("desc")) {
                                        dataItems = JSONUtil.sort(dataItems, new Comparator() {
                                            @Override
                                            public int compare(Object a, Object b) {
                                                JSONObject ja = (JSONObject) a;
                                                JSONObject jb = (JSONObject) b;
                                                return jb.optString("productName", "").toLowerCase().compareTo(ja.optString("productName", "").toLowerCase());
                                            }
                                        });
                                    }

                                    if (!datesTrx.isEmpty() && !codeTypeTrx.isEmpty()) {
                                        if (dataObj.has("transactionDate")) {
                                            String transactionDate = dataObj.getString("transactionDate");
                                            if (transactionDate.contains("T")) {
                                                String[] sp = transactionDate.split("T");
                                                String dates = sp[0].trim();

                                                String inputFormat = "dd-MM-yyyy";
                                                String outputFormat = "yyyy-MM-dd";
                                                String newDatesTrx = ConveredDate(datesTrx, inputFormat, outputFormat);
                                                if (newDatesTrx.equals(dates)) {
                                                    if (dataObj.has("idCategory")) {
                                                        String idCategory = dataObj.getString("idCategory");
                                                        if (idCategory.equals(codeTypeTrx)) {
                                                            filteredList.put(loop, dataItems.getJSONObject(k));
                                                            loop++;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    } else if (!datesTrx.isEmpty() && codeTypeTrx.isEmpty()) {
                                        if (dataObj.has("transactionDate")) {
                                            String transactionDate = dataObj.getString("transactionDate");
                                            if (transactionDate.contains("T")) {
                                                String[] sp = transactionDate.split("T");
                                                String dates = sp[0].trim();

                                                String inputFormat = "dd-MM-yyyy";
                                                String outputFormat = "yyyy-MM-dd";
                                                String newDatesTrx = ConveredDate(dates, inputFormat, outputFormat);
                                                if (newDatesTrx.equals(dates)) {
                                                    filteredList.put(loop, dataItems.getJSONObject(k));
                                                    loop++;
                                                }
                                            }
                                        }
                                    } else if (datesTrx.isEmpty() && !codeTypeTrx.isEmpty()) {
                                        if (dataObj.has("idCategory")) {
                                            String idCategory = dataObj.getString("idCategory");
                                            if (idCategory.equals(codeTypeTrx)) {
                                                filteredList.put(loop, dataItems.getJSONObject(k));
                                                loop++;
                                            }
                                        }
                                    } else {
                                        filteredList.put(loop, dataItems.getJSONObject(k));
                                        loop++;
                                    }

                                } else {
                                    filteredList.put(k, dataItems.getJSONObject(k));
                                }

                            }

                            setRecylerProd();
                            recyclerHistoryWM.notifyDataSetChanged();

                            JSONObject listproductsMirr = new JSONObject();
                            listproductsMirr.put("history", filteredList);
                            mirrObj.put(labelTrx, listproductsMirr);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(true);
                    } else {
                        DipsSwafoto.showProgress(true);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    private void processGetPortoWM() {

        try {
            JSONObject objReq = new JSONObject();
            objReq.put("cif", sessions.getNoCIF());
            objReq.put("category", "");
            objReq.put("productCode", "");
            objReq.put("beginDate", "");
            objReq.put("endDate", "");
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer " + sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().GetPortoWM(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject objBody = new JSONObject(response.body().toString());
                            JSONObject objData = objBody.getJSONObject("data");
                            JSONObject objData2 = objData.getJSONObject("data");
                            dataCatgProdArr = objData2.getJSONArray("productList");
                            if (dataCatgProdArr.length() > 0) {
                                if (objData2.has("custRiskProfileType")) {
                                    dataRisk = objData2.getString("custRiskProfileType");
                                }

                                int loop = 0;
                                processProdWMSDetail(loop);
                            } else {
                                if (isSessionZoom) {
                                    BaseMeetingActivity.showProgress(false);
                                } else {
                                    DipsSwafoto.showProgress(false);
                                }
                                DialogNullProduct();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
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

    private void DialogNullProduct() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.icon_alert_wm));
        if (catg == 1 || catg == 2) {
            tvBodyDialog.setText(getString(R.string.product_wm_not_avail));
        } else {
            tvBodyDialog.setText(getString(R.string.unit_wm_not_avail));
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
    }

    private void processGetProdByCatg() {
        try {
            JSONObject objReq = new JSONObject();
            objReq.put("categoryCode",categoryCode);
            objReq.put("riskProfile",dataRisk);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().GetProdByCatg(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            dataCatgProdArr = dataBody.getJSONArray("data");

                            if (dataCatgProdArr.length() > 0) {
                                int loop = 0;
                                processProdWMSDetail(loop);
                            } else {
                                JSONObject listproductsMirr = new JSONObject();
                                listproductsMirr.put("listproducts", dataCatgProdArr);
                                mirrObj.put(labelTrx, listproductsMirr);
                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                if (isSessionZoom) {
                                    BaseMeetingActivity.showProgress(false);
                                } else {
                                    DipsSwafoto.showProgress(false);
                                }
                                DialogNullProduct();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
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
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void processProdWMSDetail(int loop) {
        try {
            JSONObject dataItemObj = dataCatgProdArr.getJSONObject(loop);
            String productCode = dataItemObj.getString("productCode");
            JSONObject objReq = new JSONObject();
            objReq.put("productCode",productCode);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objReq.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().ProdWMSDetail(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {

                        try {
                            JSONObject dataBody = new JSONObject(response.body().toString());
                            JSONObject dataObj = dataBody.getJSONObject("data");
                            for(Iterator<String> iter = dataObj.keys(); iter.hasNext();) {
                                if (iter.hasNext()) {
                                    String key = iter.next();
                                    if (dataObj.has(key) && !dataObj.isNull(key)) {
                                        dataItemObj.put(key,dataObj.get(key));
                                    }
                                }
                            }

                            dataItems.put(loop,dataItemObj);
                            Log.e("dataItems",""+dataItems.toString());
                            if (catg == 2) {
                                ConnectionRabbitHttp.mirroringEndpoint(287);
                                if (!sort.isEmpty()) {
                                    if (sort.equals("asc")) {
                                        dataItems = JSONUtil.sort(dataItems, new Comparator() {
                                            @Override
                                            public int compare(Object a, Object b) {
                                                JSONObject ja = (JSONObject) a;
                                                JSONObject jb = (JSONObject) b;
                                                return ja.optString("productName", "").toLowerCase().compareTo(jb.optString("productName", "").toLowerCase());
                                            }
                                        });
                                    } else if (sort.equals("desc")) {
                                        dataItems = JSONUtil.sort(dataItems, new Comparator() {
                                            @Override
                                            public int compare(Object a, Object b) {
                                                JSONObject ja = (JSONObject) a;
                                                JSONObject jb = (JSONObject) b;
                                                return jb.optString("productName", "").toLowerCase().compareTo(ja.optString("productName", "").toLowerCase());
                                            }
                                        });
                                    }

                                    if (!manajInvest.isEmpty() && !imbalInvest.isEmpty()) {
                                        String fundHouseName = dataObj.getString("fundHouseName").toLowerCase();
                                        double subsFee = dataObj.getDouble("subsFee");
                                        String imbalHasil = "0% /Thn";
                                        if (sessions.getLANG().equals("id")) {
                                            imbalHasil = subsFee + "% /Thn";
                                        } else {
                                            imbalHasil = subsFee + "% /Years";
                                        }
                                        if (fundHouseName.contains(manajInvest) && imbalHasil.contains(imbalInvest)) {
                                            filteredList.put(loop,dataItems.getJSONObject(loop));
                                        }
                                    } else if (!manajInvest.isEmpty() && imbalInvest.isEmpty()) {
                                        String fundHouseName = dataObj.getString("fundHouseName").toLowerCase();
                                        if (fundHouseName.contains(manajInvest)) {
                                            filteredList.put(loop,dataItems.getJSONObject(loop));
                                        }
                                    } else if (manajInvest.isEmpty() && !imbalInvest.isEmpty()) {
                                        double subsFee = dataObj.getDouble("subsFee");
                                        String imbalHasil = "0% /Thn";
                                        if (sessions.getLANG().equals("id")) {
                                            imbalHasil = subsFee + "% /Thn";
                                        } else {
                                            imbalHasil = subsFee + "% /Years";
                                        }
                                        if (imbalHasil.contains(imbalInvest)) {
                                            filteredList.put(loop,dataItems.getJSONObject(loop));
                                        }
                                    } else {
                                        filteredList.put(loop,dataItems.getJSONObject(loop));
                                    }

                                } else {
                                    filteredList.put(loop,dataItems.getJSONObject(loop));
                                }

                                JSONObject listproductsMirr = new JSONObject();
                                listproductsMirr.put("listproducts", filteredList);
                                mirrObj.put(labelTrx, listproductsMirr);
                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                recyclerProdViewAdapter.notifyDataSetChanged();
                            } else if (catg == 3) {
                                ConnectionRabbitHttp.mirroringEndpoint(300);
                                JSONObject portfolioMirr = new JSONObject();
                                portfolioMirr.put("portfolio", dataItems);
                                mirrObj.put(labelTrx, portfolioMirr);
                                ConnectionRabbitHttp.mirroringKey(mirrObj);
                                recyclerSalesViewAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        if (loop < dataCatgProdArr.length() - 1) {
                            int loopNew = loop + 1;
                            processProdWMSDetail(loopNew);
                        }

                        if (loop == dataCatgProdArr.length() - 1) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }
                    } else {
                        if (loop == dataItems.length() - 1) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }

                        if (response.code() == 500 || response.code() == 400) {
                            if (isSessionZoom) {
                                BaseMeetingActivity.showProgress(false);
                            } else {
                                DipsSwafoto.showProgress(false);
                            }
                        }

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
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (isSessionZoom) {
                        BaseMeetingActivity.showProgress(false);
                    } else {
                        DipsSwafoto.showProgress(false);
                    }
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void parsingCatgProdRisk(int loop) {
        try {
            int id = dataCatgProdRiskArr.getJSONObject(loop).getInt("id");
            processGetMediaCatg(id,loop);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetMediaCatg(int id, int loop) {

        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetMediaCatgRisk(id,authAccess,exchangeToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String labelProd = "";
                String categoryId = "";
                try {
                    labelProd = dataCatgProdRiskArr.getJSONObject(loop).getString("label");
                    categoryId = dataCatgProdRiskArr.getJSONObject(loop).getString("categoryId");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (response.isSuccessful()) {
                    String Content_Type = response.headers().get("Content-Type");
                    if (!Content_Type.contains("json")) {
                        InputStream in = response.body().byteStream();
                        if (Content_Type.contains("image")) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(in);
                                String base64Img = bitmapToString(bitmap);
                                JSONObject dataObjProd = new JSONObject();
                                dataObjProd.put("imgStreamStr",base64Img);
                                dataObjProd.put("labelProd",labelProd);
                                dataObjProd.put("categoryId",categoryId);
                                dataItems.put(dataObjProd);
                                recyclerViewAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    int loopNew = loop + 1;
                    if (loopNew < dataCatgProdRiskArr.length()) {
                        parsingCatgProdRisk(loopNew);
                    }
                    if (loop == dataCatgProdRiskArr.length() -1 ) {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                }
                else {
                    try {
                        JSONObject dataObjProd = new JSONObject();
                        dataObjProd.put("imgStreamStr","");
                        dataObjProd.put("labelProd",labelProd);
                        dataObjProd.put("categoryId",categoryId);
                        dataItems.put(dataObjProd);
                        recyclerViewAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    int loopNew = loop + 1;
                    if (loopNew < dataCatgProdRiskArr.length()) {
                        parsingCatgProdRisk(loopNew);
                    }
                    if (loop == dataCatgProdRiskArr.length() -1 ) {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private String bitmapToString(Bitmap in){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return Base64.encodeToString(bytes.toByteArray(),Base64.DEFAULT);
    }

    private void processGetRiskProfile() {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("nik",nik);
            jsons.put("bahasa",sessions.getLANG());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

            String authAccess = "Bearer "+sessions.getAuthToken();
            String exchangeToken = sessions.getExchangeToken();
            Server.getAPIService().GetRiskProfile(requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
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
                            String valueRiskProfile = dataBody.getString("data");
                            dataRisk = valueRiskProfile.toUpperCase();
                            tvValRiskProfile.setText(dataRisk);

                            JSONObject dataRiskObj = new JSONObject();
                            dataRiskObj.put("profilrisiko",dataRisk);
                            mirrObj.put(labelTrx, dataRiskObj);
                            ConnectionRabbitHttp.mirroringKey(mirrObj);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (isSessionZoom) {
                            BaseMeetingActivity.showProgress(false);
                        } else {
                            DipsSwafoto.showProgress(false);
                        }
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
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setRecyler() {
        int spanCount = 3; // columns
        int spacing = 20; // 20px
        boolean includeEdge = true;
        recylerViewLayoutManager = new GridLayoutManager(getContext(),spanCount);
        rv_itemCatg.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        rv_itemCatg.setLayoutManager(recylerViewLayoutManager);
        recyclerViewAdapter = new ItemCatgProdMutualFund(mContext,dataItems,dataRisk);
        rv_itemCatg.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private void setRecylerProd() {
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_itemProd.setLayoutManager(recylerViewLayoutManager);

        if (catg == 2) {
            recyclerProdViewAdapter = new ItemProdMutualFundGrid(mContext, filteredList, labelserv, catg);
            rv_itemProd.setAdapter(recyclerProdViewAdapter);
            recyclerProdViewAdapter.notifyDataSetChanged();
        } else if (catg == 4) {
            recyclerHistoryWM = new ItemHistoryMutualFund(mContext,filteredList);
            rv_itemProd.setAdapter(recyclerHistoryWM);
            recyclerHistoryWM.notifyDataSetChanged();
        }

    }

    private void setRecylerSalesProd() {
        recylerViewLayoutManager = new LinearLayoutManager(getContext());
        rv_itemProdPasar.setLayoutManager(recylerViewLayoutManager);

        recyclerSalesViewAdapter = new ItemSalesMutualFundGrid(mContext,dataItems,labelserv,catg);

        rv_itemProdPasar.setAdapter(recyclerSalesViewAdapter);
        recyclerSalesViewAdapter.notifyDataSetChanged();
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

    private void getFragmentPageBackStack(Fragment fragment,String nameBackStack){
        if (isSessionZoom) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame2, fragment)
                    .addToBackStack(nameBackStack)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_frame, fragment)
                    .addToBackStack(nameBackStack)
                    .commit();
        }

    }
}