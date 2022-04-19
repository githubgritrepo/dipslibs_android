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
import android.graphics.Matrix;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsCameraActivity;
import com.evo.mitzoom.ui.DipsWaitingRoom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class frag_opening_account extends Fragment {

    public static final int REQUEST_WRITE_PERMISSION = 786;
    private Context context;
    private LinearLayout iconKtp, iconNpwp, iconSignature, iconForm;
    private ImageView viewImage,btnCamera;
    private LinearLayout btnGallery;
    private TextView filename;
    private Button btnNext;
    private LinearLayout chooseImage, delete;
    private byte[] KTP;

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
                if (!requestPermission()){
                    Toast.makeText(context, "Permission denied",Toast.LENGTH_SHORT).show();
                    return;
                }
                chooseFromCamera();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermission()){
                    Toast.makeText(context, "Permission denied",Toast.LENGTH_SHORT).show();
                    return;
                }
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
                if (KTP == null){
                    Toast.makeText(context, "Silahkan Upload Foto KTP Anda", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendDataFragment("ktp",KTP,new frag_opening_account2());
                }

            }
        });
    }

    protected boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
            return false;
        }
        return true;
    }

    private void sendDataFragment(String tag, byte[] Text, Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putByteArray(tag,Text);
        fragment.setArguments(bundle);
        getFragmentPage(fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    private void PopUp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.popupktp));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
    }
    private void chooseFromSD() {
        Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }
    private void chooseFromCamera() {
        Intent intent = new Intent(context, DipsCameraActivity.class);
        startActivityForResult(intent, 1);
        /*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        try {
            photo = createTemporaryFile("temp", ".jpg");
            photo.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Please check SD Card! Image shot is impossible!", Toast.LENGTH_LONG).show();
        }
        Uri uriImage = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",photo);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);

        startActivityForResult(intent, 1);*/
    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        String IMAGE_DIRECTORY_NAME = getString(R.string.app_name_dips);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + part + ext);

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1){
                byte[] resultCamera = data.getByteArrayExtra("result_camera");
                Bitmap bitmap = BitmapFactory.decodeByteArray(resultCamera, 0, resultCamera.length);
                btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
                btnNext.setClickable(true);
                delete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
                getResizedBitmap(bitmap, (bitmap.getWidth()/2), (bitmap.getHeight()/2));
                /*String URL_IMAGE = photo.getAbsolutePath();
                try {
                    Bitmap bitmap;
                    bitmap = BitmapFactory.decodeFile(URL_IMAGE);
                    getResizedBitmap(bitmap , (bitmap.getWidth()/2), (bitmap.getHeight()/2));
                    btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
                    btnNext.setClickable(true);
                    delete.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            else if (requestCode == 2){
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = context.getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
                btnNext.setClickable(true);
                delete.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                chooseImage.setVisibility(View.GONE);
               getResizedBitmap(thumbnail, (thumbnail.getWidth()/2), (thumbnail.getHeight()/2));
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
    public void getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        viewImage.setImageBitmap(resizedBitmap);
        //return resizedBitmap;
        imgtoByteArray(resizedBitmap);
    }
    private void imgtoByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        KTP = imageBytes;
    }
}
