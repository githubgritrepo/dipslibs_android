package com.evo.mitzoom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Fragments.frag_new_account;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GridProductAdapter extends RecyclerView.Adapter<GridProductAdapter.GriViewHolder> {

    int[] gambar;
    int position;
    Context ctx;
    MaterialStyledDialog materialStyledDialog;
    SessionManager sessions;
    private LayoutInflater inflater;
    private View dialogView;
    private SweetAlertDialog sweetAlertDialogTNC;
    private final List<JSONObject> dataProduct;
    private ImageView adsImg;

    public GridProductAdapter(Context ctx, List<JSONObject> newDataProd) {
        this.ctx = ctx;
        this.dataProduct = newDataProd;
        this.sessions = new SessionManager(this.ctx);
    }

    @NonNull
    @Override
    public GridProductAdapter.GriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid,parent,false);
        return new GriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridProductAdapter.GriViewHolder holder, int position) {
        int pos = position;
        String dataBody = "";
        String namaProduk = "";
        try {
            JSONObject dataProdukId = dataProduct.get(pos).getJSONObject("produkId");
            int produkId = dataProdukId.getInt("id");
            namaProduk = dataProdukId.getString("namaProduk").trim();
            dataBody = dataProdukId.getString("body").trim();
            processProductMedia(produkId,holder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalDataBody = dataBody;
        String finalNamaProduk = namaProduk;
        holder.ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUPData(finalNamaProduk, finalDataBody);

            }
        });
    }

    private void PopUPData(String finalNamaProduk, String finalDataBody) {

        View views = LayoutInflater.from(ctx).inflate(R.layout.item_ads,null);

        TextView tvContent = views.findViewById(R.id.tvContents);
        Button btnBack = views.findViewById(R.id.btnback);

        tvContent.setText(Html.fromHtml(finalDataBody, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int idx = source.indexOf(",");
                idx += 1;
                String new_source = source.substring(idx);
                byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Drawable d = new BitmapDrawable(ctx.getResources(), bitmap);
                int intH = d.getIntrinsicHeight();
                int intW = d.getIntrinsicWidth();
                d.setBounds(0,0,intW,intH);
                return d;
            }
        }, null));

        SweetAlertDialog sweet = new SweetAlertDialog(ctx,SweetAlertDialog.NORMAL_TYPE);
        sweet.setTitle(finalNamaProduk);
        sweet.hideConfirmButton();
        sweet.setCustomView(views);
        sweet.show();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweet.dismissWithAnimation();
            }
        });

        int width = ctx.getResources().getDisplayMetrics().widthPixels;
        int height = ctx.getResources().getDisplayMetrics().heightPixels;
        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);

        sweet.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        sweet.getWindow().setLayout(newWidth,newHeight);

        /*sweet.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        sweet.getWindow().setLayout(width,height);*/
    }

    private void processProductMedia(int produkId, GriViewHolder holder) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIWAITING_PRODUCT().getProductMedia(produkId,authAccess,exchangeToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String Content_Type = response.headers().get("Content-Type");
                    if (!Content_Type.contains("json")) {
                        InputStream in = response.body().byteStream();
                        processParsingMedia(in, Content_Type,holder);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void processParsingMedia(InputStream in, String content_type, GriViewHolder holder) {
        if (content_type.contains("image")) {
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            holder.ads.setImageBitmap(bitmap);
        }
    }

    private void getFragmentPage(Fragment fragment){
        ((FragmentActivity)ctx).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return dataProduct.size();
    }

    public class GriViewHolder extends RecyclerView.ViewHolder {

        ImageView ads;
        public GriViewHolder(@NonNull View itemView) {
            super(itemView);
            ads = itemView.findViewById(R.id.ads);
        }
    }
    private void PopUpTnc(){
        sweetAlertDialogTNC = new SweetAlertDialog(ctx, SweetAlertDialog.NORMAL_TYPE);
        inflater = ((Activity)ctx).getLayoutInflater();
        dialogView = inflater.inflate(R.layout.item_tnc,null);
        if (sweetAlertDialogTNC == null) {
            sweetAlertDialogTNC = new SweetAlertDialog(ctx, SweetAlertDialog.NORMAL_TYPE);
        }
        else{
            sweetAlertDialogTNC.setCustomView(dialogView);
            sweetAlertDialogTNC.hideConfirmButton();
            sweetAlertDialogTNC.setCancelable(false);
            sweetAlertDialogTNC.show();
            CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
            Button btn = dialogView.findViewById(R.id.btnnexttnc);
            btn.setClickable(false);
            btn.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.btnFalse));
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()){
                        btn.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.Blue));
                        btn.setClickable(true);
                    }
                    else {
                        btn.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.btnFalse));
                        btn.setClickable(false);
                    }
                }
            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()){
                        ((FragmentActivity)ctx).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.layout_frame, new frag_new_account())
                                .addToBackStack(null)
                                .commit();
                        sweetAlertDialogTNC.dismiss();
                    }
                    else {
                        btn.setClickable(false);
                    }
                }
            });
        }

    }
    private void popUpAds() {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_ads,null);
        materialStyledDialog = new MaterialStyledDialog.Builder(ctx)
                .setHeaderDrawable(R.drawable.bannerads)
                .setCancelable(false)
                .setCustomView(view, 20, 20, 20, 0)
                .setHeaderScaleType(ImageView.ScaleType.FIT_XY)
                .show();
        ExtendedFloatingActionButton materialButton = view.findViewById(R.id.btnback);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialStyledDialog.dismiss();
            }
        });
    }

}
