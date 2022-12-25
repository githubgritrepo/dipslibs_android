package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterCIF extends RecyclerView.Adapter<AdapterCIF.ViewHolder> {
    private final RabbitMirroring rabbitMirroring;
    private JSONObject objValCIF;
    private JSONArray dataList;
    private Context mContext;
    private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();
    private SessionManager sessions;
    private JSONArray dataElement;
    int lasLenChar;
    boolean backSpaceChar;

    public AdapterCIF(JSONArray dataList, Context mContext, RabbitMirroring rabbitMirroring) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.rabbitMirroring = rabbitMirroring;
        sessions = new SessionManager(mContext);
        String valDataCIF = sessions.getCIF();
        try {
            objValCIF = new JSONObject(valDataCIF);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portfolio_new,parent,false);
        return new AdapterCIF.ViewHolder(views);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String labelForm = dataList.getJSONObject(position).getString("labelForm");
            String nameForm = dataList.getJSONObject(position).getString("nameForm");
            JSONArray dataListForm = dataList.getJSONObject(position).getJSONArray("dataList");

            MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataListForm.toString(), holder.container);
            expansionsCollection.add(holder.expansionLayout);
            dataElement = parseForm.getForm();
            Log.e("CEK","dataElement : "+dataElement.toString());
            Log.e("CEK","nameForm : "+nameForm);
            holder.tv_nama_product.setText(labelForm);
            JSONObject objEl = objValCIF.getJSONObject(nameForm);
            processValidationActionForm(holder.container,objEl,nameForm);
            processMatchData(holder,objEl,nameForm);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processValidationActionForm(LinearLayout container, JSONObject objEl, String nameForm) {
        JSONObject dataFormCIF = new JSONObject();

        LinearLayout llFormBuild = container;
        int child = llFormBuild.getChildCount();
        JSONArray idElement = dataElement;

        if (child > 0 && idElement.length() > 0) {
            for (int i = 0; i < child; i++) {
                int idEl = llFormBuild.getChildAt(i).getId();
                if (idEl > 0 || idEl < -1) {
                    for (int j = 0; j < idElement.length(); j++) {
                        try {
                            int idDataEl = idElement.getJSONObject(j).getInt("id");
                            String nameDataEl = idElement.getJSONObject(j).getString("name");
                            if (idEl == idDataEl) {

                                if (llFormBuild.getChildAt(i) instanceof EditText) {
                                    EditText ed = (EditText) llFormBuild.getChildAt(i);
                                    ed.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (nameDataEl.equals("npwp")) {
                                                lasLenChar = charSequence.length();
                                            }
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            Log.e("CEK",nameDataEl+" : "+charSequence);
                                            try {
                                                objEl.put(nameDataEl, charSequence);
                                                dataFormCIF.put(nameForm,objEl);
                                                objValCIF.put(nameForm,objEl);
                                                sessions.saveCIF(objValCIF.toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            rabbitMirroring.MirroringSendKey(dataFormCIF);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (nameDataEl.equals("npwp")) {
                                                ed.removeTextChangedListener(this);
                                                backSpaceChar = lasLenChar > s.length();
                                                if (!backSpaceChar) {
                                                    String dataNPWP = s.toString();
                                                    Log.e("CEK", "dataNPWP : " + dataNPWP);
                                                    String formatNPWP = "";
                                                    if (dataNPWP.length() == 2 || dataNPWP.length() == 6 || dataNPWP.length() == 10 || dataNPWP.length() == 16) {
                                                        formatNPWP = ".";
                                                    } else if (dataNPWP.length() == 12) {
                                                        formatNPWP = "-";
                                                    }
                                                    String cekBuilder = new StringBuilder(dataNPWP).insert(dataNPWP.length(), formatNPWP).toString();
                                                    ed.setText(cekBuilder);
                                                    ed.setSelection(cekBuilder.length());
                                                }
                                                ed.addTextChangedListener(this);
                                            }
                                        }
                                    });
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);
                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            int selectedId = rg.getCheckedRadioButtonId();
                                            if (selectedId > 0 || selectedId < -1) {
                                                RadioButton rb = (RadioButton) rg.findViewById(selectedId);
                                                String results = rb.getText().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormCIF.put(nameForm,objEl);
                                                    objValCIF.put(nameForm,objEl);
                                                    sessions.saveCIF(objValCIF.toString());
                                                    rabbitMirroring.MirroringSendKey(dataFormCIF);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    chk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            boolean isChk = chk.isChecked();
                                            if (isChk) {
                                                try {
                                                    objEl.put(nameDataEl, isChk);
                                                    dataFormCIF.put(nameForm,objEl);
                                                    objValCIF.put(nameForm,objEl);
                                                    sessions.saveCIF(objValCIF.toString());
                                                    rabbitMirroring.MirroringSendKey(dataFormCIF);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = spin.getSelectedItem().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormCIF.put(nameForm,objEl);
                                                objValCIF.put(nameForm,objEl);
                                                sessions.saveCIF(objValCIF.toString());
                                                rabbitMirroring.MirroringSendKey(dataFormCIF);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout rl = (RelativeLayout) llFormBuild.getChildAt(i);
                                    if (rl.getChildAt(0) instanceof Spinner) {
                                        Spinner spin = (Spinner) rl.getChildAt(0);
                                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                Log.e("CEK","getSelectedItem : "+spin.getSelectedItem().toString());
                                                String results = spin.getSelectedItem().toString();
                                                try {
                                                    objEl.put(nameDataEl, results);
                                                    dataFormCIF.put(nameForm,objEl);
                                                    objValCIF.put(nameForm,objEl);
                                                    sessions.saveCIF(objValCIF.toString());
                                                    rabbitMirroring.MirroringSendKey(dataFormCIF);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                        break;
                                    }
                                } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                    autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String results = autoText.getText().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormCIF.put(nameForm,objEl);
                                                objValCIF.put(nameForm,objEl);
                                                sessions.saveCIF(objValCIF.toString());
                                                rabbitMirroring.MirroringSendKey(dataFormCIF);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean b) {
                                            String results = autoText.getText().toString();
                                            try {
                                                objEl.put(nameDataEl, results);
                                                dataFormCIF.put(nameForm,objEl);
                                                objValCIF.put(nameForm,objEl);
                                                sessions.saveCIF(objValCIF.toString());
                                                rabbitMirroring.MirroringSendKey(dataFormCIF);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof LinearLayout) {
                                    /*LinearLayout ll = (LinearLayout) llFormBuild.getChildAt(i);
                                    Log.e("CEK", "LinearLayout getChildCount : " + ll.getChildCount());
                                    if (ll.getChildCount() > 1) {
                                        if (ll.getChildAt(0) instanceof LinearLayout) {
                                            LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
                                            Log.e("CEK", "MASUK LinearLayout CHILD ke-" + i);

                                            TextView tvll = (TextView) ll2.getChildAt(1);
                                            String txt = tvll.getText().toString();
                                            Log.e("CEK", "tvll : " + txt);
                                            if (txt.toLowerCase().indexOf("gambar") > 0 || txt.toLowerCase().indexOf("image") > 0) {
                                                tvSavedImg = (TextView) ll.getChildAt(1);
                                                ll2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        REQUESTCODE_GALLERY = 201;
                                                        chooseFromSD();
                                                    }
                                                });
                                            } else {
                                                tvSavedFile = (TextView) ll.getChildAt(1);
                                                ll2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent();*/
                                                        //intent.setType("*/*");
                                                        /*intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                        String[] mimetypes = { "application/pdf", "application/doc", "text/*" };

                                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                                        startActivityForResult(intent, REQUESTCODE_FILE);
                                                    }
                                                });
                                            }
                                        }
                                    }*/
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private boolean checkOCREnabled(String key) {
        boolean flag = false;
        if (key.contains("nama") && key.contains("identitas")) {
            flag = true;
        } else if (key.contains("provinsi")) {
            flag = true;
        } else if (key.contains("kabupaten") || key.contains("kota")) {
            flag = true;
        } else if (key.contains("noidentitas") || key.contains("nomoridentitas")) {
            flag = true;
        } else if (key.contains("tempat") && key.contains("lahir")) {
            flag = true;
        } else if (key.contains("tanggal") && key.contains("lahir")) {
            flag = true;
        } else if (key.contains("kelamin")) {
            flag = true;
        } else if (key.contains("alamat") && key.contains("identitas")) {
            flag = true;
        } else if (key.equals("rt")) {
            flag = true;
        } else if (key.equals("rw")) {
            flag = true;
        } else if (key.contains("kelurahan") || key.contains("desa")) {
            flag = true;
        } else if (key.contains("kecamatan")) {
            flag = true;
        } else if (key.contains("agama")) {
            flag = true;
        } else if (key.contains("nikah") || key.contains("menikah")) {
            flag = true;
        } else if (key.contains("warganegara")) {
            flag = true;
        }

        return flag;
    }

    private void processMatchData(ViewHolder holder, JSONObject objEl, String nameForm) {
        if (objEl != null) {
            Log.e("CEK", "processDataFromOCR : " + objEl.toString());
            LinearLayout llFormBuild = holder.container;
            int child = llFormBuild.getChildCount();

            if (child > 0 && dataElement.length() > 0) {
                for (int i = 0; i < child; i++) {
                    int idEl = llFormBuild.getChildAt(i).getId();
                    if (idEl > 0 || idEl < -1) {
                        for (int j = 0; j < dataElement.length(); j++) {
                            try {
                                int idDataEl = dataElement.getJSONObject(j).getInt("id");
                                String nameDataEl = dataElement.getJSONObject(j).getString("name");
                                if (idEl == idDataEl) {
                                    if (llFormBuild.getChildAt(i) instanceof EditText) {
                                        EditText ed = (EditText) llFormBuild.getChildAt(i);
                                        String valEl = objEl.getString(nameDataEl);
                                        Log.e("CEK", "processMatchData "+nameDataEl+" : "+valEl);
                                        ed.setText(valEl);
                                        if (nameForm.equals("datadiri")) {
                                            boolean chkEnable = checkOCREnabled(nameDataEl);
                                            if (chkEnable) {
                                                ed.setEnabled(false);
                                            }
                                        }
                                    } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                        RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                        for (int ch = 0; ch < rg.getChildCount(); ch++) {
                                            int idRad = rg.getChildAt(ch).getId();
                                            RadioButton rb = (RadioButton) rg.findViewById(idRad);
                                            String labelRad = rb.getText().toString();
                                            String valEl = objEl.getString(nameDataEl);
                                            Log.e("CEK", "labelRad : " + labelRad + " | valEl : " + valEl);
                                            if (labelRad.equals(valEl)) {
                                                rb.setChecked(true);
                                                if (nameForm.equals("datadiri")) {
                                                    boolean chkEnable = checkOCREnabled(nameDataEl);
                                                    if (chkEnable) {
                                                        for (int ch2 = 0; ch2 < rg.getChildCount(); ch2++) {
                                                            int idRad2 = rg.getChildAt(ch2).getId();
                                                            RadioButton rb2 = (RadioButton) rg.findViewById(idRad2);
                                                            rb2.setEnabled(false);
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                        CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                        String labelCheck = chk.getText().toString();
                                        boolean valEl = false;
                                        if (objEl.has(labelCheck)) {
                                            valEl = objEl.getBoolean(labelCheck);
                                            if (nameForm.equals("datadiri")) {
                                                boolean chkEnable = checkOCREnabled(labelCheck);
                                                if (chkEnable) {
                                                    chk.setEnabled(false);
                                                    chk.setClickable(false);
                                                    /*for (int ch2 = 0; ch2 < llFormBuild.getChildCount(); ch2++) {
                                                        int idRad2 = llFormBuild.getChildAt(ch2).getId();
                                                        CheckBox rb2 = (CheckBox) llFormBuild.findViewById(idRad2);
                                                        rb2.setEnabled(false);
                                                    }*/
                                                }
                                            }
                                        } else if (objEl.has(nameDataEl)) {
                                            valEl = objEl.getBoolean(nameDataEl);
                                            if (nameForm.equals("datadiri")) {
                                                boolean chkEnable = checkOCREnabled(nameDataEl);
                                                if (chkEnable) {
                                                    chk.setEnabled(false);
                                                    chk.setClickable(false);
                                                }
                                            }
                                        }
                                        chk.setChecked(valEl);
                                        break;
                                    } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                        Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                        String valEl = objEl.getString(nameDataEl);
                                        if (nameForm.equals("datadiri")) {
                                            boolean chkEnable = checkOCREnabled(nameDataEl);
                                            if (chkEnable) {
                                                spin.setClickable(false);
                                            }
                                        }
                                        for (int ch = 0; ch < spin.getCount(); ch++) {
                                            if (spin.getItemAtPosition(ch).toString().equals(valEl)) {
                                                spin.setSelection(ch);
                                                break;
                                            }
                                        }
                                        break;
                                    } else if (llFormBuild.getChildAt(i) instanceof AutoCompleteTextView) {
                                        AutoCompleteTextView autoText = (AutoCompleteTextView) llFormBuild.getChildAt(i);
                                        String valEl = objEl.getString(nameDataEl);
                                        autoText.setText(valEl);
                                        if (nameForm.equals("datadiri")) {
                                            boolean chkEnable = checkOCREnabled(nameDataEl);
                                            if (chkEnable) {
                                                autoText.setEnabled(false);
                                            }
                                        }
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_logo_porto;
        private final TextView tv_nama_product;
        private final ExpansionLayout expansionLayout;
        private final LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_logo_porto = (ImageView) itemView.findViewById(R.id.img_logo_porto);
            tv_nama_product = (TextView) itemView.findViewById(R.id.tv_nama_product);
            expansionLayout = (ExpansionLayout) itemView.findViewById(R.id.expansionLayout);
            container = (LinearLayout) itemView.findViewById(R.id.container);

            img_logo_porto.setVisibility(View.GONE);
        }
    }
}
