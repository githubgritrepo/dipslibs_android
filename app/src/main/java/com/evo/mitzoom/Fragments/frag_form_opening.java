package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhairytripathi.library.EditTextPin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsWaitingRoom;

import org.w3c.dom.Text;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_form_opening extends Fragment {
    private Context context;
    private ImageView iconKtp, iconNpwp, iconSignature, iconForm, preview_ktp, preview_npwp, preview_signature;;
    private TextView Nama,NIK,Email,Alamat,Agama,Status;
    private Button btnProcess;
    private byte[] KTP, NPWP, TTD;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_opening_account, container, false);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        Nama = view.findViewById(R.id.et_nama);
        NIK = view.findViewById(R.id.et_nik);
        Email = view.findViewById(R.id.et_email);
        Alamat = view.findViewById(R.id.et_alamat);
        Agama = view.findViewById(R.id.et_agama);
        Status = view.findViewById(R.id.et_status);
        btnProcess = view.findViewById(R.id.btnProses);
        preview_ktp = view.findViewById(R.id.Imageview_ktp);
        preview_npwp = view.findViewById(R.id.Imageview_npwp);
        preview_signature = view.findViewById(R.id.Imageview_tanda_tangan);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconSignature.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));

        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        NPWP = arg.getByteArray("npwp");
        TTD = arg.getByteArray("ttd");
        arg.clear();

        ByteArraytoimg(KTP,preview_ktp);
        ByteArraytoimg(NPWP,preview_npwp);
        ByteArraytoimg(TTD,preview_signature);


        Log.d("BYTE ARRAY KTP = ", String.valueOf(KTP));
        Log.d("BYTE ARRAY NPWP = ", String.valueOf(NPWP));
        Log.d("BYTE ARRAY TTD = ", String.valueOf(TTD));

        Nama.setText("Andi Setiawan");
        NIK.setText("323432342304203");
        Alamat.setText("Rt.15 Rw/20 Maju, Kecamatan Suka Mulya, DKI Jakarta, Jawa Barat 12345");
        Agama.setText("Islam");
        Status.setText("Belum Kawin");

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconForm.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
                PopUpSuccesRegistration();
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
                            getFragmentPage(new frag_aktivasi_ibmb());
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



}
