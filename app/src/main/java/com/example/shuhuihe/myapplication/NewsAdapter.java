package com.example.shuhuihe.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by shuhuihe on 11/26/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private int resourceId;
    private Context mContext;

    public NewsAdapter (Context context, int textViewResourceId,
                         List<News> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mContext=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final News detail_item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = view.findViewById(R.id.title_info);
            viewHolder.newsAuthor = view.findViewById(R.id.author_info);
            viewHolder.newsDate = view.findViewById(R.id.date_info);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.newsTitle.setText(detail_item.getTitle());
//        viewHolder.newsTitle.setTextColor(Color.BLACK);
//        viewHolder.newsTitle.setMovementMethod(LinkMovementMethod.getInstance());
//        String text = "<a href='"+detail_item.getLink()+"'"+"style='text-decoration: none;'"+"'>"+detail_item.getTitle()+"</a>";
       // viewHolder.newsTitle.setText(Html.fromHtml(text));
        viewHolder.newsAuthor.setText(detail_item.getAuthor());
        viewHolder.newsDate.setText(detail_item.getDate());

        return view;
    }

    class ViewHolder {

        TextView newsTitle;

        TextView newsAuthor;

        TextView newsDate;
    }
}
