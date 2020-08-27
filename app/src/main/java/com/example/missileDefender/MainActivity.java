package com.example.missileDefender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ConstraintLayout layout;
    public static int screenHeight;
    public static int screenWidth;
    public int missile;
    private MissileMaker missileMaker;
    private ImageView launcher;
    private ImageView launcher2;
    private ImageView launcher3;
    private int scoreValue;
    private TextView score, level;
    private int levelValue;
    MediaPlayer mediaPlayer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        missile = 0;
        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.levelText);
        launcher = findViewById(R.id.launcher);
        launcher2 = findViewById(R.id.launcher2);
        launcher3 = findViewById(R.id.launcher6);
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handleTouch(motionEvent.getX(), motionEvent.getY());
                }
                return false;
            }
        });
        setupFullScreen();
        getScreenDimensions();
        ViewGroup layout = findViewById(R.id.layout);
        new ParallaxBackground(this, layout, R.drawable.clouds, 60000);
        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    // Forces full screen in landscape
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


    public void incrementScore() {
        scoreValue++;
        score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void setLevel(final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                level.setText(String.format(Locale.getDefault(), "Level: %d", value));
                levelValue = value;
            }
        });
    }

    public void removeMissile(Missile p) {
        missileMaker.removeMissile(p);
    }



    //Handles which launcher should fire the missile based on closest and if launcher is destroyed or not
    public void handleTouch(float x1, float y1) {
        float difference = Math.abs(launcher.getX()-x1);
        float difference2 = Math.abs(launcher2.getX()-x1);
        float difference3 = Math.abs(launcher3.getX()-x1);
        double startX = 0;
        double startY = 0;

        if (launcher.getVisibility() == View.VISIBLE && launcher2.getVisibility() == View.VISIBLE && launcher3.getVisibility() == View.VISIBLE){
            if (difference < difference2 && difference < difference3){
                startX = launcher.getX() + (0.5 * launcher.getWidth())- 23;
                startY = launcher.getY() + (0.5 * launcher.getHeight());
            }
            else if (difference2 < difference && difference2 < difference3){
                startX = launcher2.getX() + (0.5 * launcher2.getWidth()) - 23;
                startY = launcher2.getY() + (0.5 * launcher2.getHeight());
            }
            else{
                startX = launcher3.getX() + (0.5 * launcher3.getWidth())- 23;
                startY = launcher3.getY() + (0.5 * launcher3.getHeight());
            }
        }
        else if (launcher.getVisibility() == View.VISIBLE && launcher2.getVisibility() == View.VISIBLE && launcher3.getVisibility() == View.INVISIBLE){
            if (difference < difference2 ){
                startX = launcher.getX() + (0.5 * launcher.getWidth())- 23;
                startY = launcher.getY() + (0.5 * launcher.getHeight());
            }
            else if (difference2 < difference ){
                startX = launcher2.getX() + (0.5 * launcher2.getWidth()) - 23;
                startY = launcher2.getY() + (0.5 * launcher2.getHeight());
            }
        }
        else if (launcher.getVisibility() == View.VISIBLE && launcher2.getVisibility() == View.INVISIBLE && launcher3.getVisibility() == View.VISIBLE){
            if (difference < difference3){
                startX = launcher.getX() + (0.5 * launcher.getWidth())- 23;
                startY = launcher.getY() + (0.5 * launcher.getHeight());
            }
            else{
                startX = launcher3.getX() + (0.5 * launcher3.getWidth())- 23;
                startY = launcher3.getY() + (0.5 * launcher3.getHeight());
            }
        }
        else if (launcher.getVisibility() == View.INVISIBLE && launcher2.getVisibility() == View.VISIBLE && launcher3.getVisibility() == View.VISIBLE){
             if (difference2 < difference3){
                startX = launcher2.getX() + (0.5 * launcher2.getWidth()) - 23;
                startY = launcher2.getY() + (0.5 * launcher2.getHeight());
            }
            else{
                startX = launcher3.getX() + (0.5 * launcher3.getWidth())- 23;
                startY = launcher3.getY() + (0.5 * launcher3.getHeight());
            }
        }
        else if (launcher.getVisibility() == View.VISIBLE && launcher2.getVisibility() == View.INVISIBLE && launcher3.getVisibility() == View.INVISIBLE){
                startX = launcher.getX() + (0.5 * launcher.getWidth())- 23;
                startY = launcher.getY() + (0.5 * launcher.getHeight());
        }
        else if (launcher.getVisibility() == View.INVISIBLE && launcher2.getVisibility() == View.INVISIBLE && launcher3.getVisibility() == View.VISIBLE){
                startX = launcher3.getX() + (0.5 * launcher3.getWidth())- 23;
                startY = launcher3.getY() + (0.5 * launcher3.getHeight());

        }
        else {
                startX = launcher2.getX() + (0.5 * launcher2.getWidth()) - 23;
                startY = launcher2.getY() + (0.5 * launcher2.getHeight());
        }



if (missile <3) {
    missile = missile + 1;
    Interceptor i = new Interceptor(this, (float) (startX - 10), (float) (startY - 30), x1, y1);
    SoundPlayer.getInstance().start("launch_interceptor");
    i.launch();
}
    }



    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        float x1 = interceptor.getX();
        float y1 = interceptor.getY();
        float x2 = (int) (launcher.getX() + (0.5 * launcher.getWidth()));
        float y2 = (int) (launcher.getY() + (0.5 * launcher.getHeight()));
        float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        float a2 = (int) (launcher2.getX() + (0.5 * launcher2.getWidth()));
        float b2 = (int) (launcher2.getY() + (0.5 * launcher2.getHeight()));
        float g = (float) Math.sqrt((b2 - y1) * (b2 - y1) + (a2 - x1) * (a2 - x1));
        float c2 = (int) (launcher3.getX() + (0.5 * launcher3.getWidth()));
        float d2 = (int) (launcher3.getY() + (0.5 * launcher3.getHeight()));
        float z = (float) Math.sqrt((d2 - y1) * (d2 - y1) + (c2 - x1) * (c2 - x1));
        if (f < 100){
            SoundPlayer.getInstance().start("interceptor_hit_base");
            launcher.setVisibility(View.INVISIBLE);
            gameOver();
        }
        else if (g < 100){
            SoundPlayer.getInstance().start("interceptor_hit_base");
            launcher2.setVisibility(View.INVISIBLE);
            gameOver();
        }
        else if ( z < 100){
            SoundPlayer.getInstance().start("interceptor_hit_base");
            launcher3.setVisibility(View.INVISIBLE);
            gameOver();
        }
        missileMaker.applyInterceptorBlast(interceptor, id);
    }

    public void decreaseMissles(){
        missile = missile -1;
    }

    public boolean baseDestructionCheck(float x1, float y1){
        float x2 = (int) (launcher.getX() + (0.5 * launcher.getWidth()));
        float y2 = (int) (launcher.getY() + (0.5 * launcher.getHeight()));
        float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        float a2 = (int) (launcher2.getX() + (0.5 * launcher2.getWidth()));
        float b2 = (int) (launcher2.getY() + (0.5 * launcher2.getHeight()));
        float c2 = (int) (launcher3.getX() + (0.5 * launcher3.getWidth()));
        float d2 = (int) (launcher3.getY() + (0.5 * launcher3.getHeight()));
        float g = (float) Math.sqrt((b2 - y1) * (b2 - y1) + (a2 - x1) * (a2 - x1));
        float z = (float) Math.sqrt((d2 - y1) * (d2 - y1) + (c2 - x1) * (c2 - x1));
        if (f < 100){
            if (launcher.getVisibility() == View.VISIBLE) {
                launcher.setVisibility(View.INVISIBLE);
                gameOver();
                return true;
            }

        }
        else if (g < 100){
            if (launcher2.getVisibility() == View.VISIBLE) {
                launcher2.setVisibility(View.INVISIBLE);
                gameOver();
                return true;
            }

        }
        else if ( z < 100){
            if (launcher3.getVisibility() == View.VISIBLE) {
                launcher3.setVisibility(View.INVISIBLE);
                gameOver();
                return true;
            }



        }
        return false;
    }

    public void gameOver(){
        if (launcher.getVisibility() == View.INVISIBLE && launcher2.getVisibility() == View.INVISIBLE && launcher3.getVisibility() == View.INVISIBLE){
            missileMaker.setRunning(false);
            Intent i = new Intent(this, GameOver.class);
            i.putExtra("scores", ""+scoreValue);
            i.putExtra("level", ""+levelValue);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            mediaPlayer.stop();
            finish();
        }
    }


}
