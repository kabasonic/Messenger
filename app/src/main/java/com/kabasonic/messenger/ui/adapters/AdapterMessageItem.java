package com.kabasonic.messenger.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class AdapterMessageItem extends RecyclerView.Adapter<AdapterMessageItem.MessageItemViewHolder> {
    public AdapterMessageItem(ArrayList<RowItem> mRowItems) {
        this.mRowItems = mRowItems;
    }

    private ArrayList<RowItem> mRowItems;

    private ItemRow mListener;

    public interface ItemRow{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemRow listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MessageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_message,parent,false);
        MessageItemViewHolder messageItemViewHolder = new MessageItemViewHolder(view,mListener);
        return messageItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageItemViewHolder holder, int position) {
        RowItem currentItem = mRowItems.get(position);

        holder.mUserImage.setImageResource(currentItem.getmIcon());
        if(currentItem.ismOnlineStatus()){
            holder.mStatusUser.setImageResource(R.drawable.status_online);
            holder.mStatusUser.setVisibility(View.VISIBLE);
        } else{
            holder.mStatusUser.setVisibility(View.INVISIBLE);
        }
        holder.mUsername.setText(currentItem.getmTitle());
        holder.mMesage.setText(currentItem.getmDesc());
        if(currentItem.ismMuteStatus()){
            holder.mMute.setImageResource(R.drawable.ic_round_volume_off_24);
            holder.mMute.setVisibility(View.VISIBLE);
        }else holder.mMute.setVisibility(View.INVISIBLE);
        switch (currentItem.getmStatusMessage()){
            case "sending":
                holder.mStatusMessage.setImageResource(R.drawable.ic_round_restore_24);
                holder.mStatusMessage.setVisibility(View.VISIBLE);
                break;
            case "send":
                holder.mStatusMessage.setImageResource(R.drawable.ic_round_done_24);
                holder.mStatusMessage.setVisibility(View.VISIBLE);
                break;
            case "accepted":
                holder.mStatusMessage.setImageResource(R.drawable.ic_round_done_all_24);
                holder.mStatusMessage.setVisibility(View.VISIBLE);
                break;
            default:
                holder.mStatusMessage.setVisibility(View.INVISIBLE);
            break;
        }
        holder.mTime.setText(currentItem.getmTime());

        if(String.valueOf(currentItem.getmCountMessage())!=null && currentItem.getmCountMessage() > 0)
        {
            holder.mCountMessage.setText(String.valueOf(currentItem.getmCountMessage()));
            holder.mCountMessage.setVisibility(View.VISIBLE);
        } else{
            holder.mCountMessage.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mRowItems.size();
    }

    public class MessageItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserImage;
        public ImageView mStatusUser;
        public TextView mUsername;
        public TextView mMesage;
        public ImageView mMute;
        public ImageView mStatusMessage;
        public TextView mTime;
        public TextView mCountMessage;

        public boolean mStatusMute;

        public MessageItemViewHolder(@NonNull View itemView,final ItemRow listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.imageUser);
            mStatusUser = itemView.findViewById(R.id.statusUser);
            mUsername = itemView.findViewById(R.id.userName);
            mMesage = itemView.findViewById(R.id.textMessage);
            mMute = itemView.findViewById(R.id.muteImage);
            mStatusMessage = itemView.findViewById(R.id.statusMessage);
            mTime = itemView.findViewById(R.id.timeMessage);
            mCountMessage = itemView.findViewById(R.id.countMessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }
}
