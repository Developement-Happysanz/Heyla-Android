package com.findafun.fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.findafun.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mgoogleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.activity_map_fragment, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //googleMap = mMapView.getMapAsync(this);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // latitude and longitude
        double latitude = 10.9934;
        double longitude = 76.94325;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
            //googleMap.addMarker(marker);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(10.9934, 76.94325)).zoom(12).build();
            //googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(10.9934,76.94325) , 14.0f) );
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        double latitude = 10.9934;
        double longitude = 76.94325;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title(""));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(10.9934,76.94325) , 14.0f) );
    }
}
