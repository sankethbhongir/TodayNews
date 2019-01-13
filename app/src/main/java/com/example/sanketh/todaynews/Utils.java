package com.example.sanketh.todaynews;

import android.text.TextUtils;
import android.util.Log;

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
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from guardian api.
 */

public final class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    /**
     * Query the Guardian dataset to return {@link News} object to represent single news feed
     */

    public static List<News> fetchNewsData(String requestUrl) {
        Log.i(LOG_TAG, "Test: fetchNewsData() called ...");

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "error closing input stream");
        }

        return extractNewsFeatures(jsonResponse);
    }

    /**
     * Creating url object from string url
     *
     * @param requestUrl string  url
     * @return url object
     */
    private static URL createUrl(String requestUrl) {
        URL url = null;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "cannot build url");
        }

        return url;
    }

    /**
     * performing http request to the url and returning json string
     *
     * @param url url object
     * @return json string
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else
                Log.e(LOG_TAG, "Error Response Code: " + urlConnection.getResponseCode());
        } catch (IOException e) {

            Log.e(LOG_TAG, "Error retrieving json response", e);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        if (inputStream != null) {

            inputStream.close();
        }

        return jsonResponse;

    }


    /**
     * Conveet the {@link InputStream} into the String which contains the whole json response
     *
     * @param inputStream inputStream object
     * @return json string
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder outputString = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                outputString.append(line);
                line = bufferedReader.readLine();
            }
        }

        return outputString.toString();
    }

    /**
     * Parsing json response and return list of news objects
     *
     * @param jsonResponse String of json response
     * @return list of news objects
     */
    private static List<News> extractNewsFeatures(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse))
            return null;

        List<News> newsList = new ArrayList<>();

        try {

            JSONObject baseJsonObject = new JSONObject(jsonResponse);
            JSONObject responseObject = baseJsonObject.getJSONObject("response");
            JSONArray newsArray = responseObject.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNewsObject = newsArray.getJSONObject(i);
                JSONObject fields = currentNewsObject.getJSONObject("fields");
                JSONArray tags = currentNewsObject.getJSONArray("tags");
                JSONObject tagObject = tags.getJSONObject(0);

                String headline = fields.getString("headline");
                String author = tagObject.getString("webTitle");
                String publishedDate = currentNewsObject.getString("webPublicationDate").substring(0, 10);
                String section = currentNewsObject.getString("sectionName");
                String webUrl = currentNewsObject.getString("webUrl");
                newsList.add(new News(headline, author, publishedDate, section, webUrl));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "problem parsing news json results");
        }


        return newsList;
    }

}
