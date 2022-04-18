package com.evo.mitzoom.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.R;
import com.evo.mitzoom.ui.DipsCameraActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class frag_opening_account2 extends Fragment {
    private Context context;
    private ImageView iconKtp, iconNpwp, iconSignature, iconForm,btnCamera, viewImage;
    private LinearLayout btnGallery;
    private TextView filename;
    private Button btnNext;
    private byte[] KTP, NPWP;
    private LinearLayout chooseImage, delete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_opening_account2, container, false);
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
        Bundle arg = getArguments();
        KTP = arg.getByteArray("ktp");
        arg.clear();
        btnNext.setClickable(false);
        btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        iconKtp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif_success));
        iconNpwp.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
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
                if (NPWP == null){
                    getFragmentPage(new frag_opening_account3());
                    Toast.makeText(context, "Image is null", Toast.LENGTH_SHORT).show();
                }
                else{
                    Fragment fragment = new frag_opening_account3();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("ktp",KTP);
                    bundle.putByteArray("npwp",NPWP);
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
    private void chooseFromCamera() {
        Intent intent = new Intent(context, DipsCameraActivity.class);
        startActivityForResult(intent, 1);
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "temp.png");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        startActivityForResult(intent, 1);*/
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
                /*File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()){
                    if (temp.getName().equals("temp.png")){
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.bg_cif));
                    btnNext.setClickable(true);
                    delete.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.VISIBLE);
                    chooseImage.setVisibility(View.GONE);
                    getResizedBitmap(bitmap , (bitmap.getWidth()/2), (bitmap.getHeight()/2));
                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".png");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG,85,outFile);
                        outFile.flush();
                        outFile.close();
                    }
                    catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
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
        imgtoByteArray(resizedBitmap);
        //return resizedBitmap;
    }
    private void imgtoByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] imageBytes = baos.toByteArray();
        NPWP = imageBytes;
    }
}
