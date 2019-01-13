package com.example.sanketh.todaynews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }


    // Check if there is an existing list item view (called convertView) that we can reuse,
    // otherwise, if convertView is null, then inflate a new list item layout.
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        ViewHolder viewHolder = new ViewHolder();
        if (listItemView == null) {

            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);

            viewHolder.headline = listItemView.findViewById(R.id.heading_text_view);
            viewHolder.author = listItemView.findViewById(R.id.author_text_view);
            viewHolder.publishedDate = listItemView.findViewById(R.id.published_date_text_view);
            viewHolder.section = listItemView.findViewById(R.id.section_text_view);
            listItemView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) listItemView.getTag();


        News news = getItem(position);


        if (news != null) {


            viewHolder.headline.setText(news.getHeadline());
            viewHolder.author.setText(news.getAuthor());
            viewHolder.publishedDate.setText(news.getPublishedDate());
            viewHolder.section.setText(news.getSection());

        }

        return listItemView;


    }

    private static class ViewHolder {
        TextView headline, author, publishedDate, section;
    }

}
