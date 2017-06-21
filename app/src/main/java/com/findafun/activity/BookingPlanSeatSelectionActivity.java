package com.findafun.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.bean.events.BookPlan;
import com.findafun.bean.events.Event;
import com.findafun.ccavactivity.InitialScreenActivity;
import com.findafun.ccavutility.ServiceUtility;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.ISignUpServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;

import org.json.JSONObject;


/**
 * Created by Nandha on 11-12-2016.
 */

public class BookingPlanSeatSelectionActivity extends AppCompatActivity implements ISignUpServiceListener {

    private String eventName, eventVenue, eventBookingDate;
    private static final String TAG = "BookingPlanSeatSelectionActivity";
    private TextView txtEventName, txtEvnetVenue, txtEventPay, txtCountTicket, txtTicket;
    private ImageView imgPlus, imgMinus;
    private Button btnPay, btnPayment, btnDecline;
    private BookPlan bookPlan;
    String count;
    int _count = 0;
    double rate = 0.0;
    private Event event;
    int pay = 0;
    double _pay = 0.00;
    Double tickets;
    protected ProgressDialogHelper progressDialogHelper;
    private SignUpServiceHelper signUpServiceHelper;
    String orderId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_plan_seat_selection);

        progressDialogHelper = new ProgressDialogHelper(this);
        signUpServiceHelper = new SignUpServiceHelper(getApplicationContext());
        signUpServiceHelper.setSignUpServiceListener(this);

        txtEventName = (TextView) findViewById(R.id.event_name);
        txtEvnetVenue = (TextView) findViewById(R.id.event_venue);
        txtEventPay = (TextView) findViewById(R.id.event_pay_amount);
        txtCountTicket = (TextView) findViewById(R.id.no_tickets);
        imgPlus = (ImageView) findViewById(R.id.count_increase);
        imgMinus = (ImageView) findViewById(R.id.count_decrease);
        btnPay = (Button) findViewById(R.id.pay);
        btnPayment = (Button) findViewById(R.id.btnPayment);
        btnDecline = (Button) findViewById(R.id.btnDecline);
        txtTicket = (TextView) findViewById(R.id.amount);
        event = (Event) getIntent().getSerializableExtra("eventObj");
        bookPlan = (BookPlan) getIntent().getSerializableExtra("planObj");
        eventName = getIntent().getStringExtra("eventName");
        eventVenue = getIntent().getStringExtra("eventVenue");
        tickets = getIntent().getDoubleExtra("eventTickets", 0.00);
        eventBookingDate = getIntent().getStringExtra("eventDate");
        String _rate = bookPlan.getSeatRate();
        rate = Double.parseDouble(_rate);

        //generating order number
        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId = randomNum.toString() + "-" + PreferenceStorage.getUserId(getApplicationContext());


        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtEventName.setText(eventName);
        txtEvnetVenue.setText(eventVenue);
        txtEventPay.setText(bookPlan.getSeatPlan() + " - ₹ : " + bookPlan.getSeatRate());
        txtTicket.setText("" + tickets);

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = txtCountTicket.getText().toString();
                _count = Integer.parseInt(count);
                _count = _count + 1;
                txtCountTicket.setText("₹" + _count);

                int i = (int) rate;
//                int pay = 0;
                pay = (i * _count);
                _pay = (double) pay;

                if (_count >= 1) {
                    imgMinus.setEnabled(true);
                    btnPay.setVisibility(View.VISIBLE);
                }
                btnPay.setText("Pay - " + pay);
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = txtCountTicket.getText().toString();
                _count = Integer.parseInt(count);
                _count = _count - 1;
                txtCountTicket.setText("₹" + _count);

                int i = (int) rate;
//                int pay = 0;
                pay = (i * _count);
                _pay = (double) pay;

                if (_count <= 0) {
                    imgMinus.setEnabled(false);
                    btnPay.setVisibility(View.GONE);
                }

                btnPay.setText("Pay - " + pay);
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(getApplicationContext(), InitialScreenActivity.class);
//                intent.putExtra("planObj", bookPlan);
//                intent.putExtra("eventName", eventName);
//                intent.putExtra("eventVenue", eventVenue);
//                intent.putExtra("orderId", orderId);
//                Bundle b = new Bundle();
//                b.putDouble("eventRate", _pay);
//
//                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                finish();
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callGetAttendanceService();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void callGetAttendanceService() {

        String newSet = "";
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            //    eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            new HttpAsyncTask().execute("");
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {

            JSONObject jsonObject = new JSONObject();
            try {
//                jsonObject.put(FindAFunConstants.PARAM_CLASS_ID, PreferenceStorage.getStudentClassIdPreference(getApplicationContext()));
//                jsonObject.put(FindAFunConstants.PARAM_STUDENT_ID, PreferenceStorage.getStudentEnrollIdPreference(getApplicationContext()));
                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                signUpServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.BOOKING_TICKET, orderId, event.getId(), bookPlan.getId(), Integer.parseInt(PreferenceStorage.getUserId(getApplicationContext())), tickets, eventBookingDate, _pay));

            } catch (Exception e) {
                e.printStackTrace();
            }
//            attendanceServiceHelper.makeGetAttendanceServiceCall(jsonObject.toString());

            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            progressDialogHelper.cancelProgressDialog();
        }
    }

    @Override
    public void onSignUp(JSONObject response) {

        String newSet = "";
        Intent intent = new Intent(getApplicationContext(), InitialScreenActivity.class);
        intent.putExtra("planObj", bookPlan);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventVenue", eventVenue);
        intent.putExtra("ticketRate", tickets);
        intent.putExtra("orderId", orderId);
        Bundle b = new Bundle();
        b.putDouble("eventRate", pay);
        PreferenceStorage.saveOrderId(getApplicationContext(),""+orderId);
        PreferenceStorage.savePaymentAmount(getApplicationContext(),""+pay);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
//
    }

    @Override
    public void onSignUpError(String error) {

    }
}
