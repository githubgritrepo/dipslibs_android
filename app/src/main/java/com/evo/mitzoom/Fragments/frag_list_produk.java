package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_list_produk extends Fragment {
    private Context context;
    private NestedScrollView nested;
    private TextView btn_tabungan_a, btn_tabungan_b;
    private ImageView btnBack;
    private RelativeLayout rlOpenAccount;
    private boolean isSessionZoom = false;
    private SweetAlertDialog sweetAlertDialogTNC = null;
    private View dialogView;
    private boolean isCust = false;
    private boolean isSwafoto = false;
    private SessionManager sessions;
    private RabbitMirroring rabbitMirroring;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(context);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_daftar_produk, container, false);
        rlOpenAccount = (RelativeLayout) view.findViewById(R.id.rlOpenAccount);
        btn_tabungan_a = view.findViewById(R.id.btn_tabungan_a);
        btn_tabungan_b = view.findViewById(R.id.btn_tabungan_b);
        btnBack = view.findViewById(R.id.btn_back6);
        nested = view.findViewById(R.id.nested);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isSessionZoom) {
            btnBack.setVisibility(View.GONE);
        }
        rlOpenAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rabbitMirroring.MirroringSendEndpoint(0);
                rabbitMirroring.MirroringSendEndpoint(361);
                PopUpTnc();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_berita());
            }
        });
        btn_tabungan_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tabungan = "Tabungan A";
                sendDataFragment("headline",Tabungan,new frag_tabungan());
            }
        });
        btn_tabungan_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tabungan = "Tabungan B";
                sendDataFragment("headline",Tabungan,new frag_tabungan());
            }
        });
    }

    private void PopUpTnc(){
        Log.e("CEK","MASUK PopUpTnc");
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (sweetAlertDialogTNC == null) {
            dialogView = inflater.inflate(R.layout.item_tnc,null);
            sweetAlertDialogTNC = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
            sweetAlertDialogTNC.setCustomView(dialogView);
            sweetAlertDialogTNC.hideConfirmButton();
            sweetAlertDialogTNC.setCancelable(false);
        }
        sweetAlertDialogTNC.show();
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setClickable(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    Log.d("CHECK","TRUE");
                    btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btn.setClickable(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btn.setClickable(false);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    sweetAlertDialogTNC.dismiss();
                    sweetAlertDialogTNC.cancel();
                    sessions.saveIsCust(isCust);
                    sessions.saveIsSwafoto(isSwafoto);
                    sessions.saveFormCOde(4);
                    Fragment fragment = new frag_cif();
                    RabbitMirroring.MirroringSendEndpoint(4);
                    getFragmentPage(fragment);
                }
                else {
                    btn.setClickable(false);
                }
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

    private void sendDataFragment(String tag, String Text, Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString(tag,Text);
        fragment.setArguments(bundle);
        getFragmentPage(fragment);
    }
}
