package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterSlide extends PagerAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private JSONArray dataArr;
    private int mPlayerPosition;
    private List<InputStream> isArr;
    private List<String> contentType;

    public AdapterSlide(Context mContext, JSONArray dataArr) {
        this.mContext = mContext;
        this.dataArr = dataArr;
        this.isArr = new ArrayList<InputStream>();
        this.contentType = new ArrayList<String>();
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return dataArr.length();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.e("CEK","instantiateItem position : "+position);
        View myImageLayout = inflater.inflate(R.layout.slide,container,false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.images);
        VideoView vv = (VideoView) myImageLayout.findViewById(R.id.vvVideo);
        TextView nama = myImageLayout.findViewById(R.id.nama_item_caroseoul);

        MediaController ctlr = new MediaController(mContext);
        vv.setMediaController(ctlr);
        vv.requestFocus();

        try {
            String content_Type = dataArr.getJSONObject(position).getString("content_Type");

            if (content_Type.indexOf("image") > -1) {
                Bitmap dataBitmap = (Bitmap) dataArr.getJSONObject(position).get("dataBitmap");
                vv.stopPlayback();
                vv.setVisibility(View.GONE);
                myImage.setVisibility(View.VISIBLE);
                myImage.setImageBitmap(dataBitmap);
            } else {
                File dataFileStream = (File) dataArr.getJSONObject(position).get("dataStream");
                vv.setVisibility(View.VISIBLE);
                myImage.setVisibility(View.GONE);

                setSourceAndStartPlay(dataFileStream,vv);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        container.addView(myImageLayout, 0);
        /*myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0 :
                        return;
                    case 1:
                        getFragmentPage(new frag_list_produk());
                        return;
                    case 2:
                        Toast.makeText(mContext, R.string.credit_simulation, Toast.LENGTH_SHORT).show();
                        return;

                }
            }
        });*/
        return myImageLayout;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
    
    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)mContext).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    /*private void processParsingMedia(InputStream stream, String content_Type) {
        Log.e("CEK","processParsingMedia");
        if (content_Type.indexOf("image") > -1) {
            vv.stopPlayback();
            vv.setVisibility(View.GONE);
            myImage.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            myImage.setImageBitmap(bitmap);
        } else {
            vv.setVisibility(View.VISIBLE);
            myImage.setVisibility(View.GONE);

            new GetYoutubeFile(stream).run();

            *//*vv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("CEK","MASUK BUTTON VIDEO VIEW");
                    SweetAlertDialog sad = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
                    LayoutInflater inflaterVideo = ((Activity) mContext).getLayoutInflater();
                    View dialogView = inflaterVideo.inflate(R.layout.layout_videoview, null);
                    VideoView videov = (VideoView) dialogView.findViewById(R.id.vvVideo);

                    MediaController ctlr = new MediaController(mContext);
                    videov.setMediaController(ctlr);
                    videov.requestFocus();

                    sad.setCustomView(dialogView);
                    new GetYoutubeFile(videov,stream).start();
                    sad.show();
                }
            });*//*
        }
    }*/

    private void setSourceAndStartPlay(File bufferFile, VideoView vv) {
        vv.setVideoPath(bufferFile.getAbsolutePath());
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();

                float videoRate = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                float screenRatio = vv.getWidth() / (float) vv.getHeight();
                float scale = videoRate / screenRatio;

                if (scale >= 1f) {
                    vv.setScaleX(scale);
                } else {
                    vv.setScaleY(1f / scale);
                }
            }
        });

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }
}
