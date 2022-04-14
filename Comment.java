package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.etini.geekleem.R;
import com.etini.geekleem.adapter.CommentsAdapter;
import com.etini.geekleem.model.Comment;
import com.etini.geekleem.utils.AppController;
import com.etini.geekleem.utils.ImageRequestHandler;
import com.etini.geekleem.utils.PreferenceManager;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;
import static com.etini.geekleem.fragments.HomeFragment.CONNECTION_TIMEOUT;
import static com.etini.geekleem.fragments.HomeFragment.READ_TIMEOUT;

public class CommentActivity extends AppCompatActivity {
    private ImageView iv_PostedImage, iv_ProfileImage, iv_Likes;
    private EditText et_Comment;
    private FloatingActionButton fab_post, fab_Media;
    private TextView tv_UserName, tv_bio, tv_CreatedAtD, tv_Body, tv_Seen, tv_Likes, tv_videourl, tv_postid, tv_follow;
    CardView cv_PostedImage, cv_PostedVideo;
    Button bt_comment;
    RelativeLayout rl_comment;
    SimpleExoPlayer simpleExoPlayer;
    private CommentsAdapter mAdapter;
    PlayerView vv_PostedVideo;
    RecyclerView rv_comment;
    ProgressBar pb_Loading, pb_Comments;
    SharedPreferences userPreferences;
    // Shared Preferences reference for retrieving username
    public static final String UserPREFERENCES = "user";
    // Key for username retrieval (make variable public to access from outside)
    public static final String User = "user";
    String username;

    //retrieving profile picture
    // Key for profile_picture url (make variable public to access from outside)
    public static final String Name = "nameKey";
    PreferenceManager preferenceManager;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences preferences;
    String profile_picture;
    //url to uploading comment
    public static final String UPLOAD_COMMENT_URL = "http://192.168.137.1:80/geekbook/upload_comment.php";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        iv_PostedImage = findViewById(R.id.iv_PostedImage);
        bt_comment = findViewById(R.id.bt_comment);
        et_Comment = findViewById(R.id.et_Comment);
        tv_UserName = findViewById(R.id.tv_UserName);
        tv_bio = findViewById(R.id.tv_bio);
        tv_CreatedAtD = findViewById(R.id.tv_CreatedAtD);
        tv_Body = findViewById(R.id.tv_Body);
        rv_comment = findViewById(R.id.rv_comment);
        cv_PostedVideo = findViewById(R.id.cv_PostedVideo);
        cv_PostedImage = findViewById(R.id.cv_PostedImage);
        iv_ProfileImage = findViewById(R.id.iv_ProfileImage);
        tv_videourl = findViewById(R.id.tv_videourl);
        tv_postid = findViewById(R.id.tv_postid);
        pb_Loading = findViewById(R.id.pb_Loading);
        rl_comment = findViewById(R.id.rl_comment);
        tv_Seen = findViewById(R.id.tv_Seen);
        tv_Likes = findViewById(R.id.tv_Likes);
        iv_Likes = findViewById(R.id.iv_Likes);
        rl_comment = findViewById(R.id.rl_comment);
        tv_follow = findViewById(R.id.tv_follow);
        pb_Comments = findViewById(R.id.pb_Comments);

        rv_comment.setHasFixedSize(true);
        rv_comment.setLayoutManager(new LinearLayoutManager(CommentActivity.this));

        Intent i = getIntent();
        if (Objects.equals(i.getStringExtra("video"), "") && !Objects.equals(i.getStringExtra("image"), "")) {
            String text = i.getStringExtra("text");
            String image = i.getStringExtra("image");
            String bio = i.getStringExtra("bio");
            String postid = i.getStringExtra("postid");
            String dp = i.getStringExtra("dp");
            String time = i.getStringExtra("time");
            String username = i.getStringExtra("username");
            String likes = i.getStringExtra("likes");
            String seen = i.getStringExtra("seen");
            String likee = likes.substring(0,1);
            int like = Integer.parseInt(likee);

            tv_postid.setText(postid);
            tv_UserName.setText(username);
            tv_bio.setText(bio);
            tv_Seen.setText(seen);
            if(like != 0) {
                tv_Likes.setText(likes);
                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
            }
            else {
                tv_Likes.setText(likes);
                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_clear_black_svg));
            }
            Glide.with(getApplicationContext()).load(dp)
                    .placeholder(R.drawable.bio_back)
                    .error(R.drawable.bio_back)
                    .into(iv_ProfileImage);
            tv_CreatedAtD.setText(time);
            tv_Body.setText(text);
            PlayerView playerView = findViewById(R.id.vv_PostedVideo);
            cv_PostedVideo.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            cv_PostedImage.setVisibility(View.VISIBLE);
            iv_PostedImage.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(image)
                    .placeholder(R.drawable.ic_picture_placeholder_svg)
                    .error(R.drawable.ic_picture_placeholder_svg)
                    .into(iv_PostedImage);

        } else if ((Objects.equals(i.getStringExtra("image"), "") && !Objects.equals(i.getStringExtra("video"), ""))) {
            String video = i.getStringExtra("video");
            String text = i.getStringExtra("text");
            String bio = i.getStringExtra("bio");
            String postid = i.getStringExtra("postid");
            String dp = i.getStringExtra("dp");
            String time = i.getStringExtra("time");
            String username = i.getStringExtra("username");
            String likes = i.getStringExtra("likes");
            String seen = i.getStringExtra("seen");
            String likee = likes.substring(0,1);
            int like = Integer.parseInt(likee);

            if(like != 0) {
                tv_Likes.setText(likes);
                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
            }
            else {
                tv_Likes.setText(likes);
                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_clear_black_svg));
            }


            tv_postid.setText(postid);
            tv_UserName.setText(username);
            tv_bio.setText(bio);
            Glide.with(getApplicationContext()).load(dp)
                    .placeholder(R.drawable.bio_back)
                    .error(R.drawable.bio_back)
                    .into(iv_ProfileImage);
            tv_CreatedAtD.setText(time);
            tv_Body.setText(text);
            tv_Seen.setText(seen);

            PlayerView playerView = findViewById(R.id.vv_PostedVideo);
            cv_PostedImage.setVisibility(View.GONE);
            iv_PostedImage.setVisibility(View.GONE);
            cv_PostedVideo.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.VISIBLE);
            //Exo Player New
            simpleExoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
            playerView.setPlayer(simpleExoPlayer);
            HttpProxyCacheServer proxyServer = new HttpProxyCacheServer.Builder(getApplicationContext()).maxCacheSize(1024 * 1024 * 1024).build();
            String proxyURL = proxyServer.getProxyUrl(video);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                    Util.getUserAgent(getApplicationContext(), getApplicationContext().getPackageName()));
            simpleExoPlayer.prepare(new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(proxyURL)));
            // Prepare the player with the source.
            simpleExoPlayer.getPlaybackState();
            simpleExoPlayer.getBufferedPosition();
            simpleExoPlayer.setPlayWhenReady(false);

        } else if (Objects.equals(i.getStringExtra("video"), "") && (Objects.equals(i.getStringExtra("image"), ""))) {
            String text = i.getStringExtra("text");
            String bio = i.getStringExtra("bio");
            String postid = i.getStringExtra("postid");
            String dp = i.getStringExtra("dp");
            String time = i.getStringExtra("time");
            String username = i.getStringExtra("username");
            String likes = i.getStringExtra("likes");
            String seen = i.getStringExtra("seen");
            String likee = likes.substring(0,1);
            int like = Integer.parseInt(likee);

            if(like != 0) {
                tv_Likes.setText(likes);
                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
            }
            else {
                tv_Likes.setText(likes);
                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_clear_black_svg));
            }



            tv_UserName.setText(username);
            tv_bio.setText(bio);
            tv_postid.setText(postid);
            tv_Seen.setText(seen);
            Glide.with(getApplicationContext()).load(dp)
                    .placeholder(R.drawable.bio_back)
                    .error(R.drawable.bio_back)
                    .into(iv_ProfileImage);
            tv_CreatedAtD.setText(time);
            PlayerView playerView = findViewById(R.id.vv_PostedVideo);
            cv_PostedImage.setVisibility(View.GONE);
            iv_PostedImage.setVisibility(View.GONE);
            cv_PostedVideo.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            tv_Body.setText(text);
        }
        // Preference class instance for username retrieval
        userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
        username = userPreferences.getString(User, "");

        // Preference class instance for profile picture url retrieval
        preferenceManager = new PreferenceManager(getApplicationContext());
        preferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        profile_picture = preferences.getString(Name, "");


        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = et_Comment.getText().toString();
                if(comment.equals("")){
                    bt_comment.setEnabled(false);
                    }
                else {
                    uploadComments();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rl_comment.getWindowToken(), 0);


                }
            }
        });
        //Show done button on phone keyboard
        bt_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromInputMethod(rl_comment.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        rv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Comment.setVisibility(View.INVISIBLE);
                bt_comment.setVisibility(View.INVISIBLE);
            }
        });

        rl_comment.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
               // et_Comment.setVisibility(View.INVISIBLE);
               // bt_comment.setVisibility(View.INVISIBLE);
            }
        });

        String postid = tv_postid.getText().toString();

        iv_Likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tv_UserName.getText().toString();
                if (name.equals(username)) {
                    Toast.makeText(getApplicationContext(), "Can't Like your own post", Toast.LENGTH_LONG).show();
                } else {
                    iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                    StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "http://192.168.137.1:80/geekbook/post_likes.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray array = jsonObject.getJSONArray("hi");
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject ob = array.getJSONObject(i);
                                            String likescount = ob.getString("likes");
                                            int likes = Integer.parseInt(likescount);
                                            String zeroes = String.valueOf(likes);

                                            if (likes == 0) {
                                                tv_Likes.setText(zeroes);
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_clear_black_svg));
                                            } else if (likes >= 0 && likes <= 99) {
                                                String tens = String.valueOf(likes);
                                                tv_Likes.setText(tens);
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            } else if (likes >= 100 && likes <= 999) {
                                                String hundreds = String.valueOf(likes);
                                                tv_Likes.setText(hundreds);
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            } else if (likes >= 1000 && likes <= 9999) {
                                                String first_thousands = String.valueOf(likes);
                                                first_thousands = first_thousands.substring(0,1);
                                                tv_Likes.setText(first_thousands);
                                                tv_Likes.setText(String.format("%sK", first_thousands));
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            } else if (likes >= 10000 && likes <= 99999) {
                                                String ten_thousands = String.valueOf(likes);
                                                ten_thousands = ten_thousands.substring(0,2);
                                                tv_Likes.setText(ten_thousands);
                                                tv_Likes.setText(String.format("%sK", ten_thousands));
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            } else if (likes >= 100000 && likes <= 999999) {
                                                String hundred_thousands = String.valueOf(likes);
                                                hundred_thousands = hundred_thousands.substring(0,3);
                                                tv_Likes.setText(String.format("%sK", hundred_thousands));
                                                tv_Likes.setText(String.format(hundred_thousands));
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            } else if (likes >= 1000000 && likes <= 9999999) {
                                                String millions = String.valueOf(likes);
                                                millions = millions.substring(0,1);
                                                tv_Likes.setText(String.format("%sM", millions));
                                                tv_Likes.setText(String.format(millions));
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            } else if (likes >= 10000000 && likes <= 99999999) {
                                                String ten_millions = String.valueOf(likes);
                                                ten_millions = ten_millions.substring(0,2);
                                                tv_Likes.setText(String.format("%sM", ten_millions));
                                                tv_Likes.setText(String.format(ten_millions));
                                                iv_Likes.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_heart_solid_black_svg));
                                            }


                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
                            String postid = tv_postid.getText().toString();

                            userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                            String follower = userPreferences.getString(User, "");
                            String followed = tv_UserName.getText().toString();
                            // Preference class instance for profile picture url retrieval
                            preferenceManager = new PreferenceManager(getApplicationContext());
                            preferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            String followed_profile_pic = preferences.getString(Name,  "");

                            params.put("follower", follower);
                            params.put("followed", followed);
                            params.put("postid", postid);
                            params.put("followed_profile_pic", followed_profile_pic);
                            return params;
                        }
                    };

                    //To avoid sending twice when internet speed is slow
                    requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    AppController.getInstance(getApplicationContext()).addToRequestQueue(requestPostResponse);
                }
            }




        });

        iv_PostedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), ViewImageActivity.class);
                String image = i.getStringExtra("image");
                String time = tv_CreatedAtD.getText().toString();
                String username = tv_UserName.getText().toString();
                String type = "Picture";
                in.putExtra("image", image);
                in.putExtra("time", time);
                in.putExtra("username", username);
                in.putExtra("type", type);
                startActivity(in);

            }
        });
        iv_ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), ViewImageActivity.class);
                String image = i.getStringExtra("dp");
                String username = tv_UserName.getText().toString();
                String time = tv_bio.getText().toString();
                String type = "Profile";
                in.putExtra("image", image);
                in.putExtra("time", time);
                in.putExtra("username", username);
                in.putExtra("type", type);
                startActivity(in);

            }
        });


        new getComments(postid).execute();


    }

    private void uploadComments() {
        class uploadMediaText extends AsyncTask<String, Void, String> {


            ImageRequestHandler rh = new ImageRequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb_Loading.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pb_Loading.setVisibility(ProgressBar.INVISIBLE);

                if (s.equals("")) {
                    Toast.makeText(getApplicationContext(), "Could not upload comment", Toast.LENGTH_LONG).show();

                } else if (s.contains("Successfully Uploaded")) {
                  //  Toast.makeText(getApplicationContext(), "Comment Uploaded", Toast.LENGTH_LONG).show();
                    String postid = tv_postid.getText().toString();
                    new getComments(postid).execute();

                } else if (s.contains("Error")) {
                  //  Toast.makeText(getApplicationContext(), "Error uploading comment", Toast.LENGTH_LONG).show();


                }
            }

            @Override
            protected String doInBackground(String... params) {
                String comment = et_Comment.getText().toString();
                Intent i = getIntent();
                String bio = i.getStringExtra("bio");
                String postid = i.getStringExtra("postid");
                String recipient = tv_UserName.getText().toString();

                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("comment", comment);
                data.put("profile_picture", profile_picture);
                data.put("bio", bio);
                data.put("postid", postid);
                data.put("recipient", recipient);

                return rh.sendPostRequest(UPLOAD_COMMENT_URL, data);
            }
        }
        uploadMediaText ui = new uploadMediaText();
        ui.execute();
    }


    private class getComments extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public getComments(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_Comments.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://192.168.137.1:80/geekbook/all_comments.php");

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

            pb_Comments.setVisibility(ProgressBar.INVISIBLE);
            List<Comment> data = new ArrayList<>();

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("feed");

                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    Comment comment = new Comment();
                    comment.setCommentid(json_data.getString("commentid"));
                    comment.setUsername(json_data.getString("username"));
                    comment.setBio(json_data.getString("bio"));
                    comment.setTime(json_data.getString("time_created"));
                    comment.setText(json_data.getString("comment"));
                    comment.setDp(json_data.getString("profile_picture"));
                    data.add(comment);
                }


                // Setup and Handover data to recyclerview
                mAdapter = new CommentsAdapter(getApplicationContext(), data);
                rv_comment.setAdapter(mAdapter);
                rv_comment.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                et_Comment.setText("");


            } catch (JSONException e) {
              //  Toast.makeText(getApplicationContext(), "Error fetching Geek Comments", Toast.LENGTH_SHORT).show();


            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlayer(simpleExoPlayer);

    }

    @Override
    public void onStop() {

        super.onStop();
        pausePlayer(simpleExoPlayer);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        releaseExoPlayer(simpleExoPlayer);
    }

    @Override
    public void onResume() {

        super.onResume();
        startPlayer(simpleExoPlayer);
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
