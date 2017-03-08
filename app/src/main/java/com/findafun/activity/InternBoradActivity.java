package com.findafun.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.LeaderBoard;
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

import org.json.JSONArray;

/**
 * Created by Cube Reach 06 on 08-12-2016.
 */

public class InternBoradActivity extends AppCompatActivity implements IGamificationServiceListener, DialogClickListener {

    private static final String TAG = InternBoradActivity.class.getName();
    private TextView mLeaderboardvalueView = null;
    private TextView mLeaderboardHobby = null;
    private ListView mLeaderboardList = null;
    private final Transformation transformation;
    private InternBoradActivity.DataAdapter_local mAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private android.os.Handler mHandler = new android.os.Handler();

    public InternBoradActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.activity_intern_board);

        mLeaderboardList = (ListView) findViewById(R.id.local_leader_board_list);
        mAdapter = new InternBoradActivity.DataAdapter_local();
        mLeaderboardList.setAdapter(mAdapter);
        String userName = PreferenceStorage.getUserId(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();

        GamificationServiceHelper helper = new GamificationServiceHelper(this);
        helper.fetchLeaderBoardDetails(String.format(FindAFunConstants.GET_INTERN_BOARD,userName), this);

        ImageView backbtn = (ImageView) findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        if (result != null) {
            if (result instanceof JSONArray) {
                Log.d(TAG, "Notify adapter");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Notify adapter");
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    public void onError(String erorr) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, erorr);
    }

    class DataAdapter_local extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d(TAG, "count is" + GamificationDataHolder.getInstance().getLeaderboardCount());
            return GamificationDataHolder.getInstance().getLeaderboardCount();
        }

        @Override
        public Object getItem(int position) {
            return GamificationDataHolder.getInstance().getLeaderBoardat(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.leaderboard_row, parent, false);
            }
            Log.d(TAG, "getview called");
            TextView name = (TextView) convertView.findViewById(R.id.username);
            TextView subtitle = (TextView) convertView.findViewById(R.id.subtext);
            ImageView userimage = (ImageView) convertView.findViewById(R.id.image);

            LeaderBoard board = GamificationDataHolder.getInstance().getLeaderBoardat(position);
            Log.d(TAG, "username" + board.getUserName());
            if (board.getUserName() != null) {
                name.setText(board.getUserName());
            }
            String subtextVal = "";
            if (board.getRank() != null) {
                subtextVal = "#";
                subtextVal += board.getRank();
                subtextVal += "  | ";
            }
            if (board.getLevel_name() != null) {
                subtextVal += board.getLevel_name();
                subtextVal += "  | ";
            }
            if (board.getUserPointDetail() != null) {
                subtextVal += board.getUserPointDetail();
                subtextVal += " points";
            }
            if ((subtextVal != null) && !subtextVal.isEmpty()) {
                subtitle.setText(subtextVal);
            } else {
                subtitle.setText("N/A");
            }

            if (FindAFunValidator.checkNullString(board.getUserImageUrl())) {
                Picasso.with(InternBoradActivity.this).load(board.getUserImageUrl()).fit().transform(InternBoradActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(userimage);
            } else {
                userimage.setImageResource(R.drawable.placeholder_small_old);
            }

            return convertView;
        }
    }
}
