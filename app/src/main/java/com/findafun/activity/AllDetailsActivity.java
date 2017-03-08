package com.findafun.activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.bean.gamification.Booking;
import com.findafun.bean.gamification.BookingsBoard;
import com.findafun.bean.gamification.Checkins;
import com.findafun.bean.gamification.CheckinsBoard;
import com.findafun.bean.gamification.Engagement;
import com.findafun.bean.gamification.EngagementBoard;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.PhotoDetail;
import com.findafun.bean.gamification.PhotosBoard;
import com.findafun.bean.gamification.Rewards;
import com.findafun.bean.gamification.alldetails.AllDetailsBoard;

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

/**
 * Created by BXDC46 on 1/31/2016.
 */
public class AllDetailsActivity extends AppCompatActivity implements IGamificationServiceListener, DialogClickListener {
    private static String TAG = AllDetailsActivity.class.getName();
    private TextView mTotalPointsView = null;
    private TextView mPhotoscount = null;
    private TextView mEngagementsCOunt = null;
    private TextView mCheckinCOunt = null;
    private TextView mBookingscount = null;
    private final Transformation transformation;

    //photos layout
    private TextView mPhotosStarCount = null;
    private GridView mPhotosGrid = null;
    private GridDataAdapter mPhotosAdapter = null;

    //engagement layout
    private TextView mEngagementstarCount = null;
    private ListView mEngagementList = null;
    private AllDetailsBoard mdetailsBoard = null;
    private ProgressDialog mProgressDialog = null;
    private EngagementDataAdapter mEngagementAdapter = null;
    private TextView mEngagementtotalcount = null;

    //checkins layout
    private TextView mCheckinsstarCount = null;
    private ListView mCheckinsList = null;
    private CheckinsDataAdapter mCheckinsAdapter = null;
    private TextView mCHeckinstotalcount = null;
    //Booking layout
    private TextView mBookingsstarCount = null;
    private ListView mBookingsList = null;
    private BookingsDataAdapter mBookingsAdapter = null;
    private TextView mBookingstotalcount = null;


    public AllDetailsActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.alldetails_layout);
        mdetailsBoard = GamificationDataHolder.getInstance().getmAllDetailsBoard();
        initializeViews();
        // if(GamificationDataHolder.getInstance().getmAllDetailsBoard().ismFetchData()) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();
        GamificationServiceHelper helper = new GamificationServiceHelper(this);
        //Integer.parseInt(PreferenceStorage.getUserId(this)))
        helper.fetchAllRewardsDetails(String.format(FindAFunConstants.GET_ALL_REWARDS_URL, Integer.parseInt(PreferenceStorage.getUserId(this))), this);
        /*}else{
            Log.d(TAG,"retrive data");
            retriveData();
        }*/



        /*//check if leader board data is already loaded
        if(!(mDetailsData.getAvailableCount() > 0)){


        }else{
            if(mDetailsData.getBookingPoints() != null) {
                mPointsView.setText(mDetailsData.getBookingPoints());
            }
            if(mDetailsData.getBookingCount() != null){
                mTotalPointsView.setText(mDetailsData.getBookingCount());
            }

        }*/
    }

    private void initializeViews() {
        mTotalPointsView = (TextView) findViewById(R.id.points_value);
        mPhotoscount = (TextView) findViewById(R.id.rewards_photos_total_count);
        mEngagementsCOunt = (TextView) findViewById(R.id.rewards_engagements_total_count);
        mBookingscount = (TextView) findViewById(R.id.rewards_bookings_total_count);
        mCheckinCOunt = (TextView) findViewById(R.id.rewards_checkins_total_count);

        //Get Rwards
        Rewards rewards = GamificationDataHolder.getInstance().getmRewards();
        if (rewards != null) {
            mTotalPointsView.setText(Integer.toString(rewards.getTotalPoint()));
            //mPhotoscount.setText(Integer.toString(rewards.getPhotoSharingCount()));
            mEngagementsCOunt.setText(Integer.toString(rewards.getEngagementsCount()));
            mCheckinCOunt.setText(Integer.toString(rewards.getCheckInCount()));
        }

        mPhotosStarCount = (TextView) findViewById(R.id.photos_star_count);
        mPhotosGrid = (GridView) findViewById(R.id.photos_grid);
        mPhotosAdapter = new GridDataAdapter();
        mPhotosGrid.setAdapter(mPhotosAdapter);
        mEngagementstarCount = (TextView) findViewById(R.id.engagements_star_count);
        mEngagementList = (ListView) findViewById(R.id.engagements_list);
        mEngagementAdapter = new EngagementDataAdapter();
        mEngagementList.setAdapter(mEngagementAdapter);
        mEngagementtotalcount = (TextView) findViewById(R.id.engagements_total_count);
        //Checkins data
        mCheckinsstarCount = (TextView) findViewById(R.id.checkins_star_count);
        mCheckinsList = (ListView) findViewById(R.id.checkins_list);
        mCheckinsAdapter = new CheckinsDataAdapter();
        mCheckinsList.setAdapter(mCheckinsAdapter);
        mCHeckinstotalcount = (TextView) findViewById(R.id.checkins_total_count);
        mBookingsstarCount = (TextView) findViewById(R.id.bookings_star_count);
        mBookingsList = (ListView) findViewById(R.id.bookings_list);
        mBookingsAdapter = new BookingsDataAdapter();
        mBookingsList.setAdapter(mBookingsAdapter);
        mBookingstotalcount = (TextView) findViewById(R.id.bookings_total_count);
        ImageView backbtn = (ImageView) findViewById(R.id.bookings_back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void retriveData() {
        PhotosBoard board = GamificationDataHolder.getInstance().getmAllDetailsBoard().getPhotoList();
        if (board != null) {
            if (board.getTotalPoints() != null) {
                Log.d(TAG, "photo points" + board.getTotalPoints());
                mPhotoscount.setText(board.getTotalPoints());
                mPhotosStarCount.setText(board.getTotalPoints());
            }
        }
        EngagementBoard enboard = GamificationDataHolder.getInstance().getmAllDetailsBoard().getEngagementsList();
        if (board != null) {
            if (enboard.getEngagementsPoints() != null) {
                mEngagementstarCount.setText(enboard.getEngagementsPoints());
            }
            if (enboard.getEngagementsCount() != null) {
                mEngagementtotalcount.setText("(" + enboard.getEngagementsCount() + ")");
            }
        }
        CheckinsBoard cboard = GamificationDataHolder.getInstance().getmAllDetailsBoard().getCheckinList();
        if (cboard != null) {
            if (cboard.getCheckinPoints() != null) {
                mCheckinsstarCount.setText(cboard.getCheckinPoints());
            }
            if (cboard.getCheckinCount() != null) {
                mCHeckinstotalcount.setText("(" + cboard.getCheckinCount() + ")");
            }
        }
        BookingsBoard bookboard = GamificationDataHolder.getInstance().getmAllDetailsBoard().getBookingList();
        if (bookboard != null) {
            if (bookboard.getBookingPoints() != null) {
                mBookingsstarCount.setText(bookboard.getBookingPoints());
            }
            if (bookboard.getBookingCount() != null) {
                mBookingstotalcount.setText("(" + bookboard.getBookingCount() + ")");
            }
        }
        setListViewHeightBasedOnChildren(mEngagementList);
        setListViewHeightBasedOnChildren(mCheckinsList);
        setListViewHeightBasedOnChildren(mBookingsList);
        mPhotosAdapter.notifyDataSetChanged();
        mEngagementAdapter.notifyDataSetChanged();
        mCheckinsAdapter.notifyDataSetChanged();
        mBookingsAdapter.notifyDataSetChanged();

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

        if ((result != null) && (result instanceof AllDetailsBoard)) {
            Log.d(TAG, "Received all details");
            retriveData();

        }

    }

    @Override
    public void onError(String erorr) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, erorr);

    }

    class GridDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if ((mdetailsBoard.getPhotoList() != null) && (mdetailsBoard.getPhotoList().getAllPhotos() != null)) {
                return mdetailsBoard.getPhotoList().getAllPhotos().size();
            } else {
                return 0;
            }

        }

        @Override
        public Object getItem(int position) {
            return mdetailsBoard.getPhotoList().getAllPhotos().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.photos_grid_view, parent, false);
            }
            ImageView photo = (ImageView) convertView.findViewById(R.id.photo_image);
            String url = null;

            if (mdetailsBoard.getPhotoList().getAllPhotos() != null) {
                PhotoDetail photodetail = mdetailsBoard.getPhotoList().getAllPhotos().get(position);
                if (photodetail.getImageUrl() != null) {
                    url = photodetail.getImageUrl();
                }

            }
            if (FindAFunValidator.checkNullString(url)) {
                Picasso.with(AllDetailsActivity.this).load(url).fit().transform(AllDetailsActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(photo);
            } else {
                photo.setImageResource(R.drawable.placeholder_small_old);
            }
            return convertView;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        Log.d(TAG, "Calculating the Listview height" + listAdapter.getCount());
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            if (i == 2) {
                break;
            }
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //Engagement adpater
    class EngagementDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if ((mdetailsBoard.getEngagementsList() != null) && (mdetailsBoard.getEngagementsList().getDataArr() != null)) {
                return mdetailsBoard.getEngagementsList().getDataArr().size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return mdetailsBoard.getEngagementsList().getDataArr().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.alldetails_list_row, parent, false);
            }
            ImageView photo = (ImageView) convertView.findViewById(R.id.event_image);
            TextView eventdetail = (TextView) convertView.findViewById(R.id.event_detail);
            TextView starcount = (TextView) convertView.findViewById(R.id.event_star_count);
            TextView timedetail = (TextView) convertView.findViewById(R.id.event_time);
            String url = null;
            if (mdetailsBoard.getEngagementsList().getDataArr() != null) {
                Engagement engagement = mdetailsBoard.getEngagementsList().getDataArr().get(position);
                if (engagement.getEventName() != null) {
                    SpannableString ss1 = new SpannableString(engagement.getEventName());
                    ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
                    eventdetail.append("You reviewed event ");
                    eventdetail.append(ss1);
                    // eventdetail.setText(engagement.getEventName());
                }
                if (engagement.getDate() != null) {
                    timedetail.setText(engagement.getDate());
                }
                if (engagement.getImageUrl() != null) {
                    url = engagement.getImageUrl();
                }

            }

            if (FindAFunValidator.checkNullString(url)) {
                Picasso.with(AllDetailsActivity.this).load(url).fit().transform(AllDetailsActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(photo);
            } else {
                photo.setImageResource(R.drawable.placeholder_small_old);
            }

            return convertView;
        }
    }

    //CHeckins adpater
    class CheckinsDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if ((mdetailsBoard.getCheckinList() != null) && (mdetailsBoard.getCheckinList().getDataArr() != null)) {
                return mdetailsBoard.getCheckinList().getDataArr().size();
            } else {
                return 0;
            }

        }

        @Override
        public Object getItem(int position) {
            return mdetailsBoard.getCheckinList().getDataArr().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.alldetails_list_row, parent, false);
            }
            ImageView photo = (ImageView) convertView.findViewById(R.id.event_image);
            TextView eventdetail = (TextView) convertView.findViewById(R.id.event_detail);
            TextView starcount = (TextView) convertView.findViewById(R.id.event_star_count);
            TextView timedetail = (TextView) convertView.findViewById(R.id.event_time);
            String url = null;
            if (mdetailsBoard.getCheckinList().getDataArr() != null) {
                Checkins data = mdetailsBoard.getCheckinList().getDataArr().get(position);
                if (data.getEventName() != null) {
                    SpannableString ss1 = new SpannableString(data.getEventName());
                    ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
                    eventdetail.append("You checked in at event ");
                    eventdetail.append(ss1);
                    // eventdetail.setText(engagement.getEventName());
                }
                if (data.getDate() != null) {
                    timedetail.setText(data.getDate());
                }
                if (data.getImageUrl() != null) {
                    url = data.getImageUrl();
                }

            }

            if (FindAFunValidator.checkNullString(url)) {
                Picasso.with(AllDetailsActivity.this).load(url).fit().transform(AllDetailsActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(photo);
            } else {
                photo.setImageResource(R.drawable.placeholder_small_old);
            }
            return convertView;
        }
    }

    //CHeckins adpater
    class BookingsDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if ((mdetailsBoard.getBookingList() != null) && (mdetailsBoard.getBookingList().getDataArr() != null)) {
                return mdetailsBoard.getBookingList().getDataArr().size();
            } else {
                return 0;
            }

        }

        @Override
        public Object getItem(int position) {
            return mdetailsBoard.getBookingList().getDataArr().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.alldetails_list_row, parent, false);
            }
            ImageView photo = (ImageView) convertView.findViewById(R.id.event_image);
            TextView eventdetail = (TextView) convertView.findViewById(R.id.event_detail);
            TextView starcount = (TextView) convertView.findViewById(R.id.event_star_count);
            TextView timedetail = (TextView) convertView.findViewById(R.id.event_time);
            String url = null;
            if (mdetailsBoard.getBookingList().getDataArr() != null) {
                Booking data = mdetailsBoard.getBookingList().getDataArr().get(position);
                if (data.getEventName() != null) {
                    SpannableString ss1 = new SpannableString(data.getEventName());
                    ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
                    eventdetail.append("You booked tickets for the event ");
                    eventdetail.append(ss1);
                    // eventdetail.setText(engagement.getEventName());
                }
                if (data.getDate() != null) {
                    timedetail.setText(data.getDate());
                }
                if (data.getImageUrl() != null) {
                    url = data.getImageUrl();
                }

            }

            if (FindAFunValidator.checkNullString(url)) {
                Picasso.with(AllDetailsActivity.this).load(url).fit().transform(AllDetailsActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(photo);
            } else {
                photo.setImageResource(R.drawable.placeholder_small_old);
            }
            return convertView;
        }
    }
}
