package com.etini.geekleem.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etini.geekleem.R;
import com.etini.geekleem.fragments.ChatsFragment;
import com.etini.geekleem.fragments.ComposeFragment;
import com.etini.geekleem.fragments.HomeFragment;
import com.etini.geekleem.fragments.NotifyFragment;
import com.etini.geekleem.fragments.ProfileFragment;
import com.etini.geekleem.fragments.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.etini.geekleem.fragments.HomeFragment.CONNECTION_TIMEOUT;
import static com.etini.geekleem.fragments.HomeFragment.READ_TIMEOUT;

public class HomeActivity extends AppCompatActivity {
     //Views
    FrameLayout frameLayout;
    public static ChipNavigationBar chipNavigationBar;
    FloatingActionButton fab_newpost;
    Toolbar home_toolbar;
    public RelativeLayout sr_layout, bottom_nav_bar;

    ImageView home,search,add,notification,me,chat;
    SharedPreferences userPreferences;
    // Shared Preferences reference for retrieving username
    public static final String UserPREFERENCES = "user";
    // Key for username retrieval (make variable public to access from outside)
    public static final String User = "user";
    String username;
    int notify;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       // makeStatusbarTransparent();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //initializing views
        frameLayout = (FrameLayout) findViewById(R.id.flContent);
        home_toolbar = findViewById(R.id.home_toolbar);
        sr_layout = findViewById(R.id.sr_layout);
        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        add = findViewById(R.id.add);
        notification = findViewById(R.id.notification);
        me = findViewById(R.id.me);
        chat = findViewById(R.id.chat);
        bottom_nav_bar = findViewById(R.id.bottom_nav_bar);

        userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
        username = userPreferences.getString(User, "");
        new getNotifications(username).execute();


        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sr_layout.getWindowToken(), 0);

        //setting default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent,
                        new HomeFragment()).commit();


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_toolbar.setVisibility(View.VISIBLE);
                home.setBackgroundResource(R.drawable.ic_home_black_24dp);
                search.setBackgroundResource(R.drawable.ic_search_white_24dp);
                add.setBackgroundResource(R.drawable.add);
                notification.setBackgroundResource(R.drawable.ic_notifications_white_24dp);
                me.setBackgroundResource(R.drawable.ic_person_white_30dp);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,
                                new HomeFragment()).commit();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_toolbar.setVisibility(View.GONE);
                home.setBackgroundResource(R.drawable.ic_home_white_24dp);
                search.setBackgroundResource(R.drawable.ic_search_black_24dp);
                add.setBackgroundResource(R.drawable.add);
                notification.setBackgroundResource(R.drawable.ic_notifications_white_24dp);
                me.setBackgroundResource(R.drawable.ic_person_white_30dp);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,
                                new SearchFragment()).commit();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_toolbar.setVisibility(View.GONE);
                home.setBackgroundResource(R.drawable.ic_home_white_24dp);
                search.setBackgroundResource(R.drawable.ic_search_white_24dp);
                add.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                notification.setBackgroundResource(R.drawable.ic_notifications_white_24dp);
                me.setBackgroundResource(R.drawable.ic_person_white_30dp);

              //  bottom_nav_bar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,
                                new ComposeFragment()).commit();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_toolbar.setVisibility(View.GONE);
                home.setBackgroundResource(R.drawable.ic_home_white_24dp);
                search.setBackgroundResource(R.drawable.ic_search_white_24dp);
                add.setBackgroundResource(R.drawable.add);
                notification.setBackgroundResource(R.drawable.ic_notifications_black_24dp);
                me.setBackgroundResource(R.drawable.ic_person_white_30dp);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,
                                new NotifyFragment()).commit();
            }
        });


        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_toolbar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,
                                new ProfileFragment()).commit();
                home.setBackgroundResource(R.drawable.ic_home_white_24dp);
                search.setBackgroundResource(R.drawable.ic_search_white_24dp);
                add.setBackgroundResource(R.drawable.add);
                notification.setBackgroundResource(R.drawable.ic_notifications_white_24dp);
                me.setBackgroundResource(R.drawable.ic_person_black_30dp);

            }
        });


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_toolbar.setVisibility(View.GONE);
                home.setBackgroundResource(R.drawable.ic_home_white_24dp);
                search.setBackgroundResource(R.drawable.ic_search_white_24dp);
                add.setBackgroundResource(R.drawable.add);
                notification.setBackgroundResource(R.drawable.ic_notifications_white_24dp);
                me.setBackgroundResource(R.drawable.ic_person_white_30dp);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent,
                                new ChatsFragment()).commit();
            }
        });








    }



    public static class getNotifications extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public getNotifications(String searchQuery) {
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
                url = new URL("https://geekleem.com.ng/get_total_notifications.php");

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


                    int notify = Integer.parseInt(json_data.getString("total"));





                }






            } catch (JSONException e) {
            }

        }

    }




    @Override
    public void onBackPressed(){
        finishAffinity();


    }



}
