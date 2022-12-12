package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;

public class frag_list_produk extends Fragment {
    private Context context;
    private NestedScrollView nested;
    private TextView btn_tabungan_a, btn_tabungan_b;
    private ImageView btnBack;
    private RelativeLayout rlOpenAccount;
    private boolean isSessionZoom = false;
    private SweetAlertDialog sweetAlertDialogTNC = null;
    private View dialogView;
    private boolean isCust = false;
    private boolean isSwafoto = false;
    private SessionManager sessions;
    private RabbitMirroring rabbitMirroring;
    private String dataTnC = "";
    private TextView tvTitleList;
    private RelativeLayout rlProduct1;
    private String bodyProduk = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        isSessionZoom = ZoomVideoSDK.getInstance().isInSession();
        if (isSessionZoom) {
            rabbitMirroring = new RabbitMirroring(context);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_daftar_produk, container, false);
        rlOpenAccount = (RelativeLayout) view.findViewById(R.id.rlOpenAccount);
        btn_tabungan_a = view.findViewById(R.id.btn_tabungan_a);
        btn_tabungan_b = view.findViewById(R.id.btn_tabungan_b);
        rlProduct1 = (RelativeLayout) view.findViewById(R.id.rlProduct1);
        tvTitleList = (TextView) view.findViewById(R.id.tvTitleList);
        btnBack = view.findViewById(R.id.btn_back6);
        nested = view.findViewById(R.id.nested);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncProcess().execute();

        if (isSessionZoom) {
            btnBack.setVisibility(View.GONE);
        }
        rlOpenAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rabbitMirroring.MirroringSendEndpoint(0);
                rabbitMirroring.MirroringSendEndpoint(361);
                PopUpTnc();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentPage(new frag_berita());
            }
        });
        btn_tabungan_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tabungan = "Tabungan A";
                Bundle bundle = new Bundle();
                bundle.putString("headline",Tabungan);
                sendDataFragment(bundle,new frag_tabungan());
            }
        });
        btn_tabungan_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tabungan = "Tabungan B";
                Bundle bundle = new Bundle();
                bundle.putString("headline",Tabungan);
                sendDataFragment(bundle,new frag_tabungan());
            }
        });

        rlProduct1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Tabungan = tvTitleList.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("headline",Tabungan);
                bundle.putByteArray("body",bodyProduk.getBytes(StandardCharsets.UTF_8));
                sendDataFragment(bundle,new frag_tabungan());
            }
        });
    }

    private class AsyncProcess extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            processGetProduct();
            processGetTNC();
            return null;
        }
    }

    private void processGetTNC() {
        Server.getAPIService().getTNC(1).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        dataTnC = dataObj.getJSONObject("data").getString("data");
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
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PopUpTnc(){
        Log.e("CEK","MASUK PopUpTnc");
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (sweetAlertDialogTNC == null) {
            dialogView = inflater.inflate(R.layout.item_tnc,null);
            sweetAlertDialogTNC = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
            sweetAlertDialogTNC.setCustomView(dialogView);
            sweetAlertDialogTNC.hideConfirmButton();
            sweetAlertDialogTNC.setCancelable(false);
        }
        TextView tvBody = (TextView) dialogView.findViewById(R.id.tvBody);
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);

        if (!dataTnC.isEmpty()) {
            tvBody.setText(Html.fromHtml(dataTnC, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    int idx = source.indexOf(",");
                    idx += 1;
                    String new_source = source.substring(idx);
                    byte[] data = Base64.decode(new_source, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Drawable d = new BitmapDrawable(((Activity) context).getResources(), bitmap);
                    int intH = d.getIntrinsicHeight();
                    int intW = d.getIntrinsicWidth();
                    d.setBounds(0, 0, intW, intH);
                    return d;
                }
            }, null));
        }
        btn.setClickable(false);

        sweetAlertDialogTNC.show();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    Log.d("CHECK","TRUE");
                    btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btn.setClickable(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btn.setClickable(false);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    sweetAlertDialogTNC.dismiss();
                    sweetAlertDialogTNC.cancel();
                    sessions.saveIsCust(isCust);
                    sessions.saveIsSwafoto(isSwafoto);
                    sessions.saveFormCOde(4);
                    Fragment fragment = new frag_cif();
                    RabbitMirroring.MirroringSendEndpoint(4);
                    getFragmentPage(fragment);
                }
                else {
                    btn.setClickable(false);
                }
            }
        });
    }

    private void processGetProduct() {
        Server.getAPIWAITING_PRODUCT().getNewProductPublish().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONArray dataRows = dataObj.getJSONArray("data");
                        for (int i = 0; i < dataRows.length(); i++) {
                            JSONObject produkId = dataRows.getJSONObject(i).getJSONObject("produkId");
                            String namaProduk = produkId.getString("namaProduk");
                            bodyProduk = produkId.getString("body");
                            String lownamaProduk = namaProduk.toLowerCase();
                            if (lownamaProduk.contains("tabungan")) {
                                tvTitleList.setText(namaProduk);
                                break;
                            }
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
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
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

    private void sendDataFragment(Bundle bundle, Fragment fragment){
        fragment.setArguments(bundle);
        getFragmentPage(fragment);
    }
}
