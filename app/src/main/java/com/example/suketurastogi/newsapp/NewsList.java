package com.example.suketurastogi.newsapp;

public class NewsList {

    private String mSectionName;
    private String mWebTitle;
    private String mWebUrl;

    public NewsList(String sectionName,String webTitle,String webUrl){

        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebUrl = webUrl;

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
}
