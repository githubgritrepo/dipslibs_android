package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Model.BankItem;
import com.evo.mitzoom.Model.TypeServiceItem;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class FormRtgs extends Fragment {

    private ImageView btnBack;
    private TextView tvNoFormulir;
    private Button btnAdd;
    private EditText et_rek_penerima;
    private EditText et_nama_penerima;
    private EditText et_nominal;
    private EditText et_berita;
    private AutoCompleteTextView et_source_account, et_nama_bank, et_serviceType,et_benefitRec,et_typePopulation;
    private TextView tvCurr;
    private Button btnProsesRTGS;
    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private ArrayList<Integer> layouts = new ArrayList<Integer>();
    private Context mContext;
    private MyViewPagerAdapter myViewPagerAdapter;
    private SessionManager sessions;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    String[] sourceBenefit = {"Perorangan", "Perusahaan", "Pemerintah"};
    String[] sourcePopulation = {"Penduduk", "Bukan Penduduk"};
    private int posSourceAccount = -1;
    private int posSourceBank = -1;
    private int posSourceTypeService = -1;
    private int posSourceBenefit = -1;
    private int posSourcePopulation = -1;
    private List<BankItem> bankList;
    private List<TypeServiceItem> typeServiceList;
    private ArrayList<String> noForm = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        layouts.add(R.layout.content_form_rtgs);
        noForm.add("2103212");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_rtgs, container, false);

        btnBack = (ImageView) view.findViewById(R.id.btn_back4);
        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPager();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_berita());
            }
        });
    }

    private void initPager() {
        if (myViewPagerAdapter == null) {
            myViewPagerAdapter = new MyViewPagerAdapter();
        }
        pager.setAdapter(myViewPagerAdapter);
        pager.addOnPageChangeListener(viewPagerPageChangeListener);
        circleIndicator.setViewPager(pager);
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

    private void processSavedInstance() {
        String noFormulir = tvNoFormulir.getText().toString().trim();
        String rek_penerima = et_rek_penerima.getText().toString().trim();
        String nama_penerima = et_nama_penerima.getText().toString().trim();
        String nominal = et_nominal.getText().toString().trim();
        String berita = et_berita.getText().toString().trim();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idForm",noFormulir);
            jsons.put("sourceAccount",posSourceAccount);
            jsons.put("sourceBank",posSourceBank);
            jsons.put("sourceTypeService",posSourceTypeService);
            jsons.put("sourceBenefit",posSourceBenefit);
            jsons.put("sourcePopulation",posSourcePopulation);
            jsons.put("rek_penerima",rek_penerima);
            jsons.put("nama_penerima",nama_penerima);
            jsons.put("nominal",nominal);
            jsons.put("berita",berita);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jsons);

        String dataJs = jsonArray.toString();
        sessions.saveRTGS(dataJs);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.d("CEK","onPageScrolled position : "+position+" | positionOffset : "+positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            Log.d("CEK","onPageSelected position : "+position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d("CEK","onPageScrollStateChangedn : "+state);
        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts.get(position), container, false);
            container.addView(view);

            iniatilizeElement(view);

            actionView();

            return view;
        }

        private void iniatilizeElement(View view) {
            tvNoFormulir = (TextView) view.findViewById(R.id.tvNoFormulir);
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
        }

        @Override
        public int getCount() {
            return layouts.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private void actionView() {
            Log.d("CEK","getCurrentItem : "+pager.getCurrentItem());
            int lens = layouts.size();
            tvNoFormulir.setText(noForm.get(lens-1));

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CEK","MASUK BUTTON ADD");
                    layouts.add(R.layout.content_form_rtgs);
                    int len = layouts.size();
                    String no_form = noForm.get(lens - 1);
                    int intForm = Integer.valueOf(no_form) + 1;
                    String NoForm = String.valueOf(intForm);
                    noForm.add(NoForm);
                    initPager();
                    pager.setCurrentItem(len-1);
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
        }
    }
}