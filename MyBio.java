package com.etini.geekleem.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.etini.geekleem.R;
import com.etini.geekleem.utils.AppController;
import com.etini.geekleem.utils.ImageRequestHandler;
import com.etini.geekleem.utils.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyBioActivity extends AppCompatActivity {
    Button bt_done;
    TextView tv_welcome;
    TextView tv_CharacterCount;
    FloatingActionButton fab_choosepic;
    EditText et_biodetail;
    RelativeLayout rl_bio1;
    ProgressBar pb_bio;
    private CircleImageView circleImageView;
    String profile_picture;
    //"https://geekleem.com.ng/signup_user.php",
    //http://192.168.137.1:80/geekbook
    public static final String UPLOAD_URL = "https://geekleem.com.ng/upload_profile_picture.php";
    public static final String URL_GET_DETAILS = "https://geekleem.com.ng/get_profilepicture.php";
    public static final String UPLOAD_KEY = "image";
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    String username;

    // Preference Manager Class
    PreferenceManager preferenceManager;
    SharedPreferences preferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";

    //Shared preferences to store bio information
    SharedPreferences bioPreferences;
    public static final String BioPREFERENCES = "BioPrefs" ;
    public static final String Bio = "bioKey";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_bio);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        // Preference class instance
        preferenceManager = new PreferenceManager(getApplicationContext());
       //preference for storing image url
        preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //preference for storing bio details
        bioPreferences = getSharedPreferences(BioPREFERENCES, Context.MODE_PRIVATE);



        Intent i = getIntent();
        username = i.getStringExtra("username");

        tv_welcome = findViewById(R.id.tv_welcome);
        bt_done = findViewById(R.id.bt_done);
        circleImageView = findViewById(R.id.civ_biopic);
        fab_choosepic = findViewById(R.id.fab_choosepic);
        et_biodetail = findViewById(R.id.et_biodetail);
        rl_bio1 = findViewById(R.id.rl_bio1);
        tv_CharacterCount = findViewById(R.id.tv_CharacterCount);
        pb_bio = findViewById(R.id.pb_bio);
        pb_bio.setVisibility(View.GONE);

        rl_bio1.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           tv_CharacterCount.setVisibility(View.VISIBLE);
                                       }
                                   });

        tv_welcome.setText(String.format("Welcome to GeekLeem %s", username));

        et_biodetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_CharacterCount.setVisibility(View.VISIBLE);
            }
        });

        et_biodetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = 50 - s.length();
                tv_CharacterCount.setText(Integer.toString(length));

                int redLight = ContextCompat.getColor(getApplicationContext(), R.color.colorRedLight);
                int redDark = ContextCompat.getColor(getApplicationContext(), R.color.colorRed);
                int blueLight = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDarkLight);
                int whiteColor = ContextCompat.getColor(getApplicationContext(), R.color.colorWhite);

                if (length < 20) {
                    tv_CharacterCount.setTextColor(redLight);
                    if (length < 0) {
                        fab_choosepic.setEnabled(false);
                        tv_CharacterCount.setTextColor(redDark);

                    } else {
                        fab_choosepic.setEnabled(true);

                    }
                } else {
                    tv_CharacterCount.setTextColor(whiteColor);
                }
            }
        });


        fab_choosepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bio = et_biodetail.getText().toString();
                if (bio.equals("")) {
                    Snackbar snackbar = Snackbar.make(rl_bio1, "Please enter your Geek Bio details first!", Snackbar.LENGTH_LONG).setTextColor(getResources().getColor(R.color.colorPrimaryDark)).setBackgroundTint(getResources().getColor(R.color.colorWhite));
                    snackbar.show();
                }
                else {
                   // pickFromGallery();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            }
        });
    }
   /** private void pickFromGallery() {
        CropImage.activity().start(MyBioActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.with(this).load(resultUri).into(circleImageView);
                uploadImage();
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
                circleImageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //encoding image as string
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ImageRequestHandler rh = new ImageRequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb_bio.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pb_bio.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Successfully Uploaded",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getDetails();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                String bio = et_biodetail.getText().toString();
                SharedPreferences.Editor editor = bioPreferences.edit();
                editor.putString(Bio, bio);
                editor.apply();

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("username", username);
                data.put("bio", bio);

                return rh.sendPostRequest(UPLOAD_URL, data);
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    private  void getDetails(){
        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, URL_GET_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("notify");
                    for (int i=0; i<array.length(); i++){

                        JSONObject json_data=array.getJSONObject(i);
                        profile_picture = json_data.getString("profile_picture");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Name, profile_picture);
                        editor.commit();

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
                params.put("username", username);
                return params;
            }
        };
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(35000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(getApplicationContext()).addToRequestQueue(requestPostResponse);
    }


    private File getOutputFile(){
        File dir = new File(Environment.getExternalStorageDirectory().toString(),"GeekLeem" + "ddd");
        if(!dir.exists()){
            dir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File imageFile = new File(dir.getPath() + File.separator+ "Profile_Picture" + timeStamp + ".jpg");
        return imageFile;
    }

    //to add saved image to gallery
    private static void addImageTOGallery(final String filePath,final Context context){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        values.put(MediaStore.MediaColumns.DATA,filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
    }

    @Override
    public void onBackPressed(){


    }
}
