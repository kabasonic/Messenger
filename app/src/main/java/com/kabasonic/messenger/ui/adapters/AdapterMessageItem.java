package com.kabasonic.messenger.ui.adapters;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdapterMessageItem extends RecyclerView.Adapter<AdapterMessageItem.MessageItemViewHolder> {

    public static final String TAG = "AdapterMessageItem";

    private List<User> userArrayList;
    private Context context;
    private HashMap<String, String> lastMessageMap;
    private HashMap<String, String> timeStampMap;
    private HashMap<String, String> statusMessageMap;
    private HashMap<String, String> countMessage;
    private String reciverUID = null;
    private String myUid = null;

    public AdapterMessageItem(Context context) {
        this.userArrayList = new ArrayList<>();
        this.context = context;
        lastMessageMap = new HashMap<>();
        timeStampMap = new HashMap<>();
        statusMessageMap = new HashMap<>();
        countMessage = new HashMap<>();

    }

    private ItemRow mListener;

    public interface ItemRow {
        void onItemClick(String uid, int position);

        void onItemLongClick(String uid, int position);
    }

    public void setOnItemClickListener(ItemRow listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_message, parent, false);
        MessageItemViewHolder messageItemViewHolder = new MessageItemViewHolder(view, mListener);
        return messageItemViewHolder;
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
        String countMessages = countMessage.get(hisUid);


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
            Date date = new Date(dateLong);
            //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String output = dateFormat.format(date);
            holder.mTime.setText(output);
        }

        if (statusMessage == null || statusMessage.equals("default")) {
            holder.mStatusMessage.setVisibility(View.GONE);
        } else if (statusMessage.equals("true")) {
            Log.d("TAG", "Okay true");
            holder.mStatusMessage.setImageResource(R.drawable.ic_round_done_all_24);
            holder.mStatusMessage.setVisibility(View.VISIBLE);
        } else if (statusMessage.equals("false")) {
            holder.mStatusMessage.setImageResource(R.drawable.ic_round_done_24);
            holder.mStatusMessage.setVisibility(View.VISIBLE);
            Log.d("TAG", "Not okay false");
        }

        Log.d(TAG,"RECEIVER UID: " + reciverUID);
        if (countMessages != null) {
            Log.d("Count message", "Count messages " + countMessages);
            if (countMessages.equals("0")) {
                holder.mCountMessage.setVisibility(View.INVISIBLE);
            } else {
                if(reciverUID != null && myUid != null && reciverUID.equals(myUid))
                holder.mCountMessage.setVisibility(View.VISIBLE);
                holder.mCountMessage.setText(countMessages);

            }
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
            if (userStatus.equals("Online")) {
                Picasso.get().load(userStatus).placeholder(R.drawable.status_online).into(holder.mStatusUser);
                holder.mStatusUser.setVisibility(View.VISIBLE);
            } else {
                //Picasso.get().load(userStatus).placeholder(R.drawable.status_online).into(holder.mStatusUser);
                holder.mStatusUser.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }

    }

    public void setStatusMessageMap(String userUid, String statusMessage) {
        statusMessageMap.put(userUid, statusMessage);
    }

    public void setTimeStampMap(String userUid, String timeStamp) {
        timeStampMap.put(userUid, timeStamp);
    }

    public void setCountMessage(String userUid, String countMessages) {
        countMessage.put(userUid, countMessages);
    }

    public void setReceiverUID(String reciverUID) {
        this.reciverUID = reciverUID;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public void setLastMessageMap(String userUid, String lastMessage) {
        lastMessageMap.put(userUid, lastMessage);
    }

    public void setUserArrayList(List<User> arrayList) {
        this.userArrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        if (userArrayList == null) {
            return 0;
        }
        return userArrayList.size();
    }

    public class MessageItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserImage;
        public ImageView mStatusUser;
        public TextView mUsername;
        public TextView mMesage;
        public ImageView mStatusMessage;
        public TextView mTime;
        public TextView mCountMessage;

        public MessageItemViewHolder(@NonNull View itemView, final ItemRow listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.imageUser);
            mStatusUser = itemView.findViewById(R.id.statusUser);
            mUsername = itemView.findViewById(R.id.userName);
            mMesage = itemView.findViewById(R.id.textMessage);
            mStatusMessage = itemView.findViewById(R.id.statusMessage);
            mTime = itemView.findViewById(R.id.timeMessage);
            mCountMessage = itemView.findViewById(R.id.countMessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(userArrayList.get(position).getUid(), position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(userArrayList.get(position).getUid(), position);
                    }
                    return true;
                }
            });

        }
    }
}
