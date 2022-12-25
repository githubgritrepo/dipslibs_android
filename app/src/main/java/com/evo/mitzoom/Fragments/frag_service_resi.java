package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Helper.SingleMediaScanner;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_service_resi extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private PhotoView imgResume;
    private Button btnOK;
    private String idDips;
    private Button btnUnduh;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvMsgThanks;
    private TextView tvMsgNum;
    private TextView tvMsgThanks2;
    private byte[] bytePhoto = null;
    private boolean isSessionZoom;
    private RabbitMirroring rabbitMirroring;
    private int form_id = 0;
    private String no_Form = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }

        if (getArguments() != null) {
            if (getArguments().containsKey("form_id")) {
                form_id = getArguments().getInt("form_id");
            }
        }

        Random random=new Random();
        int dataInt = random.nextInt(999999999);

        no_Form = String.valueOf(dataInt);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_new_resi, container, false);

        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) v.findViewById(R.id.tvSubTitle);
        imgResume = (PhotoView) v.findViewById(R.id.imgResume);
        tvMsgThanks = (TextView) v.findViewById(R.id.tvMsgThanks);
        tvMsgNum = (TextView) v.findViewById(R.id.tvMsgNum);
        tvMsgThanks2 = (TextView) v.findViewById(R.id.tvMsgThanks2);
        btnOK = v.findViewById(R.id.btnSelesai);
        btnUnduh = (Button) v.findViewById(R.id.btnUnduh);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String titleSuccess = getString(R.string.title_safe_complaint);
        String titleHeadline = getString(R.string.body_complaint);
        String titleHeadline2 = getString(R.string.body2_complaint);

        titleHeadline2 = titleHeadline2.replace("Bank XYZ",getString(R.string.bank_name)).replace("XYZ Bank",getString(R.string.bank_name));

        tvTitle.setText(titleSuccess);
        tvSubTitle.setVisibility(View.GONE);
        tvMsgNum.setVisibility(View.VISIBLE);
        tvMsgThanks2.setVisibility(View.VISIBLE);

        tvMsgThanks.setText(titleHeadline);
        tvMsgNum.setText(no_Form);
        tvMsgThanks2.setText(titleHeadline2);

        idDips = sessions.getKEY_IdDips();

        btnUnduh.setEnabled(false);
        btnUnduh.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CEK","MASUK BUTTON OK");
                rabbitMirroring.MirroringSendEndpoint(14);
                sessions.clearCIF();
                getFragmentPage(new frag_portfolio_new());
            }
        });

        btnUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CEK","MASUK BUTTON UnduhResi");
                if (bytePhoto == null) {
                    Toast.makeText(mContext,"Tidak dapat mengunduh Formulir",Toast.LENGTH_SHORT).show();
                    return;
                }

                processDownload();
            }
        });

        //getResumeResi();

    }

    private void processDownload() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        try {

            String filename = "No_Formulir-"+no_Form +"-"+timeStamp+ ".jpg";
            createTemporaryFile(bytePhoto, filename);

            String appName = getString(R.string.app_name_dips);

            String contents = "File disimpan di folder Phone/DCIM/" + appName + "/" + filename;

            SweetAlertDialog sAW = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
            sAW.setContentText(contents);
            sAW.hideConfirmButton();
            sAW.setCancelText("Tutup");
            sAW.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sAW.dismiss();
                }
            });
            sAW.setCancelable(false);
            sAW.show();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createTemporaryFile(byte[] byteImage, String filename) throws Exception {
        String appName = getString(R.string.app_name_dips);
        String IMAGE_DIRECTORY_NAME = appName;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), IMAGE_DIRECTORY_NAME);

        String[] myFiles;

        myFiles = mediaStorageDir.list();
        if (myFiles != null) {
            for (int i = 0; i < myFiles.length; i++) {
                Log.d("CEK","myFiles ke-"+i+" : "+myFiles[i]);
                File myFile = new File(mediaStorageDir, myFiles[i]);
                myFile.delete();
            }
        }

        mediaStorageDir.mkdirs();

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                filename);

        FileOutputStream fos = new FileOutputStream(mediaFile);
        fos.write(byteImage);
        fos.flush();
        fos.close();

        new SingleMediaScanner(mContext,mediaFile);

        return mediaFile;
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

}
