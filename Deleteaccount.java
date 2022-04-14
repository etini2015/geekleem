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
import com.etini.geekleem.R;
import com.etini.geekleem.utils.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class DeleteAccountActivity extends AppCompatActivity {
    TextView user_signup, et_username, et_password;
    Button bt_login;
    RelativeLayout rl_login;
    private ProgressBar pB_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //Widgets
        user_signup = findViewById(R.id.tv_signup);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        rl_login = findViewById(R.id.rl_login);
        pB_delete = findViewById(R.id.pB_delete);


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                geekDelete();

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

                    geekDelete();
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

                geekDelete();

            }
        });

    }

    public void geekDelete() {
        String username = et_username.getText().toString();
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
            pB_delete.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "https://geekleem.com.ng/delete_account.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {
                                Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DeleteAccountActivity.this, SplashActivity.class);
                                startActivity(intent);


                            } else if (getAnswer.contains("Failed")) {
                                Snackbar snackbar = Snackbar.make(rl_login, "Account Deletion Failed", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
                                snackbar.show();
                                bt_login.setEnabled(true);
                                bt_login.setText("DELETE ACCOUNT");

                            } else {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                bt_login.setEnabled(true);
                                bt_login.setText("DELETE ACCOUNT");
                            }
                            pB_delete.setVisibility(View.GONE);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "An Error Occurred! Try Again", Toast.LENGTH_LONG).show();
                            pB_delete.setVisibility(View.GONE);
                            bt_login.setEnabled(true);
                            bt_login.setText("DELETE ACCOUNT");
                        }
                    }
            ) {
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();
                    params.put("username", username);
                    params.put("password", password);

                    return params;
                }
            };

            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(DeleteAccountActivity.this).addToRequestQueue(requestPostResponse);
        }
    }}
