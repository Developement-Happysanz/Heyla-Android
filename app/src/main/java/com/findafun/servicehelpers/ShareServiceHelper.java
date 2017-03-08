package com.findafun.servicehelpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.utils.FindAFunConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by BXDC46 on 1/23/2016.
 */
public class ShareServiceHelper {
    private String TAG = ShareServiceHelper.class.getSimpleName();

    private Context context;


    public ShareServiceHelper(Context context) {
        this.context = context;
    }

    public void postShareDetails(String url,final IGamificationServiceListener listener){
        Log.d(TAG, "share status " + url);
        // String urlString = String.format("http://www.mocky.io/v2/56a254c30f00005339a0f34e");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            //Parse the response and convert to Java class
                            if(listener != null)
                                listener.onSuccess(0,response);


                        }catch(Exception e){
                            Log.d(TAG, "Exception while parsing");
                            e.printStackTrace();
                            if(listener != null)
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.d(TAG,"error status"+ responseBody);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if(listener != null)
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        if(listener != null)
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        if(listener != null)
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    if(listener != null)
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }
}
