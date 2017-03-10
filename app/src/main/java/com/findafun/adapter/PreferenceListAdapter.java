package com.findafun.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.activity.SelectPreferenceActivity;
import com.findafun.bean.categories.Category;
import com.findafun.utils.FindAFunValidator;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by nandhakumar.k on 01/01/16.
 */
public class PreferenceListAdapter extends RecyclerView.Adapter<PreferenceListAdapter.ViewHolder> {
    private ArrayList<Category> categoryArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private final Transformation transformation;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mPrefTextView;
        public RelativeLayout rlPref;

        public ViewHolder(View v, int viewType) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.txt_preference_name);
            mPrefTextView = (TextView) v.findViewById(R.id.txt_pref_category_name);
            if (viewType == 1) {
                rlPref = (RelativeLayout)v.findViewById(R.id.rlPref);
            } else {
                rlPref = (RelativeLayout) v;
            }

            rlPref.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PreferenceListAdapter(Context context, ArrayList<Category> categoryArrayList, OnItemClickListener onItemClickListener) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PreferenceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View parentView;
        //Log.d("CategoryAdapter","viewType is"+ viewType);
        //if (viewType == 1) {
            parentView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.preference_view_type1, parent, false);

//        }
//        else {
//            parentView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.preference_view_type2, parent, false);
//        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(parentView, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mPrefTextView.setText(categoryArrayList.get(position).getCategory());

        //imageLoader.displayImage(events.get(position).getEventLogo(), holder.imageView, AppController.getInstance().getLogoDisplayOptions());
        if(FindAFunValidator.checkNullString(categoryArrayList.get(position).getImgPath())) {
            Picasso.with(this.context).load(categoryArrayList.get(position).getImgPath()).fit().transform(this.transformation).placeholder(R.drawable.ab_hobbistan_logo).error(R.drawable.ab_hobbistan_logo).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ab_hobbistan_logo);
        }

//        GradientDrawable bgShape = (GradientDrawable) holder.mPrefTextView.getBackground();
        if (categoryArrayList.get(position).getCategoryPreference().equals("no")) {
            // holder.tickImage.setVisibility(View.INVISIBLE);
            holder.rlPref.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.mPrefTextView.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            if (context instanceof SelectPreferenceActivity) {
                ((SelectPreferenceActivity) context).onCategorySelected(position);
            }
//            holder.mPrefTextView.setTextColor(context.getResources().getColor(R.color.preference_orange));
            holder.rlPref.setBackgroundColor(context.getResources().getColor(R.color.preference_orange));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }


    @Override
    public int getItemViewType(int position) {
     /*   if ((position + 1) % 7 == 4 || (position + 1) % 7 == 0) {
            return 2;
        } else {
            return 1;
        }*/
        if(categoryArrayList.get(position)!=null || categoryArrayList.get(position).getSize()>0)
            return categoryArrayList.get(position).getSize();
        else
            return 1;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
}