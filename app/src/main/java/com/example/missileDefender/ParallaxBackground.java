package com.example.missileDefender;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.example.missileDefender.MainActivity.screenWidth;
import static com.example.missileDefender.MainActivity.screenHeight;

public class ParallaxBackground implements Runnable {

    private Context context;
    private ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private long duration;
    private int resId;
    private float counter;
    private static final String TAG = "ParallaxBackground";
    int direction;


    ParallaxBackground(Context context, ViewGroup layout, int resId, long duration) {

        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;
        counter =.25f;
        direction = 0;

        setupBackground();

    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(), screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);

        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        backImageA.setZ(1);
        backImageB.setZ(1);

        animateBack();
    }

    @Override
    public void run() {


        backImageA.setX(0);
        backImageB.setX(-(screenWidth + getBarHeight()));
        double cycleTime = 25.0;

        double cycles = duration / cycleTime;
        double distance = (screenWidth + getBarHeight()) / cycles;

        boolean isRunning = true;
        while (isRunning) {

            long start = System.currentTimeMillis();

            double aX = backImageA.getX() - distance;
            backImageA.setX((float) aX);
            double bX = backImageB.getX() - distance;
            backImageB.setX((float) bX);

            long workTime = System.currentTimeMillis() - start;

            if (backImageA.getX() < -(screenWidth + getBarHeight()))
                backImageA.setX((screenWidth + getBarHeight()));

            if (backImageB.getX() < -(screenWidth + getBarHeight()))
                backImageB.setX((screenWidth + getBarHeight()));

            long sleepTime = (long) (cycleTime - workTime);

            if (sleepTime <= 0) {
                continue;
            }

            try {
                Thread.sleep((long) (cycleTime - workTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    private void animateBack() {


        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                float width = screenWidth + getBarHeight();

                float a_translationX = width * progress;
                float b_translationX = width * progress - width;

                backImageA.setTranslationX(a_translationX);
                backImageB.setTranslationX(b_translationX);

                if (counter >= .9f){
                    direction = 1;
                    counter = counter -.001f;
                }
                else if (counter <= .25){
                    direction = 0;
                    counter = counter +.001f;
                }
                else{
                    if (direction == 1){
                        counter = counter -.001f;
                    }
                    else{
                        counter = counter +.001f;
                    }

                }

                backImageA.setAlpha(counter);
                backImageB.setAlpha(counter);

            }
        });
        animator.start();
    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
