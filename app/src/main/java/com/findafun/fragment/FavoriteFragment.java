package com.findafun.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.activity.EventDetailActivity;
import com.findafun.activity.NearbyActivity;
import com.findafun.bean.events.Event;
import com.findafun.bean.events.EventList;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.LocationHelper;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Data Crawl 6 on 13-05-2016.
 */
public class FavoriteFragment extends LandingPagerFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = FavoriteFragment.class.getName();
    private String listFlag = null;
    private MapView mMapView = null;
    private GoogleMap mGoogleMap = null;
    GoogleApiClient mGoogleApiClient = null;
    private boolean mMapLoaded = false;
    Location mLastLocation = null;
    private ImageButton mLocationBtn;
    private List<Marker> mAddedMarkers = new ArrayList<Marker>();
    private HashMap<LatLng, Event> mDisplayedEvents = new HashMap<LatLng, Event>();
    private boolean mAddddLocations = true;
    private TextView mTotalEventCount = null;
    private boolean isFirstRunLocation = true;
    private boolean isFirstRunNearby = true;
    private boolean isFirstRunList = true;
    private SharedPreferences TransPrefs;
    private Drawable mNearbyTabUnselected = null;
    private Drawable mNearbyTabSelected = null;

    private Drawable mLocationUnselected = null;
    private Drawable mLocationSelected = null;
    private Drawable mListUnselected = null;
    private Drawable mListSelected = null;

    //icons
    private Drawable mselectednearbyicon = null;
    private Drawable munselectednearbyicon = null;

    //
    private Drawable mselectedlocationicon = null;
    private Drawable munselectedlocationicon = null;
    private Drawable mselectedlisticon = null;
    private Drawable munselectedlisticon = null;
    private BitmapDescriptor mMapIcon = null;

    private float mStartX;
    private float mStartY;
    private float mEndX;
    private float mEndY;

    private ProgressDialog mLocationProgress = null;
    private boolean mNearbySelected = false;
    private int mTotalReceivedEvents = 0;

    private int distanceFlag = 1;

    public static final CameraPosition COIMBATORE =
            new CameraPosition.Builder().target(new LatLng(11.00, 77.00))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();


    HashMap<Integer, String> latitude = new HashMap<Integer, String>();
    HashMap<Integer, String> longitude = new HashMap<Integer, String>();

    public static FavoriteFragment newInstance(int position) {
        FavoriteFragment frag = new FavoriteFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Nearby fragment onCreateView called");
        view = inflater.inflate(R.layout.favorite_layout, container, false);
        TransPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        isFirstRunLocation = TransPrefs.getBoolean("isFirstRunLocation", true);
        isFirstRunNearby = TransPrefs.getBoolean("isFirstRunNearby", true);
        isFirstRunList = TransPrefs.getBoolean("isFirstRunList", true);

        initializeViews();
        initializeEventHelpers();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent addEventIntent = new Intent(getActivity(), NearbyActivity.class);
                //navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addEventIntent);
            }


        });

     /*   FabSpeedDial fabSpeedDial = (FabSpeedDial) view.findViewById(R.id.fab_menu);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                return false;
            }
        }); */


        listFlag = "Full";

        mAddddLocations = true;
        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        setUpGoogleMaps();

        mNearbyTabUnselected = getActivity().getResources().getDrawable(R.drawable.btn_rounded_red);
        mNearbyTabSelected = getActivity().getResources().getDrawable(R.drawable.btn_rounded_white);

        mLocationUnselected = getActivity().getResources().getDrawable(R.drawable.btn_square_red);
        mLocationSelected = getActivity().getResources().getDrawable(R.drawable.btn_square_white);

        mListUnselected = getActivity().getResources().getDrawable(R.drawable.btn_rounded_red_rightside);
        mListSelected = getActivity().getResources().getDrawable(R.drawable.btn_rounded_white_right);

        mselectednearbyicon = getActivity().getResources().getDrawable(R.drawable.nearby_selected);
        munselectednearbyicon = getActivity().getResources().getDrawable(R.drawable.nearby_unselected);

        mselectedlocationicon = getActivity().getResources().getDrawable(R.drawable.map_selected);
        munselectedlocationicon = getActivity().getResources().getDrawable(R.drawable.map_unselected);

        mselectedlisticon = getActivity().getResources().getDrawable(R.drawable.list_selected);
        munselectedlisticon = getActivity().getResources().getDrawable(R.drawable.list_unselected);

        mMapIcon = BitmapDescriptorFactory.fromResource(R.drawable.location_dot_img);

        mTotalEventCount = (TextView) view.findViewById(R.id.nearby_totalevents);
        mLocationBtn = (ImageButton) view.findViewById(R.id.nearby_location_btn);

        final ImageButton listAppearence = (ImageButton) view.findViewById(R.id.nearby_grid_view_btn);

        final ImageButton listAppearenceNearBy = (ImageButton) view.findViewById(R.id.nearby_list_btn);


        listAppearenceNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*listFlag = "Near";
                performSlideRightAnimation();
                //LoadListView(response);
                mLocationBtn.setBackgroundDrawable(mLocationUnselected);
                listAppearence.setBackgroundDrawable(mListUnselected);
                listAppearenceNearBy.setBackgroundDrawable(mNearbyTabSelected);

                mLocationBtn.setImageDrawable(munselectedlocationicon);
                listAppearence.setImageDrawable(munselectedlisticon);
                listAppearenceNearBy.setImageDrawable(mselectednearbyicon);

                mTotalEventCount.setText(Integer.toString(eventsArrayList.size()) + " Nearby Events");*/

           /*     distanceFlag = 2;

                LocationHelper.FindLocationManager(getContext());

                mMapView.setVisibility(View.VISIBLE);
                performSlideLeftAnimation();
                mLocationBtn.setBackgroundDrawable(mLocationUnselected);
                listAppearence.setBackgroundDrawable(mListUnselected);
                listAppearenceNearBy.setBackgroundDrawable(mNearbyTabSelected);

                mLocationBtn.setImageDrawable(munselectedlocationicon);
                listAppearence.setImageDrawable(munselectedlisticon);
                listAppearenceNearBy.setImageDrawable(mselectednearbyicon);

                mTotalEventCount.setText(Integer.toString(eventsArrayList.size()) + " Nearby Events");

                final Dialog dialog = new Dialog(getContext(),android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.transparent_favourite);
                dialog.show();

                if (isFirstRunNearby) {

                    final TextView txtNearby = (TextView) dialog.findViewById(R.id.trans_nearby_events);

                    txtNearby.setVisibility(View.VISIBLE);
                    txtNearby.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    isFirstRunNearby=false;
                    TransPrefs.edit().putBoolean("isFirstRunNearby", isFirstRunNearby).commit();


                } else {
                    dialog.dismiss();

                }*/

                performSlideLeftAnimation();
                mMapView.setVisibility(View.GONE);
                mLocationBtn.setBackgroundDrawable(mLocationUnselected);
                listAppearence.setBackgroundDrawable(mListUnselected);
                listAppearenceNearBy.setBackgroundDrawable(mNearbyTabSelected);
                mLocationBtn.setImageDrawable(munselectedlocationicon);
                listAppearence.setImageDrawable(munselectedlisticon);
                listAppearenceNearBy.setImageDrawable(mselectednearbyicon);

                Intent addEventIntent = new Intent(getActivity(), NearbyActivity.class);
                //navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addEventIntent);

            }

        });

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreListView.setVisibility(View.GONE);
                LocationHelper.FindLocationManager(getContext());

                mMapView.setVisibility(View.VISIBLE);
                performSlideLeftAnimation();
                mLocationBtn.setBackgroundDrawable(mLocationSelected);
                listAppearence.setBackgroundDrawable(mListUnselected);
                listAppearenceNearBy.setBackgroundDrawable(mNearbyTabUnselected);
                mLocationBtn.setImageDrawable(mselectedlocationicon);
                listAppearence.setImageDrawable(munselectedlisticon);
                listAppearenceNearBy.setImageDrawable(munselectednearbyicon);

                mTotalEventCount.setText(Integer.toString(eventsArrayList.size()) + " Favorite Events");



       /*         final Dialog dialog = new Dialog(getContext(),android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.transparent_favourite);
                dialog.show();

                if (isFirstRunLocation) {

                    final TextView txtMap = (TextView) dialog.findViewById(R.id.trans_map);

                    txtMap.setVisibility(View.VISIBLE);
                    txtMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    isFirstRunLocation=false;
                    TransPrefs.edit().putBoolean("isFirstRunLocation", isFirstRunLocation).commit();


                } else {
                    dialog.dismiss();

                } */



            }
        });

        //by default set it to disabled
        mLocationBtn.setEnabled(false);
        mDisplayedEvents.clear();

        listAppearence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMapView.setVisibility(View.GONE);
                listFlag = "Full";
                performSlideRightAnimation();
                //LoadListView(response);
                mLocationBtn.setBackgroundDrawable(mLocationUnselected);
                listAppearence.setBackgroundDrawable(mListSelected);
                listAppearenceNearBy.setBackgroundDrawable(mNearbyTabUnselected);
                mLocationBtn.setImageDrawable(munselectedlocationicon);
                listAppearence.setImageDrawable(mselectedlisticon);
                listAppearenceNearBy.setImageDrawable(munselectednearbyicon);

                mTotalEventCount.setText(Integer.toString(eventsArrayList.size()) + " Favorite Events");

          /*      final Dialog dialog = new Dialog(getContext(),android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.transparent_favourite);
                dialog.show();

                if (isFirstRunList) {

                    final TextView txtList = (TextView) dialog.findViewById(R.id.trans_evntlist);

                    txtList.setVisibility(View.VISIBLE);
                    txtList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    isFirstRunList=false;
                    TransPrefs.edit().putBoolean("isFirstRunList", isFirstRunList).commit();


                } else {
                    dialog.dismiss();

                } */

                loadMoreListView.setVisibility(View.VISIBLE);
                if (eventsListAdapter != null) {
                    eventsListAdapter.notifyDataSetChanged();
                }
            }
        });
        mLocationBtn.setBackgroundDrawable(mLocationUnselected);
        listAppearence.setBackgroundDrawable(mListSelected);
        mLocationBtn.setImageDrawable(munselectedlocationicon);
        listAppearence.setImageDrawable(mselectedlisticon);
        return view;
    }

    protected void buildGoogleApiClient() {
        Log.d(TAG, "Initiate GoogleApi connection");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        mMapView.onResume();

        if ((mGoogleApiClient != null) && !mGoogleApiClient.isConnected()) {
            Log.d(TAG, "make api connect");
            mGoogleApiClient.connect();

        } else {
            Log.e(TAG, "googleapi is null");
        }
    }

    private void setUpGoogleMaps() {
        Log.d(TAG, "Setting up google maps");
        buildGoogleApiClient();
        mGoogleMap = null;
        mMapView.getMapAsync(this);
        mAddedMarkers.clear();
        mDisplayedEvents.clear();
       /* if(mGoogleMap != null){
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Log.d(TAG,"Getting the info view contents");

                    View infowindow = getActivity().getLayoutInflater().inflate(R.layout.map_info_window_layout, null);
                    LatLng pos = marker.getPosition();
                    if(pos != null) {
                        Event event = mDisplayedEvents.get(pos);
                        if(event != null) {
                            TextView title = (TextView) infowindow.findViewById(R.id.info_window_Title);
                            TextView subTitle = (TextView) infowindow.findViewById(R.id.info_window_subtext);
                            String eventname = event.getEventName();
                            if((eventname != null) && !eventname.isEmpty()){
                                if(eventname.length() > 15){
                                    Log.d(TAG,"length more that 15");
                                    String substr = eventname.substring(0,14);
                                    Log.d(TAG,"title is"+ substr);
                                    title.setText(substr + "..");
                                }else{
                                    Log.d(TAG,"title less that 15 is"+ eventname);
                                    title.setText(eventname);
                                }
                            }
                           // title.setText(event.getEventName());
                            subTitle.setText(event.getCategoryName());
                        }
                    }
                    return infowindow;
                }

                @Override
                public View getInfoContents(Marker marker) {
                   return null;
                }
            });
        }*/

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        mGoogleMap.animateCamera(cameraUpdate);*/
    }

    @Override
    public void onWindowFocusChanged() {
        Log.d(TAG, "List view coordinates" + loadMoreListView.getX() + "yval" + loadMoreListView.getLeft() + "width" + loadMoreListView.getRight());
        mStartX = loadMoreListView.getLeft();
        mEndX = loadMoreListView.getRight();
    }

    private void performSlideLeftAnimation() {
        PropertyValuesHolder transX = PropertyValuesHolder.ofFloat("x", mEndX, mStartX);
        PropertyValuesHolder alphaV = PropertyValuesHolder.ofFloat("alpha", 1, 0);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mMapView, transX);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(loadMoreListView, alphaV);
        anim.setDuration(500);
        alphaAnim.setDuration(500);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadMoreListView.setVisibility(View.GONE);

                if (mAddddLocations) {
                    showMapsView();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
        //alphaAnim.start();
    }

    private void performSlideRightAnimation() {
        PropertyValuesHolder transX = PropertyValuesHolder.ofFloat("x", mStartX, mEndX);
        PropertyValuesHolder alphaV = PropertyValuesHolder.ofFloat("alpha", 1, 0);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mMapView, transX);

        anim.setDuration(500);


        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMapView.setVisibility(View.GONE);
                loadMoreListView.setVisibility(View.VISIBLE);
                if (eventsListAdapter != null) {
                    eventsListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
        //alphaAnim.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Activity created");
    }

    private void showMapsView() {

        if (mMapView.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "displaying the Map view");
            //fetch the lat and longitudes
            int i = 0;

            if (mMapIcon == null) {
                mMapIcon = BitmapDescriptorFactory.fromResource(R.drawable.location_dot_img);
            }

            for (Event event : eventsArrayList) {
                if ((event.getEventLatitude() != null) && (event.getEventLongitude() != null)) {
                    double lat = Double.parseDouble(event.getEventLatitude());
                    double longitude = Double.parseDouble(event.getEventLongitude());
                    if ((lat > 0) | (longitude > 0)) {
                        LatLng pos = new LatLng(lat, longitude);
                        if ((pos != null) && (mGoogleMap != null)) {
                            Log.d(TAG, "has lat lon" + "lat:" + event.getEventLatitude() + "long:" + event.getEventLongitude());
                            //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1000, null);
                            Marker marker = null;
                            if (mMapIcon != null) {
                                Log.d(TAG, "Valid bitmap icon");
                                marker = mGoogleMap.addMarker(new MarkerOptions().position(pos).icon(mMapIcon));

                            } else {
                                Log.d(TAG, "No valid map icon");
                                marker = mGoogleMap.addMarker(new MarkerOptions().position(pos));
                            }

                            mAddedMarkers.add(marker);
                            mDisplayedEvents.put(pos, event);
                            marker.showInfoWindow();
                        } else {
                            Log.d(TAG, "Google maps was not created properly");
                        }
                    }
                }
            }
            //zoom the camera to current location
            if (mLastLocation != null) {
                LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
               /* mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,10));*/
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1000, null);
            }

            mAddddLocations = false;
        }
    }

    @Override
    public void onEventResponse(JSONObject response) {
        Log.d(TAG, "Received Nearby events");
        // super.onEventResponse(response);
        LoadListView(response);

    }

    private void LoadListView(JSONObject response){
        progressDialogHelper.hideProgressDialog();
        loadMoreListView.onLoadMoreComplete();
        Gson gson = new Gson();
        EventList eventsList = gson.fromJson(response.toString(), EventList.class);
        if (eventsList != null) {
            Log.d(TAG, "fetched all event list count" + eventsList.getCount());
        }
        int totalNearbyCount = 0;
        if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0) {
            if (mLastLocation != null) {
                Log.d(TAG, "Location is set");
                ArrayList<Event> mNearbyLIst = new ArrayList<Event>();
                Location temEventLoc = new Location("temp");
                //Only add those locations which are within 35km
                int i = 0;
                for (Event event : eventsList.getEvents()) {
                    //Testing. remove later
                   /* if(latitude.get(i) != null){
                        event.setEventLatitude(latitude.get(i));
                    }
                    if(longitude.get(i) != null){
                        event.setEventLongitude(longitude.get(i));
                    }*/
                    //end of testing
                    mTotalReceivedEvents++;

                    if ((event.getEventLatitude() != null) && (event.getEventLongitude() != null)) {
                        temEventLoc.setLatitude(Double.parseDouble(event.getEventLatitude()));
                        temEventLoc.setLongitude(Double.parseDouble(event.getEventLongitude()));
                        float distance = mLastLocation.distanceTo(temEventLoc);
                        Log.d(TAG, "calculated distance is" + distance);
                        if (distanceFlag==2){
                            if(distance < (5 * 1000)) {
                                mNearbyLIst.add(event);
                            }
                        } else {
                            mNearbyLIst.add(event);
                        }
                    }
                    i++;

                }
                totalNearbyCount = mNearbyLIst.size();
                Log.d(TAG, "Total event close by 35km " + totalNearbyCount);
                isLoadingForFirstTime = false;
                totalCount = eventsList.getCount();
                updateListAdapter(mNearbyLIst);
                if (mTotalReceivedEvents < totalCount) {
                    Log.d(TAG, "fetch remaining events");
                    if (eventsArrayList.size() < 10) {

                        pageNumber = (mTotalReceivedEvents / 10) + 1;

                        Log.d(TAG, "Page number" + pageNumber);
                        makeEventListServiceCall(pageNumber);
                    }
                } else {
                    Log.d(TAG, "Total received count greater than total count");
                }
            } else {

                isLoadingForFirstTime = false;
                totalCount = eventsList.getCount();
                updateListAdapter(eventsList.getEvents());
            }
        }
        // Updates the location and zoom of the MapView

        if (totalCount > 0) {
            mLocationBtn.setEnabled(true);
        } else {
            mAddddLocations = true;
        }

        mTotalEventCount.setText(Integer.toString(eventsArrayList.size()) + " Favorite Events");

       /* progressDialogHelper.hideProgressDialog();
        loadMoreListView.onLoadMoreComplete();
        Gson gson = new Gson();
        EventList eventsList = gson.fromJson(response.toString(), EventList.class);
        if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0) {
            totalCount = eventsList.getCount();
            isLoadingForFirstTime = false;
            updateListAdapter(eventsList.getEvents());
        }*/
    }

    @Override
    public void onEventError(String error) {
        super.onEventError(error);
        if (totalCount > 0) {
            mLocationBtn.setEnabled(true);
        }
        mAddddLocations = true;
    }

    @Override
    public void onLoadMore() {

        if (mTotalReceivedEvents < totalCount) {
            Log.d(TAG, "fetch remaining events");
            // if(eventsArrayList.size() < 20){

            pageNumber = (mTotalReceivedEvents / 10) + 1;

            Log.d(TAG, "Page number" + pageNumber);
            makeEventListServiceCall(pageNumber);
            // }
        }
    }

    @Override
    public void callGetEventService(int position) {
        Log.d(TAG, "fetch event list" + position);
        mNearbySelected = true;

        if (mLastLocation != null) {
            Log.d(TAG, "Location present");
            mTotalReceivedEvents = 0;
            super.callGetEventService(position);
            // getNearbyLIst(position);
        } else {
            if (mGoogleApiClient.isConnected()) {
                fetchCurrentLocation();
                if (mLastLocation == null) {
                    // AlertDialogHelper.showSimpleAlertDialog(getActivity(), "Enable Location services in settings");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Enable Location services in settings");
                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // getActivity().getFragmentManager().popBackStack();
                                    // endOfCalibration();
                                    //add pause button
                                    Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(viewIntent);


                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            } else {
                mLocationProgress = new ProgressDialog(getActivity());
                mLocationProgress.setIndeterminate(true);
                mLocationProgress.setMessage("Loading");
                mLocationProgress.show();
            }
        }
    }

    public void getNearbyLIst(int position) {
        Log.d(TAG, "fetch event list" + position);
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

        if (isLoadingForFirstTime) {
            Log.d(TAG, "Loading for the first time");
            if (eventsArrayList != null)
                eventsArrayList.clear();

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                // makeEventListServiceCall(2);
//                eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_FAVOURITES, Integer.parseInt(PreferenceStorage.getUserId(getActivity())), 1));
                eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_FAVOURITES, Integer.parseInt(PreferenceStorage.getUserId(getActivity())), pageNumber, PreferenceStorage.getUserCity(getActivity())));
            } else {
                AlertDialogHelper.showSimpleAlertDialog(getActivity(), getString(R.string.no_connectivity));
            }
        } else {
            Log.d(TAG, "Do nothing");
        }

    }

    private void fetchCurrentLocation() {
        Log.d(TAG, "fetch the current location");

        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            // Log.e(TAG, "Current location is" + "Lat" + String.valueOf(mLastLocation.getLatitude()) + "Long" + String.valueOf(mLastLocation.getLongitude()));
            if (mLocationProgress != null) {
                mLocationProgress.cancel();
            }
            if (mNearbySelected && (mLastLocation != null)) {
                mTotalReceivedEvents = 0;
                super.callGetEventService(1);
                // getNearbyLIst(2);
            }
            if (mLastLocation == null) {
                Log.e(TAG, "Received location is null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            Log.d(TAG, "make api disconnect");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "API CLient connected. fetch the list");
        fetchCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "connection suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to google locations failed");
        if (mLocationProgress != null) {
            mLocationProgress.cancel();
        }
        if (mNearbySelected) {
            super.callGetEventService(1);
            //getNearbyLIst(2);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "ON Google map ready");
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);
        if (mGoogleMap != null) {
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Log.d(TAG, "Getting the info view contents");

                    View infowindow = getActivity().getLayoutInflater().inflate(R.layout.map_info_window_layout, null);
                    LatLng pos = marker.getPosition();
                    if (pos != null) {
                        Event event = mDisplayedEvents.get(pos);

                        if (event != null) {
                            TextView title = (TextView) infowindow.findViewById(R.id.info_window_Title);
                            TextView subTitle = (TextView) infowindow.findViewById(R.id.info_window_subtext);
                            String eventname = event.getEventName();
                            if ((eventname != null) && !eventname.isEmpty()) {
                                if (eventname.length() > 15) {
                                    Log.d(TAG, "length more that 15");
                                    String substr = eventname.substring(0, 14);
                                    Log.d(TAG, "title is" + substr);
                                    title.setText(substr + "..");
                                } else {
                                    Log.d(TAG, "title less that 15 is" + eventname);
                                    title.setText(eventname);
                                }
                            }
                            // title.setText(event.getEventName());
                            subTitle.setText(event.getCategoryName());
                        }
                    }
                    return infowindow;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
        }
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng pos = marker.getPosition();
                Log.d(TAG, "Marker Info window clicked");

                Event event = mDisplayedEvents.get(pos);
                if (event != null) {
                    Log.d(TAG, "map info view clicked");
                    Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                    intent.putExtra("eventObj", event);
                    startActivity(intent);
                }
            }
        });
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // mMapLoaded = true;
                Log.d(TAG, "Map loaded");

                /*if ( mGoogleApiClient.isConnected() &&(mLastLocation != null) && (!mDisplayCurrentLocation)) {
                    showMyLocation();
                    // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                    //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                }*/

            }
        });
    }
}
