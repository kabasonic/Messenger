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

public class AdapterDoubleItem extends RecyclerView.Adapter<AdapterDoubleItem.SingleItemViewHolder> {

    private ArrayList<RowItem> mRowItems;

    private SingleItemRow mListener;

    public interface SingleItemRow {

        void onItemClick(int position);

    }

    public void setOnItemClickListener(SingleItemRow listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SingleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_row,parent,false);
        SingleItemViewHolder singleItemViewHolder = new SingleItemViewHolder(view,mListener);
        return singleItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemViewHolder holder, int position) {
        RowItem currentItem = mRowItems.get(position);

        holder.mGroupImage.setImageResource(currentItem.getmRowImage());
        holder.mGroupTitle.setText(currentItem.getmRowTitle());
        holder.mGroupDesc.setText(currentItem.getmRowDesc());
    }

    @Override
    public int getItemCount() {
        return mRowItems.size();
    }

    public AdapterDoubleItem(ArrayList<RowItem> mRowItems){
        this.mRowItems = mRowItems;
    }

    public class SingleItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mGroupImage;
        public TextView mGroupTitle;
        public TextView mGroupDesc;

        public SingleItemViewHolder(@NonNull View itemView, final SingleItemRow listener) {
            super(itemView);
            mGroupImage = itemView.findViewById(R.id.imageDoubleRow);
            mGroupTitle = itemView.findViewById(R.id.titleDoubleRow);
            mGroupDesc = itemView.findViewById(R.id.descDoubleRow);

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
