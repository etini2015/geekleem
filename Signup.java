package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.etini.geekleem.utils.AppController;
import com.etini.geekleem.utils.AppConfig;
import com.google.android.material.snackbar.Snackbar;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    Button bt_register;
    EditText et_fullname, et_username, et_email, et_password, et_phonenumber;
    TextView tv_userlogin;
    ImageView iv_backarrow;
    RelativeLayout rl_signup;
    private ProgressWheel progressWheelInterpolated;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ActionBar actionBar = getSupportActionBar();
        //this.context = context;
        actionBar.hide();

        //Widget Initialization
        bt_register = findViewById(R.id.bt_register);
        tv_userlogin = findViewById(R.id.tv_userlogin);
        iv_backarrow = findViewById(R.id.iv_backarrow);
        et_fullname = findViewById(R.id. et_fullname);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_phonenumber = findViewById(R.id.et_phonenumber);
        rl_signup = (RelativeLayout) findViewById(R.id.rl_signup);
        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.register_progress_wheel);

        //Back Arrow goes back to Log in Screen
        iv_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Show done button on keyboard
        et_phonenumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rl_signup.getWindowToken(), 0);

                    geekRegister();
                    return true;
                }
                return false;
            }
        });
        //Register button code
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rl_signup.getWindowToken(), 0);

                geekRegister();
            }
        });



    }

    public void geekRegister() {
        final String fullname = et_fullname.getText().toString();
        final String username = et_username.getText().toString();
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();
        final String phonenumber = et_phonenumber.getText().toString();


        if (fullname.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your two names Example: Elon Musk", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (fullname.length() < 3) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Your full names must not be less than 3 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (email.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your email address", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));

            snackbar.show();

        }else if (email.length() < 8) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Your email must not be less than 10 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (!AppConfig.isEmailValid(email)) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Email format required: yourname@gmail.com", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (username.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your username", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (username.length() < 5) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Username must not be less than 5 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();
        }else if (username.length() > 20) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Username must not be more than 20 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (password.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your password", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (password.length() < 8) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Password must not be less than 8 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();
        }else {
            bt_register.setEnabled(false);
            bt_register.setText("Please Wait");
            //Sending Register Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);//https://geekleem.com.ng/signup_user.php

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "https://geekleem.com.ng/signup_user.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {
                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                               Intent intent = new Intent(SignupActivity.this, MyBioActivity.class);
                               intent.putExtra("username", username);
                                startActivity(intent);

                            }else if (getAnswer.contains("Username Exists")) {
                                Snackbar snackbar = Snackbar.make(rl_signup, "Username Exists", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
                                snackbar.show();
                                bt_register.setEnabled(true);
                                bt_register.setText(R.string.register);

                            }else if (getAnswer.contains("Email Exists")) {
                                Snackbar snackbar = Snackbar.make(rl_signup, "Email Address Exists", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
                                snackbar.show();
                                bt_register.setEnabled(true);
                                bt_register.setText(R.string.register);
                            }else {
                                    Toast.makeText(getApplicationContext(), "Failed to Connect", Toast.LENGTH_LONG).show();
                                    bt_register.setEnabled(true);
                                    bt_register.setText(R.string.register);
                        }
                            progressWheelInterpolated.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "An Error Occurred", Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            bt_register.setEnabled(true);
                            bt_register.setText(R.string.register);
                        }
                    }
            ){
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("fullname", fullname);
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("phonenumber", phonenumber);
                    //params.put("user_device_type_id","2");

                    return params;
                }
            };

            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance(SignupActivity.this).addToRequestQueue(requestPostResponse);
        }
    }

    @Override
        public void onBackPressed(){
            finishAffinity();

        }
}
