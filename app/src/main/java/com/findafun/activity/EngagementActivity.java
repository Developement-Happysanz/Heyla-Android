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
import com.findafun.bean.gamification.Engagement;
import com.findafun.bean.gamification.EngagementBoard;
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
 * Created by bxdc46 on 1/30/2016.
 */
public class EngagementActivity extends AppCompatActivity implements IGamificationServiceListener, DialogClickListener {

    private static final String TAG = EngagementActivity.class.getName();
    private TextView mPointsView = null;
    private TextView mTotalPointsView = null;
    private ListView mBoardDetails = null;
    private final Transformation transformation;
    private DataAdapter mAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private android.os.Handler mHandler = new android.os.Handler();
    private EngagementBoard mEngagementDetails = null;
    private String mDateVal = "0";
    private HashMap<String, Integer> mHeaderPos = new HashMap<String, Integer>();

    public EngagementActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.engagement_layout);
        mEngagementDetails = GamificationDataHolder.getInstance().getmEngagementBoardDdetails();
        initializeViews();
        mHeaderPos.clear();

        //check if leader board data is already loaded
        if (!(mEngagementDetails.getAvailableEngagementCount() > 0)) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.show();
            GamificationServiceHelper helper = new GamificationServiceHelper(this);
            //Integer.parseInt(PreferenceStorage.getUserId(this)))
            helper.fetchEngagementDetails(String.format(FindAFunConstants.GET_ENGAGEMENTS_URL, Integer.parseInt(PreferenceStorage.getUserId(this))), this);
        } else {
            if (mEngagementDetails.getEngagementsPoints() != null) {
                mPointsView.setText(mEngagementDetails.getEngagementsPoints());
            }
            if (mEngagementDetails.getEngagementsCount() != null) {
                mTotalPointsView.setText(mEngagementDetails.getEngagementsCount());
            }
        }
    }

    private void initializeViews() {
        mPointsView = (TextView) findViewById(R.id.engagements_star_value);
        mTotalPointsView = (TextView) findViewById(R.id.engagement_total_val);
        ImageView backbtn = (ImageView) findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBoardDetails = (ListView) findViewById(R.id.engagements_list);
        mAdapter = new DataAdapter();
        mBoardDetails.setAdapter(mAdapter);
    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        if ((result != null) && (result instanceof EngagementBoard)) {
            Log.d(TAG, "Engagement success");
            Log.d(TAG, "Notify adapter");
            if (mEngagementDetails.getEngagementsPoints() != null) {
                mPointsView.setText(mEngagementDetails.getEngagementsPoints());
            }
            if (mEngagementDetails.getEngagementsCount() != null) {
                mTotalPointsView.setText(mEngagementDetails.getEngagementsCount());
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

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d(TAG, "count is" + mEngagementDetails.getAvailableEngagementCount());
            mDateVal = "0";
            return mEngagementDetails.getAvailableEngagementCount();
        }

        @Override
        public Object getItem(int position) {
            return mEngagementDetails.getEngagementAtPos(position);
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
            Engagement engagement = mEngagementDetails.getEngagementAtPos(position);
            String eventdate = engagement.getDate();
            Log.d(TAG, "event date" + eventdate + "currentdate" + mDateVal);
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
            if (engagement.getEventName() != null) {
                eventname.setText(engagement.getEventName());
            } else {
                eventname.setText("N/A");
            }
            if (FindAFunValidator.checkNullString(engagement.getImageUrl())) {
                Picasso.with(EngagementActivity.this).load(engagement.getImageUrl()).fit().transform(EngagementActivity.this.transformation).placeholder(R.drawable.placeholder_small).error(R.drawable.placeholder_small).into(image);
            } else {
                image.setImageResource(R.drawable.placeholder_small);
            }
            return convertView;
        }
    }
}
