package com.findafun.servicehelpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.findafun.R;
import com.findafun.app.AppController;
import com.findafun.bean.gamification.BookingsBoard;
import com.findafun.bean.gamification.CheckinsBoard;
import com.findafun.bean.gamification.EngagementBoard;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.bean.gamification.LeaderBoard;
import com.findafun.bean.gamification.PhotoDetail;
import com.findafun.bean.gamification.PhotosBoard;
import com.findafun.bean.gamification.Rewards;
import com.findafun.bean.gamification.alldetails.AllDetailsBoard;
import com.findafun.serviceinterfaces.IGamificationServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BXDC46 on 1/22/2016.
 */
public class GamificationServiceHelper {
    private String TAG = GamificationServiceHelper.class.getSimpleName();
    private Context context;


    public GamificationServiceHelper(Context context) {
        this.context = context;
    }

    public void fetchGamificationDetails(String url,final IGamificationServiceListener listener){
        Log.d(TAG,"fetchGamificationListener"+ url);
       // String urlString = String.format("http://www.mocky.io/v2/56a254c30f00005339a0f34e");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            //Parse the response and convert to Java class
                            Gson gson = new Gson();
                            Rewards rewards = gson.fromJson(response.toString(), Rewards.class);
                            if(rewards != null){
                                listener.onSuccess(0,rewards);
                            }else{
                                Log.e(TAG,"Couldnt get a Gson");
                                listener.onError("JSON Parser error");
                            }


                        }catch(Exception e){
                            Log.d(TAG,"Exception while parsing");
                            e.printStackTrace();
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    //fetch Leaderboard details
    public void fetchLeaderBoardDetails(String url, final IGamificationServiceListener listener){
        Log.d(TAG,"Fetch gamification leaderboard details"+ url);
        String urlString = String.format("http://www.mocky.io/v2/56a2550f0f00005539a0f34f");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG,"Received JSON response"+ response.toString());

                        GamificationDataHolder.getInstance().clearLeaderBoardData();
                        for(int i=0; i< response.length(); i++){
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Gson gson = new Gson();
                                LeaderBoard board = gson.fromJson(obj.toString(), LeaderBoard.class);
                                if(board != null){
                                    GamificationDataHolder.getInstance().addLeaderBoard(board);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d(TAG,"Total leader board count"+ GamificationDataHolder.getInstance().getLeaderboardCount());
                        listener.onSuccess(0, response);

                    }
                }, new com.android.volley.Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null && error.networkResponse.data != null) {

                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                                } catch (UnsupportedEncodingException e) {
                                    listener.onError(context.getResources().getString(R.string.error_occured));
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    listener.onError(context.getResources().getString(R.string.error_occured));
                                    e.printStackTrace();
                                }

                            } else {
                                listener.onError(context.getResources().getString(R.string.error_occured));
                            }
                        }
                });


        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    //fetch Photo details
    public void fetchPhotoDetails(String url, final IGamificationServiceListener listener){
        Log.d(TAG,"Fetching the photo details");
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        /*String testResult = "{\n" +
                                "   \"totalPoints\":\"5\",\n" +
                                "   \"totalPhotos\":\"14\",\n" +
                                "   \"allPhotos\":[\n" +
                                "      {\n" +
                                "         \"date\":\"25-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Screen_Shot_2016-02-03_at_5.47.57_pm.png\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"25-Dec-2015\",\n" +
                                "         \"image_url\":\"http://http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"26-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"25-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      }\n" +
                                "   ]\n" +
                                "}";*/
                        try {
                            //Parse the response and convert to Java class
                            Gson gson = new Gson();
                            PhotosBoard board = gson.fromJson(response.toString(), PhotosBoard.class);
                            if(board != null){
                                GamificationDataHolder dataHolder = GamificationDataHolder.getInstance();
                                dataHolder.setmTotalPhotoPoints(board.getTotalPoints());
                                dataHolder.setmPhotosBoardTotalPhotos(board.getTotalPhotos());
                                //Clear the exitin date and image URl'
                                dataHolder.clearPhotosData();
                                HashMap<String,List<String>> imagealbum = dataHolder.getmImageAlbum();
                                List<String> dateList = dataHolder.getmPhotoDates();

                                for(PhotoDetail photodetail: board.getAllPhotos()){
                                    String date = photodetail.getDate();
                                    if(date != null){
                                        //check if date already exists
                                        if(imagealbum.containsKey(date) ){

                                            List<String> array = imagealbum.get(date);
                                            Log.d(TAG,"image list already exists"+array.size());
                                            array.add(photodetail.getImageUrl());
                                            Log.d(TAG, "image list after adding" + array.size());

                                        }else{
                                            Log.d(TAG,"adding new sub list of images");
                                            dateList.add(date);
                                            List<String> array = new ArrayList<String>();
                                            array.add(photodetail.getImageUrl());
                                            imagealbum.put(date, array);

                                        }
                                    }
                                }

                                listener.onSuccess(0,board);
                            }else{
                                Log.e(TAG,"Couldnt get a Gson");
                                listener.onError("JSON Parser error");
                            }


                        }catch(Exception e){
                            Log.d(TAG,"Exception while parsing");
                            e.printStackTrace();
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    //fetch Engagement details
    public void fetchEngagementDetails(String url, final IGamificationServiceListener listener){
        Log.d(TAG,"Fetch gamification Engagement details"+ url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            //Parse the response and convert to Java class
                            Gson gson = new Gson();
                            EngagementBoard engagementboard = gson.fromJson(response.toString(), EngagementBoard.class);
                            if(engagementboard != null){
                                EngagementBoard currentboard = GamificationDataHolder.getInstance().getmEngagementBoardDdetails();
                                currentboard.setEngagementsPoints(engagementboard.getEngagementsPoints());
                                currentboard.setEngagementsCount(engagementboard.getEngagementsCount());
                                currentboard.setDataArr(engagementboard.getDataArr());
                                listener.onSuccess(0,engagementboard);
                            }else{
                                Log.e(TAG,"Couldnt get a Gson");
                                listener.onError("JSON Parser error");
                            }


                        }catch(Exception e){
                            Log.d(TAG,"Exception while parsing");
                            e.printStackTrace();
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    //fetch Booking details
    public void fetchBookingDetails(String url, final IGamificationServiceListener listener){
        Log.d(TAG,"Fetch gamification Booking details"+ url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            //Parse the response and convert to Java class
                            Gson gson = new Gson();
                            BookingsBoard engagementboard = gson.fromJson(response.toString(), BookingsBoard.class);
                            if(engagementboard != null){
                                BookingsBoard currentboard = GamificationDataHolder.getInstance().getmBookingBoard();
                                if(engagementboard.getBookingPoints() != null) {
                                    currentboard.setBookingPoints(engagementboard.getBookingPoints());
                                }
                                if(engagementboard.getBookingCount() != null) {
                                    currentboard.setBookingCount(engagementboard.getBookingCount());
                                }
                                currentboard.setDataArr(engagementboard.getDataArr());
                                listener.onSuccess(0,engagementboard);
                            }else{
                                Log.e(TAG,"Couldnt get a Gson");
                                listener.onError("JSON Parser error");
                            }


                        }catch(Exception e){
                            Log.d(TAG,"Exception while parsing");
                            e.printStackTrace();
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    //fetch Booking details
    public void fetchCheckinsDetails(String url, final IGamificationServiceListener listener){
        Log.d(TAG,"Fetch gamification Checkins details"+ url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            //Parse the response and convert to Java class
                            Gson gson = new Gson();
                            CheckinsBoard engagementboard = gson.fromJson(response.toString(), CheckinsBoard.class);
                            if(engagementboard != null){
                                CheckinsBoard currentboard = GamificationDataHolder.getInstance().getmCheckinsBoard();
                                if(engagementboard.getCheckinPoints() != null) {
                                    currentboard.setCheckinPoints(engagementboard.getCheckinPoints());
                                }
                                if(engagementboard.getCheckinCount() != null) {
                                    currentboard.setCheckinCount(engagementboard.getCheckinCount());
                                }
                                currentboard.setDataArr(engagementboard.getDataArr());
                                listener.onSuccess(0,engagementboard);
                            }else{
                                Log.e(TAG,"Couldnt get a Gson");
                                listener.onError("JSON Parser error");
                            }


                        }catch(Exception e){
                            Log.d(TAG,"Exception while parsing");
                            e.printStackTrace();
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    //fetch All Reward details
    public void fetchAllRewardsDetails(String url, final IGamificationServiceListener listener){
        Log.d(TAG,"Fetch gamification All rewards details"+ url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                       /*String result = "{\n" +
                                "\"photoList\":{\n" +
                                "   \"totalPoints\":\"5\",\n" +
                                "   \"totalPhotos\":\"14\",\n" +
                                "   \"allPhotos\":[                        \n" +
                                "      {\n" +
                                "         \"date\":\"25-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"25-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"26-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"25-Dec-2015\",\n" +
                                "         \"image_url\":\"http://heylaapp.com/app/logo/Penguins.jpg\"\n" +
                                "      }\n" +
                                "   ]\n" +
                                "},\n" +
                                "\"checkinList\":{\n" +
                                "   \"checkin_count\":\"10\",\n" +
                                "   \"checkinPoints\":\"10\",\n" +
                                "   \"dataArr\":[                         \n" +
                                "      {\n" +
                                "         \"date\":\"0000-00-00\",\n" +
                                "         \"image_url\":\"Testing\",\n" +
                                "         \"event_name\":\"Marathan\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"2016-01-21\",\n" +
                                "         \"image_url\":null,\n" +
                                "         \"event_name\":\"Marathan\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"2016-01-22\",\n" +
                                "         \"image_url\":\"ff\",\n" +
                                "         \"event_name\":\"Trivandrum Cyclathon 2016\"\n" +
                                "      }\n" +
                                "   ]\n" +
                                "},\n" +
                                "\"bookingList\":{                             \n" +
                                "   \"booking_count\":\"10\",\n" +
                                "   \"bookingPoints\":\"10\",\n" +
                                "   \"dataArr\":[                  \n" +
                                "      {\n" +
                                "         \"date\":\"0000-00-00\",\n" +
                                "         \"image_url\":\"Testing\",\n" +
                                "         \"event_name\":\"Marathan\",\n" +
                                "         \"ticket_count\":\"1\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"2016-01-21\",\n" +
                                "         \"image_url\":null,\n" +
                                "         \"event_name\":\"Marathan\",\n" +
                                "        \"ticket_count\":\"1\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"2016-01-22\",\n" +
                                "         \"image_url\":\"ff\",\n" +
                                "         \"event_name\":\"Trivandrum Cyclathon 2016\",\n" +
                                "         \"ticket_count\":\"1\"\n" +
                                "      }\n" +
                                "   ]\n" +
                                "},\n" +
                                "\"engagementsList\":{\n" +
                                "   \"engagements_count\":\"13\",\n" +
                                "   \"engagementsPoints\":\"195\",\n" +
                                "   \"dataArr\":[                                  \n" +
                                "      {\n" +
                                "         \"date\":\"0000-00-00\",\n" +
                                "         \"image_url\":\"\",\n" +
                                "         \"event_name\":\"Times Asia Wedding Fair 2016\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"0000-00-00\",\n" +
                                "         \"image_url\":\"\",\n" +
                                "         \"event_name\":\"Electronics Rocks 2016\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "         \"date\":\"0000-00-00\",\n" +
                                "         \"image_url\":\"\",\n" +
                                "         \"event_name\":\"Times Asia Wedding Fair 2016\"\n" +
                                "      }\n" +
                                "   ]\n" +
                                "}\n" +
                                "}\n";*/
                        try {
                            //Parse the response and convert to Java class
                            Gson gson = new Gson();
                            AllDetailsBoard engagementboard = gson.fromJson(response.toString(), AllDetailsBoard.class);
                            if(engagementboard != null){
                                AllDetailsBoard currentboard = GamificationDataHolder.getInstance().getmAllDetailsBoard();
                                currentboard.setmFetchData(false);
                                if(engagementboard.getEngagementsList() != null) {
                                    currentboard.setEngagementsList(engagementboard.getEngagementsList());
                                }
                                if(engagementboard.getBookingList() != null) {
                                    currentboard.setBookingList(engagementboard.getBookingList());
                                }
                                if(engagementboard.getCheckinList() != null){
                                    currentboard.setCheckinList(engagementboard.getCheckinList());
                                }
                                if(engagementboard.getPhotoList() != null){
                                    currentboard.setPhotoList(engagementboard.getPhotoList());
                                }
                                listener.onSuccess(0,engagementboard);
                            }else{
                                Log.e(TAG,"Couldnt get a Gson");
                                listener.onError("JSON Parser error");
                            }


                        }catch(Exception e){
                            Log.d(TAG,"Exception while parsing");
                            e.printStackTrace();
                            listener.onError("JSON Parser error");
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        listener.onError(jsonObject.getString(FindAFunConstants.PARAM_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    } catch (JSONException e) {
                        listener.onError(context.getResources().getString(R.string.error_occured));
                        e.printStackTrace();
                    }

                } else {
                    listener.onError(context.getResources().getString(R.string.error_occured));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }


}
