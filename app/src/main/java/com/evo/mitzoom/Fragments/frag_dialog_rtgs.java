package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.evo.mitzoom.Adapter.AdapterBank2;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Adapter.AdapterTypeService;
import com.evo.mitzoom.Model.BankItem;
import com.evo.mitzoom.Model.TypeServiceItem;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class frag_dialog_rtgs extends Fragment {

    private ImageView btnBack;
    private TextView tvCurr;
    private Button btnProsesRTGS, btnAdd;
    private Context mContext;
    private SessionManager sessions;
    private AutoCompleteTextView et_source_account, et_nama_bank, et_serviceType,et_benefitRec,et_typePopulation;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    String[] sourceBenefit = {"Perorangan", "Perusahaan", "Pemerintah"};
    String[] sourcePopulation = {"Penduduk", "Bukan Penduduk"};
    private JSONObject jsons;
    private int posSourceAccount, posSourceBank, posSourceTypeService, posSourceBenefit, posSourcePopulation = -1;
    private EditText et_rek_penerima;
    private EditText et_nama_penerima;
    private EditText et_nominal;
    private EditText et_berita;
    private List<BankItem> bankList;
    private List<TypeServiceItem> typeServiceList;
    public static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        jsons = new JSONObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dialog_rtgs, container, false);

        btnBack = (ImageView) view.findViewById(R.id.btn_back4);
        et_source_account = (AutoCompleteTextView) view.findViewById(R.id.et_source_account);
        et_nama_bank = (AutoCompleteTextView) view.findViewById(R.id.et_nama_bank);
        et_serviceType = (AutoCompleteTextView) view.findViewById(R.id.et_serviceType);
        et_benefitRec = (AutoCompleteTextView) view.findViewById(R.id.et_benefitRec);
        et_typePopulation = (AutoCompleteTextView) view.findViewById(R.id.et_typePopulation);
        et_rek_penerima = (EditText) view.findViewById(R.id.et_rek_penerima);
        et_nama_penerima = (EditText) view.findViewById(R.id.et_nama_penerima);
        et_nominal = (EditText) view.findViewById(R.id.et_nominal);
        et_berita = (EditText) view.findViewById(R.id.et_berita);
        btnProsesRTGS = (Button) view.findViewById(R.id.btnProsesRTGS);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        tvCurr = (TextView) view.findViewById(R.id.tvCurr);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnProsesRTGS.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_cif)));
        btnAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_schedule)));

        String lang = sessions.getLANG();
        if (lang.equals("en")) {
            tvCurr.setText("IDR");
        } else {
            tvCurr.setText("Rp.");
        }

        et_nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_nominal.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_nominal.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_nominal.setText(formatted);
                et_nominal.setSelection(formatted.length());
                et_nominal.addTextChangedListener(this);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_berita());
            }
        });

        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.list_item_souceacc,sourceAcc);
        et_source_account.setAdapter(adapterSourceAcc);
        et_source_account.setBackground(mContext.getResources().getDrawable(R.drawable.blue_button_background));
        et_source_account.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String selection = (String) parent.getItemAtPosition(position);
                posSourceAccount = position;
            }
        });

        et_source_account.addTextChangedListener(new TextWatcher() {
            String textContent = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textContent = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] strings = textContent.split("\\r?\\n");
                String titleAcc = strings[0]+"\n";
                s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleAcc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


        });

        fillBankList();
        AdapterBank2 adapterBank2 = new AdapterBank2(mContext,bankList);
        et_nama_bank.setAdapter(adapterBank2);
        et_nama_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourceBank = position;
            }
        });

        fillTypeServiceList();
        AdapterTypeService adapterTypeService = new AdapterTypeService(mContext,typeServiceList);
        et_serviceType.setAdapter(adapterTypeService);
        et_serviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourceTypeService = position;
            }
        });

        ArrayAdapter<String> adapterBenefit = new ArrayAdapter<String>(mContext,R.layout.list_item, sourceBenefit);
        et_benefitRec.setAdapter(adapterBenefit);
        et_benefitRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourceBenefit = position;
            }
        });

        ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(mContext,R.layout.list_item, sourcePopulation);
        et_typePopulation.setAdapter(adapterPopulation);
        et_typePopulation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourcePopulation = position;
            }
        });

        btnProsesRTGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSavedInstance();
            }
        });
    }

    @Override
    public void onDestroy() {
        processSavedInstance();
        super.onDestroy();
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static BigDecimal parseCurrencyValue(String value) {
        try {
            String replaceRegex = String.format("[%s,.\\s]", Objects.requireNonNull(numberFormat.getCurrency()).getDisplayName());
            String currencyValue = value.replaceAll(replaceRegex, "");
            return new BigDecimal(currencyValue);
        } catch (Exception e) {
            Log.e("MyApp", e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    private void processSavedInstance() {
        String rek_penerima = et_rek_penerima.getText().toString().trim();
        String nama_penerima = et_nama_penerima.getText().toString().trim();
        String nominal = et_nominal.getText().toString().trim();
        String berita = et_berita.getText().toString().trim();
        try {
            jsons.put("sourceAccount",posSourceAccount);
            jsons.put("sourceBank",posSourceBank);
            jsons.put("sourceTypeService",posSourceTypeService);
            jsons.put("sourceBenefit",posSourceBenefit);
            jsons.put("sourcePopulation",posSourcePopulation);
            jsons.put("rek_penerima",rek_penerima);
            jsons.put("nama_penerima",nama_penerima);
            jsons.put("nominal",nama_penerima);
            jsons.put("berita",berita);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String dataJs = jsons.toString();
        sessions.saveRTGS(dataJs);
    }
    private void fillBankList(){
        bankList = new ArrayList<>();
        bankList.add(new BankItem("BCA",R.drawable.bca));
        bankList.add(new BankItem("Mandiri",R.drawable.mandiri));
        bankList.add(new BankItem("BNI",R.drawable.bni));
        bankList.add(new BankItem("BRI",R.drawable.bri));
        bankList.add(new BankItem("CIMB Niaga",R.drawable.cimb));
        bankList.add(new BankItem("ANZ",R.drawable.anz));
        bankList.add(new BankItem("Bangkok Bank",R.drawable.bangkok_bank));
        bankList.add(new BankItem("IBK Bank",R.mipmap.dips361));
        bankList.add(new BankItem("Bank Amar",R.mipmap.dips361));
        bankList.add(new BankItem("Bank Artha Graha",R.mipmap.dips361));
        bankList.add(new BankItem("Bank Banten",R.mipmap.dips361));
        bankList.add(new BankItem("Bank Bengkulu",R.mipmap.dips361));
    }
    private void fillTypeServiceList(){
        typeServiceList = new ArrayList<>();
        typeServiceList.add(new TypeServiceItem("RTO", "Nominal transaksi minimal Rp. 50.000,00 dan maksimal Rp. 50.000.000,00"));
        typeServiceList.add(new TypeServiceItem("SKN","Nominal transaksi minimal Rp. 50.000,00 dan maksimal Rp. 1.000.000.000,00 pertransaksi"));
        typeServiceList.add(new TypeServiceItem("RTGS", "Nominal transaksi minimal Rp. 100.000.000,00 pertransaksi"));
    }
}