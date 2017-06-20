package com.findafun.ccavactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.ccavutility.AvenuesParams;

public class StatusActivity extends Activity {

    private TextView tv4,OrderNum,PaymentId, TransactionDate, PaymentAmount, PaymentStatus;
    private Button PaymentDone;
    private ImageView Success, Failure;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_status_ns);

//		Toast.makeText(getApplicationContext(), "status", Toast.LENGTH_SHORT).show();
        Intent mainIntent = getIntent();
        tv4 = (TextView) findViewById(R.id.textView1);
        OrderNum = (TextView) findViewById(R.id.txt_ordernum);
        PaymentId = (TextView) findViewById(R.id.txt_payid);
        TransactionDate = (TextView) findViewById(R.id.txt_transdate);
        PaymentAmount = (TextView) findViewById(R.id.txt_payamt);
        PaymentStatus = (TextView) findViewById(R.id.txt_paystatus);
        Success = (ImageView) findViewById(R.id.img_success);
        Failure = (ImageView) findViewById(R.id.img_fail);
        OrderNum.setText(AvenuesParams.MERCHANT_ID);
        PaymentId.setText(AvenuesParams.ORDER_ID);
        TransactionDate.setText(AvenuesParams.ORDER_ID);
        PaymentAmount.setText(AvenuesParams.AMOUNT);


        tv4.setText(mainIntent.getStringExtra("transStatus"));
		switch (tv4.getText().toString()) {
            case "Transaction Declined!" :
                Success.setVisibility(View.INVISIBLE);
                PaymentStatus.setText("Failed!");
                break;
            case "Transaction Successful!" :
                Failure.setVisibility(View.INVISIBLE);
                PaymentStatus.setText("Success!");
                break;
            case "Transaction Cancelled!" :
                Success.setVisibility(View.INVISIBLE);
                PaymentStatus.setText("Canceled!");
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