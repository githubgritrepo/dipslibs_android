package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKSession;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.MsgHolder> {

    private final Context ctx;
    private final List<CharSequence> list;
    private final List<Boolean> isSelf;
    private final List<Boolean> isHost;
    protected ZoomVideoSDKSession session;
    private final SessionManager sessionManager;
    public ChatMsgAdapter(Context ctx, List<CharSequence> list, List<Boolean> isHost, List<Boolean> isSelf) {
        this.ctx = ctx;
        this.list = list;
        this.isSelf = isSelf;
        this.isHost = isHost;
        this.sessionManager = new SessionManager(ctx);
    }
    public void onReceive(ZoomVideoSDKChatMessage item) {
        boolean isSelfval = item.isSelfSend();
        boolean isHostval = item.getSenderUser().isHost();
        String SenderName = item.getSenderUser().getUserName()+"\n";
        isSelf.add(isSelfval);
        isHost.add(isHostval);
        String content = item.getContent();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(SenderName).append(content);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),0,SenderName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),SenderName.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置前面的字体颜色
        list.add(builder);
        notifyItemInserted(list.size());
        SavedInstanceChat();

    }
    @NonNull
    @Override
    public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg, parent, false);
        return new MsgHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MsgHolder holder, int position) {
        CharSequence item = list.get(position);
        session = ZoomVideoSDK.getInstance().getSession();

        if (isSelf.get(position) && !isHost.get(position)) {
            holder.LLChat.setGravity(Gravity.RIGHT);
            holder.chatBubble.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.bg_mychat));
            holder.chatBubble.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_drawable));
        } else {
            holder.LLChat.setGravity(Gravity.LEFT);
            holder.chatBubble.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.bg_agenchat));
            holder.chatBubble.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_drawable2));
        }
        holder.chatMsg.setText(item);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class MsgHolder extends RecyclerView.ViewHolder {
        private final LinearLayout LLChat;
        private final LinearLayout chatBubble;
        TextView chatMsg;
        MsgHolder(View view) {
            super(view);
            LLChat = view.findViewById(R.id.LLChat);
            chatBubble = view.findViewById(R.id.chat_bubble);
            chatMsg = view.findViewById(R.id.chat_msg_text);
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
    }


}
