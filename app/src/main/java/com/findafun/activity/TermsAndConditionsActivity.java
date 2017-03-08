package com.findafun.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.findafun.R;

/**
 * Created by BXDC46 on 1/31/2016.
 */
public class TermsAndConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.terms_layout);

        ImageView backbtn = (ImageView) findViewById(R.id.bookings_back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
