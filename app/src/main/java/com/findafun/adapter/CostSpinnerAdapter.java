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
 * Created by DataCrawl on 8/29/2016.
 */
public class CostSpinnerAdapter  extends ArrayAdapter {
    private static final String TAG = CostSpinnerAdapter.class.getName();

    private Activity context;
    private ArrayList<String> values;

    public CostSpinnerAdapter(Activity context, int resource, ArrayList<String> values)
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
            convertView = inflater.inflate(R.layout.cost_dropdown_item, parent, false);

            holder = new ViewHolder();
            holder.txtCostName = (TextView) convertView.findViewById(R.id.txt_cost_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtCostName.setText(values.get(position));

        // ... Fill in other views ...
        return convertView;
    }

    /*@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.city_dropdown_item, parent, false);

            holder = new ViewHolder();
            holder.txtCityName = (TextView) convertView.findViewById(R.id.txt_city_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d(TAG,"setting city name");

        holder.txtCityName.setText(values.get(position));

        return convertView;
    }*/

    public class ViewHolder {
        public TextView txtCostName;
    }
}
