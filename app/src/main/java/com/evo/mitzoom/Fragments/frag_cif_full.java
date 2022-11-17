package com.evo.mitzoom.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterCIF;
import com.evo.mitzoom.Adapter.AdapterPortofolioNew;
import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_cif_full extends Fragment {

    private Context mContext;
    private SessionManager sessions;
    private boolean isCust;
    private boolean isSwafoto;
    private int chkFlow;
    private byte[] bytePhoto;
    private byte[] KTP = new byte[0];
    private byte[] KTP_SWAFOTO = new byte[0];
    private byte[] NPWP = new byte[0];
    private byte[] TTD = new byte[0];
    private int formCode;
    private String idDips;
    private int form_id = 0;
    private View inclHead;
    private SwipeRefreshLayout swipe;
    private LinearLayout TopBar;
    private LinearLayout ll_head;
    private TextView tvFotoKTP;
    private LinearLayout iconKtp;
    private LinearLayout iconNpwp;
    private LinearLayout iconSignature;
    private LinearLayout iconForm;
    private Button btnProses;
    private RecyclerView rv_item_expand;
    private JSONArray dataCIFArr = null;
    private JSONObject objValCIF;
    private int keysCount;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private String numberOTP;
    private String newString = "";
    private Handler handler = null;
    private Runnable myRunnable = null;
    private int getMinutes = 2;
    private int seconds = 60;
    private boolean running = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        sessions = new SessionManager(mContext);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();
        chkFlow = sessions.getFLOW();
        formCode = sessions.getFormCode();
        idDips = sessions.getKEY_IdDips();
        String valDataCIF = sessions.getCIF();
        try {
            objValCIF = new JSONObject(valDataCIF);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (getArguments() != null) {
            if (getArguments().containsKey("ktp")) {
                KTP = getArguments().getByteArray("ktp");
            }
            if (getArguments().containsKey("swafoto")) {
                KTP_SWAFOTO = getArguments().getByteArray("swafoto");
            }
            if (getArguments().containsKey("npwp")) {
                NPWP = getArguments().getByteArray("npwp");
            }
            if (getArguments().containsKey("ttd")) {
                TTD = getArguments().getByteArray("ttd");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_frag_cif_full, container, false);

        inclHead = views.findViewById(R.id.inclHead);
        TopBar = (LinearLayout) views.findViewById(R.id.TopBar);
        ll_head = (LinearLayout) views.findViewById(R.id.ll_head);
        tvFotoKTP = (TextView) views.findViewById(R.id.tvFotoKTP);
        iconKtp = (LinearLayout) views.findViewById(R.id.icon_ktp);
        iconNpwp = (LinearLayout) views.findViewById(R.id.icon_npwp);
        iconSignature = (LinearLayout) views.findViewById(R.id.icon_signature);
        iconForm = (LinearLayout) views.findViewById(R.id.icon_form);

        swipe = (SwipeRefreshLayout) views.findViewById(R.id.swipe);

        rv_item_expand = (RecyclerView) views.findViewById(R.id.rv_item_expand);

        btnProses = (Button) views.findViewById(R.id.btnProses);

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                processGetCIFForm();
            }
        });

        keysCount = 0;
        for(Iterator<String> iter = objValCIF.keys(); iter.hasNext();) {
            String key = iter.next();
            keysCount++;
        }
        Log.e("CEK","keysCount : "+keysCount);

        processGetCIFForm();

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"SEDANG DIPROSES...!!",Toast.LENGTH_SHORT).show();
                int childRecyle = rv_item_expand.getChildCount();
                Log.e("CEK","childRecyle : "+childRecyle);
                if (childRecyle > 0) {
                    for (int i = 0; i < childRecyle; i++) {
                        if (rv_item_expand.getChildAt(i) instanceof LinearLayout) {
                            int countChild = ((LinearLayout) rv_item_expand.getChildAt(i)).getChildCount();
                            Log.e("CEK","countChild : "+countChild);
                            if (countChild > 0) {
                                if (((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0) instanceof ExpansionHeader) {
                                    if (((ExpansionHeader) ((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0)).getChildAt(0) instanceof RelativeLayout) {
                                        TextView tvHeader = (TextView) ((ExpansionHeader) ((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0)).getChildAt(0).findViewById(R.id.tv_nama_product);
                                        Log.e("CEK","tvHeader : "+tvHeader.getText());
                                    }
                                }

                                if (((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0) instanceof ExpansionLayout) {
                                    LinearLayout container = (LinearLayout) ((LinearLayout) rv_item_expand.getChildAt(i)).getChildAt(0).findViewById(R.id.container);
                                    int childBodyExpand = container.getChildCount();
                                    Log.e("CEK","childBodyExpand : "+childBodyExpand);
                                }
                            }
                        }
                    }
                }

                PopUpOTP();

                /*int child = llFormBuild.getChildCount();

                if (child > 0 && idElement.length() > 0) {
                    boolean flagNext = true;
                    for (int i = 0; i < child; i++) {
                        boolean checkEmpty = false;
                        int idEl = llFormBuild.getChildAt(i).getId();
                        if (idEl > 0 || idEl < -1) {
                            for (int j = 0; j < idElement.length(); j++) {
                                try {
                                    int idDataEl = idElement.getJSONObject(j).getInt("id");
                                    String nameDataEl = idElement.getJSONObject(j).getString("name");
                                    boolean requiredDataEl = idElement.getJSONObject(j).getBoolean("required");
                                    if (idEl == idDataEl) {

                                        if (llFormBuild.getChildAt(i) instanceof EditText) {
                                            Log.e("CEK", "MASUK EDITTEXT ke-" + i);
                                            EditText ed = (EditText) llFormBuild.getChildAt(i);
                                            String results = ed.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                            Log.e("CEK", "MASUK RadioGroup ke-" + i);
                                            RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = (RadioButton) rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                            Log.e("CEK", "MASUK CheckBox ke-" + i);
                                            CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                            boolean isChk = chk.isChecked();
                                            if (isChk) {
                                                objEl.put(nameDataEl, isChk);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                            Log.e("CEK", "MASUK Spinner ke-" + i);
                                            Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                            if (spin.isSelected()) {
                                                String results = spin.getSelectedItem().toString();
                                                if (requiredDataEl && results.isEmpty()) {
                                                    Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                    checkEmpty = true;
                                                }
                                                objEl.put(nameDataEl, results);
                                            }
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                            Log.e("CEK", "MASUK AutoCompleteTextView ke-" + i);
                                            AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                            String results = autoText.getText().toString();
                                            if (requiredDataEl && results.isEmpty()) {
                                                Toast.makeText(mContext, nameDataEl + " harus diisi/dipilih", Toast.LENGTH_SHORT).show();
                                                checkEmpty = true;
                                                break;
                                            }
                                            objEl.put(nameDataEl, results);
                                            break;
                                        } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                            Log.e("CEK", "MASUK LinearLayout ke-" + i);
                                            LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (checkEmpty) {
                                flagNext = false;
                                break;
                            } else {
                                flagNext = true;
                            }
                        }
                    }
                }*/
            }
        });
    }

    private void processGetCIFForm() {
        int formId = 10;
        processGetForm(formId);
    }

    private void processGetForm(int formId) {
        Log.e("CEK", this+" MASUK processGetForm");
        Server.getAPIWAITING_PRODUCT().getFormBuilder(formId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swipe.setRefreshing(false);
                Log.e("CEK","response processGetForm : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","response dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        JSONObject dataObjForm = dataObj.getJSONObject("data");
                        String dataForm = dataObjForm.getString("data");
                        Log.e("CEK","dataForm : "+dataForm);
                        if (formId == 10) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("nameForm","datadiri");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                            if (objValCIF.has("alamatberbeda")) {
                                processGetForm(9);
                            }
                        } else if (formId == 9) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("nameForm","alamatberbeda");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                            if (objValCIF.has("pekerjaan")) {
                                processGetForm(5);
                            }
                        } else if (formId == 5) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("nameForm","pekerjaan");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                            if (objValCIF.has("keuangan")) {
                                processGetForm(14);
                            }
                        } else if (formId == 14) {
                            JSONArray arr = new JSONArray(dataForm);
                            JSONObject obj = new JSONObject();
                            obj.put("nameForm","keuangan");
                            obj.put("dataList",arr);
                            dataCIFArr.put(obj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("CEK","keysCount : "+keysCount+" | dataCIFArr.length : "+dataCIFArr.length());
                    if (keysCount == dataCIFArr.length()) {
                        setRecylerExpand();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }

    private void setRecylerExpand() {
        Log.e("CEK","MASUK setRecylerExpand");

        AdapterCIF dataExpand = new AdapterCIF(dataCIFArr, mContext);

        recylerViewLayoutManager = new LinearLayoutManager(mContext);

        rv_item_expand.setLayoutManager(recylerViewLayoutManager);
        rv_item_expand.setAdapter(dataExpand);
    }

    private void PopUpOTP(){
        //String sub_no_handphone = no_handphone.substring(no_handphone.length() - 3);
        String noHandphone = "089783434XXX";//no_handphone.replace(sub_no_handphone,"XXX");

        View dialogView = getLayoutInflater().inflate(R.layout.item_otp, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        TextView textIBMB = (TextView) dialogView.findViewById(R.id.textIBMB);
        String contentText = textIBMB.getText().toString();
        contentText = contentText.replace("+62812 3456 7XXX",noHandphone);
        textIBMB.setText(contentText);

        Button btnVerifikasi = (Button) dialogView.findViewById(R.id.btnVerifikasi);
        TextView Timer = (TextView) dialogView.findViewById(R.id.timer_otp);
        TextView Resend_Otp = (TextView) dialogView.findViewById(R.id.btn_resend_otp);
        PinView otp = (PinView) dialogView.findViewById(R.id.otp);
        otp.setAnimationEnable(true);
        otp.addTextChangedListener(new TextWatcher() {
            private boolean backSpaceOTP;
            private int lasLenOTP;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lasLenOTP = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String wordOTP = s.toString();
                String patternStr = "[0-9]";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    String getNumberOTP=wordOTP.replaceAll("[^0-9]", "");
                    if (getNumberOTP.length() > 1 && getNumberOTP.length() <= 6) {
                        getNumberOTP = getNumberOTP.substring(getNumberOTP.length()-1,getNumberOTP.length());
                    }
                    if (numberOTP.length() < 6) {
                        numberOTP += getNumberOTP;
                    }
                    //MirroringOTP(numberOTP,false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpaceOTP = lasLenOTP > s.length();
                Log.e("CEK", "backSpaceOTP : " + backSpaceOTP);
                if (backSpaceOTP) {
                    int lenOTP = numberOTP.length();
                    if (lenOTP > 0) {
                        numberOTP = numberOTP.substring(0, lenOTP - 1);
                    }
                }
                newString = myFilter(s.toString());
                otp.removeTextChangedListener(this);
                handler = new Handler();
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        otp.setText(newString);
                    }
                };
                otp.addTextChangedListener(this);
                handler.postDelayed(myRunnable, 1500);
                if (otp.length() == 6 || otp.length() == 0){
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    Log.d("TAG","STOP Loop");
                }
            }
        });

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(mContext, "Kode Otp masih kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    //MirroringOTP(otp.getText().toString(),true);
                    sweetAlertDialog.dismiss();
                    PopUpSuccesOtp();
                    //verifyOTP();
                }
            }
        });
        runTimer(Timer, Resend_Otp);
        Resend_Otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds==0){
                    //resendOTP();
                }
            }
        });
    }

    private void PopUpSuccesOtp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.otp_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.otp_content));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sweetAlertDialog.dismiss();
                PopUpSuccesRegistration();
            }
        },5000);
    }

    private void PopUpSuccesRegistration(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.reg_title));
        sweetAlertDialog.setContentText(getResources().getString(R.string.reg_content));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.activation));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.Blue));
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                PopUpTnc();
            }
        });
    }

    private void PopUpTnc(){
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_tnc, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismiss();
                getFragmentPage(new frag_aktivasi_ibmb());
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

    public String myFilter(String s) {
        String digits;
        digits = s.replaceAll("[0-9]", "*");
        if (s.equals("")) return "";
        return digits;
    }
    public void runTimer(TextView timer_run, TextView resend) {
        Handler handlerTimer = new Handler();
        handlerTimer.post(new Runnable() {
            @Override
            public void run() {
                int minutes = getMinutes;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds--;
                }
                if (seconds == 0 && minutes == 0){
                    running = false;
                    resend.setClickable(true);
                } else if (seconds == 0 && minutes > 0){
                    seconds = 59;
                }
                if (seconds == 59) {
                    getMinutes--;
                }
                handlerTimer.postDelayed(this,1000);
            }
        });
    }

}