package com.evo.mitzoom.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
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

import com.evo.mitzoom.Adapter.AdapterBank2;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Adapter.AdapterTypeService;
import com.evo.mitzoom.Model.BankItem;
import com.evo.mitzoom.Model.TypeServiceItem;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;

public class FormRtgs extends Fragment {

    private ImageView btnBack;
    private Button btnAdd;
    private Button btnProsesRTGS;
    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private ArrayList<Integer> layouts = new ArrayList<Integer>();
    private Context mContext;
    private MyViewPagerAdapter myViewPagerAdapter;
    private SessionManager sessions;
    private AdapterBank2 adapterBank2;
    String[] sourceBenefit;
    String[] sourcePopulation;
    private List<BankItem> bankList;
    private List<TypeServiceItem> typeServiceList;
    private ArrayList<String> dataNoForm = new ArrayList<String>();
    private ArrayList<String> dataBankName = new ArrayList<String>();
    private ArrayList<String> dataAccountReceive = new ArrayList<>();
    private ArrayList<String> dataNameReceive = new ArrayList<>();
    private ArrayList<String> dataNominal = new ArrayList<>();
    private ArrayList<String> dataService = new ArrayList<>();
    private ArrayList<Integer> dataBenefit = new ArrayList<>();
    private ArrayList<Integer> dataPopulation = new ArrayList<>();
    private ArrayList<String> dataNews = new ArrayList<>();
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        sessions = new SessionManager(mContext);

        layouts.add(R.layout.content_form_rtgs);
        dataNoForm.add("2103212");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_rtgs, container, false);

        btnBack = (ImageView) view.findViewById(R.id.btn_back4);
        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        btnProsesRTGS = (Button) view.findViewById(R.id.btnProsesRTGS);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);

        btnProsesRTGS.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_cif)));
        btnAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_schedule)));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("CEK","onViewCreated");

        sourceBenefit = new String[]{getResources().getString(R.string.perorangan), getResources().getString(R.string.perusahaan), getResources().getString(R.string.pemerintah)};
        sourcePopulation = new String[]{getResources().getString(R.string.penduduk), getResources().getString(R.string.bukan_penduduk)};

        initPager();

        btnProsesRTGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSavedInstance();
                generateBarcode(dataNoForm);
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_berita());
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int lens = layouts.size();
                int indexL = lens - 1;
                if (lens > 4) {
                    Toast.makeText(mContext,"Maksimal 5 Formulir.",Toast.LENGTH_LONG).show();
                    return;
                }
                layouts.add(R.layout.content_form_rtgs);
                int len = layouts.size();
                String no_form = dataNoForm.get(indexL);
                int intForm = Integer.valueOf(no_form) + 1;
                String NoForm = String.valueOf(intForm);
                dataNoForm.add(NoForm);

                initPager();
                pager.setCurrentItem(len - 1);
            }
        });
    }

    private void initPager() {
        Log.d("CEK","initPager");
        if (myViewPagerAdapter == null) {
            Log.d("CEK","myViewPagerAdapter NULL");
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
        int lenL = layouts.size();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsons = new JSONObject();

        for (int i = 0; i < lenL; i++) {
            try {
                String noFormulir = dataNoForm.get(i);
                String SumberBank = dataBankName.get(i);
                String JenisLayanan = dataService.get(i);
                int posSourceBenefit = dataBenefit.get(i);
                int posSourcePopulation = dataPopulation.get(i);
                String rek_penerima = dataAccountReceive.get(i);
                String nama_penerima = dataNameReceive.get(i);
                String nominal = dataNominal.get(i);
                String berita = dataNews.get(i);

                jsons.put("idForm",noFormulir);
                jsons.put("sourceBank",SumberBank);
                jsons.put("sourceTypeService",JenisLayanan);
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
        }

        String dataJs = jsonArray.toString();
        sessions.saveRTGS(dataJs);
    }

    private void generateBarcode(ArrayList<String> noFormList) {
        String no_Form = noFormList.get(0);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_barcode_rtgs, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setConfirmText("Download");
        sweetAlertDialog.setCancelText("Tutup");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.bg_cif));

        byte[] imgByte = null;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(no_Form, BarcodeFormat.QR_CODE,200,200);
            ImageView barcodeFormRTGS = (ImageView) dialogView.findViewById(R.id.barcodeFormRTGS);
            barcodeFormRTGS.setImageBitmap(bitmap);

            imgByte = imgtoByteArray(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        byte[] finalImgByte = imgByte;
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                    try {
                        String filename = "Barcode_No_Formulir-"+no_Form + ".jpg";
                        createTemporaryFile(finalImgByte, filename);

                        String appName = getString(R.string.app_name_dips);

                        String contents = "File disimpan di folder Pictures/" + appName + "/" + filename;

                        SweetAlertDialog sAW = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
                        sAW.setContentText(contents);
                        sAW.hideConfirmButton();
                        sAW.setCancelText("Tutup");
                        sAW.setCancelable(false);
                        sAW.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        String filename = "Barcode_No_Formulir-"+no_Form + ".jpg";
                        createTemporaryFile(finalImgByte, filename);

                        String appName = getString(R.string.app_name_dips);

                        String contents = "File disimpan di folder Pictures/" + appName + "/" + filename;

                        SweetAlertDialog sAW = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
                        sAW.setContentText(contents);
                        sAW.hideConfirmButton();
                        sAW.setCancelText("Tutup");
                        sAW.setCancelable(false);
                        sAW.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private byte[] imgtoByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    private File createTemporaryFile(byte[] byteImage, String filename) throws Exception {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.close();

        return mediaFile;
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
        typeServiceList.add(new TypeServiceItem("RTO", getResources().getString(R.string.rto_content)));
        typeServiceList.add(new TypeServiceItem("SKN",getResources().getString(R.string.skn_content)));
        typeServiceList.add(new TypeServiceItem("RTGS", getResources().getString(R.string.rtgs_content)));
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

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.d("CEK","onPageScrolled position : "+position+" | positionOffset : "+positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            Log.d("CEK","onPageSelected ke-"+position);
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
            Log.d("CEK","instantiateItem ke-"+position);
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts.get(position), container, false);
            container.addView(view);

            iniatilizeElement(view,position);

            return view;
        }

        private void iniatilizeElement(View view, int position) {
            int positionE = position;
            Log.d("CEK","iniatilizeElement : "+positionE);
            TextView tvNoFormulir = (TextView) view.findViewById(R.id.tvNoFormulir);
            AutoCompleteTextView et_nama_bank = (AutoCompleteTextView) view.findViewById(R.id.et_nama_bank);
            AutoCompleteTextView et_serviceType = (AutoCompleteTextView) view.findViewById(R.id.et_serviceType);
            AutoCompleteTextView et_benefitRec = (AutoCompleteTextView) view.findViewById(R.id.et_benefitRec);
            AutoCompleteTextView et_typePopulation = (AutoCompleteTextView) view.findViewById(R.id.et_typePopulation);
            EditText et_rek_penerima = (EditText) view.findViewById(R.id.et_rek_penerima);
            EditText et_nama_penerima = (EditText) view.findViewById(R.id.et_nama_penerima);
            EditText et_nominal = (EditText) view.findViewById(R.id.et_nominal);
            EditText et_berita = (EditText) view.findViewById(R.id.et_berita);

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

            fillBankList();
            adapterBank2 = new AdapterBank2(mContext,bankList);
            et_nama_bank.setAdapter(adapterBank2);
            et_nama_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        et_nama_bank.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                et_nama_bank.showDropDown();
                            }
                        }, 500);
                    }
                }
            });
            et_nama_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dataB = et_nama_bank.getText().toString();
                    if (dataBankName.size() == 0) {
                        dataBankName.add(positionE, dataB);
                    } else {
                        dataBankName.set(positionE, dataB);
                    }
                }
            });

            fillTypeServiceList();
            AdapterTypeService adapterTypeService = new AdapterTypeService(mContext,typeServiceList);
            et_serviceType.setAdapter(adapterTypeService);
            et_serviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dataB = et_serviceType.getText().toString();
                    if (dataService.size() == 0) {
                        dataService.add(positionE, dataB);
                    } else {
                        dataService.set(positionE, dataB);
                    }
                }
            });

            ArrayAdapter<String> adapterBenefit = new ArrayAdapter<String>(mContext,R.layout.list_item, sourceBenefit);
            et_benefitRec.setAdapter(adapterBenefit);
            et_benefitRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (dataBenefit.size() == 0) {
                        dataBenefit.add(positionE, position);
                    } else {
                        dataBenefit.set(positionE, position);
                    }
                }
            });

            ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(mContext,R.layout.list_item, sourcePopulation);
            et_typePopulation.setAdapter(adapterPopulation);
            et_typePopulation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (dataPopulation.size() == 0) {
                        dataPopulation.add(positionE, position);
                    } else {
                        dataPopulation.set(positionE, position);
                    }
                }
            });

            et_rek_penerima.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String dataB = et_rek_penerima.getText().toString();
                        if (dataAccountReceive.size() == 0) {
                            dataAccountReceive.add(positionE,dataB);
                        } else {
                            dataAccountReceive.set(positionE, dataB);
                        }

                    }
                }
            });

            et_nama_penerima.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String dataB = et_nama_penerima.getText().toString();
                        if (dataNameReceive.size() == 0) {
                            dataNameReceive.add(positionE, dataB);
                        } else {
                            dataNameReceive.set(positionE, dataB);
                        }
                    }
                }
            });

            et_nominal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String dataB = et_nominal.getText().toString();
                        if (dataNominal.size() == 0) {
                            dataNominal.add(positionE, dataB);
                        } else {
                            dataNominal.set(positionE, dataB);
                        }
                    }
                }
            });

            et_berita.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String dataB = et_berita.getText().toString();
                        if (dataNews.size() == 0) {
                            dataNews.add(positionE, dataB);
                        } else {
                            dataNews.set(positionE, dataB);
                        }
                    }
                }
            });


            //============ View Get Data ========//
            tvNoFormulir.setText(dataNoForm.get(positionE));
            if (dataBankName.size() > 0) {
                if (positionE < dataBankName.size()) {
                    et_nama_bank.setText(dataBankName.get(positionE));
                }
            }

            if (dataAccountReceive.size() > 0) {
                Log.d("CEK","dataAccountReceive : "+dataAccountReceive.size());
                if (positionE < dataAccountReceive.size()) {
                    et_rek_penerima.setText(dataAccountReceive.get(positionE));
                }
            }

            if (dataNameReceive.size() > 0) {
                if (positionE < dataNameReceive.size()) {
                    et_nama_penerima.setText(dataNameReceive.get(positionE));
                }
            }

            if (dataNominal.size() > 0) {
                if (positionE < dataNominal.size()) {
                    et_nominal.setText(dataNominal.get(positionE));
                }
            }

            if (dataService.size() > 0) {
                if (positionE < dataService.size()) {
                    et_serviceType.setText(dataService.get(positionE));
                }
            }

            if (dataBenefit.size() > 0) {
                if (positionE < dataBenefit.size()) {
                    int indexBenefit = dataBenefit.get(positionE);
                    if (indexBenefit > -1) {
                        et_benefitRec.setText(et_benefitRec.getAdapter().getItem(indexBenefit).toString(), false);
                    }
                }
            }

            if (dataPopulation.size() > 0) {
                if (positionE < dataPopulation.size()) {
                    int indexP = dataPopulation.get(positionE);
                    if (indexP > -1) {
                        et_typePopulation.setText(et_typePopulation.getAdapter().getItem(indexP).toString(), false);
                    }
                }
            }

            if (dataNews.size() > 0) {
                if (positionE < dataNews.size()) {
                    et_berita.setText(dataNews.get(positionE));
                }
            }
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
    }
}