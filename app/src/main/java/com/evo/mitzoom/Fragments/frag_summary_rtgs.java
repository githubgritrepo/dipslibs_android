package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dhairytripathi.library.EditTextPin;
import com.evo.mitzoom.R;

import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_summary_rtgs extends Fragment {
    private Context context;
    private TextView Timer, Resend_Otp, tv_RekeningSumber, tv_RekeningPenerima, tv_JenisLayanan, tv_PenerimaManfaat, tv_JenisPenduduk, tv_Berita, tv_Biaya, tv_Nominal;
    private String RekeningSumber, JenisLayanan, NamaBank, NamaPenerima, PenerimaManfaat, JenisPenduduk, Berita, Nominal, RekPenerima;
    private ImageView btnBack;
    private Button btnTransfer;
    private LayoutInflater inflater;
    private View dialogView;
    private Handler handlerSuccess;
    private Button btnVerifikasi;
    private EditTextPin editTextPin;
    public int seconds = 60;
    public boolean running = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summary_rtgs, container, false);
        btnBack = view.findViewById(R.id.btn_back5);
        btnTransfer = view.findViewById(R.id.btnTransfer);
        tv_RekeningSumber = view.findViewById(R.id.RekeningSumber);
        tv_RekeningPenerima = view.findViewById(R.id.RekeningPenerima);
        tv_JenisLayanan = view.findViewById(R.id.JenisLayanan);
        tv_PenerimaManfaat = view.findViewById(R.id.PenerimaManfaat);
        tv_JenisPenduduk = view.findViewById(R.id.JenisPenduduk);
        tv_Berita = view.findViewById(R.id.Berita);
        tv_Biaya = view.findViewById(R.id.Biaya);
        tv_Nominal = view.findViewById(R.id.Nominal);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle terima = getArguments();
        RekeningSumber = terima.getString("rekeningSumber");
        JenisLayanan = terima.getString("jenisLayanan");
        NamaBank = terima.getString("namaBank");
        NamaPenerima = terima.getString("namaPenerima");
        PenerimaManfaat = terima.getString("penerimaManfaat");
        JenisPenduduk = terima.getString("jenisPenduduk");
        Berita = terima.getString("berita");
        Nominal = terima.getString("nominal");
        RekPenerima = terima.getString("rekPenerima");

        tv_RekeningSumber.setText(RekeningSumber);
        tv_RekeningPenerima.setText(NamaBank+"\n"+RekPenerima+" - "+NamaPenerima);
        tv_JenisLayanan.setText(JenisLayanan);
        tv_PenerimaManfaat.setText(PenerimaManfaat);
        tv_JenisPenduduk.setText(JenisPenduduk);
        tv_Berita.setText(Berita);
        tv_Biaya.setText("Rp2.500");
        tv_Nominal.setText(Nominal);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_rtgs());
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUp();
            }
        });

        ///SetText
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void PopUp(){
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
        editTextPin = dialogView.findViewById(R.id.kode_otp);
        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPin.getPin().equalsIgnoreCase("")){
                    Toast.makeText(context, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    editTextPin.getPin().toString();
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
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        handlerSuccess = new Handler();
        handlerSuccess.postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                popUpBerhasil();
            }
        },5000);
    }
    private void popUpBerhasil(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.successTransaction));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                getFragmentPage(new frag_portfolio());
                sweetAlertDialog.dismiss();
            }
        });
    }
    public void runTimer(TextView timer_run, TextView resend) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = 0;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0){
                    running = false;
                    resend.setClickable(true);
                }
                handler.postDelayed(this,1000);
            }
        });
    }

}
