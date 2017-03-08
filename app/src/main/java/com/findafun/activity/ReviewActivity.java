package com.findafun.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.bean.events.Event;

/**
 * Created by Data Crawl 6 on 28-Jun-16.
 */
public class ReviewActivity extends AppCompatActivity {

    final Context context = this;
    private Event event;
    //private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        event = (Event) getIntent().getSerializableExtra("eventObj");

        ImageView backbtn = (ImageView) findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get prompts.xml view
              /*  LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.activity_review_submit, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                final RatingBar mBar = (RatingBar) findViewById(R.id.event_rating);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String str = userInput.getText().toString();

                                        Toast.makeText(getApplicationContext(), "Hello There " + str, Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getApplicationContext(), "Hello There " + ratingBarStepSize, Toast.LENGTH_SHORT).show();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show(); */

                Intent intent = new Intent(ReviewActivity.this, SubmitReviewActivity.class);
                intent.putExtra("eventObj", event);
                startActivity(intent);

            }
        });

    }

}
