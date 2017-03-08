package com.findafun.seamless.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.findafun.R;
import com.findafun.seamless.utility.AvenuesParams;
import com.findafun.seamless.utility.ServiceUtility;

public class InitialScreenActivity extends Activity {
	
	private EditText accessCode, merchantId, currency, amount, customerIdentifier;
	
	private void init(){
		accessCode = (EditText) findViewById(R.id.accessCode);
		merchantId = (EditText) findViewById(R.id.merchantId);
		currency = (EditText) findViewById(R.id.currency);
		amount = (EditText) findViewById(R.id.amount);
		customerIdentifier = (EditText) findViewById(R.id.customerIdentifier);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial_screen_ns);
		
		init();
	}

	public void onClick(View view) {
		//Mandatory parameters. Other parameters can be added if required.
		String vAccessCode = ServiceUtility.chkNull(accessCode.getText()).toString().trim();
		String vMerchantId = ServiceUtility.chkNull(merchantId.getText()).toString().trim();
		String vCurrency = ServiceUtility.chkNull(currency.getText()).toString().trim();
		String vAmount = ServiceUtility.chkNull(amount.getText()).toString().trim();
		if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
			Intent intent = new Intent(this,BillingShippingActivity.class);
			intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode.getText()).toString().trim());
			intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId.getText()).toString().trim());
			intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency.getText()).toString().trim());
			intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amount.getText()).toString().trim());
			intent.putExtra(AvenuesParams.CUSTOMER_IDENTIFIER, ServiceUtility.chkNull(customerIdentifier.getText()).toString().trim());
			startActivity(intent);
		}else{
			showToast("All parameters are mandatory.");
		}
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 