package com.evo.mitzoom;

import static com.evo.mitzoom.ui.DipsChooseLanguage.setLocale;
import static com.evo.mitzoom.ui.DipsVideoConfren.text_timer;
import static com.evo.mitzoom.ui.DipsVideoConfren.timer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.Adapter.ChatMsgAdapter;
import com.evo.mitzoom.Adapter.UserVideoAdapter;
import com.evo.mitzoom.Fragments.frag_conferee_agree;
import com.evo.mitzoom.Fragments.frag_file;
import com.evo.mitzoom.Helper.NotificationMgr;
import com.evo.mitzoom.Helper.NotificationService;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.Helper.RabbitMirroring;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.screenshare.ShareToolbar;
import com.evo.mitzoom.ui.DipsVideoConfren;
import com.evo.mitzoom.ui.RatingActivity;
import com.evo.mitzoom.util.ErrorMsgUtil;
import com.evo.mitzoom.util.UserHelper;
import com.evo.mitzoom.util.ZMAdapterOsBugHelper;
import com.evo.mitzoom.view.KeyBoardLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKAudioHelper;
import us.zoom.sdk.ZoomVideoSDKAudioRawData;
import us.zoom.sdk.ZoomVideoSDKAudioStatus;
import us.zoom.sdk.ZoomVideoSDKChatHelper;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKChatMessageDeleteType;
import us.zoom.sdk.ZoomVideoSDKDelegate;
import us.zoom.sdk.ZoomVideoSDKErrors;
import us.zoom.sdk.ZoomVideoSDKLiveStreamHelper;
import us.zoom.sdk.ZoomVideoSDKLiveStreamStatus;
import us.zoom.sdk.ZoomVideoSDKLiveTranscriptionHelper;
import us.zoom.sdk.ZoomVideoSDKMultiCameraStreamStatus;
import us.zoom.sdk.ZoomVideoSDKNetworkStatus;
import us.zoom.sdk.ZoomVideoSDKPasswordHandler;
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason;
import us.zoom.sdk.ZoomVideoSDKPhoneStatus;
import us.zoom.sdk.ZoomVideoSDKProxySettingHandler;
import us.zoom.sdk.ZoomVideoSDKRawDataPipe;
import us.zoom.sdk.ZoomVideoSDKRecordingConsentHandler;
import us.zoom.sdk.ZoomVideoSDKRecordingStatus;
import us.zoom.sdk.ZoomVideoSDKSSLCertificateInfo;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKShareHelper;
import us.zoom.sdk.ZoomVideoSDKShareStatus;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoCanvas;
import us.zoom.sdk.ZoomVideoSDKVideoHelper;

public class BaseMeetingActivity extends AppCompatActivity implements ZoomVideoSDKDelegate, UserVideoAdapter.ItemTapListener, ShareToolbar.Listener {

    protected static final String TAG = "CEK_BaseMeetingActivity";
    public static final int RENDER_TYPE_ZOOMRENDERER = 0;
    private BottomSheetDialog bottomSheetDialog;
    public static final int RENDER_TYPE_OPENGLES = 1;

    public final static int REQUEST_SHARE_SCREEN_PERMISSION = 1001;

    public final static int REQUEST_SYSTEM_ALERT_WINDOW = 1002;

    public final static int REQUEST_SELECT_ORIGINAL_PIC = 1003;

    protected boolean isActivityPaused = false;
    protected ZoomVideoSDKUser mActiveUser;
    protected ZoomVideoSDKUser currentShareUser;
    protected ZoomVideoSDKSession session;

    protected String myDisplayName = "";
    protected String meetingPwd = "";
    protected String sessionName;
    protected boolean isCust;
    protected int renderType;

    protected RecyclerView userVideoList;
    protected LinearLayout videoListContain;
    protected UserVideoAdapter adapter;
    protected View actionBar;
    protected ScrollView actionBarScroll;
    private Intent mScreenInfoData;
    protected ShareToolbar shareToolbar;
    protected ImageView iconShare;
    protected ImageView iconVideo;
    protected ImageView iconAudio;
    protected ImageView iconMore;
    private View shareViewGroup;
    private ImageView shareImageView;
    protected ImageView videoOffView;
    protected KeyBoardLayout keyBoardLayout;
    protected Display display;
    protected DisplayMetrics displayMetrics;
    protected boolean renderWithSurfaceView=true;
    public static Button btnChat;
    public static Button btnFile;
    public int seconds = 0;
    public boolean running = true;
    public boolean wasRunning;
    private int isOn = 0;
    private SessionManager sessions;
    private Context mContext;
    private CardView cardSurf;
    private RelativeLayout llUsersVideo;
    private CardView cardSurfOff;
    private RelativeLayout offUsersVideo;

    protected Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSwafoto = false;
    private RabbitMirroring rabbitMirroring;
    private GifImageView gifLoading;
    private ImageView imgBatikVic;
    private boolean flagClickEnd = false;
    private boolean flagUserLeave = false;
    public static RelativeLayout rlprogress;
    private String lang = "";
    private LinearLayout iconBubble;
    private boolean flagShowLeave = false;

    private ImageView btnClose, btnSend;
    private EditText InputChat;
    private String Chat;
    private ChatMsgAdapter chatMsgAdapter;
    protected RecyclerView chatListView;
    private final List<CharSequence> list = new ArrayList<>();
    private final List<Boolean> isSelf = new ArrayList<>();
    private final List<Boolean> isHost = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        }
        chatMsgAdapter = new ChatMsgAdapter(this, list,isHost, isSelf);
        sessions = new SessionManager(mContext);
        lang = sessions.getLANG();
        setLocale(this,lang);
        //LocaleHelper.setLocale(this,lang);
        sessions.saveFlagUpDoc(false);
        sessions.saveFlagConfAgree(false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        isCust = sessions.getKEY_iSCust();
        isSwafoto = sessions.getKEY_iSSwafoto();

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        if (!renderWithSurfaceView) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(getLayout());
        display = ((WindowManager) getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        session = ZoomVideoSDK.getInstance().getSession();
        ZoomVideoSDK.getInstance().addListener(this);
        parseIntent();
        initView();
        initMeeting();
        updateSessionInfo();
        rabbitMirroring = new RabbitMirroring(mContext);
        DipsVideoConfren.LogoCompany.setVisibility(View.VISIBLE);
        DipsVideoConfren.Zoom.setVisibility(View.VISIBLE);
        //getFragmentPage(new frag_conferee_agree());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent();
    }

    public void runTimer(TextView timer_run) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }
        });
    }

    private void bottomSheetChat(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.frag_chat, null);
        bottomSheetDialog.setContentView(v);
        InputChat = v.findViewById(R.id.et_input_chat);
        btnSend = v.findViewById(R.id.btn_send_chat);
        chatListView = v.findViewById(R.id.chat_list);
        FrameLayout frameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (frameLayout != null) {
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setPeekHeight(500);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        bottomSheetDialog.show();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat = InputChat.getText().toString();
                ZoomVideoSDK.getInstance().getChatHelper().sendChatToAll(Chat);
                InputChat.setText("");
            }
        });
        chatListView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//        if (sessions.getKEY_CHAT() != null){
//            String dataChat = sessions.getKEY_CHAT();
//            Log.d("CEK START PESAN ",dataChat);
//            try {
//                JSONArray jsonArray2 = new JSONArray(dataChat);
//                int panjang = jsonArray2.length();
//                for (int a=0;a<panjang;a++){
//                    String dataChat2 = jsonArray2.get(a).toString();
//                    JSONObject jsonObject = new JSONObject(dataChat2);
//                    boolean isSelf2 = jsonObject.getBoolean("isSelf");
//                    boolean isHost2 = jsonObject.getBoolean("isHost");
//                    String message = jsonObject.getString("message");
//
//                    String [] message2 = message.split("\n");
//                    String SenderName = message2[0]+"\n";
//                    String content = message2[1];
//                    SpannableStringBuilder builder = new SpannableStringBuilder();
//                    builder.append(SenderName).append(content);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),0,SenderName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),SenderName.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置前面的字体颜色
//
//                    isSelf.add(isSelf2);
//                    isHost.add(isHost2);
//                    list.add(builder);
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            Log.d("CEK START PESAN ","MASUK ELSE");
//        }
        chatListView.setAdapter(chatMsgAdapter);
        //updateChatLayoutParams();
        if (!bottomSheetDialog.isShowing()){
            SavedInstanceChat();
        }
    }
    private void updateChatLayoutParams() {
        if (chatMsgAdapter.getItemCount() > 0) {
            chatListView.scrollToPosition(chatMsgAdapter.getItemCount() - 1);
        }
    }

    private void SavedInstanceChat(){
        JSONArray jsonArray = new JSONArray();
        for (int i =0; i < list.size(); i++){
            JSONObject jsons = new JSONObject();
            try {
                jsons.put("isSelf",isSelf.get(i));
                jsons.put("isHost",isHost.get(i));
                jsons.put("message",list.get(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsons);
        }
        String dataArr = jsonArray.toString();
        sessions.saveChat(dataArr);
        Log.d("CEK PESAN ARRAY",dataArr);
    }
    private void getFragmentPage(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CEK","onResume");

        if (isActivityPaused) {
            resumeSubscribe();
        }
        isActivityPaused = false;
        refreshRotation();
        //updateActionBarLayoutParams();

        if (ZoomVideoSDK.getInstance().isInSession()) {
            int size = UserHelper.getAllUsers().size();
            if (size > 0 && adapter.getItemCount() == 0) {
                adapter.addAll();
                updateVideoListLayout();
                refreshUserListAdapter();
            }
        }

        if (wasRunning) {
            running = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void hideStatusBar() {
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
    }

    public static void showProgress(Boolean bool){

        if (bool){
            rlprogress.setVisibility(View.VISIBLE);
        }else {
            rlprogress.setVisibility(View.GONE);
        }
    }

    private void startVideoHandler() {
        Log.d("CEK","MASUK startVideoHandler");
        if (isOn == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sessions.saveFlagUpDoc(false);
                        //Thread.sleep(1000);
                        Log.d("CEK","ON OFF VIDEO is OFF");
                        onOffVideo();
                        onClickMoreSwitchCamera();
                        Thread.sleep(1000);
                        Log.d("CEK","ON OFF VIDEO is ON");
                        onOffVideo();
                        onClickMoreSwitchCamera();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            sessions.saveFlagUpDoc(false);
            sessions.saveMedia(0);
            Log.d("CEK","ON OFF VIDEO is ON");
            onOffVideo();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        int valMedia = sessions.getMedia();
        Log.d("CEK","onPause valMedia : "+valMedia);
        if (valMedia == 1 || valMedia == 2) {
            ZoomVideoSDK.getInstance().getVideoHelper().stopVideo();
        }
        wasRunning = running;
        running = false;
        isActivityPaused = true;
        unSubscribe();
        adapter.clear(false);
        gifLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != shareToolbar) {
            shareToolbar.destroy();
        }
        if (ZMAdapterOsBugHelper.getInstance().isNeedListenOverlayPermissionChanged()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ZMAdapterOsBugHelper.getInstance().stopListenOverlayPermissionChange(this);
            }
        }
        ZoomVideoSDK.getInstance().removeListener(this);
        adapter.onDestroyed();
        sessions.clearPartData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SHARE_SCREEN_PERMISSION:
                if (resultCode != RESULT_OK) {
//                    if (BuildConfig.DEBUG)
//                        Log.d(TAG, "onActivityResult REQUEST_SHARE_SCREEN_PERMISSION no ok ");
//                    break;
                }
                startShareScreen(data);
                break;
            case REQUEST_SYSTEM_ALERT_WINDOW:
                if (ZMAdapterOsBugHelper.getInstance().isNeedListenOverlayPermissionChanged()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ZMAdapterOsBugHelper.getInstance().stopListenOverlayPermissionChange(this);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if ((!Settings.canDrawOverlays(this)) && (!ZMAdapterOsBugHelper.getInstance().isNeedListenOverlayPermissionChanged() || !ZMAdapterOsBugHelper.getInstance().ismCanDraw())) {
                        return;
                    }
                }
                onStartShareScreen(mScreenInfoData);
                break;
            case REQUEST_SELECT_ORIGINAL_PIC: {
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        if (null != selectedImage) {
                            if (currentShareUser == null) {
                                shareImageView.setImageURI(selectedImage);
                                shareViewGroup.setVisibility(View.VISIBLE);
                                int ret = ZoomVideoSDK.getInstance().getShareHelper().startShareView(shareImageView);
                                if (ret == ZoomVideoSDKErrors.Errors_Success) {
                                    onStartShareView();
                                } else {
                                    shareImageView.setImageBitmap(null);
                                    shareViewGroup.setVisibility(View.GONE);
                                    boolean isLocked = ZoomVideoSDK.getInstance().getShareHelper().isShareLocked();
                                    Toast.makeText(this, "Share Fail isLocked=" + isLocked + " ret:" + ret, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(this, "Other is sharing", Toast.LENGTH_LONG).show();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                break;
            }
        }
    }

    protected void resumeSubscribe() {
        if (null != currentShareUser) {
            subscribeShareByUser(currentShareUser);
        } else if (null != mActiveUser) {
            subscribeVideoByUser(mActiveUser);
        }

        if (ZoomVideoSDK.getInstance().isInSession()) {
            List<ZoomVideoSDKUser> userInfoList = UserHelper.getAllUsers();
            if (null != userInfoList && userInfoList.size() > 0) {
                List<ZoomVideoSDKUser> list = new ArrayList<>(userInfoList.size());
                list.addAll(userInfoList);
                adapter.onUserJoin(list);
                selectAndScrollToUser(mActiveUser);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //updateFpsOrientation();
        refreshRotation();
        //updateActionBarLayoutParams();
        updateSmallVideoLayoutParams();
    }

    private void updateActionBarLayoutParams() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) actionBar.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.topMargin = (int) (35 * displayMetrics.scaledDensity);
//            params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//            params.bottomMargin = (int) (22 * displayMetrics.scaledDensity);
        } else {
            params.topMargin = 0;
//            params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_bottom_margin);
        }
        actionBar.setLayoutParams(params);

    }

    protected void onStartShareScreen(Intent data) {
        if (null == shareToolbar) {
            shareToolbar = new ShareToolbar(this,this);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            //MediaProjection  need service with foregroundServiceType mediaProjection in android Q
            boolean hasForegroundNotification = NotificationMgr.hasNotification(NotificationMgr.PT_NOTICICATION_ID);
            if (!hasForegroundNotification) {
                Intent intent = new Intent(this, NotificationService.class);
                startForegroundService(intent);
            }
        }
        int ret = ZoomVideoSDK.getInstance().getShareHelper().startShareScreen(data);
        if (ret == ZoomVideoSDKErrors.Errors_Success) {
            shareToolbar.showToolbar();
            showDesktop();
        }
    }

    protected void showDesktop() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(home);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onClickStopShare() {
        if (ZoomVideoSDK.getInstance().getShareHelper().isSharingOut()) {
            ZoomVideoSDK.getInstance().getShareHelper().stopShare();
            showMeetingActivity();
        }
    }

    private void showMeetingActivity() {
        Intent intent = new Intent(getApplicationContext(), IntegrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(IntegrationActivity.ACTION_RETURN_TO_CONF);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @SuppressLint("NewApi")
    protected void startShareScreen(Intent data) {
        if (data == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 24 && !Settings.canDrawOverlays(this)) {
            if (ZMAdapterOsBugHelper.getInstance().isNeedListenOverlayPermissionChanged())
                ZMAdapterOsBugHelper.getInstance().startListenOverlayPermissionChange(this);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            mScreenInfoData = data;
            startActivityForResult(intent, REQUEST_SYSTEM_ALERT_WINDOW);
        } else {
            onStartShareScreen(data);
        }
    }

    protected void refreshRotation() {
        int displayRotation = display.getRotation();
        ZoomVideoSDK.getInstance().getVideoHelper().rotateMyVideo(displayRotation);
    }

    protected void initMeeting() {

    }

    protected void parseIntent() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            myDisplayName = bundle.getString("name");
            meetingPwd = bundle.getString("password");
            sessionName = bundle.getString("sessionName");
            renderType = bundle.getInt("render_type", RENDER_TYPE_ZOOMRENDERER);
        }
    }

    protected void initView() {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDisp = displayMetrics.widthPixels;
        int dyWidth = (int) Math.ceil(widthDisp / 2);

        Log.e(TAG,"dyWidth : "+dyWidth);

        gifLoading = findViewById(R.id.gifLoading);
        rlprogress = findViewById(R.id.rlprogress);
        imgBatikVic = findViewById(R.id.imgBatikVic);
        llUsersVideo = findViewById(R.id.llUsersVideo);
        cardSurf = findViewById(R.id.cardSurf);
        cardSurfOff = findViewById(R.id.cardSurfOff);
        offUsersVideo= findViewById(R.id.offUsersVideo);
        ImageView video_off_tips2 = findViewById(R.id.video_off_tips2);
        ImageView video_off_tips3 = findViewById(R.id.video_off_tips3);
        userVideoList = findViewById(R.id.userVideoList);
        videoListContain = findViewById(R.id.video_list_contain);
        actionBar = findViewById(R.id.action_bar);
        iconAudio = findViewById(R.id.icon_audio);
        iconVideo = findViewById(R.id.icon_video);
        videoOffView = findViewById(R.id.video_off_tips);
        iconBubble = findViewById(R.id.iconBubble);
        btnChat = findViewById(R.id.icon_chat);
        btnFile = findViewById(R.id.icon_file);
        //llUsersVideo.setVisibility(View.INVISIBLE);
        cardSurf.setVisibility(View.INVISIBLE);
        cardSurfOff.setVisibility(View.VISIBLE);

        int widthCard = cardSurfOff.getMeasuredWidth();
        Log.e(TAG,"widthCard : "+widthCard);
        int dyCardWidth = (int) Math.ceil(widthCard / 2);
        Log.e(TAG,"dyCardWidth : "+dyCardWidth);
        if (dyCardWidth > 0) {
            dyWidth = dyCardWidth;
        }

        dyWidth -= 20;

        adapter = new UserVideoAdapter(this, this, renderType, dyWidth);
        userVideoList.setItemViewCacheSize(0);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        userVideoList.setLayoutManager(layoutManager);
        userVideoList.setAdapter(adapter);

        final int margin = (int) (5 * displayMetrics.scaledDensity);
        userVideoList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(0, 0, 0, 0);
            }
        });

        userVideoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) userVideoList.getLayoutManager();
                    View view = linearLayoutManager.getChildAt(0);
                    if (null == view) {
                        return;
                    }
                    int index = linearLayoutManager.findFirstVisibleItemPosition();
                    int left = view.getLeft();
                    if (left < 0) {
                        if (-left > view.getWidth() / 2) {
                            index = index + 1;
                            if (index == adapter.getItemCount() - 1) {
                                recyclerView.scrollBy(view.getWidth(), 0);
                            } else {
                                recyclerView.scrollBy(view.getWidth() + left + 2 * margin, 0);
                            }
                        } else {
                            recyclerView.scrollBy(left - margin, 0);
                        }
                        if (index == 0) {
                            recyclerView.scrollTo(0, 0);
                        }
                    }
                    view = linearLayoutManager.getChildAt(0);
                    if (null == view) {
                        return;
                    }
                    scrollVideoViewForMargin(view);

                }
            }
        });
    }

    public void onClickAudio(View view) {
        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (null == zoomSDKUserInfo)
            return;
        if (zoomSDKUserInfo.getAudioStatus().getAudioType() == ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_None) {
            ZoomVideoSDK.getInstance().getAudioHelper().startAudio();
        } else {
            if (zoomSDKUserInfo.getAudioStatus().isMuted()) {
                ZoomVideoSDK.getInstance().getAudioHelper().unMuteAudio(zoomSDKUserInfo);
            } else {
                ZoomVideoSDK.getInstance().getAudioHelper().muteAudio(zoomSDKUserInfo);
            }
        }
    }

    public void onClickVideo(View view) {
        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (null == zoomSDKUserInfo)
            return;
        if (zoomSDKUserInfo.getVideoStatus().isOn()) {
            ZoomVideoSDK.getInstance().getVideoHelper().stopVideo();
        } else {
            ZoomVideoSDK.getInstance().getVideoHelper().startVideo();
        }
    }

    private void onOffVideo() {
        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (null == zoomSDKUserInfo)
            return;
        if (zoomSDKUserInfo.getVideoStatus().isOn()) {
            ZoomVideoSDK.getInstance().getVideoHelper().stopVideo();
        } else {
            ZoomVideoSDK.getInstance().getVideoHelper().startVideo();
        }
    }

    public void onClickMoreSwitchCamera() {
        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (null == zoomSDKUserInfo)
            return;
        if (zoomSDKUserInfo.getVideoStatus().isHasVideoDevice() && zoomSDKUserInfo.getVideoStatus().isOn()) {
            ZoomVideoSDK.getInstance().getVideoHelper().switchCamera();
            refreshRotation();
        }
    }

    private void dialogAgentLeave() {
        flagShowLeave = true;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_info));
        tvBodyDialog.setText(getString(R.string.agent_leave));
        btnConfirmDialog.setText(getString(R.string.leave_leave_text));

        SweetAlertDialog dialogEnd = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        dialogEnd.setCustomView(dialogView);
        dialogEnd.setCancelable(false);
        dialogEnd.hideConfirmButton();
        dialogEnd.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagShowLeave = false;
                dialogEnd.cancel();
                dialogEnd.dismissWithAnimation();
                OutboundServiceNew.stopServiceSocket();
                Intent intentOutbound = new Intent(mContext, OutboundServiceNew.class);
                mContext.stopService(intentOutbound);
                releaseResource();
                int ret = ZoomVideoSDK.getInstance().leaveSession(false);
                sessions.clearPartData();
                RabbitMirroring.MirroringSendEndpoint(99);
                trimCache(mContext);
                startActivity(new Intent(getApplicationContext(), RatingActivity.class));
                finish();
            }
        });
    }

    public void onClickEnd(View view) {
        flagClickEnd = true;
        ZoomVideoSDKUser userInfo = session.getMySelf();

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_sweet, null);

        ImageView imgDialog = dialogView.findViewById(R.id.imgDialog);
        TextView tvTitleDialog = dialogView.findViewById(R.id.tvTitleDialog);
        TextView tvBodyDialog = dialogView.findViewById(R.id.tvBodyDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);

        tvTitleDialog.setVisibility(View.GONE);
        btnCancelDialog.setVisibility(View.VISIBLE);

        imgDialog.setImageDrawable(getDrawable(R.drawable.v_dialog_info));
        tvBodyDialog.setText(getString(R.string.leave_message));
        btnCancelDialog.setText(getString(R.string.tidak_not));
        btnConfirmDialog.setText(getString(R.string.label_ya));

        SweetAlertDialog dialogEnd = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
        dialogEnd.setCustomView(dialogView);
        dialogEnd.setCancelable(false);
        dialogEnd.hideConfirmButton();
        dialogEnd.show();

        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OutboundServiceNew.stopServiceSocket();
                Intent intentOutbound = new Intent(mContext, OutboundServiceNew.class);
                mContext.stopService(intentOutbound);

                dialogEnd.dismissWithAnimation();
                releaseResource();
                int ret = ZoomVideoSDK.getInstance().leaveSession(false);
                sessions.clearPartData();
                RabbitMirroring.MirroringSendEndpoint(99);
                trimCache(mContext);
                startActivity(new Intent(getApplicationContext(), RatingActivity.class));
                finish();
            }
        });
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEnd.dismissWithAnimation();
            }
        });

        dialogEnd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                flagClickEnd = false;
            }
        });
    }

    private void MirroringEnd(){
        String idDips = sessions.getKEY_IdDips();
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(true);
            jsons.put("idDips",idDips);
            jsons.put("code",99);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
                Intent serviceIntent = new Intent(mContext, OutboundServiceNew.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    
    public void onClickChat(View view) {
        btnChat.setFocusable(true);
        iconBubble.setVisibility(View.GONE);
        //getFragmentPage(new frag_chat());
        bottomSheetChat();
    }
    public void onClickFile(View view) {
        getFragmentPage(new frag_file());
    }

    private void releaseResource() {
        unSubscribe();
        adapter.clear(true);
        actionBar.setVisibility(View.GONE);
    }

    protected void updateSessionInfo() {
        ZoomVideoSDKSession sessionInfo = ZoomVideoSDK.getInstance().getSession();
        if (ZoomVideoSDK.getInstance().isInSession()) {
            int size = UserHelper.getAllUsers().size();
            if (size <= 0) {
                size = 1;
            }
            if (sessionInfo != null) meetingPwd = sessionInfo.getSessionPassword();
        } else {
            actionBar.setVisibility(View.GONE);
        }
        //if (sessionInfo != null) sessionNameText.setText(sessionInfo.getSessionName());
        if (TextUtils.isEmpty(meetingPwd)) {
            //iconLock.setImageResource(R.drawable.unlock);
        } else {
            //iconLock.setImageResource(R.drawable.small_lock);
        }
    }

//    @Override
//    public void onItemClick() {
//
//    }

    protected int getLayout() {
        return 0;
    }

    protected void unSubscribe() {

    }

    protected void subscribeVideoByUser(ZoomVideoSDKUser user) {

    }

    protected void subscribeShareByUser(ZoomVideoSDKUser user) {

    }

    private void updateSmallVideoLayoutParams() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoListContain.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else {
            videoListContain.setGravity(Gravity.CENTER);
        }
    }

    private void refreshUserListAdapter() {
        if (adapter.getItemCount() > 0) {
            videoListContain.setVisibility(View.VISIBLE);
            if (adapter.getSelectedVideoUser() == null) {
                ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
                if (null != zoomSDKUserInfo) {
                    selectAndScrollToUser(zoomSDKUserInfo);
                }
            }
        }
    }

    protected void selectAndScrollToUser(ZoomVideoSDKUser user) {
        if (null == user) {
            return;
        }
        adapter.updateSelectedVideoUser(user);
        int index = adapter.getIndexByUser(user);
        if (index >= 0) {
            LinearLayoutManager manager = (LinearLayoutManager) userVideoList.getLayoutManager();
            int first = manager.findFirstVisibleItemPosition();
            int last = manager.findLastVisibleItemPosition();
            if (index > last || index < first) {
                userVideoList.scrollToPosition(index);
                adapter.notifyDataSetChanged();
            }
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) userVideoList.getLayoutManager();
        View view = linearLayoutManager.getChildAt(0);
        if (null != view) {
            scrollVideoViewForMargin(view);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) userVideoList.getLayoutManager();
                    View view = linearLayoutManager.getChildAt(0);
                    scrollVideoViewForMargin(view);
                }
            }, 50);
        }
    }

    private void scrollVideoViewForMargin(View view) {
        if (null == view) {
            return;
        }
        int left = view.getLeft();
        int margin = 5;
        if (left > margin || left <= 0) {
            userVideoList.scrollBy(left - margin, 0);
        }
        /*if (BuildConfig.DEBUG) {
            Log.d(TAG, "left:" + left + " view left:" + view.getLeft());
        }*/
    }

    private void updateVideoListLayout() {
        int size = UserHelper.getAllUsers().size();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) userVideoList.getLayoutParams();
        int preWidth = params.width;

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (size - 1 >= 3) {
            int maxWidth = (int) (325 * displayMetrics.scaledDensity);
            width = maxWidth;
        }
        Log.e(TAG,"updateVideoListLayout width : "+width+" | preWidth : "+preWidth+" | params height : "+params.height);
        if (width != preWidth) {
            params.width = width;
            userVideoList.setLayoutParams(params);
        }
    }

    protected void onStartShareView() {

    }

    public void onClickStopShare(View view) {
        ZoomVideoSDK.getInstance().getShareHelper().stopShare();
    }

    protected void onUserActive(ZoomVideoSDKUser user) {
//        CmdLowerThirdRequest cmdLowerThirdRequest = null;
//        for (CmdLowerThirdRequest request : lowerThirdRequests) {
//            if (request.user.equals(user)) {
//                cmdLowerThirdRequest = request;
//                break;
//            }
//        }
//        showLowerThird(cmdLowerThirdRequest);
    }

    private void checkMoreAction() {
        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (null == zoomSDKUserInfo)
            return;
        iconMore.setVisibility(View.VISIBLE);
    }

    private boolean canSwitchAudioSource() {
        return ZoomVideoSDK.getInstance().getAudioHelper().canSwitchSpeaker();
    }

    private void showInputPwdDialog(final ZoomVideoSDKPasswordHandler handler) {
        final Dialog builder = new Dialog(this, R.style.MyDialog);
        builder.setContentView(R.layout.dialog_session_input_pwd);
        builder.setCancelable(false);
        builder.setCanceledOnTouchOutside(false);
        final EditText editText = builder.findViewById(R.id.edit_pwd);
        builder.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = editText.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    handler.inputSessionPassword(pwd);
                    builder.dismiss();
                }
            }
        });

        builder.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.leaveSessionIgnorePassword();
                builder.dismiss();
            }
        });

        builder.show();
    }

    public void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                Log.e("CEK","trimCache : "+dir.getPath());
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                Log.e("CEK","deleteDir ke-+"+i+" : "+success);
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    public void onSessionJoin() {
        //llUsersVideo.setVisibility(View.VISIBLE);
        cardSurf.setVisibility(View.VISIBLE);
        cardSurfOff.setVisibility(View.INVISIBLE);
        //btnFile.setBackgroundTintList(BaseMeetingActivity.this.getResources().getColorStateList(R.color.btnFalse));
        //btnChat.setBackgroundTintList(BaseMeetingActivity.this.getResources().getColorStateList(R.color.btnFalse));
        btnFile.setClickable(false);
        btnChat.setClickable(false);
        gifLoading.setVisibility(View.GONE);
        imgBatikVic.setVisibility(View.GONE);
        /*DipsVideoConfren.LogoCompany.setVisibility(View.VISIBLE);
        DipsVideoConfren.Zoom.setVisibility(View.VISIBLE);*/
        updateSessionInfo();
        getFragmentPage(new frag_conferee_agree());
        sessions.saveFlagConfAgree(true);
        actionBar.setVisibility(View.VISIBLE);
        if (ZoomVideoSDK.getInstance().getShareHelper().isSharingOut()) {
            ZoomVideoSDK.getInstance().getShareHelper().stopShare();
        }

        adapter.onUserJoin(UserHelper.getAllUsers());
        refreshUserListAdapter();

        timer.setVisibility(View.VISIBLE);
        runTimer(text_timer);

        String idDips = sessions.getKEY_IdDips();

        JSONObject idDipsObj = new JSONObject();
        try {
            idDipsObj.put("idDips",idDips);
            idDipsObj.put("bahasa",lang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RabbitMirroring.MirroringSendKey(idDipsObj);
    }

    @Override
    public void onSessionLeave() {
        Log.e("CEK","onSessionLeave");
        if (flagUserLeave == false) {
            if (flagShowLeave == false) {
                dialogAgentLeave();
            }
        } else {
            OutboundServiceNew.stopServiceSocket();
            Intent intentOutbound = new Intent(mContext, OutboundServiceNew.class);
            mContext.stopService(intentOutbound);
            sessions.clearPartData();
            finish();
        }
    }

    @Override
    public void onError(int errorcode) {
        Toast.makeText(this, ErrorMsgUtil.getMsgByErrorCode(errorcode) + ". Error code: "+errorcode, Toast.LENGTH_LONG).show();
        if (errorcode == ZoomVideoSDKErrors.Errors_Session_Disconnect) {
            unSubscribe();
            adapter.clear(true);
            updateSessionInfo();
            currentShareUser = null;
            mActiveUser=null;
//            chatMsgAdapter.clear();
//            chatListView.setVisibility(View.GONE);
//            btnViewShare.setVisibility(View.GONE);
        } else if (errorcode == ZoomVideoSDKErrors.Errors_Session_Reconncting) {
            //start preview
            subscribeVideoByUser(session.getMySelf());
        } else {
            ZoomVideoSDK.getInstance().leaveSession(false);
            finish();
        }
    }

    @Override
    public void onUserJoin(ZoomVideoSDKUserHelper zoomVideoSDKUserHelper, List<ZoomVideoSDKUser> userList) {
        Log.e("CEK","onUserJoin userList.size : "+userList.size()+" | isActivityPaused : "+isActivityPaused);
        updateVideoListLayout();
        if (!isActivityPaused) {
            adapter.onUserJoin(userList);
        }
        refreshUserListAdapter();
        updateSessionInfo();
    }

    @Override
    public void onUserLeave(ZoomVideoSDKUserHelper zoomVideoSDKUserHelper, List<ZoomVideoSDKUser> userList) {
        Log.e("CEK","onUserLeave userList.size : "+userList.size());
        updateVideoListLayout();
        adapter.onUserLeave(userList);
        if (adapter.getItemCount() == 0) {
            videoListContain.setVisibility(View.INVISIBLE);
        }
        updateSessionInfo();

        flagUserLeave = true;

        if (userList.size() < 2 && flagClickEnd == false) {
            if (flagShowLeave == false) {
                dialogAgentLeave();
            }
        }
    }

    @Override
    public void onUserVideoStatusChanged(ZoomVideoSDKVideoHelper zoomVideoSDKVideoHelper, List<ZoomVideoSDKUser> userList) {
        Log.d(TAG, "onUserVideoStatusChanged userList : "+userList.size());
        if (null == iconVideo) {
            return;
        }

        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (null != zoomSDKUserInfo) {
            if (zoomSDKUserInfo.getVideoStatus().isOn()) {
                isOn = 1;
                iconVideo.setImageResource(R.drawable.icon_video_off);
            } else {
                isOn = 2;
                iconVideo.setImageResource(R.drawable.icon_video_on);
            }
            //iconVideo.setImageResource(zoomSDKUserInfo.getVideoStatus().isOn() ? R.drawable.icon_video_off : R.drawable.icon_video_on);
            if (userList.contains(zoomSDKUserInfo)) {
                //checkMoreAction();
            }

            sessions.saveCamera(isOn);

            boolean flagDoc = sessions.getFlagUpDoc();
            Log.d("CEK","flagDoc : "+flagDoc);
            if (flagDoc) {
                startVideoHandler();
            } else {
                int valMedia = sessions.getMedia();
                if (valMedia == 0 && isOn == 2) {
                    onOffVideo();
                }
            }
        }
        adapter.onUserVideoStatusChanged(userList);
    }

    @Override
    public void onUserAudioStatusChanged(ZoomVideoSDKAudioHelper zoomVideoSDKAudioHelper, List<ZoomVideoSDKUser> userList) {
        ZoomVideoSDKUser zoomSDKUserInfo = session.getMySelf();
        if (zoomSDKUserInfo != null && userList.contains(zoomSDKUserInfo)) {
            if (zoomSDKUserInfo.getAudioStatus().getAudioType() == ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_None) {
                iconAudio.setImageResource(R.drawable.icon_join_audio);
            } else {
                if (zoomSDKUserInfo.getAudioStatus().isMuted()) {
                    iconAudio.setImageResource(R.drawable.v_unmute);
                } else {
                    iconAudio.setImageResource(R.drawable.v_mic);
                }
            }
            //checkMoreAction();
        }
    }

    @Override
    public void onUserShareStatusChanged(ZoomVideoSDKShareHelper zoomVideoSDKShareHelper, ZoomVideoSDKUser zoomVideoSDKUser, ZoomVideoSDKShareStatus zoomVideoSDKShareStatus) {
        Log.d(TAG, "onUserShareStatusChanged ");
    }

    @Override
    public void onLiveStreamStatusChanged(ZoomVideoSDKLiveStreamHelper zoomVideoSDKLiveStreamHelper, ZoomVideoSDKLiveStreamStatus zoomVideoSDKLiveStreamStatus) {
        Log.d(TAG, "onLiveStreamStatusChanged ");
    }

    @Override
    public void onChatNewMessageNotify(ZoomVideoSDKChatHelper zoomVideoSDKChatHelper, ZoomVideoSDKChatMessage messageItem) {
        Log.d(TAG, "onChatNewMessageNotify ");
        chatMsgAdapter.onReceive(messageItem);
        if(bottomSheetDialog.isShowing()){
            iconBubble.setVisibility(View.GONE);
        }
        else{
            iconBubble.setVisibility(View.VISIBLE);
        }
        Log.d("CEK PESAN",messageItem.getContent());
    }

    @Override
    public void onChatDeleteMessageNotify(ZoomVideoSDKChatHelper chatHelper, String msgID, ZoomVideoSDKChatMessageDeleteType deleteBy) {

    }

    @Override
    public void onUserHostChanged(ZoomVideoSDKUserHelper zoomVideoSDKUserHelper, ZoomVideoSDKUser zoomVideoSDKUser) {
        Log.d(TAG, "onUserHostChanged ");
    }

    @Override
    public void onUserManagerChanged(ZoomVideoSDKUser zoomVideoSDKUser) {
        Log.d(TAG, "onUserManagerChanged ");
    }

    @Override
    public void onUserNameChanged(ZoomVideoSDKUser zoomVideoSDKUser) {
        Log.d(TAG, "onUserNameChanged ");
    }

    @Override
    public void onUserActiveAudioChanged(ZoomVideoSDKAudioHelper zoomVideoSDKAudioHelper, List<ZoomVideoSDKUser> list) {
        Log.d(TAG, "onUserActiveAudioChanged ");
        adapter.onUserActiveAudioChanged(list, userVideoList);
    }

    @Override
    public void onSessionNeedPassword(ZoomVideoSDKPasswordHandler handler) {
        Log.d(TAG, "onSessionNeedPassword ");
        showInputPwdDialog(handler);
    }

    @Override
    public void onSessionPasswordWrong(ZoomVideoSDKPasswordHandler handler) {
        Log.d(TAG, "onSessionPasswordWrong ");
        Toast.makeText(this, "Password wrong", Toast.LENGTH_LONG).show();
        showInputPwdDialog(handler);
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
        Log.d(TAG, "onCommandReceived ");
    }

    @Override
    public void onCommandChannelConnectResult(boolean b) {
        Log.d(TAG, "onCommandChannelConnectResult "+b);
    }

    @Override
    public void onCloudRecordingStatus(ZoomVideoSDKRecordingStatus status, ZoomVideoSDKRecordingConsentHandler handler) {

    }

    @Override
    public void onHostAskUnmute() {

    }

    @Override
    public void onInviteByPhoneStatus(ZoomVideoSDKPhoneStatus zoomVideoSDKPhoneStatus, ZoomVideoSDKPhoneFailedReason zoomVideoSDKPhoneFailedReason) {
        Log.d(TAG, "onInviteByPhoneStatus ");
    }

    @Override
    public void onMultiCameraStreamStatusChanged(ZoomVideoSDKMultiCameraStreamStatus status, ZoomVideoSDKUser user, ZoomVideoSDKRawDataPipe videoPipe) {

    }

    @Override
    public void onMultiCameraStreamStatusChanged(ZoomVideoSDKMultiCameraStreamStatus status, ZoomVideoSDKUser user, ZoomVideoSDKVideoCanvas canvas) {

    }

    @Override
    public void onLiveTranscriptionStatus(ZoomVideoSDKLiveTranscriptionHelper.ZoomVideoSDKLiveTranscriptionStatus status) {

    }

    @Override
    public void onLiveTranscriptionMsgReceived(String ltMsg, ZoomVideoSDKUser pUser, ZoomVideoSDKLiveTranscriptionHelper.ZoomVideoSDKLiveTranscriptionOperationType type) {

    }

    @Override
    public void onLiveTranscriptionMsgError(ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage spokenLanguage, ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage transcriptLanguage) {

    }

    @Override
    public void onProxySettingNotification(ZoomVideoSDKProxySettingHandler handler) {

    }

    @Override
    public void onSSLCertVerifiedFailNotification(ZoomVideoSDKSSLCertificateInfo info) {

    }

    @Override
    public void onCameraControlRequestResult(ZoomVideoSDKUser user, boolean isApproved) {

    }

    @Override
    public void onUserVideoNetworkStatusChanged(ZoomVideoSDKNetworkStatus status, ZoomVideoSDKUser user) {

    }

    @Override
    public void onUserRecordingConsent(ZoomVideoSDKUser user) {

    }

    @Override
    public void onSingleTap(ZoomVideoSDKUser user) {
        Log.d(TAG, "onSingleTap ");
        subscribeVideoByUser(user);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(TAG, "onPointerCaptureChanged ");
        super.onPointerCaptureChanged(hasCapture);
    }

}
