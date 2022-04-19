package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhairytripathi.library.EditTextPin;
import com.evo.mitzoom.R;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_aktivasi_ibmb extends Fragment {
    private Context context;
    private TextView UserId, Password, Konfirmasi_password, Mpin, Konfirmasi_mpin, Timer, Resend_Otp;
    private Button btnProses;
    private LayoutInflater inflater;
    private View dialogView;
    private Button btnVerifikasi;
    private EditTextPin editTextPin;
    private Handler handlerSuccess;
    public int seconds = 60;
    public boolean running = true;
    public boolean clickable = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aktivasi_ibmb, container, false);
        UserId = view.findViewById(R.id.et_userId);
        Password = view.findViewById(R.id.et_password_ibmb);
        Konfirmasi_password = view.findViewById(R.id.et_konfirmasi_password_ibmb);
        Mpin = view.findViewById(R.id.et_mpin);
        Konfirmasi_mpin = view.findViewById(R.id.et_konfirmasi_mpin);
        btnProses = view.findViewById(R.id.btnProsesIbmb);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserId.setText("Andi Setiawan");
        Password.setText("Andi123456");
        Konfirmasi_password.setText("Andi123456");
        Mpin.setText("123456");
        Konfirmasi_mpin.setText("123456");
        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            PopUp();
            }
        });
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
                getFragmentPage(new frag_aktivasi_berhasil());
                sweetAlertDialog.dismiss();
            }
        },5000);
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
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
                    resend.setTextColor(getResources().getColorStateList(R.color.Blue));
                    timer_run.setTextColor(getResources().getColorStateList(R.color.btnFalse));
                }
                else {
                    resend.setTextColor(getResources().getColorStateList(R.color.btnFalse));
                    timer_run.setTextColor(getResources().getColorStateList(R.color.Blue));
                }
                handler.postDelayed(this,1000);
            }
        });
    }
}