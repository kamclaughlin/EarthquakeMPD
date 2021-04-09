/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam;

import android.os.AsyncTask;
import android.util.Log;

import com.kam.earthquakeuk_kam.enums.DownloadStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadData extends AsyncTask<String, Void, String> {

    private static final String TAG = "DownloadData";
    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallback;

    public interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public DownloadData(OnDownloadComplete callback) {
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mCallback = callback;
    }

    @Override
    protected void onPostExecute(String data) {

        if (mCallback != null) {
            mCallback.onDownloadComplete(data, mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute has completed");
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (strings == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            Log.d(TAG, "doInBackground: Strings are empty. Set to not initialised.");
            return null;
        }

        try {

            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();

            Log.d(TAG, "doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                result.append(line).append("\n");
            }

            mDownloadStatus = DownloadStatus.OK;
            return result.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data" + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security exception. Need permission?" + e.getMessage());
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error Closing the stream: " + e.getMessage());
                }
            }
        }

        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;

    }

}