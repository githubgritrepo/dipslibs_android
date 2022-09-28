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
import android.text.TextWatcher;
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
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Constants.MyConstants;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_new_account_cs extends Fragment {
    private Context context;
    private ImageView btnBack;
    private LinearLayout icon_isi_form,choose_gallery;
    private String idDips,filename = "";
    private String rekSumberdana, nama, tgl, produk, nominal, rsd_1 = "", rsd_2 = "", rsd_3 = "", tipe_produk = "";
    private EditText et_nama, et_tgl_daftar, et_nominal_daftar;
    private AutoCompleteTextView et_productType, et_source_accountpager;
    String[] rektype;
    private SessionManager session;
    private int typeSend = 0;
    private Button btnProses;
    String [] sourceAcc = {"Tabungan DiPS Rupiah\n011043021 - Andi\nRp. 18.231,00", "Giro DiPS Rupiah\n021008120 - Andi\nRp. 15.000.000,00"};
    public static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_new_account_cs, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btn_back_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        choose_gallery = (LinearLayout) view.findViewById(R.id.choose_gallery2);
        et_nama = view.findViewById(R.id.et_nama_new_cs);
        et_tgl_daftar = view.findViewById(R.id.et_tgl_daftar_cs);
        et_nominal_daftar = view.findViewById(R.id.et_nominal_setoran_cs);
        et_productType = view.findViewById(R.id.et_productType_cs);
        et_source_accountpager = (AutoCompleteTextView) view.findViewById(R.id.et_source_account2);
        btnProses= view.findViewById(R.id.btnProses_isi_form_cs);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        rektype = new String[]{getResources().getString(R.string.saving_new_acc),getResources().getString(R.string.saving_new_acc2),getResources().getString(R.string.giro_new_acc),getResources().getString(R.string.deposito_new_acc),getResources().getString(R.string.asuransi_new_acc),getResources().getString(R.string.Reksadana_new_acc)};
        Bundle arg = getArguments();
        if (arg != null){
            rekSumberdana = arg.getString("rek_sumber_dana");
            nama = arg.getString("nama_lengkap");
            tgl = arg.getString("tgl");
            produk = arg.getString("produk");
            nominal = arg.getString("nominal");

            et_source_accountpager.setText(rekSumberdana);
            et_nama.setText(nama);
            et_productType.setText(produk);
            et_tgl_daftar.setText(tgl);
            et_nominal_daftar.setText(nominal);
        }
        else{
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
            tgl = df.format(c.getTime());
            et_tgl_daftar.setText(tgl);
        }
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mirroring("","","","","","","","", "",false,true);
                getFragmentPage(new frag_service());
            }
        });
        choose_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromSD();
            }
        });
        ArrayAdapter<String> adapterTypeProduct = new ArrayAdapter<String>(context,R.layout.list_item, rektype);
        et_productType.setAdapter(adapterTypeProduct);
        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(context,R.layout.list_item_souceacc,sourceAcc);
        et_source_accountpager.setAdapter(adapterSourceAcc);
        et_source_accountpager.setBackground(context.getResources().getDrawable(R.drawable.blue_button_background));
        Mirroring("","","","", et_nama.getText().toString(),et_productType.getText().toString(),et_tgl_daftar.getText().toString(),et_nominal_daftar.getText().toString(), "",false,false);
        textWatcher();
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rekSumberdana = et_source_accountpager.getText().toString();
                nama = et_nama.getText().toString();
                tgl = et_tgl_daftar.getText().toString();
                produk = et_productType.getText().toString();
                nominal = et_nominal_daftar.getText().toString();
                if (nama.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else if (produk.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else if (nominal.isEmpty()){
                    Toast.makeText(context, getResources().getString(R.string.error_field), Toast.LENGTH_SHORT).show();
                }
                else {
                    Mirroring("","","","","", "","","", "",true,false);
                    Fragment fragment;
                    fragment = new frag_new_account_cs2();
                    Bundle bundle = new Bundle();
                    bundle.putInt("typesend",typeSend);
                    bundle.putString("rek_sumber_dana",rekSumberdana);
                    bundle.putString("nama_lengkap",nama);
                    bundle.putString("tgl",tgl);
                    bundle.putString("produk",produk);
                    bundle.putString("nominal",nominal);
                    fragment.setArguments(bundle);
                    getFragmentPage(fragment);
                }
            }
        });
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
    private void textWatcher(){
        et_source_accountpager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sumber_dana = (String) adapterView.getItemAtPosition(i);
                String[] sumber = sumber_dana.split("\n");
                rsd_1 = sumber[0];
                rsd_2 = sumber[1];
                rsd_3 = sumber[2];
                Mirroring(filename,rsd_1,rsd_2,rsd_3, et_nama.getText().toString(),et_productType.getText().toString(),et_tgl_daftar.getText().toString(),et_nominal_daftar.getText(),"",false,false);
            }
        });
        et_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Mirroring(filename,rsd_1,rsd_2,rsd_3, charSequence,et_productType.getText().toString(),et_tgl_daftar.getText().toString(),et_nominal_daftar.getText(),"",false,false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_productType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tipe_produk = (String) adapterView.getItemAtPosition(i);
                Mirroring(filename,rsd_1,rsd_2,rsd_3, et_nama.getText().toString(), tipe_produk,et_tgl_daftar.getText().toString(),et_nominal_daftar.getText(),"",false,false);
            }
        });
        et_nominal_daftar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                et_nominal_daftar.removeTextChangedListener(this);
                BigDecimal parsed = parseCurrencyValue(et_nominal_daftar.getText().toString());
                String formatted = numberFormat.format(parsed);
                et_nominal_daftar.setText(formatted);
                et_nominal_daftar.setSelection(formatted.length());
                et_nominal_daftar.addTextChangedListener(this);
                Mirroring(filename,rsd_1,rsd_2,rsd_3, et_nama.getText().toString(), et_productType.getText().toString(),et_tgl_daftar.getText().toString(),et_nominal_daftar.getText().toString(),"",false,false);
            }
        });
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
            String resulText = results.getText();
            Log.d("CEK BARCODE",""+resulText);
            setBarcode(resulText);
        } catch (FileNotFoundException | NotFoundException e) {
            messageBarcodeFailed();
            e.printStackTrace();
        }
    }
    private void setBarcode(String url){
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.setBarcode(url);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    String dataS = response.body().toString();
                    typeSend = 1;
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int idForm = jsObj.getInt("id");
                        String namaLengkap = jsObj.getString("namaLengkap");
                        String tipeProduk = jsObj.getString("tipeProduk");
                        String tanggalPembukaan = jsObj.getString("tanggalPembukaan");
                        int setoranAwal = jsObj.getInt("setoranAwal");

                        et_nama.setText(namaLengkap);
                        et_productType.setText(tipeProduk);
                        et_tgl_daftar.setText(tanggalPembukaan);
                        BigDecimal parsed = parseCurrencyValue(String.valueOf(setoranAwal));
                        String formatted = numberFormat.format(parsed);
                        et_nominal_daftar.setText(formatted);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void messageBarcodeFailed() {
        SweetAlertDialog sWA = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sWA.setContentText("File tidak Support");
        sWA.hideConfirmButton();
        sWA.setCancelText("Tutup");
        sWA.setCancelable(false);
        sWA.show();
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
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void Mirroring(String filename_, CharSequence rek_sumber_1, CharSequence rek_sumber_2, CharSequence rek_sumber_3, CharSequence nama_lengkap,CharSequence produk_, CharSequence tgl_, CharSequence setoran_, String base, Boolean btnsubmit, Boolean btnback){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(filename_);
            jsonArray.put(rek_sumber_1);
            jsonArray.put(rek_sumber_2);
            jsonArray.put(rek_sumber_3);
            jsonArray.put(nama_lengkap);
            jsonArray.put(produk_);
            jsonArray.put(tgl_);
            jsonArray.put(setoran_);
            jsonArray.put(base);
            jsonArray.put(btnsubmit);
            jsonArray.put(btnback);
            jsons.put("idDips",idDips);
            jsons.put("code",362);
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
