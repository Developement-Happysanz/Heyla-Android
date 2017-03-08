package com.findafun.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.bean.events.Event;

/**
 * Created by Nandha on 25-08-2016.
 */
public class SubmitReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnSubmit;
    private EditText edReview;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);
        event = (Event) getIntent().getSerializableExtra("eventObj");

        ImageView backbtn = (ImageView) findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addListenerOnRatingBar();
        addListenerOnButton();
    }

    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.event_rating);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                //txtRatingValue.setText(String.valueOf(rating));
            }
        });
    }

    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.event_rating);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(SubmitReviewActivity.this,
                        String.valueOf(ratingBar.getRating()),
                        Toast.LENGTH_SHORT).show();
                saveReview();
            }

        });
    }

    private void saveReview() {

        int UserId = 0;
        int EventId = 0;
        int EventType = 0;
        String Comments = "";
        int Rating = 1;

        //Integer.parseInt(PreferenceStorage.getUserId(EventDetailActivity.this)), Integer.parseInt((event.getId()))

    }
}
