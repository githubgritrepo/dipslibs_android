package com.evo.mitzoom.Helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.AdapterSourceAccount;
import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyParserFormBuilder {

    private static JSONArray dataArrObj;
    private static Context mContext;
    private static LinearLayout llFormBuild;
    private static SessionManager sessions;
    private static String language;
    private static String idService = "";

    public MyParserFormBuilder(Context mContext, String dataForm, LinearLayout llFormBuild) throws JSONException {
        dataArrObj = new JSONArray(dataForm);
        MyParserFormBuilder.mContext = mContext;
        MyParserFormBuilder.llFormBuild = llFormBuild;
        sessions = new SessionManager(mContext);
        language = sessions.getLANG();
    }

    public MyParserFormBuilder(Context mContext, String dataForm, LinearLayout llFormBuild, String idServiceTrx) throws JSONException {
        dataArrObj = new JSONArray(dataForm);
        MyParserFormBuilder.mContext = mContext;
        MyParserFormBuilder.llFormBuild = llFormBuild;
        sessions = new SessionManager(mContext);
        language = sessions.getLANG();
        idService = idServiceTrx;
    }

    public static JSONArray getForm() {
        JSONArray dataArrElement = new JSONArray();
        int len = dataArrObj.length();

        for (int i = 0; i < len; i++) {
            try {
                String dataS = dataArrObj.get(i).toString();
                JSONObject dataObj = new JSONObject(dataS);
                String compName = dataObj.getString("name");
                compName = compName.toLowerCase();
                JSONArray components = dataObj.getJSONArray("components");
                RadioGroup radioGroup = null;
                int compLen = components.length();
                String compLabelRad = "";
                String elNameRad = "";
                String keyLabelIndRad = "";
                boolean radGroup = false;
                boolean finishRad = false;
                int jkRad = 0;
                int radB = 0;
                String parentLabel = "";
                String parentLabelIndo = "";
                for (int j = 0; j < compLen; j++) {
                    String pcPlaceIdn = "";
                    String keyLabel = "";
                    LinearLayout.LayoutParams lpLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpLayout.setMargins(0,0,0,10);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                    String compS = components.get(j).toString();
                    JSONObject compObj = new JSONObject(compS);
                    String compNameInside = compObj.getString("name").toLowerCase();
                    String compPlaceholder = compObj.getString("placeholder");
                    String compLabel = compObj.getString("label");
                    
                    if (compPlaceholder.contains("{")) {
                        JSONObject placeholderObj = compObj.getJSONObject("placeholder");
                        pcPlaceIdn = placeholderObj.getString("placeholderIdn");
                        String pcEng = placeholderObj.getString("placeholderEng");
                        if (language.equals("id")) {
                            compPlaceholder = pcPlaceIdn;
                        } else {
                            compPlaceholder = pcEng;
                        }
                    }

                    if (compLabel.contains("{")) {
                        JSONObject labelObj = compObj.getJSONObject("label");
                        String pcIdn = labelObj.getString("labelIdn");
                        String pcEng = labelObj.getString("labelEng");
                        keyLabel = pcIdn;
                        if (language.equals("id")) {
                            compLabel = pcIdn;
                        } else {
                            compLabel = pcEng;
                        }
                    }
                    
                    boolean compRequired = false;
                    boolean compDisabled = true;
                    boolean compHidden = false;
                    boolean capitalize = false;
                    int maxLength = 0;
                    if (compObj.has("required")) {
                        compRequired = compObj.getBoolean("required");
                    } else if (compObj.has("props")) {
                        if (compObj.getJSONObject("props").has("required")) {
                            compRequired = compObj.getJSONObject("props").getBoolean("required");
                        }

                        if (compObj.getJSONObject("props").has("disabled")) {
                            boolean getDisabled = compObj.getJSONObject("props").getBoolean("disabled");
                            compDisabled = !getDisabled;
                        }

                        if (compObj.getJSONObject("props").has("hidden")) {
                            compHidden = compObj.getJSONObject("props").getBoolean("hidden");
                        }

                        if (compObj.getJSONObject("props").has("maxLength")) {
                            maxLength = compObj.getJSONObject("props").getInt("maxLength");
                        }

                        if (compObj.getJSONObject("props").has("capitalize")) {
                            capitalize = compObj.getJSONObject("props").getBoolean("capitalize");
                        }
                    }
                    String urlPath = "";
                    if (compObj.has("url")) {
                        if (!compObj.isNull("url") && !compObj.toString().isEmpty()) {
                            urlPath = compObj.getString("url");
                        }
                    }

                    String compType = compObj.getString("type");
                    String compTagHTML = compObj.getString("tagHTML");

                    if (!compHidden) {
                        switch (compTagHTML) {
                            case "input":
                                JSONObject dataObjEl = new JSONObject();
                                if (compType.equals("text") || compType.equals("date")) {
                                    if (!compLabel.isEmpty() && !compName.equals("checkbox") && !compName.equals("radiobutton")) {
                                        LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lpTv.setMargins(0, 15, 0, 0);
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lpTv);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    int intAplhabet = randomId();

                                    EditText ed = new EditText(mContext);
                                    ed.setId(intAplhabet);
                                    ed.setHint(compPlaceholder);
                                    ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    ed.setGravity(Gravity.CENTER_VERTICAL);
                                    ed.setIncludeFontPadding(false);
                                    ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                    ed.setPadding(20, 20, 20, 20);
                                    ed.setEnabled(compDisabled);
                                    ed.setClickable(compDisabled);
                                    if (maxLength > 0) {
                                        ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                                    }
                                    if (!compDisabled) {
                                        ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput_disable));
                                        ed.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                    }
                                    if (compType.equals("date")) {
                                        ed.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                                        ed.setClickable(false);
                                        ed.setFocusable(false);
                                        ed.setFocusableInTouchMode(false);
                                        ed.setCursorVisible(false);
                                        ed.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext,R.drawable.ic_date), null);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            ed.setCompoundDrawableTintList(mContext.getResources().getColorStateList(R.color.zm_grey));
                                        }

                                        Calendar currentTime = Calendar.getInstance();

                                        DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                currentTime.set(Calendar.YEAR, year);
                                                currentTime.set(Calendar.MONTH, month);
                                                currentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                                String Tgl = String.format("%02d", dayOfMonth);
                                                String bln = String.format("%02d", month + 1);

                                                //String getDates = updateDates(currentTime);
                                                String getDates = Tgl + "-" + bln + "-" + year;
                                                ed.setText(getDates);
                                            }
                                        };

                                        ed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new DatePickerDialog(mContext, dateDialog, currentTime
                                                        .get(Calendar.YEAR), currentTime.get(Calendar.MONTH),
                                                        currentTime.get(Calendar.DAY_OF_MONTH)).show();
                                            }
                                        });
                                    } else {
                                        ed.setHint(compPlaceholder);
                                        String lowLabel = keyLabel.toLowerCase();
                                        int indx = lowLabel.indexOf("(");
                                        String valKurung = "";
                                        if (indx >= 0) {
                                            valKurung = " " + lowLabel.substring(indx);
                                        }

                                        if (lowLabel.equals("npwp" + valKurung)) {
                                            ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                                        } else if (lowLabel.equals("nik" + valKurung)) {
                                            ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                                        }

                                        if (lowLabel.contains("email")) {
                                            if (capitalize) {
                                                ed.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                                            } else {
                                                ed.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                            }
                                        } else if (lowLabel.equals("nik" + valKurung) || lowLabel.equals("npwp" + valKurung) || lowLabel.equals("rt" + valKurung) || lowLabel.equals("rw" + valKurung) || lowLabel.contains("kode" + valKurung) ||
                                                ((lowLabel.contains("no.") || lowLabel.contains("nomor")) && ((lowLabel.contains("telp") ||
                                                lowLabel.contains("telepon")) || lowLabel.contains("handphone")))) {
                                            ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                        } else {
                                            if (capitalize) {
                                                ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                                            } else {
                                                ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                                            }
                                        }
                                    }

                                    int ids = ed.getId();
                                    String elName = "";
                                    String keyLabelInd = "";
                                    String compLabelGab2 = compLabel;
                                    if (compName.equals("checkbox")) {
                                        compLabelGab2 = parentLabel + " " + compLabel;
                                        String compLabelGab = parentLabel + compLabel;
                                        String compLabelGabIndo = parentLabelIndo + keyLabel;
                                        elName = compLabelGab.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        keyLabelInd = compLabelGabIndo.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        ed.setEnabled(false);
                                        ed.setFocusable(false);
                                    }
                                    if (compName.equals("radiobutton")) {
                                        compLabelGab2 = parentLabel + " " + compLabel;
                                        String compLabelGab = parentLabel + compLabel;
                                        String compLabelGabIndo = parentLabelIndo + keyLabel;
                                        elName = compLabelGab.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        keyLabelInd = compLabelGabIndo.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        ed.setEnabled(false);
                                        ed.setFocusable(false);
                                    } else {
                                        elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    }

                                    if (keyLabelInd.equals("rt") || keyLabelInd.equals("rw")) {
                                        ed.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
                                    }

                                    if (compType.equals("date")) {
                                        if (keyLabelInd.contains("tanggal") && (keyLabelInd.contains("pengaduan") || keyLabelInd.contains("komplain"))) {
                                            String timeDate = new SimpleDateFormat("dd-MM-yyyy",
                                                    Locale.getDefault()).format(new Date());
                                            ed.setText(timeDate);
                                        }
                                    }

                                    ed.setLayoutParams(lp);
                                    if (llFormBuild != null) {
                                        llFormBuild.addView(ed);
                                        if (keyLabelInd.contains("pengguna")) {
                                            TextView tvAlert = new TextView(mContext);
                                            tvAlert.setLayoutParams(lp);
                                            tvAlert.setId(R.id.alert_user);
                                            tvAlert.setTextSize(12);
                                            tvAlert.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                            tvAlert.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                            tvAlert.setVisibility(View.GONE);
                                            llFormBuild.addView(tvAlert);

                                            TextView tvAlertComb = new TextView(mContext);
                                            tvAlertComb.setLayoutParams(lp);
                                            tvAlertComb.setText(R.string.combine_alert);
                                            tvAlertComb.setTextSize(12);
                                            tvAlertComb.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                            llFormBuild.addView(tvAlertComb);
                                        }
                                    }

                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataObjEl.put("label", compLabelGab2);
                                    dataObjEl.put("required", compRequired);
                                    dataArrElement.put(dataObjEl);
                                } else if (compType.equals("password")) {
                                    if (!compLabel.isEmpty()) {
                                        LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lpTv.setMargins(0, 15, 0, 0);
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lpTv);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    int intAplhabet = randomId();

                                    LayoutInflater inflater = LayoutInflater.from(mContext);
                                    RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.layout_password, null, false);
                                    EditText ed = (EditText) rl.findViewById(R.id.edPassword);
                                    ImageView open_eye = (ImageView) rl.findViewById(R.id.open_eye);
                                    ImageView close_eye = (ImageView) rl.findViewById(R.id.close_eye);
                                    ed.setId(intAplhabet);
                                    ed.setHint(compPlaceholder);

                                    int ids = ed.getId();
                                    String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                    if (keyLabelInd.contains("mpin")) {
                                        ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                    } else {
                                        if (capitalize) {
                                            ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        } else {
                                            ed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        }
                                    }
                                    ed.setEnabled(compDisabled);
                                    ed.setClickable(compDisabled);
                                    if (maxLength > 0) {
                                        ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                                    }
                                    open_eye.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ed.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                                            open_eye.setVisibility(View.GONE);
                                            close_eye.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    boolean finalCapitalize = capitalize;
                                    close_eye.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            close_eye.setVisibility(View.GONE);
                                            open_eye.setVisibility(View.VISIBLE);
                                            if (keyLabelInd.contains("mpin")) {
                                                ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                            } else {
                                                if (finalCapitalize) {
                                                    ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                } else {
                                                    ed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                }
                                            }
                                        }
                                    });
                                    if (llFormBuild != null) {
                                        llFormBuild.addView(rl);
                                        if (keyLabelInd.equals("katasandi")) {
                                            TextView tvAlert = new TextView(mContext);
                                            tvAlert.setLayoutParams(lp);
                                            tvAlert.setId(R.id.alert_password);
                                            tvAlert.setTextSize(12);
                                            tvAlert.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                            tvAlert.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                            llFormBuild.addView(tvAlert);

                                            TextView tvAlertRek = new TextView(mContext);
                                            tvAlertRek.setLayoutParams(lp);
                                            tvAlertRek.setText(R.string.combine_alert);
                                            tvAlertRek.setTextSize(12);
                                            tvAlertRek.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                            llFormBuild.addView(tvAlertRek);
                                        } else if (keyLabelInd.contains("konfirm") && (keyLabelInd.contains("sandi"))){
                                            TextView tvAlert = new TextView(mContext);
                                            tvAlert.setLayoutParams(lp);
                                            tvAlert.setId(R.id.alert_confirm_password);
                                            tvAlert.setTextSize(12);
                                            tvAlert.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                            tvAlert.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                            llFormBuild.addView(tvAlert);
                                        }
                                    }

                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("required", compRequired);
                                    dataObjEl.put("label", compLabel);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataArrElement.put(dataObjEl);
                                }
                                else if (compType.equals("email")) {
                                    if (!compLabel.isEmpty()) {
                                        LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lpTv.setMargins(0, 15, 0, 0);
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lpTv);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    int intAplhabet = randomId();
                                    EditText ed = new EditText(mContext);
                                    ed.setId(intAplhabet);
                                    ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    ed.setGravity(Gravity.CENTER_VERTICAL);
                                    ed.setIncludeFontPadding(false);
                                    ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                    ed.setPadding(20, 20, 20, 20);
                                    ed.setHint(compPlaceholder);
                                    if (capitalize) {
                                        ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    } else {
                                        ed.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    }
                                    ed.setLayoutParams(lp);
                                    ed.setEnabled(compDisabled);
                                    ed.setClickable(compDisabled);
                                    if (!compDisabled) {
                                        ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput_disable));
                                        ed.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                    }
                                    if (llFormBuild != null) {
                                        llFormBuild.addView(ed);
                                    }

                                    int ids = ed.getId();
                                    String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("required", compRequired);
                                    dataObjEl.put("label", compLabel);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataArrElement.put(dataObjEl);
                                }
                                else if (compType.equals("number")) {
                                    if (!compLabel.isEmpty()) {
                                        LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lpTv.setMargins(0, 15, 0, 0);
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lpTv);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    int intAplhabet = randomId();

                                    EditText ed = new EditText(mContext);
                                    ed.setId(intAplhabet);
                                    ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    ed.setGravity(Gravity.CENTER_VERTICAL);
                                    ed.setIncludeFontPadding(false);
                                    ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                    ed.setPadding(20, 20, 20, 20);
                                    ed.setHint(compPlaceholder);
                                    ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                    ed.setLayoutParams(lp);
                                    ed.setEnabled(compDisabled);
                                    ed.setClickable(compDisabled);
                                    if (maxLength > 0) {
                                        ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                                    }

                                    if (!compDisabled) {
                                        ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput_disable));
                                        ed.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                    }

                                    int ids = ed.getId();
                                    String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                    if ((keyLabelInd.contains("no") || keyLabelInd.contains("nomor")) && keyLabelInd.contains("telepon")) {
                                        ed.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
                                    } else if (keyLabelInd.equals("rt") || keyLabelInd.equals("rw")) {
                                        ed.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
                                    }

                                    if (llFormBuild != null) {
                                        llFormBuild.addView(ed);
                                        if ((keyLabelInd.contains("rekening") && keyLabelInd.contains("penerima"))) {
                                            TextView tvAlertRek = new TextView(mContext);
                                            tvAlertRek.setLayoutParams(lp);
                                            tvAlertRek.setId(R.id.et_rek_penerima);
                                            tvAlertRek.setText(R.string.alert_norek_notmatch);
                                            tvAlertRek.setTextSize(12);
                                            tvAlertRek.setVisibility(View.GONE);
                                            tvAlertRek.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                            llFormBuild.addView(tvAlertRek);
                                        }
                                    }

                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("required", compRequired);
                                    dataObjEl.put("label", compLabel);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataArrElement.put(dataObjEl);

                                }
                                else if (compType.equals("radio")) {
                                    if (!compLabel.isEmpty()) {
                                        parentLabel = compLabel;
                                        parentLabelIndo = keyLabel;
                                        finishRad = false;
                                        jkRad = 0;
                                        radB = 0;
                                        radGroup = true;
                                    } else {
                                        radB++;
                                        radGroup = false;
                                    }
                                    if (radGroup) {
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lp);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }

                                        compLabelRad = compLabel;
                                        elNameRad = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        keyLabelIndRad = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                        for (int k = j; k < compLen; k++) {
                                            int loopP = k + 1;
                                            jkRad++;
                                            if (loopP >= compLen) {
                                                finishRad = true;
                                                break;
                                            }
                                            String compP = components.get(loopP).toString();
                                            JSONObject compObjP = new JSONObject(compP);
                                            String compLabelP = compObjP.getString("label");
                                            String compTypeP = compObjP.getString("type");

                                            if (compLabelP.contains("{")) {
                                                JSONObject labelObj = compObjP.getJSONObject("label");
                                                String radpcIdn = labelObj.getString("labelIdn");
                                                String radpcEng = labelObj.getString("labelEng");
                                                if (language.equals("id")) {
                                                    compLabelP = radpcIdn;
                                                } else {
                                                    compLabelP = radpcEng;
                                                }
                                            }

                                            if (!compTypeP.equals("radio")) {
                                                finishRad = true;
                                                break;
                                            } else if (!compLabelP.isEmpty() && k > j) {
                                                finishRad = true;
                                                break;
                                            }
                                        }

                                        int intAplhabet = randomId();

                                        radioGroup = new RadioGroup(mContext);

                                        if (sessions.getFlagQuestion()) {
                                            radioGroup.setOrientation(RadioGroup.VERTICAL);
                                        } else {
                                            if (jkRad == 3) {
                                                LinearLayout.LayoutParams lpRG = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 70);
                                                radioGroup.setLayoutParams(lpRG);
                                            } else {
                                                radioGroup.setLayoutParams(lp);
                                            }

                                            if (jkRad > 3) {
                                                radioGroup.setOrientation(RadioGroup.VERTICAL);
                                            } else {
                                                radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                                            }
                                        }
                                        radioGroup.setId(intAplhabet);

                                    }

                                    int intAplhabet = randomId();

                                    RadioButton rb = new RadioButton(mContext);
                                    rb.setLayoutParams(lp2);
                                    rb.setId(intAplhabet);
                                    rb.setText(compPlaceholder);
                                    rb.setButtonTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_button)));

                                    radioGroup.addView(rb);

                                    if (finishRad && radB == jkRad - 1) {
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(radioGroup);
                                        }

                                        String compLabelGabIndo = "-";
                                        if (compLen - 1 > radB) {
                                            String compSGab = components.get(j).toString();
                                            JSONObject compObjGab = new JSONObject(compSGab);
                                            String compLabelGab = compObjGab.getString("label");
                                            String keyLabelGab = "";
                                            if (compLabelGab.contains("{")) {
                                                JSONObject labelObj = compObjGab.getJSONObject("label");
                                                String pcIdn = labelObj.getString("labelIdn");
                                                keyLabelGab = pcIdn;
                                            }
                                            compLabelGabIndo = parentLabelIndo + keyLabelGab;
                                        }

                                        int ids = radioGroup.getId();
                                        dataObjEl.put("id", ids);
                                        dataObjEl.put("name", keyLabelIndRad);
                                        dataObjEl.put("CompoName", compNameInside);
                                        dataObjEl.put("nameGab", compLabelGabIndo);
                                        dataObjEl.put("required", compRequired);
                                        dataObjEl.put("keyIndo", keyLabelIndRad);
                                        dataObjEl.put("label", compLabelRad);
                                        dataArrElement.put(dataObjEl);
                                    }

                                }
                                else if (compType.equals("checkbox")) {
                                    if (!compLabel.isEmpty()) {
                                        parentLabel = compLabel;
                                        parentLabelIndo = keyLabel;
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lp);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    int intAplhabet = randomId();

                                    CheckBox chk = new CheckBox(mContext);
                                    chk.setId(intAplhabet);
                                    chk.setLayoutParams(lp);
                                    chk.setText(compPlaceholder);
                                    chk.setButtonTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.zm_button)));

                                    if (llFormBuild != null) {
                                        llFormBuild.addView(chk);
                                    }

                                    int ids = chk.getId();
                                    String elName = "";
                                    String keyLabelInd = "";
                                    if (compLen == 1) {
                                        elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    } else {
                                        elName = compPlaceholder.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                        keyLabelInd = pcPlaceIdn.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    }
                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("required", compRequired);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataObjEl.put("label", parentLabel);
                                    dataArrElement.put(dataObjEl);

                                }
                                else if (compType.equals("file")) {
                                    int intAplhabet = randomId();

                                    lp.setMargins(0, 10, 0, 10);
                                    LinearLayout ln = new LinearLayout(mContext);

                                    String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                    if (!compLabel.isEmpty()) {
                                        parentLabel = compLabel;
                                        parentLabelIndo = keyLabel;
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lp);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    if (keyLabelInd.contains("gambar") || keyLabelInd.contains("image") || keyLabelInd.contains("tangan") || keyLabelInd.contains("ktp")) {
                                        LayoutInflater inflater = LayoutInflater.from(mContext);
                                        ln = (LinearLayout) inflater.inflate(R.layout.layout_capture_gallery, null, false);
                                    } else {
                                        LayoutInflater inflater = LayoutInflater.from(mContext);
                                        ln = (LinearLayout) inflater.inflate(R.layout.layout_upload_file, null, false);

                                        TextView nama_file = ln.findViewById(R.id.labelFile);
                                        nama_file.setText(compLabel);

                                    /*ln.setId(intAplhabet);
                                    ln.setLayoutParams(lp);
                                    ln.setOrientation(LinearLayout.VERTICAL);

                                    int intLinear = randomId();
                                    LinearLayout ln2 = new LinearLayout(mContext);
                                    LinearLayout.LayoutParams lpLL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lpLL.setMargins(0, 0, 0, 5);
                                    ln2.setId(intLinear);
                                    ln2.setLayoutParams(lpLL);
                                    ln2.setOrientation(LinearLayout.HORIZONTAL);
                                    ln2.setGravity(Gravity.CENTER);
                                    ln2.setBackground(mContext.getDrawable(R.drawable.oval_background_10dp));
                                    ln2.setBackgroundTintList(ColorStateList.valueOf(mContext.getColor(R.color.color_6b6b6b)));

                                    LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(30, 30);
                                    ImageView img = new ImageView(mContext);
                                    img.setLayoutParams(lpImg);
                                    img.setImageDrawable(mContext.getDrawable(R.drawable.galeri));
                                    ln2.addView(img);

                                    lp2.gravity = Gravity.CENTER_VERTICAL;
                                    lp2.setMarginStart(10);
                                    lp2.setMarginEnd(10);
                                    TextView tv = new TextView(mContext);
                                    tv.setText(compLabel);
                                    tv.setLayoutParams(lp2);
                                    tv.setTextColor(Color.WHITE);
                                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                                    ln2.addView(tv);
                                    ln.addView(ln2);

                                    int inttvSaved = randomId();
                                    LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lptv.setMargins(0, 0, 0, 10);
                                    TextView tvSaved = new TextView(mContext);
                                    tvSaved.setId(inttvSaved);
                                    tvSaved.setTextColor(Color.BLACK);
                                    tvSaved.setLayoutParams(lptv);
                                    tvSaved.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    ln.addView(tvSaved);*/
                                    }

                                    if (llFormBuild != null) {
                                        llFormBuild.addView(ln);
                                    }

                                    int ids = ln.getId();

                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("required", compRequired);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataObjEl.put("label", compLabel);
                                    dataArrElement.put(dataObjEl);
                                }
                                else if (compType.equals("currency")) {

                                    String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                    if (!compLabel.isEmpty()) {
                                        parentLabel = compLabel;
                                        parentLabelIndo = keyLabel;
                                        TextView tv = new TextView(mContext);
                                        tv.setText(compLabel);
                                        tv.setLayoutParams(lp);
                                        if (llFormBuild != null) {
                                            llFormBuild.addView(tv);
                                        }
                                    }

                                    LayoutInflater inflater = LayoutInflater.from(mContext);
                                    LinearLayout lnCurr = (LinearLayout) inflater.inflate(R.layout.layout_currency, null, false);

                                    if (compObj.has("currency")) {
                                        String locale = compObj.getJSONObject("currency").getString("locale");
                                        String prefix = compObj.getJSONObject("currency").getString("prefix");
                                        TextView tvCurr = lnCurr.findViewById(R.id.tvCurrency);
                                        EditText tvContentCurr = lnCurr.findViewById(R.id.tvContentCurr);
                                        tvContentCurr.setEnabled(compDisabled);
                                        tvContentCurr.setClickable(compDisabled);

                                        tvCurr.setText(prefix);

                                        tvContentCurr.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                String val = s.toString();
                                                if (!val.isEmpty()) {
                                                    tvContentCurr.removeTextChangedListener(this);
                                                    //NumberFormat nf = NumberFormat.getInstance(new Locale(locale));
                                                    NumberFormat nf = null;
                                                    if (sessions.getLANG().equals("id")) {
                                                        nf = NumberFormat.getInstance(new Locale("id", "ID"));
                                                    } else {
                                                        nf = NumberFormat.getInstance(new Locale("en", "US"));
                                                    }
                                                    String cleanString = val.replaceAll("[$,.]", "");
                                                    BigDecimal parsed = new BigDecimal(cleanString);
                                                    String nfS = nf.format(parsed);
                                                    tvContentCurr.setText(nfS);
                                                    tvContentCurr.setSelection(nfS.length());
                                                    tvContentCurr.addTextChangedListener(this);
                                                }
                                            }
                                        });

                                    }

                                    if (llFormBuild != null) {
                                        llFormBuild.addView(lnCurr);
                                        TextView tvAlertRek = new TextView(mContext);
                                        tvAlertRek.setLayoutParams(lp);
                                        tvAlertRek.setId(R.id.et_nominal);
                                        if (idService.equals("191")) {
                                            tvAlertRek.setText(R.string.balance_insufficient);
                                        } else {
                                            tvAlertRek.setText(R.string.alert_nominal);
                                        }
                                        tvAlertRek.setTextSize(12);
                                        tvAlertRek.setVisibility(View.GONE);
                                        tvAlertRek.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                        llFormBuild.addView(tvAlertRek);
                                    }

                                    int ids = lnCurr.getId();

                                    dataObjEl.put("id", ids);
                                    dataObjEl.put("name", keyLabelInd);
                                    dataObjEl.put("CompoName", compNameInside);
                                    dataObjEl.put("required", compRequired);
                                    dataObjEl.put("keyIndo", keyLabelInd);
                                    dataObjEl.put("label", compLabel);
                                    dataArrElement.put(dataObjEl);
                                }
                                break;
                            case "textarea":
                                TextView tvArea = new TextView(mContext);
                                tvArea.setText(compLabel);
                                tvArea.setLayoutParams(lp);
                                if (llFormBuild != null) {
                                    llFormBuild.addView(tvArea);
                                }

                                int idArea = randomId();

                                LinearLayout.LayoutParams lpArea = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);

                                EditText ed = new EditText(mContext);
                                ed.setId(idArea);
                                ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                ed.setGravity(Gravity.CENTER_VERTICAL);
                                ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                if (capitalize) {
                                    ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                } else {
                                    ed.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                }
                                ed.setPadding(20, 20, 20, 20);
                                ed.setMaxLines(10);
                                ed.setLines(3);
                                ed.setMinLines(3);
                                ed.setSingleLine(false);
                                ed.setLayoutParams(lpArea);
                                if (maxLength > 0) {
                                    ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                                }
                                ed.setGravity(Gravity.TOP);
                                ed.setVerticalScrollBarEnabled(true);
                                ed.setScroller(new Scroller(((Activity) mContext).getBaseContext()));
                                ed.setMovementMethod(new ScrollingMovementMethod());
                                if (llFormBuild != null) {
                                    llFormBuild.addView(ed);
                                }

                                int getIdArea = ed.getId();
                                String elNameArea = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                JSONObject dataObjElArea = new JSONObject();
                                dataObjElArea.put("id", getIdArea);
                                dataObjElArea.put("name", keyLabelInd);
                                dataObjElArea.put("CompoName", compNameInside);
                                dataObjElArea.put("required", compRequired);
                                dataObjElArea.put("keyIndo", keyLabelInd);
                                dataObjElArea.put("label", compLabel);
                                dataArrElement.put(dataObjElArea);

                                break;
                            case "p":

                                LinearLayout.LayoutParams lpP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lpP.setMargins(0, 15, 0, -30);
                                if (!compLabel.contains("<p>")) {
                                    compLabel = "<p>" + compLabel + "</p>";
                                }

                                TextView tv = new TextView(mContext);
                                tv.setText(Html.fromHtml(compLabel, Html.FROM_HTML_MODE_COMPACT));
                                tv.setLayoutParams(lpP);
                                if (llFormBuild != null) {
                                    llFormBuild.addView(tv);
                                }

                                break;
                            case "option":
                                lp.setMargins(0, 10, 0, 0);
                                TextView tvOpt = new TextView(mContext);
                                tvOpt.setText(compLabel);
                                tvOpt.setLayoutParams(lp);
                                if (llFormBuild != null) {
                                    llFormBuild.addView(tvOpt);
                                }

                                int intAplhabet = randomId();

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                                RelativeLayout relativeLayout = new RelativeLayout(mContext);
                                relativeLayout.setLayoutParams(lp);
                                relativeLayout.setId(intAplhabet);
                                relativeLayout.setBackground(mContext.getDrawable(R.drawable.bg_textinput));

                                int spinId = randomId();
                                Spinner spinner = new Spinner(mContext);
                                spinner.setId(spinId);
                                spinner.setLayoutParams(layoutParams);
                                spinner.setFocusable(false);
                                spinner.setGravity(Gravity.CENTER_VERTICAL);
                                spinner.setPadding(0, 0, 20, 0);

                                /*if (!urlPath.isEmpty() && compNameInside.equals("dropdownsumberdana")) {
                                    processGetDynamicURLSumberDana(spinner,urlPath);
                                } else {*/
                                    JSONArray options = compObj.getJSONArray("options");
                                    ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                                    for (int jk = 0; jk < options.length(); jk++) {
                                        int idOpt = options.getJSONObject(jk).getInt("id");
                                        String kodeOpt = options.getJSONObject(jk).getString("kode");
                                        String labelOpt = options.getJSONObject(jk).getString("labelIdn");
                                        dataDropDown.add(new FormSpin(idOpt, kodeOpt, labelOpt, labelOpt));
                                    }
                                    ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
                                    spinner.setAdapter(adapter2);
                                //}
                                relativeLayout.addView(spinner);

                                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                                layoutParams2.addRule(RelativeLayout.RIGHT_OF, RelativeLayout.TRUE);

                                ImageView imgSpin = new ImageView(mContext);
                                imgSpin.setLayoutParams(layoutParams2);
                                relativeLayout.addView(imgSpin);
                                if (llFormBuild != null) {
                                    llFormBuild.addView(relativeLayout);
                                }

                                int ids = relativeLayout.getId();
                                String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                String keyLabelIndOpt = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                JSONObject dataObjElOpt = new JSONObject();
                                dataObjElOpt.put("id", ids);
                                dataObjElOpt.put("name", keyLabelIndOpt);
                                dataObjElOpt.put("CompoName", compNameInside);
                                dataObjElOpt.put("required", compRequired);
                                dataObjElOpt.put("url", urlPath);
                                dataObjElOpt.put("keyIndo", keyLabelIndOpt);
                                dataObjElOpt.put("label", compLabel);
                                dataArrElement.put(dataObjElOpt);

                                break;
                            case "datalist":
                                lp.setMargins(0, 10, 0, 0);
                                TextView tvList = new TextView(mContext);
                                tvList.setText(compLabel);
                                tvList.setLayoutParams(lp);
                                if (llFormBuild != null) {
                                    llFormBuild.addView(tvList);
                                }

                                int intAplhabetAuto = randomId();

                                RelativeLayout.LayoutParams layoutParamsList = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParamsList.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                                RelativeLayout relativeLayoutList = new RelativeLayout(mContext);
                                relativeLayoutList.setLayoutParams(lp);
                                relativeLayoutList.setId(intAplhabetAuto);
                                AutoCompleteTextView autoText = new AutoCompleteTextView(mContext);
                                lp.setMargins(0, 0, 0, 0);
                                autoText.setId(intAplhabetAuto);
                                autoText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                autoText.setLayoutParams(lp);
                                autoText.setHint(compPlaceholder);
                                autoText.setThreshold(1);
                                autoText.setEms(10);
                                autoText.setGravity(Gravity.CENTER_VERTICAL);
                                //autoText.setSingleLine(true);
                                autoText.setIncludeFontPadding(false);
                                autoText.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                autoText.setPadding(20, 20, 20, 20);
                                autoText.setEnabled(compDisabled);
                                autoText.setClickable(compDisabled);
                                if (!compDisabled) {
                                    autoText.setBackground(mContext.getDrawable(R.drawable.bg_textinput_disable));
                                    autoText.setTextColor(mContext.getResources().getColor(R.color.zm_text));
                                }
                                autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View view, boolean hasFocus) {
                                        if (hasFocus) {
                                            autoText.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    autoText.showDropDown();
                                                }
                                            }, 500);
                                        } else {
                                            autoText.dismissDropDown();
                                        }
                                    }
                                });

                                autoText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        autoText.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                autoText.showDropDown();
                                            }
                                        }, 500);
                                    }
                                });

                                int idsAuto = autoText.getId();
                                String elName2 = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                String keyLabelIndAuto = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                if (!urlPath.isEmpty()) {
                                    if (keyLabelIndAuto.contains("rekening") && keyLabelIndAuto.contains("penerima")) {
                                        processGetDynamicURLSumberDanaPenerima(autoText, urlPath);
                                    } else {
                                        processGetDynamicURLDataList(autoText, urlPath);
                                    }
                                } else {
                                    JSONArray optionsAuto = compObj.getJSONArray("options");
                                    ArrayList<FormSpin> dataAuto = new ArrayList<>();
                                    for (int jk = 0; jk < optionsAuto.length(); jk++) {
                                        int idOpt = optionsAuto.getJSONObject(jk).getInt("id");
                                        String kodeOpt = optionsAuto.getJSONObject(jk).getString("kode");
                                        String labelOpt = optionsAuto.getJSONObject(jk).getString("labelIdn");
                                        dataAuto.add(new FormSpin(idOpt, kodeOpt, labelOpt, labelOpt));
                                    }

                                    ArrayAdapter<FormSpin> adapterAuto = new ArrayAdapter<FormSpin>(mContext, R.layout.support_simple_spinner_dropdown_item, dataAuto);
                                    autoText.setAdapter(adapterAuto);
                                }

                                if (llFormBuild != null) {
                                    relativeLayoutList.addView(autoText);
                                    llFormBuild.addView(relativeLayoutList);
                                    if ((keyLabelIndAuto.contains("rekening") && keyLabelIndAuto.contains("penerima"))) {
                                        TextView tvAlertRek = new TextView(mContext);
                                        tvAlertRek.setLayoutParams(lp);
                                        tvAlertRek.setId(R.id.et_rek_penerima);
                                        tvAlertRek.setText(R.string.alert_norek_notmatch);
                                        tvAlertRek.setTextSize(12);
                                        tvAlertRek.setVisibility(View.GONE);
                                        tvAlertRek.setTextColor(mContext.getResources().getColor(R.color.zm_button));
                                        llFormBuild.addView(tvAlertRek);
                                    }
                                }


                                JSONObject dataObjElAuto = new JSONObject();
                                dataObjElAuto.put("id", idsAuto);
                                dataObjElAuto.put("name", keyLabelIndAuto);
                                dataObjElAuto.put("CompoName", compNameInside);
                                dataObjElAuto.put("required", compRequired);
                                dataObjElAuto.put("url", urlPath);
                                dataObjElAuto.put("keyIndo", keyLabelIndAuto);
                                dataObjElAuto.put("label", compLabel);
                                dataArrElement.put(dataObjElAuto);

                                break;
                            case "a":
                                String alamat = compObj.getString("alamat");
                                TextView tvlink = new TextView(mContext);
                                tvlink.setLayoutParams(lp);
                                tvlink.setClickable(true);
                                CharSequence sequence = Html.fromHtml("<a href=\"" + alamat + "\">" + compLabel + "</a>", Html.FROM_HTML_MODE_COMPACT);
                                tvlink.setText(sequence);
                                tvlink.setTextColor(Color.BLUE);
                                tvlink.setMovementMethod(LinkMovementMethod.getInstance());
                                if (llFormBuild != null) {
                                    llFormBuild.addView(tvlink);
                                }
                                break;
                            case "img":
                                String sourceImge = compObj.getString("img");
                                int idx = sourceImge.indexOf(",");
                                idx += 1;
                                String new_sourceImge = sourceImge.substring(idx);
                                byte[] data = Base64.decode(new_sourceImge, Base64.NO_WRAP);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                                int intH = d.getIntrinsicHeight();
                                int intW = d.getIntrinsicWidth();

                                LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(intW, intH);
                                lpImg.setMargins(0, 10, 0, 10);

                                ImageView img = new ImageView(mContext);
                                img.setLayoutParams(lpImg);
                                img.setImageDrawable(d);
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                if (llFormBuild != null) {
                                    llFormBuild.addView(img);
                                }
                                break;
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dataArrElement;
    }

    private static int randomId() {
        Random random=new Random();
        int dataInt = random.nextInt(99999999);
        return dataInt;
    }

    private static void processGetDynamicURLSumberDana(Spinner spinner, String urlPath) {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",sessions.getNoCIF());
            jsons.put("bahasa",sessions.getLANG());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrlPost(urlPath,requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        JSONObject objData = dataObj.getJSONObject("data");
                        JSONArray dataArr = objData.getJSONArray("portotabungan");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        String[] sourceAcc = new String[dataArr.length()];
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = i + 1;

                            String prodName = dataArr.getJSONObject(i).getString("prodName").replace("R/K","").trim();
                            String prodCode = dataArr.getJSONObject(i).getString("prodCode");
                            if (prodCode.equals("T21")) {
                                continue;
                            }
                            if (dataArr.getJSONObject(i).has("acctStatus")) {
                                String acctStatus = dataArr.getJSONObject(i).getString("acctStatus");
                                if (!acctStatus.equals("A")) {
                                    continue;
                                }
                            }
                            String accountNo = dataArr.getJSONObject(i).getString("accountNo");
                            String accountName = dataArr.getJSONObject(i).getString("accountName");
                            String acctCur = dataArr.getJSONObject(i).getString("acctCur");
                            String availBalance = dataArr.getJSONObject(i).getString("availBalance");
                            String accountType = dataArr.getJSONObject(i).getString("accountType");
                            availBalance = availBalance.substring(0,availBalance.length() - 2);

                            Double d = Double.valueOf(availBalance);
                            //DecimalFormat formatter = new DecimalFormat("#,###.##");
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);
                            String formattedNumber = formatter.format(d);

                            String labelIdn = prodName+"\n"+accountNo+" - "+accountName+"\n"+acctCur+" "+formattedNumber;
                            sourceAcc[i] = labelIdn;

                            dataDropDown.add(new FormSpin(idData,accountType,labelIdn,labelIdn));
                        }
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDown);
                        //ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.dropdown_multiline, dataDropDown);
                        spinner.setAdapter(adapterSourceAcc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String dataS = null;
                        try {
                            dataS = response.errorBody().string();
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void processGetDynamicURLSumberDanaPenerima(AutoCompleteTextView autoText, String urlPath) {
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("noCif",sessions.getNoCIF());
            jsons.put("bahasa",sessions.getLANG());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrlPost(urlPath,requestBody,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        JSONObject objData = dataObj.getJSONObject("data");
                        JSONArray dataArr = objData.getJSONArray("portotabungan");
                        ArrayList<FormSpin> dataDropDownSource = new ArrayList<>();
                        int len = dataArr.length() + 1;
                        String[] sourceAcc = new String[len];
                        String textSelect = mContext.getString(R.string.choose_source_fund);
                        sourceAcc[0] = textSelect;
                        dataDropDownSource.add(new FormSpin(0,"0",textSelect,textSelect));
                        int loopSource = 1;
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = i + 1;

                            String prodName = dataArr.getJSONObject(i).getString("prodName").replace("R/K","").trim();
                            String prodCode = dataArr.getJSONObject(i).getString("prodCode");
                            if (prodCode.equals("T21")) {
                                continue;
                            }
                            if (dataArr.getJSONObject(i).has("acctStatus")) {
                                String acctStatus = dataArr.getJSONObject(i).getString("acctStatus");
                                if (!acctStatus.equals("A")) {
                                    continue;
                                }
                            }
                            String accountNo = dataArr.getJSONObject(i).getString("accountNo");
                            String accountName = dataArr.getJSONObject(i).getString("accountName");
                            String acctCur = dataArr.getJSONObject(i).getString("acctCur");
                            String availBalance = dataArr.getJSONObject(i).getString("availBalance");
                            String accountType = dataArr.getJSONObject(i).getString("accountType");
                            availBalance = availBalance.substring(0,availBalance.length() - 2);

                            if (acctCur.equals("IDR")) {
                                acctCur = "Rp.";
                            }

                            Double d = Double.valueOf(availBalance);
                            NumberFormat formatter = null;
                            if (sessions.getLANG().equals("id")) {
                                formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            } else {
                                formatter = NumberFormat.getInstance(new Locale("en", "US"));
                            }
                            formatter.setMinimumFractionDigits(2);
                            String formattedNumber = formatter.format(d);

                            String labelIdn = prodName+"\n"+accountNo;
                            sourceAcc[loopSource] = labelIdn;
                            loopSource++;

                            dataDropDownSource.add(new FormSpin(idData,accountType,labelIdn,labelIdn));
                        }
                        //ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.dropdown_multiline, dataDropDownSource);
                        AdapterSourceAccount adapterSourceAcc = new AdapterSourceAccount(mContext,R.layout.dropdown_multiline, dataDropDownSource);
                        int fullHeight = dataDropDownSource.size() * 50;
                        if (fullHeight > 300) {
                            autoText.setDropDownHeight(300);
                        } else {
                            autoText.setDropDownHeight(fullHeight);
                        }
                        autoText.setAdapter(adapterSourceAcc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                    String msg = "";
                    if (response.errorBody().toString().isEmpty()) {
                        String dataS = response.errorBody().toString();
                        try {
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String dataS = null;
                        try {
                            dataS = response.errorBody().string();
                            JSONObject dataObj = new JSONObject(dataS);
                            if (dataObj.has("message")) {
                                msg = dataObj.getString("message");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void processGetDynamicURLDataList(AutoCompleteTextView autoText, String urlPath) {
        String authAccess = "Bearer "+sessions.getAuthToken();
        String exchangeToken = sessions.getExchangeToken();
        Server.getAPIService().getDynamicUrl(urlPath,authAccess,exchangeToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    try {
                        JSONObject dataObj = new JSONObject(dataS);
                        if (dataObj.has("token")) {
                            String accessToken = dataObj.getString("token");
                            String exchangeToken = dataObj.getString("exchange");
                            sessions.saveAuthToken(accessToken);
                            sessions.saveExchangeToken(exchangeToken);
                        }
                        JSONArray dataArr = dataObj.getJSONArray("data");
                        ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                        for (int i = 0; i < dataArr.length(); i++) {
                            int idData = 0;
                            String idSData = "";
                            String valueCode = "";

                            if (dataArr.getJSONObject(i).has("ids")) {
                                idSData = dataArr.getJSONObject(i).getString("ids").trim();
                                idData = Integer.parseInt(idSData);
                            } else if (dataArr.getJSONObject(i).has("id")) {
                                idData = dataArr.getJSONObject(i).getInt("id");

                            }

                            String labelIdn = dataArr.getJSONObject(i).getString("labelIdn");
                            String labelEng = dataArr.getJSONObject(i).getString("labelEng");
                            if (sessions.getLANG().equals("en")) {
                                labelIdn = labelEng;
                            }
                            valueCode = labelIdn;
                            if (dataArr.getJSONObject(i).has("beneficiaryCode")) {
                                if (!dataArr.getJSONObject(i).isNull("beneficiaryCode")) {
                                    String beneficiaryCode = dataArr.getJSONObject(i).getString("beneficiaryCode");
                                    idData = Integer.parseInt(beneficiaryCode);
                                }
                            }
                            if (dataArr.getJSONObject(i).has("swiftCode") && dataArr.getJSONObject(i).has("cityCode")) {
                                String swiftCode = dataArr.getJSONObject(i).getString("swiftCode");
                                String cityCode = dataArr.getJSONObject(i).getString("cityCode");
                                valueCode = swiftCode+" | "+cityCode;
                            }
                            if (dataArr.getJSONObject(i).has("kodeCabang")) {
                                valueCode = dataArr.getJSONObject(i).getString("kodeCabang");
                            }

                            dataDropDown.add(new FormSpin(idData,valueCode,labelIdn,labelEng));
                        }
                        ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, R.layout.simple_spinner_dropdown_customitem, dataDropDown);
                        int fullHeight = dataDropDown.size() * 25;
                        if (fullHeight > 300) {
                            autoText.setDropDownHeight(300);
                        } else {
                            autoText.setDropDownHeight(fullHeight);
                        }
                        autoText.setAdapter(adapter2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext,R.string.msg_error,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
