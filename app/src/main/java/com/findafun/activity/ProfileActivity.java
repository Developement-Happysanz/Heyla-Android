package com.findafun.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findafun.R;
import com.findafun.adapter.CitySpinnerAdapter;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.SignUpServiceHelper;
import com.findafun.serviceinterfaces.IServiceListener;
import com.findafun.utils.AndroidMultiPartEntity;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;*/

//import org.apache.http.HttpResponse;

/*import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;*/

public class ProfileActivity extends AppCompatActivity implements DialogClickListener, IServiceListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = ProfileActivity.class.getName();
    static final int REQUEST_IMAGE_GET = 1;
    EditText name;
    EditText email;
    EditText phone;
    EditText city;
    EditText password;
    TextView mBirthday;
    EditText mSex;
    EditText mOccupation;
    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;
    private CitySpinnerAdapter citySpinnerAdapter;
    private ArrayList<String> cityList = new ArrayList<String>();
    private LinearLayout mPasswordLayout = null;
    private ImageView mProfileImage = null;
    private Button mSaveBtn = null;
    private boolean mEditmode = false;
    private ProgressDialog mProgressDialog = null;
    private ArrayAdapter<String> mGenderAdapter = null;
    private List<String> mGenderList = new ArrayList<String>();
    private Uri outputFileUri;
    private Bitmap mCurrentUserImageBitmap = null;

    private List<String> mOccupationList = new ArrayList<String>();
    private ArrayAdapter<String> mOccupationAdapter = null;

    private Uri mSelectedImageUri = null;
    private String mActualFilePath = null;
    long totalSize = 0;
    private UploadFileToServer mUploadTask = null;
    private Handler mHandler = new Handler();
    private String mUpdatedImageUrl = null;
    final Context context = this;
    private final String MOBILE_NUM_PATTERN = "/^\\d{3}-\\d{3}-\\d{4}$/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");*/

        ImageView backBtn = (ImageView) findViewById(R.id.cst_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mProfileImage = (ImageView) findViewById(R.id.image_profile_pic);
        new FetchCity().execute();

        setUI();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //enable the edit boxes
                if (mEditmode) {
                    int color = getResources().getColor(R.color.text_gray);

                    name.setEnabled(false);
                    name.setTextColor(color);
                    city.setEnabled(false);
                    city.setTextColor(color);
                    phone.setEnabled(false);
                    phone.setTextColor(color);
                    // email.setEnabled(false);
                    password.setEnabled(false);
                    password.setTextColor(color);

                    mSex.setEnabled(false);
                    mSex.setTextColor(color);
                    mBirthday.setEnabled(false);
                    mBirthday.setTextColor(color);
                    mProfileImage.setEnabled(false);
                    mOccupation.setEnabled(false);
                    mOccupation.setTextColor(color);
                    mSaveBtn.setVisibility(View.INVISIBLE);
                    mEditmode = false;
                } else {
                    Snackbar.make(view, "Edit Your Profile", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    int color = getResources().getColor(R.color.detail_color);
                    name.setEnabled(true);
                    name.setTextColor(color);
                    city.setEnabled(true);
                    city.setTextColor(color);
                    phone.setEnabled(true);
                    phone.setTextColor(color);
                    // email.setEnabled(true);
                    password.setEnabled(true);
                    password.setTextColor(color);
                    mSex.setEnabled(true);
                    mSex.setTextColor(color);
                    mSex.setInputType(InputType.TYPE_NULL);
                    mOccupation.setInputType(InputType.TYPE_NULL);
                    city.setInputType(InputType.TYPE_NULL);

                    mBirthday.setEnabled(true);
                    mBirthday.setTextColor(color);

                    mBirthday.requestFocus();
                    mOccupation.requestFocus();
                    mSex.requestFocus();

                    mOccupation.setEnabled(true);
                    mOccupation.setTextColor(color);

                    mProfileImage.setEnabled(true);

                    mSaveBtn.setVisibility(View.VISIBLE);
                    mEditmode = true;
                    mSaveBtn.setFocusable(true);
                }
            }
        });
    }

    void setUI() {
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        city = (EditText) findViewById(R.id.city);
        password = (EditText) findViewById(R.id.edit_password);
        mOccupation = (EditText) findViewById(R.id.occupation_val);
        name.setText(PreferenceStorage.getUserName(getApplicationContext()));
        email.setText(PreferenceStorage.getUserEmail(getApplicationContext()));
        phone.setText(PreferenceStorage.getUserPhone(getApplicationContext()));
        city.setText(PreferenceStorage.getUserCity(getApplicationContext()));
        mBirthday = (TextView) findViewById(R.id.date_of_birth_val);
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogHelper.showSimpleAlertDialog(context, "Change your city using \n\nChange City Menu");
            }
        });
        String birthdayval = PreferenceStorage.getUserBirthday(this);
        if (birthdayval != null) {

            mBirthday.setText(birthdayval);
        }
        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "birthday widget selected");
                showBirthdayDate();

            }
        });
        mGenderList.add("Male");
        mGenderList.add("Female");
        mGenderList.add("Others");

        mGenderAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mGenderList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mGenderList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };


        mSex = (EditText) findViewById(R.id.sex_val);
        String sexVal = CommonUtils.getGenderVal(PreferenceStorage.getUserGender(this));
        if (sexVal != null) {
            mSex.setText(sexVal);
        }
        mSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderList();
            }
        });

        //occupation related data
        mOccupationList.add("Student");
        mOccupationList.add("Employed");

        mOccupationList.add("Self Employed/Business");
        mOccupationList.add("Home Maker");
        mOccupationList.add("Other");
        mOccupationAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mOccupationList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mOccupationList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };
        String occupation = PreferenceStorage.getUserOccupation(this);
        if (occupation != null) {
            mOccupation.setText(occupation);
        }
        mOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOccupationList();
            }
        });

        mSaveBtn = (Button) findViewById(R.id.profile_save_btn);
        mSaveBtn.setVisibility(View.INVISIBLE);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save button selected");
                saveUserProfile();

            }
        });

        mPasswordLayout = (LinearLayout) findViewById(R.id.edit_password_layout);
        int loginMode = PreferenceStorage.getLoginMode(this);
        if (loginMode == 2) {//signed using user name
            mPasswordLayout.setVisibility(View.VISIBLE);
            String pwdVal = PreferenceStorage.getPassword(this);
            if ((pwdVal != null) && !(pwdVal.isEmpty())) {
                password.setText(pwdVal);
            }

        } else {
            mPasswordLayout.setVisibility(View.GONE);
        }

        String url = PreferenceStorage.getProfileUrl(this);
        if ((url == null) || (url.isEmpty())) {
            if ((loginMode == 1) || (loginMode == 3)) {
                url = PreferenceStorage.getSocialNetworkProfileUrl(this);
            }
        }
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.placeholder_small).error(R.drawable.placeholder_small).into(mProfileImage);
        }
        mProfileImage.setEnabled(false);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();

            }
        });
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
                city.setText(citySpinnerAdapter.getItem(which).toString());
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showOccupationList() {
        Log.d(TAG, "Show occupation list");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Occupation");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mOccupationAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mOccupationList.get(which);
                        mOccupation.setText(strName);
                    }
                });
        builderSingle.show();
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
        int targetW = mProfileImage.getWidth();
        int targetH = mProfileImage.getHeight();

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
            mProfileImage.setImageBitmap(bitmap);
            //mUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
            mCurrentUserImageBitmap = bitmap;
            //  new UploadFileToServer().execute();
            // new Upload(bitmap,"myuserimage").execute();
            //  ServiceLocatorUtils.getInstance().setmCurrentUserProfileImage(mCurrentUserImageBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void uploadImaeUsingHttp() {

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

    /*private void  uploadImageUsingOkHttp(String url,String encodeddata){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Log.d(TAG,"encoded data length"+ encodeddata.length());
        String filePath = mActualFilePath;
       // if(filePath != null) {
         //   File sourceFile = new File(filePath);

           // Log.d(TAG, "File...::::" + sourceFile + " : " + sourceFile.exists());
        String fileNameVal = PreferenceStorage.getUserId(ProfileActivity.this);

            MediaType mediaType = MediaType.parse("application/octet-stream");
            // RequestBody body = RequestBody.create(mediaType, "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"fileToUpload\"; filename=\"3565035-green-grass-of-football-stadium-Stock-Photo.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n\r\n-----011000010111000001101001--"+encodeddata);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fileToUpload", fileNameVal+".png",
                            RequestBody.create(mediaType, encodeddata))
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("cache-control", "no-cache")

                    .build();
            try {
                Log.d(TAG, "send the data" + request.body().contentLength() + "request" + request.body().toString() + "content disposition");
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                Response response = client.newCall(request).execute();
                if (response != null) {
                    // JSONObject resp = new JSONObject(response.body().toString());
                    Log.d(TAG, "response val" + response.body().string());
                } else {
                    Log.e(TAG, "received response as null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
       // }

    }*/

    private void galleryAddPic(Uri urirequest) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(urirequest.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void showGenderList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Gender");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Gender");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mGenderAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mGenderList.get(which);
                        mSex.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = mBirthday.getText().toString();
        Log.d(TAG, "current date is" + currentdate);
        int month = newCalendar.get(Calendar.MONTH);
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        if ((currentdate != null) && !(currentdate.isEmpty())) {
            //extract the date/month and year
            try {
                Date startDate = mDateFormatter.parse(currentdate);
                Calendar newDate = Calendar.getInstance();

                newDate.setTime(startDate);
                month = newDate.get(Calendar.MONTH);
                day = newDate.get(Calendar.DAY_OF_MONTH);
                year = newDate.get(Calendar.YEAR);
                Log.d(TAG, "month" + month + "day" + day + "year" + year);

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                mDatePicker = new DatePickerDialog(this,R.style.datePickerTheme, this, year, month, day);
                mDatePicker.show();

            }
        } else {
            Log.d(TAG, "show default date");

            mDatePicker = new DatePickerDialog(this,R.style.datePickerTheme, this, year, month, day);
            mDatePicker.show();
        }
    }

    private boolean isValidMobile(String phone) {
        /*boolean check = false;
        Pattern p;
        Matcher m;
        p = Pattern.compile(MOBILE_NUM_PATTERN);

        m = p.matcher(phone);
        check = m.matches();
*/

        boolean valid = false;
        if (android.util.Patterns.PHONE.matcher(phone).matches()) {

            String Regex = "[^\\d]";
            String PhoneDigits = phone.replaceAll(Regex, "");
            if (PhoneDigits.length() == 10) {
                valid = true;
            }
        }

        return valid;
        // return check;
    }

    private void saveUserImage() {
        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();

    }

    private void saveProfileData() {
        String nameVal = null;
        nameVal = name.getText().toString();
        String emailVal = email.getText().toString();
        String phoneVal = phone.getText().toString();
        String cityVal = city.getText().toString();
        int loginMode = PreferenceStorage.getLoginMode(this);
        String passwordVal = FindAFunConstants.DEFAULT_PASSWORD;
        String genderval = mSex.getText().toString();
        String birthday = mBirthday.getText().toString();
        passwordVal = password.getText().toString();
        String occupation = mOccupation.getText().toString();
        if (loginMode == 2) {
            String pwd = password.getText().toString();
            if ((pwd != null) && !(pwd.isEmpty())) {
                passwordVal = pwd;
            } else {
                passwordVal = PreferenceStorage.getPassword(this);
            }
        }

/*
        if( (fieldVal == null) || (fieldVal.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid name");

        }
        else if( (emailVal == null) || (emailVal.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this,"Enter valid email");

        }
        else if( (phoneVal == null) || (phoneVal.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this,"Enter valid phone number");

        }
        else if( (cityVal == null) || (cityVal.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this,"Enter valid City");

        }
        else if( (loginMode == 2) && (( (passwordVal == null) || (passwordVal.isEmpty())))){//signed using user name

                AlertDialogHelper.showSimpleAlertDialog(this,"Enter valid passsword");



        }else if( (birthday == null) || (birthday.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this,"Select valid Birth Date");
        }
        else if( (genderval == null) || (genderval.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this,"Select valid gender");
        }
        else if( (occupation == null) || (occupation.isEmpty())){
            AlertDialogHelper.showSimpleAlertDialog(this,"Select valid gender");
        }*/


        String url = String.format(FindAFunConstants.UPDATE_PROFILE_URL, emailVal, Uri.encode(nameVal),
                Integer.parseInt(PreferenceStorage.getUserId(this)), cityVal, genderval, birthday, Uri.encode(occupation), phoneVal);

        SignUpServiceHelper mServiceHelper = new SignUpServiceHelper(this);
        mServiceHelper.updateUserProfile(url, this);

        //}

    }

    private boolean shouldUploadSocialNetworkPic() {
        boolean upload = false;
        String url = PreferenceStorage.getSocialNetworkProfileUrl(ProfileActivity.this);
        String userimageUrl = PreferenceStorage.getProfileUrl(ProfileActivity.this);
        int loginMode = PreferenceStorage.getLoginMode(ProfileActivity.this);
        if ((userimageUrl == null) || (userimageUrl.isEmpty())) {
            if ((loginMode == 1) || (loginMode == 3)) {
                if ((url != null) && !(url.isEmpty())) {
                    mCurrentUserImageBitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
                    Log.d(TAG, "valid URL present");
                    if (mCurrentUserImageBitmap != null) {
                        upload = true;
                    } else {
                        Log.e(TAG, "No Bitmap present");
                    }

                } else {
                    Log.e(TAG, "No image present for social network sites");
                }

            }

        }

        return upload;
    }

    private void saveUserProfile() {
        String phoneVal = phone.getText().toString();
        if ((phoneVal != null) && !(phoneVal.isEmpty()) && !isValidMobile(phoneVal)) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid phone number");
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Updating Profile");
            mProgressDialog.show();
            if ((mActualFilePath != null)) {
                Log.d(TAG, "Update profile picture");
                saveUserImage();
            } else {
                saveProfileData();
            }
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
            Log.d(TAG, "Profile was saved successfully");

            String nameVal = name.getText().toString();
            String emailVal = email.getText().toString();
            String phoneVal = phone.getText().toString();
            String cityVal = city.getText().toString();
            int loginMode = PreferenceStorage.getLoginMode(this);

            String genderval = mSex.getText().toString();
            String birthday = mBirthday.getText().toString();
            String occupation = mOccupation.getText().toString();

            PreferenceStorage.saveUserGender(this, genderval);
            PreferenceStorage.saveUserBirthday(this, birthday);
            PreferenceStorage.saveUserCity(this, cityVal);
            PreferenceStorage.saveUserEmail(this, emailVal);
            PreferenceStorage.saveUserName(this, nameVal);
            PreferenceStorage.saveUserPhone(this, phoneVal);
            PreferenceStorage.saveUserOccupation(this, occupation);

            int color = getResources().getColor(R.color.text_gray);

            name.setEnabled(false);
            name.setTextColor(color);
            city.setEnabled(false);
            city.setTextColor(color);
            phone.setEnabled(false);
            phone.setTextColor(color);
            email.setEnabled(false);
            password.setEnabled(false);
            password.setTextColor(color);
            mSex.setEnabled(false);
            mSex.setTextColor(color);
            mBirthday.setEnabled(false);
            mBirthday.setTextColor(color);
            mSaveBtn.setVisibility(View.INVISIBLE);
            mOccupation.setEnabled(false);
            mOccupation.setTextColor(color);
            mProfileImage.setEnabled(false);
            mEditmode = false;
            AlertDialogHelper.showSimpleAlertDialog(this, "Profile updated successfully");
            mActualFilePath = null;

        }
    }

    @Override
    public void onError(String erorr) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        AlertDialogHelper.showSimpleAlertDialog(this, "Error saving your profile. Try again");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        mBirthday.setText(mDateFormatter.format(newDate.getTime()));
    }

    private class ImagetoByteConverter extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... params) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            params[0].compress(Bitmap.CompressFormat.JPEG, 70, stream);
            Log.d(TAG, "Compressed bitmap size is" + stream.toByteArray().length);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            String encodedString = Base64.encodeToString(byte_arr, 0);
           /* HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(
                    "http://10.0.2.2/cfc/iphoneWebservice.cfc?returnformat=json&amp;method=testUpload");
            ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
            // File file= new File("/mnt/sdcard/forest.png");
            // FileBody bin = new FileBody(file);
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("uploaded", bab);
            reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);*/

            return encodedString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Setting the user image");
            uploadImage(result);
        }
    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UploadFileToServer";
        private HttpClient httpclient;
        HttpPost httppost;
        public boolean isTaskAborted = false;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
           /* progressBar.setVisibility(View.VISIBLE);
            txtPercentage.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);*/
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            /*progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");*/
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            //http://heylaapp.com/app/upload.php?user_id=529
            String responseString = null;

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(String.format(FindAFunConstants.UPLOAD_PROFILE_IMAGE, Integer.parseInt(PreferenceStorage.getUserId(ProfileActivity.this))));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //  publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Log.d(TAG, "actual file path is" + mActualFilePath);
                if (mActualFilePath != null) {

                    File sourceFile = new File(mActualFilePath);

                    // Adding file data to http body
                    //fileToUpload
                    entity.addPart("fileToUpload", new FileBody(sourceFile));
                /*}else {
                    String fileNameVal = PreferenceStorage.getUserId(ProfileActivity.this)+".png";
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mCurrentUserImageBitmap.compress(Bitmap.CompressFormat.PNG, 75, bos);
                    byte[] data = bos.toByteArray();
                    ByteArrayBody bab = new ByteArrayBody(data, fileNameVal);
                    entity.addPart("fileToUpload", bab);
                }*/
                    //entity.addPart("image", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(ProfileActivity.this)));
                /*entity.addPart("website",
                        new StringBody("www.ahmed.site40.net"));
                entity.addPart("email", new StringBody("ahmedchoteri@gmail.com"));*/

                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                        try {
                            JSONObject resp = new JSONObject(responseString);
                            String successVal = resp.getString("status");
                            mUpdatedImageUrl = resp.getString("image");
                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                                Log.d(TAG, "Updated image succesfully");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
                Toast.makeText(ProfileActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveProfilePic(ProfileActivity.this, mUpdatedImageUrl);
                }
            }
            saveProfileData();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public void taskCancel() {
            if (httppost != null) {
                isTaskAborted = true;
                httppost.abort();
                httppost = null;
            }
            if (httpclient != null) {
                isTaskAborted = true;
                httpclient.getConnectionManager().shutdown();
            }
            httpclient = null;
        }
    }


    private class Upload extends AsyncTask<Void, Void, String> {
        private Bitmap image;
        private String name;

        public Upload(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Log.d(TAG, "Do image compression");
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */

            // String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);
            String encodeImage = null;
            /*try {
               // encodeImage = byteArrayOutputStream.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
           /* if(encodeImage != null) {
                uploadImageUsingOkHttp(String.format(FindAFunConstants.UPLOAD_PROFILE_IMAGE, Integer.parseInt(PreferenceStorage.getUserId(ProfileActivity.this))), encodeImage);
            }*/

            //generate hashMap to store encodedImage and the name

            /*HashMap<String,String> detail = new HashMap<>();

            detail.put("fileToUpload", encodeImage);
            detail.put("tmp_name", "471.png");
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";*/
            String boundary = "---011000010111000001101001";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String fileNameVal = PreferenceStorage.getUserId(ProfileActivity.this) + ".png";

            try {
                //convert this HashMap to encodedUrl to send to php file
                Log.d(TAG, "start uploading the image");
                String dataToSend = null;

                buffer = byteArrayOutputStream.toByteArray();
                // dataToSend = hashMapToUrl(detail);
                URL url = new URL(String.format(FindAFunConstants.UPLOAD_PROFILE_IMAGE, Integer.parseInt(PreferenceStorage.getUserId(ProfileActivity.this))));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                //set timeout of 30 seconds
                con.setConnectTimeout(1000 * 20);
                con.setReadTimeout(1000 * 30);
                //method
                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                // con.setChunkedStreamingMode(1024);

                // con.setRequestProperty("fileToUpload", fileNameVal);
                con.setRequestProperty("content-type", "multipart/form-data; boundary=" + boundary);
                con.setRequestProperty("cache-control", "no-cache");

                //con.setDoOutput(true);

                DataOutputStream os = new DataOutputStream(con.getOutputStream());
                //  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                os.writeBytes(twoHyphens + boundary + lineEnd);
                os.writeBytes("Content-Disposition:form-data; name=\"fileToUpload\"; filename=\"" + fileNameVal + "\"" + lineEnd);
                // os.writeBytes("Content-Type: image/png" + lineEnd);

                //os.writeBytes("Content-Length: " + buffer.length + lineEnd);

                // os.writeBytes("Content-Length: " + buffer.length + lineEnd);
                os.writeBytes(lineEnd + lineEnd + twoHyphens + boundary + twoHyphens);

                int bufferLength = 1024;
                int i = 0;
                for (i = 0; i < buffer.length; i += bufferLength) {
                    // publishing the progress....

                    if (buffer.length - i >= bufferLength) {
                        os.write(buffer, i, bufferLength);
                    } else {
                        os.write(buffer, i, buffer.length - i);
                    }
                }
                //os.write(buffer);
                Log.d(TAG, "Total sent image bytes");

                // os.writeBytes(lineEnd);
                // os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                os.flush();
                os.close();

                //get the response
                int responseCode = con.getResponseCode();
                Log.d(TAG, "response code" + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //read the response
                    Log.d(TAG, "upload success");
                    StringBuilder sb = new StringBuilder();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String line;

                    //loop through the response from the server
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    Log.d(TAG, "received response is" + sb.toString());

                    //return the response
                    return sb.toString();
                } else {
                    Log.e(TAG, "ERROR - Invalid response code from server " + responseCode);

                    return null;
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR  " + e);
                return null;
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            if (s != null) {
                Log.d(TAG, "receive resposnse is" + s);
            }
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void uploadUsingMultiPart(String url,Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String fileNameVal = PreferenceStorage.getUserId(ProfileActivity.this)+".png";
        Log.d(TAG,"Do image compression");
        //compress the image to jpg format
        image.compress(Bitmap.CompressFormat.PNG,90,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        MultipartEntity entity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        entity.addPart("fileToUpload", new ByteArrayBody(data,fileNameVal
        ));

        //add your other name value pairs in entity.

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

       // httppost.setEntity(entity);

      //  Log.d("nameValuePairs", "nameValuePairs " + nameValuePairs);

       *//* HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity1 = response.getEntity();

        httppost.setEntity(entity);

        HttpResponse response = httpClient.execute(httppost);*//*
    }*/


    private void uploadImage(String imageString) {
        Log.d(TAG, "upload the image with size" + imageString.length());
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("uploading image");
        mProgressDialog.show();

        SignUpServiceHelper uploadHelper = new SignUpServiceHelper(this);
        uploadHelper.uploadUserImage(String.format(FindAFunConstants.UPLOAD_PROFILE_IMAGE, Integer.parseInt(PreferenceStorage.getUserId(this))), imageString, this);

    }



   /* private String openHttpConnection(String urlStr) {
        InputStream in = null;
        StringBuilder sb=new StringBuilder();
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

                while((read=br.readLine()) != null) {
                    //System.out.println(read);
                    sb.append(read);
                }

            }
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }*/

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
            citySpinnerAdapter = new CitySpinnerAdapter(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, cityList);
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
