package com.evo.mitzoom.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKAudioHelper;
import us.zoom.sdk.ZoomVideoSDKAudioRawData;
import us.zoom.sdk.ZoomVideoSDKChatHelper;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKChatMessageDeleteType;
import us.zoom.sdk.ZoomVideoSDKDelegate;
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
import us.zoom.sdk.ZoomVideoSDKShareHelper;
import us.zoom.sdk.ZoomVideoSDKShareStatus;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoCanvas;
import us.zoom.sdk.ZoomVideoSDKVideoHelper;

public class AudioRawDataUtil {

    static final String TAG = "AudioRawDataUtil";

    private Map<String, FileChannel> map = new HashMap<>();

    private Context mContext;


    public AudioRawDataUtil(Context context) {
        mContext = context.getApplicationContext();
    }

    private FileChannel createFileChannel(String name) {
        String path=mContext.getExternalCacheDir().getAbsolutePath()+ "/audiorawdata/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = path + name + ".pcm";
        File file = new File(fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            FileChannel fileChannel = fileOutputStream.getChannel();

            return fileChannel;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private ZoomVideoSDKDelegate dataDelegate = new ZoomVideoSDKDelegate() {


        @Override
        public void onMixedAudioRawDataReceived(ZoomVideoSDKAudioRawData rawData) {
            Log.d(TAG,"onMixedAudioRawDataReceived:"+rawData);
            saveAudioRawData(rawData, ZoomVideoSDK.getInstance().getSession().getMySelf().getUserName());

        }

        public void onOneWayAudioRawDataReceived(ZoomVideoSDKAudioRawData rawData, ZoomVideoSDKUser user) {
            Log.d(TAG,"onOneWayAudioRawDataReceived:"+rawData);
            saveAudioRawData(rawData, user.getUserName());
        }


        @Override
        public void onSessionJoin() {

        }

        @Override
        public void onSessionLeave() {

        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onUserJoin(ZoomVideoSDKUserHelper userHelper, List<ZoomVideoSDKUser> userList) {

        }

        @Override
        public void onUserLeave(ZoomVideoSDKUserHelper userHelper, List<ZoomVideoSDKUser> userList) {

        }

        @Override
        public void onUserVideoStatusChanged(ZoomVideoSDKVideoHelper videoHelper, List<ZoomVideoSDKUser> userList) {

        }

        @Override
        public void onUserAudioStatusChanged(ZoomVideoSDKAudioHelper audioHelper, List<ZoomVideoSDKUser> userList) {

        }

        @Override
        public void onUserShareStatusChanged(ZoomVideoSDKShareHelper shareHelper, ZoomVideoSDKUser userInfo, ZoomVideoSDKShareStatus status) {

        }

        @Override
        public void onLiveStreamStatusChanged(ZoomVideoSDKLiveStreamHelper liveStreamHelper, ZoomVideoSDKLiveStreamStatus status) {

        }

        @Override
        public void onChatNewMessageNotify(ZoomVideoSDKChatHelper chatHelper, ZoomVideoSDKChatMessage messageItem) {

        }

        @Override
        public void onChatDeleteMessageNotify(ZoomVideoSDKChatHelper chatHelper, String msgID, ZoomVideoSDKChatMessageDeleteType deleteBy) {

        }

        @Override
        public void onUserHostChanged(ZoomVideoSDKUserHelper userHelper, ZoomVideoSDKUser userInfo) {

        }

        @Override
        public void onUserActiveAudioChanged(ZoomVideoSDKAudioHelper audioHelper, List<ZoomVideoSDKUser> list) {

        }

        @Override
        public void onSessionNeedPassword(ZoomVideoSDKPasswordHandler handler) {

        }

        @Override
        public void onSessionPasswordWrong(ZoomVideoSDKPasswordHandler handler) {

        }

        @Override
        public void onUserManagerChanged(ZoomVideoSDKUser user) {

        }

        @Override
        public void onUserNameChanged(ZoomVideoSDKUser user) {

        }

        @Override
        public void onShareAudioRawDataReceived(ZoomVideoSDKAudioRawData rawData) {
            Log.d(TAG,"onShareAudioRawDataReceived:"+rawData);
            saveAudioRawData(rawData,"share");
        }

        @Override
        public void onCommandReceived(ZoomVideoSDKUser sender, String strCmd) {

        }

        @Override
        public void onCommandChannelConnectResult(boolean isSuccess) {

        }

        @Override
        public void onCloudRecordingStatus(ZoomVideoSDKRecordingStatus status, ZoomVideoSDKRecordingConsentHandler handler) {

        }

        @Override
        public void onHostAskUnmute() {

        }

        @Override
        public void onInviteByPhoneStatus(ZoomVideoSDKPhoneStatus status, ZoomVideoSDKPhoneFailedReason reason) {
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
    };

    public void saveAudioRawData(ZoomVideoSDKAudioRawData rawData, String fileName) {
        try {

            FileChannel fileChannel = map.get(fileName);
            if (null == fileChannel) {
                fileChannel = createFileChannel(fileName);
                map.put(fileName, fileChannel);
            }
            if (null != fileChannel) {
                fileChannel.write(rawData.getBuffer(), rawData.getBufferLen());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void subscribeAudio() {

        ZoomVideoSDK.getInstance().getAudioHelper().subscribe();
        ZoomVideoSDK.getInstance().addListener(dataDelegate);
    }

    public void unSubscribe() {
        ZoomVideoSDK.getInstance().removeListener(dataDelegate);

        for (FileChannel fileChannel : map.values()) {
            if (null != fileChannel) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ZoomVideoSDK.getInstance().getAudioHelper().unSubscribe();
    }

}
