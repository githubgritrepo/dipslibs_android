package com.evo.mitzoom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.evo.mitzoom.R;
import com.google.android.material.button.MaterialButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RatingActivity extends AppCompatActivity {
    private ImageView thumbs_up, thumbs_down, star1, star2, star3, star4, star5,star1_2, star2_2, star3_2, star4_2, star5_2;
    private LinearLayout allRating;
    private MaterialButton btnSend;
    private EditText kritik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        thumbs_up = findViewById(R.id.thumbs_up);
        thumbs_down = findViewById(R.id.thumbs_down);
        allRating = findViewById(R.id.rating_star);
        btnSend = findViewById(R.id.btnSend);
        kritik = findViewById(R.id.et_kritik);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        star1_2 = findViewById(R.id.star1_2);
        star2_2 = findViewById(R.id.star2_2);
        star3_2 = findViewById(R.id.star3_2);
        star4_2 = findViewById(R.id.star4_2);
        star5_2 = findViewById(R.id.star5_2);
        btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.btnFalse));
        thumbs_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allRating.setClickable(false);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
                thumbs_down.setImageResource(R.drawable.thumbs_down2);
                thumbs_up.setImageResource(R.drawable.thumbs_up);
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                star4.setVisibility(View.VISIBLE);
                star5.setVisibility(View.VISIBLE);
                star1_2.setVisibility(View.GONE);
                star2_2.setVisibility(View.GONE);
                star3_2.setVisibility(View.GONE);
                star4_2.setVisibility(View.GONE);
                star5_2.setVisibility(View.GONE);
                star1.setClickable(false);
                star2.setClickable(false);
                star3.setClickable(false);
                star4.setClickable(false);
                star5.setClickable(false);
                star1_2.setClickable(false);
                star2_2.setClickable(false);
                star3_2.setClickable(false);
                star4_2.setClickable(false);
                star5_2.setClickable(false);
            }
        });
        thumbs_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
                allRating.setClickable(true);
                thumbs_down.setImageResource(R.drawable.thumbs_down);
                star1.setClickable(true);
                star2.setClickable(true);
                star3.setClickable(true);
                star4.setClickable(true);
                star5.setClickable(true);
                star1_2.setClickable(true);
                star2_2.setClickable(true);
                star3_2.setClickable(true);
                star4_2.setClickable(true);
                star5_2.setClickable(true);
            }
        });

        ///Klik Rating
       star1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               thumbs_up.setImageResource(R.drawable.thumbs_up2);
               btnSend.setEnabled(true);
               btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
               Toast.makeText(RatingActivity.this, "Rating 20%", Toast.LENGTH_SHORT).show();
               star1_2.setVisibility(View.VISIBLE);
               star1.setVisibility(View.GONE);
           }
       });
       star1_2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               star1_2.setVisibility(View.GONE);
               star1.setVisibility(View.VISIBLE);
           }
       });

       star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
                Toast.makeText(RatingActivity.this, "Rating 40%", Toast.LENGTH_SHORT).show();
                star2_2.setVisibility(View.VISIBLE);
                star2.setVisibility(View.GONE);
                star1_2.setVisibility(View.VISIBLE);
                star1.setVisibility(View.GONE);
            }
        });
       star2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star2_2.setVisibility(View.GONE);
                star2.setVisibility(View.VISIBLE);
            }
        });

       star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
                Toast.makeText(RatingActivity.this, "Rating 60%", Toast.LENGTH_SHORT).show();
                star3_2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.GONE);
                star2_2.setVisibility(View.VISIBLE);
                star2.setVisibility(View.GONE);
                star1_2.setVisibility(View.VISIBLE);
                star1.setVisibility(View.GONE);
            }
        });
       star3_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star3_2.setVisibility(View.GONE);
                star3.setVisibility(View.VISIBLE);
            }
        });

       star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
                Toast.makeText(RatingActivity.this, "Rating 80%", Toast.LENGTH_SHORT).show();
                star4_2.setVisibility(View.VISIBLE);
                star4.setVisibility(View.GONE);
                star3_2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.GONE);
                star2_2.setVisibility(View.VISIBLE);
                star2.setVisibility(View.GONE);
                star1_2.setVisibility(View.VISIBLE);
                star1.setVisibility(View.GONE);
            }
        });
       star4_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star4_2.setVisibility(View.GONE);
                star4.setVisibility(View.VISIBLE);
            }
        });

       star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbs_up.setImageResource(R.drawable.thumbs_up2);
                btnSend.setEnabled(true);
                btnSend.setBackgroundTintList(RatingActivity.this.getResources().getColorStateList(R.color.Blue));
                Toast.makeText(RatingActivity.this, "Rating 100%", Toast.LENGTH_SHORT).show();
                star5_2.setVisibility(View.VISIBLE);
                star5.setVisibility(View.GONE);
                star4_2.setVisibility(View.VISIBLE);
                star4.setVisibility(View.GONE);
                star3_2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.GONE);
                star2_2.setVisibility(View.VISIBLE);
                star2.setVisibility(View.GONE);
                star1_2.setVisibility(View.VISIBLE);
                star1.setVisibility(View.GONE);
            }
        });
       star5_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star5_2.setVisibility(View.GONE);
                star5.setVisibility(View.VISIBLE);
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
    }
}