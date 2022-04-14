package com.etini.geekleem.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etini.geekleem.R;

public class HomeSplashActivity extends AppCompatActivity {
    Animation anim;
    TextView tv_homesplash, geekleem;
    ProgressBar pb_homesplash;
    ImageView imageView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        pb_homesplash = findViewById(R.id.pb_homesplash);
        imageView8 = findViewById(R.id.imageView8);
        geekleem = findViewById(R.id.geekleem);


       anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in); // Create the animation.
       anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Intent intent = new Intent(HomeSplashActivity.this, HomeActivity.class);
                startActivity(intent);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
       // imageView8.startAnimation(anim);
        geekleem.startAnimation(anim);
    }
}

