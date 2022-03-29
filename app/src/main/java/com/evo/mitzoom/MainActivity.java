package com.evo.mitzoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.evo.mitzoom.Constants.AuthConstants;
import com.evo.mitzoom.util.ErrorMsgUtil;
import com.evo.mitzoom.util.NetworkUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKAudioHelper;
import us.zoom.sdk.ZoomVideoSDKAudioOption;
import us.zoom.sdk.ZoomVideoSDKAudioRawData;
import us.zoom.sdk.ZoomVideoSDKChatHelper;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKDelegate;
import us.zoom.sdk.ZoomVideoSDKErrors;
import us.zoom.sdk.ZoomVideoSDKInitParams;
import us.zoom.sdk.ZoomVideoSDKLiveStreamHelper;
import us.zoom.sdk.ZoomVideoSDKLiveStreamStatus;
import us.zoom.sdk.ZoomVideoSDKPasswordHandler;
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason;
import us.zoom.sdk.ZoomVideoSDKPhoneStatus;
import us.zoom.sdk.ZoomVideoSDKRawDataMemoryMode;
import us.zoom.sdk.ZoomVideoSDKRecordingStatus;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKSessionContext;
import us.zoom.sdk.ZoomVideoSDKShareHelper;
import us.zoom.sdk.ZoomVideoSDKShareStatus;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoHelper;
import us.zoom.sdk.ZoomVideoSDKVideoOption;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ZoomVideoSDKDelegate {

    public static BottomNavigationView navigationView;
    private BottomSheetDialog bottomSheetJoin;
    private RelativeLayout rlprogress;
    private FloatingActionButton fabCall;
    private Context mContext;
    private String JWTs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initializeSdk();

        ZoomVideoSDK.getInstance().addListener(this);

        JWTs = processGenerateJWT();
        Log.d("CEK","JWTs : "+JWTs);

        initElement();

        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetJoinDialog();
            }
        });

        navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        navigationView.setBackground(null);
        navigationView.setOnNavigationItemSelectedListener(this);

    }

    private void initElement() {
        rlprogress = (RelativeLayout) findViewById(R.id.rlprogress);
        fabCall = (FloatingActionButton) findViewById(R.id.fabCall);

    }

    private void initializeSdk() {
        ZoomVideoSDKInitParams params = new ZoomVideoSDKInitParams();
        params.domain = AuthConstants.WEB_DOMAIN; // Required
        params.enableLog = true; // Optional for debugging
        params.videoRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;
        params.audioRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;
        params.shareRawDataMemoryMode = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap;

        ZoomVideoSDK sdk = ZoomVideoSDK.getInstance();
        int initResult = sdk.initialize(this, params);
        if (initResult == ZoomVideoSDKErrors.Errors_Success) {
            // You have successfully initialized the SDK
            Log.d("CEK","successfully initialized the SDK");
        } else {
            // Something went wrong, see error code documentation
            Log.d("CEK","Something went wrong : "+initResult);
            Toast.makeText(this, ErrorMsgUtil.getMsgByErrorCode(initResult), Toast.LENGTH_LONG).show();
        }

    }

    private String processGenerateJWT() {
        Date dates = new Date();
        long times = dates.getTime();
        double timesIat = Math.ceil(times / 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dates);
        calendar.add(Calendar.SECOND, 5400);
        Date expTimes = calendar.getTime();
        long expTime = expTimes.getTime();
        double timesExp = Math.ceil(expTime / 1000);

        String appKey = AuthConstants.APP_KEY;

        String claims = "{\"app_key\":\""+appKey+"\"," +
                "\"version\":1," +
                "\"role_type\":0," +
                "\"user_identity\":\"\","+
                "\"session_key\":\"test\","+
                "\"iat\":"+timesIat+"," +
                "\"exp\":"+timesExp+"," +
                "\"tpc\":\"percobaansesi20220315\"}";

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
                /*String fullname = et_fullname.getText().toString().trim();
                joinVideo();
                if (!fullname.isEmpty()) {
                    bottomSheetJoin.dismiss();
                } else {
                    Toast.makeText(mContext,"Nama Anda harus diisi",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        bottomSheetJoin.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

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

    private void joinVideo() {
        if (!NetworkUtil.hasDataNetwork(this)) {
            Toast.makeText(this, "Connection Failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        JWT jwt = new JWT(JWTs);
        Map<String, Claim> allClaims = jwt.getClaims();
        String sessionName = allClaims.get("tpc").asString();

        // Setup audio options
        ZoomVideoSDKAudioOption audioOptions = new ZoomVideoSDKAudioOption();
        audioOptions.connect = true; // Auto connect to audio upon joining
        audioOptions.mute = true; // Auto mute audio upon joining
        // Setup video options
        ZoomVideoSDKVideoOption videoOptions = new ZoomVideoSDKVideoOption();
        videoOptions.localVideoOn = true; // Turn on local/self video upon joining

        ZoomVideoSDKSessionContext params = new ZoomVideoSDKSessionContext();
        params.sessionName = sessionName;
        params.userName = "Fulanah";
        params.token = JWTs;
        params.audioOption = audioOptions;
        params.videoOption = videoOptions;

        ZoomVideoSDKSession session = ZoomVideoSDK.getInstance().joinSession(params);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onSessionJoin() {

    }

    @Override
    public void onSessionLeave() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onUserJoin(ZoomVideoSDKUserHelper zoomVideoSDKUserHelper, List<ZoomVideoSDKUser> list) {

    }

    @Override
    public void onUserLeave(ZoomVideoSDKUserHelper zoomVideoSDKUserHelper, List<ZoomVideoSDKUser> list) {

    }

    @Override
    public void onUserVideoStatusChanged(ZoomVideoSDKVideoHelper zoomVideoSDKVideoHelper, List<ZoomVideoSDKUser> list) {

    }

    @Override
    public void onUserAudioStatusChanged(ZoomVideoSDKAudioHelper zoomVideoSDKAudioHelper, List<ZoomVideoSDKUser> list) {

    }

    @Override
    public void onUserShareStatusChanged(ZoomVideoSDKShareHelper zoomVideoSDKShareHelper, ZoomVideoSDKUser zoomVideoSDKUser, ZoomVideoSDKShareStatus zoomVideoSDKShareStatus) {

    }

    @Override
    public void onLiveStreamStatusChanged(ZoomVideoSDKLiveStreamHelper zoomVideoSDKLiveStreamHelper, ZoomVideoSDKLiveStreamStatus zoomVideoSDKLiveStreamStatus) {

    }

    @Override
    public void onChatNewMessageNotify(ZoomVideoSDKChatHelper zoomVideoSDKChatHelper, ZoomVideoSDKChatMessage zoomVideoSDKChatMessage) {

    }

    @Override
    public void onUserHostChanged(ZoomVideoSDKUserHelper zoomVideoSDKUserHelper, ZoomVideoSDKUser zoomVideoSDKUser) {

    }

    @Override
    public void onUserManagerChanged(ZoomVideoSDKUser zoomVideoSDKUser) {

    }

    @Override
    public void onUserNameChanged(ZoomVideoSDKUser zoomVideoSDKUser) {

    }

    @Override
    public void onUserActiveAudioChanged(ZoomVideoSDKAudioHelper zoomVideoSDKAudioHelper, List<ZoomVideoSDKUser> list) {

    }

    @Override
    public void onSessionNeedPassword(ZoomVideoSDKPasswordHandler zoomVideoSDKPasswordHandler) {

    }

    @Override
    public void onSessionPasswordWrong(ZoomVideoSDKPasswordHandler zoomVideoSDKPasswordHandler) {

    }

    @Override
    public void onMixedAudioRawDataReceived(ZoomVideoSDKAudioRawData zoomVideoSDKAudioRawData) {

    }

    @Override
    public void onOneWayAudioRawDataReceived(ZoomVideoSDKAudioRawData zoomVideoSDKAudioRawData, ZoomVideoSDKUser zoomVideoSDKUser) {

    }

    @Override
    public void onShareAudioRawDataReceived(ZoomVideoSDKAudioRawData zoomVideoSDKAudioRawData) {

    }

    @Override
    public void onCommandReceived(ZoomVideoSDKUser zoomVideoSDKUser, String s) {

    }

    @Override
    public void onCommandChannelConnectResult(boolean b) {

    }

    @Override
    public void onCloudRecordingStatus(ZoomVideoSDKRecordingStatus zoomVideoSDKRecordingStatus) {

    }

    @Override
    public void onHostAskUnmute() {

    }

    @Override
    public void onInviteByPhoneStatus(ZoomVideoSDKPhoneStatus zoomVideoSDKPhoneStatus, ZoomVideoSDKPhoneFailedReason zoomVideoSDKPhoneFailedReason) {

    }
}