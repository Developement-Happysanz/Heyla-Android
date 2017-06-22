package com.findafun.activity;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.findafun.R;
import com.findafun.adapter.BookingPlanAdapter;
import com.findafun.app.AppController;
import com.findafun.bean.events.BookPlan;
import com.findafun.bean.events.BookPlanList;
import com.findafun.bean.events.Event;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    TextView txtEventName, txtEvnetVenue, txtEventDate, numTicketcount;
    EditText txtBookingDate;
    ImageView CountIncrease, CountDecrease, bookingImage;
    Button btnProceed;
    int selectedTicket = 0;
    private Event event;
    private BookPlan bookPlan = null;
    private String rate;
    private String flagPlan = "no", flagTicket = "no", flagBookingDate = "no";
    int year, month, day;
    int setTicketCount = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_plans);

        loadMoreListView = (LoadMoreListView) findViewById(R.id.listView_plans);
        txtEventName = (TextView) findViewById(R.id.event_name);
        txtEvnetVenue = (TextView) findViewById(R.id.event_venue);
        txtEventDate = (TextView) findViewById(R.id.event_when);
        txtBookingDate = (EditText) findViewById(R.id.book_date);
        numTicketcount = (TextView) findViewById(R.id.tcktcount);
        btnProceed = (Button) findViewById(R.id.proceed_btn);
        CountDecrease = (ImageView) findViewById(R.id.count_decrease);
        CountIncrease = (ImageView) findViewById(R.id.count_increase);
        loadMoreListView.setOnLoadMoreListener(this);
        loadMoreListView.setOnItemClickListener(this);
        bookPlanArrayList = new ArrayList<>();
        eventServiceHelper = new EventServiceHelper(this);
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        event = (Event) getIntent().getSerializableExtra("eventObj");
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");
        eventVenue = getIntent().getStringExtra("eventVenue");
        eventDate = getIntent().getStringExtra("eventStartEndDate");

        bookingImage = (ImageView) findViewById(R.id.event_booking_img);
        ImageLoader uImageLoader = AppController.getInstance().getUniversalImageLoader();
        if (event.getEventLogo().contains(".")) {
            uImageLoader.displayImage(event.getEventLogo(), bookingImage);
        }

        totalCount = setTicketCount;
        flagTicket = "yes";

        CountDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringTicketCount = numTicketcount.getText().toString();
                int getTicketCount = Integer.parseInt(stringTicketCount);
                int setTicketCount = 1;

                if (getTicketCount >= 1 & getTicketCount <= 10) {
                    CountDecrease.setEnabled(true);
                    CountIncrease.setEnabled(true);
                }

                if (getTicketCount == 1) {
                    CountDecrease.setEnabled(false);
                } else {
                    setTicketCount = getTicketCount - 1;
                    if (setTicketCount == 1) {
                        CountDecrease.setEnabled(false);
                        String setValue = String.valueOf(setTicketCount);
                        numTicketcount.setText(setValue);
                    } else {
                        String setValue = String.valueOf(setTicketCount);
                        numTicketcount.setText(setValue);
                    }
                    totalCount = setTicketCount;
                }

                if (setTicketCount == 0) {
                    flagTicket = "no";
//                    numTicketcount.setText("0");
                } else {
                    flagTicket = "yes";
                }
            }
        });

        CountIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stringTicketCount = numTicketcount.getText().toString();
                int getTicketCount = Integer.parseInt(stringTicketCount);
                int setTicketCount = 0;

                if (getTicketCount > 0 & getTicketCount <= 10) {
                    CountDecrease.setEnabled(true);
                    CountIncrease.setEnabled(true);
                }

                if (getTicketCount == 10) {
                    CountIncrease.setEnabled(false);
                } else {
                    CountIncrease.setEnabled(true);
                    setTicketCount = getTicketCount + 1;
                    numTicketcount.setText(String.valueOf(setTicketCount));
                    totalCount = setTicketCount;
                }

                if (setTicketCount == 0) {
                    flagTicket = "no";
                } else {
                    flagTicket = "yes";
                }
            }
        });

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

        txtBookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                showDateDialog();
            }
        });
    }

    private void showDateDialog() {

        final DatePickerDialog datePickerDialog = new DatePickerDialog(BookingPlansActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDate) {

                year = selectedYear;
                month = selectedMonth;
                day = selectedDate;
                DecimalFormat mFormat = new DecimalFormat("00");
                ((EditText) findViewById(R.id.book_date)).setText(new StringBuilder().append(mFormat.format(day)).append("/")
                        .append(mFormat.format(month + 1)).append("/").append(mFormat.format(year)));

                String bookDate = ((EditText) findViewById(R.id.book_date)).getText().toString();
                if (bookDate.equalsIgnoreCase("")) {
                    flagBookingDate = "no";
                } else {
                    flagBookingDate = "yes";
                }
            }
        }, year, month, day);
        try {
            String today = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime());

            String[] parts = eventDate.split("-");

            String StartDate = trimDateString(parts[0]);
            String EndDate = trimDateString(parts[1]);
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            Date dateStart = format.parse(StartDate);
            Date dateEnd = format.parse(EndDate);
            Date dateToday = format.parse(today);
//            int dateMargin = currentDate.compareTo(eventDate);
            DateTime dt1 = new DateTime(dateToday);
            DateTime dt2 = new DateTime(dateStart);
            int getDate = Days.daysBetween(dt1, dt2).getDays();


            int minMax[] = new int[2];

            if (getDate > 0) {
                System.out.println("Today is after StartDate");
                minMax[0] = dateDifference(today, StartDate); //today
                minMax[1] = dateDifference(StartDate, EndDate); //endDate
            } else if (getDate < 0) {
                System.out.println("Today is before StartDate");
                minMax[0] = 0;
                ; //startDate
                minMax[1] = dateDifference(today, EndDate); //endDate
            } else {
                System.out.println("Today is equal to StartDate");
                minMax[0] = 0; //today
                minMax[1] = dateDifference(today, EndDate); //endDate
            }

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, minMax[0]);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            c.add(Calendar.DATE, minMax[1]);
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            datePickerDialog.show();
        } catch (Exception ex) {

        }
    }

    private int dateDifference(String min, String max) {

        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        int i = 0;
        try {
            Date minDate = myFormat.parse(min);
            Date maxDate = myFormat.parse(max);
            DateTime dt1 = new DateTime(minDate);
            DateTime dt2 = new DateTime(maxDate);
            i = Days.daysBetween(dt1, dt2).getDays();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return i;
    }

    private String trimDateString(String getDate) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mFormat.format(Double.valueOf(year));
        String trimString = getDate.trim();
        String replaceString = trimString.replaceAll("  ", " ");
        String formatedDate = "";
        try {
            DateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
            Date date = (Date) formatter.parse(replaceString);
            System.out.println(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            formatedDate = mFormat.format(cal.get(Calendar.DATE)) + "/" + mFormat.format((cal.get(Calendar.MONTH) + 1)) + "/" + mFormat.format(cal.get(Calendar.YEAR));
            System.out.println("formatedDate : " + formatedDate);
        } catch (Exception ex) {
        }

        return formatedDate;
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

        if (v == btnProceed) {

            if ((flagPlan.equalsIgnoreCase("no")) || (flagTicket.equalsIgnoreCase("no")) || (flagBookingDate.equalsIgnoreCase("no"))) {
                Toast.makeText(this, "Select ticket or plan", Toast.LENGTH_SHORT).show();
            } else {
                selectedTicket = totalCount;
                String totalTicketNo = numTicketcount.getText().toString();
                int noOfTicket = Integer.parseInt(totalTicketNo);

                double _rate = 0.0;
                _rate = Double.parseDouble(rate);

                Double totalRate = noOfTicket * _rate;

                Intent intent = new Intent(getApplicationContext(), BookingPlanSeatSelectionActivity.class);
                intent.putExtra("eventObj", event);
                intent.putExtra("planObj", bookPlan);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventVenue", eventVenue);
                intent.putExtra("eventTicketsRate", totalRate);
                intent.putExtra("eventNoOfTicket", totalTicketNo);
                intent.putExtra("eventDate", txtBookingDate.getText().toString());
                String today = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime());
                PreferenceStorage.saveTransactionDate(getApplicationContext(), today);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); && (selectedTicket > 0
                startActivity(intent);
                finish();

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


