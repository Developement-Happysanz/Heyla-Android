package com.findafun.servicehelpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.findafun.R;
import com.findafun.activity.LandingActivity;
import com.findafun.app.AppController;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by zahid.r on 10/30/2015.
 */
public class EventServiceHelper {
    private String TAG = LandingActivity.class.getSimpleName();
    private Context context;
    IEventServiceListener eventServiceListener;

    public EventServiceHelper(Context context) {
        this.context = context;
    }

    public void setEventServiceListener(IEventServiceListener eventServiceListener) {
        this.eventServiceListener = eventServiceListener;
    }

    public void makeGetEventServiceCall(String URL) {
        Log.d(TAG, "Events URL" + URL);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "ajaz : " + response.toString());
                        if(response != null) {
                            eventServiceListener.onEventResponse(response);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error is" + error.getLocalizedMessage());
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.d(TAG, "error response body is" + responseBody);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        eventServiceListener.onEventError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        eventServiceListener.onEventError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        eventServiceListener.onEventError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    eventServiceListener.onEventError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public String makeRawRequest(String Url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(Url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("func_name", "advanced_event_management");
            String singledate = PreferenceStorage.getFilterSingleDate(context);
            if (!singledate.equalsIgnoreCase("")) {
                jsonObject.accumulate("single_date", singledate);
            }
            String eventType = PreferenceStorage.getFilterEventType(context);
            if (!eventType.equalsIgnoreCase("")) {
                jsonObject.accumulate("event_type", eventType);
            }
            String catgry = PreferenceStorage.getFilterCatgry(context);
            if (!catgry.equalsIgnoreCase("")) {
                jsonObject.accumulate("selected_category", catgry);
            }
            String city = PreferenceStorage.getFilterCity(context);
            if (!city.equalsIgnoreCase("")) {
                jsonObject.accumulate("selected_city", city);
            }
            String fromdate = PreferenceStorage.getFilterFromDate(context);
            if (!fromdate.equalsIgnoreCase("")) {
                jsonObject.accumulate("from_date", fromdate);
            }
            String todate = PreferenceStorage.getFilterToDate(context);
            if (!todate.equalsIgnoreCase("")) {
                jsonObject.accumulate("to_date", todate);
            }



            // jsonObject.accumulate("twitter", person.getTwitter());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("Reqjson", "ajazFilter Reqjson: " + json);
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null) {
                
                try {
                    result = convertInputStreamToString(inputStream);
                   // Log.d(TAG, "ajazFilter : " + result.toString());
                    JSONObject obj = new JSONObject(result);
                    eventServiceListener.onEventResponse(obj);

                    Log.d("ajazFilter resultjson: ", obj.toString());

                } catch (Exception t) {
                    Log.e("ajazFilterExce : ", "ajazFilter : ", t);
                }
                // eventServiceListener.onEventResponse(result);
            } else {
                result = "Did not work!";
                Log.d(TAG, "ajazFilter : " + result.toString());
            }

        } catch (Exception e) {
            //  e.printStackTrace();
            Log.e("InputStream", "ajazFilter : ", e);
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
