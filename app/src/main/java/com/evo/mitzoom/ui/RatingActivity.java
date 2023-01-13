package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RatingActivity extends AppCompatActivity {
    private ImageView thumbs_up, thumbs_down;
    private Button btnSend;
    private RatingBar rating;
    private EditText kritik;
    private Context mContext;
    private SessionManager sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        setLocale(this, lang);
        //LocaleHelper.setLocale(this,lang);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rating);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        rating = findViewById(R.id.ratingBar);
        rating.setClickable(false);
        rating.setFocusable(false);
        rating.setRating(0);
        thumbs_up = findViewById(R.id.thumbs_up);
        thumbs_down = findViewById(R.id.thumbs_down);
        btnSend = findViewById(R.id.btnSend);
        kritik = findViewById(R.id.et_kritik);
        btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.btnFalse));
        thumbs_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating.setClickable(false);
                rating.setFocusable(false);
                rating.setRating(0);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.zm_button));
                thumbs_down.setImageResource(R.drawable.thumbs_down2);
                thumbs_up.setImageResource(R.drawable.thumbs_up);
            }
        });
        thumbs_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating.setClickable(true);
                rating.setFocusable(true);
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.zm_button));
                thumbs_down.setImageResource(R.drawable.thumbs_down);
            }
        });


       btnSend.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               PopUp();
           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void PopUp(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = (ImageView) dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = (TextView) dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = (TextView) dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = (Button) dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = (Button) dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_success));
        tvBodyDialog.setText(getString(R.string.rating_pop_up_content));
        btnConfirmDialog.setText(getString(R.string.done));

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(RatingActivity.this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCustomView(dialogView);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.dismiss();
                sweetAlertDialog.cancel();
                OutApps();
            }
        });
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