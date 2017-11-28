package com.example.shuhuihe.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shuhuihe on 11/27/17.
 */

public class FavoriteAdapter extends ArrayAdapter<Favorite> {
    private int resourceId;

    public FavoriteAdapter (Context context, int textViewResourceId,
                        List<Favorite> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Favorite detail_item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new FavoriteAdapter.ViewHolder();
            viewHolder.symbol = view.findViewById(R.id.fav_symbol);
            viewHolder.price = view.findViewById(R.id.fav_price);
            viewHolder.changeInfo = view.findViewById(R.id.fav_change);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (FavoriteAdapter.ViewHolder) view.getTag();
        }

        viewHolder.symbol.setText(detail_item.getSymbol());
        viewHolder.price.setText(detail_item.getPrice().toString());
        viewHolder.changeInfo.setText(detail_item.getChangeInfo());

        if(detail_item.isIncresing()){
            viewHolder.changeInfo.setTextColor(Color.GREEN);
        } else {
            viewHolder.changeInfo.setTextColor(Color.RED);
        }
        return view;
    }

    class ViewHolder {

        TextView symbol;

        TextView price;

        TextView changeInfo;
    }
}
