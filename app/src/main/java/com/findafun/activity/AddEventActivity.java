package com.findafun.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CategorySpinnerAdapter;
import com.findafun.adapter.CitySpinnerAdapter;
import com.findafun.adapter.CostSpinnerAdapter;
import com.findafun.bean.categories.Category;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.CategoryServiceHelper;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.ICategoryServiceListener;
import com.findafun.serviceinterfaces.IServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.FindAFunValidator;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEventActivity extends AppCompatActivity implements DialogClickListener,ICategoryServiceListener,View.OnClickListener, IServiceListener {

    String nameVal,typeVal,categoryVal,cityVal,venueVal,addressVal,descVal,costVal,contPersonVal,contactVal,mailVal,latVal,lngVal;
    static String startTimeTextVal,endTimeTextVal,startDateTextVal,endDateTextVal,picture1,picture2,picture3;

    private CategoryServiceHelper categoryServiceHelper;
    private Uri mSelectedImageUri = null;
    static final int REQUEST_IMAGE_GET = 1;
    ImageView imageView_back,addEventPict1,addEventPict2,addEventPict3;
    static int pict_flag =0;

    //

    EditText nameValue;
    EditText eventType;
    EditText eventCategory;
    EditText eventCity;
    EditText venueValue;
    EditText descripValue;
    EditText mailValue;
    EditText costValue;
    EditText addEvtAddressVal;
    LinearLayout layout_date;
    TextView startDate;
    EditText startDateValue;
    TextView endDate;
    EditText endDateValue;
    private Uri outputFileUri;
    TextView startTime;
    EditText startTimeValue;
    TextView endTime;
    EditText endTimeValue;
    private String mActualFilePath = null;
    private Bitmap mCurrentUserImageBitmap = null;
    LinearLayout layout_time;
    private ImageView mProfileImage = null;
    EditText contactValue;
    EditText addEvtContactPersonVal;
    EditText latValue;
    EditText lngValue;
   // TextView txtLogo;
    Button btnLogo;
    TextView txtPic1,txtPic2,txtPic;
    Button btnPic;
    TextView txtPhotos;
    Button btnPhotos;
    Button btnSave;
    Button btnDiscard;

    DialogFragment timepicker, datepicker;
    private static final String TAG = SelectCityActivity.class.getName();

    private CitySpinnerAdapter citySpinnerAdapter;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private CostSpinnerAdapter costSpinnerAdapter;

    String selectedCategory, selectedType, selectedCity;
    private ArrayList<String> cityList = new ArrayList<String>();
    private ArrayList<String> categoryList = new ArrayList<String>();
    private ArrayList<String> costList = new ArrayList<String>();
    private static int RESULT_LOAD_IMG_BANNER = 1;
    String imgBannerDecodableString;
    private ListPopupWindow listPopupCategory, listPopupCity, listPopupType;

    private ArrayAdapter<String> mCityAdapter = null;
    private List<String> mCityList = new ArrayList<String>();
    private ArrayAdapter<String> mTypeAdapter = null;
    private List<String> mTypeList = new ArrayList<String>();
    private ArrayAdapter<String> mCategoryAdapter = null;
    private List<String> mCategoryList = new ArrayList<String>();
    static int datePickerInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        new FetchCity().execute();
      //  new FetchCategory().execute();
        fetchCategoryValues();

        setUI();
    }



    private void fetchCategoryValues(){
        categoryServiceHelper = new CategoryServiceHelper(this);
        categoryServiceHelper.setCategoryServiceListener(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "category_list");
            jsonObject.put(FindAFunConstants.PARAMS_USER_ID, PreferenceStorage.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        categoryServiceHelper.makeGetCategoryServiceCall(jsonObject);
    }


    private void setUI() {

        costList.add("Free");
        costList.add("Paid");
        costList.add("Invite");

        citySpinnerAdapter = new CitySpinnerAdapter(this, R.layout.city_dropdown_item, cityList);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this, R.layout.category_dropdown_item, categoryList);
        costSpinnerAdapter = new CostSpinnerAdapter(this,R.layout.cost_dropdown_item,costList);




        mTypeList.add("Normal Event");
        mTypeList.add("Hotspot Event");


        mTypeAdapter = new ArrayAdapter<String>(this, R.layout.type_layout, R.id.type_name, mTypeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.type_layout, parent, false);
                TextView typename = (TextView) view.findViewById(R.id.type_name);
                typename.setText(mTypeList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        imageView_back = (ImageView) findViewById(R.id.cst_back_btn);

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        addEventPict1 = (ImageView) findViewById(R.id.addEvtPict1);
        addEventPict2 = (ImageView) findViewById(R.id.addEvtPict2);
        addEventPict3 = (ImageView) findViewById(R.id.addEvtPict3);

        nameValue = (EditText) findViewById(R.id.addEvtNameVal);
        eventType = (EditText) findViewById(R.id.addEvtTypeVal);
        eventCategory = (EditText) findViewById(R.id.addEvtCatgryVal);
        eventCity = (EditText) findViewById(R.id.addEvtCityVal);
        venueValue = (EditText) findViewById(R.id.addEvtVenueVal);
        descripValue = (EditText) findViewById(R.id.addEvtDescVal);
        costValue = (EditText) findViewById(R.id.addEvtCostVal);
        addEvtAddressVal = (EditText) findViewById(R.id.addEvtAddressVal);
        mailValue = (EditText) findViewById(R.id.addEvtContactEmailVal);
        layout_date = (LinearLayout) findViewById(R.id.layout_date);
        startDate = (TextView) findViewById(R.id.addEvtStartDate);
        startDateValue = (EditText) findViewById(R.id.addEvtStartDateVal);
        endDate = (TextView) findViewById(R.id.addEvtEndDate);
        endDateValue = (EditText) findViewById(R.id.addEvtEndDateVal);
        layout_time = (LinearLayout) findViewById(R.id.layout_time);
        startTime = (TextView) findViewById(R.id.addEvtStrTime);
        startTimeValue = (EditText) findViewById(R.id.addEvtStrTimeVal);
        endTime = (TextView) findViewById(R.id.addEvtEndTime);
        endTimeValue = (EditText) findViewById(R.id.addEvtEndTimeVal);
        contactValue = (EditText) findViewById(R.id.addEvtContactVal);
        addEvtContactPersonVal = (EditText) findViewById(R.id.addEvtContactPersonVal);
        latValue = (EditText) findViewById(R.id.addEvtLatVal);
        lngValue = (EditText) findViewById(R.id.addEvtLngVal);
       // txtLogo = (TextView) findViewById(R.id.addEvtLogoNoFile);
        btnLogo = (Button) findViewById(R.id.buttonAddPic);

        txtPic1 = (TextView) findViewById(R.id.textPict1);

        txtPic2 = (TextView) findViewById(R.id.textPict2);

       
        btnPic = (Button) findViewById(R.id.buttonAddPic);

        btnSave = (Button) findViewById(R.id.addEvtSave);
        btnDiscard = (Button) findViewById(R.id.addEvtDiscard);

        eventType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeList();
            }
        });





        addEventPict1.setOnClickListener(this);
        addEventPict2.setOnClickListener(this);
        addEventPict3.setOnClickListener(this);









        eventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryList();
            }
        });

        eventCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCityList();
            }
        });

        costValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCostList();
            }
        });

        startDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 1);

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });


        endDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE", 2);

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });

        startTimeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("TIME", 1);

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "TimePicker");


            }
        });

        endTimeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("TIME", 2);

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameValue.setText("");
                eventCategory.setSelection(0);
                eventType.setSelection(0);
                eventCity.setSelection(0);
                venueValue.setText("");
                descripValue.setText("");
                startDateValue.setText("");
                endDateValue.setText("");
                startTimeValue.setText("");
                endTimeValue.setText("");
                contactValue.setText("");
                latValue.setText("");
                lngValue.setText("");

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (validateFields()) {
                    Log.d(TAG,"Add Event");
                    Toast.makeText(AddEventActivity.this, "validated", Toast.LENGTH_SHORT).show();
                    saveEventDetails();

                }
            }
        });
    }



    private void saveEventDetails() {


        nameVal = nameValue.getText().toString();
        typeVal = eventType.getText().toString();
        categoryVal = eventCategory.getText().toString();
        cityVal = eventCity.getText().toString();
        venueVal = venueValue.getText().toString();
        addressVal =addEvtAddressVal.getText().toString();
        descVal = descripValue.getText().toString();
        costVal = costValue.getText().toString();
        contPersonVal = addEvtContactPersonVal.getText().toString();
        contactVal = contactValue.getText().toString();
        mailVal = mailValue.getText().toString();

        latVal = latValue.getText().toString();
        lngVal = lngValue.getText().toString();




       Toast.makeText(getApplicationContext(),"pic 1"+picture1,Toast.LENGTH_SHORT).show();
       Toast.makeText(getApplicationContext(),"pic 2"+picture2,Toast.LENGTH_SHORT).show();
       Toast.makeText(getApplicationContext(),"pic 3"+picture3,Toast.LENGTH_SHORT).show();
       Toast.makeText(getApplication(),""+nameVal+" "+typeVal+" "+categoryVal+" "+cityVal+" "+venueVal+" "+descVal+" "+" "+costVal+" "+contPersonVal+" "+contactVal+" "+" "+mailVal+" "+latVal+" "+endTimeTextVal+" "+startTimeTextVal+" "+endDateTextVal+" "+startDateTextVal+" "+picture1+" "+picture2+" "+picture3,Toast.LENGTH_SHORT).show();

     //   http://hobbistan.com/app/hobbistan/api.php?func_name=user_add_event&event_type=%s&txtEvent=%s&cboCategory=%s&cboCity=%s&txtVenue=%s&txtAddress=%s&txtDescription=%s&cboCost=%s&txtSdate=%s&txtEdate=%s&cboStime=%s&cboEtime=%s&txtLatitude=%s&txtLongitude=%s&txtPhone=%s&txtPerson=%s&txtEmail=%s&txtpic1=%s&txtpic2=%s&txtpic3=%s

      String url = String.format(FindAFunConstants.ADD_EVENT_URL,1, Uri.encode(nameVal),categoryVal,cityVal,venueVal,addressVal,descVal,costVal,startDateTextVal,endDateTextVal,startTimeTextVal,endTimeTextVal,latVal,lngVal,contactVal,contPersonVal,mailVal,picture1,picture2,picture3);

        SignUpServiceHelper mServiceHelper = new SignUpServiceHelper(this);
        mServiceHelper.saveEvents(url, this);

    }





    private void showTypeList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Type");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);

        header.setText("Select Type");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mTypeAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mTypeList.get(which);
                        eventType.setText(strName);

                        if ((strName.trim().contentEquals("Normal Event")) || (strName.trim().contentEquals("Ad Event"))) {

                            //Date Enable
                            layout_date.setVisibility(View.VISIBLE);
                            startDate.setVisibility(View.VISIBLE);
                            startDateValue.setVisibility(View.VISIBLE);
                            endDate.setVisibility(View.VISIBLE);
                            endDateValue.setVisibility(View.VISIBLE);

                            //Time Enable
                            layout_time.setVisibility(View.VISIBLE);
                            startTime.setVisibility(View.VISIBLE);
                            startTimeValue.setVisibility(View.VISIBLE);
                            endTime.setVisibility(View.VISIBLE);
                            endTimeValue.setVisibility(View.VISIBLE);

                        } else if (strName.trim().contentEquals("Hotspot Event")) {

                            //Date Enable
                            layout_date.setVisibility(View.GONE);
                            startDate.setVisibility(View.GONE);
                            startDateValue.setVisibility(View.GONE);
                            endDate.setVisibility(View.GONE);
                            endDateValue.setVisibility(View.GONE);

                            //Time Enable
                            layout_time.setVisibility(View.VISIBLE);
                            startTime.setVisibility(View.VISIBLE);
                            startTimeValue.setVisibility(View.VISIBLE);
                            endTime.setVisibility(View.VISIBLE);
                            endTimeValue.setVisibility(View.VISIBLE);
                        }
                    }
                });
        builderSingle.show();
    }




    private void showCategoryList() {
        Log.d(TAG, "show the category list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Category");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(categorySpinnerAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventCategory.setText(categorySpinnerAdapter.getItem(which).toString());
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showCostList() {

        Log.d(TAG, "show the cost list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Cost");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(costSpinnerAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                costValue.setText(costSpinnerAdapter.getItem(which).toString());
                dialog.dismiss();
            }
        }).create().show();


    }

    private void showCityList() {
        Log.d(TAG, "show the city list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select City");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(citySpinnerAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventCity.setText(citySpinnerAdapter.getItem(which).toString());
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onCategoryResponse(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        ArrayList<Category> arrayList = gson.fromJson(response.toString(), listType);
        categoryList.clear();
      //  mSelectedCategoryList.clear();
        // isSelectedArray.clear();
        for(Category category: arrayList){
            categoryList.add(category.getCategory());
            //isSelectedArray.add(false);
        }
    }

    @Override
    public void onCategoryError(String error) {

    }

    @Override
    public void onSetCategoryResponse(JSONObject response) {

    }

    @Override
    public void onSetCategoryError(String error) {

    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.addEvtPict1)) {
            pict_flag = 1;
            openImageIntent();

        } else if(v == findViewById(R.id.addEvtPict2)){
            pict_flag = 2;
            openImageIntent();

        } else if(v == findViewById(R.id.addEvtPict3)){
            pict_flag = 3;
            openImageIntent();
        }

    }

    @Override
    public void onSuccess(int resultCode, Object result) {
        Log.d(TAG, "received on success");

        if (result instanceof JSONObject) {
            Log.d(TAG, " saved successfully");
        }
    }

    @Override
    public void onError(String error) {
        AlertDialogHelper.showSimpleAlertDialog(this, "Error saving your Event. Try again");
    }


    private class FetchCity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = FindAFunConstants.GET_CITY_URL;
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
                        Log.d(TAG, "Received city list" + jsonArray.length());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            citySpinnerAdapter = new CitySpinnerAdapter(AddEventActivity.this, android.R.layout.simple_spinner_dropdown_item, cityList);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
/*
    private class FetchCategory extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = FindAFunConstants.GET_CATEGORY_URL;
            Log.d(TAG, "fetch category list URL"+url);

            new Thread() {
                public void run() {
                    String in = null;
                    try {
                       // in = openHttpConnection("http://hobbistan.com/app/hobbistan/api.php?func_name=category_list&user_id=1");
                        JSONArray jsonArray = new JSONArray("http://hobbistan.com/app/hobbistan/api.php?func_name=category_list&user_id=2570");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            categoryList.add(jsonObject.getString("category_name"));
                        }
                        Log.d(TAG, "Received category list" + jsonArray.length());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            categorySpinnerAdapter = new CategorySpinnerAdapter(AddEventActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }*/

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

    public void showTimePickerDialog(View v) {

        timepicker = new TimePickerFragment();
        timepicker.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        static final int START_TIME = 1;
        static final int END_TIME = 2;
        private int mChosenTime;
        int cur = 0;
        String am_pm = "";


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            if (c.get(Calendar.AM_PM) == Calendar.AM) {
                am_pm = "AM";
            } else if (c.get(Calendar.AM_PM) == Calendar.PM) {
                am_pm = "PM";
            }


            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mChosenTime = bundle.getInt("TIME", 1);
            }

            switch (mChosenTime) {

                case START_TIME:
                    cur = START_TIME;
                    return new TimePickerDialog(getActivity(), this, hour, minute, true);

                case END_TIME:
                    cur = END_TIME;
                    return new TimePickerDialog(getActivity(), this, hour, minute, true);

            }
            return null;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


          //  TextView startTime,endTime;
            EditText startTimeVal,endTimeVal;


            if (cur == START_TIME) {
                // set selected date into textview
                Log.v("Date Début", "Date1 : " + new StringBuilder().append(hourOfDay)
                        .append(":").append(minute)
                        .append(" "));


                startTimeVal = (EditText) getActivity().findViewById(R.id.addEvtStrTimeVal);
                startTimeVal.setText(new StringBuilder().append(hourOfDay)
                        .append(":").append(minute)
                        .append(" "));
                startTimeTextVal =  startTimeVal.getText().toString();

                Toast.makeText(getActivity(),"start Time"+startTimeTextVal,Toast.LENGTH_LONG).show();
               /* ((EditText) getActivity().findViewById(R.id.addEvtTimeVal)).setText(new StringBuilder().append(hourOfDay)
                        .append(":").append(minute).append(" ").append(am_pm)
                        .append(" "));*/
            } else {
                Log.v("Date fin", "Date2 : " + new StringBuilder().append(hourOfDay)
                        .append(":").append(minute)
                        .append(" "));


                endTimeVal = (EditText) getActivity().findViewById(R.id.addEvtEndTimeVal);
                endTimeVal.setText(new StringBuilder().append(hourOfDay)
                        .append(":").append(minute)
                        .append(" "));
                endTimeTextVal =  endTimeVal.getText().toString();
                      /*  ((EditText) getActivity().findViewById(R.id.addEvtEndTimeVal)).setText(new StringBuilder().append(hourOfDay)
                        .append(":").append(minute).append(" ").append(am_pm)
                        .append(" "));*/

                Toast.makeText(getActivity(),"end Time"+endTimeTextVal,Toast.LENGTH_LONG).show();

            }

        }

        private void onTimeSet(String timeString) {
            ((EditText) getActivity().findViewById(R.id.addEvtTimeVal)).setText(timeString);
        }
    }

    public void showDatePickerDialog(View v) {
        datepicker = new DatePickerFragment();
        datepicker.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        static final int START_DATE = 1;
        static final int END_DATE = 2;

        private int mChosenDate;

        int cur = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mChosenDate = bundle.getInt("DATE", 1);
            }

            switch (mChosenDate) {

                case START_DATE:
                    cur = START_DATE;
                    return new DatePickerDialog(getActivity(), this, year, month, day);

                case END_DATE:
                    cur = END_DATE;
                    return new DatePickerDialog(getActivity(), this, year, month, day);

            }
            return null;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {


            EditText startDateVal,endDateVal;

            if (cur == START_DATE) {
                // set selected date into textview
                Log.v("Date Début", "Date1 : " + new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));


                startDateVal = (EditText) getActivity().findViewById(R.id.addEvtStartDateVal);
                startDateVal.setText(new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));

                startDateTextVal = startDateVal.getText().toString();


              /*  ((EditText) getActivity().findViewById(R.id.addEvtStartDateVal)).setText(new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));*/
            } else {
                Log.v("Date fin", "Date2 : " + new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));

                endDateVal = (EditText) getActivity().findViewById(R.id.addEvtEndDateVal);
                endDateVal.setText(new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));
              /*  ((EditText) getActivity().findViewById(R.id.addEvtEndDateVal)).setText(new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));*/

                endDateTextVal = endDateVal.getText().toString();


            }
        }
    }




    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");

        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.d(TAG, "Failed to create directory for storing images");
                return;
            }
        }

        final String fname = PreferenceStorage.getUserId(this) + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Log.d(TAG, "camera output Uri" + outputFileUri);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Profile Photo");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                Log.d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    Log.d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    Log.d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;

                if (isCamera) {
                    Log.d(TAG, "Add to gallery");
                    selectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(selectedImageUri);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
                    Log.d(TAG, "path to image is" + mActualFilePath);

                    // dummyflag= true;

                }
                Log.d(TAG, "image Uri is" + selectedImageUri);
                if (selectedImageUri != null) {
                    Log.d(TAG, "image URI is" + selectedImageUri);

                    if(  pict_flag == 1){

                        picture1 = selectedImageUri.toString();
                        addEventPict1.setImageURI(selectedImageUri);

                    } else if(pict_flag == 2){

                        picture2 = selectedImageUri.toString();
                        addEventPict2.setImageURI(selectedImageUri);

                    } else if(pict_flag == 3){

                        picture3 = selectedImageUri.toString();
                        addEventPict3.setImageURI(selectedImageUri);
                    }





                    //if( ! dummyflag) {
                    setPic(selectedImageUri);
                       /* }else{
                            try {
                               Bitmap  bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                mUserImage.setImageBitmap(bm);
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }*/

                    // mUserImage.setImageURI(selectedImageUri);
                    // mUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        }

    }


    private void setPic(Uri selectedImageUri) {



        // Get the dimensions of the View
        int targetW = addEventPict1.getWidth();
        int targetH = addEventPict1.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        mSelectedImageUri = selectedImageUri;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
            // Bitmap circlebitmap = createCircularBitmap(bitmap);
         //   mProfileImage.setImageBitmap(bitmap);
            Log.d(TAG, "image URI is" + bitmap);

            if(  pict_flag == 1){

                picture1 = bitmap.toString();
                Toast.makeText(AddEventActivity.this, "picture1 "+selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
                Log.d("pic1",picture1);
                addEventPict1.setImageBitmap(bitmap);
            } else if(pict_flag == 2){

                picture2 = bitmap.toString();
                Toast.makeText(AddEventActivity.this, "picture2 "+selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
                Log.d("pic2",picture2);
                addEventPict2.setImageBitmap(bitmap);

            } else if(pict_flag == 3){

                picture3 = bitmap.toString();
                addEventPict3.setImageBitmap(bitmap);
                Log.d("pic3",picture3);
            }


            //mUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
            mCurrentUserImageBitmap = bitmap;
            //  new UploadFileToServer().execute();
            // new Upload(bitmap,"myuserimage").execute();
            //  ServiceLocatorUtils.getInstance().setmCurrentUserProfileImage(mCurrentUserImageBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);


            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null");
            }
        } catch (Exception e) {
            result = null;
            Toast.makeText(this, "Was unable to save  image", Toast.LENGTH_SHORT).show();

        } finally {
            return result;
        }

    }


    private void galleryAddPic(Uri urirequest) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(urirequest.getPath());
        Uri contentUri = Uri.fromFile(f);
        Log.d("contentUri", String.valueOf(contentUri));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



    private boolean validateFields() {
        if (!FindAFunValidator.checkNullString(this.nameValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_name));
            return false;
        }  else if (!FindAFunValidator.checkNullString(eventType.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_type));
            return false;
        } else if (!FindAFunValidator.checkNullString(eventCategory.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_category));
            return false;
        } else if (!FindAFunValidator.checkNullString(eventCity.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_city));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.venueValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_venue));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.addEvtAddressVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_address));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.descripValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_desc));
            return false;
        }


        else if (!FindAFunValidator.checkNullString(this.startDateValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_start_date));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.endDateValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_end_date));
            return false;
        }







        else if (!FindAFunValidator.checkNullString(this.startTimeValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_time));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.endTimeValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_time));
            return false;
        }


        else if (!FindAFunValidator.checkNullString(this.addEvtContactPersonVal.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_contact_person));
            return false;
        }
        else if (!FindAFunValidator.checkNullString(this.contactValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_contact));
            return false;
        }



        else if (!FindAFunValidator.checkNullString(this.latValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lat));
            return false;
        } else if (!FindAFunValidator.checkNullString(this.lngValue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, this.getResources().getString(R.string.event_lng));
            return false;
        } else {
            return true;
        }
    }
}