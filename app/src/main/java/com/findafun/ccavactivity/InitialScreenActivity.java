package com.findafun.ccavactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.bean.events.BookPlan;
import com.findafun.ccavutility.AvenuesParams;
import com.findafun.ccavutility.ServiceUtility;
import com.findafun.utils.PreferenceStorage;

public class InitialScreenActivity extends Activity {

    private EditText accessCode, merchantId, currency, amount, orderId, rsaKeyUrl, redirectUrl, cancelUrl;
    private String eventName, eventVenue;
    private Double eventRate;
    private BookPlan bookPlan;
    Double tickets;

    private void init() {
        accessCode = (EditText) findViewById(R.id.accessCode);
        merchantId = (EditText) findViewById(R.id.merchantId);
        orderId = (EditText) findViewById(R.id.orderId);
        currency = (EditText) findViewById(R.id.currency);
        amount = (EditText) findViewById(R.id.amount);
        rsaKeyUrl = (EditText) findViewById(R.id.rsaUrl);
        redirectUrl = (EditText) findViewById(R.id.redirectUrl);
        cancelUrl = (EditText) findViewById(R.id.cancelUrl);
        bookPlan = (BookPlan) getIntent().getSerializableExtra("planObj");
        eventName = getIntent().getStringExtra("eventName");
        eventVenue = getIntent().getStringExtra("eventVenue");
        Bundle b = getIntent().getExtras();
        eventRate = b.getDouble("eventRate");
        tickets = getIntent().getDoubleExtra("ticketRate", 0.00);
        String orderIdValue = getIntent().getStringExtra("orderId");
        //eventRate = getIntent().getDoubleExtra("eventRate");
        //amount.setText(""+eventRate);
        amount.setText("" + tickets);
        int i = 0;

        //generating order number
//        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId.setText(orderIdValue);

        callPayment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen_ns);
        init();

        //generating order number
//        Integer randomNum = ServiceUtility.randInt(0, 9999999);
//        orderId.setText(randomNum.toString() + PreferenceStorage.getUserId(getApplicationContext()));
    }

    public void onClick(View view) {
        //Mandatory parameters. Other parameters can be added if required.
        String vAccessCode = ServiceUtility.chkNull(accessCode.getText()).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchantId.getText()).toString().trim();
        String vCurrency = ServiceUtility.chkNull(currency.getText()).toString().trim();
        String vAmount = ServiceUtility.chkNull(amount.getText()).toString().trim();
        String vRedirectUrl = ServiceUtility.chkNull(redirectUrl.getText()).toString().trim();
        String vCancelUrl = ServiceUtility.chkNull(cancelUrl.getText()).toString().trim();
        String vrsaKeyUrl = ServiceUtility.chkNull(rsaKeyUrl.getText()).toString().trim();
        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode.getText()).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency.getText()).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amount.getText()).toString().trim());
//            intent.putExtra(AvenuesParams.MERCH_PARAM1, PreferenceStorage.getUserId(getApplicationContext()));
            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl.getText()).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl.getText()).toString().trim());

            startActivity(intent);
        } else {
            showToast("All parameters are mandatory.");
        }
    }

    private void callPayment() {

        //Mandatory parameters. Other parameters can be added if required.
        String vAccessCode = ServiceUtility.chkNull(accessCode.getText()).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchantId.getText()).toString().trim();
        String vCurrency = ServiceUtility.chkNull(currency.getText()).toString().trim();
        String vAmount = ServiceUtility.chkNull(amount.getText()).toString().trim();
        String vRedirectUrl = ServiceUtility.chkNull(redirectUrl.getText()).toString().trim();
        String vCancelUrl = ServiceUtility.chkNull(cancelUrl.getText()).toString().trim();
        String vrsaKeyUrl = ServiceUtility.chkNull(rsaKeyUrl.getText()).toString().trim();
        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode.getText()).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency.getText()).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amount.getText()).toString().trim());
//            intent.putExtra(AvenuesParams.MERCH_PARAM1, PreferenceStorage.getUserId(getApplicationContext()));
            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl.getText()).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl.getText()).toString().trim());

            startActivity(intent);
//			finish();
        } else {
            showToast("All parameters are mandatory.");
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }
} 