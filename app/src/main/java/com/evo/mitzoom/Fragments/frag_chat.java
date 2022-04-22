package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Adapter.ChatMsgAdapter;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.R;

import java.util.ArrayList;
import java.util.List;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKAudioHelper;
import us.zoom.sdk.ZoomVideoSDKAudioRawData;
import us.zoom.sdk.ZoomVideoSDKChatHelper;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKDelegate;
import us.zoom.sdk.ZoomVideoSDKLiveStreamHelper;
import us.zoom.sdk.ZoomVideoSDKLiveStreamStatus;
import us.zoom.sdk.ZoomVideoSDKPasswordHandler;
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason;
import us.zoom.sdk.ZoomVideoSDKPhoneStatus;
import us.zoom.sdk.ZoomVideoSDKRecordingStatus;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKShareHelper;
import us.zoom.sdk.ZoomVideoSDKShareStatus;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoHelper;

public class frag_chat extends Fragment  implements ZoomVideoSDKDelegate {
    private Context context;
    private ImageView btnClose, btnSend;
    private EditText InputChat;
    private String Chat;
    private ChatMsgAdapter chatMsgAdapter;
    protected RecyclerView chatListView;
    private List<CharSequence> list = new ArrayList<>();
    protected ZoomVideoSDKSession session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        BaseMeetingActivity.btnChat.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        BaseMeetingActivity.btnChat.setClickable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat, container, false);
        btnClose = view.findViewById(R.id.btn_close_chat);
        InputChat = view.findViewById(R.id.et_input_chat);
        btnSend = view.findViewById(R.id.btn_send_chat);
        chatListView = view.findViewById(R.id.chat_list);
        session = ZoomVideoSDK.getInstance().getSession();
        ZoomVideoSDK.getInstance().addListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseMeetingActivity.btnChat.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                BaseMeetingActivity.btnChat.setClickable(true);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat = InputChat.getText().toString();
                ZoomVideoSDK.getInstance().getChatHelper().sendChatToAll(Chat);
                InputChat.setText("");
            }
        });
        chatListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        chatMsgAdapter = new ChatMsgAdapter(context, list);
        chatListView.setAdapter(chatMsgAdapter);
        updateChatLayoutParams();

    }

    private void updateChatLayoutParams() {
        if (chatMsgAdapter.getItemCount() > 0) {
            chatListView.scrollToPosition(chatMsgAdapter.getItemCount() - 1);
        }
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
    public void onChatNewMessageNotify(ZoomVideoSDKChatHelper zoomVideoSDKChatHelper, ZoomVideoSDKChatMessage messageItem) {
        chatMsgAdapter.onReceive(messageItem);
        updateChatLayoutParams();

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
