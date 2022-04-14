package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etini.geekleem.R;
import com.etini.geekleem.utils.ImageRequestHandler;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class SubcommentActivity extends AppCompatActivity {
    TextView tv_replyingto;
    EditText et_subcomment;
    Button bt_subcomment;
    ProgressBar pb_Loading;
    String commenter;
    String commentid;
    RelativeLayout rl_layout;
    public static final String UPLOAD_SUBCOMMENT_URL = "http://192.168.137.1:80/geekbook/upload_subcomment.php";
    SharedPreferences userPreferences;
    // Shared Preferences reference for retrieving username
    public static final String UserPREFERENCES = "user";
    // Key for username retrieval (make variable public to access from outside)
    public static final String User = "user";
    String subcommenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcomment);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        tv_replyingto = findViewById(R.id.tv_replyingto);
        et_subcomment = findViewById(R.id.et_subcomment);
        bt_subcomment = findViewById(R.id.bt_subcomment);
        rl_layout = findViewById(R.id.rl_layout);
        pb_Loading = findViewById(R.id.pb_Loading);

        Intent i = getIntent();
        commenter = i.getStringExtra("commenter");
        commentid = i.getStringExtra("commentid");
        tv_replyingto.setText(String.format("Replying to Geek_%s", commenter));

        // Preference class instance for username retrieval
        userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
        subcommenter = userPreferences.getString(User, "");


       bt_subcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String text = et_subcomment.getText().toString();
                    if (text.isEmpty())
                    {
                        Snackbar snackbar = Snackbar.make(rl_layout, "Please Write a Reply to Geek_" +commenter , Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorAccent));
                        snackbar.show();
                    }
                    else{
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(rl_layout.getWindowToken(), 0);
                        uploadComments();
                    }

                }
        });




    }
    private void uploadComments() {
        class uploadMediaText extends AsyncTask<String, Void, String> {


            ImageRequestHandler rh = new ImageRequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb_Loading.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pb_Loading.setVisibility(ProgressBar.INVISIBLE);

                if (s.equals("")) {
                    Snackbar snackbar = Snackbar.make(rl_layout, "Could not upload reply" , Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorAccent));
                    snackbar.show();
                } else if (s.contains("Successfully Uploaded")) {
                    finishActivity(1);
                } else if (s.contains("Error")) {
                    Snackbar snackbar = Snackbar.make(rl_layout, "An error occurred" , Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorAccent));
                    snackbar.show();

                }
            }

            @Override
            protected String doInBackground(String... params) {
                String comment = et_subcomment.getText().toString();
                Intent i = getIntent();
                commenter = i.getStringExtra("commenter");
                commentid = i.getStringExtra("commentid");

                HashMap<String, String> data = new HashMap<>();
                data.put("commenter", commenter);
                data.put("subcommenter", subcommenter);
                data.put("comment", comment);
                data.put(" commentid",  commentid);

                return rh.sendPostRequest(UPLOAD_SUBCOMMENT_URL, data);
            }
        }
        uploadMediaText ui = new uploadMediaText();
        ui.execute();
    }


}
