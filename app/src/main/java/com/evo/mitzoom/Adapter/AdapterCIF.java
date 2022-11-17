package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Helper.MyParserFormBuilder;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdapterCIF extends RecyclerView.Adapter<AdapterCIF.ViewHolder> {
    private JSONObject objValCIF;
    private JSONArray dataList;
    private Context mContext;
    private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();
    private SessionManager sessions;
    private JSONArray dataElement;

    public AdapterCIF(JSONArray dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
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
            String nameForm = dataList.getJSONObject(position).getString("nameForm");
            JSONArray dataListForm = dataList.getJSONObject(position).getJSONArray("dataList");

            MyParserFormBuilder parseForm = new MyParserFormBuilder(mContext, dataListForm.toString(), holder.container);
            expansionsCollection.add(holder.expansionLayout);
            dataElement = parseForm.getForm();
            Log.e("CEK","dataElement : "+dataElement.toString());
            Log.e("CEK","nameForm : "+nameForm);
            JSONObject objEl = objValCIF.getJSONObject(nameForm);
            processMatchData(holder,objEl);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processMatchData(ViewHolder holder, JSONObject objEl) {
        Log.e("CEK","processDataFromOCR : "+objEl.toString());
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
                                    ed.setText(valEl);
                                } else if (llFormBuild.getChildAt(i) instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) llFormBuild.getChildAt(i);

                                    for(int ch = 0; ch < rg.getChildCount(); ch++) {
                                        int idRad = rg.getChildAt(ch).getId();
                                        RadioButton rb = (RadioButton) rg.findViewById(idRad);
                                        String labelRad = rb.getText().toString();
                                        String valEl = objEl.getString(nameDataEl);
                                        Log.e("CEK","labelRad : "+labelRad+" | valEl : "+valEl);
                                        if (labelRad.equals(valEl)) {
                                            rb.setChecked(true);
                                            break;
                                        }
                                    }
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof CheckBox) {
                                    CheckBox chk = (CheckBox) llFormBuild.getChildAt(i);
                                    String labelCheck = chk.getText().toString();
                                    boolean valEl = objEl.getBoolean(labelCheck);
                                    chk.setChecked(valEl);
                                    break;
                                } else if (llFormBuild.getChildAt(i) instanceof Spinner) {
                                    Spinner spin = (Spinner) llFormBuild.getChildAt(i);
                                    String valEl = objEl.getString(nameDataEl);
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
