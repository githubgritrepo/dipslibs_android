package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaos.view.PinView;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_new_account_cs2 extends Fragment {
    private Context context;
    private ImageView btnBack;
    private Button btnProses;
    private String idDips, rekSumberdana, nama, tgl, produk, nominal;
    private int typeSend;
    private CheckBox pernyataan;
    private LinearLayout icon_isi_form, icon_konfirmasi_data, icon_resi;
    private TextView tv_reksumber, tv_namaLengkap, tv_tgl, tv_produk, tv_nominal;
    private SessionManager session;
    private LayoutInflater inflater;
    private View dialogView;
    private Handler handlerSuccess;
    private Button btnVerifikasi;
    private TextView Timer, Resend_Otp;
    private PinView otp;
    private Handler handler;
    private Runnable myRunnable;
    public int getMinutes = 2;
    public int seconds = 60;
    private int selPos;
    private String oldString, newString;
    public boolean running = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_new_account_cs2, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btn_back_new_acc);
        icon_isi_form = view.findViewById(R.id.icon_isi_form);
        icon_konfirmasi_data = view.findViewById(R.id.icon_konfirmasi_data);
        icon_resi = view.findViewById(R.id.icon_resi);
        btnProses = view.findViewById(R.id.btnProses_konfirmasi_data_cs);
        tv_reksumber = view.findViewById(R.id.RekeningSumber_cs);
        tv_namaLengkap = view.findViewById(R.id.nama_lengkap_new_acc_cs);
        tv_produk = view.findViewById(R.id.tipe_produk_rek_new_acc_cs);
        tv_tgl = view.findViewById(R.id.tgl_pembukaan_new_acc_cs);
        tv_nominal = view.findViewById(R.id.setoran_awal_new_acc_cs);
        pernyataan = view.findViewById(R.id.pernyataan_cs);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();
        Bundle arg = getArguments();
        btnProses.setEnabled(false);
        btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        icon_isi_form.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        rekSumberdana = arg.getString("rek_sumber_dana");
        typeSend = arg.getInt("typesend");
        nama = arg.getString("nama_lengkap");
        tgl = arg.getString("tgl");
        produk = arg.getString("produk");
        nominal = arg.getString("nominal");
        pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pernyataan.isChecked()){
                    Log.d("CHECK","TRUE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnProses.setEnabled(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btnProses.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btnProses.setEnabled(false);
                }
            }
        });
        tv_reksumber.setText(rekSumberdana);
        tv_namaLengkap.setText(nama);
        tv_produk.setText(produk);
        tv_tgl.setText(tgl);
        tv_nominal.setText("Rp"+nominal);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new frag_new_account_cs();
                Bundle bundle = new Bundle();
                bundle.putString("rek_sumber_dana",rekSumberdana);
                bundle.putString("nama_lengkap",nama);
                bundle.putString("tgl",tgl);
                bundle.putString("produk",produk);
                bundle.putString("nominal",nominal);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        });
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icon_konfirmasi_data.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                PopUpOTP();
            }
        });
    }
    private void PopUpOTP(){
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_otp,null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        btnVerifikasi = dialogView.findViewById(R.id.btnVerifikasi);
        Timer = dialogView.findViewById(R.id.timer_otp);
        Resend_Otp = dialogView.findViewById(R.id.btn_resend_otp);
        otp = dialogView.findViewById(R.id.otp);
        otp.setAnimationEnable(true);
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*selPos = otp.getSelectionStart();
                oldString = myFilter(s.toString());*/

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                newString = myFilter(s.toString());
                otp.removeTextChangedListener(this);
                handler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        otp.setText(newString);
                    }
                };
                handler.postDelayed(myRunnable, 1500);
                otp.addTextChangedListener(this);
                if (otp.length() == 6 || otp.length() == 0){
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Log.d("TAG","STOP Loop");
                }
            }
        });
        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    sweetAlertDialog.dismiss();
                    PopUpSuccesOtp();

                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    Toast.makeText(context, "Kode Terkirim", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void PopUpSuccesOtp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.otp_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.otp_content));
        sweetAlertDialog.setCancelable(true);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        handlerSuccess = new Handler();
        handlerSuccess.postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                Fragment fragment;
                fragment = new frag_new_account_cs_resi();
                Bundle bundle = new Bundle();
                bundle.putString("nama_lengkap",nama);
                fragment.setArguments(bundle);
                getFragmentPage(fragment);
            }
        },2000);
    }
    public String myFilter(String s) {
        String digits;
        digits = s.replaceAll("[0-9]", "*");
        if (s.equals("")) return "";
        return digits;
    }
    public void runTimer(TextView timer_run, TextView resend) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = getMinutes;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0 && minutes == 0){
                    running = false;
                    resend.setClickable(true);
                } else if (seconds == 0 && minutes > 0){
                    seconds = 59;
                }

                if (seconds == 59) {
                    getMinutes--;
                }
                handler.postDelayed(this,1000);
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
}
