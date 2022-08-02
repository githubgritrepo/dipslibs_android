package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_form_opening extends Fragment {
    private Context context;
    private ImageView preview_ktp, preview_npwp, preview_signature;
    private EditText Nama,NIK,Email,Alamat,Agama,Status,NoHp;
    private String Nama2,NIK2,Email2,Alamat2,Agama2,Status2,NoHp2;
    private String Produk = "";
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private Button btnProcess;
    private CheckBox pernyataan;
    private Boolean aBoolean;
    private String idDips;
    private SessionManager session;
    private byte[] KTP, NPWP, TTD;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int selectedId;
    private int idRb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_opening_account, container, false);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        radioGroup = view.findViewById(R.id.groupradio2);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        Nama = view.findViewById(R.id.et_nama);
        NIK = view.findViewById(R.id.et_nik);
        Email = view.findViewById(R.id.et_email);
        Alamat = view.findViewById(R.id.et_alamat);
        Agama = view.findViewById(R.id.et_agama);
        NoHp = view.findViewById(R.id.et_no_hp);
        Status = view.findViewById(R.id.et_status);
        btnProcess = view.findViewById(R.id.btnProses);
        preview_ktp = view.findViewById(R.id.Imageview_ktp);
        preview_npwp = view.findViewById(R.id.Imageview_npwp);
        preview_signature = view.findViewById(R.id.Imageview_tanda_tangan);
        pernyataan = view.findViewById(R.id.pernyataan);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));

        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        Log.d("CEK1","ktp "+KTP+" | npwp = "+NPWP+" | TTD = "+TTD);
        if (KTP == null && NPWP == null && TTD == null){
            KTP = session.getKEY_KTP().getBytes();
            NPWP = session.getKEY_NPWP().getBytes();
            TTD = session.getKEY_TTD().getBytes();
        }
        Log.d("CEK2","ktp "+KTP+" | npwp = "+NPWP+" | TTD = "+TTD);
        ByteArraytoimg(KTP,preview_ktp);
        ByteArraytoimg(NPWP,preview_npwp);
        ByteArraytoimg(TTD,preview_signature);


        Nama.setText("Andi Wijaya Lesmana");
        NIK.setText("320124150585005");
        Alamat.setText("JL RAYA CISEENG NO.15 BLOK G, RT 12, RW 16, Kel CIBENTANG, Kec CISEENG");
        Agama.setText("Islam");
        Status.setText("Belum Kawin");

        Nama2 = Nama.getText().toString();
        NIK2 = NIK.getText().toString();
        NoHp2 = NoHp.getText().toString();
        Email2 = Email.getText().toString();
        Alamat2 = Alamat.getText().toString();
        Agama2 = Agama.getText().toString();
        Status2 = Status.getText().toString();

        if (pernyataan.isChecked()){
            aBoolean = true;
        }
        else if (!pernyataan.isChecked()){
            aBoolean = false;
        }

        Mirroring(false,Nama2,NIK2,Email2,NoHp2,Alamat2,Agama2,Status2,Produk,aBoolean);

        Email.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9@._-]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });
        Status.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });
        Agama.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });
        //Text Watcher
        Nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false,s,NIK.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),Alamat.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Produk,aBoolean);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        NIK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false, Nama.getText().toString(), s,Email.getText().toString(),NoHp.getText().toString(),Alamat.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Produk,aBoolean);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        NoHp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false, Nama.getText().toString(),NIK.getText().toString(),Email.getText().toString(), s,Alamat.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Produk,aBoolean);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false, Nama.getText().toString(),NIK.getText().toString(),s,NoHp.getText().toString(),Alamat.getText().toString(),Agama.getText().toString(),Status.getText().toString(),Produk,aBoolean);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Alamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false, Nama.getText().toString(),NIK.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),s,Agama.getText().toString(),Status.getText().toString(),Produk,aBoolean);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Agama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false, Nama.getText().toString(),NIK.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),Alamat.getText().toString(),s,Status.getText().toString(),Produk,aBoolean);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Mirroring(false, Nama.getText().toString(),NIK.getText().toString(),Email.getText().toString(),NoHp.getText().toString(),Alamat.getText().toString(),Agama.getText().toString(), s,Produk,aBoolean);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedId = group.getCheckedRadioButtonId();
                radioButton = view.findViewById(selectedId);
                NoHp2 = NoHp.getText().toString();
                Email2 = Email.getText().toString();
                Log.d("RADIO", "Text : "+radioButton.getText());
                if (pernyataan.isChecked()){
                    Log.d("CEK","MASUK IF onCheckedChanged");
                    aBoolean = true;
                }
                else if (!pernyataan.isChecked()){
                    Log.d("CEK","MASUK ELSE IF onCheckedChanged");
                    aBoolean = false;
                }

                if (radioButton.isChecked()){
                    Log.d("CEK","MASUK ELSE IF 2 onCheckedChanged");
                    Produk = radioButton.getText().toString();
                }
                else if (!radioButton.isChecked()){
                    Log.d("CEK","MASUK ELSE IF 3 onCheckedChanged");
                    Produk = "";
                }
                Log.d("CEK","Produk : "+Produk);
                Log.d("CEK","aBoolean : "+aBoolean);
                Mirroring(false,Nama2,NIK2,Email2,NoHp2,Alamat2,Agama2,Status2,radioButton.getText(),aBoolean);
            }
        });
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoHp2 = NoHp.getText().toString();
                Email2 = Email.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (pernyataan.isChecked()){
                    Log.d("CEK","MASUK IF onClick pernyataan");
                    aBoolean = true;
                }
                else {
                    Log.d("CEK","MASUK ELSE IF onClick pernyataan");
                    aBoolean = false;
                }

                if (selectedId == -1){
                    Log.d("CEK","MASUK ELSE IF 2 onClick pernyataan");
                    Produk = "" ;
                }
                else {
                    Log.d("CEK","MASUK ELSE IF 3 onClick pernyataan");
                    Produk = radioButton.getText().toString();
                }
                Log.d("CEK","Produk : "+Produk);
                Log.d("CEK","aBoolean : "+aBoolean);
                Mirroring(false,Nama2,NIK2,Email2,NoHp2,Alamat2,Agama2,Status2,Produk,aBoolean);
            }
        });
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                Nama2 = Nama.getText().toString();
                NIK2 = NIK.getText().toString();
                NoHp2 = NoHp.getText().toString();
                Email2 = Email.getText().toString();
                Alamat2 = Alamat.getText().toString();
                Agama2 = Agama.getText().toString();
                Status2 = Status.getText().toString();

                //Validasi
                if (Nama2.isEmpty()){
                    Nama.setError(getResources().getString(R.string.error_field));
                }
                else if (NIK2.isEmpty()){
                    NIK.setError(getResources().getString(R.string.error_field));
                }
                else if (NoHp2.isEmpty()){
                    NoHp.setError(getResources().getString(R.string.error_field));
                }
                else if (Email2.isEmpty()){
                    Email.setError(getResources().getString(R.string.error_field));
                }
                else if (Alamat2.isEmpty()){
                    Alamat.setError(getResources().getString(R.string.error_field));
                }
                else if (Agama2.isEmpty()){
                    Agama.setError(getResources().getString(R.string.error_field));
                }
                else if (Status2.isEmpty()){
                    Status.setError(getResources().getString(R.string.error_field));
                }
                else if (selectedId == -1){

                }
                else if(!pernyataan.isChecked()){
                    pernyataan.setError(getResources().getString(R.string.error_field));
                }
                else {
                    Mirroring(true,Nama2,NIK2,Email2,NoHp2,Alamat2,Agama2,Status2,Produk,aBoolean);
                    iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("nama",Nama2);
                        obj.put("nik",NIK2);
                        obj.put("email",Email2);
                        obj.put("nohp",NoHp2);
                        obj.put("alamat",Alamat2);
                        obj.put("agama",Agama2);
                        obj.put("status",Status2);
                        obj.put("produk",Produk);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String dataS = obj.toString();

                    session.saveCIF(dataS);
                    PopUpSuccesRegistration();
                }
            }
        });
    }

    private void Mirroring(Boolean bool, CharSequence nama, CharSequence nik, CharSequence email, CharSequence hp, CharSequence alamat, CharSequence agama, CharSequence status, CharSequence produk, boolean pernyataan){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(nama);
            jsonArray.put(nik);
            jsonArray.put(email);
            jsonArray.put(hp);
            jsonArray.put(alamat);
            jsonArray.put(agama);
            jsonArray.put(status);
            jsonArray.put(produk);
            jsonArray.put(pernyataan);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",8);
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

    private void ByteArraytoimg(byte[] byteArray, ImageView gambar_profile){
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        gambar_profile.setImageBitmap(decodedBitmap);
    }
    private void PopUpSuccesRegistration(){
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText(getResources().getString(R.string.reg_title));
                    sweetAlertDialog.setContentText(getResources().getString(R.string.reg_content));
                    sweetAlertDialog.setConfirmText(getResources().getString(R.string.activation));
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();
                    Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
                    btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Mirroring2(true);
                            Fragment fragment = new frag_aktivasi_ibmb();
                            getFragmentPage(fragment);
                            sweetAlertDialog.dismiss();
                        }
                    });
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void Mirroring2(Boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",9);
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
