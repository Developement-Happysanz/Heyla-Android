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
import com.findafun.bean.gamification.Checkins;
import com.findafun.bean.gamification.CheckinsBoard;
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
 * Created by BXDC46 on 1/31/2016.
 */
public class CheckinsActivity extends AppCompatActivity implements IGamificationServiceListener, DialogClickListener {

    private static final String TAG = CheckinsActivity.class.getName();
    private TextView mPointsView = null;
    private TextView mTotalPointsView = null;
    private ListView mBoardDetails = null;
    private final Transformation transformation;
    private DataAdapter mAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private android.os.Handler mHandler = new android.os.Handler();
    private CheckinsBoard mDetailsData = null;
    private String mDateVal = "0";
    private HashMap<String, Integer> mHeaderPos = new HashMap<String, Integer>();

    public CheckinsActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.checkins_layout);
        mDetailsData = GamificationDataHolder.getInstance().getmCheckinsBoard();
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
        helper.fetchCheckinsDetails(String.format(FindAFunConstants.GET_CHECKINS_URL, Integer.parseInt(PreferenceStorage.getUserId(this))), this);

       /* }else{
            if(mDetailsData.getCheckinPoints() != null) {
                mPointsView.setText(mDetailsData.getCheckinPoints());
            }
            if(mDetailsData.getCheckinCount() != null){
                mTotalPointsView.setText(mDetailsData.getCheckinCount());
            }

        }*/
    }

    private void initializeViews() {
        mPointsView = (TextView) findViewById(R.id.checkin_points_value);
        mTotalPointsView = (TextView) findViewById(R.id.checkins_total_val);
        ImageView backbtn = (ImageView) findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBoardDetails = (ListView) findViewById(R.id.checkins_list);
        mAdapter = new DataAdapter();
        mBoardDetails.setAdapter(mAdapter);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        if ((result != null) && (result instanceof CheckinsBoard)) {
            Log.d(TAG, "Bookings data success");
            Log.d(TAG, "Notify adapter");
            if (mDetailsData.getCheckinPoints() != null) {
                mPointsView.setText(mDetailsData.getCheckinPoints());
            }
            if (mDetailsData.getCheckinCount() != null) {
                mTotalPointsView.setText(mDetailsData.getCheckinCount());
            }
            mHeaderPos.clear();
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onError(String erorr) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, erorr);

    }

    class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d(TAG, "count is" + mDetailsData.getAvailableCount());
            mDateVal = "0";
            return mDetailsData.getAvailableCount();

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
            Log.d(TAG, "getview called" + position);
            TextView date = (TextView) convertView.findViewById(R.id.board_data_val);
            TextView eventname = (TextView) convertView.findViewById(R.id.board_event_name);
            ImageView image = (ImageView) convertView.findViewById(R.id.board_event_img);
            Checkins checkin = mDetailsData.getAtPos(position);
            String eventdate = checkin.getDate();
            if (!mHeaderPos.containsKey(eventdate)) {
                Log.d(TAG, "setting date");
                date.setVisibility(View.VISIBLE);
                date.setText(eventdate);
                mHeaderPos.put(eventdate, position);
                // mDateVal = eventdate;
            } else {
                int headerpos = mHeaderPos.get(eventdate);
                if (headerpos == position) {
                    Log.d(TAG, "setting date");
                    date.setVisibility(View.VISIBLE);
                    date.setText(eventdate);
                } else {
                    date.setVisibility(View.GONE);
                }
            }

            if (checkin.getEventName() != null) {
                eventname.setText(checkin.getEventName());
            } else {
                eventname.setText("N/A");
            }

            if (FindAFunValidator.checkNullString(checkin.getImageUrl())) {
                Picasso.with(CheckinsActivity.this).load(checkin.getImageUrl()).fit().transform(CheckinsActivity.this.transformation).placeholder(R.drawable.placeholder_small).error(R.drawable.placeholder_small).into(image);
            } else {
                image.setImageResource(R.drawable.placeholder_small);
            }

            return convertView;
        }
    }
}
