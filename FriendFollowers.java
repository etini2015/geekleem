package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etini.geekleem.R;
import com.etini.geekleem.adapter.FollowerAdapter;
import com.etini.geekleem.model.Follow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.etini.geekleem.fragments.HomeFragment.CONNECTION_TIMEOUT;
import static com.etini.geekleem.fragments.HomeFragment.READ_TIMEOUT;

public class FriendFollowersActivity extends AppCompatActivity {
    RecyclerView rv_followers;
    private FollowerAdapter mAdapter;
    private TextView tv_info;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_followers);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        rv_followers = findViewById(R.id.rv_followers);
        tv_info = findViewById(R.id.tv_info);
        rv_followers.setHasFixedSize(true);
        rv_followers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Intent i = getIntent();
        username = i.getStringExtra("username");


        new getFollowers(username).execute();

    }
    private class getFollowers extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public getFollowers(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://geekleem.com.ng/real_follower.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput to true as we send and receive data
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // add parameter to our above url
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", searchQuery);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {
            List<Follow> data = new ArrayList<>();

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("notify");

                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    Follow follow = new Follow();
                    follow.setFollower(json_data.getString("follower"));
                    follow.setBio(json_data.getString("follower_bio"));
                    follow.setPicture(json_data.getString("follower_profile_pic"));
                    data.add(follow);
                }


                // Setup and Handover data to recyclerview
                mAdapter = new FollowerAdapter(getApplicationContext(), data);
                rv_followers.setAdapter(mAdapter);



            } catch (JSONException e) {
                rv_followers.setVisibility(View.GONE);
                tv_info.setVisibility(View.VISIBLE);


            }

        }

    }
}
