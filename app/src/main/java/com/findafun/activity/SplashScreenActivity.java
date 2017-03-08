package com.findafun.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.findafun.R;
import com.findafun.animate.ExplosionField;


public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private ExplosionField mExplosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mExplosionField = ExplosionField.attach2Window(this);
            addListener(findViewById(R.id.root));
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SplashScreenActivity.this, LoginNew.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {
            root.setClickable(true);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExplosionField.explode(v);
                    v.setOnClickListener(null);
                }
            });
        }
    }

}
