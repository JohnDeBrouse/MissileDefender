package com.example.missileDefender;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    // This is a timed introduction screen

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 4000;
    ImageView title;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        title = findViewById(R.id.imageView2);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_base", R.raw.base_blast, false);
        SoundPlayer.getInstance().setupSound(this, "hit_missile", R.raw.interceptor_hit_missile, false);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile, false);
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        setupFullScreen();
        titleAnimation(title);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

                mediaPlayer.stop();
                finish();
                overridePendingTransition(0, 0);
            }
        }, SPLASH_TIME_OUT);

    }


    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    private void titleAnimation(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(4000);
        img.startAnimation(fadeOut);
    }
}