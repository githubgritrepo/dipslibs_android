package com.evo.mitzoom.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.evo.mitzoom.R;

import java.util.Locale;

public class DipsConnectionForm extends AppCompatActivity {

    private Context mContext;
    private int seconds;
    private boolean running = true;
    private TextView tvSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dips_connection_form);

        mContext = this;

        tvSeconds = (TextView) findViewById(R.id.tvSeconds);
        Button btnConnection = (Button) findViewById(R.id.btnConnection);
        Button btnForm = (Button) findViewById(R.id.btnForm);

        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        btnForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DipsTransactions.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        seconds = 30;
        runTimer(tvSeconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        running = false;
    }

    private void runTimer(TextView timer_run) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%01d", secs);
                timer_run.setText(time);
                if (running) {
                    if (seconds == 0) {
                        running = false;
                        Intent intent = new Intent(mContext, DipsWaitingRoom.class);
                        startActivity(intent);
                    } else {
                        seconds--;
                    }
                }
                handler.postDelayed(this,1000);
            }
        });
    }
}