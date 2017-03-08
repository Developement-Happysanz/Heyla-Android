package com.findafun.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.events.BookPlan;
import com.findafun.bean.events.BookPlanList;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Nandha on 11-12-2016.
 */

public class BookingPlanAdapter extends BaseAdapter {

    private static final String TAG = BookingPlanAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<BookPlan> bookPlan;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices =new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();


    public BookingPlanAdapter(Context context, ArrayList<BookPlan> bookPlan) {

        this.context = context;
        this.bookPlan = bookPlan;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
        mSearching = false;
    }

    @Override
    public int getCount() {
        if(mSearching){
            // Log.d("Event List Adapter","Search count"+mValidSearchIndices.size());
            if(!mAnimateSearch){
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        }else{
            // Log.d(TAG,"Normal count size");
            return bookPlan.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(mSearching){
            return bookPlan.get(mValidSearchIndices.get(position));
        }else {
            return bookPlan.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.plans_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtPlanName = (TextView) convertView.findViewById(R.id.txt_plan_name);
            holder.txtPlanRate = (TextView) convertView.findViewById(R.id.txt_plan_rate);
            holder.continueBtn = (Button) convertView.findViewById(R.id.plan_continue_btn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BookPlan plan = bookPlan.get(position);

        holder.txtPlanName.setText(bookPlan.get(position).getSeatPlan());
        holder.txtPlanRate.setText("Rs : "+bookPlan.get(position).getSeatRate());



        return convertView;
    }

    public class ViewHolder {
        public TextView txtPlanName, txtPlanRate;
        public Button continueBtn;
    }

    public void startSearch(String eventName){
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter","serach for event"+eventName);
        mValidSearchIndices.clear();
        for(int i =0; i< bookPlan.size(); i++){
            String planName = bookPlan.get(i).getSeatPlan();
            if((planName != null) && !(planName.isEmpty())){
                if( planName.toLowerCase().contains(eventName.toLowerCase())){
                    mValidSearchIndices.add(i);
                }

            }

        }
        Log.d("Event List Adapter","notify"+ mValidSearchIndices.size());
        //notifyDataSetChanged();

    }

    public void exitSearch(){
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
        // notifyDataSetChanged();
    }

    public void clearSearchFlag(){
        mSearching = false;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos){
        if(selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        }else{
            return 0;
        }
    }
}
