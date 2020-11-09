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

public class AdapterSingleItem  extends RecyclerView.Adapter<AdapterSingleItem.SingleItemViewHolder> {

    private ArrayList<RowItem> mRowItems;

    private SingleItemRow mListener;

    public interface SingleItemRow {

        void onItemClick(int position);

        void onMoreButtonClick(int position);
    }

    public void setOnItemClickListener(SingleItemRow listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SingleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
        SingleItemViewHolder singleItemViewHolder = new SingleItemViewHolder(view,mListener);
        return singleItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemViewHolder holder, int position) {
        RowItem currentItem = mRowItems.get(position);

        holder.mUserImage.setImageResource(currentItem.getmIcon());
        if(currentItem.ismOnlineStatus())
        {
            holder.mStatusUser.setImageResource(R.drawable.status_online);
            holder.mStatusUser.setVisibility(View.VISIBLE);
        }else {
            holder.mStatusUser.setVisibility(View.INVISIBLE);
        }
        holder.mUsername.setText(currentItem.getmTitle());
    }

    @Override
    public int getItemCount() {
        return mRowItems.size();
    }

    public AdapterSingleItem(ArrayList<RowItem> mRowItems){
        this.mRowItems = mRowItems;
    }

    public class SingleItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mUserImage;
        public ImageView mStatusUser;
        public TextView mUsername;
        public ImageView mButtonMore;

        public SingleItemViewHolder(@NonNull View itemView, final SingleItemRow listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.imageRow);
            mStatusUser = itemView.findViewById(R.id.statusUserRow);
            mUsername = itemView.findViewById(R.id.titleRow);
            mButtonMore = itemView.findViewById(R.id.buttonMore);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
            mButtonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMoreButtonClick(position);
                    }
                }
            });

        }
    }
}
