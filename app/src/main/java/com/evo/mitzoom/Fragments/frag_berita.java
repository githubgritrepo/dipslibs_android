package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSlide;
import com.evo.mitzoom.Adapter.GridProductAdapter;
import com.evo.mitzoom.Helper.OutboundService;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_berita extends Fragment {
    private Context context;
    int [] gambar = {R.drawable.rtgs, R.drawable.rtgs, R.drawable.ads3, R.drawable.ads4,R.drawable.ads1, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4,R.drawable.ads1, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4,R.drawable.ads1, R.drawable.ads2, R.drawable.ads3, R.drawable.ads4};
    private RecyclerView rv_product;
    private ViewPager mPager;
    private GridProductAdapter gridAdapter;
    private static final Integer[]img = {R.drawable.adsv1, R.drawable.adsv2, R.drawable.adsv3};
    private ArrayList<Integer> imgArray = new ArrayList<Integer>();
    private CircleIndicator circleIndicator;
    private int currentPage;
    private MaterialButton btnSchedule,btnSchedule2, btnEndCall;
    String [] time = {"08.00 - 10.00", "10.00 - 12.00", "12.00 - 14.00", "14.00 - 16.00", "16.00 - 17.00"};
    private int year, month, day, waktu_tunggu = 6000;
    private String tanggal, waktu;
    private String Savetanggal;
    private int Savewaktu;
    private SessionManager sessions;
    private String idDips;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        sessions = new SessionManager(context);
        idDips = sessions.getKEY_IdDips();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_berita, container, false);
        rv_product = view.findViewById(R.id.rv_product);
        mPager = view.findViewById(R.id.pager);
        circleIndicator = view.findViewById(R.id.indicator);
        btnSchedule = (MaterialButton) view.findViewById(R.id.btnSchedule);
        btnEndCall = (MaterialButton) view.findViewById(R.id.end_call);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_product.setLayoutManager(new GridLayoutManager(context,2));
        gridAdapter = new GridProductAdapter(context,gambar);
        rv_product.setAdapter(gridAdapter);
        initPager();

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpSchedule();
            }
        });
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndCall();
            }
        });

    }
    private void EndCall(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.headline_endcall));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.end_call));
        sweetAlertDialog.setCancelText(getResources().getString(R.string.no));
        sweetAlertDialog.show();
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                Toast.makeText(context,getResources().getString(R.string.end_call2), Toast.LENGTH_LONG);
                OutApps();

            }
        });
        Button btnCancel = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.cancel_button);
        btnCancel.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
    }
    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ((Activity)context).overridePendingTransition(0,0);
        //System.exit(0);
    }
    private void initPager() {
        for (int i = 0; i < img.length; i++) {
            imgArray.add(img[i]);
            mPager.setAdapter(new AdapterSlide(imgArray, context));
            circleIndicator.setViewPager(mPager);
        }

        Handler handler = new Handler();
        Runnable updates = new Runnable() {
            @Override
            public void run() {
                if (currentPage == img.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage, true);
                currentPage++;
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updates);
            }
        }, 2500, 2500);
    }

    private void PopUpSchedule(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_schedule, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(context,R.layout.list_item, time);
        ImageView btnclose = (ImageView) dialogView.findViewById(R.id.btn_close_schedule);
        EditText et_Date = (EditText) dialogView.findViewById(R.id.et_Date);
        AutoCompleteTextView et_time = (AutoCompleteTextView) dialogView.findViewById(R.id.et_time);
        et_time.setAdapter(adapterTime);
        et_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Savewaktu = position;

            }
        });
        btnSchedule2 = dialogView.findViewById(R.id.btnSchedule2);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int addmonths = (month + 1);
                        String months = String.valueOf(addmonths);
                        if (addmonths < 10) {
                            months = "0"+months;
                        }
                        tanggal = dayOfMonth+"/"+months+"/"+year;
                        Savetanggal = year+""+months+""+dayOfMonth;
                        et_Date.setText(tanggal);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
            }
        });
        btnSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggal = et_Date.getText().toString();
                waktu = et_time.getText().toString();
                if (tanggal.trim().equals("")){
                    Toast.makeText(context.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else if (waktu.trim().equals("")){
                    Toast.makeText(context.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else {
                    saveSchedule();
                    Toast.makeText(context.getApplicationContext(), getResources().getString(R.string.schedule)+tanggal+" & "+getResources().getString(R.string.jam)+waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setContentText(getResources().getString(R.string.content_after_schedule));
                    sweetAlertDialog.setConfirmText(getResources().getString(R.string.done));
                    sweetAlertDialog.show();
                    Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
                    btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sweetAlertDialog.dismiss();
                            OutApps();
                        }
                    });
                }

            }
        });
        btnSchedule2.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
    }
    private void saveSchedule(){
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("tanggal",Savetanggal);
            jsons.put("grup",Savewaktu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("PARAMS JADWAL","idDips = "+idDips+", Tanggal = "+Savetanggal+", Grup index Time of ["+Savewaktu+"]");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.saveSchedule(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }
}
