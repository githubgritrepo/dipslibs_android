package com.evo.mitzoom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.evo.mitzoom.Constants.AuthConstants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Dashboard extends Activity {

    public final static String ACTION_RETURN_FROM_MEETING = "us.zoom.sdkexample2.action.ReturnFromMeeting";
    public final static String EXTRA_TAB_ID = "tabId";

    public final static int TAB_WELCOME = 1;
    public final static int TAB_MEETING = 2;
    public final static int TAB_FORMREKENING  = 3;

    private View viewTabWelcome;
    private View viewTabMeeting;
    private View viewTabPage2;
    private Button btnTabWelcome;
    private Button btnTabMeeting;
    private Button btnTabPage2;

    private BottomSheetDialog bottomSheetJoin;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mContext = this;

        setupTabs();

    }

    private String processGenerateJWT() {
        Date dates = new Date();
        long times = dates.getTime();
        double timesIat = Math.ceil(times / 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dates);
        calendar.add(Calendar.SECOND, 3600);
        Date expTimes = calendar.getTime();
        long expTime = expTimes.getTime();
        double timesExp = Math.ceil(expTime / 1000);

        String appKey = AuthConstants.APP_KEY;

        String claims = "{\"appKey\":\""+appKey+"\"," +
                "\"iat\":"+timesIat+"," +
                "\"exp\":"+timesExp+"," +
                "\"tokenExp\":"+timesExp+"}";

        String secretKey = AuthConstants.APP_SECRET;
        String base64Secret = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                base64Secret = Base64.getEncoder().encodeToString(secretKey.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String jwts = Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                .setPayload(claims)
                .signWith(SignatureAlgorithm.HS256,base64Secret)
                .compact();

        return jwts;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupTabs() {
        viewTabWelcome = findViewById(R.id.viewTabWelcome);
        viewTabMeeting = findViewById(R.id.viewTabMeeting);
        viewTabPage2 = findViewById(R.id.viewTabPage2);
        btnTabWelcome = (Button)findViewById(R.id.btnTabWelcome);
        btnTabMeeting = (Button)findViewById(R.id.btnTabMeeting);
        btnTabPage2 = (Button)findViewById(R.id.btnTabPage2);

        selectTab(TAB_WELCOME);

        btnTabMeeting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectTab(TAB_MEETING);
            }
        });

        btnTabWelcome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectTab(TAB_WELCOME);
            }
        });

        btnTabPage2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectTab(TAB_FORMREKENING);
            }
        });
    }

    private void selectTab(int tabId) {
        if(tabId == TAB_WELCOME) {
            viewTabWelcome.setVisibility(View.VISIBLE);
            viewTabMeeting.setVisibility(View.GONE);
            viewTabPage2.setVisibility(View.GONE);
            btnTabWelcome.setSelected(true);
            btnTabMeeting.setSelected(false);
            btnTabPage2.setSelected(false);
        } else if(tabId == TAB_FORMREKENING) {
            viewTabWelcome.setVisibility(View.GONE);
            viewTabMeeting.setVisibility(View.GONE);
            viewTabPage2.setVisibility(View.VISIBLE);
            btnTabWelcome.setSelected(false);
            btnTabMeeting.setSelected(false);
            btnTabPage2.setSelected(true);


        } else if(tabId == TAB_MEETING) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // disable animation
        overridePendingTransition(0,0);

        String action = intent.getAction();

        if(ACTION_RETURN_FROM_MEETING.equals(action)) {
            int tabId = intent.getIntExtra(EXTRA_TAB_ID, TAB_WELCOME);
            selectTab(tabId);
        }
    }

    private void bottomSheetJoinDialog() {
        bottomSheetJoin = new BottomSheetDialog(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.layout_bottomsheetdialog, null);
        bottomSheetJoin.setContentView(v);

        EditText et_fullname = (EditText) v.findViewById(R.id.et_fullname);
        Button btn_Start = (Button) v.findViewById(R.id.btnJoin);

        FrameLayout frameLayout = bottomSheetJoin.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (frameLayout != null) {
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = et_fullname.getText().toString().trim();

                if (!fullname.isEmpty()) {
                    bottomSheetJoin.dismiss();
                    startMeeting();
                } else {
                    Toast.makeText(mContext,"Nama Anda harus diisi",Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomSheetJoin.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                selectTab(TAB_WELCOME);
            }
        });

        v.findViewById(R.id.imageClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetJoin.dismiss();
            }
        });

        bottomSheetJoin.show();

    }

    private void startMeeting() {


    }
}