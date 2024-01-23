package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterSlide extends RecyclerView.Adapter<AdapterSlide.ViewHolder> {

    private final Context mContext;
    private final JSONArray dataArr;
    private int mPlayerPosition;
    private final List<InputStream> isArr;
    private final List<String> contentType;

    public AdapterSlide(Context mContext, JSONArray dataArr) {
        this.mContext = mContext;
        this.dataArr = dataArr;
        this.isArr = new ArrayList<InputStream>();
        this.contentType = new ArrayList<String>();
    }

    @NonNull
    @Override
    public AdapterSlide.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide,parent,false);
        return new ViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSlide.ViewHolder holder, int position) {

        try {
            if(dataArr.getJSONObject(position).has("content_Type")){
                String content_Type = dataArr.getJSONObject(position).getString("content_Type");

                if (content_Type.indexOf("image") > -1) {
                    Bitmap dataBitmap = (Bitmap) dataArr.getJSONObject(position).get("dataBitmap");
                    holder.vv.stopPlayback();
                    holder.rlVV.setVisibility(View.GONE);
                    holder.myImage.setVisibility(View.VISIBLE);
                    holder.myImage.setImageBitmap(dataBitmap);
                    holder.myImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    File dataFileStream = (File) dataArr.getJSONObject(position).get("dataStream");
                    holder.rlVV.setVisibility(View.VISIBLE);
                    holder.myImage.setVisibility(View.GONE);
                    holder.myImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    setSourceAndStartPlay(dataFileStream, holder);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSourceAndStartPlay(File dataFileStream, ViewHolder holder) {
        holder.vv.setVideoPath(dataFileStream.getAbsolutePath());
        holder.vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                holder.imgBtnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.imgBtnPlay.setVisibility(View.GONE);
                        //mediaPlayer.start();
                        holder.vv.start();
                    }
                });

                /*float videoRate = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                float screenRatio = holder.vv.getWidth() / (float) holder.vv.getHeight();
                float scale = videoRate / screenRatio;*/

                /*if (scale >= 1f) {
                    vv.setScaleX(scale);
                } else {
                    vv.setScaleY(1f / scale);
                }*/
            }
        });

        holder.vv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                holder.vv.stopPlayback();
                holder.vv.setVideoPath(dataFileStream.getAbsolutePath());
                holder.vv.requestFocus();
                holder.imgBtnPlay.setVisibility(View.VISIBLE);
                return false;
            }
        });

        holder.vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                holder.imgBtnPlay.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArr.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView myImage;
        private final RelativeLayout rlVV;
        private final VideoView vv;
        private final TextView nama;
        private final ImageView imgBtnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.images);
            rlVV = itemView.findViewById(R.id.rlVV);
            vv = itemView.findViewById(R.id.vvVideo);
            imgBtnPlay = itemView.findViewById(R.id.imgBtnPlay);
            nama = itemView.findViewById(R.id.nama_item_caroseoul);
        }
    }
    
    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)mContext).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
