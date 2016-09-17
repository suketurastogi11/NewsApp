package com.example.suketurastogi.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewListAdapter  extends ArrayAdapter<NewsList> {

    public NewListAdapter(Activity context, ArrayList<NewsList> news){
        super(context,0,news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        NewsList currentNews = getItem(position);

        TextView sectionName = (TextView)listItemView.findViewById(R.id.section_name_list_item);
        String sectionNameToShow = "Section : " + currentNews.getSectionName() ;
        sectionName.setText(sectionNameToShow);

        TextView webTitle = (TextView)listItemView.findViewById(R.id.web_title_list_item);
        String webTitleToShow = "Description : " + currentNews.getWebTitle() ;
        webTitle.setText(webTitleToShow);

        TextView authorName = (TextView)listItemView.findViewById(R.id.author_name_list_item);
        authorName.setText(currentNews.getAuthorName());

        return listItemView;
    }
}
