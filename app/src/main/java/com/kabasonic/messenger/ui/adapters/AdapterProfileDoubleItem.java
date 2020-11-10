package com.kabasonic.messenger.ui.adapters;

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
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class AdapterProfileDoubleItem extends ArrayAdapter<RowItem> {

    Context context;

    public AdapterProfileDoubleItem(Context context, int resourceId, ArrayList<RowItem> item){
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
        RowItem currentItem = getItem(position);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.double_row_profile,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageThreeRow);
            holder.textTitle = (TextView) convertView.findViewById(R.id.titleThreeRow);
            holder.textSubtitle = (TextView) convertView.findViewById(R.id.subtitleThreeRow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textTitle.setText(currentItem.getmTitle());
        holder.textSubtitle.setText(currentItem.getmDesc());
        holder.imageView.setImageResource(currentItem.getmIcon());

        return convertView;
    }
}


