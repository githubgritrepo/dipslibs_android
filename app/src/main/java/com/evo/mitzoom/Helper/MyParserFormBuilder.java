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
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MyParserFormBuilder {

    private static JSONArray dataArrObj;
    private static Context mContext;
    private static LinearLayout llFormBuild;
    private static SessionManager sessions;
    private static String language;

    public MyParserFormBuilder(Context mContext, String dataForm, LinearLayout llFormBuild) throws JSONException {
        this.dataArrObj = new JSONArray(dataForm);
        this.mContext = mContext;
        this.llFormBuild = llFormBuild;
        this.sessions = new SessionManager(mContext);
        this.language = sessions.getLANG();
        Log.e("CEK","language : "+language);
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
                for (int j = 0; j < compLen; j++) {
                    String pcPlaceIdn = "";
                    String keyLabel = "";
                    LinearLayout.LayoutParams lpLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpLayout.setMargins(0,0,0,10);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                    String compS = components.get(j).toString();
                    JSONObject compObj = new JSONObject(compS);
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
                    if (compObj.has("required")) {
                        compRequired = compObj.getBoolean("required");
                    } else if (compObj.has("props")) {
                        if (compObj.getJSONObject("props").has("required")) {
                            compRequired = compObj.getJSONObject("props").getBoolean("required");
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

                    switch (compTagHTML) {
                        case "input":
                            JSONObject dataObjEl = new JSONObject();
                            if (compType.equals("text") || compType.equals("date")) {
                                if (!compLabel.isEmpty()) {
                                    LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lpTv.setMargins(0,15,0,0);
                                    TextView tv = new TextView(mContext);
                                    tv.setText(compLabel);
                                    tv.setLayoutParams(lpTv);
                                    llFormBuild.addView(tv);
                                }

                                int intAplhabet = randomId();

                                EditText ed = new EditText(mContext);
                                ed.setId(intAplhabet);
                                ed.setHint(compPlaceholder);
                                ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                ed.setGravity(Gravity.CENTER_VERTICAL);
                                ed.setIncludeFontPadding(false);
                                ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                ed.setPadding(20,20,20,20);
                                if (compType.equals("date")) {
                                    ed.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                                    ed.setClickable(false);
                                    ed.setFocusable(false);
                                    ed.setFocusableInTouchMode(false);
                                    ed.setCursorVisible(false);

                                    Calendar currentTime = Calendar.getInstance();

                                    DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            Log.e("CEK","year : "+year+" | month : "+month+" | dayOfMonth : "+dayOfMonth);
                                            currentTime.set(Calendar.YEAR, year);
                                            currentTime.set(Calendar.MONTH, month);
                                            currentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                            String Tgl = String.format("%02d", dayOfMonth);
                                            String bln = String.format("%02d", month+1);

                                            //String getDates = updateDates(currentTime);
                                            String getDates = Tgl+"-"+bln+"-"+year;
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
                                        valKurung = " "+lowLabel.substring(indx);
                                    }

                                    Log.e("CEK","lowLabel : "+lowLabel+" | valKurung : "+valKurung);

                                    if (lowLabel.equals("npwp"+valKurung)) {
                                        ed.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
                                    } else if (lowLabel.equals("nik"+valKurung)) {
                                        ed.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
                                    }

                                    if (lowLabel.equals("nik"+valKurung) || lowLabel.equals("npwp"+valKurung) || lowLabel.equals("rt"+valKurung) || lowLabel.equals("rw"+valKurung) || lowLabel.contains("kode"+valKurung) ||
                                            lowLabel.contains("no.") || lowLabel.contains("nomor") || lowLabel.contains("telp") ||
                                            lowLabel.contains("telepon")) {
                                        ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                    } else if (lowLabel.contains("email")) {
                                        ed.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    } else {
                                        ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                                    }
                                }
                                ed.setLayoutParams(lp);
                                llFormBuild.addView(ed);

                                int ids = ed.getId();
                                String elName = "";
                                String keyLabelInd = "";
                                String compLabelGab2 = compLabel;
                                if (compName.equals("checkbox")) {
                                    compLabelGab2 = parentLabel+" "+compLabel;
                                    String compLabelGab = parentLabel+compLabel;
                                    String compLabelGabIndo = parentLabel+keyLabel;
                                    elName = compLabelGab.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    keyLabelInd = compLabelGabIndo.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                } else {
                                    elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                }

                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",keyLabelInd);
                                dataObjEl.put("keyIndo",keyLabelInd);
                                dataObjEl.put("label",compLabelGab2);
                                dataObjEl.put("required",compRequired);
                                dataArrElement.put(dataObjEl);
                            } else if (compType.equals("number")) {
                                if (!compLabel.isEmpty()) {
                                    LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lpTv.setMargins(0,15,0,0);
                                    TextView tv = new TextView(mContext);
                                    tv.setText(compLabel);
                                    tv.setLayoutParams(lpTv);
                                    llFormBuild.addView(tv);
                                }

                                int intAplhabet = randomId();

                                EditText ed = new EditText(mContext);
                                ed.setId(intAplhabet);
                                ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                ed.setGravity(Gravity.CENTER_VERTICAL);
                                ed.setIncludeFontPadding(false);
                                ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                                ed.setPadding(20,20,20,20);
                                ed.setHint(compPlaceholder);
                                ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                ed.setLayoutParams(lp);
                                llFormBuild.addView(ed);

                                int ids = ed.getId();
                                String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",keyLabelInd);
                                dataObjEl.put("required",compRequired);
                                dataObjEl.put("label",compLabel);
                                dataObjEl.put("keyIndo",keyLabelInd);
                                dataArrElement.put(dataObjEl);

                            } else if (compType.equals("radio")) {
                                if (!compLabel.isEmpty()) {
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
                                    llFormBuild.addView(tv);

                                    compLabelRad = compLabel;
                                    elNameRad = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                    keyLabelIndRad = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

                                    Log.e("CEK","compLen Radio : "+compLen);
                                    for (int k = j; k < compLen; k++) {
                                        int loopP = k + 1;
                                        jkRad++;
                                        if (loopP >= compLen) {
                                            finishRad = true;
                                            break;
                                        }
                                        Log.e("CEK","Loop Radio : "+loopP);
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

                                    if (jkRad == 3){
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
                                    radioGroup.setId(intAplhabet);

                                }

                                int intAplhabet = randomId();

                                RadioButton rb = new RadioButton(mContext);
                                rb.setLayoutParams(lp2);
                                rb.setId(intAplhabet);
                                rb.setText(compPlaceholder);

                                radioGroup.addView(rb);

                                Log.e("CEK","finishRad : "+finishRad+" | jkRad : "+jkRad+" | radB : "+radB);

                                if (finishRad && radB == jkRad-1) {
                                    llFormBuild.addView(radioGroup);

                                    int ids = radioGroup.getId();
                                    Log.e("CEK","compName : "+elNameRad+" | ids : "+ids);
                                    dataObjEl.put("id",ids);
                                    dataObjEl.put("name",keyLabelIndRad);
                                    dataObjEl.put("required",compRequired);
                                    dataObjEl.put("keyIndo",keyLabelIndRad);
                                    dataObjEl.put("label",compLabelRad);
                                    dataArrElement.put(dataObjEl);
                                }

                            } else if (compType.equals("checkbox")) {
                                if (!compLabel.isEmpty()) {
                                    parentLabel = compLabel;
                                    TextView tv = new TextView(mContext);
                                    tv.setText(compLabel);
                                    tv.setLayoutParams(lp);
                                    llFormBuild.addView(tv);
                                }

                                int intAplhabet = randomId();

                                CheckBox chk = new CheckBox(mContext);
                                chk.setId(intAplhabet);
                                chk.setLayoutParams(lp);
                                chk.setText(compPlaceholder);

                                llFormBuild.addView(chk);

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
                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",keyLabelInd);
                                dataObjEl.put("required",compRequired);
                                dataObjEl.put("keyIndo",keyLabelInd);
                                dataObjEl.put("label",parentLabel);
                                dataArrElement.put(dataObjEl);

                            } else if (compType.equals("file")) {
                                int intAplhabet = randomId();

                                lp.setMargins(0,10,0,10);
                                LinearLayout ln = new LinearLayout(mContext);
                                ln.setId(intAplhabet);
                                ln.setLayoutParams(lp);
                                ln.setOrientation(LinearLayout.VERTICAL);

                                int intLinear = randomId();
                                LinearLayout ln2 = new LinearLayout(mContext);
                                LinearLayout.LayoutParams lpLL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);;
                                lpLL.setMargins(0,0,0,5);
                                ln2.setId(intLinear);
                                ln2.setLayoutParams(lpLL);
                                ln2.setOrientation(LinearLayout.HORIZONTAL);
                                ln2.setGravity(Gravity.CENTER);
                                ln2.setBackground(mContext.getDrawable(R.drawable.oval_background_10dp));
                                ln2.setBackgroundTintList(ColorStateList.valueOf(mContext.getColor(R.color.color_6b6b6b)));

                                LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(30,30);
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
                                LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                lptv.setMargins(0,0,0,10);
                                TextView tvSaved = new TextView(mContext);
                                tvSaved.setId(inttvSaved);
                                tvSaved.setTextColor(Color.BLACK);
                                tvSaved.setLayoutParams(lptv);
                                tvSaved.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                ln.addView(tvSaved);

                                llFormBuild.addView(ln);

                                int ids = ln.getId();
                                String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",keyLabelInd);
                                dataObjEl.put("required",compRequired);
                                dataObjEl.put("keyIndo",keyLabelInd);
                                dataObjEl.put("label",compLabel);
                                dataArrElement.put(dataObjEl);
                            }
                            break;
                        case "textarea":
                            TextView tvArea = new TextView(mContext);
                            tvArea.setText(compLabel);
                            tvArea.setLayoutParams(lp);
                            llFormBuild.addView(tvArea);

                            int idArea = randomId();

                            LinearLayout.LayoutParams lpArea = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200);

                            EditText ed = new EditText(mContext);
                            ed.setId(idArea);
                            ed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                            ed.setGravity(Gravity.CENTER_VERTICAL);
                            ed.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                            ed.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                            ed.setPadding(20,20,20,20);
                            ed.setMaxLines(10);
                            ed.setLines(3);
                            ed.setMinLines(3);
                            ed.setSingleLine(false);
                            ed.setLayoutParams(lpArea);
                            ed.setGravity(Gravity.TOP);
                            ed.setVerticalScrollBarEnabled(true);
                            ed.setScroller(new Scroller(((Activity)mContext).getBaseContext()));
                            ed.setMovementMethod(new ScrollingMovementMethod());
                            llFormBuild.addView(ed);

                            int getIdArea = ed.getId();
                            Log.e("CEK","compName : "+compPlaceholder+" | ids : "+getIdArea);
                            String elNameArea = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                            String keyLabelInd = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                            JSONObject dataObjElArea= new JSONObject();
                            dataObjElArea.put("id",getIdArea);
                            dataObjElArea.put("name",keyLabelInd);
                            dataObjElArea.put("required",compRequired);
                            dataObjElArea.put("keyIndo",keyLabelInd);
                            dataObjElArea.put("label",compLabel);
                            dataArrElement.put(dataObjElArea);

                            break;
                        case "p":

                            LinearLayout.LayoutParams lpP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpP.setMargins(0,15,0,-30);
                            if (!compLabel.contains("<p>")) {
                                compLabel = "<p>" + compLabel + "</p>";
                            }

                            TextView tv = new TextView(mContext);
                            tv.setText(Html.fromHtml(compLabel,Html.FROM_HTML_MODE_COMPACT));
                            tv.setLayoutParams(lpP);
                            llFormBuild.addView(tv);

                            break;
                        case "option":
                            lp.setMargins(0,10,0,0);
                            TextView tvOpt = new TextView(mContext);
                            tvOpt.setText(compLabel);
                            tvOpt.setLayoutParams(lp);
                            llFormBuild.addView(tvOpt);

                            JSONArray options = compObj.getJSONArray("options");
                            ArrayList<FormSpin> dataDropDown = new ArrayList<>();
                            for (int jk = 0; jk < options.length(); jk++) {
                                int idOpt = options.getJSONObject(jk).getInt("id");
                                String kodeOpt = options.getJSONObject(jk).getString("kode");
                                String labelOpt = options.getJSONObject(jk).getString("labelIdn");
                                dataDropDown.add(new FormSpin(idOpt,kodeOpt,labelOpt,labelOpt));
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
                            spinner.setPadding(0,0,20,0);
                            ArrayAdapter<FormSpin> adapter2 = new ArrayAdapter<FormSpin>(mContext, android.R.layout.simple_spinner_dropdown_item, dataDropDown);
                            spinner.setAdapter(adapter2);
                            relativeLayout.addView(spinner);

                            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                            layoutParams2.addRule(RelativeLayout.RIGHT_OF,RelativeLayout.TRUE);

                            ImageView imgSpin = new ImageView(mContext);
                            imgSpin.setLayoutParams(layoutParams2);
                            relativeLayout.addView(imgSpin);
                            llFormBuild.addView(relativeLayout);

                            int ids = relativeLayout.getId();
                            Log.e("CEK","compName : "+compPlaceholder+" | ids : "+ids);
                            String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                            String keyLabelIndOpt = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                            JSONObject dataObjElOpt = new JSONObject();
                            dataObjElOpt.put("id",ids);
                            dataObjElOpt.put("name",keyLabelIndOpt);
                            dataObjElOpt.put("required",compRequired);
                            dataObjElOpt.put("url",urlPath);
                            dataObjElOpt.put("keyIndo",keyLabelIndOpt);
                            dataObjElOpt.put("label",compLabel);
                            dataArrElement.put(dataObjElOpt);

                            break;
                        case "datalist":
                            lp.setMargins(0,10,0,0);
                            TextView tvList = new TextView(mContext);
                            tvList.setText(compLabel);
                            tvList.setLayoutParams(lp);
                            llFormBuild.addView(tvList);

                            JSONArray optionsAuto = compObj.getJSONArray("options");
                            ArrayList<FormSpin> dataAuto = new ArrayList<>();
                            for (int jk = 0; jk < optionsAuto.length(); jk++) {
                                int idOpt = optionsAuto.getJSONObject(jk).getInt("id");
                                String kodeOpt = optionsAuto.getJSONObject(jk).getString("kode");
                                String labelOpt = optionsAuto.getJSONObject(jk).getString("labelIdn");
                                dataAuto.add(new FormSpin(idOpt,kodeOpt,labelOpt,labelOpt));
                            }

                            /*LinearLayout.LayoutParams lpAutoList = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lpAutoList.setMargins(0,0,0,10);*/

                            int intAplhabetAuto = randomId();
                            Log.e("CEK","intAplhabetAuto : "+intAplhabetAuto);
                            AutoCompleteTextView autoText = new AutoCompleteTextView(mContext);
                            lp.setMargins(0,0,0,0);
                            autoText.setId(intAplhabetAuto);
                            autoText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                            autoText.setLayoutParams(lp);
                            autoText.setDropDownHeight(200);
                            autoText.setGravity(Gravity.CENTER_VERTICAL);
                            autoText.setSingleLine(true);
                            autoText.setIncludeFontPadding(false);
                            autoText.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                            autoText.setPadding(20,20,20,20);
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
                            ArrayAdapter<FormSpin> adapterAuto = new ArrayAdapter<FormSpin>(mContext, android.R.layout.simple_spinner_dropdown_item, dataAuto);
                            autoText.setAdapter(adapterAuto);

                            llFormBuild.addView(autoText);

                            int idsAuto = autoText.getId();
                            Log.e("CEK","compName : "+compPlaceholder+" | ids : "+idsAuto);
                            String elName2 = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                            String keyLabelIndAuto = keyLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                            JSONObject dataObjElAuto = new JSONObject();
                            dataObjElAuto.put("id",idsAuto);
                            dataObjElAuto.put("name",keyLabelIndAuto);
                            dataObjElAuto.put("required",compRequired);
                            dataObjElAuto.put("keyIndo",keyLabelIndAuto);
                            dataObjElAuto.put("label",compLabel);
                            dataArrElement.put(dataObjElAuto);

                            break;
                        case "a":
                            String alamat = compObj.getString("alamat");
                            TextView tvlink = new TextView(mContext);
                            tvlink.setLayoutParams(lp);
                            tvlink.setClickable(true);
                            CharSequence sequence = Html.fromHtml("<a href=\""+alamat+"\">"+compLabel+"</a>",Html.FROM_HTML_MODE_COMPACT);
                            tvlink.setText(sequence);
                            tvlink.setTextColor(Color.BLUE);
                            tvlink.setMovementMethod(LinkMovementMethod.getInstance());
                            llFormBuild.addView(tvlink);
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
                            lpImg.setMargins(0,10,0,10);

                            ImageView img = new ImageView(mContext);
                            img.setLayoutParams(lpImg);
                            img.setImageDrawable(d);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            llFormBuild.addView(img);
                            break;
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

}
