package com.etini.geekleem.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import com.etini.geekleem.R;
import com.etini.geekleem.utils.AppConfig;
import com.etini.geekleem.utils.AppController;
import com.etini.geekleem.utils.ImageRequestHandler;
import com.etini.geekleem.utils.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class EditProfileActivity extends AppCompatActivity {
    ImageView iv_backarrow;
    Button bt_register;
    EditText et_bio, et_fullname, et_username, et_email,  et_phonenumber;
    RelativeLayout rl_editprofile;
    TextView et_password;
    FloatingActionButton fab_editprofile;
    CircleImageView civ_editpic;
    TextView tv_CharacterCount;
    ProgressBar pb_editprofile;
    private ProgressWheel progressWheelInterpolated;
    public Context context;
    // Shared Preferences reference for retrieving username
    SharedPreferences userPreferences;
    public static final String UserPREFERENCES = "user";
    // Key for username retrieval (make variable public to access from outside)
    public static final String User = "user";
    String username;
    String profile_picture;
    private int PICK_IMAGE_REQUEST = 1;
    public static final String UPLOAD_URL = "https://geekleem.com.ng/update_profilepicture.php";
    public static final String UPLOAD_KEY = "image";

    public static final String URL_RETRIEVE_DETAILS = "https://geekleem.com.ng/retrieve_details.php";


    // Key for profile_picture url (make variable public to access from outside)
    public static final String Name = "nameKey";
    PreferenceManager preferenceManager;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences preferences;
    private Bitmap bitmap;
    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        iv_backarrow = findViewById(R.id.iv_backarrow);

        //Widget Initialization
        bt_register = findViewById(R.id.bt_register);
        et_bio = findViewById(R.id.et_bio);
        tv_CharacterCount = findViewById(R.id.tv_CharacterCount);
        iv_backarrow = findViewById(R.id.iv_backarrow);
        et_fullname = findViewById(R.id. et_fullname);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        pb_editprofile = findViewById(R.id.pb_editprofile);
        et_phonenumber = findViewById(R.id.et_phonenumber);
        fab_editprofile = findViewById(R.id.fab_choosepic);
        rl_editprofile = (RelativeLayout) findViewById(R.id.rl_editprofile);
        civ_editpic = findViewById(R.id.civ_editpic);
        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.register_progress_wheel);

        // Preference class instance for profile picture url retrieval
        preferenceManager = new PreferenceManager(getApplicationContext());
        preferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        profile_picture = preferences.getString(Name,  "");

        // Preference class instance for username retrieval
        userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
        username = userPreferences.getString(User,  "");


        et_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "To Change Password, Check Reset Password Page", Toast.LENGTH_LONG).show();
            }
        });


        //Show done button on keyboard
        et_phonenumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rl_editprofile.getWindowToken(), 0);

                    updateDetails();
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
                imm.hideSoftInputFromWindow(rl_editprofile.getWindowToken(), 0);

                updateDetails();
            }
        });
        fab_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pickFromGallery();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);



            }
        });
        et_bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = 70 - s.length();
                tv_CharacterCount.setText(Integer.toString(length));

                int redLight = ContextCompat.getColor(getApplicationContext(), R.color.colorRedLight);
                int redDark = ContextCompat.getColor(getApplicationContext(), R.color.colorRed);
                int blueLight = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDarkLight);
                int whiteColor = ContextCompat.getColor(getApplicationContext(), R.color.colorWhite);

                if (length < 20) {
                    tv_CharacterCount.setTextColor(redLight);
                    if (length < 0) {
                        bt_register.setEnabled(false);
                        tv_CharacterCount.setTextColor(redDark);

                    } else {
                        bt_register.setEnabled(true);

                    }
                } else {
                    bt_register.setTextColor(whiteColor);
                }
            }
        });
        getDetails();


    }
/**
    private void pickFromGallery() {
        CropImage.activity().start(EditProfileActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //Picasso.with(this).load(resultUri).into(civ_editpic);
               // uploadImage();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    civ_editpic.setImageBitmap(bitmap);
                    uploadImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        }*/



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                civ_editpic.setImageBitmap(bitmap);
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDetails() {
        final String fullname = et_fullname.getText().toString();
        final String username = et_username.getText().toString();
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();
        final String phonenumber = et_phonenumber.getText().toString();
        final String bio = et_bio.getText().toString();


        if (fullname.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Please enter your two names Example: Elon Musk", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (fullname.length() < 3) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Your full names must not be less than 3 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (email.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Please enter your email address", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (email.length() < 8) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Your email must not be less than 10 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (!AppConfig.isEmailValid(email)) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Email format required: yourname@gmail.com", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (username.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Please enter your username", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (username.length() < 5) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Username must not be less than 5 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (username.length() > 20) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Username must not be more than 20 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (password.equals("")) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Please enter your password", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }else if (password.length() < 8) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Password must not be less than 8 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();

        }
        else if (bio.length() > 70) {
            Snackbar snackbar = Snackbar.make(rl_editprofile, "Bio must not be more than 70 characters", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
            snackbar.show();
        }
        else {
            bt_register.setEnabled(false);
            bt_register.setText("Please Wait");
            //Sending Update Request
            progressWheelInterpolated.setVisibility(View.VISIBLE);

            StringRequest requestPostResponse = new StringRequest(Request.Method.POST, "https://geekleem.com.ng/update_details.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String getAnswer = response.toString();
                            if (getAnswer.contains("Success")) {
                                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                                // Logout
                                SharedPreferences.Editor editor = userPreferences.edit();
                                SharedPreferences.Editor edit = preferences.edit();
                                preferenceManager.logoutUser();
                                editor.clear();
                                edit.clear();
                                startActivity(intent);

                            }else {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                bt_register.setEnabled(true);
                                bt_register.setText("UPDATE");
                            }
                            progressWheelInterpolated.setVisibility(View.INVISIBLE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                            progressWheelInterpolated.setVisibility(View.GONE);
                            bt_register.setEnabled(true);
                            bt_register.setText("UPDATE");
                        }
                    }
            ){
                //To send our parameters
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    // Preference class instance for username retrieval
                    userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                    String oldusername = userPreferences.getString(User,  "");
                    params.put("fullname", fullname);
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("phonenumber", phonenumber);
                    params.put("bio", bio);
                    params.put("oldusername", oldusername);
                    return params;
                }
            };
            //To avoid sending twice when internet speed is slow
            requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance(EditProfileActivity.this).addToRequestQueue(requestPostResponse);
        }
    }
    private  void getDetails(){
        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, URL_RETRIEVE_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("notify");
                    for (int i=0; i<array.length(); i++){

                        JSONObject json_data=array.getJSONObject(i);

                        et_bio.setText(json_data.getString("bio"));
                        et_fullname.setText(json_data.getString("fullname"));
                        et_username.setText(json_data.getString("username"));
                        et_email.setText(json_data.getString("email"));
                        et_password.setText(json_data.getString("password"));
                        et_phonenumber.setText(json_data.getString("phonenumber"));
                        Glide.with(getApplicationContext()).load(json_data.getString("profile_picture"))
                                .error(R.drawable.ic_username)
                                .into(civ_editpic);
                        tv_CharacterCount.setVisibility(View.VISIBLE);
                        pb_editprofile.setVisibility(GONE);

                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Failed to get Details", Toast.LENGTH_LONG).show();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "An Error Occurred", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String>params=new HashMap<String, String>();
                userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                String username = userPreferences.getString(User, "");
                params.put("username", username);
                return params;
            }
        };
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(getApplicationContext()).addToRequestQueue(requestPostResponse);
    }
    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ImageRequestHandler rh = new ImageRequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                // Preference class instance for username retrieval
                userPreferences = getApplicationContext().getSharedPreferences(UserPREFERENCES, Context.MODE_PRIVATE);
                String username = userPreferences.getString(User,  "");
                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("username", username);


                return rh.sendPostRequest(UPLOAD_URL, data);
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    //encoding image as string
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
