package com.example.missileDefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class Missile {

    private MainActivity mainActivity;
    private ImageView imageView;
    private AnimatorSet aSet = new AnimatorSet();
    private int screenHeight;
    private int screenWidth;
    private long screenTime;
    private static final String TAG = "Plane";
    private boolean hit = false;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;


        imageView = new ImageView(mainActivity);
        imageView.setX(-500);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.getLayout().addView(imageView);
            }
        });

    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(drawId);
            }
        });

        int startX = (int) (Math.random() * screenWidth);
        int endX = (startX + (Math.random() < 0.5 ? 150 : -150));


        ObjectAnimator y = ObjectAnimator.ofFloat(imageView, "y", -200, (screenHeight - 150));
        y.setInterpolator(new LinearInterpolator());
        y.setDuration(screenTime);
        y.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!hit) {
                            mainActivity.getLayout().removeView(imageView);
                            mainActivity.removeMissile(Missile.this);
                            float x2 = (int) (getX() + (0.5 * getWidth()));
                            float y2 = (int) (getY() + (0.5 * getHeight()));
                            boolean a = mainActivity.baseDestructionCheck(x2, y2);
                            if (a){
                                SoundPlayer.getInstance().start("interceptor_hit_base");
                                baseBlast(x2, y2);
                            }
                            interceptorBlast(x2, y2);


                        }
                        Log.d(TAG, "run: NUM VIEWS " +
                                mainActivity.getLayout().getChildCount());
                    }
                });

            }
        });

        ObjectAnimator x = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        float a = calculateAngle(startX, -200, endX, (screenHeight - 150));
        imageView.setRotation(a);
        x.setInterpolator(new LinearInterpolator());
        x.setDuration(screenTime);
        aSet.playTogether(y, x);
        return aSet;

    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }


    // Explode animation
    void interceptorBlast(float x, float y) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);
        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();
    }
    void baseBlast(float x, float y) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.blast);
        iv.setTransitionName("Missile Base Blast");
        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);

    }
}
