package com.evo.mitzoom.Adapter;

import static com.evo.mitzoom.Fragments.frag_service_antarbank.REQUESTCODE_GALLERY_QRCODE;
import static com.evo.mitzoom.Fragments.frag_service_antarbank.intPos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.io.File;
import java.util.ArrayList;

public class AdapterQR extends RecyclerView.Adapter<AdapterQR.ItemHolder> implements OnClickUploadImageListener {

    private final ArrayList<ItemModel> dataList;

    private final Context mContext;
    private final Fragment fragment;
    private final SessionManager sessions;

    public AdapterQR(Fragment fragment, Context ctx, ArrayList<ItemModel> dataList){
        this.dataList = dataList;
        this.mContext = ctx;
        this.fragment = fragment;
        this.sessions = new SessionManager(mContext);
    }

    @NonNull
    @Override
    public AdapterQR.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploadqrcode, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterQR.ItemHolder holder, int position) {
        int pos = position;
        String nameContent = dataList.get(position).getNamaItem();
        holder.tvContentQr.setText(dataList.get(position).getNamaItem());
        if (nameContent.contains("QRCode")) {
            holder.tvContentQr.setTextColor(mContext.getResources().getColor(R.color.zm_text_grey));
            holder.imgUpQR.setImageDrawable(mContext.getResources().getDrawable(R.drawable.uploadgrey));
        } else {
            holder.tvContentQr.setTextColor(mContext.getResources().getColor(R.color.zm_text));
            holder.imgUpQR.setImageDrawable(mContext.getResources().getDrawable(R.drawable.uploadblue));
        }
        holder.cvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intPos = pos;
                sessions.saveMedia(2);
                chooseFromSD();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onClickUpload(Uri selectedImage, RecyclerView.ViewHolder viewHolderForAdapterPosition) {
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor c = mContext.getContentResolver().query(selectedImage,filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);

        File files = new File(picturePath);
        String fileName = files.getName();

        String idList = dataList.get(viewHolderForAdapterPosition.getPosition()).getId();
        dataList.set(viewHolderForAdapterPosition.getPosition(),new ItemModel(idList,fileName,0)).getNamaItem();

        TextView tvContent = (TextView) viewHolderForAdapterPosition.itemView.findViewById(R.id.tvContentQr);
        tvContent.setText(fileName);
        tvContent.setTextColor(mContext.getResources().getColor(R.color.zm_text));

        ImageView imgUpQR = (ImageView) viewHolderForAdapterPosition.itemView.findViewById(R.id.imgUpQR);
        imgUpQR.setImageDrawable(mContext.getResources().getDrawable(R.drawable.uploadblue));
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final CardView cvImg;
        private final ImageView imgUpQR;
        private final TextView tvContentQr;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            cvImg = (CardView) itemView.findViewById(R.id.cvImg);
            imgUpQR = (ImageView) itemView.findViewById(R.id.imgUpQR);
            tvContentQr = (TextView) itemView.findViewById(R.id.tvContentQr);
        }
    }

    private void chooseFromSD() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fragment.startActivityForResult(intent, REQUESTCODE_GALLERY_QRCODE);
    }
}
