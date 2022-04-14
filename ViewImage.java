package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etini.geekleem.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewImageActivity extends AppCompatActivity {
TextView tv_time, tv_username;
ImageView iv_ViewImage;
CircleImageView civ_ViewImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        //widgets
        iv_ViewImage = findViewById(R.id.iv_ViewImage);
        tv_time = findViewById(R.id.tv_time);
        tv_username = findViewById(R.id.tv_username);
        civ_ViewImage = findViewById(R.id.civ_ViewImage);

        final Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom);
        iv_ViewImage.startAnimation(zoomAnimation);




        Intent i = getIntent();
        String image = i.getStringExtra("image");
        String time = i.getStringExtra("time");
        String username = i.getStringExtra("username");
        String type = i.getStringExtra("type");

        if(type.equals("Profile")) {
            tv_time.setText(time);
            tv_username.setText(username);
            civ_ViewImage.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(image)
                    .error(R.drawable.ic_picture_placeholder_svg)
                    .into(iv_ViewImage);
        }
        else if(type.equals("Picture")) {
            tv_time.setText(time);
            tv_username.setText(username);
            civ_ViewImage.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(image)
                    .error(R.drawable.ic_picture_placeholder_svg)
                    .into(iv_ViewImage);
        }

    }
}
