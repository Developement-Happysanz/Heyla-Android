package com.findafun.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.adapter.LeaderBoardAdapter;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.LeaderBoard;
import com.findafun.bean.gamification.Rewards;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.GamificationServiceHelper;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;

/**
 * Created by BXDC46 on 1/23/2016.
 */
public class LeaderBoardActivity extends AppCompatActivity implements IGamificationServiceListener, DialogClickListener {
    private static final String TAG = LeaderBoardActivity.class.getName();
    private TextView mLeaderboardvalueView = null;
    private TextView mLeaderboardHobby = null;
    private ListView mLeaderboardList = null;
    private final Transformation transformation;
    private DataAdapter mAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private LinearLayout globalLayout, localLayout;
    private android.os.Handler mHandler = new android.os.Handler();

    public LeaderBoardActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.leaderboard_layout);
        mLeaderboardvalueView = (TextView) findViewById(R.id.leaderboard_value);
        mLeaderboardHobby = (TextView) findViewById(R.id.leaderboard_hobby);
        globalLayout = (LinearLayout) findViewById(R.id.global_fragment);
        localLayout = (LinearLayout) findViewById(R.id.local_fragment);
        ImageView backbtn = (ImageView) findViewById(R.id.bookings_back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLeaderboardList = (ListView) findViewById(R.id.leader_board_list);
        mAdapter = new DataAdapter();
        mLeaderboardList.setAdapter(mAdapter);
        Rewards reward = GamificationDataHolder.getInstance().getmRewards();
        if (reward != null) {
            if (reward.getLevelName() != null) {
                mLeaderboardHobby.setText(reward.getLevelName());
            }
            mLeaderboardvalueView.setText(Integer.toString(reward.getLeaderboardPosition()));
        }

        //check if leader board data is already loaded
        //  if(!(GamificationDataHolder.getInstance().getLeaderboardCount() > 0)){

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();
        GamificationServiceHelper helper = new GamificationServiceHelper(this);
        helper.fetchLeaderBoardDetails(String.format(FindAFunConstants.GET_LEADER_BOARD), this);
        globalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GlobalLeaderBoardActivity.class);
                startActivity(intent);
            }
        });

        localLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocalLeaderBoardActivity.class);
                startActivity(intent);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Global"));
        tabLayout.addTab(tabLayout.newTab().setText("Local"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final LeaderBoardAdapter adapter = new LeaderBoardAdapter
                (getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // }
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

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    class DataAdapter extends BaseAdapter {

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
                Picasso.with(LeaderBoardActivity.this).load(board.getUserImageUrl()).fit().transform(LeaderBoardActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(userimage);
            } else {
                userimage.setImageResource(R.drawable.placeholder_small_old);
            }

            return convertView;
        }
    }
}
