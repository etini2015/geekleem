package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.etini.geekleem.R;
import com.etini.geekleem.utils.GetJson;
import com.etini.geekleem.utils.PreferenceManager;
import com.etini.geekleem.utils.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView user_signup, et_username, et_password, tv_userlogin;
    Button bt_login;
    RelativeLayout rl_login;
    private ProgressWheel progressWheelInterpolated;
    // Preference Manager Class
    PreferenceManager preferenceManager;
    SharedPreferences preferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public GetJson getjsonobj;
    String username;
    SharedPreferences userPreferences;
    // Shared Preferences reference for retrieving username
    public static final String UserPREFERENCES = "user";
    String profile_picture;

    // Key for username retrieval (make variable public to access from outside)
    public static final String User = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        // Preference class instance
        preferenceManager = new PreferenceManager(getApplicationContext());
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // Preference class instance for username storage
        userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);

        //Widgets
        user_signup = findViewById(R.id.tv_signup);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        rl_login = findViewById(R.id.rl_login);
        tv_userlogin = findViewById(R.id.tv_userlogin);
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.login_progress_wheel);

        username = et_username.getText().toString();

        user_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));


            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                et_username.getText();
                et_password.getText();

            }
        });
        //Show done button on phone keyboard
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rl_login.getWindowToken(), 0);

                    geekLogin();
                    return true;
                }
                return false;
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hidden the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rl_login.getWindowToken(), 0);

                geekLogin();

            }
        });

       tv_userlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));


            }
        });

    }

    public void geekLogin() {
        username = et_username.getText().toString();
        final String password = et_password.getText().toString();

        if (username.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_login, "Please enter your username", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        } else if (username.length() < 5) {
            Snackbar snackbar = Snackbar.make(rl_login, "Username must not be less than 5 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        } else if (password.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_login, "Please enter your password", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        } else if (password.length() < 6) {
            Snackbar snackbar = Snackbar.make(rl_login, "Password must not be less than 6 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        } else {
            bt_login.setEnabled(false);
            bt_login.setText("Please Wait");
            //Send Login Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);//http://192.168.137.1:80/geekbook

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "http://192.168.137.1:80/geekbook/login_user.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {

                                // Creating user login session
                                preferenceManager.createLoginSession(username, password);
                                SharedPreferences.Editor editor = userPreferences.edit();
                                editor.putString(User, username);
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "Log In Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeSplashActivity.class);
                                startActivity(intent);

                            } else if (getAnswer.contains("Failed")) {
                                Snackbar snackbar = Snackbar.make(rl_login, "Log In Failed", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
                                snackbar.show();
                                bt_login.setEnabled(true);
                                bt_login.setText("LOG IN");

                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to Connect", Toast.LENGTH_LONG).show();
                                bt_login.setEnabled(true);
                                bt_login.setText("LOG IN");
                            }
                            progressWheelInterpolated.setVisibility(View.GONE);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "An Error Occurred! Try Again", Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            bt_login.setEnabled(true);
                            bt_login.setText("LOG IN");
                        }
                    }
            ) {
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", username);
                    params.put("password", password);

                    return params;
                }
            };

            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(requestPostResponse);
        }
    }


    @Override
    public void onBackPressed(){
        finishAffinity();

    }
}
