package com.evo.mitzoom.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Helper.DownloadTaskHelper;
import com.evo.mitzoom.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterDownloadResi extends RecyclerView.Adapter<AdapterDownloadResi.ViewHolder> {

    private final Context mContext;
    private final JSONArray dataResi;
    private final JSONArray dataDownloadResi;

    public AdapterDownloadResi(Context mContext, JSONArray dataResi, JSONArray dataDownloadResi) {
        this.mContext = mContext;
        this.dataResi = dataResi;
        this.dataDownloadResi = dataDownloadResi;
    }

    @NonNull
    @Override
    public AdapterDownloadResi.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_resi_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDownloadResi.ViewHolder holder, int position) {
        try {
            int loopPos = position + 1;
            JSONObject dataObj = dataResi.getJSONObject(position);
            if (dataObj.has("trxTo")) {
                loopPos = dataObj.getInt("trxTo");
            }
            String base64Image = dataObj.getJSONObject("data").getString("image");
            String pdfFile = dataObj.getJSONObject("data").getString("pdf");
            String filenames = pdfFile.substring(pdfFile.lastIndexOf("/"));
            byte[] bytePhoto = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);

            JSONObject dataResiObj = new JSONObject();
            dataResiObj.put("pdfFile",pdfFile);
            dataResiObj.put("filename",filenames);
            dataDownloadResi.put(dataResiObj);

            holder.imgResume.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imgResume.setImageBitmap(bitmap);
            holder.tvLabelTrx.append(" "+loopPos);
            holder.btnUnduh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog mProgressDialog = new ProgressDialog(mContext);
                    mProgressDialog.setMessage(mContext.getString(R.string.label_downloaded));
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);

                    DownloadTaskHelper downloadTaskHelper = new DownloadTaskHelper(mContext, mProgressDialog);
                    downloadTaskHelper.execute(pdfFile,filenames);

                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            downloadTaskHelper.cancel(true); //cancel the task
                        }
                    });
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return dataResi.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgResume;
        private final TextView tvLabelTrx;
        private final Button btnUnduh;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgResume = (ImageView) itemView.findViewById(R.id.imgResume);
            tvLabelTrx = (TextView) itemView.findViewById(R.id.tvLabelTrx);
            btnUnduh = (Button) itemView.findViewById(R.id.btnUnduh);
        }
    }
}
