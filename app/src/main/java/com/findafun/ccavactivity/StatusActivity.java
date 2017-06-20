package com.findafun.ccavactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;

public class StatusActivity extends Activity {


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_status_ns);

//		Toast.makeText(getApplicationContext(), "status", Toast.LENGTH_SHORT).show();
        Intent mainIntent = getIntent();
        TextView tv4 = (TextView) findViewById(R.id.textView1);
        tv4.setText(mainIntent.getStringExtra("transStatus"));
		switch (tv4.getText().toString()) {
            case "Transaction Declined!" :

                break;
            case "Transaction Successful!" :
                break;
            case "Transaction Cancelled!" :
                break;
            default :
                break;
        }

//		finish();


    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }
} 