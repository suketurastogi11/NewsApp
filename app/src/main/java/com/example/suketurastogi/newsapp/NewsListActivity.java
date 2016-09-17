package com.example.suketurastogi.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = NewsListActivity.class.getSimpleName();

    //Array list in which all news are fetched from server and saved.
    final ArrayList<NewsList> newsArrayList = new ArrayList<>();

    //Url to hit for Getting News.
    String NewsUrl = "http://content.guardianapis.com/search?q=pokemon&api-key=test&show-tags=contributor";

    //Initializing server results to null.
    String resultNewsListServer = null;

    //List View in which all the News will be visible.
    ListView newsList;
    TextView noData;

    //Custom Adapter to show news list.
    NewListAdapter newsListAdapter;

    //To Swipe And Refresh Whole News List.
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        newsList = (ListView) findViewById(R.id.news_list_view);

        newsListAdapter = new NewListAdapter(NewsListActivity.this, newsArrayList);

        noData = (TextView) findViewById(R.id.no_data_text_view);
        newsList.setEmptyView(noData);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_news_list_view);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();

                //Call this method to Check for Internet Connectivity to get the news list.
                generateNewsList();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        generateNewsList();
    }

    public void generateNewsList() {

        if (isNetworkAvailable()) {
            // Perform the network request
            NewsAsyncTask task = new NewsAsyncTask();
            task.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
        }
    }

    //Method Returns Connectivity to Internet in Boolean Value.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onNewsItemSelection() {

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NewsList news = newsArrayList.get(position);

                String webUrl = news.getWebUrl();

                Log.e("webUrl : ", "" + webUrl);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(webUrl));
                startActivity(browserIntent);

            }
        });
    }

    public class NewsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // Create URL object
            URL url = createUrl(NewsUrl);

            Log.v("url", "" + url);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            resultNewsListServer = jsonResponse;

            Log.v("resultNewsListServer ", "" + resultNewsListServer);

            return resultNewsListServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (resultNewsListServer == null) {
                Log.v("No Data ", "No Data");
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();

            } else {
                try {

                    newsArrayList.clear();

                    String NEWS_LIST_JSON_RESPONSE = resultNewsListServer;

                    // Parse the response given by the NEWS_LIST_JSON_RESPONSE string
                    // and build up a list of News objects with the corresponding data.

                    JSONObject newsJsonResponse = new JSONObject(NEWS_LIST_JSON_RESPONSE);
                    JSONObject newsResponseJsonObject = newsJsonResponse.getJSONObject("response");

                    String newsStatusJsonObject = newsResponseJsonObject.getString("status");

                    Log.e("newsStatusJsonObject", "" + newsStatusJsonObject);

                    if (newsStatusJsonObject.equalsIgnoreCase("ok")) {

                        swipeContainer.setRefreshing(false);

                        JSONArray newsResultsJsonArray = newsResponseJsonObject.getJSONArray("results");

                        for (int i = 0; i < newsResultsJsonArray.length(); i++) {

                            JSONObject currentItem = newsResultsJsonArray.getJSONObject(i);

                            String sectionNameInfo = currentItem.getString("sectionName");
                            String webTitleInfo = currentItem.getString("webTitle");
                            String webUrlInfo = currentItem.getString("webUrl");
                            String authorNameInfo = null;

                            JSONArray authorJSONArray = currentItem.getJSONArray("tags");

                            for (int j = 0; j < authorJSONArray.length(); j++) {

                                JSONObject currentAuthor = authorJSONArray.getJSONObject(j);
                                authorNameInfo = currentAuthor.getString("webTitle");
                            }

                            Log.e("sectionNameInfo", "" + sectionNameInfo);
                            Log.e("webTitleInfo", "" + webTitleInfo);
                            Log.e("webUrlInfo", "" + webUrlInfo);
                            Log.e("authorNameInfo", "" + authorNameInfo);

                            newsArrayList.add(new NewsList(sectionNameInfo, webTitleInfo, webUrlInfo, authorNameInfo));

                            newsList.setAdapter(newsListAdapter);

                            onNewsItemSelection();
                        }
                    }

                } catch (JSONException e) {
                    // If an error is thrown when executing any of the above statements in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("Exception : ", "Problem parsing the newsList JSON results", e);
                }
            }
        }

        /**
         * Returns new URL object from the given string URL.
         */

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                Log.v("ResponseCode : ", "" + urlConnection.getResponseCode());

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }

            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }
}
