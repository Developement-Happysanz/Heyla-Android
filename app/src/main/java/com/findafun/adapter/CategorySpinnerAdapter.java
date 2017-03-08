package com.findafun.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.findafun.R;

import java.util.ArrayList;

/**
 * Created by Data Crawl 6 on 27-Jun-16.
 */
public class CategorySpinnerAdapter extends ArrayAdapter{
    private static final String TAG = CategorySpinnerAdapter.class.getName();

    private Activity context;
    private ArrayList<String> values;

    public CategorySpinnerAdapter(Activity context, int resource, ArrayList<String> values)
    {
        super(context, resource, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // Log.d(TAG,"getView called");
        if (convertView == null) {
            LayoutInflater inflater = (context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.category_dropdown_item, parent, false);

            holder = new ViewHolder();
            holder.txtCategoryName = (TextView) convertView.findViewById(R.id.txt_category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtCategoryName.setText(values.get(position));

        // ... Fill in other views ...
        return convertView;
    }

    public class ViewHolder {
        public TextView txtCategoryName;
    }
}
