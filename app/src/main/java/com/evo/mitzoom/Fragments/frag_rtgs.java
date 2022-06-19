package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterBank2;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Adapter.AdapterTypeService;
import com.evo.mitzoom.Model.BankItem;
import com.evo.mitzoom.Model.TypeServiceItem;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_rtgs extends Fragment {
    private ImageView btnBack;
    private EditText et_RekPenerima, et_NamaPenerima, et_Berita, et_Nominal;
    private Context context;
    private int posSourceBenefit, posSourcePopulation = -1;
    private AutoCompleteTextView et_source_account, et_NamaBank, et_serviceType, et_benefitRec, et_typePopulation;
    private SessionManager sessions;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    private List<BankItem> bankList;
    private List<TypeServiceItem> typeServiceList;
    String[] sourceBenefit;
    String[] sourcePopulation;
    private Button btnProses, btnAdd;
    private String RekeningSumber, NamaBank, RekPenerima, NamaPenerima, Nominal, JenisLayanan, PenerimaManfaat,JenisPenduduk,Berita;
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));
    private String dataRTGS, idDips;
    private LinearLayout choose_gallery;
    private MyViewPagerAdapter myViewPagerAdapter;
    private CircleIndicator circleIndicator;
    private ViewPager pager;
    private String getBerita = "";
    private String filename = "";
    private ArrayList<Integer> layouts = new ArrayList<Integer>();
    private ArrayList<String> dataAccount = new ArrayList<String>();
    private ArrayList<String> dataNoForm = new ArrayList<String>();
    private ArrayList<String> dataBankName = new ArrayList<String>();
    private ArrayList<String> dataAccountReceive = new ArrayList<>();
    private ArrayList<String> dataNameReceive = new ArrayList<>();
    private ArrayList<String> dataNominal = new ArrayList<>();
    private ArrayList<String> dataService = new ArrayList<>();
    private ArrayList<String> dataBenefit = new ArrayList<>();
    private ArrayList<String> dataPopulation = new ArrayList<>();
    private ArrayList<String> dataNews = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
        dataRTGS = sessions.getRTGS();
        Log.d("CEK","dataRTGS : "+dataRTGS);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_rtgs2, container, false);
        btnBack = view.findViewById(R.id.btn_back4);
        choose_gallery = (LinearLayout) view.findViewById(R.id.choose_gallery);
        pager = (ViewPager) view.findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        btnProses = view.findViewById(R.id.btnProsesRTGS);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layouts.add(R.layout.content_form_rtgs);
        dataNoForm.add("2103212");
        initPager();

        idDips = sessions.getKEY_IdDips();
        sourceBenefit = new String[]{getResources().getString(R.string.perorangan), getResources().getString(R.string.perusahaan), getResources().getString(R.string.pemerintah)};
        sourcePopulation = new String[]{getResources().getString(R.string.penduduk), getResources().getString(R.string.bukan_penduduk)};
        choose_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromSD();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring2(15,true);
                getFragmentPage(new frag_service());
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cekData = processSavedInstance();
                if (cekData) {
                    //Mirroring2(17,true);
                    Fragment fragment = new frag_summary_rtgs();
                    getFragmentPage(fragment);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int lens = layouts.size();
                int indexL = lens - 1;

                if (lens > 4) {
                    Toast.makeText(context,"Maksimal 5 Formulir.",Toast.LENGTH_LONG).show();
                    return;
                }
                layouts.add(R.layout.content_form_rtgs);
                int len = layouts.size();
                String no_form = dataNoForm.get(indexL);
                int intForm = Integer.valueOf(no_form) + 1;
                String NoForm = String.valueOf(intForm);
                dataNoForm.add(NoForm);

                initPager();
                int currPos = len - 1;
                pager.setCurrentItem(currPos);

                mirroringPagerRTGS(currPos);
            }
        });
    }

    private void initPager() {
        if (myViewPagerAdapter == null) {
            myViewPagerAdapter = new MyViewPagerAdapter();
        }
        pager.setAdapter(myViewPagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mirroringPagerRTGS(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        circleIndicator.setViewPager(pager);
    }

    private void mirroringPagerRTGS(int position) {
        //try {
            processSavedInstancePager(position);
            /*int len = 0;
            int idx;
            JSONArray jsArr = new JSONArray(dataRTGS);
            len = jsArr.length();
            idx = position + 1;
            String dataArr = jsArr.get(position).toString();
            JSONObject dataJs = new JSONObject(dataArr);
            String idForm = dataJs.getString("idForm");
            String sourceBank = dataJs.getString("sourceBank");
            String sourceTypeService = dataJs.getString("sourceTypeService");
            String sourceBenefit = dataJs.getString("sourceBenefit");
            String sourcePopulation = dataJs.getString("sourcePopulation");
            String rek_penerima = dataJs.getString("rek_penerima");
            String nama_penerima = dataJs.getString("nama_penerima");
            String nominal = dataJs.getString("nominal");
            String berita = dataJs.getString("berita");

            Mirroring(true, "", sourceBank, rek_penerima, nama_penerima, nominal,
                    sourceTypeService, sourceBenefit, sourcePopulation, berita, idx, len);*/
        /*} catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void savedRTGS() {
        Log.d("CEK","MASUK savedRTGS");
        try {
            JSONArray jsArr = new JSONArray(dataRTGS);
            int len = jsArr.length();
            int idx = 1;
            for (int i = 0; i < len; i++) {
                String dataArr = jsArr.get(i).toString();
                JSONObject dataJs = new JSONObject(dataArr);
                String idForm = dataJs.getString("idForm");
                String sourceBank = dataJs.getString("sourceBank");
                String sourceTypeService = dataJs.getString("sourceTypeService");
                String sourceBenefit = dataJs.getString("sourceBenefit");
                String sourcePopulation = dataJs.getString("sourcePopulation");
                String rek_penerima = dataJs.getString("rek_penerima");
                String nama_penerima = dataJs.getString("nama_penerima");
                String nominal = dataJs.getString("nominal");
                String berita = dataJs.getString("berita");

                if (i > 0) {
                    layouts.add(R.layout.content_form_rtgs);
                }

                if (i == 0) {
                    dataNoForm.set(i,idForm);
                } else {
                    dataNoForm.add(idForm);
                }
                dataBankName.add(sourceBank);
                dataAccountReceive.add(rek_penerima);
                dataNameReceive.add(nama_penerima);
                dataNominal.add(nominal);
                dataService.add(sourceTypeService);
                dataBenefit.add(sourceBenefit);
                dataPopulation.add(sourcePopulation);
                dataNews.add(berita);

                initPager();

                if (i == 0) {
                    Mirroring(true, "", sourceBank, rek_penerima, nama_penerima, nominal,
                            sourceTypeService, sourceBenefit, sourcePopulation, berita, idx, len);
                }

                idx++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String dataArrF(int index) {
        ArrayList<String> arrF = new ArrayList<>();
        arrF.add(getResources().getString(R.string.label_first));
        arrF.add(getResources().getString(R.string.label_second));
        arrF.add(getResources().getString(R.string.label_third));
        arrF.add(getResources().getString(R.string.label_fourth));
        arrF.add(getResources().getString(R.string.label_fifth));

        return  arrF.get(index).toString();
    }

    private boolean processSavedInstance() {
        int lenL = layouts.size();
        JSONArray jsonArray = new JSONArray();
        int idx = 1;
        for (int i = 0; i < lenL; i++) {
            JSONObject jsons = new JSONObject();
            try {
                String noFormulir = dataNoForm.get(i);

                if (dataAccount.size() == 0 || (dataAccount.size() == i) || (dataAccount.size() > 0 && dataAccount.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.source_account)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (dataBankName.size() == 0 || (dataBankName.size() == i) || (dataBankName.size() > 0 && dataBankName.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.BankReceive)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (dataAccountReceive.size() == 0 || (dataAccountReceive.size() == i) || (dataAccountReceive.size() > 0 && dataAccountReceive.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.ReceiverAccount2)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (dataNameReceive.size() == 0 || (dataNameReceive.size() == i) || (dataNameReceive.size() > 0 && dataNameReceive.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.ReceiverName2)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (dataNominal.size() == 0 || (dataNominal.size() == i) || (dataNominal.size() > 0 && dataNominal.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.Amount)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (dataService.size() == 0 || (dataService.size() == i) || (dataService.size() > 0 && dataService.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.jenis_layanan)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (dataBenefit.size() == 0 || (dataBenefit.size() == i) || (dataBenefit.size() > 0 && dataBenefit.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.penerima_manfaat)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (dataPopulation.size() == 0 || (dataPopulation.size() == i) || (dataPopulation.size() > 0 && dataPopulation.get(i).isEmpty())) {
                    Toast.makeText(context,"Data "+getResources().getString(R.string.ResidentType)+" "+
                            getResources().getString(R.string.alertRTGS)+" "+dataArrF(i),Toast.LENGTH_SHORT).show();
                    return false;
                }

                String SourceAccount = dataAccount.get(i);
                String SumberBank = dataBankName.get(i);
                String JenisLayanan = dataService.get(i);
                String posSourceBenefit = dataBenefit.get(i);
                String posSourcePopulation = dataPopulation.get(i);
                String rek_penerima = dataAccountReceive.get(i);
                String nama_penerima = dataNameReceive.get(i);
                String nominal = dataNominal.get(i);
                String berita = "";
                if (dataNews.size() == 0) {
                    berita = getBerita;
                } else {
                    if (dataNews.size() < i) {
                        int lenN = dataNews.size();
                        for (int k = lenN; k <= i; k++) {
                            dataNews.add(k, "");
                        }
                    }
                    if (dataNews.size() == i) {
                        berita = getBerita;
                    } else {
                        berita = dataNews.get(i);
                    }
                }

                jsons.put("idForm",noFormulir);
                jsons.put("sourceAccount",SourceAccount);
                jsons.put("sourceBank",SumberBank);
                jsons.put("sourceTypeService",JenisLayanan);
                jsons.put("sourceBenefit",posSourceBenefit);
                jsons.put("sourcePopulation",posSourcePopulation);
                jsons.put("rek_penerima",rek_penerima);
                jsons.put("nama_penerima",nama_penerima);
                jsons.put("nominal",nominal);
                jsons.put("berita",berita);

                Mirroring(true,SourceAccount,SumberBank,rek_penerima,nama_penerima,nominal,
                        JenisLayanan,posSourceBenefit,posSourcePopulation,berita,idx,lenL);

                idx++;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsons);
        }

        String dataJs = jsonArray.toString();
        sessions.saveRTGS(dataJs);
        return true;
    }

    private boolean processSavedInstancePager(int position) {
        int lenL = layouts.size();
        int idx = position + 1;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsons = new JSONObject();

        try {
            String noFormulir = dataNoForm.get(position);
            if (dataAccount.size() == 0 || (dataAccount.size() == position) || (dataAccount.size() > 0 && dataAccount.get(position).isEmpty())) {
                dataAccount.add("");
            }
            if (dataBankName.size() == 0 || (dataBankName.size() == position) || (dataBankName.size() > 0 && dataBankName.get(position).isEmpty())) {
                dataBankName.add("");
            }
            if (dataAccountReceive.size() == 0 || (dataAccountReceive.size() == position) || (dataAccountReceive.size() > 0 && dataAccountReceive.get(position).isEmpty())) {
                dataAccountReceive.add("");
            }
            if (dataNameReceive.size() == 0 || (dataNameReceive.size() == position) || (dataNameReceive.size() > 0 && dataNameReceive.get(position).isEmpty())) {
                dataNameReceive.add("");
            }
            if (dataNominal.size() == 0 || (dataNominal.size() == position) || (dataNominal.size() > 0 && dataNominal.get(position).isEmpty())) {
                dataNominal.add("0");
            }
            if (dataService.size() == 0 || (dataService.size() == position) || (dataService.size() > 0 && dataService.get(position).isEmpty())) {
                dataService.add("");
            }
            if (dataBenefit.size() == 0 || (dataBenefit.size() == position) || (dataBenefit.size() > 0 && dataBenefit.get(position).isEmpty())) {
                dataBenefit.add("");
            }
            if (dataPopulation.size() == 0 || (dataPopulation.size() == position) || (dataPopulation.size() > 0 && dataPopulation.get(position).isEmpty())) {
                dataPopulation.add("");
            }

            String SourceAccount = dataAccount.get(position);
            String SumberBank = dataBankName.get(position);
            String JenisLayanan = dataService.get(position);
            String posSourceBenefit = dataBenefit.get(position);
            String posSourcePopulation = dataPopulation.get(position);
            String rek_penerima = dataAccountReceive.get(position);
            String nama_penerima = dataNameReceive.get(position);
            String nominal = dataNominal.get(position);
            String berita = "";

            if (dataNews.size() == 0) {
                berita = getBerita;
                dataNews.add(berita);
            } else {
                if (dataNews.size() < position) {
                    int lenN = dataNews.size();
                    for (int k = lenN; k <= position; k++) {
                        dataNews.add(k, "");
                    }
                }
                if (dataNews.size() == position) {
                    berita = getBerita;
                } else {
                    berita = dataNews.get(position);
                }
            }

            jsons.put("idForm",noFormulir);
            jsons.put("sourceAccount",SourceAccount);
            jsons.put("sourceBank",SumberBank);
            jsons.put("sourceTypeService",JenisLayanan);
            jsons.put("sourceBenefit",posSourceBenefit);
            jsons.put("sourcePopulation",posSourcePopulation);
            jsons.put("rek_penerima",rek_penerima);
            jsons.put("nama_penerima",nama_penerima);
            jsons.put("nominal",nominal);
            jsons.put("berita",berita);

            Mirroring(false, SourceAccount, SumberBank, rek_penerima, nama_penerima, nominal,
                    JenisLayanan, posSourceBenefit, posSourcePopulation, berita, idx, lenL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jsons);

        String dataJs = jsonArray.toString();
        sessions.saveRTGS(dataJs);
        return true;
    }

    private void chooseFromSD() {
        Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2){
                Uri selectedImage = data.getData();
                getFilePath(selectedImage);
                barcodeDecoder(selectedImage);
            }
        }
    }

    private void getFilePath(Uri selectedImage) {
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor c = context.getContentResolver().query(selectedImage,filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();

        Log.d("CEK","picturePath : "+picturePath);
        int cut = picturePath.lastIndexOf('/');
        if (cut != -1) {
            filename = picturePath.substring(cut + 1);
        }

    }

    private void barcodeDecoder(Uri selectedImage) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null)
            {
                Log.e("CEK", "uri is not a bitmap," + selectedImage.toString());
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

            getSavedInstance(results);
        } catch (FileNotFoundException | NotFoundException e) {
            messageBarcodeFailed();
            e.printStackTrace();
        }
    }
    private void messageBarcodeFailed() {
        SweetAlertDialog sWA = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sWA.setContentText("File tidak Support");
        sWA.hideConfirmButton();
        sWA.setCancelText("Tutup");
        sWA.setCancelable(false);
        sWA.show();
    }
    private void getSavedInstance(Result results) {
        if (dataRTGS != null) {
            try {
                String resulText = results.getText();
                JSONArray jsArr = new JSONArray(dataRTGS);
                String dataArr = jsArr.get(0).toString();
                JSONObject dataJs = new JSONObject(dataArr);
                String idForm = dataJs.getString("idForm");

                if (resulText.equals(idForm)) {
                    savedRTGS();
                } else {
                    SweetAlertDialog sWA = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                    sWA.setContentText("Data tidak ditemukan");
                    sWA.hideConfirmButton();
                    sWA.setCancelText("Tutup");
                    sWA.setCancelable(false);
                    sWA.show();
                }

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
        typeServiceList.add(new TypeServiceItem("RTO", getResources().getString(R.string.rto_content)));
        typeServiceList.add(new TypeServiceItem("SKN",getResources().getString(R.string.skn_content)));
        typeServiceList.add(new TypeServiceItem("RTGS", getResources().getString(R.string.rtgs_content)));
    }
    private void Mirroring(boolean bool, CharSequence sumberRekening, CharSequence bank, CharSequence rekening, CharSequence nama, CharSequence nominal, CharSequence layanan, CharSequence manfaat, CharSequence penduduk, CharSequence berita, int page, int allpage ){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(filename);
            String dataRekening = sumberRekening.toString();
            if (dataRekening.indexOf("\n") > 0) {
                String[] sp = dataRekening.split("\n");
                String noRek = sp[0];
                String namaRek = sp[1];
                String valueRek = sp[2];
                jsonArray.put(noRek);
                jsonArray.put(namaRek);
                jsonArray.put(valueRek);
            } else {
                jsonArray.put(dataRekening);
            }
            jsonArray.put(bank);
            jsonArray.put(rekening);
            jsonArray.put(nama);
            jsonArray.put(nominal);
            jsonArray.put(layanan);
            jsonArray.put(manfaat);
            jsonArray.put(penduduk);
            jsonArray.put(berita);
            jsonArray.put(page);
            jsonArray.put(allpage);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",16);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring2(int nextCode, boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nextCode);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",nextCode);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d("CEK","MyViewPagerAdapter layouts : "+layouts.size()+" | position : "+position);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts.get(position), container, false);
            container.addView(view);

            iniatilizeElement(view,position);

            return view;
        }

        private void iniatilizeElement(View view, int position) {
            int positionE = position;
            int indexMirror = position + 1;

            LinearLayout llSourceAccount = (LinearLayout) view.findViewById(R.id.llSourceAccount);
            AutoCompleteTextView et_source_accountpager = (AutoCompleteTextView) view.findViewById(R.id.et_source_account);
            TextView tvNoFormulir = (TextView) view.findViewById(R.id.tvNoFormulir);
            AutoCompleteTextView et_nama_bank = (AutoCompleteTextView) view.findViewById(R.id.et_nama_bank);
            AutoCompleteTextView et_serviceType = (AutoCompleteTextView) view.findViewById(R.id.et_serviceType);
            AutoCompleteTextView et_benefitRec = (AutoCompleteTextView) view.findViewById(R.id.et_benefitRec);
            AutoCompleteTextView et_typePopulation = (AutoCompleteTextView) view.findViewById(R.id.et_typePopulation);
            EditText et_rek_penerima = (EditText) view.findViewById(R.id.et_rek_penerima);
            EditText et_nama_penerima = (EditText) view.findViewById(R.id.et_nama_penerima);
            EditText et_nominal = (EditText) view.findViewById(R.id.et_nominal);
            EditText et_berita = (EditText) view.findViewById(R.id.et_berita);

            llSourceAccount.setVisibility(View.VISIBLE);

            AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(context,R.layout.list_item_souceacc,sourceAcc);
            et_source_accountpager.setAdapter(adapterSourceAcc);
            et_source_accountpager.setBackground(context.getResources().getDrawable(R.drawable.blue_button_background));
            et_source_accountpager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //String selection = (String) parent.getItemAtPosition(position);
                    String dataB = et_source_accountpager.getText().toString();
                    if (dataAccount.size() == 0) {
                        dataAccount.add(positionE, dataB);
                    } else {
                        if (dataAccount.size() == positionE) {
                            dataAccount.add(positionE, dataB);
                        } else {
                            dataAccount.set(positionE, dataB);
                        }
                    }
                }
            });
            et_source_accountpager.addTextChangedListener(new TextWatcher() {
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
                    Mirroring(false,s,et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }


            });

            fillBankList();
            AdapterBank2 adapterBank2 = new AdapterBank2(context,bankList);
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
                    } else {
                        et_nama_bank.dismissDropDown();
                        if (dataBankName.size() > 0) {
                            String dataB = et_nama_bank.getText().toString();
                            if (dataBankName.size() == positionE) {
                                dataBankName.add(positionE, dataB);
                            } else {
                                dataBankName.set(positionE, dataB);
                            }
                            Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                                    et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                                    et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                        }
                    }
                }
            });
            et_nama_bank.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            });
            et_nama_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dataB = et_nama_bank.getText().toString();
                    if (dataBankName.size() == 0) {
                        dataBankName.add(positionE, dataB);
                    } else {
                        if (dataBankName.size() == positionE) {
                            dataBankName.add(positionE, dataB);
                        } else {
                            dataBankName.set(positionE, dataB);
                        }
                    }
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            });

            fillTypeServiceList();
            AdapterTypeService adapterTypeService = new AdapterTypeService(context,typeServiceList);
            et_serviceType.setAdapter(adapterTypeService);
            et_serviceType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        et_serviceType.dismissDropDown();
                        if (dataService.size() > 0) {
                            String dataB = et_serviceType.getText().toString();
                            if (dataService.size() == positionE) {
                                dataService.add(positionE, dataB);
                            } else {
                                dataService.set(positionE, dataB);
                            }
                            Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                                    et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                                    et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                        }
                    }
                }
            });
            et_serviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dataB = et_serviceType.getText().toString();
                    if (dataService.size() == 0) {
                        dataService.add(positionE, dataB);
                    } else {
                        if (dataService.size() == positionE) {
                            dataService.add(positionE, dataB);
                        } else {
                            dataService.set(positionE, dataB);
                        }
                    }
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            });

            ArrayAdapter<String> adapterBenefit = new ArrayAdapter<String>(context,R.layout.list_item, sourceBenefit);
            et_benefitRec.setAdapter(adapterBenefit);
            et_benefitRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dataB = et_benefitRec.getText().toString();
                    if (dataBenefit.size() == 0) {
                        dataBenefit.add(positionE, dataB);
                    } else {
                        if (dataBenefit.size() == positionE) {
                            dataBenefit.add(positionE, dataB);
                        } else {
                            dataBenefit.set(positionE, dataB);
                        }
                    }
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            });

            ArrayAdapter<String> adapterPopulation = new ArrayAdapter<String>(context,R.layout.list_item, sourcePopulation);
            et_typePopulation.setAdapter(adapterPopulation);
            et_typePopulation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dataB = et_typePopulation.getText().toString();
                    if (dataPopulation.size() == 0) {
                        dataPopulation.add(positionE, dataB);
                    } else {
                        if (dataPopulation.size() == positionE) {
                            dataPopulation.add(positionE, dataB);
                        } else {
                            dataPopulation.set(positionE, dataB);
                        }
                    }
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
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
                            if (dataAccountReceive.size() == positionE) {
                                dataAccountReceive.add(positionE,dataB);
                            } else {
                                dataAccountReceive.set(positionE, dataB);
                            }
                        }
                        Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                                et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                                et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());

                    }
                }
            });
            et_rek_penerima.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
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
                            if (dataNameReceive.size() == positionE) {
                                dataNameReceive.add(positionE, dataB);
                            } else {
                                dataNameReceive.set(positionE, dataB);
                            }
                        }
                        Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                                et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                                et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                    }
                }
            });
            et_nama_penerima.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
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
                            if (dataNominal.size() == positionE) {
                                dataNominal.add(positionE, dataB);
                            } else {
                                dataNominal.set(positionE, dataB);
                            }
                        }
                        Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                                et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                                et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                    }
                }
            });
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
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            });

            et_berita.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (dataNews.size() < positionE) {
                            int lenN = dataNews.size();
                            for (int i = lenN; i <= positionE; i++) {
                                dataNews.add(i, "");
                            }
                        }
                        String dataB = et_berita.getText().toString();
                        if (dataNews.size() == 0) {
                            dataNews.add(positionE, dataB);
                        } else {
                            if (dataNews.size() == positionE) {
                                dataNews.add(positionE, dataB);
                            } else {
                                dataNews.set(positionE, dataB);
                            }
                        }
                        Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                                et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                                et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                    }
                }
            });

            et_berita.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    et_berita.removeTextChangedListener(this);
                    String valB = et_berita.getText().toString();
                    et_berita.setText(valB);
                    getBerita = valB;
                    et_berita.setSelection(valB.length());
                    et_berita.addTextChangedListener(this);
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            });


            //============ View Get Data ========//
            tvNoFormulir.setText(dataNoForm.get(positionE));

            if (dataBankName.size() > 0) {
                if (positionE < dataBankName.size()) {
                    et_nama_bank.setText(dataBankName.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataAccountReceive.size() > 0) {
                if (positionE < dataAccountReceive.size()) {
                    et_rek_penerima.setText(dataAccountReceive.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataNameReceive.size() > 0) {
                if (positionE < dataNameReceive.size()) {
                    et_nama_penerima.setText(dataNameReceive.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataNominal.size() > 0) {
                if (positionE < dataNominal.size()) {
                    et_nominal.setText(dataNominal.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataService.size() > 0) {
                if (positionE < dataService.size()) {
                    et_serviceType.setText(dataService.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataBenefit.size() > 0) {
                if (positionE < dataBenefit.size()) {;
                    et_benefitRec.setText(dataBenefit.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataPopulation.size() > 0) {
                if (positionE < dataPopulation.size()) {
                    et_typePopulation.setText(dataPopulation.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
                }
            }

            if (dataNews.size() > 0) {
                if (positionE < dataNews.size()) {
                    et_berita.setText(dataNews.get(positionE));
                    Mirroring(false,et_source_accountpager.getText().toString(),et_nama_bank.getText().toString(),et_rek_penerima.getText().toString(),et_nama_penerima.getText().toString(),
                            et_nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),
                            et_typePopulation.getText().toString(),et_berita.getText().toString(),indexMirror,layouts.size());
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
