package com.findafun.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.bean.gamification.Booking;
import com.findafun.bean.gamification.BookingsBoard;

import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.GamificationServiceHelper;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PreferenceStorage;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;

/**
 * Created by BXDC46 on 1/30/2016.
 */
public class BookingsActivity extends AppCompatActivity implements IGamificationServiceListener , DialogClickListener {

    private static final String TAG = BookingsActivity.class.getName();
    private TextView mPointsView = null;
    private TextView mTotalPointsView = null;
    private ListView mBoardDetails = null;
    private final Transformation transformation;
    private DataAdapter mAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private android.os.Handler mHandler = new android.os.Handler();
    private BookingsBoard mDetailsData = null;
    private String mDateVal ="0";
    private HashMap<String, Integer> mHeaderPos = new HashMap<String,Integer>();

    public BookingsActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.bookings_layout);
        mDetailsData = GamificationDataHolder.getInstance().getmBookingBoard();
        initializeViews();
        mHeaderPos.clear();
        //check if leader board data is already loaded
       // if(!(mDetailsData.getAvailableCount() > 0)){

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.show();
            GamificationServiceHelper helper = new GamificationServiceHelper(this);
            //Integer.parseInt(PreferenceStorage.getUserId(this)))
            helper.fetchBookingDetails(String.format(FindAFunConstants.GET_BOOKINGS_URL, Integer.parseInt(PreferenceStorage.getUserId(this))), this);

       /* }else{
            if(mDetailsData.getBookingPoints() != null) {
                mPointsView.setText(mDetailsData.getBookingPoints());
            }
            if(mDetailsData.getBookingCount() != null){
                mTotalPointsView.setText(mDetailsData.getBookingCount());
            }

        }*/
    }

    private void initializeViews(){

        mPointsView = (TextView) findViewById(R.id.bookings_star_value);
        mTotalPointsView = (TextView) findViewById(R.id.bookings_total_val);
        ImageView backbtn = (ImageView) findViewById(R.id.bookings_back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBoardDetails = (ListView) findViewById(R.id.bookings_list);
        mAdapter =  new DataAdapter();
        mBoardDetails.setAdapter(mAdapter);

    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        if(mProgressDialog != null){
            mProgressDialog.cancel();
        }
        if( (result != null) && (result instanceof BookingsBoard)){
            Log.d(TAG, "Bookings data success");
            Log.d(TAG, "Notify adapter");
            Log.d(TAG,"Booking points"+ mDetailsData.getBookingPoints()+ "bookings count"+ mDetailsData.getBookingCount());
            if(mDetailsData.getBookingPoints() != null) {
                mPointsView.setText(mDetailsData.getBookingPoints());
            }
            if(mDetailsData.getBookingCount() != null){
                mTotalPointsView.setText(mDetailsData.getBookingCount());
            }
            mHeaderPos.clear();
            mAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onError(String erorr) {
        if(mProgressDialog != null){
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, erorr);

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d(TAG, "count is" + mDetailsData.getAvailableCount());
            mDateVal ="0";
            return  mDetailsData.getAvailableCount();

        }

        @Override
        public Object getItem(int position) {
            return mDetailsData.getAtPos(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.gamification_row, parent, false);
            }
            Log.d(TAG,"getview called"+position);
            TextView date = (TextView) convertView.findViewById(R.id.board_data_val);
            TextView eventname = (TextView) convertView.findViewById(R.id.board_event_name);
            ImageView image = (ImageView) convertView.findViewById(R.id.board_event_img);
            TextView ticketcount = (TextView) convertView.findViewById(R.id.ticket_count);
            ticketcount.setVisibility(View.VISIBLE);

            Booking booking = mDetailsData.getAtPos(position);
            String eventdate = booking.getDate();
            if(!mHeaderPos.containsKey(eventdate)){
                Log.d(TAG,"setting date");
                date.setVisibility(View.VISIBLE);
                date.setText(eventdate);
                mHeaderPos.put(eventdate, position);
               // mDateVal = eventdate;

            }else{
                int headerpos = mHeaderPos.get(eventdate);
                if(headerpos == position){
                    Log.d(TAG,"setting date");
                    date.setVisibility(View.VISIBLE);
                    date.setText(eventdate);

                }else{
                    date.setVisibility(View.GONE);
                }
            }
            if(booking.getEventName() != null){
                eventname.setText(booking.getEventName());
            }else{
                eventname.setText("N/A");
            }
            if(FindAFunValidator.checkNullString(booking.getImageUrl())) {
                Picasso.with(BookingsActivity.this).load(booking.getImageUrl()).fit().transform(BookingsActivity.this.transformation).placeholder(R.drawable.placeholder_small).error(R.drawable.placeholder_small).into(image);
            } else {
                image.setImageResource(R.drawable.placeholder_small);
            }
            if(booking.getTicketCount() != null){
                String ticketdata = booking.getTicketCount()+"  "+"Tickets";
                ticketcount.setText(ticketdata);
            }
            return convertView;
        }
    }
}
