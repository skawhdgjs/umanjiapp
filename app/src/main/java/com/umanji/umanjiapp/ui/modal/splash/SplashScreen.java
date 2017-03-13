package com.umanji.umanjiapp.ui.modal.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.main.MainActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    private Thread mSplashThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SplashScreen sPlashScreen = this;

        // The thread to wait for splash screen events
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        // Wait given period of time or exit on touch
                        wait(SPLASH_TIME_OUT);
                    }
                } catch (InterruptedException ex) {
                }

                finish();

                // Run next activity
                Intent intent = new Intent();
                intent.setClass(sPlashScreen, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        mSplashThread.start();

/*
        new Handler().postDelayed(new Runnable() {

            *//*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             *//*

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);*/
    }

}
