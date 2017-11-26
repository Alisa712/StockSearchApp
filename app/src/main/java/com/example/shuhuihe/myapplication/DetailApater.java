package com.example.shuhuihe.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shuhuihe on 11/25/17.
 */

public class DetailApater extends ArrayAdapter<Detail> {
    private int resourceId;

    public DetailApater (Context context, int textViewResourceId,
                        List<Detail> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Detail detail_item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemLabel = view.findViewById(R.id.label);
            viewHolder.itemContent = view.findViewById(R.id.content);
            viewHolder.itemIcon = view.findViewById(R.id.incraseIcon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemLabel.setText(detail_item.getLabel());
        viewHolder.itemContent.setText(detail_item.getContent());
        if(detail_item.getLabel().equals("Change")){
            viewHolder.itemIcon.setVisibility(View.VISIBLE);
            if(detail_item.isIncreased()){
                viewHolder.itemIcon.setImageResource(R.drawable.up);
            } else {
                viewHolder.itemIcon.setImageResource(R.drawable.down);
            }
        }
        else {
            viewHolder.itemIcon.setVisibility(View.INVISIBLE);

        }
        return view;
    }

    class ViewHolder {

        ImageView itemIcon;

        TextView itemLabel;

        TextView itemContent;

    }
}
