package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.etini.geekleem.R;
import com.etini.geekleem.adapter.FeedAdapter;
import com.etini.geekleem.adapter.FriendAdapter;
import com.etini.geekleem.model.Feed;
import com.etini.geekleem.utils.AppController;
import com.etini.geekleem.utils.ImageRequestHandler;
import com.etini.geekleem.utils.PreferenceManager;
import com.google.android.exoplayer2.SimpleExoPlayer;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.etini.geekleem.fragments.HomeFragment.CONNECTION_TIMEOUT;
import static com.etini.geekleem.fragments.HomeFragment.READ_TIMEOUT;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener {
    CircleImageView civ_profileimage;
    TextView tv_fullname, tv_bio, tv_following, tv_follower, tv_info, tv_profile, tv_dm, tv_follow, tv_checkfollowers, tv_checkfollowing;
    RecyclerView rv_profile;
    ProgressBar pb_profile;
    private FriendAdapter mAdapter;
    private static final String URL_FOLLOWING ="https://geekleem.com.ng/all_followers.php";
    private static final String NOTIFY_CHAT_URL ="https://geekleem.com.ng/notify_chat.php";

    // Shared Preferences reference for retrieving username
    SharedPreferences userPreferences;
    public static final String UserPREFERENCES = "user";
    // Key for username retrieval (make variable public to access from outside)
    public static final String User = "user";
    String username;

    // Key for profile_picture url (make variable public to access from outside)
    public static final String Name = "nameKey";
    PreferenceManager preferenceManager;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences preferences;


    //Shared preferences to store bio information
    SharedPreferences bioPreferences;
    public static final String BioPREFERENCES = "BioPrefs" ;
    public static final String Bio = "bioKey";
    String followed_profile_pic;
    private List<Feed> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //widgets
        tv_fullname = findViewById(R.id.tv_fullname);
        tv_bio = findViewById(R.id.tv_bio);
        tv_following = findViewById(R.id.tv_following);
        tv_follower = findViewById(R.id.tv_follower);
        tv_checkfollowing = findViewById(R.id.tv_checkfollowing);
        tv_checkfollowers = findViewById(R.id.tv_checkfollowers);
        rv_profile = findViewById(R.id.rv_profile);
        civ_profileimage = findViewById(R.id.civ_profileimage);
        tv_info = findViewById(R.id.tv_info);
        tv_profile = findViewById(R.id.tv_profile);
        tv_dm = findViewById(R.id.tv_dm);
        tv_follow = findViewById(R.id.tv_follow);
        pb_profile = findViewById(R.id.pb_profile);

        rv_profile.setHasFixedSize(true);
        rv_profile.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Initializing our list
        data = new ArrayList<>();
        //initializing our adapter
        mAdapter = new FriendAdapter(getApplicationContext(),data);
        //Adding adapter to recyclerview
        rv_profile.setAdapter(mAdapter);

        getFollowers();
        Intent i = getIntent();
        username = i.getStringExtra("username");
        followed_profile_pic = i.getStringExtra("dp");
        String bio = i.getStringExtra("bio");
        tv_profile.setText(String.format("%s 's Profile", username));

        tv_fullname.setText(username);
        tv_bio.setText(bio);
        Glide.with(getApplicationContext()).load(followed_profile_pic)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(civ_profileimage);

        new getGeekStories(username).execute();
        new getFollowers(username).execute();
        new getFollowing(username).execute();


        tv_follow.setOnClickListener(this);

        tv_dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String recipient = i.getStringExtra("username");
                String dp = i.getStringExtra("dp");
                String bio = i.getStringExtra("bio");
                Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                intent.putExtra("dp", dp);
                intent.putExtra("bio", bio);
                intent.putExtra("recipient", recipient);
                startActivity(intent);
               // notifyChat();


            }
        });

        tv_checkfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                username = i.getStringExtra("username");
                Intent intent = new Intent(FriendsActivity.this, FriendFollowedActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });
        tv_checkfollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                username = i.getStringExtra("username");
                Intent intent = new Intent(FriendsActivity.this, FriendFollowersActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        civ_profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String username = i.getStringExtra("username");
                String image = i.getStringExtra("dp");
                String bio = i.getStringExtra("bio");
                String type = "Picture";
                Intent intent = new Intent(FriendsActivity.this, ViewImageActivity.class);
                intent.putExtra("image", image);
                intent.putExtra("bio", bio);
                intent.putExtra("username", username);
                intent.putExtra("type", type);
                startActivity(intent);


            }
        });
    }

    @Override
    public void onClick(View v) {
        String follow = tv_follow.getText().toString();
        if(follow.equals("Follow")){
            tv_follow.setText("Unfollow");
            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "https://geekleem.com.ng/follow.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {
                                tv_follow.setText("Unfollow");
                                }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //Preference class for username retrieval
                    userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                    String follower = userPreferences.getString(User, "");
                    String followed = tv_fullname.getText().toString();
                    String followed_bio = tv_bio.getText().toString();


                    params.put("follower", follower);
                    params.put("followed", followed);
                    params.put("followed_bio", followed_bio);
                    params.put("followed_profile_pic", followed_profile_pic);
                    return params;
                }
            };

            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance(getApplicationContext()).addToRequestQueue(requestPostResponse);
        }


        else if(follow.equals("Unfollow")){
            tv_follow.setText("Follow");
            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "https://geekleem.com.ng/unfollow.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {
                                tv_follow.setText("Follow");
                                    }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>params=new HashMap<String, String>();
                    //Preference class for username retrieval
                    userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                    String follower = userPreferences.getString(User, "");
                    String followed = tv_fullname.getText().toString();

                    params.put("follower", follower);
                    params.put("followed", followed);
                    return params;
                }
            };

            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance(getApplicationContext()).addToRequestQueue(requestPostResponse);
        }


    }




    private class getGeekStories extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public getGeekStories(String searchQuery) {
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
                url = new URL("https://geekleem.com.ng/get_yourstories.php");

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
            pb_profile.setVisibility(View.GONE);
            List<Feed> data = new ArrayList<>();

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("feed");

                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    Feed feed = new Feed();

                    feed.setUsername(json_data.getString("username"));
                    feed.setBio(json_data.getString("bio"));
                    feed.setTime(json_data.getString("time_created"));
                    feed.setText(json_data.getString("text"));
                    feed.setDp(json_data.getString("profile_picture"));
                    feed.setPostid(json_data.getString("postid"));
                    feed.setImage(json_data.getString("image"));
                    feed.setVideo(json_data.getString("video"));
                    feed.setLikes(json_data.getString("likes"));
                    data.add(feed);
                }

                // Setup and Handover data to recyclerview
                mAdapter = new FriendAdapter(getApplicationContext(), data);
                rv_profile.setAdapter(mAdapter);

            } catch (JSONException e) {

                if(e.toString().contains("feed")) {
                    rv_profile.setVisibility(View.GONE);
                    tv_info.setVisibility(View.VISIBLE);
                    pb_profile.setVisibility(View.GONE);
                }
                else if(e.toString().contains("java.net.ConnectException")) {
                    rv_profile.setVisibility(View.GONE);
                    tv_info.setVisibility(View.VISIBLE);
                    pb_profile.setVisibility(View.GONE);
                    tv_info.setText("No Geek Stories, Check your Connectivity");
                }
                else if(e.toString().contains("java.net.SocketTimeoutException")) {
                    rv_profile.setVisibility(View.GONE);
                    tv_info.setVisibility(View.VISIBLE);
                    pb_profile.setVisibility(View.GONE);
                    tv_info.setText("No Geek Stories, Check your Connectivity");
                }



            }
        }

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
                url = new URL("https://geekleem.com.ng/get_yourfollowers.php");

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

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("notify");

                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    String notify = json_data.getString("total");
                        tv_follower.setText(notify);
                }
            } catch (JSONException e) {
                if (e.toString().contains("No value")) {
                    tv_follower.setText(String.valueOf(0));
                } else if (e.toString().contains("java.net.ConnectException")) {

                } else if (e.toString().contains("java.net.SocketTimeoutException")) {
                }



            }

        }

    }

    private class getFollowing extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public getFollowing(String searchQuery) {
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
                url = new URL("https://geekleem.com.ng/get_yourfollowing.php");

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

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("notify");

                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    String notify = json_data.getString("total");

                         tv_following.setText(notify);

                }
            } catch (JSONException e) {
                if (e.toString().contains("No value")) {
                    tv_following.setText(String.valueOf(0));
                } else if (e.toString().contains("java.net.ConnectException")) {

                } else if (e.toString().contains("java.net.SocketTimeoutException")) {
                }



            }

        }

    }

    private void getFollowers() {
        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, URL_FOLLOWING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("hi");
                    for (int i=0; i<array.length(); i++){
                        JSONObject ob=array.getJSONObject(i);
                        String followed =ob.getString("followed");
                        String follower =ob.getString("follower");
                        String followed_name = tv_fullname.getText().toString();
                        if(followed.equals(followed_name)){
                            tv_follow.setText("Unfollow");
                                  }
                        else {
                            tv_follow.setText("Follow");


                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String>params=new HashMap<String, String>();
                //Preference class for username retrieval
                userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                String follower = userPreferences.getString(User, "");
                String followed = tv_fullname.getText().toString();

                params.put("follower", follower);
                params.put("followed", followed);
                return params;
            }
        };
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(getApplicationContext()).addToRequestQueue(requestPostResponse);
    }

    private void notifyChat() {
        class uploadMediaText extends AsyncTask<String, Void, String> {


            ImageRequestHandler rh = new ImageRequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s.equals("")) {

                } else if (s.contains("Successfully Uploaded")) {

                } else if (s.contains("Error")) {


                }
            }

            @Override
            protected String doInBackground(String... params) {

                Intent i = getIntent();
                String  sender = userPreferences.getString(User, "");
                String recipient = i.getStringExtra("username");
                String recipientdp = i.getStringExtra("dp");
                String bio = i.getStringExtra("bio");


                HashMap<String, String> data = new HashMap<>();
                data.put("sender", sender);
                data.put("recipient", recipient);
                data.put("recipientdp", recipientdp);
                data.put("bio", bio);

                return rh.sendPostRequest(NOTIFY_CHAT_URL, data);
            }
        }
        uploadMediaText ui = new uploadMediaText();
        ui.execute();
    }


    @Override
    public void onPause() {

        super.onPause();
        pausePlayer(mAdapter.simpleExoPlayer);

    }

    @Override
    public void onStop() {

        super.onStop();
        pausePlayer(mAdapter.simpleExoPlayer);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        releaseExoPlayer(mAdapter.simpleExoPlayer);
    }

    @Override
    public void onResume() {

        super.onResume();
        startPlayer(mAdapter.simpleExoPlayer);
    }

    public static void startPlayer(SimpleExoPlayer simpleExoPlayer) {

        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);

        }
    }

    public static void pausePlayer(SimpleExoPlayer simpleExoPlayer) {

        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);

        }
    }

    public static void releaseExoPlayer(SimpleExoPlayer simpleExoPlayer) {

        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }

    }








}
