package com.findafun.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CitySpinnerAdapter;
import com.findafun.adapter.CountrySpinnerAdapter;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.helper.SimpleGestureFilter;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.serviceinterfaces.IServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.ExecutionException;

public class SelectCityActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DialogClickListener, IServiceListener, IEventServiceListener {
    private static final String TAG = SelectCityActivity.class.getName();

    private SimpleGestureFilter detector;
    private EditText txtCityDropDown, txtCountryDropDown;
    private CitySpinnerAdapter citySpinnerAdapter;
    private CountrySpinnerAdapter countrySpinnerAdapter;
    private ArrayList<String> cityList;
    private ArrayList<String> countryList;
    protected EventServiceHelper eventServiceHelper;
    private View mDecorView;
    private Activity activity;
    GoogleApiClient mGoogleApiClient = null;
    Location mLastLocation = null;
    private TextView txtTaptoView, autoselectCity;
    private ProgressDialog mProgressDialog = null;
    protected ProgressDialogHelper progressDialogHelper;
    private String country = null;
    private String userType = null;
    private Boolean isCountryCheck = false;
    private Boolean isCityCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        txtTaptoView = (TextView) findViewById(R.id.txt_swipe_up);
        mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        buildGoogleApiClient();

        cityList = new ArrayList<>();
        countryList = new ArrayList<>();
        //cityList.add("Coimbatore");

        new FetchCountry().execute();
        activity = this;
        //detector = new SimpleGestureFilter(this, this);
        txtCityDropDown = (EditText) findViewById(R.id.btn_city_drop_down);
        txtCountryDropDown = (EditText) findViewById(R.id.btn_country_drop_down);
        citySpinnerAdapter = new CitySpinnerAdapter(this, R.layout.city_dropdown_item, cityList);
        countrySpinnerAdapter = new CountrySpinnerAdapter(this, R.layout.city_dropdown_item, countryList);
        txtCityDropDown.setOnClickListener(this);
        txtCountryDropDown.setOnClickListener(this);
        txtTaptoView.setOnClickListener(this);
        //check if user had previously selected city
        String cityName = PreferenceStorage.getUserCity(this);
        String countryName = PreferenceStorage.getUserCountry(this);
        userType = PreferenceStorage.getUserType(this);
        if ((cityName != null) && !cityName.isEmpty()) {
            txtCityDropDown.setText(cityName);
        }
        if ((countryName != null) && !countryName.isEmpty()) {
            txtCountryDropDown.setText(countryName);
        }

        eventServiceHelper = new EventServiceHelper(getApplicationContext());
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getApplicationContext());

        autoselectCity = (TextView) findViewById(R.id.auto_select_location);

        autoselectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityList.clear();
                Log.d(TAG, "fetching the current city based on current location");

                if ((mLastLocation == null) && (mGoogleApiClient != null)) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);

                }

                if (mLastLocation != null) {

                    CountryAsyncTask country = new CountryAsyncTask(SelectCityActivity.this,
                            mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    country.execute();

                    CityAsyncTask cst = new CityAsyncTask(SelectCityActivity.this,
                            mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    cst.execute();

                    String lo = null;
                    try {
                        if (cst.get() != null) {
                            lo = cst.get().toString();
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "fetched location is Null");
                    AlertDialogHelper.showSimpleAlertDialog(SelectCityActivity.this,
                            "Current Location Not Available. Please check if Location services are turned ON");
                }

            }
        });

    }

    protected void buildGoogleApiClient() {
        Log.d(TAG, "Initiate GoogleApi connection");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

  /*  @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSwipe(int direction) {
        if (direction == SimpleGestureFilter.SWIPE_UP) {
            Log.d(TAG,"Swipe up detected");
            if (!txtCityDropDown.getText().toString().equalsIgnoreCase("Select your City")) {
                Intent intent = new Intent(this, SelectPreferenceActivity.class);
                intent.putExtra("selectedCity", txtCityDropDown.getText().toString());
                PreferenceStorage.saveUserCity(this,txtCityDropDown.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            } else {
                Toast.makeText(this, "Please select your city", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        if (v == txtCityDropDown) {
            if (isCountryCheck) {
                loadCitySpinner();
            } else {
                country = txtCountryDropDown.getText().toString();
                if (!country.equalsIgnoreCase("")) {
                    if (!isCityCheck) {
                        new FetchCity().execute();
                    }
                }
                if (citySpinnerAdapter.getCount() == 0 && (country == null && country.isEmpty())) {
                    Toast.makeText(this, "Please select your country", Toast.LENGTH_SHORT).show();
                } else {
                    loadCitySpinner();
                }
            }
        } else if (v == txtTaptoView) {
            Log.d(TAG, "Swipe up detected");
            if (!txtCityDropDown.getText().toString().equalsIgnoreCase("Select your City")) {
                updateUserCity();
//                updateUserCountry();
            } else {
                Toast.makeText(this, "Please select your city", Toast.LENGTH_SHORT).show();
            }
        } else if (v == txtCountryDropDown) {
            isCountryCheck = true;
            Log.d(TAG, "Available countries count" + countrySpinnerAdapter.getCount());
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
            TextView header = (TextView) view.findViewById(R.id.gender_header);
            header.setText("Select Country");
            builderSingle.setCustomTitle(view);

            builderSingle.setAdapter(countrySpinnerAdapter, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    txtCountryDropDown.setText(countrySpinnerAdapter.getItem(which).toString());
                    country = txtCountryDropDown.getText().toString();
                    txtCountryDropDown.clearComposingText();
                    new FetchCity().execute();
                    dialog.dismiss();
                    cityList.clear();
                    txtCityDropDown.setText("Select your city");
                }
            }).create().show();
        }
    }

    private void loadCitySpinner() {
        Log.d(TAG, "Available cities count" + citySpinnerAdapter.getCount());
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select City");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(citySpinnerAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtCityDropDown.setText(citySpinnerAdapter.getItem(which).toString());
                txtCityDropDown.clearComposingText();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void updateUserCity() {
        String userCity = txtCityDropDown.getText().toString();
        String userCountry = txtCountryDropDown.getText().toString();
        if ((userCity == null) && !(userCity.isEmpty())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Please select your city");
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Updating City");
            mProgressDialog.show();
            saveCity();
        }
    }

    private void updateUserCountry() {
        String userCountry = txtCountryDropDown.getText().toString();
        if ((userCountry == null) && !(userCountry.isEmpty())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Please select your city");
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Updating City");
            mProgressDialog.show();
            saveCity();
        }
    }

    private void saveCity() {

        String cityVal = txtCityDropDown.getText().toString();
        String countryVal = txtCountryDropDown.getText().toString();


        String url = String.format(FindAFunConstants.UPDATE_CITY, Integer.parseInt(PreferenceStorage.getUserId(this)), cityVal, countryVal, Integer.parseInt(PreferenceStorage.getUserType(this)));

        URI uri = null;
        try {
            uri = new URI(url.replace(" ", "%20"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String stringUri;
        stringUri = uri.toString();

        SignUpServiceHelper mServiceHelper = new SignUpServiceHelper(this);
        mServiceHelper.updateUserProfile(stringUri, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        if ((mGoogleApiClient != null) && !mGoogleApiClient.isConnected()) {
            Log.d(TAG, "make api connect");
            mGoogleApiClient.connect();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            Log.d(TAG, "make api disconnect");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            // Log.e(TAG, "Current location is" + "Lat" + String.valueOf(mLastLocation.getLatitude()) + "Long" + String.valueOf(mLastLocation.getLongitude()));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onSuccess(int resultCode, Object result) {

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        Log.d(TAG, "received on success");

        if (result instanceof JSONObject) {
            Log.d(TAG, "City was saved successfully");

            //AlertDialogHelper.showSimpleAlertDialog(this, "City updated succesfully");

            Intent intent = new Intent(this, SelectPreferenceActivity.class);
            intent.putExtra("selectedCity", txtCityDropDown.getText().toString());
            PreferenceStorage.saveUserCity(this, txtCityDropDown.getText().toString());
            PreferenceStorage.saveUserCountry(this, txtCountryDropDown.getText().toString());
            startActivity(intent);
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

        }
    }

    @Override
    public void onError(String erorr) {

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, "Error saving your city. Try again");

    }

    @Override
    public void onEventResponse(JSONObject response) {

    }

    @Override
    public void onEventError(String error) {

    }

    public class CityAsyncTask extends AsyncTask<String, String, String> {
        Activity act;
        double latitude;
        double longitude;

        public CityAsyncTask(Activity act, double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.act = act;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            Geocoder geocoder = new Geocoder(act, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                Log.e("Addresses", "-->" + addresses);
                Address address = addresses.get(0);
                if (address != null) {
//                    result = address.getLocality();
                    String cap = address.getLocality();
                    result = cap.substring(0, 1).toUpperCase() + cap.substring(1);

                } else {
                    result = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.d(TAG, "received city is" + result);

            String citytext = txtCityDropDown.getText().toString();
            Log.d(TAG, "current city text is" + citytext);

            if ((result != null) && !(result.isEmpty())) {
                txtCityDropDown.setText(result.toString());

                country = txtCountryDropDown.getText().toString();
                new FetchCity().execute();
                cityList.clear();

            } else {
                Toast.makeText(SelectCityActivity.this, "Unable to retrive current location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CountryAsyncTask extends AsyncTask<String, String, String> {
        Activity act;
        double latitude;
        double longitude;

        public CountryAsyncTask(Activity act, double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.act = act;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            Geocoder geocoder = new Geocoder(act, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                Log.e("Addresses", "-->" + addresses);
                Address address = addresses.get(0);
                if (address != null) {
                    String cap = address.getCountryName();
                    result = cap.substring(0, 1).toUpperCase() + cap.substring(1);

                } else {
                    result = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.d(TAG, "received country is" + result);

            String countrytext = txtCountryDropDown.getText().toString();
            Log.d(TAG, "current country text is" + countrytext);

            if ((result != null) && !(result.isEmpty())) {
                txtCountryDropDown.setText(result.toString());
            } else {
                Toast.makeText(SelectCityActivity.this, "Unable to retrive current location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class FetchCity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // final String url = FindAFunConstants.GET_CITY_NEW_URL;

            final String url = String.format(FindAFunConstants.GET_CITY_NEW_URL, country);


            Log.d(TAG, "fetch city list URL");

            new Thread() {
                public void run() {
                    String in = null;
                    try {
                        in = openHttpConnection(url);
                        JSONArray jsonArray = new JSONArray(in);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            cityList.add(jsonObject.getString("city_name"));
                        }
                        Collections.sort(cityList);
                        Log.d(TAG, "Received city list" + jsonArray.length());
                        isCityCheck = true;

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            citySpinnerAdapter = new CitySpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, cityList);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class FetchCountry extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = FindAFunConstants.GET_COUNTRY_URL;
            Log.d(TAG, "fetch country list URL");

            new Thread() {
                public void run() {
                    String in = null;
                    try {
                        in = openHttpConnection(url);
                        JSONArray jsonArray = new JSONArray(in);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            countryList.add(jsonObject.getString("country_name"));
                        }
                        Log.d(TAG, "Received country list" + jsonArray.length());

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            countrySpinnerAdapter = new CountrySpinnerAdapter(activity, android.R.layout.simple_spinner_dropdown_item, countryList);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private String openHttpConnection(String urlStr) {
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        int resCode = -1;

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String read;

                while ((read = br.readLine()) != null) {
                    //System.out.println(read);
                    sb.append(read);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
