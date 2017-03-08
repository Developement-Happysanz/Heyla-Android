package com.findafun.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.findafun.R;

import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.PhotosBoard;
import com.findafun.bean.gamification.Rewards;
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
 * Created by BXDC46 on 1/23/2016.
 */
public class PhotosActivity extends AppCompatActivity implements IGamificationServiceListener, DialogClickListener {
    private static final String TAG = PhotosActivity.class.getName();
    private TextView mPhotosCountView = null;
    private TextView mTotalPhotosCountView = null;
    private ListDataAdapter mMainAdapter = null;
    private final Transformation transformation;
    private ProgressDialog mProgressDialog = null;
    private HashMap<Integer, GridDataAdapter> mIndividualAdapters = new HashMap<Integer, GridDataAdapter>();

    public PhotosActivity() {
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(4)
                .oval(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.photos_layout);
        mPhotosCountView = (TextView) findViewById(R.id.photo_value);
        mTotalPhotosCountView = (TextView) findViewById(R.id.total_photos);
        Rewards reward = GamificationDataHolder.getInstance().getmRewards();

        if (reward != null) {
            mPhotosCountView.setText(Integer.toString(reward.getTotalPoint()));
            mTotalPhotosCountView.setText(Integer.toString(reward.getPhotoSharingCount()));
        }

        ImageView backbtn = (ImageView) findViewById(R.id.bookings_back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ListView mPhotosList = (ListView) findViewById(R.id.photos_list);
        mMainAdapter = new ListDataAdapter();
        mPhotosList.setAdapter(mMainAdapter);
        //check if leader board data is already loaded
        // if((GamificationDataHolder.getInstance().getmTotalPhotoPoints() == null)){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.show();
        GamificationServiceHelper helper = new GamificationServiceHelper(this);
        //Integer.parseInt(PreferenceStorage.getUserId(this)))
        helper.fetchPhotoDetails(String.format(FindAFunConstants.GET_PHOTOS_URL, Integer.parseInt(PreferenceStorage.getUserId(this))), this);

        /*}else{
            String totalPhotoPoints = GamificationDataHolder.getInstance().getmTotalPhotoPoints();
            String totalPhotocount = GamificationDataHolder.getInstance().getmPhotosBoardTotalPhotos();
            if(totalPhotoPoints != null) {
                mPhotosCountView.setText(totalPhotoPoints);
            }
            if(totalPhotocount != null){
                mTotalPhotosCountView.setText(totalPhotocount);
            }

        }*/


    }

    @Override
    public void onSuccess(int resultCode, Object result) {

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        if ((result != null) && (result instanceof PhotosBoard)) {
            String totalPhotoPoints = GamificationDataHolder.getInstance().getmTotalPhotoPoints();
            String totalPhotocount = GamificationDataHolder.getInstance().getmPhotosBoardTotalPhotos();
            if (totalPhotoPoints != null) {
                mPhotosCountView.setText(totalPhotoPoints);
            }
            if (totalPhotocount != null) {
                mTotalPhotosCountView.setText(totalPhotocount);
            }
            Log.d(TAG, "Succesfully received Photos Board");

            mMainAdapter.notifyDataSetChanged();

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

    //Basic ListAdpater
    class ListDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return GamificationDataHolder.getInstance().getTotalPhotDates();


        }

        @Override
        public Object getItem(int position) {
            return GamificationDataHolder.getInstance().getPhotoDateat(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.photos_row_layout, parent, false);
            }
            TextView dateview = (TextView) convertView.findViewById(R.id.photos_date_val);
            GridView gridView = (GridView) convertView.findViewById(R.id.photos_grid);

            String dateval = GamificationDataHolder.getInstance().getPhotoDateat(position);
            if (dateval != null) {
                dateview.setText(dateval);
            }
            GridDataAdapter adapter = mIndividualAdapters.get(position);
            if (adapter == null) {
                Log.d(TAG, "New row. so adding grid adapter");
                adapter = new GridDataAdapter(position, dateval);
                mIndividualAdapters.put(position, adapter);
            }
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            return convertView;
        }
    }

    //Individual row Adapter for Grid View
    class GridDataAdapter extends BaseAdapter {
        int mPosition = -1;
        String mDate = null;

        public GridDataAdapter(int pos, String date) {
            super();
            mPosition = pos;
            mDate = date;
            Log.d(TAG, "set position for this Grid is" + pos);

        }

        @Override
        public int getCount() {
            int count = GamificationDataHolder.getInstance().getImageCountforDate(mDate);
            Log.d(TAG, "image count for grid with date" + mDate + "in pos" + mPosition + "is" + count);
            return count;

        }

        @Override
        public Object getItem(int position) {
            return GamificationDataHolder.getInstance().getImageUrlforDateAt(mDate, position);
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
            Log.d(TAG, "getview called for date" + mDate + "position" + position);
            ImageView photo = (ImageView) convertView.findViewById(R.id.photo_image);
            String url = GamificationDataHolder.getInstance().getImageUrlforDateAt(mDate, position);
            if (FindAFunValidator.checkNullString(url)) {
                Picasso.with(PhotosActivity.this).load(url).fit().transform(PhotosActivity.this.transformation).placeholder(R.drawable.placeholder_small_old).error(R.drawable.placeholder_small_old).into(photo);
            } else {
                photo.setImageResource(R.drawable.placeholder_small_old);
            }
            return convertView;
        }
    }
}
