package com.evo.mitzoom.ui;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.google.android.material.button.MaterialButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RatingActivity extends AppCompatActivity {
    private ImageView thumbs_up, thumbs_down;
    private MaterialButton btnSend;
    private RatingBar rating;
    private EditText kritik;
    private Context mContext;
    private SessionManager sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        sessions = new SessionManager(mContext);
        String lang = sessions.getLANG();
        setLocale(this, lang);

        setContentView(R.layout.activity_rating);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
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
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
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
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void PopUp(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(RatingActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(getResources().getString(R.string.rating_pop_up_content));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.done));
        sweetAlertDialog.show();
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
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