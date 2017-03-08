package com.findafun.activity;


import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.bean.events.Event;
import com.findafun.helper.LocationHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MapsActivity.class.getName();

    private GoogleMap mMap;
    private Event mEvent = null;
    private BitmapDescriptor mMapIcon = null;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private CompoundButton mAnimateToggle;

    private CompoundButton mCustomDurationToggle;

    private SeekBar mCustomDurationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("EVENT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEvent = (Event) getIntent().getSerializableExtra("eventObj");
        mMapIcon = BitmapDescriptorFactory.fromResource(R.drawable.location_dot_img);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(22.593726, 81.035156))
                .zoom(13)
                .build();


        //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(10.9934,76.94325) , 14.0f) );

        LocationHelper.FindLocationManager(this);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(10.9934,76.94325) , 14.0f) );

        //mMap = map;

        // We will provide our own zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                showEventLocation();


            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Log.d(TAG, "home up button selected");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void showEventLocation() {
        double lat = Double.parseDouble(mEvent.getEventLatitude());
        double longitude = Double.parseDouble(mEvent.getEventLongitude());
        if ((lat > 0) | (longitude > 0)) {
            LatLng pos = new LatLng(lat, longitude);
            if ((pos != null) && (mMap != null)) {
                Log.d(TAG, "has lat lon" + "lat:" + mEvent.getEventLatitude() + "long:" + mEvent.getEventLongitude());
                //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1000, null);
                // Zoom in, animating the camera.
                //  mMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                // mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 500, null);
                Marker marker = null;
                if (mMapIcon != null) {
                    Log.d(TAG, "Valid bitmap icon");
                    marker = mMap.addMarker(new MarkerOptions().position(pos).icon(mMapIcon));

                } else {
                    Log.d(TAG, "No valid map icon");
                    marker = mMap.addMarker(new MarkerOptions().position(pos));
                }
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        Log.d(TAG, "Getting the info view contents");

                        View infowindow = getLayoutInflater().inflate(R.layout.map_info_window_layout, null);
                        LatLng pos = marker.getPosition();
                        if (pos != null) {
                            Event event = mEvent;

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

                // mAddedMarkers.add(marker);
                //mDisplayedEvents.put(pos, event);
                marker.showInfoWindow();
            } else {
                Log.d(TAG, "Google maps was not created properly");
            }
        }
    }

    /**
     * Update the enabled state of the custom duration controls.
     */
    private void updateEnabledState() {
        mCustomDurationToggle.setEnabled(mAnimateToggle.isChecked());
        mCustomDurationBar
                .setEnabled(mAnimateToggle.isChecked() && mCustomDurationToggle.isChecked());
    }
}
