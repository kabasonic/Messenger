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

public class AdapterItemThreeRowProfile extends ArrayAdapter<ModelItemProfile> {

    Context context;

    public AdapterItemThreeRowProfile(Context context, int resourceId, ArrayList<ModelItemProfile> item){
        super(context,resourceId,item);
        this.context = context;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textTitle;
        TextView textSubtitle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        ModelItemProfile modelItemProfile = getItem(position);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.three_row,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageThreeRow);
            holder.textTitle = (TextView) convertView.findViewById(R.id.titleThreeRow);
            holder.textSubtitle = (TextView) convertView.findViewById(R.id.subtitleThreeRow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textTitle.setText(modelItemProfile.getTitle());
        holder.textSubtitle.setText(modelItemProfile.getSubtitle());
        holder.imageView.setImageResource(modelItemProfile.getImage());

        return convertView;
    }
}


