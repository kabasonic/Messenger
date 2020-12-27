package com.kabasonic.messenger.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {

    public static final String TAG = "AdapterChat";

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Chat> chatList;

    FirebaseUser firebaseUser;
    ArrayList<String> mSelectedItems;
    public AdapterChat(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        if (i == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

//        android.text.format.DateFormat df = new android.text.format.DateFormat();
//        String dataTime = (String) df.format("yyyy-MM-dd hh:mm:ssZZZZ a", new java.util.Date());

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd-M-yyyy ");
        String output = dateFormat.format(currentTime);
        holder.time.setText(output);
        holder.message.setText(message);

        Log.wtf(TAG,"Messege last: " +  String.valueOf(chatList.get(chatList.size()-1).getMessage()));
        Log.wtf(TAG,"Messege last STATUS: " +  String.valueOf(chatList.get(chatList.size()-1).isSeen()));

//        if (position == chatList.size() - 1) {
//            if (chatList.get(position).isSeen()) {
//                //holder.isSeen.setText("Seen");
//                holder.isSeen.setImageResource(R.drawable.ic_round_done_all_24);
//            } else {
//                //holder.isSeen.setText("Delivered");
//                holder.isSeen.setImageResource(R.drawable.ic_round_done_24);
//            }
//        } else {
//            //holder.isSeen.setVisibility(View.GONE);
//        }

        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked message row");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(R.array.dialog_row_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                editMessage(position);
                                break;
                            case 1:
                                 mSelectedItems = new ArrayList();
                                builder.setTitle("Delete message");
                                builder.setMessage("Are you sure to delete this message?");

                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteMessage(position);
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });


    }

    private void editMessage(int position) {

    }

    private void deleteMessage(int position) {
        FirebaseUser myUid = FirebaseAuth.getInstance().getCurrentUser();

        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("chat");
        Query query = mDatabase.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.child("sender").getValue().equals(myUid.getUid())){
                        dataSnapshot.getRef().removeValue();

//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message","This message was deleted ... ");
//                        dataSnapshot.getRef().updateChildren(hashMap);
                    }else{
                        Toast.makeText(context,"You can delete only your messages ..." , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView isSeen;
        TextView message, time;// ,isSeen;
        LinearLayout messageLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageTx);
            time = itemView.findViewById(R.id.timeTv);
            //isSeen = itemView.findViewById(R.id.isSeenTv);
            isSeen = itemView.findViewById(R.id.isSeenIm);
            messageLayout = itemView.findViewById(R.id.messageRow);

        }
    }
}
