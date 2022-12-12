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
import android.widget.Spinner;
import android.widget.TextView;

import com.evo.mitzoom.Model.FormSpin;
import com.evo.mitzoom.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyParserFormBuilder {

    private static JSONArray dataArrObj;
    private static Context mContext;
    private static LinearLayout llFormBuild;

    public MyParserFormBuilder(Context mContext, String dataForm, LinearLayout llFormBuild) throws JSONException {
        this.dataArrObj = new JSONArray(dataForm);
        this.mContext = mContext;
        this.llFormBuild = llFormBuild;
    }

    public static JSONArray getForm() {
        JSONArray dataArrElement = new JSONArray();
        int len = dataArrObj.length();

        for (int i = 0; i < len; i++) {
            try {
                String dataS = dataArrObj.get(i).toString();
                JSONObject dataObj = new JSONObject(dataS);
                JSONArray components = dataObj.getJSONArray("components");
                RadioGroup radioGroup = null;
                int compLen = components.length();
                String elNameRad = "";
                boolean radGroup = false;
                boolean finishRad = false;
                int jkRad = 0;
                int radB = 0;
                for (int j = 0; j < compLen; j++) {
                    LinearLayout.LayoutParams lpLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpLayout.setMargins(0,0,0,10);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                    String compS = components.get(j).toString();
                    JSONObject compObj = new JSONObject(compS);
                    String compName = compObj.getString("name");
                    String compPlaceholder = compObj.getString("placeholder");
                    String compLabel = compObj.getString("label");
                    boolean compRequired = compObj.getBoolean("required");
                    String compType = compObj.getString("type");
                    String compTagHTML = compObj.getString("tagHTML");

                    switch (compTagHTML) {
                        case "input":
                            JSONObject dataObjEl = new JSONObject();
                            if (compType.equals("text") || compType.equals("date")) {
                                if (!compLabel.isEmpty()) {
                                    TextView tv = new TextView(mContext);
                                    tv.setText(compLabel);
                                    tv.setLayoutParams(lp);
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
                                    String lowLabel = compLabel.toLowerCase();
                                    if (lowLabel.equals("npwp")) {
                                        ed.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
                                    }

                                    if (lowLabel.equals("nik") || lowLabel.equals("npwp") || lowLabel.equals("rt") || lowLabel.equals("rw") || lowLabel.contains("kode") ||
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
                                String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",elName);
                                dataObjEl.put("required",compRequired);
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

                                    elNameRad = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");

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
                                    radioGroup.setLayoutParams(lp);
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
                                    dataObjEl.put("name",elNameRad);
                                    dataObjEl.put("required",compRequired);
                                    dataArrElement.put(dataObjEl);
                                }

                            } else if (compType.equals("checkbox")) {
                                TextView tv = new TextView(mContext);
                                tv.setText(compLabel);
                                tv.setLayoutParams(lp);
                                llFormBuild.addView(tv);

                                int intAplhabet = randomId();

                                CheckBox chk = new CheckBox(mContext);
                                chk.setId(intAplhabet);
                                chk.setLayoutParams(lp);
                                chk.setText(compPlaceholder);

                                llFormBuild.addView(chk);

                                int ids = chk.getId();
                                String elName = compLabel.toLowerCase().replace(" ", "").replace("-", "").replace("/", "").replace(".", "");
                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",elName);
                                dataObjEl.put("required",compRequired);
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
                                LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);;
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
                                Log.e("CEK","compName : "+elName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",elName);
                                dataObjEl.put("required",compRequired);
                                dataArrElement.put(dataObjEl);
                            }
                            break;
                        case "p":
                            compLabel = "<p>"+compLabel+"</p>";

                            TextView tv = new TextView(mContext);
                            tv.setText(Html.fromHtml(compLabel,Html.FROM_HTML_MODE_COMPACT));
                            tv.setLayoutParams(lp);
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
                                dataDropDown.add(new FormSpin(idOpt,kodeOpt,labelOpt));
                            }

                            LinearLayout.LayoutParams lpSpin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50);
                            lpSpin.setMargins(0,0,0,10);

                            int intAplhabet = randomId();

                            /*Spinner spinner = new Spinner(mContext);
                            spinner.setId(intAplhabet);
                            spinner.setLayoutParams(lpSpin);
                            spinner.setFocusable(false);
                            spinner.setGravity(Gravity.CENTER_VERTICAL);
                            spinner.setBackground(mContext.getDrawable(R.drawable.bg_textinput));
                            spinner.setPadding(10,10,20,10);
                            ArrayAdapter<FormSpin> adapter = new ArrayAdapter<FormSpin>(mContext, android.R.layout.simple_spinner_dropdown_item, dataDropDown);
                            spinner.setAdapter(adapter);

                            llFormBuild.addView(spinner);*/

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
                            JSONObject dataObjElOpt = new JSONObject();
                            dataObjElOpt.put("id",ids);
                            dataObjElOpt.put("name",elName);
                            dataObjElOpt.put("required",compRequired);
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
                                dataAuto.add(new FormSpin(idOpt,kodeOpt,labelOpt));
                            }

                            LinearLayout.LayoutParams lpAutoList = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lpAutoList.setMargins(0,0,0,10);

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
                            JSONObject dataObjElAuto = new JSONObject();
                            dataObjElAuto.put("id",idsAuto);
                            dataObjElAuto.put("name",elName2);
                            dataObjElAuto.put("required",compRequired);
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

    private static int alphabetToInt(String data) {
        Random random=new Random();
        int dataInt = random.nextInt(99999999);
        /*String repl = data.replace("_", "").replace(" ","").replace("-","");
        String lwS = repl.toLowerCase();
        Map<Character, Integer> map = new HashMap<>(26);
        map.put('a', 1);
        map.put('i', 1);
        map.put('j', 1);
        map.put('q', 1);
        map.put('y', 1);
        map.put('b', 2);
        map.put('k', 2);
        map.put('r', 2);
        map.put('c', 3);
        map.put('g', 3);
        map.put('l', 3);
        map.put('s', 3);
        map.put('d', 4);
        map.put('m', 4);
        map.put('t', 4);
        map.put('e', 5);
        map.put('h', 5);
        map.put('n', 5);
        map.put('x', 5);
        map.put('u', 6);
        map.put('v', 6);
        map.put('w', 6);
        map.put('o', 7);
        map.put('z', 6);
        map.put('f', 8);
        map.put('p', 8);

        char[]crt = lwS.trim().toCharArray();
        String dataS = "";

        for(int i = 0; i < crt.length; i ++){
            int getInt = map.get(crt[i]);
            dataS += String.valueOf(getInt);
        }

        int dataInt = (int) Long.parseLong(dataS);*/

        return dataInt;
    }

    private static String updateDates(Calendar currentTime) {
        int days = currentTime.get(Calendar.DAY_OF_WEEK);
        int tgl = currentTime.get(Calendar.DAY_OF_MONTH);
        int bln = currentTime.get(Calendar.MONTH);
        int thn = currentTime.get(Calendar.YEAR);

        String blnLabel = blnIndo(bln);

        String Tgl = String.format("%02d", tgl);

        String currentDate = Tgl + " " + blnLabel + " " + thn;

        return currentDate;
    }

    private static String blnIndo(int bln) {
        List<String> monthIn = new ArrayList<>();
        monthIn.add("Januari");
        monthIn.add("Februari");
        monthIn.add("Maret");
        monthIn.add("April");
        monthIn.add("Mei");
        monthIn.add("Juni");
        monthIn.add("Juli");
        monthIn.add("Agustus");
        monthIn.add("September");
        monthIn.add("Oktober");
        monthIn.add("November");
        monthIn.add("Desember");
        return monthIn.get(bln);

    }
}
