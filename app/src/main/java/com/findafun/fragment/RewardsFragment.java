package com.findafun.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.activity.AddEventActivity;
import com.findafun.activity.AllDetailsActivity;
import com.findafun.activity.BookingsActivity;
import com.findafun.activity.CheckinsActivity;
import com.findafun.activity.EngagementActivity;
import com.findafun.activity.EventDetailActivity;
import com.findafun.activity.InternBoradActivity;
import com.findafun.activity.LandingActivity;
import com.findafun.activity.LeaderBoardActivity;
import com.findafun.activity.PhotosActivity;
import com.findafun.activity.SplashScreenActivity;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.Rewards;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.GamificationServiceHelper;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.twitter.TwitterUtil;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by BXDC46 on 1/14/2016.
 */
public class RewardsFragment extends LandingPagerFragment implements IGamificationServiceListener, DialogClickListener {
    private static final String TAG = RewardsFragment.class.getName();

    private ImageView mUserImage = null;
    private TextView mUsername = null;
    private TextView mUserHobby;
    private RelativeLayout mRewardsLayout = null;
    private TextView mPointsCount = null;
    private RelativeLayout mVisaLayout = null;
    private TextView mVisaCount = null;
    private RelativeLayout mLeaderBoardLayout = null;
    private TextView mLeaderBoardCount = null;
    private TextView mPointsFOrNextLevel = null;
    private TextView mpositionForNextLevel = null;

    private RelativeLayout mPhotosLayout = null;
    private ImageView mPhotosImage = null;
    private TextView mPhotosCount = null;

    private RelativeLayout mEngagementsLayout = null;
    private ImageView mEngagementImage = null;
    private TextView mEngagementCount = null;

    private RelativeLayout mChekinLayout = null;
    private ImageView mCheckinImage = null;
    private TextView mCheckinCount = null;

    private RelativeLayout mBookingsLayout = null;
    private ImageView mBookingsImage = null;
    private TextView mBookingsCount = null;

    private RelativeLayout mReferralLayout = null;
    private ImageView mReferralImage = null;
    private TextView mReferralCount = null;

    private boolean mLoadData = true;
    private Rewards mRewards = null;
    private ProgressDialog mProgressDialog = null;
    private Transformation transformation;
    private ImageView mUserlevelImage = null;

    private void initializeRewardsViews(View view) {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();

        mUserImage = (ImageView) view.findViewById(R.id.rewards_user_img);
        mUsername = (TextView) view.findViewById(R.id.rewards_user_name);
        mUserHobby = (TextView) view.findViewById(R.id.rewards_user_hobby);
        mUserlevelImage = (ImageView) view.findViewById(R.id.rewards_level_image_url);

        mPointsFOrNextLevel = (TextView) view.findViewById(R.id.rewards_points_subtext);
        mpositionForNextLevel = (TextView) view.findViewById(R.id.rewards_leaderboard_subtext);

        mRewardsLayout = (RelativeLayout) view.findViewById(R.id.rewards_points_layout);
        mPointsCount = (TextView) view.findViewById(R.id.rewards_points_total_count);

        mVisaLayout = (RelativeLayout) view.findViewById(R.id.rewards_visa_layout);
        mVisaCount = (TextView) view.findViewById(R.id.rewards_visa_total_count);

        mLeaderBoardLayout = (RelativeLayout) view.findViewById(R.id.rewards_leaderboard_layout);
        mLeaderBoardCount = (TextView) view.findViewById(R.id.rewards_leaderboard_total_count);
        mLeaderBoardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeaderBoardActivity.class);
                startActivity(intent);
            }
        });

        mPhotosLayout = (RelativeLayout) view.findViewById(R.id.rewards_photos_layout);
        mPhotosImage = (ImageView) view.findViewById(R.id.rewards_photos_image);
        mPhotosCount = (TextView) view.findViewById(R.id.rewards_photos_total_count);
        mPhotosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhotosActivity.class);
                startActivity(intent);
            }
        });

        mEngagementsLayout = (RelativeLayout) view.findViewById(R.id.rewards_engagements_layout);
        mEngagementImage = (ImageView) view.findViewById(R.id.rewards_engagements_image);
        mEngagementCount = (TextView) view.findViewById(R.id.rewards_engagements_total_count);
        mEngagementsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EngagementActivity.class);
                startActivity(intent);

            }
        });

        mChekinLayout = (RelativeLayout) view.findViewById(R.id.rewards_checkins_layout);
        mCheckinImage = (ImageView) view.findViewById(R.id.rewards_checkins_image);
        mCheckinCount = (TextView) view.findViewById(R.id.rewards_checkins_total_count);
        mChekinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckinsActivity.class);
                startActivity(intent);
            }
        });

        mBookingsLayout = (RelativeLayout) view.findViewById(R.id.rewards_bookings_layout);
        mBookingsCount = (TextView) view.findViewById(R.id.rewards_bookings_total_count);
        mBookingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookingsActivity.class);
                startActivity(intent);

            }
        });

        mReferralLayout = (RelativeLayout) view.findViewById(R.id.referral_points_layout);
        mReferralCount = (TextView) view.findViewById(R.id.referral_points_total_count);
        mReferralLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InternBoradActivity.class);
                startActivity(intent);
                // Toast.makeText(getActivity(), "Detailed information not available for this card", Toast.LENGTH_SHORT).show();


            }
        });

        int loginMode = PreferenceStorage.getLoginMode(getActivity());
        // if((loginmode == FindAFunConstants.FACEBOOK) || (loginmode == FindAFunConstants.GOOGLE_PLUS)){
        String url = PreferenceStorage.getProfileUrl(getActivity());
        if ((url == null) || (url.isEmpty())) {
            if ((loginMode == 1) || (loginMode == 3)) {
                url = PreferenceStorage.getSocialNetworkProfileUrl(getActivity());
            }
        }
        if ((url != null) && !(url.isEmpty())) {
            Picasso.with(getActivity()).load(url).fit().transform(this.transformation).placeholder(R.drawable.placeholder_small).error(R.drawable.placeholder_small).into(mUserImage);
        }
        // }

        TextView statictics = (TextView) view.findViewById(R.id.view_statistics);
        statictics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public static RewardsFragment newInstance(int position) {
        RewardsFragment frag = new RewardsFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Log.d(TAG, "Nearby fragment onCreateView called");
        View view = inflater.inflate(R.layout.rewards_layout, container, false);

        initializeRewardsViews(view);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                Intent addEventIntent = new Intent(getActivity(), AddEventActivity.class);
//                //navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(addEventIntent);
                //getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void callGetEventService(int position) {

        // if(mLoadData ) {
        int loginMode = PreferenceStorage.getLoginMode(getActivity());
        // if((loginmode == FindAFunConstants.FACEBOOK) || (loginmode == FindAFunConstants.GOOGLE_PLUS)){
        String url = PreferenceStorage.getProfileUrl(getActivity());
        if ((url == null) || (url.isEmpty())) {
            if ((loginMode == 1) || (loginMode == 3)) {
                url = PreferenceStorage.getSocialNetworkProfileUrl(getActivity());
            }

        }
        Log.d(TAG, "image URL is" + url);
        if ((url != null) && !(url.isEmpty())) {
            Picasso.with(getActivity()).load(url).fit().transform(this.transformation).noPlaceholder().error(R.drawable.placeholder_small).into(mUserImage);
        }
        Log.d(TAG, "Fetch rewards data");
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();

        GamificationServiceHelper serviceHelper = new GamificationServiceHelper(getActivity());
        serviceHelper.fetchGamificationDetails(String.format(FindAFunConstants.GET_REWARDS, Integer.parseInt(PreferenceStorage.getUserId(getActivity()))), this);
        //}

    }

    private void setUpRewardsData() {
        if (mRewards.getLevelName() != null) {
            mUserHobby.setText(mRewards.getLevelName());
        } else {
            mUserHobby.setText("N/A");

        }
        if (mRewards.getLevelImageUrl() != null) {
            mUserlevelImage.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(mRewards.getLevelImageUrl()).fit().placeholder(R.drawable.amateur_trophy).error(R.drawable.amateur_trophy).into(mUserlevelImage);

        }
        String username = PreferenceStorage.getUserName(getActivity());
        if (username != null) {
            mUsername.setText(username);
        } else {
            mUsername.setText("N/A");
        }
        mPointsCount.setText(Integer.toString(mRewards.getTotalPoint()));
        mVisaCount.setText(Integer.toString(mRewards.getVisaCount()));
        String leaderboardcount = Integer.toString(mRewards.getLeaderboardPosition());
        mLeaderBoardCount.setText("#" + leaderboardcount);

        if (mRewards.getPointsfornextlevel() != null) {
            mPointsFOrNextLevel.setText("+ " + mRewards.getPointsfornextlevel() + " more points for next Level");
        }
        if (mRewards.getPointsfornextrank() != null) {
            mpositionForNextLevel.setText("+ " + mRewards.getPointsfornextrank() + " more points for next rank");
        }

        mReferralCount.setText(Integer.toString(mRewards.getReferralPromoCount()));
        mPhotosCount.setText(Integer.toString(mRewards.getPhotoSharingCount()));
        mEngagementCount.setText(Integer.toString(mRewards.getEngagementsCount()));
        mCheckinCount.setText(Integer.toString(mRewards.getCheckInCount()));
        String count = Integer.toString(mRewards.getEventBookingCount());
        Log.d(TAG, "bookingcount" + count);
        if (count != null) {
            mBookingsCount.setText(count);
        }
    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        if (result != null) {
            Rewards rewards = (Rewards) result;
            if (rewards != null) {
                mRewards = rewards;
                GamificationDataHolder.getInstance().setmRewards(mRewards);
                setUpRewardsData();
                mLoadData = false;
            }
        }

        if (PreferenceStorage.getUserType(getActivity()).equalsIgnoreCase("1")) {

        } else {

//            AlertDialogHelper.showCompoundAlertDialog(getActivity(), "Login", "Login to access", "OK", "CANCEL", 1);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Login");
            alertDialogBuilder.setMessage("Log in to Access");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    doLogout();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });
            alertDialogBuilder.show();


        }
    }

    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().clear().commit();
        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onError(String error) {
        AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
