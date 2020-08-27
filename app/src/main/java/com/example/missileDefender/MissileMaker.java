package com.example.missileDefender;

import android.animation.AnimatorSet;
import android.util.Log;

import java.util.ArrayList;

import static com.example.missileDefender.Interceptor.INTERCEPTOR_BLAST;

public class MissileMaker implements Runnable {

    private MainActivity mainActivity;
    private boolean isRunning;
    private ArrayList<Missile> activeMissiles = new ArrayList<>();
    private int screenWidth, screenHeight;
    private static final int NUM_LEVELS = 5;

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile p : temp) {
            p.stop();
        }
    }

    @Override
    public void run() {
        setRunning(true);
        long delay = NUM_LEVELS * 1000;
        while (isRunning) {

            int resId = pickMissile();


            long planeTime = (long) ((delay * 0.5) + (Math.random() * delay));
            final Missile missile = new Missile(screenWidth, screenHeight, planeTime, mainActivity);
            SoundPlayer.getInstance().start("launch_missile");
            activeMissiles.add(missile);
            final AnimatorSet as = missile.setData(resId);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    as.start();
                }
            });

            try {
                Thread.sleep((long) (delay * 0.333));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            delay -= 10;
            if (delay <= 0)
                delay = 10;

            int levelNum = (NUM_LEVELS * 4) - Math.round(delay / 250);
            mainActivity.setLevel(levelNum);

        }
    }

    private int pickMissile() {
            return R.drawable.missile;
    }

    void removeMissile(Missile p) {
        activeMissiles.remove(p);
    }

    void applyInterceptorBlast(Interceptor interceptor, int id) {

        float x1 = interceptor.getX();
        float y1 = interceptor.getY();


        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for (Missile m : temp) {

            float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
            float y2 = (int) (m.getY() + (0.5 * m.getHeight()));
            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

            if (f < INTERCEPTOR_BLAST) {
                SoundPlayer.getInstance().start("hit_missile");
                mainActivity.incrementScore();
                m.interceptorBlast(x2, y2);
                nowGone.add(m);
            }
        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }
}
