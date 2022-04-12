package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.FileProvider.getUriForFile;
import static com.evo.mitzoom.ui.DipsCapture.resizeAndCropCenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_opening_account extends Fragment {
    private Context context;
    private ImageView iconKtp, iconNpwp, iconSignature, iconForm,btnCamera, viewImage;
    private LinearLayout btnGallery;
    private TextView filename;
    private Button btnNext;
    private Bitmap bitmapz = null;
    private LinearLayout chooseImage, delete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_opening_account, container, false);
        btnCamera = view.findViewById(R.id.choose_camera);
        btnGallery = view.findViewById(R.id.choose_gallery);
        btnNext = view.findViewById(R.id.btnNext);
        iconKtp = view.findViewById(R.id.icon_ktp);
        iconNpwp = view.findViewById(R.id.icon_npwp);
        iconSignature = view.findViewById(R.id.icon_signature);
        iconForm = view.findViewById(R.id.icon_form);
        viewImage = view.findViewById(R.id.Imageview);
        chooseImage = view.findViewById(R.id.Choose_Image);
        delete = view.findViewById(R.id.delete);
        filename = view.findViewById(R.id.filename);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PopUp();
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromCamera();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromSD();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNext.setClickable(false);
                btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                viewImage.setVisibility(View.GONE);
                chooseImage.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentPage(new frag_opening_account2());
            }
        });
    }

    private void PopUp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.popupktp));
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
    }
    private void chooseFromSD() {
    }
    private void chooseFromCamera() {
    }
    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
