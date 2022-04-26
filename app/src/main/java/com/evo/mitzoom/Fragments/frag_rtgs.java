package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class frag_rtgs extends Fragment {
    private ImageView btnBack;
    private EditText et_RekPenerima, et_NamaPenerima, et_Berita, et_Nominal;
    private Context context;
    private TextView tvCurr;
    private int posSourceAccount, posSourceBank, posSourceTypeService, posSourceBenefit, posSourcePopulation = -1;
    private AutoCompleteTextView et_source_account, et_NamaBank, et_serviceType, et_benefitRec, et_typePopulation;
    private SessionManager sessions;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    private List<BankItem> bankList;
    private List<TypeServiceItem> typeServiceList;
    String[] sourceBenefit = {"Perorangan", "Perusahaan", "Pemerintah"};
    String[] sourcePopulation = {"Penduduk", "Bukan Penduduk"};
    private Button btnProses;

    private String NamaBank, RekPenerima, NamaPenerima, Berita, Nominal;
    public static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private String dataRTGS;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        sessions = new SessionManager(context);
        dataRTGS = sessions.getRTGS();

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_rtgs, container, false);
        btnBack = view.findViewById(R.id.btn_back4);
        et_NamaBank = view.findViewById(R.id.et_nama_bank);
        et_RekPenerima = view.findViewById(R.id.et_rek_penerima);
        et_serviceType = (AutoCompleteTextView) view.findViewById(R.id.et_serviceType);
        et_NamaPenerima = view.findViewById(R.id.et_nama_penerima);
        et_typePopulation = (AutoCompleteTextView) view.findViewById(R.id.et_typePopulation);
        tvCurr = view.findViewById(R.id.tvCurr);
        et_Nominal = view.findViewById(R.id.et_nominal);
        et_benefitRec = (AutoCompleteTextView) view.findViewById(R.id.et_benefitRec);
        et_Berita = view.findViewById(R.id.et_berita);
        btnProses = view.findViewById(R.id.btnProsesRTGS);
        et_source_account = (AutoCompleteTextView) view.findViewById(R.id.et_source_account);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String lang = sessions.getLANG();
        if (lang.equals("en")) {
            tvCurr.setText("IDR");
        } else {
            tvCurr.setText("Rp.");
        }

        et_Nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_Nominal.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_Nominal.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_Nominal.setText(formatted);
                et_Nominal.setSelection(formatted.length());
                et_Nominal.addTextChangedListener(this);
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NamaBank = et_NamaBank.getText().toString();
                RekPenerima = et_RekPenerima.getText().toString();
                NamaPenerima = et_NamaPenerima.getText().toString();
                Nominal = et_Nominal.getText().toString();
                Berita = et_Berita.getText().toString();
                if(NamaBank.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (RekPenerima.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (NamaPenerima.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (Nominal.equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else if (Berita.trim().equals("")){
                    Toast.makeText(context, getResources().getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                }
                else {
                    Fragment fragment = new frag_summary_rtgs();
                    Bundle bundle = new Bundle();
                    bundle.putString("namaBank",NamaBank);
                    bundle.putString("rekPenerima",RekPenerima);
                    bundle.putString("namaPenerima",NamaPenerima);
                    bundle.putString("nominal",Nominal);
                    bundle.putString("berita",Berita);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_service());
            }
        });

        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(context,R.layout.list_item_souceacc,sourceAcc);
        et_source_account.setAdapter(adapterSourceAcc);
        et_source_account.setBackground(context.getResources().getDrawable(R.drawable.blue_button_background));
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
                Log.d("CEK","beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textContent = s.toString();
                Log.d("CEK","onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("CEK","afterTextChanged");
                String[] strings = textContent.split("\\r?\\n");
                String titleAcc = strings[0]+"\n";
                s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleAcc.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


        });

        fillBankList();
        AdapterBank2 adapterBank2 = new AdapterBank2(context,bankList);
        et_NamaBank.setAdapter(adapterBank2);
        et_NamaBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourceBank = position;
            }
        });

        fillTypeServiceList();
        AdapterTypeService adapterTypeService = new AdapterTypeService(context,typeServiceList);
        et_serviceType.setAdapter(adapterTypeService);
        et_serviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourceTypeService = position;
            }
        });

        ArrayAdapter<String> adapterBenefit = new ArrayAdapter<String>(context,R.layout.list_item, sourceBenefit);
        et_benefitRec.setAdapter(adapterBenefit);
        et_benefitRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourceBenefit = position;
            }
        });

        ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(context,R.layout.list_item, sourcePopulation);
        et_typePopulation.setAdapter(adapterPopulation);
        et_typePopulation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posSourcePopulation = position;
            }
        });

        getSavedInstance();
    }

    private void getSavedInstance() {
        if (dataRTGS != null) {
            try {

                JSONObject dataJs = new JSONObject(dataRTGS);
                posSourceAccount = dataJs.getInt("sourceAccount");
                posSourceBank = dataJs.getInt("sourceBank");
                posSourceTypeService = dataJs.getInt("sourceTypeService");
                posSourceBenefit = dataJs.getInt("sourceBenefit");
                posSourcePopulation = dataJs.getInt("sourcePopulation");
                String rek_penerima = dataJs.getString("rek_penerima");
                String nama_penerima = dataJs.getString("nama_penerima");
                String nominal = dataJs.getString("nominal");
                String berita = dataJs.getString("berita");

                if (posSourceAccount > -1) {
                    et_source_account.setText(et_source_account.getAdapter().getItem(posSourceAccount).toString(), false);
                    et_NamaBank.setText(et_NamaBank.getAdapter().getItem(posSourceBank).toString(), false);
                    et_serviceType.setText(et_serviceType.getAdapter().getItem(posSourceTypeService).toString(), false);
                    et_benefitRec.setText(et_benefitRec.getAdapter().getItem(posSourceBenefit).toString(), false);
                    et_typePopulation.setText(et_typePopulation.getAdapter().getItem(posSourcePopulation).toString(), false);
                }
                et_RekPenerima.setText(rek_penerima);
                et_NamaPenerima.setText(nama_penerima);
                et_Nominal.setText(nominal);
                et_Berita.setText(berita);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
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
