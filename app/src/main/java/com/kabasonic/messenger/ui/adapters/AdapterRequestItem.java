package com.kabasonic.messenger.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRequestItem extends RecyclerView.Adapter<AdapterRequestItem.SingleItemViewHolder> {

    private ArrayList<User> mRowItems;
    private RequestItemRow mListener;

    public interface RequestItemRow {

        void onItemClick(String uid);
        void onItemAccept(String uid, int position);
        void onItemDecline(String uid, int position);

    }

    public void setOnItemClickListener(RequestItemRow listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SingleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_request,parent,false);
        SingleItemViewHolder singleItemViewHolder = new SingleItemViewHolder(view, mListener);
        return singleItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemViewHolder holder, int position) {
        //Get data
        String userImage = mRowItems.get(position).getImageUser();
        String userName = mRowItems.get(position).getFirstName() + " " + mRowItems.get(position).getLastName();
        String userBio = mRowItems.get(position).getBio();

        //Set data
        holder.mUsername.setText(userName);
        holder.mUserBio.setText(userBio);
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.default_user_image).into(holder.mUserImage);
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return mRowItems.size();
    }

    public AdapterRequestItem(ArrayList<User> mRowItems){
        this.mRowItems = mRowItems;
    }

    public class SingleItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mUserImage;
        public TextView mUsername;
        public TextView mUserBio;
        public Button mButtonAccept, mButtonDecline;

        public SingleItemViewHolder(@NonNull View itemView, final RequestItemRow listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.imageRequest);
            mUsername = itemView.findViewById(R.id.titleRequest);
            mUserBio = itemView.findViewById(R.id.descRequest);
            mButtonAccept = itemView.findViewById(R.id.submitRequest);
            mButtonDecline = itemView.findViewById(R.id.declineRequest);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(mRowItems.get(position).getUid());
                    }
                }
            });
            mButtonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemAccept(mRowItems.get(position).getUid(), position);
                    }
                }
            });
            mButtonDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemDecline(mRowItems.get(position).getUid(), position);
                    }
                }
            });

        }
    }
}
