package com.findafun.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.activity.LeaderBoardActivity;
import com.findafun.activity.LoginActivity;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.LeaderBoard;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.servicehelpers.GamificationServiceHelper;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;

/**
 * Created by Nandha on 25-08-2016.
 */
public class LeaderFullBoardFragment extends Fragment implements IGamificationServiceListener {

    private static final String TAG = LeaderBoardActivity.class.getName();
    public View view;
    public View viewResult;
    private final Transformation transformation;
    private View mLayout;
    private ListView mLeaderboardList = null;
    private DataAdapter mAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private android.os.Handler mHandler = new android.os.Handler();

    public LeaderFullBoardFragment() {
        // Required empty public constructor
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_leader_full_board, container, false);

        viewResult = initializeViews(view);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();

        GamificationServiceHelper helper = new GamificationServiceHelper(getActivity());
        helper.fetchLeaderBoardDetails(String.format(FindAFunConstants.GET_LEADER_BOARD), this);

        // Inflate the layout for this fragment
        return viewResult;
    }

    // Initialize Views
    private View initializeViews(View view) {
        mLayout = view.findViewById(R.id.list_board);
        mLeaderboardList = (ListView) getActivity().findViewById(R.id.leader_board_list);
        mAdapter = new DataAdapter();
        mLeaderboardList.setAdapter(mAdapter);
        return view;
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
        AlertDialogHelper.showSimpleAlertDialog(getActivity(), erorr);
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.leaderboard_row, parent, false);
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
                Picasso.with(getActivity()).load(board.getUserImageUrl()).fit().transform(LeaderFullBoardFragment.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(userimage);
            } else {
                userimage.setImageResource(R.drawable.placeholder_small_old);
            }

            return convertView;
        }
    }
}
