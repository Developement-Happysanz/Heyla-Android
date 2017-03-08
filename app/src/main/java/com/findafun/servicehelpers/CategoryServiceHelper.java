package com.findafun.servicehelpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.findafun.R;
import com.findafun.activity.SelectPreferenceActivity;
import com.findafun.app.AppController;
import com.findafun.serviceinterfaces.ICategoryServiceListener;

import com.findafun.utils.FindAFunConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by zahid.r on 10/30/2015.
 */
public class CategoryServiceHelper {
    private String TAG = SelectPreferenceActivity.class.getSimpleName();
    private Context context;
    ICategoryServiceListener categoryServiceListener;

    public CategoryServiceHelper(Context context) {
        this.context = context;
    }

    public void setCategoryServiceListener(ICategoryServiceListener categoryServiceListener) {
        this.categoryServiceListener = categoryServiceListener;
    }

    public void makeGetCategoryServiceCall(JSONObject jsonObject) {
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                FindAFunConstants.GET_CATEGORY_URL, jsonObject,
                new com.android.volley.Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        categoryServiceListener.onCategoryResponse(response);
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        categoryServiceListener.onCategoryError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        categoryServiceListener.onCategoryError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        categoryServiceListener.onCategoryError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    categoryServiceListener.onCategoryError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    public void makeSetCategoryServiceCall(String params) {
        Log.d(TAG,"size of selected preferences"+ params);
        final JsonObjectRequest jsonOnjectRequest = new JsonObjectRequest(Request.Method.POST,
                FindAFunConstants.SET_CATEGORY_URL, params,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        categoryServiceListener.onSetCategoryResponse(response);
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        categoryServiceListener.onSetCategoryError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        categoryServiceListener.onSetCategoryError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        categoryServiceListener.onSetCategoryError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    categoryServiceListener.onSetCategoryError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonOnjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonOnjectRequest);

    }
}
