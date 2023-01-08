package com.evo.mitzoom.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.Adapter.ChatMsgAdapter;
import com.evo.mitzoom.BaseMeetingActivity;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import us.zoom.sdk.ZoomVideoSDKMultiCameraStreamStatus;
import us.zoom.sdk.ZoomVideoSDKPasswordHandler;
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason;
import us.zoom.sdk.ZoomVideoSDKPhoneStatus;
import us.zoom.sdk.ZoomVideoSDKRawDataPipe;
import us.zoom.sdk.ZoomVideoSDKRecordingStatus;
import us.zoom.sdk.ZoomVideoSDKSession;
import us.zoom.sdk.ZoomVideoSDKShareHelper;
import us.zoom.sdk.ZoomVideoSDKShareStatus;
import us.zoom.sdk.ZoomVideoSDKUser;
import us.zoom.sdk.ZoomVideoSDKUserHelper;
import us.zoom.sdk.ZoomVideoSDKVideoCanvas;
import us.zoom.sdk.ZoomVideoSDKVideoHelper;

public class frag_chat extends Fragment  implements ZoomVideoSDKDelegate {
    private Context context;
    private ImageView btnClose, btnSend;
    private EditText InputChat;
    private String Chat;
    private ChatMsgAdapter chatMsgAdapter;
    protected RecyclerView chatListView;
    private SessionManager sessionManager;
    private List<CharSequence> list = new ArrayList<>();
    private List<Boolean> isSelf = new ArrayList<>();
    private List<Boolean> isHost = new ArrayList<>();
    protected ZoomVideoSDKSession session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        //BaseMeetingActivity.btnChat.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
        BaseMeetingActivity.btnChat.setClickable(false);
        sessionManager = new SessionManager(context);
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
                //BaseMeetingActivity.btnChat.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                BaseMeetingActivity.btnChat.setClickable(true);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                SavedInstanceChat();
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
        if (sessionManager.getKEY_CHAT() != null){
            String dataChat = sessionManager.getKEY_CHAT();
            Log.d("CEK START PESAN ",dataChat);
            try {
                JSONArray jsonArray2 = new JSONArray(dataChat);
                int panjang = jsonArray2.length();
                for (int a=0;a<panjang;a++){
                    String dataChat2 = jsonArray2.get(a).toString();
                    JSONObject jsonObject = new JSONObject(dataChat2);
                    boolean isSelf2 = jsonObject.getBoolean("isSelf");
                    boolean isHost2 = jsonObject.getBoolean("isHost");
                    String message = jsonObject.getString("message");

                    String [] message2 = message.split("\n");
                    String SenderName = message2[0]+"\n";
                    String content = message2[1];
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(SenderName).append(content);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),0,SenderName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),SenderName.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置前面的字体颜色

                    isSelf.add(isSelf2);
                    isHost.add(isHost2);
                    list.add(builder);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d("CEK START PESAN ","MASUK ELSE");
        }
        chatMsgAdapter = new ChatMsgAdapter(context, list,isHost, isSelf);
        chatListView.setAdapter(chatMsgAdapter);
        updateChatLayoutParams();
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
        sessionManager.saveChat(dataArr);
        Log.d("CEK PESAN ARRAY",dataArr);
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
        Log.d("CEK PESAN",messageItem.getContent());
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

    @Override
    public void onMultiCameraStreamStatusChanged(ZoomVideoSDKMultiCameraStreamStatus status, ZoomVideoSDKUser user, ZoomVideoSDKRawDataPipe videoPipe) {

    }

    @Override
    public void onMultiCameraStreamStatusChanged(ZoomVideoSDKMultiCameraStreamStatus status, ZoomVideoSDKUser user, ZoomVideoSDKVideoCanvas canvas) {

    }
}
