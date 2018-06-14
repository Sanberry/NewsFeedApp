package com.example.android.newsfeedapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {
    private static final String TIME_SEPARATOR = "T";

    public NewsFeedAdapter(Context context, List<NewsFeed> newsFeed) {
        super(context, 0, newsFeed);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }
        String date;
        NewsFeed currentNewsFeed = getItem(position);
        String titleOfNews = currentNewsFeed.getTitle();
        String fullDate = currentNewsFeed.getDate();
        String authorName = currentNewsFeed.getAuthor();
        String articleSection = currentNewsFeed.getSection();

        String[] parts = fullDate.split(TIME_SEPARATOR);
        date = parts[0];
        TextView dateOfTheArticle = (TextView) listItemView.findViewById(R.id.date);
        dateOfTheArticle.setText(date);
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(titleOfNews);
        TextView author = (TextView) listItemView.findViewById(R.id.author);
        author.setText(authorName);
        TextView section = (TextView) listItemView.findViewById(R.id.section);
        section.setText(articleSection);
        return listItemView;
    }
}
