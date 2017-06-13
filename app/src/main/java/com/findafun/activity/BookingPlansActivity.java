package com.findafun.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.findafun.R;
import com.findafun.adapter.BookingPlanAdapter;
import com.findafun.bean.events.BookPlan;
import com.findafun.bean.events.BookPlanList;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nandha on 11-12-2016.
 */

public class BookingPlansActivity extends AppCompatActivity implements LoadMoreListView.OnLoadMoreListener, IEventServiceListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "BookingPlansActivity";
    LoadMoreListView loadMoreListView;
    View view;
    String eventId, eventName, eventVenue, eventDate;
    BookingPlanAdapter bookingPlanAdapter;
    EventServiceHelper eventServiceHelper;
    ArrayList<BookPlan> bookPlanArrayList;
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private SearchView mSearchView = null;
    TextView txtEventName, txtEvnetVenue, txtEventDate;
    TextView txtOne, txtTwo, txtThree, txtFour, txtFive, txtSix, txtSeven, txtEight, txtNine, txtTen, txtSelectedQunatity;
    Button btnProceed;
    int selectedTicket = 0;
    private BookPlan bookPlan = null;
    private String rate;
    private String flagPlan = "no", flagTicket = "no";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_plans);
        //getSupportActionBar().hide();
        loadMoreListView = (LoadMoreListView) findViewById(R.id.listView_plans);
        txtEventName = (TextView) findViewById(R.id.event_name);
        txtEvnetVenue = (TextView) findViewById(R.id.event_venue);
        txtEventDate = (TextView) findViewById(R.id.event_when);

        txtOne = (TextView) findViewById(R.id.txtOne);
        txtTwo = (TextView) findViewById(R.id.txtTwo);
        txtThree = (TextView) findViewById(R.id.txtThree);
        txtFour = (TextView) findViewById(R.id.txtFour);
        txtFive = (TextView) findViewById(R.id.txtFive);
        txtSix = (TextView) findViewById(R.id.txtSix);
        txtSeven = (TextView) findViewById(R.id.txtSeven);
        txtEight = (TextView) findViewById(R.id.txtEight);
        txtNine = (TextView) findViewById(R.id.txtNine);
        txtTen = (TextView) findViewById(R.id.txtTen);
        txtSelectedQunatity = (TextView) findViewById(R.id.txtSelectedTicket);
        btnProceed = (Button) findViewById(R.id.proceed_btn);

        loadMoreListView.setOnLoadMoreListener(this);
        loadMoreListView.setOnItemClickListener(this);
        bookPlanArrayList = new ArrayList<>();
        eventServiceHelper = new EventServiceHelper(this);
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        eventVenue = getIntent().getStringExtra("eventVenue");
        eventDate = getIntent().getStringExtra("eventStartEndDate");

        txtOne.setOnClickListener(this);
        txtTwo.setOnClickListener(this);
        txtThree.setOnClickListener(this);
        txtFour.setOnClickListener(this);
        txtFive.setOnClickListener(this);
        txtSix.setOnClickListener(this);
        txtSeven.setOnClickListener(this);
        txtEight.setOnClickListener(this);
        txtNine.setOnClickListener(this);
        txtTen.setOnClickListener(this);
        txtSelectedQunatity.setOnClickListener(this);
        btnProceed.setOnClickListener(this);

        txtEventName.setText(eventName);
        txtEvnetVenue.setText(eventVenue);
        txtEventDate.setText("" + eventDate);

        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //  if (PreferenceStorage.getFilterApply(this)) {
        //    PreferenceStorage.IsFilterApply(this, false);
        callGetFilterService();
        //}

        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void callGetFilterService() {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/
        if (bookPlanArrayList != null)
            bookPlanArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            //    eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            new BookingPlansActivity.HttpAsyncTask().execute("");
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.no_connectivity));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == txtOne) {

            selectedTicket = Integer.parseInt(txtOne.getText().toString());
            txtSelectedQunatity.setText(txtOne.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.button_rounded_white);
            txtThree.setBackgroundResource(R.drawable.button_rounded_white);
            txtFour.setBackgroundResource(R.drawable.button_rounded_white);
            txtFive.setBackgroundResource(R.drawable.button_rounded_white);
            txtSix.setBackgroundResource(R.drawable.button_rounded_white);
            txtSeven.setBackgroundResource(R.drawable.button_rounded_white);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtTwo) {
            selectedTicket = Integer.parseInt(txtTwo.getText().toString());
            txtSelectedQunatity.setText(txtTwo.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.button_rounded_white);
            txtFour.setBackgroundResource(R.drawable.button_rounded_white);
            txtFive.setBackgroundResource(R.drawable.button_rounded_white);
            txtSix.setBackgroundResource(R.drawable.button_rounded_white);
            txtSeven.setBackgroundResource(R.drawable.button_rounded_white);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtThree) {
            selectedTicket = Integer.parseInt(txtThree.getText().toString());
            txtSelectedQunatity.setText(txtThree.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.button_rounded_white);
            txtFive.setBackgroundResource(R.drawable.button_rounded_white);
            txtSix.setBackgroundResource(R.drawable.button_rounded_white);
            txtSeven.setBackgroundResource(R.drawable.button_rounded_white);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtFour) {
            selectedTicket = Integer.parseInt(txtFour.getText().toString());
            txtSelectedQunatity.setText(txtFour.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.button_rounded_white);
            txtSix.setBackgroundResource(R.drawable.button_rounded_white);
            txtSeven.setBackgroundResource(R.drawable.button_rounded_white);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtFive) {
            selectedTicket = Integer.parseInt(txtFive.getText().toString());
            txtSelectedQunatity.setText(txtFive.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.btn_square_red);
            txtSix.setBackgroundResource(R.drawable.button_rounded_white);
            txtSeven.setBackgroundResource(R.drawable.button_rounded_white);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtSix) {
            selectedTicket = Integer.parseInt(txtSix.getText().toString());
            txtSelectedQunatity.setText(txtSix.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.btn_square_red);
            txtSix.setBackgroundResource(R.drawable.btn_square_red);
            txtSeven.setBackgroundResource(R.drawable.button_rounded_white);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtSeven) {
            selectedTicket = Integer.parseInt(txtSeven.getText().toString());
            txtSelectedQunatity.setText(txtSeven.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.btn_square_red);
            txtSix.setBackgroundResource(R.drawable.btn_square_red);
            txtSeven.setBackgroundResource(R.drawable.btn_square_red);
            txtEight.setBackgroundResource(R.drawable.button_rounded_white);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtEight) {
            selectedTicket = Integer.parseInt(txtEight.getText().toString());
            txtSelectedQunatity.setText(txtEight.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.btn_square_red);
            txtSix.setBackgroundResource(R.drawable.btn_square_red);
            txtSeven.setBackgroundResource(R.drawable.btn_square_red);
            txtEight.setBackgroundResource(R.drawable.btn_square_red);
            txtNine.setBackgroundResource(R.drawable.button_rounded_white);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtNine) {
            selectedTicket = Integer.parseInt(txtNine.getText().toString());
            txtSelectedQunatity.setText(txtNine.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.btn_square_red);
            txtSix.setBackgroundResource(R.drawable.btn_square_red);
            txtSeven.setBackgroundResource(R.drawable.btn_square_red);
            txtEight.setBackgroundResource(R.drawable.btn_square_red);
            txtNine.setBackgroundResource(R.drawable.btn_square_red);
            txtTen.setBackgroundResource(R.drawable.button_rounded_white);

            flagTicket = "yes";

        } else if (v == txtTen) {

            selectedTicket = Integer.parseInt(txtTen.getText().toString());
            txtSelectedQunatity.setText(txtTen.getText().toString());

            txtOne.setBackgroundResource(R.drawable.btn_square_red);
            txtTwo.setBackgroundResource(R.drawable.btn_square_red);
            txtThree.setBackgroundResource(R.drawable.btn_square_red);
            txtFour.setBackgroundResource(R.drawable.btn_square_red);
            txtFive.setBackgroundResource(R.drawable.btn_square_red);
            txtSix.setBackgroundResource(R.drawable.btn_square_red);
            txtSeven.setBackgroundResource(R.drawable.btn_square_red);
            txtEight.setBackgroundResource(R.drawable.btn_square_red);
            txtNine.setBackgroundResource(R.drawable.btn_square_red);
            txtTen.setBackgroundResource(R.drawable.btn_square_red);

            flagTicket = "yes";

        } else if (v == txtSelectedQunatity) {

        } else if (v == btnProceed) {

            if ((flagPlan.equalsIgnoreCase("no")) || (flagTicket.equalsIgnoreCase("no"))) {
                Toast.makeText(this, "Select ticket or plan", Toast.LENGTH_SHORT)
                        .show();
            } else {

                double _rate = 0.0;
                _rate = Double.parseDouble(rate);

                Double value = selectedTicket * _rate;

                Intent intent = new Intent(getApplicationContext(), BookingPlanSeatSelectionActivity.class);
                intent.putExtra("planObj", bookPlan);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventVenue", eventVenue);
                intent.putExtra("eventTickets", value);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); && (selectedTicket > 0
                startActivity(intent);

//                Toast.makeText(this, "Select ticket plan" + value, Toast.LENGTH_SHORT).show();

            }
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_BOOKING_PLAN, eventId));
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            progressDialogHelper.cancelProgressDialog();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        Log.d(TAG, "onEvent list item clicked" + i);
        view.setSelected(true);

        if ((bookingPlanAdapter != null) && (bookingPlanAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = bookingPlanAdapter.getActualEventPos(i);
            Log.d(TAG, "actual index" + actualindex);
            bookPlan = bookPlanArrayList.get(actualindex);
        } else {
            bookPlan = bookPlanArrayList.get(i);
        }

        rate = bookPlan.getSeatRate();
        // Toast.makeText(this, "Select ticket plan" + rate, Toast.LENGTH_SHORT).show();
        flagPlan = "yes";


      /*  Intent intent = new Intent(getApplicationContext(), BookingPlanSeatSelectionActivity.class);
        intent.putExtra("planObj", bookPlan);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventVenue", eventVenue);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);  */
//        // getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    @Override
    public void onEventResponse(final JSONObject response) {
        Log.d("ajazFilterresponse : ", response.toString());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                loadMoreListView.onLoadMoreComplete();

                Gson gson = new Gson();
                BookPlanList planList = gson.fromJson(response.toString(), BookPlanList.class);
                if (planList.getPlans() != null && planList.getPlans().size() > 0) {
                    totalCount = planList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(planList.getPlans());
                }
            }
        });
    }

    @Override
    public void onEventError(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                loadMoreListView.onLoadMoreComplete();
                AlertDialogHelper.showSimpleAlertDialog(BookingPlansActivity.this, error);
            }
        });
    }

    @Override
    public void onLoadMore() {

    }

    protected void updateListAdapter(ArrayList<BookPlan> bookPlanArrayList) {
        this.bookPlanArrayList.addAll(bookPlanArrayList);
        if (bookingPlanAdapter == null) {
            bookingPlanAdapter = new BookingPlanAdapter(this, this.bookPlanArrayList);
            loadMoreListView.setAdapter(bookingPlanAdapter);
        } else {
            bookingPlanAdapter.notifyDataSetChanged();
        }
    }

    public void searchForEvent(String eventname) {
        Log.d(TAG, "searchevent called");
        if (bookingPlanAdapter != null) {
            bookingPlanAdapter.startSearch(eventname);
            bookingPlanAdapter.notifyDataSetChanged();
            //loadMoreListView.invalidateViews();
        }
    }

    public void exitSearch() {
        Log.d(TAG, "exit event called");
        if (bookingPlanAdapter != null) {
            bookingPlanAdapter.exitSearch();
            bookingPlanAdapter.notifyDataSetChanged();
        }

    }
}
