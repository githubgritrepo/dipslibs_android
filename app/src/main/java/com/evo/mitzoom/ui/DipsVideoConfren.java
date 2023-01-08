package com.evo.mitzoom.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.Helper.NotificationService;
import com.evo.mitzoom.Helper.OutboundServiceNew;
import com.evo.mitzoom.R;
import com.evo.mitzoom.cmd.CmdFeedbackPushRequest;
import com.evo.mitzoom.cmd.CmdHandler;
import com.evo.mitzoom.cmd.CmdHelper;
import com.evo.mitzoom.cmd.CmdRequest;
import com.evo.mitzoom.rawdata.RawDataRenderer;
import com.evo.mitzoom.util.AudioRawDataUtil;

import java.util.List;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKErrors;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoAspect;
import us.zoom.sdk.ZoomVideoSDKVideoResolution;
import us.zoom.sdk.ZoomVideoSDKVideoView;

public class DipsVideoConfren extends BaseMeetingActivity {

    private static final String TAG = "CEK_MeetingActivity";

    ZoomVideoSDKVideoView zoomCanvas;

    RawDataRenderer rawDataRenderer;

    public static ImageView LogoCompany, Zoom;
    public static LinearLayout timer;
    public static TextView text_timer;

    private FrameLayout videoContain;
    private AudioRawDataUtil audioRawDataUtil;
    private CmdHandler mFeedbackPushHandler = new CmdHandler() {
        @Override
        public void onCmdReceived(CmdRequest request) {
            if (request instanceof CmdFeedbackPushRequest) {
//                FeedbackSubmitDialog.show(DipsVideoConfren.this);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioRawDataUtil = new AudioRawDataUtil(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CmdHelper.getInstance().addListener(mFeedbackPushHandler);
//        FeedbackDataManager.getInstance().startListenerFeedbackData();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_dips_video_confren;
    }

    @Override
    public void onSessionJoin() {
        super.onSessionJoin();
//        audioRawDataUtil.subscribeAudio();
        startMeetingService();
    }

    private void startMeetingService() {
        Intent intent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        Intent intentOutbound = new Intent(this, OutboundServiceNew.class);
        stopService(intentOutbound);
    }

    private void stopMeetingService() {
        Intent intent = new Intent(this, NotificationService.class);
        stopService(intent);

        /*Intent intentOutbound = new Intent(this, OutboundServiceNew.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentOutbound);
        } else {
            startService(intentOutbound);
        }*/
    }

    @Override
    public void onSessionLeave() {
        super.onSessionLeave();
        audioRawDataUtil.unSubscribe();
        if (null != shareToolbar) {
            shareToolbar.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        CmdHelper.getInstance().removeListener(emojiHandler);
//        CmdHelper.getInstance().removeListener(lowerThirdHandler);
        stopMeetingService();
//        handler.removeCallbacks(runnable);
        CmdHelper.getInstance().removeListener(mFeedbackPushHandler);
//        FeedbackDataManager.getInstance().stopListenerFeedbackData();
//        FeedbackDataManager.getInstance().clear();
    }

    @Override
    protected void initView() {
        super.initView();
        videoContain = findViewById(R.id.big_video_contain);
        videoContain.setOnClickListener(onEmptyContentClick);
        LogoCompany = findViewById(R.id.logo_company);
        text_timer = findViewById(R.id.text_timer);
        timer = findViewById(R.id.timer);
        Zoom = findViewById(R.id.poweredbyzoom);

    }

    View.OnClickListener onEmptyContentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!ZoomVideoSDK.getInstance().isInSession()) {
                return;
            }
            boolean isShow = actionBar.getVisibility() == View.VISIBLE;
            //toggleView(!isShow);
//            if (BuildConfig.DEBUG) {
//                changeResolution();
//            }
        }
    };
//
//    @Override
//    public void onItemClick() {
//        if (!ZoomVideoSDK.getInstance().isInSession()) {
//            return;
//        }
//        boolean isShow = actionBar.getVisibility() == View.VISIBLE;
//        toggleView(!isShow);
//    }

    protected void toggleView(boolean show) {
        if (!show) {
            if (keyBoardLayout.isKeyBoardShow()) {
                keyBoardLayout.dismissChat(true);
                return;
            }
        }
        actionBar.setVisibility(show ? View.VISIBLE : View.GONE);
//        chatListView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initMeeting() {
        ZoomVideoSDK.getInstance().addListener(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if (renderType == RENDER_TYPE_ZOOMRENDERER) {
            zoomCanvas = new ZoomVideoSDKVideoView(this, !renderWithSurfaceView);
            videoContain.addView(zoomCanvas, 0, params);
        } else {
            rawDataRenderer = new RawDataRenderer(this);
            videoContain.addView(rawDataRenderer, 0, params);
        }

        ZoomVideoSDKUser mySelf = ZoomVideoSDK.getInstance().getSession().getMySelf();
        subscribeVideoByUser(mySelf);
//        refreshFps();
//        CmdHelper.getInstance().addListener(lowerThirdHandler);
//        CmdHelper.getInstance().addListener(emojiHandler);

    }

    protected void unSubscribe() {
        if (null != currentShareUser) {
            if (renderType == RENDER_TYPE_ZOOMRENDERER) {
//                currentShareUser.getVideoCanvas().unSubscribe(zoomCanvas);
//                currentShareUser.getShareCanvas().unSubscribe(zoomCanvas);
            } else {
                currentShareUser.getVideoPipe().unSubscribe(rawDataRenderer);
            }
        }

        if (null != mActiveUser) {
            if (renderType == RENDER_TYPE_ZOOMRENDERER) {
                mActiveUser.getVideoCanvas().unSubscribe(zoomCanvas);
//                mActiveUser.getShareCanvas().unSubscribe(zoomCanvas);
            } else {
                mActiveUser.getVideoPipe().unSubscribe(rawDataRenderer);
            }
        }
    }

    protected void subscribeVideoByUser(ZoomVideoSDKUser user) {
        if (renderType == RENDER_TYPE_ZOOMRENDERER) {
            ZoomVideoSDKVideoAspect aspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_LetterBox;
            if (ZoomVideoSDK.getInstance().isInSession()) {
                aspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original;
            }
            if (null != currentShareUser) {
//                currentShareUser.getShareCanvas().unSubscribe(zoomCanvas);
            }
            user.getVideoCanvas().unSubscribe(zoomCanvas);
//            int ret=user.getVideoCanvas().subscribe(zoomCanvas, aspect);
//            if(ret!= ZoomVideoSDKErrors.Errors_Success)
//
//            {
//                Toast.makeText(this,"subscribe error:"+ret,Toast.LENGTH_LONG).show();
//            }
        } else {
            if (ZoomVideoSDK.getInstance().isInSession()) {
                rawDataRenderer.setVideoAspectModel(RawDataRenderer.VideoAspect_Original);
            } else {
                rawDataRenderer.setVideoAspectModel(RawDataRenderer.VideoAspect_Full_Filled);
            }
            if (null != currentShareUser) {
                currentShareUser.getSharePipe().unSubscribe(rawDataRenderer);
            }
            user.getVideoPipe().unSubscribe(rawDataRenderer);
            int ret= user.getVideoPipe().subscribe(ZoomVideoSDKVideoResolution.VideoResolution_720P, rawDataRenderer);
            if(ret!= ZoomVideoSDKErrors.Errors_Success)
            {
                Toast.makeText(this,"subscribe error:"+ret,Toast.LENGTH_LONG).show();
            }
        }
        mActiveUser = user;
        onUserActive(mActiveUser);

        if (null != user.getVideoStatus()) {
//            updateVideoAvatar(user.getVideoStatus().isOn());
        }

        if (null != currentShareUser) {
//            btnViewShare.setVisibility(View.VISIBLE);
        } else {
//            btnViewShare.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserLeave(ZoomVideoSDKUserHelper userHelper, List<ZoomVideoSDKUser> userList) {
        super.onUserLeave(userHelper, userList);
        if (null == mActiveUser || userList.contains(mActiveUser)) {
            subscribeVideoByUser(session.getMySelf());
            selectAndScrollToUser(session.getMySelf());
        }
    }


}