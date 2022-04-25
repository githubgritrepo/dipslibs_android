package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.evo.mitzoom.Adapter.AdapterBank;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Adapter.AdapterTypeService;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsWaitingRoom;

public class frag_dialog_rtgs extends Fragment {

    private ImageView btnBack;
    private TextView tvCurr;
    private Button btnProsesRTGS, btnAdd;
    private Context mContext;
    private SessionManager sessions;
    private AutoCompleteTextView et_source_account, et_nama_bank, et_serviceType,et_benefitRec,et_typePopulation;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    String[] sourceBank = {"BCA", "Mandiri", "BNI", "BRI", "CIMB Niaga", "ANZ", "Bangkok Bank", "IBK Bank "};
    String[] sourceTypeService = {
            "RTO\nNominal transaksi minimal Rp. 50.000,00 dan maksimal Rp. 50.000.000,00",
            "SKN\nNominal transaksi minimal Rp. 50.000,00 dan maksimal Rp. 1.000.000.000,00 pertransaksi",
            "RTGS\nNominal transaksi minimal Rp. 100.000.000,00 pertransaksi"
    };
    String[] sourceBenefit = {"Perorangan", "Perusahaan", "Pemerintah"};
    String[] sourcePopulation = {"Penduduk", "Bukan Penduduk"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
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
                String selection = (String) parent.getItemAtPosition(position);
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

        AdapterBank adapterBank = new AdapterBank(mContext,R.layout.list_item2,sourceBank);
        et_nama_bank.setAdapter(adapterBank);

        AdapterTypeService adapterTypeService = new AdapterTypeService(mContext,R.layout.list_item3, sourceTypeService);
        et_serviceType.setAdapter(adapterTypeService);

        ArrayAdapter<String> adapterBenefit = new ArrayAdapter<String>(mContext,R.layout.list_item, sourceBenefit);
        et_benefitRec.setAdapter(adapterBenefit);

        ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(mContext,R.layout.list_item, sourcePopulation);
        et_typePopulation.setAdapter(adapterPopulation);

        btnProsesRTGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroy() {
        Log.d("CEK","MASUK DESTROY");
        super.onDestroy();
    }
}