package com.kabasonic.messenger.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.AllContactsFragment;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.OnlineContactsFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSingleItem  extends RecyclerView.Adapter<AdapterSingleItem.SingleItemViewHolder> {
    //edit User -> RowItem
    public static final String TAG = "AdapterSingleItem";
    private ArrayList<User> mRowItems;

    private OnItemClickListener mListener;

    private Context context;

    public interface OnItemClickListener {

        void onItemClick(String uid);

        void onMoreButtonClick(String uid);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
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

        //Get data
        String userImage = mRowItems.get(position).getImageUser();
        String userName = mRowItems.get(position).getFirstName() + " " + mRowItems.get(position).getLastName();
        String userStatus = mRowItems.get(position).getStatus();
        //Set data
        holder.mUsername.setText(userName);
        Log.d(TAG,"onBindViewHolder");
        FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            if(!userImage.isEmpty()){
                storageRef.child("uploadsUserIcon/").child(userImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Log.d("Adapter URI image: ","" + String.valueOf(uri));
                        Glide.with(context).load(uri).into(holder.mUserImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

        try{
            if(userStatus.equals("online")){
                Picasso.get().load(userStatus).placeholder(R.drawable.status_online).into(holder.mStatusUser);
                holder.mStatusUser.setVisibility(View.VISIBLE);
            } else{
                Picasso.get().load(userStatus).placeholder(R.drawable.status_online).into(holder.mStatusUser);
                holder.mStatusUser.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return mRowItems.size();
    }

    public AdapterSingleItem(ArrayList<User> mRowItems,Context context){
        this.mRowItems = mRowItems;
        this.context = context;
    }



    public class SingleItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mUserImage;
        public ImageView mStatusUser;
        public TextView mUsername;
        public ImageView mButtonMore;

        public SingleItemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.imageRow);
            mStatusUser = itemView.findViewById(R.id.statusUserRow);
            mUsername = itemView.findViewById(R.id.titleRow);
            mButtonMore = itemView.findViewById(R.id.buttonMore);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Adapter", String.valueOf(getAdapterPosition()));
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(mRowItems.get(position).getUid());
                    }
                    Log.d("Adapter row", String.valueOf(getAdapterPosition()));
                }
            });
            mButtonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onMoreButtonClick(mRowItems.get(position).getUid());
                    }
                    Log.d("Adapter button", String.valueOf(getAdapterPosition()));
                }
            });

        }
    }
}
