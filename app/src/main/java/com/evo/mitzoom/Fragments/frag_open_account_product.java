package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.ItemOpenAccount;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Model.ItemModel;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.Alternative.DipsSwafoto;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_open_account_product extends Fragment {

    private Context mContext;
    private RabbitMirroring rabbitMirroring;
    private SessionManager sessions;
    private boolean isSessionZoom = false;

    private ImageView btnBack;
    private RecyclerView rv_item;
    private RecyclerView rv_item2;
    private ArrayList<ItemModel> dataItems;
    private ArrayList<ItemModel> dataItems2;
    private ItemOpenAccount gridAdapter;
    private ItemOpenAccount gridAdapter2;
    private Button btnNext;

    private String dataTnC = "";
    private boolean isCust = false;
    private boolean isSwafoto = false;
    private boolean flagViewTNC = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        /*if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(mContext);
        }*/

        Log.e("CEK_FRAG_OPEN","getNoCIF : "+sessions.getNoCIF());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_open_account_product, container, false);

        btnBack = views.findViewById(R.id.btn_back);
        rv_item = views.findViewById(R.id.rv_item);
        rv_item2 = views.findViewById(R.id.rv_item2);
        btnNext = views.findViewById(R.id.btnNext);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isSessionZoom) {
            BaseMeetingActivity.showProgress(true);
        } else {
            DipsSwafoto.showProgress(true);
        }
        processGetTNC();
        //new AsyncProcess().execute();

        addData();

        int spanCount = 3; // columns
        int spacing = 10; // 20px
        boolean includeEdge = true;
        rv_item.setLayoutManager(new GridLayoutManager(mContext,spanCount));
        rv_item.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        gridAdapter = new ItemOpenAccount(dataItems,mContext,rabbitMirroring);
        rv_item.setAdapter(gridAdapter);

        rv_item2.setLayoutManager(new GridLayoutManager(mContext,spanCount));
        rv_item2.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        gridAdapter2 = new ItemOpenAccount(dataItems2,mContext,rabbitMirroring);
        rv_item2.setAdapter(gridAdapter2);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessions.getNoCIF() == null || sessions.getNoCIF().isEmpty()) {
                    RabbitMirroring.MirroringSendEndpoint(2);
                    getFragmentPage(new frag_list_produk());
                } else {
                    RabbitMirroring.MirroringSendEndpoint(15);
                    getFragmentPage(new frag_service_new());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataTnC.isEmpty()) {
                    Toast.makeText(mContext,getString(R.string.waiting),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (flagViewTNC == false) {
                    RabbitMirroring.MirroringSendEndpoint(361);
                    PopUpTnc();
                }
            }
        });

    }

    private class AsyncProcess extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            processGetTNC();
            return null;
        }
    }

    private void processGetTNC() {
        Server.getAPIService().getTNC(1).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        String cekdataTnC = dataObj.getJSONObject("data").getString("data");
                        if (cekdataTnC.contains("{")) {
                            JSONObject labelTNC = new JSONObject(cekdataTnC);
                            String language = sessions.getLANG();
                            if (language.equals("id")) {
                                dataTnC = labelTNC.getString("labelIdn");
                            } else {
                                dataTnC = labelTNC.getString("labelEng");
                            }
                        } else {
                            dataTnC = cekdataTnC;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = "";
                    if (response.body() != null) {
                        String dataS = response.body().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            msg = dataObj.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (response.errorBody().toString().isEmpty()) {
                            String dataS = response.errorBody().toString();
                            try {
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String dataS = null;
                            try {
                                dataS = response.errorBody().string();
                                JSONObject dataObj = new JSONObject(dataS);
                                msg = dataObj.getString("message");
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (isSessionZoom) {
                    BaseMeetingActivity.showProgress(false);
                } else {
                    DipsSwafoto.showProgress(false);
                }
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopUpTnc(){
        flagViewTNC = true;
        Log.e("CEK","MASUK PopUpTnc");
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialogTNC = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        TextView tvBody = dialogView.findViewById(R.id.tvBody);
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setClickable(false);

        sweetAlertDialogTNC.setCustomView(dialogView);
        sweetAlertDialogTNC.hideConfirmButton();
        sweetAlertDialogTNC.setCancelable(true);

        if (!dataTnC.isEmpty()) {
            tvBody.setText(Html.fromHtml(dataTnC, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int idx = source.indexOf(",");
                    idx += 1;
                    String new_source = source.substring(idx);
                    byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                    int intH = d.getIntrinsicHeight();
                    int intW = d.getIntrinsicWidth();
                    d.setBounds(0, 0, intW, intH);
                    return d;
                }
            }, null));
        }

        sweetAlertDialogTNC.show();

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        Log.e("CEK","PopUpTnc width : "+width+" | height : "+height);
        int newWidth = (int)(width*0.8);
        int newHeight = (int)(height*0.85);
        Log.e("CEK","PopUpTnc newWidth : "+newWidth+" | newHeight : "+newHeight);

        //sweetAlertDialogTNC.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        sweetAlertDialogTNC.getWindow().setLayout(newWidth,newHeight);
        sweetAlertDialogTNC.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                flagViewTNC = false;
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    Log.d("CHECK","TRUE");
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.zm_button));
                    btn.setClickable(true);
                    JSONObject tncCheckObj = new JSONObject();
                    try {
                        tncCheckObj.put("tnc1",true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(tncCheckObj);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btn.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.btnFalse));
                    btn.setClickable(false);
                    JSONObject tncCheckObj = new JSONObject();
                    try {
                        tncCheckObj.put("tnc1",false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RabbitMirroring.MirroringSendKey(tncCheckObj);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    sweetAlertDialogTNC.cancel();
                    sweetAlertDialogTNC.dismissWithAnimation();
                    if (sessions.getNoCIF() == null || sessions.getNoCIF().isEmpty()) {
                        sessions.saveIsCust(isCust);
                        sessions.saveIsSwafoto(isSwafoto);
                        sessions.saveFormCOde(4);
                        Fragment fragment = new frag_cif_new();
                        RabbitMirroring.MirroringSendEndpoint(4);
                        getFragmentPage(fragment);
                    } else {
                        sessions.saveFormCOde(150);
                        Fragment fragment = new frag_ready_account();
                        RabbitMirroring.MirroringSendEndpoint(150);
                        getFragmentPage(fragment);
                        //Toast.makeText(mContext,"Halaman belum tersedia",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    btn.setClickable(false);
                }
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

    private void addData(){
        dataItems = new ArrayList<>();
        dataItems.add(new ItemModel("0",getResources().getString(R.string.perorangan),R.drawable.v_people)); //Sudah
        dataItems.add(new ItemModel("1",getString(R.string.badan_usaha),R.drawable.v_building)); //Sudah
        dataItems.add(new ItemModel("2",getString(R.string.join_account),R.drawable.v_people3)); //Sudah

        dataItems2 = new ArrayList<>();
        dataItems2.add(new ItemModel("0",getString(R.string.tunggal_account),R.drawable.v_people2)); //Sudah
        dataItems2.add(new ItemModel("1",getResources().getString(R.string.join_account_and),R.drawable.v_people4)); //Sudah
        dataItems2.add(new ItemModel("2",getResources().getString(R.string.join_account_or),R.drawable.v_people5)); //Sudah
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}