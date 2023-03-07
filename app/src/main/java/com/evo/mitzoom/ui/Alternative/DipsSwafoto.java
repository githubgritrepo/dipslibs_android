package com.evo.mitzoom.ui.Alternative;

import static com.evo.mitzoom.Helper.MyWorker.EXTRA_START;
import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Fragments.frag_cif_new;
import com.evo.mitzoom.Fragments.frag_inputdata_new;
import com.evo.mitzoom.Helper.MyWorker;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsWaitingRoom;
import com.evo.mitzoom.view.CircularSurfaceView;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DipsSwafoto extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private Context mContext;
    SensorManager sm;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private CircularSurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    public static int CAM_ID = 0;
    private static final String KEY_USE_FACING = "use_facing";
    public static Integer useFacing = null;
    private Camera camera = null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;
    private SessionManager sessions;
    private DisplayMetrics displayMetrics;
    String idDips;
    boolean isCust = false;
    String custName;
    private Button btnSchedule;
    private Button btnEndCall;
    ArrayList<String> time = new ArrayList<>();
    List<Integer> periodeInt = new ArrayList<>();
    HashMap<Integer,String> dataPeriode = new HashMap<>();
    HashMap<String,Integer> dataPeriodeId = new HashMap<>();
    private int year;
    private int month;
    private int day;
    private final int waktu_tunggu = 6000;
    private String tanggal, waktu;
    private String Savetanggal;
    private List<Integer> indeksNotFound;
    private DatePickerDialog dpd;
    private EditText et_Date;
    private Spinner et_time;
    private JSONArray tanggalPenuh;
    private JSONArray periodePenuh;
    private Button btnSchedule2;
    public static RelativeLayout rlprogress;
    Camera.Parameters parameters;
    public boolean surfaceCreated = false;
    private boolean isConfigure;
    private WorkManager workManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        setLocale(this,lang);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_swafoto);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sessions.saveRTGS(null);
        sessions.saveCSID(null);
        sessions.saveIsSwafoto(false);
        idDips = sessions.getKEY_IdDips();
        isCust = sessions.getKEY_iSCust();

        CardView cardSurf = findViewById(R.id.cardSurf);
        preview = findViewById(R.id.mySurface);
        btnSchedule = findViewById(R.id.btnSchedule);
        btnEndCall = findViewById(R.id.end_call);
        rlprogress = findViewById(R.id.rlprogress);

        previewHolder();

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);

        custName = getIntent().getExtras().getString("CUSTNAME");
        int formCode = getIntent().getExtras().getInt("formCode");
        boolean ocrKTP = getIntent().getExtras().getBoolean("OCRKTP");

        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (formCode == 22) {
            sessions.saveFormCOde(formCode);
            fragment = new frag_cif_new();
            bundle.putBoolean("OCRKTP",ocrKTP);
        } else {
            fragment = new frag_inputdata_new();
            sessions.saveIsCust(isCust);
            bundle.putString("CUSTNAME", custName);
        }
        fragment.setArguments(bundle);
        getFragmentPage(fragment);

        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_sweet, null);
        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_info));
        tvTitleDialog.setVisibility(View.GONE);
        tvBodyDialog.setText(getString(R.string.prepare_id_card));
        btnCancelDialog.setVisibility(View.GONE);

        SweetAlertDialog sweet = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        sweet.setCustomView(dialogView);
        sweet.setCancelable(false);
        sweet.hideConfirmButton();
        sweet.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweet.dismissWithAnimation();
            }
        });

        sessions.saveScheduledDate(null);
        sessions.saveScheduledTime(null);
        new AsyncProcess().execute();

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpSchedule();
            }
        });

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OutApps();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.e("CEK","CAMERA IS"+cameraConfigured);
            isConfigure = false;
            Log.d("CEK","MASUK onResume");
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                requestPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgress(Boolean bool){

        if (bool){
            rlprogress.setVisibility(View.VISIBLE);
        }else {
            rlprogress.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Log.e("CEK","MASUK ONPAUSE");
            if (inPreview) {
                camera.stopPreview();
            }

            Log.e("CEK INPREVIEW",""+inPreview);
            Log.e("CEK CAMERA pause",""+camera);

            if (camera != null) {
                camera.release();
                camera = null;
                inPreview = false;
                cameraConfigured = false;
                surfaceCreated = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void requestPermission() {
        Log.e("CEK","requestPermission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.e("CEK","WRITE_EXTERNAL_STORAGE");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},  REQUEST_WRITE_PERMISSION);
            } else {
                Log.e("CEK","Camera.open");
                camera = Camera.open(useFacing);
                startPreview();
            }
        } else {
            camera = Camera.open(useFacing);
            startPreview();
        }
    }

    private class AsyncProcess extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            processGetCheckSchedule();
            processGetScheduleTimes();
            return null;
        }
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.e("CEK","onDateSet");
        int addmonths = (month + 1);
        String months = String.valueOf(addmonths);
        if (addmonths < 10) {
            months = "0"+months;
        }
        String days = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10 ) {
            days = "0"+days;
        }
        tanggal = days+"/"+months+"/"+year;
        Savetanggal = year + "-" + months + "-" + days;
        et_Date.setText(tanggal);

        if (periodePenuh.length() > 0) {
            ArrayList<String> times_new = new ArrayList<>(time);
            for (int i = 0; i < periodePenuh.length(); i++) {
                try {
                    String tglFull = periodePenuh.getJSONObject(i).getString("tanggal");
                    int periodeId = periodePenuh.getJSONObject(i).getInt("periodeId");
                    if (tglFull.equals(Savetanggal)) {
                        String valP = dataPeriode.get(periodeId);
                        for (int j = 0; j < times_new.size(); j++) {
                            String times = times_new.get(j);
                            if (valP.equals(times)) {
                                times_new.remove(j);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(mContext,R.layout.list_item, times_new);
            et_time.setAdapter(adapterTime);
        } else {
            ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(mContext,R.layout.list_item, time);
            et_time.setAdapter(adapterTime);
        }
    }

    private void PopUpSchedule(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_schedule, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        ImageView btnclose = dialogView.findViewById(R.id.btn_close_schedule);
        et_Date = dialogView.findViewById(R.id.et_Date);
        et_time = dialogView.findViewById(R.id.et_time);

        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(mContext,R.layout.list_item, time);

        et_time.setAdapter(adapterTime);
        btnSchedule2 = dialogView.findViewById(R.id.btnSchedule2);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                dpd = null;
                if (dpd == null) {
                    dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(DipsSwafoto.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                }
                else {
                    dpd.initialize(
                            DipsSwafoto.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                }

                // restrict to weekdays only
                ArrayList<Calendar> weekdays = new ArrayList<Calendar>();
                Calendar day = Calendar.getInstance();
                int loopAdd = 0;
                for (int i = 0; i < 30; i++) {
                    if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        if (tanggalPenuh.length() > 0) {
                            for (int tg = 0; tg < tanggalPenuh.length(); tg++) {
                                try {
                                    String tglFull = tanggalPenuh.getString(tg);
                                    int yearChk = day.get(Calendar.YEAR);
                                    int monthChk = day.get(Calendar.MONTH);
                                    int dayChk = day.get(Calendar.DAY_OF_MONTH);

                                    int addmonths = (monthChk + 1);
                                    String months = String.valueOf(addmonths);
                                    if (addmonths < 10) {
                                        months = "0"+months;
                                    }
                                    String days = String.valueOf(dayChk);
                                    if (dayChk < 10 ) {
                                        days = "0"+days;
                                    }

                                    String tglChk = yearChk + "-" + months + "-" + days;
                                    if (!tglFull.equals(tglChk)) {
                                        Calendar d = (Calendar) day.clone();
                                        weekdays.add(d);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            Calendar d = (Calendar) day.clone();
                            weekdays.add(d);
                        }
                    }
                    else {
                        loopAdd++;
                    }
                    day.add(Calendar.DATE, 1);
                }
                Calendar[] weekdayDays = weekdays.toArray(new Calendar[weekdays.size()]);
                dpd.setSelectableDays(weekdayDays);
                //dpd.setMaxDate(day);
                dpd.setOnCancelListener(dialog -> {
                    Log.e("DatePickerDialog", "Dialog was cancelled");
                    dpd = null;
                });
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");

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
                tanggal = et_Date.getText().toString().trim().trim();
                waktu = et_time.getSelectedItem().toString();
                if (tanggal.trim().equals("")){
                    Toast.makeText(mContext.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else if (waktu.trim().equals("")){
                    Toast.makeText(mContext.getApplicationContext(), R.string.notif_blank, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (idDips.isEmpty()) {
                        idDips = sessions.getKEY_IdDips();
                    }
                    //Toast.makeText(context.getApplicationContext(), getResources().getString(R.string.schedule) + tanggal + " & " + getResources().getString(R.string.jam) + waktu, Toast.LENGTH_LONG).show();
                    sweetAlertDialog.dismiss();
                    sessions.saveIDSchedule(0);
                    showProgress(true);
                    saveSchedule();
                }

            }
        });
    }

    private void saveSchedule(){
        int periodeId = dataPeriodeId.get(waktu);
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("tanggal",Savetanggal);
            jsons.put("periodeId",periodeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Log.e("CEK","PARAMS saveSchedule : "+ jsons);
        Log.d("PARAMS JADWAL","idDips = "+idDips+", Tanggal = "+Savetanggal);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.saveSchedule(requestBody,authAccess,exchangeToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                showProgress(false);
                Log.e("CEK","saveSchedule Respon Code : "+response.code());
                if (response.isSuccessful() && response.body().size() > 0) {
                    Log.e("CEK","saveSchedule Respon : "+ response.body());

                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int idSchedule = dataObj.getJSONObject("data").getInt("id");
                        sessions.saveIDSchedule(idSchedule);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*if (DipsWaitingRoom.channelCall != null) {
                        try {
                            Log.e("CEK","MASUK channelCall abort close");
                            DipsWaitingRoom.channelCall.close();
                        } catch (IOException | TimeoutException e) {
                            e.printStackTrace();
                        }
                        if (DipsWaitingRoom.subscribeThreadCall != null) {
                            Log.e("CEK","MASUK subscribeThreadCall interrupt");
                            DipsWaitingRoom.subscribeThreadCall.interrupt();
                        }
                    }*/

                    //doWorkMyWorker();
                    serviceOutbound();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

                    ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
                    TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
                    TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
                    Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
                    Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

                    tvTitleDialog.setVisibility(View.GONE);

                    imgDialog.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.v_dialog_success));
                    tvBodyDialog.setText(getString(R.string.content_after_schedule));
                    btnConfirmDialog.setText(getString(R.string.done));

                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
                    sweetAlertDialog.setCustomView(dialogView);
                    sweetAlertDialog.hideConfirmButton();
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();

                    btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sweetAlertDialog.dismiss();
                            OutApps();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showProgress(false);
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void doWorkMyWorker() {
        workManager = WorkManager.getInstance(mContext);

        String jam = waktu;
        Log.e("CEK","doWorkMyWorker Savetanggal : "+Savetanggal);
        Log.e("CEK","doWorkMyWorker jam : "+jam);
        if (jam.indexOf("-") > 0) {
            String[] sp = jam.split("-");
            for (int i = 0; i < sp.length; i++) {
                workManager.cancelUniqueWork(idDips+"_timesStart"+i);
            }

            for (int i = 0; i < sp.length; i++) {
                Calendar currentDate = Calendar.getInstance();

                String[] timeArray = sp[i].split(":");
                String[] spDate = Savetanggal.split("-");
                String thn = spDate[0].toString().trim();
                String getBln = spDate[1].toString().trim();
                int bln = Integer.parseInt(getBln) - 1;
                String tgl = spDate[2].toString().trim();

                Log.e("CEK","DATETIMES-"+i+" : "+Savetanggal+" "+sp[i]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(thn),bln,Integer.parseInt(tgl));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
                calendar.set(Calendar.SECOND, 0);

                long timeCurrentMilis = currentDate.getTimeInMillis();
                long timeInMilis = calendar.getTimeInMillis();
                long timeDiff = timeInMilis - timeCurrentMilis;

                boolean start = true;
                if (i > 0) {
                    start = false;
                }

                Data data = new Data.Builder()
                        .putBoolean(EXTRA_START,start)
                        .build();

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .build();

                workManager.enqueueUniqueWork(idDips+"_timesStart"+i,
                        ExistingWorkPolicy.KEEP, (OneTimeWorkRequest) workRequest);
            }
        } else {
            workManager.cancelUniqueWork(idDips);
            Calendar currentDate = Calendar.getInstance();

            String[] timeArray = jam.split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
            calendar.set(Calendar.SECOND, 0);

            long timeCurrentMilis = currentDate.getTimeInMillis();
            long timeInMilis = calendar.getTimeInMillis();
            long timeDiff = timeInMilis - timeCurrentMilis;

            Data data = new Data.Builder()
                    .putBoolean(EXTRA_START,true)
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .setConstraints(constraints)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build();

            workManager.enqueueUniqueWork(idDips,
                    ExistingWorkPolicy.KEEP, (OneTimeWorkRequest) workRequest);
        }
    }

    private void serviceOutbound() {
        Intent serviceIntent = new Intent(mContext, OutboundServiceNew.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void processGetCheckSchedule() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetCheckSchedule(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        sessions.saveScheduledDate(dataObj.toString());
                        int errCode = dataObj.getInt("code");
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (errCode == 200) {
                            tanggalPenuh = dataObj.getJSONObject("data").getJSONArray("tanggalPenuh");
                            periodePenuh = dataObj.getJSONObject("data").getJSONArray("periodePenuh");

                            Log.e("CEK","tanggalPenuh : "+tanggalPenuh.toString());
                            Log.e("CEK","periodePenuh : "+periodePenuh.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void processGetScheduleTimes() {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().GetScheduleTimes(authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        int errCode = dataObj.getInt("code");
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        if (errCode == 200) {
                            JSONArray dataArrTimes = dataObj.getJSONArray("data");
                            Log.e("CEK","dataArrTimes : "+dataArrTimes);
                            sessions.saveScheduledTime(dataArrTimes.toString());
                            for (int i = 0; i < dataArrTimes.length(); i++) {
                                int periodeId = dataArrTimes.getJSONObject(i).getInt("id");
                                String periode = dataArrTimes.getJSONObject(i).getString("periode");
                                time.add(periode);
                                periodeInt.add(periodeId);
                                dataPeriode.put(periodeId,periode);
                                dataPeriodeId.put(periode,periodeId);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void previewHolder(){
        previewHolder = preview.getHolder();
        previewHolder.setFormat(PixelFormat.TRANSLUCENT);
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Log.e("CEK","surfaceCreated");
            surfaceCreated = true;
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Log.e("CEK","surfaceChanged width : "+width+" | height : "+height);
            initPreview(width, height, holder);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Log.e("CEK","surfaceDestroyed");
        }
    };

    private void initPreview(int width, int height, SurfaceHolder holder) {
        if (camera != null && holder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(holder);
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (manager == null) {
                    Log.i("CEK", "camera manager is null");
                    return;
                }
                try {
                    for (String id: manager.getCameraIdList()) {
                        CAM_ID = Integer.valueOf(id);
                        setCameraDisplayOrientation();
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (!cameraConfigured) {
                isConfigure = true;
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                Camera.Size size = getOptimalPreviewSize(sizes, width, height);

                if (size != null) {
                    Log.e("CEK","size.width : "+size.width+" | size.height : "+size.height);
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    }

    private void startPreview() {
        Log.e("CEK CAMERA",""+camera);
        if (cameraConfigured && camera != null) {
            try
            {
                Thread.sleep(20);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            camera.startPreview();
            parameters = camera.getParameters();
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            camera.setParameters(parameters);
            inPreview = true;
        }
        else{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!surfaceCreated){
                        previewHolder.addCallback(surfaceCallback);
                        cameraConfigured = true;
                        previewHolder();
                        startPreview();
                        Log.e("Masuk sini","");
                    }
                }
            }, 1000);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void setCameraDisplayOrientation(){
        if (camera == null)
        {
            Log.d("CEK","setCameraDisplayOrientation - camera null");
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CAM_ID, info);

        WindowManager winManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            //result = (info.orientation - degrees + 360) % 360;
            result = 180;
        }
        camera.setDisplayOrientation(result);
    }

    private boolean getFragmentPage(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    private void OutApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}