package com.kabasonic.messenger.ui.bottomnavigation.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kabasonic.messenger.R;

import java.util.ArrayList;

public class AdapterItemTwoRowProfile extends ArrayAdapter<ModelItemProfile> {

    Context context;

    public AdapterItemTwoRowProfile(Context context, int resourceId, ArrayList<ModelItemProfile> item){
        super(context,resourceId,item);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textTitle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        ModelItemProfile modelItemProfile = getItem(position);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.two_row,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageTwoRow);
            holder.textTitle = (TextView) convertView.findViewById(R.id.titleTwoRow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textTitle.setText(modelItemProfile.getTitle());
        holder.imageView.setImageResource(modelItemProfile.getImage());

        return convertView;
    }
}


