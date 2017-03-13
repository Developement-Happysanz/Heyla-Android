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
import com.findafun.bean.events.Event;
import com.findafun.helper.FindAFunHelper;
import com.findafun.utils.FindAFunValidator;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nandha on 02/05/2016.
 */
public class StaticEventListAdapter extends BaseAdapter {
    private static final String TAG = StaticEventListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<Event> events;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();

    public StaticEventListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(0)
                .oval(false)
                .build();
        mSearching = false;
    }

    @Override
    public int getCount() {
        if (mSearching) {
            // Log.d("Event List Adapter","Search count"+mValidSearchIndices.size());
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            // Log.d(TAG,"Normal count size");
            return events.size();
        }

    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return events.get(mValidSearchIndices.get(position));
        } else {
            return events.get(position);
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
            convertView = inflater.inflate(R.layout.static_event_list_item_new, parent, false);

            holder = new ViewHolder();
            holder.txtEventName = (TextView) convertView.findViewById(R.id.txt_event_name);
            holder.txtEventVenue = (TextView) convertView.findViewById(R.id.txt_event_venue);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_event_date);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txt_event_time);
            holder.txtCategory = (TextView) convertView.findViewById(R.id.txt_event_category);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img_logo);
            holder.adImage = (ImageView) convertView.findViewById(R.id.event_ad_image);
            holder.paidBtn = (Button) convertView.findViewById(R.id.event_paid_btn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            // Log.d("Event List Adapter","actual position"+ position);
            position = mValidSearchIndices.get(position);
            //Log.d("Event List Adapter", "position is"+ position);

        } else {
            // Log.d("Event List Adapter","getview pos called"+ position);
        }

        Event event = events.get(position);

        holder.txtEventName.setText(events.get(position).getEventName());
        String[] aux = events.get(position).getEventVenue().toString().split(",\\s*");
        String result = "";
        if (aux.length > 2) {
            result = aux[aux.length - 2];
        } else {
            result = aux[aux.length - 1];
        }

        holder.txtEventVenue.setText(result);
        // Log.d("Event Adapter","event isAd "+ event.getIsAd());
        String isAd = event.getIsAd();
        if ((isAd != null) && (isAd.equalsIgnoreCase("1"))) {
            // Log.d("EventAdapter", "setting the ad image to visible");
            holder.adImage.setVisibility(View.VISIBLE);
        } else {
            holder.adImage.setVisibility(View.INVISIBLE);
        }
        String paidBtnVal = event.getEvent_cost();
        if (paidBtnVal != null) {
            holder.paidBtn.setText(event.getEvent_cost());
            if (paidBtnVal.equalsIgnoreCase("invite")) {
                holder.paidBtn.setTextColor(context.getResources().getColor(R.color.white)); //Blue
            } else if (paidBtnVal.equalsIgnoreCase("free")) {
                holder.paidBtn.setTextColor(context.getResources().getColor(R.color.White)); //Green
            } else if (paidBtnVal.equalsIgnoreCase("paid")) {
                holder.paidBtn.setTextColor(context.getResources().getColor(R.color.white)); //rounder_button
            }
        }

        //imageLoader.displayImage(events.get(position).getEventLogo(), holder.imageView, AppController.getInstance().getLogoDisplayOptions());
        if (FindAFunValidator.checkNullString(events.get(position).getEventLogo())) {
            Picasso.with(this.context).load(events.get(position).getEventLogo()).fit().transform(this.transformation).placeholder(R.drawable.ab_hobbistan_logo).error(R.drawable.ab_hobbistan_logo).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ab_hobbistan_logo);
        }
        String start = FindAFunHelper.getDate(events.get(position).getStartDate());
        String end = FindAFunHelper.getDate(events.get(position).getEndDate());

        if ((start != null) && (end != null)) {
            holder.txtDate.setText(start + "-" + end);
        } else {
            holder.txtDate.setText("N/A");
        }
        //fetch timer values
        start = FindAFunHelper.getTime(events.get(position).getStartDate());
        end = FindAFunHelper.getTime(events.get(position).getEndDate());
        String startTime = "", endTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date startDateObj = sdf.parse(start);
            final Date endDateObj = sdf.parse(end);
            System.out.println(startDateObj);
            startTime = (new SimpleDateFormat("hh:mm a").format(startDateObj));
            endTime = (new SimpleDateFormat("hh:mm a").format(endDateObj));
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        if ((startTime != null) && (end != null)) {
            holder.txtTime.setText(startTime + " - " + endTime);
        }

        holder.txtCategory.setText("  "+events.get(position).getCategoryName()+"  ");

        //Add animation if searching
       /* if(mSearching) {
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(convertView, "rotationX", 90, 0);
            // alphaAnim.setInterpolator(mInterpolator);
            alphaAnim.setDuration(300);
            alphaAnim.start();
        }*/
        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < events.size(); i++) {
            String eventname = events.get(i).getEventName();
            if ((eventname != null) && !(eventname.isEmpty())) {
                if (eventname.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }

            }

        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
        //notifyDataSetChanged();

    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
        // notifyDataSetChanged();
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtEventName, txtEventVenue, txtDate, txtTime, txtCategory;
        public ImageView imageView, adImage;
        public Button paidBtn;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}
