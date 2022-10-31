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
                for (int j = 0; j < components.length(); j++) {
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
                                TextView tv = new TextView(mContext);
                                tv.setText(compLabel);
                                tv.setLayoutParams(lp);
                                llFormBuild.addView(tv);

                                int intAplhabet = alphabetToInt(compName);

                                EditText ed = new EditText(mContext);
                                ed.setId(intAplhabet);
                                if (compType.equals("date")) {
                                    ed.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                                    ed.setClickable(false);
                                    ed.setGravity(Gravity.CENTER_VERTICAL);
                                    ed.setIncludeFontPadding(false);
                                    ed.setFocusable(false);
                                    ed.setFocusableInTouchMode(false);
                                    ed.setCursorVisible(false);

                                    Calendar currentTime = Calendar.getInstance();

                                    DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            currentTime.set(Calendar.YEAR, year);
                                            currentTime.set(Calendar.MONTH, month);
                                            currentTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                            String getDates = updateDates(currentTime);
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
                                    if (compLabel.equals("Nama")) {
                                        ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                                    } else if (compLabel.equals("NIK")) {
                                        ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                    }
                                }
                                ed.setLayoutParams(lp);
                                llFormBuild.addView(ed);

                                int ids = ed.getId();
                                Log.e("CEK","compName : "+compName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",compName);
                                dataObjEl.put("required",compRequired);
                                dataArrElement.put(dataObjEl);
                            } else if (compType.equals("radio")) {
                                RadioGroup radioGroup = null;
                                if (j == 0) {
                                    int intAplhabet = alphabetToInt("Radio"+compName);
                                    radioGroup = new RadioGroup(mContext);
                                    radioGroup.setLayoutParams(lp);
                                    radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                                    radioGroup.setId(intAplhabet);
                                }

                                int intAplhabet = alphabetToInt(compName);

                                RadioButton rb = new RadioButton(mContext);
                                rb.setLayoutParams(lp2);
                                rb.setId(intAplhabet);
                                rb.setText(compLabel);

                                radioGroup.addView(rb);

                                if (j == components.length()-1) {
                                    llFormBuild.addView(radioGroup);
                                }

                                int ids = radioGroup.getId();
                                Log.e("CEK","compName : "+compName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",compName);
                                dataObjEl.put("required",compRequired);
                                dataArrElement.put(dataObjEl);

                            } else if (compType.equals("checkbox")) {
                                int intAplhabet = alphabetToInt(compName);

                                CheckBox chk = new CheckBox(mContext);
                                chk.setId(intAplhabet);
                                chk.setLayoutParams(lp);
                                chk.setText(compLabel);

                                llFormBuild.addView(chk);

                                int ids = chk.getId();
                                Log.e("CEK","compName : "+compName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",compName);
                                dataObjEl.put("required",compRequired);
                                dataArrElement.put(dataObjEl);

                            } else if (compType.equals("file")) {
                                int intAplhabet = alphabetToInt(compName);

                                lp.setMargins(0,10,0,10);
                                LinearLayout ln = new LinearLayout(mContext);
                                ln.setId(intAplhabet);
                                ln.setLayoutParams(lp);
                                ln.setOrientation(LinearLayout.HORIZONTAL);
                                ln.setGravity(Gravity.CENTER);
                                ln.setBackground(mContext.getDrawable(R.drawable.oval_background_10dp));
                                ln.setBackgroundTintList(ColorStateList.valueOf(mContext.getColor(R.color.color_6b6b6b)));

                                LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(20,20);
                                ImageView img = new ImageView(mContext);
                                img.setLayoutParams(lpImg);
                                img.setImageDrawable(mContext.getDrawable(R.drawable.galeri));
                                ln.addView(img);

                                lp2.gravity = Gravity.CENTER_VERTICAL;
                                lp2.setMarginStart(10);
                                lp2.setMarginEnd(10);
                                TextView tv = new TextView(mContext);
                                tv.setText(compLabel);
                                tv.setLayoutParams(lp2);
                                tv.setTextColor(Color.WHITE);
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                                ln.addView(tv);

                                llFormBuild.addView(ln);

                                int ids = ln.getId();
                                Log.e("CEK","compName : "+compName+" | ids : "+ids);
                                dataObjEl.put("id",ids);
                                dataObjEl.put("name",compName);
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

                            LinearLayout.LayoutParams lpSpin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,50);
                            lpSpin.setMargins(0,0,0,10);

                            int intAplhabet = alphabetToInt(compName);

                            Spinner spinner = new Spinner(mContext);
                            spinner.setId(intAplhabet);
                            spinner.setLayoutParams(lpSpin);
                            ArrayAdapter<FormSpin> adapter = new ArrayAdapter<FormSpin>(mContext, android.R.layout.simple_spinner_dropdown_item, dataDropDown);
                            spinner.setAdapter(adapter);

                            llFormBuild.addView(spinner);

                            int ids = spinner.getId();
                            Log.e("CEK","compName : "+compName+" | ids : "+ids);
                            JSONObject dataObjElOpt = new JSONObject();
                            dataObjElOpt.put("id",ids);
                            dataObjElOpt.put("name",compName);
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

                            int intAplhabetAuto = alphabetToInt(compName);
                            Log.e("CEK","intAplhabetAuto : "+intAplhabetAuto);
                            AutoCompleteTextView autoText = new AutoCompleteTextView(mContext);
                            lp.setMargins(0,0,0,0);
                            autoText.setId(intAplhabetAuto);
                            autoText.setLayoutParams(lp);
                            autoText.setDropDownHeight(200);
                            autoText.setGravity(Gravity.CENTER_VERTICAL);
                            autoText.setSingleLine(true);
                            autoText.setIncludeFontPadding(false);
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
                            Log.e("CEK","compName : "+compName+" | ids : "+idsAuto);
                            JSONObject dataObjElAuto = new JSONObject();
                            dataObjElAuto.put("id",idsAuto);
                            dataObjElAuto.put("name",compName);
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

    private static int alphabetToInt(String data) {
        String repl = data.replace("_", "");
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

        int dataInt = (int) Long.parseLong(dataS);

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
