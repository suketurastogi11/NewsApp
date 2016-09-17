package com.example.suketurastogi.newsapp;

public class NewsList {

    private String mSectionName;
    private String mWebTitle;
    private String mWebUrl;
    private String mAuthorName;

    public NewsList(String sectionName,String webTitle,String webUrl,String authorName){

        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mAuthorName = authorName;

    }
    public String getSectionName(){
        return mSectionName;
    }

    public String getWebTitle(){
        return mWebTitle;
    }

    public String getWebUrl(){
        return mWebUrl;
    }

    public String getAuthorName(){
        return  mAuthorName;
    }
}
