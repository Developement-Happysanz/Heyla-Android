package com.findafun.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import com.findafun.R;

/**
 * Created by Data Crawl 6 on 24-03-2016.
 */
public class LocationHelper {

    public static void FindLocationManager(final Context context) {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
// else {
//            latitude = 0.0;
//            longitude = 0.0;
//            GPSTracker gps = new GPSTracker(MapsActivity.this);
//            if (gps.canGetLocation()) {
//
//                latitude = gps.getLatitude();
//                longitude = gps.getLongitude();
//
//                mEvent.setEventLatitude(Double.toString(latitude));
//                mEvent.setEventLongitude(Double.toString(longitude));
//            }
//        }
    }
}

