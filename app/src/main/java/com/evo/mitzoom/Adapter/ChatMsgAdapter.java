package com.evo.mitzoom.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

import java.util.List;

import us.zoom.sdk.ZoomVideoSDK;
import us.zoom.sdk.ZoomVideoSDKChatMessage;
import us.zoom.sdk.ZoomVideoSDKSession;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.MsgHolder> {

    private Context ctx;
    private List<CharSequence> list;
    private List<Boolean> isSelf;
    private List<Boolean> isHost;
    protected ZoomVideoSDKSession session;
    public ChatMsgAdapter(Context ctx, List<CharSequence> list, List<Boolean> isHost, List<Boolean> isSelf) {
        this.ctx = ctx;
        this.list = list;
        this.isSelf = isSelf;
        this.isHost = isHost;
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
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),SenderName.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置前面的字体颜色
        list.add(builder);
        notifyItemInserted(list.size());

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

        if (isSelf.get(position) == true && isHost.get(position) == false) {
            holder.LLChat.setGravity(Gravity.RIGHT);
            holder.chatBubble.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.teal_200));
            holder.chatBubble.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_drawable));
        } else {
            holder.LLChat.setGravity(Gravity.LEFT);
            holder.chatBubble.setBackgroundTintList(ctx.getResources().getColorStateList(R.color.bg_cif));
            holder.chatBubble.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_drawable2));
        }
        holder.chatMsg.setText(item);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class MsgHolder extends RecyclerView.ViewHolder {
        private LinearLayout LLChat;
        private LinearLayout chatBubble;
        TextView chatMsg;
        MsgHolder(View view) {
            super(view);
            LLChat = view.findViewById(R.id.LLChat);
            chatBubble = view.findViewById(R.id.chat_bubble);
            chatMsg = view.findViewById(R.id.chat_msg_text);
        }
    }


}
