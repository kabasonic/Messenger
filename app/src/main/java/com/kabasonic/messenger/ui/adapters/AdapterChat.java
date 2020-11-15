package com.kabasonic.messenger.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.ui.userchat.UserChat;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Chat> chatList;

    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        if(i == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent,false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent,false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dataTime = (String) df.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date());

        holder.message.setText(message);
        holder.time.setText(dataTime);

        

        if(position==chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeen.setText("Seen");
            }else{
                holder.isSeen.setText("Delivered");
            }
        } else{
            holder.isSeen.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView message, time, isSeen;
        LinearLayout messageLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageTx);
            time = itemView.findViewById(R.id.timeTv);
            isSeen = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageRow);

        }
    }
}
