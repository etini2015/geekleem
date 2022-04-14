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
import com.etini.geekleem.utils.AppConfig;
import com.etini.geekleem.utils.AppController;
import com.google.android.material.snackbar.Snackbar;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {
    Button bt_register;
    EditText tv_userlogin, et_fullname, et_username, et_email, et_password, et_phonenumber, et_passwordb;
    ImageView iv_backarrow;
    RelativeLayout rl_signup;
    private ProgressWheel progressWheelInterpolated;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ActionBar actionBar = getSupportActionBar();
        //this.context = context;
        actionBar.hide();
        //Widget Initialization
        bt_register = findViewById(R.id.bt_register);
        iv_backarrow = findViewById(R.id.iv_backarrow);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_passwordb = findViewById(R.id.et_passwordb);
        rl_signup = (RelativeLayout) findViewById(R.id.rl_signup);
        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.register_progress_wheel);

        //Back Arrow goes back to Log in Screen
        iv_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        //Show done button on keyboard
        et_passwordb.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rl_signup.getWindowToken(), 0);

                    updatePassword();
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

                updatePassword();
            }
        });
    }

    public void updatePassword() {
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();
        final String passwordb = et_passwordb.getText().toString();




        if (email.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your email address", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (email.length() < 8) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Your email must not be less than 10 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (!AppConfig.isEmailValid(email)) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Email format required: yourname@zuzu.com", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (password.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your password", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (password.length() < 8) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Password must not be less than 8 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();
        }
        else if (passwordb.equals("")) {
        Snackbar snackbar = Snackbar.make(rl_signup, "Please enter your matching password", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
        snackbar.show();

       }else if (passwordb.length() < 8) {
        Snackbar snackbar = Snackbar.make(rl_signup, "Password must not be less than 8 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
        snackbar.show();
      }
      else if (!passwordb.equals(password)) {
            Snackbar snackbar = Snackbar.make(rl_signup, "Passwords do not match", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();
        }else {
            bt_register.setEnabled(false);
            bt_register.setText("Please Wait");
            //Sending Register Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "https://geekleem.com.ng/update_password.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {
                                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }else {
                                Toast.makeText(getApplicationContext(), "Failed to Connect", Toast.LENGTH_LONG).show();
                                bt_register.setEnabled(true);
                                bt_register.setText("Reset Password");
                            }
                            progressWheelInterpolated.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"An Error Occurred! Try Again", Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            bt_register.setEnabled(true);
                            bt_register.setText("Reset Password");
                        }
                    }
            ){
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("email", email);
                    params.put("password", password);

                    return params;
                }
            };

            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance(ResetPasswordActivity.this).addToRequestQueue(requestPostResponse);
        }
    }

    @Override
    public void onBackPressed(){
        finishAffinity();

    }
}
