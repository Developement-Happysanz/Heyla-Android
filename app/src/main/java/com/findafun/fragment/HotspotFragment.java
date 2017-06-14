package com.findafun.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import com.findafun.R;

import com.findafun.activity.StaticEventDetailActivity;
import com.findafun.bean.events.Event;
import com.findafun.bean.events.EventList;

import com.findafun.helper.LocationHelper;

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
 * Created by Data Crawl 6 on 20-05-2016.
 */
public class HotspotFragment extends StaticEventFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = HotspotFragment.class.getName();
    private MapView mMapView_hp = null;
    private GoogleMap mGoogleMap_hp = null;
    GoogleApiClient mGoogleApiClient_hp = null;
    private boolean mMapLoaded = false;
    Location mLastLocation_hp = null;
    private ImageButton mLocationBtn_hp = null;
    private List<Marker> mAddedMarkers_hp = new ArrayList<Marker>();
    private HashMap<LatLng, Event> mDisplayedEvents_hp = new HashMap<LatLng,Event>();
    private boolean mAddddLocations_hp = true;
    private TextView mTotalEventCount_hp = null;
    private Drawable  mLocationUnselected_hp = null;
    private Drawable  mLocationSelected_hp = null;
    private Drawable  mListUnselected_hp = null;
    private Drawable mListSelected_hp = null;
    private Drawable  mNearbyTabUnselected_hp = null;
    private Drawable  mNearbyTabSelected_hp = null;


    //icons
    private Drawable mselectednearbyicon_hp = null;
    private Drawable munselectednearbyicon_hp = null;
    private Drawable mselectedlocationicon_hp = null;
    private Drawable munselectedlocationicon_hp = null;
    private Drawable mselectedlisticon_hp = null;
    private Drawable munselectedlisticon_hp = null;
    private BitmapDescriptor mMapIcon_hp = null;

    private float mStartX_hp;
    private float mStartY;
    private float mEndX_hp;
    private float mEndY;

    private ProgressDialog mLocationProgress_hp = null;
    private boolean mNearbySelected_hp = false;
    private int mTotalReceivedEvents_hp =0;

    public static final CameraPosition COIMBATORE =
            new CameraPosition.Builder().target(new LatLng(11.00, 77.00))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();


    HashMap<Integer,String> latitude = new HashMap<Integer, String>();
    HashMap<Integer, String> longitude = new HashMap<Integer, String>();

    public static HotspotFragment newInstance(int position) {
        HotspotFragment frag = new HotspotFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"Nearby fragment onCreateView called");
        view = inflater.inflate(R.layout.favorite_layout, container, false);

        initializeViews();
        initializeEventHelpers();

        mAddddLocations_hp = true;
        mMapView_hp = (MapView) view.findViewById(R.id.mapview);
        mMapView_hp.onCreate(savedInstanceState);
        setUpGoogleMaps();



        mMapIcon_hp = BitmapDescriptorFactory.fromResource(R.drawable.location_dot_img);

        mTotalEventCount_hp = (TextView) view.findViewById(R.id.nearby_totalevents);
        mLocationBtn_hp = (ImageButton) view.findViewById(R.id.nearby_location_btn);
        final ImageButton listAppearence = (ImageButton) view.findViewById(R.id.nearby_grid_view_btn);
        mLocationBtn_hp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadMoreListView.setVisibility(View.GONE);
                LocationHelper.FindLocationManager(getContext());

                mMapView_hp.setVisibility(View.VISIBLE);
                performSlideLeftAnimation();
                mLocationBtn_hp.setBackgroundDrawable(mLocationSelected_hp);
                listAppearence.setBackgroundDrawable(mListUnselected_hp);
                mLocationBtn_hp.setImageDrawable(mselectedlocationicon_hp);
                listAppearence.setImageDrawable(munselectedlisticon_hp);
            }
        });

        //by default set it to disabled
        mLocationBtn_hp.setEnabled(false);
        mDisplayedEvents_hp.clear();

        listAppearence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMapView.setVisibility(View.GONE);
                performSlideRightAnimation();
                mLocationBtn_hp.setBackgroundDrawable(mLocationUnselected_hp);
                listAppearence.setBackgroundDrawable(mListSelected_hp);
                mLocationBtn_hp.setImageDrawable(munselectedlocationicon_hp);
                listAppearence.setImageDrawable(mselectedlisticon_hp);
                /*loadMoreListView.setVisibility(View.VISIBLE);
                if (eventsListAdapter != null) {
                    eventsListAdapter.notifyDataSetChanged();
                }*/
            }
        });
        mLocationBtn_hp.setBackgroundDrawable(mLocationUnselected_hp);
        listAppearence.setBackgroundDrawable(mListSelected_hp);
        mLocationBtn_hp.setImageDrawable(munselectedlocationicon_hp);
        listAppearence.setImageDrawable(mselectedlisticon_hp);
        return view;
    }

    protected  void buildGoogleApiClient() {
        Log.d(TAG, "Initiate GoogleApi connection");
        mGoogleApiClient_hp = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        mMapView_hp.onResume();

        if((mGoogleApiClient_hp != null)  && !mGoogleApiClient_hp.isConnected()){
            Log.d(TAG, "make api connect");
            mGoogleApiClient_hp.connect();

        }else{
            Log.e(TAG,"googleapi is null");
        }
    }

    private void setUpGoogleMaps() {
        Log.d(TAG, "Setting up google maps");
        buildGoogleApiClient();
        mGoogleMap_hp = null;
        mMapView_hp.getMapAsync(this);
        mAddedMarkers_hp.clear();
        mDisplayedEvents_hp.clear();

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFoucusChanged(){
        Log.d(TAG, "List view coordinates" + loadMoreListView.getX() + "yval" + loadMoreListView.getLeft() + "width" + loadMoreListView.getRight());
        mStartX_hp = loadMoreListView.getLeft();
        mEndX_hp = loadMoreListView.getRight();
    }

    private void performSlideLeftAnimation(){
        PropertyValuesHolder transX = PropertyValuesHolder.ofFloat("x",mEndX_hp,mStartX_hp);
        PropertyValuesHolder alphaV = PropertyValuesHolder.ofFloat("alpha", 1, 0);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mMapView_hp, transX);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(loadMoreListView,alphaV);
        anim.setDuration(500);
        alphaAnim.setDuration(500);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadMoreListView.setVisibility(View.GONE);

                if (mAddddLocations_hp) {
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

    private void performSlideRightAnimation(){
        PropertyValuesHolder transX = PropertyValuesHolder.ofFloat("x",mStartX_hp,mEndX_hp);
        PropertyValuesHolder alphaV = PropertyValuesHolder.ofFloat("alpha", 1, 0);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mMapView_hp, transX);

        anim.setDuration(500);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMapView_hp.setVisibility(View.GONE);
                loadMoreListView.setVisibility(View.VISIBLE);
                if (staticEventsListAdapter != null) {
                    staticEventsListAdapter.notifyDataSetChanged();
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
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Activity created");
    }

    private void showMapsView(){

        if(mMapView_hp.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "displaying the Map view");
            //fetch the lat and longitudes
            int i = 0;

            if (mMapIcon_hp == null) {
                mMapIcon_hp = BitmapDescriptorFactory.fromResource(R.drawable.location_dot_img);
            }

            for (Event event : eventsArrayList) {
                if ((event.getEventLatitude() != null) && (event.getEventLongitude() != null)) {
                    double lat = Double.parseDouble(event.getEventLatitude());
                    double longitude = Double.parseDouble(event.getEventLongitude());
                    if ((lat > 0) | (longitude > 0)) {
                        LatLng pos = new LatLng(lat, longitude);
                        if ((pos != null) && (mGoogleMap_hp != null)) {
                            Log.d(TAG, "has lat lon" + "lat:" + event.getEventLatitude() + "long:" + event.getEventLongitude());
                            //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1000, null);
                            Marker marker = null;
                            if (mMapIcon_hp != null) {
                                Log.d(TAG, "Valid bitmap icon");
                                marker = mGoogleMap_hp.addMarker(new MarkerOptions().position(pos).icon(mMapIcon_hp));

                            } else {
                                Log.d(TAG, "No valid map icon");
                                marker = mGoogleMap_hp.addMarker(new MarkerOptions().position(pos));
                            }

                            mAddedMarkers_hp.add(marker);
                            mDisplayedEvents_hp.put(pos, event);
                            marker.showInfoWindow();
                        } else {
                            Log.d(TAG, "Google maps was not created properly");
                        }
                    }
                }
            }
            //zoom the camera to current location
            if (mLastLocation_hp != null) {
                LatLng pos = new LatLng(mLastLocation_hp.getLatitude(), mLastLocation_hp.getLongitude());
               /* mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,10));*/
                mGoogleMap_hp.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1000, null);
            }

            mAddddLocations_hp = false;
        }
    }

    @Override
    public void onEventResponse(JSONObject response) {
        Log.d(TAG, "Received Nearby events");
        // super.onEventResponse(response);
        progressDialogHelper.hideProgressDialog();
     //   loadMoreListView.onLoadMoreComplete();
        Gson gson = new Gson();
        EventList eventsList = gson.fromJson(response.toString(), EventList.class);
        if(eventsList != null){
            Log.d(TAG,"fetched all event list count"+ eventsList.getCount());
        }
        int totalNearbyCount =0;
        if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0 ) {
            if(mLastLocation_hp != null) {
                Log.d(TAG,"Location is set");
                ArrayList<Event> mNearbyLIst = new ArrayList<Event>();
                Location temEventLoc = new Location("temp");
                //Only add those locations which are within 35km
                int i=0;
                for (Event event :eventsList.getEvents()){
                    //Testing. remove later
                   /* if(latitude.get(i) != null){
                        event.setEventLatitude(latitude.get(i));
                    }
                    if(longitude.get(i) != null){
                        event.setEventLongitude(longitude.get(i));
                    }*/
                    //end of testing
                    mTotalReceivedEvents_hp ++;

                    if((event.getEventLatitude() != null) && (event.getEventLongitude() != null)){
                        temEventLoc.setLatitude(Double.parseDouble(event.getEventLatitude()));
                        temEventLoc.setLongitude(Double.parseDouble(event.getEventLongitude()));
                        float distance = mLastLocation_hp.distanceTo(temEventLoc);
                        Log.d(TAG,"calculated distance is"+ distance);
                        if(distance < (350 * 1000)){
                            mNearbyLIst.add(event);
                        }
                    }
                    i++;
                }
                totalNearbyCount = mNearbyLIst.size();
                Log.d(TAG,"Total event close by 35km "+ totalNearbyCount);
                isLoadingForFirstTime = false;
                totalCount = eventsList.getCount();
                updateListAdapter(mNearbyLIst);
                if(mTotalReceivedEvents_hp < totalCount){
                    Log.d(TAG,"fetch remaining events");
                    if(eventsArrayList.size() < 10){

                        pageNumber = (mTotalReceivedEvents_hp / 10) + 1;

                        Log.d(TAG,"Page number"+ pageNumber);
                        makeEventListServiceCall(pageNumber);
                    }
                }else{
                    Log.d(TAG,"Total received count greater than total count");
                }
            }else {

                isLoadingForFirstTime = false;
                totalCount = eventsList.getCount();
                updateListAdapter(eventsList.getEvents());
            }
        }
        // Updates the location and zoom of the MapView

        if(totalCount > 0){
            mLocationBtn_hp.setEnabled(true);
        }else{
            mAddddLocations_hp = true;
        }

        mTotalEventCount_hp.setText(Integer.toString(eventsArrayList.size())+ " Hotspot Events");
    }

    @Override
    public void onEventError(String error) {
        super.onEventError(error);
        if(totalCount > 0){
            mLocationBtn_hp.setEnabled(true);
        }
        mAddddLocations_hp = true;
    }

    @Override
    public void onLoadMore() {

        if(mTotalReceivedEvents_hp < totalCount){
            Log.d(TAG,"fetch remaining events");
            // if(eventsArrayList.size() < 20){

            pageNumber = (mTotalReceivedEvents_hp / 10) + 1;

            Log.d(TAG,"Page number"+ pageNumber);
            makeEventListServiceCall(pageNumber);
            // }
        }
    }

    @Override
    public void callGetEventService(int position) {
        Log.d(TAG, "fetch event list" + position);
        mNearbySelected_hp = true;

        if(mLastLocation_hp != null){
            Log.d(TAG, "Location present");
            mTotalReceivedEvents_hp =0;
            super.callGetEventService(position);
            // getNearbyLIst(position);
        }else{
            if(mGoogleApiClient_hp.isConnected()){
                fetchCurrentLocation();
                if(mLastLocation_hp == null) {
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

            }else {
                mLocationProgress_hp = new ProgressDialog(getActivity());
                mLocationProgress_hp.setIndeterminate(true);
                mLocationProgress_hp.setMessage("Loading");
                mLocationProgress_hp.show();
            }
        }
    }

    private void fetchCurrentLocation(){
        Log.d(TAG,"fetch the current location");
        try {

            mLastLocation_hp = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient_hp);
            // Log.e(TAG, "Current location is" + "Lat" + String.valueOf(mLastLocation.getLatitude()) + "Long" + String.valueOf(mLastLocation.getLongitude()));
            if(mLocationProgress_hp != null){
                mLocationProgress_hp.cancel();
            }
            if (mNearbySelected_hp && (mLastLocation_hp != null)) {
                mTotalReceivedEvents_hp =0;
                super.callGetEventService(1);
                // getNearbyLIst(2);
            }
            if(mLastLocation_hp == null){
                Log.e(TAG,"Received location is null");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView_hp.onPause();
        if((mGoogleApiClient_hp != null) && (mGoogleApiClient_hp.isConnected())) {
            Log.d(TAG,"make api disconnect");
            mGoogleApiClient_hp.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView_hp.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView_hp.onLowMemory();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"API CLient connected. fetch the list");
        fetchCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"connection suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG,"Connection to google locations failed");
        if(mLocationProgress_hp != null){
            mLocationProgress_hp.cancel();
        }
        if(mNearbySelected_hp) {
            super.callGetEventService(1);
            //getNearbyLIst(2);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"ON Google map ready");
        mGoogleMap_hp = googleMap;
        mGoogleMap_hp.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap_hp.setMyLocationEnabled(true);
        if(mGoogleMap_hp != null){
            mGoogleMap_hp.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Log.d(TAG,"Getting the info view contents");

                    View infowindow = getActivity().getLayoutInflater().inflate(R.layout.map_info_window_layout, null);
                    LatLng pos = marker.getPosition();
                    if(pos != null) {
                        Event event = mDisplayedEvents_hp.get(pos);

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
        }
        mGoogleMap_hp.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng pos = marker.getPosition();
                Log.d(TAG,"Marker Info window clicked");

                Event event = mDisplayedEvents_hp.get(pos);
                if(event != null) {
                    Log.d(TAG, "map info view clicked");
                    Intent intent = new Intent(getActivity(), StaticEventDetailActivity.class);
                    intent.putExtra("eventObj", event);
                    startActivity(intent);
                }
            }
        });
        mGoogleMap_hp.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
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
