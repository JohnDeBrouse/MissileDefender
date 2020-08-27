package com.example.missileDefender;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {
    ImageView gameover;
    TextView scores;
    String initials;
    TextView stuff;
    String score;
    String level;
    TextView ranks;
    TextView topplayer;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        setupFullScreen();
        Intent i = getIntent();
        level = i.getStringExtra("level");
        score = i.getStringExtra("scores");
        gameover = findViewById(R.id.gameover);
        scores = findViewById(R.id.scores);
        ranks = findViewById(R.id.ranks);
        topplayer = findViewById(R.id.topplayer);
        stuff = findViewById(R.id.stuff);
        scores.setVisibility(View.INVISIBLE);
        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        gameoverAnimation(gameover);
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

    private void gameoverAnimation(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(0, 3f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(7000);
        img.startAnimation(fadeOut);
        img.setVisibility(View.INVISIBLE);

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                doSave("080");
            }
        });


    }

    public void doSave(String initials) {
        DatabaseHandler dbh = new DatabaseHandler(this);
        dbh.execute(initials, ""+score, ""+level);
    }

    public void setResults(String s) {
        Intent intent = new Intent();
        intent.putExtra("DATA", s);
        setResult(Activity.RESULT_OK, intent);
        if (s.contains("*")){
            if (Integer.parseInt(s.substring(1)) < Integer.parseInt(score)){
                alertBox();
            }
            else{
                doSave("070");
            }
        }
        else {
            scores.setText(s);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            stuff.setVisibility(View.VISIBLE);
                            ranks.setVisibility(View.VISIBLE);
                            topplayer.setVisibility(View.VISIBLE);
                            scores.setVisibility(View.VISIBLE);
                        }
                    },
                    300);
        }
        scores.setText(s);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        stuff.setVisibility(View.VISIBLE);
                        ranks.setVisibility(View.VISIBLE);
                        topplayer.setVisibility(View.VISIBLE);
                        scores.setVisibility(View.VISIBLE);
                    }
                },
                300);

    }

    // Top 10 alert box for the highscore. User is to enter in their name to be saved in a sql database
    public void alertBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congrats! You made top 10!");
        builder.setMessage("Please enter your initials");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);

        int maxLength = 3;
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initials = input.getText().toString();
                doSave(initials);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
