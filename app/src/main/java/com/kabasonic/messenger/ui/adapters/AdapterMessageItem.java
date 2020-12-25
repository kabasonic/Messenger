package com.kabasonic.messenger.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.notifications.Data;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.kabasonic.messenger.ui.userchat.UserChat;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AdapterMessageItem extends RecyclerView.Adapter<AdapterMessageItem.MessageItemViewHolder> {


    private ArrayList<User> userArrayList;
    private Context context;
    private HashMap<String, String> lastMessageMap;
    private HashMap<String, String> timeStampMap;
    private HashMap<String, String> statusMessageMap;

    public AdapterMessageItem(ArrayList<User> userArrayList, Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
        lastMessageMap = new HashMap<>();
        timeStampMap = new HashMap<>();
        statusMessageMap = new HashMap<>();
    }

    private ItemRow mListener;

    public interface ItemRow {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemRow listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_message, parent, false);
        return new MessageItemViewHolder(view, mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MessageItemViewHolder holder, int position) {
        //get data
        String hisUid = userArrayList.get(position).getUid();
        String userImage = userArrayList.get(position).getImageUser();
        String firstName = userArrayList.get(position).getFirstName();
        String lastName = userArrayList.get(position).getLastName();
        String userStatus = userArrayList.get(position).getStatus();

        String lastMessage = lastMessageMap.get(hisUid);
        String timeStamp = timeStampMap.get(hisUid);
        String statusMessage = statusMessageMap.get(hisUid);

        //set data
        String userName = firstName + " " + lastName;
        holder.mUsername.setText(userName);

        if (lastMessage == null || lastMessage.equals("default")) {
            holder.mMesage.setVisibility(View.GONE);
        } else {
            holder.mMesage.setVisibility(View.VISIBLE);
            holder.mMesage.setText(lastMessage);
        }

        if (timeStamp == null || timeStamp.equals("default")) {
            holder.mTime.setVisibility(View.GONE);
        } else {
            holder.mTime.setVisibility(View.VISIBLE);
            long dateLong = Long.parseLong(timeStamp);
            Date date = new Date(dateLong * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a");
            String output = dateFormat.format(date);
            holder.mTime.setText(output);
        }

        if(statusMessage == null || statusMessage.equals("default")){
            holder.mStatusMessage.setVisibility(View.GONE);
        }else if(statusMessage.equals("true")){
            Log.d("TAG","Okay true");
            holder.mStatusMessage.setImageResource(R.drawable.ic_round_done_all_24);
            holder.mStatusMessage.setVisibility(View.VISIBLE);
        }else if(statusMessage.equals("false")){
            holder.mStatusMessage.setImageResource(R.drawable.ic_round_done_24);
            holder.mStatusMessage.setVisibility(View.VISIBLE);
            Log.d("TAG","Not okay false");
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (!userImage.isEmpty()) {
            storageRef.child("uploadsUserIcon/").child(userImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Glide.with(context).load(uri).into(holder.mUserImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        try {
            if (userStatus.equals("online")) {
                Picasso.get().load(userStatus).placeholder(R.drawable.status_online).into(holder.mStatusUser);
                holder.mStatusUser.setVisibility(View.VISIBLE);
            } else {
                //Picasso.get().load(userStatus).placeholder(R.drawable.status_online).into(holder.mStatusUser);
                holder.mStatusUser.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserChat.class);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });

    }

    public void setStatusMessageMap(String userUid, String statusMessage) {
        statusMessageMap.put(userUid, statusMessage);
    }

    public void setTimeStampMap(String userUid, String timeStamp) {
        timeStampMap.put(userUid, timeStamp);
    }

    public void setLastMessageMap(String userUid, String lastMessage) {
        lastMessageMap.put(userUid, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class MessageItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserImage;
        public ImageView mStatusUser;
        public TextView mUsername;
        public TextView mMesage;
        //        public ImageView mMute;
        public ImageView mStatusMessage;
        public TextView mTime;
//        public TextView mCountMessage;

        public MessageItemViewHolder(@NonNull View itemView, final ItemRow listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.imageUser);
            mStatusUser = itemView.findViewById(R.id.statusUser);
            mUsername = itemView.findViewById(R.id.userName);
            mMesage = itemView.findViewById(R.id.textMessage);
//            mMute = itemView.findViewById(R.id.muteImage);
            mStatusMessage = itemView.findViewById(R.id.statusMessage);
            mTime = itemView.findViewById(R.id.timeMessage);
//            mCountMessage = itemView.findViewById(R.id.countMessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }
}
