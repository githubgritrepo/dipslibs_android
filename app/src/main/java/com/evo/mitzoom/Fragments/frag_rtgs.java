package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private Button btnProses;
    private String RekeningSumber, NamaBank, RekPenerima, NamaPenerima, Nominal, JenisLayanan, PenerimaManfaat,JenisPenduduk,Berita;
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));
    private String dataRTGS, idDips;
    private LinearLayout choose_gallery;

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
        View view = inflater.inflate(R.layout.frag_form_rtgs, container, false);
        btnBack = view.findViewById(R.id.btn_back4);
        choose_gallery = (LinearLayout) view.findViewById(R.id.choose_gallery);
        et_NamaBank = view.findViewById(R.id.et_nama_bank);
        et_RekPenerima = view.findViewById(R.id.et_rek_penerima);
        et_serviceType = (AutoCompleteTextView) view.findViewById(R.id.et_serviceType);
        et_NamaPenerima = view.findViewById(R.id.et_nama_penerima);
        et_typePopulation = (AutoCompleteTextView) view.findViewById(R.id.et_typePopulation);
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
        idDips = sessions.getKEY_IdDips();
        sourceBenefit = new String[]{getResources().getString(R.string.perorangan), getResources().getString(R.string.perusahaan), getResources().getString(R.string.pemerintah)};
        sourcePopulation = new String[]{getResources().getString(R.string.penduduk), getResources().getString(R.string.bukan_penduduk)};
        choose_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromSD();
            }
        });
        et_Nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_Nominal.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_Nominal.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_Nominal.setText(formatted);
                et_Nominal.setSelection(formatted.length());
                et_Nominal.addTextChangedListener(this);
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),s,et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mirroring2(15,false);
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
                Mirroring(false,s,et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
            }


        });

        fillBankList();
        AdapterBank2 adapterBank2 = new AdapterBank2(context,bankList);
        et_NamaBank.setAdapter(adapterBank2);
        et_NamaBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        et_NamaBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),s,et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_RekPenerima.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),s,et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_NamaPenerima.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),s,et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fillTypeServiceList();
        AdapterTypeService adapterTypeService = new AdapterTypeService(context,typeServiceList);
        et_serviceType.setAdapter(adapterTypeService);
        et_serviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        et_serviceType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),s,et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        et_benefitRec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),s,et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        et_typePopulation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),s,et_Berita.getText().toString(),1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_Berita.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),s,1,1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RekeningSumber = et_source_account.getText().toString();
                NamaBank = et_NamaBank.getText().toString();
                RekPenerima = et_RekPenerima.getText().toString();
                NamaPenerima = et_NamaPenerima.getText().toString();
                Nominal = et_Nominal.getText().toString();
                JenisLayanan = et_serviceType.getText().toString();
                PenerimaManfaat = et_benefitRec.getText().toString();
                JenisPenduduk = et_typePopulation.getText().toString();
                Berita = et_Berita.getText().toString();

                if (RekeningSumber.isEmpty() || NamaBank.isEmpty() || RekPenerima.isEmpty() || NamaPenerima.isEmpty() || Nominal.isEmpty() || JenisLayanan.isEmpty() || PenerimaManfaat.isEmpty() || JenisPenduduk.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }
                else{
                    Mirroring(true,et_source_account.getText().toString(),et_NamaBank.getText().toString(),et_RekPenerima.getText().toString(),et_NamaPenerima.getText().toString(),et_Nominal.getText().toString(),et_serviceType.getText().toString(),et_benefitRec.getText().toString(),et_typePopulation.getText().toString(),et_Berita.getText().toString(),1,1);
                    Fragment fragment = new frag_summary_rtgs();
                    Bundle bundle = new Bundle();
                    bundle.putString("rekeningSumber",RekeningSumber);
                    bundle.putString("jenisLayanan",JenisLayanan);
                    bundle.putString("namaBank",NamaBank);
                    bundle.putString("namaPenerima",NamaPenerima);
                    bundle.putString("penerimaManfaat",PenerimaManfaat);
                    bundle.putString("jenisPenduduk",JenisPenduduk);
                    bundle.putString("berita",Berita);
                    bundle.putString("nominal",Nominal);
                    bundle.putString("rekPenerima",RekPenerima);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                }

            }
        });
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
                barcodeDecoder(selectedImage);
            }
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
                    String SourceBank = dataJs.getString("sourceBank");
                    String SourceTypeService = dataJs.getString("sourceTypeService");
                    String SourceBenefit = dataJs.getString("sourceBenefit");
                    String SourcePopulation = dataJs.getString("sourcePopulation");
                    String rek_penerima = dataJs.getString("rek_penerima");
                    String nama_penerima = dataJs.getString("nama_penerima");
                    String nominal = dataJs.getString("nominal");
                    String berita = "";
                    if (dataJs.has("berita")) {
                        if (!dataJs.isNull("berita")) {
                            berita = dataJs.getString("berita");
                        }
                    }

                    /*if (posSourceBenefit > -1) {
                        et_benefitRec.setText(et_benefitRec.getAdapter().getItem(posSourceBenefit).toString(), false);
                    }
                    if (posSourcePopulation > -1) {
                        et_typePopulation.setText(et_typePopulation.getAdapter().getItem(posSourcePopulation).toString(), false);
                    }*/

                    if (!SourceBenefit.isEmpty()){
                        et_benefitRec.setText(SourceBenefit);
                    }
                    if (!SourcePopulation.isEmpty()){
                        et_typePopulation.setText(SourcePopulation);
                    }
                    if (!SourceBank.isEmpty()){
                        et_NamaBank.setText(SourceBank);
                    }
                    if (!SourceTypeService.isEmpty()){
                        et_serviceType.setText(SourceTypeService);
                    }
                    if (!rek_penerima.isEmpty()) {
                        et_RekPenerima.setText(rek_penerima);
                    }
                    if (!nama_penerima.isEmpty()) {
                        et_NamaPenerima.setText(nama_penerima);
                    }
                    if (!nominal.isEmpty()) {
                        et_Nominal.setText(nominal);
                    }
                    if (!berita.isEmpty()) {
                        et_Berita.setText(berita);
                    }

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
            jsonArray.put(sumberRekening);
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
            jsons.put("code",15);
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
}
